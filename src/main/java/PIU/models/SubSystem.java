package PIU.models;

import PIU.adapters.SubSystemAdapter;
import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(SubSystemAdapter.class)
public class SubSystem implements Comparable<SubSystem>{
    private Integer subVendor;
    private Integer subDevice;
    private String name;
    public SubSystem()
    {
        this.subVendor = null;
        this.subDevice = null;
        this.name = null;
    }

    public SubSystem(int sv, int sd, String name)
    {
        this.subVendor = sv;
        this.subDevice = sd;
        this.name = name;
    }
    @Override
    public int compareTo(SubSystem o) {
        if(this.subVendor < o.subVendor) {
            return -1;
        }else if(this.subVendor == o.subVendor){
            if(this.subDevice == o.subVendor){
                return 0;
            }else if (this.subDevice < o.subDevice) {
                return -1;
            }else{
                return 1;
            }
        }else{
            return 1;
        }
    }

    @Override
    public String toString() {
        return toLine();
    }
    public String toLine() {
        return "\t\t" + String.format("%04x",this.subVendor) + " " + String.format("%04x",this.subDevice) + "  " + this.name;
    }


    public Integer getSubVendor() {
        return subVendor;
    }

    public Integer getSubDevice() {
        return subDevice;
    }

    public String getName() {
        return name;
    }

    public void setSubVendor(Integer subVendor) {
        this.subVendor = subVendor;
    }

    public void setSubDevice(Integer subDevice) {
        this.subDevice = subDevice;
    }

    public void setName(String name) {
        this.name = name;
    }
}
