package ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists;

public class PaymentReceiveDataList {
    private String deptd_id;
    private String deptd_name;
    private String depts_id;
    private String depts_name;
    private String pfn_id;
    private String pfn_fee_name;
    private String prd_rate;
    private String tot_qty;
    private String tot_amt;

    public PaymentReceiveDataList(String deptd_id, String deptd_name, String depts_id, String depts_name, String pfn_id, String pfn_fee_name, String prd_rate, String tot_qty, String tot_amt) {
        this.deptd_id = deptd_id;
        this.deptd_name = deptd_name;
        this.depts_id = depts_id;
        this.depts_name = depts_name;
        this.pfn_id = pfn_id;
        this.pfn_fee_name = pfn_fee_name;
        this.prd_rate = prd_rate;
        this.tot_qty = tot_qty;
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

    public String getPfn_id() {
        return pfn_id;
    }

    public void setPfn_id(String pfn_id) {
        this.pfn_id = pfn_id;
    }

    public String getPfn_fee_name() {
        return pfn_fee_name;
    }

    public void setPfn_fee_name(String pfn_fee_name) {
        this.pfn_fee_name = pfn_fee_name;
    }

    public String getPrd_rate() {
        return prd_rate;
    }

    public void setPrd_rate(String prd_rate) {
        this.prd_rate = prd_rate;
    }

    public String getTot_qty() {
        return tot_qty;
    }

    public void setTot_qty(String tot_qty) {
        this.tot_qty = tot_qty;
    }

    public String getTot_amt() {
        return tot_amt;
    }

    public void setTot_amt(String tot_amt) {
        this.tot_amt = tot_amt;
    }
}
