package pm.ftm.tonto.ontology;

import org.semanticweb.HermiT.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import pm.ftm.tonto.model.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class Manager implements OntologyLoader, QueryReasoner {

    private static final String NEW_VERSION = "Rule will be available in new version";
    private static final String INCORRECT_QUESTION = "Incorrect question";
    private static final String ONTOLOGY_WAS_NOT_LOADED = "Ontology was not loaded";

    private OWLOntology ontology;
    private OWLOntologyManager ontologyManager;
    private OWLDataFactory factory;
    private OWLReasoner reasoner;
    private String prefix = "#";
    private boolean full = false;

    public Manager() {
        ontologyManager = OWLManager.createOWLOntologyManager();
    }

    @Override
    public boolean load(String ontologyIRI) throws IOException {
        String ontologyString = this.getOntologyContentByOntologyIRI(ontologyIRI);

        return this.loadFromString(ontologyString);
    }

    @Override
    public boolean loadFromString(String ontologyIRI) {
        try {
            // need to check the changes and if there are no changes, do not reload the ontology, now just reset
            if (ontology != null) {
                ontologyManager.removeOntology(ontology);
                ontology = null;
                prefix = "#";
            }
            ontology = ontologyManager.loadOntologyFromOntologyDocument(new StringDocumentSource(ontologyIRI));
            factory = ontologyManager.getOWLDataFactory();
            reasoner = new ReasonerFactory().createReasoner(ontology);
            prefix = ontology.getOntologyID().getOntologyIRI().map(iri -> iri  + prefix).orElseGet(() -> prefix);

        } catch (OWLOntologyCreationException exception) {
            ontology = null;
        }

        return ontology != null;
    }

    @Override
    public ResultResponse ask(Query query) throws OntologyException {
        ResultResponse response;

        // If ontology is empty then throw exception
        if (ontology == null) {
            throw new OntologyException(ONTOLOGY_WAS_NOT_LOADED);
        }
        // Set query result filter on bases OWL entities
        full = query.isFull();

        List<Token> tokens = query.getTokens();
        if (!tokens.isEmpty()) {
            Token first = tokens.get(0);
            // Check type first token and selected rule
            switch (first.getTag()) {
                case Tag.SUBCLASS_OF:
                    response = getSubEntitiesResponse(tokens);
                    break;
                case Tag.IS:
                case Tag.SUPERCLASS_OF:
                    response = getSuperEntitiesResponse(tokens);
                    break;
                case Tag.WHO_WHAT:
                    response = getWhoWhatResponse(tokens);
                    break;
                case Tag.OBJECT_PROPERTY:
                case Tag.DATA_PROPERTY:
                    response = getPropertyResponse(tokens);
                    break;
                case Tag.INDIVIDUAL:
                case Tag.INSTANCE:
                    response = getNamedIndividualResponse(tokens);
                    break;
                case Tag.CLASS:
                    throw new OntologyException("Wait: <SUBCLASS|SUPERCLASS|INSTANCE> <CLASS>");
                default:
                    throw new OntologyException(INCORRECT_QUESTION);
            }
        } else {
            throw new OntologyException(INCORRECT_QUESTION);
        }

        return response;
    }

    /**
     * Return ontology content from ontologyIRI
     *
     * @param ontologyIRI Ontology IRI
     * @return the ontology
     * @throws IOException Ontology wasn't loaded
     */
    private String getOntologyContentByOntologyIRI(String ontologyIRI) throws IOException {
        URL url = new URL(ontologyIRI);
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(url.openStream(), "UTF-8")) {
            char[] buffer = new char[1024];
            int count;
            while ((count = isr.read(buffer, 0, 1024)) != -1) {
                stringBuilder.append(new String(buffer, 0, count));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Return object property assertions
     *
     * @param tokens Query tokens
     * @return Return object property assertions
     * @throws OntologyException If rule wasn't founded.
     */
    private ResultResponse getPropertyResponse(List<Token> tokens) throws OntologyException {
        ResultResponse response = new ResultResponse();
        Token first = tokens.get(0);
        if (tokens.size() > 1) {
            Token second = tokens.get(1);
            if (second.getTag() == Tag.WHO_WHAT) {
                ObjectPropertyAssertionsResponse objectPropertyAssertionsResponse = getObjectPropertyAssertionsResponse(first, null, second);
                ClassResponse classResponse = new ClassResponse();
                classResponse.setSubClasses(getSubClassesByObjectProperty(first));
                response.setObjectPropertyAssertionsResponse(objectPropertyAssertionsResponse);
                response.setClassResponse(classResponse);
            } else {
                throw new OntologyException(NEW_VERSION);
            }
        } else {
            throw new OntologyException("Wait: <IS|PROPERTY> <WHO_THAT|ALL>");
        }

        return response;
    }

    /**
     * Return object property assertions
     *
     * @param tokens Query tokens
     * @return Return object property assertions
     * @throws OntologyException If rule wasn't founded.
     */
    private ResultResponse getWhoWhatResponse(List<Token> tokens) throws OntologyException {
        ResultResponse response = new ResultResponse();

        Token first = tokens.get(0);
        if (tokens.size() > 1) {
            Token second = tokens.get(1);
            if (second.getTag() ==  Tag.OBJECT_PROPERTY || second.getTag() ==  Tag.DATA_PROPERTY) {
                ObjectPropertyAssertionsResponse objectPropertyAssertionsResponse;
                if (tokens.size() > 2) {
                    Token third = tokens.get(2);
                    if (third.getTag() == Tag.WHO_WHAT) {
                        objectPropertyAssertionsResponse = getObjectPropertyAssertionsResponse(second, first, third);
                        getSubClassesByObjectProperty(second);
                    } else {
                        throw new OntologyException(NEW_VERSION);
                    }
                } else {
                    objectPropertyAssertionsResponse = getObjectPropertyAssertionsResponse(second, first, null);
                }
                ClassResponse classResponse = new ClassResponse();
                classResponse.setSubClasses(getSubClassesByObjectProperty(second));
                response.setObjectPropertyAssertionsResponse(objectPropertyAssertionsResponse);
                response.setClassResponse(classResponse);
            } else {
                throw new OntologyException("Wait: <WHO_THAT> <IS|PROPERTY> [ <WHO_THAT> ]");
            }
        } else {
            throw new OntologyException("Wait: <WHO_THAT> <IS|PROPERTY> [ <WHO_THAT> ]");
        }

        return response;
    }

    /**
     * Return result response with sub classes for class or object property or data property
     *
     * @param tokens Query tokens
     * @return Return result response
     * @throws OntologyException If class or object property or data property was not founded
     */
    private ResultResponse getSubEntitiesResponse(List<Token> tokens) throws OntologyException {
        ResultResponse response = new ResultResponse();
        Token token = tokens.get(1);
        if (token != null) {
            switch (token.getTag()) {
                case Tag.CLASS:
                    response.setClassResponse(getSubClassesResponse(token));
                    break;
                case Tag.OBJECT_PROPERTY:
                    response.setObjectPropertyResponse(getSubObjectPropertyResponse(token));
                    break;
                case Tag.DATA_PROPERTY:
                    throw new OntologyException(NEW_VERSION);
                default:
                    throw new OntologyException("Wait: <Subclass> <CLASS|OBJECT_PROPERTY|DATA_PROPERTY>");
            }

        } else {
            throw new OntologyException(INCORRECT_QUESTION);
        }

        return response;
    }

    /**
     * Return result response with super classes for query
     *
     * @param tokens Query tokens
     * @return Return result response
     * @throws OntologyException If class or object property or data property was not founded
     */
    private ResultResponse getSuperEntitiesResponse(List<Token> tokens) throws OntologyException {
        ResultResponse response = new ResultResponse();
        Token token = tokens.get(1);
        if (token != null) {
            switch (token.getTag()) {
                case Tag.CLASS:
                    response.setClassResponse(getSuperClassesResponse(token));
                    break;
                case Tag.OBJECT_PROPERTY:
                    response.setObjectPropertyResponse(getSuperObjectPropertyResponse(token));
                    break;
                case Tag.DATA_PROPERTY:
                    throw new OntologyException(NEW_VERSION);
                default:
                    throw new OntologyException("Wait: <Subclass> <CLASS|OBJECT_PROPERTY|DATA_PROPERTY>");
            }

        } else {
            throw new OntologyException(INCORRECT_QUESTION);
        }

        return response;
    }

    /**
     * Return named individual response with instances for query
     *
     * @param tokens Query tokens
     * @return Return sub classes
     * @throws OntologyException If class expression wasn't founded.
     */
    private ResultResponse getNamedIndividualResponse(List<Token> tokens) throws OntologyException {
        ResultResponse response = new ResultResponse();

        if(tokens.size() > 1) {
            Token token = tokens.get(1);
            List<String> collection = getNamedIndividuals(token);
            NamedIndividualResponse namedIndividualResponse = new NamedIndividualResponse();
            namedIndividualResponse.setCollection(collection);
            response.setNamedIndividualResponse(namedIndividualResponse);
        } else {
            throw new OntologyException("Wait: <INSTANCE> <CLASS>");
        }

        return response;
    }

    /**
     * Return class response with sub classes
     *
     * @param token Query token
     * @return Return sub classes
     */
    private ClassResponse getSubClassesResponse(Token token) {
        List<String> subClasses = getSubClasses(token);
        ClassResponse classResponse = new ClassResponse();
        classResponse.setSubClasses(subClasses);

        return classResponse;
    }

    /**
     * Return class response with super classes
     *
     * @param token Query tokens
     * @return Return super classes
     */
    private ClassResponse getSuperClassesResponse(Token token) {
        List<String> superClasses = getSuperClasses(token);
        ClassResponse classResponse = new ClassResponse();
        classResponse.setSuperClasses(superClasses);

        return classResponse;
    }

    /**
     * Return object property response with sub classes
     *
     * @param token Query token
     * @return Return sub classes
     */
    private ObjectPropertyResponse getSubObjectPropertyResponse(Token token) {
        List<String> subObjectProperies = getSubObjectProperties(token);
        ObjectPropertyResponse objectPropertyResponse = new ObjectPropertyResponse();
        objectPropertyResponse.setSubObjectProperties(subObjectProperies);

        return objectPropertyResponse;
    }

    /**
     * Return object property response with super classes
     *
     * @param token Query tokens
     * @return Return super classes
     */
    private ObjectPropertyResponse getSuperObjectPropertyResponse(Token token) {
        List<String> subObjectProperies = getSuperObjectProperties(token);
        ObjectPropertyResponse objectPropertyResponse = new ObjectPropertyResponse();
        objectPropertyResponse.setSuperObjectProperties(subObjectProperies);

        return objectPropertyResponse;
    }

    /**
     * Get list sub classes
     *
     * @param token root objectProperty
     * @return list object properties
     */
    private List<String> getSubClasses(Token token) {
        List<String> response = new ArrayList<>();

        OWLClass owlClass = factory.getOWLClass(IRI.create(prefix + token.getLexeme()));
        NodeSet<OWLClass> subClasses = reasoner.getSubClasses(owlClass);
        for (Node<OWLClass> subClass : subClasses) {
            subClass.entities().forEach(entity -> {
                if (!entity.getIRI().getShortForm().equals("Nothing") || full) {
                    response.add(entity.getIRI().getShortForm());
                }
            });
        }

        return response;
    }

    /**
     * Get list named individuals
     *
     * @param token class for named individuals
     * @return Return list named individuals
     */
    private List<String> getNamedIndividuals(Token token) {
        List<String> response = new ArrayList<>();

        OWLClass owlClass = factory.getOWLClass(IRI.create(prefix + token.getLexeme()));
        NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(owlClass);
        for (Node<OWLNamedIndividual> instance : instances) {
            instance.entities().forEach(entity -> {
                if (!entity.getIRI().getShortForm().equals("Nothing") || full) {
                    response.add(entity.getIRI().getShortForm());
                }
            });
        }

        return response;
    }

    /**
     * Get list sub object properties
     *
     * @param token root objectProperty
     * @return list object properties
     */
    private List<String> getSubObjectProperties(Token token) {
        List<String> response = new ArrayList<>();

        OWLObjectProperty owlObjectProperty = factory.getOWLObjectProperty(IRI.create(prefix + token.getLexeme()));
        NodeSet<OWLObjectPropertyExpression> subObjectProperties = reasoner.getSubObjectProperties(owlObjectProperty);
        for (Node<OWLObjectPropertyExpression> subObjectPropery : subObjectProperties) {
            subObjectPropery.entities().forEach(entity -> {
                if (!entity.getNamedProperty().getIRI().getShortForm().equals("bottomObjectProperty") || full) {
                    response.add(entity.getNamedProperty().getIRI().getShortForm());
                }
            });
        }

        return response;
    }

    /**
     * Get list sub classes
     *
     * @param token root objectProperty
     * @return list object properties
     */
    private List<String> getSuperClasses(Token token) {
        List<String> response = new ArrayList<>();

        OWLClass owlClass = factory.getOWLClass(IRI.create(prefix + token.getLexeme()));
        NodeSet<OWLClass> subClasses = reasoner.getSuperClasses(owlClass);
        for (Node<OWLClass> subClass : subClasses) {
            subClass.entities().forEach(entity -> {
                if (!entity.getIRI().getShortForm().equals("Thing") || full) {
                    response.add(entity.getIRI().getShortForm());
                }
            });
        }

        return response;
    }

    /**
     * Get list super object properties
     *
     * @param token root objectProperty
     * @return list object properties
     */
    private List<String> getSuperObjectProperties(Token token) {
        List<String> response = new ArrayList<>();

        OWLObjectProperty owlObjectProperty = factory.getOWLObjectProperty(IRI.create(prefix + token.getLexeme()));
        NodeSet<OWLObjectPropertyExpression> subObjectProperties = reasoner.getSuperObjectProperties(owlObjectProperty);
        for (Node<OWLObjectPropertyExpression> subObjectPropery : subObjectProperties) {
            subObjectPropery.entities().forEach(entity -> {
                if (!entity.getNamedProperty().getIRI().getShortForm().equals("topObjectProperty") || full) {
                    response.add(entity.getNamedProperty().getIRI().getShortForm());
                }
            });
        }

        return response;
    }

    /**
     * Return object property assertions
     *
     * @param property Object property or Data property
     * @param subject WHO_WHAT or null
     * @param object WHO_WHAT or null
     * @return Return object property assertions response
     */
    private ObjectPropertyAssertionsResponse getObjectPropertyAssertionsResponse(Token property, Token subject, Token object)  {
        List<ObjectPropertyAssertionResponse> collection = getObjectPropertyAssertions(property, subject, object);
        ObjectPropertyAssertionsResponse objectPropertyAssertionsResponse = new ObjectPropertyAssertionsResponse();
        objectPropertyAssertionsResponse.setCollection(collection);

        return objectPropertyAssertionsResponse;
    }

    /**
     * Get list sub classes
     *
     * @param property Object property or Data property
     * @param subject WHO_WHAT or null
     * @param object WHO_WHAT or null
     * @return list object properties
     */
    private List<ObjectPropertyAssertionResponse> getObjectPropertyAssertions(Token property, Token subject, Token object) {
        List<ObjectPropertyAssertionResponse> collection = new ArrayList<>();

        OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(prefix + property.getLexeme()));
        Stream<OWLNamedIndividual> individuals = ontology.individualsInSignature();

        individuals.forEach(subjectIndividual -> reasoner.objectPropertyValues(subjectIndividual, objectProperty)
                .forEach(objectIndividual -> {
                    ObjectPropertyAssertionResponse opar = new ObjectPropertyAssertionResponse();
                    opar.setObjectProperty(objectProperty.getIRI().getShortForm());
                    if (subject != null) {
                        opar.setNamedIndividualFirst(subjectIndividual.getIRI().getShortForm());
                    }
                    if (object != null) {
                        opar.setNamedIndividualSecond(objectIndividual.getIRI().getShortForm());
                    }
                    collection.add(opar);
                })
        );

        return collection;
    }

    /**
     * Return list sub classes
     *
     * @param property Object property or Data property
     * @return Return list sub classes
     */
    private List<String> getSubClassesByObjectProperty(Token property) {
        List<String> collection = new ArrayList<>();

        // It's a very bad solution, because i don't know how get it another way
        Stream<OWLSubClassOfAxiom> classes = ontology.axioms(AxiomType.SUBCLASS_OF);
        classes.forEach(ind -> {
            if(ind.getSuperClass().getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM ||
                    ind.getSuperClass().getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                OWLClassExpression superClass = ind.getSuperClass();
                Stream<OWLObjectProperty> props = superClass.objectPropertiesInSignature();
                props.forEach(prop -> {

                    if(prop.getIRI().getShortForm().equals(property.getLexeme())) {
                        OWLClass owlClass = (OWLClass)ind.getSubClass();
                        collection.add(owlClass.getIRI().getShortForm());
                    }
                });
            }
        });

        return collection;
    }
}
