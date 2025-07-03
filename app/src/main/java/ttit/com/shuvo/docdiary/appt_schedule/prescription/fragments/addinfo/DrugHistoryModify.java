package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists.DrugNameList;

public class DrugHistoryModify extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView close;
    TextView appbarName;

    TextInputLayout drugHistoryLay;
    AppCompatAutoCompleteTextView drugHistory;
    TextView drugHistoryMissing;
    ArrayList<DrugNameList> drugNameLists;

    TextInputEditText details;

    Button modify;

    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";

    String type = "";
    String pmm_id = "";
    String medicine_name = "";
    String medicine_id = "";
    String pdh_details = "";
    boolean selectedFromItems = false;

    String pdh_id = "";

    Logger logger = Logger.getLogger(DrugHistoryModify.class.getName());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (API 34) or Android 15 (API 35)
            WindowCompat.setDecorFitsSystemWindows(window, false);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.clouds));
            WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(false);
        } else {
            // Safe legacy approach for Android < 14
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.clouds));
        }
        setContentView(R.layout.activity_drug_history_modify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drug_hist_mod_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.pat_drug_history_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_drug_history_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_pat_drug_history_modify);
        appbarName = findViewById(R.id.pat_drug_history_modify_app_bar_text);

        drugHistoryLay = findViewById(R.id.spinner_layout_drug_history_for_patient);
        drugHistory = findViewById(R.id.drug_history_for_patient_spinner);
        drugHistoryMissing = findViewById(R.id.drug_history_for_patient_missing_msg);
        drugHistoryMissing.setVisibility(View.GONE);

        details = findViewById(R.id.drug_history_details_for_patient_spinner);

        modify = findViewById(R.id.pat_drug_history_modify_button);

        Intent intent = getIntent();
        pmm_id = intent.getStringExtra("P_PMM_ID");
        type = intent.getStringExtra("HIST_MODIFY_TYPE");

        assert type != null;
        if (type.equals("ADD")) {
            String an = "Add Drug History";
            appbarName.setText(an);
            modify.setText(type);
        }
        else {
            String an = "Update Drug History";
            appbarName.setText(an);
            modify.setText(type);

            pdh_id = intent.getStringExtra("P_PDH_ID" );
            medicine_id = intent.getStringExtra("P_MEDICINE_ID");
            medicine_name = intent.getStringExtra("P_MEDICINE_NAME");
            pdh_details = intent.getStringExtra("P_PDH_DETAILS");

            drugHistory.setText(medicine_name);
            details.setText(pdh_details);
        }

        drugHistory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    drugHistoryMissing.setVisibility(View.GONE);
                }
            }
        });

        drugHistory.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <drugNameLists.size(); i++) {
                if (name.equals(drugNameLists.get(i).getMedicine_name())) {
                    medicine_id = drugNameLists.get(i).getMedicine_id();
                    medicine_name = drugNameLists.get(i).getMedicine_name();
                }
            }

            selectedFromItems = true;
            drugHistoryMissing.setVisibility(View.GONE);
            System.out.println("medicine_id: " + medicine_id);
            System.out.println("medicine_name: "+ medicine_name);
            closeKeyBoard();
        });

        drugHistory.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = drugHistory.getText().toString();
                if (!selectedFromItems) {
                    medicine_id = "";
                    for (int i = 0; i < drugNameLists.size(); i++) {
                        if (ss.equals(drugNameLists.get(i).getMedicine_name())) {
                            medicine_id = drugNameLists.get(i).getMedicine_id();
                            medicine_name = drugNameLists.get(i).getMedicine_name();
                        }
                    }
                    if (medicine_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            drugHistoryMissing.setVisibility(View.VISIBLE);
                            String dhmt = "Please Select Drug Name";
                            drugHistoryMissing.setText(dhmt);
                        }
                        else {
                            drugHistoryMissing.setVisibility(View.VISIBLE);
                            String dhmt = "Invalid Drug Name";
                            drugHistoryMissing.setText(dhmt);
                        }
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        drugHistory.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    drugHistory.clearFocus();
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
            pdh_details = Objects.requireNonNull(details.getText()).toString();
            if (!medicine_id.isEmpty()) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DrugHistoryModify.this);
                if (type.equals("ADD")) {
                    alertDialogBuilder.setTitle("Add Drug History!")
                            .setMessage("Do you want to add patient's new Drug History?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                addDrugHistory();
                            })
                            .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
                else {
                    alertDialogBuilder.setTitle("Update Drug History!")
                            .setMessage("Do you want to update this Drug History?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                updateDrugHistory();
                            })
                            .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Provide Drug Name",Toast.LENGTH_SHORT).show();
                drugHistoryMissing.setVisibility(View.VISIBLE);
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

        drugNameLists = new ArrayList<>();

        String drugHistoryUrl = pre_url_api+"prescription/getDrugNames?pmm_id="+pmm_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest drugHistoryReq = new StringRequest(Request.Method.GET, drugHistoryUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (type.equals("UPDATE")) {
                    drugNameLists.add(new DrugNameList(medicine_id,medicine_name));
                }
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String medicine_id = info.getString("medicine_id")
                                .equals("null") ? "" : info.getString("medicine_id");
                        String medicine_name = info.getString("medicine_name")
                                .equals("null") ? "" : info.getString("medicine_name");

                        drugNameLists.add(new DrugNameList(medicine_id,medicine_name));
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

        requestQueue.add(drugHistoryReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (drugNameLists.isEmpty()) {
                    String dhmt = "No Drugs Found";
                    drugHistoryMissing.setText(dhmt);
                    drugHistoryMissing.setVisibility(View.VISIBLE);
                }
                else {
                    drugHistoryMissing.setVisibility(View.GONE);
                }
                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < drugNameLists.size(); i++) {
                    type.add(drugNameLists.get(i).getMedicine_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DrugHistoryModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                drugHistory.setAdapter(arrayAdapter);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DrugHistoryModify.this);
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

    public void addDrugHistory() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String insertComplain = pre_url_api+"prescription/insertDrugHistory";
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
                headers.put("P_MEDICINE_ID",medicine_id);
                headers.put("P_DETAILS",pdh_details);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DrugHistoryModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    addDrugHistory();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void updateDrugHistory() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updatedhUrl = pre_url_api+"prescription/updateDrugHistory";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, updatedhUrl, response -> {
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
                headers.put("P_PDH_ID",pdh_id);
                headers.put("P_DETAILS",pdh_details);
                headers.put("P_MEDICINE_ID",medicine_id);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DrugHistoryModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateDrugHistory();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}