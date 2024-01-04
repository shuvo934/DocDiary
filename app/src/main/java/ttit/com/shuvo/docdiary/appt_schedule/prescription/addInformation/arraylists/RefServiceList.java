package ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists;

public class RefServiceList {
    private String pfn_id;
    private String pfn_fee_name;

    public RefServiceList(String pfn_id, String pfn_fee_name) {
        this.pfn_id = pfn_id;
        this.pfn_fee_name = pfn_fee_name;
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
}
