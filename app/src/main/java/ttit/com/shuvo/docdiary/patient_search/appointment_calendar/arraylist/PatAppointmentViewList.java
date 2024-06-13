package ttit.com.shuvo.docdiary.patient_search.appointment_calendar.arraylist;

public class PatAppointmentViewList {
    private String depts_name;
    private String deptd_name;
    private String deptm_name;
    private String schedule_time;
    private String ets;
    private String doc_name;
    private String pfn_fee_name;
    private String avail;
    private String ad_pat_app_status;
    private String cancel_status;
    private String prm_code;

    public PatAppointmentViewList(String depts_name, String deptd_name, String deptm_name, String schedule_time, String ets, String doc_name, String pfn_fee_name, String avail, String ad_pat_app_status, String cancel_status, String prm_code) {
        this.depts_name = depts_name;
        this.deptd_name = deptd_name;
        this.deptm_name = deptm_name;
        this.schedule_time = schedule_time;
        this.ets = ets;
        this.doc_name = doc_name;
        this.pfn_fee_name = pfn_fee_name;
        this.avail = avail;
        this.ad_pat_app_status = ad_pat_app_status;
        this.cancel_status = cancel_status;
        this.prm_code = prm_code;
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
}
