package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.activity.OnBackPressedCallback;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists.MedicalHistoryList;

public class MedHistoryModify extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView close;
    TextView appbarName;

    TextInputLayout medHistoryLay;
    AppCompatAutoCompleteTextView medHistory;
    TextView medHistoryMissing;
    ArrayList<MedicalHistoryList> medicalHistoryLists;

    TextInputEditText details;

    Button modify;

    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";

    String type = "";
    String pmm_id = "";
    String mhm_name = "";
    String mhm_id = "";
    String mhm_details = "";
    boolean selectedFromItems = false;

    String pmh_id = "";

    Logger logger = Logger.getLogger(MedHistoryModify.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MedHistoryModify.this,R.color.clouds));
        setContentView(R.layout.activity_med_history_modify);

        fullLayout = findViewById(R.id.pat_med_history_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_med_history_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_pat_med_history_modify);
        appbarName = findViewById(R.id.pat_med_history_modify_app_bar_text);

        medHistoryLay = findViewById(R.id.spinner_layout_med_history_for_patient);
        medHistory = findViewById(R.id.med_history_for_patient_spinner);
        medHistoryMissing = findViewById(R.id.med_history_for_patient_missing_msg);
        medHistoryMissing.setVisibility(View.GONE);

        details = findViewById(R.id.med_history_details_for_patient_spinner);

        modify = findViewById(R.id.pat_med_history_modify_button);

        Intent intent = getIntent();
        pmm_id = intent.getStringExtra("P_PMM_ID");
        type = intent.getStringExtra("HIST_MODIFY_TYPE");

        System.out.println("PMM_ID: " + pmm_id);

        assert type != null;
        if (type.equals("ADD")) {
            String an = "Add Medical History";
            appbarName.setText(an);
            modify.setText(type);
        }
        else {
            String an = "Update Medical History";
            appbarName.setText(an);
            modify.setText(type);

            pmh_id = intent.getStringExtra("P_PMH_ID" );
            mhm_id = intent.getStringExtra("P_MHM_ID");
            mhm_name = intent.getStringExtra("P_MHM_NAME");
            mhm_details = intent.getStringExtra("P_PMH_DETAILS");

            medHistory.setText(mhm_name);
            details.setText(mhm_details);
        }

        medHistory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    medHistoryMissing.setVisibility(View.GONE);
                }
            }
        });

        medHistory.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <medicalHistoryLists.size(); i++) {
                if (name.equals(medicalHistoryLists.get(i).getMhm_name())) {
                    mhm_id = medicalHistoryLists.get(i).getMhm_id();
                    mhm_name = medicalHistoryLists.get(i).getMhm_name();
                }
            }

            selectedFromItems = true;
            medHistoryMissing.setVisibility(View.GONE);
            System.out.println("mhm_name: " + mhm_name);
            System.out.println("mhm_id: "+ mhm_id);
            closeKeyBoard();
        });

        medHistory.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = medHistory.getText().toString();
                if (!selectedFromItems) {
                    mhm_id = "";
                    for (int i = 0; i < medicalHistoryLists.size(); i++) {
                        if (ss.equals(medicalHistoryLists.get(i).getMhm_name())) {
                            mhm_id = medicalHistoryLists.get(i).getMhm_id();
                            mhm_name = medicalHistoryLists.get(i).getMhm_name();
                        }
                    }
                    if (mhm_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            medHistoryMissing.setVisibility(View.VISIBLE);
                            String mhmt = "Please Select Medical History";
                            medHistoryMissing.setText(mhmt);
                        }
                        else {
                            medHistoryMissing.setVisibility(View.VISIBLE);
                            String mhmt = "Invalid Medical History";
                            medHistoryMissing.setText(mhmt);
                        }
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        medHistory.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    medHistory.clearFocus();
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

        close.setOnClickListener(v -> finish());

        modify.setOnClickListener(v -> {
            mhm_details = Objects.requireNonNull(details.getText()).toString();
            if (!mhm_id.isEmpty()) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MedHistoryModify.this);
                if (type.equals("ADD")) {
                    alertDialogBuilder.setTitle("Add Medical History!")
                            .setMessage("Do you want to add patient's new Medical History?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                addMedicalHistory();
                            })
                            .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
                else {
                    alertDialogBuilder.setTitle("Update Medical History!")
                            .setMessage("Do you want to update this Medical History?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                updateMedicalHistory();
                            })
                            .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Provide Medical History",Toast.LENGTH_SHORT).show();
                medHistoryMissing.setVisibility(View.VISIBLE);
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        getData();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

//    @Override
//    public void onBackPressed() {
//
//    }

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

        medicalHistoryLists = new ArrayList<>();

        String medicalHistoryUrl = pre_url_api+"prescription/getMedHistory?pmm_id="+pmm_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest medicalHistoryReq = new StringRequest(Request.Method.GET, medicalHistoryUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (type.equals("UPDATE")) {
                    medicalHistoryLists.add(new MedicalHistoryList(mhm_id,mhm_name,""));
                }
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String mhm_id = info.getString("mhm_id")
                                .equals("null") ? "" : info.getString("mhm_id");
                        String mhm_name = info.getString("mhm_name")
                                .equals("null") ? "" : info.getString("mhm_name");
                        String mhm_details = info.getString("mhm_details")
                                .equals("null") ? "" : info.getString("mhm_details");

                        medicalHistoryLists.add(new MedicalHistoryList(mhm_id,mhm_name,mhm_details));
                    }
                }

                connected = true;
                updateInterface();
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

        requestQueue.add(medicalHistoryReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (medicalHistoryLists.isEmpty()) {
                    String mhmt = "No Medical History Found";
                    medHistoryMissing.setText(mhmt);
                    medHistoryMissing.setVisibility(View.VISIBLE);
                }
                else {
                    medHistoryMissing.setVisibility(View.GONE);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < medicalHistoryLists.size(); i++) {
                    type.add(medicalHistoryLists.get(i).getMhm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MedHistoryModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                medHistory.setAdapter(arrayAdapter);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MedHistoryModify.this);
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

    public void addMedicalHistory() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String insertComplain = pre_url_api+"prescription/insertMedicalHistory";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, insertComplain, response -> {
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
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAfterAdd();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAfterAdd();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PMM_ID",pmm_id);
                headers.put("P_MHM_ID",mhm_id);
                headers.put("P_DETAILS",mhm_details);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MedHistoryModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    addMedicalHistory();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void updateMedicalHistory() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateComplain = pre_url_api+"prescription/updateMedicalHistory";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, updateComplain, response -> {
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
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAfterUpdate();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAfterUpdate();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PMM_ID",pmm_id);
                headers.put("P_PMH_ID",pmh_id);
                headers.put("P_DETAILS",mhm_details);
                headers.put("P_MHM_ID",mhm_id);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MedHistoryModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateMedicalHistory();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}