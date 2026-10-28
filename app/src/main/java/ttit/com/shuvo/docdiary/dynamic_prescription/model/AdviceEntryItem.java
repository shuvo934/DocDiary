package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

public class AdviceEntryItem {

    /*
     * Current prescription-এর মধ্যে
     * Edit/Delete করার unique local ID।
     */
    @NonNull
    private final String localEntryId;

    /*
     * Advice/Follow-up/Special Alert master list
     * থেকে select করলে ID থাকবে।
     *
     * Reference, Notes অথবা সম্পূর্ণ custom text
     * হলে null থাকবে।
     */
    @Nullable
    private final String masterOptionId;

    /*
     * User prescription-এ যে final text দেখবে।
     */
    @NonNull
    private final String displayText;

    public AdviceEntryItem(@NonNull String localEntryId, @Nullable String masterOptionId, @NonNull String displayText) {
        this.localEntryId = localEntryId.trim();
        this.masterOptionId = normalizeMasterId(masterOptionId);
        this.displayText = displayText.trim();
    }

    /**
     * Master option select করার পর text customize করলে
     * Master ID রেখে final customized text save করবে।
     */
    @NonNull
    public static AdviceEntryItem fromMasterOption(@NonNull ClinicalOption option, @NonNull String finalDisplayText) {
        return new AdviceEntryItem(UUID.randomUUID().toString(), option.getOptionId(), finalDisplayText);
    }

    /**
     * কোনো master option select না করে custom text।
     */
    @NonNull
    public static AdviceEntryItem fromCustomText(@NonNull String customText) {
        return new AdviceEntryItem(UUID.randomUUID().toString(), null, customText);
    }

    /**
     * Existing item-এর Master ID রেখে
     * customized display text update করবে।
     */
    @NonNull
    public AdviceEntryItem updateFromMasterOption(@NonNull ClinicalOption option, @NonNull String finalDisplayText) {
        return new AdviceEntryItem(localEntryId, option.getOptionId(), finalDisplayText);
    }

    /**
     * Existing item custom text দিয়ে update করবে।
     */
    @NonNull
    public AdviceEntryItem updateFromCustomText(@NonNull String customText) {
        return new AdviceEntryItem(localEntryId, null, customText);
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

    public boolean isCustomEntry() {
        return masterOptionId == null;
    }

    /**
     * একই Master ID অথবা একই final text হলে
     * duplicate item হিসেবে ধরা হবে।
     */
    public boolean hasSameValue(@NonNull AdviceEntryItem otherItem) {
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