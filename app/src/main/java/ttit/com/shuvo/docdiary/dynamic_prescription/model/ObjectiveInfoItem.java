package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

public class ObjectiveInfoItem {

    /*
     * Current prescription-এর মধ্যে
     * item edit/delete করার unique ID।
     */
    private final String localEntryId;

    private final String objectiveId;
    private final String objectiveName;

    private final String objectiveValueId;
    private final String objectiveValueName;

    /*
     * Muscle/Joint optional।
     */
    @Nullable
    private final String muscleJointId;

    @NonNull
    private final String muscleJointName;

    /*
     * Notes / Degree Value optional।
     * কিছু না থাকলে empty String থাকবে।
     */
    @NonNull
    private final String notes;

    public ObjectiveInfoItem(@NonNull String localEntryId, @NonNull String objectiveId, @NonNull String objectiveName, @NonNull String objectiveValueId, @NonNull String objectiveValueName, @Nullable String muscleJointId, @NonNull String muscleJointName, @NonNull String notes) {
        this.localEntryId = localEntryId.trim();

        this.objectiveId = objectiveId.trim();
        this.objectiveName = objectiveName.trim();

        this.objectiveValueId = objectiveValueId.trim();
        this.objectiveValueName = objectiveValueName.trim();

        this.muscleJointId = normalizeOptionalId(muscleJointId);
        this.muscleJointName = muscleJointName.trim();

        this.notes = notes.trim();
    }

    /**
     * নতুন Objective Info item তৈরি করবে।
     */

    @NonNull
    public static ObjectiveInfoItem fromSelections(@NonNull ClinicalOption objective, @Nullable ClinicalOption objectiveValue, @Nullable ClinicalOption muscleJoint, @NonNull String notes) {
        return new ObjectiveInfoItem(
                UUID.randomUUID().toString(),

                objective.getOptionId(),
                objective.getOptionName(),

                objectiveValue == null ? "" : objectiveValue.getOptionId(),
                objectiveValue == null ? "" : objectiveValue.getOptionName(),

                muscleJoint == null ? null : muscleJoint.getOptionId(),
                muscleJoint == null ? "" : muscleJoint.getOptionName(),

                notes
        );
    }

    /**
     * Existing item edit করবে।
     * localEntryId পরিবর্তন হবে না।
     */

    @NonNull
    public ObjectiveInfoItem updateFromSelections(@NonNull ClinicalOption objective, @Nullable ClinicalOption objectiveValue, @Nullable ClinicalOption muscleJoint, @NonNull String notes) {
        return new ObjectiveInfoItem(
                localEntryId,

                objective.getOptionId(),
                objective.getOptionName(),

                objectiveValue == null ? "" : objectiveValue.getOptionId(),
                objectiveValue == null ? "" : objectiveValue.getOptionName(),

                muscleJoint == null ? null : muscleJoint.getOptionId(),
                muscleJoint == null ? "" : muscleJoint.getOptionName(),

                notes
        );
    }

    @NonNull
    public String getLocalEntryId() {
        return localEntryId;
    }

    @NonNull
    public String getObjectiveId() {
        return objectiveId;
    }

    @NonNull
    public String getObjectiveName() {
        return objectiveName;
    }

    @NonNull
    public String getObjectiveValueId() {
        return objectiveValueId;
    }

    @NonNull
    public String getObjectiveValueName() {
        return objectiveValueName;
    }

    @Nullable
    public String getMuscleJointId() {
        return muscleJointId;
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
        return !objectiveValueId.isEmpty() && !objectiveValueName.isEmpty();
    }

    public boolean hasMuscleJoint() {
        return muscleJointId != null || !muscleJointName.isEmpty();
    }

    public boolean hasNotes() {
        return !notes.isEmpty();
    }

    /**
     * সম্পূর্ণ একই item আবার add হচ্ছে কি না
     * তা পরীক্ষা করবে।
     */
    public boolean hasSameValue(@NonNull ObjectiveInfoItem otherItem) {
        return hasSameRequiredOption(
                objectiveId,
                objectiveName,
                otherItem.objectiveId,
                otherItem.objectiveName
        ) && hasSameRequiredOption(
                objectiveValueId,
                objectiveValueName,
                otherItem.objectiveValueId,
                otherItem.objectiveValueName
        ) && hasSameOptionalOption(
                muscleJointId,
                muscleJointName,
                otherItem.muscleJointId,
                otherItem.muscleJointName
        ) && notes.equalsIgnoreCase(
                otherItem.notes
        );
    }

    private boolean hasSameRequiredOption(@NonNull String firstId, @NonNull String firstName, @NonNull String secondId, @NonNull String secondName) {
        if (!firstId.isEmpty() && !secondId.isEmpty()) {
            return firstId.equals(secondId);
        }

        return firstName.equalsIgnoreCase(secondName);
    }

    private boolean hasSameOptionalOption(@Nullable String firstId, @NonNull String firstName, @Nullable String secondId, @NonNull String secondName) {
        if (firstId != null && secondId != null) {
            return firstId.equals(secondId);
        }

        return firstName.equalsIgnoreCase(secondName);
    }

    @Nullable
    private String normalizeOptionalId(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}