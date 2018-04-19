package pm.ftm.tonto.model;

public class OntologyException extends Exception {
    private OntologyException() {
    }

    public OntologyException(String message) {
        super(message);
    }

    public OntologyException(String message, Throwable cause) {
        super(message, cause);
    }

    public OntologyException(Throwable cause) {
        super(cause);
    }
}
