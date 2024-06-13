package ttit.com.shuvo.docdiary.appointment_admin.arraylists;

import java.util.ArrayList;

public class AppointDateTimeCancelList {
    private String ad_id;
    private String app_date;
    private ArrayList<AppointTimeCancelList> appointTimeCancelLists;

    public AppointDateTimeCancelList(String ad_id, String app_date, ArrayList<AppointTimeCancelList> appointTimeCancelLists) {
        this.ad_id = ad_id;
        this.app_date = app_date;
        this.appointTimeCancelLists = appointTimeCancelLists;
    }

    public String getAd_id() {
        return ad_id;
    }

    public void setAd_id(String ad_id) {
        this.ad_id = ad_id;
    }

    public String getApp_date() {
        return app_date;
    }

    public void setApp_date(String app_date) {
        this.app_date = app_date;
    }

    public ArrayList<AppointTimeCancelList> getAppointTimeCancelLists() {
        return appointTimeCancelLists;
    }

    public void setAppointTimeCancelLists(ArrayList<AppointTimeCancelList> appointTimeCancelLists) {
        this.appointTimeCancelLists = appointTimeCancelLists;
    }
}
