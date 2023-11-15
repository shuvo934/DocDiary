package ttit.com.shuvo.docdiary.leave_schedule;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.leave_schedule.adapters.TimeSchAdapter;
import ttit.com.shuvo.docdiary.leave_schedule.arraylists.LeaveCalenderValues;
import ttit.com.shuvo.docdiary.leave_schedule.arraylists.LeaveDays;
import ttit.com.shuvo.docdiary.leave_schedule.arraylists.LeaveTimeSchedule;
import ttit.com.shuvo.docdiary.progressbar.WaitProgress;

public class DocLeave extends AppCompatActivity {

    ImageView backButton;
//    MaterialCalendarView materialCalendarView;
    CalendarView calendarView;
    String selected_date = "";
    String selected_date_day = "";

    ArrayList<LeaveTimeSchedule> leaveTimeSchedules;
//    ArrayList<LeaveDays> leaveDays;
    ArrayList<LeaveCalenderValues> leaveCalenderValues;
    String first_month = "";
    String last_month = "";
    String doc_id = "";

    WaitProgress waitProgress = new WaitProgress();
    private Boolean conn = false;
    private Boolean connected = false;

    String parsing_message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_leave);
        backButton = findViewById(R.id.back_logo_of_doc_leave);
        calendarView = findViewById(R.id.calendarView_new);

        leaveTimeSchedules = new ArrayList<>();
//        leaveDays = new ArrayList<>();
        leaveCalenderValues = new ArrayList<>();

        doc_id = userInfoLists.get(0).getDoc_id();

        backButton.setOnClickListener(v -> finish());

        Calendar firstMonthCalender = Calendar.getInstance();
        SimpleDateFormat month_format = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);
        first_month = month_format.format(firstMonthCalender.getTime());
        first_month = first_month.toUpperCase();
//        System.out.println("FIRST M: "+first_month);

        Calendar lastMonthCalender = Calendar.getInstance();
        lastMonthCalender.add(Calendar.MONTH,2);
        last_month = month_format.format(lastMonthCalender.getTime());
        last_month = last_month.toUpperCase();
//        System.out.println("LAST M: "+last_month);

        Calendar startDates = Calendar.getInstance();
        startDates.set(Calendar.DAY_OF_MONTH,1);
        startDates.set(Calendar.HOUR_OF_DAY,0);
        startDates.set(Calendar.MINUTE,0);
        startDates.set(Calendar.SECOND,0);
        startDates.set(Calendar.MILLISECOND,0);

        Calendar testStartdates = Calendar.getInstance();
        testStartdates.set(Calendar.DAY_OF_MONTH,1);
//        testStartdates.set(Calendar.HOUR_OF_DAY,0);
//        testStartdates.set(Calendar.MINUTE,0);
//        testStartdates.set(Calendar.SECOND,0);
//        testStartdates.set(Calendar.MILLISECOND,0);
//        System.out.println("Start Date : "+startDates.getTime().toString());

        Calendar endDates = Calendar.getInstance();
        endDates.add(Calendar.MONTH,2);
        endDates.set(Calendar.DAY_OF_MONTH,endDates.getActualMaximum(Calendar.DAY_OF_MONTH));

        Calendar testEndDates = Calendar.getInstance();
        testEndDates.add(Calendar.MONTH,2);
        testEndDates.set(Calendar.DAY_OF_MONTH,testEndDates.getActualMaximum(Calendar.DAY_OF_MONTH));
