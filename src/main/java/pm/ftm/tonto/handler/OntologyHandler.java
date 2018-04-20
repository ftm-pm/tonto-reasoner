package pm.ftm.tonto.handler;

import org.springframework.stereotype.Service;
import pm.ftm.tonto.model.Request;
import pm.ftm.tonto.model.Response;
import pm.ftm.tonto.model.ResultResponse;
import pm.ftm.tonto.ontology.Manager;

@Service
public final class OntologyHandler {

    /**
     * Ontology manager
     */
    private final Manager manager;

    public OntologyHandler() {
        this.manager = new Manager();
    }

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
                boolean loaded;
                if (request.isEmptyOntologyIRI()) {
                    loaded = manager.loadFromString(request.getOntology());
                } else {
                    loaded = manager.load(request.getOntologyIRI());
                }
                if (loaded) {
                    ResultResponse ask = manager.ask(request.getQuery());
                    response = new Response(ask);
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
