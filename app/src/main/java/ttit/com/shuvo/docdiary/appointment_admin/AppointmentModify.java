package ttit.com.shuvo.docdiary.appointment_admin;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.adminInfoLists;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.rosemaryapp.amazingspinner.AmazingSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.all_appointment.AllAppointment;
import ttit.com.shuvo.docdiary.appointment_admin.adapters.SavedAppointmentDateTimeAdapter;
import ttit.com.shuvo.docdiary.appointment_admin.add_schedule.AddSchedule;
import ttit.com.shuvo.docdiary.appointment_admin.add_schedule.adapters.TakenScheduleAdapter;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.AppointDateTimeCancelList;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.AppointTimeCancelList;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.DoctorForAppList;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.PaymentForAppList;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.SavedAppointmentDateTimeList;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.ServiceForAppList;
import ttit.com.shuvo.docdiary.appointment_admin.dialogue.SearchDoctorAppDialog;
import ttit.com.shuvo.docdiary.appointment_admin.dialogue.SearchDoctorCancelAppDialog;
import ttit.com.shuvo.docdiary.appointment_admin.dialogue.SearchPatientAppDialog;
import ttit.com.shuvo.docdiary.appointment_admin.dialogue.SearchPatientCancelAppDialog;
import ttit.com.shuvo.docdiary.appointment_admin.dialogue.SearchPaymentAppDialog;
import ttit.com.shuvo.docdiary.appointment_admin.interfaces.AppCancelDoctorSelectListener;
import ttit.com.shuvo.docdiary.appointment_admin.interfaces.PatAppCancelCallBackListener;
import ttit.com.shuvo.docdiary.appointment_admin.interfaces.PatAppDoctorSelectionListener;
import ttit.com.shuvo.docdiary.appointment_admin.interfaces.PatAppPaymentSelectionListener;
import ttit.com.shuvo.docdiary.appointment_admin.interfaces.PatAppSelectCallBackListener;

