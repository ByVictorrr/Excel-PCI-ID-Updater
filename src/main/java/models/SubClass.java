package models;

import adapters.SubClassAdapter;
import com.google.gson.annotations.JsonAdapter;

import java.util.PriorityQueue;

@JsonAdapter(SubClassAdapter.class)
public class SubClass implements Comparable<SubClass>{
    private Integer subClass;
    private String name;
    private PriorityQueue<ProgIF> progIFS;
    public SubClass(){
        subClass = null;
        name = null;
        progIFS = null;
    }
    public SubClass(int sc, String name){
        this.subClass = sc;
        this.name = name;
        this.progIFS = null;
    }

    @Override
    public int compareTo(SubClass sc){
        return this.subClass - sc.subClass;
    }
    public void addProgIF(ProgIF prog){ this.progIFS.add(prog); }
    public int size(){
        if(this.progIFS != null)
            return this.progIFS.size();
        return 0;
    }


    public Integer getSubClass() {
        return subClass;
    }

    public void setSubClass(Integer subClass) {
        this.subClass = subClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PriorityQueue<ProgIF> getProgIFS() {
        return progIFS;
    }

    public void setProgIFS(PriorityQueue<ProgIF> progIFS) {
        this.progIFS = progIFS;
    }
}
