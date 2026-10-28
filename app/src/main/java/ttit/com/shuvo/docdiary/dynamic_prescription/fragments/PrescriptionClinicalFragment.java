package ttit.com.shuvo.docdiary.dynamic_prescription.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionClinicalEntryAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionVitalsAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionClinicalEntryBottomSheet;
import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionVitalsBottomSheet;
import ttit.com.shuvo.docdiary.dynamic_prescription.helper.PrescriptionSectionUiHelper;
import ttit.com.shuvo.docdiary.dynamic_prescription.helper.SectionViews;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.VitalData;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionObjectiveInfoAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionObjectiveInfoBottomSheet;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ObjectiveInfoItem;

import java.util.EnumMap;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalEntryType;

public class PrescriptionClinicalFragment extends Fragment {

    private static final String SECTION_COMPLAIN = "complain";
    private static final String SECTION_DIAGNOSIS = "diagnosis";
    private static final String SECTION_INJURY = "injury";
    private static final String SECTION_OBJECTIVE_INFO = "objective_info";
    private static final String SECTION_DRUG_HISTORY = "drug_history";
    private static final String SECTION_CLINICAL_FINDINGS = "clinical_findings";
    private static final String SECTION_TESTS_INVESTIGATION = "tests_investigation";
    private static final String SECTION_VITALS = "vitals";
    private static final String SECTION_IMPORTANT = "important_info";

    private SectionViews complaintsSection;
    private SectionViews diagnosisSection;
    private SectionViews injurySection;
    private SectionViews objectiveInfoSection;
    private SectionViews drugHistorySection;
    private SectionViews clinicalFindingsSection;
    private SectionViews testsInvestigationSection;
    private SectionViews vitalsSection;
    private SectionViews importantInfoSection;

    private PrescriptionBuilderViewModel prescriptionViewModel;

    private PrescriptionVitalsAdapter vitalsAdapter;
    private PrescriptionObjectiveInfoAdapter objectiveInfoAdapter;

