package ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists;

public class ChoiceServiceList {
    private String pph_pfn_id;
    private String pfn_name;

    public ChoiceServiceList(String pph_pfn_id, String pfn_name) {
        this.pph_pfn_id = pph_pfn_id;
        this.pfn_name = pfn_name;
    }

    public String getPph_pfn_id() {
        return pph_pfn_id;
    }

    public void setPph_pfn_id(String pph_pfn_id) {
        this.pph_pfn_id = pph_pfn_id;
    }

    public String getPfn_name() {
        return pfn_name;
    }

    public void setPfn_name(String pfn_name) {
        this.pfn_name = pfn_name;
    }
}
