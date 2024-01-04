package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists;

public class PatAdviceList {
    private String pa_id;
    private String pa_name;
    private String pa_pmm_id;

    public PatAdviceList(String pa_id, String pa_name, String pa_pmm_id) {
        this.pa_id = pa_id;
        this.pa_name = pa_name;
        this.pa_pmm_id = pa_pmm_id;
    }

    public String getPa_id() {
        return pa_id;
    }

    public void setPa_id(String pa_id) {
        this.pa_id = pa_id;
    }

    public String getPa_name() {
        return pa_name;
    }

    public void setPa_name(String pa_name) {
        this.pa_name = pa_name;
    }

    public String getPa_pmm_id() {
        return pa_pmm_id;
    }

    public void setPa_pmm_id(String pa_pmm_id) {
        this.pa_pmm_id = pa_pmm_id;
    }
}
