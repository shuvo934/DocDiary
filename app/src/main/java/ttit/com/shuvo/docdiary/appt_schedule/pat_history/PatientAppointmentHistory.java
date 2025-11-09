package ttit.com.shuvo.docdiary.appt_schedule.pat_history;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.ChoiceDoctorList;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.ChoiceServiceList;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.ChoiceUnitList;
import ttit.com.shuvo.docdiary.appt_schedule.pat_history.adapters.AppointHistoryAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.pat_history.arraylists.PatAppHistList;

public class PatientAppointmentHistory extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView close;

    TextView patName, patCode;

    TextInputEditText dateRange;
    String firstDate = "";
    String lastDate = "";
    private Long selectedStartDate = null;
    private Long selectedEndDate = null;

    LinearLayout searchLay;

    AppCompatAutoCompleteTextView serviceSearch;
    AppCompatAutoCompleteTextView unitSearch;
    AppCompatAutoCompleteTextView doctorSearch;

    ArrayList<ChoiceServiceList> choiceServiceLists;
    ArrayList<ChoiceUnitList> choiceUnitLists;
    ArrayList<ChoiceDoctorList> choiceDoctorLists;

    String search_pfn_id = "";
    String search_depts_id = "";
    String search_doc_id = "";

    MaterialButton filterSearch;
    private int shortAnimationDuration;

    RecyclerView patAppointHistView;
    AppointHistoryAdapter appointHistoryAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<PatAppHistList> patAppHistLists;
    ArrayList<PatAppHistList> filteredList;
    TextView noHistFound;

    String pat_name = "";
    String pat_code = "";
    String ph_id = "";

    private boolean conn = false;
    private boolean connected = false;
    private boolean loading = false;
    String parsing_message = "";

    boolean isUpcomingActive = false;

    Logger logger = Logger.getLogger(PatientAppointmentHistory.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointment_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pat_app_history_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.pat_app_history_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_appoint_history);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.back_logo_of_pat_app_history);

        patName = findViewById(R.id.pat_app_hist_patient_name);
        patCode = findViewById(R.id.pat_app_hist_patient_ph_code);

        dateRange = findViewById(R.id.date_range_pat_app_hist);

        searchLay = findViewById(R.id.search_layout_pat_app_hist);
        searchLay.setVisibility(View.GONE);

        serviceSearch = findViewById(R.id.service_search_for_pat_app_hist);
        unitSearch = findViewById(R.id.unit_search_for_pat_app_hist);
        doctorSearch = findViewById(R.id.doc_search_for_pat_app_hist);
        choiceServiceLists = new ArrayList<>();
        choiceUnitLists = new ArrayList<>();
        choiceDoctorLists = new ArrayList<>();

        filterSearch = findViewById(R.id.search_filter_button);

        patAppointHistView = findViewById(R.id.pat_appoint_history_recyclerview);
        noHistFound = findViewById(R.id.no_information_found_msg_pat_app_hist);
        noHistFound.setVisibility(View.GONE);

        patAppHistLists = new ArrayList<>();
        filteredList = new ArrayList<>();
        patAppointHistView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        patAppointHistView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        pat_name = intent.getStringExtra("P_NAME");
        pat_code = intent.getStringExtra("P_CODE");
        ph_id = intent.getStringExtra("P_PH_ID");

        patName.setText(pat_name);
        patCode.setText(pat_code);

        if (userInfoLists == null) {
            restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (userInfoLists.isEmpty()) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            } else {
                isUpcomingActive = Integer.parseInt(userInfoLists.get(0).getUpcoming_pat_history()) == 1;
            }
        }

        // Getting date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);

        firstDate = simpleDateFormat.format(c);
        firstDate = "01-"+firstDate;
        lastDate = df.format(c);

        String date_range = firstDate.toUpperCase(Locale.ENGLISH) + "  ---  " + lastDate.toUpperCase(Locale.ENGLISH);
        dateRange.setText(date_range);

        dateRange.setOnClickListener(v -> {
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            if (!isUpcomingActive) {
                constraintsBuilder.setValidator(DateValidatorPointBackward.now());
            }

            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Begin & End Date")
                    .setCalendarConstraints(constraintsBuilder.build());

            // Set previously selected dates if available
            if (!firstDate.isEmpty() && !lastDate.isEmpty()) {
                SimpleDateFormat da = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                da.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date s_date = null;
                Date e_date = null;
                try {
                    s_date = da.parse(firstDate);
                    e_date = da.parse(lastDate);
                } catch (ParseException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
                if (s_date != null && e_date != null) {
                    System.out.println(s_date);
                    System.out.println(e_date);
                    selectedStartDate = s_date.getTime(); //+ 21600000;
                    selectedEndDate = e_date.getTime(); // + 21600000;
                }
                if (selectedStartDate != null && selectedEndDate != null) {
                    builder.setSelection(new Pair<>(selectedStartDate, selectedEndDate));
                }
            }

            MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

            // Show Date Picker
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            // Handle Date Selection
            datePicker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null && selection.first != null && selection.second != null) {
                    selectedStartDate = selection.first;  // Save selected start date
                    selectedEndDate = selection.second;
                    System.out.println(selectedStartDate);
                    System.out.println(selectedEndDate);// Save selected end date

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    firstDate = dateFormat.format(selection.first).toUpperCase(Locale.ENGLISH);
                    lastDate = dateFormat.format(selection.second).toUpperCase(Locale.ENGLISH);

                    String d_range;
                    if (firstDate.equals(lastDate)) {
                        d_range = firstDate;
                    }
                    else {
                        d_range = firstDate + "  ---  " + lastDate;
                    }
                    dateRange.setText(d_range);
                    getPatAppHistory();
                }
            });
        });

        serviceSearch.setOnItemClickListener((parent, view, position, id) -> {
            search_pfn_id = "";
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <choiceServiceLists.size(); i++) {
                if (name.equals(choiceServiceLists.get(i).getPfn_name())) {
                    if (name.equals("...")) {
                        serviceSearch.setText("");
                    }
                    search_pfn_id = choiceServiceLists.get(i).getPph_pfn_id();
                }
            }

            filterService(search_pfn_id);

        });

        unitSearch.setOnItemClickListener((parent, view, position, id) -> {
            search_depts_id = "";
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <choiceUnitLists.size(); i++) {
                if (name.equals(choiceUnitLists.get(i).getDetps_name())) {
                    if (name.equals("...")) {
                        unitSearch.setText("");
                    }
                    search_depts_id = choiceUnitLists.get(i).getPph_depts_id();
                }
            }

            filterUnit(search_depts_id);

        });

        doctorSearch.setOnItemClickListener((parent, view, position, id) -> {
            search_doc_id = "";
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <choiceDoctorLists.size(); i++) {
                if (name.equals(choiceDoctorLists.get(i).getDoc_name())) {
                    if (name.equals("...")) {
                        doctorSearch.setText("");
                    }
                    search_doc_id = choiceDoctorLists.get(i).getPph_doc_id();
                }
            }

            filterDoctor(search_doc_id);
        });

        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        filterSearch.setOnClickListener(v -> {
            if (searchLay.getVisibility() == View.GONE) {
                searchLay.setVisibility(View.VISIBLE);
                searchLay.setAlpha(0.0f);
                // Start the animation
                searchLay.animate()
                        .alpha(1.0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(null);
            }
            else {
                searchLay.animate()
                        .alpha(0.0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                searchLay.setVisibility(View.GONE);
                            }
                        });
            }
        });

        close.setOnClickListener(v -> {
            if (loading) {
                Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
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

        getPatAppHistory();
    }

    private void filterService(String text) {

        filteredList = new ArrayList<>();
        for (PatAppHistList item : patAppHistLists) {
            if (search_doc_id.isEmpty()){
                if (search_depts_id.isEmpty()) {
                    if (text.isEmpty()) {
                        filteredList.add((item));
                    }
                    else {
                        if (item.getPfn_id().toLowerCase().contains(text.toLowerCase())) {
                            filteredList.add((item));
                        }
                    }
                } else {
                    if (item.getDepts_id().toLowerCase().contains(search_depts_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getPfn_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                }
            } else {
                if (search_depts_id.isEmpty()) {
                    if (item.getDoc_id().toLowerCase().contains(search_doc_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getPfn_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                } else {
                    if (item.getDoc_id().toLowerCase().contains(search_doc_id.toLowerCase())) {
                        if (item.getDepts_id().toLowerCase().contains(search_depts_id.toLowerCase())) {
                            if (text.isEmpty()) {
                                filteredList.add((item));
                            }
                            else {
                                if (item.getPfn_id().toLowerCase().contains(text.toLowerCase())) {
                                    filteredList.add((item));
                                }
                            }
                        }
                    }
                }
            }
        }


        if (filteredList.isEmpty()) {
            noHistFound.setVisibility(View.VISIBLE);
        }
        else {
            noHistFound.setVisibility(View.GONE);
        }
        appointHistoryAdapter.filterList(filteredList);
    }

    private void filterUnit(String text) {

        filteredList = new ArrayList<>();
        for (PatAppHistList item : patAppHistLists) {
            if (search_doc_id.isEmpty()){
                if (search_pfn_id.isEmpty()) {
                    if (text.isEmpty()) {
                        filteredList.add((item));
                    }
                    else {
                        if (item.getDepts_id().toLowerCase().contains(text.toLowerCase())) {
                            filteredList.add((item));
                        }
                    }
                } else {
                    if (item.getPfn_id().toLowerCase().contains(search_pfn_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getDepts_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                }
            } else {
                if (search_pfn_id.isEmpty()) {
                    if (item.getDoc_id().toLowerCase().contains(search_doc_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getDepts_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                } else {
                    if (item.getDoc_id().toLowerCase().contains(search_doc_id.toLowerCase())) {
                        if (item.getPfn_id().toLowerCase().contains(search_pfn_id.toLowerCase())) {
                            if (text.isEmpty()) {
                                filteredList.add((item));
                            }
                            else {
                                if (item.getDepts_id().toLowerCase().contains(text.toLowerCase())) {
                                    filteredList.add((item));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            noHistFound.setVisibility(View.VISIBLE);
        }
        else {
            noHistFound.setVisibility(View.GONE);
        }
        appointHistoryAdapter.filterList(filteredList);
    }

    private void filterDoctor(String text) {

        filteredList = new ArrayList<>();
        for (PatAppHistList item : patAppHistLists) {
            if (search_depts_id.isEmpty()){
                if (search_pfn_id.isEmpty()) {
                    if (text.isEmpty()) {
                        filteredList.add((item));
                    }
                    else {
                        if (item.getDoc_id().toLowerCase().contains(text.toLowerCase())) {
                            filteredList.add((item));
                        }
                    }
                } else {
                    if (item.getPfn_id().toLowerCase().contains(search_pfn_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getDoc_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                }
            } else {
                if (search_pfn_id.isEmpty()) {
                    if (item.getDepts_id().toLowerCase().contains(search_depts_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getDoc_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                } else {
                    if (item.getPfn_id().toLowerCase().contains(search_pfn_id.toLowerCase())) {
                        if (item.getDepts_id().toLowerCase().contains(search_depts_id.toLowerCase())) {
                            if (text.isEmpty()) {
                                filteredList.add((item));
                            }
                            else {
                                if (item.getDoc_id().toLowerCase().contains(text.toLowerCase())) {
                                    filteredList.add((item));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            noHistFound.setVisibility(View.VISIBLE);
        }
        else {
            noHistFound.setVisibility(View.GONE);
        }
        appointHistoryAdapter.filterList(filteredList);
    }

    public void getPatAppHistory() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        patAppHistLists = new ArrayList<>();
        choiceServiceLists = new ArrayList<>();
        choiceUnitLists = new ArrayList<>();
        choiceDoctorLists = new ArrayList<>();

        search_doc_id = "";
        search_depts_id = "";
        search_pfn_id = "";

        String patAppHistUrl = pre_url_api+"appointment_schedule/getPatAppointHistory?begin_date="+firstDate+"&end_date="+lastDate+"&p_ph_id="+ph_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest patPrgReq = new StringRequest(Request.Method.GET, patAppHistUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    choiceDoctorLists.add(new ChoiceDoctorList("","..."));
                    choiceUnitLists.add(new ChoiceUnitList("","..."));
                    choiceServiceLists.add(new ChoiceServiceList("","..."));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String ph_patient_status = info.getString("ph_patient_status")
                                .equals("null") ? "" : info.getString("ph_patient_status");
                        String ph_patient_cat = info.getString("ph_patient_cat")
                                .equals("null") ? "" :info.getString("ph_patient_cat");
                        String app_date = info.getString("app_date")
                                .equals("null") ? "" :info.getString("app_date");
                        String date_name = info.getString("date_name")
                                .equals("null") ? "" :info.getString("date_name");
                        String depts_name = info.getString("depts_name")
                                .equals("null") ? "" :info.getString("depts_name");
                        String depts_id = info.getString("depts_id")
                                .equals("null") ? "" :info.getString("depts_id");
                        String deptd_name = info.getString("deptd_name")
                                .equals("null") ? "" :info.getString("deptd_name");
                        String deptm_name = info.getString("deptm_name")
                                .equals("null") ? "" :info.getString("deptm_name");
                        String schedule_time = info.getString("schedule_time")
                                .equals("null") ? "" :info.getString("schedule_time");
                        String ets = info.getString("ets")
                                .equals("null") ? "" :info.getString("ets");
                        String doc_name = info.getString("doc_name")
                                .equals("null") ? "" : info.getString("doc_name");
                        String doc_id = info.getString("doc_id")
                                .equals("null") ? "" : info.getString("doc_id");
                        String pfn_fee_name = info.getString("pfn_fee_name")
                                .equals("null") ? "" : info.getString("pfn_fee_name");
                        String pfn_id = info.getString("pfn_id")
                                .equals("null") ? "" : info.getString("pfn_id");
                        String avail = info.getString("avail")
                                .equals("null") ? "" : info.getString("avail");
                        String ad_pat_app_status = info.getString("ad_pat_app_status")
                                .equals("null") ? "" : info.getString("ad_pat_app_status");
                        String cancel_status = info.getString("cancel_status")
                                .equals("null") ? "0" : info.getString("cancel_status");
                        String prm_code = info.getString("prm_code")
                                .equals("null") ? "" : info.getString("prm_code");

                        patAppHistLists.add(new PatAppHistList(ph_patient_status, ph_patient_cat,app_date,date_name, depts_name,depts_id,
                                deptd_name, deptm_name,schedule_time,ets,doc_name,doc_id, pfn_fee_name,pfn_id, avail,ad_pat_app_status,
                                cancel_status,prm_code));

                        boolean doc_exists = false;

                        for (ChoiceDoctorList doctor : choiceDoctorLists) {
                            if (doctor.getPph_doc_id().equals(doc_id) && doctor.getDoc_name().equals(doc_name)) {
                                doc_exists = true;
                                break;
                            }
                        }

                        if (!doc_exists) {
                            choiceDoctorLists.add(new ChoiceDoctorList(doc_id, doc_name));
                        }


                        boolean unit_exists = false;

                        for (ChoiceUnitList unit : choiceUnitLists) {
                            if (unit.getPph_depts_id().equals(depts_id) && unit.getDetps_name().equals(depts_name)) {
                                unit_exists = true;
                                break;
                            }
                        }

                        if (!unit_exists) {
                            choiceUnitLists.add(new ChoiceUnitList(depts_id, depts_name));
                        }


                        boolean service_exists = false;

                        for (ChoiceServiceList service : choiceServiceLists) {
                            if (service.getPph_pfn_id().equals(pfn_id) && service.getPfn_name().equals(pfn_fee_name)) {
                                service_exists = true;
                                break;
                            }
                        }

                        if (!service_exists) {
                            choiceServiceLists.add(new ChoiceServiceList(pfn_id, pfn_fee_name));
                        }

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

        requestQueue.add(patPrgReq);
    }

    private void updateInterface() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (patAppHistLists.isEmpty()) {
                    noHistFound.setVisibility(View.VISIBLE);
                }
                else {
                    noHistFound.setVisibility(View.GONE);
                }
                appointHistoryAdapter = new AppointHistoryAdapter(patAppHistLists, PatientAppointmentHistory.this);
                patAppointHistView.setAdapter(appointHistoryAdapter);

                serviceSearch.setText("");
                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < choiceServiceLists.size(); i++) {
                    type.add(choiceServiceLists.get(i).getPfn_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(PatientAppointmentHistory.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                serviceSearch.setAdapter(arrayAdapter);

                unitSearch.setText("");
                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < choiceUnitLists.size(); i++) {
                    type1.add(choiceUnitLists.get(i).getDetps_name());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(PatientAppointmentHistory.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                unitSearch.setAdapter(arrayAdapter1);

                doctorSearch.setText("");
                ArrayList<String> type2 = new ArrayList<>();
                for(int i = 0; i < choiceDoctorLists.size(); i++) {
                    type2.add(choiceDoctorLists.get(i).getDoc_name());
                }

                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(PatientAppointmentHistory.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type2);
                doctorSearch.setAdapter(arrayAdapter2);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PatientAppointmentHistory.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getPatAppHistory();
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

    public void restart(String msg) {
        try {
            ProcessPhoenix.triggerRebirth(getApplicationContext());
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }
}