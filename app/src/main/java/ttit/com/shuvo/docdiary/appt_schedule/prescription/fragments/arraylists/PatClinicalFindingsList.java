package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists;

public class PatClinicalFindingsList {
    private String cf_id;
    private String cf_pmm_id;
    private String cfm_id;
    private String cfm_name;
    private String cf_details;

    public PatClinicalFindingsList(String cf_id, String cf_pmm_id, String cfm_id, String cfm_name, String cf_details) {
        this.cf_id = cf_id;
        this.cf_pmm_id = cf_pmm_id;
        this.cfm_id = cfm_id;
        this.cfm_name = cfm_name;
        this.cf_details = cf_details;
    }

    public String getCf_id() {
        return cf_id;
    }

    public void setCf_id(String cf_id) {
        this.cf_id = cf_id;
    }

    public String getCf_pmm_id() {
        return cf_pmm_id;
    }

    public void setCf_pmm_id(String cf_pmm_id) {
        this.cf_pmm_id = cf_pmm_id;
    }

    public String getCfm_id() {
        return cfm_id;
    }

    public void setCfm_id(String cfm_id) {
        this.cfm_id = cfm_id;
    }

    public String getCfm_name() {
        return cfm_name;
    }

    public void setCfm_name(String cfm_name) {
        this.cfm_name = cfm_name;
    }

    public String getCf_details() {
        return cf_details;
    }

    public void setCf_details(String cf_details) {
        this.cf_details = cf_details;
    }
}
