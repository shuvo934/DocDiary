package ttit.com.shuvo.docdiary.payment.arraylists;

public class ServiceList {
    private String pfn_fee_name;
    private String pfn_id;
    private String pct_flag;

    public ServiceList(String pfn_fee_name, String pfn_id, String pct_flag) {
        this.pfn_fee_name = pfn_fee_name;
        this.pfn_id = pfn_id;
        this.pct_flag = pct_flag;
    }

    public String getPfn_fee_name() {
        return pfn_fee_name;
    }

    public void setPfn_fee_name(String pfn_fee_name) {
        this.pfn_fee_name = pfn_fee_name;
    }

    public String getPfn_id() {
        return pfn_id;
    }

    public void setPfn_id(String pfn_id) {
        this.pfn_id = pfn_id;
    }

    public String getPct_flag() {
        return pct_flag;
    }

    public void setPct_flag(String pct_flag) {
        this.pct_flag = pct_flag;
    }
}
