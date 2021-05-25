package models;

import adapters.DeviceAdapter;
import com.google.gson.annotations.JsonAdapter;

import java.util.PriorityQueue;
@JsonAdapter(DeviceAdapter.class)
public class Device implements Comparable<Device>{
    private Integer device;
    private String name;
    private PriorityQueue<SubSystem>subSystems;

    public Device(){
        this.device = null;
        this.name = null;
        this.subSystems = null;
    }

    public Device(Integer device, String name) {
        this.device = device;
        this.name = name;
        this.subSystems = null;
    }

    @Override
    public int compareTo(Device o) {
        return this.device - o.device;
    }

    public String toLine() {
        return "\t" + String.format("%04x", this.device) + "  " + this.name;
    }

    @Override
    public String toString() {
        String ret =toLine();
        if(this.subSystems != null){
            for(SubSystem sub: this.subSystems){
                ret+="\n"+sub.toString();
            }
        }
        return ret;
    }

    public Integer getDevice() {
        return device;
    }

    public String getName() {
        return name;
    }

    public PriorityQueue<SubSystem> getSubSystems() {
        return subSystems;
    }

    public void setDevice(Integer device) {
        this.device = device;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubSystems(PriorityQueue<SubSystem> subSystems) {
        this.subSystems = subSystems;
    }
    public void addSubSystem(SubSystem s){
        this.subSystems.add(s);
    }

    public int size(){
        if(subSystems == null)
            return 0;
        return subSystems.size();
    }
}
