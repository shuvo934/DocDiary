package ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionTemplateAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionTemplateItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionTemplateBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionTemplateBottomSheet";

    private PrescriptionBuilderViewModel viewModel;

    private AppCompatImageButton closeButton;

    private LinearLayout diseaseSection;
    private TextInputEditText diseaseSearchInput;
    private CircularProgressIndicator diseaseProgress;
    private TextView diseaseErrorView;
    private TextView diseaseEmptyView;
    private RecyclerView diseaseRecyclerView;
    private MaterialButton loadDiseaseButton;

    private LinearLayout treatmentSection;
    private TextView treatmentHelperView;
    private TextView loadedDiseaseTemplateNameView;
    private TextInputEditText treatmentSearchInput;
    private CircularProgressIndicator treatmentProgress;
    private TextView treatmentErrorView;
    private TextView treatmentEmptyView;
    private RecyclerView treatmentRecyclerView;
    private MaterialButton changeDiseaseButton;
    private MaterialButton doneWithoutTreatmentButton;
    private MaterialButton loadTreatmentButton;

    private PrescriptionTemplateAdapter diseaseAdapter;
    private PrescriptionTemplateAdapter treatmentAdapter;

    @Nullable
    private PrescriptionTemplateItem selectedDiseaseTemplate;

    @Nullable
    private PrescriptionTemplateItem selectedTreatmentTemplate;

    private boolean showingTreatmentStep = false;

    private boolean diseaseListLoading = false;
    private boolean diseaseApplyLoading = false;
    private boolean treatmentListLoading = false;
    private boolean treatmentApplyLoading = false;

    @NonNull
    private String diseaseListError = "";

    @NonNull
    private String diseaseApplyError = "";

    @NonNull
    private String treatmentListError = "";

    @NonNull
    private String treatmentApplyError = "";

    @NonNull
    private String pendingDiseaseTemplateId = "";

    @NonNull
    private String pendingTreatmentTemplateId = "";

    @Nullable
    private BottomSheetBehavior<FrameLayout> bottomSheetBehavior;

    @NonNull
    public static PrescriptionTemplateBottomSheet newInstance() {
        return new PrescriptionTemplateBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_prescription_template, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);

        showingTreatmentStep = isTreatmentSectionAvailable();

        initializeViews(view);
        configureAdapters();
        configureListeners();
        observeTemplateState();

        viewModel.loadDiseaseTemplatesIfNeeded();

        if (isTreatmentSectionAvailable()) {
            viewModel.loadTreatmentTemplatesForDisease(viewModel.getLoadedDiseaseTemplateId());
        }

        updateTemplateUi();
    }

    private void initializeViews(@NonNull View view) {
        closeButton = view.findViewById(R.id.btn_close_template_sheet);
        diseaseSection = view.findViewById(R.id.layout_disease_template_section);
        diseaseSearchInput = view.findViewById(R.id.et_search_disease_template);
        diseaseProgress = view.findViewById(R.id.progress_disease_template);
        diseaseErrorView = view.findViewById(R.id.tv_disease_template_error);
        diseaseEmptyView = view.findViewById(R.id.tv_disease_template_empty);
        diseaseRecyclerView = view.findViewById(R.id.rv_disease_templates);
        loadDiseaseButton = view.findViewById(R.id.btn_load_disease_template);
        treatmentSection = view.findViewById(R.id.layout_treatment_template_section);
        treatmentHelperView = view.findViewById(R.id.tv_treatment_template_helper);
        loadedDiseaseTemplateNameView = view.findViewById(R.id.tv_loaded_disease_template_name);
        treatmentSearchInput = view.findViewById(R.id.et_search_treatment_template);
        treatmentProgress = view.findViewById(R.id.progress_treatment_template);
        treatmentErrorView = view.findViewById(R.id.tv_treatment_template_error);
        treatmentEmptyView = view.findViewById(R.id.tv_treatment_template_empty);
        treatmentRecyclerView = view.findViewById(R.id.rv_treatment_templates);
        changeDiseaseButton = view.findViewById(R.id.btn_change_disease_template);
        doneWithoutTreatmentButton = view.findViewById(R.id.btn_done_without_treatment);
        loadTreatmentButton = view.findViewById(R.id.btn_load_treatment_template);
    }

    private void configureAdapters() {
        diseaseAdapter = new PrescriptionTemplateAdapter(templateItem -> {
            if (diseaseListLoading || isTemplateApplyBusy()) {
                return;
            }

            selectedDiseaseTemplate = templateItem;
            updateTemplateUi();
        });

        treatmentAdapter = new PrescriptionTemplateAdapter(templateItem -> {
            if (!isTreatmentSectionAvailable() || treatmentListLoading || isTemplateApplyBusy()) {
                return;
            }

            selectedTreatmentTemplate = templateItem;
            updateTemplateUi();
        });

        diseaseRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        diseaseRecyclerView.setAdapter(diseaseAdapter);
        treatmentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        treatmentRecyclerView.setAdapter(treatmentAdapter);
    }

    private void configureListeners() {
        closeButton.setOnClickListener(view -> {
            if (!isTemplateApplyBusy()) {
                dismiss();
            }
        });

        loadDiseaseButton.setOnClickListener(view -> requestDiseaseTemplateLoad());
        loadTreatmentButton.setOnClickListener(view -> requestTreatmentTemplateLoad());
        changeDiseaseButton.setOnClickListener(view -> {
            if (!isTemplateApplyBusy()) {
                showDiseaseStep();
            }
        });
        doneWithoutTreatmentButton.setOnClickListener(view -> {
            if (!isTemplateApplyBusy()) {
                dismiss();
            }
        });

        diseaseSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                diseaseAdapter.filter(text == null ? "" : text.toString());
                updateTemplateUi();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        diseaseSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    diseaseSearchInput.clearFocus();
                    hideKeyboard(diseaseSearchInput);

                    return false; // consume.
                }
            }
            return true;
        });

        treatmentSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                treatmentAdapter.filter(text == null ? "" : text.toString());
                updateTemplateUi();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        treatmentSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    treatmentSearchInput.clearFocus();
                    hideKeyboard(treatmentSearchInput);

                    return false; // consume.
                }
            }
            return true;
        });

        diseaseErrorView.setOnClickListener(view -> {
            if (!diseaseApplyError.isEmpty()) {
                viewModel.clearDiseaseTemplateApplyError();

            } else if (!diseaseListError.isEmpty()) {
                viewModel.retryDiseaseTemplates();
            }
        });

        treatmentErrorView.setOnClickListener(view -> {
            if (!treatmentApplyError.isEmpty()) {
                viewModel.clearTreatmentTemplateApplyError();

            } else if (!treatmentListError.isEmpty()) {
                viewModel.retryTreatmentTemplates();
            }
        });
    }

    private void observeTemplateState() {
        viewModel.getDiseaseTemplateItems().observe(getViewLifecycleOwner(), items -> {
            ArrayList<PrescriptionTemplateItem> safeItems = items == null ? new ArrayList<>() : items;
            diseaseAdapter.submitList(safeItems);

            if (selectedDiseaseTemplate == null && !viewModel.getLoadedDiseaseTemplateId().isEmpty()) {
                diseaseAdapter.setSelectedTemplateId(viewModel.getLoadedDiseaseTemplateId());
            }

            selectedDiseaseTemplate = diseaseAdapter.getSelectedItem();
            updateTemplateUi();
        });

        viewModel.getDiseaseTemplateLoading().observe(getViewLifecycleOwner(), loading -> {
            diseaseListLoading = Boolean.TRUE.equals(loading);
            updateTemplateUi();
        });

        viewModel.getDiseaseTemplateError().observe(getViewLifecycleOwner(), error -> {
            diseaseListError = cleanText(error);
            updateTemplateUi();
        });

        viewModel.getDiseaseTemplateApplyLoading().observe(getViewLifecycleOwner(), loading -> {
            boolean wasLoading = diseaseApplyLoading;
            diseaseApplyLoading = Boolean.TRUE.equals(loading);
            if (wasLoading && !diseaseApplyLoading) {
                handleDiseaseApplyFinished();
            }

            updateTemplateUi();
        });

        viewModel.getDiseaseTemplateApplyError().observe(getViewLifecycleOwner(), error -> {
            diseaseApplyError = cleanText(error);
            updateTemplateUi();
        });

        viewModel.getTreatmentTemplateItems().observe(getViewLifecycleOwner(), items -> {
            ArrayList<PrescriptionTemplateItem> safeItems = items == null ? new ArrayList<>() : items;
            treatmentAdapter.submitList(safeItems);

            if (selectedTreatmentTemplate == null && !viewModel.getLoadedTreatmentTemplateId().isEmpty()) {
                treatmentAdapter.setSelectedTemplateId(viewModel.getLoadedTreatmentTemplateId());
            }

            selectedTreatmentTemplate = treatmentAdapter.getSelectedItem();
            updateTemplateUi();
        });

        viewModel.getTreatmentTemplateLoading().observe(getViewLifecycleOwner(), loading -> {
            treatmentListLoading = Boolean.TRUE.equals(loading);

            updateTemplateUi();
        });

        viewModel.getTreatmentTemplateError().observe(getViewLifecycleOwner(), error -> {
            treatmentListError = cleanText(error);
            updateTemplateUi();
        });

        viewModel.getTreatmentTemplateApplyLoading().observe(getViewLifecycleOwner(), loading -> {
            boolean wasLoading = treatmentApplyLoading;
            treatmentApplyLoading = Boolean.TRUE.equals(loading);

            if (wasLoading && !treatmentApplyLoading) {
                handleTreatmentApplyFinished();
            }

            updateTemplateUi();
        });

        viewModel.getTreatmentTemplateApplyError().observe(getViewLifecycleOwner(), error -> {
            treatmentApplyError = cleanText(error);
            updateTemplateUi();
        });
    }

    private void requestDiseaseTemplateLoad() {
        if (selectedDiseaseTemplate == null || diseaseListLoading || isTemplateApplyBusy()) {
            return;
        }

        if (viewModel.hasDiseaseTemplateReplaceableData()) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Replace current prescription data?")
                    .setMessage(
                            "Loading this Disease Template will replace "
                                    + "all current Clinical, Advice and Rx data."
                                    + "\n\nVitals, Reference and Notes "
                                    + "will remain unchanged."
                    )
                    .setPositiveButton("LOAD", (dialog, which) -> applySelectedDiseaseTemplate())
                    .setNegativeButton("CANCEL", null)
                    .show();

            return;
        }

        applySelectedDiseaseTemplate();
    }

    private void applySelectedDiseaseTemplate() {
        if (selectedDiseaseTemplate == null) {
            return;
        }

        pendingDiseaseTemplateId = selectedDiseaseTemplate.getTemplateId();

        pendingTreatmentTemplateId = "";
        selectedTreatmentTemplate = null;

        treatmentAdapter.clearSelection();
        treatmentAdapter.clearFilter();
        treatmentSearchInput.setText("");

        viewModel.clearDiseaseTemplateApplyError();
        viewModel.applyDiseaseTemplate(
                selectedDiseaseTemplate
        );
    }

    private void requestTreatmentTemplateLoad() {
        if (selectedTreatmentTemplate == null || !isTreatmentSectionAvailable() || treatmentListLoading || isTemplateApplyBusy()) {
            return;
        }

        if (viewModel.hasRxTemplateReplaceableData()) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Replace current Rx data?")
                    .setMessage(
                            "Loading this Treatment Template will replace "
                                    + "all current Medicine, Therapy and "
                                    + "Treatment Plan data."
                    )
                    .setPositiveButton("LOAD", (dialog, which) -> applySelectedTreatmentTemplate())
                    .setNegativeButton("CANCEL", null)
                    .show();

            return;
        }

        applySelectedTreatmentTemplate();
    }

    private void applySelectedTreatmentTemplate() {
        if (selectedTreatmentTemplate == null) {
            return;
        }

        pendingTreatmentTemplateId = selectedTreatmentTemplate.getTemplateId();

        viewModel.clearTreatmentTemplateApplyError();
        viewModel.applyTreatmentTemplate(selectedTreatmentTemplate);
    }

    private void handleDiseaseApplyFinished() {
        if (pendingDiseaseTemplateId.isEmpty()) {
            return;
        }

        boolean successful = Boolean.TRUE.equals(viewModel.getDiseaseTemplateApplySuccess().getValue())
                && pendingDiseaseTemplateId.equals(viewModel.getLoadedDiseaseTemplateId());

        pendingDiseaseTemplateId = "";

        if (!successful) {
            return;
        }

        diseaseAdapter.setSelectedTemplateId(viewModel.getLoadedDiseaseTemplateId());

        selectedDiseaseTemplate = diseaseAdapter.getSelectedItem();

        selectedTreatmentTemplate = null;
        treatmentAdapter.clearSelection();

        Toast.makeText(requireContext(), "Disease Template loaded successfully", Toast.LENGTH_SHORT).show();
        showTreatmentStep();
    }

    private void handleTreatmentApplyFinished() {
        if (pendingTreatmentTemplateId.isEmpty()) {
            return;
        }

        boolean successful = Boolean.TRUE.equals(viewModel.getTreatmentTemplateApplySuccess().getValue())
                        && pendingTreatmentTemplateId.equals(viewModel.getLoadedTreatmentTemplateId());

        pendingTreatmentTemplateId = "";

        if (!successful) {
            return;
        }

        Toast.makeText(requireContext(), "Treatment Template loaded successfully", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void showDiseaseStep() {
        if (isTemplateApplyBusy()) {
            return;
        }

        showingTreatmentStep = false;

        diseaseSearchInput.setText("");
        diseaseAdapter.clearFilter();

        updateTemplateUi();
    }

    private void showTreatmentStep() {
        if (!isTreatmentSectionAvailable()) {
            return;
        }

        showingTreatmentStep = true;

        treatmentSearchInput.setText("");
        treatmentAdapter.clearFilter();

        viewModel.loadTreatmentTemplatesForDisease(viewModel.getLoadedDiseaseTemplateId());

        updateTemplateUi();
    }

    @SuppressLint("SetTextI18n")
    private void updateTemplateUi() {
        if (diseaseAdapter == null || treatmentAdapter == null) {
            return;
        }

        boolean applyBusy = isTemplateApplyBusy();
        boolean treatmentAvailable = isTreatmentSectionAvailable();

        if (showingTreatmentStep && !treatmentAvailable) {
            showingTreatmentStep = false;
        }

        diseaseSection.setVisibility(showingTreatmentStep ? View.GONE : View.VISIBLE);

        treatmentSection.setVisibility(showingTreatmentStep && treatmentAvailable ? View.VISIBLE : View.GONE);

        updateDiseaseTemplateUi(applyBusy);
        updateTreatmentTemplateUi(applyBusy, treatmentAvailable);

        closeButton.setEnabled(!applyBusy);
        closeButton.setAlpha(applyBusy ? 0.45f : 1.0f);

        setCancelable(!applyBusy);

        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setHideable(!applyBusy);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateDiseaseTemplateUi(boolean applyBusy) {
        boolean progressVisible = diseaseListLoading || diseaseApplyLoading;

        String visibleError = !diseaseApplyError.isEmpty() ? diseaseApplyError : diseaseListError;

        diseaseProgress.setVisibility(progressVisible ? View.VISIBLE : View.GONE);

        diseaseRecyclerView.setVisibility(!progressVisible
                        && visibleError.isEmpty()
                        && diseaseAdapter.getItemCount() > 0
                        ? View.VISIBLE
                        : View.GONE
        );

        if (!diseaseApplyError.isEmpty()) {
            diseaseErrorView.setText(
                    "Disease Template could not be loaded.\n"
                            + diseaseApplyError
                            + "\n\nTap here to return to the template list and try again."
            );

        } else if (!diseaseListError.isEmpty()) {
            diseaseErrorView.setText(
                    "Disease Template list could not be loaded.\n"
                            + diseaseListError
                            + "\n\nTap here to retry."
            );

        } else {
            diseaseErrorView.setText("");
        }

        diseaseErrorView.setVisibility(visibleError.isEmpty() ? View.GONE : View.VISIBLE);

        boolean showEmptyState = !progressVisible && visibleError.isEmpty() && diseaseAdapter.getItemCount() == 0;

        if (showEmptyState) {
            diseaseEmptyView.setText(diseaseAdapter.getTotalItemCount() == 0
                            ? "No Disease Template available"
                            : "No matching Disease Template found"
            );
        }

        diseaseEmptyView.setVisibility(showEmptyState ? View.VISIBLE : View.GONE);

        boolean canLoadDisease = selectedDiseaseTemplate != null && !diseaseListLoading && !applyBusy;

        loadDiseaseButton.setEnabled(canLoadDisease);
        loadDiseaseButton.setAlpha(canLoadDisease ? 1.0f : 0.45f);

        loadDiseaseButton.setText(diseaseApplyLoading ? "LOADING DISEASE TEMPLATE..." : "LOAD DISEASE TEMPLATE");

        diseaseRecyclerView.setEnabled(!diseaseListLoading && !applyBusy);

        diseaseSearchInput.setEnabled(!diseaseListLoading && !applyBusy);
    }

    @SuppressLint("SetTextI18n")
    private void updateTreatmentTemplateUi(boolean applyBusy, boolean treatmentAvailable) {
        loadedDiseaseTemplateNameView.setText(viewModel.getLoadedDiseaseTemplateName());
        treatmentHelperView.setText("Medicine, Therapy and Treatment Plan data will be loaded");

        boolean progressVisible = treatmentAvailable && (treatmentListLoading || treatmentApplyLoading);

        String visibleError = !treatmentApplyError.isEmpty() ? treatmentApplyError : treatmentListError;

        treatmentProgress.setVisibility(progressVisible ? View.VISIBLE : View.GONE);

        treatmentRecyclerView.setVisibility(treatmentAvailable
                        && !progressVisible
                        && visibleError.isEmpty()
                        && treatmentAdapter.getItemCount() > 0
                        ? View.VISIBLE
                        : View.GONE
        );

        if (!treatmentApplyError.isEmpty()) {
            treatmentErrorView.setText(
                    "Treatment Template could not be loaded.\n"
                            + treatmentApplyError
                            + "\n\nTap here to return to the template list and try again."
            );

        } else if (!treatmentListError.isEmpty()) {
            treatmentErrorView.setText(
                    "Treatment Template list could not be loaded.\n"
                            + treatmentListError
                            + "\n\nTap here to retry."
            );

        } else {
            treatmentErrorView.setText("");
        }

        treatmentErrorView.setVisibility(treatmentAvailable
                        && !visibleError.isEmpty()
                        ? View.VISIBLE
                        : View.GONE
        );

        boolean showEmptyState = treatmentAvailable
                        && !progressVisible
                        && visibleError.isEmpty()
                        && treatmentAdapter.getItemCount() == 0;

        if (showEmptyState) {
            treatmentEmptyView.setText(treatmentAdapter.getTotalItemCount() == 0
                            ? "No Treatment Template available"
                            : "No matching Treatment Template found"
            );
        }

        treatmentEmptyView.setVisibility(showEmptyState ? View.VISIBLE : View.GONE);

        boolean canLoadTreatment = treatmentAvailable
                        && selectedTreatmentTemplate != null
                        && !treatmentListLoading
                        && !applyBusy;

        loadTreatmentButton.setEnabled(canLoadTreatment);
        loadTreatmentButton.setAlpha(canLoadTreatment ? 1.0f : 0.45f);
        loadTreatmentButton.setText(treatmentApplyLoading ? "LOADING TREATMENT..." : "LOAD TREATMENT");
        treatmentRecyclerView.setEnabled(treatmentAvailable && !treatmentListLoading && !applyBusy);

        treatmentSearchInput.setEnabled(treatmentAvailable && !treatmentListLoading && !applyBusy);

        changeDiseaseButton.setEnabled(!applyBusy);
        changeDiseaseButton.setAlpha(applyBusy ? 0.45f : 1.0f);

        doneWithoutTreatmentButton.setEnabled(!applyBusy);
        doneWithoutTreatmentButton.setAlpha(applyBusy ? 0.45f : 1.0f);
    }

    private boolean isTreatmentSectionAvailable() {
        return viewModel != null && !viewModel.getLoadedDiseaseTemplateId().isEmpty();
    }

    private boolean isTemplateApplyBusy() {
        return diseaseApplyLoading || treatmentApplyLoading;
    }

    @NonNull
    private String cleanText(@Nullable String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {

            return "";
        }

        return value.trim();
    }

    private void hideKeyboard(@NonNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        if (bottomSheet == null) {
            return;
        }

        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        bottomSheet.setLayoutParams(layoutParams);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.setHideable(!isTemplateApplyBusy());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        viewModel = null;

        closeButton = null;

        diseaseSection = null;
        diseaseSearchInput = null;
        diseaseProgress = null;
        diseaseErrorView = null;
        diseaseEmptyView = null;
        diseaseRecyclerView = null;
        loadDiseaseButton = null;

        treatmentSection = null;
        treatmentHelperView = null;
        loadedDiseaseTemplateNameView = null;
        treatmentSearchInput = null;
        treatmentProgress = null;
        treatmentErrorView = null;
        treatmentEmptyView = null;
        treatmentRecyclerView = null;
        changeDiseaseButton = null;
        doneWithoutTreatmentButton = null;
        loadTreatmentButton = null;

        diseaseAdapter = null;
        treatmentAdapter = null;

        selectedDiseaseTemplate = null;
        selectedTreatmentTemplate = null;

        bottomSheetBehavior = null;
    }
}