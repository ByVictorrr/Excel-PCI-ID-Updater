package models;

import adapters.ClassAdapter;
import com.google.gson.annotations.JsonAdapter;

import java.util.PriorityQueue;

@JsonAdapter(ClassAdapter.class)
public class Class implements Comparable<Class>{
    private Integer _class;
    private String name;
    private PriorityQueue<SubClass> subClasses;

    public Class(){
        this._class = null;
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
        String ret;
        String subClassesToString = subClassesToString();
        if(subClassesToString == null) ret = toLine();
        else ret = toLine() + subClassesToString;
        return ret;
    }

    public String subClassesToString(){
        String ret = "";
        if(this.subClasses == null) return ret;
        for(SubClass s: this.subClasses){
            ret += "\n" + s.toString();
        }
        return ret;

    }
    public String toLine(){
        return String.format("C %02x", this._class) + "  " + this.name;
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

    public PriorityQueue<SubClass> getSubClasses() {
        return subClasses;
    }

    public void setSubClasses(PriorityQueue<SubClass> subClasses) {
        this.subClasses = subClasses;
    }
}
