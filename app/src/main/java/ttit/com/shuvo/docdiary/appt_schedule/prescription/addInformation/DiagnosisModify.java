package ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation;

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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.PrescriptionSetup;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.DiagnosisList;

public class DiagnosisModify extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView close;
    TextView appbarName;

    TextInputLayout diagLay;
    AppCompatAutoCompleteTextView diag;
    TextView diagMissing;
    ArrayList<DiagnosisList> diagnosisLists;

    Button modify, delete;


    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";

    String type = "";
    String pmm_id = "";
    String dm_name = "";
    String dm_id = "";
    boolean selectedFromItems = false;

    String pdi_id = "";
    int position = 0;
    Logger logger = Logger.getLogger(DiagnosisModify.class.getName());
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
        setContentView(R.layout.activity_diagnosis_modify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.diagnosis_mod_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.pat_diagnosis_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_diagnosis_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_pat_diagnosis_modify);
        appbarName = findViewById(R.id.pat_diagnosis_modify_app_bar_text);

        diagLay = findViewById(R.id.spinner_layout_diagnosis_for_patient);
        diag = findViewById(R.id.diagnosis_for_patient_spinner);
        diagMissing = findViewById(R.id.diagnosis_for_patient_missing_msg);
        diagMissing.setVisibility(View.GONE);

        modify = findViewById(R.id.pat_diagnosis_modify_button);
        delete = findViewById(R.id.pat_diagnosis_delete_button);

        diagnosisLists = new ArrayList<>();

        Intent intent = getIntent();
        pmm_id = intent.getStringExtra("P_PMM_ID");
        type = intent.getStringExtra("DIAG_MODIFY_TYPE");

        assert type != null;
        if (type.equals("ADD")) {
            String an = "Add Diagnosis";
            appbarName.setText(an);
            modify.setText(type);
            delete.setVisibility(View.GONE);
        }
        else {
            String an = "Update Diagnosis";
            appbarName.setText(an);
            modify.setText(type);
            delete.setVisibility(View.VISIBLE);

            dm_id = intent.getStringExtra("P_DM_ID" );
            pdi_id = intent.getStringExtra("P_PDI_ID");
            dm_name = intent.getStringExtra("P_DM_NAME");
            position = intent.getIntExtra("POSITION",0);

            diag.setText(dm_name);
        }

        diag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    diagMissing.setVisibility(View.GONE);
                }
            }
        });

        diag.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <diagnosisLists.size(); i++) {
                if (name.equals(diagnosisLists.get(i).getDm_name())) {
                    dm_id = diagnosisLists.get(i).getDm_id();
                    dm_name = diagnosisLists.get(i).getDm_name();
                }
            }

            selectedFromItems = true;
            diagMissing.setVisibility(View.GONE);
            System.out.println("dm_name: " + dm_name);
            System.out.println("dm_id: "+ dm_id);
            closeKeyBoard();
        });

        diag.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = diag.getText().toString();
                if (!selectedFromItems) {
                    dm_id = "";
                    for (int i = 0; i < diagnosisLists.size(); i++) {
                        if (ss.equals(diagnosisLists.get(i).getDm_name())) {
                            dm_id = diagnosisLists.get(i).getDm_id();
                            dm_name = diagnosisLists.get(i).getDm_name();

                        }
                    }
                    if (dm_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            diagMissing.setVisibility(View.VISIBLE);
                            String dmt = "Please Select Diagnosis";
                            diagMissing.setText(dmt);
                        }
                        else {
                            diagMissing.setVisibility(View.VISIBLE);
                            String dmt = "Invalid Diagnosis";
                            diagMissing.setText(dmt);
                        }
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        diag.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    diag.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        close.setOnClickListener(v -> finish());

        modify.setOnClickListener(v -> {
            if (!dm_id.isEmpty()) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DiagnosisModify.this);
                if (type.equals("ADD")) {
                    alertDialogBuilder.setTitle("Add Diagnosis!")
                            .setMessage("Do you want to add patient's new Diagnosis?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                addDiagnosis();
                            })
                            .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
                else {
                    alertDialogBuilder.setTitle("Update Diagnosis!")
                            .setMessage("Do you want to update this Diagnosis?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                updateDiagns();
                            })
                            .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Provide Diagnosis",Toast.LENGTH_SHORT).show();
                diagMissing.setVisibility(View.VISIBLE);
            }
        });

        delete.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DiagnosisModify.this);
            builder.setMessage("Do you want to delete this Diagnosis?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> deleteDiagnosis())
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        PrescriptionSetup.previousDataSelected = true;

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

        diagnosisLists = new ArrayList<>();

        String diagUrl = pre_url_api+"prescription/getDiagnosisList?pmm_id="+pmm_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest diagReq = new StringRequest(Request.Method.GET, diagUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (type.equals("UPDATE")) {
                    diagnosisLists.add(new DiagnosisList(dm_name,dm_id,""));
                }
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String dm_name = info.getString("dm_name")
                                .equals("null") ? "" : info.getString("dm_name");
                        String dm_id = info.getString("dm_id")
                                .equals("null") ? "" : info.getString("dm_id");
                        String dm_notes = info.getString("dm_notes")
                                .equals("null") ? "" : info.getString("dm_notes");


                        diagnosisLists.add(new DiagnosisList(dm_name,dm_id,dm_notes));
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

        requestQueue.add(diagReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (diagnosisLists.isEmpty()) {
                    String dmt = "No Diagnosis List Found";
                    diagMissing.setText(dmt);
                    diagMissing.setVisibility(View.VISIBLE);
                }
                else {
                    diagMissing.setVisibility(View.GONE);
                }
                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < diagnosisLists.size(); i++) {
                    type.add(diagnosisLists.get(i).getDm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DiagnosisModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                diag.setAdapter(arrayAdapter);


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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DiagnosisModify.this);
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

    public void addDiagnosis() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String insertDiag = pre_url_api+"prescription/insertDiagnosis";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, insertDiag, response -> {
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
                headers.put("P_DM_ID",dm_id);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DiagnosisModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    addDiagnosis();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void updateDiagns() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateSLVUrl = pre_url_api+"prescription/updateDiagnosis";
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
                headers.put("P_DM_ID",dm_id);
                headers.put("P_PDI_ID",pdi_id);
                headers.put("P_PMM_ID",pmm_id);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DiagnosisModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateDiagns();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void deleteDiagnosis() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateSLVUrl = pre_url_api+"prescription/deleteDiagnosis";
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
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAfterDelete();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAfterDelete();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PDI_ID",pdi_id);
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
                if (position == PrescriptionSetup.selectedPosition) {
                    if (position > 0) {
                        PrescriptionSetup.selectedPosition--;
                    }
                }
                else {
                    if (position < PrescriptionSetup.selectedPosition) {
                        PrescriptionSetup.selectedPosition--;
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DiagnosisModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    deleteDiagnosis();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}