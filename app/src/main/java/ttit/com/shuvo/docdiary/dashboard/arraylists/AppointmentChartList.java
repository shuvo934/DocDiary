package ttit.com.shuvo.docdiary.dashboard.arraylists;

public class AppointmentChartList {
    private String id;
    private String dateMonth;
    private String total_app;
    private String cancel_app;

    public AppointmentChartList(String id,String dateMonth, String total_app, String cancel_app) {
        this.id = id;
        this.dateMonth = dateMonth;
        this.total_app = total_app;
        this.cancel_app = cancel_app;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateMonth() {
        return dateMonth;
    }

    public void setDateMonth(String dateMonth) {
        this.dateMonth = dateMonth;
    }

    public String getTotal_app() {
        return total_app;
    }

    public void setTotal_app(String total_app) {
        this.total_app = total_app;
    }

    public String getCancel_app() {
        return cancel_app;
    }

    public void setCancel_app(String cancel_app) {
        this.cancel_app = cancel_app;
    }
}
