package ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryType;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalEntryType;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalOption;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ObjectiveInfoItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionHistoryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPatientDetails;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPatientItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.TherapyEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.VitalData;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.MedicineEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.TreatmentPlanEntryItem;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionTemplateItem;

import ttit.com.shuvo.docdiary.dynamic_prescription.repository.PrescriptionMainDataRepository;
import ttit.com.shuvo.docdiary.dynamic_prescription.repository.PrescriptionPatientRepository;

public class PrescriptionBuilderViewModel extends AndroidViewModel {

    private static final int TOTAL_MAIN_DATA_REQUESTS = 17;
    private final PrescriptionMainDataRepository mainDataRepository;
    private final PrescriptionPatientRepository patientRepository;


    private final MutableLiveData<VitalData> vitals = new MutableLiveData<>(null);

    private final EnumMap<ClinicalEntryType, MutableLiveData<ArrayList<ClinicalEntryItem>>> clinicalEntryItems = new EnumMap<>(ClinicalEntryType.class);
    private final MutableLiveData<ArrayList<ObjectiveInfoItem>> objectiveInfoItems = new MutableLiveData<>(new ArrayList<>());

    private final EnumMap<AdviceEntryType, MutableLiveData<ArrayList<AdviceEntryItem>>> adviceEntryItems = new EnumMap<>(AdviceEntryType.class);

    private final MutableLiveData<ArrayList<MedicineEntryItem>> medicineItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<TherapyEntryItem>> therapyItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<TreatmentPlanEntryItem>> treatmentPlanItems = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> diseaseTemplateApplyLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> diseaseTemplateApplyError = new MutableLiveData<>("");

    private final MutableLiveData<Boolean> treatmentTemplateApplyLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> treatmentTemplateApplyError = new MutableLiveData<>("");

    private final MutableLiveData<Boolean> diseaseTemplateApplySuccess = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> treatmentTemplateApplySuccess = new MutableLiveData<>(false);

    /*
     * Walk-in patient selection data।
     */
    private final MutableLiveData<ArrayList<PrescriptionPatientItem>> patientItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> patientListLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> patientListError = new MutableLiveData<>("");

    /*
     * Recent এবং search request-এর মধ্যে পুরোনো response
     * যেন নতুন result replace করতে না পারে।
     */
    private int patientListRequestVersion = 0;


    private final MutableLiveData<PrescriptionPatientDetails> patientDetails = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> patientDetailsLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> patientDetailsError = new MutableLiveData<>("");

    private int patientDetailsRequestVersion = 0;

    /**
     * Initializing Main Data Repository
     */
    public PrescriptionBuilderViewModel(@NonNull Application application) {
        super(application);
        for (ClinicalEntryType entryType : ClinicalEntryType.values()) {
            clinicalEntryItems.put(entryType, new MutableLiveData<>(new ArrayList<>()));
        }
        for (AdviceEntryType entryType : AdviceEntryType.values()) {
            adviceEntryItems.put(entryType, new MutableLiveData<>(new ArrayList<>()));
        }
        mainDataRepository = PrescriptionMainDataRepository.getInstance(application);
        patientRepository = PrescriptionPatientRepository.getInstance(application);
    }

    /*
     * Vitals
     */
    public LiveData<VitalData> getVitals() {
        return vitals;
    }

    @Nullable
    public VitalData getCurrentVitals() {
        return vitals.getValue();
    }

    public void saveVitals(@NonNull VitalData vitalData) {
        vitals.setValue(vitalData);
    }

    public void clearVitals() {
        vitals.setValue(null);
    }

    /**
     * CRUD Methods for Clinical Entry
     */
    @NonNull
    public LiveData<ArrayList<ClinicalEntryItem>> getClinicalEntries(@NonNull ClinicalEntryType entryType) {
        return getClinicalEntryLiveData(entryType);
    }

    public boolean addClinicalEntry(@NonNull ClinicalEntryType entryType, @NonNull ClinicalEntryItem newItem) {
        ArrayList<ClinicalEntryItem> currentItems = getCurrentClinicalEntries(entryType);
        if (containsDuplicate(currentItems, newItem, null)) {
            return false;
        }

        currentItems.add(newItem);
        publishClinicalEntries(entryType, currentItems);
        return true;
    }

    public boolean updateClinicalEntry(@NonNull ClinicalEntryType entryType, @NonNull ClinicalEntryItem updatedItem) {
        ArrayList<ClinicalEntryItem> currentItems = getCurrentClinicalEntries(entryType);
        String targetEntryId = updatedItem.getLocalEntryId();
        if (containsDuplicate(currentItems, updatedItem, targetEntryId)) {
            return false;
        }

        for (int index = 0; index < currentItems.size(); index++) {
            ClinicalEntryItem currentItem = currentItems.get(index);
            if (currentItem.getLocalEntryId().equals(targetEntryId)) {
                currentItems.set(index, updatedItem);
                publishClinicalEntries(entryType, currentItems);
                return true;
            }
        }

        return false;
    }

