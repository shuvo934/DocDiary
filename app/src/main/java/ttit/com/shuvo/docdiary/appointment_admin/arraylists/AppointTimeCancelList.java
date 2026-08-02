package ttit.com.shuvo.docdiary.appointment_admin.arraylists;

public class AppointTimeCancelList {
    private final String as_ts_id;
    private final String schedule;

    public AppointTimeCancelList(String as_ts_id, String schedule) {
        this.as_ts_id = as_ts_id;
        this.schedule = schedule;
    }

    public String getAs_ts_id() {
        return as_ts_id;
    }

    public String getSchedule() {
        return schedule;
    }
}
