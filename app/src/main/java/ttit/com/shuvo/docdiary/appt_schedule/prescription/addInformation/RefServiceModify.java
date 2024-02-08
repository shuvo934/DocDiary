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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.PrescriptionSetup;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.RefServiceList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.ClinicalFindingsModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists.ClinicalFindingsList;

public class RefServiceModify extends AppCompatActivity {
    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView close;
    TextView appbarName;

    TextInputLayout refServiceLay;
    AppCompatAutoCompleteTextView refService;
    TextView refServiceMissing;
    ArrayList<RefServiceList> refServiceLists;

    TextInputEditText serviceQty;

    Button modify;

    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";

    String type = "";
    String drd_id = "";
    String depts_id = "";
    String pfn_fee_name = "";
    String pfn_id = "";
    String service_qty = "";
    boolean selectedFromItems = false;

    String drs_id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(RefServiceModify.this,R.color.clouds));
        setContentView(R.layout.activity_ref_service_modify);

        fullLayout = findViewById(R.id.pat_ref_service_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_ref_service_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_pat_ref_service_modify);
        appbarName = findViewById(R.id.pat_ref_service_modify_app_bar_text);

        refServiceLay = findViewById(R.id.spinner_layout_ref_service_for_patient);
        refService = findViewById(R.id.ref_service_for_patient_spinner);
        refServiceMissing = findViewById(R.id.ref_service_for_patient_missing_msg);
        refServiceMissing.setVisibility(View.GONE);

        serviceQty = findViewById(R.id.ref_service_qty_for_patient_spinner);

        modify = findViewById(R.id.pat_ref_service_modify_button);

        Intent intent = getIntent();
        depts_id = intent.getStringExtra("P_DEPTS_ID");
        drd_id = intent.getStringExtra("P_DRD_ID");
        type = intent.getStringExtra("SERVICE_MODIFY_TYPE");

        assert type != null;
        if (type.equals("ADD")) {
            String an = "Add Service";
            appbarName.setText(an);
            modify.setText(type);
        }
        else {
            String an = "Update Service";
            appbarName.setText(an);
            modify.setText(type);

            drs_id = intent.getStringExtra("P_DRS_ID" );
            pfn_id = intent.getStringExtra("P_PFN_ID");
            pfn_fee_name = intent.getStringExtra("P_PFN_NAME");
            service_qty = intent.getStringExtra("P_QTY");

            refService.setText(pfn_fee_name);
            serviceQty.setText(service_qty);
        }

        refService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    refServiceMissing.setVisibility(View.GONE);
                }
            }
        });

        refService.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <refServiceLists.size(); i++) {
                if (name.equals(refServiceLists.get(i).getPfn_fee_name())) {
                    pfn_id = refServiceLists.get(i).getPfn_id();
                    pfn_fee_name = refServiceLists.get(i).getPfn_fee_name();
                }
            }

            selectedFromItems = true;
            refServiceMissing.setVisibility(View.GONE);
            System.out.println("pfn_id: " + pfn_id);
            System.out.println("pfn_fee_name: "+ pfn_fee_name);
            closeKeyBoard();
        });

        refService.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = refService.getText().toString();
                if (!selectedFromItems) {
                    pfn_id = "";
                    for (int i = 0; i < refServiceLists.size(); i++) {
                        if (ss.equals(refServiceLists.get(i).getPfn_fee_name())) {
                            pfn_id = refServiceLists.get(i).getPfn_id();
                            pfn_fee_name = refServiceLists.get(i).getPfn_fee_name();
                        }
                    }
                    if (pfn_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            refServiceMissing.setVisibility(View.VISIBLE);
                            refServiceMissing.setText("Please Select Service Name");
                        }
                        else {
                            refServiceMissing.setVisibility(View.VISIBLE);
                            refServiceMissing.setText("Invalid Service Name");
                        }
                    }
                    System.out.println(selectedFromItems);
                    System.out.println("pfn_id: " + pfn_id);
                    System.out.println("pfn_fee_name: "+ pfn_fee_name);
                }
                else {
                    selectedFromItems = false;
                    System.out.println(selectedFromItems);
                    System.out.println("pfn_id: " + pfn_id);
                    System.out.println("pfn_fee_name: "+ pfn_fee_name);
                }
            }
        });

        refService.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    refService.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        serviceQty.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    serviceQty.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        close.setOnClickListener(v -> onBackPressed());

        modify.setOnClickListener(v -> {
            service_qty = Objects.requireNonNull(serviceQty.getText()).toString();
            if (!pfn_id.isEmpty()) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(RefServiceModify.this);
                if (type.equals("ADD")) {
                    alertDialogBuilder.setTitle("Add Service!")
                            .setMessage("Do you want to add patient's new Service?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                addRefServices();
                            })
                            .setNegativeButton("No",(dialog, which) -> {
                                dialog.dismiss();
                            });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
                else {
                    alertDialogBuilder.setTitle("Update Service!")
                            .setMessage("Do you want to update this Service?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                updateRefService();
                            })
                            .setNegativeButton("No",(dialog, which) -> {
                                dialog.dismiss();
                            });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Provide Service",Toast.LENGTH_SHORT).show();
                refServiceMissing.setVisibility(View.VISIBLE);
            }
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

        refServiceLists = new ArrayList<>();

        String refServUrl = pre_url_api+"prescription/getServices?drd_id="+drd_id+"&depts_id="+depts_id+"";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest refServReq = new StringRequest(Request.Method.GET, refServUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (type.equals("UPDATE")) {
                    refServiceLists.add(new RefServiceList(pfn_id,pfn_fee_name));
                }
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pfn_id = info.getString("pfn_id")
                                .equals("null") ? "" : info.getString("pfn_id");
                        String pfn_fee_name = info.getString("pfn_fee_name")
                                .equals("null") ? "" : info.getString("pfn_fee_name");

                        refServiceLists.add(new RefServiceList(pfn_id,pfn_fee_name));
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

        requestQueue.add(refServReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (refServiceLists.size() == 0) {
                    refServiceMissing.setText("No Service Found");
                    refServiceMissing.setVisibility(View.VISIBLE);
                }
                else {
                    refServiceMissing.setVisibility(View.GONE);
                }
                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < refServiceLists.size(); i++) {
                    type.add(refServiceLists.get(i).getPfn_fee_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(RefServiceModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                refService.setAdapter(arrayAdapter);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(RefServiceModify.this);
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

    public void addRefServices() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String insertService = pre_url_api+"prescription/insertRefService";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, insertService, response -> {
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
                headers.put("P_DRD_ID",drd_id);
                headers.put("P_PFN_ID",pfn_id);
                headers.put("P_QTY",service_qty);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(RefServiceModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    addRefServices();
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

    public void updateRefService() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateSLVUrl = pre_url_api+"prescription/updateRefService";
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
                headers.put("P_PFN_ID",pfn_id);
                headers.put("P_QTY",service_qty);
                headers.put("P_DRS_ID",drs_id);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(RefServiceModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateRefService();
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