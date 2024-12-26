package ttit.com.shuvo.docdiary.payment.arraylists;

public class ServiceAmountIdList {
    private String pfn_id;
    private String amount;
    private String service_charge;
    private boolean updated;

    public ServiceAmountIdList(String pfn_id, String amount, String service_charge, boolean updated) {
        this.pfn_id = pfn_id;
        this.amount = amount;
        this.service_charge = service_charge;
        this.updated = updated;
    }

    public String getPfn_id() {
        return pfn_id;
    }

    public void setPfn_id(String pfn_id) {
        this.pfn_id = pfn_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getService_charge() {
        return service_charge;
    }

    public void setService_charge(String service_charge) {
        this.service_charge = service_charge;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
