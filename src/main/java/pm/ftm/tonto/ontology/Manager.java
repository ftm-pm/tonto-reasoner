package pm.ftm.tonto.ontology;

import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import pm.ftm.tonto.model.OntologyException;
import pm.ftm.tonto.model.Query;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class Manager implements OntologyLoader, QueryReasoner {

    private OWLOntology ontology;
    private OWLOntologyManager ontologyManager;

    public Manager() {
        ontologyManager = OWLManager.createOWLOntologyManager();
    }

    @Override
    public boolean load(String ontologyIRI) throws IOException {
        String ontologyString = this.getOntologyContentByOntologyIRI(ontologyIRI);
        return this.loadFromString(ontologyString);
    }

    /**
     * Return ontology content from otologyIRI
     * @param ontologyIRI Ontology IRI
     * @return the ontology
     * @throws IOException Ontology wasn't loaded
     */
    private String getOntologyContentByOntologyIRI(String ontologyIRI) throws IOException {
        URL url = new URL(ontologyIRI);
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStreamReader isr = new InputStreamReader(url.openStream(), "UTF-8")) {
            char[] buffer = new char[1024];
            int count = 0;
            while ((count = isr.read(buffer, 0, 1024)) != -1) {
                stringBuilder.append(new String(buffer, 0, count));
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean loadFromString(String ontologyIRI) {
        try {
            if(ontology != null) {
                ontologyManager.removeOntology(ontology);
                ontology = null;
            }
            ontology = ontologyManager.loadOntologyFromOntologyDocument(new StringDocumentSource(ontologyIRI));
        } catch (OWLOntologyCreationException exception) {
            ontology = null;
        }

        return ontology != null;
    }

    @Override
    public List<String> ask(Query query) throws OntologyException {
        if (ontology == null) {
            throw new OntologyException("Ontology was not loaded.");
        }
        List<String> response = new ArrayList<>();

        OWLDataFactory fac = ontologyManager.getOWLDataFactory();
        OWLReasoner reasoner = new ReasonerFactory().createReasoner(ontology);

        String prefix = ontology.getOntologyID().getOntologyIRI().get() + "#";
        OWLObjectProperty objectProperty = fac.getOWLObjectProperty(IRI.create(prefix + query));
        Stream<OWLNamedIndividual> individuals = ontology.individualsInSignature();

        individuals.forEach(individual -> {
            response.add(individual.getIRI().getShortForm());
//            ontology.objectPropertyAssertionAxioms(individual).forEach(expr -> {
//                System.out.println(expr.toString());
//            });
//
//            reasoner.objectPropertyValues(individual, objectProperty).forEach(ind -> {
//                response.add(individual.getIRI());
//            });
        });

        return response;
    }
}
