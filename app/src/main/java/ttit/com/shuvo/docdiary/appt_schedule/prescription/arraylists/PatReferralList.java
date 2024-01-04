package ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists;

public class PatReferralList {
    private String drd_id;
    private String drd_pdi_id;
    private String depts_id;
    private String depts_name;
    private String doc_id;
    private String doc_name;
    private String child_count;

    public PatReferralList(String drd_id, String drd_pdi_id, String depts_id, String depts_name, String doc_id, String doc_name, String child_count) {
        this.drd_id = drd_id;
        this.drd_pdi_id = drd_pdi_id;
        this.depts_id = depts_id;
        this.depts_name = depts_name;
        this.doc_id = doc_id;
        this.doc_name = doc_name;
        this.child_count = child_count;
    }

    public String getDrd_id() {
        return drd_id;
    }

    public void setDrd_id(String drd_id) {
        this.drd_id = drd_id;
    }

    public String getDrd_pdi_id() {
        return drd_pdi_id;
    }

    public void setDrd_pdi_id(String drd_pdi_id) {
        this.drd_pdi_id = drd_pdi_id;
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

    public String getChild_count() {
        return child_count;
    }

    public void setChild_count(String child_count) {
        this.child_count = child_count;
    }
}
