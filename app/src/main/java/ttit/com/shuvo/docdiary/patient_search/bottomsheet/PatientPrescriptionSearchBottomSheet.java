package ttit.com.shuvo.docdiary.patient_search.bottomsheet;

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
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionHistoryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;
import ttit.com.shuvo.docdiary.patient_search.adapters.PatientSearchPresHistAdapter;

public class PatientPrescriptionSearchBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PatientPrescriptionSearchBottomSheet";

    public static final String RESULT_REQUEST_KEY = "prescription_history_result";
    public static final String KEY_PREVIEW_REQUESTED = "preview_requested";
    public static final String KEY_SELECTED_RVM_ID = "selected_rvm_id";

    private static final String ARG_PH_ID = "arg_ph_id";
    private static final String ARG_PATIENT_ID = "arg_patient_id";
    private static final String ARG_DOC_ID = "arg_doc_id";

    private String phId = "";
    private String patientId = "";
    private String docId = "";

    private PrescriptionBuilderViewModel viewModel;
    private PatientSearchPresHistAdapter historyAdapter;

    private RecyclerView historyRecyclerView;
    private CircularProgressIndicator progressIndicator;

    private LinearLayout emptyLayout;
    private LinearLayout errorLayout;

    private TextView historyCountView;
    private TextView errorView;

    private AppCompatImageButton closeButton;
    private MaterialButton retryButton;
    private MaterialButton previewButton;

    @Nullable
    private PrescriptionHistoryItem selectedHistory;

    private boolean historyLoading = false;
    private String historyError = "";

    @NonNull
    public static PatientPrescriptionSearchBottomSheet newInstance(@NonNull String phId, @NonNull String patientId, @NonNull String docId) {
        PatientPrescriptionSearchBottomSheet bottomSheet = new PatientPrescriptionSearchBottomSheet();

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
        return inflater.inflate(R.layout.bottom_sheet_search_patient_pres_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        viewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);

        configureHistoryList();
        configureActions();


        selectedHistory = null;
        historyAdapter.clearSelection();

        observeHistoryState();
        updateActionButtons();

        System.out.println(phId + patientId + docId);
        viewModel.loadPrescriptionHistory(phId, patientId, docId);
    }

    private void initializeViews(@NonNull View view) {
        historyRecyclerView = view.findViewById(R.id.rv_pat_search_pres_history);
        progressIndicator = view.findViewById(R.id.progress_pat_search_pres_history);

        emptyLayout = view.findViewById(R.id.layout_pat_search_pres_history_empty);
        errorLayout = view.findViewById(R.id.layout_pat_search_pres_history_error);

        historyCountView = view.findViewById(R.id.tv_pat_search_pres_history_count);
        errorView = view.findViewById(R.id.tv_pat_search_pres_history_error);

        closeButton = view.findViewById(R.id.btn_close_pat_search_pres_history_sheet);
        retryButton = view.findViewById(R.id.btn_retry_pat_search_pres_history);
        previewButton = view.findViewById(R.id.btn_preview_pat_search_pres_history);
    }

    private void configureHistoryList() {
        historyAdapter = new PatientSearchPresHistAdapter(item -> {
            selectedHistory = item;
            updateActionButtons();
        });

        historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyRecyclerView.setAdapter(historyAdapter);
    }

    private void configureActions() {
        closeButton.setOnClickListener(view -> dismiss());

        retryButton.setOnClickListener(view -> viewModel.retryPrescriptionHistory(phId, patientId, docId));

        previewButton.setOnClickListener(view -> sendPreviewRequest());
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

    private void sendPreviewRequest() {
        if (selectedHistory == null || historyLoading) {
            return;
        }

        Bundle result = new Bundle();
        result.putBoolean(KEY_PREVIEW_REQUESTED, true);
        result.putString(KEY_SELECTED_RVM_ID, selectedHistory.getRvmId());
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
        progressIndicator.setVisibility(historyLoading ? View.VISIBLE : View.GONE);
    }

    private void updateActionButtons() {
        boolean enabled = selectedHistory != null
                && !historyLoading;

        previewButton.setEnabled(enabled);
        previewButton.setAlpha(enabled ? 1.0f : 0.45f);
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
        behavior.setHideable(false);
    }

    @Override
    public void onDestroyView() {
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

        viewModel = null;

        super.onDestroyView();
    }
}
