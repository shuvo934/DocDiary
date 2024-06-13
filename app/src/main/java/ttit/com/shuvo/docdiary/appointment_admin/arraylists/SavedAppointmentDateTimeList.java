package ttit.com.shuvo.docdiary.appointment_admin.arraylists;

public class SavedAppointmentDateTimeList {
    private String adm_date;
    private String adm_id;
    private String schedule;
    private String ts_id;
    private String doc_id;
    private String pfn_id;
    private String depts_id;
    private String ph_id;
    private String prm_id;
    private String prd_id;
    private boolean takenScheduleAvailable = false;
    private boolean inserted;
    private boolean schDuplicate;


    public SavedAppointmentDateTimeList(String adm_date, String adm_id, String schedule, String ts_id, String doc_id, String pfn_id, String depts_id, String ph_id, String prm_id, String prd_id, boolean takenScheduleAvailable, boolean inserted, boolean schDuplicate) {
        this.adm_date = adm_date;
        this.adm_id = adm_id;
        this.schedule = schedule;
        this.ts_id = ts_id;
        this.doc_id = doc_id;
        this.pfn_id = pfn_id;
        this.depts_id = depts_id;
        this.ph_id = ph_id;
        this.prm_id = prm_id;
        this.prd_id = prd_id;
        this.takenScheduleAvailable = takenScheduleAvailable;
        this.inserted = inserted;
        this.schDuplicate = schDuplicate;
    }

    public String getAdm_date() {
        return adm_date;
    }

    public void setAdm_date(String adm_date) {
        this.adm_date = adm_date;
    }

    public String getAdm_id() {
        return adm_id;
    }

    public void setAdm_id(String adm_id) {
        this.adm_id = adm_id;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getTs_id() {
        return ts_id;
    }

    public void setTs_id(String ts_id) {
        this.ts_id = ts_id;
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

    public String getDepts_id() {
        return depts_id;
    }

    public void setDepts_id(String depts_id) {
        this.depts_id = depts_id;
    }

    public String getPh_id() {
        return ph_id;
    }

    public void setPh_id(String ph_id) {
        this.ph_id = ph_id;
    }

    public String getPrm_id() {
        return prm_id;
    }

    public void setPrm_id(String prm_id) {
        this.prm_id = prm_id;
    }

    public String getPrd_id() {
        return prd_id;
    }

    public void setPrd_id(String prd_id) {
        this.prd_id = prd_id;
    }

    public boolean isTakenScheduleAvailable() {
        return takenScheduleAvailable;
    }

    public void setTakenScheduleAvailable(boolean takenScheduleAvailable) {
        this.takenScheduleAvailable = takenScheduleAvailable;
    }

    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }

    public boolean isSchDuplicate() {
        return schDuplicate;
    }

    public void setSchDuplicate(boolean schDuplicate) {
        this.schDuplicate = schDuplicate;
    }
}
