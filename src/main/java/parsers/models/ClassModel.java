package parsers.models;

import models.SubSystem;

public class ClassModel {
    private Integer _class, subClass, progIF;
    private ClassModel instance;

    private ClassModel(){
        this.resetModel();
    }
    public ClassModel getInstance() {
        if(instance == null){
            this.instance = new ClassModel();
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

}
