package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists;

public class ClinicalFindingsList {
    private String cfm_id;
    private String cfm_name;
    private String cfm_details;

    public ClinicalFindingsList(String cfm_id, String cfm_name, String cfm_details) {
        this.cfm_id = cfm_id;
        this.cfm_name = cfm_name;
        this.cfm_details = cfm_details;
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

    public String getCfm_details() {
        return cfm_details;
    }

    public void setCfm_details(String cfm_details) {
        this.cfm_details = cfm_details;
    }
}
