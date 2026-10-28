package ttit.com.shuvo.docdiary.dynamic_prescription.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import androidx.lifecycle.ViewModelProvider;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ObjectiveInfoItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.VitalData;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionReviewRowAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.helper.ReviewSectionViews;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ReviewSummaryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalEntryType;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryType;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

public class PrescriptionReviewFragment extends Fragment {

    private static final ClinicalEntryType[] CLINICAL_REVIEW_ENTRY_TYPES = {
            ClinicalEntryType.COMPLAIN,
            ClinicalEntryType.INJURY,
            ClinicalEntryType.DIAGNOSIS,
            ClinicalEntryType.DRUG_HISTORY,
            ClinicalEntryType.CLINICAL_FINDING,
            ClinicalEntryType.TESTS_INVESTIGATION,
            ClinicalEntryType.IMPORTANT_INFO
    };

    private static final AdviceEntryType[] ADVICE_REVIEW_ENTRY_TYPES = {
            AdviceEntryType.ADVICE,
            AdviceEntryType.FOLLOW_UP,
            AdviceEntryType.SPECIAL_ALERT,
            AdviceEntryType.REFERENCE,
            AdviceEntryType.NOTES
    };

    private ReviewSectionViews clinicalSection;
    private ReviewSectionViews rxSection;
    private ReviewSectionViews adviceSection;

    private PrescriptionReviewRowAdapter clinicalAdapter;
    private PrescriptionReviewRowAdapter rxAdapter;
    private PrescriptionReviewRowAdapter adviceAdapter;

    private MaterialCardView reviewStatusCard;
    private MaterialCardView reviewStatusIconCard;

    private AppCompatImageView reviewStatusIcon;

    private TextView reviewStatusValue;
    private TextView reviewStatusMessage;

