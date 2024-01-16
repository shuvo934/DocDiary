package ttit.com.shuvo.docdiary.profile.arraylists;

public class ThanaList {
    private String dd_id;
    private String dd_dist_id;
    private String dd_thana_name;
    private String dist_name;

    public ThanaList(String dd_id, String dd_dist_id, String dd_thana_name, String dist_name) {
        this.dd_id = dd_id;
        this.dd_dist_id = dd_dist_id;
        this.dd_thana_name = dd_thana_name;
        this.dist_name = dist_name;
    }

    public String getDd_id() {
        return dd_id;
    }

    public void setDd_id(String dd_id) {
        this.dd_id = dd_id;
    }

    public String getDd_dist_id() {
        return dd_dist_id;
    }

    public void setDd_dist_id(String dd_dist_id) {
        this.dd_dist_id = dd_dist_id;
    }

    public String getDd_thana_name() {
        return dd_thana_name;
    }

    public void setDd_thana_name(String dd_thana_name) {
        this.dd_thana_name = dd_thana_name;
    }

    public String getDist_name() {
        return dist_name;
    }

    public void setDist_name(String dist_name) {
        this.dist_name = dist_name;
    }
}
