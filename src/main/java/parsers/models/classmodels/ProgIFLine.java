package parsers.models.classmodels;

public class ProgIFLine {
    private Integer id;
    private String name;

    public ProgIFLine(){
        this.id = null;
        this.name = null;
    }

    public ProgIFLine(Integer id, String name) {
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
