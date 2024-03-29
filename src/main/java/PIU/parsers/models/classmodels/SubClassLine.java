package PIU.parsers.models.classmodels;

public class SubClassLine {
    private Integer id;
    private String name;
    public SubClassLine(){
        this.name = null;
        this.id = null;
    }
    public SubClassLine(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
