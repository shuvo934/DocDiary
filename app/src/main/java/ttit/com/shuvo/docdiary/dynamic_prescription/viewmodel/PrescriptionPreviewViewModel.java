package ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPreviewData;
import ttit.com.shuvo.docdiary.dynamic_prescription.repository.PrescriptionPreviewRepository;

public class PrescriptionPreviewViewModel extends AndroidViewModel {

    private final PrescriptionPreviewRepository previewRepository;

    private final MutableLiveData<PrescriptionPreviewData> previewData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> previewLoading = new MutableLiveData<>(false);

    private final MutableLiveData<String> previewError = new MutableLiveData<>("");

    @NonNull
    private String loadedRvmId = "";

    @NonNull
    private String activeRequestRvmId = "";

    private boolean requestRunning = false;

    public PrescriptionPreviewViewModel(@NonNull Application application) {
        super(application);
        previewRepository = new PrescriptionPreviewRepository(application);
    }

    public LiveData<PrescriptionPreviewData> getPreviewData() {
        return previewData;
    }

    public LiveData<Boolean> getPreviewLoading() {
        return previewLoading;
    }

    public LiveData<String> getPreviewError() {
        return previewError;
    }

    public void loadPreviewIfNeeded(@NonNull String rvmId) {
        String cleanRvmId = rvmId.trim();

        if (cleanRvmId.isEmpty()) {
            previewError.setValue("Prescription information not found");

            return;
        }

        /*
         * একই prescription ইতিমধ্যে successfully load হয়ে থাকলে
         * rotation-এর পরে আবার API call হবে না।
         */
        if (cleanRvmId.equals(loadedRvmId) && previewData.getValue() != null) {
            return;
        }

        /*
         * একই RVM_ID-এর request ইতিমধ্যে running থাকলে
         * duplicate request হবে না।
         */
        if (requestRunning && cleanRvmId.equals(activeRequestRvmId)) {
            return;
        }

        loadPreview(cleanRvmId);
    }

    public void reloadPreview(@NonNull String rvmId) {
        String cleanRvmId = rvmId.trim();

        if (cleanRvmId.isEmpty()) {
            previewError.setValue("Prescription information not found");

            return;
        }

        loadedRvmId = "";
        loadPreview(cleanRvmId);
    }

    private void loadPreview(@NonNull String rvmId) {
        requestRunning = true;
        activeRequestRvmId = rvmId;

        previewLoading.setValue(true);
        previewError.setValue("");

        /*
         * নতুন prescription load করার সময় আগের data
         * UI-তে থেকে যাবে না।
         */
        if (!rvmId.equals(loadedRvmId)) {
            previewData.setValue(null);
        }

        previewRepository.loadPrescriptionPreview(rvmId, new PrescriptionPreviewRepository.PreviewLoadCallback() {
            @Override
            public void onPreviewLoaded(@NonNull PrescriptionPreviewData loadedData) {
                if (!rvmId.equals(activeRequestRvmId)) {
                    return;
                }

                requestRunning = false;
                activeRequestRvmId = "";
                loadedRvmId = rvmId;

                previewLoading.setValue(false);
                previewError.setValue("");
                previewData.setValue(loadedData);
            }

            @Override
            public void onPreviewLoadFailed(@NonNull String errorMessage) {
                if (!rvmId.equals(activeRequestRvmId)) {
                    return;
                }

                requestRunning = false;
                activeRequestRvmId = "";
                loadedRvmId = "";

                previewLoading.setValue(false);
                previewError.setValue(errorMessage);
            }
        });
    }

    public void clearPreviewError() {
        previewError.setValue("");
    }

    @Override
    protected void onCleared() {
        previewRepository.cancelPreviewRequest();

        requestRunning = false;
        activeRequestRvmId = "";

        super.onCleared();
    }
}