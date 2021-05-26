package parsers.models;

import models.*;
import models.Class;
import parsers.models.classmodels.ClassLine;
import parsers.models.classmodels.ProgIFLine;
import parsers.models.classmodels.SubClassLine;

public class ClassModel {
    private ClassLine _class;
    private SubClassLine subClass;
    private ProgIFLine progIF;


    private static ClassModel instance;

    private ClassModel(){
        this.resetModel();
    }
    public static ClassModel getInstance() {
        if(instance == null){
            instance = new ClassModel();
        }
        return instance;
    }

    public void resetModel(){
        this._class = null;
        this.subClass = null;
        this.progIF = null;
    }

    public ClassLine get_class() {
        return _class;
    }

    public void set_class(ClassLine _class) {
        this._class = _class;
    }

    public SubClassLine getSubClass() {
        return subClass;
    }

    public void setSubClass(SubClassLine subClass) {
        this.subClass = subClass;
    }

    public ProgIFLine getProgIF() {
        return progIF;
    }

    public void setProgIF(ProgIFLine progIF) {
        this.progIF = progIF;
    }

    public static void setInstance(ClassModel instance) {
        ClassModel.instance = instance;
    }
}
