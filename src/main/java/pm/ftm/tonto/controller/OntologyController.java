package pm.ftm.tonto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import pm.ftm.tonto.handler.OntologyHandler;
import pm.ftm.tonto.model.Request;
import pm.ftm.tonto.model.Response;

@RestController
public class OntologyController {

    /**
     * The Ontology handler
     */
    private final OntologyHandler ontologyHandler;

    @Autowired
    public OntologyController(OntologyHandler ontologyHandler) {
        this.ontologyHandler = ontologyHandler;
    }

    /**
     * @param request Request model
     * @return Return response
     */
    @RequestMapping(value = "/api/ontology", method = RequestMethod.POST, produces = "application/json")
    public Response index(@RequestBody Request request) {
        return ontologyHandler.getResponse(request);
    }
}