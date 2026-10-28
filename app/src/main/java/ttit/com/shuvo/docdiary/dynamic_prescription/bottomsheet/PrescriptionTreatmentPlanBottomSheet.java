package ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.ClinicalOptionAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalOption;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.TreatmentPlanEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionTreatmentPlanBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionTreatmentPlanBottomSheet";
    private static final String ARG_EDIT_ENTRY_ID = "edit_entry_id";

    private PrescriptionBuilderViewModel viewModel;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextView tvHelper;

    private AppCompatImageView ivHeaderIcon;

    private TextInputLayout treatmentPlanInputLayout;
    private MaterialAutoCompleteTextView treatmentPlanAutoComplete;

    private MaterialButton btnSave;

    private ClinicalOptionAdapter optionAdapter;
    private final ArrayList<ClinicalOption> optionItems = new ArrayList<>();

    private ClinicalOption selectedOption;
    private ClinicalOption lastSelectedMasterOption;
    private TreatmentPlanEntryItem editingItem;

    @NonNull
    public static PrescriptionTreatmentPlanBottomSheet newAddInstance() {
        return new PrescriptionTreatmentPlanBottomSheet();
    }

    @NonNull
    public static PrescriptionTreatmentPlanBottomSheet newEditInstance(@NonNull String localEntryId) {
        PrescriptionTreatmentPlanBottomSheet sheet = new PrescriptionTreatmentPlanBottomSheet();
        Bundle arguments = new Bundle();
        arguments.putString(ARG_EDIT_ENTRY_ID, localEntryId);
        sheet.setArguments(arguments);

        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_prescription_clinical_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);

        initializeViews(view);
        configureUi(view);
        configureOptionAdapter();
        configureSelection();
        configureActions(view);
        loadEditingItem();
    }

    private void initializeViews(@NonNull View view) {
        tvTitle = view.findViewById(R.id.tv_entry_sheet_title);
        tvSubtitle = view.findViewById(R.id.tv_entry_sheet_subtitle);
        tvHelper = view.findViewById(R.id.tv_entry_helper);

        ivHeaderIcon = view.findViewById(R.id.iv_entry_header_icon);

        treatmentPlanInputLayout = view.findViewById(R.id.layout_clinical_entry);
        treatmentPlanAutoComplete = view.findViewById(R.id.actv_clinical_entry);

        btnSave = view.findViewById(R.id.btn_save_clinical_entry);
    }

    @SuppressLint("SetTextI18n")
    private void configureUi(@NonNull View view) {
        tvTitle.setText("Add Treatment Plan");
        tvSubtitle.setText("Search or enter treatment plan information");
        tvHelper.setText("Select from the list or enter a custom treatment plan");

        ivHeaderIcon.setImageResource(R.drawable.treatment_plan);

        treatmentPlanInputLayout.setHint("Treatment Plan");
        treatmentPlanInputLayout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);

        btnSave.setText("ADD TREATMENT PLAN");

        view.findViewById(R.id.btn_delete_clinical_entry).setVisibility(View.GONE);
    }

    private void configureOptionAdapter() {
        optionItems.clear();
        optionItems.addAll(viewModel.getCurrentTreatmentPlanOptions());

        optionAdapter = new ClinicalOptionAdapter(requireContext());
        optionAdapter.submitList(optionItems);

        treatmentPlanAutoComplete.setAdapter(optionAdapter);
    }

    private void configureSelection() {
        treatmentPlanAutoComplete.setOnClickListener(view -> {
            if (!optionItems.isEmpty()) {
                treatmentPlanAutoComplete.showDropDown();
            }
        });

        treatmentPlanAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);

            if (!(selectedItem instanceof ClinicalOption)) {
                return;
            }

            selectedOption = (ClinicalOption) selectedItem;
            lastSelectedMasterOption = selectedOption;

            treatmentPlanAutoComplete.setText(selectedOption.getOptionName(), false);
            clearInputError();

