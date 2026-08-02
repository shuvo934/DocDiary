package ttit.com.shuvo.docdiary.appointment_admin.arraylists;

import java.util.ArrayList;

public class AppointDateTimeCancelList {
    private final String ad_id;
    private final String app_date;
    private final ArrayList<AppointTimeCancelList> appointTimeCancelLists;

    public AppointDateTimeCancelList(String ad_id, String app_date, ArrayList<AppointTimeCancelList> appointTimeCancelLists) {
        this.ad_id = ad_id;
        this.app_date = app_date;
        this.appointTimeCancelLists = appointTimeCancelLists;
    }

    public String getAd_id() {
        return ad_id;
    }

    public String getApp_date() {
        return app_date;
    }

    public ArrayList<AppointTimeCancelList> getAppointTimeCancelLists() {
        return appointTimeCancelLists;
    }
}
