package parsers.models;

import models.SubSystem;

public class ClassModel {
    private Integer _class, subClass, progIF;
    private String className, subClassName, progIFName;
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
    private int size() {
        if(_class == null) {
            return 0;
        }else if(subClass == null) {
            return 1;
        }else if(progIF == null){
            return 2;
        }
        return 3;
    }
    public void resetModel(){
        this._class = null;
        this.subClass = null;
        this.progIF = null;
    }


    /*
    public int compareTo(){
        int ret = 0;
        if(this.sVendor < to.getSubVendor()) {
            ret = CONTINUE;
        }else if(this.sVendor == to.getSubVendor()) {
            if (this.sDevice < to.getSubDevice()) {
                ret = CONTINUE;
            } else if (this.sDevice == to.getSubDevice()){
                return EQUAL;
            }else {
                ret = WRITE;
            }
        }else{
            ret = WRITE;
        }
        return ret;
    }
     */

    public Integer get_class() {
        return _class;
    }

    public void set_class(Integer _class) {
        this._class = _class;
    }

    public Integer getSubClass() {
        return subClass;
    }

    public void setSubClass(Integer subClass) {
        this.subClass = subClass;
    }

    public Integer getProgIF() {
        return progIF;
    }

    public void setProgIF(Integer progIF) {
        this.progIF = progIF;
    }

    public static void setInstance(ClassModel instance) {
        ClassModel.instance = instance;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubClassName() {
        return subClassName;
    }

    public void setSubClassName(String subClassName) {
        this.subClassName = subClassName;
    }

    public String getProgIFName() {
        return progIFName;
    }

    public void setProgIFName(String progIFName) {
        this.progIFName = progIFName;
    }
}
