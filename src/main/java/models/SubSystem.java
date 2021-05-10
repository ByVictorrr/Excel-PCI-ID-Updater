package models;

public class SubSystem implements Comparable<SubSystem>{
    private int subVendor, subDevice;
    private String name;
    public SubSystem()
    {
        this.subVendor = -1;
        this.subDevice = -1;
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


    public int getSubVendor() {
        return subVendor;
    }

    public int getSubDevice() {
        return subDevice;
    }

    public String getName() {
        return name;
    }

    public void setSubVendor(int subVendor) {
        this.subVendor = subVendor;
    }

    public void setSubDevice(int subDevice) {
        this.subDevice = subDevice;
    }

    public void setName(String name) {
        this.name = name;
    }
}
