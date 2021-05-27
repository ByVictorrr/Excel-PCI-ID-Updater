package PIU.parsers.models.classmodels;

public class ClassLine {
    private Integer id;
    private String name;

    public ClassLine(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ClassLine() {
        this.id = null;
        this.name = null;
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
