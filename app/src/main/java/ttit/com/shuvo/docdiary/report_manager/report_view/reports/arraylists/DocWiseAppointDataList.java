package ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists;

public class DocWiseAppointDataList {
    private String deptd_id;
    private String depts_id;
    private String depts_name;
    private String doc_id;
    private String doc_name;
    private String regular_count;
    private String extra_count;
    private String block_count;
    private String blank_count;

    public DocWiseAppointDataList(String deptd_id, String depts_id, String depts_name, String doc_id, String doc_name, String regular_count, String extra_count, String block_count, String blank_count) {
        this.deptd_id = deptd_id;
        this.depts_id = depts_id;
        this.depts_name = depts_name;
        this.doc_id = doc_id;
        this.doc_name = doc_name;
        this.regular_count = regular_count;
        this.extra_count = extra_count;
        this.block_count = block_count;
        this.blank_count = blank_count;
    }

    public String getDeptd_id() {
        return deptd_id;
    }

    public void setDeptd_id(String deptd_id) {
        this.deptd_id = deptd_id;
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

    public String getRegular_count() {
        return regular_count;
    }

    public void setRegular_count(String regular_count) {
        this.regular_count = regular_count;
    }

    public String getExtra_count() {
        return extra_count;
    }

    public void setExtra_count(String extra_count) {
        this.extra_count = extra_count;
    }

    public String getBlock_count() {
        return block_count;
    }

    public void setBlock_count(String block_count) {
        this.block_count = block_count;
    }

    public String getBlank_count() {
        return blank_count;
    }

    public void setBlank_count(String blank_count) {
        this.blank_count = blank_count;
    }
}
