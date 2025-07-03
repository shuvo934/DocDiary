package ttit.com.shuvo.docdiary.patient_search.appointment_calendar;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.CalendarWeekDay;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.patient_search.appointment_calendar.adapters.PatientAppSchAdapter;
import ttit.com.shuvo.docdiary.patient_search.appointment_calendar.arraylist.PatAppCalendarList;
import ttit.com.shuvo.docdiary.patient_search.appointment_calendar.arraylist.PatAppointmentList;
import ttit.com.shuvo.docdiary.patient_search.appointment_calendar.arraylist.PatAppointmentViewList;
import ttit.com.shuvo.docdiary.progressbar.WaitProgress;

public class PatAppointmentCalendar extends AppCompatActivity {

    ImageView backButton;
    CalendarView calendarView;
    String selected_date = "";
    String selected_date_day = "";

    TextView patientName;
    TextView patientCode;

    String first_month = "";
    String last_month = "";

    WaitProgress waitProgress = new WaitProgress();
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;

    String parsing_message = "";

    String pat_name = "";
    String pat_code = "";
    String ph_id = "";

    ArrayList<PatAppointmentList> patAppointmentLists;
    ArrayList<PatAppCalendarList> patAppCalendarLists;

    Calendar startDates;
    Calendar endDates;

