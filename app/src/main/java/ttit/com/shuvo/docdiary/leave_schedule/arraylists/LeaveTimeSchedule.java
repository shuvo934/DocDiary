package ttit.com.shuvo.docdiary.leave_schedule.arraylists;

public class LeaveTimeSchedule {
    private String lad_id;
    private String lad_date;
    private String las_id;
    private String las_ts_id;
    private String las_notes;
    private String la_id;
    private String la_month;
    private String schedule_time;
    private String tot_sc;

    public LeaveTimeSchedule(String lad_id, String lad_date, String las_id, String las_ts_id, String las_notes, String la_id, String la_month, String schedule_time, String tot_sc) {
        this.lad_id = lad_id;
        this.lad_date = lad_date;
        this.las_id = las_id;
        this.las_ts_id = las_ts_id;
        this.las_notes = las_notes;
        this.la_id = la_id;
        this.la_month = la_month;
        this.schedule_time = schedule_time;
        this.tot_sc = tot_sc;
    }

    public String getLad_id() {
        return lad_id;
    }

    public void setLad_id(String lad_id) {
        this.lad_id = lad_id;
    }

    public String getLad_date() {
        return lad_date;
    }

    public void setLad_date(String lad_date) {
        this.lad_date = lad_date;
    }

    public String getLas_id() {
        return las_id;
    }

    public void setLas_id(String las_id) {
        this.las_id = las_id;
    }

    public String getLas_ts_id() {
        return las_ts_id;
    }

    public void setLas_ts_id(String las_ts_id) {
        this.las_ts_id = las_ts_id;
    }

    public String getLas_notes() {
        return las_notes;
    }

    public void setLas_notes(String las_notes) {
        this.las_notes = las_notes;
    }

    public String getLa_id() {
        return la_id;
    }

    public void setLa_id(String la_id) {
        this.la_id = la_id;
    }

    public String getLa_month() {
        return la_month;
    }

    public void setLa_month(String la_month) {
        this.la_month = la_month;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public String getTot_sc() {
        return tot_sc;
    }

    public void setTot_sc(String tot_sc) {
        this.tot_sc = tot_sc;
    }
}
