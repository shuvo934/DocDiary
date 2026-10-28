package ttit.com.shuvo.docdiary.dynamic_prescription.fragments;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionMedicineAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionMedicineBottomSheet;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.MedicineEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionTherapyAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionTherapyBottomSheet;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.TherapyEntryItem;

import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionTreatmentPlanAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionTreatmentPlanBottomSheet;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.TreatmentPlanEntryItem;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.helper.PrescriptionSectionUiHelper;
import ttit.com.shuvo.docdiary.dynamic_prescription.helper.SectionViews;

public class PrescriptionRxFragment extends Fragment {

    private SectionViews medicineSection;
    private SectionViews therapySection;
    private SectionViews treatmentPlanSection;

    private MaterialCardView medicineSegmentCard;
    private MaterialCardView therapySegmentCard;

    private MaterialCardView medicineCountCard;
    private MaterialCardView therapyCountCard;

    private AppCompatImageView medicineSegmentIcon;
    private AppCompatImageView therapySegmentIcon;
    private AppCompatImageView medicineLockedIcon;

    private TextView medicineTitleView;
    private TextView therapyTitleView;
    private TextView medicineCountView;
    private TextView therapyCountView;

    private PrescriptionBuilderViewModel prescriptionViewModel;
    private PrescriptionMedicineAdapter medicineAdapter;
    private PrescriptionTherapyAdapter therapyAdapter;
    private PrescriptionTreatmentPlanAdapter treatmentPlanAdapter;

