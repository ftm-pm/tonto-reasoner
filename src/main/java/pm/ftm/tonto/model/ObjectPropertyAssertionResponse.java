package pm.ftm.tonto.model;

public class ObjectPropertyAssertionResponse {
    private String objectProperty;
    private String namedIndividualFirst;
    private String namedIndividualSecond;

    public ObjectPropertyAssertionResponse() {
    }

    public ObjectPropertyAssertionResponse(String objectProperty, String namedIndividualFirst, String namedIndividualSecond) {
        this.objectProperty = objectProperty;
        this.namedIndividualFirst = namedIndividualFirst;
        this.namedIndividualSecond = namedIndividualSecond;
    }

    public String getObjectProperty() {
        return objectProperty;
    }

    public void setObjectProperty(String objectProperty) {
        this.objectProperty = objectProperty;
    }

    public String getNamedIndividualFirst() {
        return namedIndividualFirst;
    }

    public void setNamedIndividualFirst(String namedIndividualFirst) {
        this.namedIndividualFirst = namedIndividualFirst;
    }

    public String getNamedIndividualSecond() {
        return namedIndividualSecond;
    }

    public void setNamedIndividualSecond(String namedIndividualSecond) {
        this.namedIndividualSecond = namedIndividualSecond;
    }
}
