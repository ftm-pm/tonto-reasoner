package pm.ftm.tonto.model;

public class Request {
    private String ontologyIRI;
    private String ontology;
    private Query query;

    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public String getOntologyIRI() {
        return ontologyIRI;
    }

    public void setOntologyIRI(String ontologyIRI) {
        this.ontologyIRI = ontologyIRI;
    }

    public boolean isEmptyOntology() {
        return ontology == null && ontologyIRI == null;
    }

    public boolean isEmptyOntologyIRI() {
        return ontologyIRI == null;
    }
}
