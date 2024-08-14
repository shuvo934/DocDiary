package ttit.com.shuvo.docdiary.appointment_admin.add_schedule;

import static java.util.Calendar.SATURDAY;

import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.appointmentDateTimeLists;
import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.scheduleSelected;
import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.selected_service_qty;
import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.serviceQty;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.rosemaryapp.amazingspinner.AmazingSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify;
import ttit.com.shuvo.docdiary.appointment_admin.add_schedule.adapters.TakenScheduleAdapter;
import ttit.com.shuvo.docdiary.appointment_admin.add_schedule.arraylists.SelectableAppointmentDateList;
import ttit.com.shuvo.docdiary.appointment_admin.add_schedule.arraylists.SelectableTimeList;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.SavedAppointmentDateTimeList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.ComplainModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.ListOfComplains;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.ListOfInjuries;

public class AddSchedule extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView close;
    TextView appbarName;

    TextInputLayout dateSelectionLay;
    TextInputEditText dateSelection;
    TextView dateMissing;
    ArrayList<SelectableAppointmentDateList> dateLists;
    ArrayList<SelectableTimeList> timeLists;
    ArrayList<String> takenTimeLists;

    TextInputLayout timeSelectionLay;
    AmazingSpinner timeSelection;
    TextView timeMissing;

    CardView takenScheduleLay;
    RecyclerView takenScheduleView;
    RecyclerView.LayoutManager layoutManager;
    TakenScheduleAdapter takenScheduleAdapter;

    Button addApp;


    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean timeApiFound = false;
    boolean loading = false;
    String parsing_message = "";

    String doc_id = "";
    String pfn_id = "";
    String depts_id = "";
    String ph_id = "";
    String prm_id = "";
    String prd_id = "";

    String selected_date = "";
    String adm_id = "";
    String selected_time = "";
    String ts_id = "";
    String ts_eta_flag = "";
    String time_type = "";

    int pos = 0;
    String previous_selected_time = "";
    boolean updateFlag = false;
    boolean sc_inserted = false;
    boolean sc_duplicate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(AddSchedule.this,R.color.clouds));
        setContentView(R.layout.activity_add_schedule);

        fullLayout = findViewById(R.id.add_schedule_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_add_schedule_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_add_schedule_modify);
        appbarName = findViewById(R.id.add_schedule_modify_app_bar_text);

        dateSelectionLay = findViewById(R.id.appointment_date_selection_layout);
        dateSelection = findViewById(R.id.appointment_date_selection);
        dateMissing = findViewById(R.id.appointment_date_for_patient_missing_msg);
        dateMissing.setVisibility(View.GONE);

        timeSelectionLay = findViewById(R.id.spinner_layout_appointment_time_selection_for_add_appointment);
        timeSelection = findViewById(R.id.appointment_time_selection_for_add_appointment_spinner);
        timeMissing = findViewById(R.id.appointment_time_for_patient_missing_msg);
        timeMissing.setVisibility(View.GONE);

        takenScheduleLay = findViewById(R.id.taken_schedule_layout_card);
        takenScheduleLay.setVisibility(View.GONE);
        takenScheduleView = findViewById(R.id.taken_appointment_time_recyclerview_for_patient);

        takenScheduleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        takenScheduleView.setLayoutManager(layoutManager);

        addApp = findViewById(R.id.add_schedule_modify_button);

        dateLists = new ArrayList<>();
        timeLists = new ArrayList<>();
        takenTimeLists = new ArrayList<>();

        Intent intent = getIntent();
        doc_id = intent.getStringExtra("P_DOC_ID");
        pfn_id = intent.getStringExtra("P_PFN_ID");
        depts_id = intent.getStringExtra("P_DEPTS_ID");
        ph_id = intent.getStringExtra("P_PH_ID");
        prm_id = intent.getStringExtra("P_PRM_ID");
        prd_id = intent.getStringExtra("P_PRD_ID");

        updateFlag = intent.getBooleanExtra("P_UPDATE",false);

        if (updateFlag) {
            selected_date = intent.getStringExtra("P_DATE");
            adm_id = intent.getStringExtra("P_ADM_ID");
            selected_time = intent.getStringExtra("P_SCHEDULE");
            ts_id = intent.getStringExtra("P_TS_ID");
            pos = intent.getIntExtra("P_POSITION",0);

            sc_inserted =  intent.getBooleanExtra("SCH_INSERTED", false);
            sc_duplicate = intent.getBooleanExtra("SCH_DUPLICATE",false);

            if (sc_inserted) {
                if (sc_duplicate) {
                    selected_time = "";
                    ts_id = "";
                }
            }
            previous_selected_time = selected_time;
            dateSelection.setText(selected_date);
            timeSelection.setText(selected_time);
            dateSelectionLay.setEnabled(false);

            addApp.setText("Update Schedule");
        }
        else {
            addApp.setText("Add Schedule");
        }

        dateSelection.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    AddSchedule.this,
                    now.get(Calendar.YEAR), // Initial year selection
                    now.get(Calendar.MONTH), // Initial month selection
                    now.get(Calendar.DAY_OF_MONTH) // Inital day selection
            );

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

            String firstDate = dateLists.get(0).getP_adm_date();
            String lastDate = dateLists.get(dateLists.size()-1).getP_adm_date();

            Date fdate = null;
            Date ldate = null;
            try {
                fdate = sdf.parse(firstDate);
                ldate = sdf.parse(lastDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar firstDateCalendar = Calendar.getInstance();
            assert fdate != null;
            firstDateCalendar.setTime(fdate);
            firstDateCalendar.set(Calendar.DAY_OF_MONTH,1);

            Calendar lastDateCalendar = Calendar.getInstance();
            assert ldate != null;
            lastDateCalendar.setTime(ldate);
            lastDateCalendar.add(Calendar.MONTH,1);
            lastDateCalendar.set(Calendar.DAY_OF_MONTH,1);
            lastDateCalendar.add(Calendar.DAY_OF_MONTH,-1);

            dpd.setMinDate(firstDateCalendar);
            dpd.setMaxDate(lastDateCalendar);

            ArrayList<Calendar> dates = new ArrayList<>();

            for (int i = 0; i < dateLists.size(); i++) {
                Date date = null;
                try {
                    date = sdf.parse(dateLists.get(i).getP_adm_date());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                if (date != null) {
                    calendar.setTime(date);
                }
                dates.add(calendar);
            }
            Calendar[] selectableDays = dates.toArray(new Calendar[dates.size()]);
            dpd.setSelectableDays(selectableDays);
//
            dpd.setFirstDayOfWeek(SATURDAY);
            dpd.show(getSupportFragmentManager(),"DATE_PICKER");
        });

        timeSelection.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <timeLists.size(); i++) {
                if (name.equals(timeLists.get(i).getSchedule())) {
                    ts_id = timeLists.get(i).getTs_id();
                    selected_time = timeLists.get(i).getSchedule();
                    ts_eta_flag = timeLists.get(i).getTs_eta_flag();
                    time_type = timeLists.get(i).getType();
                    break;
                }
                else {
                    ts_id = "";
                    selected_time = "";
                    ts_eta_flag = "";
                    time_type = "";
                }
            }
            timeMissing.setVisibility(View.GONE);
        });

        close.setOnClickListener(v -> onBackPressed());

        addApp.setOnClickListener(v -> {
            if (updateFlag) {
                if (adm_id.isEmpty()) {
                    dateMissing.setVisibility(View.VISIBLE);
                    dateMissing.setText("Please Select Appointment Date");
                }
                else {
                    if (ts_id.isEmpty()) {
                        timeMissing.setVisibility(View.VISIBLE);
                        timeMissing.setText("Please Select Appointment Time");
                    }
                    else {
                        scheduleSelected = true;
                        appointmentDateTimeLists.get(pos).setAdm_date(selected_date);
                        appointmentDateTimeLists.get(pos).setAdm_id(adm_id);
                        appointmentDateTimeLists.get(pos).setSchedule(selected_time);
                        appointmentDateTimeLists.get(pos).setTs_id(ts_id);
                        appointmentDateTimeLists.get(pos).setDoc_id(doc_id);
                        appointmentDateTimeLists.get(pos).setPfn_id(pfn_id);
                        appointmentDateTimeLists.get(pos).setDepts_id(depts_id);
                        appointmentDateTimeLists.get(pos).setPh_id(ph_id);
                        appointmentDateTimeLists.get(pos).setPrm_id(prm_id);
                        appointmentDateTimeLists.get(pos).setPrd_id(prd_id);
                        appointmentDateTimeLists.get(pos).setInserted(false);
                        appointmentDateTimeLists.get(pos).setSchDuplicate(false);
                        if (takenTimeLists != null) {
                            appointmentDateTimeLists.get(pos).setTakenScheduleAvailable(takenTimeLists.size() > 0);
                        }
                        else {
                            appointmentDateTimeLists.get(pos).setTakenScheduleAvailable(false);
                        }
                        finish();
                    }
                }
            }
            else {
                if (adm_id.isEmpty()) {
                    dateMissing.setVisibility(View.VISIBLE);
                    dateMissing.setText("Please Select Appointment Date");
                }
                else {
                    if (ts_id.isEmpty()) {
                        timeMissing.setVisibility(View.VISIBLE);
                        timeMissing.setText("Please Select Appointment Time");
                    }
                    else {
                        int service_qty = Integer.parseInt(selected_service_qty);
                        boolean found = false;
                        for (int i = 0; i < appointmentDateTimeLists.size(); i++) {
                            if (appointmentDateTimeLists.get(i).getAdm_id().equals(adm_id)) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            if (service_qty > 0) {
                                scheduleSelected = true;
                                if (takenTimeLists != null) {
                                    if (takenTimeLists.size() > 0) {
                                        appointmentDateTimeLists.add(new SavedAppointmentDateTimeList(selected_date,adm_id,selected_time,ts_id,doc_id,pfn_id,depts_id,ph_id,prm_id,prd_id,true,false,false));
                                        finish();
                                    }
                                    else {
                                        appointmentDateTimeLists.add(new SavedAppointmentDateTimeList(selected_date,adm_id,selected_time,ts_id,doc_id,pfn_id,depts_id,ph_id,prm_id,prd_id,false,false,false));
                                        service_qty = service_qty - 1;
                                        selected_service_qty = String.valueOf(service_qty);
                                        finish();
                                    }
                                }
                                else {
                                    appointmentDateTimeLists.add(new SavedAppointmentDateTimeList(selected_date,adm_id,selected_time,ts_id,doc_id,pfn_id,depts_id,ph_id,prm_id,prd_id,false,false,false));
                                    service_qty = service_qty - 1;
                                    selected_service_qty = String.valueOf(service_qty);
                                    finish();
                                }
                            }
                            else {
                                StringBuilder dates = new StringBuilder();
                                ArrayList<String> date = new ArrayList<>();
                                int count = 0;
                                for (int i = 0; i < appointmentDateTimeLists.size(); i++) {
                                    if (i == 0) {
                                        date.add(appointmentDateTimeLists.get(i).getAdm_date());
                                        dates.append(appointmentDateTimeLists.get(i).getAdm_date()).append(", ");
                                        count++;
                                    }
                                    else {
                                        boolean f = false;
                                        for (int j = 0; j < date.size(); j++) {
                                            String d = date.get(j);
                                            if (appointmentDateTimeLists.get(i).getAdm_date().equals(d)) {
                                                f = true;
                                                break;
                                            }
                                        }
                                        if (!f) {
                                            date.add(appointmentDateTimeLists.get(i).getAdm_date());
                                            dates.append(appointmentDateTimeLists.get(i).getAdm_date()).append(", ");
                                            count++;
                                        }
                                    }
                                }

                                dates = new StringBuilder(dates.substring(0, dates.length() - 2));

                                String msg;
                                if (count > 1) {
                                    msg = "You have taken Service for " + dates + " dates. You can not add more schedule other than these dates.";
                                }
                                else {
                                    msg = "You have taken Service for " + dates + " date. You can not add more schedule other than this date.";
                                }
                                if (takenTimeLists != null) {
                                    if (takenTimeLists.size() > 0) {
                                        scheduleSelected = true;
                                        appointmentDateTimeLists.add(new SavedAppointmentDateTimeList(selected_date,adm_id,selected_time,ts_id,doc_id,pfn_id,depts_id,ph_id,prm_id,prd_id,true, false,false));
                                        finish();
                                    }
                                    else {
                                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddSchedule.this);
                                        alertDialogBuilder.setTitle("Service Quantity Empty!")
                                                .setMessage(msg)
                                                .setPositiveButton("Ok", (dialog, which) -> {
                                                    dialog.dismiss();
                                                });

                                        AlertDialog alert = alertDialogBuilder.create();
                                        alert.setCancelable(false);
                                        alert.setCanceledOnTouchOutside(false);
                                        alert.show();
                                    }
                                }
                                else {
                                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddSchedule.this);
                                    alertDialogBuilder.setTitle("Service Quantity Empty!")
                                            .setMessage(msg)
                                            .setPositiveButton("Ok", (dialog, which) -> {
                                                dialog.dismiss();
                                            });

                                    AlertDialog alert = alertDialogBuilder.create();
                                    alert.setCancelable(false);
                                    alert.setCanceledOnTouchOutside(false);
                                    alert.show();
                                }
                            }
                        }
                        else {
                            scheduleSelected = true;
                            boolean tsa = false;
                            if (takenTimeLists != null) {
                                tsa = takenTimeLists.size() > 0;
                            }
                            appointmentDateTimeLists.add(new SavedAppointmentDateTimeList(selected_date,adm_id,selected_time,ts_id,doc_id,pfn_id,depts_id,ph_id,prm_id,prd_id,tsa, false,false));
                            finish();
                        }
                    }
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

    @Override
    public void onBackPressed() {
        if (loading) {
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String monthName = "";
        String dayOfMonthName = "";
        String yearName = "";
        monthOfYear = monthOfYear + 1;
        if (monthOfYear == 1) {
            monthName = "JAN";
        } else if (monthOfYear == 2) {
            monthName = "FEB";
        } else if (monthOfYear == 3) {
            monthName = "MAR";
        } else if (monthOfYear == 4) {
            monthName = "APR";
        } else if (monthOfYear == 5) {
            monthName = "MAY";
        } else if (monthOfYear == 6) {
            monthName = "JUN";
        } else if (monthOfYear == 7) {
            monthName = "JUL";
        } else if (monthOfYear == 8) {
            monthName = "AUG";
        } else if (monthOfYear == 9) {
            monthName = "SEP";
        } else if (monthOfYear == 10) {
            monthName = "OCT";
        } else if (monthOfYear == 11) {
            monthName = "NOV";
        } else if (monthOfYear == 12) {
            monthName = "DEC";
        }

        if (dayOfMonth <= 9) {
            dayOfMonthName = "0" + dayOfMonth;
        } else {
            dayOfMonthName = String.valueOf(dayOfMonth);
        }
        yearName  = String.valueOf(year);
        yearName = yearName.substring(yearName.length()-2);
        System.out.println(yearName);
        System.out.println(dayOfMonthName);
        String dateText = dayOfMonthName + "-" + monthName + "-" + yearName;
        dateSelection.setText(dateText);
        dateMissing.setVisibility(View.GONE);
        selected_date = Objects.requireNonNull(dateSelection.getText()).toString();
        for (int i = 0; i < dateLists.size(); i++) {
            if (selected_date.equals(dateLists.get(i).getP_adm_date())) {
                adm_id = dateLists.get(i).getAdm_id();
                break;
            }
        }
        System.out.println(adm_id);

        ts_id = "";
        selected_time = "";
        ts_eta_flag = "";
        time_type = "";
        timeSelection.setText("");
        timeSelectionLay.setEnabled(false);
        timeMissing.setVisibility(View.GONE);
        getTimeData();
    }

    public void getData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        dateLists = new ArrayList<>();

        String appDateUrl = pre_url_api+"appointmentModify/getAppointmentDateList?doc_id="+doc_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest appDateReq = new StringRequest(Request.Method.GET, appDateUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String adm_id = info.getString("adm_id")
                                .equals("null") ? "" : info.getString("adm_id");
                        String p_adm_date = info.getString("p_adm_date")
                                .equals("null") ? "" : info.getString("p_adm_date");

                        dateLists.add(new SelectableAppointmentDateList(adm_id,p_adm_date));
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

        requestQueue.add(appDateReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                conn = false;
                connected = false;

                timeSelectionLay.setEnabled(false);
                timeMissing.setVisibility(View.GONE);

                if (dateLists.size() == 0) {
                    dateMissing.setText("No Appointment Date Found. Please Reselect Doctor");
                    dateMissing.setVisibility(View.VISIBLE);
                    dateSelectionLay.setEnabled(false);
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);
                }
                else {
                    dateMissing.setVisibility(View.GONE);
                    dateSelectionLay.setEnabled(true);

                    if (updateFlag) {
                        dateSelectionLay.setEnabled(false);
                        getTimeData();
                    }
                    else {
                        for (int i = 0; i < appointmentDateTimeLists.size(); i++) {
                            String date = appointmentDateTimeLists.get(i).getAdm_date();
                            int index = 0;
                            boolean f = false;
                            for (int j = 0; j < dateLists.size(); j++) {
                                if (date.equals(dateLists.get(j).getP_adm_date())) {
                                    f = true;
                                    index = j;
                                    break;
                                }
                            }
                            if (f) {
                                dateLists.remove(index);
                            }
                        }
                        fullLayout.setVisibility(View.VISIBLE);
                        circularProgressIndicator.setVisibility(View.GONE);
                    }
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddSchedule.this);
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

    public void getTimeData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        timeApiFound = false;

        String timeApiUrl = pre_url_api+"appointmentModify/getTimeApiForDiffOccurance?p_adm_date="+selected_date+"&p_pfn_id="+pfn_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest timeApiReq = new StringRequest(Request.Method.GET, timeApiUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String apiName = jsonObject.getString("api_name")
                        .equals("null") ? "" : jsonObject.getString("api_name");
                if (apiName.isEmpty()) {
                    timeApiFound = false;
                    updateTimeInterface();
                }
                else {
                    timeApiFound = true;
                    getTimeList(apiName);
                }
            }
            catch (Exception e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateTimeInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateTimeInterface();
        });

        requestQueue.add(timeApiReq);
    }

    public void getTimeList(String api_name) {
        conn = false;
        connected = false;

        timeLists = new ArrayList<>();
        takenTimeLists = new ArrayList<>();

        System.out.println(api_name);
        System.out.println(doc_id);
        System.out.println(depts_id);
        System.out.println(adm_id);
        System.out.println(selected_date);
        System.out.println(ph_id);
        System.out.println(ts_id);

        String timeUrl = pre_url_api+"appointmentModify/"+api_name+"?doc_id="+doc_id+"&depts_id="+depts_id
                +"&adm_id="+adm_id+"&adm_date="+selected_date+"&ph_id="+ph_id+"&ts_id=&am_id=";

        String takenTimeUrl = pre_url_api+"appointmentModify/getTakenScheduleList?P_ADM_ID="+adm_id+"&P_DOC_ID="+doc_id
                +"&P_DEPTS_ID="+depts_id+"&P_PH_ID="+ph_id+"&P_PRM_ID="+prm_id+"&P_PFN_ID="+pfn_id+"&P_PRD_ID="+prd_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest takenTimeReq = new StringRequest(Request.Method.GET, takenTimeUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

//                        String ts_id = info.getString("ts_id")
//                                .equals("null") ? "" : info.getString("ts_id");
                        String schedule = info.getString("schedule")
                                .equals("null") ? "" : info.getString("schedule");

                        takenTimeLists.add(schedule);
                    }
                }
                connected = true;
                updateTimeInterface();
            }
            catch (Exception e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateTimeInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateTimeInterface();
        });

        StringRequest timeReq = new StringRequest(Request.Method.GET, timeUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String ts_id = info.getString("ts_id")
                                .equals("null") ? "" : info.getString("ts_id");
                        String schedule = info.getString("schedule")
                                .equals("null") ? "" : info.getString("schedule");
                        String ts_eta_flag = info.getString("ts_eta_flag")
                                .equals("null") ? "" : info.getString("ts_eta_flag");
                        String type = info.getString("type")
                                .equals("null") ? "" : info.getString("type");

                        timeLists.add(new SelectableTimeList(ts_id,schedule,ts_eta_flag,type));
                    }
                }
                requestQueue.add(takenTimeReq);
            }
            catch (Exception e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateTimeInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateTimeInterface();
        });

        requestQueue.add(timeReq);

    }

    private void updateTimeInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                if (timeApiFound) {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);
                    conn = false;
                    connected = false;


                    if (timeLists.size() == 0) {
                        timeMissing.setText("No Appointment Time Found. Please Reselect Date");
                        timeMissing.setVisibility(View.VISIBLE);
                        timeSelectionLay.setEnabled(false);
                    }
                    else {
                        boolean found = false;
                        if (updateFlag) {
                            for (int i = 0; i < appointmentDateTimeLists.size(); i++) {
                                if (appointmentDateTimeLists.get(i).getAdm_id().equals(adm_id)) {
                                    String time = appointmentDateTimeLists.get(i).getSchedule();
                                    if (!time.equals(previous_selected_time)) {
                                        int t_index = 0;
                                        for (int j = 0; j < timeLists.size(); j++) {
                                            if (timeLists.get(j).getSchedule().equals(time)) {
                                                found = true;
                                                t_index = j;
                                                break;
                                            }
                                        }
                                        if (found) {
                                            found = false;
                                            timeLists.remove(t_index);
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            for (int i = 0; i < appointmentDateTimeLists.size(); i++) {
                                if (appointmentDateTimeLists.get(i).getAdm_id().equals(adm_id)) {
                                    String time = appointmentDateTimeLists.get(i).getSchedule();
                                    int t_index = 0;
                                    for (int j = 0; j < timeLists.size(); j++) {
                                        if (timeLists.get(j).getSchedule().equals(time)) {
                                            found = true;
                                            t_index = j;
                                            break;
                                        }
                                    }
                                    if (found) {
                                        found = false;
                                        timeLists.remove(t_index);
                                    }
                                }
                            }
                        }
                        if (timeLists.size() == 0) {
                            timeMissing.setText("All available time already Selected. Please Reselect Date");
                            timeMissing.setVisibility(View.VISIBLE);
                            timeSelectionLay.setEnabled(false);
                        }
                        else {
                            timeMissing.setVisibility(View.GONE);
                            timeSelectionLay.setEnabled(true);
                        }
                    }

                    ArrayList<String> type = new ArrayList<>();
                    for(int i = 0; i < timeLists.size(); i++) {
                        type.add(timeLists.get(i).getSchedule());
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddSchedule.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                    timeSelection.setAdapter(arrayAdapter);

                    if (takenTimeLists.size() == 0) {
                        takenScheduleLay.setVisibility(View.GONE);
                    }
                    else {
                        timeSelectionLay.setEnabled(false);
                        takenScheduleLay.setVisibility(View.VISIBLE);
                    }

                    takenScheduleAdapter = new TakenScheduleAdapter(takenTimeLists,AddSchedule.this);
                    takenScheduleView.setAdapter(takenScheduleAdapter);

                }
                else {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddSchedule.this);
                    alertDialogBuilder.setTitle("Error!")
                            .setMessage("Could Not Find Appointment Time Data. Try again")
                            .setPositiveButton("Retry", (dialog, which) -> {
                                getTimeData();
                                dialog.dismiss();
                            })
                            .setNegativeButton("Cancel",(dialog, which) -> {
                                timeSelectionLay.setEnabled(false);
                                timeMissing.setVisibility(View.VISIBLE);
                                timeMissing.setText("Could Not Find Appointment Time Data. Try again");
                                dialog.dismiss();
                            });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.setCancelable(false);
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();
                }
            }
            else {
                alertMessageTime();
            }
        }
        else {
            alertMessageTime();
        }
    }

    public void alertMessageTime() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddSchedule.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getTimeData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    timeSelectionLay.setEnabled(false);
                    timeMissing.setVisibility(View.VISIBLE);
                    timeMissing.setText("Appointment Time failed to load. Please Try Again");
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}