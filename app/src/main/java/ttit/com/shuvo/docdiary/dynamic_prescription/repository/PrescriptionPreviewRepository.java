package ttit.com.shuvo.docdiary.dynamic_prescription.repository;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPreviewData;

public class PrescriptionPreviewRepository {

    private static final String PREVIEW_REQUEST_TAG = "prescription_preview_request";

    private final RequestQueue requestQueue;

    public PrescriptionPreviewRepository(@NonNull Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public interface PreviewLoadCallback {
        void onPreviewLoaded(@NonNull PrescriptionPreviewData previewData);
        void onPreviewLoadFailed(@NonNull String errorMessage);
    }

    public void loadPrescriptionPreview(@NonNull String rvmId, @NonNull PreviewLoadCallback callback) {
        String cleanRvmId = rvmId.trim();

        if (cleanRvmId.isEmpty()) {
            callback.onPreviewLoadFailed("Prescription information not found");
            return;
        }

        cancelPreviewRequest();

        String previewUrl = Uri.parse(pre_url_api + "dynamic_presc/getPreviewData").buildUpon().appendQueryParameter("p_rvm_id", cleanRvmId).build().toString();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                previewUrl,
                response -> parsePreviewResponse(
                        response,
                        callback
                ),
                error -> callback.onPreviewLoadFailed(
                        getVolleyErrorMessage(error)
                )
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        /*
         * Update-এর পর যেন পুরোনো cached prescription
         * preview না দেখায়।
         */
        request.setShouldCache(false);
        request.setTag(PREVIEW_REQUEST_TAG);

        requestQueue.add(request);
    }

    private void parsePreviewResponse(@NonNull String response, @NonNull PreviewLoadCallback callback) {
        try {
            JSONObject responseObject = new JSONObject(response);

            PrescriptionPreviewData previewData = new PrescriptionPreviewData(responseObject);
            callback.onPreviewLoaded(previewData);

        } catch (JSONException exception) {
            String errorMessage = exception.getMessage();
            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                errorMessage = "Prescription preview response could not be processed";
            }

            callback.onPreviewLoadFailed(errorMessage);
        }
    }

    @NonNull
    private String getVolleyErrorMessage(@NonNull VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String errorResponse = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorObject = new JSONObject(errorResponse);
                String serverMessage = getJsonString(errorObject, "message");

                if (serverMessage.isEmpty()) {
                    serverMessage = getJsonString(errorObject, "string_out");
                }

                if (!serverMessage.isEmpty()) {
                    return serverMessage;
                }

            } catch (JSONException ignored) {}
        }

        if (error.getLocalizedMessage() != null && !error.getLocalizedMessage().trim().isEmpty()) {
            return error.getLocalizedMessage();
        }

        return "Prescription preview failed to load. "
                + "Please check your network and try again.";
    }

    @NonNull
    private String getJsonString(@NonNull JSONObject jsonObject, @NonNull String key) {
        if (!jsonObject.has(key) || jsonObject.isNull(key)) {
            return "";
        }

        Object value = jsonObject.opt(key);

        if (value == null || value == JSONObject.NULL) {
            return "";
        }

        return String.valueOf(value).trim();
    }

    public void cancelPreviewRequest() {
        requestQueue.cancelAll(PREVIEW_REQUEST_TAG);
    }
}