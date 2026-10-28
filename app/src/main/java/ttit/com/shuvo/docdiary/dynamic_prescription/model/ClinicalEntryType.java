package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ttit.com.shuvo.docdiary.R;

public enum ClinicalEntryType {
    COMPLAIN(
            "Complain",
            R.drawable.ic_complaint_24,
            InputMode.SEARCHABLE_CUSTOM
    ),

    INJURY(
            "Injury",
            R.drawable.patient,
            InputMode.SEARCHABLE_CUSTOM
    ),

    DIAGNOSIS(
            "Diagnosis",
            R.drawable.diagnosis,
            InputMode.SEARCHABLE_CUSTOM
    ),

    CLINICAL_FINDING(
            "Clinical Finding",
            R.drawable.ic_clinical_findings_24,
            InputMode.SEARCHABLE_CUSTOM
    ),

    DRUG_HISTORY(
            "Drug History",
            R.drawable.medicine,
            InputMode.SEARCHABLE_STRICT
    ),

    TESTS_INVESTIGATION(
            "Tests / Investigation",
            R.drawable.chemistry,
            InputMode.SEARCHABLE_STRICT
    ),

    IMPORTANT_INFO(
            "Important Info",
            R.drawable.caution,
            InputMode.TEXT_ONLY
    );

    @NonNull
    private final String displayName;

    @DrawableRes
    private final int iconResource;

    @NonNull
    private final InputMode inputMode;

    ClinicalEntryType(@NonNull String displayName, @DrawableRes int iconResource, @NonNull InputMode inputMode) {
        this.displayName = displayName;
        this.iconResource = iconResource;
        this.inputMode = inputMode;
    }

    @NonNull
    public String getDisplayName() {
        return displayName;
    }

    @DrawableRes
    public int getIconResource() {
        return iconResource;
    }

    @NonNull
    public InputMode getInputMode() {
        return inputMode;
    }

    /**
     * এই section-এর API/master dropdown list আছে কি না।
     */
    public boolean hasMasterOptions() {
        return inputMode != InputMode.TEXT_ONLY;
    }

    /**
     * Master list-এর বাইরে custom text লেখা যাবে কি না।
     */
    public boolean allowsCustomEntry() {
        return inputMode != InputMode.SEARCHABLE_STRICT;
    }

    /**
     * Fragment arguments-এ রাখার value।
     */
    @NonNull
    public String getArgumentValue() {
        return name();
    }

    /**
     * Fragment arguments-এর String থেকে
     * ClinicalEntryType ফেরত দেবে।
     */
    @NonNull
    public static ClinicalEntryType fromArgument(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) {
            return COMPLAIN;
        }
        try {
            return ClinicalEntryType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return COMPLAIN;
        }
    }

    public enum InputMode {

        /*
         * Dropdown থেকে select অথবা
         * custom text—দুটিই গ্রহণ করবে।
         */
        SEARCHABLE_CUSTOM,

        /*
         * শুধু master dropdown-এর
         * valid option গ্রহণ করবে।
         */
        SEARCHABLE_STRICT,

        /*
         * কোনো master list থাকবে না;
         * শুধু text input হবে।
         */
        TEXT_ONLY
    }
}