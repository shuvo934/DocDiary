package ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists;

public class DiagnosisList {
    private String dm_name;
    private String dm_id;
    private String dm_notes;

    public DiagnosisList(String dm_name, String dm_id, String dm_notes) {
        this.dm_name = dm_name;
        this.dm_id = dm_id;
        this.dm_notes = dm_notes;
    }

    public String getDm_name() {
        return dm_name;
    }

    public void setDm_name(String dm_name) {
        this.dm_name = dm_name;
    }

    public String getDm_id() {
        return dm_id;
    }

    public void setDm_id(String dm_id) {
        this.dm_id = dm_id;
    }

    public String getDm_notes() {
        return dm_notes;
    }

    public void setDm_notes(String dm_notes) {
        this.dm_notes = dm_notes;
    }
}
