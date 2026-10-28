package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;

public class ReviewSummaryItem {

    private final String label;
    private final String value;

    public ReviewSummaryItem(@NonNull String label, @NonNull String value) {
        this.label = label;
        this.value = value;
    }

    @NonNull
    public String getLabel() {
        return label;
    }

    @NonNull
    public String getValue() {
        return value;
    }
}