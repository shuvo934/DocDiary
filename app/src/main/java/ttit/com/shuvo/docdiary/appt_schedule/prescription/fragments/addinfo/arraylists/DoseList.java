package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists;

public class DoseList {
    private String dose_id;
    private String dose_name;

    public DoseList(String dose_id, String dose_name) {
        this.dose_id = dose_id;
        this.dose_name = dose_name;
    }

    public String getDose_id() {
        return dose_id;
    }

    public void setDose_id(String dose_id) {
        this.dose_id = dose_id;
    }

    public String getDose_name() {
        return dose_name;
    }

    public void setDose_name(String dose_name) {
        this.dose_name = dose_name;
    }
}
