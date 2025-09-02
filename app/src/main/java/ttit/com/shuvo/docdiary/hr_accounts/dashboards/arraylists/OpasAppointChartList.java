package ttit.com.shuvo.docdiary.hr_accounts.dashboards.arraylists;

public class OpasAppointChartList {
    private String id;
    private String all_dates;
    private String all_app_count;
    private String c_app_count;
    private String uc_app_count;

    public OpasAppointChartList(String id, String all_dates, String all_app_count, String c_app_count, String uc_app_count) {
        this.id = id;
        this.all_dates = all_dates;
        this.all_app_count = all_app_count;
        this.c_app_count = c_app_count;
        this.uc_app_count = uc_app_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAll_dates() {
        return all_dates;
    }

    public void setAll_dates(String all_dates) {
        this.all_dates = all_dates;
    }

    public String getAll_app_count() {
        return all_app_count;
    }

    public void setAll_app_count(String all_app_count) {
        this.all_app_count = all_app_count;
    }

    public String getC_app_count() {
        return c_app_count;
    }

    public void setC_app_count(String c_app_count) {
        this.c_app_count = c_app_count;
    }

    public String getUc_app_count() {
        return uc_app_count;
    }

    public void setUc_app_count(String uc_app_count) {
        this.uc_app_count = uc_app_count;
    }
}
