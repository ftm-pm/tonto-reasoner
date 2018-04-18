package pm.ftm.tonto.controller;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.springframework.web.bind.annotation.*;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Stream;

@RestController
public class DataController {

    @Nonnull
    private static final String baseOntology = "<?xml version=\"1.0\"?>\n" +
            "<Ontology xmlns=\"http://www.w3.org/2002/07/owl#\"\n" +
            "     xml:base=\"alex\"\n" +
            "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
            "     xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n" +
            "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
            "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
            "     ontologyIRI=\"alex\">\n" +
            "    <Prefix query=\"owl\" IRI=\"http://www.w3.org/2002/07/owl#\"/>\n" +
            "    <Prefix query=\"rdf\" IRI=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"/>\n" +
            "    <Prefix query=\"xml\" IRI=\"http://www.w3.org/XML/1998/namespace\"/>\n" +
            "    <Prefix query=\"xsd\" IRI=\"http://www.w3.org/2001/XMLSchema#\"/>\n" +
            "    <Prefix query=\"rdfs\" IRI=\"http://www.w3.org/2000/01/rdf-schema#\"/>\n" +
            "    <Declaration>\n" +
            "        <NamedIndividual IRI=\"#Bill\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <ObjectProperty IRI=\"#hasUncle\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <NamedIndividual IRI=\"#Max\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <ObjectProperty IRI=\"#hasParent\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <NamedIndividual IRI=\"#Tom\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <ObjectProperty IRI=\"#hasBrother\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <Class IRI=\"#human\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <AnnotationProperty IRI=\"http://swrl.stanford.edu/ontologies/3.3/swrla.owl#isRuleEnabled\"/>\n" +
            "    </Declaration>\n" +
            "    <ObjectPropertyAssertion>\n" +
            "        <ObjectProperty IRI=\"#hasParent\"/>\n" +
            "        <NamedIndividual IRI=\"#Max\"/>\n" +
            "        <NamedIndividual IRI=\"#Tom\"/>\n" +
            "    </ObjectPropertyAssertion>\n" +
            "    <ObjectPropertyAssertion>\n" +
            "        <ObjectProperty IRI=\"#hasBrother\"/>\n" +
            "        <NamedIndividual IRI=\"#Tom\"/>\n" +
            "        <NamedIndividual IRI=\"#Bill\"/>\n" +
            "    </ObjectPropertyAssertion>\n" +
            "    <DLSafeRule>\n" +
            "        <Body>\n" +
            "            <ObjectPropertyAtom>\n" +
            "                <ObjectProperty IRI=\"#hasParent\"/>\n" +
            "                <Variable IRI=\"x\"/>\n" +
            "                <Variable IRI=\"y\"/>\n" +
            "            </ObjectPropertyAtom>\n" +
            "            <ObjectPropertyAtom>\n" +
            "                <ObjectProperty IRI=\"#hasBrother\"/>\n" +
            "                <Variable IRI=\"y\"/>\n" +
            "                <Variable IRI=\"z\"/>\n" +
            "            </ObjectPropertyAtom>\n" +
            "        </Body>\n" +
            "        <Head>\n" +
            "            <ObjectPropertyAtom>\n" +
            "                <ObjectProperty IRI=\"#hasUncle\"/>\n" +
            "                <Variable IRI=\"x\"/>\n" +
            "                <Variable IRI=\"z\"/>\n" +
            "            </ObjectPropertyAtom>\n" +
            "        </Head>\n" +
            "    </DLSafeRule>\n" +
            "</Ontology>\n";

    @RequestMapping(value = "/api/ontology")
    public String greeting(@RequestBody Params params) {
        if (params.ontology == null) {
            params.setOntology(baseOntology);
        }
        List<IRI> response = new ArrayList<IRI>();

        try {
            response = shouldUseReasoner(params.ontology, params.query);
        } catch (OWLOntologyCreationException exp) {
            System.out.print(exp.getMessage());
        }
        Optional<String> list = response.stream().map(item -> item.getShortForm().toString()).reduce((s1, s2) -> s1 + s2);
        return list.toString();
    }

    public static class Params {

        private String ontology;
        private String query;

        public Params() {
        }

        public String getOntology() {
            return ontology;
        }

        public void setOntology(String ontology) {
            this.ontology = ontology;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }

    /**
     * @param manager OWLOntologyManager
     * @return loaded ontology
     * @throws OWLOntologyCreationException if a problem pops up
     */
    @Nonnull
    private OWLOntology load(@Nonnull OWLOntologyManager manager, String ontology) throws OWLOntologyCreationException {
        return manager.loadOntologyFromOntologyDocument(new StringDocumentSource(ontology));
    }

    /**
     * @param ontologyString The ontology string
     * @param query          The query
     * @throws OWLOntologyCreationException if a problem pops up
     */
    private List<IRI> shouldUseReasoner(String ontologyString, String query) throws OWLOntologyCreationException {
        List<IRI> response = new ArrayList<IRI>();
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ont = load(manager, baseOntology);

        OWLDataFactory fac = manager.getOWLDataFactory();
        OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(ont);

        String prefix = ont.getOntologyID().getOntologyIRI().get() + "#";
        OWLObjectProperty objectProperty = fac.getOWLObjectProperty(IRI.create(prefix + query));
        Stream<OWLNamedIndividual> individuals = ont.individualsInSignature();

        // Max hasParent Tom
        // Tom hasBrother Bill
        // ->
        // Max hasUncle Bill
        // objectProperty === "hasParent"
//        OWLNamedIndividual individualx;

        individuals.forEach(individual -> {

            ont.objectPropertyAssertionAxioms(individual).forEach(expr -> {
                System.out.println(expr.toString());
            });

            reasoner.objectPropertyValues(individual, objectProperty).forEach(ind -> {
                response.add(individual.getIRI());
            });
        });

        return response;
    }

}