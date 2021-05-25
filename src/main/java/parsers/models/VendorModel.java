package parsers.models;

import exceptions.LineOutOfOrderException;
import models.Device;
import models.SubSystem;
import models.Vendor;
import parsers.models.vendormodels.DeviceLine;
import parsers.models.vendormodels.SubSystemLine;
import parsers.models.vendormodels.VendorLine;

// should line parser produce a variable to hold everything
    /* NOTES:
        Vendor: is never null whenever device, sdevice, or svendor is set
        Device: is never null whenever sdevice, svendor is set
        sDevice && sVendor : always are null or not null togher
     */
public class VendorModel{
    private SubSystemLine sub;
    private DeviceLine dev;
    private VendorLine ven;
    private static VendorModel instance;


    public static final int CONTINUE =  -1;
    public static final int EQUAL =  0; // EQUAL mean continue unless overiding enabled
    public static final int WRITE =  1;

    private VendorModel(){ this.resetModel();}


    static public VendorModel getInstance()
    {
        if(instance == null){
            instance = new VendorModel();
        }
        return instance;
    }

    /* Because sDevice and sVendor are never set seperately the size will be 0,1 or 4*/
    private int size(){
        if(ven == null){
            return 0;
        }else if(dev == null) {
            return 1;
        }else if(sub == null){
            return 2;
        }else{
            return 4;
        }
    }


    public int compareTo(SubSystem to){
        int ret = WRITE;
        if(this.sub.getvId() < to.getSubVendor()) {
            ret = CONTINUE;
        }else if(this.sub.getvId() == to.getSubVendor()) {
            if (this.sub.getdId() < to.getSubDevice()) {
                ret = CONTINUE;
            } else if (this.sub.getdId() == to.getSubDevice()){
                return EQUAL;
            }else {
                ret = WRITE;
            }
        }else{
            ret = WRITE;
        }
        return ret;
    }

    public int compareTo(Device to){
        int ret = CONTINUE;
        if(this.dev.getId() < to.getDevice()) {
            ret = CONTINUE;
        }else if( this.dev.getId() == to.getDevice()){
            if(to.size() > 0 && this.sub != null)
                ret = this.compareTo(to.getSubSystems().peek());
            else if(to.getSubSystems().isEmpty() && this.sub != null)
                ret = EQUAL;
            else ret = WRITE;
        }
        return ret;
    }


    public int compareTo(Vendor to){

        int ret = CONTINUE;
        if(this.ven.getId() < to.getVendor()) {
            ret = CONTINUE;
        }else if( this.ven.getId() == to.getVendor()){
            if(to.size() > 0 && this.dev != null){
                ret = this.compareTo(to.getDevices().peek());
            }else if(to.getDevices().isEmpty() && this.dev == null) {
                ret = EQUAL;
            }else{
                ret = CONTINUE;
            }
        }else{
            ret = WRITE; // means write vendor
        }

        return ret;
    }

    public void resetModel(){
        ven = null;
        dev = null;
        sub = null;
    }

    public SubSystemLine getSub() {
        return sub;
    }

    public void setSub(SubSystemLine sub) {
        this.sub = sub;
    }

    public DeviceLine getDev() {
        return dev;
    }

    public void setDev(DeviceLine dev) {
        this.dev = dev;
    }

    public VendorLine getVen() {
        return ven;
    }

    public void setVen(VendorLine ven) {
        this.ven = ven;
    }
}