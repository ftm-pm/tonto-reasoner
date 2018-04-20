package pm.ftm.tonto.model;

import java.util.List;

public class ObjectPropertyAssertionsResponse {
    private List<ObjectPropertyAssertionResponse> collection;

    public ObjectPropertyAssertionsResponse() {
    }

    public ObjectPropertyAssertionsResponse(List<ObjectPropertyAssertionResponse> collection) {
        this.collection = collection;
    }

    public List<ObjectPropertyAssertionResponse> getCollection() {
        return collection;
    }

    public void setCollection(List<ObjectPropertyAssertionResponse> collection) {
        this.collection = collection;
    }

    public void addCollection(ObjectPropertyAssertionResponse objectPropertyAssertion) {
        this.collection.add(objectPropertyAssertion);
    }

    public void removeCollection(ObjectPropertyAssertionResponse objectPropertyAssertion) {
        this.collection.remove(objectPropertyAssertion);
    }
}
