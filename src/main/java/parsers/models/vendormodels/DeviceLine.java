package parsers.models.vendormodels;

import parsers.models.VendorModel;

public class DeviceLine {
    private Integer id;
    private String name;

    public DeviceLine(Integer id, String name){
        this.id = id;
        this.name = name;
    }
    public DeviceLine(){
        this.id = null;
        this.name = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
