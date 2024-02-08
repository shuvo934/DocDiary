package ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.PrescriptionSetup;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.ListOfComplains;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.ListOfInjuries;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.ReferralDoctorList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.ReferralUnitList;

public class ReferralUnitModify extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView close;
    TextView appbarName;

    TextInputLayout refUnitLay;
    AppCompatAutoCompleteTextView refUnit;
    TextView refUnitMissing;
    ArrayList<ReferralUnitList> referralUnitLists;

    TextInputLayout refDocsLay;
    AppCompatAutoCompleteTextView refDocs;
    TextView refDoctorInvalid;
    ArrayList<ReferralDoctorList> referralDoctorLists;

    Button modify, delete;


    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";

    String type = "";
    String pdi_id = "";
    String ref_unit_name = "";
    String depts_id = "";
    String ref_doc_name = "";
    String ref_doc_id = "";
    boolean selectedFromItems = false;

    String drd_id = "";
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ReferralUnitModify.this,R.color.clouds));
        setContentView(R.layout.activity_referral_unit_modify);


        fullLayout = findViewById(R.id.pat_referral_unit_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_referral_unit_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_pat_referral_unit_modify);
        appbarName = findViewById(R.id.pat_referral_unit_modify_app_bar_text);

        refUnitLay = findViewById(R.id.spinner_layout_referral_unit_for_patient);
        refUnit = findViewById(R.id.referral_unit_for_patient_spinner);
        refUnitMissing = findViewById(R.id.referral_unit_for_patient_missing_msg);
        refUnitMissing.setVisibility(View.GONE);

        refDocsLay = findViewById(R.id.spinner_layout_referral_doctor_for_patient);
        refDocs = findViewById(R.id.referral_doctor_for_patient_spinner);
        refDoctorInvalid = findViewById(R.id.referral_doctor_for_patient_missing_msg);
        refDoctorInvalid.setVisibility(View.GONE);

        modify = findViewById(R.id.pat_referral_unit_modify_button);
        delete = findViewById(R.id.pat_referral_unit_delete_button);

        referralDoctorLists = new ArrayList<>();
        referralUnitLists = new ArrayList<>();

        Intent intent = getIntent();
        pdi_id = intent.getStringExtra("P_PDI_ID");
        type = intent.getStringExtra("REF_MODIFY_TYPE");

        assert type != null;
        if (type.equals("ADD")) {
            String an = "Add Referral Unit";
            appbarName.setText(an);
            modify.setText(type);
            delete.setVisibility(View.GONE);
        }
        else {
            String an = "Update Referral Unit";
            appbarName.setText(an);
            modify.setText(type);
            delete.setVisibility(View.VISIBLE);

            drd_id = intent.getStringExtra("P_DRD_ID" );
            depts_id = intent.getStringExtra("P_DEPTS_ID");
            ref_unit_name = intent.getStringExtra("P_DEPTS_NAME");
            ref_doc_name = intent.getStringExtra("P_DOC_NAME");
            ref_doc_id = intent.getStringExtra("P_DOC_ID");
            position = intent.getIntExtra("POSITION",0);

            refUnit.setText(ref_unit_name);
            refDocs.setText(ref_doc_name);
        }

        refUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    refUnitMissing.setVisibility(View.GONE);
                }
            }
        });

        refUnit.setOnItemClickListener((parent, view, position, id) -> {
            refDocsLay.setEnabled(false);
            ref_doc_name = "";
            ref_doc_id = "";
            refDocs.setText("");
            refDoctorInvalid.setVisibility(View.GONE);

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <referralUnitLists.size(); i++) {
                if (name.equals(referralUnitLists.get(i).getDepts_name())) {
                    depts_id = referralUnitLists.get(i).getDepts_id();
                    ref_unit_name = referralUnitLists.get(i).getDepts_name();
                }
            }

            selectedFromItems = true;
            refUnitMissing.setVisibility(View.GONE);
            System.out.println("ref_unit_name: " + ref_unit_name);
            System.out.println("depts_id: "+ depts_id);
            closeKeyBoard();
            getDoctors();
        });

        refUnit.setOnFocusChangeListener((v, hasFocus) -> {
            refDocsLay.setEnabled(false);

            if (!hasFocus) {
                String ss = refUnit.getText().toString();
                if (!selectedFromItems) {
                    ref_doc_name = "";
                    ref_doc_id = "";
                    refDocs.setText("");
                    refDoctorInvalid.setVisibility(View.GONE);
                    depts_id = "";
                    for (int i = 0; i < referralUnitLists.size(); i++) {
                        if (ss.equals(referralUnitLists.get(i).getDepts_name())) {
                            depts_id = referralUnitLists.get(i).getDepts_id();
                            ref_unit_name = referralUnitLists.get(i).getDepts_name();

                        }
                    }
                    if (depts_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            refUnitMissing.setVisibility(View.VISIBLE);
                            refUnitMissing.setText("Please Select Referral Unit");
                        }
                        else {
                            refUnitMissing.setVisibility(View.VISIBLE);
                            refUnitMissing.setText("Invalid Referral Unit");
                        }
                    }
                    else {
                        getDoctors();
                    }
                    System.out.println(selectedFromItems);
                    System.out.println("ref_unit_name: " + ref_unit_name);
                    System.out.println("depts_id: "+ depts_id);
                }
                else {
                    selectedFromItems = false;
                    System.out.println(selectedFromItems);
                    System.out.println("ref_unit_name: " + ref_unit_name);
                    System.out.println("depts_id: "+ depts_id);
                }
            }
        });

        refUnit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    refUnit.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        refDocs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    refDoctorInvalid.setVisibility(View.GONE);
                }
            }
        });

        refDocs.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <referralDoctorLists.size(); i++) {
                if (name.equals(referralDoctorLists.get(i).getDoc_name())) {
                    ref_doc_id = referralDoctorLists.get(i).getDoc_id();
                    ref_doc_name = referralDoctorLists.get(i).getDoc_name();
                }
            }

            selectedFromItems = true;
            refDoctorInvalid.setVisibility(View.GONE);
            closeKeyBoard();
        });

        refDocs.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = refDocs.getText().toString();
                if (!selectedFromItems) {
                    ref_doc_id = "";
                    for (int i = 0; i < referralDoctorLists.size(); i++) {
                        if (ss.equals(referralDoctorLists.get(i).getDoc_name())) {
                            ref_doc_id = referralDoctorLists.get(i).getDoc_id();
                            ref_doc_name = referralDoctorLists.get(i).getDoc_name();

                        }
                    }
                    if (ref_doc_id.isEmpty()) {
                        if (!ss.isEmpty()) {
                            refDoctorInvalid.setVisibility(View.VISIBLE);
                            refDoctorInvalid.setText("Invalid Doctor Name");
                        }
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        refDocs.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    refDocs.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        close.setOnClickListener(v -> onBackPressed());

        modify.setOnClickListener(v -> {
            String ss = refDocs.getText().toString();
            if (!depts_id.isEmpty()) {
                if (!ss.isEmpty() && ref_doc_id.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Invalid Doctor selected. Please Select valid Doctor",Toast.LENGTH_SHORT).show();
                    refDoctorInvalid.setVisibility(View.VISIBLE);
                }
                else {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ReferralUnitModify.this);
                    if (type.equals("ADD")) {
                        alertDialogBuilder.setTitle("Add Referral Unit!")
                                .setMessage("Do you want to add patient's new Referral Unit?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    dialog.dismiss();
                                    addReferral();
                                })
                                .setNegativeButton("No",(dialog, which) -> {
                                    dialog.dismiss();
                                });

                        AlertDialog alert = alertDialogBuilder.create();
                        alert.show();
                    }
                    else {
                        alertDialogBuilder.setTitle("Update Referral Unit!")
                                .setMessage("Do you want to update this Referral Unit?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    dialog.dismiss();
                                    updateReferral();
                                })
                                .setNegativeButton("No",(dialog, which) -> {
                                    dialog.dismiss();
                                });

                        AlertDialog alert = alertDialogBuilder.create();
                        alert.show();
                    }
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Provide Referral Unit",Toast.LENGTH_SHORT).show();
                refUnitMissing.setVisibility(View.VISIBLE);
            }
        });

        delete.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ReferralUnitModify.this);
            builder.setMessage("Do you want to delete this Referral Unit?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        deleteReferral();
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        PrescriptionSetup.previousDataSelected = true;

        getData();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (loading) {
            Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
        }
        else {
            super.onBackPressed();
        }
    }

    private void closeKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void getData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        referralDoctorLists = new ArrayList<>();
        referralUnitLists = new ArrayList<>();

        String refUnitUrl = pre_url_api+"prescription/getReferralUnit?pdi_id="+pdi_id+"";
        String refDocUrl = pre_url_api+"prescription/getReferralDoctor?depts_id="+depts_id+"";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest refDocReq = new StringRequest(Request.Method.GET, refDocUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String doc_name = info.getString("doc_name")
                                .equals("null") ? "" : info.getString("doc_name");
                        String doc_code = info.getString("doc_code")
                                .equals("null") ? "" : info.getString("doc_code");
                        String desig_name = info.getString("desig_name")
                                .equals("null") ? "" : info.getString("desig_name");
                        String doc_id = info.getString("doc_id")
                                .equals("null") ? "" : info.getString("doc_id");

                        referralDoctorLists.add(new ReferralDoctorList(doc_name,doc_code,desig_name,doc_id));
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

        StringRequest refUnitDoubleReq = new StringRequest(Request.Method.GET, refUnitUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (type.equals("UPDATE")) {
                    referralUnitLists.add(new ReferralUnitList(depts_id,ref_unit_name,"","",""));
                }
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String depts_id = info.getString("depts_id")
                                .equals("null") ? "" : info.getString("depts_id");
                        String depts_name = info.getString("depts_name")
                                .equals("null") ? "" : info.getString("depts_name");
                        String deptd_name = info.getString("deptd_name")
                                .equals("null") ? "" : info.getString("deptd_name");
                        String depts_desc = info.getString("depts_desc")
                                .equals("null") ? "" : info.getString("depts_desc");
                        String deptm_name = info.getString("deptm_name")
                                .equals("null") ? "" : info.getString("deptm_name");

                        referralUnitLists.add(new ReferralUnitList(depts_id,depts_name,deptd_name,depts_desc,deptm_name));

                    }
                }

                requestQueue.add(refDocReq);
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

        StringRequest refUnitSingleReq = new StringRequest(Request.Method.GET, refUnitUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (type.equals("UPDATE")) {
                    referralUnitLists.add(new ReferralUnitList(depts_id,ref_unit_name,"","",""));
                }
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String depts_id = info.getString("depts_id")
                                .equals("null") ? "" : info.getString("depts_id");
                        String depts_name = info.getString("depts_name")
                                .equals("null") ? "" : info.getString("depts_name");
                        String deptd_name = info.getString("deptd_name")
                                .equals("null") ? "" : info.getString("deptd_name");
                        String depts_desc = info.getString("depts_desc")
                                .equals("null") ? "" : info.getString("depts_desc");
                        String deptm_name = info.getString("deptm_name")
                                .equals("null") ? "" : info.getString("deptm_name");

                        referralUnitLists.add(new ReferralUnitList(depts_id,depts_name,deptd_name,depts_desc,deptm_name));

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

        if (type.equals("ADD")) {
            requestQueue.add(refUnitSingleReq);
        }
        else {
            requestQueue.add(refUnitDoubleReq);
        }

    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (type.equals("ADD")) {
                    if (referralUnitLists.size() == 0) {
                        refUnitMissing.setText("No Referral Unit Found");
                        refUnitMissing.setVisibility(View.VISIBLE);
                    }
                    else {
                        refUnitMissing.setVisibility(View.GONE);
                    }

                    refDocsLay.setEnabled(false);

                    ArrayList<String> type = new ArrayList<>();
                    for(int i = 0; i < referralUnitLists.size(); i++) {
                        type.add(referralUnitLists.get(i).getDepts_name());
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ReferralUnitModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                    refUnit.setAdapter(arrayAdapter);

                }
                else {
                    if (referralUnitLists.size() == 0) {
                        refUnitMissing.setText("No Referral Unit Found");
                        refUnitMissing.setVisibility(View.VISIBLE);
                    }
                    else {
                        refUnitMissing.setVisibility(View.GONE);
                    }

                    refDocsLay.setEnabled(referralDoctorLists.size() != 0);

                    ArrayList<String> type = new ArrayList<>();
                    for(int i = 0; i < referralUnitLists.size(); i++) {
                        type.add(referralUnitLists.get(i).getDepts_name());
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ReferralUnitModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                    refUnit.setAdapter(arrayAdapter);

                    ArrayList<String> type1 = new ArrayList<>();
                    for(int i = 0; i < referralDoctorLists.size(); i++) {
                        type1.add(referralDoctorLists.get(i).getDoc_name());
                    }

                    ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(ReferralUnitModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                    refDocs.setAdapter(arrayAdapter1);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ReferralUnitModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getData();
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

    public void getDoctors() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        referralDoctorLists = new ArrayList<>();

        String refDocUrl = pre_url_api+"prescription/getReferralDoctor?depts_id="+depts_id+"";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest refDocReq = new StringRequest(Request.Method.GET, refDocUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String doc_name = info.getString("doc_name")
                                .equals("null") ? "" : info.getString("doc_name");
                        String doc_code = info.getString("doc_code")
                                .equals("null") ? "" : info.getString("doc_code");
                        String desig_name = info.getString("desig_name")
                                .equals("null") ? "" : info.getString("desig_name");
                        String doc_id = info.getString("doc_id")
                                .equals("null") ? "" : info.getString("doc_id");

                        referralDoctorLists.add(new ReferralDoctorList(doc_name,doc_code,desig_name,doc_id));
                    }
                }

                connected = true;
                updateDoctorsList();
            }
            catch (Exception e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateDoctorsList();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateDoctorsList();
        });

        requestQueue.add(refDocReq);
    }

    private void updateDoctorsList() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (referralDoctorLists.size() != 0) {
                    refDocsLay.setEnabled(true);
                    refDoctorInvalid.setText("Invalid Doctor Name");
                    refDoctorInvalid.setVisibility(View.GONE);
                }
                else {
                    refDocsLay.setEnabled(false);
                    refDoctorInvalid.setText("No Doctor Found");
                    refDoctorInvalid.setVisibility(View.VISIBLE);
                }


                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < referralDoctorLists.size(); i++) {
                    type1.add(referralDoctorLists.get(i).getDoc_name());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(ReferralUnitModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                refDocs.setAdapter(arrayAdapter1);


            }
            else {
                alertMessageForDocs();
            }
        }
        else {
            alertMessageForDocs();
        }
    }

    public void alertMessageForDocs() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ReferralUnitModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getDoctors();
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


    public void addReferral() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String insertRef = pre_url_api+"prescription/insertReferralUnit";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, insertRef, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;

                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterAdd();
                }
                else {
                    connected = false;
                    updateAfterAdd();
                }
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateAfterAdd();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateAfterAdd();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PDI_ID",pdi_id);
                headers.put("P_DEPTS_ID",depts_id);
                headers.put("P_DOC_ID",ref_doc_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void updateAfterAdd() {
        loading = false;
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                finish();
            }
            else {
                alertMessageFailedAdd();
            }
        }
        else {
            alertMessageFailedAdd();
        }
    }

    public void alertMessageFailedAdd() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ReferralUnitModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    addReferral();
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

    public void updateReferral() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateSLVUrl = pre_url_api+"prescription/updateReferralUnit";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, updateSLVUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;

                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterUpdate();
                }
                else {
                    connected = false;
                    updateAfterUpdate();
                }
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateAfterUpdate();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateAfterUpdate();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_DEPTS_ID",depts_id);
                headers.put("P_DOC_ID",ref_doc_id);
                headers.put("P_PDI_ID",pdi_id);
                headers.put("P_DRD_ID",drd_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void updateAfterUpdate() {
        loading = false;
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                finish();
            }
            else {
                alertMessageFailedUpdate();
            }
        }
        else {
            alertMessageFailedUpdate();
        }
    }

    public void alertMessageFailedUpdate() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ReferralUnitModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateReferral();
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

    public void deleteReferral() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateSLVUrl = pre_url_api+"prescription/deleteReferralUnit";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, updateSLVUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;

                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterDelete();
                }
                else {
                    connected = false;
                    updateAfterDelete();
                }
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateAfterDelete();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateAfterDelete();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_DRD_ID",drd_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void updateAfterDelete() {
        loading = false;
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                if (position == PrescriptionSetup.selectedPosition2) {
                    if (position > 0) {
                        PrescriptionSetup.selectedPosition2--;
                    }
                }
                else {
                    if (position < PrescriptionSetup.selectedPosition2) {
                        PrescriptionSetup.selectedPosition2--;
                    }
                }
                finish();
            }
            else {
                alertMessageFailedDelete();
            }
        }
        else {
            alertMessageFailedDelete();
        }
    }

    public void alertMessageFailedDelete() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ReferralUnitModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    deleteReferral();
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

}