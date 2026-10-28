package ttit.com.shuvo.docdiary.patient_search;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.preview.PrescriptionPreview;
import ttit.com.shuvo.docdiary.patient_search.adapters.PatientSearchDypAdapter;
import ttit.com.shuvo.docdiary.patient_search.arraylists.PatientSearchDypList;
import ttit.com.shuvo.docdiary.patient_search.bottomsheet.PatientPrescriptionSearchBottomSheet;
import ttit.com.shuvo.docdiary.utilities.EdgeToEdgeHelper;

public class PatientSearchDyPrescription extends AppCompatActivity implements PatientSearchDypAdapter.PatientSelection {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    TextView selectYear;
    String selected_year = "";
    AlertDialog yearDialog;

    TextInputEditText searchPatient;
    TextInputLayout searchPatientLay;
    String searching_data = "";

    MaterialButton searchButton;

    TextView helperText;

    RecyclerView patientView;
    RecyclerView.LayoutManager layoutManager;
    PatientSearchDypAdapter patientSearchDypAdapter;
    TextView noPatientFound;

    ArrayList<PatientSearchDypList> patientSearchDypLists;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    String doc_id = "";
    boolean all_presc = false;

    Logger logger = Logger.getLogger(PatientSearchDyPrescription.class.getName());

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdgeHelper.enable(this);
        setContentView(R.layout.activity_patient_search_dy_prescription);
        EdgeToEdgeHelper.applyInsets(
                this,
                findViewById(R.id.pat_src_dy_pres_root),
                false,
                false);

