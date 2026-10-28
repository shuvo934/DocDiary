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

import java.util.ArrayList;
import java.util.EnumMap;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.adapter.PrescriptionAdviceEntryAdapter;
import ttit.com.shuvo.docdiary.dynamic_prescription.bottomsheet.PrescriptionAdviceEntryBottomSheet;
import ttit.com.shuvo.docdiary.dynamic_prescription.helper.PrescriptionSectionUiHelper;
import ttit.com.shuvo.docdiary.dynamic_prescription.helper.SectionViews;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryType;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionBuilderViewModel;

public class PrescriptionAdviceFragment extends Fragment {

    private SectionViews adviceSection;
    private SectionViews followUpSection;
    private SectionViews specialAlertSection;
    private SectionViews referenceSection;
    private SectionViews notesSection;

    private PrescriptionBuilderViewModel prescriptionViewModel;

    private final EnumMap<AdviceEntryType, SectionViews> adviceEntrySections = new EnumMap<>(AdviceEntryType.class);
    private final EnumMap<AdviceEntryType, PrescriptionAdviceEntryAdapter> adviceEntryAdapters = new EnumMap<>(AdviceEntryType.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prescription_advice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeSectionViews(view);
        configureAdviceSections();
        initializeSharedViewModel();
        configureAdviceEntryData(AdviceEntryType.ADVICE);
        configureAdviceEntryData(AdviceEntryType.FOLLOW_UP);
        configureAdviceEntryData(AdviceEntryType.SPECIAL_ALERT);
        configureAdviceEntryData(AdviceEntryType.REFERENCE);
        configureAdviceEntryData(AdviceEntryType.NOTES);
    }

    private void initializeSectionViews(@NonNull View view) {
        adviceSection = new SectionViews(view.findViewById(R.id.section_advice));
        followUpSection = new SectionViews(view.findViewById(R.id.section_follow_up));
        specialAlertSection = new SectionViews(view.findViewById(R.id.section_special_alert));
        referenceSection = new SectionViews(view.findViewById(R.id.section_reference));
        notesSection = new SectionViews(view.findViewById(R.id.section_notes));

        adviceEntrySections.clear();

        adviceEntrySections.put(AdviceEntryType.ADVICE, adviceSection);
        adviceEntrySections.put(AdviceEntryType.FOLLOW_UP, followUpSection);
        adviceEntrySections.put(AdviceEntryType.SPECIAL_ALERT, specialAlertSection);
        adviceEntrySections.put(AdviceEntryType.REFERENCE, referenceSection);
        adviceEntrySections.put(AdviceEntryType.NOTES, notesSection);
    }

    @SuppressLint("SetTextI18n")
    private void configureAdviceSections() {
        setupAdviceSection(
                adviceSection,
                AdviceEntryType.ADVICE,
                "No advice added"
        );

        setupAdviceSection(
                followUpSection,
                AdviceEntryType.FOLLOW_UP,
                "No follow-up added"
        );

        setupAdviceSection(
                specialAlertSection,
                AdviceEntryType.SPECIAL_ALERT,
                "No special alert added"
        );

        setupAdviceSection(
                referenceSection,
                AdviceEntryType.REFERENCE,
                "No reference added"
        );

        setupAdviceSection(
                notesSection,
                AdviceEntryType.NOTES,
                "No notes added"
        );
    }

    @SuppressLint("SetTextI18n")
    private void setupAdviceSection(@NonNull SectionViews section, @NonNull AdviceEntryType entryType, @NonNull String emptyMessage) {
        PrescriptionSectionUiHelper.setupSection(section, entryType.getDisplayName(), entryType.getIconResource(), emptyMessage, false,
                view -> handleAdviceSectionAdd(entryType));
        if (entryType.isSingleEntry()) {
            section.tvCount.setText("Not added");
        }
    }

    private void initializeSharedViewModel() {
        prescriptionViewModel = new ViewModelProvider(requireActivity()).get(PrescriptionBuilderViewModel.class);
    }

