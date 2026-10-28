package ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionPatientAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPatientItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionPatientSelectionBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionPatientSelectionBottomSheet";

    public static final String RESULT_REQUEST_KEY = "prescription_patient_selection_result";
    public static final String KEY_CANCELLED = "patient_selection_cancelled";
    public static final String KEY_PATIENT_ID = "selected_patient_id";
    public static final String KEY_PATIENT_HISTORY_ID = "selected_patient_history_id";
    public static final String KEY_PATIENT_NAME = "selected_patient_name";
    public static final String KEY_PATIENT_CODE = "selected_patient_code";
    public static final String KEY_PATIENT_THANA = "selected_patient_thana";
    public static final String KEY_PATIENT_YEAR = "selected_patient_year";
    public static final String KEY_PATIENT_MOBILE = "selected_patient_mobile";
    public static final String KEY_PATIENT_GENDER = "selected_patient_gender";
    public static final String KEY_PATIENT_AGE = "selected_patient_age";
    public static final String KEY_PRESCRIPTION_TYPE = "selected_prescription_type";

    private static final String ARG_REQUIRED_SELECTION = "required_selection";
    private static final String ARG_CURRENT_PATIENT_HISTORY_ID = "current_patient_history_id";
    private static final String ARG_CURRENT_PRESCRIPTION_TYPE = "current_prescription_type";

    private static final long SEARCH_DELAY_MS = 500L;
    private static final int MINIMUM_SEARCH_LENGTH = 3;

    private PrescriptionBuilderViewModel viewModel;

    private AppCompatImageButton closeButton;
