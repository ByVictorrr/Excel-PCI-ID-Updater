package models;

import adapters.ClassAdapter;
import adapters.DeviceAdapter;
import com.google.gson.annotations.JsonAdapter;

import java.util.PriorityQueue;

@JsonAdapter(ClassAdapter.class)
public class Class implements Comparable<Class>{
    private int _class;
    private String name;
    private PriorityQueue<SubClass> subClasses;

    public Class(){
        this._class = -1;
        this.name = null;
        this.subClasses = null;
    }


    public Class(int _class, String name){
        this._class = _class;
        this.name = name;
        this.subClasses = null;
    }
    public void addSubClass(SubClass s){this.subClasses.add(s);}


    @Override
    public int compareTo(Class o){
        return this._class - o._class;
    }

    public int size(){
        if(this.subClasses != null)
            return subClasses.size();
        return 0;
    }
    @Override
    public String toString() {
        String ret = toLine();
        if(this.subClasses != null) {
            for(SubClass subClass: this.subClasses){
                ret += "\n" + subClass.toString();
            }
        }
        return ret;
    }
    public String toLine(){
        return String.format("C %02x", this._class) + "  " + this.name;
    }


    public int get_class() {
        return _class;
    }

    public void set_class(int _class) {
        this._class = _class;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PriorityQueue<SubClass> getSubClasses() {
        return subClasses;
    }

    public void setSubClasses(PriorityQueue<SubClass> subClasses) {
        this.subClasses = subClasses;
    }
}
