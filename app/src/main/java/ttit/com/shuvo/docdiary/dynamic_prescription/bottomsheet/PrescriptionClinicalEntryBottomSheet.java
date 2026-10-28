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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.ClinicalOptionAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalEntryType;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalOption;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionClinicalEntryBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionClinicalEntryBottomSheet";
    private static final String ARG_EDIT_ENTRY_ID = "edit_entry_id";
    private static final String ARG_ENTRY_TYPE = "entry_type";

    private PrescriptionBuilderViewModel viewModel;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextView tvHelper;

    private AppCompatImageView ivHeaderIcon;

    private TextInputLayout entryInputLayout;
    private MaterialAutoCompleteTextView entryAutoComplete;

    private TextInputLayout notesInputLayout;
    private TextInputEditText notesEditText;

    private MaterialButton btnSave;
    private MaterialButton btnDelete;

    private ClinicalOptionAdapter optionAdapter;

    private final ArrayList<ClinicalOption> optionItems = new ArrayList<>();

    private ClinicalEntryType entryType = ClinicalEntryType.COMPLAIN;

    private ClinicalOption selectedOption;
    private ClinicalOption lastSelectedMasterOption;
    private ClinicalEntryItem editingItem;

    // =====================================================
    // Generic factory methods
    // =====================================================

    @NonNull
    public static PrescriptionClinicalEntryBottomSheet newAddInstance(@NonNull ClinicalEntryType entryType) {
        return newInstance(entryType, null);
    }

    @NonNull
    public static PrescriptionClinicalEntryBottomSheet newEditInstance(@NonNull ClinicalEntryType entryType, @NonNull String localEntryId) {
        return newInstance(entryType, localEntryId);
    }

    @NonNull
    private static PrescriptionClinicalEntryBottomSheet newInstance(@NonNull ClinicalEntryType entryType, @Nullable String localEntryId) {
        PrescriptionClinicalEntryBottomSheet sheet = new PrescriptionClinicalEntryBottomSheet();
        Bundle arguments = new Bundle();
        arguments.putString(ARG_ENTRY_TYPE, entryType.getArgumentValue());
        if (localEntryId != null) {
            arguments.putString(ARG_EDIT_ENTRY_ID, localEntryId);
        }
        sheet.setArguments(arguments);

        return sheet;
    }

    // =====================================================
    // Fragment lifecycle
    // =====================================================

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_prescription_clinical_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);

        readEntryType();
        initializeViews(view);
        configureEntryUi();
        configureOptionAdapter();
        configureEntrySelection();
        configureActions(view);
        loadEditingItem();
    }

    private void readEntryType() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            entryType = ClinicalEntryType.COMPLAIN;
            return;
        }

        entryType = ClinicalEntryType.fromArgument(arguments.getString(ARG_ENTRY_TYPE));
    }

    private void initializeViews(@NonNull View view) {
        tvTitle = view.findViewById(R.id.tv_entry_sheet_title);
        tvSubtitle = view.findViewById(R.id.tv_entry_sheet_subtitle);
        tvHelper = view.findViewById(R.id.tv_entry_helper);

        ivHeaderIcon = view.findViewById(R.id.iv_entry_header_icon);
        entryInputLayout = view.findViewById(R.id.layout_clinical_entry);
        entryAutoComplete = view.findViewById(R.id.actv_clinical_entry);

        notesInputLayout = view.findViewById(R.id.layout_entry_notes);
        notesEditText = view.findViewById(R.id.et_entry_notes);

        btnSave = view.findViewById(R.id.btn_save_clinical_entry);
        btnDelete = view.findViewById(R.id.btn_delete_clinical_entry);
    }

    // =====================================================
    // Dynamic UI
    // =====================================================
    @SuppressLint("SetTextI18n")
    private void configureEntryUi() {
        String displayName = entryType.getDisplayName();
        String entryName = entryType.getDisplayName().toLowerCase(Locale.ENGLISH);
        tvTitle.setText("Add " + displayName);

        ivHeaderIcon.setImageResource(entryType.getIconResource());
        entryInputLayout.setHint(displayName);
        btnSave.setText("ADD " + displayName.toUpperCase(Locale.ENGLISH));

        configureInputModeText(entryName);

        btnDelete.setVisibility(View.GONE);

        boolean isInvestigation = entryType == ClinicalEntryType.TESTS_INVESTIGATION;

        notesInputLayout.setVisibility(isInvestigation ? View.VISIBLE : View.GONE);

        notesEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    notesEditText.clearFocus();
                    hideKeyboard(notesEditText);

                    return false; // consume.
                }
            }
            return true;
        });
    }

    @SuppressLint("SetTextI18n")
    private void configureInputModeText(@NonNull String entryName) {
        switch (entryType.getInputMode()) {
            case SEARCHABLE_CUSTOM:
                tvSubtitle.setText("Search or enter " + entryName + " information");
                tvHelper.setText("Select from the list or enter a custom " + entryName);
                entryInputLayout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
                break;

            case SEARCHABLE_STRICT:
                tvSubtitle.setText("Search and select " + entryName);
                tvHelper.setText("Select " + entryName + " from the available list");
                entryInputLayout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
                break;

            case TEXT_ONLY:
                tvSubtitle.setText("Enter " + entryName + " information");
                tvHelper.setText("Write the required information");
                entryInputLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
                break;
        }
    }

    // =====================================================
    // Master option list
    // =====================================================
    private void configureOptionAdapter() {
        optionItems.clear();
        if (!entryType.hasMasterOptions()) {
            optionAdapter = null;
            entryAutoComplete.setAdapter(null);
            return;
        }
        optionAdapter = new ClinicalOptionAdapter(requireContext());
        optionItems.addAll(viewModel.getCurrentClinicalOptions(entryType));

        optionAdapter.submitList(optionItems);
        entryAutoComplete.setAdapter(optionAdapter);
    }

    private void configureEntrySelection() {
        if (entryType.hasMasterOptions()) {
            entryAutoComplete.setOnClickListener(view -> {
                if (!optionItems.isEmpty()) {
                    entryAutoComplete.showDropDown();
                }
            });

            entryAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                Object selectedItem = parent.getItemAtPosition(position);
                if (!(selectedItem instanceof ClinicalOption)) {
                    return;
                }

                selectedOption = (ClinicalOption) selectedItem;
                lastSelectedMasterOption = selectedOption;
                entryInputLayout.setError(null);
                entryAutoComplete.post(() -> entryAutoComplete.setSelection(entryAutoComplete.length()));
            });
        }

        entryAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                entryInputLayout.setError(null);
                if (selectedOption == null) {
                    return;
                }

                String currentText = s == null ? "" : s.toString().trim();

                if (!selectedOption.getOptionName().equalsIgnoreCase(currentText)) {
                    selectedOption = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        entryAutoComplete.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    entryAutoComplete.clearFocus();
                    hideKeyboard(entryAutoComplete);

                    return false; // consume.
                }
            }
            return true;
        });
    }

    // =====================================================
    // Actions
    // =====================================================
    private void configureActions(@NonNull View view) {
        view.findViewById(R.id.btn_close_entry_sheet).setOnClickListener(button -> dismiss());
        view.findViewById(R.id.btn_cancel_clinical_entry).setOnClickListener(button -> dismiss());

        btnSave.setOnClickListener(button -> saveEntry());
    }

    // =====================================================
    // Edit existing entry
    // =====================================================
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

        editingItem = viewModel.findClinicalEntryById(entryType, localEntryId);

        if (editingItem == null) {
            dismiss();
            return;
        }

        String displayName = entryType.getDisplayName();
        String entryName = entryType.getDisplayName().toLowerCase(Locale.ENGLISH);

        tvTitle.setText("Edit " + displayName);
        tvSubtitle.setText("Update the selected " + entryName + " information");
        btnSave.setText("UPDATE " + displayName.toUpperCase(Locale.ENGLISH));

        selectedOption = findOptionByMasterId(editingItem.getMasterOptionId());
        lastSelectedMasterOption = selectedOption;
        entryAutoComplete.setText(editingItem.getDisplayText(), false);
        entryAutoComplete.setSelection(entryAutoComplete.length());
        if (entryType == ClinicalEntryType.TESTS_INVESTIGATION) {
            notesEditText.setText(editingItem.getNotes());
        }
    }

    // =====================================================
    // Save
    // =====================================================
    private void saveEntry() {
        entryInputLayout.setError(null);
        String enteredText = entryAutoComplete.getText() == null ? "" : entryAutoComplete.getText().toString().trim();
        String notes = entryType == ClinicalEntryType.TESTS_INVESTIGATION ? getNotesText() : "";
        String entryName = entryType.getDisplayName().toLowerCase(Locale.ENGLISH);

        if (enteredText.isEmpty()) {
            if (entryType.hasMasterOptions()) {
                entryInputLayout.setError("Enter or select " + entryName);
            } else {
                entryInputLayout.setError("Enter " + entryName);
            }
            entryAutoComplete.requestFocus();
            return;
        }

        /*
         * Current text কোনো master option-এর
         * সঙ্গে exact match করে কি না দেখবে।
         */
        ClinicalOption exactMatch = null;
        if (optionAdapter != null) {
            exactMatch = optionAdapter.findExactMatch(enteredText);
        }

        /*
         * Exact match সবসময় আগের retained
         * master option-কে override করবে।
         */
        if (exactMatch != null) {
            selectedOption = exactMatch;
            lastSelectedMasterOption = exactMatch;
        }

        /*
         * প্রথম priority current exact option।
         */
        ClinicalOption masterOptionForSave = selectedOption;

        /*
         * SEARCHABLE_CUSTOM field হলে current text
         * exact match না করলেও আগে selected master
         * option-এর ID retain করবে।
         */
        if (masterOptionForSave == null && entryType.allowsCustomEntry()) {
            masterOptionForSave = lastSelectedMasterOption;
        }

        /*
         * SEARCHABLE_STRICT field-এ modified/custom
         * value save করা যাবে না।
         */
        if (!entryType.allowsCustomEntry() && masterOptionForSave == null) {
            entryInputLayout.setError("Select a valid " + entryName + " from the list");
            entryAutoComplete.requestFocus();
            return;
        }

        ClinicalEntryItem itemToSave;
        if (editingItem == null) {
            if (masterOptionForSave != null) {
                itemToSave = ClinicalEntryItem.fromMasterOption(masterOptionForSave, enteredText, notes);
            } else {
                itemToSave = ClinicalEntryItem.fromCustomText(enteredText, notes);
            }

        } else {
            if (masterOptionForSave != null) {
                itemToSave = editingItem.updateFromMasterOption(masterOptionForSave, enteredText, notes);
            } else {
                itemToSave = editingItem.updateFromCustomText(enteredText, notes);
            }
        }

        boolean successful;

        if (editingItem == null) {
            successful = viewModel.addClinicalEntry(entryType, itemToSave);
        } else {
            successful = viewModel.updateClinicalEntry(entryType, itemToSave);
        }

        if (!successful) {
            entryInputLayout.setError("This " + entryName + " has already been added");
            return;
        }

        dismiss();
    }

    // =====================================================
    // Helpers
    // =====================================================
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

    private void hideKeyboard(@NonNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @NonNull
    private String getNotesText() {
        if (notesEditText == null || notesEditText.getText() == null) {
            return "";
        }

        return notesEditText.getText().toString().trim();
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

        tvTitle = null;
        tvSubtitle = null;
        tvHelper = null;

        ivHeaderIcon = null;

        entryInputLayout = null;
        entryAutoComplete = null;

        btnSave = null;
        btnDelete = null;

        optionAdapter = null;
        selectedOption = null;
        lastSelectedMasterOption = null;
        editingItem = null;

        notesInputLayout = null;
        notesEditText = null;
        viewModel = null;
    }
}