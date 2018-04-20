package pm.ftm.tonto.ontology;

import pm.ftm.tonto.model.OntologyException;
import pm.ftm.tonto.model.Query;
import pm.ftm.tonto.model.ResultResponse;

public interface QueryReasoner {
    /**
     * Get response on question
     *
     * @param query The query
     * @return Reasoner answer
     * @throws OntologyException If ontology is not load
     */
    ResultResponse ask(Query query) throws OntologyException;
}
