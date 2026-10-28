package ttit.com.shuvo.docdiary.dynamic_prescription.preview;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPreviewData;

public final class PrescriptionPreviewHtmlBuilder {

    private static final String DEFAULT_PRIMARY_COLOR = "#087D49";
    private static final String DEFAULT_SECONDARY_COLOR = "#123B68";

    private PrescriptionPreviewHtmlBuilder() {
    }

    @NonNull
    public static String build(@NonNull PrescriptionPreviewData data) {
        String primaryColor = normalizeColor(data.getFacility().getPrimaryColor(), DEFAULT_PRIMARY_COLOR);
        String secondaryColor = normalizeColor(data.getFacility().getSecondaryColor(), DEFAULT_SECONDARY_COLOR);

        String primaryLight = mixWithWhite(primaryColor, 0.90f);
        String primaryLightPat = mixWithWhite(primaryColor, 0.97f);
        String primaryBorder = mixWithWhite(primaryColor, 0.70f);

        StringBuilder html = new StringBuilder(30000);

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width,initial-scale=1.0'>");

        appendStyles(
                html,
                primaryColor,
                secondaryColor,
                primaryLight,
                primaryBorder,
                primaryLightPat
        );

        html.append("</head>");
        html.append("<body>");
        html.append("<div class='sheet'>");

        appendHeader(html, data);

        appendPatientSummary(html, data);

        appendVitals(html, data.getVitals());

        appendMainContent(html, data);

        appendReferenceAndSignature(html, data);

        appendFooter(html, data.getFacility());

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    private static void appendStyles(@NonNull StringBuilder html, @NonNull String primary, @NonNull String secondary, @NonNull String light, @NonNull String border, @NonNull String lightPat) {
        html.append("<style>");

        html.append("@page{");
        html.append("size:A4;");
        html.append("margin:0;");
        html.append("}");

        html.append("*{");
        html.append("box-sizing:border-box;");
        html.append("}");

        html.append("html,body{");
        html.append("margin:0;");
        html.append("padding:0;");
        html.append("}");

        html.append("body{");
        html.append("background:#E9EEF3;");
        html.append("color:#263244;");
        html.append("font-family:Arial,'Noto Sans Bengali',sans-serif;");
        html.append("-webkit-print-color-adjust:exact;");
        html.append("print-color-adjust:exact;");
        html.append("}");

        html.append(".sheet{");
        html.append("width:210mm;");
        html.append("min-height:297mm;");
        html.append("margin:12px auto;");
        html.append("padding:10mm 10mm 0;");
        html.append("background:#FFFFFF;");
        html.append("box-shadow:0 3px 16px rgba(16,24,40,.16);");
        html.append("display:flex;");
        html.append("flex-direction:column;");
        html.append("}");

        html.append(".facility-header{");
        html.append("display:flex;");
        html.append("align-items:center;");
        html.append("justify-content:center;");
        html.append("gap:16px;");
        html.append("padding:0 5px 12px;");
        html.append("}");

        html.append(".facility-logo{");
        html.append("width:82px;");
        html.append("height:68px;");
        html.append("object-fit:contain;");
        html.append("flex:0 0 82px;");
        html.append("}");

        html.append(".logo-fallback{");
        html.append("width:68px;");
        html.append("height:68px;");
        html.append("border-radius:50%;");
        html.append("display:flex;");
        html.append("align-items:center;");
        html.append("justify-content:center;");
        html.append("background:").append(primary).append(";");
        html.append("color:#FFFFFF;");
        html.append("font-size:25px;");
        html.append("font-weight:800;");
        html.append("flex:0 0 68px;");
        html.append("}");

        html.append(".facility-info{");
        html.append("min-width:0;");
        html.append("flex:1;");
        html.append("}");

        html.append(".facility-name{");
        html.append("margin:0;");
        html.append("color:").append(secondary).append(";");
        html.append("font-size:21px;");
        html.append("font-family:Arial,'Noto Sans Bengali',sans-serif;");
        html.append("font-weight:900;");
        html.append("letter-spacing:1px;");
        html.append("line-height:1.25;");
        html.append("}");

        html.append(".facility-details,.facility-contact{");
        html.append("margin-top:4px;");
        html.append("color:#667085;");
        html.append("font-size:10px;");
        html.append("line-height:1.45;");
        html.append("white-space:pre-line;");
        html.append("}");

        html.append(".prescription-title{");
        html.append("text-align:center;");
        html.append("color:").append(secondary).append(";");
        html.append("font-size:16px;");
        html.append("font-weight:900;");
        html.append("letter-spacing:.5px;");
        html.append("padding:2px 0 10px;");
        html.append("}");

        html.append(".department-bar{");
        html.append("padding:8px 12px;");
        html.append("border-radius:6px;");
        html.append("background:").append(primary).append(";");
        html.append("color:#FFFFFF;");
        html.append("text-align:center;");
        html.append("font-size:14px;");
        html.append("font-weight:900;");
        html.append("letter-spacing:.45px;");
        html.append("}");

        html.append(".patient-card{");
        html.append("margin-top:10px;");
        html.append("border:1px solid ").append(border).append(";");
        html.append("border-radius:9px;");
        html.append("background:").append(lightPat).append(";");
        html.append("overflow:hidden;");
        html.append("break-inside:avoid;");
        html.append("page-break-inside:avoid;");
        html.append("}");

        html.append(".patient-grid{");
        html.append("display:grid;");
        html.append("grid-template-columns:repeat(3,1fr);");
        html.append("padding:4px 8px 7px;");
        html.append("}");

        html.append(".patient-grid-new{");
        html.append("display:grid;");
        html.append("grid-template-columns:1fr 1.1fr 0.9fr;");
        html.append("padding:4px 5px 7px;");
        html.append("}");

        html.append(".patient-item{");
        html.append("min-height:45px;");
        html.append("padding:9px 8px;");
        html.append("}");

        html.append(".patient-item-new{");
        html.append("min-height:10px;");
        html.append("align-items:center;");
        html.append("padding:3px 3px;");
        html.append("}");

        html.append(".meta-label{");
        html.append("display:block;");
        html.append("color:#667085;");
        html.append("font-size:7.5px;");
        html.append("font-weight:800;");
        html.append("text-transform:uppercase;");
        html.append("letter-spacing:.35px;");
        html.append("}");

        html.append(".meta-label-new{");
        html.append("color:#667085;");
        html.append("font-size:7.5px;");
        html.append("font-weight:800;");
        html.append("text-transform:uppercase;");
        html.append("letter-spacing:.35px;");
        html.append("}");

        html.append(".meta-value{");
        html.append("display:block;");
        html.append("margin-top:4px;");
        html.append("color:#202B3C;");
        html.append("font-size:11px;");
        html.append("font-weight:800;");
        html.append("line-height:1.35;");
        html.append("overflow-wrap:anywhere;");
        html.append("}");

        html.append(".meta-value-new{");
        html.append("color:#202B3C;");
        html.append("font-size:11px;");
        html.append("font-weight:800;");
        html.append("line-height:1.35;");
        html.append("overflow-wrap:anywhere;");
        html.append("}");

        html.append(".vitals{");
        html.append("display:flex;");
        html.append("margin-top:8px;");
        html.append("border:1px solid ").append(border).append(";");
        html.append("border-radius:7px;");
        html.append("background:").append(light).append(";");
        html.append("overflow:hidden;");
        html.append("break-inside:avoid;");
        html.append("page-break-inside:avoid;");
        html.append("}");

        html.append(".vital{");
        html.append("min-width:0;");
        html.append("padding:7px 4px;");
        html.append("text-align:center;");
        html.append("flex:1;");
        html.append("border-right:1px solid ").append(border).append(";");
        html.append("}");

        html.append(".vital:last-child{");
        html.append("border-right:0;");
        html.append("}");

        html.append(".vital-label{");
        html.append("display:block;");
        html.append("color:#667085;");
        html.append("font-size:7px;");
        html.append("font-weight:800;");
        html.append("text-transform:uppercase;");
        html.append("}");

        html.append(".vital-value{");
        html.append("display:block;");
        html.append("margin-top:3px;");
        html.append("color:").append(secondary).append(";");
        html.append("font-size:9.5px;");
        html.append("font-weight:900;");
        html.append("white-space:nowrap;");
        html.append("}");

        html.append(".columns{");
        html.append("display:grid;");
        html.append("grid-template-columns:minmax(0,1fr) minmax(0,1fr);");
        html.append("gap:20px;");
        html.append("margin-top:8px;");
        html.append("align-items:start;");
        html.append("}");

        html.append(".columns.single{");
        html.append("grid-template-columns:1fr;");
        html.append("}");

        html.append(".column{");
        html.append("min-width:0;");
        html.append("}");

        html.append(".rx-title{");
        html.append("margin:-2px 0 5px;");
        html.append("color:").append(secondary).append(";");
        html.append("font-family:Arial,Georgia,serif;");
        html.append("font-size:31px;");
        html.append("font-weight:900;");
        html.append("line-height:1;");
        html.append("}");

        html.append(".section{");
        html.append("margin-bottom:10px;");
        html.append("}");

        html.append(".section-title{");
        html.append("padding:4px 9px;");
        html.append("border-radius:5px;");
        html.append("background:").append(light).append(";");
        html.append("color:").append(primary).append(";");
        html.append("font-size:12px;");
        html.append("font-weight:900;");
        html.append("letter-spacing:.2px;");
        html.append("text-transform:uppercase;");
        html.append("break-after:avoid;");
        html.append("page-break-after:avoid;");
        html.append("}");

        html.append(".section-item{");
        html.append("display:flex;");
        html.append("gap:7px;");
        html.append("margin:4px 1px 0;");
        html.append("font-size:11px;");
        html.append("line-height:1.38;");
        html.append("break-inside:avoid;");
        html.append("page-break-inside:avoid;");
        html.append("}");

        html.append(".bullet{");
        html.append("width:4px;");
        html.append("height:4px;");
        html.append("margin-top:5px;");
        html.append("border-radius:50%;");
        html.append("background:").append(primary).append(";");
        html.append("flex:0 0 4px;");
        html.append("}");

        html.append(".item-body{");
        html.append("min-width:0;");
        html.append("flex:1;");
        html.append("overflow-wrap:anywhere;");
        html.append("}");

        html.append(".item-note{");
        html.append("margin-top:2px;");
        html.append("color:#667085;");
        html.append("font-size:8.5px;");
        html.append("font-style:italic;");
        html.append("}");

        html.append(".item-note-new{");
        html.append("color:#667085;");
        html.append("font-size:8.5px;");
        html.append("font-style:italic;");
        html.append("}");

        html.append(".medicine-card-new{");
        html.append("display:flex;");
        html.append("align-items:flex-start;");
        html.append("gap:5px;");
        html.append("margin:0;");
        html.append("padding:4px 0;");
        html.append("border:0;");
        html.append("border-bottom:1px solid #EAECF0;");
        html.append("border-radius:0;");
        html.append("break-inside:avoid;");
        html.append("page-break-inside:avoid;");
        html.append("}");

        html.append(".medicine-card-new:last-child{");
        html.append("border-bottom:0;");
        html.append("padding-bottom:1px;");
        html.append("}");

        html.append(".medicine-serial-new{");
        html.append("flex:0 0 15px;");
        html.append("color:").append(primary).append(";");
        html.append("font-size:9.5px;");
        html.append("font-weight:900;");
        html.append("line-height:1.3;");
        html.append("}");

        html.append(".medicine-content-new{");
        html.append("min-width:0;");
        html.append("flex:1;");
        html.append("}");

        html.append(".medicine-name-new{");
        html.append("color:").append(secondary).append(";");
        html.append("font-size:10px;");
        html.append("font-weight:900;");
        html.append("line-height:1.3;");
        html.append("}");

        html.append(".medicine-meta-new{");
        html.append("margin-top:1px;");
        html.append("color:#667085;");
        html.append("font-size:8.3px;");
        html.append("line-height:1.3;");
        html.append("}");

        html.append(".medicine-meta-new span+span:before{");
        html.append("content:'  |  ';");
        html.append("color:#98A2B3;");
        html.append("}");

        html.append(".medicine-duration-new{");
        html.append("color:").append(primary).append(";");
        html.append("font-weight:800;");
        html.append("}");

        html.append(".medicine-notes-new{");
        html.append("color:#667085;");
        html.append("font-style:italic;");
        html.append("}");

        html.append(".medicine-card{");
        html.append("margin:8px 0 9px;");
        html.append("padding:9px 10px;");
        html.append("border:1px solid ").append(border).append(";");
        html.append("border-radius:7px;");
        html.append("break-inside:avoid;");
        html.append("page-break-inside:avoid;");
        html.append("}");

        html.append(".medicine-name{");
        html.append("color:").append(secondary).append(";");
        html.append("font-size:10.5px;");
        html.append("font-weight:900;");
        html.append("line-height:1.35;");
        html.append("}");

        html.append(".medicine-meta{");
        html.append("margin-top:4px;");
        html.append("color:#667085;");
        html.append("font-size:8.7px;");
        html.append("line-height:1.45;");
        html.append("}");

        html.append(".medicine-meta span+span:before{");
        html.append("content:'  |  ';");
        html.append("color:#98A2B3;");
        html.append("}");

        html.append(".medicine-bottom{");
        html.append("display:flex;");
        html.append("justify-content:space-between;");
        html.append("gap:10px;");
        html.append("margin-top:5px;");
        html.append("font-size:8.7px;");
        html.append("line-height:1.35;");
        html.append("}");

        html.append(".medicine-duration{");
        html.append("color:").append(primary).append(";");
        html.append("font-weight:900;");
        html.append("}");

        html.append(".medicine-notes{");
        html.append("color:#667085;");
        html.append("text-align:right;");
        html.append("font-style:italic;");
        html.append("}");

        html.append(".special-alert{");
        html.append("margin-top:8px;");
        html.append("padding:9px 10px;");
        html.append("border:1px solid #F5A623;");
        html.append("border-radius:7px;");
        html.append("background:#FFF8EA;");
        html.append("break-inside:avoid;");
        html.append("page-break-inside:avoid;");
        html.append("}");

        html.append(".alert-title{");
        html.append("color:#B54708;");
        html.append("font-size:9px;");
        html.append("font-weight:900;");
        html.append("text-transform:uppercase;");
        html.append("}");

        html.append(".alert-item{");
        html.append("margin-top:5px;");
        html.append("color:#263244;");
        html.append("font-size:9px;");
        html.append("line-height:1.4;");
        html.append("}");

        html.append(".bottom-info{");
        html.append("display:grid;");
        html.append("grid-template-columns:minmax(0,1.35fr) minmax(220px,.65fr);");
        html.append("gap:22px;");
        html.append("margin-top:auto;");
        html.append("padding:16px 0 22px;");
        html.append("border-top:1px solid ").append(border).append(";");
        html.append("break-inside:avoid;");
        html.append("page-break-inside:avoid;");
        html.append("}");

        html.append(".reference-title{");
        html.append("color:").append(secondary).append(";");
        html.append("font-size:9.5px;");
        html.append("font-weight:900;");
        html.append("text-transform:uppercase;");
        html.append("}");

        html.append(".reference-value{");
        html.append("margin-top:5px;");
        html.append("color:#667085;");
        html.append("font-size:9px;");
        html.append("line-height:1.5;");
        html.append("white-space:pre-line;");
        html.append("overflow-wrap:anywhere;");
        html.append("}");

        html.append(".reference-extra-value{");
        html.append("margin-top:1px;");
        html.append("color:#667085;");
        html.append("font-size:9px;");
        html.append("line-height:1.5;");
        html.append("white-space:pre-line;");
        html.append("overflow-wrap:anywhere;");
        html.append("}");

        html.append(".signature{");
        html.append("padding-top:8px;");
        html.append("border-top:1px solid ").append(secondary).append(";");
        html.append("text-align:right;");
        html.append("}");

        html.append(".signature-name{");
        html.append("color:").append(secondary).append(";");
        html.append("font-size:10px;");
        html.append("font-weight:900;");
        html.append("}");

        html.append(".signature-detail{");
        html.append("margin-top:3px;");
        html.append("color:#667085;");
        html.append("font-size:8.5px;");
        html.append("line-height:1.4;");
        html.append("}");

        html.append(".footer{");
        html.append("margin:0 -10mm;");
        html.append("padding:8px 10mm 9px;");
        html.append("background:").append(primary).append(";");
        html.append("color:#FFFFFF;");
        html.append("text-align:center;");
        html.append("font-size:8.5px;");
        html.append("line-height:1.55;");
        html.append("}");

        html.append("@media print{");

        html.append("body{");
        html.append("background:#FFFFFF;");
        html.append("}");

        html.append(".sheet{");
        html.append("width:210mm;");
        html.append("min-height:297mm;");
        html.append("margin:0;");
        html.append("padding:10mm 10mm 0;");
        html.append("box-shadow:none;");
        html.append("}");

        html.append("}");

        html.append("</style>");
    }

    private static void appendHeader(@NonNull StringBuilder html, @NonNull PrescriptionPreviewData data) {
        PrescriptionPreviewData.Facility facility = data.getFacility();

        String logoUri = getLogoDataUri(facility.getLogoBase64());

        html.append("<header class='facility-header'>");

        if (!logoUri.isEmpty()) {
            html.append("<img class='facility-logo' src='").append(htmlEscape(logoUri)).append("' alt='Facility logo'>");
        } else {
            html.append("<div class='logo-fallback'>Rx</div>");
        }

        html.append("<div class='facility-info'>");

        html.append("<h1 class='facility-name'>")
                .append(htmlEscape(valueOrDefault(facility.getName(), "Patient Data Management System")))
                .append("</h1>");

        if (!facility.getDetails().isEmpty()) {
            html.append("<div class='facility-details'>")
                    .append(htmlEscape(facility.getDetails()))
                    .append("</div>");
        }

        String contact = buildFacilityContact(facility);

        if (!contact.isEmpty()) {
            html.append("<div class='facility-contact'>")
                    .append(htmlEscape(contact))
                    .append("</div>");
        }

        html.append("</div>");
        html.append("</header>");

        html.append("<div class='prescription-title'>")
                .append("REHABILITATION PRESCRIPTION")
                .append("</div>");

        html.append("<div class='department-bar'>")
                .append(htmlEscape(getDepartmentTitle(data.getProvider()).toUpperCase(Locale.US)))
                .append("</div>");
    }

    private static void appendPatientSummary(@NonNull StringBuilder html, @NonNull PrescriptionPreviewData data) {
        PrescriptionPreviewData.Patient patient = data.getPatient();

        html.append("<section class='patient-card'>");
        html.append("<div class='patient-grid-new'>");

        appendPatientItemHorizontal(
                html,
                "Prescription Date: ",
                data.getPrescriptionDate()
        );

        appendPatientItemHorizontal(
                html,
                "Prescription No: ",
                data.getRvmCode()
        );

        appendPatientItemHorizontal(
                html,
                "Patient Code: ",
                patient.getPatientCode()
        );

        appendPatientItemHorizontal(
                html,
                "Patient Name: ",
                patient.getPatientName()
        );

        appendPatientItemHorizontal(
                html,
                "Age / Gender: ",
                joinNonEmpty(patient.getAge(), patient.getGender(), " / ")
        );

//        appendPatientItemHorizontal(
//                html,
//                "Age: ",
//                patient.getAge()
//        );

//        appendPatientItemHorizontal(
//                html,
//                "Gender: ",
//                patient.getGender()
//        );

        appendPatientItemHorizontal(
                html,
                "Category / Type: ",
                buildCategoryType(data)
        );

        html.append("</div>");
        html.append("</section>");
    }

    private static void appendPatientItem(@NonNull StringBuilder html, @NonNull String label, @NonNull String value) {
        html.append("<div class='patient-item'>");
        html.append("<span class='meta-label'>")
                .append(htmlEscape(label))
                .append("</span>");

        html.append("<span class='meta-value'>").append(htmlEscape(valueOrDefault(value, "-")))
                .append("</span>");

        html.append("</div>");
    }

    private static void appendPatientItemHorizontal(@NonNull StringBuilder html, @NonNull String label, @NonNull String value) {
        html.append("<div class='patient-item-new'>");
        html.append("<span class='meta-label-new'>")
                .append(htmlEscape(label))
                .append("</span>");

        html.append("<span class='meta-value-new'>")
                .append(htmlEscape(valueOrDefault(value, "-")))
                .append("</span>");

        html.append("</div>");
    }

    private static void appendVitals(@NonNull StringBuilder html, @NonNull PrescriptionPreviewData.Vitals vitals) {
        if (vitals.isEmpty()) {
            return;
        }

        html.append("<section class='vitals'>");

        appendVital(
                html,
                "BP",
                buildBloodPressure(vitals),
                ""
        );

        appendVital(
                html,
                "Pulse",
                vitals.getPulse(),
                "bpm"
        );

        appendVital(
                html,
                "Resp.",
                vitals.getRespiration(),
                "/min"
        );

        appendVital(
                html,
                "Temp.",
                vitals.getTemperature(),
                "°F"
        );

        appendVital(
                html,
                "SpO2",
                vitals.getSpo2(),
                "%"
        );

        appendVital(
                html,
                "Height",
                vitals.getHeight(),
                "cm"
        );

        appendVital(
                html,
                "Weight",
                vitals.getWeight(),
                "kg"
        );

        appendVital(
                html,
                "BMI",
                vitals.getBmi(),
                "kg/m²"
        );

        html.append("</section>");
    }

    private static void appendVital(@NonNull StringBuilder html, @NonNull String label, @NonNull String value, @NonNull String unit) {
//        if (value.isEmpty()) {
//            return;
//        }
        if (value.isEmpty()) {
            value = "-";
        }

        html.append("<div class='vital'>");

        html.append("<span class='vital-label'>")
                .append(htmlEscape(label))
                .append("</span>");

        html.append("<span class='vital-value'>")
                .append(htmlEscape(value));

        if (!unit.isEmpty()) {
            if (!value.equals("-")) {
                html.append(" ")
                        .append(htmlEscape(unit));
            }
        }

        html.append("</span>");
        html.append("</div>");
    }

    private static void appendMainContent(@NonNull StringBuilder html, @NonNull PrescriptionPreviewData data) {
        boolean hasLeftContent = hasLeftContent(data);
        boolean hasRightContent = hasRightContent(data);

        if (!hasLeftContent && !hasRightContent) {
            return;
        }

        html.append("<main class='columns");

        if (!hasLeftContent || !hasRightContent) {
            html.append(" single");
        }

        html.append("'>");

        if (hasLeftContent) {
            appendLeftColumn(
                    html,
                    data
            );
        }

        if (hasRightContent) {
            appendRightColumn(
                    html,
                    data
            );
        }

        html.append("</main>");
    }

    private static void appendLeftColumn(@NonNull StringBuilder html, @NonNull PrescriptionPreviewData data) {
        html.append("<div class='column'>");

        appendSection(
                html,
                "Chief Complaints",
                data.getComplaints()
        );

        appendSectionInjury(
                html,
                data.getInjuries()
        );

        appendSection(
                html,
                "Diagnosis / Functional Problems",
                data.getDiagnosis()
        );

        appendObjectives(
                html,
                data.getObjectiveInfo()
        );

        appendSection(
                html,
                "Clinical Findings",
                data.getClinicalFindings()
        );

        appendSection(
                html,
                "Drug History",
                data.getDrugHistory()
        );

        appendSection(
                html,
                "Tests / Investigation",
                data.getInvestigations()
        );

        appendSection(
                html,
                "Important Information",
                data.getImportantInfo()
        );

        html.append("</div>");
    }

    private static void appendRightColumn(@NonNull StringBuilder html, @NonNull PrescriptionPreviewData data) {
        html.append("<div class='column'>");

        html.append("<div class='rx-title'>Rx</div>");

        appendMedications(
                html,
                data.getMedications()
        );

        appendSection(
                html,
                "Therapy Interventions",
                data.getTherapies()
        );

        appendSection(
                html,
                "Goals / Treatment Plan",
                data.getTreatmentPlans()
        );

        appendSection(
                html,
                "Advice",
                data.getAdvice()
        );

        appendSection(
                html,
                "Follow-up / Treatment Schedule",
                data.getFollowups()
        );

        appendSpecialAlerts(
                html,
                data.getSpecialAlerts()
        );

        html.append("</div>");
    }

    private static void appendSection(@NonNull StringBuilder html, @NonNull String title, @NonNull List<PrescriptionPreviewData.SectionItem> items) {
        if (items.isEmpty()) {
            return;
        }

        html.append("<section class='section'>");

        html.append("<div class='section-title'>")
                .append(htmlEscape(title))
                .append("</div>");

        for (PrescriptionPreviewData.SectionItem item : items) {
            html.append("<div class='section-item'>");

            html.append("<span class='bullet'></span>");

            html.append("<div class='item-body'>")
                    .append(htmlEscape(item.getText()));

//            if (item.hasNotes()) {
//                html.append("<div class='item-note'>")
//                        .append(htmlEscape(item.getNotes()))
//                        .append("</div>");
//            }
            if (item.hasNotes()) {
                html.append("<span class='item-note-new'>")
                        .append(" (")
                        .append(htmlEscape(item.getNotes()))
                        .append(")")
                        .append("</span>");
            }

            html.append("</div>");
            html.append("</div>");
        }

        html.append("</section>");
    }

    private static void appendSectionInjury(@NonNull StringBuilder html, @NonNull List<PrescriptionPreviewData.SectionItem> items) {
        if (items.isEmpty()) {
            return;
        }

        html.append("<section class='section'>");

        html.append("<div class='section-title'>")
                .append(htmlEscape("Injury"))
                .append("</div>");

        html.append("<div class='section-item'>");
        html.append("<span class='bullet'></span>");
        html.append("<div class='item-body'>");
        for (int i = 0; i < items.size(); i++) {
            PrescriptionPreviewData.SectionItem item = items.get(i);
            html.append(htmlEscape(item.getText()));
            if (i != items.size() - 1) {
                html.append(", ");
            }
        }
        html.append("</div>");
        html.append("</div>");
        html.append("</section>");
    }

    private static void appendObjectives(@NonNull StringBuilder html, @NonNull List<PrescriptionPreviewData.ObjectiveItem> items) {
        if (items.isEmpty()) {
            return;
        }

        html.append("<section class='section'>");
        html.append("<div class='section-title'>Objective Assessment</div>");

        for (PrescriptionPreviewData.ObjectiveItem item : items) {
            StringBuilder objectiveText = new StringBuilder();

            objectiveText.append(
                    item.getObjectiveName()
            );

            if (item.hasObjectiveValue()) {
                if (objectiveText.length() > 0) {
                    objectiveText.append(": ");
                }

                objectiveText.append(
                        item.getObjectiveValue()
                );
            }

            if (item.hasMuscleJoint()) {
                if (objectiveText.length() > 0) {
                    objectiveText.append(" - ");
                }

                objectiveText.append(
                        item.getMuscleJointName()
                );
            }

            if (item.hasNotes()) {
                if (objectiveText.length() > 0) {
                    objectiveText.append(" - ");
                }

                objectiveText.append(
                        item.getNotes()
                );
            }

            if (objectiveText.length() > 0) {
                html.append("<div class='section-item'>");
                html.append("<span class='bullet'></span>");

                html.append("<div class='item-body'>")
                        .append(htmlEscape(objectiveText.toString()))
                        .append("</div>");

                html.append("</div>");
            }
        }

        html.append("</section>");
    }

    private static void appendMedications(@NonNull StringBuilder html, @NonNull List<PrescriptionPreviewData.MedicationItem> items) {
        if (items.isEmpty()) {
            return;
        }

        html.append("<section class='section'>");
        html.append("<div class='section-title'>Medication</div>");

//        for (PrescriptionPreviewData.MedicationItem item : items) {
//            html.append("<div class='medicine-card'>");
//
//            html.append("<div class='medicine-name'>")
//                    .append(htmlEscape(item.getMedicineName()))
//                    .append("</div>");
//
//            html.append("<div class='medicine-meta'>");
//
//            appendMedicationMeta(
//                    html,
//                    item.getDoseName()
//            );
//
//            appendMedicationMeta(
//                    html,
//                    item.getTimingName()
//            );
//
//            appendMedicationMeta(
//                    html,
//                    item.getRouteName()
//            );
//
//            html.append("</div>");
//
//            if (item.hasDuration() || item.hasNotes()) {
//                html.append("<div class='medicine-bottom'>");
//
//                if (item.hasDuration()) {
//                    html.append("<span class='medicine-duration'>")
//                            .append("Duration: ")
//                            .append(htmlEscape(item.getDuration()))
//                            .append(" days")
//                            .append("</span>");
//                } else {
//                    html.append("<span></span>");
//                }
//
//                if (item.hasNotes()) {
//                    html.append("<span class='medicine-notes'>")
//                            .append(htmlEscape(item.getNotes()))
//                            .append("</span>");
//                }
//
//                html.append("</div>");
//            }
//
//            html.append("</div>");
//        }

        int medicineSerial = 1;

        for (PrescriptionPreviewData.MedicationItem item : items) {
            html.append("<div class='medicine-card-new'>");

            html.append("<span class='medicine-serial-new'>")
                    .append(medicineSerial)
                    .append(".</span>");

            html.append("<div class='medicine-content-new'>");

            html.append("<div class='medicine-name-new'>")
                    .append(htmlEscape(item.getMedicineName()))
                    .append("</div>");

            html.append("<div class='medicine-meta-new'>");

            appendMedicationMeta(
                    html,
                    item.getDoseName()
            );

            appendMedicationMeta(
                    html,
                    item.getTimingName()
            );

            appendMedicationMeta(
                    html,
                    item.getRouteName()
            );

            if (item.hasDuration()) {
                html.append("<span class='medicine-duration-new'>")
                        .append(htmlEscape(item.getDuration()))
                        .append(" days")
                        .append("</span>");
            }

            if (item.hasNotes()) {
                html.append("<span class='medicine-notes-new'>")
                        .append("Note: ")
                        .append(htmlEscape(item.getNotes()))
                        .append("</span>");
            }

            html.append("</div>");
            html.append("</div>");
            html.append("</div>");

            medicineSerial++;
        }

        html.append("</section>");
    }

    private static void appendMedicationMeta(@NonNull StringBuilder html, @NonNull String value) {
        if (!value.isEmpty()) {
            html.append("<span>")
                    .append(htmlEscape(value))
                    .append("</span>");
        }
    }

    private static void appendSpecialAlerts(@NonNull StringBuilder html, @NonNull List<PrescriptionPreviewData.SectionItem> items) {
        if (items.isEmpty()) {
            return;
        }

        html.append("<section class='special-alert'>");
        html.append("<div class='alert-title'>Special Alert</div>");

        for (PrescriptionPreviewData.SectionItem item : items) {
            html.append("<div class='alert-item'>")
                    .append(htmlEscape(item.getText()));

            if (item.hasNotes()) {
                html.append(" - ")
                        .append(htmlEscape(item.getNotes()));
            }

            html.append("</div>");
        }

        html.append("</section>");
    }

    private static void appendReferenceAndSignature(@NonNull StringBuilder html, @NonNull PrescriptionPreviewData data) {
        String patientReference = data.getPatient().getReference();

        boolean hasPatientReference = !patientReference.isEmpty();

        boolean hasPrescriptionReference = !data.getReference().isEmpty();

        boolean hasPrescriptionNotes = !data.getNotes().isEmpty();

        html.append("<section class='bottom-info'>");
        html.append("<div>");

        if (hasPatientReference || hasPrescriptionReference || hasPrescriptionNotes) {

            html.append("<div class='reference-title'>")
                    .append("Reference / Notes")
                    .append("</div>");

//            html.append("<div class='reference-value'>");
//
//            if (hasPatientReference) {
//                html.append("Referred by: ")
//                        .append(htmlEscape(patientReference));
//            }
//
//            if (hasPatientReference
//                    && (hasPrescriptionReference || hasPrescriptionNotes)) {
//                html.append(" | ");
//            }
//
//            if (hasPrescriptionReference) {
//                html.append(
//                        htmlEscape(
//                                data.getReference()
//                        )
//                );
//            }
//
//            if (hasPrescriptionReference
//                    && hasPrescriptionNotes) {
//                html.append(" | ");
//            }
//
//            if (hasPrescriptionNotes) {
//                html.append(
//                        htmlEscape(
//                                data.getNotes()
//                        )
//                );
//            }
//
//            html.append("</div>");

            if (hasPatientReference) {
                html.append("<div class='reference-value'>");
                html.append("Referred by: ")
                        .append(htmlEscape(patientReference));
                html.append("</div>");
            }

            if (hasPrescriptionReference) {
                html.append("<div class='reference-extra-value'>");
                html.append("Doctor/Therapist Reference: ")
                        .append(htmlEscape(data.getReference()));
                html.append("</div>");
            }

            if (hasPrescriptionNotes) {
                html.append("<div class='reference-extra-value'>");
                html.append("Doctor/Therapist Notes: ")
                        .append(htmlEscape(data.getNotes()));
                html.append("</div>");
            }
        }

        html.append("</div>");

        PrescriptionPreviewData.Provider provider =
                data.getProvider();

        html.append("<div class='signature'>");

        html.append("<div class='signature-name'>")
                .append(htmlEscape(valueOrDefault(provider.getDoctorName(), "Prescribing Clinician")))
                .append("</div>");

        if (!provider.getDesignation().isEmpty()) {
            html.append("<div class='signature-detail'>")
                    .append(htmlEscape(provider.getDesignation()))
                    .append("</div>");
        }

        String workplace = joinNonEmpty(
                provider.getUnit(),
                provider.getDepartment(),
                ", "
        );

        if (!workplace.isEmpty()) {
            html.append("<div class='signature-detail'>")
                    .append(htmlEscape(workplace))
                    .append("</div>");
        }

        html.append("</div>");
        html.append("</section>");
    }

    private static void appendFooter(@NonNull StringBuilder html, @NonNull PrescriptionPreviewData.Facility facility) {
        ArrayList<String> facilityInformation =
                new ArrayList<>();

        addIfNotEmpty(facilityInformation, facility.getDetails(),"");
        addIfNotEmpty(facilityInformation, facility.getPhone(), "Mobile: ");
        addIfNotEmpty(facilityInformation, facility.getCell(), "Phone: ");
        addIfNotEmpty(facilityInformation, facility.getEmail(),"Email: ");
        addIfNotEmpty(facilityInformation, facility.getWebsite(),"Web: ");

        html.append("<footer class='footer'>");

        if (!facilityInformation.isEmpty()) {
            html.append(
                    htmlEscape(
                            join(
                                    facilityInformation,
                                    "  |  "
                            )
                    )
            );

            html.append("<br>");
        }

        html.append("Digitally generated prescription");
        html.append("  |  Printed: ");
        html.append(
                htmlEscape(
                        getPrintedTime()
                )
        );
        html.append("  |  Page 1 of 1");

        html.append("</footer>");
    }

    private static boolean hasLeftContent(@NonNull PrescriptionPreviewData data) {
        return !data.getComplaints().isEmpty()
                || !data.getInjuries().isEmpty()
                || !data.getDiagnosis().isEmpty()
                || !data.getObjectiveInfo().isEmpty()
                || !data.getClinicalFindings().isEmpty()
                || !data.getDrugHistory().isEmpty()
                || !data.getInvestigations().isEmpty()
                || !data.getImportantInfo().isEmpty();
    }

    private static boolean hasRightContent(@NonNull PrescriptionPreviewData data) {
        return !data.getMedications().isEmpty()
                || !data.getTherapies().isEmpty()
                || !data.getTreatmentPlans().isEmpty()
                || !data.getAdvice().isEmpty()
                || !data.getFollowups().isEmpty()
                || !data.getSpecialAlerts().isEmpty();
    }

    @NonNull
    private static String buildCategoryType(@NonNull PrescriptionPreviewData data) {
        ArrayList<String> values = new ArrayList<>();

        addIfNotEmpty(values, data.getPatient().getCategory(),"");

        String visitType = joinNonEmpty(data.getPatientType(), data.getPrescriptionType(), " - ");

        addIfNotEmpty(values, visitType,"");

        return join(values, " | ");
    }

    @NonNull
    private static String getDepartmentTitle(@NonNull PrescriptionPreviewData.Provider provider) {
        String unit;

//        if (!provider.getUnit().isEmpty()) {
//            unit = provider.getUnit();
//        } else {
//            unit = provider.getDepartment();
//        }

//        if (unit.isEmpty()) {
//            return "Rehabilitation Services";
//        }

//        if (unit.toLowerCase(Locale.US).startsWith("department")) {
//            return unit;
//        }

//        return "Department of " + unit;

        if (!provider.getDepartment().isEmpty()) {
            unit = provider.getDepartment();
        } else {
            unit = "Department of Rehabilitation Services";
        }



        return unit;
    }

    @NonNull
    private static String buildBloodPressure(@NonNull PrescriptionPreviewData.Vitals vitals) {
        String systolic = vitals.getSystolic();

        String diastolic = vitals.getDiastolic();

        if (systolic.isEmpty() && diastolic.isEmpty()) {
            return "";
        }

        if (systolic.isEmpty()) {
            return "-/" + diastolic;
        }

        if (diastolic.isEmpty()) {
            return systolic + "/-";
        }

        return systolic + "/" + diastolic;
    }

    @NonNull
    private static String buildFacilityContact(@NonNull PrescriptionPreviewData.Facility facility) {
        ArrayList<String> values = new ArrayList<>();

        addIfNotEmpty(values, facility.getPhone(), "Mobile: ");
        addIfNotEmpty(values, facility.getCell(), "Phone: ");
        addIfNotEmpty(values, facility.getEmail(), "Email: ");
        addIfNotEmpty(values, facility.getWebsite(), "Web: ");

        return join(values, "  |  ");
    }

    @NonNull
    private static String getPrintedTime() {
        return new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(new Date()
        ).toUpperCase(Locale.US);
    }

    @NonNull
    private static String getLogoDataUri(@NonNull String logoBase64) {
        String cleanLogo =
                logoBase64.replaceAll(
                        "\\s+",
                        ""
                );

        if (cleanLogo.isEmpty()) {
            return "";
        }

        if (cleanLogo.startsWith("data:image/")) {
            return cleanLogo;
        }

        String mimeType =
                "image/png";

        if (cleanLogo.startsWith("/9j/")) {
            mimeType = "image/jpeg";

        } else if (cleanLogo.startsWith("R0lGOD")) {
            mimeType = "image/gif";

        } else if (cleanLogo.startsWith("PHN2Zy")) {
            mimeType = "image/svg+xml";
        }

        return "data:"
                + mimeType
                + ";base64,"
                + cleanLogo;
    }

    @NonNull
    private static String normalizeColor(@NonNull String color, @NonNull String fallback) {
        String cleanColor = color.trim();

        if (cleanColor.matches("^#[0-9A-Fa-f]{6}$")) {
            return cleanColor;
        }

        if (cleanColor.matches("^[0-9A-Fa-f]{6}$")) {
            return "#" + cleanColor;
        }

        return fallback;
    }

    @NonNull
    private static String mixWithWhite(@NonNull String color, float whiteRatio) {
        try {
            int red = Integer.parseInt(
                    color.substring(1, 3),
                    16
            );

            int green = Integer.parseInt(
                    color.substring(3, 5),
                    16
            );

            int blue = Integer.parseInt(
                    color.substring(5, 7),
                    16
            );

            red = Math.round(
                    red + (255 - red) * whiteRatio
            );

            green = Math.round(
                    green + (255 - green) * whiteRatio
            );

            blue = Math.round(
                    blue + (255 - blue) * whiteRatio
            );

            return String.format(
                    Locale.US,
                    "#%02X%02X%02X",
                    red,
                    green,
                    blue
            );

        } catch (Exception exception) {
            return "#EAF6F0";
        }
    }

    private static void addIfNotEmpty(@NonNull List<String> values, @NonNull String value, String preText) {
        if (!value.isEmpty()) {
            values.add(preText+value);
        }
    }

    @NonNull
    private static String join(@NonNull List<String> values, @NonNull String separator) {
        StringBuilder result = new StringBuilder();

        for (String value : values) {
            if (result.length() > 0) {
                result.append(separator);
            }

            result.append(value);
        }

        return result.toString();
    }

    @NonNull
    private static String joinNonEmpty(@NonNull String first, @NonNull String second, @NonNull String separator) {
        if (first.isEmpty()) {
            return second;
        }

        if (second.isEmpty()) {
            return first;
        }

        return first + separator + second;
    }

    @NonNull
    private static String valueOrDefault(@NonNull String value, @NonNull String fallback) {
        if (value.isEmpty()) {
            return fallback;
        }

        return value;
    }

    @NonNull
    private static String htmlEscape(@NonNull String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}