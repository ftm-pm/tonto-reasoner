package pm.ftm.tonto.model;

import java.util.List;

public class ObjectPropertyResponse {
    private List<String> subObjectProperties;
    private List<String> superObjectProperties;

    public ObjectPropertyResponse() {
    }

    public ObjectPropertyResponse(List<String> subObjectProperties, List<String> superObjectProperties) {
        this.subObjectProperties = subObjectProperties;
        this.superObjectProperties = superObjectProperties;
    }

    public List<String> getSubObjectProperties() {
        return subObjectProperties;
    }

    public void setSubObjectProperties(List<String> subObjectProperties) {
        this.subObjectProperties = subObjectProperties;
    }

    public List<String> getSuperObjectProperties() {
        return superObjectProperties;
    }

    public void setSuperObjectProperties(List<String> superObjectProperties) {
        this.superObjectProperties = superObjectProperties;
    }
}
