package ttit.com.shuvo.docdiary.unit_app_schedule;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.AppointmentSchedule;
import ttit.com.shuvo.docdiary.appt_schedule.adapters.TimeLineAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.arraylists.ApptScheduleInfoList;
import ttit.com.shuvo.docdiary.appt_schedule.arraylists.MonthSelectionList;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.HealthProgress;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.ChoiceDoctorList;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.ChoiceUnitList;
import ttit.com.shuvo.docdiary.patient_search.arraylists.PatientSearchList;
import ttit.com.shuvo.docdiary.progressbar.WaitProgress;
import ttit.com.shuvo.docdiary.unit_app_schedule.adapters.DoctorsListAdapter;
import ttit.com.shuvo.docdiary.unit_app_schedule.arraylists.DoctorAppSchList;
import ttit.com.shuvo.docdiary.unit_app_schedule.arraylists.UnitDoctorsList;

public class UnitWiseAppointment extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    MaterialButton preMonthButton, nextMonthButton;
    TextView selectedMonthName;
    String monthYearName = "";
    HorizontalCalendar horizontalCalendar;

    ArrayList<MonthSelectionList> monthSelectionLists;
    int month_i = 0;

    TextInputLayout unitSelectLay;
    AppCompatAutoCompleteTextView unitSelect;
    ArrayList<ChoiceUnitList> choiceUnitLists;

    TextInputLayout docSelectLay;
    AppCompatAutoCompleteTextView docSelect;
    ArrayList<ChoiceDoctorList> choiceDoctorLists;

    TextView unitSchCount;
    TextView unitDocCount;

    RecyclerView docListView;
    RecyclerView.LayoutManager layoutManager;
    DoctorsListAdapter doctorsListAdapter;

    ArrayList<UnitDoctorsList> unitDoctorsLists;
    ArrayList<UnitDoctorsList> filteredList;
    String deptd_id = "";
    String selected_depts_id = "";
    String selected_doc_id = "";
    String selected_date = "";
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";
    TextView noDocsMess;
    ImageView backButton;
    WaitProgress waitProgress = new WaitProgress();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_wise_appointment);

        fullLayout = findViewById(R.id.unit_app_schedule_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_unit_wise_appointment_schedule);
        circularProgressIndicator.setVisibility(View.GONE);

        preMonthButton = findViewById(R.id.previous_month_button_unit_app_sch);
        nextMonthButton = findViewById(R.id.next_month_button_unit_app_sch);
        selectedMonthName = findViewById(R.id.selected_month_name_unit_app_sch);
        docListView = findViewById(R.id.timeline_recycle_view_unit_app_sch);
        noDocsMess = findViewById(R.id.no_doctor_found_message_unit_app_sch);
        noDocsMess.setVisibility(View.GONE);
        backButton = findViewById(R.id.back_logo_of_unit_appointment_schedule);

        unitSelect = findViewById(R.id.unit_select_for_unit_app_sch);
        unitSelectLay = findViewById(R.id.spinner_layout_unit_select_for_unit_app_sch);
        choiceUnitLists = new ArrayList<>();

        docSelectLay = findViewById(R.id.spinner_layout_doctor_select_for_unit_app_sch);
        docSelect = findViewById(R.id.doctor_select_for_unit_app_sch);
        choiceDoctorLists = new ArrayList<>();

        unitSchCount = findViewById(R.id.unit_app_count_un_app);
        unitDocCount = findViewById(R.id.unit_docs_count_un_app);

        docListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        docListView.setLayoutManager(layoutManager);

        monthSelectionLists = new ArrayList<>();
        unitDoctorsLists = new ArrayList<>();
        filteredList = new ArrayList<>();

        if (userInfoLists == null) {
            restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (userInfoLists.size() == 0) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            }
            else {
                deptd_id = userInfoLists.get(0).getDeptd_id();
                selected_depts_id = userInfoLists.get(0).getDepts_id();
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        selected_date = simpleDateFormat.format(Calendar.getInstance().getTime());
        selected_date = selected_date.toUpperCase();

        Calendar startDates = Calendar.getInstance();
        startDates.add(Calendar.MONTH, -1);

        Calendar endDates = Calendar.getInstance();
        endDates.add(Calendar.MONTH, 1);

        Calendar today_calender  = Calendar.getInstance();

        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);
        SimpleDateFormat monthNoFormat = new SimpleDateFormat("MM", Locale.ENGLISH);
        monthYearName = monthYearFormat.format(today_calender.getTime());
        selectedMonthName.setText(monthYearName);

        monthSelectionLists.add(new MonthSelectionList(monthNoFormat.format(startDates.getTime()),monthYearFormat.format(startDates.getTime())));
        monthSelectionLists.add(new MonthSelectionList(monthNoFormat.format(today_calender.getTime()),monthYearName));
        monthSelectionLists.add(new MonthSelectionList(monthNoFormat.format(endDates.getTime()),monthYearFormat.format(endDates.getTime())));

        for (int i = 0; i < monthSelectionLists.size(); i++) {
            if (monthYearName.equals(monthSelectionLists.get(i).getMonthName())) {
                month_i = i;
            }
        }

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView_unit_app_sch)
                .range(startDates,endDates)
                .datesNumberOnScreen(5)
                .configure()
                .showTopText(false)
                .textSize(12,12,12)
                .selectedDateBackground(getRoundRect())
                .end()
                .defaultSelectedDate(today_calender)
                .build();


        horizontalCalendar.selectDate(today_calender,false);
        horizontalCalendar.goToday(false);

