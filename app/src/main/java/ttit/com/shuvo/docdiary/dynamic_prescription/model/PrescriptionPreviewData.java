package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PrescriptionPreviewData {

    @NonNull
    private final String rvmId;

    @NonNull
    private final String rvmCode;

    @NonNull
    private final String prescriptionDate;

    @NonNull
    private final String reference;

    @NonNull
    private final String notes;

    @NonNull
    private final String prescriptionType;

    @NonNull
    private final String patientType;

    @NonNull
    private final Facility facility;

    @NonNull
    private final Provider provider;

    @NonNull
    private final Patient patient;

    @NonNull
    private final Vitals vitals;

    @NonNull
    private final ArrayList<SectionItem> complaints;

    @NonNull
    private final ArrayList<SectionItem> injuries;

    @NonNull
    private final ArrayList<SectionItem> diagnosis;

    @NonNull
    private final ArrayList<ObjectiveItem> objectiveInfo;

    @NonNull
    private final ArrayList<SectionItem> drugHistory;

    @NonNull
    private final ArrayList<SectionItem> clinicalFindings;

    @NonNull
    private final ArrayList<SectionItem> investigations;

    @NonNull
    private final ArrayList<SectionItem> importantInfo;

    @NonNull
    private final ArrayList<MedicationItem> medications;

    @NonNull
    private final ArrayList<SectionItem> therapies;

    @NonNull
    private final ArrayList<SectionItem> treatmentPlans;

    @NonNull
    private final ArrayList<SectionItem> advice;

    @NonNull
    private final ArrayList<SectionItem> followups;

    @NonNull
    private final ArrayList<SectionItem> specialAlerts;

    public PrescriptionPreviewData(@NonNull JSONObject responseObject) throws JSONException {
        int responseStatus = responseObject.optInt("status", 0);

        if (responseStatus != 200) {
            String errorMessage = getStringValue(responseObject, "message");

            if (errorMessage.isEmpty()) {
                errorMessage = "Prescription preview data not found";
            }

            throw new JSONException(errorMessage);
        }

        JSONObject dataObject = responseObject.optJSONObject("data");

        if (dataObject == null) {
            throw new JSONException("Prescription preview information not found");
        }

        rvmId = getStringValue(dataObject, "rvm_id");
        rvmCode = getStringValue(dataObject, "rvm_code");
        prescriptionDate = getStringValue(dataObject, "presc_date");
        reference = getStringValue(dataObject, "reference");
        notes = getStringValue(dataObject, "notes");
        prescriptionType = getStringValue(dataObject, "presc_type");
        patientType = getStringValue(dataObject, "patient_type");

        facility = new Facility(dataObject.optJSONObject("facility"));
        provider = new Provider(dataObject.optJSONObject("provider"));
        patient = new Patient(dataObject.optJSONObject("patient"));
        vitals = new Vitals(dataObject.optJSONObject("vitals"));

        complaints = parseSectionItems(
                dataObject.optJSONArray("complaints"),
                "name",
                null
        );

        injuries = parseSectionItems(
                dataObject.optJSONArray("injuries"),
                "name",
                null
        );

        diagnosis = parseSectionItems(
                dataObject.optJSONArray("diagnosis"),
                "name",
                null
        );

        objectiveInfo = parseObjectiveItems(
                dataObject.optJSONArray("objective_info")
        );

        drugHistory = parseSectionItems(
                dataObject.optJSONArray("drug_history"),
                "name",
                null
        );

        clinicalFindings = parseSectionItems(
                dataObject.optJSONArray("clinical_findings"),
                "name",
                null
        );

        investigations = parseSectionItems(
                dataObject.optJSONArray("investigations"),
                "name",
                "notes"
        );

        importantInfo = parseSectionItems(
                dataObject.optJSONArray("important_info"),
                "text",
                null
        );

        medications = parseMedicationItems(
                dataObject.optJSONArray("medications")
        );

        therapies = parseSectionItems(
                dataObject.optJSONArray("therapies"),
                "name",
                "notes"
        );

        treatmentPlans = parseSectionItems(
                dataObject.optJSONArray("treatment_plans"),
                "name",
                null
        );

        advice = parseSectionItems(
                dataObject.optJSONArray("advice"),
                "text",
                null
        );

        followups = parseSectionItems(
                dataObject.optJSONArray("followups"),
                "text",
                null
        );

        specialAlerts = parseSectionItems(
                dataObject.optJSONArray("special_alerts"),
                "text",
                null
        );
    }

    @NonNull
    private static ArrayList<SectionItem> parseSectionItems(@Nullable JSONArray jsonArray, @NonNull String textKey, @Nullable String notesKey) {
        ArrayList<SectionItem> items = new ArrayList<>();

        if (jsonArray == null) {
            return items;
        }

        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject itemObject = jsonArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String text = getStringValue(itemObject, textKey);
            String itemNotes = notesKey == null
                    ? ""
                    : getStringValue(itemObject, notesKey);

            if (!text.isEmpty()) {
                items.add(new SectionItem(text, itemNotes));
            }
        }

        return items;
    }

    @NonNull
    private static ArrayList<ObjectiveItem> parseObjectiveItems(@Nullable JSONArray jsonArray) {
        ArrayList<ObjectiveItem> items = new ArrayList<>();

        if (jsonArray == null) {
            return items;
        }

        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject itemObject = jsonArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String objectiveName = getStringValue(itemObject, "om_name");
            String objectiveValue = getStringValue(itemObject, "od_name");
            String muscleJointName = getStringValue(itemObject, "mjl_name");
            String objectiveNotes = getStringValue(itemObject, "notes");

            if (!objectiveName.isEmpty() || !objectiveValue.isEmpty() || !muscleJointName.isEmpty() || !objectiveNotes.isEmpty()) {

                items.add(new ObjectiveItem(
                        objectiveName,
                        objectiveValue,
                        muscleJointName,
                        objectiveNotes
                ));
            }
        }

        return items;
    }

    @NonNull
    private static ArrayList<MedicationItem> parseMedicationItems(@Nullable JSONArray jsonArray) {
        ArrayList<MedicationItem> items = new ArrayList<>();

        if (jsonArray == null) {
            return items;
        }

        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject itemObject = jsonArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String medicineName = getStringValue(
                    itemObject,
                    "medicine_name"
            );

            if (medicineName.isEmpty()) {
                continue;
            }

            items.add(new MedicationItem(
                    medicineName,
                    getStringValue(itemObject, "dose_name"),
                    getStringValue(itemObject, "timing_name"),
                    getStringValue(itemObject, "route_name"),
                    getStringValue(itemObject, "duration"),
                    getStringValue(itemObject, "notes")
            ));
        }

        return items;
    }

    @NonNull
    private static String getStringValue(@Nullable JSONObject jsonObject, @NonNull String key) {
        if (jsonObject == null || !jsonObject.has(key) || jsonObject.isNull(key)) {
            return "";
        }

        Object value = jsonObject.opt(key);

        if (value == null || value == JSONObject.NULL) {
            return "";
        }

        return String.valueOf(value).trim();
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
    public String getReference() {
        return reference;
    }

    @NonNull
    public String getNotes() {
        return notes;
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
    public Facility getFacility() {
        return facility;
    }

    @NonNull
    public Provider getProvider() {
        return provider;
    }

    @NonNull
    public Patient getPatient() {
        return patient;
    }

    @NonNull
    public Vitals getVitals() {
        return vitals;
    }

    @NonNull
    public ArrayList<SectionItem> getComplaints() {
        return complaints;
    }

    @NonNull
    public ArrayList<SectionItem> getInjuries() {
        return injuries;
    }

    @NonNull
    public ArrayList<SectionItem> getDiagnosis() {
        return diagnosis;
    }

    @NonNull
    public ArrayList<ObjectiveItem> getObjectiveInfo() {
        return objectiveInfo;
    }

    @NonNull
    public ArrayList<SectionItem> getDrugHistory() {
        return drugHistory;
    }

    @NonNull
    public ArrayList<SectionItem> getClinicalFindings() {
        return clinicalFindings;
    }

    @NonNull
    public ArrayList<SectionItem> getInvestigations() {
        return investigations;
    }

    @NonNull
    public ArrayList<SectionItem> getImportantInfo() {
        return importantInfo;
    }

    @NonNull
    public ArrayList<MedicationItem> getMedications() {
        return medications;
    }

    @NonNull
    public ArrayList<SectionItem> getTherapies() {
        return therapies;
    }

    @NonNull
    public ArrayList<SectionItem> getTreatmentPlans() {
        return treatmentPlans;
    }

    @NonNull
    public ArrayList<SectionItem> getAdvice() {
        return advice;
    }

    @NonNull
    public ArrayList<SectionItem> getFollowups() {
        return followups;
    }

    @NonNull
    public ArrayList<SectionItem> getSpecialAlerts() {
        return specialAlerts;
    }

    public static class Facility {

        @NonNull
        private final String name;

        @NonNull
        private final String details;

        @NonNull
        private final String phone;

        @NonNull
        private final String cell;

        @NonNull
        private final String email;

        @NonNull
        private final String website;

        @NonNull
        private final String primaryColor;

        @NonNull
        private final String secondaryColor;

        @NonNull
        private final String logoBase64;

        private Facility(@Nullable JSONObject jsonObject) {
            name = getStringValue(jsonObject, "cim_name");
            details = getStringValue(jsonObject, "cim_details");
            phone = getStringValue(jsonObject, "cim_phone");
            cell = getStringValue(jsonObject, "cim_cell");
            email = getStringValue(jsonObject, "cim_email");
            website = getStringValue(jsonObject, "cim_web");
            primaryColor = getStringValue(jsonObject, "primary_color");
            secondaryColor = getStringValue(jsonObject, "secondary_color");
            logoBase64 = getStringValue(jsonObject, "cim_logo");
        }

        @NonNull
        public String getName() {
            return name;
        }

        @NonNull
        public String getDetails() {
            return details;
        }

        @NonNull
        public String getPhone() {
            return phone;
        }

        @NonNull
        public String getCell() {
            return cell;
        }

        @NonNull
        public String getEmail() {
            return email;
        }

        @NonNull
        public String getWebsite() {
            return website;
        }

        @NonNull
        public String getPrimaryColor() {
            return primaryColor;
        }

        @NonNull
        public String getSecondaryColor() {
            return secondaryColor;
        }

        @NonNull
        public String getLogoBase64() {
            return logoBase64;
        }
    }

    public static class Provider {

        @NonNull
        private final String doctorName;

        @NonNull
        private final String doctorCode;

        @NonNull
        private final String designation;

        @NonNull
        private final String unit;

        @NonNull
        private final String department;

        private Provider(@Nullable JSONObject jsonObject) {
            doctorName = getStringValue(jsonObject, "doc_name");
            doctorCode = getStringValue(jsonObject, "doc_code");
            designation = getStringValue(jsonObject, "designation");
            unit = getStringValue(jsonObject, "unit");
            department = getStringValue(jsonObject, "department");
        }

        @NonNull
        public String getDoctorName() {
            return doctorName;
        }

        @NonNull
        public String getDoctorCode() {
            return doctorCode;
        }

        @NonNull
        public String getDesignation() {
            return designation;
        }

        @NonNull
        public String getUnit() {
            return unit;
        }

        @NonNull
        public String getDepartment() {
            return department;
        }
    }

    public static class Patient {

        @NonNull
        private final String patientName;

        @NonNull
        private final String patientCode;

        @NonNull
        private final String gender;

        @NonNull
        private final String age;

        @NonNull
        private final String category;

        @NonNull
        private final String reference;

        private Patient(@Nullable JSONObject jsonObject) {
            patientName = getStringValue(jsonObject, "pat_name");
            patientCode = getStringValue(jsonObject, "sub_code");
            gender = getStringValue(jsonObject, "gender");
            age = getStringValue(jsonObject, "age");
            category = getStringValue(jsonObject, "category");
            reference = getStringValue(jsonObject, "reference");
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
        public String getGender() {
            return gender;
        }

        @NonNull
        public String getAge() {
            return age;
        }

        @NonNull
        public String getCategory() {
            return category;
        }

        @NonNull
        public String getReference() {
            return reference;
        }
    }

    public static class Vitals {

        @NonNull
        private final String weight;

        @NonNull
        private final String height;

        @NonNull
        private final String systolic;

        @NonNull
        private final String diastolic;

        @NonNull
        private final String pulse;

        @NonNull
        private final String respiration;

        @NonNull
        private final String temperature;

        @NonNull
        private final String spo2;

        @NonNull
        private final String bmi;

        private Vitals(@Nullable JSONObject jsonObject) {
            weight = getStringValue(jsonObject, "weight");
            height = getStringValue(jsonObject, "height");
            systolic = getStringValue(jsonObject, "systolic");
            diastolic = getStringValue(jsonObject, "diastolic");
            pulse = getStringValue(jsonObject, "pulse");
            respiration = getStringValue(jsonObject, "respiration");
            temperature = getStringValue(jsonObject, "temperature");
            spo2 = getStringValue(jsonObject, "spo2");
            bmi = getStringValue(jsonObject, "bmi");
        }

        @NonNull
        public String getWeight() {
            return weight;
        }

        @NonNull
        public String getHeight() {
            return height;
        }

        @NonNull
        public String getSystolic() {
            return systolic;
        }

        @NonNull
        public String getDiastolic() {
            return diastolic;
        }

        @NonNull
        public String getPulse() {
            return pulse;
        }

        @NonNull
        public String getRespiration() {
            return respiration;
        }

        @NonNull
        public String getTemperature() {
            return temperature;
        }

        @NonNull
        public String getSpo2() {
            return spo2;
        }

        @NonNull
        public String getBmi() {
            return bmi;
        }

        public boolean isEmpty() {
            return weight.isEmpty()
                    && height.isEmpty()
                    && systolic.isEmpty()
                    && diastolic.isEmpty()
                    && pulse.isEmpty()
                    && respiration.isEmpty()
                    && temperature.isEmpty()
                    && spo2.isEmpty()
                    && bmi.isEmpty();
        }
    }

    public static class SectionItem {

        @NonNull
        private final String text;

        @NonNull
        private final String notes;

        private SectionItem(
                @NonNull String text,
                @NonNull String notes
        ) {
            this.text = text;
            this.notes = notes;
        }

        @NonNull
        public String getText() {
            return text;
        }

        @NonNull
        public String getNotes() {
            return notes;
        }

        public boolean hasNotes() {
            return !notes.isEmpty();
        }
    }

    public static class ObjectiveItem {

        @NonNull
        private final String objectiveName;

        @NonNull
        private final String objectiveValue;

        @NonNull
        private final String muscleJointName;

        @NonNull
        private final String notes;

        private ObjectiveItem(@NonNull String objectiveName, @NonNull String objectiveValue, @NonNull String muscleJointName, @NonNull String notes) {
            this.objectiveName = objectiveName;
            this.objectiveValue = objectiveValue;
            this.muscleJointName = muscleJointName;
            this.notes = notes;
        }

        @NonNull
        public String getObjectiveName() {
            return objectiveName;
        }

        @NonNull
        public String getObjectiveValue() {
            return objectiveValue;
        }

        @NonNull
        public String getMuscleJointName() {
            return muscleJointName;
        }

        @NonNull
        public String getNotes() {
            return notes;
        }

        public boolean hasObjectiveValue() {
            return !objectiveValue.isEmpty();
        }

        public boolean hasMuscleJoint() {
            return !muscleJointName.isEmpty();
        }

        public boolean hasNotes() {
            return !notes.isEmpty();
        }
    }

    public static class MedicationItem {

        @NonNull
        private final String medicineName;

        @NonNull
        private final String doseName;

        @NonNull
        private final String timingName;

        @NonNull
        private final String routeName;

        @NonNull
        private final String duration;

        @NonNull
        private final String notes;

        private MedicationItem(@NonNull String medicineName, @NonNull String doseName, @NonNull String timingName, @NonNull String routeName, @NonNull String duration, @NonNull String notes) {
            this.medicineName = medicineName;
            this.doseName = doseName;
            this.timingName = timingName;
            this.routeName = routeName;
            this.duration = duration;
            this.notes = notes;
        }

        @NonNull
        public String getMedicineName() {
            return medicineName;
        }

        @NonNull
        public String getDoseName() {
            return doseName;
        }

        @NonNull
        public String getTimingName() {
            return timingName;
        }

        @NonNull
        public String getRouteName() {
            return routeName;
        }

        @NonNull
        public String getDuration() {
            return duration;
        }

        @NonNull
        public String getNotes() {
            return notes;
        }

        public boolean hasDuration() {
            return !duration.isEmpty();
        }

        public boolean hasNotes() {
            return !notes.isEmpty();
        }
    }
}