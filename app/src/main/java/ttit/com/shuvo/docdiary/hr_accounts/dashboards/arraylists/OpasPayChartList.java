package ttit.com.shuvo.docdiary.hr_accounts.dashboards.arraylists;

public class OpasPayChartList {
    private String id;
    private String all_dates;
    private String all_pay_count;
    private String c_pay_count;
    private String f_pay_count;
    private String na_pay_count;

    public OpasPayChartList(String id, String all_dates, String all_pay_count, String c_pay_count, String f_pay_count, String na_pay_count) {
        this.id = id;
        this.all_dates = all_dates;
        this.all_pay_count = all_pay_count;
        this.c_pay_count = c_pay_count;
        this.f_pay_count = f_pay_count;
        this.na_pay_count = na_pay_count;
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

    public String getAll_pay_count() {
        return all_pay_count;
    }

    public void setAll_pay_count(String all_pay_count) {
        this.all_pay_count = all_pay_count;
    }

    public String getC_pay_count() {
        return c_pay_count;
    }

    public void setC_pay_count(String c_pay_count) {
        this.c_pay_count = c_pay_count;
    }

    public String getF_pay_count() {
        return f_pay_count;
    }

    public void setF_pay_count(String f_pay_count) {
        this.f_pay_count = f_pay_count;
    }

    public String getNa_pay_count() {
        return na_pay_count;
    }

    public void setNa_pay_count(String na_pay_count) {
        this.na_pay_count = na_pay_count;
    }
}
