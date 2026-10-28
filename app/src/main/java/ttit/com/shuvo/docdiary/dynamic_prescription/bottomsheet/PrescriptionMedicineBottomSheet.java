package ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.ClinicalOptionAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalOption;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.MedicineEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionMedicineBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionMedicineBottomSheet";
    private static final String ARG_EDIT_ENTRY_ID = "edit_entry_id";

    private static final String DEFAULT_DOSE_ID = "2";
    private static final String DEFAULT_TIMING_ID = "2";
    private static final String DEFAULT_ROUTE_ID = "11";

    private boolean durationRequired = false;

    private PrescriptionBuilderViewModel viewModel;

    private TextView tvTitle;
    private TextView tvSubtitle;

    private TextInputLayout medicineInputLayout;
    private TextInputLayout doseInputLayout;
    private TextInputLayout timingInputLayout;
    private TextInputLayout routeInputLayout;
    private TextInputLayout durationInputLayout;

    private MaterialAutoCompleteTextView medicineAutoComplete;
    private MaterialAutoCompleteTextView doseAutoComplete;
    private MaterialAutoCompleteTextView timingAutoComplete;
    private MaterialAutoCompleteTextView routeAutoComplete;

    private TextInputEditText durationEditText;
    private TextInputEditText notesEditText;

    private MaterialButton btnSave;

    private ClinicalOptionAdapter medicineAdapter;
    private ClinicalOptionAdapter doseAdapter;
    private ClinicalOptionAdapter timingAdapter;
    private ClinicalOptionAdapter routeAdapter;

    private MedicineEntryItem editingItem;

    @NonNull
    public static PrescriptionMedicineBottomSheet newAddInstance() {
        return new PrescriptionMedicineBottomSheet();
    }

    @NonNull
    public static PrescriptionMedicineBottomSheet newEditInstance(@NonNull String localEntryId) {
        PrescriptionMedicineBottomSheet sheet = new PrescriptionMedicineBottomSheet();
        Bundle arguments = new Bundle();
        arguments.putString(ARG_EDIT_ENTRY_ID, localEntryId);
        sheet.setArguments(arguments);

        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_prescription_medicine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);

        initializeViews(view);
        configureDurationRequirement();
        configureOptionAdapters();
        configureSelectionFields();
        configureActions(view);
        loadEditingItem();
        applyDefaultValuesForAddMode();
    }

    private void initializeViews(@NonNull View view) {
        tvTitle = view.findViewById(R.id.tv_medicine_sheet_title);
        tvSubtitle = view.findViewById(R.id.tv_medicine_sheet_subtitle);

        medicineInputLayout = view.findViewById(R.id.layout_medicine_name);
        doseInputLayout = view.findViewById(R.id.layout_medicine_dose);
        timingInputLayout = view.findViewById(R.id.layout_medicine_timing);
        routeInputLayout = view.findViewById(R.id.layout_medicine_route);
        durationInputLayout = view.findViewById(R.id.layout_medicine_duration);

        medicineAutoComplete = view.findViewById(R.id.actv_medicine_name);
        doseAutoComplete = view.findViewById(R.id.actv_medicine_dose);
        timingAutoComplete = view.findViewById(R.id.actv_medicine_timing);
        routeAutoComplete = view.findViewById(R.id.actv_medicine_route);

        durationEditText = view.findViewById(R.id.et_medicine_duration);
        notesEditText = view.findViewById(R.id.et_medicine_notes);

        btnSave = view.findViewById(R.id.btn_save_medicine);

        durationEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    durationEditText.clearFocus();
                    hideKeyboard(durationEditText);

                    return false; // consume.
                }
            }
            return true;
        });
    }

    private void configureDurationRequirement() {
        durationInputLayout.setHint(durationRequired ? "Duration (Days)" : "Duration (Days) - Optional");
    }

    private void configureOptionAdapters() {
        medicineAdapter = createOptionAdapter(viewModel.getCurrentMedicineOptions(), medicineAutoComplete);
        doseAdapter = createOptionAdapter(viewModel.getCurrentDoseOptions(), doseAutoComplete);
        timingAdapter = createOptionAdapter(viewModel.getCurrentMedicineTimingOptions(), timingAutoComplete);
        routeAdapter = createOptionAdapter(viewModel.getCurrentMedicineRouteOptions(), routeAutoComplete);
    }

    @NonNull
    private ClinicalOptionAdapter createOptionAdapter(@NonNull ArrayList<ClinicalOption> items, @NonNull MaterialAutoCompleteTextView autoCompleteTextView) {
        ClinicalOptionAdapter adapter = new ClinicalOptionAdapter(requireContext());
        adapter.submitList(items);
        autoCompleteTextView.setAdapter(adapter);

        return adapter;
    }

    private void configureSelectionFields() {
        configureStrictSelection(medicineAutoComplete, medicineInputLayout, medicineAdapter);
        configureStrictSelection(doseAutoComplete, doseInputLayout, doseAdapter);
        configureStrictSelection(timingAutoComplete, timingInputLayout, timingAdapter);
        configureStrictSelection(routeAutoComplete, routeInputLayout, routeAdapter);

        configureErrorClearing(durationEditText, durationInputLayout);
    }

    private void configureStrictSelection(@NonNull MaterialAutoCompleteTextView autoCompleteTextView, @NonNull TextInputLayout inputLayout, @NonNull ClinicalOptionAdapter adapter) {
        autoCompleteTextView.setOnClickListener(view -> {
            if (adapter.getCount() > 0) {
                autoCompleteTextView.showDropDown();
            }
        });

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);

            if (!(selectedItem instanceof ClinicalOption)) {
                return;
            }

            ClinicalOption selectedOption = (ClinicalOption) selectedItem;
            autoCompleteTextView.setText(selectedOption.getOptionName(), false);
            clearInputError(inputLayout);

            autoCompleteTextView.clearFocus();
            hideKeyboard(autoCompleteTextView);

            autoCompleteTextView.post(() -> autoCompleteTextView.setSelection(autoCompleteTextView.length()));
        });

        autoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    autoCompleteTextView.clearFocus();
                    hideKeyboard(autoCompleteTextView);

                    return false; // consume.
                }
            }
            return true;
        });

        configureErrorClearing(autoCompleteTextView, inputLayout);
    }

    private void configureErrorClearing(@NonNull TextView inputView, @NonNull TextInputLayout inputLayout) {
        inputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                clearInputError(inputLayout);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void configureActions(@NonNull View view) {
        view.findViewById(R.id.btn_close_medicine_sheet).setOnClickListener(button -> dismiss());
        view.findViewById(R.id.btn_cancel_medicine).setOnClickListener(button -> dismiss());
        btnSave.setOnClickListener(button -> saveMedicine());
    }

    @SuppressLint("SetTextI18n")
    private void loadEditingItem() {
        Bundle arguments = getArguments();

        if (arguments == null) {
            return;
        }

        String localEntryId = arguments.getString(ARG_EDIT_ENTRY_ID);

        if (localEntryId == null || localEntryId.trim().isEmpty()) {
            return;
        }

        editingItem = viewModel.findMedicineEntryById(localEntryId);

        if (editingItem == null) {
            dismiss();
            return;
        }

        tvTitle.setText("Edit Medicine");
        tvSubtitle.setText("Update medicine and prescription details");
        btnSave.setText("UPDATE MEDICINE");

        setAutoCompleteText(medicineAutoComplete, editingItem.getMedicineName());
        setAutoCompleteText(doseAutoComplete, editingItem.getDoseName());
        setAutoCompleteText(timingAutoComplete, editingItem.getTimingName());
        setAutoCompleteText(routeAutoComplete, editingItem.getRouteName());

        durationEditText.setText(editingItem.getDurationDays());
        notesEditText.setText(editingItem.getNotes());
    }

    private void applyDefaultValuesForAddMode() {
        Bundle arguments = getArguments();

        /*
         * Edit request হলে existing item-এর value-ই থাকবে।
         */
        if (arguments != null && arguments.containsKey(ARG_EDIT_ENTRY_ID)) {
            return;
        }

        applyDefaultOption(doseAdapter, doseAutoComplete, DEFAULT_DOSE_ID);
        applyDefaultOption(timingAdapter, timingAutoComplete, DEFAULT_TIMING_ID);
        applyDefaultOption(routeAdapter, routeAutoComplete, DEFAULT_ROUTE_ID);
    }

    private void applyDefaultOption(@NonNull ClinicalOptionAdapter adapter, @NonNull MaterialAutoCompleteTextView autoCompleteTextView, @NonNull String optionId) {
        ClinicalOption defaultOption = findOptionById(adapter, optionId);

        if (defaultOption != null) {
            setAutoCompleteText(autoCompleteTextView, defaultOption.getOptionName());
        }
    }

    @Nullable
    private ClinicalOption findOptionById(@NonNull ClinicalOptionAdapter adapter, @NonNull String optionId) {
        for (int index = 0; index < adapter.getCount(); index++) {
            ClinicalOption option = adapter.getItem(index);

            if (option != null && option.getOptionId().trim().equals(optionId)) {
                return option;
            }
        }

        return null;
    }

    private void saveMedicine() {
        clearAllErrors();

        String medicineText = getInputText(medicineAutoComplete);
        String doseText = getInputText(doseAutoComplete);
        String timingText = getInputText(timingAutoComplete);
        String routeText = getInputText(routeAutoComplete);
        String durationText = getInputText(durationEditText);
        String notesText = getInputText(notesEditText);

        if (medicineText.isEmpty()) {
            showInputError(medicineInputLayout, medicineAutoComplete, "Select a medicine");
            return;
        }

        ClinicalOption medicineOption = medicineAdapter.findExactMatch(medicineText);

        if (medicineOption == null) {
            showInputError(medicineInputLayout, medicineAutoComplete, "Select a valid medicine from the list");
            return;
        }

        if (doseText.isEmpty()) {
            showInputError(doseInputLayout, doseAutoComplete, "Select a dose");
            return;
        }

        ClinicalOption doseOption = doseAdapter.findExactMatch(doseText);

        if (doseOption == null) {
            showInputError(doseInputLayout, doseAutoComplete, "Select a valid dose from the list");
            return;
        }

        if (timingText.isEmpty()) {
            showInputError(timingInputLayout, timingAutoComplete, "Select medicine timing");
            return;
        }

        ClinicalOption timingOption = timingAdapter.findExactMatch(timingText);

        if (timingOption == null) {
            showInputError(timingInputLayout, timingAutoComplete, "Select a valid timing from the list");
            return;
        }

        if (routeText.isEmpty()) {
            showInputError(routeInputLayout, routeAutoComplete, "Select medicine route");
            return;
        }

        ClinicalOption routeOption = routeAdapter.findExactMatch(routeText);

        if (routeOption == null) {
            showInputError(routeInputLayout, routeAutoComplete, "Select a valid route from the list");
            return;
        }

        if (durationRequired && durationText.isEmpty()) {
            showInputError(durationInputLayout, durationEditText, "Enter duration in days");
            return;
        }

        if (!durationText.isEmpty()) {
            try {
                int durationValue = Integer.parseInt(durationText);

                if (durationValue <= 0) {
                    showInputError(durationInputLayout, durationEditText, "Duration must be greater than 0");
                    return;
                }

            } catch (NumberFormatException exception) {
                showInputError(durationInputLayout, durationEditText, "Enter a valid positive duration");
                return;
            }
        }

        MedicineEntryItem itemToSave;

        if (editingItem == null) {
            itemToSave = MedicineEntryItem.create(
                    medicineOption,
                    doseOption,
                    timingOption,
                    routeOption,
                    durationText,
                    notesText
            );

        } else {
            itemToSave = editingItem.update(
                    medicineOption,
                    doseOption,
                    timingOption,
                    routeOption,
                    durationText,
                    notesText
            );
        }

        boolean successful;

        if (editingItem == null) {
            successful = viewModel.addMedicine(itemToSave);
        } else {
            successful = viewModel.updateMedicine(itemToSave);
        }

        if (!successful) {
            showInputError(medicineInputLayout, medicineAutoComplete, "This medicine has already been added");
            return;
        }

        dismiss();
    }

    private void clearAllErrors() {
        clearInputError(medicineInputLayout);
        clearInputError(doseInputLayout);
        clearInputError(timingInputLayout);
        clearInputError(routeInputLayout);
        clearInputError(durationInputLayout);
    }

    private void showInputError(@NonNull TextInputLayout inputLayout, @NonNull View inputView, @NonNull String errorMessage) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(errorMessage);
        inputView.requestFocus();
    }

    private void clearInputError(@NonNull TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    private void setAutoCompleteText(@NonNull MaterialAutoCompleteTextView autoCompleteTextView, @NonNull String value) {
        autoCompleteTextView.setText(value, false);
        autoCompleteTextView.setSelection(autoCompleteTextView.length());
    }

    @NonNull
    private String getInputText(@NonNull TextView inputView) {
        if (inputView.getText() == null) {
            return "";
        }

        return inputView.getText().toString().trim();
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

        BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);
        behavior.setHideable(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        viewModel = null;

        tvTitle = null;
        tvSubtitle = null;

        medicineInputLayout = null;
        doseInputLayout = null;
        timingInputLayout = null;
        routeInputLayout = null;
        durationInputLayout = null;

        medicineAutoComplete = null;
        doseAutoComplete = null;
        timingAutoComplete = null;
        routeAutoComplete = null;

        durationEditText = null;
        notesEditText = null;

        btnSave = null;

        medicineAdapter = null;
        doseAdapter = null;
        timingAdapter = null;
        routeAdapter = null;

        editingItem = null;
    }
}