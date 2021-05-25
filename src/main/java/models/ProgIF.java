package models;

import adapters.ProgIFAdapter;
import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(ProgIFAdapter.class)
public class ProgIF implements Comparable<ProgIF>{
    private int progIF;
    private String name;

    public ProgIF(){
        this.progIF = -1;
        this.name = null;
    }
    public ProgIF(int progIF, String name){
        this.progIF = progIF;
        this.name = name;
    }

    @Override
    public int compareTo(ProgIF o){ return this.progIF - o.progIF; }

    public int getProgIF() {
        return progIF;
    }

    public void setProgIF(int progIF) {
        this.progIF = progIF;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
