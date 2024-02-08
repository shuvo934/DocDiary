package ttit.com.shuvo.docdiary.patient_search.arraylists;

public class PatientSearchList {
    private String pat_id;
    private String pat_name;
    private String dd_thana_name;
    private String ph_id;
    private String sub_code;
    private String pph_progress;

    public PatientSearchList(String pat_id, String pat_name, String dd_thana_name, String ph_id, String sub_code, String pph_progress) {
        this.pat_id = pat_id;
        this.pat_name = pat_name;
        this.dd_thana_name = dd_thana_name;
        this.ph_id = ph_id;
        this.sub_code = sub_code;
        this.pph_progress = pph_progress;
    }

    public String getPat_id() {
        return pat_id;
    }

    public void setPat_id(String pat_id) {
        this.pat_id = pat_id;
    }

    public String getPat_name() {
        return pat_name;
    }

    public void setPat_name(String pat_name) {
        this.pat_name = pat_name;
    }

    public String getDd_thana_name() {
        return dd_thana_name;
    }

    public void setDd_thana_name(String dd_thana_name) {
        this.dd_thana_name = dd_thana_name;
    }

    public String getPh_id() {
        return ph_id;
    }

    public void setPh_id(String ph_id) {
        this.ph_id = ph_id;
    }

    public String getSub_code() {
        return sub_code;
    }

    public void setSub_code(String sub_code) {
        this.sub_code = sub_code;
    }

    public String getPph_progress() {
        return pph_progress;
    }

    public void setPph_progress(String pph_progress) {
        this.pph_progress = pph_progress;
    }
}
