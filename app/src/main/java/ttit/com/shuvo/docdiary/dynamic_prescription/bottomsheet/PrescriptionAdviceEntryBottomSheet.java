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
import android.view.inputmethod.EditorInfo;
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
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.ClinicalOptionAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryType;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalOption;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionAdviceEntryBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionAdviceEntryBottomSheet";
    private static final String ARG_ENTRY_TYPE = "entry_type";
    private static final String ARG_EDIT_ENTRY_ID = "edit_entry_id";

    private PrescriptionBuilderViewModel viewModel;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextView tvHelper;

    private AppCompatImageView ivHeaderIcon;

    private TextInputLayout entryInputLayout;
    private MaterialAutoCompleteTextView entryAutoComplete;

    private MaterialButton btnSave;
    private MaterialButton btnDelete;

    private ClinicalOptionAdapter optionAdapter;

    private final ArrayList<ClinicalOption> optionItems = new ArrayList<>();
    private AdviceEntryType entryType = AdviceEntryType.ADVICE;

    /*
     * Current text যদি selected option-এর exact
     * text হয়, তাহলে এটি selectedOption-এ থাকবে।
     */
    private ClinicalOption selectedOption;

    /*
     * Master list থেকে শেষবার select করা option।
     * User পরে text customize করলেও এর ID রাখা হবে।
     */
    private ClinicalOption lastSelectedMasterOption;

    private AdviceEntryItem editingItem;

    // =====================================================
    // Factory methods
    // =====================================================

    @NonNull
    public static PrescriptionAdviceEntryBottomSheet newAddInstance(@NonNull AdviceEntryType entryType) {
        return newInstance(entryType, null);
    }

    @NonNull
    public static PrescriptionAdviceEntryBottomSheet newEditInstance(@NonNull AdviceEntryType entryType, @NonNull String localEntryId) {
        return newInstance(entryType, localEntryId);
    }

    @NonNull
    private static PrescriptionAdviceEntryBottomSheet newInstance(@NonNull AdviceEntryType entryType, @Nullable String localEntryId) {
        PrescriptionAdviceEntryBottomSheet sheet = new PrescriptionAdviceEntryBottomSheet();
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
            entryType = AdviceEntryType.ADVICE;
            return;
        }
        entryType = AdviceEntryType.fromArgument(arguments.getString(ARG_ENTRY_TYPE));
    }

    private void initializeViews(@NonNull View view) {
        tvTitle = view.findViewById(R.id.tv_entry_sheet_title);
        tvSubtitle = view.findViewById(R.id.tv_entry_sheet_subtitle);
        tvHelper = view.findViewById(R.id.tv_entry_helper);
        ivHeaderIcon = view.findViewById(R.id.iv_entry_header_icon);
        entryInputLayout = view.findViewById(R.id.layout_clinical_entry);
        entryAutoComplete = view.findViewById(R.id.actv_clinical_entry);

        btnSave = view.findViewById(R.id.btn_save_clinical_entry);
        btnDelete = view.findViewById(R.id.btn_delete_clinical_entry);
    }

    // =====================================================
    // Dynamic UI
    // =====================================================

    @SuppressLint("SetTextI18n")
    private void configureEntryUi() {
        String displayName = entryType.getDisplayName();
        String entryName = displayName.toLowerCase(Locale.ENGLISH);

        tvTitle.setText("Add " + displayName);

        ivHeaderIcon.setImageResource(entryType.getIconResource());

        entryInputLayout.setHint(displayName);
        btnSave.setText("ADD " + displayName.toUpperCase(Locale.ENGLISH));

        configureInputModeText(entryName);

        /*
         * Delete section list-এর বাইরে থেকেই হবে।
         */
        btnDelete.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void configureInputModeText(@NonNull String entryName) {
        switch (entryType.getInputMode()) {
            case SEARCHABLE_CUSTOM:
                tvSubtitle.setText("Search or enter " + entryName + " information");
                tvHelper.setText("Select from the list or enter a custom " + entryName);

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
    // Master options
    // =====================================================

    private void configureOptionAdapter() {
        optionItems.clear();

        if (!entryType.hasMasterOptions()) {
            optionAdapter = null;
            entryAutoComplete.setAdapter(null);
            return;
        }

        optionAdapter = new ClinicalOptionAdapter(requireContext());
        optionItems.addAll(viewModel.getCurrentAdviceOptions(entryType));

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

                clearEntryError();

                entryAutoComplete.post(() -> entryAutoComplete.setSelection(entryAutoComplete.length()));
            });
        }

        entryAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                clearEntryError();
                if (selectedOption == null) {
                    return;
                }

                String currentText = text == null ? "" : text.toString().trim();

                /*
                 * Selected text customize করলে
                 * current exact selection চলে যাবে।
                 *
                 * lastSelectedMasterOption থাকবে,
                 * তাই master ID হারাবে না।
                 */
                if (!selectedOption.getOptionName().equalsIgnoreCase(currentText)) {
                    selectedOption = null;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        entryAutoComplete.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveEntry();
                return true;
            }

            return false;
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

        editingItem = viewModel.findAdviceEntryById(entryType, localEntryId);

        if (editingItem == null) {
            dismiss();
            return;
        }

        String displayName = entryType.getDisplayName();
        String entryName = displayName.toLowerCase(Locale.ENGLISH);

        tvTitle.setText("Edit " + displayName);
        tvSubtitle.setText("Update the selected " + entryName + " information");

        btnSave.setText("UPDATE " + displayName.toUpperCase(Locale.ENGLISH));

        selectedOption = findOptionByMasterId(editingItem.getMasterOptionId());
        lastSelectedMasterOption = selectedOption;

        entryAutoComplete.setText(editingItem.getDisplayText(), false);
        entryAutoComplete.setSelection(entryAutoComplete.length());
    }

    // =====================================================
    // Save
    // =====================================================

    private void saveEntry() {
        clearEntryError();

        String enteredText = entryAutoComplete.getText() == null ? "" : entryAutoComplete.getText().toString().trim();
        String entryName = entryType.getDisplayName().toLowerCase(Locale.ENGLISH);

        if (enteredText.isEmpty()) {
            if (entryType.hasMasterOptions()) {
                showEntryError("Enter or select " + entryName);
            } else {
                showEntryError("Enter " + entryName);
            }

            entryAutoComplete.requestFocus();
            return;
        }

        /*
         * User dropdown থেকে click না করে master
         * option-এর সম্পূর্ণ নাম লিখলেও exact
         * master option হিসেবে নেওয়া হবে।
         */
        ClinicalOption exactMatch = null;

        if (optionAdapter != null) {
            exactMatch = optionAdapter.findExactMatch(enteredText);
        }

        /*
         * Exact match পাওয়া গেলে সেটি আগের retained
         * master option-কে override করবে।
         */
        if (exactMatch != null) {
            selectedOption = exactMatch;
            lastSelectedMasterOption = exactMatch;
        }

        ClinicalOption masterOptionForSave = selectedOption;

        /*
         * Text customize করা হয়ে থাকলে শেষবার
         * selected master option-এর ID রাখা হবে।
         */
        if (masterOptionForSave == null && entryType.allowsCustomEntry()) {
            masterOptionForSave = lastSelectedMasterOption;
        }

        AdviceEntryItem itemToSave;

        if (editingItem == null) {
            if (masterOptionForSave != null) {
                itemToSave = AdviceEntryItem.fromMasterOption(masterOptionForSave, enteredText);
            } else {
                itemToSave = AdviceEntryItem.fromCustomText(enteredText);
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
            successful = viewModel.addAdviceEntry(entryType, itemToSave);
        } else {
            successful = viewModel.updateAdviceEntry(entryType, itemToSave);
        }

        if (!successful) {
            showEntryError("This " + entryName + " has already been added");
            return;
        }

        dismiss();
    }

    // =====================================================
    // Helpers
    // =====================================================

    private void clearEntryError() {
        entryInputLayout.setError(null);

        /*
         * শুধু setError(null) করলে error-এর জায়গার
         * খালি space থেকে যেতে পারে।
         */
        entryInputLayout.setErrorEnabled(false);
    }

    private void showEntryError(@NonNull String errorMessage) {
        entryInputLayout.setErrorEnabled(true);
        entryInputLayout.setError(errorMessage);
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
    }
}