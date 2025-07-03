package ttit.com.shuvo.docdiary.appt_schedule;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.jakewharton.processphoenix.ProcessPhoenix;

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

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.adapters.TimeLineAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.arraylists.ApptScheduleInfoList;
import ttit.com.shuvo.docdiary.appt_schedule.arraylists.MonthSelectionList;
import ttit.com.shuvo.docdiary.progressbar.WaitProgress;

public class AppointmentSchedule extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    MaterialButton preMonthButton, nextMonthButton;
    TextView selectedMonthName;
    String monthYearName = "";
    HorizontalCalendar horizontalCalendar;

    ArrayList<MonthSelectionList> monthSelectionLists;
    int month_i = 0;

    RecyclerView timelineView;
    RecyclerView.LayoutManager layoutManager;
    TimeLineAdapter timeLineAdapter;

    ArrayList<ApptScheduleInfoList> apptScheduleInfoLists;
    String doc_id = "";
    String selected_date = "";
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";
    TextView noSchMess;
    ImageView backButton;

    Logger logger = Logger.getLogger(AppointmentSchedule.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.app_sch_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.app_schedule_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_appointment_schedule);
        circularProgressIndicator.setVisibility(View.GONE);

        preMonthButton = findViewById(R.id.previous_month_button);
        nextMonthButton = findViewById(R.id.next_month_button);
        selectedMonthName = findViewById(R.id.selected_month_name);
        timelineView = findViewById(R.id.timeline_recycle_view);
        noSchMess = findViewById(R.id.no_schedule_found_message);
        noSchMess.setVisibility(View.GONE);
        backButton = findViewById(R.id.back_logo_of_appointment_schedule);


        timelineView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        timelineView.setLayoutManager(layoutManager);

        monthSelectionLists = new ArrayList<>();
        apptScheduleInfoLists = new ArrayList<>();

        if (userInfoLists == null) {
            restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (userInfoLists.isEmpty()) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            }
            else {
                doc_id = userInfoLists.get(0).getDoc_id();
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);
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

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
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

                getAppointmentData();

            }

        });

        preMonthButton.setOnClickListener(v -> {
            if (month_i > 0) {
                month_i = month_i - 1;
                Animation animation = AnimationUtils.loadAnimation(AppointmentSchedule.this,R.anim.translate_p);
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
                Animation animation = AnimationUtils.loadAnimation(AppointmentSchedule.this,R.anim.translate_n);
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
    protected void onResume() {
        super.onResume();
        if (loading != null) {
            if (!loading) {
                getAppointmentData();
            }
        }
        else {
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

    public void getAppointmentData() {
        loading = true;
        WaitProgress waitProgress = new WaitProgress();
        try {
            waitProgress.show(getSupportFragmentManager(), "WaitBar");
            waitProgress.setCancelable(false);
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }

        conn = false;
        connected = false;

        apptScheduleInfoLists = new ArrayList<>();

        String url = pre_url_api+"appointment_schedule/getAppointmentData?doc_id="+doc_id+"&pdate="+selected_date;
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

                        String adm_date = apptDataInfo.getString("adm_date")
                                .equals("null") ? "" : apptDataInfo.getString("adm_date");
                        String schedule_time = apptDataInfo.getString("schedule_time")
                                .equals("null") ? "" : apptDataInfo.getString("schedule_time");

                        String patient_data = apptDataInfo.getString("patient_data")
                                .equals("null") ? "" :apptDataInfo.getString("patient_data");
                        String depts_id = apptDataInfo.getString("depts_id")
                                .equals("null") ? "" :apptDataInfo.getString("depts_id");
                        String depts_name = apptDataInfo.getString("depts_name")
                                .equals("null") ? "" :apptDataInfo.getString("depts_name");
                        String pat_name = apptDataInfo.getString("pat_name")
                                .equals("null") ? "" :apptDataInfo.getString("pat_name");
                        String pat_code = apptDataInfo.getString("pat_code")
                                .equals("null") ? "" :apptDataInfo.getString("pat_code");
                        String pat_age_now = apptDataInfo.getString("pat_age_now")
                                .equals("null") ? "" :apptDataInfo.getString("pat_age_now");
                        String pfn_fee_name = apptDataInfo.getString("pfn_fee_name")
                                .equals("null") ? "" :apptDataInfo.getString("pfn_fee_name");
                        String doc_video_link = apptDataInfo.getString("doc_video_link")
                                .equals("null") ? "" :apptDataInfo.getString("doc_video_link");
                        String ts_video_conf_flag = apptDataInfo.getString("ts_video_conf_flag")
                                .equals("null") ? "0" :apptDataInfo.getString("ts_video_conf_flag");
                        String pmm = apptDataInfo.getString("pmm")
                                .equals("null") ? "0" :apptDataInfo.getString("pmm");
                        String is_ranked = apptDataInfo.getString("is_ranked")
                                .equals("null") ? "0" :apptDataInfo.getString("is_ranked");
                        String pph_progress = apptDataInfo.getString("pph_progress")
                                .equals("null") ? "0" :apptDataInfo.getString("pph_progress");
                        String pfn_id = apptDataInfo.getString("pfn_id")
                                .equals("null") ? "" :apptDataInfo.getString("pfn_id");
                        String ph_cat_id = apptDataInfo.getString("ph_cat_id")
                                .equals("null") ? "" :apptDataInfo.getString("ph_cat_id");
                        String ad_id = apptDataInfo.getString("ad_id")
                                .equals("null") ? "" :apptDataInfo.getString("ad_id");
                        String ad_prm_id = apptDataInfo.getString("ad_prm_id")
                                .equals("null") ? "" :apptDataInfo.getString("ad_prm_id");
                        String ad_prd_id = apptDataInfo.getString("ad_prd_id")
                                .equals("null") ? "" :apptDataInfo.getString("ad_prd_id");

                        apptScheduleInfoLists.add(new ApptScheduleInfoList(adm_date, schedule_time, "",pat_name,patient_data,pat_code,selected_date,
                                pat_age_now,pfn_fee_name, doc_video_link, ts_video_conf_flag, pmm,depts_id,depts_name,is_ranked,pph_progress,
                                pfn_id, ph_cat_id, ad_id, ad_prm_id, ad_prd_id));

                    }
                }
                connected = true;
                updateInterface(waitProgress);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface(waitProgress);
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface(waitProgress);
        });

        requestQueue.add(apptDataReq);
    }

    private void updateInterface(WaitProgress waitProgress) {
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
                if (apptScheduleInfoLists.isEmpty()) {
                    noSchMess.setVisibility(View.VISIBLE);
                }
                else {
                    noSchMess.setVisibility(View.GONE);
                }
                timeLineAdapter = new TimeLineAdapter(apptScheduleInfoLists,AppointmentSchedule.this);
                timelineView.setAdapter(timeLineAdapter);
                loading = false;
            }
            else {
                alertMessage(waitProgress);
            }
        }
        else {
            alertMessage(waitProgress);
        }
    }

    public void alertMessage(WaitProgress waitProgress) {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AppointmentSchedule.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getAppointmentData();
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