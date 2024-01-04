package ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists;

public class HealthProgressList {
    private String serialNo;
    private String pph_id;
    private String pph_date;
    private String pph_doc_id;
    private String doc_name;
    private String pph_depts_id;
    private String detps_name;
    private String pph_pfn_id;
    private String pfn_name;
    private String pph_ad_id;
    private String pph_progress;
    private String pph_notes;
    private String pph_rec_no;

    public HealthProgressList(String serialNo, String pph_id, String pph_date, String pph_doc_id, String doc_name, String pph_depts_id, String detps_name, String pph_pfn_id, String pfn_name, String pph_ad_id, String pph_progress, String pph_notes, String pph_rec_no) {
        this.serialNo = serialNo;
        this.pph_id = pph_id;
        this.pph_date = pph_date;
        this.pph_doc_id = pph_doc_id;
        this.doc_name = doc_name;
        this.pph_depts_id = pph_depts_id;
        this.detps_name = detps_name;
        this.pph_pfn_id = pph_pfn_id;
        this.pfn_name = pfn_name;
        this.pph_ad_id = pph_ad_id;
        this.pph_progress = pph_progress;
        this.pph_notes = pph_notes;
        this.pph_rec_no = pph_rec_no;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getPph_id() {
        return pph_id;
    }

    public void setPph_id(String pph_id) {
        this.pph_id = pph_id;
    }

    public String getPph_date() {
        return pph_date;
    }

    public void setPph_date(String pph_date) {
        this.pph_date = pph_date;
    }

    public String getPph_doc_id() {
        return pph_doc_id;
    }

    public void setPph_doc_id(String pph_doc_id) {
        this.pph_doc_id = pph_doc_id;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getPph_depts_id() {
        return pph_depts_id;
    }

    public void setPph_depts_id(String pph_depts_id) {
        this.pph_depts_id = pph_depts_id;
    }

    public String getDetps_name() {
        return detps_name;
    }

    public void setDetps_name(String detps_name) {
        this.detps_name = detps_name;
    }

    public String getPph_pfn_id() {
        return pph_pfn_id;
    }

    public void setPph_pfn_id(String pph_pfn_id) {
        this.pph_pfn_id = pph_pfn_id;
    }

    public String getPfn_name() {
        return pfn_name;
    }

    public void setPfn_name(String pfn_name) {
        this.pfn_name = pfn_name;
    }

    public String getPph_ad_id() {
        return pph_ad_id;
    }

    public void setPph_ad_id(String pph_ad_id) {
        this.pph_ad_id = pph_ad_id;
    }

    public String getPph_progress() {
        return pph_progress;
    }

    public void setPph_progress(String pph_progress) {
        this.pph_progress = pph_progress;
    }

    public String getPph_notes() {
        return pph_notes;
    }

    public void setPph_notes(String pph_notes) {
        this.pph_notes = pph_notes;
    }

    public String getPph_rec_no() {
        return pph_rec_no;
    }

    public void setPph_rec_no(String pph_rec_no) {
        this.pph_rec_no = pph_rec_no;
    }
}
