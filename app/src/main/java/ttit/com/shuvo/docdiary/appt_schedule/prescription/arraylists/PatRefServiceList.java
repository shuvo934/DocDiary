package ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists;

public class PatRefServiceList {
    private String drs_id;
    private String drs_drd_id;
    private String pfn_id;
    private String pfn_fee_name;
    private String drs_qty;
    private String depts_id;

    public PatRefServiceList(String drs_id, String drs_drd_id, String pfn_id, String pfn_fee_name, String drs_qty, String depts_id) {
        this.drs_id = drs_id;
        this.drs_drd_id = drs_drd_id;
        this.pfn_id = pfn_id;
        this.pfn_fee_name = pfn_fee_name;
        this.drs_qty = drs_qty;
        this.depts_id = depts_id;
    }

    public String getDrs_id() {
        return drs_id;
    }

    public void setDrs_id(String drs_id) {
        this.drs_id = drs_id;
    }

    public String getDrs_drd_id() {
        return drs_drd_id;
    }

    public void setDrs_drd_id(String drs_drd_id) {
        this.drs_drd_id = drs_drd_id;
    }

    public String getPfn_id() {
        return pfn_id;
    }

    public void setPfn_id(String pfn_id) {
        this.pfn_id = pfn_id;
    }

    public String getPfn_fee_name() {
        return pfn_fee_name;
    }

    public void setPfn_fee_name(String pfn_fee_name) {
        this.pfn_fee_name = pfn_fee_name;
    }

    public String getDrs_qty() {
        return drs_qty;
    }

    public void setDrs_qty(String drs_qty) {
        this.drs_qty = drs_qty;
    }

    public String getDepts_id() {
        return depts_id;
    }

    public void setDepts_id(String depts_id) {
        this.depts_id = depts_id;
    }
}
