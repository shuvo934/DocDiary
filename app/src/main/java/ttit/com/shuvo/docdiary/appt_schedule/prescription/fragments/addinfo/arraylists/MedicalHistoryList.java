package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists;

public class MedicalHistoryList {
    private String mhm_id;
    private String mhm_name;
    private String mhm_details;

    public MedicalHistoryList(String mhm_id, String mhm_name, String mhm_details) {
        this.mhm_id = mhm_id;
        this.mhm_name = mhm_name;
        this.mhm_details = mhm_details;
    }

    public String getMhm_id() {
        return mhm_id;
    }

    public void setMhm_id(String mhm_id) {
        this.mhm_id = mhm_id;
    }

    public String getMhm_name() {
        return mhm_name;
    }

    public void setMhm_name(String mhm_name) {
        this.mhm_name = mhm_name;
    }

    public String getMhm_details() {
        return mhm_details;
    }

    public void setMhm_details(String mhm_details) {
        this.mhm_details = mhm_details;
    }
}
