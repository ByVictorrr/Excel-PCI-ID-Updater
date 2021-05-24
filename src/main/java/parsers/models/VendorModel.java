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
        int ret = 0;
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
        int ret = WRITE;
        if(this.dev.getId() < to.getDevice()) {
            ret = CONTINUE; // means continue and dont write ye
        }else if( this.dev.getId() == to.getDevice()){
            if(this.size() == 2){
                ret = EQUAL;
            }else if(to.getSubSystems().size() == 0){
                ret = CONTINUE;
            }else{
                ret = this.compareTo(to.getSubSystems().peek());
            }
        }else{
            ret = WRITE;
        }
        return ret;
    }


    public int compareTo(Vendor to){

        int ret = 0;
        // Case 0 - equal meaning size == 1 and samw vendor
        // Case 1 - if lined vendor is less than the pending vendor
        if(this.ven.getId() < to.getVendor()) {
            ret = CONTINUE; // means continue and dont write yet
        }else if( this.ven.getId() == to.getVendor()){
            // Case where we havent read a device yet
            if(this.size() == 1){
                ret = EQUAL;
            }else if(to.getDevices().size() == 0){
                ret = CONTINUE;
            }else {
                ret = this.compareTo(to.getDevices().peek());
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