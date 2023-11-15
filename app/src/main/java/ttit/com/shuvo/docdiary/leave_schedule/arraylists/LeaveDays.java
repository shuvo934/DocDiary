package ttit.com.shuvo.docdiary.leave_schedule.arraylists;

public class LeaveDays {
    private String la_id;
    private String from_date;
    private String to_date;

    public LeaveDays(String la_id, String from_date, String to_date) {
        this.la_id = la_id;
        this.from_date = from_date;
        this.to_date = to_date;
    }

    public String getLa_id() {
        return la_id;
    }

    public void setLa_id(String la_id) {
        this.la_id = la_id;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }
}