//        Calendar calendar = horizontalCalendar.getSelectedDate();
        horizontalCalendar.setElevation(2);
        horizontalCalendar.show();

//        System.out.println(calendar.getTime());

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                System.out.println("Position: " + position);
                Date c = date.getTime();
//                SimpleDateFormat sdf = new SimpleDateFormat("dd, MMMM, yyyy", Locale.ENGLISH);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);

                selected_date = simpleDateFormat.format(c);
                selected_date = selected_date.toUpperCase();

                monthYearName = monthYearFormat.format(c);
                selectedMonthName.setText(monthYearName);

                System.out.println(horizontalCalendar.getSelectedDate().getTime());

                for (int i = 0; i < monthSelectionLists.size(); i++) {
                    System.out.println(monthSelectionLists.get(i).getMonthName());
                    if (monthYearName.equals(monthSelectionLists.get(i).getMonthName())) {
                        month_i = i;
                        if (i == 0) {
                            preMonthButton.setBackgroundColor(Color.parseColor("#b2bec3"));
                            nextMonthButton.setBackgroundColor(getColor(R.color.light_green));
                            break;
                        }
                        else if (i == (monthSelectionLists.size() - 1)) {
                            preMonthButton.setBackgroundColor(getColor(R.color.light_green));
                            nextMonthButton.setBackgroundColor(Color.parseColor("#b2bec3"));
                            break;
                        }
                        else {
                            preMonthButton.setBackgroundColor(getColor(R.color.light_green));
                            nextMonthButton.setBackgroundColor(getColor(R.color.light_green));
                            break;
                        }
                    }
                }

                if (!selected_depts_id.isEmpty()) {
                    docSelect.setText("");
                    docSelectLay.setHint("Select Doctor/Therapist");
                    selected_doc_id = "";
                    getDoctors(selected_depts_id,2);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please Select Unit",Toast.LENGTH_SHORT).show();
                }


            }

        });

        preMonthButton.setOnClickListener(v -> {
            if (month_i > 0) {
                month_i = month_i - 1;
                Animation animation = AnimationUtils.loadAnimation(UnitWiseAppointment.this,R.anim.translate_p);
                animation.reset();
                monthYearName = monthSelectionLists.get(month_i).getMonthName();
                selectedMonthName.setText("");
                selectedMonthName.setText(monthYearName);
                selectedMonthName.clearAnimation();
                selectedMonthName.startAnimation(animation);
                Calendar cal = horizontalCalendar.getSelectedDate();
                System.out.println(cal.getTime());
                cal.add(Calendar.MONTH,-1);
                if (horizontalCalendar.contains(cal)) {
                    horizontalCalendar.selectDate(cal,false);
                }
                else {
                    horizontalCalendar.selectDate(startDates,false);
                }

                Calendar selectedCalender = horizontalCalendar.getSelectedDate();
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);
                selected_date = simpleDateFormat1.format(selectedCalender.getTime());
                selected_date = selected_date.toUpperCase();

                if (month_i == 0) {
                    preMonthButton.setBackgroundColor(Color.parseColor("#b2bec3"));
                    nextMonthButton.setBackgroundColor(getColor(R.color.light_green));
                }
                else if (month_i < monthSelectionLists.size() - 1){
                    nextMonthButton.setBackgroundColor(getColor(R.color.light_green));
                }
            }
        });

        nextMonthButton.setOnClickListener(v -> {
            if (month_i < monthSelectionLists.size() - 1) {
                month_i = month_i + 1;
                Animation animation = AnimationUtils.loadAnimation(UnitWiseAppointment.this,R.anim.translate_n);
                animation.reset();
                monthYearName = monthSelectionLists.get(month_i).getMonthName();
                selectedMonthName.setText("");
                selectedMonthName.setText(monthYearName);
                selectedMonthName.clearAnimation();
                selectedMonthName.startAnimation(animation);
                Calendar cal = horizontalCalendar.getSelectedDate();
                cal.add(Calendar.MONTH,1);
                if (horizontalCalendar.contains(cal)) {
                    horizontalCalendar.selectDate(cal,false);
                }
                else {
                    horizontalCalendar.selectDate(endDates,false);
                }

                Calendar selectedCalender = horizontalCalendar.getSelectedDate();
                SimpleDateFormat simpleDateFormat12 = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);
                selected_date = simpleDateFormat12.format(selectedCalender.getTime());
                selected_date = selected_date.toUpperCase();

                if (month_i == monthSelectionLists.size() - 1) {
                    nextMonthButton.setBackgroundColor(Color.parseColor("#b2bec3"));
                    preMonthButton.setBackgroundColor(getColor(R.color.light_green));
                }
                else if (month_i > 0) {
                    preMonthButton.setBackgroundColor(getColor(R.color.light_green));
                }
            }
        });

        backButton.setOnClickListener(v -> finish());

        unitSelect.setOnItemClickListener((parent, view, position, id) -> {
            docSelect.setText("");
            docSelectLay.setHint("Select Doctor/Therapist");
            selected_doc_id = "";
            selected_depts_id = "";
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <choiceUnitLists.size(); i++) {
                if (name.equals(choiceUnitLists.get(i).getDetps_name())) {

                    selected_depts_id = choiceUnitLists.get(i).getPph_depts_id();
                }
            }
            unitSelectLay.setHint("Unit");
            getDoctors(selected_depts_id,2);

        });

        docSelect.setOnItemClickListener((parent, view, position, id) -> {
            selected_doc_id = "";
            String name = parent.getItemAtPosition(position).toString();
            for (int i = 0; i <choiceDoctorLists.size(); i++) {
                if (name.equals(choiceDoctorLists.get(i).getDoc_name())) {
                    if (name.equals("...")) {
                        docSelect.setText("");
                        docSelectLay.setHint("Select Doctor/Therapist");
                    }
                    else {
                        docSelectLay.setHint("Doctor/Therapist");
                    }
                    selected_doc_id = choiceDoctorLists.get(i).getPph_doc_id();
                }
            }

            filter(selected_doc_id);
        });

        getUnits();
    }

    private void filter(String text) {
        filteredList = new ArrayList<>();
        for (UnitDoctorsList item : unitDoctorsLists) {
            if (text.isEmpty()) {
                filteredList.add((item));
            }
            else {
                if (item.getDoc_id().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add((item));
                }
            }

        }

        if (filteredList.size() == 0) {
            noDocsMess.setVisibility(View.VISIBLE);
        }
        else {
            noDocsMess.setVisibility(View.GONE);
        }
        try {
            doctorsListAdapter.filterList(filteredList);
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }
    }

    public Drawable getRoundRect() {
        RoundRectShape rectShape = new RoundRectShape(new float[]{
                20, 20, 20, 20,
                20, 20, 20, 20
        }, null, null);

        ShapeDrawable shapeDrawable = new ShapeDrawable(rectShape);
        shapeDrawable.getPaint().setColor(getColor(R.color.light_green));
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        return shapeDrawable;
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

    public void getUnits() {
        loading = true;
        try {
            waitProgress.show(getSupportFragmentManager(), "WaitBar");
            waitProgress.setCancelable(false);
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }

        conn = false;
        connected = false;

        choiceUnitLists = new ArrayList<>();

        String url = pre_url_api+"doctors_app_schedule/getUnitList?deptd_id="+deptd_id+"";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest apptDataReq = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject apptDataInfo = array.getJSONObject(i);

                        String depts_id = apptDataInfo.getString("depts_id")
                                .equals("null") ? "" :apptDataInfo.getString("depts_id");
                        String depts_name = apptDataInfo.getString("depts_name")
                                .equals("null") ? "" :apptDataInfo.getString("depts_name");

                        choiceUnitLists.add(new ChoiceUnitList(depts_id,depts_name));
                    }
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

        requestQueue.add(apptDataReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {

                conn = false;
                connected = false;

                String name = "";
                if (!selected_depts_id.isEmpty()) {
                    for(int i = 0; i < choiceUnitLists.size(); i++) {
                        if (selected_depts_id.equals(choiceUnitLists.get(i).getPph_depts_id())) {
                            name = choiceUnitLists.get(i).getDetps_name();
                        }
                    }
                }
                unitSelect.setText(name);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < choiceUnitLists.size(); i++) {
                    type.add(choiceUnitLists.get(i).getDetps_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UnitWiseAppointment.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                unitSelect.setAdapter(arrayAdapter);

                if (selected_depts_id.isEmpty()) {
                    try {
                        waitProgress.dismiss();
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }

                    loading = false;
                }
                else {
                    getDoctors(selected_depts_id,1);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(UnitWiseAppointment.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getUnits();
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

    public void getDoctors(String id, int choice) {
        loading = true;
        if (choice == 2) {
            try {
                waitProgress.show(getSupportFragmentManager(), "WaitBar");
                waitProgress.setCancelable(false);
            }
            catch (Exception e) {
                restart("App is paused for a long time. Please Start the app again.");
            }
        }

        conn = false;
        connected = false;

        unitDoctorsLists = new ArrayList<>();
        choiceDoctorLists = new ArrayList<>();

        String url = pre_url_api+"doctors_app_schedule/getDocWithSchedule?pdate="+selected_date+"&depts_id="+id+"";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest apptDataReq = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    if (array.length() != 0) {
                        if (array.length() > 1) {
                            choiceDoctorLists.add(new ChoiceDoctorList("","..."));
                        }
                    }
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject apptDataInfo = array.getJSONObject(i);
                        String doc_id = apptDataInfo.getString("doc_id")
                                .equals("null") ? "" :apptDataInfo.getString("doc_id");
                        String doc_name = apptDataInfo.getString("doc_name")
                                .equals("null") ? "" :apptDataInfo.getString("doc_name");
                        String doc_code = apptDataInfo.getString("doc_code")
                                .equals("null") ? "" :apptDataInfo.getString("doc_code");
                        String app_count = apptDataInfo.getString("app_count")
                                .equals("null") ? "" :apptDataInfo.getString("app_count");

                        String app_list = apptDataInfo.getString("app_list");
                        JSONArray array1 = new JSONArray(app_list);

                        ArrayList<DoctorAppSchList> doctorAppSchLists = new ArrayList<>();
                        boolean found = false;

                        for (int j = 0; j < array1.length(); j++) {
                            JSONObject info = array1.getJSONObject(j);

                            String adm_date = info.getString("adm_date")
                                    .equals("null") ? "" :info.getString("adm_date");
                            String schedule_time = info.getString("schedule_time")
                                    .equals("null") ? "" :info.getString("schedule_time");
                            String patient_data = info.getString("patient_data")
                                    .equals("null") ? "" :info.getString("patient_data");
                            String pat_name = info.getString("pat_name")
                                    .equals("null") ? "" :info.getString("pat_name");
                            String pat_code = info.getString("pat_code")
                                    .equals("null") ? "" :info.getString("pat_code");
                            String pfn_fee_name = info.getString("pfn_fee_name")
                                    .equals("null") ? "" :info.getString("pfn_fee_name");

                            String pos = "0";

                            String appt_date = adm_date;
                            appt_date = appt_date + " " + schedule_time;
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
                            Date appDate;
                            try {
                                appDate = simpleDateFormat.parse(appt_date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            if (isNumeric(patient_data)) {
                                if (appDate != null) {
                                    Calendar calendar = Calendar.getInstance();
                                    Date nowDate = calendar.getTime();
                                    if (!appDate.before(nowDate)) {
                                        if (!found) {
                                            pos = String.valueOf(j);
                                            found = true;
                                        }
                                    }
                                }
                            }


                            doctorAppSchLists.add(new DoctorAppSchList(adm_date,schedule_time,patient_data,pat_name,
                                    pat_code,pfn_fee_name,pos));

                        }

                        doc_name = doc_name +" ("+doc_code+")";
                        unitDoctorsLists.add(new UnitDoctorsList(doc_id,doc_name,doc_code,app_count,doctorAppSchLists));
                        choiceDoctorLists.add(new ChoiceDoctorList(doc_id,doc_name));
                    }
                }

                connected = true;
                updateLayout();
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateLayout();
        });

        apptDataReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(apptDataReq);

    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void updateLayout() {
        if (conn) {
            if (connected) {
                try {
                    waitProgress.dismiss();
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }

                conn = false;
                connected = false;

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < choiceDoctorLists.size(); i++) {
                    type.add(choiceDoctorLists.get(i).getDoc_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UnitWiseAppointment.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                docSelect.setAdapter(arrayAdapter);

                if (unitDoctorsLists.size() == 0) {
                    noDocsMess.setVisibility(View.VISIBLE);
                    unitDocCount.setText("  0");
                    unitSchCount.setText("  0");
                }
                else {
                    noDocsMess.setVisibility(View.GONE);
                    String udc = "  " + unitDoctorsLists.size();
                    unitDocCount.setText(udc);
                    int cc = 0;
                    for (int i = 0; i < unitDoctorsLists.size(); i++) {
                        cc = cc + Integer.parseInt(unitDoctorsLists.get(i).getApp_count());
                    }
                    String usc = "  "+ cc;
                    unitSchCount.setText(usc);
                }
                doctorsListAdapter = new DoctorsListAdapter(unitDoctorsLists,UnitWiseAppointment.this);
                docListView.setAdapter(doctorsListAdapter);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(UnitWiseAppointment.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getDoctors(selected_depts_id,2);
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