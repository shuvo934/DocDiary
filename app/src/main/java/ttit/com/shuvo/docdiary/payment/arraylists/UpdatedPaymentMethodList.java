package ttit.com.shuvo.docdiary.payment.arraylists;

public class UpdatedPaymentMethodList {
    private String sl_no;
    private String prmd_id;
    private String prmd_pmm_id;
    private String pmm_name;
    private String prmd_pmd_id;
    private String prmd_ad_id;
    private String account_name;
    private String prmd_amt;
    private boolean updated;
    private String insertDeleteTag; // 0 for nothing, 1 for insert and 2 for delete

    public UpdatedPaymentMethodList(String sl_no, String prmd_id, String prmd_pmm_id, String pmm_name,
                                    String prmd_pmd_id, String prmd_ad_id, String account_name,
                                    String prmd_amt, boolean updated, String insertDeleteTag) {
        this.sl_no = sl_no;
        this.prmd_id = prmd_id;
        this.prmd_pmm_id = prmd_pmm_id;
        this.pmm_name = pmm_name;
        this.prmd_pmd_id = prmd_pmd_id;
        this.prmd_ad_id = prmd_ad_id;
        this.account_name = account_name;
        this.prmd_amt = prmd_amt;
        this.updated = updated;
        this.insertDeleteTag = insertDeleteTag;
    }

    public String getSl_no() {
        return sl_no;
    }

    public void setSl_no(String sl_no) {
        this.sl_no = sl_no;
    }

    public String getPrmd_id() {
        return prmd_id;
    }

    public void setPrmd_id(String prmd_id) {
        this.prmd_id = prmd_id;
    }

    public String getPrmd_pmm_id() {
        return prmd_pmm_id;
    }

    public void setPrmd_pmm_id(String prmd_pmm_id) {
        this.prmd_pmm_id = prmd_pmm_id;
    }

    public String getPmm_name() {
        return pmm_name;
    }

    public void setPmm_name(String pmm_name) {
        this.pmm_name = pmm_name;
    }

    public String getPrmd_pmd_id() {
        return prmd_pmd_id;
    }

    public void setPrmd_pmd_id(String prmd_pmd_id) {
        this.prmd_pmd_id = prmd_pmd_id;
    }

    public String getPrmd_ad_id() {
        return prmd_ad_id;
    }

    public void setPrmd_ad_id(String prmd_ad_id) {
        this.prmd_ad_id = prmd_ad_id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getPrmd_amt() {
        return prmd_amt;
    }

    public void setPrmd_amt(String prmd_amt) {
        this.prmd_amt = prmd_amt;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getInsertDeleteTag() {
        return insertDeleteTag;
    }

    public void setInsertDeleteTag(String insertDeleteTag) {
        this.insertDeleteTag = insertDeleteTag;
    }
}
