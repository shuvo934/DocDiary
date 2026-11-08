package ttit.com.shuvo.docdiary.dynamic_prescription;

import static android.view.View.GONE;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionPagerAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionHistoryBottomSheet;
import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionPatientDetailsBottomSheet;
import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionPatientSelectionBottomSheet;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.VitalData;
import ttit.com.shuvo.docdiary.dynamic_prescription.preview.PrescriptionPreview;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;
import ttit.com.shuvo.docdiary.utilities.EdgeToEdgeHelper;

import androidx.annotation.Nullable;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalEntryType;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ObjectiveInfoItem;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.MedicineEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.TherapyEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.TreatmentPlanEntryItem;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryType;

import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionTemplateBottomSheet;

public class PrescriptionBuilder extends AppCompatActivity {

    private static final String[] TAB_TITLES = {
            "CLINICAL",
            "RX",
            "ADVICE",
            "REVIEW"
    };

    ConstraintLayout prescriptionBuilderFullLayout;
    CircularProgressIndicator prescriptionBuilderProgress;
    private PrescriptionBuilderViewModel prescriptionViewModel;

    String doc_id = "";

    private MaterialCardView cardPatientSummary;

    TextView patientName;
    TextView patientCode;
    TextView patientSexAge;

    TextView visitNo;
    TextView visitDate;

    private boolean patientSelectionRequired = false;

    String pat_name = "";
    String pat_code = "";
    String ph_id = "";
    String pat_details = "";
    String p_pat_type = "";
    String p_prescription_type = "1";

    String rvm_id = "";
    String rvm_code = "";
    String visit_date = "";
    String today_date = "";

    String pat_id = "";
    String ad_id = "";
    String is_prescription_created = "";

    private ViewPager2 prescriptionViewPager;
    private TabLayout tabPrescription;

    private ImageView backButton;
    private ImageView prescriptionHistoryButton;
    private ImageView prescriptionPreviewButton;
    private MaterialCardView cardTemplates;
    private TextView templateSubtitle;
    private ImageView templateStatusIcon;

//    private MaterialButton btnSaveDraft;
    private MaterialButton btnNext;
    private boolean hasRxData = false;

    PrescriptionPagerAdapter pagerAdapter;
    private TabLayoutMediator tabLayoutMediator;