public class AppointmentModify extends AppCompatActivity implements PatAppSelectCallBackListener,
        PatAppDoctorSelectionListener, PatAppPaymentSelectionListener, PatAppCancelCallBackListener, AppCancelDoctorSelectListener {

    RelativeLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    MaterialButton allAppointment;

    TabLayout appointmentTab;
    RelativeLayout addAppointmentLay;
    RelativeLayout cancelAppointmentLay;

    TextInputLayout searchPatientLay, selectDoctorLay, selectPaymentLay, selectServiceLay;
    AppCompatAutoCompleteTextView searchPatient, selectDoctor, unitSelect, selectPayment;
    public static AppCompatAutoCompleteTextView serviceQty;
    AmazingSpinner selectService;
    TextView patSelectErrorMsg, docSelectErrMsg, paymentSelectErrMsg, serviceSelectErrMsg;


    TextInputLayout searchPatientCancLay, selectDoctorCancLay, selectServiceCancLay, selectAppDateCancLay;
    AppCompatAutoCompleteTextView searchPatientCanc, selectDoctorCanc, unitSelectCanc ;
    AmazingSpinner selectServiceCanc, selectAppDateCanc;
    TextView patSelectCancErrorMsg, docSelectCancErrMsg, serviceSelectCancErrMsg, appDateSelectCancErrMsg;

    CardView appTimeCardViewLay;
    RecyclerView appTimeView;
    RecyclerView.LayoutManager timeLayoutManager;
    TakenScheduleAdapter takenScheduleAdapter;


    public static String selected_patient_name = "";
    public static String selected_pat_ph_id = "";
    public static String selected_pat_id = "";
    public static String selected_pat_cat_id = "";
    public static String selected_doc_id = "";
    public static String selected_doc_name = "";
    public static String selected_depts_id = "";
    public static String selected_depts_name = "";
    public static String selected_deptd_id = "";
    public static String selected_desig_id = "";

    public static String selected_prm_id = "";
    public static String selected_prm_code = "";

    String selected_prd_id = "";
    String selected_pfn_id = "";
    public static String selected_service_qty = "";
    String selected_service_name = "";

    String selected_adm_date = "";
    String  adm_date_position = "";
    String selected_ad_id = "";

    ArrayList<DoctorForAppList> doctorForAppLists;
    ArrayList<PaymentForAppList> paymentForAppLists;
    ArrayList<ServiceForAppList> serviceForAppLists;

    ArrayList<AppointDateTimeCancelList> appointDateTimeCancelLists;
    ArrayList<String> appTimeLists;


    RecyclerView appointDateTimeView;
    SavedAppointmentDateTimeAdapter savedAppointmentDateTimeAdapter;
    RecyclerView.LayoutManager layoutManager;
    MaterialButton addScheduleButton;

    LinearLayout buttonLay;
    Button addAppointment;
    Button cancelAppointment;

    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";
    public static boolean scheduleSelected = false;

    public static ArrayList<SavedAppointmentDateTimeList> appointmentDateTimeLists;
    int previousTabPosition = 0;
    
    Logger logger = Logger.getLogger(AppointmentModify.class.getName());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_modify);

        fullLayout = findViewById(R.id.appointment_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_appointment_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_appointment_modify);

        appointmentTab = findViewById(R.id.appointment_modify_tab_layout);
        addAppointmentLay = findViewById(R.id.add_appointment_selected_tab_layout);
        cancelAppointmentLay = findViewById(R.id.cancel_appointment_selected_tab_layout);
        cancelAppointmentLay.setVisibility(View.GONE);

        allAppointment = findViewById(R.id.view_all_appointment_from_add_app_modify);

        searchPatientLay = findViewById(R.id.spinner_layout_search_patient_for_add_appointment);
        searchPatient = findViewById(R.id.search_patient_for_add_appointment_spinner);
        patSelectErrorMsg = findViewById(R.id.select_pat_for_app_error_handling_msg);

        searchPatientCancLay = findViewById(R.id.spinner_layout_search_patient_for_app_cancel);
        searchPatientCanc = findViewById(R.id.search_patient_for_app_cancel_spinner);
        patSelectCancErrorMsg = findViewById(R.id.select_pat_for_app_cancel_error_handling_msg);

        selectDoctorLay = findViewById(R.id.spinner_layout_select_doctor_for_add_appointment);
        selectDoctorLay.setEnabled(false);
        selectDoctor = findViewById(R.id.select_doctor_for_add_appointment_spinner);
        docSelectErrMsg = findViewById(R.id.select_doctor_for_app_error_handling_msg);

        selectDoctorCancLay = findViewById(R.id.spinner_layout_select_doctor_for_app_cancel);
        selectDoctorCancLay.setEnabled(false);
        selectDoctorCanc = findViewById(R.id.select_doctor_for_app_cancel_spinner);
        docSelectCancErrMsg = findViewById(R.id.select_doctor_for_app_cancel_error_handling_msg);

        unitSelect = findViewById(R.id.select_unit_for_add_appointment_spinner);

        unitSelectCanc = findViewById(R.id.select_unit_for_app_cancel_spinner);

        selectPaymentLay = findViewById(R.id.spinner_layout_select_payment_for_add_appointment);
        selectPaymentLay.setEnabled(false);
        selectPayment = findViewById(R.id.select_payment_for_add_appointment_spinner);
        paymentSelectErrMsg = findViewById(R.id.select_payment_for_app_error_handling_msg);

        selectServiceLay = findViewById(R.id.spinner_layout_select_service_for_add_appointment);
        selectServiceLay.setEnabled(false);
        selectService = findViewById(R.id.select_service_for_add_appointment_spinner);
        serviceQty = findViewById(R.id.service_qty_from_payment_add_appointment);
        serviceSelectErrMsg = findViewById(R.id.select_service_for_app_error_handling_msg);

        selectServiceCancLay = findViewById(R.id.spinner_layout_select_service_for_app_cancel);
        selectServiceCancLay.setEnabled(false);
        selectServiceCanc = findViewById(R.id.select_service_for_app_cancel_spinner);
        serviceSelectCancErrMsg = findViewById(R.id.select_service_for_app_cancel_error_handling_msg);

        selectAppDateCancLay = findViewById(R.id.spinner_layout_select_appoint_date_for_app_cancel);
        selectAppDateCancLay.setEnabled(false);
        selectAppDateCanc = findViewById(R.id.select_appoint_date_for_app_cancel_spinner);
        appDateSelectCancErrMsg = findViewById(R.id.select_appoint_date_for_app_cancel_error_handling_msg);

        appTimeCardViewLay = findViewById(R.id.taken_schedule_app_cancel_layout_card);
        appTimeCardViewLay.setVisibility(View.GONE);
        appTimeView = findViewById(R.id.taken_appointment_time_for_app_cancel_recyclerview_for_patient);

        patSelectErrorMsg.setVisibility(View.GONE);
        docSelectErrMsg.setVisibility(View.GONE);
        paymentSelectErrMsg.setVisibility(View.GONE);
        serviceSelectErrMsg.setVisibility(View.GONE);

        patSelectCancErrorMsg.setVisibility(View.GONE);
        docSelectCancErrMsg.setVisibility(View.GONE);
        serviceSelectCancErrMsg.setVisibility(View.GONE);
        appDateSelectCancErrMsg.setVisibility(View.GONE);

        appointDateTimeView = findViewById(R.id.appointment_date_time_recyclerview_for_patient_add);
        addScheduleButton = findViewById(R.id.add_schedule_for_add_appointment_patient);

        buttonLay = findViewById(R.id.appointment_modify_button_layout);
        addAppointment = findViewById(R.id.add_appointment_button);
        cancelAppointment = findViewById(R.id.cancel_appointment_button);
        cancelAppointment.setVisibility(View.GONE);

        previousTabPosition = appointmentTab.getSelectedTabPosition();

        doctorForAppLists = new ArrayList<>();
        paymentForAppLists = new ArrayList<>();
        serviceForAppLists = new ArrayList<>();
        selected_patient_name = "";
        selected_pat_ph_id = "";
        selected_pat_id = "";
        selected_pat_cat_id = "";
        selected_doc_id = "";
        selected_doc_name = "";
        selected_depts_id = "";
        selected_depts_name = "";
        selected_deptd_id = "";
        selected_desig_id = "";
        selected_prm_id = "";
        selected_prm_code = "";
        selected_prd_id = "";
        selected_pfn_id = "";
        selected_service_qty = "";
        selected_service_name = "";
        selected_adm_date = "";
        adm_date_position = "";
        selected_ad_id = "";
        scheduleSelected = false;
        appointmentDateTimeLists = new ArrayList<>();
        appTimeLists = new ArrayList<>();
        appointDateTimeCancelLists = new ArrayList<>();

        appointDateTimeView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        appointDateTimeView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(appointDateTimeView.getContext(),DividerItemDecoration.VERTICAL);
        appointDateTimeView.addItemDecoration(dividerItemDecoration);

        appTimeView.setHasFixedSize(true);
        timeLayoutManager = new LinearLayoutManager(getApplicationContext());
        appTimeView.setLayoutManager(timeLayoutManager);

        appointmentTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (previousTabPosition != tab.getPosition()) {
                    if (tab.getPosition() == 1) {
                        if (!appointmentDateTimeLists.isEmpty()) {
                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
                            alertDialogBuilder
                                    .setMessage("You have Data Stored in this section. Do you want to switch to Cancel Appointment section?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        previousTabPosition = tab.getPosition();

                                        resetAllData();

                                        addAppointment.setVisibility(View.GONE);
                                        addScheduleButton.setVisibility(View.GONE);
                                        cancelAppointment.setVisibility(View.VISIBLE);
                                        addAppointmentLay.setVisibility(View.GONE);
                                        cancelAppointmentLay.setVisibility(View.VISIBLE);

                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("No",((dialog, which) -> {
                                        appointmentTab.selectTab(appointmentTab.getTabAt(previousTabPosition));
                                        dialog.dismiss();
                                    }));

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.setCancelable(false);
                            alert.setCanceledOnTouchOutside(false);
                            alert.show();
                        }
                        else {
                            previousTabPosition = tab.getPosition();

                            resetAllData();

                            addAppointment.setVisibility(View.GONE);
                            addScheduleButton.setVisibility(View.GONE);
                            cancelAppointment.setVisibility(View.VISIBLE);
                            addAppointmentLay.setVisibility(View.GONE);
                            cancelAppointmentLay.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        if (!selected_ad_id.isEmpty()) {
                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
                            alertDialogBuilder
                                    .setMessage("You have Data Stored in this section. Do you want to switch to Add Appointment section?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        previousTabPosition = tab.getPosition();

                                        resetAllData();

                                        addAppointment.setVisibility(View.VISIBLE);
                                        addScheduleButton.setVisibility(View.VISIBLE);
                                        cancelAppointment.setVisibility(View.GONE);
                                        addAppointmentLay.setVisibility(View.VISIBLE);
                                        cancelAppointmentLay.setVisibility(View.GONE);
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("No",((dialog, which) -> {
                                        appointmentTab.selectTab(appointmentTab.getTabAt(previousTabPosition));
                                        dialog.dismiss();
                                    }));

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.setCancelable(false);
                            alert.setCanceledOnTouchOutside(false);
                            alert.show();
                        }
                        else{
                            previousTabPosition = tab.getPosition();

                            resetAllData();

                            addAppointment.setVisibility(View.VISIBLE);
                            addScheduleButton.setVisibility(View.VISIBLE);
                            cancelAppointment.setVisibility(View.GONE);
                            addAppointmentLay.setVisibility(View.VISIBLE);
                            cancelAppointmentLay.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        allAppointment.setOnClickListener(v -> {
            resetAllData();
            Intent intent = new Intent(AppointmentModify.this, AllAppointment.class);
            intent.putExtra("ADMIN_USER_FLAG","2");
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());

        searchPatient.setOnClickListener(v -> {
            SearchPatientAppDialog searchPatientAppDialog = new SearchPatientAppDialog(AppointmentModify.this);
            searchPatientAppDialog.show(getSupportFragmentManager(),"PAT_SEARCH");
        });

        searchPatientCanc.setOnClickListener(v -> {
            SearchPatientCancelAppDialog searchPatientCancelAppDialog = new SearchPatientCancelAppDialog(AppointmentModify.this);
            searchPatientCancelAppDialog.show(getSupportFragmentManager(),"PAT_CANCEL");
        });

        selectDoctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    selectDoctorLay.setHint("Doctor");
                    docSelectErrMsg.setVisibility(View.GONE);
                }
                else {
                    selectDoctorLay.setHint("Select Doctor");
                    String dt = "Please Select Doctor";
                    docSelectErrMsg.setText(dt);
                    docSelectErrMsg.setVisibility(View.VISIBLE);
                }
            }
        });

        selectDoctor.setOnClickListener(v -> {
            SearchDoctorAppDialog searchDoctorAppDialog = new SearchDoctorAppDialog(AppointmentModify.this, doctorForAppLists);
            searchDoctorAppDialog.show(getSupportFragmentManager(),"DOC_SEARCH");
        });

        selectDoctorCanc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    selectDoctorCancLay.setHint("Doctor");
                    docSelectCancErrMsg.setVisibility(View.GONE);
                }
                else {
                    selectDoctorCancLay.setHint("Select Doctor");
                    String dt = "Please Select Doctor";
                    docSelectCancErrMsg.setText(dt);
                    docSelectCancErrMsg.setVisibility(View.VISIBLE);
                }
            }
        });

        selectDoctorCanc.setOnClickListener(v -> {
            SearchDoctorCancelAppDialog searchDoctorCancelAppDialog = new SearchDoctorCancelAppDialog(AppointmentModify.this, doctorForAppLists);
            searchDoctorCancelAppDialog.show(getSupportFragmentManager(),"DOC_CANCEL");
        });

        selectPayment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    selectPaymentLay.setHint("Payment Code");
                    paymentSelectErrMsg.setVisibility(View.GONE);
                }
                else {
                    selectPaymentLay.setHint("Select Payment Code");
                    String pt = "Please Select Payment Code";
                    paymentSelectErrMsg.setText(pt);
                    paymentSelectErrMsg.setVisibility(View.VISIBLE);
                }
            }
        });

        selectPayment.setOnClickListener(v -> {
            SearchPaymentAppDialog searchPaymentAppDialog = new SearchPaymentAppDialog(AppointmentModify.this, paymentForAppLists);
            searchPaymentAppDialog.show(getSupportFragmentManager(),"PAYMENT_SEARCH");
        });

        selectService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    selectServiceLay.setHint("Service");
                    serviceSelectErrMsg.setVisibility(View.GONE);
                }
                else {
                    selectServiceLay.setHint("Select Service");
                    String st = "Please Select Service";
                    serviceSelectErrMsg.setText(st);
                    serviceSelectErrMsg.setVisibility(View.VISIBLE);
                }
            }
        });

        selectService.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            if (!name.equals(selected_service_name)) {
                for (int i = 0; i <serviceForAppLists.size(); i++) {
                    if (name.equals(serviceForAppLists.get(i).getPfn_fee_name())) {
                        selected_prd_id = serviceForAppLists.get(i).getPrd_id();
                        selected_pfn_id = serviceForAppLists.get(i).getPfn_id();
                        selected_service_qty = serviceForAppLists.get(i).getService_qty();
                        selected_service_name = serviceForAppLists.get(i).getPfn_fee_name();
                        break;
                    }
                    else {
                        selected_prd_id = "";
                        selected_pfn_id = "";
                        selected_service_qty = "";
                        selected_service_name = "";
                    }
                }
                serviceSelectErrMsg.setVisibility(View.GONE);
                selectServiceLay.setHint("Service");

                serviceQty.setText(selected_service_qty);

                appointmentDateTimeLists = new ArrayList<>();
                resetAppTimeSchView();
            }
        });

        selectServiceCanc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    selectServiceCancLay.setHint("Service");
                    serviceSelectCancErrMsg.setVisibility(View.GONE);
                }
                else {
                    selectServiceCancLay.setHint("Select Service");
                    String st = "Please Select Service";
                    serviceSelectCancErrMsg.setText(st);
                    serviceSelectCancErrMsg.setVisibility(View.VISIBLE);
                }
            }
        });

        selectServiceCanc.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            if (!name.equals(selected_service_name)) {
                for (int i = 0; i <serviceForAppLists.size(); i++) {
                    if (name.equals(serviceForAppLists.get(i).getPfn_fee_name())) {
                        selected_prd_id = serviceForAppLists.get(i).getPrd_id();
                        selected_pfn_id = serviceForAppLists.get(i).getPfn_id();
                        selected_service_qty = serviceForAppLists.get(i).getService_qty();
                        selected_service_name = serviceForAppLists.get(i).getPfn_fee_name();
                        break;
                    }
                    else {
                        selected_prd_id = "";
                        selected_pfn_id = "";
                        selected_service_qty = "";
                        selected_service_name = "";
                    }
                }
                serviceSelectCancErrMsg.setVisibility(View.GONE);
                selectServiceCancLay.setHint("Service");

                selected_adm_date = "";
                adm_date_position = "";
                selected_ad_id = "";

                selectAppDateCancLay.setEnabled(false);
                selectAppDateCancLay.setHint("Select Appointment Date");
                selectAppDateCanc.setText("");

                appTimeCardViewLay.setVisibility(View.GONE);

                appDateSelectCancErrMsg.setVisibility(View.GONE);

                appTimeLists = new ArrayList<>();
                appointDateTimeCancelLists = new ArrayList<>();
                resetAppTimeForCancelSchView();
                getAppdateTime();
            }
        });

        selectAppDateCanc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    selectAppDateCancLay.setHint("Appointment Date");
                    appDateSelectCancErrMsg.setVisibility(View.GONE);
                }
                else {
                    selectAppDateCancLay.setHint("Select Appointment Date");
                    String dt = "Please Select Appointment Date";
                    appDateSelectCancErrMsg.setText(dt);
                    appDateSelectCancErrMsg.setVisibility(View.VISIBLE);
                }
            }
        });

        selectAppDateCanc.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            if (adm_date_position.isEmpty()) {
                adm_date_position = String.valueOf(position);
                appTimeLists = new ArrayList<>();
                ArrayList<AppointTimeCancelList> timeCancelLists;
//                for (int i = 0; i <appointDateTimeCancelLists.size(); i++) {
//                    if (name.equals(appointDateTimeCancelLists.get(i).getApp_date())) {
//
//
//                        System.out.println(selected_ad_id);
//                        break;
//                    }
//                    else {
//                        selected_ad_id = "";
//                        selected_adm_date = "";
//                        adm_date_position = "";
//                    }
//                }
                selected_ad_id = appointDateTimeCancelLists.get(position).getAd_id();
                selected_adm_date = appointDateTimeCancelLists.get(position).getApp_date();
                timeCancelLists = appointDateTimeCancelLists.get(position).getAppointTimeCancelLists();

                appDateSelectCancErrMsg.setVisibility(View.GONE);
                selectAppDateCancLay.setHint("Appointment Date");

                for (int i = 0; i < timeCancelLists.size(); i++) {
                    appTimeLists.add(timeCancelLists.get(i).getSchedule());
//                    System.out.println(timeCancelLists.get(i).getSchedule());
                }
                appTimeCardViewLay.setVisibility(View.VISIBLE);
                resetAppTimeForCancelSchView();
//                if (!name.equals(selected_adm_date)) {
//
//                }
            }
            else {
                if (position != Integer.parseInt(adm_date_position)) {
                    adm_date_position = String.valueOf(position);
                    appTimeLists = new ArrayList<>();
                    ArrayList<AppointTimeCancelList> timeCancelLists;
//                    for (int i = 0; i <appointDateTimeCancelLists.size(); i++) {
//                        if (name.equals(appointDateTimeCancelLists.get(i).getApp_date())) {
//                            selected_ad_id = appointDateTimeCancelLists.get(i).getAd_id();
//                            selected_adm_date = appointDateTimeCancelLists.get(i).getApp_date();
//                            timeCancelLists = appointDateTimeCancelLists.get(i).getAppointTimeCancelLists();
//                            System.out.println(selected_ad_id);
//                            break;
//                        }
//                        else {
//                            selected_ad_id = "";
//                            selected_adm_date = "";
//                            adm_date_position = "";
//                        }
//                    }
                    selected_ad_id = appointDateTimeCancelLists.get(position).getAd_id();
                    selected_adm_date = appointDateTimeCancelLists.get(position).getApp_date();
                    timeCancelLists = appointDateTimeCancelLists.get(position).getAppointTimeCancelLists();

                    appDateSelectCancErrMsg.setVisibility(View.GONE);
                    selectAppDateCancLay.setHint("Appointment Date");

                    for (int i = 0; i < timeCancelLists.size(); i++) {
                        appTimeLists.add(timeCancelLists.get(i).getSchedule());
//                        System.out.println(timeCancelLists.get(i).getSchedule());
                    }
                    appTimeCardViewLay.setVisibility(View.VISIBLE);
                    resetAppTimeForCancelSchView();
                }
            }

        });

        addScheduleButton.setOnClickListener(v -> {
            if (selected_doc_id.isEmpty() || selected_prd_id.isEmpty()) {
                Toast.makeText(this, "Please Select Doctor & Service First", Toast.LENGTH_SHORT).show();
            }
            else {
                if (!selected_service_qty.isEmpty()) {

                    Intent intent = new Intent(AppointmentModify.this, AddSchedule.class);
                    intent.putExtra("P_DOC_ID",selected_doc_id);
                    intent.putExtra("P_PFN_ID",selected_pfn_id);
                    intent.putExtra("P_DEPTS_ID",selected_depts_id);
                    intent.putExtra("P_PH_ID",selected_pat_ph_id);
                    intent.putExtra("P_PRM_ID",selected_prm_id);
                    intent.putExtra("P_PRD_ID",selected_prd_id);
                    intent.putExtra("P_UPDATE",false);

                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "Service Quantity is Empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

        addAppointment.setOnClickListener(v -> {
            if (!selected_pat_ph_id.isEmpty()) {
                if (!selected_doc_id.isEmpty()) {
                    if (!selected_prm_id.isEmpty()) {
                        if (!selected_pfn_id.isEmpty()) {
                            if (!appointmentDateTimeLists.isEmpty()) {
                                insertAppointment();
                            }
                            else {
                                Toast.makeText(this, "Please Select Appointment Time to Save.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            String st = "Please Select Service";
                            serviceSelectErrMsg.setText(st);
                            serviceSelectErrMsg.setVisibility(View.VISIBLE);
                            Toast.makeText(this, "Please Select Service", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        String pt = "Please Select Payment Code";
                        paymentSelectErrMsg.setText(pt);
                        paymentSelectErrMsg.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Please Select Payment Code", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    String dct = "Please Select Doctor";
                    docSelectErrMsg.setText(dct);
                    docSelectErrMsg.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Please Select Doctor", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                patSelectErrorMsg.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Please Select Patient", Toast.LENGTH_SHORT).show();
            }

        });

        cancelAppointment.setOnClickListener(v -> {
            if (!selected_pat_ph_id.isEmpty()) {
                if (!selected_doc_id.isEmpty()) {
                    if (!selected_pfn_id.isEmpty()) {
                        if (!selected_ad_id.isEmpty()) {
                            if (!appTimeLists.isEmpty()) {
                                updateAppointmentToCancel();
                            }
                            else {
                                Toast.makeText(this, "No Time Schedule Found for this Date.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            String act = "Please Select Appointment Date";
                            appDateSelectCancErrMsg.setText(act);
                            appDateSelectCancErrMsg.setVisibility(View.VISIBLE);
                            Toast.makeText(this, "Please Select Appointment Date", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        String st = "Please Select Service";
                        serviceSelectCancErrMsg.setText(st);
                        serviceSelectCancErrMsg.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Please Select Service", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    String dct = "Please Select Doctor";
                    docSelectCancErrMsg.setText(dct);
                    docSelectCancErrMsg.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Please Select Doctor", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                patSelectCancErrorMsg.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Please Select Patient", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onResume() {

        if (scheduleSelected) {
            System.out.println("Schedule Added");
            scheduleSelected = false;
            serviceQty.setText(selected_service_qty);
            resetAppTimeSchView();
        }
        super.onResume();
    }

    public void resetAllData() {
        doctorForAppLists = new ArrayList<>();
        paymentForAppLists = new ArrayList<>();
        serviceForAppLists = new ArrayList<>();
        selected_patient_name = "";
        selected_pat_ph_id = "";
        selected_pat_id = "";
        selected_pat_cat_id = "";
        selected_doc_id = "";
        selected_doc_name = "";
        selected_depts_id = "";
        selected_depts_name = "";
        selected_deptd_id = "";
        selected_desig_id = "";
        selected_prm_id = "";
        selected_prm_code = "";
        selected_prd_id = "";
        selected_pfn_id = "";
        selected_service_qty = "";
        selected_service_name = "";
        selected_adm_date = "";
        adm_date_position = "";
        selected_ad_id = "";
        scheduleSelected = false;
        appointmentDateTimeLists = new ArrayList<>();
        appTimeLists = new ArrayList<>();
        appointDateTimeCancelLists = new ArrayList<>();

        searchPatientLay.setHint("Select Patient");
        searchPatient.setText("");

        searchPatientCancLay.setHint("Select Patient");
        searchPatientCanc.setText("");

        selectDoctorLay.setEnabled(false);
        selectDoctorLay.setHint("Select Doctor");
        selectDoctor.setText("");

        selectDoctorCancLay.setEnabled(false);
        selectDoctorCancLay.setHint("Select Doctor");
        selectDoctorCanc.setText("");

        unitSelect.setText("");

        unitSelectCanc.setText("");

        selectPaymentLay.setEnabled(false);
        selectPaymentLay.setHint("Select Payment");
        selectPayment.setText("");

        selectServiceLay.setEnabled(false);
        selectServiceLay.setHint("Select Service");
        selectService.setText("");
        serviceQty.setText("");

        selectServiceCancLay.setEnabled(false);
        selectServiceCancLay.setHint("Select Service");
        selectServiceCanc.setText("");

        selectAppDateCancLay.setEnabled(false);
        selectAppDateCancLay.setHint("Select Appointment Date");
        selectAppDateCanc.setText("");

        appTimeCardViewLay.setVisibility(View.GONE);

        resetAppTimeSchView();
        resetAppTimeForCancelSchView();

        patSelectErrorMsg.setVisibility(View.GONE);
        docSelectErrMsg.setVisibility(View.GONE);
        paymentSelectErrMsg.setVisibility(View.GONE);
        serviceSelectErrMsg.setVisibility(View.GONE);

        patSelectCancErrorMsg.setVisibility(View.GONE);
        docSelectCancErrMsg.setVisibility(View.GONE);
        serviceSelectCancErrMsg.setVisibility(View.GONE);
        appDateSelectCancErrMsg.setVisibility(View.GONE);
    }

    public void resetAppTimeSchView() {
        savedAppointmentDateTimeAdapter = new SavedAppointmentDateTimeAdapter(appointmentDateTimeLists,AppointmentModify.this);
        appointDateTimeView.setAdapter(savedAppointmentDateTimeAdapter);
    }

    public void resetAppTimeForCancelSchView() {
        takenScheduleAdapter = new TakenScheduleAdapter(appTimeLists,AppointmentModify.this);
        appTimeView.setAdapter(takenScheduleAdapter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

    private void closeKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

//    @Override
//    public void onBackPressed() {
//
//    }

    @Override
    public void onPatientSelection() {
        searchPatientLay.setHint("Patient");
        searchPatient.setText(selected_patient_name);

        unitSelect.setText("");

        selectPaymentLay.setEnabled(false);
        selectPaymentLay.setHint("Select Payment");
        selectPayment.setText("");

        selectServiceLay.setEnabled(false);
        selectServiceLay.setHint("Select Service");
        selectService.setText("");

        serviceQty.setText("");

        patSelectErrorMsg.setVisibility(View.GONE);
        docSelectErrMsg.setVisibility(View.GONE);
        paymentSelectErrMsg.setVisibility(View.GONE);
        serviceSelectErrMsg.setVisibility(View.GONE);

        selected_doc_id = "";
        selected_doc_name = "";
        selected_depts_id = "";
        selected_depts_name = "";
        selected_deptd_id = "";
        selected_desig_id = "";
        selected_prm_id = "";
        selected_prm_code = "";
        selected_prd_id = "";
        selected_pfn_id = "";
        selected_service_qty = "";
        selected_service_name = "";
        appointmentDateTimeLists = new ArrayList<>();
        resetAppTimeSchView();
        getDoctorData();
    }

    @Override
    public void onPatSelect() {
        searchPatientCancLay.setHint("Patient");
        searchPatientCanc.setText(selected_patient_name);

        unitSelectCanc.setText("");

        selectServiceCancLay.setEnabled(false);
        selectServiceCancLay.setHint("Select Service");
        selectServiceCanc.setText("");

        selectAppDateCancLay.setEnabled(false);
        selectAppDateCancLay.setHint("Select Appointment Date");
        selectAppDateCanc.setText("");

        appTimeCardViewLay.setVisibility(View.GONE);

        patSelectCancErrorMsg.setVisibility(View.GONE);
        docSelectCancErrMsg.setVisibility(View.GONE);
        serviceSelectCancErrMsg.setVisibility(View.GONE);
        appDateSelectCancErrMsg.setVisibility(View.GONE);

        selected_doc_id = "";
        selected_doc_name = "";
        selected_depts_id = "";
        selected_depts_name = "";
        selected_deptd_id = "";
        selected_desig_id = "";
        selected_prm_id = "";
        selected_prm_code = "";
        selected_prd_id = "";
        selected_pfn_id = "";
        selected_service_qty = "";
        selected_service_name = "";
        selected_adm_date = "";
        adm_date_position = "";
        selected_ad_id = "";
        appTimeLists = new ArrayList<>();
        appointDateTimeCancelLists = new ArrayList<>();
        resetAppTimeForCancelSchView();
        getDocDataForCancel();
    }

    @Override
    public void onDoctorSelect() {
        selected_prm_id = "";
        selected_prm_code = "";
        selected_prd_id = "";
        selected_pfn_id = "";
        selected_service_qty = "";
        selected_service_name = "";

        selectDoctor.setText(selected_doc_name);
        docSelectErrMsg.setVisibility(View.GONE);
        unitSelect.setText(selected_depts_name);
        selectDoctorLay.setHint("Doctor");

        selectPaymentLay.setEnabled(false);
        selectPaymentLay.setHint("Select Payment Code");
        selectPayment.setText("");

        selectServiceLay.setEnabled(false);
        selectServiceLay.setHint("Select Service");
        selectService.setText("");

        serviceQty.setText("");

        docSelectErrMsg.setVisibility(View.GONE);
        paymentSelectErrMsg.setVisibility(View.GONE);
        serviceSelectErrMsg.setVisibility(View.GONE);

        appointmentDateTimeLists = new ArrayList<>();
        resetAppTimeSchView();

        getPaymentData();
    }

    @Override
    public void onDocSelect() {
        selected_pfn_id = "";
        selected_service_name = "";
        selected_adm_date = "";
        adm_date_position = "";
        selected_ad_id = "";

        selectDoctorCanc.setText(selected_doc_name);
        docSelectCancErrMsg.setVisibility(View.GONE);
        unitSelectCanc.setText(selected_depts_name);
        selectDoctorCancLay.setHint("Doctor");

        selectAppDateCancLay.setEnabled(false);
        selectAppDateCancLay.setHint("Select Appointment Date");
        selectAppDateCanc.setText("");

        appTimeCardViewLay.setVisibility(View.GONE);

        docSelectCancErrMsg.setVisibility(View.GONE);
        serviceSelectCancErrMsg.setVisibility(View.GONE);
        appDateSelectCancErrMsg.setVisibility(View.GONE);

        appTimeLists = new ArrayList<>();
        appointDateTimeCancelLists = new ArrayList<>();
        resetAppTimeForCancelSchView();

        getServiceCanData();
    }

    @Override
    public void onPaymentSelect() {
        selected_prd_id = "";
        selected_pfn_id = "";
        selected_service_qty = "";
        selected_service_name = "";

        selectPayment.setText(selected_prm_code);
        paymentSelectErrMsg.setVisibility(View.GONE);
        selectPaymentLay.setHint("Payment Code");

        selectServiceLay.setEnabled(false);
        selectServiceLay.setHint("Select Service");
        selectService.setText("");

        serviceQty.setText("");

        appointmentDateTimeLists = new ArrayList<>();
        resetAppTimeSchView();

        getServiceData();
    }

    public void getDoctorData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        doctorForAppLists = new ArrayList<>();

        String doctorUrl = pre_url_api+"appointmentModify/getDoctorList?ph_id="+selected_pat_ph_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest doctorReq = new StringRequest(Request.Method.GET, doctorUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String doc_id = info.getString("doc_id")
                                .equals("null") ? "" : info.getString("doc_id");
                        String doc_name = info.getString("doc_name")
                                .equals("null") ? "" : info.getString("doc_name");
                        String doc_code = info.getString("doc_code")
                                .equals("null") ? "" : info.getString("doc_code");
                        String depts_id = info.getString("depts_id")
                                .equals("null") ? "" : info.getString("depts_id");
                        String depts_name = info.getString("depts_name")
                                .equals("null") ? "" : info.getString("depts_name");
                        String deptd_id = info.getString("deptd_id")
                                .equals("null") ? "" : info.getString("deptd_id");
                        String deptd_code = info.getString("deptd_code")
                                .equals("null") ? "" : info.getString("deptd_code");
                        String deptd_name = info.getString("deptd_name")
                                .equals("null") ? "" : info.getString("deptd_name");
                        String desig_id = info.getString("desig_id")
                                .equals("null") ? "" : info.getString("desig_id");
                        String desig_name = info.getString("desig_name")
                                .equals("null") ? "" : info.getString("desig_name");

                        doctorForAppLists.add(new DoctorForAppList(doc_id,doc_name,doc_code,depts_id,
                                depts_name,deptd_id,deptd_code,deptd_name,desig_id,desig_name));
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

        doctorReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(doctorReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                conn = false;
                connected = false;

                selectDoctorLay.setEnabled(true);
                selectDoctorLay.setHint("Select Doctor");
                selectDoctor.setText("");

                docSelectErrMsg.setVisibility(View.GONE);
                paymentSelectErrMsg.setVisibility(View.GONE);
                serviceSelectErrMsg.setVisibility(View.GONE);

                if (doctorForAppLists.isEmpty()) {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);

                    selectDoctorLay.setEnabled(false);
                    String ndf = "No Doctor found for this Patient";
                    docSelectErrMsg.setText(ndf);
                    docSelectErrMsg.setVisibility(View.VISIBLE);
                }
                else if (doctorForAppLists.size() == 1) {

                    fullLayout.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.VISIBLE);

                    selected_doc_id = doctorForAppLists.get(0).getDoc_id();
                    selected_doc_name = doctorForAppLists.get(0).getDoc_name();
                    selected_depts_id = doctorForAppLists.get(0).getDepts_id();
                    selected_depts_name = doctorForAppLists.get(0).getDepts_name();
                    selected_deptd_id = doctorForAppLists.get(0).getDeptd_id();
                    selected_desig_id = doctorForAppLists.get(0).getDesig_id();

                    unitSelect.setText(selected_depts_name);

                    selectDoctorLay.setEnabled(true);
                    selectDoctorLay.setHint("Doctor");
                    selectDoctor.setText(selected_doc_name);
                    docSelectErrMsg.setVisibility(View.GONE);

                    getPaymentData();
                }
                else {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);

                    selectDoctorLay.setEnabled(true);
                    docSelectErrMsg.setVisibility(View.GONE);
                }
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getDoctorData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    String dem = "Doctor List Failed to load. Please Try again";
                    docSelectErrMsg.setText(dem);
                    docSelectErrMsg.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getPaymentData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        paymentForAppLists = new ArrayList<>();

        String paymentUrl = pre_url_api+"appointmentModify/getPaymentForAddAppList?ph_id="+selected_pat_ph_id+"&depts_id="+selected_depts_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest paymentReq = new StringRequest(Request.Method.GET, paymentUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String prm_id = info.getString("prm_id")
                                .equals("null") ? "" : info.getString("prm_id");
                        String prm_code = info.getString("prm_code")
                                .equals("null") ? "" : info.getString("prm_code");
                        String prm_date = info.getString("prm_date")
                                .equals("null") ? "" : info.getString("prm_date");

                        paymentForAppLists.add(new PaymentForAppList(prm_id,prm_code,prm_date));
                    }
                }

                connected = true;
                updatePaySpinner();
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updatePaySpinner();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updatePaySpinner();
        });

        paymentReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(paymentReq);
    }

    private void updatePaySpinner() {
        loading = false;
        if(conn) {
            if (connected) {
                conn = false;
                connected = false;

                selectPaymentLay.setEnabled(true);
                selectPaymentLay.setHint("Select Payment Code");
                selectPayment.setText("");

                selectServiceLay.setEnabled(false);
                selectServiceLay.setHint("Select Service");
                selectService.setText("");

                serviceQty.setText("");

                paymentSelectErrMsg.setVisibility(View.GONE);
                serviceSelectErrMsg.setVisibility(View.GONE);

                if (paymentForAppLists.isEmpty()) {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);

                    selectPaymentLay.setEnabled(false);
                    String npf = "No Payment List found for this Patient";
                    paymentSelectErrMsg.setText(npf);
                    paymentSelectErrMsg.setVisibility(View.VISIBLE);
                }
                else if (paymentForAppLists.size() == 1) {
                    fullLayout.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.VISIBLE);

                    selected_prm_id = paymentForAppLists.get(0).getPrm_id();
                    selected_prm_code = paymentForAppLists.get(0).getPrm_code();

                    selectPaymentLay.setEnabled(true);
                    selectPaymentLay.setHint("Payment Code");
                    selectPayment.setText(selected_prm_code);
                    paymentSelectErrMsg.setVisibility(View.GONE);

                    getServiceData();
                }
                else {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);

                    selectPaymentLay.setEnabled(true);
                    paymentSelectErrMsg.setVisibility(View.GONE);
                }
            }
            else {
                alertMessagePay();
            }
        }
        else {
            alertMessagePay();
        }
    }

    public void alertMessagePay() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getPaymentData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    String psem = "Payment List Failed to load. Please Try again";
                    paymentSelectErrMsg.setText(psem);
                    paymentSelectErrMsg.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getServiceData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        serviceForAppLists = new ArrayList<>();

        String serviceUrl = pre_url_api+"appointmentModify/getServiceForAddAppList?ph_id="+selected_pat_ph_id+"&depts_id="+selected_depts_id+"&prm_id="+selected_prm_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest serviceReq = new StringRequest(Request.Method.GET, serviceUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String prd_id = info.getString("prd_id")
                                .equals("null") ? "" : info.getString("prd_id");
                        String pfn_id = info.getString("pfn_id")
                                .equals("null") ? "" : info.getString("pfn_id");
                        String pfn_fee_name = info.getString("pfn_fee_name")
                                .equals("null") ? "" : info.getString("pfn_fee_name");
                        String rest = info.getString("rest")
                                .equals("null") ? "0" : info.getString("rest");

                        serviceForAppLists.add(new ServiceForAppList(prd_id,pfn_id,pfn_fee_name,rest));
                    }
                }

                connected = true;
                updateServiceSpinner();
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateServiceSpinner();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateServiceSpinner();
        });

        serviceReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(serviceReq);
    }

    private void updateServiceSpinner() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                selectServiceLay.setEnabled(true);
                selectServiceLay.setHint("Select Service");
                selectService.setText("");

                serviceQty.setText("");

                serviceSelectErrMsg.setVisibility(View.GONE);

                if (serviceForAppLists.isEmpty()) {
                    selectServiceLay.setEnabled(false);
                    String sem = "No Service List found for this Patient";
                    serviceSelectErrMsg.setText(sem);
                    serviceSelectErrMsg.setVisibility(View.VISIBLE);
                }
                else if (serviceForAppLists.size() == 1) {
                    selected_prd_id = serviceForAppLists.get(0).getPrd_id();
                    selected_pfn_id = serviceForAppLists.get(0).getPfn_id();
                    selected_service_name = serviceForAppLists.get(0).getPfn_fee_name();
                    selected_service_qty = serviceForAppLists.get(0).getService_qty();

                    selectServiceLay.setEnabled(true);
                    selectServiceLay.setHint("Service");
                    selectService.setText(selected_service_name);
                    serviceSelectErrMsg.setVisibility(View.GONE);
                    serviceQty.setText(selected_service_qty);
                }
                else {
                    selectServiceLay.setEnabled(true);
                    serviceSelectErrMsg.setVisibility(View.GONE);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < serviceForAppLists.size(); i++) {
                    type.add(serviceForAppLists.get(i).getPfn_fee_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AppointmentModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                selectService.setAdapter(arrayAdapter);
            }
            else {
                alertMessageService();
            }
        }
        else {
            alertMessageService();
        }
    }

    public void alertMessageService() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getServiceData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    String sem = "Service List Failed to load. Please Try again";
                    serviceSelectErrMsg.setText(sem);
                    serviceSelectErrMsg.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void insertAppointment() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        parsing_message = "";

        checkToInsertAppointment();


    }

    public void checkToInsertAppointment() {
        boolean allUpdated = false;
        for (int x = 0; x < appointmentDateTimeLists.size(); x++) {
            allUpdated = appointmentDateTimeLists.get(x).isInserted();
            if (!appointmentDateTimeLists.get(x).isInserted()) {
                insertData(x);
                break;
            }
        }

        if (allUpdated) {
            conn = true;
            connected = true;
            updateAfterAppInsert();
        }
    }

    public void insertData(int index) {
        String appointmentUrl = pre_url_api+"appointmentModify/insertAppointment";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest insertAppReq = new StringRequest(Request.Method.POST, appointmentUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                String p_sch_msg = jsonObject.getString("p_sch_msg");
                parsing_message = string_out;

                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    appointmentDateTimeLists.get(index).setInserted(true);
                    if (p_sch_msg.equals("false")) {
                        appointmentDateTimeLists.get(index).setSchDuplicate(false);
                        checkToInsertAppointment();
                    }
                    else {
                        appointmentDateTimeLists.get(index).setSchDuplicate(true);
                        checkToInsertAppointment();
                    }
                }
                else {
                    connected = false;
                    updateAfterAppInsert();
                }

            } catch (JSONException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateAfterAppInsert();
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateAfterAppInsert();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_DOC_ID",selected_doc_id);
                headers.put("P_DEPTS_ID",selected_depts_id);
                headers.put("P_ADM_DATE",appointmentDateTimeLists.get(index).getAdm_date());
                headers.put("P_TS_ID",appointmentDateTimeLists.get(index).getTs_id());
                headers.put("P_DESIG_ID", selected_desig_id);
                headers.put("P_ADM_ID", appointmentDateTimeLists.get(index).getAdm_id());
                headers.put("P_PH_ID", selected_pat_ph_id);
                headers.put("P_PAT_CAT_ID", selected_pat_cat_id);
                headers.put("P_PRM_ID", selected_prm_id);
                headers.put("P_PFN_ID", selected_pfn_id);
                headers.put("P_PRD_ID", selected_prd_id);
                headers.put("P_USER", adminInfoLists.get(0).getUsr_name());
                return headers;
            }
        };

        insertAppReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(insertAppReq);
    }

    private void updateAfterAppInsert() {
        loading = false;
        if (conn) {
            if (connected) {
                circularProgressIndicator.setVisibility(View.GONE);
                fullLayout.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                int dup_count = 0;
                for (int i = 0; i < appointmentDateTimeLists.size(); i++) {
                    if (appointmentDateTimeLists.get(i).isSchDuplicate()) {
                        dup_count++;
                    }
                }
                if (dup_count > 0) {
                    addScheduleButton.setVisibility(View.GONE);
                    resetAppTimeSchView();
                    if (appointmentDateTimeLists.size() == dup_count) {
                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
                        alertDialogBuilder.setTitle("Success!")
                                .setMessage("Red Marked Appointment Time has already been taken. Please Reselect Time or Date.")
                                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = alertDialogBuilder.create();
                        alert.setCancelable(false);
                        alert.setCanceledOnTouchOutside(false);
                        alert.show();
                    }
                    else {
                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
                        alertDialogBuilder.setTitle("Success!")
                                .setMessage("Red Marked Appointment Time has already been taken. Please Reselect Time or Date. Green Marked Appointment Time added successfully.")
                                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = alertDialogBuilder.create();
                        alert.setCancelable(false);
                        alert.setCanceledOnTouchOutside(false);
                        alert.show();
                    }
                }
                else {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
                    alertDialogBuilder.setTitle("Success!")
                            .setMessage("Appointment saved successfully.")
                            .setPositiveButton("Ok", (dialog, which) -> {
                                dialog.dismiss();
                                addScheduleButton.setVisibility(View.VISIBLE);
                                resetAllData();
                            });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.setCancelable(false);
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();
                }
            }
            else {
                alertMessageInApp();
            }
        }
        else {
            alertMessageInApp();
        }
    }

    public void alertMessageInApp() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected. Please retry or data will be lost.";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected. Please retry or data will be lost.";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please retry or data will be lost..")
                .setPositiveButton("Retry", (dialog, which) -> {
                    insertAppointment();
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    // Cancel Appointment Data
    public void getDocDataForCancel() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        doctorForAppLists = new ArrayList<>();

        String doctorUrl = pre_url_api+"appointmentModify/getDoctorListForCancel?ph_id="+selected_pat_ph_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest doctorReq = new StringRequest(Request.Method.GET, doctorUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String doc_id = info.getString("doc_id")
                                .equals("null") ? "" : info.getString("doc_id");
                        String doc_name = info.getString("doc_name")
                                .equals("null") ? "" : info.getString("doc_name");
                        String doc_code = info.getString("doc_code")
                                .equals("null") ? "" : info.getString("doc_code");
                        String depts_id = info.getString("depts_id")
                                .equals("null") ? "" : info.getString("depts_id");
                        String depts_name = info.getString("depts_name")
                                .equals("null") ? "" : info.getString("depts_name");
                        String deptd_id = info.getString("deptd_id")
                                .equals("null") ? "" : info.getString("deptd_id");
                        String deptd_code = info.getString("deptd_code")
                                .equals("null") ? "" : info.getString("deptd_code");
                        String deptd_name = info.getString("deptd_name")
                                .equals("null") ? "" : info.getString("deptd_name");
                        String desig_id = info.getString("desig_id")
                                .equals("null") ? "" : info.getString("desig_id");
                        String desig_name = info.getString("desig_name")
                                .equals("null") ? "" : info.getString("desig_name");

                        doctorForAppLists.add(new DoctorForAppList(doc_id,doc_name,doc_code,depts_id,
                                depts_name,deptd_id,deptd_code,deptd_name,desig_id,desig_name));
                    }
                }

                connected = true;
                updateDocCancelInterface();
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDocCancelInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDocCancelInterface();
        });

        doctorReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(doctorReq);
    }

    private void updateDocCancelInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                conn = false;
                connected = false;

                selectDoctorCancLay.setEnabled(true);
                selectDoctorCancLay.setHint("Select Doctor");
                selectDoctorCanc.setText("");

                docSelectCancErrMsg.setVisibility(View.GONE);
                serviceSelectCancErrMsg.setVisibility(View.GONE);
                appDateSelectCancErrMsg.setVisibility(View.GONE);

                appTimeCardViewLay.setVisibility(View.GONE);

                if (doctorForAppLists.isEmpty()) {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);

                    selectDoctorCancLay.setEnabled(false);
                    String dem = "No Doctor found for this Patient";
                    docSelectCancErrMsg.setText(dem);
                    docSelectCancErrMsg.setVisibility(View.VISIBLE);
                }
                else if (doctorForAppLists.size() == 1) {
                    fullLayout.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.VISIBLE);

                    selected_doc_id = doctorForAppLists.get(0).getDoc_id();
                    selected_doc_name = doctorForAppLists.get(0).getDoc_name();
                    selected_depts_id = doctorForAppLists.get(0).getDepts_id();
                    selected_depts_name = doctorForAppLists.get(0).getDepts_name();
                    selected_deptd_id = doctorForAppLists.get(0).getDeptd_id();
                    selected_desig_id = doctorForAppLists.get(0).getDesig_id();

                    unitSelectCanc.setText(selected_depts_name);

                    selectDoctorCancLay.setEnabled(true);
                    selectDoctorCancLay.setHint("Doctor");
                    selectDoctorCanc.setText(selected_doc_name);
                    docSelectCancErrMsg.setVisibility(View.GONE);

                    getServiceCanData();
                }
                else {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);

                    selectDoctorCancLay.setEnabled(true);
                    docSelectCancErrMsg.setVisibility(View.GONE);
                }
            }
            else {
                alertDocCancelMessage();
            }
        }
        else {
            alertDocCancelMessage();
        }
    }

    public void alertDocCancelMessage() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getDocDataForCancel();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    String dem = "Doctor List Failed to load. Please Try again";
                    docSelectCancErrMsg.setText(dem);
                    docSelectCancErrMsg.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void  getServiceCanData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        serviceForAppLists = new ArrayList<>();

        String serviceUrl = pre_url_api+"appointmentModify/getServiceForCancAppList?ph_id="+selected_pat_ph_id+"&depts_id="+selected_depts_id+"&doc_id="+selected_doc_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest serviceReq = new StringRequest(Request.Method.GET, serviceUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pfn_id = info.getString("pfn_id")
                                .equals("null") ? "" : info.getString("pfn_id");
                        String pfn_fee_name = info.getString("pfn_fee_name")
                                .equals("null") ? "" : info.getString("pfn_fee_name");

                        serviceForAppLists.add(new ServiceForAppList("",pfn_id,pfn_fee_name,"0"));
                    }
                }

                connected = true;
                updateServiceCancSpinner();
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateServiceCancSpinner();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateServiceCancSpinner();
        });

        serviceReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(serviceReq);
    }

    private void updateServiceCancSpinner() {
        loading = false;
        if(conn) {
            if (connected) {
                conn = false;
                connected = false;

                selectServiceCancLay.setEnabled(true);
                selectServiceCancLay.setHint("Select Service");
                selectServiceCanc.setText("");

                serviceSelectCancErrMsg.setVisibility(View.GONE);
                appDateSelectCancErrMsg.setVisibility(View.GONE);

                appTimeCardViewLay.setVisibility(View.GONE);

                if (serviceForAppLists.isEmpty()) {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);

                    selectServiceCancLay.setEnabled(false);
                    String sem = "No Service List found for this Patient";
                    serviceSelectCancErrMsg.setText(sem);
                    serviceSelectCancErrMsg.setVisibility(View.VISIBLE);
                }
                else if (serviceForAppLists.size() == 1) {
                    fullLayout.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.VISIBLE);

                    selected_prd_id = serviceForAppLists.get(0).getPrd_id();
                    selected_pfn_id = serviceForAppLists.get(0).getPfn_id();
                    selected_service_name = serviceForAppLists.get(0).getPfn_fee_name();
                    selected_service_qty = serviceForAppLists.get(0).getService_qty();

                    selectServiceCancLay.setEnabled(true);
                    selectServiceCancLay.setHint("Service");
                    selectServiceCanc.setText(selected_service_name);
                    serviceSelectCancErrMsg.setVisibility(View.GONE);

                    getAppdateTime();
                }
                else {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);

                    selectServiceCancLay.setEnabled(true);
                    serviceSelectCancErrMsg.setVisibility(View.GONE);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < serviceForAppLists.size(); i++) {
                    type.add(serviceForAppLists.get(i).getPfn_fee_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AppointmentModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                selectServiceCanc.setAdapter(arrayAdapter);
            }
            else {
                alertMessageServiceCanc();
            }
        }
        else {
            alertMessageServiceCanc();
        }
    }

    public void alertMessageServiceCanc() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getServiceCanData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    String sem = "Service List Failed to load. Please Try again";
                    serviceSelectCancErrMsg.setText(sem);
                    serviceSelectCancErrMsg.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getAppdateTime() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        appointDateTimeCancelLists = new ArrayList<>();
        appTimeLists = new ArrayList<>();

        System.out.println(selected_pat_ph_id+" "+ selected_depts_id+" " + selected_doc_id+" "+ selected_pfn_id);
        String dateTimeUrl = pre_url_api+"appointmentModify/getAppDateTimeForCancel?ph_id="+selected_pat_ph_id+
                "&depts_id="+selected_depts_id+"&doc_id="+selected_doc_id+"&pfn_id="+selected_pfn_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest dateTimeReq = new StringRequest(Request.Method.GET, dateTimeUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String ad_id = info.getString("ad_id")
                                .equals("null") ? "" : info.getString("ad_id");
                        String app_date = info.getString("app_date")
                                .equals("null") ? "" : info.getString("app_date");

                        String time_list = info.getString("time_list");

                        JSONArray timeArray = new JSONArray(time_list);

                        ArrayList<AppointTimeCancelList> timeCancelLists = new ArrayList<>();

                        for (int j = 0; j < timeArray.length(); j++) {
                            JSONObject timeInfo = timeArray.getJSONObject(j);

                            String as_ts_id = timeInfo.getString("as_ts_id")
                                    .equals("null") ? "" : timeInfo.getString("as_ts_id");
                            String schedule = timeInfo.getString("schedule")
                                    .equals("null") ? "" : timeInfo.getString("schedule");

                            timeCancelLists.add(new AppointTimeCancelList(as_ts_id,schedule));
                        }

                        appointDateTimeCancelLists.add(new AppointDateTimeCancelList(ad_id,app_date,timeCancelLists));
                    }
                }

                connected = true;
                updateDateCancSpinner();
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateDateCancSpinner();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateDateCancSpinner();
        });

        dateTimeReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(dateTimeReq);
    }

    private void updateDateCancSpinner() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                selectAppDateCancLay.setEnabled(true);
                selectAppDateCancLay.setHint("Select Appointment Date");
                selectAppDateCanc.setText("");

                appDateSelectCancErrMsg.setVisibility(View.GONE);

                appTimeCardViewLay.setVisibility(View.GONE);

                if (appointDateTimeCancelLists.isEmpty()) {
                    selectAppDateCancLay.setEnabled(false);
                    String adem = "No Appointment Date found for this Patient";
                    appDateSelectCancErrMsg.setText(adem);
                    appDateSelectCancErrMsg.setVisibility(View.VISIBLE);
                }
                else if (appointDateTimeCancelLists.size() == 1) {

                    appTimeLists = new ArrayList<>();
                    selected_ad_id = appointDateTimeCancelLists.get(0).getAd_id();
                    selected_adm_date = appointDateTimeCancelLists.get(0).getApp_date();
                    adm_date_position = "0";
                    ArrayList<AppointTimeCancelList> timeCancelLists = appointDateTimeCancelLists.get(0).getAppointTimeCancelLists();

                    selectAppDateCancLay.setEnabled(true);
                    selectAppDateCancLay.setHint("Appointment Date");
                    selectAppDateCanc.setText(selected_adm_date);
                    appDateSelectCancErrMsg.setVisibility(View.GONE);

                    for (int i = 0; i < timeCancelLists.size(); i++) {
                        appTimeLists.add(timeCancelLists.get(i).getSchedule());
                    }
                    appTimeCardViewLay.setVisibility(View.VISIBLE);
                    resetAppTimeForCancelSchView();
                }
                else {
                    selectAppDateCancLay.setEnabled(true);
                    appDateSelectCancErrMsg.setVisibility(View.GONE);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < appointDateTimeCancelLists.size(); i++) {
                    type.add(appointDateTimeCancelLists.get(i).getApp_date());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AppointmentModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                selectAppDateCanc.setAdapter(arrayAdapter);
            }
            else {
                alertMessageDateCanc();
            }
        }
        else {
            alertMessageDateCanc();
        }
    }

    public void alertMessageDateCanc() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getAppdateTime();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    String adem = "Appointment Date Failed to load. Please Try again";
                    appDateSelectCancErrMsg.setText(adem);
                    appDateSelectCancErrMsg.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void updateAppointmentToCancel() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        parsing_message = "";

        String appointCancelUrl = pre_url_api+"appointmentModify/updateforCancelAppoint";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest cancelAppReq = new StringRequest(Request.Method.POST, appointCancelUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;

                connected = string_out.equals("Successfully Created");
                System.out.println(string_out);
                updateAfterAppCancel();

            }
            catch (JSONException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateAfterAppCancel();
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateAfterAppCancel();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_AD_ID",selected_ad_id);
                return headers;
            }
        };

        cancelAppReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(cancelAppReq);
    }

    private void updateAfterAppCancel() {
        loading = false;
        if (conn) {
            if (connected) {
                circularProgressIndicator.setVisibility(View.GONE);
                fullLayout.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
                alertDialogBuilder.setTitle("Success!")
                        .setMessage("Appointment Canceled successfully.")
                        .setPositiveButton("Ok", (dialog, which) -> {
                            dialog.dismiss();
                            resetAllData();
                        });

                AlertDialog alert = alertDialogBuilder.create();
                alert.setCancelable(false);
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
            else {
                alertMessageInApp();
            }
        }
        else {
            alertMessageCancelApp();
        }
    }

    public void alertMessageCancelApp() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected. Please retry or data will be lost.";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected. Please retry or data will be lost.";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please retry or data will be lost..")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateAppointmentToCancel();
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}