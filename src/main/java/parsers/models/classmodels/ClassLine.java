package parsers.models.classmodels;

public class ClassLine {
    private Integer _class;
    private String name;

    public ClassLine(int _class, String name) {
        this._class = _class;
        this.name = name;
    }

    public ClassLine() {
        this._class = null;
        this.name = null;
    }

    public Integer get_class() {
        return _class;
    }

    public void set_class(Integer _class) {
        this._class = _class;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
