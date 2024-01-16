package ttit.com.shuvo.docdiary.login.arraylists;

import java.util.ArrayList;

public class CenterList {
    private String center_name;
    private String center_api;
    private String doc_code;
    private ArrayList<MultipleUserList> multipleUserLists;

    public CenterList(String center_name, String center_api, String  doc_code, ArrayList<MultipleUserList> multipleUserLists) {
        this.center_name = center_name;
        this.center_api = center_api;
        this.doc_code = doc_code;
        this.multipleUserLists = multipleUserLists;
    }

    public String getCenter_name() {
        return center_name;
    }

    public void setCenter_name(String center_name) {
        this.center_name = center_name;
    }

    public String getCenter_api() {
        return center_api;
    }

    public void setCenter_api(String center_api) {
        this.center_api = center_api;
    }

    public String getDoc_code() {
        return doc_code;
    }

    public void setDoc_code(String doc_code) {
        this.doc_code = doc_code;
    }

    public ArrayList<MultipleUserList> getMultipleUserLists() {
        return multipleUserLists;
    }

    public void setMultipleUserLists(ArrayList<MultipleUserList> multipleUserLists) {
        this.multipleUserLists = multipleUserLists;
    }
}
