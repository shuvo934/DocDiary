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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists.DoseList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists.MedTakeTimeList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.arraylists.MedicineList;

public class MedicationModify extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView close;
    TextView appbarName;

    TextInputLayout medicineLay;
    AppCompatAutoCompleteTextView medicine;
    TextView medicineMissing;
    ArrayList<MedicineList> medicineLists;

    TextInputLayout medicineTakeLay;
    AppCompatAutoCompleteTextView medicineTake;
    TextView medicineTakeMissing;
    ArrayList<MedTakeTimeList> medTakeTimeLists;

    TextInputLayout doseLay;
    AppCompatAutoCompleteTextView dose;
    TextView doseMissing;
    ArrayList<DoseList> doseLists;

    TextInputEditText duration;

    Button modify;

    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";

    String type = "";
    String pmm_id = "";
    String medicine_name = "";
    String medicine_id = "";
    String mpm_name = "";
    String mpm_id = "";
    String dose_name = "";
    String dose_id = "";
    String med_duration = "";
    boolean selectedFromItems = false;

    String pmp_id = "";
    
    Logger logger = Logger.getLogger(MedicationModify.class.getName());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MedicationModify.this,R.color.clouds));
        setContentView(R.layout.activity_medication_modify);

        fullLayout = findViewById(R.id.pat_medication_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_medication_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_pat_medication_modify);
        appbarName = findViewById(R.id.pat_medication_modify_app_bar_text);

        medicineLay = findViewById(R.id.spinner_layout_medicine_for_patient);
        medicine = findViewById(R.id.medicine_for_patient_spinner);
        medicineMissing = findViewById(R.id.medicine_for_patient_missing_msg);
        medicineMissing.setVisibility(View.GONE);

        medicineTakeLay = findViewById(R.id.spinner_layout_medicine_time_for_patient);
        medicineTake = findViewById(R.id.medicine_time_for_patient_spinner);
        medicineTakeMissing = findViewById(R.id.medicine_time_for_patient_missing_msg);
        medicineTakeMissing.setVisibility(View.GONE);

        doseLay = findViewById(R.id.spinner_layout_medicine_dose_for_patient);
        dose = findViewById(R.id.medicine_dose_for_patient_spinner);
        doseMissing = findViewById(R.id.medicine_dose_for_patient_missing_msg);
        doseMissing.setVisibility(View.GONE);

        duration = findViewById(R.id.medicine_duration_for_patient_spinner);

        modify = findViewById(R.id.pat_medication_modify_button);

        Intent intent = getIntent();
        pmm_id = intent.getStringExtra("P_PMM_ID");
        type = intent.getStringExtra("MED_MODIFY_TYPE");

        assert type != null;
        if (type.equals("ADD")) {
            String an = "Add Medicine";
            appbarName.setText(an);
            modify.setText(type);
        }
        else {
            String an = "Update Medicine";
            appbarName.setText(an);
            modify.setText(type);

            pmp_id = intent.getStringExtra("P_PMP_ID" );
            medicine_id = intent.getStringExtra("P_MEDICINE_ID");
            medicine_name = intent.getStringExtra("P_MEDICINE_NAME");
            dose_id = intent.getStringExtra("P_DOSE_ID");
            dose_name = intent.getStringExtra("P_DOSE_NAME" );
            mpm_name = intent.getStringExtra("P_MPM_NAME");
            mpm_id = intent.getStringExtra("P_MPM_ID");
            med_duration = intent.getStringExtra("P_DURATION");

            medicine.setText(medicine_name);
            medicineTake.setText(mpm_name);
            dose.setText(dose_name);
            duration.setText(med_duration);
        }

        medicine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    medicineMissing.setVisibility(View.GONE);
                }
            }
        });

        medicine.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <medicineLists.size(); i++) {
                if (name.equals(medicineLists.get(i).getMedicine_name())) {
                    medicine_id = medicineLists.get(i).getMedicine_id();
                    medicine_name = medicineLists.get(i).getMedicine_name();
                }
            }

            selectedFromItems = true;
            medicineMissing.setVisibility(View.GONE);
            System.out.println("medicine_name: " + medicine_name);
            System.out.println("medicine_name: "+ medicine_name);
            closeKeyBoard();
        });

        medicine.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = medicine.getText().toString();
                if (!selectedFromItems) {
                    medicine_id = "";
                    for (int i = 0; i < medicineLists.size(); i++) {
                        if (ss.equals(medicineLists.get(i).getMedicine_name())) {
                            medicine_id = medicineLists.get(i).getMedicine_id();
                            medicine_name = medicineLists.get(i).getMedicine_name();
                        }
                    }
                    if (medicine_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            medicineMissing.setVisibility(View.VISIBLE);
                            String mmt = "Please Select Medicine";
                            medicineMissing.setText(mmt);
                        }
                        else {
                            medicineMissing.setVisibility(View.VISIBLE);
                            String mmt = "Invalid Medicine";
                            medicineMissing.setText(mmt);
                        }
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        medicine.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    medicine.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        medicineTake.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    medicineTakeMissing.setVisibility(View.GONE);
                }
            }
        });

        medicineTake.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <medTakeTimeLists.size(); i++) {
                if (name.equals(medTakeTimeLists.get(i).getMpm_name())) {
                    mpm_id = medTakeTimeLists.get(i).getMpm_id();
                    mpm_name = medTakeTimeLists.get(i).getMpm_name();
                }
            }

            selectedFromItems = true;
            medicineTakeMissing.setVisibility(View.GONE);
            closeKeyBoard();
        });

        medicineTake.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = medicineTake.getText().toString();
                if (!selectedFromItems) {
                    mpm_id = "";
                    for (int i = 0; i < medTakeTimeLists.size(); i++) {
                        if (ss.equals(medTakeTimeLists.get(i).getMpm_name())) {
                            mpm_id = medTakeTimeLists.get(i).getMpm_id();
                            mpm_name = medTakeTimeLists.get(i).getMpm_name();
                        }
                    }
                    if (mpm_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            medicineTakeMissing.setVisibility(View.VISIBLE);
                            String mtmt = "Please Select Medicine Time";
                            medicineTakeMissing.setText(mtmt);
                        }
                        else {
                            medicineTakeMissing.setVisibility(View.VISIBLE);
                            String mtmt = "Invalid Medicine Time";
                            medicineTakeMissing.setText(mtmt);
                        }
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        medicineTake.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    medicineTake.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        dose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    doseMissing.setVisibility(View.GONE);
                }
            }
        });

        dose.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <doseLists.size(); i++) {
                if (name.equals(doseLists.get(i).getDose_name())) {
                    dose_id = doseLists.get(i).getDose_id();
                    dose_name = doseLists.get(i).getDose_name();
                }
            }

            selectedFromItems = true;
            doseMissing.setVisibility(View.GONE);
            closeKeyBoard();
        });

        dose.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = dose.getText().toString();
                if (!selectedFromItems) {
                    dose_id = "";
                    for (int i = 0; i < doseLists.size(); i++) {
                        if (ss.equals(doseLists.get(i).getDose_name())) {
                            dose_id = doseLists.get(i).getDose_id();
                            dose_name = doseLists.get(i).getDose_name();
                        }
                    }
                    if (dose_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            doseMissing.setVisibility(View.VISIBLE);
                            String dmt = "Please Select Dose";
                            doseMissing.setText(dmt);
                        }
                        else {
                            doseMissing.setVisibility(View.VISIBLE);
                            String dmt = "Invalid Dose";
                            doseMissing.setText(dmt);
                        }
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        dose.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    dose.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        duration.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    duration.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        close.setOnClickListener(v -> finish());

        modify.setOnClickListener(v -> {
            med_duration = Objects.requireNonNull(duration.getText()).toString();
            if (!medicine_id.isEmpty()) {
                if (!mpm_id.isEmpty()) {
                    if (!dose_id.isEmpty()) {
                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MedicationModify.this);
                        if (type.equals("ADD")) {
                            alertDialogBuilder.setTitle("Add Medicine!")
                                    .setMessage("Do you want to add patient's new Medicine?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        dialog.dismiss();
                                        addMedication();
                                    })
                                    .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
                        }
                        else {
                            alertDialogBuilder.setTitle("Update Medicine!")
                                    .setMessage("Do you want to update this Medicine?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        dialog.dismiss();
                                        updateMedication();
                                    })
                                    .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Please Provide Dose",Toast.LENGTH_SHORT).show();
                        doseMissing.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please Provide Medicine Time",Toast.LENGTH_SHORT).show();
                    medicineTakeMissing.setVisibility(View.VISIBLE);
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Provide Medicine",Toast.LENGTH_SHORT).show();
                medicineMissing.setVisibility(View.VISIBLE);
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

        medicineLists = new ArrayList<>();
        medTakeTimeLists = new ArrayList<>();
        doseLists = new ArrayList<>();

        String medicineUrl = pre_url_api+"prescription/getMedicines?pmm_id="+pmm_id;
        String medicineTimeUrl = pre_url_api+"prescription/getMedTakeTime";
        String doseUrl = pre_url_api+"prescription/getDoses";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest doseReq = new StringRequest(Request.Method.GET, doseUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String dose_id = info.getString("dose_id")
                                .equals("null") ? "" : info.getString("dose_id");
                        String dose_name = info.getString("dose_name")
                                .equals("null") ? "" : info.getString("dose_name");

                        doseLists.add(new DoseList(dose_id,dose_name));
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

        StringRequest medicineTimeReq = new StringRequest(Request.Method.GET, medicineTimeUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String mpm_id = info.getString("mpm_id")
                                .equals("null") ? "" : info.getString("mpm_id");
                        String mpm_name = info.getString("mpm_name")
                                .equals("null") ? "" : info.getString("mpm_name");

                        medTakeTimeLists.add(new MedTakeTimeList(mpm_id,mpm_name,""));
                    }
                }

                requestQueue.add(doseReq);
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

        StringRequest medicineReq = new StringRequest(Request.Method.GET, medicineUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (type.equals("UPDATE")) {
                    medicineLists.add(new MedicineList(medicine_id,medicine_name));
                }
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String medicine_id = info.getString("medicine_id")
                                .equals("null") ? "" : info.getString("medicine_id");
                        String medicine_name = info.getString("medicine_name")
                                .equals("null") ? "" : info.getString("medicine_name");

                        medicineLists.add(new MedicineList(medicine_id,medicine_name));
                    }
                }

                requestQueue.add(medicineTimeReq);
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

        requestQueue.add(medicineReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (medicineLists.isEmpty()) {
                    String mmt = "No Medicine Found";
                    medicineMissing.setText(mmt);
                    medicineMissing.setVisibility(View.VISIBLE);
                }
                else {
                    medicineMissing.setVisibility(View.GONE);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < medicineLists.size(); i++) {
                    type.add(medicineLists.get(i).getMedicine_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MedicationModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                medicine.setAdapter(arrayAdapter);

                if (medTakeTimeLists.isEmpty()) {
                    String mtmt = "No Medicine Time Found";
                    medicineTakeMissing.setText(mtmt);
                    medicineTakeMissing.setVisibility(View.VISIBLE);
                }
                else {
                    medicineTakeMissing.setVisibility(View.GONE);
                }

                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < medTakeTimeLists.size(); i++) {
                    type1.add(medTakeTimeLists.get(i).getMpm_name());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(MedicationModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                medicineTake.setAdapter(arrayAdapter1);

                if (doseLists.isEmpty()) {
                    String dmt = "No Dose Found";
                    doseMissing.setText(dmt);
                    doseMissing.setVisibility(View.VISIBLE);
                }
                else {
                    doseMissing.setVisibility(View.GONE);
                }

                ArrayList<String> type2 = new ArrayList<>();
                for(int i = 0; i < doseLists.size(); i++) {
                    type2.add(doseLists.get(i).getDose_name());
                }

                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(MedicationModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type2);
                dose.setAdapter(arrayAdapter2);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MedicationModify.this);
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

    public void addMedication() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String insertMed = pre_url_api+"prescription/insertMedication";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, insertMed, response -> {
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
                headers.put("P_MPM_ID",mpm_id);
                headers.put("P_DURATION",med_duration);
                headers.put("P_DOSE_ID",dose_id);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MedicationModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    addMedication();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void updateMedication() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateMed = pre_url_api+"prescription/updateMedication";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, updateMed, response -> {
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
                headers.put("P_MEDICINE_ID",medicine_id);
                headers.put("P_MPM_ID",mpm_id);
                headers.put("P_DURATION",med_duration);
                headers.put("P_DOSE_ID",dose_id);
                headers.put("P_PMP_ID",pmp_id);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MedicationModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateMedication();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}