        fullLayout = findViewById(R.id.patient_search_dyp_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_patient_search_dyp);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_patient_search_dyp);

        selectYear = findViewById(R.id.select_year_for_patient_search_dyp);

        searchPatientLay = findViewById(R.id.search_patient_lay_search_box_dyp);
        searchPatient = findViewById(R.id.search_patient_search_box_dyp);
        searchButton = findViewById(R.id.search_button_pat_list_dyp);

        helperText = findViewById(R.id.patient_list_helper_dyp);

        patientView = findViewById(R.id.patient_list_recyclerview_dyp);
        noPatientFound = findViewById(R.id.no_patient_prescription_found_message_dyp);
        noPatientFound.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        patientView.setLayoutManager(layoutManager);

        patientSearchDypLists = new ArrayList<>();

        if (userInfoLists == null) {
            restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (userInfoLists.isEmpty()) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            } else {
                all_presc = Integer.parseInt(userInfoLists.get(0).getAll_presc()) == 1;
                doc_id = userInfoLists.get(0).getDoc_id();
            }
        }

        backButton.setOnClickListener(v -> finish());

        Calendar today = Calendar.getInstance();
        today.get(Calendar.YEAR);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ENGLISH);

        selected_year = df.format(c);

        int year = Integer.parseInt(selected_year);
        String yy = "Search Year: "+ selected_year;
        selectYear.setText(yy);

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(PatientSearchDyPrescription.this, (selectedMonth, selectedYear) -> {
            System.out.println("Selected Year: "+selectedYear);
            String yy1 = "Search Year: "+selectedYear;
            selectYear.setText(yy1);

            selected_year = String.valueOf(selectedYear);

            searchPatient.setText("");
            searching_data = "";

            getYearWiseData();

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
            }
        });

        searchPatient.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_NEXT
                    || event != null
                    && event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
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
            searching_data = searchPatient.getText() == null ? "" : searchPatient.getText().toString().trim();
            if (searching_data.isEmpty()) {
                helperText.setText("Please write patient name, code or mobile before search");
                helperText.setTextColor(getColor(R.color.red_dark));
            }
            else {
                if (searching_data.length() < 3) {
                    helperText.setText("Please write at least 3 characters before search");
                    helperText.setTextColor(getColor(R.color.red_dark));
                }
                else {
                    closeKeyBoard();
                    getSearchData();
                }
            }
        });

        getSupportFragmentManager().setFragmentResultListener(PatientPrescriptionSearchBottomSheet.RESULT_REQUEST_KEY, this, (requestKey, result) -> handlePrescriptionHistoryResult(result));

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

        getYearWiseData();
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

    public void getYearWiseData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        patientSearchDypLists = new ArrayList<>();

        String url;
        if (all_presc) {
            url = pre_url_api+"patient_search/getDyPresPatientList?p_year="+selected_year;
        }
        else {
            url = pre_url_api+"patient_search/getDyPresPatientList?p_year="+selected_year+"&p_doc_id="+doc_id;
        }


        RequestQueue requestQueue = Volley.newRequestQueue(PatientSearchDyPrescription.this);

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
                    String ph_id = docInfo.getString("ph_id")
                            .equals("null") ? "" : docInfo.getString("ph_id");
                    String sub_code = docInfo.getString("sub_code")
                            .equals("null") ? "" : docInfo.getString("sub_code");
                    String dd_thana_name = docInfo.getString("dd_thana_name")
                            .equals("null") ? "" : docInfo.getString("dd_thana_name");
                    String pph_progress = docInfo.getString("pph_progress")
                            .equals("null") ? "0" : docInfo.getString("pph_progress");
                    String pyear = docInfo.getString("pyear")
                            .equals("null") ? "" : docInfo.getString("pyear");
                    String pat_cell = docInfo.getString("pat_cell")
                            .equals("null") ? "" : docInfo.getString("pat_cell");
                    String gender = docInfo.getString("gender")
                            .equals("null") ? "" : docInfo.getString("gender");
                    String pat_age = docInfo.getString("pat_age")
                            .equals("null") ? "" : docInfo.getString("pat_age");

                    patientSearchDypLists.add(new PatientSearchDypList(pat_id,pat_name,ph_id,sub_code,dd_thana_name,
                            pph_progress,pyear,pat_cell,gender,pat_age));
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

    @SuppressLint("SetTextI18n")
    private void updateInterface() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (patientSearchDypLists.isEmpty()) {
                    noPatientFound.setVisibility(View.VISIBLE);
                }
                else {
                    noPatientFound.setVisibility(View.GONE);
                }

                helperText.setText("Showing the latest 100 patients for the selected year");
                patientSearchDypAdapter = new PatientSearchDypAdapter(patientSearchDypLists, PatientSearchDyPrescription.this, PatientSearchDyPrescription.this);
                patientView.setAdapter(patientSearchDypAdapter);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PatientSearchDyPrescription.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getYearWiseData();
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

        patientSearchDypLists = new ArrayList<>();

        Uri.Builder uriBuilder = Uri
                .parse(pre_url_api + "patient_search/getDyPresPatientListBySearch")
                .buildUpon()
                .appendQueryParameter("p_search", searching_data.trim());

        if (!all_presc) {
            uriBuilder.appendQueryParameter("p_doc_id", doc_id);
        }

        String url = uriBuilder.build().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(PatientSearchDyPrescription.this);

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
                    String ph_id = docInfo.getString("ph_id")
                            .equals("null") ? "" : docInfo.getString("ph_id");
                    String sub_code = docInfo.getString("sub_code")
                            .equals("null") ? "" : docInfo.getString("sub_code");
                    String dd_thana_name = docInfo.getString("dd_thana_name")
                            .equals("null") ? "" : docInfo.getString("dd_thana_name");
                    String pph_progress = docInfo.getString("pph_progress")
                            .equals("null") ? "0" : docInfo.getString("pph_progress");
                    String pyear = docInfo.getString("pyear")
                            .equals("null") ? "" : docInfo.getString("pyear");
                    String pat_cell = docInfo.getString("pat_cell")
                            .equals("null") ? "" : docInfo.getString("pat_cell");
                    String gender = docInfo.getString("gender")
                            .equals("null") ? "" : docInfo.getString("gender");
                    String pat_age = docInfo.getString("pat_age")
                            .equals("null") ? "" : docInfo.getString("pat_age");

                    patientSearchDypLists.add(new PatientSearchDypList(pat_id,pat_name,ph_id,sub_code,dd_thana_name,
                            pph_progress,pyear,pat_cell,gender,pat_age));
                }

                connected = true;
                updateSearchInterface();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateSearchInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateSearchInterface();
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);

    }

    @SuppressLint("SetTextI18n")
    private void updateSearchInterface() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (patientSearchDypLists.isEmpty()) {
                    noPatientFound.setVisibility(View.VISIBLE);
                }
                else {
                    noPatientFound.setVisibility(View.GONE);
                }

                helperText.setText("Showing all searched registered patients");
                helperText.setTextColor(getColor(R.color.default_text_color));
                helperText.setTextColor(getColor(R.color.default_text_color));
                patientSearchDypAdapter = new PatientSearchDypAdapter(patientSearchDypLists, PatientSearchDyPrescription.this, PatientSearchDyPrescription.this);
                patientView.setAdapter(patientSearchDypAdapter);
                loading = false;

            }
            else {
                alertMessageSearch();
            }
        }
        else {
            alertMessageSearch();
        }
    }

    public void alertMessageSearch() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PatientSearchDyPrescription.this);
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

    @Override
    public void onSelect(int position) {
        PatientSearchDypList dypList = patientSearchDypLists.get(position);
        if (isEmpty(dypList.getPat_id()) || isEmpty(dypList.getPh_id())) {
            showPrescriptionError("Select a patient before viewing prescription history.");
            return;
        }

        if (getSupportFragmentManager().findFragmentByTag(PatientPrescriptionSearchBottomSheet.TAG) != null) {
            return;
        }

        PatientPrescriptionSearchBottomSheet.newInstance(dypList.getPh_id(), dypList.getPat_id(), !all_presc ? doc_id : "").show(getSupportFragmentManager(), PatientPrescriptionSearchBottomSheet.TAG);
    }

    private void handlePrescriptionHistoryResult(@NonNull Bundle result) {
        boolean previewRequested = result.getBoolean(PatientPrescriptionSearchBottomSheet.KEY_PREVIEW_REQUESTED, false);
        String selectedRvmId = cleanIntentValue(result.getString(PatientPrescriptionSearchBottomSheet.KEY_SELECTED_RVM_ID));

        if (previewRequested) {
            if (isEmpty(selectedRvmId)) {
                showPrescriptionError("Prescription information not found.");
                return;
            }

            Intent previewIntent = new Intent(PatientSearchDyPrescription.this, PrescriptionPreview.class);
            previewIntent.putExtra(PrescriptionPreview.EXTRA_RVM_ID, selectedRvmId);
            startActivity(previewIntent);
        }
    }

    private boolean isEmpty(@Nullable String value) {
        return value == null || value.trim().isEmpty();
    }

    @NonNull
    private String cleanIntentValue(@Nullable String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return "";
        }

        return value.trim();
    }

    private void showPrescriptionError(@NonNull String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Error!")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}