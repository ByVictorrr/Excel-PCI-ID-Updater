package PIU.models;

import PIU.adapters.VendorAdapter;
import PIU.utilities.UniquePriorityQueue;
import com.google.gson.annotations.JsonAdapter;

import java.util.Objects;


@JsonAdapter(VendorAdapter.class)
public class Vendor implements Comparable<Vendor>{
    private Integer vendor;
    private String name;
    private UniquePriorityQueue<Device> devices;


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

    public Integer getVendor() {
        return vendor;
    }

    public String getName() {
        return name;
    }

    public UniquePriorityQueue<Device> getDevices() {
        return devices;
    }

    public void setVendor(Integer vendor) {
        this.vendor = vendor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDevices(UniquePriorityQueue<Device> devices) {
        this.devices = devices;
    }

    public void addDevice(Device d){
        this.devices.add(d);
    }

    public int size(){
        if(devices == null) return 0;
        return devices.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vendor vendor1 = (Vendor) o;
        return Objects.equals(vendor, vendor1.vendor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendor);
    }

    @Override
    public int compareTo(Vendor o) {
        return this.vendor - o.vendor;
    }

    @Override
    public String toString() {
        String ret;
        String devicesToString = devicesToString();
        if(devicesToString == null) ret =  toLine();
        else ret = toLine() +  devicesToString;
        return ret;
    }
    public String devicesToString(){
        String ret = "";
        if(this.devices == null) return ret;
        for(Device d: this.devices){
           ret += "\n" + d.toString();
        }
        return ret;

    }
    public String toLine(){
        return String.format("%04x", this.vendor) + "  " + this.name;
    }



}
