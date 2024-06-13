package ttit.com.shuvo.docdiary.appointment_admin.arraylists;

public class ServiceForAppList {
    private String prd_id;
    private String pfn_id;
    private String pfn_fee_name;
    private String service_qty;

    public ServiceForAppList(String prd_id, String pfn_id, String pfn_fee_name, String service_qty) {
        this.prd_id = prd_id;
        this.pfn_id = pfn_id;
        this.pfn_fee_name = pfn_fee_name;
        this.service_qty = service_qty;
    }

    public String getPrd_id() {
        return prd_id;
    }

    public void setPrd_id(String prd_id) {
        this.prd_id = prd_id;
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

    public String getService_qty() {
        return service_qty;
    }

    public void setService_qty(String service_qty) {
        this.service_qty = service_qty;
    }
}