//        System.out.println("End Date : "+endDates.getTime().toString());

        List<Calendar> disabledDays = new ArrayList<>();
        do {
            SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
            String text = "";
            Calendar ddc = Calendar.getInstance();
            if (testStartdates.getTime().equals(startDates.getTime())) {
                text = dayNameFormat.format(testStartdates.getTime());
                if (text.contains("Fri")) {
                    ddc.setTime(testStartdates.getTime());
                    disabledDays.add(ddc);
                }
            }

            testStartdates.add(Calendar.DAY_OF_MONTH,1);
            text = dayNameFormat.format(testStartdates.getTime());
            if (text.contains("Fri")) {
                ddc.setTime(testStartdates.getTime());
                disabledDays.add(ddc);
            }
//            System.out.println(text);
//            System.out.println(testStartdates.getTime());
        }
        while (!testStartdates.getTime().equals(testEndDates.getTime()));



        calendarView.setDisabledDays(disabledDays);

        calendarView.setMinimumDate(startDates);
        calendarView.setMaximumDate(endDates);

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar ccc = eventDay.getCalendar();

            if (ccc.getTime().getTime() >= startDates.getTime().getTime() && ccc.getTime().getTime() <= endDates.getTime().getTime()) {
                for (int i = 0; i < leaveCalenderValues.size(); i++) {
                    if (leaveCalenderValues.get(i).getDate().equals(ccc.getTime())) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH);
                        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                        selected_date = simpleDateFormat.format(ccc.getTime());
                        selected_date_day = dayNameFormat.format(ccc.getTime());
                        boolean val = false;
                        if (leaveCalenderValues.get(i).isFullDay()) {
                            val = leaveCalenderValues.get(i).isFullDay();
                        }
                        ArrayList<String> times = leaveCalenderValues.get(i).getTimeLists();
                        String tot_sc = leaveCalenderValues.get(i).getTotal_sc();
                        showBottomSheetDialog(val,times,tot_sc);
                        break;
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getLeaveScheduleData();
    }

    private void showBottomSheetDialog(boolean fullDay, ArrayList<String> times,String tot_sc) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DocLeave.this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_leave_schedule);
        TextView nameType = bottomSheetDialog.findViewById(R.id.name_of_type_of_schedule);
        TextView today_date = bottomSheetDialog.findViewById(R.id.today_date_of_leave_time);
        TextView today_date_name = bottomSheetDialog.findViewById(R.id.today_date_name_of_leave_time);
        TextView full_day_off = bottomSheetDialog.findViewById(R.id.full_day_off_msg);
        RelativeLayout trl = bottomSheetDialog.findViewById(R.id.time_sc_relative_layout);

        RecyclerView offTimeView = bottomSheetDialog.findViewById(R.id.off_time_schedule_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DocLeave.this);

        assert nameType != null;
        assert today_date != null;
        assert today_date_name != null;
        assert full_day_off != null;
        assert offTimeView != null;
        assert trl != null;

        today_date.setText(selected_date);
        today_date_name.setText(selected_date_day);

        if (fullDay) {
            full_day_off.setVisibility(View.VISIBLE);
            String dd = "Full Day Off";
            full_day_off.setText(dd);
            trl.setVisibility(View.GONE);
            String tt = "Leave Day";
            nameType.setText(tt);
        }
        else {
            if (times.size() == Integer.parseInt(tot_sc)) {
                full_day_off.setVisibility(View.VISIBLE);
                String dd = "All Schedule Blocked";
                full_day_off.setText(dd);
                trl.setVisibility(View.GONE);
                String tt = "Blocked Schedule";
                nameType.setText(tt);
            }
            else {
                full_day_off.setVisibility(View.GONE);
                trl.setVisibility(View.VISIBLE);
                String tt = "Blocked Schedule";
                nameType.setText(tt);
            }

        }

        offTimeView.setHasFixedSize(true);
        offTimeView.setLayoutManager(layoutManager);
        TimeSchAdapter timeSchAdapter = new TimeSchAdapter(times,DocLeave.this);
        offTimeView.setAdapter(timeSchAdapter);

        bottomSheetDialog.show();
    }

    public void getLeaveScheduleData() {
        waitProgress.show(getSupportFragmentManager(), "WaitBar");
        waitProgress.setCancelable(false);
        conn = false;
        connected = false;

        leaveTimeSchedules = new ArrayList<>();
//        leaveDays = new ArrayList<>();
        leaveCalenderValues = new ArrayList<>();

        String timeUrl = pre_url_api+"leave_schedule/getTimeSchedule?doc_id="+doc_id+"&FIRST_MONTH="+first_month+"&LAST_MONTH="+last_month+"";
//        String dayUrl = pre_url_api+"leave_schedule/getLeaveDays?doc_id="+doc_id+"&FIRST_MONTH="+first_month+"&LAST_MONTH="+last_month+"";

        RequestQueue requestQueue = Volley.newRequestQueue(DocLeave.this);

//        StringRequest dayDataReq = new StringRequest(Request.Method.GET, dayUrl, response -> {
//            conn = true;
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                String items = jsonObject.getString("items");
//                String count = jsonObject.getString("count");
//                if (!count.equals("0")) {
//                    JSONArray array = new JSONArray(items);
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject apptDataInfo = array.getJSONObject(i);
//
//                        String la_id = apptDataInfo.getString("la_id")
//                                .equals("null") ? "" : apptDataInfo.getString("la_id");
//                        String from_date = apptDataInfo.getString("from_date")
//                                .equals("null") ? "" :apptDataInfo.getString("from_date");
//                        String to_date = apptDataInfo.getString("to_date")
//                                .equals("null") ? "" :apptDataInfo.getString("to_date");
//
//
//                        leaveDays.add(new LeaveDays(la_id, from_date,to_date));
//
//                    }
//                }
//                connected = true;
//                updateInterface();
//            }
//            catch (JSONException e) {
//                connected = false;
//                e.printStackTrace();
//                parsing_message = e.getLocalizedMessage();
//                updateInterface();
//            }
//        }, error -> {
//            conn = false;
//            connected = false;
//            error.printStackTrace();
//            parsing_message = error.getLocalizedMessage();
//            updateInterface();
//        });

        StringRequest timeDataReq = new StringRequest(Request.Method.GET, timeUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject timeSdInfo = array.getJSONObject(i);

                        String lad_id = timeSdInfo.getString("lad_id")
                                .equals("null") ? "" : timeSdInfo.getString("lad_id");
                        String lad_date = timeSdInfo.getString("lad_date")
                                .equals("null") ? "" :timeSdInfo.getString("lad_date");
                        String las_id = timeSdInfo.getString("las_id")
                                .equals("null") ? "" :timeSdInfo.getString("las_id");
                        String las_ts_id = timeSdInfo.getString("las_ts_id")
                                .equals("null") ? "" :timeSdInfo.getString("las_ts_id");
                        String las_notes = timeSdInfo.getString("las_notes")
                                .equals("null") ? "" :timeSdInfo.getString("las_notes");
                        String la_id = timeSdInfo.getString("la_id")
                                .equals("null") ? "" :timeSdInfo.getString("la_id");
                        String la_month = timeSdInfo.getString("la_month")
                                .equals("null") ? "" :timeSdInfo.getString("la_month");
                        String schedule_time = timeSdInfo.getString("schedule_time")
                                .equals("null") ? "" :timeSdInfo.getString("schedule_time");
                        String tot_sc = timeSdInfo.getString("tot_sc")
                                .equals("null") ? "0" : timeSdInfo.getString("tot_sc");

                        leaveTimeSchedules.add(new LeaveTimeSchedule(lad_id, lad_date,las_id,las_ts_id,las_notes,
                                la_id,la_month,schedule_time,tot_sc));

                    }
                }
