package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;

import java.util.UUID;

public class TherapyEntryItem {

    @NonNull
    private final String localEntryId;

    @NonNull
    private final String therapyId;

    @NonNull
    private final String therapyName;

    @NonNull
    private final String notes;

    public TherapyEntryItem(@NonNull String localEntryId, @NonNull String therapyId, @NonNull String therapyName, @NonNull String notes) {
        this.localEntryId = localEntryId.trim();
        this.therapyId = therapyId.trim();
        this.therapyName = therapyName.trim();
        this.notes = notes.trim();
    }

    @NonNull
    public static TherapyEntryItem create(@NonNull ClinicalOption therapyOption, @NonNull String notes) {
        return new TherapyEntryItem(
                UUID.randomUUID().toString(),
                therapyOption.getOptionId(),
                therapyOption.getOptionName(),
                notes
        );
    }

    @NonNull
    public TherapyEntryItem update(@NonNull ClinicalOption therapyOption, @NonNull String updatedNotes) {
        return new TherapyEntryItem(
                localEntryId,
                therapyOption.getOptionId(),
                therapyOption.getOptionName(),
                updatedNotes
        );
    }

    @NonNull
    public String getLocalEntryId() {
        return localEntryId;
    }

    @NonNull
    public String getTherapyId() {
        return therapyId;
    }

    @NonNull
    public String getTherapyName() {
        return therapyName;
    }

    @NonNull
    public String getNotes() {
        return notes;
    }

    public boolean hasNotes() {
        return !notes.isEmpty();
    }

    public boolean hasSameTherapy(@NonNull TherapyEntryItem otherItem) {
        if (!therapyId.isEmpty() && !otherItem.therapyId.isEmpty()) {
            return therapyId.equals(otherItem.therapyId);
        }

        return therapyName.equalsIgnoreCase(otherItem.therapyName);
    }
}