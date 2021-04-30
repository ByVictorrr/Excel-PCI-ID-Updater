package models;

import org.apache.commons.lang3.StringUtils;


public class Device implements Comparable<Device>{
    private String desc;
    private PCI_ID ids;
    private long lineNum;

    public Device(String desc, String pci_ids){
        this.desc = desc;
        this.ids = new PCI_ID(pci_ids);
        this.lineNum = -1;

    }

    public long getLineNum() {
        return lineNum;
    }

    public boolean isValid(){
       return this.ids.isValid();
    }

    public void setLineNum(long lineNum) {
        this.lineNum = lineNum;
    }

    public String getDesc() {
        return desc;
    }

    public PCI_ID getIds() {
        return ids;
    }

    @Override
    public int compareTo(Device o) {
        return ids.compareTo(o.ids);
    }

    public String formatOutput() {
        String s_ven =  StringUtils.leftPad(Long.toHexString(this.getIds().getSven_id()), 4, "0");
        String s_dev = StringUtils.leftPad(Long.toHexString(this.getIds().getSdev_id()), 4, "0");
        return "\t\t" + s_ven
                + " " + s_dev
                + "  " + this.desc;
    }

    @Override
    public String toString() {
        return "Device{" +
                "desc='" + desc + '\'' +
                ", ids=" + ids +
                ", lineNum=" + lineNum +
                '}';
    }
}
