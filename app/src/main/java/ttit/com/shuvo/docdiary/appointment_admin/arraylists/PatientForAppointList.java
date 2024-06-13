package ttit.com.shuvo.docdiary.appointment_admin.arraylists;

public class PatientForAppointList {
    private String ph_id;
    private String ph_pat_id;
    private String ph_sub_code;
    private String ph_date;
    private String ph_patient_cat_id;
    private String ph_patient_cat;
    private String pat_code;
    private String pat_name;
    private String pat_phone;

    public PatientForAppointList(String ph_id, String ph_pat_id, String ph_sub_code, String ph_date, String ph_patient_cat_id, String ph_patient_cat, String pat_code, String pat_name, String pat_phone) {
        this.ph_id = ph_id;
        this.ph_pat_id = ph_pat_id;
        this.ph_sub_code = ph_sub_code;
        this.ph_date = ph_date;
        this.ph_patient_cat_id = ph_patient_cat_id;
        this.ph_patient_cat = ph_patient_cat;
        this.pat_code = pat_code;
        this.pat_name = pat_name;
        this.pat_phone = pat_phone;
    }

    public String getPh_id() {
        return ph_id;
    }

    public void setPh_id(String ph_id) {
        this.ph_id = ph_id;
    }

    public String getPh_pat_id() {
        return ph_pat_id;
    }

    public void setPh_pat_id(String ph_pat_id) {
        this.ph_pat_id = ph_pat_id;
    }

    public String getPh_sub_code() {
        return ph_sub_code;
    }

    public void setPh_sub_code(String ph_sub_code) {
        this.ph_sub_code = ph_sub_code;
    }

    public String getPh_date() {
        return ph_date;
    }

    public void setPh_date(String ph_date) {
        this.ph_date = ph_date;
    }

    public String getPh_patient_cat_id() {
        return ph_patient_cat_id;
    }

    public void setPh_patient_cat_id(String ph_patient_cat_id) {
        this.ph_patient_cat_id = ph_patient_cat_id;
    }

    public String getPh_patient_cat() {
        return ph_patient_cat;
    }

    public void setPh_patient_cat(String ph_patient_cat) {
        this.ph_patient_cat = ph_patient_cat;
    }

    public String getPat_code() {
        return pat_code;
    }

    public void setPat_code(String pat_code) {
        this.pat_code = pat_code;
    }

    public String getPat_name() {
        return pat_name;
    }

    public void setPat_name(String pat_name) {
        this.pat_name = pat_name;
    }

    public String getPat_phone() {
        return pat_phone;
    }

    public void setPat_phone(String pat_phone) {
        this.pat_phone = pat_phone;
    }
}
