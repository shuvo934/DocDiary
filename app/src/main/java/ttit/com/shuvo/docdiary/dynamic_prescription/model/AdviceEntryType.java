package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ttit.com.shuvo.docdiary.R;

public enum AdviceEntryType {
    ADVICE(
            "advice",
            "Advice",
            R.drawable.ic_advice_24,
            InputMode.SEARCHABLE_CUSTOM
    ),

    FOLLOW_UP(
            "follow_up",
            "Follow-up",
            R.drawable.ic_follow_up_24,
            InputMode.SEARCHABLE_CUSTOM
    ),

    SPECIAL_ALERT(
            "special_alert",
            "Special Alert",
            R.drawable.ic_special_alert_24,
            InputMode.SEARCHABLE_CUSTOM
    ),

    REFERENCE(
            "reference",
            "Reference",
            R.drawable.ic_reference_24,
            InputMode.TEXT_ONLY
    ),

    NOTES(
            "notes",
            "Notes",
            R.drawable.ic_notes_24,
            InputMode.TEXT_ONLY
    );

    public enum InputMode {
        SEARCHABLE_CUSTOM,
        TEXT_ONLY
    }

    @NonNull
    private final String argumentValue;

    @NonNull
    private final String displayName;

    @DrawableRes
    private final int iconResource;

    @NonNull
    private final InputMode inputMode;

    AdviceEntryType(@NonNull String argumentValue, @NonNull String displayName, @DrawableRes int iconResource, @NonNull InputMode inputMode) {
        this.argumentValue = argumentValue;
        this.displayName = displayName;
        this.iconResource = iconResource;
        this.inputMode = inputMode;
    }

    @NonNull
    public String getArgumentValue() {
        return argumentValue;
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

    public boolean hasMasterOptions() {
        return inputMode == InputMode.SEARCHABLE_CUSTOM;
    }

    public boolean allowsCustomEntry() {
        return true;
    }

    public boolean isSingleEntry() {
        return this == REFERENCE || this == NOTES;
    }

    @NonNull
    public static AdviceEntryType fromArgument(@Nullable String argumentValue) {
        if (argumentValue != null) {
            for (AdviceEntryType entryType : values()) {
                if (entryType.argumentValue.equals(argumentValue)) {
                    return entryType;
                }
            }
        }

        return ADVICE;
    }
}