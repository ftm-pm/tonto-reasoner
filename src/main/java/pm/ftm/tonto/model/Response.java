package pm.ftm.tonto.model;

public class Response {
    private String error;
    private ResultResponse body;

    public Response(ResultResponse body) {
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

    public ResultResponse getBody() {
        return body;
    }

    public void setBody(ResultResponse body) {
        this.body = body;
    }
}
