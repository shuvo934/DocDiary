package ttit.com.shuvo.docdiary.login.arraylists;

public class CenterList {
    private String center_name;
    private String center_api;

    public CenterList(String center_name, String center_api) {
        this.center_name = center_name;
        this.center_api = center_api;
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
}
