package ttit.com.shuvo.docdiary.appointment_admin.add_schedule.arraylists;

public class SelectableTimeList {
    private String ts_id;
    private String schedule;
    private String ts_eta_flag;
    private String type;

    public SelectableTimeList(String ts_id, String schedule, String ts_eta_flag, String type) {
        this.ts_id = ts_id;
        this.schedule = schedule;
        this.ts_eta_flag = ts_eta_flag;
        this.type = type;
    }

    public String getTs_id() {
        return ts_id;
    }

    public void setTs_id(String ts_id) {
        this.ts_id = ts_id;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getTs_eta_flag() {
        return ts_eta_flag;
    }

    public void setTs_eta_flag(String ts_eta_flag) {
        this.ts_eta_flag = ts_eta_flag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
