package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists;

public class PatMedicHistList {
    private String pmh_id;
    private String pmh_pmm_id;
    private String mhm_id;
    private String mhm_name;
    private String pmh_details;

    public PatMedicHistList(String pmh_id, String pmh_pmm_id, String mhm_id, String mhm_name, String pmh_details) {
        this.pmh_id = pmh_id;
        this.pmh_pmm_id = pmh_pmm_id;
        this.mhm_id = mhm_id;
        this.mhm_name = mhm_name;
        this.pmh_details = pmh_details;
    }

    public String getPmh_id() {
        return pmh_id;
    }

    public void setPmh_id(String pmh_id) {
        this.pmh_id = pmh_id;
    }

    public String getPmh_pmm_id() {
        return pmh_pmm_id;
    }

    public void setPmh_pmm_id(String pmh_pmm_id) {
        this.pmh_pmm_id = pmh_pmm_id;
    }

    public String getMhm_id() {
        return mhm_id;
    }

    public void setMhm_id(String mhm_id) {
        this.mhm_id = mhm_id;
    }

    public String getMhm_name() {
        return mhm_name;
    }

    public void setMhm_name(String mhm_name) {
        this.mhm_name = mhm_name;
    }

    public String getPmh_details() {
        return pmh_details;
    }

    public void setPmh_details(String pmh_details) {
        this.pmh_details = pmh_details;
    }
}
