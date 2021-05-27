package PIU.models;

import PIU.adapters.ProgIFAdapter;
import com.google.gson.annotations.JsonAdapter;

import java.util.Objects;

@JsonAdapter(ProgIFAdapter.class)
public class ProgIF implements Comparable<ProgIF>{
    private Integer progIF;
    private String name;

    public ProgIF(){
        this.progIF = null;
        this.name = null;
    }
    public ProgIF(int progIF, String name){
        this.progIF = progIF;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgIF progIF1 = (ProgIF) o;
        return Objects.equals(progIF, progIF1.progIF);
    }

    @Override
    public int hashCode() {
        return this.progIF.hashCode();
    }

    @Override
    public String toString(){
        return toLine();
    }

    public String toLine(){
        return String.format("\t\t%02x", this.progIF) + "  " + this.name;
    }
    @Override
    public int compareTo(ProgIF o){ return this.progIF - o.progIF; }

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
