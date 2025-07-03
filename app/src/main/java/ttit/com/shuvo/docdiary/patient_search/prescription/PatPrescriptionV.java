package ttit.com.shuvo.docdiary.patient_search.prescription;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.ComplainLists;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatDiagnosisList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatRefServiceList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatReferralList;
import ttit.com.shuvo.docdiary.patient_search.prescription.adapters.ComplainPSVAdapter;
import ttit.com.shuvo.docdiary.patient_search.prescription.adapters.PatDiagnosisPSVAdapter;
import ttit.com.shuvo.docdiary.patient_search.prescription.adapters.PatRefServicePSVAdapter;
import ttit.com.shuvo.docdiary.patient_search.prescription.adapters.PatReferralUniPSVAdapter;
import ttit.com.shuvo.docdiary.patient_search.prescription.fragments.ClinicalFindPSV;
import ttit.com.shuvo.docdiary.patient_search.prescription.fragments.DrugHistPSV;
import ttit.com.shuvo.docdiary.patient_search.prescription.fragments.ManagementPlanPSV;
import ttit.com.shuvo.docdiary.patient_search.prescription.fragments.MedicalHistPSV;
import ttit.com.shuvo.docdiary.patient_search.prescription.fragments.MedicationPSV;
import ttit.com.shuvo.docdiary.patient_search.prescription.fragments.PatAdvicePSV;
import ttit.com.shuvo.docdiary.patient_search.prescription.fragments.PatReferencePSV;

public class PatPrescriptionV extends AppCompatActivity implements PatDiagnosisPSVAdapter.ClickedItem, PatReferralUniPSVAdapter.ClickedItem2 {

    LinearLayout fullLayoutPatPSV;
    CircularProgressIndicator circularProgressIndicatorPatPSV;

    ImageView close;
    TextView patName,patSubCode;
    TextInputEditText mdtCode, patAge, patMaritalStat, patGender, patBlood, patCatgory, patStatus, patAddress;

    String pat_name = "";
    String pat_sub_code = "";
    String mdt_code = "";
    String pat_age = "";
    String pat_marital_stat = "";
    String pat_gender = "";
    String pat_blood = "";
    String pat_category = "";
    String pat_status = "";
    String pat_address = "";
    String ph_id = "";
    String pmm_id = "";
    String pmm_date = "";
    String pat_cat_id = "";
    String pmm_admission_flag = "0";

    private Boolean conn = false;
    private Boolean connected = false;
    boolean prescriptionLoadingPatPSV = false;
    public static boolean historyLoadingPatPSV = false;
    public static boolean medicationLoadingPatPSV = false;
    String parsing_message = "";

    ArrayList<ComplainLists> complainLists;
    RecyclerView complainView;
    RecyclerView.LayoutManager layoutManager;
    ComplainPSVAdapter complainPSVAdapter;
    public TextView noComplainFoundMsgPatPSV;

    public static TabLayout historytabLayoutPatPSV;
    public TabItem medHistTab, drugHistTab, clinicalFindTab;

    ArrayList<PatDiagnosisList> patDiagnosisLists;
    RecyclerView diagnosisView;
    RecyclerView.LayoutManager daigLayoutManager;
    PatDiagnosisPSVAdapter patDiagnosisPSVAdapter;
    TextView noDiagnosisFoundMsg;

    LinearLayout referralUnitLay;
    ArrayList<PatReferralList> patReferralListsPatPSV;
    RecyclerView referralView;
    RecyclerView.LayoutManager refLayoutManager;
    PatReferralUniPSVAdapter patReferralUniPSVAdapter;
    TextView noReferralFoundMsg;
    TextView refUnitDivider;

    String selected_pdi_id = "";

    LinearLayout refServiceUnitLay;
    ArrayList<PatRefServiceList> patRefServiceListsPatPSV;
    RecyclerView refServiceView;
    RecyclerView.LayoutManager refServLayoutManager;
    PatRefServicePSVAdapter patRefServicePSVAdapter;
    TextView noRefServiceFoundMsgPatPSV;
    TextView refServiceDivider;

    String selected_drd_id = "";
    String selected_depts_id = "";

