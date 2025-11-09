package ttit.com.shuvo.docdiary.appt_schedule.pat_history.arraylists;

public class PatAppHistList {
    private String ph_patient_status;
    private String ph_patient_cat;
    private String app_date;
    private String date_name;
    private String depts_name;
    private String depts_id;
    private String deptd_name;
    private String deptm_name;
    private String schedule_time;
    private String ets;
    private String doc_name;
    private String doc_id;
    private String pfn_fee_name;
    private String pfn_id;
    private String avail;
    private String ad_pat_app_status;
    private String cancel_status;
    private String prm_code;

    public PatAppHistList(String ph_patient_status, String ph_patient_cat, String app_date, String date_name,
                          String depts_name, String depts_id, String deptd_name, String deptm_name, String schedule_time, String ets,
                          String doc_name, String doc_id, String pfn_fee_name, String pfn_id, String avail, String ad_pat_app_status,
                          String cancel_status, String prm_code) {
        this.ph_patient_status = ph_patient_status;
        this.ph_patient_cat = ph_patient_cat;
        this.app_date = app_date;
        this.date_name = date_name;
        this.depts_name = depts_name;
        this.depts_id = depts_id;
        this.deptd_name = deptd_name;
        this.deptm_name = deptm_name;
        this.schedule_time = schedule_time;
        this.ets = ets;
        this.doc_name = doc_name;
        this.doc_id = doc_id;
        this.pfn_fee_name = pfn_fee_name;
        this.pfn_id = pfn_id;
        this.avail = avail;
        this.ad_pat_app_status = ad_pat_app_status;
        this.cancel_status = cancel_status;
        this.prm_code = prm_code;
    }

    public String getPh_patient_status() {
        return ph_patient_status;
    }

    public void setPh_patient_status(String ph_patient_status) {
        this.ph_patient_status = ph_patient_status;
    }

    public String getPh_patient_cat() {
        return ph_patient_cat;
    }

    public void setPh_patient_cat(String ph_patient_cat) {
        this.ph_patient_cat = ph_patient_cat;
    }

    public String getApp_date() {
        return app_date;
    }

    public void setApp_date(String app_date) {
        this.app_date = app_date;
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

    public String getDeptm_name() {
        return deptm_name;
    }

    public void setDeptm_name(String deptm_name) {
        this.deptm_name = deptm_name;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public String getEts() {
        return ets;
    }

    public void setEts(String ets) {
        this.ets = ets;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public String getPfn_fee_name() {
        return pfn_fee_name;
    }

    public void setPfn_fee_name(String pfn_fee_name) {
        this.pfn_fee_name = pfn_fee_name;
    }

    public String getAvail() {
        return avail;
    }

    public void setAvail(String avail) {
        this.avail = avail;
    }

    public String getAd_pat_app_status() {
        return ad_pat_app_status;
    }

    public void setAd_pat_app_status(String ad_pat_app_status) {
        this.ad_pat_app_status = ad_pat_app_status;
    }

    public String getCancel_status() {
        return cancel_status;
    }

    public void setCancel_status(String cancel_status) {
        this.cancel_status = cancel_status;
    }

    public String getPrm_code() {
        return prm_code;
    }

    public void setPrm_code(String prm_code) {
        this.prm_code = prm_code;
    }

    public String getDate_name() {
        return date_name;
    }

    public void setDate_name(String date_name) {
        this.date_name = date_name;
    }

    public String getDepts_id() {
        return depts_id;
    }

    public void setDepts_id(String depts_id) {
        this.depts_id = depts_id;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getPfn_id() {
        return pfn_id;
    }

    public void setPfn_id(String pfn_id) {
        this.pfn_id = pfn_id;
    }
}

