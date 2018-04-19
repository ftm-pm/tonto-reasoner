package pm.ftm.tonto.model;

import java.util.List;

public class Response {
    private String error;
    private List<String> body;

    public Response(List<String> body) {
        this.body = body;
    }

    public Response(Exception exception) {
        this.error = exception.getMessage();
    }

    public Response(String error) {
        this.error = error;
    }

    ///////////

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }
}