    public static int selectedPositionPatPSV = 0;
    public static int selectedPosition2PatPSV = 0;
    boolean previousDataSelectedPatPSV = false;

    public static TabLayout medicationtabLayoutPatPSV;
    public TabItem managementTab, medicineTab, adviceTab, referenceTab;

    CheckBox patAdmission;
    ScrollView scrollview;
    TextView noPresFound;
    Boolean patPrescription = false;

    Logger logger = Logger.getLogger(PatPrescriptionV.class.getName());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_prescription_v);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pat_presc_v_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fullLayoutPatPSV = findViewById(R.id.pat_prescription_v_full_layout);
        circularProgressIndicatorPatPSV = findViewById(R.id.progress_indicator_pat_prescription_v);
        circularProgressIndicatorPatPSV.setVisibility(View.GONE);

        close = findViewById(R.id.back_logo_of_pat_prescription_v);

        patName = findViewById(R.id.pat_prescription_v_patient_name);
        patSubCode = findViewById(R.id.pat_prescription_v_patient_ph_code);
        mdtCode = findViewById(R.id.patient_mdt_code_in_pat_prescription_v);
        patAge = findViewById(R.id.patient_age_in_pat_prescription_v);
        patMaritalStat = findViewById(R.id.patient_marital_status_in_pat_prescription_v);
        patGender = findViewById(R.id.patient_gender_in_pat_prescription_v);
        patBlood = findViewById(R.id.patient_blood_group_in_pat_prescription_v);
        patCatgory = findViewById(R.id.patient_category_in_pat_prescription_v);
        patStatus = findViewById(R.id.patient_status_in_pat_prescription_v);
        patAddress = findViewById(R.id.patient_address_in_pat_prescription_v);

        complainView = findViewById(R.id.complain_injury_recyclerview_pat_pres_v);
        noComplainFoundMsgPatPSV = findViewById(R.id.no_information_found_msg_complain_pat_pres_v);

        historytabLayoutPatPSV = findViewById(R.id.history_findings_layout_pat_pres_v);
        medHistTab = findViewById(R.id.medical_history_tab_pat_pres_v);
        drugHistTab = findViewById(R.id.drug_history_tab_pat_pres_v);
        clinicalFindTab = findViewById(R.id.clinical_findings_tab_pat_pres_v);

        diagnosisView = findViewById(R.id.diagnosis_recyclerview_pat_pres_v);
        noDiagnosisFoundMsg = findViewById(R.id.no_information_found_msg_diagnosis_pat_pres_v);

        referralUnitLay = findViewById(R.id.referral_unit_layout_pat_pres_v);

        refUnitDivider = findViewById(R.id.divider_pat_psv_ref_unit);
        referralView = findViewById(R.id.referral_unit_recyclerview_pat_pres_v);
        noReferralFoundMsg = findViewById(R.id.no_information_found_msg_referral_unit_pat_pres_v);

        refServiceUnitLay = findViewById(R.id.service_unit_layout_pat_pres_v);

        refServiceDivider = findViewById(R.id.divider_pat_psv_service_unit);
        refServiceView = findViewById(R.id.service_unit_recyclerview_pat_pres_v);
        noRefServiceFoundMsgPatPSV = findViewById(R.id.no_information_found_msg_service_unit_pat_pres_v);

        medicationtabLayoutPatPSV = findViewById(R.id.medication_plan_layout_pat_pres_v);
        managementTab = findViewById(R.id.management_plan_tab_pat_pres_v);
        medicineTab = findViewById(R.id.medication_tab_pat_pres_v);
        adviceTab = findViewById(R.id.advice_tab_pat_pres_v);
        referenceTab = findViewById(R.id.reference_tab_pat_pres_v);

        patAdmission = findViewById(R.id.pat_admission_flag_checkbox_pat_pres_v);

        scrollview = findViewById(R.id.scrollview_of_pat_prescription_view);
        noPresFound = findViewById(R.id.no_prescription_found_msg_from_patient_search_admin);
        noPresFound.setVisibility(View.GONE);

        Intent intent = getIntent();
        ph_id = intent.getStringExtra("P_PH_ID");

        complainLists = new ArrayList<>();
        patDiagnosisLists = new ArrayList<>();
        patReferralListsPatPSV = new ArrayList<>();
        patRefServiceListsPatPSV = new ArrayList<>();

        complainView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        complainView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(complainView.getContext(),DividerItemDecoration.VERTICAL);
        complainView.addItemDecoration(dividerItemDecoration);

        diagnosisView.setHasFixedSize(true);
        daigLayoutManager = new LinearLayoutManager(getApplicationContext());
        diagnosisView.setLayoutManager(daigLayoutManager);
        DividerItemDecoration dividerItemDecorationDns = new DividerItemDecoration(diagnosisView.getContext(),DividerItemDecoration.VERTICAL);
        diagnosisView.addItemDecoration(dividerItemDecorationDns);

        referralView.setHasFixedSize(true);
        refLayoutManager = new LinearLayoutManager(getApplicationContext());
        referralView.setLayoutManager(refLayoutManager);
        DividerItemDecoration dividerItemDecorationRU = new DividerItemDecoration(referralView.getContext(),DividerItemDecoration.VERTICAL);
        referralView.addItemDecoration(dividerItemDecorationRU);

        refServiceView.setHasFixedSize(true);
        refServLayoutManager = new LinearLayoutManager(getApplicationContext());
        refServiceView.setLayoutManager(refServLayoutManager);
        DividerItemDecoration dividerItemDecorationRS = new DividerItemDecoration(refServiceView.getContext(),DividerItemDecoration.VERTICAL);
        refServiceView.addItemDecoration(dividerItemDecorationRS);

        close.setOnClickListener(v -> {
            if (prescriptionLoadingPatPSV || historyLoadingPatPSV || medicationLoadingPatPSV) {
                Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        historytabLayoutPatPSV.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab_pat_pres_v, MedicalHistPSV.newInstance(pmm_id, "false"), "MHTEST").commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab_pat_pres_v, DrugHistPSV.newInstance(pmm_id, "false"), "DHTEST").commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab_pat_pres_v, ClinicalFindPSV.newInstance(pmm_id,"false"),"CFTEST").commit();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        medicationtabLayoutPatPSV.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab_pat_pres_v, ManagementPlanPSV.newInstance(pmm_id,"false"),"MPTEST").commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab_pat_pres_v, MedicationPSV.newInstance(pmm_id, "false"), "MEDTEST").commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab_pat_pres_v, PatAdvicePSV.newInstance(pmm_id,"false"),"PATEST").commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab_pat_pres_v, PatReferencePSV.newInstance(pmm_id,"false"),"PRTEST").commit();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (prescriptionLoadingPatPSV || historyLoadingPatPSV || medicationLoadingPatPSV) {
                    Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        getPrescriptionData();
    }

