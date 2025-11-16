package ttit.com.shuvo.docdiary.hr_accounts.dashboards.arraylists;

public class OpasPayCardWiseList {
    private String card_name;
    private String all_pay_count;
    private String c_pay_count;
    private String f_pay_count;
    private String na_pay_count;

    public OpasPayCardWiseList(String card_name, String all_pay_count, String c_pay_count, String f_pay_count, String na_pay_count) {
        this.card_name = card_name;
        this.all_pay_count = all_pay_count;
        this.c_pay_count = c_pay_count;
        this.f_pay_count = f_pay_count;
        this.na_pay_count = na_pay_count;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
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
