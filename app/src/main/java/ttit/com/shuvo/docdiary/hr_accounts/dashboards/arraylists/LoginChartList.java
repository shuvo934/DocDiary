package ttit.com.shuvo.docdiary.hr_accounts.dashboards.arraylists;

public class LoginChartList {
    private String id;
    private String dateMonth;
    private String totCount;
    private String uniqueCount;

    public LoginChartList(String id, String dateMonth, String totCount, String uniqueCount) {
        this.id = id;
        this.dateMonth = dateMonth;
        this.totCount = totCount;
        this.uniqueCount = uniqueCount;
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

    public String getTotCount() {
        return totCount;
    }

    public void setTotCount(String totCount) {
        this.totCount = totCount;
    }

    public String getUniqueCount() {
        return uniqueCount;
    }

    public void setUniqueCount(String uniqueCount) {
        this.uniqueCount = uniqueCount;
    }
}