//            treatmentPlanAutoComplete.clearFocus();
//            hideKeyboard(treatmentPlanAutoComplete);

            treatmentPlanAutoComplete.post(() -> treatmentPlanAutoComplete.setSelection(treatmentPlanAutoComplete.length()));
        });

        treatmentPlanAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                clearInputError();

                if (selectedOption == null) {
                    return;
                }

                String currentText = text == null ? "" : text.toString().trim();

                if (!selectedOption.getOptionName().equalsIgnoreCase(currentText)) {
                    selectedOption = null;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void configureActions(@NonNull View view) {
        view.findViewById(R.id.btn_close_entry_sheet).setOnClickListener(button -> dismiss());
        view.findViewById(R.id.btn_cancel_clinical_entry).setOnClickListener(button -> dismiss());
        btnSave.setOnClickListener(button -> saveTreatmentPlan());
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

        editingItem = viewModel.findTreatmentPlanEntryById(localEntryId);

        if (editingItem == null) {
            dismiss();
            return;
        }

        tvTitle.setText("Edit Treatment Plan");
        tvSubtitle.setText("Update the selected treatment plan");
        btnSave.setText("UPDATE TREATMENT PLAN");

        selectedOption = findOptionByMasterId(editingItem.getMasterOptionId());
        lastSelectedMasterOption = selectedOption;

        treatmentPlanAutoComplete.setText(editingItem.getDisplayText(), false);
        treatmentPlanAutoComplete.setSelection(treatmentPlanAutoComplete.length());
    }

    private void saveTreatmentPlan() {
        clearInputError();

        String enteredText = getInputText();

        if (enteredText.isEmpty()) {
            showInputError("Enter or select treatment plan");
            return;
        }

        /*
         * Current text master list-এর কোনো value-এর
         * সঙ্গে exact match করলে সেই ID নেওয়া হবে।
         */
        ClinicalOption exactMatch = optionAdapter.findExactMatch(enteredText);

        if (exactMatch != null) {
            selectedOption = exactMatch;
            lastSelectedMasterOption = exactMatch;
        }

        /*
         * Current exact match না থাকলে সর্বশেষ
         * selected master option-এর ID retain হবে।
         */
        ClinicalOption masterOptionForSave = selectedOption;

        if (masterOptionForSave == null) {
            masterOptionForSave = lastSelectedMasterOption;
        }

        TreatmentPlanEntryItem itemToSave;

        if (editingItem == null) {
            if (masterOptionForSave != null) {
                itemToSave = TreatmentPlanEntryItem.fromMasterOption(masterOptionForSave, enteredText);
            } else {
                itemToSave = TreatmentPlanEntryItem.fromCustomText(enteredText);
            }

        } else {
            if (masterOptionForSave != null) {
                itemToSave = editingItem.updateFromMasterOption(masterOptionForSave, enteredText);
            } else {
                itemToSave = editingItem.updateFromCustomText(enteredText);
            }
        }

        boolean successful;

        if (editingItem == null) {
            successful = viewModel.addTreatmentPlan(itemToSave);
        } else {
            successful = viewModel.updateTreatmentPlan(itemToSave);
        }

        if (!successful) {
            showInputError("This treatment plan has already been added");
            return;
        }

        dismiss();
    }

    @Nullable
    private ClinicalOption findOptionByMasterId(@Nullable String masterOptionId) {
        if (masterOptionId == null || masterOptionId.trim().isEmpty()) {
            return null;
        }

        for (ClinicalOption option : optionItems) {
            if (option.getOptionId().equals(masterOptionId)) {
                return option;
            }
        }

        return null;
    }

    private void showInputError(@NonNull String errorMessage) {
        treatmentPlanInputLayout.setErrorEnabled(true);
        treatmentPlanInputLayout.setError(errorMessage);
        treatmentPlanAutoComplete.requestFocus();
    }

    private void clearInputError() {
        treatmentPlanInputLayout.setError(null);
        treatmentPlanInputLayout.setErrorEnabled(false);
    }

    @NonNull
    private String getInputText() {
        if (treatmentPlanAutoComplete.getText() == null) {
            return "";
        }

        return treatmentPlanAutoComplete.getText().toString().trim();
    }

//    private void hideKeyboard(@NonNull View view) {
//        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        if (inputMethodManager != null) {
//            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }

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
        tvHelper = null;

        ivHeaderIcon = null;

        treatmentPlanInputLayout = null;
        treatmentPlanAutoComplete = null;

        btnSave = null;
        optionAdapter = null;

        selectedOption = null;
        lastSelectedMasterOption = null;
        editingItem = null;

        optionItems.clear();
    }
}