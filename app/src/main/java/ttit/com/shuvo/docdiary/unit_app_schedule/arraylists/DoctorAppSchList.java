package ttit.com.shuvo.docdiary.unit_app_schedule.arraylists;

public class DoctorAppSchList {
    private String adm_date;
    private String schedule_time;
    private String patient_data;
    private String pat_name;
    private String pat_code;
    private String pat_cell;
    private String pfn_fee_name;
    private String position;
    private String calling_permission;

    public DoctorAppSchList(String adm_date, String schedule_time, String patient_data, String pat_name,
                            String pat_code, String pat_cell, String pfn_fee_name, String position, String calling_permission) {
        this.adm_date = adm_date;
        this.schedule_time = schedule_time;
        this.patient_data = patient_data;
        this.pat_name = pat_name;
        this.pat_code = pat_code;
        this.pat_cell = pat_cell;
        this.pfn_fee_name = pfn_fee_name;
        this.position = position;
        this.calling_permission = calling_permission;
    }

    public String getAdm_date() {
        return adm_date;
    }

    public void setAdm_date(String adm_date) {
        this.adm_date = adm_date;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public String getPatient_data() {
        return patient_data;
    }

    public void setPatient_data(String patient_data) {
        this.patient_data = patient_data;
    }

    public String getPat_name() {
        return pat_name;
    }

    public void setPat_name(String pat_name) {
        this.pat_name = pat_name;
    }

    public String getPat_code() {
        return pat_code;
    }

    public void setPat_code(String pat_code) {
        this.pat_code = pat_code;
    }

    public String getPfn_fee_name() {
        return pfn_fee_name;
    }

    public void setPfn_fee_name(String pfn_fee_name) {
        this.pfn_fee_name = pfn_fee_name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCalling_permission() {
        return calling_permission;
    }

    public void setCalling_permission(String calling_permission) {
        this.calling_permission = calling_permission;
    }

    public String getPat_cell() {
        return pat_cell;
    }

    public void setPat_cell(String pat_cell) {
        this.pat_cell = pat_cell;
    }
}
