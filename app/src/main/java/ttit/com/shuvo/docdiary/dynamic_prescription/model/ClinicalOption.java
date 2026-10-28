package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ClinicalOption {
    private final String optionId;
    private final String optionName;
    private final String doctorInfo;

    public ClinicalOption(@NonNull String optionId, @NonNull String optionName, @Nullable String doctorInfo) {
        this.optionId = optionId.trim();
        this.optionName = optionName.trim();
        this.doctorInfo = doctorInfo == null ? "" : doctorInfo.trim();
    }

    @NonNull
    public String getOptionId() {
        return optionId;
    }

    @NonNull
    public String getOptionName() {
        return optionName;
    }

    @NonNull
    public String getDoctorInfo() {
        return doctorInfo;
    }

    public boolean hasDoctorInfo() {
        return !doctorInfo.isEmpty() && !doctorInfo.equals("-");
    }

    /*
     * MaterialAutoCompleteTextView-তে option select করলে
     * field-এর ভিতরে শুধু option name দেখাবে।
     */
    @NonNull
    @Override
    public String toString() {
        return optionName;
    }
}