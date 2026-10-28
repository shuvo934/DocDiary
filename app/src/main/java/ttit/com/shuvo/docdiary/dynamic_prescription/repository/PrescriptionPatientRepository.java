package ttit.com.shuvo.docdiary.dynamic_prescription.repository;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPatientDetails;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPatientItem;

public class PrescriptionPatientRepository {

    private static final String BASE_URL = pre_url_api + "dynamic_presc/";

    private static final String RECENT_PATIENT_REQUEST_TAG = "recent_prescription_patients";
    private static final String SEARCH_PATIENT_REQUEST_TAG = "search_prescription_patients";
    private static final String PATIENT_DETAILS_REQUEST_TAG = "prescription_patient_details";

    private static final int REQUEST_TIMEOUT_MS = 15000;
    private static final int MAX_REQUEST_RETRIES = 0;

    private static volatile PrescriptionPatientRepository instance;

    private final RequestQueue requestQueue;

    private PrescriptionPatientRepository(@NonNull Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static PrescriptionPatientRepository getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (PrescriptionPatientRepository.class) {
                if (instance == null) {
                    instance = new PrescriptionPatientRepository(context);
                }
            }
        }

        return instance;
    }

    public void getRecentPatients(@NonNull String year, @NonNull DataCallback<ArrayList<PrescriptionPatientItem>> callback) {
        String validYear = year.trim();

        if (validYear.isEmpty()) {
            callback.onError("Patient registration year is required");
            return;
        }

        requestQueue.cancelAll(RECENT_PATIENT_REQUEST_TAG);

        String requestUrl = BASE_URL
                + "getHundredPatListYearWise?p_year="
                + validYear;

        StringRequest request = new StringRequest(Request.Method.GET, requestUrl, response -> {
            try {
                callback.onSuccess(parsePatientList(response));
            } catch (Exception exception) {
                callback.onError(getExceptionMessage(exception));
            }
        }, error -> callback.onError(getVolleyErrorMessage(error)));

        configureRequest(request, RECENT_PATIENT_REQUEST_TAG);
        requestQueue.add(request);
    }

    public void searchPatients(@NonNull String searchText, @NonNull DataCallback<ArrayList<PrescriptionPatientItem>> callback) {
        String validSearchText = searchText.trim();

        requestQueue.cancelAll(SEARCH_PATIENT_REQUEST_TAG);

        if (validSearchText.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        String requestUrl = BASE_URL
                + "getPatListBySearch?p_search="
                + Uri.encode(validSearchText);

        StringRequest request = new StringRequest(Request.Method.GET, requestUrl, response -> {
            try {
                callback.onSuccess(parsePatientList(response));
            } catch (Exception exception) {
                callback.onError(getExceptionMessage(exception));
            }
        }, error -> callback.onError(getVolleyErrorMessage(error)));

        configureRequest(request, SEARCH_PATIENT_REQUEST_TAG);
        requestQueue.add(request);
    }

    public void getPatientDetails(@NonNull String patientHistoryId, @NonNull DataCallback<PrescriptionPatientDetails> callback) {
        String validPatientHistoryId = patientHistoryId.trim();

        if (validPatientHistoryId.isEmpty()) {
            callback.onError("Patient history information not found");
            return;
        }

        requestQueue.cancelAll(PATIENT_DETAILS_REQUEST_TAG);

        String requestUrl = BASE_URL
                + "getPatDetails?p_ph_id="
                + validPatientHistoryId;

        StringRequest request = new StringRequest(Request.Method.GET, requestUrl, response -> {
            try {
                callback.onSuccess(parsePatientDetails(response));
            } catch (Exception exception) {
                callback.onError(getExceptionMessage(exception));
            }
        }, error -> callback.onError(getVolleyErrorMessage(error)));

        configureRequest(request, PATIENT_DETAILS_REQUEST_TAG);
        requestQueue.add(request);
    }

    public void cancelSearchRequest() {
        requestQueue.cancelAll(SEARCH_PATIENT_REQUEST_TAG);
    }

    public void cancelPatientDetailsRequest() {
        requestQueue.cancelAll(PATIENT_DETAILS_REQUEST_TAG);
    }

    public void cancelAllRequests() {
        requestQueue.cancelAll(RECENT_PATIENT_REQUEST_TAG);
        requestQueue.cancelAll(SEARCH_PATIENT_REQUEST_TAG);
        requestQueue.cancelAll(PATIENT_DETAILS_REQUEST_TAG);
    }

    private void configureRequest(@NonNull StringRequest request, @NonNull String requestTag) {
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT_MS,
                MAX_REQUEST_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        request.setTag(requestTag);
    }

    @NonNull
    private ArrayList<PrescriptionPatientItem> parsePatientList(@NonNull String response) throws Exception {
        ArrayList<PrescriptionPatientItem> patientItems = new ArrayList<>();

        JSONObject rootObject = new JSONObject(response);
        JSONArray patientArray = rootObject.optJSONArray("pat_list");

        if (patientArray == null) {
            return patientItems;
        }

        for (int index = 0; index < patientArray.length(); index++) {
            JSONObject patientObject = patientArray.optJSONObject(index);

            if (patientObject == null) {
                continue;
            }

            String patientId = getSafeString(patientObject, "pat_id");
            String patientHistoryId = getSafeString(patientObject, "ph_id");
            String patientName = getSafeString(patientObject, "pat_name");

            if (patientId.isEmpty() || patientHistoryId.isEmpty() || patientName.isEmpty()) {
                continue;
            }

            patientItems.add(new PrescriptionPatientItem(
                    patientId,
                    patientName,
                    patientHistoryId,
                    getSafeString(patientObject, "sub_code"),
                    getSafeString(patientObject, "dd_thana_name"),
                    getSafeString(patientObject, "pyear"),
                    getSafeString(patientObject, "pat_cell"),
                    getSafeString(patientObject, "gender"),
                    getSafeString(patientObject, "pat_age")
            ));
        }

        return patientItems;
    }

    @NonNull
    private PrescriptionPatientDetails parsePatientDetails(@NonNull String response) throws Exception {
        JSONObject rootObject = new JSONObject(response);
        JSONArray detailsArray = rootObject.optJSONArray("items");

        if (detailsArray == null || detailsArray.length() == 0) {
            throw new Exception("Patient details not found");
        }

        JSONObject detailsObject = detailsArray.optJSONObject(0);

        if (detailsObject == null) {
            throw new Exception("Patient details not found");
        }

        return new PrescriptionPatientDetails(
                getSafeString(detailsObject, "pat_name"),
                getSafeString(detailsObject, "ph_sub_code"),
                getSafeString(detailsObject, "marital_status"),
                getSafeString(detailsObject, "gender"),
                getSafeString(detailsObject, "pat_blood"),
                getSafeString(detailsObject, "pat_category_name"),
                getSafeString(detailsObject, "address"),
                getSafeString(detailsObject, "pat_cell"),
                getSafeString(detailsObject, "pat_phone"),
                getSafeString(detailsObject, "pat_age"),
                getSafeString(detailsObject, "reg_year"),
                getSafeString(detailsObject, "pat_fathers_name"),
                getSafeString(detailsObject, "pat_mothers_name")
        );
    }

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
            if (error.networkResponse.data != null) {
                try {
                    String response = new String(
                            error.networkResponse.data,
                            StandardCharsets.UTF_8
                    );

                    JSONObject errorObject = new JSONObject(response);

                    String message = getSafeString(
                            errorObject,
                            "message"
                    );

                    if (!message.isEmpty()) {
                        return message;
                    }
                } catch (Exception ignored) {
                }
            }

            return "Server error: "
                    + error.networkResponse.statusCode;
        }

        if (error.getLocalizedMessage() != null
                && !error.getLocalizedMessage().trim().isEmpty()) {
            return error.getLocalizedMessage();
        }

        return "Unable to connect to the server";
    }

    @NonNull
    private String getExceptionMessage(@NonNull Exception exception) {
        String message = exception.getLocalizedMessage();

        if (message == null || message.trim().isEmpty()) {
            return "Unable to parse patient data";
        }

        return message;
    }

    public interface DataCallback<T> {
        void onSuccess(@NonNull T data);
        void onError(@NonNull String errorMessage);
    }
}