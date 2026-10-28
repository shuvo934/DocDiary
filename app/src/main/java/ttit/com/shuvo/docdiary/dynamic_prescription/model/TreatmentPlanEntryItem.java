package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

public class TreatmentPlanEntryItem {

    @NonNull
    private final String localEntryId;

    @Nullable
    private final String masterOptionId;

    @NonNull
    private final String displayText;

    public TreatmentPlanEntryItem(@NonNull String localEntryId, @Nullable String masterOptionId, @NonNull String displayText) {
        this.localEntryId = localEntryId.trim();
        this.masterOptionId = normalizeMasterId(masterOptionId);
        this.displayText = displayText.trim();
    }

    @NonNull
    public static TreatmentPlanEntryItem fromMasterOption(@NonNull ClinicalOption option) {
        return fromMasterOption(option, option.getOptionName());
    }

    @NonNull
    public static TreatmentPlanEntryItem fromMasterOption(@NonNull ClinicalOption option, @NonNull String finalDisplayText) {
        return new TreatmentPlanEntryItem(
                UUID.randomUUID().toString(),
                option.getOptionId(),
                finalDisplayText
        );
    }

    @NonNull
    public static TreatmentPlanEntryItem fromCustomText(@NonNull String customText) {
        return new TreatmentPlanEntryItem(
                UUID.randomUUID().toString(),
                null,
                customText
        );
    }

    @NonNull
    public TreatmentPlanEntryItem updateFromMasterOption(@NonNull ClinicalOption option) {
        return updateFromMasterOption(option, option.getOptionName());
    }

    @NonNull
    public TreatmentPlanEntryItem updateFromMasterOption(@NonNull ClinicalOption option, @NonNull String finalDisplayText) {
        return new TreatmentPlanEntryItem(
                localEntryId,
                option.getOptionId(),
                finalDisplayText
        );
    }

    @NonNull
    public TreatmentPlanEntryItem updateFromCustomText(@NonNull String customText) {
        return new TreatmentPlanEntryItem(
                localEntryId,
                null,
                customText
        );
    }

    @NonNull
    public String getLocalEntryId() {
        return localEntryId;
    }

    @Nullable
    public String getMasterOptionId() {
        return masterOptionId;
    }

    @NonNull
    public String getDisplayText() {
        return displayText;
    }

    public boolean hasSameValue(@NonNull TreatmentPlanEntryItem otherItem) {
        if (masterOptionId != null && otherItem.masterOptionId != null && masterOptionId.equals(otherItem.masterOptionId)) {
            return true;
        }

        return displayText.equalsIgnoreCase(otherItem.displayText);
    }

    @Nullable
    private String normalizeMasterId(@Nullable String masterId) {
        if (masterId == null || masterId.trim().isEmpty()) {
            return null;
        }

        return masterId.trim();
    }
}