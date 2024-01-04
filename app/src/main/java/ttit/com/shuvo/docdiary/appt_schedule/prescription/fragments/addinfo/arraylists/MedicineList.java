package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists;

public class MedicineList {
    private String medicine_id;
    private String medicine_name;

    public MedicineList(String medicine_id, String medicine_name) {
        this.medicine_id = medicine_id;
        this.medicine_name = medicine_name;
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
}
