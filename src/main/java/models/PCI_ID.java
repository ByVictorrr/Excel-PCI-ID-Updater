package models;

import org.apache.commons.lang3.StringUtils;
import parsers.IDParser;

import java.util.Comparator;
import java.util.List;

import static parsers.IDParser.*;

public class PCI_ID implements Comparable<PCI_ID>{
    private Long ven_id, dev_id, sven_id, sdev_id;

    public PCI_ID(String pci_id){
        List<Long> ids;
        if ((ids= IDParser.getExcelIDS(pci_id)) != null){
            this.ven_id = ids.get(VEN);
            this.dev_id = ids.get(DEV);
            this.sven_id = ids.get(SVEN);
            this.sdev_id = ids.get(SDEV);
        }else{
            this.ven_id = null;
            this.dev_id = null;
            this.sven_id = null;
            this.sdev_id = null;
        }
    }
    public PCI_ID(){
        this.ven_id = new Long(-1);
        this.dev_id = new Long(-1);
        this.sven_id = new Long(-1);
        this.sdev_id = new Long(-1);
    }

    public boolean isValid(){
       return this.ven_id != null && this.dev_id != null
               && this.sven_id != null && this.sdev_id != null;
    }

    public Long getVen_id() {
        return ven_id;
    }

    public Long getDev_id() {
        return dev_id;
    }

    public Long getSven_id() {
        return sven_id;
    }

    public Long getSdev_id() {
        return sdev_id;
    }


    public void setVen_id(Long ven_id) {
        this.ven_id = ven_id;
    }

    public void setDev_id(Long dev_id) {
        this.dev_id = dev_id;
    }

    public void setSven_id(Long sven_id) {
        this.sven_id = sven_id;
    }

    public void setSdev_id(Long sdev_id) {
        this.sdev_id = sdev_id;
    }

    @Override
    public int compareTo(PCI_ID o) {
        Comparator comparator = Comparator.comparing(PCI_ID::getVen_id)
                .thenComparing(PCI_ID::getDev_id)
                .thenComparing(PCI_ID::getSven_id)
                .thenComparing(PCI_ID::getSdev_id);
        return comparator.compare(this, o);
    }

    @Override
    public String toString() {
        return "PCI_ID{" +
                "ven_id=" + StringUtils.leftPad(Long.toHexString(ven_id),4,"0") +
                ", dev_id=" + StringUtils.leftPad(Long.toHexString(dev_id), 4, "0") +
                ", sven_id=" + StringUtils.leftPad(Long.toHexString(sven_id), 4, "0") +
                ", sdev_id=" + StringUtils.leftPad(Long.toHexString(sdev_id), 4, "0") +
                '}';
    }
}
