package pm.ftm.tonto.model;

public class ResultResponse {
    private ClassResponse classResponse;
    private ObjectPropertyResponse objectPropertyResponse;
    private NamedIndividualResponse namedIndividualResponse;
    private ObjectPropertyAssertionsResponse objectPropertyAssertionsResponse;

    public ResultResponse() {
    }

    public ResultResponse(ClassResponse classResponse,
                          ObjectPropertyResponse objectPropertyResponse,
                          NamedIndividualResponse namedIndividualResponse,
                          ObjectPropertyAssertionsResponse objectPropertyAssertionsResponse) {
        this.namedIndividualResponse = namedIndividualResponse;
        this.classResponse = classResponse;
        this.objectPropertyResponse = objectPropertyResponse;
        this.objectPropertyAssertionsResponse = objectPropertyAssertionsResponse;
    }

    public NamedIndividualResponse getNamedIndividualResponse() {
        return namedIndividualResponse;
    }

    public void setNamedIndividualResponse(NamedIndividualResponse namedIndividualResponse) {
        this.namedIndividualResponse = namedIndividualResponse;
    }

    public ClassResponse getClassResponse() {
        return classResponse;
    }

    public void setClassResponse(ClassResponse classResponse) {
        this.classResponse = classResponse;
    }

    public ObjectPropertyResponse getObjectPropertyResponse() {
        return objectPropertyResponse;
    }

    public void setObjectPropertyResponse(ObjectPropertyResponse objectPropertyResponse) {
        this.objectPropertyResponse = objectPropertyResponse;
    }

    public ObjectPropertyAssertionsResponse getObjectPropertyAssertionsResponse() {
        return objectPropertyAssertionsResponse;
    }

    public void setObjectPropertyAssertionsResponse(ObjectPropertyAssertionsResponse objectPropertyAssertionsResponse) {
        this.objectPropertyAssertionsResponse = objectPropertyAssertionsResponse;
    }
}
