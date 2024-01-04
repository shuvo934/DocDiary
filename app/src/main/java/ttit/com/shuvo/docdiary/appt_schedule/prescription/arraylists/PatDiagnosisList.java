package ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists;

public class PatDiagnosisList {
    private String pdi_id;
    private String pdi_pmm_id;
    private String dm_id;
    private String dm_name;
    private String child_count;

    public PatDiagnosisList(String pdi_id, String pdi_pmm_id, String dm_id, String dm_name, String child_count) {
        this.pdi_id = pdi_id;
        this.pdi_pmm_id = pdi_pmm_id;
        this.dm_id = dm_id;
        this.dm_name = dm_name;
        this.child_count = child_count;
    }

    public String getPdi_id() {
        return pdi_id;
    }

    public void setPdi_id(String pdi_id) {
        this.pdi_id = pdi_id;
    }

    public String getPdi_pmm_id() {
        return pdi_pmm_id;
    }

    public void setPdi_pmm_id(String pdi_pmm_id) {
        this.pdi_pmm_id = pdi_pmm_id;
    }

    public String getDm_id() {
        return dm_id;
    }

    public void setDm_id(String dm_id) {
        this.dm_id = dm_id;
    }

    public String getDm_name() {
        return dm_name;
    }

    public void setDm_name(String dm_name) {
        this.dm_name = dm_name;
    }

    public String getChild_count() {
        return child_count;
    }

    public void setChild_count(String child_count) {
        this.child_count = child_count;
    }
}
