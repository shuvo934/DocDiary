package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;

public class PrescriptionPatientDetails {

    @NonNull
    private final String patientName;

    @NonNull
    private final String patientCode;

    @NonNull
    private final String maritalStatus;

    @NonNull
    private final String gender;

    @NonNull
    private final String bloodGroup;

    @NonNull
    private final String patientCategory;

    @NonNull
    private final String address;

    @NonNull
    private final String mobileNumber;

    @NonNull
    private final String phoneNumber;

    @NonNull
    private final String age;

    @NonNull
    private final String registrationYear;

    @NonNull
    private final String fatherName;

    @NonNull
    private final String motherName;

    public PrescriptionPatientDetails(@NonNull String patientName, @NonNull String patientCode, @NonNull String maritalStatus, @NonNull String gender, @NonNull String bloodGroup, @NonNull String patientCategory, @NonNull String address, @NonNull String mobileNumber, @NonNull String phoneNumber, @NonNull String age, @NonNull String registrationYear, @NonNull String fatherName, @NonNull String motherName) {
        this.patientName = clean(patientName);
        this.patientCode = clean(patientCode);
        this.maritalStatus = clean(maritalStatus);
        this.gender = clean(gender);
        this.bloodGroup = clean(bloodGroup);
        this.patientCategory = clean(patientCategory);
        this.address = clean(address);
        this.mobileNumber = clean(mobileNumber);
        this.phoneNumber = clean(phoneNumber);
        this.age = clean(age);
        this.registrationYear = clean(registrationYear);
        this.fatherName = clean(fatherName);
        this.motherName = clean(motherName);
    }

    @NonNull
    public String getPatientName() {
        return patientName;
    }

    @NonNull
    public String getPatientCode() {
        return patientCode;
    }

    @NonNull
    public String getMaritalStatus() {
        return maritalStatus;
    }

    @NonNull
    public String getGender() {
        return gender;
    }

    @NonNull
    public String getBloodGroup() {
        return bloodGroup;
    }

    @NonNull
    public String getPatientCategory() {
        return patientCategory;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    @NonNull
    public String getMobileNumber() {
        return mobileNumber;
    }

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @NonNull
    public String getAge() {
        return age;
    }

    @NonNull
    public String getRegistrationYear() {
        return registrationYear;
    }

    @NonNull
    public String getFatherName() {
        return fatherName;
    }

    @NonNull
    public String getMotherName() {
        return motherName;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}