    public void deleteClinicalEntry(@NonNull ClinicalEntryType entryType, @NonNull String localEntryId) {
        ArrayList<ClinicalEntryItem> currentItems = getCurrentClinicalEntries(entryType);
        for (int index = 0; index < currentItems.size(); index++) {
            ClinicalEntryItem currentItem = currentItems.get(index);
            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                currentItems.remove(index);
                publishClinicalEntries(entryType, currentItems);
                return;
            }
        }
    }

    @Nullable
    public ClinicalEntryItem findClinicalEntryById(@NonNull ClinicalEntryType entryType, @NonNull String localEntryId) {
        ArrayList<ClinicalEntryItem> currentItems = getCurrentClinicalEntries(entryType);
        for (ClinicalEntryItem currentItem : currentItems) {
            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                return currentItem;
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    public void clearClinicalEntries(@NonNull ClinicalEntryType entryType) {
        publishClinicalEntries(entryType, new ArrayList<>());
    }

    @NonNull
    private MutableLiveData<ArrayList<ClinicalEntryItem>> getClinicalEntryLiveData(@NonNull ClinicalEntryType entryType) {
        MutableLiveData<ArrayList<ClinicalEntryItem>> liveData = clinicalEntryItems.get(entryType);
        if (liveData == null) {
            liveData = new MutableLiveData<>(new ArrayList<>());
            clinicalEntryItems.put(entryType, liveData);
        }

        return liveData;
    }

    @NonNull
    public ArrayList<ClinicalEntryItem> getCurrentClinicalEntries(@NonNull ClinicalEntryType entryType) {
        ArrayList<ClinicalEntryItem> currentItems = getClinicalEntryLiveData(entryType).getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    private void publishClinicalEntries(@NonNull ClinicalEntryType entryType, @NonNull ArrayList<ClinicalEntryItem> items) {
        getClinicalEntryLiveData(entryType).setValue(new ArrayList<>(items));
    }

    private boolean containsDuplicate(@NonNull ArrayList<ClinicalEntryItem> items, @NonNull ClinicalEntryItem candidate, @Nullable String ignoredLocalEntryId) {
        for (ClinicalEntryItem existingItem : items) {
            if (existingItem.getLocalEntryId().equals(ignoredLocalEntryId)) {
                continue;
            }

            if (existingItem.hasSameValue(candidate)) {
                return true;
            }
        }

        return false;
    }

    /**
     * CRUD Methods for Objective Info items।
     */
    public LiveData<ArrayList<ObjectiveInfoItem>> getObjectiveInfoItems() {
        return objectiveInfoItems;
    }

    @NonNull
    public ArrayList<ObjectiveInfoItem> getCurrentObjectiveInfoItems() {
        ArrayList<ObjectiveInfoItem> currentItems = objectiveInfoItems.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    public boolean addObjectiveInfo(@NonNull ObjectiveInfoItem newItem) {
        ArrayList<ObjectiveInfoItem> currentItems = getCurrentObjectiveInfoItems();

        if (containsDuplicateObjectiveInfo(currentItems, newItem, null)) {
            return false;
        }

        currentItems.add(newItem);
        publishObjectiveInfoItems(currentItems);
        return true;
    }

    public boolean updateObjectiveInfo(@NonNull ObjectiveInfoItem updatedItem) {
        ArrayList<ObjectiveInfoItem> currentItems = getCurrentObjectiveInfoItems();
        String targetEntryId = updatedItem.getLocalEntryId();

        if (containsDuplicateObjectiveInfo(currentItems, updatedItem, targetEntryId)) {
            return false;
        }

        for (int index = 0; index < currentItems.size(); index++) {
            ObjectiveInfoItem currentItem = currentItems.get(index);
            if (currentItem.getLocalEntryId().equals(targetEntryId)) {
                currentItems.set(index, updatedItem);
                publishObjectiveInfoItems(currentItems);
                return true;
            }
        }

        return false;
    }

    public void deleteObjectiveInfo(@NonNull String localEntryId) {
        ArrayList<ObjectiveInfoItem> currentItems = getCurrentObjectiveInfoItems();

        for (int index = 0; index < currentItems.size(); index++) {
            ObjectiveInfoItem currentItem = currentItems.get(index);
            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                currentItems.remove(index);
                publishObjectiveInfoItems(currentItems);
                return;
            }
        }
    }

    @Nullable
    public ObjectiveInfoItem findObjectiveInfoById(@NonNull String localEntryId) {
        ArrayList<ObjectiveInfoItem> currentItems = getCurrentObjectiveInfoItems();

        for (ObjectiveInfoItem currentItem : currentItems) {
            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                return currentItem;
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    public void clearObjectiveInfoItems() {
        publishObjectiveInfoItems(new ArrayList<>());
    }

    private void publishObjectiveInfoItems(@NonNull ArrayList<ObjectiveInfoItem> items) {
        objectiveInfoItems.setValue(new ArrayList<>(items));
    }

    private boolean containsDuplicateObjectiveInfo(@NonNull ArrayList<ObjectiveInfoItem> items, @NonNull ObjectiveInfoItem candidate, @Nullable String ignoredLocalEntryId) {
        for (ObjectiveInfoItem existingItem : items) {
            if (existingItem.getLocalEntryId().equals(ignoredLocalEntryId)) {
                continue;
            }

            if (existingItem.hasSameValue(candidate)) {
                return true;
            }
        }

        return false;
    }

    /**
     * CRUD Methods for Advice Entry
     */
    @NonNull
    public LiveData<ArrayList<AdviceEntryItem>> getAdviceEntries(@NonNull AdviceEntryType entryType) {
        return getAdviceEntryLiveData(entryType);
    }

    public boolean addAdviceEntry(@NonNull AdviceEntryType entryType, @NonNull AdviceEntryItem newItem) {
        ArrayList<AdviceEntryItem> currentItems = getCurrentAdviceEntries(entryType);
        if (entryType.isSingleEntry() && !currentItems.isEmpty()) {
            return false;
        }
        if (containsDuplicateAdviceEntry(currentItems, newItem, null)) {
            return false;
        }

        currentItems.add(newItem);
        publishAdviceEntries(entryType, currentItems);
        return true;
    }

    public boolean updateAdviceEntry(@NonNull AdviceEntryType entryType, @NonNull AdviceEntryItem updatedItem) {
        ArrayList<AdviceEntryItem> currentItems = getCurrentAdviceEntries(entryType);
        String targetEntryId = updatedItem.getLocalEntryId();
        if (containsDuplicateAdviceEntry(currentItems, updatedItem, targetEntryId)) {
            return false;
        }

        for (int index = 0; index < currentItems.size(); index++) {
            AdviceEntryItem currentItem = currentItems.get(index);
            if (currentItem.getLocalEntryId().equals(targetEntryId)) {
                currentItems.set(index, updatedItem);
                publishAdviceEntries(entryType, currentItems);
                return true;
            }
        }

        return false;
    }

    public void deleteAdviceEntry(@NonNull AdviceEntryType entryType, @NonNull String localEntryId) {
        ArrayList<AdviceEntryItem> currentItems = getCurrentAdviceEntries(entryType);

        for (int index = 0; index < currentItems.size(); index++) {
            AdviceEntryItem currentItem = currentItems.get(index);
            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                currentItems.remove(index);
                publishAdviceEntries(entryType, currentItems);
                return;
            }
        }
    }

    @Nullable
    public AdviceEntryItem findAdviceEntryById(@NonNull AdviceEntryType entryType, @NonNull String localEntryId) {
        ArrayList<AdviceEntryItem> currentItems = getCurrentAdviceEntries(entryType);

        for (AdviceEntryItem currentItem : currentItems) {
            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                return currentItem;
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    public void clearAdviceEntries(@NonNull AdviceEntryType entryType) {
        publishAdviceEntries(entryType, new ArrayList<>());
    }

    @NonNull
    private MutableLiveData<ArrayList<AdviceEntryItem>> getAdviceEntryLiveData(@NonNull AdviceEntryType entryType) {
        MutableLiveData<ArrayList<AdviceEntryItem>> liveData = adviceEntryItems.get(entryType);

        if (liveData == null) {
            liveData = new MutableLiveData<>(new ArrayList<>());
            adviceEntryItems.put(entryType, liveData);
        }

        return liveData;
    }

    @NonNull
    public ArrayList<AdviceEntryItem> getCurrentAdviceEntries(@NonNull AdviceEntryType entryType) {
        ArrayList<AdviceEntryItem> currentItems = getAdviceEntryLiveData(entryType).getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    private void publishAdviceEntries(@NonNull AdviceEntryType entryType, @NonNull ArrayList<AdviceEntryItem> items) {
        getAdviceEntryLiveData(entryType).setValue(new ArrayList<>(items));
    }

    private boolean containsDuplicateAdviceEntry(@NonNull ArrayList<AdviceEntryItem> items, @NonNull AdviceEntryItem candidate, @Nullable String ignoredLocalEntryId) {
        for (AdviceEntryItem existingItem : items) {
            if (existingItem.getLocalEntryId().equals(ignoredLocalEntryId)) {
                continue;
            }

            if (existingItem.hasSameValue(candidate)) {
                return true;
            }
        }

        return false;
    }

    /**
     * CRUD Methods for Medicine Entry
     */
    @NonNull
    public LiveData<ArrayList<MedicineEntryItem>> getMedicineItems() {
        return medicineItems;
    }

    @NonNull
    public ArrayList<MedicineEntryItem> getCurrentMedicineItems() {
        ArrayList<MedicineEntryItem> currentItems = medicineItems.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    public boolean addMedicine(@NonNull MedicineEntryItem newItem) {
        ArrayList<MedicineEntryItem> currentItems = getCurrentMedicineItems();

        if (containsDuplicateMedicine(currentItems, newItem, null)) {
            return false;
        }

        currentItems.add(newItem);
        publishMedicineItems(currentItems);
        return true;
    }

    public boolean updateMedicine(@NonNull MedicineEntryItem updatedItem) {
        ArrayList<MedicineEntryItem> currentItems = getCurrentMedicineItems();
        String targetEntryId = updatedItem.getLocalEntryId();

        if (containsDuplicateMedicine(currentItems, updatedItem, targetEntryId)) {
            return false;
        }

        for (int index = 0; index < currentItems.size(); index++) {
            MedicineEntryItem currentItem = currentItems.get(index);

            if (currentItem.getLocalEntryId().equals(targetEntryId)) {
                currentItems.set(index, updatedItem);
                publishMedicineItems(currentItems);
                return true;
            }
        }

        return false;
    }

    public void deleteMedicine(@NonNull String localEntryId) {
        ArrayList<MedicineEntryItem> currentItems = getCurrentMedicineItems();

        for (int index = 0; index < currentItems.size(); index++) {
            MedicineEntryItem currentItem = currentItems.get(index);

            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                currentItems.remove(index);
                publishMedicineItems(currentItems);
                return;
            }
        }
    }

    @Nullable
    public MedicineEntryItem findMedicineEntryById(@NonNull String localEntryId) {
        ArrayList<MedicineEntryItem> currentItems = getCurrentMedicineItems();

        for (MedicineEntryItem currentItem : currentItems) {
            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                return currentItem;
            }
        }

        return null;
    }

    private void publishMedicineItems(@NonNull ArrayList<MedicineEntryItem> items) {
        medicineItems.setValue(new ArrayList<>(items));
    }

    private boolean containsDuplicateMedicine(@NonNull ArrayList<MedicineEntryItem> items, @NonNull MedicineEntryItem candidate, @Nullable String ignoredLocalEntryId) {
        for (MedicineEntryItem existingItem : items) {
            if (existingItem.getLocalEntryId().equals(ignoredLocalEntryId)) {
                continue;
            }

            if (existingItem.hasSameMedicine(candidate)) {
                return true;
            }
        }

        return false;
    }

    /**
     * CRUD Methods for Therapy Entry
     */
    @NonNull
    public LiveData<ArrayList<TherapyEntryItem>> getTherapyItems() {
        return therapyItems;
    }

    @NonNull
    public ArrayList<TherapyEntryItem> getCurrentTherapyItems() {
        ArrayList<TherapyEntryItem> currentItems = therapyItems.getValue();

        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    public boolean addTherapy(@NonNull TherapyEntryItem newItem) {
        ArrayList<TherapyEntryItem> currentItems = getCurrentTherapyItems();

        if (containsDuplicateTherapy(currentItems, newItem, null)) {
            return false;
        }

        currentItems.add(newItem);
        publishTherapyItems(currentItems);
        return true;
    }

    public boolean updateTherapy(@NonNull TherapyEntryItem updatedItem) {
        ArrayList<TherapyEntryItem> currentItems = getCurrentTherapyItems();
        String targetEntryId = updatedItem.getLocalEntryId();

        if (containsDuplicateTherapy(currentItems, updatedItem, targetEntryId)) {
            return false;
        }

        for (int index = 0; index < currentItems.size(); index++) {
            TherapyEntryItem currentItem = currentItems.get(index);

            if (currentItem.getLocalEntryId().equals(targetEntryId)) {
                currentItems.set(index, updatedItem);
                publishTherapyItems(currentItems);
                return true;
            }
        }

        return false;
    }

    public void deleteTherapy(@NonNull String localEntryId) {
        ArrayList<TherapyEntryItem> currentItems = getCurrentTherapyItems();

        for (int index = 0; index < currentItems.size(); index++) {
            TherapyEntryItem currentItem = currentItems.get(index);

            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                currentItems.remove(index);
                publishTherapyItems(currentItems);
                return;
            }
        }
    }

    @Nullable
    public TherapyEntryItem findTherapyEntryById(@NonNull String localEntryId) {
        ArrayList<TherapyEntryItem> currentItems = getCurrentTherapyItems();

        for (TherapyEntryItem currentItem : currentItems) {
            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                return currentItem;
            }
        }

        return null;
    }

    private void publishTherapyItems(@NonNull ArrayList<TherapyEntryItem> items) {
        therapyItems.setValue(new ArrayList<>(items));
    }

    private boolean containsDuplicateTherapy(@NonNull ArrayList<TherapyEntryItem> items, @NonNull TherapyEntryItem candidate, @Nullable String ignoredLocalEntryId) {
        for (TherapyEntryItem existingItem : items) {
            if (existingItem.getLocalEntryId().equals(ignoredLocalEntryId)) {
                continue;
            }

            if (existingItem.hasSameTherapy(candidate)) {
                return true;
            }
        }

        return false;
    }

    /**
     * CRUD Methods for Treatment Plan Entry
     */
    @NonNull
    public LiveData<ArrayList<TreatmentPlanEntryItem>> getTreatmentPlanItems() {
        return treatmentPlanItems;
    }

    @NonNull
    public ArrayList<TreatmentPlanEntryItem> getCurrentTreatmentPlanItems() {
        ArrayList<TreatmentPlanEntryItem> currentItems = treatmentPlanItems.getValue();

        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    public boolean addTreatmentPlan(@NonNull TreatmentPlanEntryItem newItem) {
        ArrayList<TreatmentPlanEntryItem> currentItems = getCurrentTreatmentPlanItems();

        if (containsDuplicateTreatmentPlan(currentItems, newItem, null)) {
            return false;
        }

        currentItems.add(newItem);
        publishTreatmentPlanItems(currentItems);
        return true;
    }

    public boolean updateTreatmentPlan(@NonNull TreatmentPlanEntryItem updatedItem) {
        ArrayList<TreatmentPlanEntryItem> currentItems = getCurrentTreatmentPlanItems();
        String targetEntryId = updatedItem.getLocalEntryId();

        if (containsDuplicateTreatmentPlan(currentItems, updatedItem, targetEntryId)) {
            return false;
        }

        for (int index = 0; index < currentItems.size(); index++) {
            TreatmentPlanEntryItem currentItem = currentItems.get(index);

            if (currentItem.getLocalEntryId().equals(targetEntryId)) {
                currentItems.set(index, updatedItem);
                publishTreatmentPlanItems(currentItems);
                return true;
            }
        }

        return false;
    }

    public void deleteTreatmentPlan(@NonNull String localEntryId) {
        ArrayList<TreatmentPlanEntryItem> currentItems = getCurrentTreatmentPlanItems();

        for (int index = 0; index < currentItems.size(); index++) {
            TreatmentPlanEntryItem currentItem = currentItems.get(index);

            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                currentItems.remove(index);
                publishTreatmentPlanItems(currentItems);
                return;
            }
        }
    }

    @Nullable
    public TreatmentPlanEntryItem findTreatmentPlanEntryById(@NonNull String localEntryId) {
        ArrayList<TreatmentPlanEntryItem> currentItems = getCurrentTreatmentPlanItems();

        for (TreatmentPlanEntryItem currentItem : currentItems) {
            if (currentItem.getLocalEntryId().equals(localEntryId)) {
                return currentItem;
            }
        }

        return null;
    }

    private void publishTreatmentPlanItems(@NonNull ArrayList<TreatmentPlanEntryItem> items) {
        treatmentPlanItems.setValue(new ArrayList<>(items));
    }

    private boolean containsDuplicateTreatmentPlan(@NonNull ArrayList<TreatmentPlanEntryItem> items, @NonNull TreatmentPlanEntryItem candidate, @Nullable String ignoredLocalEntryId) {
        for (TreatmentPlanEntryItem existingItem : items) {
            if (existingItem.getLocalEntryId().equals(ignoredLocalEntryId)) {
                continue;
            }

            if (existingItem.hasSameValue(candidate)) {
                return true;
            }
        }

        return false;
    }



    // -------- MAIN DATA ---------
    private final MutableLiveData<ArrayList<ClinicalOption>> complainOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> injuryOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> diagnosisOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> clinicalFindingOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> drugHistoryOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> testInvestigationOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> objectiveOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> muscleJointOptions = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<ArrayList<ClinicalOption>> adviceOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> followUpOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> specialAlertOptions = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<ArrayList<ClinicalOption>> medicineOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> doseOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> medicineTimingOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> medicineRouteOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> therapyOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<ClinicalOption>> treatmentPlanOptions = new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> mainDataLoading = new MutableLiveData<>(false);
    private final MutableLiveData<ArrayList<String>> mainDataErrors = new MutableLiveData<>(new ArrayList<>());
    private final ArrayList<String> currentMainDataErrors = new ArrayList<>();
    private boolean mainDataLoadStarted = false;
    private int completedMainDataRequests = 0;
    private String currentDoctorId = "";

    /*
     * Objective ID অনুযায়ী ইতোমধ্যে load হওয়া
     * Objective Value list এখানে cache থাকবে।
     */
    private final HashMap<String, ArrayList<ClinicalOption>> objectiveValueCache = new HashMap<>();

    /*
     * একই Objective-এর API request একাধিকবার
     * একসঙ্গে call হওয়া বন্ধ করবে।
     */
    private final HashSet<String> loadingObjectiveValueIds = new HashSet<>();

    /*
     * বর্তমানে BottomSheet-এ selected Objective-এর
     * Value list ও loading/error state।
     */
    private final MutableLiveData<ArrayList<ClinicalOption>> objectiveValueOptions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> objectiveValueLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> objectiveValueError = new MutableLiveData<>("");

    private String activeObjectiveId = "";

    // Existing Prescription
    private final MutableLiveData<Boolean> existingPrescriptionLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> existingPrescriptionLoaded = new MutableLiveData<>(false);
    private final MutableLiveData<String> existingPrescriptionError = new MutableLiveData<>("");

    private boolean existingPrescriptionLoadStarted = false;
    private String loadedRvmId = "";
    private String loadedRvmCode = "";

    // Disease Template list state
    private final MutableLiveData<ArrayList<PrescriptionTemplateItem>> diseaseTemplateItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> diseaseTemplateLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> diseaseTemplateError = new MutableLiveData<>("");

    private boolean diseaseTemplateListLoaded = false;

    // Treatment Template list state
    private final MutableLiveData<ArrayList<PrescriptionTemplateItem>> treatmentTemplateItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> treatmentTemplateLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> treatmentTemplateError = new MutableLiveData<>("");

    private boolean treatmentTemplateListLoaded = false;
    private String treatmentTemplateListDiseaseId = "";
    private int treatmentTemplateRequestVersion = 0;

    // Successfully applied template information
    private String loadedDiseaseTemplateId = "";
    private String loadedDiseaseTemplateName = "";
    private String loadedTreatmentTemplateId = "";
    private String loadedTreatmentTemplateName = "";

    // Prescription History List
    private final MutableLiveData<ArrayList<PrescriptionHistoryItem>> prescriptionHistoryItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> prescriptionHistoryLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> prescriptionHistoryError = new MutableLiveData<>("");

    private int prescriptionHistoryRequestVersion = 0;

    // Previous Prescription Load
    private final MutableLiveData<Boolean> previousPrescriptionLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> previousPrescriptionLoadSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> previousPrescriptionError = new MutableLiveData<>("");

    private int previousPrescriptionRequestVersion = 0;

    // Main data load
    public void loadMainDataIfNeeded(@NonNull String doctorId) {
        if (mainDataLoadStarted) {
            return;
        }

        String validDoctorId = doctorId.trim();
        if (validDoctorId.isEmpty()) {
            ArrayList<String> errors = new ArrayList<>();
            errors.add("Doctor information not found");

            mainDataErrors.setValue(errors);
            mainDataLoading.setValue(false);

            return;
        }

        currentDoctorId = validDoctorId;

        mainDataLoadStarted = true;
        completedMainDataRequests = 0;
        currentMainDataErrors.clear();
        complainOptions.setValue(new ArrayList<>());
        injuryOptions.setValue(new ArrayList<>());
        diagnosisOptions.setValue(new ArrayList<>());
        clinicalFindingOptions.setValue(new ArrayList<>());
        drugHistoryOptions.setValue(new ArrayList<>());
        testInvestigationOptions.setValue(new ArrayList<>());
        objectiveOptions.setValue(new ArrayList<>());
        muscleJointOptions.setValue(new ArrayList<>());

        objectiveValueCache.clear();
        loadingObjectiveValueIds.clear();
        clearObjectiveValueSelection();

        adviceOptions.setValue(new ArrayList<>());
        followUpOptions.setValue(new ArrayList<>());
        specialAlertOptions.setValue(new ArrayList<>());

        medicineOptions.setValue(new ArrayList<>());
        doseOptions.setValue(new ArrayList<>());
        medicineTimingOptions.setValue(new ArrayList<>());
        medicineRouteOptions.setValue(new ArrayList<>());
        therapyOptions.setValue(new ArrayList<>());
        treatmentPlanOptions.setValue(new ArrayList<>());

        mainDataLoading.setValue(true);
        mainDataErrors.setValue(new ArrayList<>());

        loadComplainOptions(validDoctorId);
        loadInjuryOptions(validDoctorId);
        loadDiagnosisOptions(validDoctorId);
        loadClinicalFindingOptions(validDoctorId);
        loadDrugHistoryOptions();
        loadTestInvestigationOptions();
        loadObjectiveOptions(validDoctorId);
        loadMuscleJointOptions();

        loadAdviceOptions(validDoctorId);
        loadFollowUpOptions(validDoctorId);
        loadSpecialAlertOptions(validDoctorId);

        loadMedicineOptions();
        loadDoseOptions();
        loadMedicineTimingOptions();
        loadMedicineRouteOptions();
        loadTherapyOptions(validDoctorId);
        loadTreatmentPlanOptions(validDoctorId);
    }

    private void loadComplainOptions(@NonNull String doctorId) {
        mainDataRepository.getComplainOptions(doctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                complainOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Complain", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                complainOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Complain", errorMessage);
            }
        });
    }

    private void loadInjuryOptions(@NonNull String doctorId) {
        mainDataRepository.getInjuryOptions(doctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                injuryOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Injury", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                injuryOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Injury", errorMessage);
            }
        });
    }

    private void loadDiagnosisOptions(@NonNull String doctorId) {
        mainDataRepository.getDiagnosisOptions(doctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                diagnosisOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Diagnosis", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                diagnosisOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Diagnosis", errorMessage);
            }
        });
    }

    private void loadClinicalFindingOptions(@NonNull String doctorId) {
        mainDataRepository.getClinicalFindingOptions(doctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                clinicalFindingOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Clinical Findings", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                clinicalFindingOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Clinical Findings", errorMessage);
            }
        });
    }

    private void loadDrugHistoryOptions() {
        mainDataRepository.getDrugHistoryOptions(new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                drugHistoryOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Drug History", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                drugHistoryOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Drug History", errorMessage);
            }
        });
    }

    private void loadTestInvestigationOptions() {
        mainDataRepository.getTestInvestigationOptions(new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                testInvestigationOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Tests / Investigation", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                testInvestigationOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Tests / Investigation", errorMessage);
            }
        });
    }

    private void loadObjectiveOptions(@NonNull String doctorId) {
        mainDataRepository.getObjectiveOptions(doctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                objectiveOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Objective Info", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                objectiveOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Objective Info", errorMessage);
            }
        });
    }

    private void loadMuscleJointOptions() {
        mainDataRepository.getMuscleJointOptions(new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                muscleJointOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Muscle / Joint", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                muscleJointOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Muscle / Joint", errorMessage);
            }
        });
    }

    public void loadObjectiveValueOptions(@NonNull String objectiveId) {
        String validObjectiveId = objectiveId.trim();

        if (validObjectiveId.isEmpty()) {
            clearObjectiveValueSelection();
            return;
        }

        activeObjectiveId = validObjectiveId;
        objectiveValueError.setValue("");

        /*
         * আগে load করা থাকলে API call না করে
         * cache থেকে result দেওয়া হবে।
         */
        ArrayList<ClinicalOption> cachedOptions = objectiveValueCache.get(validObjectiveId);

        if (cachedOptions != null) {
            objectiveValueOptions.setValue(new ArrayList<>(cachedOptions));
            objectiveValueLoading.setValue(false);
            return;
        }

        if (currentDoctorId.isEmpty()) {
            objectiveValueOptions.setValue(new ArrayList<>());
            objectiveValueLoading.setValue(false);
            objectiveValueError.setValue("Doctor information not found");
            return;
        }

        /*
         * একই Objective-এর request ইতোমধ্যে চললে
         * নতুন duplicate request পাঠানো হবে না।
         */
        if (loadingObjectiveValueIds.contains(validObjectiveId)) {
            objectiveValueOptions.setValue(new ArrayList<>());
            objectiveValueLoading.setValue(true);
            return;
        }

        objectiveValueOptions.setValue(new ArrayList<>());
        objectiveValueLoading.setValue(true);
        loadingObjectiveValueIds.add(validObjectiveId);

        mainDataRepository.getObjectiveValueOptions(validObjectiveId, currentDoctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                loadingObjectiveValueIds.remove(validObjectiveId);
                ArrayList<ClinicalOption> loadedOptions = new ArrayList<>(data);

                /*
                 * সফল empty list-ও cache হবে।
                 */
                objectiveValueCache.put(validObjectiveId, loadedOptions);

                /*
                 * Request চলাকালে user অন্য Objective
                 * select করলে পুরোনো result UI-তে বসবে না।
                 */
                if (!validObjectiveId.equals(activeObjectiveId)) {
                    return;
                }

                objectiveValueOptions.setValue(new ArrayList<>(loadedOptions));

                objectiveValueLoading.setValue(false);
                objectiveValueError.setValue("");
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                loadingObjectiveValueIds.remove(validObjectiveId);

                if (!validObjectiveId.equals(activeObjectiveId)) {
                    return;
                }

                objectiveValueOptions.setValue(new ArrayList<>());
                objectiveValueLoading.setValue(false);
                objectiveValueError.setValue(errorMessage);
            }
        });
    }

    public void clearObjectiveValueSelection() {
        activeObjectiveId = "";
        objectiveValueOptions.setValue(new ArrayList<>());
        objectiveValueLoading.setValue(false);
        objectiveValueError.setValue("");
    }

    private void loadAdviceOptions(@NonNull String doctorId) {
        mainDataRepository.getAdviceOptions(doctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                adviceOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Advice", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                adviceOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Advice", errorMessage);
            }
        });
    }

    private void loadFollowUpOptions(@NonNull String doctorId) {
        mainDataRepository.getFollowUpOptions(doctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                followUpOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Follow-up", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                followUpOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Follow-up", errorMessage);
            }
        });
    }

    private void loadSpecialAlertOptions(@NonNull String doctorId) {
        mainDataRepository.getSpecialAlertOptions(doctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                specialAlertOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Special Alert", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                specialAlertOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Special Alert", errorMessage);
            }
        });
    }

    private void loadMedicineOptions() {
        mainDataRepository.getMedicineOptions(new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                medicineOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Medicine", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                medicineOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Medicine", errorMessage);
            }
        });
    }

    private void loadDoseOptions() {
        mainDataRepository.getDoseOptions(new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                doseOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Dose", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                doseOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Dose", errorMessage);
            }
        });
    }

    private void loadMedicineTimingOptions() {
        mainDataRepository.getMedicineTimingOptions(new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                medicineTimingOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Medicine Timing", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                medicineTimingOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Medicine Timing", errorMessage);
            }
        });
    }

    private void loadMedicineRouteOptions() {
        mainDataRepository.getMedicineRouteOptions(new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                medicineRouteOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Medicine Route", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                medicineRouteOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Medicine Route", errorMessage);
            }
        });
    }

    private void loadTherapyOptions(@NonNull String doctorId) {
        mainDataRepository.getTherapyOptions(doctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                therapyOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Therapy", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                therapyOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Therapy", errorMessage);
            }
        });
    }

    private void loadTreatmentPlanOptions(@NonNull String doctorId) {
        mainDataRepository.getTreatmentPlanOptions(doctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<ClinicalOption> data) {
                treatmentPlanOptions.setValue(new ArrayList<>(data));
                markMainDataRequestCompleted("Treatment Plan", null);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                treatmentPlanOptions.setValue(new ArrayList<>());
                markMainDataRequestCompleted("Treatment Plan", errorMessage);
            }
        });
    }

    private void markMainDataRequestCompleted(@NonNull String sectionName, String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            currentMainDataErrors.add(sectionName + ": " + errorMessage);
        }

        completedMainDataRequests++;

        if (completedMainDataRequests >= TOTAL_MAIN_DATA_REQUESTS) {
            mainDataLoading.setValue(false);
            mainDataErrors.setValue(new ArrayList<>(currentMainDataErrors));
        }
    }

    public void retryMainData(@NonNull String doctorId) {
        if (Boolean.TRUE.equals(mainDataLoading.getValue())) {
            return;
        }

        mainDataLoadStarted = false;
        loadMainDataIfNeeded(doctorId);
    }

    public void retryObjectiveValueOptions() {
        if (activeObjectiveId.isEmpty()) {
            return;
        }

        String objectiveIdToRetry = activeObjectiveId;
        objectiveValueCache.remove(objectiveIdToRetry);
        loadObjectiveValueOptions(objectiveIdToRetry);
    }

    // Existing Prescription Load
    public void loadExistingPrescriptionIfNeeded(@NonNull String phId, @NonNull String patientId, @NonNull String adId) {
        if (existingPrescriptionLoadStarted) {
            return;
        }

        String validPhId = phId.trim();
        String validPatientId = patientId.trim();
        String validAdId = adId.trim();

        if (validPhId.isEmpty() || validPatientId.isEmpty() || validAdId.isEmpty()) {
            existingPrescriptionError.setValue("Required patient or visit information is missing");
            existingPrescriptionLoading.setValue(false);
            return;
        }

        existingPrescriptionLoadStarted = true;
        existingPrescriptionLoading.setValue(true);
        existingPrescriptionLoaded.setValue(false);
        existingPrescriptionError.setValue("");

        mainDataRepository.getExistingPrescription(validPhId, validPatientId, validAdId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull JSONObject data) {
                try {
                    parseAndPublishExistingPrescription(data);
                    existingPrescriptionLoading.setValue(false);
                    existingPrescriptionLoaded.setValue(true);
                } catch (Exception exception) {
                    existingPrescriptionLoadStarted = false;
                    existingPrescriptionLoading.setValue(false);
                    existingPrescriptionLoaded.setValue(false);

                    String message = exception.getLocalizedMessage();
                    existingPrescriptionError.setValue(message == null || message.trim().isEmpty() ? "Unable to parse prescription data" : message);
                }
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                existingPrescriptionLoadStarted = false;
                existingPrescriptionLoading.setValue(false);
                existingPrescriptionLoaded.setValue(false);
                existingPrescriptionError.setValue(errorMessage);
            }
        });
    }

    private void parseAndPublishExistingPrescription(@NonNull JSONObject dataObject) throws Exception {
        String parsedRvmId = getJsonString(dataObject, "rvm_id");
        String parsedRvmCode = getJsonString(dataObject, "rvm_code");
        String parsedTdId = getJsonString(dataObject, "td_id");
        String parsedTdName = getJsonString(dataObject, "td_name");
        String parsedTtId = getJsonString(dataObject, "tt_id");
        String parsedTtName = getJsonString(dataObject, "tt_name");

        if (parsedRvmId.isEmpty()) {
            throw new Exception("Prescription ID not found");
        }

        VitalData parsedVitals = parseVitals(dataObject.optJSONObject("vitals"));
        ArrayList<ObjectiveInfoItem> parsedObjectives = parseObjectiveInfo(dataObject);
        ArrayList<MedicineEntryItem> parsedMedicines = parseMedicines(dataObject);
        ArrayList<TherapyEntryItem> parsedTherapies = parseTherapies(dataObject);
        ArrayList<TreatmentPlanEntryItem> parsedTreatmentPlans = parseTreatmentPlans(dataObject);

        EnumMap<ClinicalEntryType, ArrayList<ClinicalEntryItem>> parsedClinicalItems = new EnumMap<>(ClinicalEntryType.class);
        parsedClinicalItems.put(ClinicalEntryType.COMPLAIN, parseClinicalItems(dataObject, "complaints", "cm_id", "name", null));
        parsedClinicalItems.put(ClinicalEntryType.INJURY, parseClinicalItems(dataObject, "injuries", "injury_id", "name", null));
        parsedClinicalItems.put(ClinicalEntryType.DIAGNOSIS, parseClinicalItems(dataObject, "diagnosis", "dm_id", "name", null));
        parsedClinicalItems.put(ClinicalEntryType.CLINICAL_FINDING, parseClinicalItems(dataObject, "clinical_findings", "cfm_id", "name", null));
        parsedClinicalItems.put(ClinicalEntryType.DRUG_HISTORY, parseClinicalItems(dataObject, "drug_history", "medicine_id", "name", null));
        parsedClinicalItems.put(ClinicalEntryType.TESTS_INVESTIGATION, parseClinicalItems(dataObject, "investigations", "pfn_id", "name", "notes"));
        parsedClinicalItems.put(ClinicalEntryType.IMPORTANT_INFO, parseClinicalItems(dataObject, "important_info", null, "text", null));

        EnumMap<AdviceEntryType, ArrayList<AdviceEntryItem>> parsedAdviceItems = new EnumMap<>(AdviceEntryType.class);
        parsedAdviceItems.put(AdviceEntryType.ADVICE, parseAdviceItems(dataObject, "advice", null));
        parsedAdviceItems.put(AdviceEntryType.FOLLOW_UP, parseAdviceItems(dataObject, "followups", "dfi_id"));
        parsedAdviceItems.put(AdviceEntryType.SPECIAL_ALERT, parseAdviceItems(dataObject, "special_alerts", "dsai_id"));
        parsedAdviceItems.put(AdviceEntryType.REFERENCE, createSingleAdviceItem(getJsonString(dataObject, "reference")));
        parsedAdviceItems.put(AdviceEntryType.NOTES, createSingleAdviceItem(getJsonString(dataObject, "notes")));

        loadedRvmId = parsedRvmId;
        loadedRvmCode = parsedRvmCode;
        loadedDiseaseTemplateId = parsedTdId;
        loadedDiseaseTemplateName = parsedTdName;

        loadedTreatmentTemplateId = parsedTtId;
        loadedTreatmentTemplateName = parsedTtName;

        vitals.setValue(parsedVitals);
        publishObjectiveInfoItems(parsedObjectives);
        publishMedicineItems(parsedMedicines);
        publishTherapyItems(parsedTherapies);
        publishTreatmentPlanItems(parsedTreatmentPlans);

        for (ClinicalEntryType entryType : ClinicalEntryType.values()) {
            ArrayList<ClinicalEntryItem> items = parsedClinicalItems.get(entryType);
            publishClinicalEntries(entryType, items == null ? new ArrayList<>() : items);
        }

        for (AdviceEntryType entryType : AdviceEntryType.values()) {
            ArrayList<AdviceEntryItem> items = parsedAdviceItems.get(entryType);
            publishAdviceEntries(entryType, items == null ? new ArrayList<>() : items);
        }
    }

    @NonNull
    private ArrayList<ClinicalEntryItem> parseClinicalItems(@NonNull JSONObject dataObject, @NonNull String arrayKey, @Nullable String idKey, @NonNull String textKey, @Nullable String notesKey) {
        ArrayList<ClinicalEntryItem> items = new ArrayList<>();
        JSONArray jsonArray = dataObject.optJSONArray(arrayKey);

        if (jsonArray == null) {
            return items;
        }

        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject itemObject = jsonArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String displayText = getJsonString(itemObject, textKey);

            if (displayText.isEmpty()) {
                continue;
            }

            String masterId = idKey == null ? null : getNullableJsonString(itemObject, idKey);
            String notes = notesKey == null ? "" : getJsonString(itemObject, notesKey);
            items.add(new ClinicalEntryItem(UUID.randomUUID().toString(), masterId, displayText, notes));
        }

        return items;
    }

    @NonNull
    private ArrayList<AdviceEntryItem> parseAdviceItems(@NonNull JSONObject dataObject, @NonNull String arrayKey, @Nullable String idKey) {
        ArrayList<AdviceEntryItem> items = new ArrayList<>();
        JSONArray jsonArray = dataObject.optJSONArray(arrayKey);

        if (jsonArray == null) {
            return items;
        }

        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject itemObject = jsonArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String displayText = getJsonString(itemObject, "text");

            if (displayText.isEmpty()) {
                continue;
            }

            String masterId = idKey == null ? null : getNullableJsonString(itemObject, idKey);
            items.add(new AdviceEntryItem(UUID.randomUUID().toString(), masterId, displayText));
        }

        return items;
    }

    @NonNull
    private ArrayList<AdviceEntryItem> createSingleAdviceItem(@NonNull String displayText) {
        ArrayList<AdviceEntryItem> items = new ArrayList<>();

        if (!displayText.isEmpty()) {
            items.add(new AdviceEntryItem(UUID.randomUUID().toString(), null, displayText));
        }

        return items;
    }

    @Nullable
    private VitalData parseVitals(@Nullable JSONObject vitalObject) {
        if (vitalObject == null) {
            return null;
        }

        VitalData vitalData = new VitalData(
                getJsonString(vitalObject, "systolic"),
                getJsonString(vitalObject, "diastolic"),
                getJsonString(vitalObject, "pulse"),
                getJsonString(vitalObject, "temperature"),
                getJsonString(vitalObject, "spo2"),
                getJsonString(vitalObject, "respiration"),
                getJsonString(vitalObject, "height"),
                getJsonString(vitalObject, "weight"),
                getJsonString(vitalObject, "bmi")
        );

        return vitalData.isEmpty() && vitalData.getBmi().isEmpty() ? null : vitalData;
    }

    @NonNull
    private ArrayList<ObjectiveInfoItem> parseObjectiveInfo(@NonNull JSONObject dataObject) {
        ArrayList<ObjectiveInfoItem> items = new ArrayList<>();
        JSONArray jsonArray = dataObject.optJSONArray("objective_info");

        if (jsonArray == null) {
            return items;
        }

        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject itemObject = jsonArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String objectiveId = getJsonString(itemObject, "om_id");
            String objectiveName = getJsonString(itemObject, "om_name");
            String objectiveValueId = getJsonString(itemObject, "od_id");
            String objectiveValueName = getJsonString(itemObject, "od_name");

            if (objectiveId.isEmpty() || objectiveName.isEmpty()) {
                continue;
            }

            items.add(new ObjectiveInfoItem(
                    UUID.randomUUID().toString(),
                    objectiveId,
                    objectiveName,
                    objectiveValueId,
                    objectiveValueName,
                    getNullableJsonString(itemObject, "mjl_id"),
                    getJsonString(itemObject, "mjl_name"),
                    getJsonString(itemObject, "notes")
            ));
        }

        return items;
    }

    @NonNull
    private ArrayList<MedicineEntryItem> parseMedicines(@NonNull JSONObject dataObject) {
        ArrayList<MedicineEntryItem> items = new ArrayList<>();
        JSONArray jsonArray = dataObject.optJSONArray("medications");

        if (jsonArray == null) {
            return items;
        }

        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject itemObject = jsonArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String medicineId = getJsonString(itemObject, "medication_id");
            String medicineName = getJsonString(itemObject, "medicine_name");
            String doseId = getJsonString(itemObject, "dose_id");
            String doseName = getJsonString(itemObject, "dose_name");
            String timingId = getJsonString(itemObject, "timing_id");
            String timingName = getJsonString(itemObject, "timing_name");
            String routeId = getJsonString(itemObject, "route_id");
            String routeName = getJsonString(itemObject, "route_name");

            if (medicineId.isEmpty() || medicineName.isEmpty() || doseId.isEmpty() || timingId.isEmpty() || routeId.isEmpty()) {
                continue;
            }

            items.add(new MedicineEntryItem(
                    UUID.randomUUID().toString(),
                    medicineId,
                    medicineName,
                    doseId,
                    doseName,
                    timingId,
                    timingName,
                    routeId,
                    routeName,
                    getJsonString(itemObject, "duration"),
                    getJsonString(itemObject, "notes")
            ));
        }

        return items;
    }

    @NonNull
    private ArrayList<TherapyEntryItem> parseTherapies(@NonNull JSONObject dataObject) {
        ArrayList<TherapyEntryItem> items = new ArrayList<>();
        JSONArray jsonArray = dataObject.optJSONArray("therapies");

        if (jsonArray == null) {
            return items;
        }

        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject itemObject = jsonArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String therapyId = getJsonString(itemObject, "tm_id");
            String therapyName = getJsonString(itemObject, "name");
            String therapyNotes = getJsonString(itemObject, "notes");

            if (therapyId.isEmpty() || therapyName.isEmpty()) {
                continue;
            }

            items.add(new TherapyEntryItem(UUID.randomUUID().toString(), therapyId, therapyName, therapyNotes));
        }

        return items;
    }

    @NonNull
    private ArrayList<TreatmentPlanEntryItem> parseTreatmentPlans(@NonNull JSONObject dataObject) {
        ArrayList<TreatmentPlanEntryItem> items = new ArrayList<>();
        JSONArray jsonArray = dataObject.optJSONArray("treatment_plans");

        if (jsonArray == null) {
            return items;
        }

        for (int index = 0; index < jsonArray.length(); index++) {
            JSONObject itemObject = jsonArray.optJSONObject(index);

            if (itemObject == null) {
                continue;
            }

            String displayText = getJsonString(itemObject, "name");

            if (displayText.isEmpty()) {
                continue;
            }

            items.add(new TreatmentPlanEntryItem(
                    UUID.randomUUID().toString(),
                    getNullableJsonString(itemObject, "tpm_id"),
                    displayText
            ));
        }

        return items;
    }

    @NonNull
    private String getJsonString(@NonNull JSONObject object, @NonNull String key) {
        if (!object.has(key) || object.isNull(key)) {
            return "";
        }

        String value = object.optString(key, "");
        return "null".equalsIgnoreCase(value) ? "" : value.trim();
    }

    @Nullable
    private String getNullableJsonString(@NonNull JSONObject object, @NonNull String key) {
        String value = getJsonString(object, key);
        return value.isEmpty() ? null : value;
    }

    public void retryExistingPrescription(@NonNull String phId, @NonNull String patientId, @NonNull String adId) {
        existingPrescriptionLoadStarted = false;
        loadExistingPrescriptionIfNeeded(phId, patientId, adId);
    }

    public LiveData<Boolean> getExistingPrescriptionLoading() {
        return existingPrescriptionLoading;
    }

    public LiveData<Boolean> getExistingPrescriptionLoaded() {
        return existingPrescriptionLoaded;
    }

    public LiveData<String> getExistingPrescriptionError() {
        return existingPrescriptionError;
    }

    @NonNull
    public String getLoadedRvmId() {
        return loadedRvmId;
    }

    @NonNull
    public String getLoadedRvmCode() {
        return loadedRvmCode;
    }


    // Prescription History
    public LiveData<ArrayList<PrescriptionHistoryItem>> getPrescriptionHistoryItems() {
        return prescriptionHistoryItems;
    }

    public LiveData<Boolean> getPrescriptionHistoryLoading() {
        return prescriptionHistoryLoading;
    }

    public LiveData<String> getPrescriptionHistoryError() {
        return prescriptionHistoryError;
    }

    @NonNull
    public ArrayList<PrescriptionHistoryItem> getCurrentPrescriptionHistoryItems() {
        ArrayList<PrescriptionHistoryItem> currentItems = prescriptionHistoryItems.getValue();

        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    public void loadPrescriptionHistory(@NonNull String phId, @NonNull String patientId, String docId) {
        String validPhId = phId.trim();
        String validPatientId = patientId.trim();
        String validDocId = docId.trim();

        int currentRequestVersion = ++prescriptionHistoryRequestVersion;

        prescriptionHistoryItems.setValue(new ArrayList<>());
        prescriptionHistoryError.setValue("");

        if (validPhId.isEmpty() || validPatientId.isEmpty()) {
            prescriptionHistoryLoading.setValue(false);
            prescriptionHistoryError.setValue("Patient information not found");
            return;
        }

        prescriptionHistoryLoading.setValue(true);

        mainDataRepository.getPrescriptionHistory(validPhId, validPatientId, validDocId,  new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<PrescriptionHistoryItem> data) {
                if (currentRequestVersion != prescriptionHistoryRequestVersion) {
                    return;
                }

                prescriptionHistoryItems.setValue(new ArrayList<>(data));
                prescriptionHistoryLoading.setValue(false);
                prescriptionHistoryError.setValue("");
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                if (currentRequestVersion != prescriptionHistoryRequestVersion) {
                    return;
                }

                prescriptionHistoryItems.setValue(new ArrayList<>());
                prescriptionHistoryLoading.setValue(false);
                prescriptionHistoryError.setValue(errorMessage);
            }
        });
    }

    public void retryPrescriptionHistory(@NonNull String phId, @NonNull String patientId, @NonNull String docId) {
        loadPrescriptionHistory(phId, patientId, docId);
    }

    public void clearPrescriptionHistoryError() {
        prescriptionHistoryError.setValue("");
    }

    public void clearPrescriptionHistoryState() {
        prescriptionHistoryRequestVersion++;

        prescriptionHistoryItems.setValue(new ArrayList<>());
        prescriptionHistoryLoading.setValue(false);
        prescriptionHistoryError.setValue("");
    }


    // Previous Prescription Load
    public LiveData<Boolean> getPreviousPrescriptionLoading() {
        return previousPrescriptionLoading;
    }

    public LiveData<Boolean> getPreviousPrescriptionLoadSuccess() {
        return previousPrescriptionLoadSuccess;
    }

    public LiveData<String> getPreviousPrescriptionError() {
        return previousPrescriptionError;
    }

    public void loadPreviousPrescription(@NonNull String sourceRvmId) {
        String validSourceRvmId = sourceRvmId.trim();

        if (validSourceRvmId.isEmpty()) {
            previousPrescriptionError.setValue("Prescription information not found");
            return;
        }

        if (Boolean.TRUE.equals(previousPrescriptionLoading.getValue())) {
            return;
        }

        int currentRequestVersion = ++previousPrescriptionRequestVersion;

        previousPrescriptionLoading.setValue(true);
        previousPrescriptionLoadSuccess.setValue(false);
        previousPrescriptionError.setValue("");

        mainDataRepository.getPreviousPrescription(validSourceRvmId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull JSONObject dataObject) {
                if (currentRequestVersion != previousPrescriptionRequestVersion) {
                    return;
                }

                try {
                    parseAndPublishPreviousPrescription(dataObject, validSourceRvmId);

                    previousPrescriptionLoading.setValue(false);
                    previousPrescriptionLoadSuccess.setValue(true);

                } catch (Exception exception) {
                    previousPrescriptionLoading.setValue(false);
                    previousPrescriptionLoadSuccess.setValue(false);

                    String message = exception.getLocalizedMessage();

                    previousPrescriptionError.setValue(message == null || message.trim().isEmpty() ? "Unable to parse previous prescription" : message);
                }
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                if (currentRequestVersion != previousPrescriptionRequestVersion) {
                    return;
                }

                previousPrescriptionLoading.setValue(false);
                previousPrescriptionLoadSuccess.setValue(false);
                previousPrescriptionError.setValue(errorMessage);
            }
        });
    }

    public void clearPreviousPrescriptionLoadResult() {
        previousPrescriptionLoadSuccess.setValue(false);
        previousPrescriptionError.setValue("");
    }

    public void cancelPreviousPrescriptionLoad() {
        previousPrescriptionRequestVersion++;

        previousPrescriptionLoading.setValue(false);
        previousPrescriptionLoadSuccess.setValue(false);
        previousPrescriptionError.setValue("");
    }

    private void parseAndPublishPreviousPrescription(@NonNull JSONObject dataObject, @NonNull String expectedRvmId) throws Exception {
        String sourceRvmId = getJsonString(dataObject, "rvm_id");

        if (sourceRvmId.isEmpty()) {
            throw new Exception("Previous prescription ID not found");
        }

        if (!expectedRvmId.equals(sourceRvmId)) {
            throw new Exception("Loaded prescription does not match the selected history");
        }

        /*
         * আগে সব data parse হবে।
         * কোনো parsing error হলে current prescription অপরিবর্তিত থাকবে।
         */
        VitalData parsedVitals = parseVitals(dataObject.optJSONObject("vitals"));
        ArrayList<ObjectiveInfoItem> parsedObjectives = parseObjectiveInfo(dataObject);
        ArrayList<MedicineEntryItem> parsedMedicines = parseMedicines(dataObject);
        ArrayList<TherapyEntryItem> parsedTherapies = parseTherapies(dataObject);
        ArrayList<TreatmentPlanEntryItem> parsedTreatmentPlans = parseTreatmentPlans(dataObject);

        EnumMap<ClinicalEntryType, ArrayList<ClinicalEntryItem>> parsedClinicalItems = new EnumMap<>(ClinicalEntryType.class);

        parsedClinicalItems.put(ClinicalEntryType.COMPLAIN, parseClinicalItems(dataObject, "complaints", "cm_id", "name", null));
        parsedClinicalItems.put(ClinicalEntryType.INJURY, parseClinicalItems(dataObject, "injuries", "injury_id", "name", null));
        parsedClinicalItems.put(ClinicalEntryType.DIAGNOSIS, parseClinicalItems(dataObject, "diagnosis", "dm_id", "name", null));
        parsedClinicalItems.put(ClinicalEntryType.CLINICAL_FINDING, parseClinicalItems(dataObject, "clinical_findings", "cfm_id", "name", null));
        parsedClinicalItems.put(ClinicalEntryType.DRUG_HISTORY, parseClinicalItems(dataObject, "drug_history", "medicine_id", "name", null));
        parsedClinicalItems.put(ClinicalEntryType.TESTS_INVESTIGATION, parseClinicalItems(dataObject, "investigations", "pfn_id", "name", "notes"));
        parsedClinicalItems.put(ClinicalEntryType.IMPORTANT_INFO, parseClinicalItems(dataObject, "important_info", null, "text", null));

        EnumMap<AdviceEntryType, ArrayList<AdviceEntryItem>> parsedAdviceItems = new EnumMap<>(AdviceEntryType.class);

        parsedAdviceItems.put(AdviceEntryType.ADVICE, parseAdviceItems(dataObject, "advice", null));
        parsedAdviceItems.put(AdviceEntryType.FOLLOW_UP, parseAdviceItems(dataObject, "followups", "dfi_id"));
        parsedAdviceItems.put(AdviceEntryType.SPECIAL_ALERT, parseAdviceItems(dataObject, "special_alerts", "dsai_id"));
        parsedAdviceItems.put(AdviceEntryType.REFERENCE, createSingleAdviceItem(getJsonString(dataObject, "reference")));
        parsedAdviceItems.put(AdviceEntryType.NOTES, createSingleAdviceItem(getJsonString(dataObject, "notes")));

        /*
         * সব parsing successful হওয়ার পর current field data replace হবে।
         */
        vitals.setValue(parsedVitals);
        publishObjectiveInfoItems(parsedObjectives);
        publishMedicineItems(parsedMedicines);
        publishTherapyItems(parsedTherapies);
        publishTreatmentPlanItems(parsedTreatmentPlans);

        for (ClinicalEntryType entryType : ClinicalEntryType.values()) {
            ArrayList<ClinicalEntryItem> items = parsedClinicalItems.get(entryType);
            publishClinicalEntries(entryType, items == null ? new ArrayList<>() : items);
        }

        for (AdviceEntryType entryType : AdviceEntryType.values()) {
            ArrayList<AdviceEntryItem> items = parsedAdviceItems.get(entryType);
            publishAdviceEntries(entryType, items == null ? new ArrayList<>() : items);
        }

        /*
         * Previous prescription data কোনো template selection
         * বহন করবে না। History load-এর পরে doctor চাইলে
         * নতুন করে Disease/Treatment Template ব্যবহার করতে পারবেন।
         */
        loadedDiseaseTemplateId = "";
        loadedDiseaseTemplateName = "";

        resetTreatmentTemplateState();

        diseaseTemplateApplyLoading.setValue(false);
        diseaseTemplateApplySuccess.setValue(false);
        diseaseTemplateApplyError.setValue("");

        treatmentTemplateApplyLoading.setValue(false);
        treatmentTemplateApplySuccess.setValue(false);
        treatmentTemplateApplyError.setValue("");
    }

    // Template List Load
    public void loadDiseaseTemplatesIfNeeded() {
        if (diseaseTemplateListLoaded || Boolean.TRUE.equals(diseaseTemplateLoading.getValue())) {
            return;
        }

        if (currentDoctorId.isEmpty()) {
            diseaseTemplateError.setValue("Doctor information not found");
            return;
        }

        diseaseTemplateLoading.setValue(true);
        diseaseTemplateError.setValue("");

        mainDataRepository.getDiseaseTemplates(currentDoctorId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<PrescriptionTemplateItem> data) {
                diseaseTemplateItems.setValue(new ArrayList<>(data));
                diseaseTemplateListLoaded = true;
                diseaseTemplateLoading.setValue(false);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                diseaseTemplateListLoaded = false;
                diseaseTemplateLoading.setValue(false);
                diseaseTemplateError.setValue(errorMessage);
            }
        });
    }

    public void retryDiseaseTemplates() {
        diseaseTemplateListLoaded = false;
        diseaseTemplateItems.setValue(new ArrayList<>());
        diseaseTemplateError.setValue("");
        loadDiseaseTemplatesIfNeeded();
    }

    public void loadTreatmentTemplatesForDisease(@NonNull String diseaseTemplateId) {
        String validDiseaseTemplateId = diseaseTemplateId.trim();

        if (validDiseaseTemplateId.isEmpty()) {
            treatmentTemplateError.setValue("Disease Template information not found");
            return;
        }

        if (treatmentTemplateListLoaded && validDiseaseTemplateId.equals(treatmentTemplateListDiseaseId)) {
            return;
        }

        treatmentTemplateListDiseaseId = validDiseaseTemplateId;
        treatmentTemplateListLoaded = false;

        treatmentTemplateItems.setValue(new ArrayList<>());
        treatmentTemplateLoading.setValue(true);
        treatmentTemplateError.setValue("");

        int currentRequestVersion = ++treatmentTemplateRequestVersion;

        mainDataRepository.getTreatmentTemplates(
                currentDoctorId,
                validDiseaseTemplateId,
                new PrescriptionMainDataRepository.DataCallback<>() {
                    @Override
                    public void onSuccess(@NonNull ArrayList<PrescriptionTemplateItem> data) {
                        if (currentRequestVersion != treatmentTemplateRequestVersion) {
                            return;
                        }

                        treatmentTemplateItems.setValue(new ArrayList<>(data));
                        treatmentTemplateListLoaded = true;
                        treatmentTemplateLoading.setValue(false);
                    }

                    @Override
                    public void onError(@NonNull String errorMessage) {
                        if (currentRequestVersion != treatmentTemplateRequestVersion) {
                            return;
                        }

                        treatmentTemplateListLoaded = false;
                        treatmentTemplateLoading.setValue(false);
                        treatmentTemplateError.setValue(errorMessage);
                    }
                }
        );
    }

    public void retryTreatmentTemplates() {
        if (treatmentTemplateListDiseaseId.isEmpty()) {
            return;
        }

        String diseaseTemplateId = treatmentTemplateListDiseaseId;

        treatmentTemplateListLoaded = false;
        treatmentTemplateItems.setValue(new ArrayList<>());
        treatmentTemplateError.setValue("");

        loadTreatmentTemplatesForDisease(diseaseTemplateId);
    }

    private void resetTreatmentTemplateState() {
        treatmentTemplateRequestVersion++;

        treatmentTemplateListLoaded = false;
        treatmentTemplateListDiseaseId = "";

        treatmentTemplateItems.setValue(new ArrayList<>());
        treatmentTemplateLoading.setValue(false);
        treatmentTemplateError.setValue("");

        loadedTreatmentTemplateId = "";
        loadedTreatmentTemplateName = "";
    }

    public LiveData<ArrayList<PrescriptionTemplateItem>> getDiseaseTemplateItems() {
        return diseaseTemplateItems;
    }

    public LiveData<Boolean> getDiseaseTemplateLoading() {
        return diseaseTemplateLoading;
    }

    public LiveData<String> getDiseaseTemplateError() {
        return diseaseTemplateError;
    }

    public LiveData<ArrayList<PrescriptionTemplateItem>> getTreatmentTemplateItems() {
        return treatmentTemplateItems;
    }

    public LiveData<Boolean> getTreatmentTemplateLoading() {
        return treatmentTemplateLoading;
    }

    public LiveData<String> getTreatmentTemplateError() {
        return treatmentTemplateError;
    }

    @NonNull
    public String getLoadedDiseaseTemplateId() {
        return loadedDiseaseTemplateId;
    }

    @NonNull
    public String getLoadedDiseaseTemplateName() {
        return loadedDiseaseTemplateName;
    }

    @NonNull
    public String getLoadedTreatmentTemplateId() {
        return loadedTreatmentTemplateId;
    }

    @NonNull
    public String getLoadedTreatmentTemplateName() {
        return loadedTreatmentTemplateName;
    }


    // Template Data Load
    public void applyDiseaseTemplate(@NonNull PrescriptionTemplateItem templateItem) {
        if (Boolean.TRUE.equals(diseaseTemplateApplyLoading.getValue())) {
            return;
        }

        String templateId = templateItem.getTemplateId();

        if (templateId.isEmpty()) {
            diseaseTemplateApplyError.setValue("Disease Template information not found");
            return;
        }

        diseaseTemplateApplySuccess.setValue(false);
        diseaseTemplateApplyLoading.setValue(true);
        diseaseTemplateApplyError.setValue("");

        mainDataRepository.getDiseaseTemplateData(templateId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull JSONObject dataObject) {
                try {
                    EnumMap<ClinicalEntryType, ArrayList<ClinicalEntryItem>> parsedClinicalItems = new EnumMap<>(ClinicalEntryType.class);

                    parsedClinicalItems.put(ClinicalEntryType.COMPLAIN, parseClinicalItems(dataObject, "complaints", "cm_id", "name", null));
                    parsedClinicalItems.put(ClinicalEntryType.INJURY, parseClinicalItems(dataObject, "injuries", "injury_id", "name", null));
                    parsedClinicalItems.put(ClinicalEntryType.DIAGNOSIS, parseClinicalItems(dataObject, "diagnosis", "dm_id", "name", null));
                    parsedClinicalItems.put(ClinicalEntryType.CLINICAL_FINDING, parseClinicalItems(dataObject, "clinical_findings", "cfm_id", "name", null));
                    parsedClinicalItems.put(ClinicalEntryType.DRUG_HISTORY, parseClinicalItems(dataObject, "drug_history", "medicine_id", "name", null));
                    parsedClinicalItems.put(ClinicalEntryType.TESTS_INVESTIGATION, parseClinicalItems(dataObject, "investigations", "pfn_id", "name", "notes"));
                    parsedClinicalItems.put(ClinicalEntryType.IMPORTANT_INFO, parseClinicalItems(dataObject, "important_info", null, "text", null));

                    ArrayList<ObjectiveInfoItem> parsedObjectives = parseObjectiveInfo(dataObject);

                    ArrayList<AdviceEntryItem> parsedAdvice = parseAdviceItems(dataObject, "advice", null);
                    ArrayList<AdviceEntryItem> parsedFollowUps = parseAdviceItems(dataObject, "followups", "dfi_id");
                    ArrayList<AdviceEntryItem> parsedSpecialAlerts = parseAdviceItems(dataObject, "special_alerts", "dsai_id");

                    for (ClinicalEntryType entryType : ClinicalEntryType.values()) {
                        ArrayList<ClinicalEntryItem> items = parsedClinicalItems.get(entryType);
                        publishClinicalEntries(entryType, items == null ? new ArrayList<>() : items);
                    }

                    publishObjectiveInfoItems(parsedObjectives);

                    publishAdviceEntries(AdviceEntryType.ADVICE, parsedAdvice);
                    publishAdviceEntries(AdviceEntryType.FOLLOW_UP, parsedFollowUps);
                    publishAdviceEntries(AdviceEntryType.SPECIAL_ALERT, parsedSpecialAlerts);

                    clearRxTemplateData();
                    resetTreatmentTemplateState();

                    loadedDiseaseTemplateId = templateItem.getTemplateId();
                    loadedDiseaseTemplateName = templateItem.getTemplateName();

                    diseaseTemplateApplySuccess.setValue(true);
                    diseaseTemplateApplyLoading.setValue(false);

                    loadTreatmentTemplatesForDisease(loadedDiseaseTemplateId);

                } catch (Exception exception) {
                    diseaseTemplateApplySuccess.setValue(false);
                    diseaseTemplateApplyLoading.setValue(false);

                    String message = exception.getLocalizedMessage();

                    diseaseTemplateApplyError.setValue(message == null || message.trim().isEmpty() ? "Disease Template could not be applied" : message);
                }
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                diseaseTemplateApplySuccess.setValue(false);
                diseaseTemplateApplyLoading.setValue(false);
                diseaseTemplateApplyError.setValue(errorMessage);
            }
        });
    }

    public void applyTreatmentTemplate(@NonNull PrescriptionTemplateItem templateItem) {
        if (Boolean.TRUE.equals(treatmentTemplateApplyLoading.getValue())) {
            return;
        }

        if (loadedDiseaseTemplateId.isEmpty()) {
            treatmentTemplateApplyError.setValue("Load a Disease Template first");
            return;
        }

        String templateId = templateItem.getTemplateId();

        if (templateId.isEmpty()) {
            treatmentTemplateApplyError.setValue("Treatment Template information not found");
            return;
        }

        treatmentTemplateApplySuccess.setValue(false);
        treatmentTemplateApplyLoading.setValue(true);
        treatmentTemplateApplyError.setValue("");

        mainDataRepository.getTreatmentTemplateData(templateId, new PrescriptionMainDataRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull JSONObject dataObject) {
                try {
                    ArrayList<MedicineEntryItem> parsedMedicines = parseMedicines(dataObject);
                    ArrayList<TherapyEntryItem> parsedTherapies = parseTherapies(dataObject);
                    ArrayList<TreatmentPlanEntryItem> parsedTreatmentPlans = parseTreatmentPlans(dataObject);

                    publishMedicineItems(parsedMedicines);
                    publishTherapyItems(parsedTherapies);
                    publishTreatmentPlanItems(parsedTreatmentPlans);

                    loadedTreatmentTemplateId = templateItem.getTemplateId();
                    loadedTreatmentTemplateName = templateItem.getTemplateName();

                    treatmentTemplateApplySuccess.setValue(true);
                    treatmentTemplateApplyLoading.setValue(false);

                } catch (Exception exception) {
                    treatmentTemplateApplySuccess.setValue(false);
                    treatmentTemplateApplyLoading.setValue(false);

                    String message = exception.getLocalizedMessage();

                    treatmentTemplateApplyError.setValue(message == null || message.trim().isEmpty() ? "Treatment Template could not be applied" : message);
                }
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                treatmentTemplateApplySuccess.setValue(false);
                treatmentTemplateApplyLoading.setValue(false);
                treatmentTemplateApplyError.setValue(errorMessage);
            }
        });
    }

    private void clearRxTemplateData() {
        publishMedicineItems(new ArrayList<>());
        publishTherapyItems(new ArrayList<>());
        publishTreatmentPlanItems(new ArrayList<>());
    }

    public boolean hasDiseaseTemplateReplaceableData() {
        if (!getCurrentObjectiveInfoItems().isEmpty()) {
            return true;
        }

        for (ClinicalEntryType entryType : ClinicalEntryType.values()) {
            if (!getCurrentClinicalEntries(entryType).isEmpty()) {
                return true;
            }
        }

        if (!getCurrentAdviceEntries(AdviceEntryType.ADVICE).isEmpty()
                || !getCurrentAdviceEntries(AdviceEntryType.FOLLOW_UP).isEmpty()
                || !getCurrentAdviceEntries(AdviceEntryType.SPECIAL_ALERT).isEmpty()) {
            return true;
        }

        return hasRxTemplateReplaceableData();
    }

    public boolean hasRxTemplateReplaceableData() {
        return !getCurrentMedicineItems().isEmpty()
                || !getCurrentTherapyItems().isEmpty()
                || !getCurrentTreatmentPlanItems().isEmpty();
    }

    public boolean hasIncompleteObjectiveInfo() {
        return !getIncompleteObjectiveNames().isEmpty();
    }

