package ttit.com.shuvo.docdiary.appt_schedule.prescription;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.adapters.ComplainAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.adapters.PatDiagnosisAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.adapters.PatRefServiceAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.adapters.PatReferralUnitAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.ComplainModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.RefServiceModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.ReferralUnitModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.DiagnosisModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.ComplainLists;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatDiagnosisList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatRefServiceList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatReferralList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.ClinicalFindings;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.DrugHistory;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.ManagementPlan;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.MedicalHistory;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.Medication;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.PatAdvice;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.PatReference;

public class PrescriptionSetup extends AppCompatActivity implements PatDiagnosisAdapter.ClickedItem, PatReferralUnitAdapter.ClickedItem2 {

    public static LinearLayout fullLayout;
    public static CircularProgressIndicator circularProgressIndicator;
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
    String prescription_type = "";
    String ph_id = "";
    String pmm_id = "";
    String pmm_date = "";
    String pat_cat_id = "";
    String doc_id = "";
    LinearLayout prescription_after_create_layout;
    MaterialButton create_prescription;
    private Boolean conn = false;
    private Boolean connected = false;
    public static boolean prescriptionLoading = false;
    public static boolean historyLoading = false;
    public static boolean medicationLoading = false;
    String parsing_message = "";

    ArrayList<ComplainLists> complainLists;
    RecyclerView complainView;
    RecyclerView.LayoutManager layoutManager;
    ComplainAdapter complainAdapter;
    public static TextView noComplainFoundMsg;
    MaterialButton complainAdd;

    public static TabLayout historytabLayout;
    public TabItem medHistTab, drugHistTab, clinicalFindTab;

    ArrayList<PatDiagnosisList> patDiagnosisLists;
    RecyclerView diagnosisView;
    RecyclerView.LayoutManager daigLayoutManager;
    PatDiagnosisAdapter patDiagnosisAdapter;
    TextView noDiagnosisFoundMsg;
    MaterialButton diagnosisAdd;

    LinearLayout referralUnitLay;
    public static ArrayList<PatReferralList> patReferralLists;
    RecyclerView referralView;
    RecyclerView.LayoutManager refLayoutManager;
    public static PatReferralUnitAdapter patReferralUnitAdapter;
    TextView noReferralFoundMsg;
    MaterialButton referralAdd;
    String selected_pdi_id = "";

    LinearLayout refServiceUnitLay;
    public static ArrayList<PatRefServiceList> patRefServiceLists;
    RecyclerView refServiceView;
    RecyclerView.LayoutManager refServLayoutManager;
    PatRefServiceAdapter patRefServiceAdapter;
    public static TextView noRefServiceFoundMsg;
    MaterialButton serviceAdd;
    String selected_drd_id = "";
    String selected_depts_id = "";

    public static int selectedPosition = 0;
    public static int selectedPosition2 = 0;
    public static boolean previousDataSelected = false;

    public static TabLayout medicationtabLayout;
    public TabItem managementTab, medicineTab, adviceTab, referenceTab;

