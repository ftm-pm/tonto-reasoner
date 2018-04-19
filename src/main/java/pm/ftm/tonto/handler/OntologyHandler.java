package pm.ftm.tonto.handler;

import org.springframework.stereotype.Service;
import pm.ftm.tonto.model.Request;
import pm.ftm.tonto.model.Response;
import pm.ftm.tonto.ontology.Manager;
import pm.ftm.tonto.model.OntologyException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

@Service
public final class OntologyHandler {

    /**
     * Ontology manager
     */
    private final Manager manager;

    public OntologyHandler() {
        this.manager = new Manager();
    }

    @Nonnull
    public static final String BASE_ONTOLOGY = "<?xml version=\"1.0\"?>\n" +
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

    /**
     * Return response
     *
     * @param request Request
     * @return Return response
     */
    public Response getResponse(Request request) {
        Response response;
        if (!request.isEmptyOntology()) {
            try {
                boolean loaded = false;
                if (request.isEmptyOntologyIRI()) {
                    loaded = manager.loadFromString(request.getOntology());
                } else {
                    loaded = manager.load(request.getOntologyIRI());
                    // loaded = manager.loadFromString(BASE_ONTOLOGY);
                }
                if (loaded) {
                    List<String> ask = manager.ask(request.getQuery());
                    if (ask.isEmpty()) {
                        response = new Response("Empty response");
                    } else {
                        response = new Response(ask);
                    }
                } else {
                    response = new Response("Ontology not loaded");
                }

            } catch (Exception exception) {
                response = new Response(exception);
            }
        } else {
            response = new Response("Ontology not found");
        }

        return response;
    }
}
