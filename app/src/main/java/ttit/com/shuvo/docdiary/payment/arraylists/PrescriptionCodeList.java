package ttit.com.shuvo.docdiary.payment.arraylists;

public class PrescriptionCodeList {
    private String ph_id;
    private String ph_sub_code;
    private String pat_name;
    private String pat_code;
    private String pat_cell;
    private String gender;
    private String pat_blood;
    private String address;
    private String ph_patient_cat;
    private String ph_patient_cat_id;
    private String pat_category_name;
    private String ph_patient_status;
    private String marital_status;
    private String p_age;
    private String pat_date;

    public PrescriptionCodeList(String ph_id, String ph_sub_code, String pat_name, String pat_code, String pat_cell, String gender, String pat_blood, String address, String ph_patient_cat, String ph_patient_cat_id, String pat_category_name, String ph_patient_status, String marital_status, String p_age, String pat_date) {
        this.ph_id = ph_id;
        this.ph_sub_code = ph_sub_code;
        this.pat_name = pat_name;
        this.pat_code = pat_code;
        this.pat_cell = pat_cell;
        this.gender = gender;
        this.pat_blood = pat_blood;
        this.address = address;
        this.ph_patient_cat = ph_patient_cat;
        this.ph_patient_cat_id = ph_patient_cat_id;
        this.pat_category_name = pat_category_name;
        this.ph_patient_status = ph_patient_status;
        this.marital_status = marital_status;
        this.p_age = p_age;
        this.pat_date = pat_date;
    }

    public String getPh_id() {
        return ph_id;
    }

    public void setPh_id(String ph_id) {
        this.ph_id = ph_id;
    }

    public String getPh_sub_code() {
        return ph_sub_code;
    }

    public void setPh_sub_code(String ph_sub_code) {
        this.ph_sub_code = ph_sub_code;
    }

    public String getPat_name() {
        return pat_name;
    }

    public void setPat_name(String pat_name) {
        this.pat_name = pat_name;
    }

    public String getPat_code() {
        return pat_code;
    }

    public void setPat_code(String pat_code) {
        this.pat_code = pat_code;
    }

    public String getPat_cell() {
        return pat_cell;
    }

    public void setPat_cell(String pat_cell) {
        this.pat_cell = pat_cell;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPat_blood() {
        return pat_blood;
    }

    public void setPat_blood(String pat_blood) {
        this.pat_blood = pat_blood;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPh_patient_cat() {
        return ph_patient_cat;
    }

    public void setPh_patient_cat(String ph_patient_cat) {
        this.ph_patient_cat = ph_patient_cat;
    }

    public String getPh_patient_cat_id() {
        return ph_patient_cat_id;
    }

    public void setPh_patient_cat_id(String ph_patient_cat_id) {
        this.ph_patient_cat_id = ph_patient_cat_id;
    }

    public String getPat_category_name() {
        return pat_category_name;
    }

    public void setPat_category_name(String pat_category_name) {
        this.pat_category_name = pat_category_name;
    }

    public String getPh_patient_status() {
        return ph_patient_status;
    }

    public void setPh_patient_status(String ph_patient_status) {
        this.ph_patient_status = ph_patient_status;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getP_age() {
        return p_age;
    }

    public void setP_age(String p_age) {
        this.p_age = p_age;
    }

    public String getPat_date() {
        return pat_date;
    }

    public void setPat_date(String pat_date) {
        this.pat_date = pat_date;
    }
}