//    private TextInputLayout searchInputLayout;
    private TextInputEditText searchEditText;
    private TextInputLayout yearInputLayout;
    private MaterialAutoCompleteTextView yearAutoComplete;
    private MaterialAutoCompleteTextView prescriptionTypeAutoComplete;
    private TextView helperView;
    private RecyclerView patientRecyclerView;
    private CircularProgressIndicator patientProgress;
    private TextView patientEmptyView;
    private MaterialButton cancelButton;
    private MaterialButton selectButton;

    private PrescriptionPatientAdapter patientAdapter;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private final ArrayList<PrescriptionPatientItem> currentPatientItems = new ArrayList<>();

    @Nullable
    private Runnable pendingSearchRunnable;

    @Nullable
    private PrescriptionPatientItem selectedPatient;

    @NonNull
    private String selectedYear = "";

    @NonNull
    private String selectedPrescriptionType = "2";

    @NonNull
    private String currentPatientHistoryId = "";

    @NonNull
    private String patientListError = "";

    private boolean requiredSelection = false;
    private boolean patientListLoading = false;
    private boolean resultDelivered = false;

    @NonNull
    public static PrescriptionPatientSelectionBottomSheet newRequiredInstance() {
        PrescriptionPatientSelectionBottomSheet sheet = new PrescriptionPatientSelectionBottomSheet();

        Bundle arguments = new Bundle();
        arguments.putBoolean(ARG_REQUIRED_SELECTION, true);
        arguments.putString(ARG_CURRENT_PRESCRIPTION_TYPE, "2");
        sheet.setArguments(arguments);

        return sheet;
    }

    @NonNull
    public static PrescriptionPatientSelectionBottomSheet newChangeInstance(@Nullable String currentPatientHistoryId, @Nullable String currentPrescriptionType) {
        PrescriptionPatientSelectionBottomSheet sheet = new PrescriptionPatientSelectionBottomSheet();

        Bundle arguments = new Bundle();
        arguments.putBoolean(ARG_REQUIRED_SELECTION, false);
        arguments.putString(ARG_CURRENT_PATIENT_HISTORY_ID, currentPatientHistoryId == null ? "" : currentPatientHistoryId);
        arguments.putString(ARG_CURRENT_PRESCRIPTION_TYPE, "1".equals(currentPrescriptionType) ? "1" : "2");

        sheet.setArguments(arguments);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_prescription_patient_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);

        readArguments();
        initializeViews(view);
        configurePatientAdapter();
        configureDropdowns();
        configureSearch();
        configureActions();

        selectedPatient = null;
        resultDelivered = false;
        viewModel.clearPatientList();

        observePatientState();

        viewModel.clearPatientListError();
        viewModel.loadRecentPatients(selectedYear);
        updateSelectButtonState();
    }

    private void readArguments() {
        Bundle arguments = getArguments();

        if (arguments == null) {
            requiredSelection = false;
            selectedPrescriptionType = "2";
            currentPatientHistoryId = "";
            return;
        }

        requiredSelection = arguments.getBoolean(ARG_REQUIRED_SELECTION, false);
        currentPatientHistoryId = cleanText(arguments.getString(ARG_CURRENT_PATIENT_HISTORY_ID));
        selectedPrescriptionType = "1".equals(arguments.getString(ARG_CURRENT_PRESCRIPTION_TYPE)) ? "1" : "2";
    }

    private void initializeViews(@NonNull View view) {
        closeButton = view.findViewById(R.id.btn_close_patient_selection);
//        searchInputLayout = view.findViewById(R.id.layout_patient_search);
        searchEditText = view.findViewById(R.id.et_patient_search);
        yearInputLayout = view.findViewById(R.id.layout_patient_registration_year);
        yearAutoComplete = view.findViewById(R.id.actv_patient_registration_year);
        prescriptionTypeAutoComplete = view.findViewById(R.id.actv_patient_prescription_type);
        helperView = view.findViewById(R.id.tv_patient_list_helper);
        patientRecyclerView = view.findViewById(R.id.rv_prescription_patients);
        patientProgress = view.findViewById(R.id.progress_prescription_patients);
        patientEmptyView = view.findViewById(R.id.tv_prescription_patient_empty);
        cancelButton = view.findViewById(R.id.btn_cancel_patient_selection);
        selectButton = view.findViewById(R.id.btn_select_prescription_patient);
    }

    private void configurePatientAdapter() {
        patientAdapter = new PrescriptionPatientAdapter(patientItem -> {
            selectedPatient = patientItem;
            updateSelectButtonState();
        });

        patientRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        patientRecyclerView.setAdapter(patientAdapter);
    }

    private void configureDropdowns() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        ArrayList<String> registrationYears = new ArrayList<>();

        for (int year = currentYear; year >= currentYear - 50; year--) {
            registrationYears.add(String.valueOf(year));
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, registrationYears);
        yearAutoComplete.setAdapter(yearAdapter);

        selectedYear = String.valueOf(currentYear);
        yearAutoComplete.setText(selectedYear, false);

        yearAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            selectedYear = registrationYears.get(position);
            clearCurrentSelection();

            if (!getSearchText().isEmpty()) {
                searchEditText.setText("");
            } else {
                viewModel.loadRecentPatients(selectedYear);
            }
        });

        ArrayList<String> prescriptionTypes = new ArrayList<>();

        prescriptionTypes.add("Indoor");
        prescriptionTypes.add("Outdoor");

        ArrayAdapter<String> prescriptionTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, prescriptionTypes);
        prescriptionTypeAutoComplete.setAdapter(prescriptionTypeAdapter);

        prescriptionTypeAutoComplete.setText("1".equals(selectedPrescriptionType) ? "Outdoor" : "Indoor", false);

        prescriptionTypeAutoComplete.setOnItemClickListener((parent, view, position, id) -> selectedPrescriptionType = position == 0 ? "2" : "1");
    }

    private void configureSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                handleSearchTextChanged(editable == null ? "" : editable.toString());
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    searchEditText.clearFocus();
                    hideKeyboard(searchEditText);

                    return false; // consume.
                }
            }

            String searchText = getSearchText();
            if (searchText.length() >= MINIMUM_SEARCH_LENGTH) {
                cancelPendingSearch();
                viewModel.searchPatients(searchText);
            }

            return true;
        });
    }

    @SuppressLint("SetTextI18n")
    private void handleSearchTextChanged(@NonNull String enteredText) {
        cancelPendingSearch();
        clearCurrentSelection();

        String searchText = enteredText.trim();

        if (searchText.isEmpty()) {
            yearInputLayout.setEnabled(true);
            helperView.setText("Showing the latest 100 patients for the selected year");
            viewModel.loadRecentPatients(selectedYear);

            return;
        }

        yearInputLayout.setEnabled(false);

        if (searchText.length() < MINIMUM_SEARCH_LENGTH) {
            helperView.setText("Enter at least 3 characters to search");
            viewModel.clearPatientList();
            return;
        }

        helperView.setText("Searching all registered patients");

        /*
         * আগের request/result invalid হবে।
         * নতুন request 500ms পরে যাবে।
         */
        viewModel.clearPatientList();

        pendingSearchRunnable = () -> viewModel.searchPatients(searchText);

        searchHandler.postDelayed(pendingSearchRunnable, SEARCH_DELAY_MS);
    }

    private void configureActions() {
        closeButton.setOnClickListener(view -> cancelSelection());
        cancelButton.setOnClickListener(view -> cancelSelection());
        selectButton.setOnClickListener(view -> deliverSelectedPatient());
        patientEmptyView.setOnClickListener(view -> {
            if (patientListLoading) {
                return;
            }

            String searchText = getSearchText();

            if (searchText.length() >= MINIMUM_SEARCH_LENGTH) {
                viewModel.searchPatients(searchText);

            } else if (searchText.isEmpty()) {
                viewModel.loadRecentPatients(selectedYear);
            }
        });
    }

    private void observePatientState() {
        viewModel.getPatientItems().observe(getViewLifecycleOwner(), patientItems -> {
            currentPatientItems.clear();

            if (patientItems != null) {
                currentPatientItems.addAll(patientItems);
            }

            patientAdapter.submitList(currentPatientItems);

            if (selectedPatient != null) {
                patientAdapter.setSelectedPatient(selectedPatient);
            }
            updatePatientListState();
        });

        viewModel.getPatientListLoading().observe(getViewLifecycleOwner(), isLoading -> {
            patientListLoading = Boolean.TRUE.equals(isLoading);
            updatePatientListState();
        });

        viewModel.getPatientListError().observe(getViewLifecycleOwner(), errorMessage -> {
            patientListError = cleanText(errorMessage);
            updatePatientListState();
        });
    }

