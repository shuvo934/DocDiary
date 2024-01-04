package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.ListOfComplains;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists.ClinicalFindingsList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists.DrugNameList;

public class ClinicalFindingsModify extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView close;
    TextView appbarName;

    TextInputLayout clFindingsLay;
    AppCompatAutoCompleteTextView clFindings;
    TextView clFindingsMissing;
    ArrayList<ClinicalFindingsList> clinicalFindingsLists;

    TextInputEditText details;

    Button modify;

    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";

    String type = "";
    String pmm_id = "";
    String cfm_name = "";
    String cfm_id = "";
    String cf_details = "";
    boolean selectedFromItems = false;

    String cf_id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ClinicalFindingsModify.this,R.color.clouds));
        setContentView(R.layout.activity_clinical_findings_modify);

        fullLayout = findViewById(R.id.pat_cl_findings_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_cl_findings_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_pat_cl_findings_modify);
        appbarName = findViewById(R.id.pat_cl_findings_modify_app_bar_text);

        clFindingsLay = findViewById(R.id.spinner_layout_cl_findings_for_patient);
        clFindings = findViewById(R.id.cl_findings_for_patient_spinner);
        clFindingsMissing = findViewById(R.id.cl_findings_for_patient_missing_msg);
        clFindingsMissing.setVisibility(View.GONE);

        details = findViewById(R.id.cl_findings_details_for_patient_spinner);

        modify = findViewById(R.id.pat_cl_findings_modify_button);

        Intent intent = getIntent();
        pmm_id = intent.getStringExtra("P_PMM_ID");
        type = intent.getStringExtra("HIST_MODIFY_TYPE");

        assert type != null;
        if (type.equals("ADD")) {
            String an = "Add Clinical Findings";
            appbarName.setText(an);
            modify.setText(type);
        }
        else {
            String an = "Update Clinical Findings";
            appbarName.setText(an);
            modify.setText(type);

            cf_id = intent.getStringExtra("P_CF_ID" );
            cfm_id = intent.getStringExtra("P_CFM_ID");
            cfm_name = intent.getStringExtra("P_CFM_NAME");
            cf_details = intent.getStringExtra("P_CF_DETAILS");

            clFindings.setText(cfm_name);
            details.setText(cf_details);
        }

        clFindings.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    clFindingsMissing.setVisibility(View.GONE);
                }
            }
        });

        clFindings.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <clinicalFindingsLists.size(); i++) {
                if (name.equals(clinicalFindingsLists.get(i).getCfm_name())) {
                    cfm_id = clinicalFindingsLists.get(i).getCfm_id();
                    cfm_name = clinicalFindingsLists.get(i).getCfm_name();
                }
            }

            selectedFromItems = true;
            clFindingsMissing.setVisibility(View.GONE);
            System.out.println("cfm_id: " + cfm_id);
            System.out.println("cfm_name: "+ cfm_name);
            closeKeyBoard();
        });

        clFindings.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = clFindings.getText().toString();
                if (!selectedFromItems) {
                    cfm_id = "";
                    for (int i = 0; i < clinicalFindingsLists.size(); i++) {
                        if (ss.equals(clinicalFindingsLists.get(i).getCfm_name())) {
                            cfm_id = clinicalFindingsLists.get(i).getCfm_id();
                            cfm_name = clinicalFindingsLists.get(i).getCfm_name();
                        }
                    }
                    if (cfm_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            clFindingsMissing.setVisibility(View.VISIBLE);
                            clFindingsMissing.setText("Please Select Clinical Findings");
                        }
                        else {
                            clFindingsMissing.setVisibility(View.VISIBLE);
                            clFindingsMissing.setText("Invalid Clinical Findings");
                        }
                    }
                    System.out.println(selectedFromItems);
                    System.out.println("cfm_id: " + cfm_id);
                    System.out.println("cfm_name: "+ cfm_name);
                }
                else {
                    selectedFromItems = false;
                    System.out.println(selectedFromItems);
                    System.out.println("cfm_id: " + cfm_id);
                    System.out.println("cfm_name: "+ cfm_name);
                }
            }
        });

        clFindings.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    clFindings.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        details.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    details.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        close.setOnClickListener(v -> onBackPressed());

        modify.setOnClickListener(v -> {
            cf_details = Objects.requireNonNull(details.getText()).toString();
            if (!cfm_id.isEmpty()) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ClinicalFindingsModify.this);
                if (type.equals("ADD")) {
                    alertDialogBuilder.setTitle("Add Clinical Findings!")
                            .setMessage("Do you want to add patient's new Clinical Findings?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                addClinicalFindings();
                            })
                            .setNegativeButton("No",(dialog, which) -> {
                                dialog.dismiss();
                            });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
                else {
                    alertDialogBuilder.setTitle("Update Clinical Findings!")
                            .setMessage("Do you want to update this Clinical Findings?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                updateClinicalFindings();
                            })
                            .setNegativeButton("No",(dialog, which) -> {
                                dialog.dismiss();
                            });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Provide Clinical Findings",Toast.LENGTH_SHORT).show();
                clFindingsMissing.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
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

        clinicalFindingsLists = new ArrayList<>();

        String clFindingsUrl = pre_url_api+"prescription/getClinicalFindings?pmm_id="+pmm_id+"";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest clFindingsReq = new StringRequest(Request.Method.GET, clFindingsUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (type.equals("UPDATE")) {
                    clinicalFindingsLists.add(new ClinicalFindingsList(cfm_id,cfm_name,""));
                }
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String cfm_id = info.getString("cfm_id")
                                .equals("null") ? "" : info.getString("cfm_id");
                        String cfm_name = info.getString("cfm_name")
                                .equals("null") ? "" : info.getString("cfm_name");
                        String cfm_details = info.getString("cfm_details")
                                .equals("null") ? "" : info.getString("cfm_details");

                        clinicalFindingsLists.add(new ClinicalFindingsList(cfm_id,cfm_name,cfm_details));
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

        requestQueue.add(clFindingsReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (clinicalFindingsLists.size() == 0) {
                    clFindingsMissing.setText("No Clinical Findings Found");
                    clFindingsMissing.setVisibility(View.VISIBLE);
                }
                else {
                    clFindingsMissing.setVisibility(View.GONE);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < clinicalFindingsLists.size(); i++) {
                    type.add(clinicalFindingsLists.get(i).getCfm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ClinicalFindingsModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                clFindings.setAdapter(arrayAdapter);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ClinicalFindingsModify.this);
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

    public void addClinicalFindings() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String insertClf = pre_url_api+"prescription/insertClinicalFindings";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, insertClf, response -> {
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
                headers.put("P_PMM_ID",pmm_id);
                headers.put("P_CFM_ID",cfm_id);
                headers.put("P_DETAILS",cf_details);
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
//                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ComplainModify.this);
//                alertDialogBuilder.setTitle("Success!")
//                        .setMessage("New Complain Added.")
//                        .setPositiveButton("Ok", (dialog, which) -> {
//                            dialog.dismiss();
//                            finish();
//                        });
//
//                AlertDialog alert = alertDialogBuilder.create();
//                alert.setCancelable(false);
//                alert.setCanceledOnTouchOutside(false);
//                alert.show();
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ClinicalFindingsModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    addClinicalFindings();
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

    public void updateClinicalFindings() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateclfUrl = pre_url_api+"prescription/updateClinicalFindings";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, updateclfUrl, response -> {
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
                headers.put("P_PMM_ID",pmm_id);
                headers.put("P_CFM_ID",cfm_id);
                headers.put("P_DETAILS",cf_details);
                headers.put("P_CF_ID",cf_id);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ClinicalFindingsModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateClinicalFindings();
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