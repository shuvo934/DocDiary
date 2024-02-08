package ttit.com.shuvo.docdiary.patient_search;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.patient_search.adapters.PatientSearchAdapter;
import ttit.com.shuvo.docdiary.patient_search.arraylists.PatientSearchList;

public class PatientSearch extends AppCompatActivity {

    LinearLayout fullLayout;
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
    ArrayList<PatientSearchList> filteredList;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_search);

        fullLayout = findViewById(R.id.patient_search_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_patient_search);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_patient_search);

        selectYear = findViewById(R.id.select_year_for_patient_search);

        searchPatientLay = findViewById(R.id.search_patient_lay_search_box);
        searchPatient = findViewById(R.id.search_patient_search_box);

        patientView = findViewById(R.id.patient_list_recyclerview);
        noPatientFound = findViewById(R.id.no_patient_prescription_found_message);
        noPatientFound.setVisibility(View.GONE);

        patientView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        patientView.setLayoutManager(layoutManager);

        patientSearchLists = new ArrayList<>();
        filteredList = new ArrayList<>();

        backButton.setOnClickListener(v -> onBackPressed());

        Calendar today = Calendar.getInstance();
        today.get(Calendar.YEAR);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ENGLISH);

        selected_year = df.format(c);

        int year = Integer.parseInt(selected_year);
        String yy = "Year: "+ selected_year;
        selectYear.setText(yy);

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(PatientSearch.this, (selectedMonth, selectedYear) -> {
            System.out.println("Selected Year: "+selectedYear);
            String yy1 = "Year: "+selectedYear;
            selectYear.setText(yy1);

            selected_year = String.valueOf(selectedYear);

            searchPatient.setText("");
            searching_data = "";

            getData();

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
                filter(s.toString());
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

        getData();
    }

    private void filter(String text) {
        filteredList = new ArrayList<>();
        for (PatientSearchList item : patientSearchLists) {
            if (item.getPat_name().toLowerCase().contains(text.toLowerCase()) || item.getSub_code().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add((item));
            }
        }

        if (filteredList.size() == 0) {
            noPatientFound.setVisibility(View.VISIBLE);
        }
        else {
            noPatientFound.setVisibility(View.GONE);
        }
        patientSearchAdapter.filterList(filteredList);
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

    @Override
    public void onBackPressed() {
        if (loading) {
            Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
        }
        else {
            super.onBackPressed();
        }
    }

    public void getData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        patientSearchLists = new ArrayList<>();

        String url = pre_url_api+"patient_search/getPatientList?p_year="+selected_year+"";

        RequestQueue requestQueue = Volley.newRequestQueue(PatientSearch.this);

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

                    patientSearchLists.add(new PatientSearchList(pat_id,pat_name,dd_thana_name,ph_id,sub_code,pph_progress));
                }

                connected = true;
                updateInterface();
            }
            catch (JSONException e) {
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

                if (patientSearchLists.size() == 0) {
                    noPatientFound.setVisibility(View.VISIBLE);
                }
                else {
                    noPatientFound.setVisibility(View.GONE);
                }
                patientSearchAdapter = new PatientSearchAdapter(patientSearchLists, PatientSearch.this);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PatientSearch.this);
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
}