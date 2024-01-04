package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists;

public class PatMedicationList {
    private String pmp_id;
    private String pmp_pmm_id;
    private String medicine_id;
    private String medicine_name;
    private String mpm_id;
    private String mpm_name;
    private String dose_id;
    private String dose_name;
    private String pmp_duration;

    public PatMedicationList(String pmp_id, String pmp_pmm_id, String medicine_id, String medicine_name, String mpm_id, String mpm_name, String dose_id, String dose_name, String pmp_duration) {
        this.pmp_id = pmp_id;
        this.pmp_pmm_id = pmp_pmm_id;
        this.medicine_id = medicine_id;
        this.medicine_name = medicine_name;
        this.mpm_id = mpm_id;
        this.mpm_name = mpm_name;
        this.dose_id = dose_id;
        this.dose_name = dose_name;
        this.pmp_duration = pmp_duration;
    }

    public String getPmp_id() {
        return pmp_id;
    }

    public void setPmp_id(String pmp_id) {
        this.pmp_id = pmp_id;
    }

    public String getPmp_pmm_id() {
        return pmp_pmm_id;
    }

    public void setPmp_pmm_id(String pmp_pmm_id) {
        this.pmp_pmm_id = pmp_pmm_id;
    }

    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getMpm_id() {
        return mpm_id;
    }

    public void setMpm_id(String mpm_id) {
        this.mpm_id = mpm_id;
    }

    public String getMpm_name() {
        return mpm_name;
    }

    public void setMpm_name(String mpm_name) {
        this.mpm_name = mpm_name;
    }

    public String getDose_id() {
        return dose_id;
    }

    public void setDose_id(String dose_id) {
        this.dose_id = dose_id;
    }

    public String getDose_name() {
        return dose_name;
    }

    public void setDose_name(String dose_name) {
        this.dose_name = dose_name;
    }

    public String getPmp_duration() {
        return pmp_duration;
    }

    public void setPmp_duration(String pmp_duration) {
        this.pmp_duration = pmp_duration;
    }
}