    CheckBox patAdmission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_setup);

        fullLayout = findViewById(R.id.prescription_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_prescription);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.back_logo_of_prescription);

        patName = findViewById(R.id.prescription_patient_name);
        patSubCode = findViewById(R.id.prescription_patient_ph_code);
        mdtCode = findViewById(R.id.patient_mdt_code_in_prescription);
        patAge = findViewById(R.id.patient_age_in_prescription);
        patMaritalStat = findViewById(R.id.patient_marital_status_in_prescription);
        patGender = findViewById(R.id.patient_gender_in_prescription);
        patBlood = findViewById(R.id.patient_blood_group_in_prescription);
        patCatgory = findViewById(R.id.patient_category_in_prescription);
        patStatus = findViewById(R.id.patient_status_in_prescription);
        patAddress = findViewById(R.id.patient_address_in_prescription);

        prescription_after_create_layout = findViewById(R.id.after_creating_prescription);
        create_prescription = findViewById(R.id.create_prescription_button_from_prescription);

        complainView = findViewById(R.id.complain_injury_recyclerview);
        noComplainFoundMsg = findViewById(R.id.no_information_found_msg_complain);
        complainAdd = findViewById(R.id.add_complain_for_patient);

        historytabLayout = findViewById(R.id.history_findings_layout);
        medHistTab = findViewById(R.id.medical_history_tab);
        drugHistTab = findViewById(R.id.drug_history_tab);
        clinicalFindTab = findViewById(R.id.clinical_findings_tab);

        diagnosisView = findViewById(R.id.diagnosis_recyclerview);
        noDiagnosisFoundMsg = findViewById(R.id.no_information_found_msg_diagnosis);
        diagnosisAdd = findViewById(R.id.add_diagnosis);

        referralUnitLay = findViewById(R.id.referral_unit_layout);

        referralView = findViewById(R.id.referral_unit_recyclerview);
        noReferralFoundMsg = findViewById(R.id.no_information_found_msg_referral_unit);
        referralAdd = findViewById(R.id.add_referral_unit);

        refServiceUnitLay = findViewById(R.id.service_unit_layout);

        refServiceView = findViewById(R.id.service_unit_recyclerview);
        noRefServiceFoundMsg = findViewById(R.id.no_information_found_msg_service_unit);
        serviceAdd = findViewById(R.id.add_service_unit);

        medicationtabLayout = findViewById(R.id.medication_plan_layout);
        managementTab = findViewById(R.id.management_plan_tab);
        medicineTab = findViewById(R.id.medication_tab);
        adviceTab = findViewById(R.id.advice_tab);
        referenceTab = findViewById(R.id.reference_tab);

        patAdmission = findViewById(R.id.pat_admission_flag_checkbox);

        Intent intent = getIntent();
        pat_age = intent.getStringExtra("P_AGE");
        prescription_type = intent.getStringExtra("PRESCRIPTION_TYPE");
        ph_id = intent.getStringExtra("P_PH_ID");

        doc_id = userInfoLists.get(0).getDoc_id();

        complainLists = new ArrayList<>();
        patDiagnosisLists = new ArrayList<>();
        patReferralLists = new ArrayList<>();
        patRefServiceLists = new ArrayList<>();

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

        close.setOnClickListener(v -> onBackPressed());

        create_prescription.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PrescriptionSetup.this);
            alertDialogBuilder.setTitle("Create Prescription!")
                    .setMessage("Do you want to create prescription for this patient?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        createPrescription();
                    })
                    .setNegativeButton("No",(dialog, which) -> {
                        dialog.dismiss();
                    });

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        });

        complainAdd.setOnClickListener(v -> {
            Intent intent1 = new Intent(PrescriptionSetup.this, ComplainModify.class);
            intent1.putExtra("P_PMM_ID", pmm_id);
            intent1.putExtra("MODIFY_TYPE","ADD");
            startActivity(intent1);
        });

        historytabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab, MedicalHistory.newInstance(pmm_id, "false"), "MHTEST").commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab, DrugHistory.newInstance(pmm_id, "false"), "DHTEST").commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab, ClinicalFindings.newInstance(pmm_id,"false"),"CFTEST").commit();
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

        diagnosisAdd.setOnClickListener(v -> {
            Intent intent1 = new Intent(PrescriptionSetup.this, DiagnosisModify.class);
            intent1.putExtra("P_PMM_ID",pmm_id);
            intent1.putExtra("DIAG_MODIFY_TYPE","ADD");
            startActivity(intent1);
        });

        referralAdd.setOnClickListener(v -> {
            Intent intent1 = new Intent(PrescriptionSetup.this, ReferralUnitModify.class);
            intent1.putExtra("P_PDI_ID",selected_pdi_id);
            intent1.putExtra("REF_MODIFY_TYPE","ADD");
            startActivity(intent1);
        });

        serviceAdd.setOnClickListener(v -> {
            Intent intent1 = new Intent(PrescriptionSetup.this, RefServiceModify.class);
            intent1.putExtra("P_DRD_ID",selected_drd_id);
            intent1.putExtra("P_DEPTS_ID", selected_depts_id);
            intent1.putExtra("SERVICE_MODIFY_TYPE","ADD");
            startActivity(intent1);
        });

        medicationtabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab, ManagementPlan.newInstance(pmm_id,"false"),"MPTEST").commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab, Medication.newInstance(pmm_id, "false"), "MEDTEST").commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab, PatAdvice.newInstance(pmm_id,"false"),"PATEST").commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab, PatReference.newInstance(pmm_id,"false"),"PRTEST").commit();
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

        patAdmission.setOnClickListener(v -> {
            if (patAdmission.isChecked()) {
                System.out.println("true");
                updatePatAdmFlag("1");
            }
            else {
                System.out.println("false");
                updatePatAdmFlag("0");
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPrescriptionData();
    }

    @Override
    public void onBackPressed() {
        System.out.println(historyLoading);
        System.out.println(prescriptionLoading);
        System.out.println(medicationLoading);
        if (prescriptionLoading || historyLoading || medicationLoading) {
            Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
        }
        else {
            super.onBackPressed();
        }
    }

    public void getPrescriptionData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        prescriptionLoading = true;

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

        String newPresUrl = pre_url_api+"prescription/getNewPatData?p_ph_id="+ph_id+"";
        String oldPresUrl = pre_url_api+"prescription/getOldPatData?p_ph_id="+ph_id+"";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest newPresReq = new StringRequest(Request.Method.GET, newPresUrl, response -> {
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
                        pat_cat_id = info.getString("pat_category_id")
                                .equals("null") ? "" : info.getString("pat_category_id");
                    }
                }

                connected = true;
                updateInterface();
            }
            catch (Exception e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        StringRequest oldPresReq = new StringRequest(Request.Method.GET, oldPresUrl, response -> {
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
                    }
                }

                getOldPrescriptionData();
            }
            catch (Exception e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        if (prescription_type.equals("1")) {
            requestQueue.add(oldPresReq);
        }
        else {
            requestQueue.add(newPresReq);
        }
    }

    public void getOldPrescriptionData() {
        complainLists = new ArrayList<>();
        patDiagnosisLists = new ArrayList<>();
        patReferralLists = new ArrayList<>();
        patRefServiceLists = new ArrayList<>();
        selected_pdi_id = "";
        selected_drd_id = "";
        selected_depts_id = "";
        if (!previousDataSelected) {
            selectedPosition = 0;
            selectedPosition2 = 0;
        }


        String complainUrl = pre_url_api+"prescription/getComplainData?pmm_id="+pmm_id+"";
        String patDiagUrl = pre_url_api+"prescription/getPatDiagnosis?pmm_id="+pmm_id+"";
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

                        if (i==selectedPosition) {
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
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
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
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(complainReq);
    }

    public void getReferralUnit(String pdi_id) {
        String patRefUrl = pre_url_api+"prescription/getPatReferral?pdi_id="+pdi_id+"";
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

                        patReferralLists.add(new PatReferralList(drd_id,drd_pdi_id,depts_id,depts_name,doc_id,doc_name,child_count));

                        if (i==selectedPosition2) {
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
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(patRefReq);
    }

    public void getRefService(String drd_id, int choice) {
        String patRefServUrl = pre_url_api+"prescription/getPatRefService?drd_id="+drd_id+"";
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

                        patRefServiceLists.add(new PatRefServiceList(drs_id,drs_drd_id,pfn_id,pfn_fee_name,drs_qty,selected_depts_id));

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
                e.printStackTrace();
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
            error.printStackTrace();
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
        prescriptionLoading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;
                previousDataSelected = false;

                if (prescription_type.equals("1")) {
                    create_prescription.setVisibility(View.GONE);
                    prescription_after_create_layout.setVisibility(View.VISIBLE);
                }
                else {
                    create_prescription.setVisibility(View.VISIBLE);
                    prescription_after_create_layout.setVisibility(View.GONE);
                }

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

                if (complainLists.size() == 0) {
                    noComplainFoundMsg.setVisibility(View.VISIBLE);
                }
                else {
                    noComplainFoundMsg.setVisibility(View.GONE);
                }
                complainAdapter = new ComplainAdapter(complainLists,PrescriptionSetup.this);
                complainView.setAdapter(complainAdapter);

                System.out.println("PMM_ID: "+pmm_id);
                System.out.println("PMM_CODE: "+ mdt_code);

                int tab_pos  = historytabLayout.getSelectedTabPosition();
                System.out.println("TAB POS: "+tab_pos);
                if (tab_pos == 0) {
                    System.out.println("EKHANE 1");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab,MedicalHistory.newInstance(pmm_id,"false"),"MHTEST").commit();
                }
                else if (tab_pos == 1) {
                    System.out.println("EKHANE 2");
//                    TabLayout.Tab tab = historytabLayout.getTabAt(0);
//                    historytabLayout.selectTab(tab);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab,DrugHistory.newInstance(pmm_id,"false"),"DHTEST").commit();
                }
                else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_history_tab, ClinicalFindings.newInstance(pmm_id,"false"),"CFTEST").commit();
                }

                //------
                if (patDiagnosisLists.size() == 0) {
                    noDiagnosisFoundMsg.setVisibility(View.VISIBLE);
                    referralUnitLay.setVisibility(View.GONE);
                    refServiceUnitLay.setVisibility(View.GONE);
                }
                else {
                    noDiagnosisFoundMsg.setVisibility(View.GONE);
                    referralUnitLay.setVisibility(View.VISIBLE);
                    refServiceUnitLay.setVisibility(View.GONE);
                }
                patDiagnosisAdapter = new PatDiagnosisAdapter(patDiagnosisLists,PrescriptionSetup.this,PrescriptionSetup.this);
                diagnosisView.setAdapter(patDiagnosisAdapter);

                //------
                if (patReferralLists.size() == 0) {
                    noReferralFoundMsg.setVisibility(View.VISIBLE);
                    refServiceUnitLay.setVisibility(View.GONE);
                }
                else {
                    noReferralFoundMsg.setVisibility(View.GONE);
                    refServiceUnitLay.setVisibility(View.VISIBLE);
                }
                patReferralUnitAdapter = new PatReferralUnitAdapter(patReferralLists,PrescriptionSetup.this,PrescriptionSetup.this);
                referralView.setAdapter(patReferralUnitAdapter);

                //------
                if (patRefServiceLists.size() == 0) {
                    noRefServiceFoundMsg.setVisibility(View.VISIBLE);
                }
                else {
                    noRefServiceFoundMsg.setVisibility(View.GONE);
                }
                patRefServiceAdapter = new PatRefServiceAdapter(patRefServiceLists,PrescriptionSetup.this);
                refServiceView.setAdapter(patRefServiceAdapter);

                int med_tab_pos  = medicationtabLayout.getSelectedTabPosition();
                System.out.println("MED TAB POS: "+med_tab_pos);
                if (med_tab_pos == 0) {
                    System.out.println("EKHANE 1");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab, ManagementPlan.newInstance(pmm_id,"false"),"MPTEST").commit();
                }
                else if (med_tab_pos == 1) {
                    System.out.println("EKHANE 2");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab, Medication.newInstance(pmm_id,"false"),"MEDTEST").commit();
                }
                else if (med_tab_pos == 2) {
                    System.out.println("EKHANE 3");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab, PatAdvice.newInstance(pmm_id,"false"),"PATEST").commit();
                }
                else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_medication_plan_tab, PatReference.newInstance(pmm_id,"false"),"PRTEST").commit();
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
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PrescriptionSetup.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getPrescriptionData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public static void updateDiagnosis() {
//        if (patDiagnosisLists.size() == 0) {
//            noDiagnosisFoundMsg.setVisibility(View.VISIBLE);
//            referralUnitLay.setVisibility(View.GONE);
//            refServiceUnitLay.setVisibility(View.GONE);
//        }
//        else {
//            noDiagnosisFoundMsg.setVisibility(View.GONE);
//            referralUnitLay.setVisibility(View.VISIBLE);
//            refServiceUnitLay.setVisibility(View.GONE);
//        }
//        patDiagnosisAdapter = new PatDiagnosisAdapter(patDiagnosisLists,PrescriptionSetup.this,PrescriptionSetup.this);
//        diagnosisView.setAdapter(patDiagnosisAdapter);

        //------
//        if (patReferralLists.size() == 0) {
//            noReferralFoundMsg.setVisibility(View.VISIBLE);
//            refServiceUnitLay.setVisibility(View.GONE);
//        }
//        else {
//            noReferralFoundMsg.setVisibility(View.GONE);
//            refServiceUnitLay.setVisibility(View.VISIBLE);
//        }
//        patReferralUnitAdapter = new PatReferralUnitAdapter(patReferralLists,PrescriptionSetup.this,PrescriptionSetup.this);
//        referralView.setAdapter(patReferralUnitAdapter);

        //------
        if (patRefServiceLists.size() == 0) {
            noRefServiceFoundMsg.setVisibility(View.VISIBLE);
        }
        else {
            noRefServiceFoundMsg.setVisibility(View.GONE);
        }
        patReferralUnitAdapter.notifyDataSetChanged();
    }


    // Creating Prescription API
    public void createPrescription() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        prescriptionLoading = true;

        String insertPrescriptionUrl = pre_url_api+"prescription/insertNewPrescription";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, insertPrescriptionUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                String pmm = jsonObject.getString("out_pmm_id")
                        .equals("null") ? "" : jsonObject.getString("out_pmm_id");
                String pmm_code = jsonObject.getString("out_pmm_code")
                        .equals("null") ? "" : jsonObject.getString("out_pmm_code");
                if (string_out.equals("Successfully Created")) {
                    if (!pmm.isEmpty()) {
                        pmm_id = pmm;
                        mdt_code = pmm_code;
                        connected = true;
                        prescriptionCreatedInterface();
                    }
                    else {
                        parsing_message = "PMM_ID is Empty";
                        connected = false;
                        prescriptionCreatedInterface();
                    }
                }
                else {
                    connected = false;
                    prescriptionCreatedInterface();
                }
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                prescriptionCreatedInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            prescriptionCreatedInterface();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PH_ID",ph_id);
                headers.put("P_CAT_ID",pat_cat_id);
                headers.put("P_USER",doc_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void prescriptionCreatedInterface() {
        if (conn) {
            if (connected) {
                prescription_type = "1";
                getPrescriptionData();
            }
            else {
                prescriptionLoading = false;
                alertMessageForCreationPresc();
            }
        }
        else {
            prescriptionLoading = false;
            alertMessageForCreationPresc();
        }
    }

    public void alertMessageForCreationPresc() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PrescriptionSetup.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    createPrescription();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @Override
    public void onCategoryClicked(int CategoryPosition) {
        selected_pdi_id = patDiagnosisLists.get(CategoryPosition).getPdi_id();

        System.out.println(CategoryPosition);
        selectedPosition = CategoryPosition;
        selectedPosition2 = 0;

        patDiagnosisAdapter.notifyDataSetChanged();

        getReferralDataBySelect();
    }

    public void getReferralDataBySelect() {
        prescriptionLoading = true;
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        patReferralLists = new ArrayList<>();
        patRefServiceLists = new ArrayList<>();
        selected_drd_id = "";
        selected_depts_id = "";

        String patRefUrl = pre_url_api+"prescription/getPatReferral?pdi_id="+selected_pdi_id+"";
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

                        patReferralLists.add(new PatReferralList(drd_id,drd_pdi_id,depts_id,depts_name,doc_id,doc_name,child_count));

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
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateReferralData();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateReferralData();
        });

        requestQueue.add(patRefReq);
    }

    private void updateReferralData() {
        prescriptionLoading = false;
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;

                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);

                if (patReferralLists.size() == 0) {
                    noReferralFoundMsg.setVisibility(View.VISIBLE);
                    refServiceUnitLay.setVisibility(View.GONE);
                }
                else {
                    noReferralFoundMsg.setVisibility(View.GONE);
                    refServiceUnitLay.setVisibility(View.VISIBLE);
                }
                patReferralUnitAdapter = new PatReferralUnitAdapter(patReferralLists,PrescriptionSetup.this,PrescriptionSetup.this);
                referralView.setAdapter(patReferralUnitAdapter);

                if (patRefServiceLists.size() == 0) {
                    noRefServiceFoundMsg.setVisibility(View.VISIBLE);
                }
                else {
                    noRefServiceFoundMsg.setVisibility(View.GONE);
                }
                patRefServiceAdapter = new PatRefServiceAdapter(patRefServiceLists,PrescriptionSetup.this);
                refServiceView.setAdapter(patRefServiceAdapter);
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
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PrescriptionSetup.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getReferralDataBySelect();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @Override
    public void onCategoryClicked2(int CategoryPosition2) {
        selected_drd_id = patReferralLists.get(CategoryPosition2).getDrd_id();
        selected_depts_id = patReferralLists.get(CategoryPosition2).getDepts_id();
        System.out.println("POSSSSS: "+ CategoryPosition2);

        selectedPosition2 = CategoryPosition2;

        patReferralUnitAdapter.notifyDataSetChanged();

//        new Level3Check().execute();
        getRefServiceDataBySelect();
    }

    public void getRefServiceDataBySelect() {
        prescriptionLoading = true;
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        patRefServiceLists = new ArrayList<>();

        String patRefServUrl = pre_url_api+"prescription/getPatRefService?drd_id="+selected_drd_id+"";
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

                        patRefServiceLists.add(new PatRefServiceList(drs_id,drs_drd_id,pfn_id,pfn_fee_name,drs_qty,selected_depts_id));

                    }
                }
                connected = true;
                updateRefServiceData();

            }
            catch (Exception e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateRefServiceData();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateRefServiceData();
        });

        requestQueue.add(patRefServReq);

    }

    private void updateRefServiceData() {
        prescriptionLoading = false;
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;

                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);

                if (patRefServiceLists.size() == 0) {
                    noRefServiceFoundMsg.setVisibility(View.VISIBLE);
                }
                else {
                    noRefServiceFoundMsg.setVisibility(View.GONE);
                }
                patRefServiceAdapter = new PatRefServiceAdapter(patRefServiceLists,PrescriptionSetup.this);
                refServiceView.setAdapter(patRefServiceAdapter);
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
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PrescriptionSetup.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getRefServiceDataBySelect();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void updatePatAdmFlag(String flag) {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        prescriptionLoading = true;

        String upPAFUrl = pre_url_api+"prescription/updatePatAdmFlag";
        RequestQueue requestQueue = Volley.newRequestQueue(PrescriptionSetup.this);

        StringRequest request = new StringRequest(Request.Method.POST, upPAFUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterUpdatePAF(flag);
                }
                else {
                    connected = false;
                    updateAfterUpdatePAF(flag);
                }
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateAfterUpdatePAF(flag);
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateAfterUpdatePAF(flag);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_FLAG",flag);
                headers.put("P_PMM_ID",pmm_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void updateAfterUpdatePAF(String flag) {
        prescriptionLoading = false;
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;
            }
            else {
                boolean pat = patAdmission.isChecked();
                patAdmission.setChecked(!pat);
                alertMessageForFaiedUpatePAF(flag);
            }
        }
        else {
            boolean pat = patAdmission.isChecked();
            patAdmission.setChecked(!pat);
            alertMessageForFaiedUpatePAF(flag);
        }
    }

    public void alertMessageForFaiedUpatePAF(String flag) {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PrescriptionSetup.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updatePatAdmFlag(flag);
                    dialog.dismiss();
                })
                .setNegativeButton("cancel",(dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}