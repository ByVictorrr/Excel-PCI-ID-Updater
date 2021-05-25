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

    public static final int CONTINUE =  -1;
    public static final int EQUAL =  0; // EQUAL mean continue unless overiding enabled
    public static final int WRITE =  1;

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
    public int compareTo(SubClass to) {

        int ret = WRITE;
        /*
        if(this.subClass.getSubClass() < to.getSubClass()) {
            ret = CONTINUE; // means continue and dont write ye
        }else if( this.subClass.getSubClass() == to.getSubClass()){
            if(this.size() == 2){
                ret = EQUAL;
            }else if(to.getSubSystems().size() == 0){
                ret = CONTINUE;
            }else{
                ret = this.compareTo(to.getSubSystems().peek());
            }
        }else{
            ret = WRITE;
        }

         */
        return ret;
    }



    public int compareTo(Class to){

        int ret = CONTINUE;
        if(this._class.get_class() < to.get_class()) {
            ret = CONTINUE;
        }else if( this._class.get_class() == to.get_class()){
            if(to.getSubClasses().size() > 0){
                ret = this.compareTo(to.getSubClasses().peek());
            }else if(to.getSubClasses().size() == 0){
                ret = EQUAL;
            }
        }else{
            ret = WRITE; // means write vendor
        }

        return ret;
    }
}
