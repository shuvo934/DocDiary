package ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists;

public class ReferralDoctorList {
    private String doc_name;
    private String doc_code;
    private String desig_name;
    private String doc_id;

    public ReferralDoctorList(String doc_name, String doc_code, String desig_name, String doc_id) {
        this.doc_name = doc_name;
        this.doc_code = doc_code;
        this.desig_name = desig_name;
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

    public String getDesig_name() {
        return desig_name;
    }

    public void setDesig_name(String desig_name) {
        this.desig_name = desig_name;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }
}
