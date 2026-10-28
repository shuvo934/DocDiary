package ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPatientDetails;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionPatientDetailsBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "PrescriptionPatientDetailsBottomSheet";

    public static final String RESULT_REQUEST_KEY = "prescription_patient_details_result";
    public static final String KEY_CHANGE_PATIENT = "change_prescription_patient";

    private static final String ARG_PATIENT_HISTORY_ID = "patient_history_id";
    private static final String ARG_ALLOW_PATIENT_CHANGE = "allow_patient_change";

    private PrescriptionBuilderViewModel viewModel;

    private AppCompatImageButton closeButton;
    private NestedScrollView detailsScrollView;
    private LinearLayout detailsContainer;
    private CircularProgressIndicator progressIndicator;
    private TextView errorView;
    private MaterialButton closeActionButton;
    private MaterialButton changePatientButton;
    private Space changePatientSpace;

    @NonNull
    private String patientHistoryId = "";

    private boolean allowPatientChange = false;

    @NonNull
    public static PrescriptionPatientDetailsBottomSheet newInstance(@NonNull String patientHistoryId, boolean allowPatientChange) {
        PrescriptionPatientDetailsBottomSheet sheet = new PrescriptionPatientDetailsBottomSheet();

        Bundle arguments = new Bundle();
        arguments.putString(ARG_PATIENT_HISTORY_ID, patientHistoryId.trim());
        arguments.putBoolean(ARG_ALLOW_PATIENT_CHANGE, allowPatientChange);
        sheet.setArguments(arguments);

        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_prescription_patient_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);

        readArguments();
        initializeViews(view);
        configureActions();
        observePatientDetails();
        configurePatientChangeButton();

        viewModel.clearPatientDetailsState();
        viewModel.loadPatientDetails(patientHistoryId);
    }

    private void readArguments() {
        Bundle arguments = getArguments();

        if (arguments == null) {
            return;
        }

        String argumentPatientHistoryId = arguments.getString(ARG_PATIENT_HISTORY_ID);
        patientHistoryId = argumentPatientHistoryId == null ? "" : argumentPatientHistoryId.trim();
        allowPatientChange = arguments.getBoolean(ARG_ALLOW_PATIENT_CHANGE, false);
    }

    private void initializeViews(@NonNull View view) {
        closeButton = view.findViewById(R.id.btn_close_patient_details);
        detailsScrollView = view.findViewById(R.id.scroll_patient_details);
        detailsContainer = view.findViewById(R.id.layout_patient_detail_rows);
        progressIndicator = view.findViewById(R.id.progress_patient_details);
        errorView = view.findViewById(R.id.tv_patient_details_error);
        closeActionButton = view.findViewById(R.id.btn_close_patient_details_action);
        changePatientButton = view.findViewById(R.id.btn_change_patient);
        changePatientSpace = view.findViewById(R.id.space_patient_change);
    }

    private void configureActions() {
        closeButton.setOnClickListener(view -> dismiss());
        closeActionButton.setOnClickListener(view -> dismiss());

        errorView.setOnClickListener(view -> {
            if (!patientHistoryId.isEmpty()) {
                viewModel.loadPatientDetails(patientHistoryId);
            }
        });

        changePatientButton.setOnClickListener(view -> {
            Bundle result = new Bundle();
            result.putBoolean(KEY_CHANGE_PATIENT, true);

            getParentFragmentManager().setFragmentResult(RESULT_REQUEST_KEY, result);
            dismiss();
        });
    }

    private void configurePatientChangeButton() {
        changePatientButton.setVisibility(allowPatientChange ? View.VISIBLE : View.GONE);
        changePatientSpace.setVisibility(allowPatientChange ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void observePatientDetails() {
        viewModel.getPatientDetailsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);

            progressIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);

            if (loading) {
                detailsScrollView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
            }
        });

        viewModel.getPatientDetailsData().observe(getViewLifecycleOwner(), details -> {
            if (details == null) {
                return;
            }

            displayPatientDetails(details);

            progressIndicator.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
            detailsScrollView.setVisibility(View.VISIBLE);
        });

        viewModel.getPatientDetailsError().observe(getViewLifecycleOwner(), errorMessage -> {
            String validError = cleanText(errorMessage);

            if (validError.isEmpty()) {
                return;
            }

            detailsScrollView.setVisibility(View.GONE);
            progressIndicator.setVisibility(View.GONE);

            errorView.setText(validError + "\nTap to retry");
            errorView.setVisibility(View.VISIBLE);
        });
    }

    private void displayPatientDetails(@NonNull PrescriptionPatientDetails details) {
        detailsContainer.removeAllViews();

        addDetailRow("Patient Name", details.getPatientName());
        addDetailRow("Patient Code", details.getPatientCode());
        addDetailRow("Age", details.getAge());
        addDetailRow("Gender", details.getGender());
        addDetailRow("Marital Status", details.getMaritalStatus());
        addDetailRow("Blood Group", details.getBloodGroup());
        addDetailRow("Patient Category", details.getPatientCategory());
        addDetailRow("Mobile Number", details.getMobileNumber());
        addDetailRow("Phone Number", details.getPhoneNumber());
        addDetailRow("Address", details.getAddress());
        addDetailRow("Registration Year", details.getRegistrationYear());
        addDetailRow("Father's Name", details.getFatherName());
        addDetailRow("Mother's Name", details.getMotherName());
    }

    private void addDetailRow(@NonNull String label, @Nullable String value) {
        String validValue = cleanText(value);

        if (validValue.isEmpty()) {
            return;
        }

        View rowView = LayoutInflater.from(requireContext()).inflate(
                R.layout.item_prescription_patient_detail,
                detailsContainer,
                false
        );

        TextView labelView = rowView.findViewById(R.id.tv_patient_detail_label);
        TextView valueView = rowView.findViewById(R.id.tv_patient_detail_value);

        labelView.setText(label);
        valueView.setText(validValue);

        detailsContainer.addView(rowView);
    }

    @NonNull
    private String cleanText(@Nullable String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return "";
        }

        return value.trim();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (!(dialog instanceof BottomSheetDialog)) {
            return;
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
        viewModel.cancelPatientDetailsLoad();
        super.onDestroyView();

        viewModel = null;

        closeButton = null;
        detailsScrollView = null;
        detailsContainer = null;
        progressIndicator = null;
        errorView = null;
        closeActionButton = null;
        changePatientButton = null;
        changePatientSpace = null;
    }
}