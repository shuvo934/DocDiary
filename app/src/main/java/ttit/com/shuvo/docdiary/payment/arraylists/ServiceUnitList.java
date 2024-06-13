package ttit.com.shuvo.docdiary.payment.arraylists;

public class ServiceUnitList {
    private String depts_name;
    private String depts_id;

    public ServiceUnitList(String depts_name, String depts_id) {
        this.depts_name = depts_name;
        this.depts_id = depts_id;
    }

    public String getDepts_name() {
        return depts_name;
    }

    public void setDepts_name(String depts_name) {
        this.depts_name = depts_name;
    }

    public String getDepts_id() {
        return depts_id;
    }

    public void setDepts_id(String depts_id) {
        this.depts_id = depts_id;
    }
}