    Logger logger = Logger.getLogger(PatAppointmentCalendar.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_appointment_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pat_app_calendar_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backButton = findViewById(R.id.back_logo_of_pat_appointment_cal);
        calendarView = findViewById(R.id.calendarView_pat_appointment_cal);
        patientName = findViewById(R.id.pat_appointment_cal_patient_name);
        patientCode = findViewById(R.id.pat_appointment_cal_patient_ph_code);

        patAppointmentLists = new ArrayList<>();
        patAppCalendarLists = new ArrayList<>();

        backButton.setOnClickListener(v -> finish());

        Calendar firstMonthCalender = Calendar.getInstance();
        SimpleDateFormat month_format = new SimpleDateFormat("MMM-yy", Locale.ENGLISH);
        first_month = month_format.format(firstMonthCalender.getTime());
        first_month = first_month.toUpperCase();
        first_month = "01-"+first_month;

        Calendar lastMonthCalender = Calendar.getInstance();
        lastMonthCalender.add(Calendar.MONTH,2);
        last_month = month_format.format(lastMonthCalender.getTime());
        last_month = last_month.toUpperCase();
        int last_date = lastMonthCalender.getActualMaximum(Calendar.DAY_OF_MONTH);
        last_month = last_date +"-" + last_month;

        startDates = Calendar.getInstance();
        startDates.set(Calendar.DAY_OF_MONTH,1);
        startDates.set(Calendar.HOUR_OF_DAY,0);
        startDates.set(Calendar.MINUTE,0);
        startDates.set(Calendar.SECOND,0);
        startDates.set(Calendar.MILLISECOND,0);

        endDates = Calendar.getInstance();
        endDates.add(Calendar.MONTH,2);
        endDates.set(Calendar.DAY_OF_MONTH,endDates.getActualMaximum(Calendar.DAY_OF_MONTH));

        calendarView.setMinimumDate(startDates);
        calendarView.setMaximumDate(endDates);
        calendarView.setFirstDayOfWeek(CalendarWeekDay.SATURDAY);

        Intent intent = getIntent();
        pat_name = intent.getStringExtra("P_NAME");
        pat_code = intent.getStringExtra("P_CODE");
        ph_id = intent.getStringExtra("P_PH_ID");

        patientName.setText(pat_name);
        patientCode.setText(pat_code);
        System.out.println("PH_ID: "+ph_id);

        calendarView.setOnCalendarDayClickListener(calendarDay -> {
            Calendar ccc = calendarDay.getCalendar();
            if (ccc.getTime().getTime() >= startDates.getTime().getTime() && ccc.getTime().getTime() <= endDates.getTime().getTime()) {
                for (int i = 0; i < patAppCalendarLists.size(); i++) {
                    if (patAppCalendarLists.get(i).getDate().equals(ccc.getTime())) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH);
                        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                        selected_date = simpleDateFormat.format(ccc.getTime());
                        selected_date_day = dayNameFormat.format(ccc.getTime());
                        ArrayList<PatAppointmentViewList> patAppointmentViewLists = patAppCalendarLists.get(i).getPatAppointmentViewLists();
                        String appointment_date = patAppCalendarLists.get(i).getApp_date();
                        showBottomSheetDialog(appointment_date,patAppointmentViewLists);
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loading != null) {
            if (!loading) {
                getPatAppData();
            }
        }
        else {
            restart("App is paused for a long time. Please Start the app again.");
        }
    }

    private void showBottomSheetDialog(String appointment_date, ArrayList<PatAppointmentViewList> list) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(PatAppointmentCalendar.this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_pat_appointment_schedule);
        TextView patName = bottomSheetDialog.findViewById(R.id.pat_name_in_bottom_sheet_from_calendar);
        TextView today_date = bottomSheetDialog.findViewById(R.id.today_date_selected_from_pat_appointment_calendar);
        TextView today_date_name = bottomSheetDialog.findViewById(R.id.today_date_name_selected_from_pat_appointment_calendar);

        RecyclerView recyclerView = bottomSheetDialog.findViewById(R.id.pat_appointment_from_calendar_schedule_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PatAppointmentCalendar.this);

        assert patName != null;
        assert today_date != null;
        assert today_date_name != null;
        assert recyclerView != null;

        patName.setText(pat_name);
        today_date.setText(selected_date);
        today_date_name.setText(selected_date_day);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        PatientAppSchAdapter patientAppSchAdapter = new PatientAppSchAdapter(list,PatAppointmentCalendar.this,appointment_date);
        recyclerView.setAdapter(patientAppSchAdapter);

        bottomSheetDialog.show();
    }

    public void getPatAppData() {
        try {
            waitProgress.show(getSupportFragmentManager(), "WaitBar");
            waitProgress.setCancelable(false);
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }

        conn = false;
        connected = false;
        loading = true;

        patAppointmentLists = new ArrayList<>();
        patAppCalendarLists = new ArrayList<>();

        String apptUrl = pre_url_api+"patient_search/getPatAppointment?PHID="+ph_id+"&FIRST_MONTH="+first_month+"&LAST_MONTH="+last_month;

        RequestQueue requestQueue = Volley.newRequestQueue(PatAppointmentCalendar.this);

        StringRequest timeDataReq = new StringRequest(Request.Method.GET, apptUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject timeSdInfo = array.getJSONObject(i);

                        String ph_patient_status = timeSdInfo.getString("ph_patient_status")
                                .equals("null") ? "" : timeSdInfo.getString("ph_patient_status");
                        String ph_patient_cat = timeSdInfo.getString("ph_patient_cat")
                                .equals("null") ? "" :timeSdInfo.getString("ph_patient_cat");
                        String app_date = timeSdInfo.getString("app_date")
                                .equals("null") ? "" :timeSdInfo.getString("app_date");
                        String depts_name = timeSdInfo.getString("depts_name")
                                .equals("null") ? "" :timeSdInfo.getString("depts_name");
                        String deptd_name = timeSdInfo.getString("deptd_name")
                                .equals("null") ? "" :timeSdInfo.getString("deptd_name");
                        String deptm_name = timeSdInfo.getString("deptm_name")
                                .equals("null") ? "" :timeSdInfo.getString("deptm_name");
                        String schedule_time = timeSdInfo.getString("schedule_time")
                                .equals("null") ? "" :timeSdInfo.getString("schedule_time");
                        String ets = timeSdInfo.getString("ets")
                                .equals("null") ? "" :timeSdInfo.getString("ets");
                        String doc_name = timeSdInfo.getString("doc_name")
                                .equals("null") ? "" : timeSdInfo.getString("doc_name");
                        String pfn_fee_name = timeSdInfo.getString("pfn_fee_name")
                                .equals("null") ? "" : timeSdInfo.getString("pfn_fee_name");
                        String avail = timeSdInfo.getString("avail")
                                .equals("null") ? "" : timeSdInfo.getString("avail");
                        String ad_pat_app_status = timeSdInfo.getString("ad_pat_app_status")
                                .equals("null") ? "" : timeSdInfo.getString("ad_pat_app_status");
                        String cancel_status = timeSdInfo.getString("cancel_status")
                                .equals("null") ? "0" : timeSdInfo.getString("cancel_status");
                        String prm_code = timeSdInfo.getString("prm_code")
                                .equals("null") ? "" : timeSdInfo.getString("prm_code");

                        patAppointmentLists.add(new PatAppointmentList(ph_patient_status, ph_patient_cat,app_date,depts_name,
                                deptd_name,deptm_name,schedule_time,ets,doc_name,pfn_fee_name,avail,ad_pat_app_status,
                                cancel_status,prm_code));

                    }
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

        requestQueue.add(timeDataReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {

                conn = false;
                connected = false;
                List<CalendarDay> events = new ArrayList<>();
                List<Date> listOfTimeDates = new ArrayList<>();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);

                Handler handler = new Handler();
                handler.postDelayed(() -> {

                    if (!patAppointmentLists.isEmpty()) {
                        int z = 0;
                        Date insertedDate = null;
                        for (int i = 0; i < patAppointmentLists.size(); i++) {
                            if (i==0) {
                                z = 1;
                            }
                            String app_date = patAppointmentLists.get(i).getApp_date();
                            Date date;
                            try {
                                date = dateFormat.parse(app_date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            assert date != null;

                            if (insertedDate != null) {
                                if (insertedDate.equals(date)) {
                                    z = 0;
                                }
                                else {
                                    z = 1;
                                }
                            }

                            if (z == 1) {
                                insertedDate = date;
                                listOfTimeDates.add(date);
                            }
                        }

                        for (int j = 0; j < listOfTimeDates.size(); j++) {

                            Calendar cc = Calendar.getInstance();
                            cc.setTime(listOfTimeDates.get(j));
                            ArrayList<PatAppointmentViewList> patAppointmentViewLists = new ArrayList<>();
                            String appointment_date = "";
                            for (int i = 0; i < patAppointmentLists.size(); i++) {
                                String app_date = patAppointmentLists.get(i).getApp_date();
                                Date date;
                                try {
                                    date = dateFormat.parse(app_date);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                assert date != null;

                                if (listOfTimeDates.get(j).equals(date)) {
                                    String depts_name = patAppointmentLists.get(i).getDepts_name();
                                    String deptd_name = patAppointmentLists.get(i).getDeptd_name();
                                    String deptm_name = patAppointmentLists.get(i).getDeptm_name();
                                    String schedule_time = patAppointmentLists.get(i).getSchedule_time();
                                    String ets = patAppointmentLists.get(i).getEts();
                                    String doc_name = patAppointmentLists.get(i).getDoc_name();
                                    String pfn_fee_name = patAppointmentLists.get(i).getPfn_fee_name();
                                    String avail = patAppointmentLists.get(i).getAvail();
                                    String ad_pat_app_status = patAppointmentLists.get(i).getAd_pat_app_status();
                                    String cancel_status = patAppointmentLists.get(i).getCancel_status();
                                    String prm_code = patAppointmentLists.get(i).getPrm_code();
                                    patAppointmentViewLists.add(new PatAppointmentViewList(depts_name,deptd_name,deptm_name,
                                            schedule_time,ets,doc_name,pfn_fee_name,avail,ad_pat_app_status,cancel_status,prm_code));
                                    appointment_date = patAppointmentLists.get(i).getApp_date();

                                }
                            }

                            patAppCalendarLists.add(new PatAppCalendarList(appointment_date,cc.getTime(),patAppointmentViewLists));

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
                            Date to_date = Calendar.getInstance().getTime();
                            String now_date = dateFormat.format(to_date);
                            Date app_date;
                            Date only_date;
                            try {
                                app_date = dateFormat.parse(appointment_date);
                                only_date = dateFormat.parse(now_date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            assert app_date != null;
                            assert only_date != null;
                            if (app_date.before(only_date)) {
                                System.out.println("DONE: "+app_date);
                                int cancelCount = 0;
                                if (patAppointmentViewLists.size() == 1) {
                                    CalendarDay calendarDay = new CalendarDay(cc);
                                    if (patAppointmentViewLists.get(0).getCancel_status().equals("Yes")) {
                                        calendarDay.setImageResource(R.drawable.cancel_appt);
                                    }
                                    else {
                                        calendarDay.setImageResource(R.drawable.done_appt);
                                    }
                                    calendarDay.setLabelColor(R.color.blue_end);
                                    events.add(calendarDay);
                                }
                                else {
                                    for (int i = 0; i < patAppointmentViewLists.size(); i++) {
                                        if (patAppointmentViewLists.get(i).getCancel_status().equals("Yes")) {
                                            cancelCount++;
                                        }
                                    }
                                    CalendarDay calendarDay = new CalendarDay(cc);
                                    if (cancelCount == 0) {
                                        calendarDay.setImageResource(R.drawable.done_appt);
                                    }
                                    else {
                                        if (cancelCount == patAppointmentViewLists.size()) {
                                            calendarDay.setImageResource(R.drawable.cancel_appt);
                                        }
                                        else {
                                            calendarDay.setImageResource(R.drawable.cancel_done_appt);
                                        }
                                    }
                                    calendarDay.setLabelColor(R.color.blue_end);
                                    events.add(calendarDay);
                                }
                            }
                            else if (app_date.equals(only_date)) {
                                System.out.println("EQUAL Date: "+app_date);
                                if (patAppointmentViewLists.size() == 1) {
                                    String schedule = patAppointmentViewLists.get(0).getSchedule_time();
                                    String schedule_time_date = appointment_date + " " + schedule;
                                    System.out.println("TODAY SCHEDULE TIME: "+schedule);
                                    Date appDate;
                                    try {
                                        appDate = simpleDateFormat.parse(schedule_time_date);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                    assert appDate != null;
                                    CalendarDay calendarDay = new CalendarDay(cc);
                                    if (appDate.before(to_date)) {
                                        if (patAppointmentViewLists.get(0).getCancel_status().equals("Yes")) {
                                            calendarDay.setImageResource(R.drawable.cancel_appt);
                                        }
                                        else {
                                            calendarDay.setImageResource(R.drawable.done_appt);
                                        }
                                    }
                                    else {
                                        if (patAppointmentViewLists.get(0).getCancel_status().equals("Yes")) {
                                            calendarDay.setImageResource(R.drawable.cancel_appt);
                                        }
                                        else {
                                            calendarDay.setImageResource(R.drawable.upcoming_appt);
                                        }
                                    }
                                    calendarDay.setLabelColor(R.color.blue_end);
                                    events.add(calendarDay);
                                }
                                else {
                                    int upcomingCount = 0;
                                    int cancelCount = 0;
                                    int upcoming_cancel_count = 0;
                                    int done_cancel_count = 0;
                                    for (int i = 0; i < patAppointmentViewLists.size(); i++) {
                                        boolean canceled = false;
                                        if (patAppointmentViewLists.get(i).getCancel_status().equals("Yes")) {
                                            cancelCount++;
                                            canceled = true;
                                        }
                                        String schedule = patAppointmentViewLists.get(i).getSchedule_time();
                                        String schedule_time_date = appointment_date + " " + schedule;
                                        Date appDate;
                                        try {
                                            appDate = simpleDateFormat.parse(schedule_time_date);
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                        assert appDate != null;

                                        if (!appDate.before(to_date)) {
                                            upcomingCount++;
                                            if (canceled) {
                                                upcoming_cancel_count++;
                                            }
                                        }
                                        else {
                                            if (canceled) {
                                                done_cancel_count++;
                                            }
                                        }
                                    }
                                    CalendarDay calendarDay = new CalendarDay(cc);
                                    if (upcomingCount == 0) {
                                        if (cancelCount == 0) {
                                            calendarDay.setImageResource(R.drawable.done_appt);
                                        }
                                        else {
                                            if (cancelCount == patAppointmentViewLists.size()) {
                                                calendarDay.setImageResource(R.drawable.cancel_appt);
                                            }
                                            else {
                                                calendarDay.setImageResource(R.drawable.cancel_done_appt);
                                            }
                                        }
                                    }
                                    else {
                                        if (cancelCount == 0) {
                                            if (upcomingCount == patAppointmentViewLists.size()) {
                                                calendarDay.setImageResource(R.drawable.upcoming_appt);
                                            }
                                            else {
                                                calendarDay.setImageResource(R.drawable.done_upcoming_appt);
                                            }
                                        }
                                        else {
                                            if (cancelCount == patAppointmentViewLists.size()) {
                                                calendarDay.setImageResource(R.drawable.cancel_appt);
                                            }
                                            else {
                                                if (upcomingCount == patAppointmentViewLists.size()) {
                                                    calendarDay.setImageResource(R.drawable.cancel_upcoming_appt);
                                                }
                                                else {
                                                    if (upcomingCount == upcoming_cancel_count) {
                                                        calendarDay.setImageResource(R.drawable.cancel_done_appt);
                                                    }
                                                    else if ((patAppointmentViewLists.size() - upcomingCount) == done_cancel_count) {
                                                        calendarDay.setImageResource(R.drawable.cancel_upcoming_appt);
                                                    }
                                                    else {
                                                        calendarDay.setImageResource(R.drawable.cancel_done_upcoming_appt);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    calendarDay.setLabelColor(R.color.blue_end);
                                    events.add(calendarDay);
                                }
                            }
                            else {
                                System.out.println("UPCOMING: "+app_date);
                                int cancelCount = 0;
                                if (patAppointmentViewLists.size() == 1) {
                                    CalendarDay calendarDay = new CalendarDay(cc);
                                    if (patAppointmentViewLists.get(0).getCancel_status().equals("Yes")) {
                                        calendarDay.setImageResource(R.drawable.cancel_appt);
                                    }
                                    else {
                                        calendarDay.setImageResource(R.drawable.upcoming_appt);
                                    }
                                    calendarDay.setLabelColor(R.color.blue_end);
                                    events.add(calendarDay);
                                }
                                else {
                                    for (int i = 0; i < patAppointmentViewLists.size(); i++) {
                                        if (patAppointmentViewLists.get(i).getCancel_status().equals("Yes")) {
                                            cancelCount++;
                                        }
                                    }
                                    CalendarDay calendarDay = new CalendarDay(cc);
                                    if (cancelCount == 0) {
                                        calendarDay.setImageResource(R.drawable.upcoming_appt);
                                    }
                                    else {
                                        if (cancelCount == patAppointmentViewLists.size()) {
                                            calendarDay.setImageResource(R.drawable.cancel_appt);
                                        }
                                        else {
                                            calendarDay.setImageResource(R.drawable.cancel_upcoming_appt);
                                        }
                                    }
                                    calendarDay.setLabelColor(R.color.blue_end);
                                    events.add(calendarDay);
                                }
                            }
                        }
                    }

                    Calendar testStartdates = Calendar.getInstance();
                    testStartdates.set(Calendar.DAY_OF_MONTH,1);

                    Calendar testEndDates = Calendar.getInstance();
                    testEndDates.add(Calendar.MONTH,2);
                    testEndDates.set(Calendar.DAY_OF_MONTH,testEndDates.getActualMaximum(Calendar.DAY_OF_MONTH));

                    SimpleDateFormat dayNameFormat3 = new SimpleDateFormat("EEE", Locale.ENGLISH);
                    do {
                        String text;
                        Calendar ddc = Calendar.getInstance();
                        if (testStartdates.getTime().equals(startDates.getTime())) {
                            text = dayNameFormat3.format(testStartdates.getTime());
                            if (text.contains("Fri")) {
                                ddc.setTime(testStartdates.getTime());
                                CalendarDay calendarDay = new CalendarDay(ddc);
                                calendarDay.setLabelColor(R.color.red_dark);
                                events.add(calendarDay);
                            }
                        }

                        testStartdates.add(Calendar.DAY_OF_MONTH,1);
                        text = dayNameFormat3.format(testStartdates.getTime());
                        if (text.contains("Fri")) {
                            ddc.setTime(testStartdates.getTime());
                            CalendarDay calendarDay = new CalendarDay(ddc);
                            calendarDay.setLabelColor(R.color.red_dark);
                            events.add(calendarDay);
                        }
                    }
                    while (!testStartdates.getTime().equals(testEndDates.getTime()));

                    calendarView.setCalendarDays(events);

                    try {
                        waitProgress.dismiss();
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                    loading = false;
                },1000);


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
        try {
            waitProgress.dismiss();
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PatAppointmentCalendar.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getPatAppData();
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