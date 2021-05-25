package models;

import adapters.DeviceAdapter;
import adapters.VendorAdapter;
import com.google.gson.annotations.JsonAdapter;
import org.apache.commons.lang3.StringUtils;

import java.util.PriorityQueue;


@JsonAdapter(VendorAdapter.class)
public class Vendor implements Comparable<Vendor>{
    private int vendor;
    private String name;
    private PriorityQueue<Device> devices;


    public Vendor(){
        this.vendor = -1;
        this.name = null;
        this.devices = null;
    }


    public Vendor(int vendor, String name){
        this.vendor = vendor;
        this.name = name;
        this.devices = null;
    }

    public int getVendor() {
        return vendor;
    }

    public String getName() {
        return name;
    }

    public PriorityQueue<Device> getDevices() {
        return devices;
    }

    public void setVendor(int vendor) {
        this.vendor = vendor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDevices(PriorityQueue<Device> devices) {
        this.devices = devices;
    }

    public void addDevice(Device d){
        this.devices.add(d);
    }

    @Override
    public int compareTo(Vendor o) {
        return this.vendor - o.vendor;
    }

    @Override
    public String toString() {
        String ret = toLine();
        if(this.devices != null) {
            for(Device dev: this.devices){
                ret += "\n" + dev.toString();
            }
        }
        return ret;
    }
    public String toLine(){
        return String.format("%04x", this.vendor) + "  " + this.name;
    }
    public int size(){
        if(devices == null){
           return 0;
        }
        return devices.size();
    }



}
