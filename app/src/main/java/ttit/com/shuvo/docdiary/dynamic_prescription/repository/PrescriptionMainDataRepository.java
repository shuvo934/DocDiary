package ttit.com.shuvo.docdiary.dynamic_prescription.repository;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalOption;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionHistoryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionTemplateItem;

public class PrescriptionMainDataRepository {

    private static final String BASE_URL = pre_url_api + "dynamic_presc/";
    private static final int REQUEST_TIMEOUT_MS = 15000;
    private static final int MAX_REQUEST_RETRIES = 0;
    private static volatile PrescriptionMainDataRepository instance;

    private final RequestQueue requestQueue;

    private PrescriptionMainDataRepository(@NonNull Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static PrescriptionMainDataRepository getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (PrescriptionMainDataRepository.class) {
                if (instance == null) {
                    instance = new PrescriptionMainDataRepository(context);
                }
            }
        }

        return instance;
    }

    // Existing Prescription Load API
    public void getExistingPrescription(@NonNull String phId, @NonNull String patientId, @NonNull String adId, @NonNull DataCallback<JSONObject> callback) {
        String prescriptionUrl = BASE_URL + "getPrescription?p_ph_id="+ phId + "&p_pat_id=" + patientId + "&p_ad_id=" + adId;

        StringRequest prescriptionRequest = new StringRequest(Request.Method.GET, prescriptionUrl, response -> {
            try {
                JSONObject rootObject = new JSONObject(response);
                int status = rootObject.optInt("status", 0);

                if (status != 200) {
                    String message = getRawSafeString(rootObject, "message");
                    throw new Exception(message.isEmpty() ? "Prescription could not be loaded" : message);
                }

                JSONObject dataObject = rootObject.optJSONObject("data");

                if (dataObject == null) {
                    throw new Exception("Prescription data not found");
                }

                callback.onSuccess(dataObject);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        }, error -> callback.onError(getPrescriptionLoadErrorMessage(error)));

        prescriptionRequest.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(prescriptionRequest);
    }

    // Template Load API
    public void getDiseaseTemplates(@NonNull String doctorId, @NonNull DataCallback<ArrayList<PrescriptionTemplateItem>> callback) {
        String url = BASE_URL + "getDiseaseTemplate?p_doc_id=" + doctorId;

        getTemplateList(
                url,
                "td_id",
                "td_name",
                "td_description",
                callback
        );
    }

    public void getTreatmentTemplates(@NonNull String doctorId, @NonNull String diseaseTemplateId, @NonNull DataCallback<ArrayList<PrescriptionTemplateItem>> callback) {
        String url = BASE_URL + "getTreatmentTemplate?p_doc_id=" + doctorId + "&p_td_id=" + diseaseTemplateId;

        getTemplateList(
                url,
                "tt_id",
                "tt_name",
                "tt_description",
                callback
        );
    }

    public void getDiseaseTemplateData(@NonNull String diseaseTemplateId, @NonNull DataCallback<JSONObject> callback) {
        String url = BASE_URL + "loadDiseaseTemplate?p_td_id=" + diseaseTemplateId;

        getTemplateData(
                url,
                "Disease Template could not be loaded",
                callback
        );
    }

    public void getTreatmentTemplateData(@NonNull String treatmentTemplateId, @NonNull DataCallback<JSONObject> callback) {
        String url = BASE_URL + "loadTreatmentTemplate?p_tt_id=" + treatmentTemplateId;

        getTemplateData(
                url,
                "Treatment Template could not be loaded",
                callback
        );
    }

    private void getTemplateList(@NonNull String url, @NonNull String idKey, @NonNull String nameKey, @NonNull String descriptionKey, @NonNull DataCallback<ArrayList<PrescriptionTemplateItem>> callback) {
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject rootObject = new JSONObject(response);
                JSONArray itemArray = rootObject.optJSONArray("items");

                if (itemArray == null) {
                    throw new Exception("Template list not found");
                }

                ArrayList<PrescriptionTemplateItem> templateItems = new ArrayList<>();

                for (int index = 0; index < itemArray.length(); index++) {
                    JSONObject itemObject = itemArray.optJSONObject(index);

                    if (itemObject == null) {
                        continue;
                    }

                    String templateId = getSafeString(itemObject, idKey);
                    String templateName = getSafeString(itemObject, nameKey);
                    String templateDescription = getSafeString(itemObject, descriptionKey);

                    if (templateId.isEmpty() || templateName.isEmpty()) {
                        continue;
                    }

                    templateItems.add(new PrescriptionTemplateItem(templateId, templateName, templateDescription));
                }

                callback.onSuccess(templateItems);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        }, error -> callback.onError(getPrescriptionLoadErrorMessage(error)));

        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    private void getTemplateData(@NonNull String url, @NonNull String defaultErrorMessage, @NonNull DataCallback<JSONObject> callback) {
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject rootObject = new JSONObject(response);
                int status = rootObject.optInt("status", 0);

                if (status != 200) {
                    String message = getRawSafeString(rootObject, "message");
                    throw new Exception(message.isEmpty() ? defaultErrorMessage : message);
                }

                JSONObject dataObject = rootObject.optJSONObject("data");

                if (dataObject == null) {
                    throw new Exception(defaultErrorMessage);
                }

                callback.onSuccess(dataObject);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        }, error -> callback.onError(getPrescriptionLoadErrorMessage(error)));

        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    // Main Data Load API
    public void getComplainOptions(@NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String complainUrl = BASE_URL + "getComplain?p_doc_id="+ doctorId;

        StringRequest complainRequest = new StringRequest(Request.Method.GET, complainUrl, response -> {

            try {
                ArrayList<ClinicalOption> options = parseComplainResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        complainRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(complainRequest);
    }

    public void getInjuryOptions(@NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String injuryUrl = BASE_URL + "getInjury?p_doc_id=" + doctorId;

        StringRequest injuryRequest = new StringRequest(Request.Method.GET, injuryUrl, response -> {

            try {
                ArrayList<ClinicalOption> options = parseInjuryResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        injuryRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(injuryRequest);
    }

    public void getDiagnosisOptions(@NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String diagnosisUrl = BASE_URL + "getDiagnosis?p_doc_id=" + doctorId;

        StringRequest diagnosisRequest = new StringRequest(Request.Method.GET, diagnosisUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseDiagnosisResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        diagnosisRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(diagnosisRequest);
    }

    public void getClinicalFindingOptions(@NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String clinicalFindingUrl = BASE_URL + "getClinicalFindings?p_doc_id=" + doctorId;

        StringRequest clinicalFindingRequest = new StringRequest(Request.Method.GET, clinicalFindingUrl, response -> {

            try {
                ArrayList<ClinicalOption> options = parseClinicalFindingResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        clinicalFindingRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(clinicalFindingRequest);
    }

    public void getDrugHistoryOptions( @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String drugHistoryUrl = BASE_URL + "getDrugHistory";

        StringRequest drugHistoryRequest = new StringRequest(Request.Method.GET, drugHistoryUrl, response -> {

            try {
                ArrayList<ClinicalOption> options = parseDrugHistoryResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        drugHistoryRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(drugHistoryRequest);
    }

    public void getTestInvestigationOptions( @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String testInvestigationUrl = BASE_URL + "getTestInvestigation";

        StringRequest testInvestigationRequest = new StringRequest(Request.Method.GET, testInvestigationUrl, response -> {

            try {
                ArrayList<ClinicalOption> options = parseTestInvestigationResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        testInvestigationRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(testInvestigationRequest);
    }

    public void getObjectiveOptions(@NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String objectiveUrl = BASE_URL + "getObjective?p_doc_id=" + doctorId;

        StringRequest objectiveRequest = new StringRequest(Request.Method.GET, objectiveUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseObjectiveResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        objectiveRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(objectiveRequest);
    }

    public void getMuscleJointOptions(@NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String muscleJointUrl = BASE_URL + "getMuscleJoint";

        StringRequest muscleJointRequest = new StringRequest(Request.Method.GET, muscleJointUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseMuscleJointResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        muscleJointRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(muscleJointRequest);
    }

    public void getObjectiveValueOptions(@NonNull String objectiveId, @NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String objectiveValueUrl = BASE_URL + "getObjectiveValue?p_om_id=" + objectiveId + "&p_doc_id=" + doctorId;

        StringRequest objectiveValueRequest = new StringRequest(Request.Method.GET, objectiveValueUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseObjectiveValueResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        objectiveValueRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(objectiveValueRequest);
    }

    public void getAdviceOptions(@NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String adviceUrl = BASE_URL + "getAdvice?p_doc_id=" + doctorId;

        StringRequest adviceRequest = new StringRequest(Request.Method.GET, adviceUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseAdviceResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        adviceRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(adviceRequest);
    }

    public void getFollowUpOptions(@NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String followUpUrl = BASE_URL + "getFollowUp?p_doc_id=" + doctorId;

        StringRequest followUpRequest = new StringRequest(Request.Method.GET, followUpUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseFollowUpResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        followUpRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(followUpRequest);
    }

    public void getSpecialAlertOptions(@NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String specialAlertUrl = BASE_URL + "getSpecialAlert?p_doc_id=" + doctorId;

        StringRequest specialAlertRequest = new StringRequest(Request.Method.GET, specialAlertUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseSpecialAlertResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        specialAlertRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(specialAlertRequest);
    }

    public void getMedicineOptions(@NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String medicineUrl = BASE_URL + "getMedicine";

        StringRequest medicineRequest = new StringRequest(Request.Method.GET, medicineUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseMedicineResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        medicineRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(medicineRequest);
    }

    public void getDoseOptions(@NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String doseUrl = BASE_URL + "getDoses";

        StringRequest doseRequest = new StringRequest(Request.Method.GET, doseUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseDoseResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        doseRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(doseRequest);
    }

    public void getMedicineTimingOptions(@NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String timingUrl = BASE_URL + "getMedicineTiming";

        StringRequest timingRequest = new StringRequest(Request.Method.GET, timingUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseMedicineTimingResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        timingRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(timingRequest);
    }

    public void getMedicineRouteOptions(@NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String routeUrl = BASE_URL + "getMedicineRoute";

        StringRequest routeRequest = new StringRequest(Request.Method.GET, routeUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseMedicineRouteResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        routeRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(routeRequest);
    }

    public void getTherapyOptions(@NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String therapyUrl = BASE_URL + "getTherapy?p_doc_id=" + doctorId;

        StringRequest therapyRequest = new StringRequest(Request.Method.GET, therapyUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseTherapyResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        }, error -> callback.onError(getVolleyErrorMessage(error)));

        therapyRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(therapyRequest);
    }

    public void getTreatmentPlanOptions(@NonNull String doctorId, @NonNull DataCallback<ArrayList<ClinicalOption>> callback) {
        String treatmentPlanUrl = BASE_URL + "getTreatmentPlan?p_doc_id=" + doctorId;

        StringRequest treatmentPlanRequest = new StringRequest(Request.Method.GET, treatmentPlanUrl, response -> {
            try {
                ArrayList<ClinicalOption> options = parseTreatmentPlanResponse(response);
                callback.onSuccess(options);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        },error -> callback.onError(getVolleyErrorMessage(error)));

        treatmentPlanRequest.setRetryPolicy(new DefaultRetryPolicy(REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(treatmentPlanRequest);
    }

    // Main Data Parsing
    @NonNull
    private ArrayList<ClinicalOption> parseComplainResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String complainId = getSafeString(itemObject, "cm_id");
            String complainName = getSafeString(itemObject, "cm_name");
            String doctorInfo = getSafeString(itemObject, "cm_notes");

            /*
             * Name না থাকলে dropdown-এ invalid
             * option দেখানো হবে না।
             */
            if (complainName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(complainId, complainName, doctorInfo));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseInjuryResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String injuryId = getSafeString(itemObject, "injury_id");
            String injuryName = getSafeString(itemObject, "injury_name");
            String injuryNotes = getSafeString(itemObject, "injury_notes");

            /*
             * Name ছাড়া invalid item
             * dropdown-এ দেখানো হবে না।
             */
            if (injuryName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(injuryId, injuryName, injuryNotes));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseDiagnosisResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String diagnosisId = getSafeString(itemObject, "dm_id");
            String diagnosisName = getSafeString(itemObject, "dm_name");
            String diagnosisNotes = getSafeString(itemObject, "dm_notes");

            if (diagnosisName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(diagnosisId, diagnosisName, diagnosisNotes));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseDrugHistoryResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String medicineId = getSafeString(itemObject, "medicine_id");
            String medicineName = getSafeString(itemObject, "medicine_name");

            if (medicineName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(medicineId, medicineName, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseClinicalFindingResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String clinicalFindingId = getSafeString(itemObject, "cfm_id");
            String clinicalFindingName = getSafeString(itemObject, "cfm_name");
            String clfNotes = getSafeString(itemObject, "cfm_notes");

            if (clinicalFindingName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(clinicalFindingId, clinicalFindingName, clfNotes));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseTestInvestigationResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String investigationId = getSafeString(itemObject, "pfn_id");
            String investigationName = getSafeString(itemObject, "pfn_fee_name");

            if (investigationName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(investigationId, investigationName, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseObjectiveResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String objectiveId = getSafeString(itemObject, "om_id");
            String objectiveName = getSafeString(itemObject, "om_name");

            if (objectiveName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(objectiveId, objectiveName, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseObjectiveValueResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String objectiveValueId = getSafeString(itemObject, "od_id");
            String objectiveValueName = getSafeString(itemObject, "od_name");

            if (objectiveValueName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(objectiveValueId, objectiveValueName, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseMuscleJointResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String muscleJointId = getSafeString(itemObject, "mjl_id");
            String muscleJointName = getSafeString(itemObject, "mjl_name");

            if (muscleJointName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(muscleJointId, muscleJointName, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseAdviceResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String adviceId = getSafeString(itemObject, "dai_id");
            String adviceText = getSafeString(itemObject, "dai_advice");

            if (adviceText.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(adviceId, adviceText, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseFollowUpResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String followUpId = getSafeString(itemObject, "dfi_id");
            String followUpText = getSafeString(itemObject, "dfi_followup_details");

            if (followUpText.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(followUpId, followUpText, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseSpecialAlertResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String specialAlertId = getSafeString(itemObject, "dsai_id");
            String specialAlertText = getSafeString(itemObject, "dsai_special_alert_details");

            if (specialAlertText.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(specialAlertId, specialAlertText, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseMedicineResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String medicineId = getSafeString(itemObject, "medicine_id");
            String medicineName = getSafeString(itemObject, "medicine_name");

            if (medicineName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(medicineId, medicineName, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseDoseResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String doseId = getSafeString(itemObject, "dose_id");
            String doseName = getSafeString(itemObject, "dose_name");

            if (doseName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(doseId, doseName, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseMedicineTimingResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String timingId = getSafeString(itemObject, "timing_id");
            String timingName = getSafeString(itemObject, "timing_name");

            if (timingName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(timingId, timingName, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseMedicineRouteResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String routeId = getSafeString(itemObject, "route_id");
            String routeName = getSafeString(itemObject, "route_name");

            if (routeName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(routeId, routeName, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseTherapyResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String therapyId = getSafeString(itemObject, "tm_id");
            String therapyName = getSafeString(itemObject, "tm_name");

            if (therapyName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(therapyId, therapyName, ""));
        }

        return options;
    }

    @NonNull
    private ArrayList<ClinicalOption> parseTreatmentPlanResponse(@NonNull String response) throws Exception {
        ArrayList<ClinicalOption> options = new ArrayList<>();
        JSONObject rootObject = new JSONObject(response);
        JSONArray itemsArray = rootObject.optJSONArray("items");

        if (itemsArray == null) {
            return options;
        }

        for (int index = 0; index < itemsArray.length(); index++) {
            JSONObject itemObject = itemsArray.optJSONObject(index);
            if (itemObject == null) {
                continue;
            }

            String treatmentPlanId = getSafeString(itemObject, "tpm_id");
            String treatmentPlanName = getSafeString(itemObject, "tpm_treatment_plan_name");

            if (treatmentPlanName.isEmpty()) {
                continue;
            }

            options.add(new ClinicalOption(treatmentPlanId, treatmentPlanName, ""));
        }

        return options;
    }

    // Prescription History List API
    public void getPrescriptionHistory(@NonNull String phId, @NonNull String patientId, @NonNull String docId, @NonNull DataCallback<ArrayList<PrescriptionHistoryItem>> callback) {
        String url = BASE_URL + "getPrescriptionHistory?p_ph_id=" + phId + "&p_pat_id=" + patientId + "&p_doc_id=" + docId;

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject rootObject = new JSONObject(response);
                JSONArray itemArray = rootObject.optJSONArray("items");

                if (itemArray == null) {
                    throw new Exception("Prescription history data not found");
                }

                ArrayList<PrescriptionHistoryItem> historyItems = new ArrayList<>();

                for (int index = 0; index < itemArray.length(); index++) {
                    JSONObject itemObject = itemArray.optJSONObject(index);

                    if (itemObject == null) {
                        continue;
                    }

                    String rvmId = getSafeString(itemObject, "rvm_id");

                    if (rvmId.isEmpty()) {
                        continue;
                    }

                    historyItems.add(new PrescriptionHistoryItem(
                            rvmId,
                            getSafeString(itemObject, "rvm_code"),
                            getSafeString(itemObject, "presc_date"),
                            getSafeString(itemObject, "app_date"),
                            getSafeString(itemObject, "sch_time"),
                            getSafeString(itemObject, "depts_name"),
                            getSafeString(itemObject, "pfn_fee_name"),
                            getSafeString(itemObject, "rvm_ad_id"),
                            getSafeString(itemObject, "rvm_doc_id"),
                            getSafeString(itemObject, "doc_name"),
                            getSafeString(itemObject, "desig_name"),
                            getSafeString(itemObject, "last_updated_time"),
                            getSafeString(itemObject, "presc_type"),
                            getSafeString(itemObject, "pat_type")
                    ));
                }

                callback.onSuccess(historyItems);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        }, error -> callback.onError(getPrescriptionLoadErrorMessage(error)));

        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    // Previous Prescription Details API
    public void getPreviousPrescription(@NonNull String rvmId, @NonNull DataCallback<JSONObject> callback) {
        String url = BASE_URL + "loadPreviousPrescription?p_rvm_id=" + rvmId;

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject rootObject = new JSONObject(response);
                int status = rootObject.optInt("status", 0);

                if (status != 200) {
                    String message = getRawSafeString(rootObject, "message");

                    throw new Exception(
                            message.isEmpty()
                                    ? "Previous prescription could not be loaded"
                                    : message
                    );
                }

                JSONObject dataObject = rootObject.optJSONObject("data");

                if (dataObject == null) {
                    throw new Exception("Previous prescription data not found");
                }

                callback.onSuccess(dataObject);

            } catch (Exception exception) {
                callback.onError(getErrorMessage(exception));
            }
        }, error -> callback.onError(getPrescriptionLoadErrorMessage(error)));

        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    // HELPER
    @NonNull
    private String getSafeString(@NonNull JSONObject object, @NonNull String key) {
        if (!object.has(key) || object.isNull(key)) {
            return "";
        }

        String value = object.optString(key, "");

        if ("null".equalsIgnoreCase(value)) {
            return "";
        }

        byte[] bytes = value.trim().getBytes(ISO_8859_1);
        return new String(bytes, UTF_8);
    }

    @NonNull
    private String getVolleyErrorMessage(@NonNull VolleyError error) {
        if (error.networkResponse != null) {
            return "Server error: " + error.networkResponse.statusCode;
        }

        if (error.getLocalizedMessage() != null && !error.getLocalizedMessage().isEmpty()) {
            return error.getLocalizedMessage();
        }

        return "Unable to connect to the server";
    }

    @NonNull
    private String getErrorMessage(@NonNull Exception exception) {
        String message = exception.getLocalizedMessage();

        if (message == null || message.isEmpty()) {
            return "Unable to parse server data";
        }

        return message;
    }

    @NonNull
    private String getRawSafeString(@NonNull JSONObject object, @NonNull String key) {
        if (!object.has(key) || object.isNull(key)) {
            return "";
        }

        String value = object.optString(key, "");
        return "null".equalsIgnoreCase(value) ? "" : value.trim();
    }

    @NonNull
    private String getPrescriptionLoadErrorMessage(@NonNull VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String response = new String(error.networkResponse.data, UTF_8);
                JSONObject errorObject = new JSONObject(response);
                String message = getRawSafeString(errorObject, "message");

                if (!message.isEmpty()) {
                    return message;
                }
            } catch (Exception ignored) {
            }

            return "Server error: " + error.networkResponse.statusCode;
        }

        return getVolleyErrorMessage(error);
    }

    public interface DataCallback<T> {
        void onSuccess(@NonNull T data);
        void onError(@NonNull String errorMessage);
    }
}