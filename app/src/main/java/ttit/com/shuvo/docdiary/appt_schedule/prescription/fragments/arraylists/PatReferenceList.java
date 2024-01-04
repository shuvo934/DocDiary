package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists;

public class PatReferenceList {
    private String pref_id;
    private String pref_name;
    private String pref_pmm_id;

    public PatReferenceList(String pref_id, String pref_name, String pref_pmm_id) {
        this.pref_id = pref_id;
        this.pref_name = pref_name;
        this.pref_pmm_id = pref_pmm_id;
    }

    public String getPref_id() {
        return pref_id;
    }

    public void setPref_id(String pref_id) {
        this.pref_id = pref_id;
    }

    public String getPref_name() {
        return pref_name;
    }

    public void setPref_name(String pref_name) {
        this.pref_name = pref_name;
    }

    public String getPref_pmm_id() {
        return pref_pmm_id;
    }

    public void setPref_pmm_id(String pref_pmm_id) {
        this.pref_pmm_id = pref_pmm_id;
    }
}
