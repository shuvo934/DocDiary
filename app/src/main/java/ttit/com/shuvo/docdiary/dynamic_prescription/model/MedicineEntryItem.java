package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;

import java.util.UUID;

public class MedicineEntryItem {

    @NonNull
    private final String localEntryId;

    @NonNull
    private final String medicineId;

    @NonNull
    private final String medicineName;

    @NonNull
    private final String doseId;

    @NonNull
    private final String doseName;

    @NonNull
    private final String timingId;

    @NonNull
    private final String timingName;

    @NonNull
    private final String routeId;

    @NonNull
    private final String routeName;

    @NonNull
    private final String durationDays;

    @NonNull
    private final String notes;

    public MedicineEntryItem(@NonNull String localEntryId, @NonNull String medicineId, @NonNull String medicineName, @NonNull String doseId, @NonNull String doseName, @NonNull String timingId, @NonNull String timingName, @NonNull String routeId, @NonNull String routeName, @NonNull String durationDays, @NonNull String notes) {
        this.localEntryId = localEntryId.trim();
        this.medicineId = medicineId.trim();
        this.medicineName = medicineName.trim();
        this.doseId = doseId.trim();
        this.doseName = doseName.trim();
        this.timingId = timingId.trim();
        this.timingName = timingName.trim();
        this.routeId = routeId.trim();
        this.routeName = routeName.trim();
        this.durationDays = durationDays.trim();
        this.notes = notes.trim();
    }

    @NonNull
    public static MedicineEntryItem create(@NonNull ClinicalOption medicineOption, @NonNull ClinicalOption doseOption, @NonNull ClinicalOption timingOption, @NonNull ClinicalOption routeOption, @NonNull String durationDays, @NonNull String notes) {
        return new MedicineEntryItem(
                UUID.randomUUID().toString(),
                medicineOption.getOptionId(),
                medicineOption.getOptionName(),
                doseOption.getOptionId(),
                doseOption.getOptionName(),
                timingOption.getOptionId(),
                timingOption.getOptionName(),
                routeOption.getOptionId(),
                routeOption.getOptionName(),
                durationDays,
                notes
        );
    }

    @NonNull
    public MedicineEntryItem update(@NonNull ClinicalOption medicineOption, @NonNull ClinicalOption doseOption, @NonNull ClinicalOption timingOption, @NonNull ClinicalOption routeOption, @NonNull String durationDays, @NonNull String notes) {
        return new MedicineEntryItem(
                localEntryId,
                medicineOption.getOptionId(),
                medicineOption.getOptionName(),
                doseOption.getOptionId(),
                doseOption.getOptionName(),
                timingOption.getOptionId(),
                timingOption.getOptionName(),
                routeOption.getOptionId(),
                routeOption.getOptionName(),
                durationDays,
                notes
        );
    }

    @NonNull
    public String getLocalEntryId() {
        return localEntryId;
    }

    @NonNull
    public String getMedicineId() {
        return medicineId;
    }

    @NonNull
    public String getMedicineName() {
        return medicineName;
    }

    @NonNull
    public String getDoseId() {
        return doseId;
    }

    @NonNull
    public String getDoseName() {
        return doseName;
    }

    @NonNull
    public String getTimingId() {
        return timingId;
    }

    @NonNull
    public String getTimingName() {
        return timingName;
    }

    @NonNull
    public String getRouteId() {
        return routeId;
    }

    @NonNull
    public String getRouteName() {
        return routeName;
    }

    @NonNull
    public String getDurationDays() {
        return durationDays;
    }

    @NonNull
    public String getNotes() {
        return notes;
    }

    public boolean hasSameMedicine(@NonNull MedicineEntryItem otherItem) {
        if (!medicineId.isEmpty() && !otherItem.medicineId.isEmpty()) {
            return medicineId.equals(otherItem.medicineId);
        }

        return medicineName.equalsIgnoreCase(otherItem.medicineName);
    }
}