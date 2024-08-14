package ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists;

public class PaymentRcvSumDataList {
    private String deptd_id;
    private String deptd_name;
    private String depts_id;
    private String depts_name;
    private String tot_amt;

    public PaymentRcvSumDataList(String deptd_id, String deptd_name, String depts_id, String depts_name, String tot_amt) {
        this.deptd_id = deptd_id;
        this.deptd_name = deptd_name;
        this.depts_id = depts_id;
        this.depts_name = depts_name;
        this.tot_amt = tot_amt;
    }

    public String getDeptd_id() {
        return deptd_id;
    }

    public void setDeptd_id(String deptd_id) {
        this.deptd_id = deptd_id;
    }

    public String getDeptd_name() {
        return deptd_name;
    }

    public void setDeptd_name(String deptd_name) {
        this.deptd_name = deptd_name;
    }

    public String getDepts_id() {
        return depts_id;
    }

    public void setDepts_id(String depts_id) {
        this.depts_id = depts_id;
    }

    public String getDepts_name() {
        return depts_name;
    }

    public void setDepts_name(String depts_name) {
        this.depts_name = depts_name;
    }

    public String getTot_amt() {
        return tot_amt;
    }

    public void setTot_amt(String tot_amt) {
        this.tot_amt = tot_amt;
    }
}