//    @Override
//    public void onBackPressed() {
//
//
//    }

    public void getPrescriptionData() {
        fullLayoutPatPSV.setVisibility(View.GONE);
        circularProgressIndicatorPatPSV.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        prescriptionLoadingPatPSV = true;

        pat_name = "";
        pat_sub_code = "";
        mdt_code = "";
        pat_marital_stat = "";
        pat_gender = "";
        pat_blood = "";
        pat_category = "";
        pat_status = "";
        pat_address = "";
        pmm_id = "";
        pmm_date = "";
        pat_cat_id = "";
        pat_age = "";
        pmm_admission_flag = "0";
        patPrescription = false;

        String presUrl = pre_url_api+"patient_search/getPatData?p_ph_id="+ph_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest presReq = new StringRequest(Request.Method.GET, presUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        pat_name = info.getString("pat_name")
                                .equals("null") ? "" : info.getString("pat_name");
                        pat_sub_code = info.getString("ph_sub_code")
                                .equals("null") ? "" : info.getString("ph_sub_code");
                        mdt_code = info.getString("pmm_code")
                                .equals("null") ? "" : info.getString("pmm_code");
                        pat_marital_stat = info.getString("marital_status")
                                .equals("null") ? "" : info.getString("marital_status");
                        pat_gender = info.getString("gender")
                                .equals("null") ? "" : info.getString("gender");
                        pat_blood = info.getString("pat_blood")
                                .equals("null") ? "" : info.getString("pat_blood");
                        pat_category = info.getString("pat_category_name")
                                .equals("null") ? "" : info.getString("pat_category_name");
                        pat_status = info.getString("ph_patient_status")
                                .equals("null") ? "" : info.getString("ph_patient_status");
                        pat_address = info.getString("address")
                                .equals("null") ? "" : info.getString("address");
                        pmm_id = info.getString("pmm_id")
                                .equals("null") ? "" : info.getString("pmm_id");
                        pmm_date = info.getString("pmm_date")
                                .equals("null") ? "" : info.getString("pmm_date");
                        pat_cat_id = info.getString("pat_category_id")
                                .equals("null") ? "" : info.getString("pat_category_id");
                        pat_age = info.getString("pat_age")
                                .equals("null") ? "" : info.getString("pat_age");
                        pmm_admission_flag = info.getString("pmm_admission_flag")
                                .equals("null") ? "0" : info.getString("pmm_admission_flag");
                    }
                    patPrescription = true;
                    getOldPrescriptionData();
                }
                else {
                    patPrescription = false;
                    connected = true;
                    updateInterface();
                }
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(presReq);
    }

    public void getOldPrescriptionData() {
        complainLists = new ArrayList<>();
        patDiagnosisLists = new ArrayList<>();
        patReferralListsPatPSV = new ArrayList<>();
        patRefServiceListsPatPSV = new ArrayList<>();
        selected_pdi_id = "";
        selected_drd_id = "";
        selected_depts_id = "";
        if (!previousDataSelectedPatPSV) {
            selectedPositionPatPSV = 0;
            selectedPosition2PatPSV = 0;
        }


        String complainUrl = pre_url_api+"prescription/getComplainData?pmm_id="+pmm_id;
        String patDiagUrl = pre_url_api+"prescription/getPatDiagnosis?pmm_id="+pmm_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest patDiagReq = new StringRequest(Request.Method.GET, patDiagUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pdi_id = info.getString("pdi_id")
                                .equals("null") ? "" : info.getString("pdi_id");
                        String pdi_pmm_id = info.getString("pdi_pmm_id")
                                .equals("null") ? "" : info.getString("pdi_pmm_id");
                        String dm_id = info.getString("dm_id")
                                .equals("null") ? "" : info.getString("dm_id");
                        String dm_name = info.getString("dm_name")
                                .equals("null") ? "" : info.getString("dm_name");
                        String child_count = info.getString("child_count")
                                .equals("null") ? "" : info.getString("child_count");

                        patDiagnosisLists.add(new PatDiagnosisList(pdi_id,pdi_pmm_id,dm_id,dm_name,child_count));

                        if (i==selectedPositionPatPSV) {
                            selected_pdi_id = pdi_id;
                        }
                    }
                }

                if (selected_pdi_id.isEmpty()) {
                    connected = true;
                    updateInterface();
                }
                else {
                    getReferralUnit(selected_pdi_id);
                }
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest complainReq = new StringRequest(Request.Method.GET, complainUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pci_id = info.getString("pci_id")
                                .equals("null") ? "" : info.getString("pci_id");
                        String pci_pmm_id = info.getString("pci_pmm_id")
                                .equals("null") ? "" : info.getString("pci_pmm_id");
                        String cm_id = info.getString("cm_id")
                                .equals("null") ? "" : info.getString("cm_id");
                        String cm_name = info.getString("cm_name")
                                .equals("null") ? "" : info.getString("cm_name");
                        String pci_date = info.getString("pci_date")
                                .equals("null") ? "" : info.getString("pci_date");
                        String injury_id = info.getString("injury_id")
                                .equals("null") ? "" : info.getString("injury_id");
                        String injury_name = info.getString("injury_name")
                                .equals("null") ? "" : info.getString("injury_name");

                        complainLists.add(new ComplainLists(pci_id,pci_pmm_id,cm_id,cm_name,pci_date,injury_id,injury_name));
                    }
                }

                requestQueue.add(patDiagReq);
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(complainReq);
    }

    public void getReferralUnit(String pdi_id) {
        String patRefUrl = pre_url_api+"prescription/getPatReferral?pdi_id="+pdi_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest patRefReq = new StringRequest(Request.Method.GET, patRefUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String drd_id = info.getString("drd_id")
                                .equals("null") ? "" : info.getString("drd_id");
                        String drd_pdi_id = info.getString("drd_pdi_id")
                                .equals("null") ? "" : info.getString("drd_pdi_id");
                        String depts_id = info.getString("depts_id")
                                .equals("null") ? "" : info.getString("depts_id");
                        String depts_name = info.getString("depts_name")
                                .equals("null") ? "" : info.getString("depts_name");
                        String doc_id = info.getString("doc_id")
                                .equals("null") ? "" : info.getString("doc_id");
                        String doc_name = info.getString("doc_name")
                                .equals("null") ? "" : info.getString("doc_name");
                        String child_count = info.getString("child_count")
                                .equals("null") ? "" : info.getString("child_count");

                        patReferralListsPatPSV.add(new PatReferralList(drd_id,drd_pdi_id,depts_id,depts_name,doc_id,doc_name,child_count));

                        if (i==selectedPosition2PatPSV) {
                            selected_drd_id = drd_id;
                            selected_depts_id = depts_id;
                        }
                    }
                }

                if (selected_drd_id.isEmpty()) {
                    connected = true;
                    updateInterface();
                }
                else {
                    getRefService(selected_drd_id,1);
                }
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(patRefReq);
    }

    public void getRefService(String drd_id, int choice) {
        String patRefServUrl = pre_url_api+"prescription/getPatRefService?drd_id="+drd_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest patRefServReq = new StringRequest(Request.Method.GET, patRefServUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String drs_id = info.getString("drs_id")
                                .equals("null") ? "" : info.getString("drs_id");
                        String drs_drd_id = info.getString("drs_drd_id")
                                .equals("null") ? "" : info.getString("drs_drd_id");
                        String pfn_id = info.getString("pfn_id")
                                .equals("null") ? "" : info.getString("pfn_id");
                        String pfn_fee_name = info.getString("pfn_fee_name")
                                .equals("null") ? "" : info.getString("pfn_fee_name");
                        String drs_qty = info.getString("drs_qty")
                                .equals("null") ? "" : info.getString("drs_qty");

                        patRefServiceListsPatPSV.add(new PatRefServiceList(drs_id,drs_drd_id,pfn_id,pfn_fee_name,drs_qty,selected_depts_id));

                    }
                }
                if (choice == 1) {
                    connected = true;
                    updateInterface();
                }
                else {
                    connected = true;
                    updateReferralData();
                }

            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                if (choice == 1) {
                    updateInterface();
                }
                else {
                    updateReferralData();
                }
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            if (choice == 1) {
                updateInterface();
            }
            else {
                updateReferralData();
            }
        });

        requestQueue.add(patRefServReq);
    }

    private void updateInterface() {
        if(conn) {
            if (connected) {
                if (patPrescription) {
                    fullLayoutPatPSV.setVisibility(View.VISIBLE);
                    circularProgressIndicatorPatPSV.setVisibility(View.GONE);
                    scrollview.setVisibility(View.VISIBLE);
                    noPresFound.setVisibility(View.GONE);
                    conn = false;
                    connected = false;
                    previousDataSelectedPatPSV = false;

                    patName.setText(pat_name);
                    patSubCode.setText(pat_sub_code);
                    mdtCode.setText(mdt_code);
                    patAge.setText(pat_age);
                    patMaritalStat.setText(pat_marital_stat);
                    patGender.setText(pat_gender);
                    patBlood.setText(pat_blood);
                    patCatgory.setText(pat_category);
                    patStatus.setText(pat_status);
                    patAddress.setText(pat_address);
                    patAdmission.setChecked(pmm_admission_flag.equals("1"));

                    if (complainLists.isEmpty()) {
                        noComplainFoundMsgPatPSV.setVisibility(View.VISIBLE);
                    }
                    else {
                        noComplainFoundMsgPatPSV.setVisibility(View.GONE);
                    }
                    complainPSVAdapter = new ComplainPSVAdapter(complainLists, PatPrescriptionV.this);
                    complainView.setAdapter(complainPSVAdapter);

                    System.out.println("PMM_ID: "+pmm_id);
                    System.out.println("PMM_CODE: "+ mdt_code);

                    int tab_pos  = historytabLayoutPatPSV.getSelectedTabPosition();
                    System.out.println("TAB POS: "+tab_pos);
                    if (tab_pos == 0) {
                        System.out.println("EKHANE 1");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab_pat_pres_v, MedicalHistPSV.newInstance(pmm_id,"false"),"MHTEST").commit();
                    }
                    else if (tab_pos == 1) {
                        System.out.println("EKHANE 2");
//                    TabLayout.Tab tab = historytabLayout.getTabAt(0);
//                    historytabLayout.selectTab(tab);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab_pat_pres_v, DrugHistPSV.newInstance(pmm_id,"false"),"DHTEST").commit();
                    }
                    else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab_pat_pres_v, ClinicalFindPSV.newInstance(pmm_id,"false"),"CFTEST").commit();
                    }

                    //------
                    if (patDiagnosisLists.isEmpty()) {
                        noDiagnosisFoundMsg.setVisibility(View.VISIBLE);
                        referralUnitLay.setVisibility(View.GONE);
                        refUnitDivider.setVisibility(View.GONE);
                        refServiceUnitLay.setVisibility(View.GONE);
                        refServiceDivider.setVisibility(View.GONE);
                    }
                    else {
                        noDiagnosisFoundMsg.setVisibility(View.GONE);
                        referralUnitLay.setVisibility(View.VISIBLE);
                        refUnitDivider.setVisibility(View.VISIBLE);
                        refServiceUnitLay.setVisibility(View.GONE);
                        refServiceDivider.setVisibility(View.GONE);
                    }
                    patDiagnosisPSVAdapter = new PatDiagnosisPSVAdapter(patDiagnosisLists,PatPrescriptionV.this,PatPrescriptionV.this);
                    diagnosisView.setAdapter(patDiagnosisPSVAdapter);

                    //------
                    if (patReferralListsPatPSV.isEmpty()) {
                        noReferralFoundMsg.setVisibility(View.VISIBLE);
                        refServiceUnitLay.setVisibility(View.GONE);
                        refServiceDivider.setVisibility(View.GONE);
                    }
                    else {
                        noReferralFoundMsg.setVisibility(View.GONE);
                        refServiceUnitLay.setVisibility(View.VISIBLE);
                        refServiceDivider.setVisibility(View.VISIBLE);
                    }
                    patReferralUniPSVAdapter = new PatReferralUniPSVAdapter(patReferralListsPatPSV,PatPrescriptionV.this,PatPrescriptionV.this);
                    referralView.setAdapter(patReferralUniPSVAdapter);

                    //------
                    if (patRefServiceListsPatPSV.isEmpty()) {
                        noRefServiceFoundMsgPatPSV.setVisibility(View.VISIBLE);
                    }
                    else {
                        noRefServiceFoundMsgPatPSV.setVisibility(View.GONE);
                    }
                    patRefServicePSVAdapter = new PatRefServicePSVAdapter(patRefServiceListsPatPSV,PatPrescriptionV.this);
                    refServiceView.setAdapter(patRefServicePSVAdapter);

                    int med_tab_pos  = medicationtabLayoutPatPSV.getSelectedTabPosition();
                    System.out.println("MED TAB POS: "+med_tab_pos);
                    if (med_tab_pos == 0) {
                        System.out.println("EKHANE 1");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab_pat_pres_v, ManagementPlanPSV.newInstance(pmm_id,"false"),"MPTEST").commit();
                    }
                    else if (med_tab_pos == 1) {
                        System.out.println("EKHANE 2");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab_pat_pres_v, MedicationPSV.newInstance(pmm_id,"false"),"MEDTEST").commit();
                    }
                    else if (med_tab_pos == 2) {
                        System.out.println("EKHANE 3");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab_pat_pres_v, PatAdvicePSV.newInstance(pmm_id,"false"),"PATEST").commit();
                    }
                    else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab_pat_pres_v, PatReferencePSV.newInstance(pmm_id,"false"),"PRTEST").commit();
                    }
                    prescriptionLoadingPatPSV = false;
                }
                else {
                    fullLayoutPatPSV.setVisibility(View.VISIBLE);
                    circularProgressIndicatorPatPSV.setVisibility(View.GONE);
                    scrollview.setVisibility(View.GONE);
                    noPresFound.setVisibility(View.VISIBLE);
                    conn = false;
                    connected = false;
                    previousDataSelectedPatPSV = false;
                    prescriptionLoadingPatPSV = false;
                }
            }
            else {
                alertMessage();
            }
        }
        else {
            alertMessage();
        }
    }

    public void alertMessage() {
        fullLayoutPatPSV.setVisibility(View.VISIBLE);
        circularProgressIndicatorPatPSV.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PatPrescriptionV.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    prescriptionLoadingPatPSV= false;
                    getPrescriptionData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    prescriptionLoadingPatPSV = false;
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        try {
            alert.show();
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }
    }

    @Override
    public void onDiagnosisClicked(int diagPosition) {
        selected_pdi_id = patDiagnosisLists.get(diagPosition).getPdi_id();

        System.out.println(diagPosition);
        selectedPositionPatPSV = diagPosition;
        selectedPosition2PatPSV = 0;

        patDiagnosisPSVAdapter.notifyDataSetChanged();

        getReferralDataBySelect();
    }

    public void getReferralDataBySelect() {
        prescriptionLoadingPatPSV = true;
        fullLayoutPatPSV.setVisibility(View.GONE);
        circularProgressIndicatorPatPSV.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        patReferralListsPatPSV = new ArrayList<>();
        patRefServiceListsPatPSV = new ArrayList<>();
        selected_drd_id = "";
        selected_depts_id = "";

        String patRefUrl = pre_url_api+"prescription/getPatReferral?pdi_id="+selected_pdi_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest patRefReq = new StringRequest(Request.Method.GET, patRefUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String drd_id = info.getString("drd_id")
                                .equals("null") ? "" : info.getString("drd_id");
                        String drd_pdi_id = info.getString("drd_pdi_id")
                                .equals("null") ? "" : info.getString("drd_pdi_id");
                        String depts_id = info.getString("depts_id")
                                .equals("null") ? "" : info.getString("depts_id");
                        String depts_name = info.getString("depts_name")
                                .equals("null") ? "" : info.getString("depts_name");
                        String doc_id = info.getString("doc_id")
                                .equals("null") ? "" : info.getString("doc_id");
                        String doc_name = info.getString("doc_name")
                                .equals("null") ? "" : info.getString("doc_name");
                        String child_count = info.getString("child_count")
                                .equals("null") ? "" : info.getString("child_count");

                        patReferralListsPatPSV.add(new PatReferralList(drd_id,drd_pdi_id,depts_id,depts_name,doc_id,doc_name,child_count));

                        if (i==0) {
                            selected_drd_id = drd_id;
                            selected_depts_id = depts_id;
                        }
                    }
                }

                if (selected_drd_id.isEmpty()) {
                    connected = true;
                    updateReferralData();
                }
                else {
                    getRefService(selected_drd_id,2);
                }
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateReferralData();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateReferralData();
        });

        requestQueue.add(patRefReq);
    }

    private void updateReferralData() {
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;

                fullLayoutPatPSV.setVisibility(View.VISIBLE);
                circularProgressIndicatorPatPSV.setVisibility(View.GONE);

                if (patReferralListsPatPSV.isEmpty()) {
                    noReferralFoundMsg.setVisibility(View.VISIBLE);
                    refServiceUnitLay.setVisibility(View.GONE);
                    refServiceDivider.setVisibility(View.GONE);
                }
                else {
                    noReferralFoundMsg.setVisibility(View.GONE);
                    refServiceUnitLay.setVisibility(View.VISIBLE);
                    refServiceDivider.setVisibility(View.VISIBLE);
                }

                patReferralUniPSVAdapter = new PatReferralUniPSVAdapter(patReferralListsPatPSV, PatPrescriptionV.this,PatPrescriptionV.this);
                referralView.setAdapter(patReferralUniPSVAdapter);

                if (patRefServiceListsPatPSV.isEmpty()) {
                    noRefServiceFoundMsgPatPSV.setVisibility(View.VISIBLE);
                }
                else {
                    noRefServiceFoundMsgPatPSV.setVisibility(View.GONE);
                }
                patRefServicePSVAdapter = new PatRefServicePSVAdapter(patRefServiceListsPatPSV,PatPrescriptionV.this);
                refServiceView.setAdapter(patRefServicePSVAdapter);
                prescriptionLoadingPatPSV = false;
            }
            else {
                alertMessageForGettingRef();
            }
        }
        else {
            alertMessageForGettingRef();
        }
    }

    public void alertMessageForGettingRef() {
        fullLayoutPatPSV.setVisibility(View.VISIBLE);
        circularProgressIndicatorPatPSV.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PatPrescriptionV.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    prescriptionLoadingPatPSV = false;
                    getReferralDataBySelect();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    prescriptionLoadingPatPSV = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        try {
            alert.show();
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }
    }

    @Override
    public void onRefUnitClicked(int refUnitPosition) {
        selected_drd_id = patReferralListsPatPSV.get(refUnitPosition).getDrd_id();
        selected_depts_id = patReferralListsPatPSV.get(refUnitPosition).getDepts_id();
        System.out.println("POSSSSS: "+ refUnitPosition);

        selectedPosition2PatPSV = refUnitPosition;

        patReferralUniPSVAdapter.notifyDataSetChanged();

        getRefServiceDataBySelect();
    }

    public void getRefServiceDataBySelect() {
        prescriptionLoadingPatPSV = true;
        fullLayoutPatPSV.setVisibility(View.GONE);
        circularProgressIndicatorPatPSV.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        patRefServiceListsPatPSV = new ArrayList<>();

        String patRefServUrl = pre_url_api+"prescription/getPatRefService?drd_id="+selected_drd_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest patRefServReq = new StringRequest(Request.Method.GET, patRefServUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String drs_id = info.getString("drs_id")
                                .equals("null") ? "" : info.getString("drs_id");
                        String drs_drd_id = info.getString("drs_drd_id")
                                .equals("null") ? "" : info.getString("drs_drd_id");
                        String pfn_id = info.getString("pfn_id")
                                .equals("null") ? "" : info.getString("pfn_id");
                        String pfn_fee_name = info.getString("pfn_fee_name")
                                .equals("null") ? "" : info.getString("pfn_fee_name");
                        String drs_qty = info.getString("drs_qty")
                                .equals("null") ? "" : info.getString("drs_qty");

                        patRefServiceListsPatPSV.add(new PatRefServiceList(drs_id,drs_drd_id,pfn_id,pfn_fee_name,drs_qty,selected_depts_id));

                    }
                }
                connected = true;
                updateRefServiceData();

            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateRefServiceData();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateRefServiceData();
        });

        requestQueue.add(patRefServReq);

    }

    private void updateRefServiceData() {
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;

                fullLayoutPatPSV.setVisibility(View.VISIBLE);
                circularProgressIndicatorPatPSV.setVisibility(View.GONE);

                if (patRefServiceListsPatPSV.isEmpty()) {
                    noRefServiceFoundMsgPatPSV.setVisibility(View.VISIBLE);
                }
                else {
                    noRefServiceFoundMsgPatPSV.setVisibility(View.GONE);
                }
                patRefServicePSVAdapter = new PatRefServicePSVAdapter(patRefServiceListsPatPSV,PatPrescriptionV.this);
                refServiceView.setAdapter(patRefServicePSVAdapter);
                prescriptionLoadingPatPSV = false;
            }
            else {
                alertMessageForGettingServ();
            }
        }
        else {
            alertMessageForGettingServ();
        }
    }

    public void alertMessageForGettingServ() {
        fullLayoutPatPSV.setVisibility(View.VISIBLE);
        circularProgressIndicatorPatPSV.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PatPrescriptionV.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    prescriptionLoadingPatPSV = false;
                    getRefServiceDataBySelect();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    prescriptionLoadingPatPSV = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        try {
            alert.show();
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }
    }

    public void restart(String msg) {
        try {
            ProcessPhoenix.triggerRebirth(getApplicationContext());
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }
}