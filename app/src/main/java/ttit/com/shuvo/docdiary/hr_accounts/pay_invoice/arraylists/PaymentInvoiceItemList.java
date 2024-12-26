package ttit.com.shuvo.docdiary.hr_accounts.pay_invoice.arraylists;

public class PaymentInvoiceItemList {
    private String sl_no;
    private String pfn_fee_name;
    private String depts_name;
    private String prd_payment_for_month;
    private String prd_rate;
    private String prd_qty;
    private String amt;
    private String prd_service_charge_pct;
    private String prd_discount_amt;
    private String prm_amt;

    public PaymentInvoiceItemList(String sl_no, String pfn_fee_name, String depts_name, String prd_payment_for_month, String prd_rate,
                                  String prd_qty, String amt, String prd_service_charge_pct, String prd_discount_amt, String prm_amt) {
        this.sl_no = sl_no;
        this.pfn_fee_name = pfn_fee_name;
        this.depts_name = depts_name;
        this.prd_payment_for_month = prd_payment_for_month;
        this.prd_rate = prd_rate;
        this.prd_qty = prd_qty;
        this.amt = amt;
        this.prd_service_charge_pct = prd_service_charge_pct;
        this.prd_discount_amt = prd_discount_amt;
        this.prm_amt = prm_amt;
    }

    public String getSl_no() {
        return sl_no;
    }

    public void setSl_no(String sl_no) {
        this.sl_no = sl_no;
    }

    public String getPfn_fee_name() {
        return pfn_fee_name;
    }

    public void setPfn_fee_name(String pfn_fee_name) {
        this.pfn_fee_name = pfn_fee_name;
    }

    public String getDepts_name() {
        return depts_name;
    }

    public void setDepts_name(String depts_name) {
        this.depts_name = depts_name;
    }

    public String getPrd_payment_for_month() {
        return prd_payment_for_month;
    }

    public void setPrd_payment_for_month(String prd_payment_for_month) {
        this.prd_payment_for_month = prd_payment_for_month;
    }

    public String getPrd_rate() {
        return prd_rate;
    }

    public void setPrd_rate(String prd_rate) {
        this.prd_rate = prd_rate;
    }

    public String getPrd_qty() {
        return prd_qty;
    }

    public void setPrd_qty(String prd_qty) {
        this.prd_qty = prd_qty;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getPrd_service_charge_pct() {
        return prd_service_charge_pct;
    }

    public void setPrd_service_charge_pct(String prd_service_charge_pct) {
        this.prd_service_charge_pct = prd_service_charge_pct;
    }

    public String getPrd_discount_amt() {
        return prd_discount_amt;
    }

    public void setPrd_discount_amt(String prd_discount_amt) {
        this.prd_discount_amt = prd_discount_amt;
    }

    public String getPrm_amt() {
        return prm_amt;
    }

    public void setPrm_amt(String prm_amt) {
        this.prm_amt = prm_amt;
    }
}
