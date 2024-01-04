package ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists;

public class ReferralUnitList {
    private String depts_id;
    private String depts_name;
    private String deptd_name;
    private String depts_desc;
    private String deptm_name;

    public ReferralUnitList(String depts_id, String depts_name, String deptd_name, String depts_desc, String deptm_name) {
        this.depts_id = depts_id;
        this.depts_name = depts_name;
        this.deptd_name = deptd_name;
        this.depts_desc = depts_desc;
        this.deptm_name = deptm_name;
    }

    public String getDepts_id() {
        return depts_id;
    }

    public void setDepts_id(String depts_id) {
        this.depts_id = depts_id;
    }

    public String getDepts_name() {
        return depts_name;
    }

    public void setDepts_name(String depts_name) {
        this.depts_name = depts_name;
    }

    public String getDeptd_name() {
        return deptd_name;
    }

    public void setDeptd_name(String deptd_name) {
        this.deptd_name = deptd_name;
    }

    public String getDepts_desc() {
        return depts_desc;
    }

    public void setDepts_desc(String depts_desc) {
        this.depts_desc = depts_desc;
    }

    public String getDeptm_name() {
        return deptm_name;
    }

    public void setDeptm_name(String deptm_name) {
        this.deptm_name = deptm_name;
    }
}
