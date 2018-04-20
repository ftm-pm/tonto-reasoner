package pm.ftm.tonto.model;

import java.util.List;

public class NamedIndividualResponse {
    private List<String> collection;

    public NamedIndividualResponse() {}

    public NamedIndividualResponse(List<String> collection) {
        this.collection = collection;
    }

    public List<String> getCollection() {
        return collection;
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
    }

    public void addCollection(String namedIndividual) {
        this.collection.add(namedIndividual);
    }

    public void removeCollection(String namedIndividual) {
        this.collection.remove(namedIndividual);
    }
}
