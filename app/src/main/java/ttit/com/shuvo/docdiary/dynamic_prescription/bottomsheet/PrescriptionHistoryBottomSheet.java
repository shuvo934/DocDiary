package ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionHistoryAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionHistoryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionHistoryBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionHistoryBottomSheet";

    public static final String RESULT_REQUEST_KEY = "prescription_history_result";
    public static final String KEY_HISTORY_LOADED = "history_loaded";
    public static final String KEY_PREVIEW_REQUESTED = "preview_requested";
    public static final String KEY_SELECTED_RVM_ID = "selected_rvm_id";
    public static final String KEY_SELECTED_RVM_CODE = "selected_rvm_code";

    private static final String ARG_PH_ID = "arg_ph_id";
    private static final String ARG_PATIENT_ID = "arg_patient_id";
    private static final String ARG_DOC_ID = "arg_doc_id";

    private String phId = "";
    private String patientId = "";
    private String docId = "";

    private PrescriptionBuilderViewModel viewModel;
    private PrescriptionHistoryAdapter historyAdapter;

    private RecyclerView historyRecyclerView;
    private CircularProgressIndicator progressIndicator;

    private LinearLayout emptyLayout;
    private LinearLayout errorLayout;

    private TextView historyCountView;
    private TextView errorView;

    private AppCompatImageButton closeButton;
    private MaterialButton retryButton;
    private MaterialButton previewButton;
    private MaterialButton loadButton;

    @Nullable
    private PrescriptionHistoryItem selectedHistory;

    private boolean historyLoading = false;
    private boolean previousPrescriptionLoading = false;
    private String historyError = "";

    @NonNull
    public static PrescriptionHistoryBottomSheet newInstance(@NonNull String phId, @NonNull String patientId, @NonNull String docId) {
        PrescriptionHistoryBottomSheet bottomSheet = new PrescriptionHistoryBottomSheet();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_PH_ID, phId);
        arguments.putString(ARG_PATIENT_ID, patientId);
        arguments.putString(ARG_DOC_ID, docId);

        bottomSheet.setArguments(arguments);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        if (arguments != null) {
            phId = cleanValue(arguments.getString(ARG_PH_ID));
            patientId = cleanValue(arguments.getString(ARG_PATIENT_ID));
            docId = cleanValue(arguments.getString(ARG_DOC_ID));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_prescription_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        viewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);

        configureHistoryList();
        configureActions();

        /*
         * BottomSheet প্রতিবার নতুন selection দিয়ে খুলবে।
         */
        selectedHistory = null;
        historyAdapter.clearSelection();

        viewModel.clearPreviousPrescriptionLoadResult();

        observeHistoryState();
        observePreviousPrescriptionLoad();

        updateActionButtons();

        /*
         * প্রতিবার History button click করলে fresh API call হবে।
         */
        viewModel.loadPrescriptionHistory(phId, patientId, docId);
    }

    private void initializeViews(@NonNull View view) {
        historyRecyclerView = view.findViewById(R.id.rv_prescription_history);
        progressIndicator = view.findViewById(R.id.progress_prescription_history);

        emptyLayout = view.findViewById(R.id.layout_history_empty);
        errorLayout = view.findViewById(R.id.layout_history_error);

        historyCountView = view.findViewById(R.id.tv_history_count);
        errorView = view.findViewById(R.id.tv_history_error);

        closeButton = view.findViewById(R.id.btn_close_history_sheet);
        retryButton = view.findViewById(R.id.btn_retry_history);
        previewButton = view.findViewById(R.id.btn_preview_history);
        loadButton = view.findViewById(R.id.btn_load_history);
    }

    private void configureHistoryList() {
        historyAdapter = new PrescriptionHistoryAdapter(item -> {
            selectedHistory = item;
            updateActionButtons();
        });

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyRecyclerView.setAdapter(historyAdapter);
    }

    private void configureActions() {
        closeButton.setOnClickListener(view -> {
            if (!previousPrescriptionLoading) {
                dismiss();
            }
        });

        retryButton.setOnClickListener(view -> viewModel.retryPrescriptionHistory(phId, patientId, docId));

        previewButton.setOnClickListener(view -> sendPreviewRequest());

        loadButton.setOnClickListener(view -> confirmAndLoadSelectedHistory());
    }

    private void observeHistoryState() {
        viewModel.getPrescriptionHistoryItems().observe(getViewLifecycleOwner(), items -> {
            ArrayList<PrescriptionHistoryItem> currentItems =
                    items == null ? new ArrayList<>() : items;

            historyAdapter.submitList(currentItems);
            updateHistoryCount(currentItems.size());
            updateContentState(currentItems);
        });

        viewModel.getPrescriptionHistoryLoading().observe(getViewLifecycleOwner(), loading -> {
            historyLoading = Boolean.TRUE.equals(loading);

            updateProgressState();
            updateContentState(viewModel.getCurrentPrescriptionHistoryItems());
            updateActionButtons();
        });

        viewModel.getPrescriptionHistoryError().observe(getViewLifecycleOwner(), errorMessage -> {
            historyError = cleanValue(errorMessage);

            updateContentState(viewModel.getCurrentPrescriptionHistoryItems());
        });
    }

    private void observePreviousPrescriptionLoad() {
        viewModel.getPreviousPrescriptionLoading().observe(getViewLifecycleOwner(), loading -> {
            previousPrescriptionLoading = Boolean.TRUE.equals(loading);

            setCancelable(!previousPrescriptionLoading);

            updateProgressState();
            updateActionButtons();

            historyRecyclerView.setAlpha(previousPrescriptionLoading ? 0.45f : 1.0f);

            closeButton.setEnabled(!previousPrescriptionLoading);
            closeButton.setAlpha(previousPrescriptionLoading ? 0.45f : 1.0f);
        });

        viewModel.getPreviousPrescriptionLoadSuccess().observe(getViewLifecycleOwner(), successful -> {
            if (!Boolean.TRUE.equals(successful)) {
                return;
            }

            Bundle result = new Bundle();
            result.putBoolean(KEY_HISTORY_LOADED, true);

            if (selectedHistory != null) {
                result.putString(KEY_SELECTED_RVM_ID, selectedHistory.getRvmId());
                result.putString(KEY_SELECTED_RVM_CODE, selectedHistory.getRvmCode());
            }

            getParentFragmentManager().setFragmentResult(RESULT_REQUEST_KEY, result);

            viewModel.clearPreviousPrescriptionLoadResult();
            dismiss();
        });

        viewModel.getPreviousPrescriptionError().observe(getViewLifecycleOwner(), errorMessage -> {
            String validErrorMessage = cleanValue(errorMessage);

            if (validErrorMessage.isEmpty()) {
                return;
            }

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Load Failed")
                    .setMessage(validErrorMessage)
                    .setPositiveButton("OK", null)
                    .show();

            viewModel.clearPreviousPrescriptionLoadResult();
        });
    }

    private void confirmAndLoadSelectedHistory() {
        if (selectedHistory == null || previousPrescriptionLoading) {
            return;
        }

        if (!viewModel.hasAnyPrescriptionData()) {
            loadSelectedHistory();
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Load previous prescription?")
                .setMessage(
                        "Loading this prescription will replace all current "
                                + "Clinical, Rx and Advice information.\n\n"
                                + "The current patient and prescription identity will remain unchanged."
                )
                .setPositiveButton("LOAD & REPLACE", (dialog, which) -> loadSelectedHistory())
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void loadSelectedHistory() {
        if (selectedHistory == null) {
            return;
        }

        viewModel.loadPreviousPrescription(selectedHistory.getRvmId());
    }

    private void sendPreviewRequest() {
        if (selectedHistory == null || previousPrescriptionLoading) {
            return;
        }

        Bundle result = new Bundle();
        result.putBoolean(KEY_PREVIEW_REQUESTED, true);
        result.putString(KEY_SELECTED_RVM_ID, selectedHistory.getRvmId());
        result.putString(KEY_SELECTED_RVM_CODE, selectedHistory.getRvmCode());

        getParentFragmentManager().setFragmentResult(RESULT_REQUEST_KEY, result);

        dismiss();
    }

    private void updateHistoryCount(int itemCount) {
        if (itemCount <= 0) {
            historyCountView.setVisibility(View.GONE);
            return;
        }

        String countText = itemCount == 1 ? "1 prescription found" : itemCount + " prescriptions found";

        historyCountView.setText(countText);
        historyCountView.setVisibility(View.VISIBLE);
    }

    private void updateContentState(@NonNull ArrayList<PrescriptionHistoryItem> items) {
        boolean hasError = !historyError.isEmpty();
        boolean empty = !historyLoading && !hasError && items.isEmpty();
        boolean showList = !historyLoading && !hasError && !items.isEmpty();

        historyRecyclerView.setVisibility(showList ? View.VISIBLE : View.GONE);
        emptyLayout.setVisibility(empty ? View.VISIBLE : View.GONE);
        errorLayout.setVisibility(hasError ? View.VISIBLE : View.GONE);

        errorView.setText(historyError);
    }

    private void updateProgressState() {
        boolean showProgress = historyLoading || previousPrescriptionLoading;

        progressIndicator.setVisibility(showProgress ? View.VISIBLE : View.GONE);
    }

    private void updateActionButtons() {
        boolean enabled = selectedHistory != null
                && !historyLoading
                && !previousPrescriptionLoading;

        previewButton.setEnabled(enabled);
        previewButton.setAlpha(enabled ? 1.0f : 0.45f);

        loadButton.setEnabled(enabled);
        loadButton.setAlpha(enabled ? 1.0f : 0.45f);
    }

    @NonNull
    private String cleanValue(@Nullable String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return "";
        }

        return value.trim();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (!(dialog instanceof BottomSheetDialog)) {
            return;
        }

        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        if (bottomSheet == null) {
            return;
        }

        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        bottomSheet.setLayoutParams(layoutParams);

        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
        behavior.setHideable(true);
    }

    @Override
    public void onDestroyView() {
        /*
         * History list এবং selection retain হবে না।
         * পরেরবার open করলে আবার API call হবে।
         */
        if (viewModel != null) {
            viewModel.clearPrescriptionHistoryState();
        }

        historyRecyclerView.setAdapter(null);

        historyAdapter = null;
        selectedHistory = null;

        historyRecyclerView = null;
        progressIndicator = null;

        emptyLayout = null;
        errorLayout = null;

        historyCountView = null;
        errorView = null;

        closeButton = null;
        retryButton = null;
        previewButton = null;
        loadButton = null;

        viewModel = null;

        super.onDestroyView();
    }
}