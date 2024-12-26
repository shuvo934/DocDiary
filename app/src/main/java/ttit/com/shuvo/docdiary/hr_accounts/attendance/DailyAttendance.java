package ttit.com.shuvo.docdiary.hr_accounts.attendance;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ttit.com.shuvo.docdiary.appt_schedule.arraylists.MonthSelectionList;
import ttit.com.shuvo.docdiary.hr_accounts.attendance.adapters.DailyAttendanceAdapter;
import ttit.com.shuvo.docdiary.hr_accounts.attendance.araylists.DailyAttendanceList;
import ttit.com.shuvo.docdiary.hr_accounts.attendance.araylists.DepartmentList;
import ttit.com.shuvo.docdiary.hr_accounts.attendance.araylists.DivisionList;
import ttit.com.shuvo.docdiary.hr_accounts.attendance.araylists.EmployeeList;
import ttit.com.shuvo.docdiary.progressbar.WaitProgress;

public class DailyAttendance extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView backButton;

    AppCompatAutoCompleteTextView divisionSelect;
    ArrayList<DivisionList> divisionLists;

    TextInputLayout departmentSelectLay;
    AppCompatAutoCompleteTextView departmentSelect;
    ArrayList<DepartmentList> departmentLists;
    ArrayList<DepartmentList> filteredDepartmentLists;

    TextInputLayout employeeSelectLay;
    AppCompatAutoCompleteTextView employeeSelect;
    ArrayList<EmployeeList> employeeLists;
    ArrayList<EmployeeList> filteredEmpLists;

    MaterialButton preMonthButton, nextMonthButton;
    TextView selectedMonthName;
    String monthYearName = "";
    HorizontalCalendar horizontalCalendar;

    ArrayList<MonthSelectionList> monthSelectionLists;
    int month_i = 0;

    RecyclerView daListView;
    RecyclerView.LayoutManager layoutManager;
    DailyAttendanceAdapter dailyAttendanceAdapter;

    ArrayList<DailyAttendanceList> dailyAttendanceLists;
    ArrayList<DailyAttendanceList> filteredList;

    TextView noDa;

    String selected_div_id = "";
    String selected_dept_id = "";
    String selected_emp_id = "";
    String selected_date = "";

    WaitProgress waitProgress = new WaitProgress();
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = true;
    String parsing_message = "";

    Logger logger = Logger.getLogger(DailyAttendance.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(DailyAttendance.this,R.color.clouds));
        setContentView(R.layout.activity_daily_attendance);

        fullLayout = findViewById(R.id.daily_attendance_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_daily_attendance);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_daily_attendance);

        divisionSelect = findViewById(R.id.division_select_for_da);
        divisionLists = new ArrayList<>();

        departmentSelectLay = findViewById(R.id.spinner_layout_department_select_for_da);
        departmentSelectLay.setEnabled(false);
        departmentSelect = findViewById(R.id.department_select_for_da);
        departmentLists = new ArrayList<>();
        filteredDepartmentLists = new ArrayList<>();

        employeeSelectLay = findViewById(R.id.spinner_layout_emp_select_for_da);
        employeeSelectLay.setEnabled(false);
        employeeSelect = findViewById(R.id.emp_select_for_da);
        employeeLists = new ArrayList<>();
        filteredEmpLists = new ArrayList<>();

        preMonthButton = findViewById(R.id.previous_month_button_da);
        nextMonthButton = findViewById(R.id.next_month_button_da);
        selectedMonthName = findViewById(R.id.selected_month_name_da);

        daListView = findViewById(R.id.daily_attendance_check_recycle_view);
        noDa = findViewById(R.id.no_attendance_found_message_da);
        noDa.setVisibility(View.GONE);

        monthSelectionLists = new ArrayList<>();
        dailyAttendanceLists = new ArrayList<>();
        filteredList = new ArrayList<>();

        daListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        daListView.setLayoutManager(layoutManager);

        nextMonthButton.setBackgroundColor(Color.parseColor("#b2bec3"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        selected_date = simpleDateFormat.format(Calendar.getInstance().getTime());
        selected_date = selected_date.toUpperCase();

        Calendar startDates = Calendar.getInstance();

        Calendar endDates = Calendar.getInstance();

        Calendar today_calender  = Calendar.getInstance();

        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH);
        SimpleDateFormat monthNoFormat = new SimpleDateFormat("MM", Locale.ENGLISH);
        monthYearName = monthYearFormat.format(today_calender.getTime());
        selectedMonthName.setText(monthYearName);

        startDates.add(Calendar.MONTH, -2);
        monthSelectionLists.add(new MonthSelectionList(monthNoFormat.format(startDates.getTime()),monthYearFormat.format(startDates.getTime())));

        startDates.add(Calendar.MONTH, 1);
        monthSelectionLists.add(new MonthSelectionList(monthNoFormat.format(startDates.getTime()),monthYearFormat.format(startDates.getTime())));

        startDates.add(Calendar.MONTH, -1);
//        monthSelectionLists.add(new MonthSelectionList(monthNoFormat.format(today_calender.getTime()),monthYearName));
        monthSelectionLists.add(new MonthSelectionList(monthNoFormat.format(endDates.getTime()),monthYearFormat.format(endDates.getTime())));

        for (int i = 0; i < monthSelectionLists.size(); i++) {
            if (monthYearName.equals(monthSelectionLists.get(i).getMonthName())) {
                month_i = i;
            }
        }

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView_da)
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

        horizontalCalendar.setElevation(2);
        horizontalCalendar.show();

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

                getDailyAttendance();
            }

        });

        preMonthButton.setOnClickListener(v -> {
            if (month_i > 0) {
                month_i = month_i - 1;
                Animation animation = AnimationUtils.loadAnimation(DailyAttendance.this,R.anim.translate_p);
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
                else if (month_i < monthSelectionLists.size() - 1) {
                    nextMonthButton.setBackgroundColor(getColor(R.color.light_green));
                }
            }
        });

        nextMonthButton.setOnClickListener(v -> {
            if (month_i < monthSelectionLists.size() - 1) {
                month_i = month_i + 1;
                Animation animation = AnimationUtils.loadAnimation(DailyAttendance.this,R.anim.translate_n);
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

        divisionSelect.setOnItemClickListener((parent, view, position, id) -> {
            selected_dept_id = "";
            departmentSelect.setText("");
            departmentSelectLay.setEnabled(false);
            filteredDepartmentLists = new ArrayList<>();

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);
            filteredEmpLists = new ArrayList<>();

            filteredList = new ArrayList<>();

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <divisionLists.size(); i++) {
                if (name.equals(divisionLists.get(i).getDivm_name())) {

                    selected_div_id = divisionLists.get(i).getDivm_id();
                }
            }

            if (selected_div_id.isEmpty()) {
                divisionSelect.setText("");
            }

            filterAll();

        });

        departmentSelect.setOnItemClickListener((parent, view, position, id) -> {
            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);
            filteredEmpLists = new ArrayList<>();


            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <filteredDepartmentLists.size(); i++) {
                if (name.equals(filteredDepartmentLists.get(i).getDept_name())) {

                    selected_dept_id = filteredDepartmentLists.get(i).getDept_id();
                }
            }
            if (selected_dept_id.isEmpty()) {
                departmentSelect.setText("");
            }

            filterAll();

        });

        employeeSelect.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            for (int i = 0; i <filteredEmpLists.size(); i++) {
                if (name.equals(filteredEmpLists.get(i).getEmp_name())) {

                    selected_emp_id = filteredEmpLists.get(i).getEmp_id();
                }
            }

            if (selected_emp_id.isEmpty()) {
                employeeSelect.setText("");
            }

            filterAll();
        });

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(DailyAttendance.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(DailyAttendance.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        getAllData();
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

    public void filterAll() {
        if (!selected_div_id.isEmpty()) {
            if (!selected_dept_id.isEmpty()) {
                if (!selected_emp_id.isEmpty()) {
                    filteredList = new ArrayList<>();

                    for (int i = 0; i < dailyAttendanceLists.size(); i++) {
                        if (selected_div_id.equals(dailyAttendanceLists.get(i).getJsm_divm_id())
                                && selected_dept_id.equals(dailyAttendanceLists.get(i).getJsm_dept_id())
                                && selected_emp_id.equals(dailyAttendanceLists.get(i).getEmp_id())) {

                            String emp_id = dailyAttendanceLists.get(i).getEmp_id();
                            String emp_code = dailyAttendanceLists.get(i).getEmp_code();
                            String emp_name = dailyAttendanceLists.get(i).getEmp_name();
                            String job_calling_title = dailyAttendanceLists.get(i).getJob_calling_title();
                            String jsm_divm_id = dailyAttendanceLists.get(i).getJsm_divm_id();
                            String jsm_dept_id = dailyAttendanceLists.get(i).getJsm_dept_id();
                            String jsm_desig_id = dailyAttendanceLists.get(i).getJsm_desig_id();
                            String osm_name = dailyAttendanceLists.get(i).getOsm_name();
                            String in_time = dailyAttendanceLists.get(i).getIn_time();
                            String out_time = dailyAttendanceLists.get(i).getOut_time();
                            String status = dailyAttendanceLists.get(i).getStatus();
                            String late_status = dailyAttendanceLists.get(i).getLate_status();
                            String early_status = dailyAttendanceLists.get(i).getEarly_status();

                            filteredList.add(new DailyAttendanceList(emp_id, emp_code,emp_name,job_calling_title,jsm_divm_id,
                                    jsm_dept_id,jsm_desig_id,osm_name,in_time,out_time,status,late_status,early_status));
                        }
                    }

                }
                else {
                    selected_emp_id = "";
                    employeeSelect.setText("");
                    employeeSelectLay.setEnabled(true);
                    filteredEmpLists = new ArrayList<>();

                    filteredList = new ArrayList<>();

                    filteredEmpLists.add(new EmployeeList("","","...","","","",""));
                    for (int i = 0; i < employeeLists.size(); i++) {
                        if (selected_div_id.equals(employeeLists.get(i).getJsm_divm_id()) && selected_dept_id.equals(employeeLists.get(i).getJsm_dept_id())) {
                            String emp_id = employeeLists.get(i).getEmp_id();
                            String emp_code = employeeLists.get(i).getEmp_code();
                            String emp_name = employeeLists.get(i).getEmp_name();
                            String jsm_divm_id = employeeLists.get(i).getJsm_divm_id();
                            String jsm_dept_id = employeeLists.get(i).getJsm_dept_id();
                            String jsm_desig_id = employeeLists.get(i).getJsm_desig_id();
                            String job_calling_title = employeeLists.get(i).getJob_calling_title();

                            filteredEmpLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title));
                        }
                    }

                    ArrayList<String> type1 = new ArrayList<>();
                    for(int i = 0; i < filteredEmpLists.size(); i++) {
                        type1.add(filteredEmpLists.get(i).getEmp_name());
                    }

                    ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(DailyAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                    employeeSelect.setAdapter(arrayAdapter1);

                    for (int i = 0; i < dailyAttendanceLists.size(); i++) {
                        if (selected_div_id.equals(dailyAttendanceLists.get(i).getJsm_divm_id()) && selected_dept_id.equals(dailyAttendanceLists.get(i).getJsm_dept_id())) {
                            String emp_id = dailyAttendanceLists.get(i).getEmp_id();
                            String emp_code = dailyAttendanceLists.get(i).getEmp_code();
                            String emp_name = dailyAttendanceLists.get(i).getEmp_name();
                            String job_calling_title = dailyAttendanceLists.get(i).getJob_calling_title();
                            String jsm_divm_id = dailyAttendanceLists.get(i).getJsm_divm_id();
                            String jsm_dept_id = dailyAttendanceLists.get(i).getJsm_dept_id();
                            String jsm_desig_id = dailyAttendanceLists.get(i).getJsm_desig_id();
                            String osm_name = dailyAttendanceLists.get(i).getOsm_name();
                            String in_time = dailyAttendanceLists.get(i).getIn_time();
                            String out_time = dailyAttendanceLists.get(i).getOut_time();
                            String status = dailyAttendanceLists.get(i).getStatus();
                            String late_status = dailyAttendanceLists.get(i).getLate_status();
                            String early_status = dailyAttendanceLists.get(i).getEarly_status();

                            filteredList.add(new DailyAttendanceList(emp_id, emp_code,emp_name,job_calling_title,jsm_divm_id,
                                    jsm_dept_id,jsm_desig_id,osm_name,in_time,out_time,status,late_status,early_status));
                        }
                    }

                }
            }
            else {
                selected_dept_id = "";
                departmentSelect.setText("");
                departmentSelectLay.setEnabled(true);
                filteredDepartmentLists = new ArrayList<>();

                selected_emp_id = "";
                employeeSelect.setText("");
                employeeSelectLay.setEnabled(false);
                filteredEmpLists = new ArrayList<>();

                filteredList = new ArrayList<>();

                filteredDepartmentLists.add(new DepartmentList("","...",""));
                for (int i = 0; i < departmentLists.size(); i++) {
                    if (selected_div_id.equals(departmentLists.get(i).getDivm_id())) {
                        String dept_id = departmentLists.get(i).getDept_id();
                        String dept_name = departmentLists.get(i).getDept_name();
                        String divm_id = departmentLists.get(i).getDivm_id();

                        filteredDepartmentLists.add(new DepartmentList(dept_id,dept_name,divm_id));
                    }
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < filteredDepartmentLists.size(); i++) {
                    type.add(filteredDepartmentLists.get(i).getDept_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DailyAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                departmentSelect.setAdapter(arrayAdapter);

                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < employeeLists.size(); i++) {
                    type1.add(employeeLists.get(i).getEmp_name());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(DailyAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                employeeSelect.setAdapter(arrayAdapter1);

                for (int i = 0; i < dailyAttendanceLists.size(); i++) {
                    if (selected_div_id.equals(dailyAttendanceLists.get(i).getJsm_divm_id())) {
                        String emp_id = dailyAttendanceLists.get(i).getEmp_id();
                        String emp_code = dailyAttendanceLists.get(i).getEmp_code();
                        String emp_name = dailyAttendanceLists.get(i).getEmp_name();
                        String job_calling_title = dailyAttendanceLists.get(i).getJob_calling_title();
                        String jsm_divm_id = dailyAttendanceLists.get(i).getJsm_divm_id();
                        String jsm_dept_id = dailyAttendanceLists.get(i).getJsm_dept_id();
                        String jsm_desig_id = dailyAttendanceLists.get(i).getJsm_desig_id();
                        String osm_name = dailyAttendanceLists.get(i).getOsm_name();
                        String in_time = dailyAttendanceLists.get(i).getIn_time();
                        String out_time = dailyAttendanceLists.get(i).getOut_time();
                        String status = dailyAttendanceLists.get(i).getStatus();
                        String late_status = dailyAttendanceLists.get(i).getLate_status();
                        String early_status = dailyAttendanceLists.get(i).getEarly_status();

                        filteredList.add(new DailyAttendanceList(emp_id, emp_code,emp_name,job_calling_title,jsm_divm_id,
                                jsm_dept_id,jsm_desig_id,osm_name,in_time,out_time,status,late_status,early_status));
                    }
                }

            }

            dailyAttendanceAdapter = new DailyAttendanceAdapter(DailyAttendance.this,filteredList);
            daListView.setAdapter(dailyAttendanceAdapter);
            if (filteredList.isEmpty()) {
                noDa.setVisibility(View.VISIBLE);
            }
            else {
                noDa.setVisibility(View.GONE);
            }
        }
        else {
            selected_dept_id = "";
            departmentSelect.setText("");
            departmentSelectLay.setEnabled(false);
            filteredDepartmentLists = new ArrayList<>();

            selected_emp_id = "";
            employeeSelect.setText("");
            employeeSelectLay.setEnabled(false);
            filteredEmpLists = new ArrayList<>();

            filteredList = new ArrayList<>();

            ArrayList<String> type = new ArrayList<>();
            for(int i = 0; i < departmentLists.size(); i++) {
                type.add(departmentLists.get(i).getDept_name());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DailyAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
            departmentSelect.setAdapter(arrayAdapter);

            ArrayList<String> type1 = new ArrayList<>();
            for(int i = 0; i < employeeLists.size(); i++) {
                type1.add(employeeLists.get(i).getEmp_name());
            }

            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(DailyAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
            employeeSelect.setAdapter(arrayAdapter1);

            dailyAttendanceAdapter = new DailyAttendanceAdapter(DailyAttendance.this,dailyAttendanceLists);
            daListView.setAdapter(dailyAttendanceAdapter);

            if (dailyAttendanceLists.isEmpty()) {
                noDa.setVisibility(View.VISIBLE);
            }
            else {
                noDa.setVisibility(View.GONE);
            }
        }
    }

    public void getAllData() {
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

        divisionLists = new ArrayList<>();
        departmentLists = new ArrayList<>();
        filteredDepartmentLists = new ArrayList<>();
        employeeLists = new ArrayList<>();
        filteredEmpLists = new ArrayList<>();
        dailyAttendanceLists = new ArrayList<>();
        filteredList = new ArrayList<>();

        String divUrl = pre_url_api+"hrm_dashboard/getDivision";
        String depUrl = pre_url_api+"hrm_dashboard/getDepartment";
        String empUrl = pre_url_api+"hrm_dashboard/getEmployee";
        String daUrl = pre_url_api+"hrm_dashboard/getDailyAttendance?p_date="+selected_date;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest daReq = new StringRequest(Request.Method.GET, daUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String emp_code = info.getString("emp_code")
                                .equals("null") ? "" :info.getString("emp_code");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String osm_name = info.getString("osm_name")
                                .equals("null") ? "" :info.getString("osm_name");
                        String in_time = info.getString("in_time")
                                .equals("null") ? "" :info.getString("in_time");
                        String out_time = info.getString("out_time")
                                .equals("null") ? "" :info.getString("out_time");
                        String status = info.getString("status")
                                .equals("null") ? "" :info.getString("status");
                        String late_status = info.getString("late_status")
                                .equals("null") ? "" :info.getString("late_status");
                        String early_status = info.getString("early_status")
                                .equals("null") ? "" :info.getString("early_status");

                        dailyAttendanceLists.add(new DailyAttendanceList(emp_id,emp_code,emp_name,job_calling_title,jsm_divm_id,jsm_dept_id,jsm_desig_id,
                                osm_name,in_time,out_time,status,late_status,early_status));
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

        StringRequest empReq = new StringRequest(Request.Method.GET, empUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String emp_code = info.getString("emp_code")
                                .equals("null") ? "" :info.getString("emp_code");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");

                        employeeLists.add(new EmployeeList(emp_id,emp_code,emp_name,jsm_divm_id,jsm_dept_id,jsm_desig_id,job_calling_title));
                    }
                }
                requestQueue.add(daReq);
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

        StringRequest depReq = new StringRequest(Request.Method.GET, depUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String dept_id = info.getString("dept_id")
                                .equals("null") ? "" :info.getString("dept_id");
                        String dept_name = info.getString("dept_name")
                                .equals("null") ? "" :info.getString("dept_name");
                        String dept_divm_id = info.getString("dept_divm_id")
                                .equals("null") ? "" :info.getString("dept_divm_id");

                        departmentLists.add(new DepartmentList(dept_id,dept_name,dept_divm_id));
                    }
                }
                requestQueue.add(empReq);
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

        StringRequest divReq = new StringRequest(Request.Method.GET, divUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    divisionLists.add(new DivisionList("","..."));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String divm_id = info.getString("divm_id")
                                .equals("null") ? "" :info.getString("divm_id");
                        String divm_name = info.getString("divm_name")
                                .equals("null") ? "" :info.getString("divm_name");

                        divisionLists.add(new DivisionList(divm_id,divm_name));
                    }
                }
                requestQueue.add(depReq);
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

        requestQueue.add(divReq);

    }

    private void updateInterface() {
        if (conn) {
            if (connected) {

                conn = false;
                connected = false;

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < divisionLists.size(); i++) {
                    type.add(divisionLists.get(i).getDivm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DailyAttendance.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                divisionSelect.setAdapter(arrayAdapter);

                filterAll();

                loading = false;
                try {
                    waitProgress.dismiss();
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DailyAttendance.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getAllData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
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

    public void getDailyAttendance() {
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

        dailyAttendanceLists = new ArrayList<>();
        filteredList = new ArrayList<>();

        String daUrl = pre_url_api+"hrm_dashboard/getDailyAttendance?p_date="+selected_date;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest daReq = new StringRequest(Request.Method.GET, daUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String emp_id = info.getString("emp_id")
                                .equals("null") ? "" :info.getString("emp_id");
                        String emp_code = info.getString("emp_code")
                                .equals("null") ? "" :info.getString("emp_code");
                        String emp_name = info.getString("emp_name")
                                .equals("null") ? "" :info.getString("emp_name");
                        String job_calling_title = info.getString("job_calling_title")
                                .equals("null") ? "" :info.getString("job_calling_title");
                        String jsm_divm_id = info.getString("jsm_divm_id")
                                .equals("null") ? "" :info.getString("jsm_divm_id");
                        String jsm_dept_id = info.getString("jsm_dept_id")
                                .equals("null") ? "" :info.getString("jsm_dept_id");
                        String jsm_desig_id = info.getString("jsm_desig_id")
                                .equals("null") ? "" :info.getString("jsm_desig_id");
                        String osm_name = info.getString("osm_name")
                                .equals("null") ? "" :info.getString("osm_name");
                        String in_time = info.getString("in_time")
                                .equals("null") ? "" :info.getString("in_time");
                        String out_time = info.getString("out_time")
                                .equals("null") ? "" :info.getString("out_time");
                        String status = info.getString("status")
                                .equals("null") ? "" :info.getString("status");
                        String late_status = info.getString("late_status")
                                .equals("null") ? "" :info.getString("late_status");
                        String early_status = info.getString("early_status")
                                .equals("null") ? "" :info.getString("early_status");

                        dailyAttendanceLists.add(new DailyAttendanceList(emp_id,emp_code,emp_name,job_calling_title,jsm_divm_id,jsm_dept_id,jsm_desig_id,
                                osm_name,in_time,out_time,status,late_status,early_status));
                    }
                }
                connected = true;
                updateLayout();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateLayout();
        });

        requestQueue.add(daReq);

    }

    private void updateLayout() {
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;

                filterAll();

                loading = false;
                try {
                    waitProgress.dismiss();
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }

            }
            else {
                alertMessageDa();
            }
        }
        else {
            alertMessageDa();
        }
    }

    public void alertMessageDa() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DailyAttendance.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getDailyAttendance();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
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