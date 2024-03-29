package PIU.models;

import PIU.adapters.SubClassAdapter;
import PIU.utilities.UniquePriorityQueue;
import com.google.gson.annotations.JsonAdapter;

import java.util.Objects;

@JsonAdapter(SubClassAdapter.class)
public class SubClass implements Comparable<SubClass>{
    private Integer subClass;
    private String name;
    private UniquePriorityQueue<ProgIF> progIFS;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubClass subClass1 = (SubClass) o;
        return Objects.equals(subClass, subClass1.subClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subClass);
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
    @Override
    public String toString() {
        String ret;
        String progIFsToString = progIFsToString();
        if(progIFsToString == null) ret = toLine();
        else ret = toLine() + progIFsToString;
        return ret;
    }

    public String progIFsToString(){
        String ret = "";
        if(this.progIFS == null) return null;
        for(ProgIF p: this.progIFS){
            ret += "\n" + p.toString();
        }
        return ret;

    }
    public String toLine(){
        return String.format("\t%02x", this.subClass) + "  " + this.name;
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

    public UniquePriorityQueue<ProgIF> getProgIFS() {
        return progIFS;
    }

    public void setProgIFS(UniquePriorityQueue<ProgIF> progIFS) {
        this.progIFS = progIFS;
    }
}
