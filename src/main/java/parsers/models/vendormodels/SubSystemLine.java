package parsers.models.vendormodels;

public class SubSystemLine {
    private Integer vId, dId;
    private String name;

    public SubSystemLine(Integer vId, Integer dId, String name) {
        this.vId = vId;
        this.dId = dId;
        this.name = name;
    }
    public SubSystemLine(){
        this.vId = null;
        this.dId = null;
        this.name = null;
    }


    public Integer getvId() {
        return vId;
    }

    public void setvId(Integer vId) {
        this.vId = vId;
    }

    public Integer getdId() {
        return dId;
    }

    public void setdId(Integer dId) {
        this.dId = dId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
