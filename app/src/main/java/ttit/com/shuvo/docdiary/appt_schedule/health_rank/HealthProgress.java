package ttit.com.shuvo.docdiary.appt_schedule.health_rank;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.adapters.HealthProgressAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.ChoiceDoctorList;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.ChoiceServiceList;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.ChoiceUnitList;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.HealthProgressAdder;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.HealthProgressList;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.HealthProgressResponse;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.retrofit_process.ApiClient;

public class HealthProgress extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView close;

    TextView patName, patCode;

    CardView patHistSHCard, addPrSHCard;
    LinearLayout patPrHistLay;
    LinearLayout patAddProgressLay;

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

    CardView searchSHCard;
    TextView healthPrgText;
    ProgressBar hpBar;

    RecyclerView patProgressHistView;
    HealthProgressAdapter healthProgressAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<HealthProgressList> healthProgressLists;
    ArrayList<HealthProgressList> filteredList;
    TextView noHistFound;

    TextInputEditText progressDate,serviceName,doctorName,unitName;
    AppCompatAutoCompleteTextView patProgressScore;
    TextView prgScoreMissing;
    TextInputEditText progressNotes;
    TextView progressNoteMissing;
    Button addProgress;


    private int shortAnimationDuration;

    String pat_name = "";
    String pat_code = "";
    String ph_id = "";
    String is_ranked = "";
    String adm_date = "";
    String sch_time = "";
    String pfn_name = "";
    String pfn_id = "";
    String doc_name = "";
    String doc_id = "";
    String doc_code = "";
    String depts_name = "";
    String depts_id = "";
    String cat_id = "";
    String prm_id = "";
    String prd_id = "";
    String ad_id = "";
    String entry_time = "";
    String pat_progress = "";
    String progress_notes = "";

    private boolean conn = false;
    private boolean connected = false;
    private boolean patProgressLoading = false;
    String parsing_message = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_progress);

        fullLayout = findViewById(R.id.health_progress_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_health_progress);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.back_logo_of_health_progress);

        patName = findViewById(R.id.pat_progress_patient_name);
        patCode = findViewById(R.id.pat_progress_patient_ph_code);

        patHistSHCard = findViewById(R.id.pat_pr_hist_card_text);
        addPrSHCard = findViewById(R.id.add_pr_icon_card_view);

        patAddProgressLay = findViewById(R.id.patient_add_progress_layout);
        patPrHistLay = findViewById(R.id.patient_progress_history_layout);
        patPrHistLay.setVisibility(View.VISIBLE);
        patAddProgressLay.setVisibility(View.GONE);

        searchLay = findViewById(R.id.search_layout_p_hp);
        searchLay.setVisibility(View.GONE);

        serviceSearch = findViewById(R.id.service_search_for_p_hp);
        unitSearch = findViewById(R.id.unit_search_for_p_hp);
        doctorSearch = findViewById(R.id.doc_search_for_p_hp);
        choiceServiceLists = new ArrayList<>();
        choiceUnitLists = new ArrayList<>();
        choiceDoctorLists = new ArrayList<>();

        searchSHCard = findViewById(R.id.search_layout_show_visible_icon);
        healthPrgText = findViewById(R.id.pat_current_progress_text_view);
        hpBar = findViewById(R.id.pat_health_progress_bar_in_p_hp);

        patProgressHistView = findViewById(R.id.pat_progress_history_recyclerview);
        noHistFound = findViewById(R.id.no_information_found_msg_pat_prg_hist);
        noHistFound.setVisibility(View.GONE);

        progressDate = findViewById(R.id.patient_progress_date_in_hp);
        serviceName = findViewById(R.id.patient_service_name_in_hp);
        doctorName = findViewById(R.id.patient_doctor_name_in_hp);
        unitName = findViewById(R.id.patient_unit_name_in_hp);

        patProgressScore = findViewById(R.id.patient_health_progress_for_patient_spinner);
        prgScoreMissing = findViewById(R.id.health_progress_for_patient_missing_msg);
        prgScoreMissing.setVisibility(View.GONE);
        progressNotes = findViewById(R.id.progress_note_for_patient_spinner);
        progressNoteMissing = findViewById(R.id.progress_note_for_patient_missing_msg);
        progressNoteMissing.setVisibility(View.GONE);

        addProgress = findViewById(R.id.pat_add_health_progress_button);

        healthProgressLists = new ArrayList<>();
        patProgressHistView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        patProgressHistView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(patProgressHistView.getContext(),DividerItemDecoration.VERTICAL);
        patProgressHistView.addItemDecoration(dividerItemDecoration);

        if (userInfoLists == null) {
            restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (userInfoLists.size() == 0) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            }
            else {
                doc_name = userInfoLists.get(0).getDoc_name();
                doc_id = userInfoLists.get(0).getDoc_id();
                doc_code = userInfoLists.get(0).getDoc_code();
            }
        }

        Intent intent = getIntent();
        pat_name = intent.getStringExtra("P_NAME");
        pat_code = intent.getStringExtra("P_CODE");
        ph_id = intent.getStringExtra("P_PH_ID");
        is_ranked = intent.getStringExtra("IS_RANKED");
        adm_date = intent.getStringExtra("ADM_DATE");
        sch_time = intent.getStringExtra("APPT_TIME");
        pfn_name = intent.getStringExtra("PFN_NAME");
        depts_name = intent.getStringExtra("DEPTS_NAME");
        cat_id = intent.getStringExtra("CAT_ID");
        pfn_id = intent.getStringExtra("PFN_ID");
        depts_id = intent.getStringExtra("DEPTS_ID");
        prm_id = intent.getStringExtra("PRM_ID");
        prd_id = intent.getStringExtra("PRD_ID");
        ad_id = intent.getStringExtra("AD_ID");

        patName.setText(pat_name);
        patCode.setText(pat_code);

        progressDate.setText(adm_date);
        serviceName.setText(pfn_name);
        doctorName.setText(doc_name);
        unitName.setText(depts_name);

        ArrayList<String> progressScoreList = new ArrayList<>();
        progressScoreList.add("1");
        progressScoreList.add("2");
        progressScoreList.add("3");
        progressScoreList.add("4");
        progressScoreList.add("5");
        progressScoreList.add("6");
        progressScoreList.add("7");
        progressScoreList.add("8");
        progressScoreList.add("9");
        progressScoreList.add("10");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(HealthProgress.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,progressScoreList);
        patProgressScore.setAdapter(arrayAdapter);

        String appt_date = adm_date;
        appt_date = appt_date + " " + sch_time;
        System.out.println("APP DATE: "+ appt_date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
        Date appDate;
        try {
            appDate = simpleDateFormat.parse(appt_date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Calendar calendar = Calendar.getInstance();
        Date nowDate = calendar.getTime();
        SimpleDateFormat sss = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        String nnn = sss.format(nowDate);
        nnn = nnn.toUpperCase();

        if (nnn.equals(adm_date)) {
            if (appDate != null) {
                if (appDate.before(nowDate)) {
                    if (is_ranked.equals("0")) {
                        addPrSHCard.setVisibility(View.VISIBLE);
                    }
                    else {
                        addPrSHCard.setVisibility(View.GONE);
                    }
                }
                else {
                    addPrSHCard.setVisibility(View.GONE);
                }
            }
            else {
                addPrSHCard.setVisibility(View.GONE);
            }
        }
        else {
            addPrSHCard.setVisibility(View.GONE);
        }

        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        searchSHCard.setOnClickListener(v -> {
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

        patHistSHCard.setOnClickListener(v -> {
            if (patPrHistLay.getVisibility() == View.GONE) {
                patAddProgressLay.animate()
                        .alpha(0.0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                patAddProgressLay.setVisibility(View.GONE);
                                patPrHistLay.setVisibility(View.VISIBLE);
                                patPrHistLay.setAlpha(0.0f);
                                // Start the animation
                                patPrHistLay.animate()
                                        .alpha(1.0f)
                                        .setDuration(shortAnimationDuration)
                                        .setListener(null);
                            }
                        });
            }

        });

        addPrSHCard.setOnClickListener(v -> {
            if (patAddProgressLay.getVisibility() == View.GONE) {
                patPrHistLay.animate()
                        .alpha(0.0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                patPrHistLay.setVisibility(View.GONE);
                                patAddProgressLay.setVisibility(View.VISIBLE);
                                patAddProgressLay.setAlpha(0.0f);
                                // Start the animation
                                patAddProgressLay.animate()
                                        .alpha(1.0f)
                                        .setDuration(shortAnimationDuration)
                                        .setListener(null);
                            }
                        });

            }
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

        patProgressScore.setOnItemClickListener((parent, view, position, id) -> {
            pat_progress = "";
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            pat_progress = name;
            prgScoreMissing.setVisibility(View.GONE);
        });

        progressNotes.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    progressNotes.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        progressNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    progressNoteMissing.setVisibility(View.VISIBLE);
                }
                else {
                    progressNoteMissing.setVisibility(View.GONE);
                }
            }
        });

        addProgress.setOnClickListener(v -> {
            progress_notes = Objects.requireNonNull(progressNotes.getText()).toString();
            if (!pat_progress.isEmpty()) {
                if (!progress_notes.isEmpty()) {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(HealthProgress.this);
                    alertDialogBuilder.setTitle("Add Progress!")
                            .setMessage("Do you want to add Progress for this patient in this schedule? if you add progress" +
                                    " for this schedule, you will not be able to change or update this progress. So, please carefully add progress" +
                                    " for patient.")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                SimpleDateFormat smdft = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH);
                                Calendar cc = Calendar.getInstance();
                                Date nd = cc.getTime();
                                entry_time = smdft.format(nd);
                                dialog.dismiss();
                                addPatProgress(healthProgressAdder());
                            })
                            .setNegativeButton("No",(dialog, which) -> {
                                dialog.dismiss();
                            });

                    AlertDialog alert = alertDialogBuilder.create();
                    try {
                        alert.show();
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please Provide Progress Note",Toast.LENGTH_SHORT).show();
                    progressNoteMissing.setVisibility(View.VISIBLE);
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Provide Progress",Toast.LENGTH_SHORT).show();
                prgScoreMissing.setVisibility(View.VISIBLE);
            }
        });

        close.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPatPrgHistData();

    }

    @Override
    public void onBackPressed() {
        if (patProgressLoading) {
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

    private void filterService(String text) {

        filteredList = new ArrayList<>();
        for (HealthProgressList item : healthProgressLists) {
            if (search_doc_id.isEmpty()){
                if (search_depts_id.isEmpty()) {
                    if (text.isEmpty()) {
                        filteredList.add((item));
                    }
                    else {
                        if (item.getPph_pfn_id().toLowerCase().contains(text.toLowerCase())) {
                            filteredList.add((item));
                        }
                    }
                } else {
                    if (item.getPph_depts_id().toLowerCase().contains(search_depts_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getPph_pfn_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                }
            } else {
                if (search_depts_id.isEmpty()) {
                    if (item.getPph_doc_id().toLowerCase().contains(search_doc_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getPph_pfn_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                } else {
                    if (item.getPph_doc_id().toLowerCase().contains(search_doc_id.toLowerCase())) {
                        if (item.getPph_depts_id().toLowerCase().contains(search_depts_id.toLowerCase())) {
                            if (text.isEmpty()) {
                                filteredList.add((item));
                            }
                            else {
                                if (item.getPph_pfn_id().toLowerCase().contains(text.toLowerCase())) {
                                    filteredList.add((item));
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < filteredList.size(); i++) {
            filteredList.get(i).setSerialNo(String.valueOf(filteredList.size()-i));
        }
        if (filteredList.size() == 0) {
            noHistFound.setVisibility(View.VISIBLE);
            healthPrgText.setText("0");
            hpBar.setProgress(0);
        }
        else {
            noHistFound.setVisibility(View.GONE);
            healthPrgText.setText(filteredList.get(0).getPph_progress());
            hpBar.setProgress(Integer.parseInt(filteredList.get(0).getPph_progress()));
        }
        healthProgressAdapter.filterList(filteredList);
    }

    private void filterUnit(String text) {

        filteredList = new ArrayList<>();
        for (HealthProgressList item : healthProgressLists) {
            if (search_doc_id.isEmpty()){
                if (search_pfn_id.isEmpty()) {
                    if (text.isEmpty()) {
                        filteredList.add((item));
                    }
                    else {
                        if (item.getPph_depts_id().toLowerCase().contains(text.toLowerCase())) {
                            filteredList.add((item));
                        }
                    }
                } else {
                    if (item.getPph_pfn_id().toLowerCase().contains(search_pfn_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getPph_depts_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                }
            } else {
                if (search_pfn_id.isEmpty()) {
                    if (item.getPph_doc_id().toLowerCase().contains(search_doc_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getPph_depts_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                } else {
                    if (item.getPph_doc_id().toLowerCase().contains(search_doc_id.toLowerCase())) {
                        if (item.getPph_pfn_id().toLowerCase().contains(search_pfn_id.toLowerCase())) {
                            if (text.isEmpty()) {
                                filteredList.add((item));
                            }
                            else {
                                if (item.getPph_depts_id().toLowerCase().contains(text.toLowerCase())) {
                                    filteredList.add((item));
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < filteredList.size(); i++) {
            filteredList.get(i).setSerialNo(String.valueOf(filteredList.size()-i));
        }
        if (filteredList.size() == 0) {
            noHistFound.setVisibility(View.VISIBLE);
            healthPrgText.setText("0");
            hpBar.setProgress(0);
        }
        else {
            noHistFound.setVisibility(View.GONE);
            healthPrgText.setText(filteredList.get(0).getPph_progress());
            hpBar.setProgress(Integer.parseInt(filteredList.get(0).getPph_progress()));
        }
        healthProgressAdapter.filterList(filteredList);
    }

    private void filterDoctor(String text) {

        filteredList = new ArrayList<>();
        for (HealthProgressList item : healthProgressLists) {
            if (search_depts_id.isEmpty()){
                if (search_pfn_id.isEmpty()) {
                    if (text.isEmpty()) {
                        filteredList.add((item));
                    }
                    else {
                        if (item.getPph_doc_id().toLowerCase().contains(text.toLowerCase())) {
                            filteredList.add((item));
                        }
                    }
                } else {
                    if (item.getPph_pfn_id().toLowerCase().contains(search_pfn_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getPph_doc_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                }
            } else {
                if (search_pfn_id.isEmpty()) {
                    if (item.getPph_depts_id().toLowerCase().contains(search_depts_id.toLowerCase())) {
                        if (text.isEmpty()) {
                            filteredList.add((item));
                        }
                        else {
                            if (item.getPph_doc_id().toLowerCase().contains(text.toLowerCase())) {
                                filteredList.add((item));
                            }
                        }
                    }
                } else {
                    if (item.getPph_pfn_id().toLowerCase().contains(search_pfn_id.toLowerCase())) {
                        if (item.getPph_depts_id().toLowerCase().contains(search_depts_id.toLowerCase())) {
                            if (text.isEmpty()) {
                                filteredList.add((item));
                            }
                            else {
                                if (item.getPph_doc_id().toLowerCase().contains(text.toLowerCase())) {
                                    filteredList.add((item));
                                }
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < filteredList.size(); i++) {
            filteredList.get(i).setSerialNo(String.valueOf(filteredList.size()-i));
        }
        if (filteredList.size() == 0) {
            noHistFound.setVisibility(View.VISIBLE);
            healthPrgText.setText("0");
            hpBar.setProgress(0);
        }
        else {
            noHistFound.setVisibility(View.GONE);
            healthPrgText.setText(filteredList.get(0).getPph_progress());
            hpBar.setProgress(Integer.parseInt(filteredList.get(0).getPph_progress()));
        }
        healthProgressAdapter.filterList(filteredList);
    }

    public void getPatPrgHistData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        patProgressLoading = true;

        healthProgressLists = new ArrayList<>();
        choiceServiceLists = new ArrayList<>();
        choiceUnitLists = new ArrayList<>();
        choiceDoctorLists = new ArrayList<>();

        String patPrgUrl = pre_url_api+"health_progress/getHealthProgress?p_ph_id="+ph_id+"";
        String patPrgServiceUrl = pre_url_api+"health_progress/getService?p_ph_id="+ph_id+"";
        String patPrgUnitUrl = pre_url_api+"health_progress/getUnit?p_ph_id="+ph_id+"";
        String patPrgDocUrl = pre_url_api+"health_progress/getDocs?p_ph_id="+ph_id+"";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest patPrgDocReq = new StringRequest(Request.Method.GET, patPrgDocUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    choiceDoctorLists.add(new ChoiceDoctorList("","..."));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pph_doc_id = info.getString("pph_doc_id")
                                .equals("null") ? "" : info.getString("pph_doc_id");
                        String doc_name = info.getString("doc_name")
                                .equals("null") ? "" : info.getString("doc_name");

                        choiceDoctorLists.add(new ChoiceDoctorList(pph_doc_id,doc_name));
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

        StringRequest patPrgUnitReq = new StringRequest(Request.Method.GET, patPrgUnitUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    choiceUnitLists.add(new ChoiceUnitList("","..."));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pph_depts_id = info.getString("pph_depts_id")
                                .equals("null") ? "" : info.getString("pph_depts_id");
                        String detps_name = info.getString("detps_name")
                                .equals("null") ? "" : info.getString("detps_name");

                        choiceUnitLists.add(new ChoiceUnitList(pph_depts_id,detps_name));
                    }
                }

                requestQueue.add(patPrgDocReq);
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

        StringRequest patPrgServReq = new StringRequest(Request.Method.GET, patPrgServiceUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    choiceServiceLists.add(new ChoiceServiceList("","..."));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pph_pfn_id = info.getString("pph_pfn_id")
                                .equals("null") ? "" : info.getString("pph_pfn_id");
                        String pfn_name = info.getString("pfn_name")
                                .equals("null") ? "" : info.getString("pfn_name");

                        choiceServiceLists.add(new ChoiceServiceList(pph_pfn_id,pfn_name));
                    }
                }

                requestQueue.add(patPrgUnitReq);
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

        StringRequest patPrgReq = new StringRequest(Request.Method.GET, patPrgUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pph_id = info.getString("pph_id")
                                .equals("null") ? "" : info.getString("pph_id");
                        String pph_date = info.getString("pph_date")
                                .equals("null") ? "" : info.getString("pph_date");
                        String pph_doc_id = info.getString("pph_doc_id")
                                .equals("null") ? "" : info.getString("pph_doc_id");
                        String doc_name = info.getString("doc_name")
                                .equals("null") ? "" : info.getString("doc_name");
                        String pph_depts_id = info.getString("pph_depts_id")
                                .equals("null") ? "" : info.getString("pph_depts_id");
                        String detps_name = info.getString("detps_name")
                                .equals("null") ? "" : info.getString("detps_name");
                        String pph_pfn_id = info.getString("pph_pfn_id")
                                .equals("null") ? "" : info.getString("pph_pfn_id");
                        String pfn_name = info.getString("pfn_name")
                                .equals("null") ? "" : info.getString("pfn_name");
                        String pph_ad_id = info.getString("pph_ad_id")
                                .equals("null") ? "" : info.getString("pph_ad_id");
                        String pph_progress = info.getString("pph_progress")
                                .equals("null") ? "" : info.getString("pph_progress");
                        String pph_notes = info.getString("pph_notes")
                                .equals("null") ? "" : info.getString("pph_notes");
                        pph_notes = transformText(pph_notes);
                        String pph_rec_no = info.getString("pph_rec_no")
                                .equals("null") ? "" : info.getString("pph_rec_no");

                        healthProgressLists.add(new HealthProgressList(String.valueOf(array.length()-i),pph_id,pph_date,pph_doc_id,doc_name,pph_depts_id,
                                detps_name, pph_pfn_id,pfn_name,pph_ad_id,pph_progress,pph_notes,pph_rec_no));
                    }
                }

                requestQueue.add(patPrgServReq);
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

        requestQueue.add(patPrgReq);
    }

    private void updateInterface() {
        patProgressLoading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (healthProgressLists.size() == 0) {
                    noHistFound.setVisibility(View.VISIBLE);
                    healthPrgText.setText("0");
                    hpBar.setProgress(0);
                }
                else {
                    noHistFound.setVisibility(View.GONE);
                    healthPrgText.setText(healthProgressLists.get(0).getPph_progress());
                    hpBar.setProgress(Integer.parseInt(healthProgressLists.get(0).getPph_progress()));
                }
                healthProgressAdapter = new HealthProgressAdapter(healthProgressLists, HealthProgress.this);
                patProgressHistView.setAdapter(healthProgressAdapter);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < choiceServiceLists.size(); i++) {
                    type.add(choiceServiceLists.get(i).getPfn_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(HealthProgress.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                serviceSearch.setAdapter(arrayAdapter);

                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < choiceUnitLists.size(); i++) {
                    type1.add(choiceUnitLists.get(i).getDetps_name());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(HealthProgress.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                unitSearch.setAdapter(arrayAdapter1);

                ArrayList<String> type2 = new ArrayList<>();
                for(int i = 0; i < choiceDoctorLists.size(); i++) {
                    type2.add(choiceDoctorLists.get(i).getDoc_name());
                }

                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(HealthProgress.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type2);
                doctorSearch.setAdapter(arrayAdapter2);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(HealthProgress.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getPatPrgHistData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
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

    public HealthProgressAdder healthProgressAdder() {
        HealthProgressAdder hpAdd = new HealthProgressAdder();

        hpAdd.setP_PH_ID(ph_id);
        hpAdd.setP_CAT_ID(cat_id);
        hpAdd.setP_DOC_ID(doc_id);
        hpAdd.setP_DEPTS_ID(depts_id);
        hpAdd.setP_PRM_ID(prm_id);
        hpAdd.setP_PFN_ID(pfn_id);
        hpAdd.setP_PRD_ID(prd_id);
        hpAdd.setP_AD_ID(ad_id);
        hpAdd.setP_PROGRESS(pat_progress);
        hpAdd.setP_NOTES(progress_notes);
        hpAdd.setP_DOC_CODE(doc_code);
        hpAdd.setP_ENTRY_TIME(entry_time);

        return hpAdd;
    }


    public void addPatProgress(HealthProgressAdder healthProgressAdder) {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        patProgressLoading = true;

        Call<HealthProgressResponse> healthProgressResponseCall = ApiClient.getService().addHealthProgress(healthProgressAdder);

        healthProgressResponseCall.enqueue(new Callback<HealthProgressResponse>() {
            @Override
            public void onResponse(Call<HealthProgressResponse> call, @NonNull Response<HealthProgressResponse> response) {

                if (response.isSuccessful()) {
                    conn = true;
                    String string_out = Objects.requireNonNull(response.body()).getString_out();
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
                else {
                    conn = false;
                    connected = false;
                    parsing_message = "";
                    updateAfterAdd();
                }
            }

            @Override
            public void onFailure(Call<HealthProgressResponse> call, @NonNull Throwable t) {
                conn = false;
                connected = false;
                t.printStackTrace();
                parsing_message = t.getLocalizedMessage();
                updateAfterAdd();
            }
        });
    }
//    public void addPatProgress() {
//        fullLayout.setVisibility(View.GONE);
//        circularProgressIndicator.setVisibility(View.VISIBLE);
//        conn = false;
//        connected = false;
//        patProgressLoading = true;
//
//        String insertProgress = pre_url_api+"health_progress/InsertPatProgress";
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        StringRequest request = new StringRequest(Request.Method.POST, insertProgress, response -> {
//            conn = true;
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                String string_out = jsonObject.getString("string_out");
//                parsing_message = string_out;
//
//                if (string_out.equals("Successfully Created")) {
//                    connected = true;
//                    updateAfterAdd();
//                }
//                else {
//                    connected = false;
//                    updateAfterAdd();
//                }
//            }
//            catch (JSONException e) {
//                connected = false;
//                e.printStackTrace();
//                parsing_message = e.getLocalizedMessage();
//                updateAfterAdd();
//            }
//        }, error -> {
//            conn = false;
//            connected = false;
//            error.printStackTrace();
//            parsing_message = error.getLocalizedMessage();
//            updateAfterAdd();
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("P_PH_ID",ph_id);
//                headers.put("P_CAT_ID",cat_id);
//                headers.put("P_DOC_ID",doc_id);
//                headers.put("P_DEPTS_ID",depts_id);
//                headers.put("P_PRM_ID",prm_id);
//                headers.put("P_PFN_ID",pfn_id);
//                headers.put("P_PRD_ID",prd_id);
//                headers.put("P_AD_ID",ad_id);
//                headers.put("P_PROGRESS",pat_progress);
//                headers.put("P_NOTES",progress_notes);
//                headers.put("P_DOC_CODE",doc_code);
//                headers.put("P_ENTRY_TIME",entry_time);
//                return headers;
//            }
//        };
//
//        requestQueue.add(request);
//    }

    private void updateAfterAdd() {
        patProgressLoading = false;
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;
                Toast.makeText(getApplicationContext(),"New Progress Added",Toast.LENGTH_SHORT).show();
                addPrSHCard.setVisibility(View.GONE);
                patAddProgressLay.setVisibility(View.GONE);
                patAddProgressLay.setAlpha(0.0f);
                patPrHistLay.setVisibility(View.VISIBLE);
                patPrHistLay.setAlpha(1.0f);
                getPatPrgHistData();
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(HealthProgress.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    addPatProgress(healthProgressAdder());
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
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
    private String transformText(String text) {
        byte[] bytes = text.getBytes(ISO_8859_1);
        return new String(bytes, UTF_8);
    }
}