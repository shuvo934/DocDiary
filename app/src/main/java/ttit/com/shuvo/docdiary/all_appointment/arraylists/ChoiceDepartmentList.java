package ttit.com.shuvo.docdiary.all_appointment.arraylists;

public class ChoiceDepartmentList {
    private String deptd_id;
    private String deptd_name;

    public ChoiceDepartmentList(String deptd_id, String deptd_name) {
        this.deptd_id = deptd_id;
        this.deptd_name = deptd_name;
    }

    public String getDeptd_id() {
        return deptd_id;
    }

    public void setDeptd_id(String deptd_id) {
        this.deptd_id = deptd_id;
    }

    public String getDeptd_name() {
        return deptd_name;
    }

    public void setDeptd_name(String deptd_name) {
        this.deptd_name = deptd_name;
    }
}
