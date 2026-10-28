package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;

public class PrescriptionPatientItem {

    @NonNull
    private final String patientId;

    @NonNull
    private final String patientName;

    @NonNull
    private final String patientHistoryId;

    @NonNull
    private final String patientCode;

    @NonNull
    private final String thanaName;

    @NonNull
    private final String registrationYear;

    @NonNull
    private final String mobileNumber;

    @NonNull
    private final String gender;

    @NonNull
    private final String age;

    public PrescriptionPatientItem(@NonNull String patientId, @NonNull String patientName, @NonNull String patientHistoryId, @NonNull String patientCode, @NonNull String thanaName, @NonNull String registrationYear, @NonNull String mobileNumber, @NonNull String gender, @NonNull String age) {
        this.patientId = clean(patientId);
        this.patientName = clean(patientName);
        this.patientHistoryId = clean(patientHistoryId);
        this.patientCode = clean(patientCode);
        this.thanaName = clean(thanaName);
        this.registrationYear = clean(registrationYear);
        this.mobileNumber = clean(mobileNumber);
        this.gender = clean(gender);
        this.age = clean(age);
    }

    @NonNull
    public String getPatientId() {
        return patientId;
    }

    @NonNull
    public String getPatientName() {
        return patientName;
    }

    @NonNull
    public String getPatientHistoryId() {
        return patientHistoryId;
    }

    @NonNull
    public String getPatientCode() {
        return patientCode;
    }

    @NonNull
    public String getThanaName() {
        return thanaName;
    }

    @NonNull
    public String getRegistrationYear() {
        return registrationYear;
    }

    @NonNull
    public String getMobileNumber() {
        return mobileNumber;
    }

    @NonNull
    public String getGender() {
        return gender;
    }

    @NonNull
    public String getAge() {
        return age;
    }

    @NonNull
    public String getGenderAgeText() {
        if (gender.isEmpty()) {
            return age;
        }

        if (age.isEmpty()) {
            return gender;
        }

        return gender + " • " + age;
    }

    @NonNull
    public String getLocationPhoneText() {
        if (mobileNumber.isEmpty()) {
            return thanaName;
        }

        if (thanaName.isEmpty()) {
            return mobileNumber;
        }

        return mobileNumber + " • " + thanaName;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}