    private boolean medicinePermissionAllowed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prescription_rx, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeSections(view);
        configureRxSections();
        setupRxToggle(view);
        initializeSharedViewModel();
        configureMedicineData();
        configureTherapyData();
        configureTreatmentPlanData();
    }

    private void initializeSections(@NonNull View view) {
        medicineSection = new SectionViews(view.findViewById(R.id.section_rx_medicine));
        therapySection = new SectionViews(view.findViewById(R.id.section_rx_therapy));
        treatmentPlanSection = new SectionViews(view.findViewById(R.id.section_rx_treatment_plan));

        medicinePermissionAllowed = userInfoLists.get(0).getMed_active().equals("1");
    }

    private void configureRxSections() {
        PrescriptionSectionUiHelper.setupSection(
                medicineSection,
                "Medicine",
                R.drawable.medicine,
                "No medicine added",
                true,
                view -> showAddMedicineBottomSheet()
        );

        PrescriptionSectionUiHelper.setupSection(
                therapySection,
                "Therapy",
                R.drawable.ic_therapy_24,
                "No therapy added",
                true,
                view -> showAddTherapyBottomSheet()
        );

        PrescriptionSectionUiHelper.setupSection(
                treatmentPlanSection,
                "Treatment Plan",
                R.drawable.treatment_plan,
                "No treatment plan added",
                false,
                view -> showAddTreatmentPlanBottomSheet()
        );
    }

    private void setupRxToggle(@NonNull View view) {
        medicineSegmentCard = view.findViewById(R.id.card_rx_medicine_segment);
        therapySegmentCard = view.findViewById(R.id.card_rx_therapy_segment);

        medicineCountCard = view.findViewById(R.id.card_rx_medicine_count);
        therapyCountCard = view.findViewById(R.id.card_rx_therapy_count);

        medicineSegmentIcon = view.findViewById(R.id.iv_rx_medicine_segment);
        therapySegmentIcon = view.findViewById(R.id.iv_rx_therapy_segment);

        medicineLockedIcon = view.findViewById(R.id.iv_rx_medicine_locked);

        medicineTitleView = view.findViewById(R.id.tv_rx_medicine_title);
        therapyTitleView = view.findViewById(R.id.tv_rx_therapy_title);

        medicineCountView = view.findViewById(R.id.tv_rx_medicine_count);
        therapyCountView = view.findViewById(R.id.tv_rx_therapy_count);

        medicineSegmentCard.setOnClickListener(clickedView -> {
            if (!medicinePermissionAllowed) {
                Toast.makeText(requireContext(), "Medicine prescribing is not enabled for your account", Toast.LENGTH_SHORT).show();
                return;
            }
            selectRxSection(true);
        });

        therapySegmentCard.setOnClickListener(clickedView -> selectRxSection(false));

        /*
         * Temporary preview.
         * Database response পাওয়ার পরে true/false পাঠাবেন।
         */
        applyMedicinePermission(medicinePermissionAllowed);
    }

    private void initializeSharedViewModel() {
        prescriptionViewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);
    }

    private void configureMedicineData() {
        medicineAdapter = new PrescriptionMedicineAdapter(new PrescriptionMedicineAdapter.MedicineActionListener() {
            @Override
            public void onEditMedicine(@NonNull MedicineEntryItem item) {
                showEditMedicineBottomSheet(item);
            }

            @Override
            public void onDeleteMedicine(@NonNull MedicineEntryItem item) {
                deleteMedicine(item);
            }
        });

        medicineSection.recyclerView.setAdapter(medicineAdapter);

        prescriptionViewModel.getMedicineItems().observe(getViewLifecycleOwner(), this::updateMedicineSection);
    }

    private void configureTherapyData() {
        therapyAdapter = new PrescriptionTherapyAdapter(new PrescriptionTherapyAdapter.TherapyActionListener() {
            @Override
            public void onEditTherapy(@NonNull TherapyEntryItem item) {
                showEditTherapyBottomSheet(item);
            }

            @Override
            public void onDeleteTherapy(@NonNull TherapyEntryItem item) {
                deleteTherapy(item);
            }
        });

        therapySection.recyclerView.setAdapter(therapyAdapter);

        prescriptionViewModel.getTherapyItems().observe(getViewLifecycleOwner(), this::updateTherapySection);
    }

    private void configureTreatmentPlanData() {
        treatmentPlanAdapter = new PrescriptionTreatmentPlanAdapter(new PrescriptionTreatmentPlanAdapter.TreatmentPlanActionListener() {
            @Override
            public void onEditTreatmentPlan(@NonNull TreatmentPlanEntryItem item) {
                showEditTreatmentPlanBottomSheet(item);
            }

            @Override
            public void onDeleteTreatmentPlan(@NonNull TreatmentPlanEntryItem item) {
                deleteTreatmentPlan(item);
            }
        });

        treatmentPlanSection.recyclerView.setAdapter(treatmentPlanAdapter);

        prescriptionViewModel.getTreatmentPlanItems().observe(getViewLifecycleOwner(), this::updateTreatmentPlanSection);
    }

    private void applyMedicinePermission(boolean medicineAllowed) {
        if (medicineAllowed) {
            medicineCountCard.setVisibility(View.VISIBLE);
            medicineLockedIcon.setVisibility(View.GONE);
            medicineSegmentCard.setContentDescription("Open Medicine");
            selectRxSection(true);
        } else {
            medicineCountCard.setVisibility(View.GONE);
            medicineLockedIcon.setVisibility(View.VISIBLE);
            medicineSegmentCard.setContentDescription("Medicine prescribing locked");
            selectRxSection(false);
        }
    }

    private void selectRxSection(boolean medicineSelected) {
        if (medicineSelected && !medicinePermissionAllowed) {
            return;
        }

        if (medicineSelected) {
            setSegmentStyle(
                    medicineSegmentCard,
                    medicineSegmentIcon,
                    medicineTitleView,
                    medicineCountCard,
                    medicineCountView,
                    true
            );

            setSegmentStyle(
                    therapySegmentCard,
                    therapySegmentIcon,
                    therapyTitleView,
                    therapyCountCard,
                    therapyCountView,
                    false
            );

            showMedicineSection();

        } else {
            if (medicinePermissionAllowed) {
                setSegmentStyle(
                        medicineSegmentCard,
                        medicineSegmentIcon,
                        medicineTitleView,
                        medicineCountCard,
                        medicineCountView,
                        false
                );

            } else {
                setMedicineLockedStyle();
            }

            setSegmentStyle(
                    therapySegmentCard,
                    therapySegmentIcon,
                    therapyTitleView,
                    therapyCountCard,
                    therapyCountView,
                    true
            );

            showTherapySection();
        }
    }

    private void setSegmentStyle(@NonNull MaterialCardView segmentCard, @NonNull AppCompatImageView segmentIcon, @NonNull TextView titleView, @NonNull MaterialCardView countCard, @NonNull TextView countView, boolean selected) {
        int segmentColor = Color.parseColor(selected ? "#27AEB8" : "#FFFFFF");
        int contentColor = Color.parseColor(selected ? "#FFFFFF" : "#667085");
        int countBackground = Color.parseColor(selected ? "#E8F7F8" : "#F2F4F7");
        int countTextColor = Color.parseColor(selected ? "#159AA5" : "#667085");

        segmentCard.setCardBackgroundColor(segmentColor);
        titleView.setTextColor(contentColor);

        ImageViewCompat.setImageTintList(segmentIcon, ColorStateList.valueOf(contentColor));

        countCard.setCardBackgroundColor(countBackground);
        countView.setTextColor(countTextColor);
    }

    private void setMedicineLockedStyle() {
        int lockedBackground = Color.parseColor("#F2F4F7");
        int lockedContent = Color.parseColor("#98A2B3");

        medicineSegmentCard.setCardBackgroundColor(lockedBackground);
        medicineTitleView.setTextColor(lockedContent);
        ImageViewCompat.setImageTintList(medicineSegmentIcon, ColorStateList.valueOf(lockedContent));
    }

    private void showMedicineSection() {
        medicineSection.root.setVisibility(View.VISIBLE);
        therapySection.root.setVisibility(View.GONE);
    }

    private void showTherapySection() {
        medicineSection.root.setVisibility(View.GONE);
        therapySection.root.setVisibility(View.VISIBLE);
    }

    private void updateMedicineSection(@Nullable ArrayList<MedicineEntryItem> medicineItems) {
        int itemCount = medicineItems == null ? 0 : medicineItems.size();

        medicineAdapter.submitList(medicineItems);
        PrescriptionSectionUiHelper.updateItemCount(medicineSection, itemCount);
        medicineCountView.setText(String.valueOf(itemCount));
    }

    private void showAddMedicineBottomSheet() {
        if (!medicinePermissionAllowed) {
            Toast.makeText(requireContext(), "Medicine prescribing is not enabled for your account", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getParentFragmentManager().findFragmentByTag(PrescriptionMedicineBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionMedicineBottomSheet.newAddInstance().show(getParentFragmentManager(), PrescriptionMedicineBottomSheet.TAG);
    }

    private void showEditMedicineBottomSheet(@NonNull MedicineEntryItem item) {
        if (!medicinePermissionAllowed) {
            return;
        }

        if (getParentFragmentManager().findFragmentByTag(PrescriptionMedicineBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionMedicineBottomSheet.newEditInstance(item.getLocalEntryId()).show(getParentFragmentManager(), PrescriptionMedicineBottomSheet.TAG);
    }

    private void deleteMedicine(@NonNull MedicineEntryItem item) {
        prescriptionViewModel.deleteMedicine(item.getLocalEntryId());
    }

    private void updateTherapySection(@Nullable ArrayList<TherapyEntryItem> therapyItems) {
        int itemCount = therapyItems == null ? 0 : therapyItems.size();

        therapyAdapter.submitList(therapyItems);
        PrescriptionSectionUiHelper.updateItemCount(therapySection, itemCount);
        therapyCountView.setText(String.valueOf(itemCount));
    }

    private void showAddTherapyBottomSheet() {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionTherapyBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionTherapyBottomSheet.newAddInstance().show(getParentFragmentManager(), PrescriptionTherapyBottomSheet.TAG);
    }

    private void showEditTherapyBottomSheet(@NonNull TherapyEntryItem item) {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionTherapyBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionTherapyBottomSheet.newEditInstance(item.getLocalEntryId()).show(getParentFragmentManager(), PrescriptionTherapyBottomSheet.TAG);
    }

    private void deleteTherapy(@NonNull TherapyEntryItem item) {
        prescriptionViewModel.deleteTherapy(item.getLocalEntryId());
    }

    private void updateTreatmentPlanSection(@Nullable ArrayList<TreatmentPlanEntryItem> treatmentPlanItems) {
        int itemCount = treatmentPlanItems == null ? 0 : treatmentPlanItems.size();

        treatmentPlanAdapter.submitList(treatmentPlanItems);
        PrescriptionSectionUiHelper.updateItemCount(treatmentPlanSection, itemCount);
    }

    private void showAddTreatmentPlanBottomSheet() {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionTreatmentPlanBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionTreatmentPlanBottomSheet.newAddInstance().show(getParentFragmentManager(), PrescriptionTreatmentPlanBottomSheet.TAG);
    }

    private void showEditTreatmentPlanBottomSheet(@NonNull TreatmentPlanEntryItem item) {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionTreatmentPlanBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionTreatmentPlanBottomSheet.newEditInstance(item.getLocalEntryId()).show(getParentFragmentManager(), PrescriptionTreatmentPlanBottomSheet.TAG);
    }

    private void deleteTreatmentPlan(@NonNull TreatmentPlanEntryItem item) {
        prescriptionViewModel.deleteTreatmentPlan(item.getLocalEntryId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        medicineSection = null;
        therapySection = null;
        treatmentPlanSection = null;

        medicineSegmentCard = null;
        therapySegmentCard = null;

        medicineCountCard = null;
        therapyCountCard = null;

        medicineSegmentIcon = null;
        therapySegmentIcon = null;
        medicineLockedIcon = null;

        medicineTitleView = null;
        therapyTitleView = null;
        medicineCountView = null;
        therapyCountView = null;

        medicineAdapter = null;
        therapyAdapter = null;
        treatmentPlanAdapter = null;
        prescriptionViewModel = null;
    }
}