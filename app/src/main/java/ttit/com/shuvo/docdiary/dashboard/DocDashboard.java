package ttit.com.shuvo.docdiary.dashboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androchef.happytimer.countdowntimer.HappyTimer;
import com.androchef.happytimer.countdowntimer.NormalCountDownView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.AppointmentSchedule;
import ttit.com.shuvo.docdiary.dashboard.arraylists.UserInfoList;
import ttit.com.shuvo.docdiary.leave_schedule.DocLeave;
import ttit.com.shuvo.docdiary.login.DocLogin;
import ttit.com.shuvo.docdiary.profile.DocProfile;

public class DocDashboard extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    BottomNavigationView bottomNavigationView;
    TextView welcomeText;
    TextView docName;
    TextView docCenterName;
    TextView meetingTime;
    ImageView logOut;

    SharedPreferences sharedPreferences;
    public static final String LOGIN_ACTIVITY_FILE = "LOGIN_ACTIVITY_FILE_DOCDIARY";
    public static final String LOGIN_TF = "TRUE_FALSE";
    public static final String DOC_USER_CODE = "DOC_USER_CODE";
    public static final String DOC_USER_PASSWORD = "DOC_USER_PASSWORD";
    public static final String DOC_DATA_API = "DOC_DATA_API";
    String doc_code = "";
    public static String pre_url_api = "";
    public static ArrayList<UserInfoList> userInfoLists;
    String expiry_date = "";
    String first_login_flag = "";
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean userAvailable = false;
    String effected_date = "";
    String doctor_status = "";
    String parsing_message = "";
    LinearProgressIndicator horizontalProgressView;
//    Counter mCounter;
    long prv_seconds = 0;
    long progress_track_flag_value = 0;

    String last_schedule = "";
    String next_schedule = "";
    String patient_name = "";
    String doc_id = "";
    String doc_fl_flag = "";
    int progress_track = 100;
    int total_seconds = 1;
    NormalCountDownView normalCountDownView;
    TextView nextMeetingText;
    ImageView timerIcon;
    TextView patientName;
    private final Handler mHandler = new Handler();

    int total_meeting = 0;
    int completed_meeting = 0;
    int remaining_meeting = 0;

    TextView total;
    TextView completed;
    TextView remaining;

    int first_flag = 0;
    TabLayout tabLayout;
    LinearLayout tabFullLayout;
    CircularProgressIndicator tabCircularProgressIndicator;
    ImageView tabRefresh;
    TextView dateRangeText;
    String tabFirstDate = "";
    String tabEndDate = "";

    TextView totalSchedule;
    TextView blockedSchedule;
    int total_schedule = 0;
    int blocked_schedule = 0;

    String model = "";
    String brand = "";
    String ipAddress = "";
    String hostUserName = "";
    String osName = "";
    Bitmap bitmap;
    private boolean imageFound = false;
    ImageView docImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(DocDashboard.this,R.color.clouds));
        setContentView(R.layout.activity_doc_dashboard);

        fullLayout = findViewById(R.id.doc_dashboard_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_doc_dashboard);
        circularProgressIndicator.setVisibility(View.GONE);
        welcomeText = findViewById(R.id.greetings_text);
        docName = findViewById(R.id.doctor_name);
        docImage = findViewById(R.id.doc_profile_image_in_dashboard);
        docCenterName = findViewById(R.id.doctor_s_center_name);
        logOut = findViewById(R.id.log_out_doc);
        horizontalProgressView = findViewById(R.id.progressView_horizontal);

        normalCountDownView = findViewById(R.id.normalCountDownView);
        nextMeetingText = findViewById(R.id.next_meeting_text_view);
        timerIcon = findViewById(R.id.timer_icon_image);
        patientName = findViewById(R.id.next_meeting_patient_name);

        meetingTime = findViewById(R.id.next_meeting_time_dashboard);

        total = findViewById(R.id.total_meeting_count);
        completed = findViewById(R.id.completed_meeting_count);
        remaining = findViewById(R.id.remaining_meeting_count);

        tabLayout = findViewById(R.id.three_tab_layout);
        tabFullLayout = findViewById(R.id.tab_selected_full_layout);
        tabCircularProgressIndicator = findViewById(R.id.progress_indicator_doc_dashboard_tab_layout);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabRefresh = findViewById(R.id.tab_refresh_button);
        tabRefresh.setVisibility(View.GONE);
        dateRangeText = findViewById(R.id.date_range_tab_text);
        totalSchedule = findViewById(R.id.total_schedule_count);
        blockedSchedule = findViewById(R.id.blocked_schedule_count);

        sharedPreferences = getSharedPreferences(LOGIN_ACTIVITY_FILE,MODE_PRIVATE);
        doc_code = sharedPreferences.getString(DOC_USER_CODE,"");
        pre_url_api = sharedPreferences.getString(DOC_DATA_API,"");

        userInfoLists = new ArrayList<>();

        bottomNavigationView = findViewById(R.id.bottom_navigation_layout);
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        normalCountDownView.setOnTickListener(new HappyTimer.OnTickListener() {
            @Override
            public void onTick(int i, int i1) {
                Log.d("Remaining Seconds:",String.valueOf(i1));
                if (progress_track_flag_value == 0) {
                    prv_seconds = i1;
                }
                else {
                    if (i1 != prv_seconds) {
                        prv_seconds = i1;
                        progress_track = (int) (100 * prv_seconds) / total_seconds;
                        if (progress_track < 0) {
                            progress_track = 0;
                        }
                        int mm = (int) i1 / 60;
                        int hh = (int) mm / 60;

                        normalCountDownView.setShowHour(hh != 0);

                        normalCountDownView.setShowMinutes(mm != 0);
                        horizontalProgressView.setProgressCompat(progress_track,true);
                        System.out.println("progress_track: "+progress_track);
//                        horizontalProgressView.setProgress(horizontalProgressView.getProgress() + 1);
                    }
                }

                progress_track_flag_value++;
            }

            @Override
            public void onTimeUp() {
                fullLayout.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.GONE);
                circularProgressIndicator.setVisibility(View.VISIBLE);
                tabFullLayout.setVisibility(View.GONE);
                tabCircularProgressIndicator.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                tabRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;
                mHandler.postDelayed(() -> getDocSchedule(),3000);

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.ENGLISH);
                SimpleDateFormat moY = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);
                SimpleDateFormat full_date_format = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);
                SimpleDateFormat only_month_full_date_format = new SimpleDateFormat("MMMM, yy", Locale.ENGLISH);
                tabFirstDate = dateFormat.format(Calendar.getInstance().getTime());
                tabEndDate = dateFormat.format(Calendar.getInstance().getTime());
                switch (tab.getPosition()) {
                    case 0:
                        System.out.println("Tab 1");
                        // --- Current Day ---
                        tabFirstDate = dateFormat.format(Calendar.getInstance().getTime());
                        tabEndDate = dateFormat.format(Calendar.getInstance().getTime());
                        String date_range_text = full_date_format.format(Calendar.getInstance().getTime());
                        dateRangeText.setText(date_range_text);
                        getDocMeetingCount();
                        break;
                    case 1:
                        System.out.println("Tab 2");
                        Calendar ccc = Calendar.getInstance();
//                        Log.v("Current Week", String.valueOf(ccc.get(Calendar.WEEK_OF_YEAR)));
//                        int current_week=ccc.get(Calendar.WEEK_OF_YEAR);
//                        int week_start_day=ccc.getFirstDayOfWeek();
//                        Toast.makeText(getApplicationContext(),"Current Week is "+current_week +"Start Day is "+week_start_day,Toast.LENGTH_SHORT).show();
                        ccc.setFirstDayOfWeek(Calendar.SATURDAY);
                        ccc.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
//                        System.out.println("Current week = " + Calendar.DAY_OF_WEEK);
                        tabFirstDate = dateFormat.format(ccc.getTime());
                        String first_date_range = full_date_format.format(ccc.getTime());
                        ccc.add(Calendar.DATE, 6);
                        tabEndDate = dateFormat.format(ccc.getTime());
                        String last_date_range = full_date_format.format(ccc.getTime());
//                        System.out.println("Start Date = " + tabFirstDate);
//                        System.out.println("End Date = " + tabEndDate);
                        String text = first_date_range + "  --  " + last_date_range;
                        dateRangeText.setText(text);
                        getDocMeetingCount();
                        break;
                    case 2:
                        System.out.println("Tab 3");
                        Calendar monthcc = Calendar.getInstance();
                        monthcc.add(Calendar.MONTH, 1);
                        monthcc.set(Calendar.DAY_OF_MONTH, 1);
                        monthcc.add(Calendar.DATE, -1);

                        Date lastDayOfMonth = monthcc.getTime();

                        String llll = sdff.format(lastDayOfMonth);
                        String llmmmyy = moY.format(lastDayOfMonth);
                        tabEndDate = llll + "-" + llmmmyy;
                        tabFirstDate = "01-"+ llmmmyy;
                        String month_only_full = only_month_full_date_format.format(lastDayOfMonth);

//                        System.out.println("Start Date = " + tabFirstDate);
//                        System.out.println("End Date = " + tabEndDate);
                        String text1 = "01 " + month_only_full + "  --  " + llll + " " + month_only_full;
                        dateRangeText.setText(text1);
                        getDocMeetingCount();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_for_tab,new DiscountItem()).commit();
                        break;
                    case 3:
                        System.out.println("Tab 4");
                        Calendar n_monthcc = Calendar.getInstance();
