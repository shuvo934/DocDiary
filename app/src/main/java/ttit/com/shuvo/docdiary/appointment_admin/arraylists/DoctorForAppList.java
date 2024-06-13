package ttit.com.shuvo.docdiary.appointment_admin.arraylists;

public class DoctorForAppList {
    private String doc_id;
    private String doc_name;
    private String doc_code;
    private String depts_id;
    private String depts_name;
    private String deptd_id;
    private String deptd_code;
    private String deptd_name;
    private String desig_id;
    private String desig_name;

    public DoctorForAppList(String doc_id, String doc_name, String doc_code, String depts_id, String depts_name, String deptd_id, String deptd_code, String deptd_name, String desig_id, String desig_name) {
        this.doc_id = doc_id;
        this.doc_name = doc_name;
        this.doc_code = doc_code;
        this.depts_id = depts_id;
        this.depts_name = depts_name;
        this.deptd_id = deptd_id;
        this.deptd_code = deptd_code;
        this.deptd_name = deptd_name;
        this.desig_id = desig_id;
        this.desig_name = desig_name;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getDoc_code() {
        return doc_code;
    }

    public void setDoc_code(String doc_code) {
        this.doc_code = doc_code;
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

    public String getDeptd_id() {
        return deptd_id;
    }

    public void setDeptd_id(String deptd_id) {
        this.deptd_id = deptd_id;
    }

    public String getDeptd_code() {
        return deptd_code;
    }

    public void setDeptd_code(String deptd_code) {
        this.deptd_code = deptd_code;
    }

    public String getDeptd_name() {
        return deptd_name;
    }

    public void setDeptd_name(String deptd_name) {
        this.deptd_name = deptd_name;
    }

    public String getDesig_id() {
        return desig_id;
    }

    public void setDesig_id(String desig_id) {
        this.desig_id = desig_id;
    }

    public String getDesig_name() {
        return desig_name;
    }

    public void setDesig_name(String desig_name) {
        this.desig_name = desig_name;
    }
}