//    @NonNull
//    public ArrayList<String> getIncompleteObjectiveNames() {
//        ArrayList<String> incompleteObjectiveNames = new ArrayList<>();
//
//        for (ObjectiveInfoItem item : getCurrentObjectiveInfoItems()) {
//            boolean valueMissing = item.getObjectiveValueId().isEmpty() || item.getObjectiveValueName().isEmpty();
//
//            if (valueMissing && !incompleteObjectiveNames.contains(item.getObjectiveName())) {
//                incompleteObjectiveNames.add(item.getObjectiveName());
//            }
//        }
//
//        return incompleteObjectiveNames;
//    }

    @NonNull
    public ArrayList<String> getIncompleteObjectiveNames() {
        ArrayList<String> incompleteObjectiveNames = new ArrayList<>();

        for (ObjectiveInfoItem item : getCurrentObjectiveInfoItems()) {
            boolean valueAndNotesMissing = !item.hasObjectiveValue() && !item.hasNotes();

            if (valueAndNotesMissing && !incompleteObjectiveNames.contains(item.getObjectiveName())) {
                incompleteObjectiveNames.add(item.getObjectiveName());
            }
        }

        return incompleteObjectiveNames;
    }

    public LiveData<Boolean> getDiseaseTemplateApplyLoading() {
        return diseaseTemplateApplyLoading;
    }

    public LiveData<String> getDiseaseTemplateApplyError() {
        return diseaseTemplateApplyError;
    }

    public LiveData<Boolean> getTreatmentTemplateApplyLoading() {
        return treatmentTemplateApplyLoading;
    }

    public LiveData<String> getTreatmentTemplateApplyError() {
        return treatmentTemplateApplyError;
    }

    public void clearDiseaseTemplateApplyError() {
        diseaseTemplateApplyError.setValue("");
    }

    public void clearTreatmentTemplateApplyError() {
        treatmentTemplateApplyError.setValue("");
    }

    public LiveData<Boolean> getDiseaseTemplateApplySuccess() {
        return diseaseTemplateApplySuccess;
    }

    public LiveData<Boolean> getTreatmentTemplateApplySuccess() {
        return treatmentTemplateApplySuccess;
    }


    /*
     * Walk-in Patient Selection
     */
    public LiveData<ArrayList<PrescriptionPatientItem>> getPatientItems() {
        return patientItems;
    }

    public LiveData<Boolean> getPatientListLoading() {
        return patientListLoading;
    }

    public LiveData<String> getPatientListError() {
        return patientListError;
    }

    @NonNull
    public ArrayList<PrescriptionPatientItem> getCurrentPatientItems() {
        ArrayList<PrescriptionPatientItem> currentItems = patientItems.getValue();

        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    public void loadRecentPatients(@NonNull String registrationYear) {
        String validYear = registrationYear.trim();
        int currentRequestVersion = ++patientListRequestVersion;

        patientRepository.cancelSearchRequest();

        patientListLoading.setValue(true);
        patientListError.setValue("");

        patientRepository.getRecentPatients(validYear, new PrescriptionPatientRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<PrescriptionPatientItem> data) {
                if (currentRequestVersion != patientListRequestVersion) {
                    return;
                }

                patientItems.setValue(new ArrayList<>(data));
                patientListLoading.setValue(false);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                if (currentRequestVersion != patientListRequestVersion) {
                    return;
                }

                patientItems.setValue(new ArrayList<>());
                patientListLoading.setValue(false);
                patientListError.setValue(errorMessage);
            }
        });
    }

    public void searchPatients(@NonNull String searchText) {
        String validSearchText = searchText.trim();

        if (validSearchText.length() < 3) {
            cancelPatientSearch();
            return;
        }

        int currentRequestVersion = ++patientListRequestVersion;

        patientListLoading.setValue(true);
        patientListError.setValue("");

        patientRepository.searchPatients(validSearchText, new PrescriptionPatientRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull ArrayList<PrescriptionPatientItem> data) {
                if (currentRequestVersion != patientListRequestVersion) {
                    return;
                }

                patientItems.setValue(new ArrayList<>(data));
                patientListLoading.setValue(false);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                if (currentRequestVersion != patientListRequestVersion) {
                    return;
                }

                patientItems.setValue(new ArrayList<>());
                patientListLoading.setValue(false);
                patientListError.setValue(errorMessage);
            }
        });
    }

    public void cancelPatientSearch() {
        /*
         * Running callback-এর version invalid করা হচ্ছে।
         */
        patientListRequestVersion++;

        patientRepository.cancelSearchRequest();
        patientListLoading.setValue(false);
    }

    public void clearPatientListError() {
        patientListError.setValue("");
    }

    public void clearPatientList() {
        patientListRequestVersion++;

        patientRepository.cancelSearchRequest();
        patientItems.setValue(new ArrayList<>());
        patientListLoading.setValue(false);
        patientListError.setValue("");
    }

    public boolean hasAnyPrescriptionData() {
        VitalData currentVitals = getCurrentVitals();

        if (currentVitals != null && !currentVitals.isEmpty()) {
            return true;
        }

        if (!getCurrentObjectiveInfoItems().isEmpty()) {
            return true;
        }

        for (ClinicalEntryType entryType : ClinicalEntryType.values()) {
            if (!getCurrentClinicalEntries(entryType).isEmpty()) {
                return true;
            }
        }

        for (AdviceEntryType entryType : AdviceEntryType.values()) {
            if (!getCurrentAdviceEntries(entryType).isEmpty()) {
                return true;
            }
        }

        if (!getCurrentMedicineItems().isEmpty()
                || !getCurrentTherapyItems().isEmpty()
                || !getCurrentTreatmentPlanItems().isEmpty()) {
            return true;
        }

        return !loadedDiseaseTemplateId.isEmpty() || !loadedTreatmentTemplateId.isEmpty();
    }

    public void clearAllPrescriptionDataForPatientChange() {
        clearVitals();

        for (ClinicalEntryType entryType : ClinicalEntryType.values()) {
            publishClinicalEntries(entryType, new ArrayList<>());
        }

        publishObjectiveInfoItems(new ArrayList<>());

        for (AdviceEntryType entryType : AdviceEntryType.values()) {
            publishAdviceEntries(entryType, new ArrayList<>());
        }

        clearRxTemplateData();

        loadedDiseaseTemplateId = "";
        loadedDiseaseTemplateName = "";

        resetTreatmentTemplateState();

        diseaseTemplateApplySuccess.setValue(false);
        treatmentTemplateApplySuccess.setValue(false);

        diseaseTemplateApplyError.setValue("");
        treatmentTemplateApplyError.setValue("");

        clearObjectiveValueSelection();
    }


    // Patient Details
    public LiveData<PrescriptionPatientDetails> getPatientDetailsData() {
        return patientDetails;
    }

    public LiveData<Boolean> getPatientDetailsLoading() {
        return patientDetailsLoading;
    }

    public LiveData<String> getPatientDetailsError() {
        return patientDetailsError;
    }

    public void loadPatientDetails(@NonNull String patientHistoryId) {
        String validPatientHistoryId = patientHistoryId.trim();
        int currentRequestVersion = ++patientDetailsRequestVersion;

        patientRepository.cancelPatientDetailsRequest();

        patientDetails.setValue(null);
        patientDetailsLoading.setValue(true);
        patientDetailsError.setValue("");

        patientRepository.getPatientDetails(validPatientHistoryId, new PrescriptionPatientRepository.DataCallback<>() {
            @Override
            public void onSuccess(@NonNull PrescriptionPatientDetails data) {
                if (currentRequestVersion != patientDetailsRequestVersion) {
                    return;
                }

                patientDetails.setValue(data);
                patientDetailsLoading.setValue(false);
            }

            @Override
            public void onError(@NonNull String errorMessage) {
                if (currentRequestVersion != patientDetailsRequestVersion) {
                    return;
                }

                patientDetails.setValue(null);
                patientDetailsLoading.setValue(false);
                patientDetailsError.setValue(errorMessage);
            }
        });
    }

    public void cancelPatientDetailsLoad() {
        patientDetailsRequestVersion++;
        patientRepository.cancelPatientDetailsRequest();
        patientDetailsLoading.setValue(false);
    }

    public void clearPatientDetailsState() {
        patientDetailsRequestVersion++;
        patientRepository.cancelPatientDetailsRequest();

        patientDetails.setValue(null);
        patientDetailsLoading.setValue(false);
        patientDetailsError.setValue("");
    }

    // Getter for Main Data
    public ArrayList<ClinicalOption> getCurrentComplainOptions() {
        ArrayList<ClinicalOption> currentItems = complainOptions.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentInjuryOptions() {
        ArrayList<ClinicalOption> currentItems = injuryOptions.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentDiagnosisOptions() {
        ArrayList<ClinicalOption> currentItems = diagnosisOptions.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentClinicalFindingOptions() {
        ArrayList<ClinicalOption> currentItems = clinicalFindingOptions.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentDrugHistoryOptions() {
        ArrayList<ClinicalOption> currentItems = drugHistoryOptions.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentTestInvestigationOptions() {
        ArrayList<ClinicalOption> currentItems = testInvestigationOptions.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentObjectiveOptions() {
        ArrayList<ClinicalOption> currentItems = objectiveOptions.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentMuscleJointOptions() {
        ArrayList<ClinicalOption> currentItems = muscleJointOptions.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentMedicineOptions() {
        return copyClinicalOptions(medicineOptions);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentDoseOptions() {
        return copyClinicalOptions(doseOptions);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentMedicineTimingOptions() {
        return copyClinicalOptions(medicineTimingOptions);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentMedicineRouteOptions() {
        return copyClinicalOptions(medicineRouteOptions);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentTherapyOptions() {
        return copyClinicalOptions(therapyOptions);
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentTreatmentPlanOptions() {
        return copyClinicalOptions(treatmentPlanOptions);
    }

    public LiveData<ArrayList<ClinicalOption>> getObjectiveValueOptions() {return objectiveValueOptions;}

    public LiveData<Boolean> getObjectiveValueLoading() {return objectiveValueLoading;}

    public LiveData<String> getObjectiveValueError() {return objectiveValueError;}

    @NonNull
    private ArrayList<ClinicalOption> copyClinicalOptions(@NonNull MutableLiveData<ArrayList<ClinicalOption>> source) {
        ArrayList<ClinicalOption> currentItems = source.getValue();
        if (currentItems == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(currentItems);
    }


    @NonNull
    public ArrayList<ClinicalOption> getCurrentClinicalOptions(@NonNull ClinicalEntryType entryType) {
        switch (entryType) {
            case COMPLAIN:
                return getCurrentComplainOptions();

            case INJURY:
                return getCurrentInjuryOptions();

            case DIAGNOSIS:
                return getCurrentDiagnosisOptions();

            case CLINICAL_FINDING:
                return getCurrentClinicalFindingOptions();

            case DRUG_HISTORY:
                return getCurrentDrugHistoryOptions();

            case TESTS_INVESTIGATION:
                return getCurrentTestInvestigationOptions();

            default:
                return new ArrayList<>();
        }
    }

    @NonNull
    public ArrayList<ClinicalOption> getCurrentAdviceOptions(@NonNull AdviceEntryType entryType) {
        switch (entryType) {
            case ADVICE:
                return copyClinicalOptions(adviceOptions);

            case FOLLOW_UP:
                return copyClinicalOptions(followUpOptions);

            case SPECIAL_ALERT:
                return copyClinicalOptions(specialAlertOptions);

            case REFERENCE:
            case NOTES:
            default:
                return new ArrayList<>();
        }
    }

    public LiveData<Boolean> getMainDataLoading() {
        return mainDataLoading;
    }

    public LiveData<ArrayList<String>> getMainDataErrors() {
        return mainDataErrors;
    }

    @Override
    protected void onCleared() {
        patientListRequestVersion++;
        patientDetailsRequestVersion++;
        prescriptionHistoryRequestVersion++;
        previousPrescriptionRequestVersion++;
        patientRepository.cancelAllRequests();
        super.onCleared();
    }
}
