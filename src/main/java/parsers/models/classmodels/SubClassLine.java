package parsers.models.classmodels;

public class SubClassLine {
    private Integer subClass;
    private String name;
    public SubClassLine(){
        this.name = null;
        this.subClass = null;
    }
    public SubClassLine(Integer subClass, String name) {
        this.subClass = subClass;
        this.name = name;
    }

    public Integer getSubClass() {
        return subClass;
    }

    public void setSubClass(Integer subClass) {
        this.subClass = subClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