    // =====================================================
    // Section adapters and observers
    // =====================================================
    private void configureAdviceEntryData(@NonNull AdviceEntryType entryType) {
        SectionViews section = adviceEntrySections.get(entryType);

        if (section == null) {
            return;
        }

        PrescriptionAdviceEntryAdapter adapter = new PrescriptionAdviceEntryAdapter(new PrescriptionAdviceEntryAdapter.EntryActionListener() {
            @Override
            public void onEditEntry(@NonNull AdviceEntryItem item) {
                showEditAdviceEntryBottomSheet(entryType, item);
            }

            @Override
            public void onDeleteEntry(@NonNull AdviceEntryItem item) {
                deleteAdviceEntry(entryType, item);
            }
        });

        adviceEntryAdapters.put(entryType, adapter);
        section.recyclerView.setAdapter(adapter);
        prescriptionViewModel.getAdviceEntries(entryType).observe(getViewLifecycleOwner(),
                items -> updateAdviceEntrySection(entryType, items));
    }

    private void updateAdviceEntrySection(@NonNull AdviceEntryType entryType, @Nullable ArrayList<AdviceEntryItem> items) {
        SectionViews section = adviceEntrySections.get(entryType);
        PrescriptionAdviceEntryAdapter adapter = adviceEntryAdapters.get(entryType);

        if (section == null || adapter == null) {
            return;
        }

        ArrayList<AdviceEntryItem> safeItems = items == null ? new ArrayList<>() : new ArrayList<>(items);
        adapter.submitList(safeItems);

        /*
         * Count, empty message ও RecyclerView
         * visibility একসঙ্গে update হবে।
         */
        PrescriptionSectionUiHelper.updateItemCount(section, safeItems.size());
        if (entryType.isSingleEntry()) {
            section.tvCount.setText(safeItems.isEmpty() ? "Not added" : "Added");
        }
    }

    // =====================================================
    // BottomSheet
    // =====================================================
    private void handleAdviceSectionAdd(@NonNull AdviceEntryType entryType) {
        /*
         * Multiple-item section সবসময় নতুন
         * item add করবে।
         */
        if (!entryType.isSingleEntry()) {
            showAddAdviceEntryBottomSheet(entryType);
            return;
        }

        ArrayList<AdviceEntryItem> currentItems = prescriptionViewModel.getAdviceEntries(entryType).getValue();

        /*
         * Reference/Notes এখনো add না হলে
         * Add BottomSheet খুলবে।
         */
        if (currentItems == null || currentItems.isEmpty()) {
            showAddAdviceEntryBottomSheet(entryType);
            return;
        }

        /*
         * আগে থেকে item থাকলে নতুন item না বানিয়ে
         * existing item Edit mode-এ খুলবে।
         */
        showEditAdviceEntryBottomSheet(entryType, currentItems.get(0));
    }

    private void showAddAdviceEntryBottomSheet(@NonNull AdviceEntryType entryType) {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionAdviceEntryBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionAdviceEntryBottomSheet.newAddInstance(entryType).show(getParentFragmentManager(), PrescriptionAdviceEntryBottomSheet.TAG);
    }

    private void showEditAdviceEntryBottomSheet(@NonNull AdviceEntryType entryType, @NonNull AdviceEntryItem item) {
        if (getParentFragmentManager().findFragmentByTag(PrescriptionAdviceEntryBottomSheet.TAG) != null) {
            return;
        }

        PrescriptionAdviceEntryBottomSheet.newEditInstance(entryType, item.getLocalEntryId()).show(getParentFragmentManager(), PrescriptionAdviceEntryBottomSheet.TAG);
    }

    private void deleteAdviceEntry(@NonNull AdviceEntryType entryType, @NonNull AdviceEntryItem item) {
        prescriptionViewModel.deleteAdviceEntry(entryType, item.getLocalEntryId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adviceSection = null;
        followUpSection = null;
        specialAlertSection = null;
        referenceSection = null;
        notesSection = null;

        prescriptionViewModel = null;

        adviceEntryAdapters.clear();
        adviceEntrySections.clear();
    }
}