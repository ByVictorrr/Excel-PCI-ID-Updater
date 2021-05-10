package parsers.models;

import models.Device;
import models.SubSystem;
import models.Vendor;

// should line parser produce a variable to hold everything
    /* NOTES:
        Vendor: is never null whenever device, sdevice, or svendor is set
        Device: is never null whenever sdevice, svendor is set
        sDevice && sVendor : always are null or not null togher
     */
public class VendorModel{
    private Integer vendor, device, sVendor, sDevice;
    private String vName, dName, sName ;
    private VendorModel instance;
    public static final int CONTINUE =  -1;
    public static final int EQUAL =  0; // EQUAL mean continue unless overiding enabled
    public static final int WRITE =  1;
    private VendorModel(){}
    /* Because sDevice and sVendor are never set seperately the size will be 0,1 or 4*/
    private int size() {
        if(vendor == null) {
            return 0;
        }else if(device == null) {
            return 1;
        }else if(sDevice == null && sDevice == null){
            return 2;
        }else{
            return 4;
        }
    }

    public VendorModel getInstance()
    {
        if(instance == null){
            this.instance = new VendorModel();
        }
        return instance;
    }

    public int compareTo(SubSystem to){
        int ret = 0;
        if(this.sVendor < to.getSubVendor()) {
            ret = CONTINUE;
        }else if(this.sVendor == to.getSubVendor()) {
            if (this.sDevice < to.getSubDevice()) {
                ret = CONTINUE;
            } else if (this.sDevice == to.getSubDevice()){
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
        if(this.device < to.getDevice()) {
            ret = CONTINUE; // means continue and dont write ye
        }else if( this.device == to.getDevice()){
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

    // handle vendorarr.size() in other
    public int compareTo(Vendor to){

        int ret = 0;
        // Case 0 - equal meaning size == 1 and samw vendor
        // Case 1 - if lined vendor is less than the pending vendor
        if(this.vendor < to.getVendor()) {
            ret = CONTINUE; // means continue and dont write yet
        }else if( this.vendor == to.getVendor()){
            // Case where we havent read a device yet
            if(this.size() == 1){
                ret = EQUAL;
            }
            else if(to.getDevices().size() == 0){
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
        vendor = null;
        device = null;
        sVendor = null;
        sDevice = null;
    }


}