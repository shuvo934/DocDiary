package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;

public class PrescriptionHistoryItem {

    @NonNull
    private final String rvmId;

    @NonNull
    private final String rvmCode;

    @NonNull
    private final String prescriptionDate;

    @NonNull
    private final String appointmentDate;

    @NonNull
    private final String scheduleTime;

    @NonNull
    private final String departmentName;

    @NonNull
    private final String serviceName;

    @NonNull
    private final String admissionId;

    @NonNull
    private final String doctorId;

    @NonNull
    private final String doctorName;

    @NonNull
    private final String designationName;

    @NonNull
    private final String lastUpdatedTime;

    @NonNull
    private final String prescriptionType;

    @NonNull
    private final String patientType;

    public PrescriptionHistoryItem(@NonNull String rvmId, @NonNull String rvmCode, @NonNull String prescriptionDate,
                                   @NonNull String appointmentDate, @NonNull String scheduleTime, @NonNull String departmentName,
                                   @NonNull String serviceName, @NonNull String admissionId, @NonNull String doctorId,
                                   @NonNull String doctorName, @NonNull String designationName, @NonNull String lastUpdatedTime,
                                   @NonNull String prescriptionType, @NonNull String patientType) {
        this.rvmId = clean(rvmId);
        this.rvmCode = clean(rvmCode);
        this.prescriptionDate = clean(prescriptionDate);
        this.appointmentDate = clean(appointmentDate);
        this.scheduleTime = clean(scheduleTime);
        this.departmentName = clean(departmentName);
        this.serviceName = clean(serviceName);
        this.admissionId = clean(admissionId);
        this.doctorId = clean(doctorId);
        this.doctorName = clean(doctorName);
        this.designationName = clean(designationName);
        this.lastUpdatedTime = clean(lastUpdatedTime);
        this.prescriptionType = cleanLegacyValue(prescriptionType);
        this.patientType = cleanLegacyValue(patientType);
    }

    @NonNull
    public String getRvmId() {
        return rvmId;
    }

    @NonNull
    public String getRvmCode() {
        return rvmCode;
    }

    @NonNull
    public String getPrescriptionDate() {
        return prescriptionDate;
    }

    @NonNull
    public String getAppointmentDate() {
        return appointmentDate;
    }

    @NonNull
    public String getScheduleTime() {
        return scheduleTime;
    }

    @NonNull
    public String getDepartmentName() {
        return departmentName;
    }

    @NonNull
    public String getServiceName() {
        return serviceName;
    }

    @NonNull
    public String getAdmissionId() {
        return admissionId;
    }

    @NonNull
    public String getDoctorId() {
        return doctorId;
    }

    @NonNull
    public String getDoctorName() {
        return doctorName;
    }

    @NonNull
    public String getDesignationName() {
        return designationName;
    }

    @NonNull
    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    @NonNull
    public String getPrescriptionType() {
        return prescriptionType;
    }

    @NonNull
    public String getPatientType() {
        return patientType;
    }

    @NonNull
    private String clean(String value) {
        if (value == null || "null".equalsIgnoreCase(value.trim())) {
            return "";
        }

        return value.trim();
    }

    @NonNull
    private String cleanLegacyValue(String value) {
        String cleanedValue = clean(value);

        if ("No Data Found".equalsIgnoreCase(cleanedValue)) {
            return "";
        }

        return cleanedValue;
    }
}