    private final EnumMap<ClinicalEntryType, SectionViews> clinicalEntrySections = new EnumMap<>(ClinicalEntryType.class);
    private final EnumMap<ClinicalEntryType, PrescriptionClinicalEntryAdapter> clinicalEntryAdapters = new EnumMap<>(ClinicalEntryType.class);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prescription_clinical, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeSectionViews(view);
        configureClinicalSections();
        initializeSharedViewModel();
        configureVitalsData();
        configureObjectiveInfoData();
        configureClinicalEntryData(ClinicalEntryType.COMPLAIN);
        configureClinicalEntryData(ClinicalEntryType.INJURY);
        configureClinicalEntryData(ClinicalEntryType.DIAGNOSIS);
        configureClinicalEntryData(ClinicalEntryType.CLINICAL_FINDING);
        configureClinicalEntryData(ClinicalEntryType.DRUG_HISTORY);
        configureClinicalEntryData(ClinicalEntryType.TESTS_INVESTIGATION);
        configureClinicalEntryData(ClinicalEntryType.IMPORTANT_INFO);
    }

    private void initializeSectionViews(@NonNull View view) {
        complaintsSection = new SectionViews(view.findViewById(R.id.section_complaints));
        diagnosisSection = new SectionViews(view.findViewById(R.id.section_diagnosis));
        injurySection = new SectionViews(view.findViewById(R.id.section_injury));
        objectiveInfoSection = new SectionViews(view.findViewById(R.id.section_objective_info));
        drugHistorySection = new SectionViews(view.findViewById(R.id.section_drug_history));
        clinicalFindingsSection = new SectionViews(view.findViewById(R.id.section_clinical_findings));
        testsInvestigationSection = new SectionViews(view.findViewById(R.id.section_tests_investigation));
        vitalsSection = new SectionViews(view.findViewById(R.id.section_vitals));
        importantInfoSection = new SectionViews(view.findViewById(R.id.section_important_info));

        clinicalEntrySections.clear();

        clinicalEntrySections.put(ClinicalEntryType.COMPLAIN, complaintsSection);
        clinicalEntrySections.put(ClinicalEntryType.INJURY, injurySection);
        clinicalEntrySections.put(ClinicalEntryType.DIAGNOSIS, diagnosisSection);
        clinicalEntrySections.put(ClinicalEntryType.CLINICAL_FINDING, clinicalFindingsSection);
        clinicalEntrySections.put(ClinicalEntryType.DRUG_HISTORY, drugHistorySection);
        clinicalEntrySections.put(ClinicalEntryType.TESTS_INVESTIGATION, testsInvestigationSection);
        clinicalEntrySections.put(ClinicalEntryType.IMPORTANT_INFO, importantInfoSection);
    }

    @SuppressLint("SetTextI18n")
    private void configureClinicalSections() {
        setupClinicalSection(
                vitalsSection,
                "Vitals",
                R.drawable.ic_vitals_24,
                "No vitals added",
                SECTION_VITALS,
                true
        );

        setupClinicalSection(complaintsSection,
                "Complain",
                R.drawable.ic_complaint_24,
                "No complain added",
                SECTION_COMPLAIN,
                false
        );

        setupClinicalSection(
                injurySection,
                "Injury",
                R.drawable.patient,
                "No injury added",
                SECTION_INJURY,
                false
        );

        setupClinicalSection(
                diagnosisSection,
                "Diagnosis",
                R.drawable.diagnosis,
                "No diagnosis added",
                SECTION_DIAGNOSIS,
                false
        );

        setupClinicalSection(
                objectiveInfoSection,
                "Objective Info",
                R.drawable.ic_objective_info_24,
                "No objective information added",
                SECTION_OBJECTIVE_INFO,
                false
        );

        setupClinicalSection(
                drugHistorySection,
                "Drug History",
                R.drawable.medicine,
                "No drug history added",
                SECTION_DRUG_HISTORY,
                false
        );

        setupClinicalSection(
                clinicalFindingsSection,
                "Clinical Findings",
                R.drawable.ic_clinical_findings_24,
                "No clinical finding added",
                SECTION_CLINICAL_FINDINGS,
                false
        );

        setupClinicalSection(
                testsInvestigationSection,
                "Tests / Investigation",
                R.drawable.chemistry,
                "No test or investigation added",
                SECTION_TESTS_INVESTIGATION,
                false
        );
        setupClinicalSection(
                importantInfoSection,
                "Important Info",
                R.drawable.caution,
                "No important info added",
                SECTION_IMPORTANT,
                false
        );
    }

    private void initializeSharedViewModel() {
        prescriptionViewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);
    }

    private void setupClinicalSection(@NonNull SectionViews section, @NonNull String title, int iconResource, @NonNull String emptyMessage, @NonNull String sectionKey, boolean expandedInitially) {
        PrescriptionSectionUiHelper.setupSection(
                section,
                title,
                iconResource,
                emptyMessage,

                expandedInitially,

                view -> handleAddSectionItem(
                        sectionKey,
                        title
                )
        );
    }

    private void handleAddSectionItem(@NonNull String sectionKey, @NonNull String sectionTitle) {
        switch (sectionKey) {

            case SECTION_VITALS:
                showVitalsBottomSheet();
                break;

            case SECTION_COMPLAIN:
                showAddClinicalEntryBottomSheet(ClinicalEntryType.COMPLAIN);
                break;

            case SECTION_INJURY:
                showAddClinicalEntryBottomSheet(ClinicalEntryType.INJURY);
                break;

            case SECTION_DIAGNOSIS:
                showAddClinicalEntryBottomSheet(ClinicalEntryType.DIAGNOSIS);
                break;

            case SECTION_OBJECTIVE_INFO:
                showAddObjectiveInfoBottomSheet();
                break;

            case SECTION_DRUG_HISTORY:
                showAddClinicalEntryBottomSheet(ClinicalEntryType.DRUG_HISTORY);
                break;

            case SECTION_CLINICAL_FINDINGS:
                showAddClinicalEntryBottomSheet(ClinicalEntryType.CLINICAL_FINDING);
                break;

            case SECTION_TESTS_INVESTIGATION:
                showAddClinicalEntryBottomSheet(ClinicalEntryType.TESTS_INVESTIGATION);
                break;

            case SECTION_IMPORTANT:
                showAddClinicalEntryBottomSheet(ClinicalEntryType.IMPORTANT_INFO);
                break;

            default:
                showTemporaryMessage("Add " + sectionTitle);
                break;
        }
    }

    private void showVitalsBottomSheet() {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionVitalsBottomSheet.TAG) != null) {
            return;
        }
        new PrescriptionVitalsBottomSheet().show(getParentFragmentManager(), PrescriptionVitalsBottomSheet.TAG);
    }

    private void configureVitalsData() {
        vitalsAdapter = new PrescriptionVitalsAdapter(this::showVitalsBottomSheet);

        vitalsSection.recyclerView.setAdapter(vitalsAdapter);

        prescriptionViewModel.getVitals().observe(getViewLifecycleOwner(), this::updateVitalsSection);
    }

    private void configureObjectiveInfoData() {
        objectiveInfoAdapter = new PrescriptionObjectiveInfoAdapter(new PrescriptionObjectiveInfoAdapter.ObjectiveActionListener() {

            @Override
            public void onEditObjective(@NonNull ObjectiveInfoItem item) {
                showEditObjectiveInfoBottomSheet(item);
            }

            @Override
            public void onDeleteObjective(@NonNull ObjectiveInfoItem item) {
                deleteObjectiveInfo(item);
            }
        });

        objectiveInfoSection.recyclerView.setAdapter(objectiveInfoAdapter);
        prescriptionViewModel.getObjectiveInfoItems().observe(getViewLifecycleOwner(), this::updateObjectiveInfoSection);
    }

    @SuppressLint("SetTextI18n")
    private void updateVitalsSection(@Nullable VitalData vitalData) {
        boolean hasVitals = vitalData != null && !vitalData.isEmpty();
        if (!hasVitals) {
            vitalsAdapter.submitVitals(null);
            PrescriptionSectionUiHelper.showEmptyState(vitalsSection);
            vitalsSection.tvCount.setText("Not added");
            return;
        }

        vitalsAdapter.submitVitals(vitalData);

        PrescriptionSectionUiHelper.updateItemCount(vitalsSection, 1);
        vitalsSection.tvCount.setText("Added");
    }

    private void updateObjectiveInfoSection(@Nullable ArrayList<ObjectiveInfoItem> items) {
        objectiveInfoAdapter.submitList(items);
        int itemCount = items == null ? 0 : items.size();

        PrescriptionSectionUiHelper.updateItemCount(objectiveInfoSection, itemCount);
    }

    private void showAddObjectiveInfoBottomSheet() {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionObjectiveInfoBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionObjectiveInfoBottomSheet.newAddInstance().show(getParentFragmentManager(), PrescriptionObjectiveInfoBottomSheet.TAG);
    }

    private void showEditObjectiveInfoBottomSheet(@NonNull ObjectiveInfoItem item) {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionObjectiveInfoBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionObjectiveInfoBottomSheet.newEditInstance(item.getLocalEntryId()).show(getParentFragmentManager(), PrescriptionObjectiveInfoBottomSheet.TAG);
    }

    private void deleteObjectiveInfo(@NonNull ObjectiveInfoItem item) {
        prescriptionViewModel.deleteObjectiveInfo(item.getLocalEntryId());
    }

    private void configureClinicalEntryData(@NonNull ClinicalEntryType entryType) {
        SectionViews section = clinicalEntrySections.get(entryType);
        if (section == null) {
            return;
        }

        PrescriptionClinicalEntryAdapter adapter = new PrescriptionClinicalEntryAdapter(new PrescriptionClinicalEntryAdapter.EntryActionListener() {
            @Override
            public void onEditEntry(@NonNull ClinicalEntryItem item) {
                showEditClinicalEntryBottomSheet(entryType, item);
            }

            @Override
            public void onDeleteEntry(@NonNull ClinicalEntryItem item) {
                deleteClinicalEntry(entryType, item);
            }
        });

        clinicalEntryAdapters.put(entryType, adapter);
        section.recyclerView.setAdapter(adapter);
        prescriptionViewModel.getClinicalEntries(entryType).observe(getViewLifecycleOwner(), items -> updateClinicalEntrySection(entryType, items));
    }

    private void updateClinicalEntrySection(@NonNull ClinicalEntryType entryType, @Nullable ArrayList<ClinicalEntryItem> items) {
        SectionViews section = clinicalEntrySections.get(entryType);
        PrescriptionClinicalEntryAdapter adapter = clinicalEntryAdapters.get(entryType);

        if (section == null || adapter == null) {
            return;
        }

        ArrayList<ClinicalEntryItem> safeItems = items == null ? new ArrayList<>() : new ArrayList<>(items);
        adapter.submitList(safeItems);
        PrescriptionSectionUiHelper.updateItemCount(section, safeItems.size());
    }

    private void showAddClinicalEntryBottomSheet(@NonNull ClinicalEntryType entryType) {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionClinicalEntryBottomSheet.TAG) != null) {
            return;
        }
        PrescriptionClinicalEntryBottomSheet.newAddInstance(entryType).show(getParentFragmentManager(), PrescriptionClinicalEntryBottomSheet.TAG);
    }

    private void showEditClinicalEntryBottomSheet(@NonNull ClinicalEntryType entryType, @NonNull ClinicalEntryItem item) {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionClinicalEntryBottomSheet.TAG) != null) {
            return;
        }
        PrescriptionClinicalEntryBottomSheet.newEditInstance(entryType, item.getLocalEntryId()).show(getParentFragmentManager(), PrescriptionClinicalEntryBottomSheet.TAG);
    }

    private void deleteClinicalEntry(@NonNull ClinicalEntryType entryType, @NonNull ClinicalEntryItem item) {
        prescriptionViewModel.deleteClinicalEntry(entryType, item.getLocalEntryId());
    }

    private void showTemporaryMessage(@NonNull String message) {
        Toast.makeText(requireContext(), message + " form will be connected next", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        complaintsSection = null;
        diagnosisSection = null;
        injurySection = null;
        objectiveInfoSection = null;
        drugHistorySection = null;
        clinicalFindingsSection = null;
        testsInvestigationSection = null;
        vitalsSection = null;
        importantInfoSection = null;

        vitalsAdapter = null;
        objectiveInfoAdapter = null;
        clinicalEntryAdapters.clear();
        clinicalEntrySections.clear();
    }
}