//                        monthcc.setTime(today);
                        n_monthcc.add(Calendar.MONTH, 2);
                        n_monthcc.set(Calendar.DAY_OF_MONTH, 1);
                        n_monthcc.add(Calendar.DATE, -1);

                        Date lastDayOf_n_Month = n_monthcc.getTime();

                        String llll_n = sdff.format(lastDayOf_n_Month);
                        String n_llmmmyy = moY.format(lastDayOf_n_Month);
                        tabEndDate = llll_n + "-" + n_llmmmyy;
                        tabFirstDate = "01-"+ n_llmmmyy;
                        String month_only_full1 = only_month_full_date_format.format(lastDayOf_n_Month);
//                        System.out.println("Start Date = " + tabFirstDate);
//                        System.out.println("End Date = " + tabEndDate);
                        String text12 = "01 " + month_only_full1 + "  --  " + llll_n + " " + month_only_full1;
                        dateRangeText.setText(text12);
                        getDocMeetingCount();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabRefresh.setOnClickListener(v -> {
            getDocMeetingCount();
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.appointment_schedule_menu) {
                Intent intent = new Intent(DocDashboard.this, AppointmentSchedule.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.doc_profile_menu) {
                Intent intent = new Intent(DocDashboard.this, DocProfile.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.leave_schedule_menu) {
                Intent intent = new Intent(DocDashboard.this, DocLeave.class);
                startActivity(intent);
            }
            return true;
        });

        logOut.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
            alertDialogBuilder.setTitle("Log Out!")
                    .setMessage("Do you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                        editor1.remove(DOC_USER_CODE);
                        editor1.remove(DOC_USER_PASSWORD);
                        editor1.remove(DOC_DATA_API);
                        editor1.remove(LOGIN_TF);
                        editor1.apply();
                        editor1.commit();

                        Intent intent = new Intent(DocDashboard.this, DocLogin.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
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
        });

        first_flag = 0;
        System.out.println("First_flag: "+ first_flag );

        model = android.os.Build.MODEL;

        brand = Build.BRAND;

        ipAddress = getIPAddress(true);

        hostUserName = getHostName("localhost");


        StringBuilder builder = new StringBuilder();
        builder.append("ANDROID: ").append(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(": ").append(fieldName);
                //builder.append(" : ").append(fieldName).append(" : ");
                //builder.append("sdk=").append(fieldValue);
            }
        }
        osName = builder.toString();
