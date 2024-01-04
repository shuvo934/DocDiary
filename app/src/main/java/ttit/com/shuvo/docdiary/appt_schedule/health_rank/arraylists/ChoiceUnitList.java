package ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists;

public class ChoiceUnitList {
    private String pph_depts_id;
    private String detps_name;

    public ChoiceUnitList(String pph_depts_id, String detps_name) {
        this.pph_depts_id = pph_depts_id;
        this.detps_name = detps_name;
    }

    public String getPph_depts_id() {
        return pph_depts_id;
    }

    public void setPph_depts_id(String pph_depts_id) {
        this.pph_depts_id = pph_depts_id;
    }

    public String getDetps_name() {
        return detps_name;
    }

    public void setDetps_name(String detps_name) {
        this.detps_name = detps_name;
    }
}
