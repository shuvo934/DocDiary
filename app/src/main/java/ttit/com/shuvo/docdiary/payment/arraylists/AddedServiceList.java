package ttit.com.shuvo.docdiary.payment.arraylists;

public class AddedServiceList {
    private String pfn_id;
    private String pfn_name;
    private String depts_id;
    private String depts_name;
    private String service_rate;
    private String service_top_rate;
    private String service_qty;
    private String service_amnt;
    private boolean inserted;

    public AddedServiceList(String pfn_id, String pfn_name, String depts_id, String depts_name, String service_rate, String service_top_rate, String service_qty, String service_amnt, boolean inserted) {
        this.pfn_id = pfn_id;
        this.pfn_name = pfn_name;
        this.depts_id = depts_id;
        this.depts_name = depts_name;
        this.service_rate = service_rate;
        this.service_top_rate = service_top_rate;
        this.service_qty = service_qty;
        this.service_amnt = service_amnt;
        this.inserted = inserted;
    }

    public String getPfn_id() {
        return pfn_id;
    }

    public void setPfn_id(String pfn_id) {
        this.pfn_id = pfn_id;
    }

    public String getPfn_name() {
        return pfn_name;
    }

    public void setPfn_name(String pfn_name) {
        this.pfn_name = pfn_name;
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

    public String getService_rate() {
        return service_rate;
    }

    public void setService_rate(String service_rate) {
        this.service_rate = service_rate;
    }

    public String getService_top_rate() {
        return service_top_rate;
    }

    public void setService_top_rate(String service_top_rate) {
        this.service_top_rate = service_top_rate;
    }

    public String getService_qty() {
        return service_qty;
    }

    public void setService_qty(String service_qty) {
        this.service_qty = service_qty;
    }

    public String getService_amnt() {
        return service_amnt;
    }

    public void setService_amnt(String service_amnt) {
        this.service_amnt = service_amnt;
    }

    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }
}
