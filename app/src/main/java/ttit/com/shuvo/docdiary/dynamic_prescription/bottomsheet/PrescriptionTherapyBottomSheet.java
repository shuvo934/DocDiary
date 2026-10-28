package ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.ClinicalOptionAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalOption;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.TherapyEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionTherapyBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionTherapyBottomSheet";
    private static final String ARG_EDIT_ENTRY_ID = "edit_entry_id";

    private PrescriptionBuilderViewModel viewModel;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextView tvHelper;

    private AppCompatImageView ivHeaderIcon;

    private TextInputLayout therapyInputLayout;
    private MaterialAutoCompleteTextView therapyAutoComplete;

    private TextInputLayout notesInputLayout;
    private TextInputEditText notesEditText;

    private MaterialButton btnSave;

    private ClinicalOptionAdapter optionAdapter;
    private TherapyEntryItem editingItem;

    @NonNull
    public static PrescriptionTherapyBottomSheet newAddInstance() {
        return new PrescriptionTherapyBottomSheet();
    }

    @NonNull
    public static PrescriptionTherapyBottomSheet newEditInstance(@NonNull String localEntryId) {
        PrescriptionTherapyBottomSheet sheet = new PrescriptionTherapyBottomSheet();
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

        therapyInputLayout = view.findViewById(R.id.layout_clinical_entry);
        therapyAutoComplete = view.findViewById(R.id.actv_clinical_entry);

        notesInputLayout = view.findViewById(R.id.layout_entry_notes);
        notesEditText = view.findViewById(R.id.et_entry_notes);

        btnSave = view.findViewById(R.id.btn_save_clinical_entry);
    }

    @SuppressLint("SetTextI18n")
    private void configureUi(@NonNull View view) {
        tvTitle.setText("Add Therapy");
        tvSubtitle.setText("Search and select therapy");
        tvHelper.setText("Select therapy from the available list");

        ivHeaderIcon.setImageResource(R.drawable.ic_therapy_24);

        therapyInputLayout.setHint("Therapy");
        therapyInputLayout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);

        therapyAutoComplete.setInputType(InputType.TYPE_CLASS_TEXT);
        therapyAutoComplete.setSingleLine(true);
        therapyAutoComplete.setMaxLines(1);
        therapyAutoComplete.setImeOptions(EditorInfo.IME_ACTION_DONE);

        btnSave.setText("ADD THERAPY");

        view.findViewById(R.id.btn_delete_clinical_entry).setVisibility(View.GONE);

        notesInputLayout.setVisibility(View.VISIBLE);

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

    private void configureOptionAdapter() {
        optionAdapter = new ClinicalOptionAdapter(requireContext());
        optionAdapter.submitList(viewModel.getCurrentTherapyOptions());
        therapyAutoComplete.setAdapter(optionAdapter);
    }

    private void configureSelection() {
        therapyAutoComplete.setOnClickListener(view -> {
            if (optionAdapter.getCount() > 0) {
                therapyAutoComplete.showDropDown();
            }
        });

        therapyAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);

            if (!(selectedItem instanceof ClinicalOption)) {
                return;
            }

            ClinicalOption selectedOption = (ClinicalOption) selectedItem;
            therapyAutoComplete.setText(selectedOption.getOptionName(), false);

            clearInputError();
            therapyAutoComplete.clearFocus();
            hideKeyboard(therapyAutoComplete);

            therapyAutoComplete.post(() -> therapyAutoComplete.setSelection(therapyAutoComplete.length()));
        });

        therapyAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                clearInputError();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        therapyAutoComplete.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    therapyAutoComplete.clearFocus();
                    hideKeyboard(therapyAutoComplete);

                    return false; // consume.
                }
            }
            return true;
        });
    }

    private void configureActions(@NonNull View view) {
        view.findViewById(R.id.btn_close_entry_sheet).setOnClickListener(button -> dismiss());
        view.findViewById(R.id.btn_cancel_clinical_entry).setOnClickListener(button -> dismiss());
        btnSave.setOnClickListener(button -> saveTherapy());
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

        editingItem = viewModel.findTherapyEntryById(localEntryId);

        if (editingItem == null) {
            dismiss();
            return;
        }

        tvTitle.setText("Edit Therapy");
        tvSubtitle.setText("Update the selected therapy");
        btnSave.setText("UPDATE THERAPY");

        therapyAutoComplete.setText(editingItem.getTherapyName(), false);
        therapyAutoComplete.setSelection(therapyAutoComplete.length());
        notesEditText.setText(editingItem.getNotes());
    }

    private void saveTherapy() {
        clearInputError();

        String therapyText = getInputText();
        String notes = getNotesText();

        if (therapyText.isEmpty()) {
            showInputError("Select a therapy");
            return;
        }

        ClinicalOption therapyOption = optionAdapter.findExactMatch(therapyText);

        if (therapyOption == null) {
            showInputError("Select a valid therapy from the list");
            return;
        }

        TherapyEntryItem itemToSave;

        if (editingItem == null) {
            itemToSave = TherapyEntryItem.create(therapyOption, notes);
        } else {
            itemToSave = editingItem.update(therapyOption, notes);
        }

        boolean successful;

        if (editingItem == null) {
            successful = viewModel.addTherapy(itemToSave);
        } else {
            successful = viewModel.updateTherapy(itemToSave);
        }

        if (!successful) {
            showInputError("This therapy has already been added");
            return;
        }

        dismiss();
    }

    private void showInputError(@NonNull String errorMessage) {
        therapyInputLayout.setErrorEnabled(true);
        therapyInputLayout.setError(errorMessage);
        therapyAutoComplete.requestFocus();
    }

    private void clearInputError() {
        therapyInputLayout.setError(null);
        therapyInputLayout.setErrorEnabled(false);
    }

    @NonNull
    private String getInputText() {
        if (therapyAutoComplete.getText() == null) {
            return "";
        }

        return therapyAutoComplete.getText().toString().trim();
    }

    @NonNull
    private String getNotesText() {
        if (notesEditText == null || notesEditText.getText() == null) {
            return "";
        }

        return notesEditText.getText().toString().trim();
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
        tvHelper = null;

        ivHeaderIcon = null;

        therapyInputLayout = null;
        therapyAutoComplete = null;

        btnSave = null;
        optionAdapter = null;
        editingItem = null;

        notesInputLayout = null;
        notesEditText = null;
    }
}