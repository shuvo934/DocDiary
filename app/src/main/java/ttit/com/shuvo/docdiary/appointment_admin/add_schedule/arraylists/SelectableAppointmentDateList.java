package ttit.com.shuvo.docdiary.appointment_admin.add_schedule.arraylists;

public class SelectableAppointmentDateList {
    private final String adm_id;
    private final String p_adm_date;

    public SelectableAppointmentDateList(String adm_id, String p_adm_date) {
        this.adm_id = adm_id;
        this.p_adm_date = p_adm_date;
    }

    public String getAdm_id() {
        return adm_id;
    }

    public String getP_adm_date() {
        return p_adm_date;
    }
}
