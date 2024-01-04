package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists;

public class MedTakeTimeList {
    private String mpm_id;
    private String mpm_name;
    private String mpm_details;

    public MedTakeTimeList(String mpm_id, String mpm_name, String mpm_details) {
        this.mpm_id = mpm_id;
        this.mpm_name = mpm_name;
        this.mpm_details = mpm_details;
    }

    public String getMpm_id() {
        return mpm_id;
    }

    public void setMpm_id(String mpm_id) {
        this.mpm_id = mpm_id;
    }

    public String getMpm_name() {
        return mpm_name;
    }

    public void setMpm_name(String mpm_name) {
        this.mpm_name = mpm_name;
    }

    public String getMpm_details() {
        return mpm_details;
    }

    public void setMpm_details(String mpm_details) {
        this.mpm_details = mpm_details;
    }
}
