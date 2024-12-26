package ttit.com.shuvo.docdiary.payment.arraylists;

public class PaymentMethodList {
    private String pmm_id;
    private String pmm_name;
    private String pmd_id;
    private String ad_id;
    private String account_name;

    public PaymentMethodList(String pmm_id, String pmm_name, String pmd_id, String ad_id, String account_name) {
        this.pmm_id = pmm_id;
        this.pmm_name = pmm_name;
        this.pmd_id = pmd_id;
        this.ad_id = ad_id;
        this.account_name = account_name;
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
}
