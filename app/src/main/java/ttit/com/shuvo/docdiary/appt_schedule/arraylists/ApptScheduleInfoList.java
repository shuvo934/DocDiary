package ttit.com.shuvo.docdiary.appt_schedule.arraylists;

public class ApptScheduleInfoList {
    private String time;
    private String apptInfo;
    private String patientName;
    private String apptStatus;
    private String patient_code;
    private String appointment_date;
    private String patient_age;
    private String pfn_fee_name;
    private String doc_video_link;
    private String ts_video_conf_flag;


    public ApptScheduleInfoList(String time, String apptInfo, String patientName, String apptStatus, String patient_code, String appointment_date, String patient_age, String pfn_fee_name, String doc_video_link, String ts_video_conf_flag) {
        this.time = time;
        this.apptInfo = apptInfo;
        this.patientName = patientName;
        this.apptStatus = apptStatus;
        this.patient_code = patient_code;
        this.appointment_date = appointment_date;
        this.patient_age = patient_age;
        this.pfn_fee_name = pfn_fee_name;
        this.doc_video_link = doc_video_link;
        this.ts_video_conf_flag = ts_video_conf_flag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getApptInfo() {
        return apptInfo;
    }

    public void setApptInfo(String apptInfo) {
        this.apptInfo = apptInfo;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getApptStatus() {
        return apptStatus;
    }

    public void setApptStatus(String apptStatus) {
        this.apptStatus = apptStatus;
    }

    public String getPatient_code() {
        return patient_code;
    }

    public void setPatient_code(String patient_code) {
        this.patient_code = patient_code;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public String getPatient_age() {
        return patient_age;
    }

    public void setPatient_age(String patient_age) {
        this.patient_age = patient_age;
    }

    public String getPfn_fee_name() {
        return pfn_fee_name;
    }

    public void setPfn_fee_name(String pfn_fee_name) {
        this.pfn_fee_name = pfn_fee_name;
    }

    public String getDoc_video_link() {
        return doc_video_link;
    }

    public void setDoc_video_link(String doc_video_link) {
        this.doc_video_link = doc_video_link;
    }

    public String getTs_video_conf_flag() {
        return ts_video_conf_flag;
    }

    public void setTs_video_conf_flag(String ts_video_conf_flag) {
        this.ts_video_conf_flag = ts_video_conf_flag;
    }
}
