package ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists;

public class ListOfInjuries {
    private String injury_id;
    private String injury_name;
    private String injury_notes;

    public ListOfInjuries(String injury_id, String injury_name, String injury_notes) {
        this.injury_id = injury_id;
        this.injury_name = injury_name;
        this.injury_notes = injury_notes;
    }

    public String getInjury_id() {
        return injury_id;
    }

    public void setInjury_id(String injury_id) {
        this.injury_id = injury_id;
    }

    public String getInjury_name() {
        return injury_name;
    }

    public void setInjury_name(String injury_name) {
        this.injury_name = injury_name;
    }

    public String getInjury_notes() {
        return injury_notes;
    }

    public void setInjury_notes(String injury_notes) {
        this.injury_notes = injury_notes;
    }
}
