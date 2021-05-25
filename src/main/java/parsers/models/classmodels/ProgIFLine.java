package parsers.models.classmodels;

public class ProgIFLine {
    private Integer progIF;
    private String name;

    public ProgIFLine(){
        this.progIF = null;
        this.name = null;
    }

    public ProgIFLine(Integer progIF, String name) {
        this.progIF = progIF;
        this.name = name;
    }


    public Integer getProgIF() {
        return progIF;
    }

    public void setProgIF(Integer progIF) {
        this.progIF = progIF;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
