package ttit.com.shuvo.docdiary.unit_app_schedule.arraylists;

import java.util.ArrayList;

public class UnitDoctorsList {
    private String doc_id;
    private String doc_name;
    private String doc_code;
    private String app_count;
    private ArrayList<DoctorAppSchList> doctorAppSchLists;

    public UnitDoctorsList(String doc_id, String doc_name, String doc_code, String app_count, ArrayList<DoctorAppSchList> doctorAppSchLists) {
        this.doc_id = doc_id;
        this.doc_name = doc_name;
        this.doc_code = doc_code;
        this.app_count = app_count;
        this.doctorAppSchLists = doctorAppSchLists;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String doc_name) {
        this.doc_name = doc_name;
    }

    public ArrayList<DoctorAppSchList> getDoctorAppSchLists() {
        return doctorAppSchLists;
    }

    public void setDoctorAppSchLists(ArrayList<DoctorAppSchList> doctorAppSchLists) {
        this.doctorAppSchLists = doctorAppSchLists;
    }

    public String getDoc_code() {
        return doc_code;
    }

    public void setDoc_code(String doc_code) {
        this.doc_code = doc_code;
    }

    public String getApp_count() {
        return app_count;
    }

    public void setApp_count(String app_count) {
        this.app_count = app_count;
    }
}