    private PrescriptionBuilderViewModel prescriptionViewModel;
    private boolean medicinePermissionAllowed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prescription_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        configureReviewSections();
        initializeSharedViewModel();
        loadInitialReviewSummary();
        observeClinicalData();
        observeRxData();
        observeAdviceData();
    }

    private void initializeViews(@NonNull View view) {
        clinicalSection = new ReviewSectionViews(view.findViewById(R.id.review_section_clinical));
        rxSection = new ReviewSectionViews(view.findViewById(R.id.review_section_rx));
        adviceSection = new ReviewSectionViews(view.findViewById(R.id.review_section_advice));

        reviewStatusCard = view.findViewById(R.id.card_review_status);
        reviewStatusIconCard = view.findViewById(R.id.card_review_status_icon);
        reviewStatusIcon = view.findViewById(R.id.iv_review_status_icon);
        reviewStatusValue = view.findViewById(R.id.tv_review_status_value);
        reviewStatusMessage = view.findViewById(R.id.tv_review_status_message);
    }

    private void configureReviewSections() {
        clinicalAdapter = setupReviewSection(
                clinicalSection,
                "Clinical",
                R.drawable.stethoscope,
                0
        );
        rxAdapter = setupReviewSection(
                rxSection,
                "Rx",
                R.drawable.pill,
                1
        );
        adviceAdapter = setupReviewSection(
                adviceSection,
                "Advice & Follow-up",
                R.drawable.ic_advice_24,
                2
        );
    }

    @SuppressLint("SetTextI18n")
    private PrescriptionReviewRowAdapter setupReviewSection(@NonNull ReviewSectionViews section, @NonNull String title, @DrawableRes int iconResource, int targetTab) {
        section.tvTitle.setText(title);
        section.ivIcon.setImageResource(iconResource);
        section.tvBadge.setText("0 items");

        section.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        section.recyclerView.setNestedScrollingEnabled(false);
        section.recyclerView.setHasFixedSize(false);

        PrescriptionReviewRowAdapter adapter = new PrescriptionReviewRowAdapter();

        section.recyclerView.setAdapter(adapter);
        section.btnEdit.setContentDescription("Edit " + title);

        section.btnEdit.setOnClickListener(view -> navigateToTab(targetTab));

        return adapter;
    }

    private void initializeSharedViewModel() {
        prescriptionViewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);
        medicinePermissionAllowed = userInfoLists.get(0).getMed_active().equals("1");
    }

    private void observeClinicalData() {
        prescriptionViewModel.getVitals().observe(getViewLifecycleOwner(), vitalData -> updateClinicalReviewSummary());
        for (ClinicalEntryType entryType : CLINICAL_REVIEW_ENTRY_TYPES) {
            prescriptionViewModel.getClinicalEntries(entryType).observe(getViewLifecycleOwner(), items -> updateClinicalReviewSummary());
        }
        prescriptionViewModel.getObjectiveInfoItems().observe(getViewLifecycleOwner(), items -> updateClinicalReviewSummary());
    }

    private void observeRxData() {
        prescriptionViewModel.getMedicineItems().observe(getViewLifecycleOwner(), items -> updateRxReviewSummary());
        prescriptionViewModel.getTherapyItems().observe(getViewLifecycleOwner(), items -> updateRxReviewSummary());
        prescriptionViewModel.getTreatmentPlanItems().observe(getViewLifecycleOwner(), items -> updateRxReviewSummary());
    }

    private void observeAdviceData() {
        for (AdviceEntryType entryType : ADVICE_REVIEW_ENTRY_TYPES) {
            prescriptionViewModel.getAdviceEntries(entryType).observe(getViewLifecycleOwner(), items -> updateAdviceReviewSummary());
        }
    }

    private void loadInitialReviewSummary() {
        updateClinicalReviewSummary();
        updateRxReviewSummary();
        updateAdviceReviewSummary();
    }

    private void showSectionData(@NonNull ReviewSectionViews section, @NonNull PrescriptionReviewRowAdapter adapter, @NonNull ArrayList<ReviewSummaryItem> items, @NonNull String badgeText) {
        adapter.submitList(items);
        section.tvBadge.setText(badgeText);

        boolean hasRows = !items.isEmpty();

        section.tvEmpty.setVisibility(hasRows ? View.GONE : View.VISIBLE);
        section.recyclerView.setVisibility(hasRows ? View.VISIBLE : View.GONE);
    }

    private void updateClinicalReviewSummary() {
        ArrayList<ReviewSummaryItem> clinicalItems = new ArrayList<>();
        VitalData vitalData = prescriptionViewModel.getVitals().getValue();

        boolean hasVitals = vitalData != null && !vitalData.isEmpty();

        if (hasVitals) {
            clinicalItems.add(new ReviewSummaryItem("Vitals", "Added"));
        }

        for (ClinicalEntryType entryType : CLINICAL_REVIEW_ENTRY_TYPES) {
            int entryCount = getClinicalEntryCount(entryType);
            if (entryCount > 0) {
                clinicalItems.add(new ReviewSummaryItem(entryType.getDisplayName(), String.valueOf(entryCount)));
            }

            if (entryType == ClinicalEntryType.DIAGNOSIS) {

                int objectiveInfoCount = getObjectiveInfoCount();

                if (objectiveInfoCount > 0) {
                    clinicalItems.add(new ReviewSummaryItem("Objective Info", String.valueOf(objectiveInfoCount)));
                }
            }
        }

        int addedSectionCount = clinicalItems.size();

        showSectionData(
                clinicalSection,
                clinicalAdapter,
                clinicalItems,
                getItemCountText(addedSectionCount)
        );
    }

    private void updateRxReviewSummary() {
        ArrayList<ReviewSummaryItem> rxItems = new ArrayList<>();

        int medicineCount = 0;

        if (medicinePermissionAllowed) {
            medicineCount = prescriptionViewModel.getCurrentMedicineItems().size();

            if (medicineCount > 0) {
                rxItems.add(new ReviewSummaryItem("Medicine", String.valueOf(medicineCount)));
            }
        }

        int therapyCount = prescriptionViewModel.getCurrentTherapyItems().size();

        if (therapyCount > 0) {
            rxItems.add(new ReviewSummaryItem("Therapy", String.valueOf(therapyCount)));
        }

        int treatmentPlanCount = prescriptionViewModel.getCurrentTreatmentPlanItems().size();

        if (treatmentPlanCount > 0) {
            rxItems.add(new ReviewSummaryItem("Treatment Plan", String.valueOf(treatmentPlanCount)));
        }

        int addedSectionCount = rxItems.size();

        showSectionData(
                rxSection,
                rxAdapter,
                rxItems,
                getItemCountText(addedSectionCount)
        );

        boolean hasRxData = medicineCount > 0 || therapyCount > 0 || treatmentPlanCount > 0;

        updateReviewStatus(hasRxData);
    }

    private void updateAdviceReviewSummary() {
        ArrayList<ReviewSummaryItem> adviceItems = new ArrayList<>();

        for (AdviceEntryType entryType : ADVICE_REVIEW_ENTRY_TYPES) {
            int entryCount = getAdviceEntryCount(entryType);

            if (entryCount == 0) {
                continue;
            }

            String value;

            if (entryType.isSingleEntry()) {
                value = "Added";
            } else {
                value = String.valueOf(entryCount);
            }
            adviceItems.add(new ReviewSummaryItem(entryType.getDisplayName(), value));
        }

        int addedSectionCount = adviceItems.size();

        showSectionData(
                adviceSection,
                adviceAdapter,
                adviceItems,
                getItemCountText(addedSectionCount)
        );
    }

    private int getClinicalEntryCount(@NonNull ClinicalEntryType entryType) {
        ArrayList<ClinicalEntryItem> items = prescriptionViewModel.getClinicalEntries(entryType).getValue();
        return items == null ? 0 : items.size();
    }

    private int getObjectiveInfoCount() {
        ArrayList<ObjectiveInfoItem> items = prescriptionViewModel.getObjectiveInfoItems().getValue();
        return items == null ? 0 : items.size();
    }

    private int getAdviceEntryCount(@NonNull AdviceEntryType entryType) {
        ArrayList<AdviceEntryItem> items = prescriptionViewModel.getAdviceEntries(entryType).getValue();
        return items == null ? 0 : items.size();
    }

    @NonNull
    private String getItemCountText(int itemCount) {
        return itemCount > 1 ? itemCount+ " items" : itemCount + " item";
    }

    @SuppressLint("SetTextI18n")
    private void updateReviewStatus(boolean hasRxData) {
        if (hasRxData) {
            reviewStatusCard.setCardBackgroundColor(Color.parseColor("#F2FBF6"));
            reviewStatusCard.setStrokeColor(Color.parseColor("#B7E4C7"));

            reviewStatusIconCard.setCardBackgroundColor(Color.parseColor("#27AE60"));
            reviewStatusIcon.setImageResource(R.drawable.ic_review_ready_24);
            reviewStatusValue.setText("Ready for final review");
            reviewStatusValue.setTextColor(Color.parseColor("#1B8F4B"));
            reviewStatusMessage.setText("Verify the details and confirm below to enable Generate.");

        } else {
            reviewStatusCard.setCardBackgroundColor(Color.parseColor("#FFFAEB"));
            reviewStatusCard.setStrokeColor(Color.parseColor("#FEDF89"));

            reviewStatusIconCard.setCardBackgroundColor(Color.parseColor("#F79009"));
            reviewStatusIcon.setImageResource(R.drawable.ic_review_attention_24);
            reviewStatusValue.setText("Rx information required");
            reviewStatusValue.setTextColor(Color.parseColor("#B54708"));
            reviewStatusMessage.setText("Add at least one Rx item before generating.");
        }
    }

    private void navigateToTab(int tabPosition) {
        ViewPager2 prescriptionViewPager = requireActivity().findViewById(R.id.prescription_view_pager);
        prescriptionViewPager.setCurrentItem(tabPosition, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        clinicalSection = null;
        rxSection = null;
        adviceSection = null;

        clinicalAdapter = null;
        rxAdapter = null;
        adviceAdapter = null;

        reviewStatusCard = null;
        reviewStatusIconCard = null;
        reviewStatusIcon = null;
        reviewStatusValue = null;
        reviewStatusMessage = null;

        prescriptionViewModel = null;
    }
}