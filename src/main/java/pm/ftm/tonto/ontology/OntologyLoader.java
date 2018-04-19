package pm.ftm.tonto.ontology;

import java.io.IOException;

public interface OntologyLoader {
    /**
     * Load ontology by IRI
     *
     * @param ontologyIRI Path to ontology
     * @return Is loaded
     * @throws IOException Throw error
     */
    boolean load(String ontologyIRI) throws IOException;

    /**
     * Load ontology from string
     *
     * @param ontology The ontology
     * @return Is loaded
     */
    boolean loadFromString(String ontology);
}
