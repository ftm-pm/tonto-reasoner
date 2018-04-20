package pm.ftm.tonto.model;

import java.util.List;

public class ClassResponse {
    private List<String> subClasses;
    private List<String> superClasses;

    public ClassResponse() {
    }

    public ClassResponse(List<String> subClasses, List<String> superClasses) {
        this.subClasses = subClasses;
        this.superClasses = superClasses;
    }

    public List<String> getSubClasses() {
        return subClasses;
    }

    public void setSubClasses(List<String> subClasses) {
        this.subClasses = subClasses;
    }

    public List<String> getSuperClasses() {
        return superClasses;
    }

    public void setSuperClasses(List<String> superClasses) {
        this.superClasses = superClasses;
    }
}
