package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists;

public class PatDrugHistList {
    private String pdh_id;
    private String pdh_pmm_id;
    private String medicine_id;
    private String medicine_name;
    private String pdh_details;

    public PatDrugHistList(String pdh_id, String pdh_pmm_id, String medicine_id, String medicine_name, String pdh_details) {
        this.pdh_id = pdh_id;
        this.pdh_pmm_id = pdh_pmm_id;
        this.medicine_id = medicine_id;
        this.medicine_name = medicine_name;
        this.pdh_details = pdh_details;
    }

    public String getPdh_id() {
        return pdh_id;
    }

    public void setPdh_id(String pdh_id) {
        this.pdh_id = pdh_id;
    }

    public String getPdh_pmm_id() {
        return pdh_pmm_id;
    }

    public void setPdh_pmm_id(String pdh_pmm_id) {
        this.pdh_pmm_id = pdh_pmm_id;
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

    public String getPdh_details() {
        return pdh_details;
    }

    public void setPdh_details(String pdh_details) {
        this.pdh_details = pdh_details;
    }
}
