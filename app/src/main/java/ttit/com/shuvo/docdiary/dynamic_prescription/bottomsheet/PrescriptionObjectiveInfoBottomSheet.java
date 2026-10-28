package ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.ClinicalOptionAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalOption;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ObjectiveInfoItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionObjectiveInfoBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionObjectiveInfoBottomSheet";
    private static final String ARG_EDIT_ENTRY_ID = "edit_entry_id";

    private PrescriptionBuilderViewModel viewModel;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextView tvValueError;

    private TextInputLayout objectiveInputLayout;
    private TextInputLayout objectiveValueInputLayout;
    private TextInputLayout muscleJointInputLayout;
    private TextInputLayout notesInputLayout;

    private MaterialAutoCompleteTextView objectiveAutoComplete;
    private MaterialAutoCompleteTextView objectiveValueAutoComplete;
    private MaterialAutoCompleteTextView muscleJointAutoComplete;

    private TextInputEditText notesEditText;

    private View valueLoadingLayout;
    private View valueErrorLayout;

    private MaterialButton btnRetryValue;
    private MaterialButton btnSave;

    private ClinicalOptionAdapter objectiveAdapter;
    private ClinicalOptionAdapter objectiveValueAdapter;
    private ClinicalOptionAdapter muscleJointAdapter;

    private final ArrayList<ClinicalOption> objectiveItems = new ArrayList<>();
    private final ArrayList<ClinicalOption> objectiveValueItems = new ArrayList<>();
    private final ArrayList<ClinicalOption> muscleJointItems = new ArrayList<>();

    private ClinicalOption selectedObjective;
    private ClinicalOption selectedObjectiveValue;
    private ClinicalOption selectedMuscleJoint;

    private ObjectiveInfoItem editingItem;

    /*
     * Edit mode-এ Value API response আসার পর
     * আগের selected Value restore করতে ব্যবহৃত হবে।
     */
    private String pendingEditingValueId;

    @NonNull
    public static PrescriptionObjectiveInfoBottomSheet newAddInstance() {
        return new PrescriptionObjectiveInfoBottomSheet();
    }

    @NonNull
    public static PrescriptionObjectiveInfoBottomSheet newEditInstance(@NonNull String localEntryId) {
        PrescriptionObjectiveInfoBottomSheet sheet = new PrescriptionObjectiveInfoBottomSheet();
        Bundle arguments = new Bundle();
        arguments.putString(ARG_EDIT_ENTRY_ID, localEntryId);
        sheet.setArguments(arguments);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_prescription_objective_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);
        /*
         * আগের BottomSheet-এর active Value UI state clear হবে।
         * Cache clear হবে না।
         */
        viewModel.clearObjectiveValueSelection();

        initializeViews(view);
        configureAdapters();
        configureSelections();
        observeObjectiveValueState();
        configureActions(view);
        loadEditingItem();
    }

    private void initializeViews(@NonNull View view) {
        tvTitle = view.findViewById(R.id.tv_objective_sheet_title);
        tvSubtitle = view.findViewById(R.id.tv_objective_sheet_subtitle);
        tvValueError = view.findViewById(R.id.tv_objective_value_error);

        objectiveInputLayout = view.findViewById(R.id.layout_objective);
        objectiveValueInputLayout = view.findViewById(R.id.layout_objective_value);

        muscleJointInputLayout = view.findViewById(R.id.layout_muscle_joint);
        notesInputLayout = view.findViewById(R.id.layout_objective_notes);

        objectiveAutoComplete = view.findViewById(R.id.actv_objective);
        objectiveValueAutoComplete = view.findViewById(R.id.actv_objective_value);

        muscleJointAutoComplete = view.findViewById(R.id.actv_muscle_joint);
        notesEditText = view.findViewById(R.id.et_objective_notes);

        valueLoadingLayout = view.findViewById(R.id.layout_objective_value_loading);
        valueErrorLayout = view.findViewById(R.id.layout_objective_value_error);

        btnRetryValue = view.findViewById(R.id.btn_retry_objective_value);
        btnSave = view.findViewById(R.id.btn_save_objective);

        configureObjectiveValueAsDropdownOnly();
        setObjectiveValueEnabled(false);
    }

    private void configureAdapters() {
        objectiveAdapter = new ClinicalOptionAdapter(requireContext());
        objectiveValueAdapter = new ClinicalOptionAdapter(requireContext());
        muscleJointAdapter = new ClinicalOptionAdapter(requireContext());

        objectiveItems.clear();
        objectiveItems.addAll(viewModel.getCurrentObjectiveOptions());

        muscleJointItems.clear();
        muscleJointItems.addAll(viewModel.getCurrentMuscleJointOptions());

        objectiveAdapter.submitList(objectiveItems);
        objectiveValueAdapter.submitList(objectiveValueItems);
        muscleJointAdapter.submitList(muscleJointItems);

        objectiveAutoComplete.setAdapter(objectiveAdapter);
        objectiveValueAutoComplete.setAdapter(objectiveValueAdapter);
        muscleJointAutoComplete.setAdapter(muscleJointAdapter);
    }

    private void configureSelections() {
        configureObjectiveSelection();
        configureObjectiveValueSelection();
        configureMuscleJointSelection();
        configureNotesInput();
    }

    private void configureObjectiveSelection() {
        objectiveAutoComplete.setOnClickListener(view -> {
            if (!objectiveItems.isEmpty()) {
                objectiveAutoComplete.showDropDown();
            }
        });

        objectiveAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);
            if (!(selectedItem instanceof ClinicalOption)) {
                return;
            }

            selectObjective((ClinicalOption) selectedItem);
            objectiveAutoComplete.post(() -> objectiveAutoComplete.setSelection(objectiveAutoComplete.length()));
            objectiveAutoComplete.clearFocus();
            hideKeyboard(objectiveAutoComplete);
        });

        objectiveAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                clearInputError(objectiveInputLayout);
                String currentText = text == null ? "" : text.toString().trim();

                if (selectedObjective != null && selectedObjective.getOptionName().equalsIgnoreCase(currentText)) {
                    return;
                }

                ClinicalOption exactMatch = findExactMatch(objectiveItems, currentText);

                if (exactMatch != null) {
                    selectObjective(exactMatch);
                    return;
                }

                if (selectedObjective != null) {
                    clearSelectedObjective();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void configureObjectiveValueSelection() {
        objectiveValueAutoComplete.setOnClickListener(view -> {
            hideKeyboard(view);
            if (objectiveValueAutoComplete.isEnabled() && !objectiveValueItems.isEmpty()) {
                objectiveValueAutoComplete.showDropDown();
            }
        });

        objectiveValueAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);
            if (!(selectedItem instanceof ClinicalOption)) {
                return;
            }

            selectedObjectiveValue = (ClinicalOption) selectedItem;
            clearInputError(objectiveValueInputLayout);
            clearInputError(notesInputLayout);
            objectiveValueAutoComplete.clearFocus();
            hideKeyboard(objectiveValueAutoComplete);
            objectiveValueAutoComplete.post(() -> objectiveValueAutoComplete.setSelection(objectiveValueAutoComplete.length()));
        });

        objectiveValueAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                clearInputError(objectiveValueInputLayout);
                if (selectedObjectiveValue == null) {
                    return;
                }

                String currentText = text == null ? "" : text.toString().trim();

                if (!selectedObjectiveValue.getOptionName().equalsIgnoreCase(currentText)) {
                    selectedObjectiveValue = null;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void configureMuscleJointSelection() {
        muscleJointAutoComplete.setOnClickListener(view -> {
            if (!muscleJointItems.isEmpty()) {
                muscleJointAutoComplete.showDropDown();
            }
        });

        muscleJointAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);
            if (!(selectedItem instanceof ClinicalOption)) {
                return;
            }

            selectedMuscleJoint = (ClinicalOption) selectedItem;
            clearInputError(muscleJointInputLayout);
            muscleJointAutoComplete.clearFocus();
            hideKeyboard(muscleJointAutoComplete);
            muscleJointAutoComplete.post(() -> muscleJointAutoComplete.setSelection(muscleJointAutoComplete.length()));
        });

        muscleJointAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                clearInputError(muscleJointInputLayout);
                if (selectedMuscleJoint == null) {
                    return;
                }

                String currentText = text == null ? "" : text.toString().trim();

                if (!selectedMuscleJoint.getOptionName().equalsIgnoreCase(currentText)) {
                    selectedMuscleJoint = null;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void configureNotesInput() {
        notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                clearInputError(notesInputLayout);

                String currentText = text == null ? "" : text.toString().trim();

                if (!currentText.isEmpty()) {
                    clearInputError(objectiveValueInputLayout);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void selectObjective(@NonNull ClinicalOption objective) {
        boolean objectiveChanged = selectedObjective == null || isDifferentOption(selectedObjective, objective);

        selectedObjective = objective;
        clearInputError(objectiveInputLayout);

        if (!objectiveChanged) {
            return;
        }

        if (editingItem == null || !editingItem.getObjectiveId().equals(objective.getOptionId())) {
            pendingEditingValueId = null;
        }

        clearSelectedObjectiveValue();
        viewModel.loadObjectiveValueOptions(objective.getOptionId());
    }

    private void clearSelectedObjective() {
        selectedObjective = null;
        pendingEditingValueId = null;

        clearSelectedObjectiveValue();
        viewModel.clearObjectiveValueSelection();
    }

    private void clearSelectedObjectiveValue() {
        selectedObjectiveValue = null;

        objectiveValueItems.clear();

        if (objectiveValueAdapter != null) {
            objectiveValueAdapter.submitList(objectiveValueItems);
        }

        objectiveValueAutoComplete.setText("", false);
        clearInputError(objectiveValueInputLayout);

        setObjectiveValueEnabled(false);
    }

    private void observeObjectiveValueState() {
        viewModel.getObjectiveValueOptions().observe(getViewLifecycleOwner(), this::updateObjectiveValueOptions);
        viewModel.getObjectiveValueLoading().observe(getViewLifecycleOwner(), this::updateObjectiveValueLoading);
        viewModel.getObjectiveValueError().observe(getViewLifecycleOwner(), this::updateObjectiveValueError);
    }

    private void updateObjectiveValueOptions(@Nullable ArrayList<ClinicalOption> options) {
        objectiveValueItems.clear();
        if (options != null) {
            objectiveValueItems.addAll(options);
        }

        objectiveValueAdapter.submitList(objectiveValueItems);

        selectedObjectiveValue = null;
        objectiveValueAutoComplete.setText("", false);

        restoreEditingObjectiveValueIfPossible();
        updateObjectiveValueEnabledState();
    }

    private void updateObjectiveValueLoading(@Nullable Boolean loadingValue) {
        boolean loading = Boolean.TRUE.equals(loadingValue);
        valueLoadingLayout.setVisibility(loading ? View.VISIBLE : View.GONE);

        btnSave.setEnabled(!loading);
        btnSave.setAlpha(loading ? 0.55f : 1.0f);

        updateObjectiveValueEnabledState();
    }

    private void updateObjectiveValueError(@Nullable String errorMessage) {
        String validError = errorMessage == null ? "" : errorMessage.trim();
        boolean hasError = !validError.isEmpty();

        valueErrorLayout.setVisibility(hasError ? View.VISIBLE : View.GONE);

        if (hasError) {
            tvValueError.setText(validError);
        }

        updateObjectiveValueEnabledState();
    }

    private void updateObjectiveValueEnabledState() {
        boolean loading = Boolean.TRUE.equals(viewModel.getObjectiveValueLoading().getValue());

        String errorMessage = viewModel.getObjectiveValueError().getValue();
        boolean hasError = errorMessage != null && !errorMessage.trim().isEmpty();
        boolean enabled = selectedObjective != null && !loading && !hasError && !objectiveValueItems.isEmpty();
        setObjectiveValueEnabled(enabled);
    }

    private void configureObjectiveValueAsDropdownOnly() {
        /*
         * Field focus পেলেও keyboard খুলবে না।
         */
        objectiveValueAutoComplete.setShowSoftInputOnFocus(false);
        objectiveValueAutoComplete.setCursorVisible(false);
        objectiveValueAutoComplete.setKeyListener(null);

        objectiveValueAutoComplete.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                hideKeyboard(view);
            }
        });
    }

    private void hideKeyboard(@NonNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setObjectiveValueEnabled(boolean enabled) {
        objectiveValueInputLayout.setEnabled(enabled);
        objectiveValueAutoComplete.setEnabled(enabled);
    }

    private void restoreEditingObjectiveValueIfPossible() {
        if (editingItem == null || pendingEditingValueId == null || selectedObjective == null || !editingItem.getObjectiveId().equals(selectedObjective.getOptionId())) {
            return;
        }

        ClinicalOption valueOption = findOptionById(objectiveValueItems, pendingEditingValueId);

        if (valueOption == null) {
            return;
        }

        selectedObjectiveValue = valueOption;
        objectiveValueAutoComplete.setText(valueOption.getOptionName(), false);

        objectiveValueAutoComplete.setSelection(objectiveValueAutoComplete.length());
        pendingEditingValueId = null;
    }

    private void configureActions(@NonNull View view) {
        view.findViewById(R.id.btn_close_objective_sheet).setOnClickListener(button -> dismiss());
        view.findViewById(R.id.btn_cancel_objective).setOnClickListener(button -> dismiss());

        btnRetryValue.setOnClickListener(button -> viewModel.retryObjectiveValueOptions());

        btnSave.setOnClickListener(button -> saveObjectiveInfo());
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

        editingItem = viewModel.findObjectiveInfoById(localEntryId);

        if (editingItem == null) {
            dismiss();
            return;
        }

        tvTitle.setText("Edit Objective Info");
        tvSubtitle.setText("Update the selected objective details");

        btnSave.setText("UPDATE OBJECTIVE");

        pendingEditingValueId = editingItem.hasObjectiveValue() ? editingItem.getObjectiveValueId() : null;

        ClinicalOption objectiveOption = findOptionById(objectiveItems, editingItem.getObjectiveId());

        if (objectiveOption != null) {
            selectObjective(objectiveOption);

            objectiveAutoComplete.setText(objectiveOption.getOptionName(), false);
            objectiveAutoComplete.setSelection(objectiveAutoComplete.length());

        } else {
            objectiveAutoComplete.setText(editingItem.getObjectiveName(), false);
            objectiveAutoComplete.setSelection(objectiveAutoComplete.length());
        }

        ClinicalOption muscleJointOption = findOptionById(muscleJointItems, editingItem.getMuscleJointId());
        selectedMuscleJoint = muscleJointOption;

        if (muscleJointOption != null) {
            muscleJointAutoComplete.setText(muscleJointOption.getOptionName(), false);
            muscleJointAutoComplete.setSelection(muscleJointAutoComplete.length());

        } else {
            muscleJointAutoComplete.setText(editingItem.getMuscleJointName(), false);
            muscleJointAutoComplete.setSelection(muscleJointAutoComplete.length());
        }

        notesEditText.setText(editingItem.getNotes());
        notesEditText.setSelection(notesEditText.length());
    }

//    private void saveObjectiveInfo() {
//        clearInputErrors();
//
//        String objectiveText = getText(objectiveAutoComplete);
//
//        if (objectiveText.isEmpty()) {
//            showInputError(objectiveInputLayout,"Select an objective");
//            objectiveAutoComplete.requestFocus();
//            return;
//        }
//
//        ClinicalOption exactObjective = findExactMatch(objectiveItems, objectiveText);
//
//        if (exactObjective == null) {
//            selectedObjective = null;
//
//            showInputError(objectiveInputLayout, "Select a valid objective from the list");
//            objectiveAutoComplete.requestFocus();
//            return;
//        }
//
//        if (selectedObjective == null || isDifferentOption(selectedObjective, exactObjective)) {
//            selectObjective(exactObjective);
//        }
//
//        boolean valueLoading = Boolean.TRUE.equals(viewModel.getObjectiveValueLoading().getValue());
//
//        if (valueLoading) {
//            showInputError(objectiveValueInputLayout, "Please wait for the values to load");
//            return;
//        }
//
//        String objectiveValueText = getText(objectiveValueAutoComplete);
//
//        ClinicalOption exactObjectiveValue = findExactMatch(objectiveValueItems, objectiveValueText);
//
//        if (objectiveValueText.isEmpty() || exactObjectiveValue == null) {
//
//            selectedObjectiveValue = null;
//
//            if (objectiveValueItems.isEmpty()) {
//                showInputError(objectiveValueInputLayout,"No value is available for this objective");
//            } else {
//                showInputError(objectiveValueInputLayout,"Select a value from the list");
//            }
//
//            return;
//        }
//
//        selectedObjectiveValue = exactObjectiveValue;
//        String muscleJointText = getText(muscleJointAutoComplete);
//
//        if (muscleJointText.isEmpty()) {
//            selectedMuscleJoint = null;
//
//        } else {
//            ClinicalOption exactMuscleJoint = findExactMatch(muscleJointItems, muscleJointText);
//
//            if (exactMuscleJoint == null) {
//                selectedMuscleJoint = null;
//                showInputError(muscleJointInputLayout, "Select a valid Muscle / Joint from the list");
//                muscleJointAutoComplete.requestFocus();
//                return;
//            }
//
//            selectedMuscleJoint = exactMuscleJoint;
//        }
//
//        String notes = getText(notesEditText);
//
//        ObjectiveInfoItem itemToSave;
//
//        if (editingItem == null) {
//            itemToSave = ObjectiveInfoItem.fromSelections(selectedObjective, selectedObjectiveValue, selectedMuscleJoint, notes);
//
//        } else {
//            itemToSave = editingItem.updateFromSelections(selectedObjective, selectedObjectiveValue, selectedMuscleJoint, notes);
//        }
//
//        boolean successful;
//
//        if (editingItem == null) {
//            successful = viewModel.addObjectiveInfo(itemToSave);
//
//        } else {
//            successful = viewModel.updateObjectiveInfo(itemToSave);
//        }
//
//        if (!successful) {
//            showInputError(objectiveInputLayout, "This objective information has already been added");
//            return;
//        }
//
//        dismiss();
//    }

    private void saveObjectiveInfo() {
        clearInputErrors();

        String objectiveText = getText(objectiveAutoComplete);

        if (objectiveText.isEmpty()) {
            showInputError(objectiveInputLayout, "Select an objective");
            objectiveAutoComplete.requestFocus();
            return;
        }

        ClinicalOption exactObjective = findExactMatch(objectiveItems, objectiveText);

        if (exactObjective == null) {
            selectedObjective = null;

            showInputError(objectiveInputLayout, "Select a valid objective from the list");

            objectiveAutoComplete.requestFocus();
            return;
        }

        if (selectedObjective == null || isDifferentOption(selectedObjective, exactObjective)) {
            selectObjective(exactObjective);
        }

        boolean valueLoading = Boolean.TRUE.equals(viewModel.getObjectiveValueLoading().getValue());

        if (valueLoading) {
            showInputError(objectiveValueInputLayout, "Please wait for the values to load");
            return;
        }

        String objectiveValueText = getText(objectiveValueAutoComplete);
        String notes = getText(notesEditText);

        ClinicalOption exactObjectiveValue = null;

        /*
         * Value লেখা/selected থাকলে অবশ্যই master list-এর
         * valid option হতে হবে।
         */
        if (!objectiveValueText.isEmpty()) {
            exactObjectiveValue = findExactMatch(objectiveValueItems, objectiveValueText);

            if (exactObjectiveValue == null) {
                selectedObjectiveValue = null;
                showInputError(objectiveValueInputLayout, "Select a valid value from the list");
                return;
            }
        }

        /*
         * Objective Value এবং Notes—দুটিই empty হলে
         * item save করা যাবে না।
         */
        if (exactObjectiveValue == null && notes.isEmpty()) {
            selectedObjectiveValue = null;

            if (objectiveValueItems.isEmpty()) {
                showInputError(objectiveValueInputLayout, "No predefined value is available");

            } else {
                showInputError(objectiveValueInputLayout, "Select a value or enter notes");
            }

            showInputError(notesInputLayout, "Enter notes if no value is selected");

            notesEditText.requestFocus();
            return;
        }

        /*
         * Notes থাকলে Objective Value null হতে পারবে।
         */
        selectedObjectiveValue = exactObjectiveValue;

        String muscleJointText = getText(muscleJointAutoComplete);

        if (muscleJointText.isEmpty()) {
            selectedMuscleJoint = null;

        } else {
            ClinicalOption exactMuscleJoint = findExactMatch(muscleJointItems, muscleJointText);

            if (exactMuscleJoint == null) {
                selectedMuscleJoint = null;
                showInputError(muscleJointInputLayout, "Select a valid Muscle / Joint from the list");
                muscleJointAutoComplete.requestFocus();
                return;
            }

            selectedMuscleJoint = exactMuscleJoint;
        }

        ObjectiveInfoItem itemToSave;

        if (editingItem == null) {
            itemToSave = ObjectiveInfoItem.fromSelections(selectedObjective, selectedObjectiveValue, selectedMuscleJoint, notes);

        } else {
            itemToSave = editingItem.updateFromSelections(selectedObjective, selectedObjectiveValue, selectedMuscleJoint, notes);
        }

        boolean successful;

        if (editingItem == null) {
            successful = viewModel.addObjectiveInfo(itemToSave);

        } else {
            successful = viewModel.updateObjectiveInfo(itemToSave);
        }

        if (!successful) {
            showInputError(objectiveInputLayout, "This objective information has already been added");
            return;
        }

        dismiss();
    }

    private void clearInputErrors() {
        clearInputError(objectiveInputLayout);
        clearInputError(objectiveValueInputLayout);
        clearInputError(muscleJointInputLayout);
        clearInputError(notesInputLayout);
    }

    @NonNull
    private String getText(@NonNull TextView textView) {
        return textView.getText() == null ? "" : textView.getText().toString().trim();
    }

    @Nullable
    private ClinicalOption findExactMatch(@NonNull ArrayList<ClinicalOption> options, @NonNull String value) {
        if (value.trim().isEmpty()) {
            return null;
        }

        for (ClinicalOption option : options) {
            if (option.getOptionName().equalsIgnoreCase(value.trim())) {
                return option;
            }
        }

        return null;
    }

    @Nullable
    private ClinicalOption findOptionById(@NonNull ArrayList<ClinicalOption> options, @Nullable String optionId) {
        if (optionId == null || optionId.trim().isEmpty()) {
            return null;
        }

        for (ClinicalOption option : options) {
            if (option.getOptionId().equals(optionId.trim())) {
                return option;
            }
        }

        return null;
    }

    private boolean isDifferentOption(@NonNull ClinicalOption firstOption, @NonNull ClinicalOption secondOption) {
        String firstId = firstOption.getOptionId().trim();
        String secondId = secondOption.getOptionId().trim();
        if (!firstId.isEmpty() && !secondId.isEmpty()) {
            return !firstId.equals(secondId);
        }

        return !firstOption.getOptionName().equalsIgnoreCase(secondOption.getOptionName());
    }

    private void clearInputError(@NonNull TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    private void showInputError(@NonNull TextInputLayout inputLayout, @NonNull String errorMessage) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(errorMessage);
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
        if (viewModel != null) {
            /*
             * Current field state clear হবে,
             * কিন্তু HashMap cache clear হবে না।
             */
            viewModel.clearObjectiveValueSelection();
        }

        super.onDestroyView();

        tvTitle = null;
        tvSubtitle = null;
        tvValueError = null;

        objectiveInputLayout = null;
        objectiveValueInputLayout = null;
        muscleJointInputLayout = null;
        notesInputLayout = null;

        objectiveAutoComplete = null;
        objectiveValueAutoComplete = null;
        muscleJointAutoComplete = null;
        notesEditText = null;

        valueLoadingLayout = null;
        valueErrorLayout = null;

        btnRetryValue = null;
        btnSave = null;

        objectiveAdapter = null;
        objectiveValueAdapter = null;
        muscleJointAdapter = null;

        selectedObjective = null;
        selectedObjectiveValue = null;
        selectedMuscleJoint = null;

        editingItem = null;
        pendingEditingValueId = null;
    }
}