//        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = false;
                        if (sAddr != null) {
                            isIPv4 = sAddr.indexOf(':')<0;
                        }

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = 0; // drop ip6 zone suffix
                                if (sAddr != null) {
                                    delim = sAddr.indexOf('%');
                                }
                                if (sAddr != null) {
                                    return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    public static String getHostName(String defValue) {
        try {
            @SuppressLint("DiscouragedPrivateApi") Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("First_flag on resume: "+ first_flag);
        if (first_flag == 0) {
            getDocData();
        }
        else {
            fullLayout.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
            circularProgressIndicator.setVisibility(View.VISIBLE);
            tabFullLayout.setVisibility(View.GONE);
            tabCircularProgressIndicator.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            tabRefresh.setVisibility(View.GONE);
            conn = false;
            connected = false;
            getDocSchedule();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        normalCountDownView.stopTimer();
    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
        alertDialogBuilder.setTitle("Exit!")
                .setIcon(R.drawable.doc_diary_default)
                .setMessage("Do you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
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

    public void getDocData() {
        fullLayout.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        tabFullLayout.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        userAvailable = false;
        userInfoLists = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

        Calendar calendarcc = Calendar.getInstance();
        calendarcc.add(Calendar.MONTH,3);
        Date mmm = calendarcc.getTime();

        if (expiry_date.isEmpty()) {
            expiry_date = dateFormat.format(mmm);
        }

        String docDataUrl = pre_url_api+"doc_dashboard/getDocData?doc_code="+doc_code+"";
        String expiryDateUrl = pre_url_api+"doc_dashboard/updateDocExpDate";

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        StringRequest expDateUpdate = new StringRequest(Request.Method.POST, expiryDateUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                if (string_out.equals("Successfully Created")) {
                    loginLogInsert();
                }
                else {
                    parsing_message = string_out;
                    System.out.println(string_out);
                    connected = false;
                    updateInterface();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateInterface();
            }
        }, error -> {
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateInterface();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_DATE",expiry_date);
                headers.put("P_DOC_CODE",doc_code);
                return  headers;
            }
        };

        StringRequest docDataReq = new StringRequest(Request.Method.GET, docDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);
                        String user_avail = docInfo.getString("user_avail")
                                .equals("null") ? "0" : docInfo.getString("user_avail");

                        String doc_eff_date = docInfo.getString("doc_eff_date")
                                .equals("null") ? "" :docInfo.getString("doc_eff_date");
                        String doc_status = docInfo.getString("doc_status")
                                .equals("null") ? "" :docInfo.getString("doc_status");

                        effected_date = doc_eff_date;
                        doctor_status = doc_status;

                        if (user_avail.equals("1")) {
                            String doc_name = docInfo.getString("doc_name")
                                    .equals("null") ? "" :docInfo.getString("doc_name");
                            String nn_doc_id = docInfo.getString("doc_id")
                                    .equals("null") ? "" :docInfo.getString("doc_id");
                            String doc_code = docInfo.getString("doc_code")
                                    .equals("null") ? "" :docInfo.getString("doc_code");
                            String depts_name = docInfo.getString("depts_name")
                                    .equals("null") ? "" :docInfo.getString("depts_name");
                            String deptd_name = docInfo.getString("deptd_name")
                                    .equals("null") ? "" :docInfo.getString("deptd_name");
                            String deptm_name = docInfo.getString("deptm_name")
                                    .equals("null") ? "" :docInfo.getString("deptm_name");
                            String desig_name = docInfo.getString("desig_name")
                                    .equals("null") ? "" :docInfo.getString("desig_name");
                            String depts_id = docInfo.getString("depts_id")
                                    .equals("null") ? "" :docInfo.getString("depts_id");
                            String desig_id = docInfo.getString("desig_id")
                                    .equals("null") ? "" :docInfo.getString("desig_id");

                            String doc_video_link = docInfo.getString("doc_video_link")
                                    .equals("null") ? "" : docInfo.getString("doc_video_link");

                            String doc_video_link_enable_flag = docInfo.getString("doc_video_link_enable_flag")
                                    .equals("null") ? "0" : docInfo.getString("doc_video_link_enable_flag");

                            doc_id = nn_doc_id;

                            expiry_date = docInfo.getString("exp_date")
                                    .equals("null") ? "" :docInfo.getString("exp_date");

                            first_login_flag = docInfo.getString("doc_app_first_login_flag")
                                    .equals("null") ? "0" :docInfo.getString("doc_app_first_login_flag");
                            String doc_center_name = docInfo.getString("doc_center_name")
                                    .equals("null") ? "" : docInfo.getString("doc_center_name");

                            userInfoLists.add(new UserInfoList(doc_name,nn_doc_id,doc_code,depts_name,deptd_name,deptm_name,
                                    desig_name,doc_eff_date,doc_status,depts_id,desig_id,doc_video_link,doc_video_link_enable_flag,doc_center_name));
                            doc_fl_flag = docInfo.getString("fl_flag")
                                    .equals("null") ? "0" :docInfo.getString("fl_flag");

                            userAvailable = true;
                        }
                        else if (user_avail.equals("0")) {

                            userAvailable = false;
                        }
                        else {
                            userAvailable = false;
                        }
                    }
                }
                else {
                    userAvailable = false;
                }

                if (userAvailable) {
                    if (doc_fl_flag.equals("1")) {
                        connected = true;
                        updateInterface();
                    }
                    else {
                        if (first_login_flag.equals("1")) {
                            loginLogInsert();
                            System.out.println("FFF");
                        } else {
                            if (expiry_date.isEmpty()) {
                                expiry_date = dateFormat.format(mmm);
                            }
                            requestQueue.add(expDateUpdate);
                            System.out.println("GGG");
                        }
                    }
                }
                else {
                    connected = true;
                    updateInterface();
                }
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

        requestQueue.add(docDataReq);
    }

    public void loginLogInsert() {
        String loginLogUrl = pre_url_api+"doc_dashboard/loginLog";

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        StringRequest loginLogReq = new StringRequest(Request.Method.POST, loginLogUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                if (string_out.equals("Successfully Created")) {
                    getDocSchedule();
                }
                else {
                    parsing_message = string_out;
                    System.out.println(string_out);
                    connected = false;
                    updateInterface();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateInterface();
            }
        }, error -> {
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateInterface();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String userName = userInfoLists.get(0).getDoc_code();
                String userId = userInfoLists.get(0).getDoc_id();
                if (userId != null) {
                    if (!userId.isEmpty()) {
                        System.out.println(userId);
                    } else {
                        userId = "0";
                    }
                } else {
                    userId = "0";
                }
                headers.put("P_IULL_USER_ID",userName);
                headers.put("P_IULL_CLIENT_HOST_NAME",brand+" "+model);
                headers.put("P_IULL_CLIENT_IP_ADDR",ipAddress);
                headers.put("P_IULL_CLIENT_HOST_USER_NAME",hostUserName);
                headers.put("P_IULL_SESSION_USER_ID",userId);
                headers.put("P_IULL_OS_NAME",osName);
                return headers;
            }
        };

        requestQueue.add(loginLogReq);
    }

    public void getDocSchedule() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
        String time_now = simpleDateFormat.format(Calendar.getInstance().getTime());
        String date_now = dateFormat.format(Calendar.getInstance().getTime());

        if (doc_id == null) {
           restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (doc_id.isEmpty()) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            }
        }
        String docDataUrl = pre_url_api+"doc_dashboard/getMeetingSchedule?doc_id="+doc_id+"&time_now="+time_now+"";
        String docMeetingUrl = pre_url_api+"doc_dashboard/getMeetingCount?doc_id="+doc_id+"&first_date="+date_now+"&end_date="+date_now+"";
        String docScheduleUrl = pre_url_api+"doc_dashboard/getScheduleCount?doc_id="+doc_id+"&first_date="+date_now+"&end_date="+date_now+"";
        String docPicUrl = pre_url_api+"doc_dashboard/getDocPic?doc_id="+doc_id;

        last_schedule = "";
        next_schedule = "";
        patient_name = "";
        total_meeting = 0;
        completed_meeting = 0;
        remaining_meeting = 0;
        total_schedule = 0;
        blocked_schedule = 0;

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        StringRequest docPicReq = new StringRequest(Request.Method.GET, docPicUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String doc_profile_pic = docInfo.optString("doc_profile_pic");

                        if (doc_profile_pic.equals("null") || doc_profile_pic.equals("") ) {
                            System.out.println("NULL IMAGE");
                            imageFound = false;
                        }
                        else {
                            byte[] decodedString = Base64.decode(doc_profile_pic,Base64.DEFAULT);
                            bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                            if (bitmap != null) {
                                System.out.println("OK");
                                imageFound = true;
                            }
                            else {
                                System.out.println("NOT OK");
                                imageFound = false;
                            }
                        }

                    }
                }

                connected = true;
                if (first_flag == 0) {
                    updateInterface();
                }
                else {
                    updateLayout();
                }

            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                if (first_flag == 0) {
                    updateInterface();
                }
                else {
                    updateLayout();
                }
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            if (first_flag == 0) {
                updateInterface();
            }
            else {
                updateLayout();
            }
        });

        StringRequest docSchCountReq = new StringRequest(Request.Method.GET, docScheduleUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String schedule = docInfo.getString("schedule")
                                .equals("null") ? "0" : docInfo.getString("schedule");
                        String block_schedule = docInfo.getString("block_schedule")
                                .equals("null") ? "0" : docInfo.getString("block_schedule");

                        total_schedule = Integer.parseInt(schedule);
                        blocked_schedule = Integer.parseInt(block_schedule);

                    }
                }

                requestQueue.add(docPicReq);

            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                if (first_flag == 0) {
                    updateInterface();
                }
                else {
                    updateLayout();
                }
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            if (first_flag == 0) {
                updateInterface();
            }
            else {
                updateLayout();
            }
        });

        StringRequest docMeetReq = new StringRequest(Request.Method.GET, docMeetingUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String schedule_time = docInfo.getString("schedule_time")
                                .equals("null") ? "" : docInfo.getString("schedule_time");

                        Date nt = Calendar.getInstance().getTime();
                        if (!schedule_time.isEmpty()) {
                            schedule_time = date_now +" "+schedule_time;
                            Date time = null;
                            try {
                                time = timeFormat.parse(schedule_time);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
//                            System.out.println(time);
                            assert time != null;
                            if (nt.getTime() >= time.getTime()) {
                                completed_meeting++;
                            }
                            else {
                                remaining_meeting++;
                            }
                        }

                    }

                    total_meeting = Integer.parseInt(count);
                }

                requestQueue.add(docSchCountReq);

            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                if (first_flag == 0) {
                    updateInterface();
                }
                else {
                    updateLayout();
                }
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            if (first_flag == 0) {
                updateInterface();
            }
            else {
                updateLayout();
            }
        });

        StringRequest docSchReq = new StringRequest(Request.Method.GET, docDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        last_schedule = docInfo.getString("last_sc")
                                .equals("null") ? "" : docInfo.getString("last_sc");

                        next_schedule = docInfo.getString("next_sc")
                                .equals("null") ? "" : docInfo.getString("next_sc");

                        patient_name = docInfo.getString("pat_name")
                                .equals("null") ? "" : docInfo.getString("pat_name");

                    }
                }

                requestQueue.add(docMeetReq);
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                if (first_flag == 0) {
                    updateInterface();
                }
                else {
                    updateLayout();
                }
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            if (first_flag == 0) {
                updateInterface();
            }
            else {
                updateLayout();
            }
        });

        requestQueue.add(docSchReq);

    }

    private void updateInterface() {
        if (conn) {
            if (connected) {
                if (userAvailable) {
                    if (doc_fl_flag.equals("1")) {
                        fullLayout.setVisibility(View.GONE);
                        bottomNavigationView.setVisibility(View.GONE);
                        circularProgressIndicator.setVisibility(View.GONE);
                        tabFullLayout.setVisibility(View.GONE);
                        tabCircularProgressIndicator.setVisibility(View.GONE);
                        tabLayout.setVisibility(View.GONE);
                        tabRefresh.setVisibility(View.GONE);

                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
                        alertDialogBuilder.setTitle("Forced Log Out!")
                                .setMessage("You are forced to log out from the app for server maintenance. We are sorry for the disturbance. Please Login again to continue the app.")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                    editor1.remove(DOC_USER_CODE);
                                    editor1.remove(DOC_USER_PASSWORD);
                                    editor1.remove(DOC_DATA_API);
                                    editor1.remove(LOGIN_TF);
                                    editor1.apply();
                                    editor1.commit();

                                    Intent intent = new Intent(DocDashboard.this, DocLogin.class);
                                    startActivity(intent);
                                    finish();
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
                    else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                        Date exp_date = null;
                        Date now_date = Calendar.getInstance().getTime();
                        String nnn_date = dateFormat.format(now_date);
                        try {
                            exp_date = dateFormat.parse(expiry_date);
                            now_date = dateFormat.parse(nnn_date);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        assert exp_date != null;
                        assert now_date != null;
//                    System.out.println(now_date);
//                    System.out.println(exp_date);
                        if (exp_date.getTime() < now_date.getTime()) {
                            fullLayout.setVisibility(View.GONE);
                            bottomNavigationView.setVisibility(View.GONE);
                            circularProgressIndicator.setVisibility(View.GONE);
                            tabFullLayout.setVisibility(View.GONE);
                            tabCircularProgressIndicator.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.GONE);
                            tabRefresh.setVisibility(View.GONE);

                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
                            alertDialogBuilder.setTitle("Date Expired!")
                                    .setMessage("Your access to the app is expired on: "+expiry_date+".\n" +
                                            "To gain access to the app, Please contact with the administrator")
                                    .setPositiveButton("OK", (dialog, which) -> {

                                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                        editor1.remove(DOC_USER_CODE);
                                        editor1.remove(DOC_USER_PASSWORD);
                                        editor1.remove(DOC_DATA_API);
                                        editor1.remove(LOGIN_TF);
                                        editor1.apply();
                                        editor1.commit();
                                        dialog.dismiss();
                                        System.exit(0);
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
                        else {
                            fullLayout.setVisibility(View.VISIBLE);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            circularProgressIndicator.setVisibility(View.GONE);
                            tabFullLayout.setVisibility(View.VISIBLE);
                            tabCircularProgressIndicator.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.VISIBLE);
                            tabRefresh.setVisibility(View.GONE);

                            conn = false;
                            connected = false;
                            userAvailable = false;

                            progress_track_flag_value = 0;
                            String doc_center = "";
                            if (userInfoLists == null) {
                                restart("Could Not Get Doctor Data. Please Restart the App.");
                            }
                            else {
                                if (userInfoLists.size() == 0) {
                                    restart("Could Not Get Doctor Data. Please Restart the App.");
                                }
                                else {
                                    doc_id = userInfoLists.get(0).getDoc_id();
                                    String doc_name = "Dr. "+userInfoLists.get(0).getDoc_name();
                                    docName.setText(doc_name);

                                    doc_center = userInfoLists.get(0).getDoc_center_name();
                                }
                            }

                            if (doc_center.isEmpty()) {
                                docCenterName.setVisibility(View.GONE);
                            }
                            else {
                                docCenterName.setVisibility(View.VISIBLE);
                                doc_center = "("+doc_center+")";
                                docCenterName.setText(doc_center);
                            }

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                            SimpleDateFormat only_date_format = new SimpleDateFormat("dd", Locale.ENGLISH);
                            SimpleDateFormat full_date_format = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                            SimpleDateFormat full_date_format_range = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);


                            if (!last_schedule.isEmpty() && !next_schedule.isEmpty()) {
                                nextMeetingText.setText("Next Meeting");
                                meetingTime.setVisibility(View.VISIBLE);
                                patientName.setVisibility(View.VISIBLE);
                                timerIcon.setVisibility(View.VISIBLE);
                                normalCountDownView.setVisibility(View.VISIBLE);
                                Date prv_meeting_date = null;
                                try {
                                    prv_meeting_date = sdf.parse(last_schedule);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }

                                Date to_Date = Calendar.getInstance().getTime();

                                Date next_meeting_date = null;
                                try {
                                    next_meeting_date = sdf.parse(next_schedule);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }


                                assert next_meeting_date != null;
                                assert prv_meeting_date != null;
                                total_seconds = (int) ((next_meeting_date.getTime() - prv_meeting_date.getTime())/1000);

                                System.out.println("total_seconds: "+total_seconds);

                                int remaining_seconds = (int) ((next_meeting_date.getTime()- to_Date.getTime())/1000);

                                System.out.println("remaining_seconds: "+remaining_seconds);

                                progress_track = (int) (100 * remaining_seconds) / total_seconds;

                                if (progress_track < 0) {
                                    progress_track = 0;
                                }

                                System.out.println("progress_track: "+progress_track);

                                String time = simpleDateFormat.format(next_meeting_date);
                                String date = only_date_format.format(next_meeting_date);
                                String n_date = only_date_format.format(Calendar.getInstance().getTime());
                                String full_date = full_date_format.format(next_meeting_date);

                                if (date.equals(n_date)) {

                                    if (time.startsWith("0")) {
                                        time = time.substring(1);
                                    }

                                    meetingTime.setText("At  "+time);
                                }
                                else {
                                    String  tt = "At  " + time +"\nIn  "+ full_date;
                                    meetingTime.setText(tt);
                                }

                                horizontalProgressView.setProgressCompat(progress_track,true);
                                normalCountDownView.stopTimer();

                                int mm = (int) remaining_seconds / 60;
                                int hh = (int) mm / 60;
                                normalCountDownView.setShowHour(hh != 0);

                                normalCountDownView.setShowMinutes(mm != 0);

                                normalCountDownView.initTimer(remaining_seconds, HappyTimer.Type.COUNT_DOWN);
                                normalCountDownView.startTimer();

                                if (patient_name.isEmpty()) {
                                    String text = "No Name Found";
                                    patientName.setText(text);
                                }
                                else {
                                    String text = "With "+patient_name;
                                    patientName.setText(text);
                                }
                            }
                            else if (last_schedule.isEmpty() && !next_schedule.isEmpty()) {
                                nextMeetingText.setText("Next Meeting");
                                meetingTime.setVisibility(View.VISIBLE);
                                patientName.setVisibility(View.VISIBLE);
                                timerIcon.setVisibility(View.VISIBLE);
                                normalCountDownView.setVisibility(View.VISIBLE);

                                Date to_Date = Calendar.getInstance().getTime();

                                Date next_meeting_date = null;
                                try {
                                    next_meeting_date = sdf.parse(next_schedule);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }


                                assert next_meeting_date != null;

                                total_seconds = (int) ((next_meeting_date.getTime() - to_Date.getTime())/1000);

                                System.out.println("total_seconds: "+total_seconds);

                                int remaining_seconds = (int) ((next_meeting_date.getTime()- to_Date.getTime())/1000);

                                System.out.println("remaining_seconds: "+remaining_seconds);

                                progress_track = (int) (100 * remaining_seconds) / total_seconds;

                                if (progress_track < 0) {
                                    progress_track = 0;
                                }

                                System.out.println("progress_track: "+progress_track);

                                String time = simpleDateFormat.format(next_meeting_date);
                                String date = only_date_format.format(next_meeting_date);
                                String n_date = only_date_format.format(Calendar.getInstance().getTime());
                                String full_date = full_date_format.format(next_meeting_date);

                                if (date.equals(n_date)) {
                                    meetingTime.setText("At "+time);
                                }
                                else {
                                    String  tt = "At " + time +"\nIn "+ full_date;
                                    meetingTime.setText(tt);
                                }

                                horizontalProgressView.setProgressCompat(progress_track,true);
                                normalCountDownView.stopTimer();

                                int mm = (int) remaining_seconds / 60;
                                int hh = (int) mm / 60;
                                normalCountDownView.setShowHour(hh != 0);

                                normalCountDownView.setShowMinutes(mm != 0);

                                normalCountDownView.initTimer(remaining_seconds, HappyTimer.Type.COUNT_DOWN);
                                normalCountDownView.startTimer();

                                if (patient_name.isEmpty()) {
                                    String text = "No Name Found";
                                    patientName.setText(text);
                                }
                                else {
                                    String text = "With "+patient_name;
                                    patientName.setText(text);
                                }
                            }
                            else  {
                                nextMeetingText.setText("No Upcoming Meeting Available");
                                meetingTime.setVisibility(View.GONE);
                                patientName.setVisibility(View.GONE);
                                timerIcon.setVisibility(View.GONE);
                                normalCountDownView.setVisibility(View.GONE);
                                progress_track = 0;
                                horizontalProgressView.setProgressCompat(progress_track,true);

                            }

                            Date dateRangCc = Calendar.getInstance().getTime();
                            String date_range_text = full_date_format_range.format(dateRangCc);
                            dateRangeText.setText(date_range_text);

                            total.setText(String.valueOf(total_meeting));
                            completed.setText(String.valueOf(completed_meeting));
                            remaining.setText(String.valueOf(remaining_meeting));
                            totalSchedule.setText(String.valueOf(total_schedule));
                            blockedSchedule.setText(String.valueOf(blocked_schedule));

                            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                            String wt = "";
                            if (currentHour >= 4 && currentHour <= 11) {
                                wt = "Good Morning,";
                            }
                            else if (currentHour >= 12 && currentHour <= 16) {
                                wt = "Good Afternoon,";
                            }
                            else if (currentHour >= 17 && currentHour <= 22) {
                                wt = "Good Evening,";
                            }
                            else {
                                wt = "Hello,";
                            }
                            welcomeText.setText(wt);

                            if (imageFound) {
                                Glide.with(DocDashboard.this)
                                        .load(bitmap)
                                        .fitCenter()
                                        .into(docImage);
                            }
                            else {
                                docImage.setImageResource(R.drawable.doctor);
                            }

                            first_flag = 1;
                        }
                    }
                }
                else {
                    fullLayout.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.GONE);
                    tabFullLayout.setVisibility(View.GONE);
                    tabCircularProgressIndicator.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    tabRefresh.setVisibility(View.GONE);

                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
                    alertDialogBuilder.setTitle("Access Denied!")
                            .setMessage("Your status is: "+doctor_status+", with effected date: "+effected_date+". " +
                                    "So, You are not eligible to access this app.")
                            .setPositiveButton("OK", (dialog, which) -> {

                                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                editor1.remove(DOC_USER_CODE);
                                editor1.remove(DOC_USER_PASSWORD);
                                editor1.remove(DOC_DATA_API);
                                editor1.remove(LOGIN_TF);
                                editor1.apply();
                                editor1.commit();
                                dialog.dismiss();
                                System.exit(0);
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
            else {
                alertMessage();
            }
        }
        else {
            alertMessage();
        }
    }

    public void alertMessage() {
        fullLayout.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        tabFullLayout.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getDocData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    dialog.dismiss();
                    System.exit(0);
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

    private void updateLayout() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                tabFullLayout.setVisibility(View.VISIBLE);
                tabCircularProgressIndicator.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                tabRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;

                progress_track_flag_value = 0;

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                SimpleDateFormat only_date_format = new SimpleDateFormat("dd", Locale.ENGLISH);
                SimpleDateFormat full_date_format = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                SimpleDateFormat full_date_format_range = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);


                if (!last_schedule.isEmpty() && !next_schedule.isEmpty()) {
                    nextMeetingText.setText("Next Meeting");
                    meetingTime.setVisibility(View.VISIBLE);
                    patientName.setVisibility(View.VISIBLE);
                    timerIcon.setVisibility(View.VISIBLE);
                    normalCountDownView.setVisibility(View.VISIBLE);
                    Date prv_meeting_date = null;
                    try {
                        prv_meeting_date = sdf.parse(last_schedule);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    Date to_Date = Calendar.getInstance().getTime();

                    Date next_meeting_date = null;
                    try {
                        next_meeting_date = sdf.parse(next_schedule);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }


                    assert next_meeting_date != null;
                    assert prv_meeting_date != null;
                    total_seconds = (int) ((next_meeting_date.getTime() - prv_meeting_date.getTime())/1000);

                    System.out.println("total_seconds: "+total_seconds);

                    int remaining_seconds = (int) ((next_meeting_date.getTime()- to_Date.getTime())/1000);

                    System.out.println("remaining_seconds: "+remaining_seconds);

                    progress_track = (int) (100 * remaining_seconds) / total_seconds;

                    if (progress_track < 0) {
                        progress_track = 0;
                    }

                    System.out.println("progress_track: "+progress_track);

                    String time = simpleDateFormat.format(next_meeting_date);
                    String date = only_date_format.format(next_meeting_date);
                    String n_date = only_date_format.format(Calendar.getInstance().getTime());
                    String full_date = full_date_format.format(next_meeting_date);

                    if (date.equals(n_date)) {

                        if (time.startsWith("0")) {
                            time = time.substring(1);
                        }

                        meetingTime.setText("At  "+time);
                    }
                    else {
                        String  tt = "At  " + time +"\nIn  "+ full_date;
                        meetingTime.setText(tt);
                    }

                    horizontalProgressView.setProgressCompat(progress_track,true);
                    normalCountDownView.stopTimer();

                    int mm = (int) remaining_seconds / 60;
                    int hh = (int) mm / 60;
                    normalCountDownView.setShowHour(hh != 0);

                    normalCountDownView.setShowMinutes(mm != 0);

                    normalCountDownView.initTimer(remaining_seconds, HappyTimer.Type.COUNT_DOWN);
                    normalCountDownView.startTimer();

                    if (patient_name.isEmpty()) {
                        String text = "No Name Found";
                        patientName.setText(text);
                    }
                    else {
                        String text = "With "+patient_name;
                        patientName.setText(text);
                    }
                }
                else if (last_schedule.isEmpty() && !next_schedule.isEmpty()) {
                    nextMeetingText.setText("Next Meeting");
                    meetingTime.setVisibility(View.VISIBLE);
                    patientName.setVisibility(View.VISIBLE);
                    timerIcon.setVisibility(View.VISIBLE);
                    normalCountDownView.setVisibility(View.VISIBLE);

                    Date to_Date = Calendar.getInstance().getTime();

                    Date next_meeting_date = null;
                    try {
                        next_meeting_date = sdf.parse(next_schedule);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }


                    assert next_meeting_date != null;

                    total_seconds = (int) ((next_meeting_date.getTime() - to_Date.getTime())/1000);

                    System.out.println("total_seconds: "+total_seconds);

                    int remaining_seconds = (int) ((next_meeting_date.getTime()- to_Date.getTime())/1000);

                    System.out.println("remaining_seconds: "+remaining_seconds);

                    progress_track = (int) (100 * remaining_seconds) / total_seconds;

                    if (progress_track < 0) {
                        progress_track = 0;
                    }

                    System.out.println("progress_track: "+progress_track);

                    String time = simpleDateFormat.format(next_meeting_date);
                    String date = only_date_format.format(next_meeting_date);
                    String n_date = only_date_format.format(Calendar.getInstance().getTime());
                    String full_date = full_date_format.format(next_meeting_date);

                    if (date.equals(n_date)) {
                        meetingTime.setText("At "+time);
                    }
                    else {
                        String  tt = "At " + time +"\nIn "+ full_date;
                        meetingTime.setText(tt);
                    }

                    horizontalProgressView.setProgressCompat(progress_track,true);
                    normalCountDownView.stopTimer();

                    int mm = (int) remaining_seconds / 60;
                    int hh = (int) mm / 60;
                    normalCountDownView.setShowHour(hh != 0);

                    normalCountDownView.setShowMinutes(mm != 0);

                    normalCountDownView.initTimer(remaining_seconds, HappyTimer.Type.COUNT_DOWN);
                    normalCountDownView.startTimer();

                    if (patient_name.isEmpty()) {
                        String text = "No Name Found";
                        patientName.setText(text);
                    }
                    else {
                        String text = "With "+patient_name;
                        patientName.setText(text);
                    }
                }
                else  {
                    nextMeetingText.setText("No Upcoming Meeting Available");
                    meetingTime.setVisibility(View.GONE);
                    patientName.setVisibility(View.GONE);
                    timerIcon.setVisibility(View.GONE);
                    normalCountDownView.setVisibility(View.GONE);
                    progress_track = 0;
                    horizontalProgressView.setProgressCompat(progress_track,true);

                }

                Date dateRangCc = Calendar.getInstance().getTime();
                String date_range_text = full_date_format_range.format(dateRangCc);
                dateRangeText.setText(date_range_text);

                total.setText(String.valueOf(total_meeting));
                completed.setText(String.valueOf(completed_meeting));
                remaining.setText(String.valueOf(remaining_meeting));
                totalSchedule.setText(String.valueOf(total_schedule));
                blockedSchedule.setText(String.valueOf(blocked_schedule));

                int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                String wt = "";
                if (currentHour >= 4 && currentHour <= 11) {
                    wt = "Good Morning,";
                }
                else if (currentHour >= 12 && currentHour <= 16) {
                    wt = "Good Afternoon,";
                }
                else if (currentHour >= 17 && currentHour <= 22) {
                    wt = "Good Evening,";
                }
                else {
                    wt = "Hello,";
                }
                welcomeText.setText(wt);

                TabLayout.Tab tabAt = tabLayout.getTabAt(0);
                tabLayout.selectTab(tabAt);

                if (imageFound) {
                    Glide.with(DocDashboard.this)
                            .load(bitmap)
                            .fitCenter()
                            .into(docImage);
                }
                else {
                    docImage.setImageResource(R.drawable.doctor);
                }

                first_flag = 1;
            }
            else {
                alertMessage2();
            }
        }
        else {
            alertMessage2();
        }
    }

    public void alertMessage2() {
        fullLayout.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        tabFullLayout.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    fullLayout.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    tabFullLayout.setVisibility(View.GONE);
                    tabCircularProgressIndicator.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    tabRefresh.setVisibility(View.GONE);
                    conn = false;
                    connected = false;
                    getDocSchedule();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    dialog.dismiss();
                    System.exit(0);
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

    public void getDocMeetingCount() {
        tabFullLayout.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);
        conn = false;
        connected = false;

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
//        String time_now = simpleDateFormat.format(Calendar.getInstance().getTime());
        String date_now = dateFormat.format(Calendar.getInstance().getTime());
        String docMeetingUrl = pre_url_api+"doc_dashboard/getMeetingCount?doc_id="+doc_id+"&first_date="+tabFirstDate+"&end_date="+tabEndDate+"";
        String docScheduleUrl = pre_url_api+"doc_dashboard/getScheduleCount?doc_id="+doc_id+"&first_date="+tabFirstDate+"&end_date="+tabEndDate+"";

        total_meeting = 0;
        completed_meeting = 0;
        remaining_meeting = 0;
        total_schedule = 0;
        blocked_schedule = 0;

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        StringRequest docSchCountReq = new StringRequest(Request.Method.GET, docScheduleUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String schedule = docInfo.getString("schedule")
                                .equals("null") ? "0" : docInfo.getString("schedule");
                        String block_schedule = docInfo.getString("block_schedule")
                                .equals("null") ? "0" : docInfo.getString("block_schedule");

                        total_schedule = Integer.parseInt(schedule);
                        blocked_schedule = Integer.parseInt(block_schedule);

                    }
                }

                connected = true;
                updateTabLayout();

            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateTabLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateTabLayout();
        });


        StringRequest docMeetReq = new StringRequest(Request.Method.GET, docMeetingUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String schedule_time = docInfo.getString("schedule_time")
                                .equals("null") ? "" : docInfo.getString("schedule_time");
                        String appointment_date = docInfo.getString("appointment_date")
                                .equals("null") ? "" : docInfo.getString("appointment_date");

                        Date nt = Calendar.getInstance().getTime();
                        if (!schedule_time.isEmpty()) {
                            schedule_time = appointment_date +" "+schedule_time;
                            Date time = null;
                            try {
                                time = timeFormat.parse(schedule_time);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
//                            System.out.println(time);
                            assert time != null;
                            if (nt.getTime() >= time.getTime()) {
                                completed_meeting++;
                            }
                            else {
                                remaining_meeting++;
                            }
                        }

                    }

                    total_meeting = Integer.parseInt(count);
                }

                requestQueue.add(docSchCountReq);

            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateTabLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateTabLayout();
        });

        requestQueue.add(docMeetReq);
    }

    private void updateTabLayout() {
        if (conn) {
            if (connected) {
                tabFullLayout.setVisibility(View.VISIBLE);
                tabCircularProgressIndicator.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                tabRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;

                total.setText(String.valueOf(total_meeting));
                completed.setText(String.valueOf(completed_meeting));
                remaining.setText(String.valueOf(remaining_meeting));
                totalSchedule.setText(String.valueOf(total_schedule));
                blockedSchedule.setText(String.valueOf(blocked_schedule));

            }
            else {
                alertMessage3();
            }
        }
        else {
            alertMessage3();
        }
    }

    public void alertMessage3() {
        tabFullLayout.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
        tabRefresh.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("OK", (dialog, which) -> {
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
}