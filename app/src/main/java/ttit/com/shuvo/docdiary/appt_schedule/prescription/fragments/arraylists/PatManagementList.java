package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists;

public class PatManagementList {
    private String pmap_id;
    private String pmap_details;
    private String pmap_pmm_id;

    public PatManagementList(String pmap_id, String pmap_details, String pmap_pmm_id) {
        this.pmap_id = pmap_id;
        this.pmap_details = pmap_details;
        this.pmap_pmm_id = pmap_pmm_id;
    }

    public String getPmap_id() {
        return pmap_id;
    }

    public void setPmap_id(String pmap_id) {
        this.pmap_id = pmap_id;
    }

    public String getPmap_details() {
        return pmap_details;
    }

    public void setPmap_details(String pmap_details) {
        this.pmap_details = pmap_details;
    }

    public String getPmap_pmm_id() {
        return pmap_pmm_id;
    }

    public void setPmap_pmm_id(String pmap_pmm_id) {
        this.pmap_pmm_id = pmap_pmm_id;
    }
}
