package ttit.com.shuvo.docdiary.patient_search;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.adminInfoLists;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.patient_search.adapters.PatientSearchAdapter;
import ttit.com.shuvo.docdiary.patient_search.arraylists.PatientSearchList;

public class PatientSearchAdmin extends AppCompatActivity {

    RelativeLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    TextView selectYear;
    String selected_year = "";
    AlertDialog yearDialog;

    TextInputEditText searchPatient;
    TextInputLayout searchPatientLay;
    String searching_data = "";

    RecyclerView patientView;
    RecyclerView.LayoutManager layoutManager;
    PatientSearchAdapter patientSearchAdapter;
    TextView noPatientFound;

    ArrayList<PatientSearchList> patientSearchLists;
    ArrayList<PatientSearchList> hundredPatLists;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    MaterialButton addPatient;
    MaterialButton searchButton;
    TextView searchPatientError;
    TextView latestPatMsg;
    MaterialButton clearButton;
    public static boolean needToUpdatePatList = true;

    String perm = "1";

    Logger logger = Logger.getLogger(PatientSearchAdmin.class.getName());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_search_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pat_src_admin_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.patient_search_admin_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_patient_search_admin);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_patient_search_admin);

        selectYear = findViewById(R.id.select_year_for_patient_search_admin);

        searchPatientLay = findViewById(R.id.search_patient_lay_search_box_admin);
        searchPatient = findViewById(R.id.search_patient_search_box_admin);

        patientView = findViewById(R.id.patient_list_recyclerview_admin);
        noPatientFound = findViewById(R.id.no_patient_prescription_found_message_admin);
        noPatientFound.setVisibility(View.GONE);
        searchPatientError = findViewById(R.id.search_patient_error_handling_msg);
        searchPatientError.setVisibility(View.GONE);
        latestPatMsg = findViewById(R.id.latest_hundred_patient_msg_admin);
        latestPatMsg.setVisibility(View.GONE);
        clearButton = findViewById(R.id.clear_list_view_button_pat_search_admin);
        clearButton.setVisibility(View.GONE);

        patientView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        patientView.setLayoutManager(layoutManager);

        addPatient = findViewById(R.id.add_patient_from_patient_search);
        searchButton = findViewById(R.id.search_button_for_patient_search_admin);

        patientSearchLists = new ArrayList<>();
        hundredPatLists = new ArrayList<>();
        needToUpdatePatList = true;

        if (adminInfoLists == null) {
            restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (adminInfoLists.isEmpty()) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            }
            else {
                if (Integer.parseInt(adminInfoLists.get(0).getAll_access_flag()) == 1) {
                    perm = "2";
                }
                else {
                    perm = "1";
                }
            }
        }

        backButton.setOnClickListener(v -> finish());

        Calendar today = Calendar.getInstance();
        today.get(Calendar.YEAR);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ENGLISH);

        selected_year = df.format(c);

        int year = Integer.parseInt(selected_year);
        String yy = "Year: "+ selected_year;
        selectYear.setText(yy);

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(PatientSearchAdmin.this, (selectedMonth, selectedYear) -> {
            System.out.println("Selected Year: "+selectedYear);
            String yy1 = "Year: "+selectedYear;
            selectYear.setText(yy1);

            selected_year = String.valueOf(selectedYear);

        },today.get(Calendar.YEAR),today.get(Calendar.MONTH));

        builder.setActivatedYear(Integer.parseInt(selected_year))
                .setMinYear(1950)
                .setMaxYear(year)
                .showYearOnly()
                .setTitle("Selected Year")
                .setOnYearChangedListener(year1 -> {
                });

        yearDialog = builder.build();

        selectYear.setOnClickListener(v -> yearDialog.show());

        searchPatient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searching_data = s.toString();
                searchPatientError.setVisibility(View.GONE);
                if (searching_data.contains(".") || searching_data.contains(",") || searching_data.contains("-")) {
                    searchPatientError.setVisibility(View.VISIBLE);
                    String text = "Invalid Character";
                    searchPatientError.setText(text);
                }
            }
        });

        searchPatient.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        searchButton.setOnClickListener(v -> {
            searching_data = Objects.requireNonNull(searchPatient.getText()).toString();
            if (!searching_data.isEmpty()) {
                if (searching_data.length() >= 3) {
                    if (searching_data.contains(".") || searching_data.contains(",") || searching_data.contains("-")) {
                        searchPatientError.setVisibility(View.VISIBLE);
                        String text = "Name or Code must not contain '.,-' character";
                        searchPatientError.setText(text);
                        Toast.makeText(this, "Name or Code must not contain '.,-' character", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        closeKeyBoard();
                        searchPatientError.setVisibility(View.GONE);
                        getSearchData();
                    }
                }
                else {
                    searchPatientError.setVisibility(View.VISIBLE);
                    String text = "Name or Code must be greater than 2 character";
                    searchPatientError.setText(text);
                    Toast.makeText(this, "Name or Code must be greater than 2 character", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                searchPatientError.setVisibility(View.VISIBLE);
                String text = "Please Provide Patient Name or Code";
                searchPatientError.setText(text);
                Toast.makeText(this, "Please Provide Patient Name or Code", Toast.LENGTH_SHORT).show();
            }

        });

        clearButton.setOnClickListener(v -> {
            clearButton.setVisibility(View.GONE);
            searching_data = "";
            searchPatientError.setVisibility(View.GONE);
            searchPatient.setText("");

            if (hundredPatLists.isEmpty()) {
                noPatientFound.setVisibility(View.VISIBLE);
                latestPatMsg.setVisibility(View.GONE);
            }
            else {
                noPatientFound.setVisibility(View.GONE);
                latestPatMsg.setVisibility(View.VISIBLE);
            }
            patientSearchAdapter = new PatientSearchAdapter(hundredPatLists, PatientSearchAdmin.this);
            patientView.setAdapter(patientSearchAdapter);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needToUpdatePatList) {
            getData();
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

    public void restart(String msg) {
        try {
            ProcessPhoenix.triggerRebirth(getApplicationContext());
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }

//    @Override
//    public void onBackPressed() {
//
//    }

    public void getData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        hundredPatLists = new ArrayList<>();

        String url = pre_url_api+"patient_search/getHundredPatList";

        RequestQueue requestQueue = Volley.newRequestQueue(PatientSearchAdmin.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String pat_list = jsonObject.getString("pat_list");
                JSONArray array = new JSONArray(pat_list);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject docInfo = array.getJSONObject(i);

                    String pat_id = docInfo.getString("pat_id")
                            .equals("null") ? "" : docInfo.getString("pat_id");
                    String pat_name = docInfo.getString("pat_name")
                            .equals("null") ? "" : docInfo.getString("pat_name");
                    String dd_thana_name = docInfo.getString("dd_thana_name")
                            .equals("null") ? "" : docInfo.getString("dd_thana_name");
                    String ph_id = docInfo.getString("ph_id")
                            .equals("null") ? "" : docInfo.getString("ph_id");
                    String sub_code = docInfo.getString("sub_code")
                            .equals("null") ? "" : docInfo.getString("sub_code");
                    String pph_progress = docInfo.getString("pph_progress")
                            .equals("null") ? "0" : docInfo.getString("pph_progress");
                    String pat_cell = docInfo.getString("pat_cell")
                            .equals("null") ? "" :docInfo.getString("pat_cell");
                    String pyear = docInfo.getString("pyear")
                            .equals("null") ? "" : docInfo.getString("pyear");

                    hundredPatLists.add(new PatientSearchList(pat_id,pat_name,dd_thana_name,ph_id,
                            sub_code,pph_progress,pyear,pat_cell,perm));
                }

                connected = true;
                updateLayout();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateLayout();
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);

    }

    private void updateLayout() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                clearButton.setVisibility(View.GONE);
                searching_data = "";
                searchPatientError.setVisibility(View.GONE);
                searchPatient.setText("");

                if (hundredPatLists.isEmpty()) {
                    noPatientFound.setVisibility(View.VISIBLE);
                    latestPatMsg.setVisibility(View.GONE);
                }
                else {
                    noPatientFound.setVisibility(View.GONE);
                    latestPatMsg.setVisibility(View.VISIBLE);
                }
                patientSearchAdapter = new PatientSearchAdapter(hundredPatLists, PatientSearchAdmin.this);
                patientView.setAdapter(patientSearchAdapter);
                loading = false;

            }
            else {
                alertMessage1();
            }
        }
        else {
            alertMessage1();
        }
    }

    public void alertMessage1() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PatientSearchAdmin.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    loading = false;
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

    public void getSearchData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        patientSearchLists = new ArrayList<>();

        String url = pre_url_api+"patient_search/getPatienListBySearch?p_year="+selected_year+"&p_name="+searching_data;

        RequestQueue requestQueue = Volley.newRequestQueue(PatientSearchAdmin.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String pat_list = jsonObject.getString("pat_list");
                JSONArray array = new JSONArray(pat_list);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject docInfo = array.getJSONObject(i);

                    String pat_id = docInfo.getString("pat_id")
                            .equals("null") ? "" : docInfo.getString("pat_id");
                    String pat_name = docInfo.getString("pat_name")
                            .equals("null") ? "" : docInfo.getString("pat_name");
                    String dd_thana_name = docInfo.getString("dd_thana_name")
                            .equals("null") ? "" : docInfo.getString("dd_thana_name");
                    String ph_id = docInfo.getString("ph_id")
                            .equals("null") ? "" : docInfo.getString("ph_id");
                    String sub_code = docInfo.getString("sub_code")
                            .equals("null") ? "" : docInfo.getString("sub_code");
                    String pph_progress = docInfo.getString("pph_progress")
                            .equals("null") ? "0" : docInfo.getString("pph_progress");
                    String pat_cell = docInfo.getString("pat_cell")
                            .equals("null") ? "" :docInfo.getString("pat_cell");

                    patientSearchLists.add(new PatientSearchList(pat_id,pat_name,dd_thana_name,ph_id,
                            sub_code,pph_progress,"",pat_cell,perm));
                }

                connected = true;
                updateInterface();
            }
            catch (JSONException e) {
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);

    }

    private void updateInterface() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                latestPatMsg.setVisibility(View.GONE);
                clearButton.setVisibility(View.VISIBLE);

                if (patientSearchLists.isEmpty()) {
                    noPatientFound.setVisibility(View.VISIBLE);
                }
                else {
                    noPatientFound.setVisibility(View.GONE);
                }
                patientSearchAdapter = new PatientSearchAdapter(patientSearchLists, PatientSearchAdmin.this);
                patientView.setAdapter(patientSearchAdapter);
                loading = false;

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PatientSearchAdmin.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getSearchData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    loading = false;
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
}