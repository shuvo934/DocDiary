package ttit.com.shuvo.docdiary.patient_search.appointment_calendar.arraylist;

import java.util.ArrayList;
import java.util.Date;

public class PatAppCalendarList {
    private String app_date;
    private Date date;
    private ArrayList<PatAppointmentViewList> patAppointmentViewLists;

    public PatAppCalendarList(String app_date, Date date, ArrayList<PatAppointmentViewList> patAppointmentViewLists) {
        this.app_date = app_date;
        this.date = date;
        this.patAppointmentViewLists = patAppointmentViewLists;
    }

    public String getApp_date() {
        return app_date;
    }

    public void setApp_date(String app_date) {
        this.app_date = app_date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<PatAppointmentViewList> getPatAppointmentViewLists() {
        return patAppointmentViewLists;
    }

    public void setPatAppointmentViewLists(ArrayList<PatAppointmentViewList> patAppointmentViewLists) {
        this.patAppointmentViewLists = patAppointmentViewLists;
    }
}