//    private void restoreCurrentPatientSelection() {
//        if (selectedPatient != null || currentPatientHistoryId.isEmpty()) {
//            if (selectedPatient != null) {
//                patientAdapter.setSelectedPatient(selectedPatient);
//            }
//            return;
//        }
//
//        for (PrescriptionPatientItem patientItem : currentPatientItems) {
//            if (patientItem.getPatientHistoryId().equals(currentPatientHistoryId)) {
//                selectedPatient = patientItem;
//                patientAdapter.setSelectedPatient(patientItem);
//
//                updateSelectButtonState();
//                return;
//            }
//        }
//    }

    @SuppressLint("SetTextI18n")
    private void updatePatientListState() {
        if (patientProgress == null || patientRecyclerView == null || patientEmptyView == null) {
            return;
        }

        patientProgress.setVisibility(patientListLoading ? View.VISIBLE : View.GONE);

        if (patientListLoading) {
            patientRecyclerView.setVisibility(View.GONE);
            patientEmptyView.setVisibility(View.GONE);
            return;
        }

        boolean hasPatients = !currentPatientItems.isEmpty();

        patientRecyclerView.setVisibility(hasPatients ? View.VISIBLE : View.GONE);
        patientEmptyView.setVisibility(hasPatients ? View.GONE : View.VISIBLE);

        if (!hasPatients) {
            if (!patientListError.isEmpty()) {
                patientEmptyView.setText(patientListError + "\nTap to retry");

            } else if (!getSearchText().isEmpty() && getSearchText().length() < MINIMUM_SEARCH_LENGTH) {
                patientEmptyView.setText("Enter at least 3 characters to search");

            } else {
                patientEmptyView.setText("No patient found");
            }
        }
    }

    private void updateSelectButtonState() {
        if (selectButton == null) {
            return;
        }

        boolean patientSelected = selectedPatient != null;

        selectButton.setEnabled(patientSelected);
        selectButton.setAlpha(patientSelected ? 1.0f : 0.45f);
    }

    private void clearCurrentSelection() {
        selectedPatient = null;

        if (patientAdapter != null) {
            patientAdapter.clearSelection();
        }

        updateSelectButtonState();
    }

    private void deliverSelectedPatient() {
        if (selectedPatient == null || resultDelivered) {
            return;
        }

        Bundle result = new Bundle();

        result.putBoolean(KEY_CANCELLED, false);
        result.putString(KEY_PATIENT_ID, selectedPatient.getPatientId());
        result.putString(KEY_PATIENT_HISTORY_ID, selectedPatient.getPatientHistoryId());
        result.putString(KEY_PATIENT_NAME, selectedPatient.getPatientName());
        result.putString(KEY_PATIENT_CODE, selectedPatient.getPatientCode());
        result.putString(KEY_PATIENT_THANA, selectedPatient.getThanaName());
        result.putString(KEY_PATIENT_YEAR, selectedPatient.getRegistrationYear());
        result.putString(KEY_PATIENT_MOBILE, selectedPatient.getMobileNumber());
        result.putString(KEY_PATIENT_GENDER, selectedPatient.getGender());
        result.putString(KEY_PATIENT_AGE, selectedPatient.getAge());
        result.putString(KEY_PRESCRIPTION_TYPE, selectedPrescriptionType);

        resultDelivered = true;

        getParentFragmentManager().setFragmentResult(RESULT_REQUEST_KEY, result);

        dismiss();
    }

    private void cancelSelection() {
        if (resultDelivered) {
            return;
        }

        Bundle result = new Bundle();

        result.putBoolean(KEY_CANCELLED, true);
        resultDelivered = true;
        getParentFragmentManager().setFragmentResult(RESULT_REQUEST_KEY, result);

        dismiss();
    }

    private void hideKeyboard(@NonNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void cancelPendingSearch() {
        if (pendingSearchRunnable != null) {
            searchHandler.removeCallbacks(pendingSearchRunnable);

            pendingSearchRunnable = null;
        }

        if (viewModel != null) {
            viewModel.cancelPatientSearch();
        }
    }

    @NonNull
    private String getSearchText() {
        if (searchEditText == null || searchEditText.getText() == null) {
            return "";
        }

        return searchEditText.getText().toString().trim();
    }

    @NonNull
    private String cleanText(
            @Nullable String value
    ) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return "";
        }

        return value.trim();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if (!resultDelivered) {
            Bundle result = new Bundle();

            result.putBoolean(KEY_CANCELLED, true);
            resultDelivered = true;
            getParentFragmentManager().setFragmentResult(RESULT_REQUEST_KEY, result);
        }

        super.onCancel(dialog);
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

        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);

        /*
         * Mandatory selection-এ accidental swipe-dismiss বন্ধ।
         * Close/Cancel/Back ব্যবহার করলে result Activity-তে যাবে।
         */
        behavior.setHideable(!requiredSelection);
    }

    @Override
    public void onDestroyView() {
        cancelPendingSearch();
        super.onDestroyView();

        viewModel = null;

        closeButton = null;
//        searchInputLayout = null;
        searchEditText = null;
        yearInputLayout = null;
        yearAutoComplete = null;
        prescriptionTypeAutoComplete = null;
        helperView = null;
        patientRecyclerView = null;
        patientProgress = null;
        patientEmptyView = null;
        cancelButton = null;
        selectButton = null;

        patientAdapter = null;
        selectedPatient = null;

        currentPatientItems.clear();
    }
}