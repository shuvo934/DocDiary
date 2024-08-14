package ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists;

public class DocWisePayDataList {
    private String app_date1;
    private String date_total_reg;
    private String date_total_ets;
    private String date_total;

    public DocWisePayDataList(String app_date1, String date_total_reg, String date_total_ets, String date_total) {
        this.app_date1 = app_date1;
        this.date_total_reg = date_total_reg;
        this.date_total_ets = date_total_ets;
        this.date_total = date_total;
    }

    public String getApp_date1() {
        return app_date1;
    }

    public void setApp_date1(String app_date1) {
        this.app_date1 = app_date1;
    }

    public String getDate_total_reg() {
        return date_total_reg;
    }

    public void setDate_total_reg(String date_total_reg) {
        this.date_total_reg = date_total_reg;
    }

    public String getDate_total_ets() {
        return date_total_ets;
    }

    public void setDate_total_ets(String date_total_ets) {
        this.date_total_ets = date_total_ets;
    }

    public String getDate_total() {
        return date_total;
    }

    public void setDate_total(String date_total) {
        this.date_total = date_total;
    }
}
