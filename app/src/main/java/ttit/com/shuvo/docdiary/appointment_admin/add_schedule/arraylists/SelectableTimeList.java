package ttit.com.shuvo.docdiary.appointment_admin.add_schedule.arraylists;

public class SelectableTimeList {
    private final String ts_id;
    private final String schedule;
    private final String ts_eta_flag;
    private final String type;

    public SelectableTimeList(String ts_id, String schedule, String ts_eta_flag, String type) {
        this.ts_id = ts_id;
        this.schedule = schedule;
        this.ts_eta_flag = ts_eta_flag;
        this.type = type;
    }

    public String getTs_id() {
        return ts_id;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getTs_eta_flag() {
        return ts_eta_flag;
    }

    public String getType() {
        return type;
    }
}