    private final ViewPager2.OnPageChangeCallback pageChangeCallback =
            new ViewPager2.OnPageChangeCallback() {

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    updateBottomAction(position);
                }
            };

    private MaterialCheckBox checkboxReviewConfirmation;

    private AlertDialog mainDataErrorDialog;

    private static final String CREATE_PRESCRIPTION_REQUEST_TAG = "create_prescription_request";
    private static final String UPDATE_PRESCRIPTION_REQUEST_TAG = "update_prescription_request";

    private RequestQueue requestQueue;
    private boolean isGeneratingPrescription = false;

    private AlertDialog existingPrescriptionErrorDialog;

    private boolean mainDataLoading = false;
    private boolean existingPrescriptionLoading = false;

    private static final String STATE_SAVED_PRESCRIPTION_SNAPSHOT = "saved_prescription_snapshot";

    @Nullable
    private String savedPrescriptionSnapshot;

    private AlertDialog unsavedChangesDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdgeHelper.enable(this);
        setContentView(R.layout.activity_prescription_builder);
        if (savedInstanceState != null) {
            savedPrescriptionSnapshot = savedInstanceState.getString(STATE_SAVED_PRESCRIPTION_SNAPSHOT);
        }
        EdgeToEdgeHelper.applyInsets(
                this,
                findViewById(R.id.presc_builder_root),
                false,
                false);

        initializeViews();
        setupDetails();
        setupViewPager();
        setupBackNavigation();
        setupClickListeners();
        configurePrescriptionMainData();
        configurePatientSelection();
        configurePrescriptionHistory();
    }

    // 1
    private void initializeViews() {
        prescriptionBuilderFullLayout = findViewById(R.id.prescription_builder_full_layout);
        prescriptionBuilderProgress = findViewById(R.id.progress_indicator_prescription_builder);
        prescriptionBuilderProgress.setVisibility(GONE);
        cardPatientSummary = findViewById(R.id.card_patient_summary);
        patientName = findViewById(R.id.patient_name_in_presc_buider);
        patientCode = findViewById(R.id.patient_code_in_presc_builder);
        patientSexAge = findViewById(R.id.patient_details_text);

        visitNo = findViewById(R.id.patient_visit_no);
        visitDate = findViewById(R.id.patient_visit_date);

        backButton = findViewById(R.id.back_logo_of_prescription_builder);
        backButton = findViewById(R.id.back_logo_of_prescription_builder);
        prescriptionHistoryButton = findViewById(R.id.prescription_hist_btn);
        prescriptionPreviewButton = findViewById(R.id.prescription_prv_btn);
        cardTemplates = findViewById(R.id.card_templates);
        templateSubtitle = findViewById(R.id.tv_templates_subtitle);
        templateStatusIcon = findViewById(R.id.iv_template_status);

        tabPrescription = findViewById(R.id.tab_prescription);
        prescriptionViewPager = findViewById(R.id.prescription_view_pager);

//        btnSaveDraft = findViewById(R.id.btn_save_draft);
        btnNext = findViewById(R.id.btn_next);

        checkboxReviewConfirmation = findViewById(R.id.checkbox_review_confirmation);
    }

    // 2
    @SuppressLint("SetTextI18n")
    private void setupDetails() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        today_date = simpleDateFormat.format(todayDate).toUpperCase();

        Intent intent = getIntent();
        if (intent != null) {
            String age = cleanIntentValue(intent.getStringExtra("P_AGE"));
            String gender = cleanIntentValue(intent.getStringExtra("PAT_GENDER"));
            pat_name = cleanIntentValue(intent.getStringExtra("PAT_NAME"));
            pat_code = cleanIntentValue(intent.getStringExtra("PAT_CODE"));
            ph_id = cleanIntentValue(intent.getStringExtra("P_PH_ID"));
            visit_date = cleanIntentValue(intent.getStringExtra("ADM_DATE"));
            pat_id = cleanIntentValue(intent.getStringExtra("P_PAT_ID"));
            ad_id = cleanIntentValue(intent.getStringExtra("P_AD_ID"));
            is_prescription_created = cleanIntentValue(intent.getStringExtra("IS_CREATED"));
            p_pat_type = cleanIntentValue(intent.getStringExtra("P_PAT_TYPE"));

            if (isEmpty(p_pat_type)) {
                boolean hasScheduledPatientData = !isEmpty(pat_id) && !isEmpty(ph_id) && !isEmpty(ad_id);
                p_pat_type = hasScheduledPatientData ? "1" : "2";
            }

            if (isEmpty(is_prescription_created)) {
                is_prescription_created = "0";
            }

            if (isEmpty(visit_date)) {
                visit_date = today_date;
            }

            p_prescription_type = "2".equals(p_pat_type) ? "2" : "1";

            pat_details = buildGenderAgeText(gender, age);
            updatePatientSummary();

            visitNo.setText("Visit: ...");

            String vd = "Date: " + visit_date;
            visitDate.setText(vd);
        }
    }

    @NonNull
    private String buildGenderAgeText(@Nullable String gender, @Nullable String age) {
        String validGender = cleanIntentValue(gender);
        String validAge = cleanIntentValue(age);

        if (!validGender.isEmpty() && !validAge.isEmpty()) {
            return validGender + " • " + validAge;
        }

        if (!validGender.isEmpty()) {
            return validGender;
        }

        return validAge;
    }

    @SuppressLint("SetTextI18n")
    private void updatePatientSummary() {
        if (isEmpty(pat_id) || isEmpty(ph_id)) {
            patientName.setText("Select Patient");
            patientCode.setText("Walk-in Patient");
            patientSexAge.setText("Tap here to select a patient");
            return;
        }

        patientName.setText(isEmpty(pat_name) ? "Patient" : pat_name);
        patientCode.setText(isEmpty(pat_code) ? "-" : pat_code);
        patientSexAge.setText(isEmpty(pat_details) ? "Patient information" : pat_details);

        updateAppBarActionVisibility();
    }

    @NonNull
    private String cleanIntentValue(@Nullable String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return "";
        }

        return value.trim();
    }

    // 3
    private void setupViewPager() {
        pagerAdapter = new PrescriptionPagerAdapter(this);
        prescriptionViewPager.setAdapter(pagerAdapter);

        /*
         * Keeps all four prescription fragments alive.
         * This prevents entered data from disappearing during tab changes.
         */
        prescriptionViewPager.setOffscreenPageLimit(3);

        /*
         * Doctors can change tabs by both tapping and swiping.
         */
        prescriptionViewPager.setUserInputEnabled(true);

        tabLayoutMediator = new TabLayoutMediator(
                tabPrescription,
                prescriptionViewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        );

        /*
         * Existing XML TabItems are replaced here.
         */
        tabLayoutMediator.attach();

        prescriptionViewPager.registerOnPageChangeCallback(pageChangeCallback);

        updateBottomAction(prescriptionViewPager.getCurrentItem());
    }

    // 4
    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isGeneratingPrescription) {
                    Toast.makeText(PrescriptionBuilder.this, "Please wait while the prescription is being saved", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!hasUnsavedPrescriptionChanges()) {
                    finish();
                    return;
                }

                showUnsavedChangesDialog();
            }
        });
    }

    private void showUnsavedChangesDialog() {
        if (unsavedChangesDialog != null && unsavedChangesDialog.isShowing()) {
            return;
        }

        unsavedChangesDialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Unsaved changes")
                .setMessage("You have unsaved prescription changes. " + "Do you want to discard them and exit?")
                .setPositiveButton("KEEP EDITING", null)
                .setNegativeButton("DISCARD & EXIT", (dialog, which) -> finish())
                .create();

        unsavedChangesDialog.setOnDismissListener(dialog -> unsavedChangesDialog = null);
        unsavedChangesDialog.show();
    }

    // 5
    private void setupClickListeners() {
        backButton.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        cardPatientSummary.setOnClickListener(view -> handlePatientSummaryClick());
        cardTemplates.setOnClickListener(view -> showPrescriptionTemplateBottomSheet());
        prescriptionHistoryButton.setOnClickListener(view -> showPrescriptionHistoryBottomSheet());

//        btnSaveDraft.setOnClickListener(view -> savePrescriptionDraft());

        btnNext.setOnClickListener(view -> handleNextButton());

        checkboxReviewConfirmation.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (prescriptionViewPager.getCurrentItem() == 3) {
                        updateGenerateButtonState();
                    }
                }
        );

        prescriptionPreviewButton.setOnClickListener(view -> {
            if (rvm_id == null || rvm_id.trim().isEmpty()) {
                Toast.makeText(PrescriptionBuilder.this, "Generate the prescription before opening preview", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent previewIntent = new Intent(PrescriptionBuilder.this, PrescriptionPreview.class);
            previewIntent.putExtra(PrescriptionPreview.EXTRA_RVM_ID, rvm_id.trim());
            startActivity(previewIntent);
        });
    }

    //  6 Main Data configure
    private void configurePrescriptionMainData() {
        prescriptionViewModel = new ViewModelProvider(this).get(PrescriptionBuilderViewModel.class);

        observeMainDataState();
        observeExistingPrescriptionState();
        observeRxGenerationEligibility();
        observeNonRxReviewInvalidation();
        observeTemplateCardState();

        doc_id = String.valueOf(userInfoLists.get(0).getDoc_id());
        prescriptionViewModel.loadMainDataIfNeeded(doc_id);
        if (!"0".equals(is_prescription_created)) {
            prescriptionViewModel.loadExistingPrescriptionIfNeeded(ph_id, pat_id, ad_id);
        }
        else if (savedPrescriptionSnapshot == null) {
            markCurrentPrescriptionAsSaved();
        }

    }


    // 7
    private void configurePrescriptionHistory() {
        getSupportFragmentManager().setFragmentResultListener(
                PrescriptionHistoryBottomSheet.RESULT_REQUEST_KEY,
                this,
                (requestKey, result) -> handlePrescriptionHistoryResult(result)
        );
    }

    private void showPrescriptionHistoryBottomSheet() {
        if (isEmpty(pat_id) || isEmpty(ph_id)) {
            showPrescriptionError("Select a patient before viewing prescription history.");
            return;
        }

        if (isGeneratingPrescription) {
            Toast.makeText(this, "Please wait while the prescription is being saved", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getSupportFragmentManager().findFragmentByTag(PrescriptionHistoryBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionHistoryBottomSheet.newInstance(ph_id, pat_id, doc_id).show(getSupportFragmentManager(), PrescriptionHistoryBottomSheet.TAG);
    }

    private void handlePrescriptionHistoryResult(@NonNull Bundle result) {
        boolean historyLoaded = result.getBoolean(PrescriptionHistoryBottomSheet.KEY_HISTORY_LOADED, false);
        boolean previewRequested = result.getBoolean(PrescriptionHistoryBottomSheet.KEY_PREVIEW_REQUESTED, false);

        String selectedRvmId = cleanIntentValue(result.getString(PrescriptionHistoryBottomSheet.KEY_SELECTED_RVM_ID));

        if (historyLoaded) {
            handlePreviousPrescriptionLoaded();
            return;
        }

        if (previewRequested) {
            handleHistoryPrescriptionPreview(selectedRvmId);
        }
    }

    private void handlePreviousPrescriptionLoaded() {
        /*
         * History load-এর পরে template state clear হয়েছে,
         * তাই card আবার No template loaded দেখাবে।
         */
        updateTemplateCardState();

        /*
         * Loaded data review করার আগে পুরোনো confirmation
         * আর valid থাকবে না।
         */
        resetReviewConfirmation();

        updateBottomAction(prescriptionViewPager.getCurrentItem());

        /*
         * Doctor যেন loaded information সঙ্গে সঙ্গে
         * review/edit করতে পারে।
         */
        prescriptionViewPager.setCurrentItem(PrescriptionPagerAdapter.PAGE_CLINICAL, true);

        Toast.makeText(this, "Previous prescription loaded. Review and edit before saving.", Toast.LENGTH_LONG).show();
    }

    private void handleHistoryPrescriptionPreview(@NonNull String selectedRvmId) {
        if (isEmpty(selectedRvmId)) {
            showPrescriptionError("Prescription information not found.");
            return;
        }

        Intent previewIntent = new Intent(PrescriptionBuilder.this, PrescriptionPreview.class);
        previewIntent.putExtra(PrescriptionPreview.EXTRA_RVM_ID, selectedRvmId.trim());
        startActivity(previewIntent);
    }

    private void updateAppBarActionVisibility() {
        boolean hasPatient = !isEmpty(pat_id) && !isEmpty(ph_id);

        prescriptionHistoryButton.setVisibility(hasPatient ? View.VISIBLE : View.GONE);

        boolean hasCurrentPrescription = hasPatient && !isEmpty(rvm_id);

        prescriptionPreviewButton.setVisibility(hasCurrentPrescription ? View.VISIBLE : View.GONE);
    }

    private void observeMainDataState() {
        prescriptionViewModel.getMainDataLoading().observe(this, isLoading -> {
            mainDataLoading = Boolean.TRUE.equals(isLoading);
            updateInitialDataLoadingState();
        });

        prescriptionViewModel.getMainDataErrors().observe(this, errors -> {
            if (errors == null || errors.isEmpty()) {
                return;
            }

            showMainDataError(errors);
        });
    }

    @SuppressLint("SetTextI18n")
    private void observeExistingPrescriptionState() {
        prescriptionViewModel.getExistingPrescriptionLoading().observe(this, isLoading -> {
            existingPrescriptionLoading = Boolean.TRUE.equals(isLoading);
            updateInitialDataLoadingState();
        });

        prescriptionViewModel.getExistingPrescriptionLoaded().observe(this, isLoaded -> {
            if (!Boolean.TRUE.equals(isLoaded)) {
                return;
            }

            rvm_id = prescriptionViewModel.getLoadedRvmId();
            rvm_code = prescriptionViewModel.getLoadedRvmCode();

            if (!isEmpty(rvm_code)) {
                visitNo.setText("Visit: " + rvm_code);
            }

            updateAppBarActionVisibility();
            updateTemplateCardState();
            updateBottomAction(prescriptionViewPager.getCurrentItem());

            if (savedPrescriptionSnapshot == null) {
                markCurrentPrescriptionAsSaved();
            }
        });

        prescriptionViewModel.getExistingPrescriptionError().observe(this, errorMessage -> {
            if (isEmpty(errorMessage)) {
                return;
            }
            showExistingPrescriptionError(errorMessage);
        });
    }

    private void updateInitialDataLoadingState() {
        boolean loading = mainDataLoading || existingPrescriptionLoading;

        prescriptionBuilderFullLayout.setVisibility(loading ? View.GONE : View.VISIBLE);
        prescriptionBuilderProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showMainDataError(@NonNull ArrayList<String> errors) {
        if (mainDataErrorDialog != null && mainDataErrorDialog.isShowing()) {
            return;
        }

        StringBuilder errorBuilder = new StringBuilder();

        errorBuilder.append("Some prescription data could not be loaded.");

        for (String error : errors) {
            errorBuilder.append("\n").append(error);
        }
        errorBuilder.append("\nPlease try again.");


        mainDataErrorDialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Error!")
                .setMessage(errorBuilder.toString())
                .setPositiveButton("Retry", (dialog, which) -> prescriptionViewModel.retryMainData(doc_id))
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .create();

        mainDataErrorDialog.setCancelable(false);
        mainDataErrorDialog.setCanceledOnTouchOutside(false);
        mainDataErrorDialog.setOnDismissListener(dialog -> mainDataErrorDialog = null);

        mainDataErrorDialog.show();
    }

    private void showExistingPrescriptionError(@NonNull String errorMessage) {
        if (existingPrescriptionErrorDialog != null && existingPrescriptionErrorDialog.isShowing()) {
            return;
        }

        existingPrescriptionErrorDialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Prescription Load Failed")
                .setMessage(errorMessage + "\nPlease try again.")
                .setPositiveButton("Retry", (dialog, which) -> prescriptionViewModel.retryExistingPrescription(ph_id, pat_id, ad_id))
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .create();

        existingPrescriptionErrorDialog.setCancelable(false);
        existingPrescriptionErrorDialog.setCanceledOnTouchOutside(false);
        existingPrescriptionErrorDialog.setOnDismissListener(dialog -> existingPrescriptionErrorDialog = null);

        existingPrescriptionErrorDialog.show();
    }

    private void observeRxGenerationEligibility() {
        prescriptionViewModel.getMedicineItems().observe(this, items -> updateRxGenerationEligibility());
        prescriptionViewModel.getTherapyItems().observe(this, items -> updateRxGenerationEligibility());
        prescriptionViewModel.getTreatmentPlanItems().observe(this, items -> updateRxGenerationEligibility());
    }

    private void updateRxGenerationEligibility() {
        boolean medicinePermissionAllowed = userInfoLists.get(0).getMed_active().equals("1");

        boolean hasMedicine = medicinePermissionAllowed && !prescriptionViewModel.getCurrentMedicineItems().isEmpty();
        boolean hasTherapy = !prescriptionViewModel.getCurrentTherapyItems().isEmpty();
        boolean hasTreatmentPlan = !prescriptionViewModel.getCurrentTreatmentPlanItems().isEmpty();

        hasRxData = hasMedicine || hasTherapy || hasTreatmentPlan;

        resetReviewConfirmation();

        checkboxReviewConfirmation.setEnabled(hasRxData);
        checkboxReviewConfirmation.setAlpha(hasRxData ? 1.0f : 0.45f);

        if (prescriptionViewPager.getCurrentItem() == PrescriptionPagerAdapter.PAGE_REVIEW) {
            updateGenerateButtonState();
        }
    }

    // 7
    private void configurePatientSelection() {
        getSupportFragmentManager().setFragmentResultListener(
                PrescriptionPatientSelectionBottomSheet.RESULT_REQUEST_KEY, this,
                (requestKey, result) -> handlePatientSelectionResult(result));

        /*
         * শুধু নতুন walk-in prescription এবং patient না থাকলে
         * বাধ্যতামূলকভাবে selection sheet খুলবে।
         */
        if (isNewWalkInWithoutPatient()) {
            showRequiredPatientSelection();
        }

        getSupportFragmentManager().setFragmentResultListener(PrescriptionPatientDetailsBottomSheet.RESULT_REQUEST_KEY, this,
                (requestKey, result) -> {
                    boolean changePatient = result.getBoolean(PrescriptionPatientDetailsBottomSheet.KEY_CHANGE_PATIENT, false);
                    if (!changePatient) {
                        return;
                    }

                    cardPatientSummary.post(this::showChangePatientSelection);
                }
        );
    }

    private boolean isNewWalkInWithoutPatient() {
        return "2".equals(p_pat_type) && "0".equals(is_prescription_created) && (isEmpty(pat_id) || isEmpty(ph_id));
    }

    private void showRequiredPatientSelection() {
        if (getSupportFragmentManager().findFragmentByTag(PrescriptionPatientSelectionBottomSheet.TAG) != null) {
            return;
        }

        patientSelectionRequired = true;
        PrescriptionPatientSelectionBottomSheet.newRequiredInstance().show(getSupportFragmentManager(), PrescriptionPatientSelectionBottomSheet.TAG);
    }

    private void showChangePatientSelection() {
        if (getSupportFragmentManager().findFragmentByTag(PrescriptionPatientSelectionBottomSheet.TAG) != null) {
            return;
        }

        patientSelectionRequired = false;
        PrescriptionPatientSelectionBottomSheet.newChangeInstance(ph_id, p_prescription_type).show(getSupportFragmentManager(), PrescriptionPatientSelectionBottomSheet.TAG);
    }

    private void handlePatientSummaryClick() {
        if (isEmpty(ph_id)) {
            if (isNewWalkInWithoutPatient()) {
                showRequiredPatientSelection();
            }
            return;
        }

        boolean allowPatientChange = "2".equals(p_pat_type) && "0".equals(is_prescription_created);

        showPatientDetailsBottomSheet(allowPatientChange);
    }

    private void showPatientDetailsBottomSheet(boolean allowPatientChange) {
        if (getSupportFragmentManager().findFragmentByTag(PrescriptionPatientDetailsBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionPatientDetailsBottomSheet.newInstance(ph_id, allowPatientChange).show(getSupportFragmentManager(), PrescriptionPatientDetailsBottomSheet.TAG);
    }

    private void handlePatientSelectionResult(@NonNull Bundle result) {
        boolean wasRequiredSelection = patientSelectionRequired;

        patientSelectionRequired = false;

        boolean cancelled = result.getBoolean(PrescriptionPatientSelectionBottomSheet.KEY_CANCELLED, false);

        if (cancelled) {
            if (wasRequiredSelection && isNewWalkInWithoutPatient()) {
                finish();
            }
            return;
        }

        String newPatientId = cleanIntentValue(result.getString(PrescriptionPatientSelectionBottomSheet.KEY_PATIENT_ID));
        String newPatientHistoryId = cleanIntentValue(result.getString(PrescriptionPatientSelectionBottomSheet.KEY_PATIENT_HISTORY_ID));

        if (newPatientId.isEmpty() || newPatientHistoryId.isEmpty()) {
            showPrescriptionError("Selected patient information not found.");
            if (wasRequiredSelection) {
                showRequiredPatientSelection();
            }

            return;
        }

        boolean changingPatient = !isEmpty(ph_id) && !ph_id.equals(newPatientHistoryId);

        if (changingPatient && prescriptionViewModel.hasAnyPrescriptionData()) {

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Change patient?")
                    .setMessage("Changing the patient will clear all entered prescription data. Do you want to continue?")
                    .setPositiveButton("CLEAR & CHANGE", (dialog, which) -> applySelectedPatient(result, true))
                    .setNegativeButton("CANCEL", null)
                    .show();

            return;
        }

        applySelectedPatient(result, changingPatient);
    }

    private void applySelectedPatient(@NonNull Bundle result, boolean clearPrescriptionData) {
        if (clearPrescriptionData) {
            prescriptionViewModel.clearAllPrescriptionDataForPatientChange();
        }

        pat_id = cleanIntentValue(result.getString(PrescriptionPatientSelectionBottomSheet.KEY_PATIENT_ID));
        ph_id = cleanIntentValue(result.getString(PrescriptionPatientSelectionBottomSheet.KEY_PATIENT_HISTORY_ID));
        pat_name = cleanIntentValue(result.getString(PrescriptionPatientSelectionBottomSheet.KEY_PATIENT_NAME));
        pat_code = cleanIntentValue(result.getString(PrescriptionPatientSelectionBottomSheet.KEY_PATIENT_CODE));
        String gender = cleanIntentValue(result.getString(PrescriptionPatientSelectionBottomSheet.KEY_PATIENT_GENDER));
        String age = cleanIntentValue(result.getString(PrescriptionPatientSelectionBottomSheet.KEY_PATIENT_AGE));

        pat_details = buildGenderAgeText(gender, age);
        p_prescription_type = cleanIntentValue(result.getString(PrescriptionPatientSelectionBottomSheet.KEY_PRESCRIPTION_TYPE));

        if (!"1".equals(p_prescription_type) && !"2".equals(p_prescription_type)) {
            p_prescription_type = "2";
        }

        p_pat_type = "2";
        ad_id = "";

        updatePatientSummary();
        resetReviewConfirmation();
        updateTemplateCardState();

        markCurrentPrescriptionAsSaved();
    }


    // State Handler
    private void showPrescriptionTemplateBottomSheet() {
        if (getSupportFragmentManager().findFragmentByTag(PrescriptionTemplateBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionTemplateBottomSheet.newInstance().show(getSupportFragmentManager(), PrescriptionTemplateBottomSheet.TAG);
    }

    private void handleNextButton() {
        int currentPage = prescriptionViewPager.getCurrentItem();

        if (currentPage < PrescriptionPagerAdapter.PAGE_REVIEW) {
            prescriptionViewPager.setCurrentItem(
                    currentPage + 1,
                    true
            );

            return;

        }

        if (!hasRxData) {
            Toast.makeText(this, "Add at least one Rx item before generating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (prescriptionViewModel.hasIncompleteObjectiveInfo()) {
            showIncompleteObjectiveDialog(prescriptionViewModel.getIncompleteObjectiveNames());
            return;
        }

        if (!checkboxReviewConfirmation.isChecked()) {
            Toast.makeText(this, "Please review and confirm the prescription", Toast.LENGTH_SHORT).show();
            return;
        }

        if (is_prescription_created.equals("0")) {
            generatePrescription();
        }
        else {
            updatePrescription();
        }

    }

    private void showIncompleteObjectiveDialog(@NonNull ArrayList<String> objectiveNames) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Please select a value or enter notes for the following Objective Info before ");

        if ("0".equals(is_prescription_created)) {
            messageBuilder.append("generating the prescription:");
        } else {
            messageBuilder.append("updating the prescription:");
        }

        for (String objectiveName : objectiveNames) {
            messageBuilder.append("\n• ").append(objectiveName);
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Objective Information Required")
                .setMessage(messageBuilder.toString())
                .setPositiveButton("GO TO CLINICAL", (dialog, which) -> {
                    resetReviewConfirmation();
                    prescriptionViewPager.setCurrentItem(PrescriptionPagerAdapter.PAGE_CLINICAL, true);
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void updateBottomAction(int currentPage) {
        boolean isReviewTab = currentPage == 3;
        checkboxReviewConfirmation.setVisibility(isReviewTab ? View.VISIBLE : GONE);
        switch (currentPage) {
            case PrescriptionPagerAdapter.PAGE_CLINICAL:
                btnNext.setText("NEXT: RX");
                break;

            case PrescriptionPagerAdapter.PAGE_RX:
                btnNext.setText("NEXT: ADVICE");
                break;

            case PrescriptionPagerAdapter.PAGE_ADVICE:
                btnNext.setText("NEXT: REVIEW");
                break;

            case PrescriptionPagerAdapter.PAGE_REVIEW:
                btnNext.setText("0".equals(is_prescription_created) ? "GENERATE" : "UPDATE");
                break;

            default:
                btnNext.setText("NEXT");
                break;
        }

        if (isReviewTab) {
            checkboxReviewConfirmation.setEnabled(hasRxData);
            checkboxReviewConfirmation.setAlpha(hasRxData ? 1.0f : 0.45f);
            updateGenerateButtonState();

        } else {
            btnNext.setEnabled(true);
            btnNext.setAlpha(1.0f);
        }
    }

    private void updateGenerateButtonState() {
        boolean canGenerate = hasRxData && checkboxReviewConfirmation.isChecked();
        btnNext.setEnabled(canGenerate);
        btnNext.setAlpha(canGenerate ? 1.0f : 0.45f);
    }

    private void observeNonRxReviewInvalidation() {
        prescriptionViewModel.getVitals().observe(
                this,
                vitalData -> resetReviewConfirmation()
        );

        prescriptionViewModel.getObjectiveInfoItems().observe(
                this,
                items -> resetReviewConfirmation()
        );

        for (ClinicalEntryType entryType : ClinicalEntryType.values()) {
            prescriptionViewModel.getClinicalEntries(entryType).observe(
                    this,
                    items -> resetReviewConfirmation()
            );
        }

        for (AdviceEntryType entryType : AdviceEntryType.values()) {
            prescriptionViewModel.getAdviceEntries(entryType).observe(
                    this,
                    items -> resetReviewConfirmation()
            );
        }
    }

    private void observeTemplateCardState() {
        prescriptionViewModel.getDiseaseTemplateApplySuccess().observe(this, successful -> {
            if (Boolean.TRUE.equals(successful)) {
                updateTemplateCardState();
            }
        });

        prescriptionViewModel.getTreatmentTemplateApplySuccess().observe(this, successful -> {
            if (Boolean.TRUE.equals(successful)) {
                updateTemplateCardState();
            }
        });

        updateTemplateCardState();
    }

    @SuppressLint("SetTextI18n")
    private void updateTemplateCardState() {
        String diseaseTemplateName = prescriptionViewModel.getLoadedDiseaseTemplateName();
        String treatmentTemplateName = prescriptionViewModel.getLoadedTreatmentTemplateName();

        boolean hasDiseaseTemplate = !isEmpty(diseaseTemplateName);
        boolean hasTreatmentTemplate = !isEmpty(treatmentTemplateName);

        if (!hasDiseaseTemplate && !hasTreatmentTemplate) {
            templateSubtitle.setText("No template loaded");
            templateStatusIcon.setVisibility(View.GONE);
            return;
        }

        StringBuilder subtitleBuilder = new StringBuilder();

        if (hasDiseaseTemplate) {
            subtitleBuilder.append("Disease: ").append(diseaseTemplateName);
        }

        if (hasTreatmentTemplate) {
            if (subtitleBuilder.length() > 0) {
                subtitleBuilder.append("\n");
            }

            subtitleBuilder.append("Treatment: ").append(treatmentTemplateName);
        }

        templateSubtitle.setText(subtitleBuilder.toString());
        templateStatusIcon.setVisibility(View.VISIBLE);
    }

//    private void savePrescriptionDraft() {
//        Toast.makeText(this, "Prescription draft save will be connected later", Toast.LENGTH_SHORT).show();
//    }

    // Prescription generate & update
    private void generatePrescription() {
        if (isGeneratingPrescription) {
            return;
        }

        if (needRequiredInformation()) {
            showPrescriptionError("Required patient or visit information is missing.");
            return;
        }

        final String requestBody;

        try {
            JSONObject bodyObject = new JSONObject();

            addClinicalDataToBody(bodyObject);
            addObjectiveInfoToBody(bodyObject);
            addRxDataToBody(bodyObject);
            addAdviceDataToBody(bodyObject);

            requestBody = bodyObject.toString();

        } catch (JSONException exception) {
            showPrescriptionError("Prescription data failed to create: " + exception.getMessage());
            return;
        }

        VitalData vitalData = prescriptionViewModel.getCurrentVitals();
        String insertPrescriptionUrl = pre_url_api + "dynamic_presc/insertPrescription";

        setPrescriptionGenerating(true);

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        StringRequest request = new StringRequest(Request.Method.POST, insertPrescriptionUrl, response -> {
            setPrescriptionGenerating(false);

            try {
                JSONObject jsonObject = new JSONObject(response);

                String message = getJsonResponseValue(jsonObject, "string_out");
                String outRvmId = getJsonResponseValue(jsonObject, "out_rvm_id");
                String outRvmCode = getJsonResponseValue(jsonObject, "out_rvm_code");

                rvm_id = outRvmId;
                rvm_code = outRvmCode;
                is_prescription_created = "1";

                updateAppBarActionVisibility();

                resetReviewConfirmation();
                updateBottomAction(PrescriptionPagerAdapter.PAGE_REVIEW);
                markCurrentPrescriptionAsSaved();
                showPrescriptionSuccess(message, outRvmCode);

            } catch (JSONException exception) {
                showPrescriptionError("Response not found: " + exception.getMessage());
            }

        }, error -> {
            setPrescriptionGenerating(false);
            showPrescriptionError(getVolleyErrorMessage(error));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();

                headers.put("P_DOC_ID", cleanHeaderValue(doc_id));
                headers.put("P_PH_ID", cleanHeaderValue(ph_id));
                headers.put("P_PAT_ID", cleanHeaderValue(pat_id));
                headers.put("P_AD_ID", cleanHeaderValue(ad_id));

                headers.put("P_TD_ID", cleanHeaderValue(prescriptionViewModel.getLoadedDiseaseTemplateId()));
                headers.put("P_TT_ID", cleanHeaderValue(prescriptionViewModel.getLoadedTreatmentTemplateId()));

                headers.put("P_PRESCRIPTION_TYPE", p_prescription_type);
                headers.put("P_INSERT_TYPE", "3");
                headers.put("P_PATIENT_TYPE", p_pat_type);

                headers.put("P_WEIGHT", cleanHeaderValue(vitalData == null ? "" : vitalData.getWeight()));
                headers.put("P_HEIGHT", cleanHeaderValue(vitalData == null ? "" : vitalData.getHeight()));
                headers.put("P_SYSTOLIC", cleanHeaderValue(vitalData == null ? "" : vitalData.getSystolic()));
                headers.put("P_DIASTOLIC", cleanHeaderValue(vitalData == null ? "" : vitalData.getDiastolic()));
                headers.put("P_PULSE", cleanHeaderValue(vitalData == null ? "" : vitalData.getPulse()));
                headers.put("P_RESPIRATION", cleanHeaderValue(vitalData == null ? "" : vitalData.getRespiratoryRate()));
                headers.put("P_TEMPERATURE", cleanHeaderValue(vitalData == null ? "" : vitalData.getTemperature()));
                headers.put("P_SPO", cleanHeaderValue(vitalData == null ? "" : vitalData.getSpo2()));
                headers.put("P_BMI", cleanHeaderValue(vitalData == null ? "" : vitalData.getBmi()));

                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        request.setTag(CREATE_PRESCRIPTION_REQUEST_TAG);
        requestQueue.add(request);
    }

    private void updatePrescription() {
        if (isGeneratingPrescription) {
            return;
        }

        if (isEmpty(rvm_id) || needRequiredInformation()) {
            showPrescriptionError("Required prescription, patient or visit information is missing.");
            return;
        }

        final String requestBody;

        try {
            JSONObject bodyObject = new JSONObject();

            addClinicalDataToBody(bodyObject);
            addObjectiveInfoToBody(bodyObject);
            addRxDataToBody(bodyObject);
            addAdviceDataToBody(bodyObject);

            requestBody = bodyObject.toString();

        } catch (JSONException exception) {
            showPrescriptionError("Prescription data failed to create: " + exception.getMessage());
            return;
        }

        VitalData vitalData = prescriptionViewModel.getCurrentVitals();
        String updatePrescriptionUrl = pre_url_api + "dynamic_presc/updatePrescription";

        setPrescriptionGenerating(true);

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        StringRequest request = new StringRequest(Request.Method.POST, updatePrescriptionUrl, response -> {
            setPrescriptionGenerating(false);

            try {
                JSONObject jsonObject = new JSONObject(response);
                String message = getJsonResponseValue(jsonObject, "string_out");

                resetReviewConfirmation();
                markCurrentPrescriptionAsSaved();
                showPrescriptionUpdateSuccess(message);

            } catch (JSONException exception) {
                showPrescriptionError("Response not found: " + exception.getMessage());
            }

        }, error -> {
            setPrescriptionGenerating(false);
            showPrescriptionError(getVolleyErrorMessage(error));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();

                headers.put("P_RVM_ID", cleanHeaderValue(rvm_id));
                headers.put("P_DOC_ID", cleanHeaderValue(doc_id));
                headers.put("P_PH_ID", cleanHeaderValue(ph_id));
                headers.put("P_PAT_ID", cleanHeaderValue(pat_id));
                headers.put("P_AD_ID", cleanHeaderValue(ad_id));

                headers.put("P_TD_ID", cleanHeaderValue(prescriptionViewModel.getLoadedDiseaseTemplateId()));
                headers.put("P_TT_ID", cleanHeaderValue(prescriptionViewModel.getLoadedTreatmentTemplateId()));

                headers.put("P_INSERT_TYPE", "3");

                headers.put("P_WEIGHT", cleanHeaderValue(vitalData == null ? "" : vitalData.getWeight()));
                headers.put("P_HEIGHT", cleanHeaderValue(vitalData == null ? "" : vitalData.getHeight()));
                headers.put("P_SYSTOLIC", cleanHeaderValue(vitalData == null ? "" : vitalData.getSystolic()));
                headers.put("P_DIASTOLIC", cleanHeaderValue(vitalData == null ? "" : vitalData.getDiastolic()));
                headers.put("P_PULSE", cleanHeaderValue(vitalData == null ? "" : vitalData.getPulse()));
                headers.put("P_RESPIRATION", cleanHeaderValue(vitalData == null ? "" : vitalData.getRespiratoryRate()));
                headers.put("P_TEMPERATURE", cleanHeaderValue(vitalData == null ? "" : vitalData.getTemperature()));
                headers.put("P_SPO", cleanHeaderValue(vitalData == null ? "" : vitalData.getSpo2()));
                headers.put("P_BMI", cleanHeaderValue(vitalData == null ? "" : vitalData.getBmi()));

                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        request.setTag(UPDATE_PRESCRIPTION_REQUEST_TAG);
        requestQueue.add(request);
    }

    private void addClinicalDataToBody(@NonNull JSONObject bodyObject) throws JSONException {
        bodyObject.put("complaints", buildMasterTextArray(ClinicalEntryType.COMPLAIN, "cm_id"));
        bodyObject.put("injuries", buildMasterTextArray(ClinicalEntryType.INJURY, "injury_id"));
        bodyObject.put("diagnosis", buildMasterTextArray(ClinicalEntryType.DIAGNOSIS, "dm_id"));
        bodyObject.put("clinical_findings", buildMasterTextArray(ClinicalEntryType.CLINICAL_FINDING, "cfm_id"));
        bodyObject.put("drug_history", buildMasterIdArray());
        bodyObject.put("investigations", buildInvestigationArray());
        bodyObject.put("important_info", buildTextArray());
    }

    private void addObjectiveInfoToBody(@NonNull JSONObject bodyObject) throws JSONException {
        JSONArray objectiveInfoArray = new JSONArray();
        for (ObjectiveInfoItem item : prescriptionViewModel.getCurrentObjectiveInfoItems()) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("om_id", getJsonNumberOrNull(item.getObjectiveId()));
            itemObject.put("od_id", getJsonNumberOrNull(item.getObjectiveValueId()));
            itemObject.put("mjl_id", getJsonNumberOrNull(item.getMuscleJointId()));
            itemObject.put("notes", item.getNotes());
            objectiveInfoArray.put(itemObject);
        }

        bodyObject.put("objective_info", objectiveInfoArray);
    }

    private void addRxDataToBody(@NonNull JSONObject bodyObject) throws JSONException {
        JSONArray medicationsArray = new JSONArray();
        JSONArray therapiesArray = new JSONArray();
        JSONArray treatmentPlansArray = new JSONArray();

        boolean medicinePermissionAllowed = "1".equals(userInfoLists.get(0).getMed_active());

        if (medicinePermissionAllowed) {
            for (MedicineEntryItem item : prescriptionViewModel.getCurrentMedicineItems()) {
                JSONObject itemObject = new JSONObject();
                itemObject.put("medication_id", getJsonNumberOrNull(item.getMedicineId()));
                itemObject.put("dose_id", getJsonNumberOrNull(item.getDoseId()));
                itemObject.put("timing_id", getJsonNumberOrNull(item.getTimingId()));
                itemObject.put("route_id", getJsonNumberOrNull(item.getRouteId()));
                itemObject.put("duration", item.getDurationDays());
                itemObject.put("notes", item.getNotes());

                medicationsArray.put(itemObject);
            }
        }

        for (TherapyEntryItem item : prescriptionViewModel.getCurrentTherapyItems()) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("tm_id", getJsonNumberOrNull(item.getTherapyId()));
            itemObject.put("notes", item.getNotes());
            therapiesArray.put(itemObject);
        }

        for (TreatmentPlanEntryItem item : prescriptionViewModel.getCurrentTreatmentPlanItems()) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("tpm_id", getJsonNumberOrNull(item.getMasterOptionId()));
            itemObject.put("name", item.getDisplayText());

            treatmentPlansArray.put(itemObject);
        }

        bodyObject.put("medications", medicationsArray);
        bodyObject.put("therapies", therapiesArray);
        bodyObject.put("treatment_plans", treatmentPlansArray);
    }

    private void addAdviceDataToBody(@NonNull JSONObject bodyObject) throws JSONException {
        JSONArray adviceArray = new JSONArray();
        JSONArray followUpsArray = new JSONArray();
        JSONArray specialAlertsArray = new JSONArray();

        for (AdviceEntryItem item : prescriptionViewModel.getCurrentAdviceEntries(AdviceEntryType.ADVICE)) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("text", item.getDisplayText());
            adviceArray.put(itemObject);
        }

        for (AdviceEntryItem item : prescriptionViewModel.getCurrentAdviceEntries(AdviceEntryType.FOLLOW_UP)) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("dfi_id", getJsonNumberOrNull(item.getMasterOptionId()));
            itemObject.put("text", item.getDisplayText());
            followUpsArray.put(itemObject);
        }

        for (AdviceEntryItem item : prescriptionViewModel.getCurrentAdviceEntries(AdviceEntryType.SPECIAL_ALERT)) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("dsai_id", getJsonNumberOrNull(item.getMasterOptionId()));
            itemObject.put("text", item.getDisplayText());
            specialAlertsArray.put(itemObject);
        }

        bodyObject.put("advice", adviceArray);
        bodyObject.put("followups", followUpsArray);
        bodyObject.put("special_alerts", specialAlertsArray);
        bodyObject.put("reference", getSingleAdviceText(AdviceEntryType.REFERENCE));
        bodyObject.put("prescription_notes", getSingleAdviceText(AdviceEntryType.NOTES));
    }

    @NonNull
    private String getSingleAdviceText(@NonNull AdviceEntryType entryType) {
        ArrayList<AdviceEntryItem> items = prescriptionViewModel.getCurrentAdviceEntries(entryType);

        if (items.isEmpty()) {
            return "";
        }

        return items.get(0).getDisplayText();
    }

    @NonNull
    private JSONArray buildMasterTextArray(@NonNull ClinicalEntryType entryType, @NonNull String idKey) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (ClinicalEntryItem item : prescriptionViewModel.getCurrentClinicalEntries(entryType)) {
            JSONObject itemObject = new JSONObject();
            itemObject.put(idKey, getJsonNumberOrNull(item.getMasterOptionId()));
            itemObject.put("name", item.getDisplayText());
            jsonArray.put(itemObject);
        }

        return jsonArray;
    }

    @NonNull
    private JSONArray buildMasterIdArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (ClinicalEntryItem item : prescriptionViewModel.getCurrentClinicalEntries(ClinicalEntryType.DRUG_HISTORY)) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("medicine_id", getJsonNumberOrNull(item.getMasterOptionId()));
            jsonArray.put(itemObject);
        }

        return jsonArray;
    }

    @NonNull
    private JSONArray buildInvestigationArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (ClinicalEntryItem item : prescriptionViewModel.getCurrentClinicalEntries(ClinicalEntryType.TESTS_INVESTIGATION)) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("pfn_id", getJsonNumberOrNull(item.getMasterOptionId()));
            itemObject.put("notes", item.getNotes());
            jsonArray.put(itemObject);
        }

        return jsonArray;
    }

    @NonNull
    private JSONArray buildTextArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (ClinicalEntryItem item : prescriptionViewModel.getCurrentClinicalEntries(ClinicalEntryType.IMPORTANT_INFO)) {
            JSONObject itemObject = new JSONObject();
            itemObject.put("text", item.getDisplayText());
            jsonArray.put(itemObject);
        }

        return jsonArray;
    }

    @NonNull
    private Object getJsonNumberOrNull(@Nullable String value) throws JSONException {
        if (value == null || value.trim().isEmpty()) {
            return JSONObject.NULL;
        }

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException exception) {
            throw new JSONException("Invalid master ID: " + value);
        }
    }

    // snapshot save
    @Nullable
    private String buildCurrentPrescriptionSnapshot() {
        if (prescriptionViewModel == null) {
            return null;
        }

        try {
            JSONObject snapshotObject = new JSONObject();

            addClinicalDataToBody(snapshotObject);
            addObjectiveInfoToBody(snapshotObject);
            addRxDataToBody(snapshotObject);
            addAdviceDataToBody(snapshotObject);

            snapshotObject.put("reference", getSingleAdviceText(AdviceEntryType.REFERENCE));
            snapshotObject.put("prescription_notes", getSingleAdviceText(AdviceEntryType.NOTES));
            snapshotObject.put("disease_template_id", prescriptionViewModel.getLoadedDiseaseTemplateId());
            snapshotObject.put("treatment_template_id", prescriptionViewModel.getLoadedTreatmentTemplateId());
            snapshotObject.put("prescription_type", p_prescription_type);
            VitalData vitalData = prescriptionViewModel.getCurrentVitals();

            JSONObject vitalObject = new JSONObject();
            vitalObject.put("weight", vitalData == null ? "" : vitalData.getWeight());
            vitalObject.put("height", vitalData == null ? "" : vitalData.getHeight());
            vitalObject.put("systolic", vitalData == null ? "" : vitalData.getSystolic());
            vitalObject.put("diastolic", vitalData == null ? "" : vitalData.getDiastolic());
            vitalObject.put("pulse", vitalData == null ? "" : vitalData.getPulse());
            vitalObject.put("respiration", vitalData == null ? "" : vitalData.getRespiratoryRate());
            vitalObject.put("temperature", vitalData == null ? "" : vitalData.getTemperature());
            vitalObject.put("spo2", vitalData == null ? "" : vitalData.getSpo2());
            vitalObject.put("bmi", vitalData == null ? "" : vitalData.getBmi());
            snapshotObject.put("vitals", vitalObject);

            return snapshotObject.toString();

        } catch (JSONException exception) {
            return null;
        }
    }

    private void markCurrentPrescriptionAsSaved() {
        String currentSnapshot = buildCurrentPrescriptionSnapshot();

        if (currentSnapshot != null) {
            savedPrescriptionSnapshot = currentSnapshot;
        }
    }

    private boolean hasUnsavedPrescriptionChanges() {
        /*
         * Existing prescription এখনো load হচ্ছে অথবা
         * initial snapshot তৈরি হয়নি।
         */
        if (savedPrescriptionSnapshot == null) {
            return false;
        }

        String currentSnapshot = buildCurrentPrescriptionSnapshot();

        /*
         * Snapshot তৈরি করতে সমস্যা হলে নিরাপত্তার জন্য
         * data changed হিসেবে গণ্য হবে।
         */
        if (currentSnapshot == null) {
            return true;
        }

        return !savedPrescriptionSnapshot.equals(currentSnapshot);
    }

    // HELPER METHODS
    private void setPrescriptionGenerating(boolean generating) {
        isGeneratingPrescription = generating;

        prescriptionBuilderFullLayout.setVisibility(generating ? View.GONE : View.VISIBLE);
        prescriptionBuilderProgress.setVisibility(generating ? View.VISIBLE : View.GONE);

        if (!generating) {
            checkboxReviewConfirmation.setEnabled(hasRxData);
            checkboxReviewConfirmation.setAlpha(hasRxData ? 1.0f : 0.45f);
            updateGenerateButtonState();
        }
    }

    public void resetReviewConfirmation() {
        if (checkboxReviewConfirmation != null) {
            checkboxReviewConfirmation.setChecked(false);
        }
    }

    @NonNull
    private String cleanHeaderValue(@Nullable String value) {
        if (value == null) {
            return "";
        }

        return value.trim().replace("\r", " ").replace("\n", " ");
    }

    private boolean isEmpty(@Nullable String value) {
        return value == null || value.trim().isEmpty();
    }

    @NonNull
    private String getJsonResponseValue(@NonNull JSONObject jsonObject, @NonNull String key) {
        if (jsonObject.isNull(key)) {
            return "";
        }

        return jsonObject.optString(key, "");
    }

    @NonNull
    private String getVolleyErrorMessage(@NonNull VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String errorResponse = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject errorObject = new JSONObject(errorResponse);
                String serverMessage = getJsonResponseValue(errorObject, "string_out");

                if (!serverMessage.isEmpty()) {
                    return serverMessage;
                }
            } catch (JSONException ignored) {
            }
        }

        if (error.getLocalizedMessage() != null && !error.getLocalizedMessage().trim().isEmpty()) {
            return error.getLocalizedMessage();
        }

        return "Prescription failed to create. Please check your network and try again.";
    }

    private void showPrescriptionSuccess(@NonNull String message, @NonNull String rvmCode) {
        String successMessage = message.isEmpty() ? "Prescription created successfully." : message;

        if (!rvmCode.isEmpty()) {
            successMessage = successMessage + "\nPrescription Code: " + rvmCode;
            String vn = "Visit: "+ rvmCode;
            visitNo.setText(vn);
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Success")
                .setMessage(successMessage)
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .show();
    }

    private void showPrescriptionUpdateSuccess(@NonNull String message) {
        String successMessage = isEmpty(message) ? "Prescription updated successfully." : message;

        new MaterialAlertDialogBuilder(this)
                .setTitle("Success")
                .setMessage(successMessage)
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .show();
    }

    private void showPrescriptionError(@NonNull String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error!")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (savedPrescriptionSnapshot != null) {
            outState.putString(STATE_SAVED_PRESCRIPTION_SNAPSHOT, savedPrescriptionSnapshot);
        }

        super.onSaveInstanceState(outState);
    }

    private boolean needRequiredInformation() {
        if (isEmpty(doc_id) || isEmpty(pat_id) || isEmpty(ph_id)) {
            return true;
        }

        /*
         * Scheduled patient-এর AD_ID required।
         * Walk-in patient-এর AD_ID থাকবে না।
         */
        return "1".equals(p_pat_type) && isEmpty(ad_id);
    }

    @Override
    protected void onDestroy() {
        prescriptionViewPager.unregisterOnPageChangeCallback(pageChangeCallback);
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
        }

        if (requestQueue != null) {
            requestQueue.cancelAll(CREATE_PRESCRIPTION_REQUEST_TAG);
            requestQueue.cancelAll(UPDATE_PRESCRIPTION_REQUEST_TAG);
        }

        if (existingPrescriptionErrorDialog != null) {
            existingPrescriptionErrorDialog.dismiss();
            existingPrescriptionErrorDialog = null;
        }

        if (mainDataErrorDialog != null) {
            mainDataErrorDialog.dismiss();
            mainDataErrorDialog = null;
        }

        if (unsavedChangesDialog != null) {
            unsavedChangesDialog.dismiss();
            unsavedChangesDialog = null;
        }

        super.onDestroy();
    }
}