package ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.VitalData;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionVitalsBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionVitalsBottomSheet";
    private PrescriptionBuilderViewModel viewModel;

    private TextView tvTitle;

    private TextInputLayout layoutSystolic;
    private TextInputLayout layoutDiastolic;
    private TextInputLayout layoutPulse;
    private TextInputLayout layoutTemperature;
    private TextInputLayout layoutSpo2;
    private TextInputLayout layoutRespiratoryRate;
    private TextInputLayout layoutHeight;
    private TextInputLayout layoutWeight;

    private TextInputEditText etSystolic;
    private TextInputEditText etDiastolic;
    private TextInputEditText etPulse;
    private TextInputEditText etTemperature;
    private TextInputEditText etSpo2;
    private TextInputEditText etRespiratoryRate;
    private TextInputEditText etHeight;
    private TextInputEditText etWeight;
    private TextInputEditText etBmi;

    private MaterialButton btnClearVitals;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_prescription_vitals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);
        initializeViews(view);
        setupBmiCalculation();
        setupActions(view);
        loadExistingVitals();
    }

    private void initializeViews(@NonNull View view) {
        tvTitle = view.findViewById(R.id.tv_vitals_sheet_title);

        layoutSystolic = view.findViewById(R.id.layout_vitals_systolic);
        layoutDiastolic = view.findViewById(R.id.layout_vitals_diastolic);
        layoutPulse = view.findViewById(R.id.layout_vitals_pulse);
        layoutTemperature = view.findViewById(R.id.layout_vitals_temperature);
        layoutSpo2 = view.findViewById(R.id.layout_vitals_spo2);
        layoutRespiratoryRate = view.findViewById(R.id.layout_vitals_respiratory_rate);
        layoutHeight = view.findViewById(R.id.layout_vitals_height);
        layoutWeight = view.findViewById(R.id.layout_vitals_weight);

        etSystolic = view.findViewById(R.id.et_vitals_systolic);
        etDiastolic = view.findViewById(R.id.et_vitals_diastolic);
        etPulse = view.findViewById(R.id.et_vitals_pulse);
        etTemperature = view.findViewById(R.id.et_vitals_temperature);
        etSpo2 = view.findViewById(R.id.et_vitals_spo2);
        etRespiratoryRate = view.findViewById(R.id.et_vitals_respiratory_rate);
        etHeight = view.findViewById(R.id.et_vitals_height);
        etWeight = view.findViewById(R.id.et_vitals_weight);

        etBmi = view.findViewById(R.id.et_vitals_bmi);

        btnClearVitals = view.findViewById(R.id.btn_clear_vitals);

        editorActionListener(etSystolic);
        editorActionListener(etDiastolic);
        editorActionListener(etPulse);
        editorActionListener(etTemperature);
        editorActionListener(etSpo2);
        editorActionListener(etRespiratoryRate);
        editorActionListener(etHeight);
        editorActionListener(etWeight);
    }

    private void editorActionListener(TextInputEditText editText) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    editText.clearFocus();
                    hideKeyboard(editText);

                    return false; // consume.
                }
            }
            return true;
        });
    }

    private void setupActions(@NonNull View view) {
        view.findViewById(R.id.btn_close_vitals_sheet).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btn_cancel_vitals).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btn_save_vitals).setOnClickListener(v -> saveVitals());

        btnClearVitals.setOnClickListener(v -> showClearConfirmation());
    }

    private void setupBmiCalculation() {
        TextWatcher bmiTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateBmi();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        etHeight.addTextChangedListener(bmiTextWatcher);

        etWeight.addTextChangedListener(bmiTextWatcher);
    }

    private void calculateBmi() {
        String heightText = getTextValue(etHeight);
        String weightText = getTextValue(etWeight);

        if (heightText.isEmpty() || weightText.isEmpty()) {
            etBmi.setText("");
            return;
        }

        try {
            double heightCm = Double.parseDouble(heightText);
            double weightKg = Double.parseDouble(weightText);

            if (heightCm <= 0 || weightKg <= 0) {
                etBmi.setText("");
                return;
            }

            double heightMeter = heightCm / 100.0;

            double bmi = weightKg / (heightMeter * heightMeter);

            etBmi.setText(String.format(Locale.ENGLISH, "%.1f", bmi));

        } catch (NumberFormatException exception) {
            etBmi.setText("");
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadExistingVitals() {
        VitalData existingData = viewModel.getVitals().getValue();

        if (existingData == null || existingData.isEmpty()) {
            tvTitle.setText("Add Vitals");
            btnClearVitals.setVisibility(View.GONE);
            return;
        }

        tvTitle.setText("Edit Vitals");
        btnClearVitals.setVisibility(View.VISIBLE);

        etSystolic.setText(existingData.getSystolic());
        etDiastolic.setText(existingData.getDiastolic());
        etPulse.setText(existingData.getPulse());
        etTemperature.setText(existingData.getTemperature());
        etSpo2.setText(existingData.getSpo2());
        etRespiratoryRate.setText(existingData.getRespiratoryRate());
        etHeight.setText(existingData.getHeight());
        etWeight.setText(existingData.getWeight());
        etBmi.setText(existingData.getBmi());
    }

    private void saveVitals() {

        clearErrors();

        VitalData vitalData = new VitalData(
                getTextValue(etSystolic),
                getTextValue(etDiastolic),
                getTextValue(etPulse),
                getTextValue(etTemperature),
                getTextValue(etSpo2),
                getTextValue(etRespiratoryRate),
                getTextValue(etHeight),
                getTextValue(etWeight),
                getTextValue(etBmi));

        if (vitalData.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "Enter at least one vital measurement",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!validateVitals(vitalData)) {
            return;
        }

        viewModel.saveVitals(vitalData);
        dismiss();
    }

    private boolean validateVitals(@NonNull VitalData vitalData) {
        boolean valid = true;
        /*
         * Blood pressure-এর একটি value দিলে
         * অপর value-ও দিতে হবে।
         */

        if (!vitalData.getSystolic().isEmpty() && vitalData.getDiastolic().isEmpty()) {
            layoutDiastolic.setError("Enter diastolic pressure");
            valid = false;
        }

        if (vitalData.getSystolic().isEmpty() && !vitalData.getDiastolic().isEmpty()) {
            layoutSystolic.setError("Enter systolic pressure");
            valid = false;
        }

        if (isInvalidNumber(layoutSystolic, vitalData.getSystolic())) {
            valid = false;
        }

        if (isInvalidNumber(layoutDiastolic, vitalData.getDiastolic())) {
            valid = false;
        }

        if (isInvalidNumber(layoutPulse, vitalData.getPulse())) {
            valid = false;
        }

        if (isInvalidNumber(layoutTemperature, vitalData.getTemperature())) {
            valid = false;
        }

        if (!validateRange(layoutSpo2, vitalData.getSpo2())) {
            valid = false;
        }

        if (isInvalidNumber(layoutRespiratoryRate, vitalData.getRespiratoryRate())) {
            valid = false;
        }

        if (isInvalidNumber(layoutHeight, vitalData.getHeight())) {
            valid = false;
        }

        if (isInvalidNumber(layoutWeight, vitalData.getWeight())) {
            valid = false;
        }

        return valid;
    }

    private boolean isInvalidNumber(@NonNull TextInputLayout layout, @NonNull String value) {
        if (value.isEmpty()) {
            return false;
        }

        try {
            double number = Double.parseDouble(value);
            if (number > 0) {
                return false;
            }

        } catch (NumberFormatException ignored) {}

        layout.setError("Enter a valid value");
        return true;
    }

    private boolean validateRange(@NonNull TextInputLayout layout, @NonNull String value) {
        if (value.isEmpty()) {
            return true;
        }

        try {
            double number = Double.parseDouble(value);

            if (number >= (double) 1 && number <= (double) 100) {
                return true;
            }

        } catch (NumberFormatException ignored) {}

        layout.setError("Value must be " + (int) (double) 1 + "–" + (int) (double) 100);

        return false;
    }

    private void clearErrors() {
        layoutSystolic.setError(null);
        layoutDiastolic.setError(null);
        layoutPulse.setError(null);
        layoutTemperature.setError(null);
        layoutSpo2.setError(null);
        layoutRespiratoryRate.setError(null);
        layoutHeight.setError(null);
        layoutWeight.setError(null);
    }

    private String getTextValue(@NonNull TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private void showClearConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear vitals?")
                .setMessage("All saved vital measurements will be removed.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Clear", (dialog, which) -> {
                    viewModel.clearVitals();
                    dismiss();
                })
                .show();
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

        layoutSystolic = null;
        layoutDiastolic = null;
        layoutPulse = null;
        layoutTemperature = null;
        layoutSpo2 = null;
        layoutRespiratoryRate = null;
        layoutHeight = null;
        layoutWeight = null;

        etSystolic = null;
        etDiastolic = null;
        etPulse = null;
        etTemperature = null;
        etSpo2 = null;
        etRespiratoryRate = null;
        etHeight = null;
        etWeight = null;
        etBmi = null;

        btnClearVitals = null;
    }

    private void hideKeyboard(@NonNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
