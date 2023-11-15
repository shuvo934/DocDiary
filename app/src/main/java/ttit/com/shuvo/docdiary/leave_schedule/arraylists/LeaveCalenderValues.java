package ttit.com.shuvo.docdiary.leave_schedule.arraylists;

import java.util.ArrayList;
import java.util.Date;

public class LeaveCalenderValues {
    private Date date;
    private boolean fullDay;
    private ArrayList<String> timeLists;
    private String total_sc;

    public LeaveCalenderValues(Date date, boolean fullDay, ArrayList<String> timeLists, String total_sc) {
        this.date = date;
        this.fullDay = fullDay;
        this.timeLists = timeLists;
        this.total_sc = total_sc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isFullDay() {
        return fullDay;
    }

    public void setFullDay(boolean fullDay) {
        this.fullDay = fullDay;
    }

    public ArrayList<String> getTimeLists() {
        return timeLists;
    }

    public void setTimeLists(ArrayList<String> timeLists) {
        this.timeLists = timeLists;
    }

    public String getTotal_sc() {
        return total_sc;
    }

    public void setTotal_sc(String total_sc) {
        this.total_sc = total_sc;
    }
}
