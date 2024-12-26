package ttit.com.shuvo.docdiary.payment.arraylists;

public class PaymentModeMstList {
    private String pmm_id;
    private String pmd_id;
    private String ad_id;
    private String pmm_amnt;
    private boolean updated;

    public PaymentModeMstList(String pmm_id, String pmd_id, String ad_id, String pmm_amnt, boolean updated) {
        this.pmm_id = pmm_id;
        this.pmd_id = pmd_id;
        this.ad_id = ad_id;
        this.pmm_amnt = pmm_amnt;
        this.updated = updated;
    }

    public String getPmm_id() {
        return pmm_id;
    }

    public void setPmm_id(String pmm_id) {
        this.pmm_id = pmm_id;
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

    public String getPmm_amnt() {
        return pmm_amnt;
    }

    public void setPmm_amnt(String pmm_amnt) {
        this.pmm_amnt = pmm_amnt;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
