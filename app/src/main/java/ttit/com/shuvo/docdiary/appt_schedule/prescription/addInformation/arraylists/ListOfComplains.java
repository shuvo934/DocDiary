package ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists;

public class ListOfComplains {
    private String cm_name;
    private String cm_notes;
    private String cm_id;

    public ListOfComplains(String cm_name, String cm_notes, String cm_id) {
        this.cm_name = cm_name;
        this.cm_notes = cm_notes;
        this.cm_id = cm_id;
    }

    public String getCm_name() {
        return cm_name;
    }

    public void setCm_name(String cm_name) {
        this.cm_name = cm_name;
    }

    public String getCm_notes() {
        return cm_notes;
    }

    public void setCm_notes(String cm_notes) {
        this.cm_notes = cm_notes;
    }

    public String getCm_id() {
        return cm_id;
    }

    public void setCm_id(String cm_id) {
        this.cm_id = cm_id;
    }
}
