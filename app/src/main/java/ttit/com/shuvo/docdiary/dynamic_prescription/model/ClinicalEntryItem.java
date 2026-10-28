package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

public class ClinicalEntryItem {

    /*
     * App-এর current prescription-এর মধ্যে
     * item edit/delete করার জন্য unique local ID।
     */
    private final String localEntryId;

    /*
     * Master list থেকে select করলে option ID থাকবে।
     * Custom text হলে null থাকবে।
     */
    private final String masterOptionId;

    private final String displayText;

    private final String notes;

    public ClinicalEntryItem(@NonNull String localEntryId, @Nullable String masterOptionId, @NonNull String displayText, @NonNull String notes) {
        this.localEntryId = localEntryId.trim();
        this.masterOptionId = normalizeMasterId(masterOptionId);
        this.displayText = displayText.trim();
        this.notes = notes.trim();
    }


    /**
     * Master option ID রেখে doctor-এর final
     * customized text save করবে।
     */
    @NonNull
    public static ClinicalEntryItem fromMasterOption(@NonNull ClinicalOption option, @NonNull String finalDisplayText, @NonNull String notes) {
        return new ClinicalEntryItem(UUID.randomUUID().toString(), option.getOptionId(), finalDisplayText, notes);
    }

    /**
     * কোনো master option select না করে
     * manually entered custom item তৈরি করবে।
     */
    @NonNull
    public static ClinicalEntryItem fromCustomText(@NonNull String customText, @NonNull String notes) {
        return new ClinicalEntryItem(UUID.randomUUID().toString(), null, customText, notes);
    }


    /**
     * Existing item-এর local ID একই রেখে
     * master option ID এবং customized final text save করবে।
     */
    @NonNull
    public ClinicalEntryItem updateFromMasterOption(@NonNull ClinicalOption option, @NonNull String finalDisplayText, @NonNull String updatedNotes) {
        return new ClinicalEntryItem(localEntryId, option.getOptionId(), finalDisplayText, updatedNotes);
    }

    /**
     * Existing item সম্পূর্ণ custom text হিসেবে update করবে।
     * এক্ষেত্রে master option ID null হয়ে যাবে।
     */
    @NonNull
    public ClinicalEntryItem updateFromCustomText(@NonNull String customText, @NonNull String updatedNotes) {
        return new ClinicalEntryItem(localEntryId, null, customText, updatedNotes);
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

    @NonNull
    public String getNotes() {
        return notes;
    }

    public boolean hasNotes() {
        return !notes.isEmpty();
    }

    public boolean isCustomEntry() {
        return masterOptionId == null;
    }

    /**
     * একই master ID অথবা একই final text হলে
     * duplicate item হিসেবে গণ্য হবে।
     */
    public boolean hasSameValue(@NonNull ClinicalEntryItem otherItem) {
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