package ttit.com.shuvo.docdiary.payment.arraylists;

public class UpdatedServiceList {
    private String prd_id;
    private String prd_pfn_id;
    private String pfn_fee_name;
    private String prd_depts_id;
    private String depts_name;
    private String prd_rate;
    private String prd_top_cat_rate;
    private String prd_qty;
    private String amount;
    private String available_qty;
    private String prd_sched_avail_mark;
    private String prd_cancel_mark;
    private String prd_return_mark;
    private boolean updated;

    public UpdatedServiceList(String prd_id, String prd_pfn_id, String pfn_fee_name, String prd_depts_id, String depts_name, String prd_rate, String prd_top_cat_rate, String prd_qty, String amount, String available_qty, String prd_sched_avail_mark, String prd_cancel_mark, String prd_return_mark, boolean updated) {
        this.prd_id = prd_id;
        this.prd_pfn_id = prd_pfn_id;
        this.pfn_fee_name = pfn_fee_name;
        this.prd_depts_id = prd_depts_id;
        this.depts_name = depts_name;
        this.prd_rate = prd_rate;
        this.prd_top_cat_rate = prd_top_cat_rate;
        this.prd_qty = prd_qty;
        this.amount = amount;
        this.available_qty = available_qty;
        this.prd_sched_avail_mark = prd_sched_avail_mark;
        this.prd_cancel_mark = prd_cancel_mark;
        this.prd_return_mark = prd_return_mark;
        this.updated = updated;
    }

    public String getPrd_id() {
        return prd_id;
    }

    public void setPrd_id(String prd_id) {
        this.prd_id = prd_id;
    }

    public String getPrd_pfn_id() {
        return prd_pfn_id;
    }

    public void setPrd_pfn_id(String prd_pfn_id) {
        this.prd_pfn_id = prd_pfn_id;
    }

    public String getPfn_fee_name() {
        return pfn_fee_name;
    }

    public void setPfn_fee_name(String pfn_fee_name) {
        this.pfn_fee_name = pfn_fee_name;
    }

    public String getPrd_depts_id() {
        return prd_depts_id;
    }

    public void setPrd_depts_id(String prd_depts_id) {
        this.prd_depts_id = prd_depts_id;
    }

    public String getDepts_name() {
        return depts_name;
    }

    public void setDepts_name(String depts_name) {
        this.depts_name = depts_name;
    }

    public String getPrd_rate() {
        return prd_rate;
    }

    public void setPrd_rate(String prd_rate) {
        this.prd_rate = prd_rate;
    }

    public String getPrd_top_cat_rate() {
        return prd_top_cat_rate;
    }

    public void setPrd_top_cat_rate(String prd_top_cat_rate) {
        this.prd_top_cat_rate = prd_top_cat_rate;
    }

    public String getPrd_qty() {
        return prd_qty;
    }

    public void setPrd_qty(String prd_qty) {
        this.prd_qty = prd_qty;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAvailable_qty() {
        return available_qty;
    }

    public void setAvailable_qty(String available_qty) {
        this.available_qty = available_qty;
    }

    public String getPrd_sched_avail_mark() {
        return prd_sched_avail_mark;
    }

    public void setPrd_sched_avail_mark(String prd_sched_avail_mark) {
        this.prd_sched_avail_mark = prd_sched_avail_mark;
    }

    public String getPrd_cancel_mark() {
        return prd_cancel_mark;
    }

    public void setPrd_cancel_mark(String prd_cancel_mark) {
        this.prd_cancel_mark = prd_cancel_mark;
    }

    public String getPrd_return_mark() {
        return prd_return_mark;
    }

    public void setPrd_return_mark(String prd_return_mark) {
        this.prd_return_mark = prd_return_mark;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
