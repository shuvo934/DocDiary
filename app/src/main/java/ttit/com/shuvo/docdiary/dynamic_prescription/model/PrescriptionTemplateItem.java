package ttit.com.shuvo.docdiary.dynamic_prescription.model;

import androidx.annotation.NonNull;

public class PrescriptionTemplateItem {

    @NonNull
    private final String templateId;

    @NonNull
    private final String templateName;

    @NonNull
    private final String templateDescription;

    public PrescriptionTemplateItem(@NonNull String templateId, @NonNull String templateName, @NonNull String templateDescription) {
        this.templateId = templateId.trim();
        this.templateName = templateName.trim();
        this.templateDescription = templateDescription.trim();
    }

    @NonNull
    public String getTemplateId() {
        return templateId;
    }

    @NonNull
    public String getTemplateName() {
        return templateName;
    }

    @NonNull
    public String getTemplateDescription() {
        return templateDescription;
    }

    public boolean hasDescription() {
        return !templateDescription.isEmpty();
    }
}