//                requestQueue.add(dayDataReq);
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

        requestQueue.add(timeDataReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {

                conn = false;
                connected = false;
                List<EventDay> events = new ArrayList<>();
                List<Date> listOfDates = new ArrayList<>();
                List<Date> listOfTimeDates = new ArrayList<>();
                List<Calendar> l_d_date = new ArrayList<>();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);

                Handler handler = new Handler();
                handler.postDelayed(() -> {
//                    if (leaveDays.size() != 0) {
//                        for (int i = 0; i < leaveDays.size(); i++) {
//                            String from = leaveDays.get(i).getFrom_date();
//                            String to = leaveDays.get(i).getTo_date();
//
//                            Date fromDate = null;
//                            Date toDate = null;
//                            try {
//                                fromDate = dateFormat.parse(from);
//                                toDate = dateFormat.parse(to);
//                            } catch (ParseException e) {
//                                throw new RuntimeException(e);
//                            }
//
//                            listOfDates.addAll(getDaysBetweenDates(fromDate,toDate));
//                        }
//
////                  System.out.println(listOfDates.size());
//                        for (int i = 0; i < listOfDates.size(); i++) {
////                      System.out.println(listOfDates.get(i));
//                            Calendar cc = Calendar.getInstance();
//                            cc.setTime(listOfDates.get(i));
//                            leaveCalenderValues.add(new LeaveCalenderValues(cc.getTime(),true,new ArrayList<>(),"0"));
//                            l_d_date.add(cc);
//                            events.add(new EventDay(cc, R.drawable.baseline_work_off_24));
//                        }
//                    }

                    if (leaveTimeSchedules.size() != 0) {
                        int z = 0;
                        Date insertedDate = null;
                        for (int i = 0; i < leaveTimeSchedules.size(); i++) {
                            if (i==0) {
                                z = 1;
                            }
                            String lad_date = leaveTimeSchedules.get(i).getLad_date();
                            Date date = null;
                            try {
                                date = dateFormat.parse(lad_date);
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
                            ArrayList<String> time_schedule = new ArrayList<>();
                            String totsc = "0";

                            for (int i = 0; i < leaveTimeSchedules.size(); i++) {
                                String lad_date = leaveTimeSchedules.get(i).getLad_date();
                                Date date = null;
                                try {
                                    date = dateFormat.parse(lad_date);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                assert date != null;

                                if (listOfTimeDates.get(j).equals(date)) {
                                    String schedule_time = leaveTimeSchedules.get(i).getSchedule_time();
                                    time_schedule.add(schedule_time);
                                }
                                totsc = leaveTimeSchedules.get(i).getTot_sc();
                            }

                            leaveCalenderValues.add(new LeaveCalenderValues(cc.getTime(),false,time_schedule,totsc));
                            l_d_date.add(cc);
                            if (time_schedule.size() == Integer.parseInt(totsc)) {
                                events.add(new EventDay(cc, R.drawable.calender_timer_off_red));
                            }
                            else {
                                events.add(new EventDay(cc, R.drawable.calender_timer_off_24));
                            }
                        }
                    }

//                    for (int i = 0; i < l_d_date.size(); i++) {
//                        System.out.println(l_d_date.get(i).getTime());
//                    }
                    calendarView.setHighlightedDays(l_d_date);
                    calendarView.setEvents(events);
                    waitProgress.dismiss();
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
        waitProgress.dismiss();
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocLeave.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getLeaveScheduleData();
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

//    public static List<Date> getDaysBetweenDates(Date startdate, Date enddate)
//    {
//        List<Date> dates = new ArrayList<>();
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTime(startdate);
//
//        while (calendar.getTime().before(enddate) || calendar.getTime().equals(enddate))
//        {
//            Date result = calendar.getTime();
//            dates.add(result);
//            calendar.add(Calendar.DATE, 1);
//        }
//        return dates;
//    }


    // GARBAGE
    //-------------------------------------------------

    /*CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

    Event ev1 = new Event(Color.GREEN, Calendar.getInstance().getTimeInMillis(), "Some extra data that I want to store.");
    compactCalendarView.addEvent(ev1);

    Event ev2 = new Event(Color.GREEN, Calendar.getInstance().getTimeInMillis()+10800000);
    compactCalendarView.addEvent(ev2);
    EventsCalendar eventsCalendar = findViewById(R.id.eventsCalendar);

    eventsCalendar//set mode of Calendar
            .setMonthRange(startDates, endDates) //set starting month [start: Calendar] and ending month [end: Calendar]
            .setWeekStartDay(Calendar.SUNDAY, false) //set start day of the week as you wish [startday: Int, doReset: Boolean]
            .setCurrentSelectedDate(Calendar.getInstance()) //set current date and scrolls the calendar to the corresponding month of the selected date [today: Calendar]
            .setDateTextFontSize(16f) //set font size for dates
            .setMonthTitleFontSize(16f) //set font size for title of the calendar
            .setWeekHeaderFontSize(16f) //set font size for week names//set events on the EventsCalendar [c: Calendar]
            .build();

    Calendar instance = Calendar.getInstance();
    instance.add(Calendar.MONTH,-1);
    eventsCalendar.setToday(Calendar.getInstance());
    eventsCalendar.addEvent(Calendar.getInstance());
    eventsCalendar.setMonthRange(instance, Calendar.getInstance());
    eventsCalendar.disableDate(Calendar.getInstance());

    materialCalendarView.setMonthIndicatorVisible(false);
    materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
    materialCalendarView.state().edit()
            .setMinimumDate(startDates)
            .setMaximumDate(endDates)
            .commit();
     */
}