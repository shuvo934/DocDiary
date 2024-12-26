package ttit.com.shuvo.docdiary.payment.arraylists;

public class SelectedPaymentMethodList {
    private String sl_no;
    private String pmm_id;
    private String pmm_name;
    private String pmd_id;
    private String ad_id;
    private String account_name;
    private String method_amount;
    private boolean inserted;

    public SelectedPaymentMethodList(String sl_no, String pmm_id, String pmm_name, String pmd_id, String ad_id, String account_name, String method_amount, boolean inserted) {
        this.sl_no = sl_no;
        this.pmm_id = pmm_id;
        this.pmm_name = pmm_name;
        this.pmd_id = pmd_id;
        this.ad_id = ad_id;
        this.account_name = account_name;
        this.method_amount = method_amount;
        this.inserted = inserted;
    }

    public String getSl_no() {
        return sl_no;
    }

    public void setSl_no(String sl_no) {
        this.sl_no = sl_no;
    }

    public String getPmm_id() {
        return pmm_id;
    }

    public void setPmm_id(String pmm_id) {
        this.pmm_id = pmm_id;
    }

    public String getPmm_name() {
        return pmm_name;
    }

    public void setPmm_name(String pmm_name) {
        this.pmm_name = pmm_name;
    }

    public String getPmd_id() {
        return pmd_id;
    }

    public void setPmd_id(String pmd_id) {
        this.pmd_id = pmd_id;
    }

    public String getAd_id() {
        return ad_id;
    }

    public void setAd_id(String ad_id) {
        this.ad_id = ad_id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getMethod_amount() {
        return method_amount;
    }

    public void setMethod_amount(String method_amount) {
        this.method_amount = method_amount;
    }

    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }
}
