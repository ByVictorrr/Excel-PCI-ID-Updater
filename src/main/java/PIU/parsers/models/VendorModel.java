package PIU.parsers.models;

import PIU.parsers.models.vendormodels.DeviceLine;
import PIU.parsers.models.vendormodels.SubSystemLine;
import PIU.parsers.models.vendormodels.VendorLine;

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