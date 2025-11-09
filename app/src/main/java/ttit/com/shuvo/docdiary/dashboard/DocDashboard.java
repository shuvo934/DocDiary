package ttit.com.shuvo.docdiary.dashboard;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androchef.happytimer.countdowntimer.HappyTimer;
import com.androchef.happytimer.countdowntimer.NormalCountDownView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.dewinjm.monthyearpicker.MonthFormat;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.whiteelephant.monthpicker.MonthPickerDialog;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify;
import ttit.com.shuvo.docdiary.camera_preview.BitmapCallBack;
import ttit.com.shuvo.docdiary.camera_preview.CameraPreview;
import ttit.com.shuvo.docdiary.dashboard.arraylists.AdminInfoList;
import ttit.com.shuvo.docdiary.dashboard.arraylists.AppointmentChartList;
import ttit.com.shuvo.docdiary.dashboard.arraylists.PaymentChartList;
import ttit.com.shuvo.docdiary.dashboard.dialogue.ImageTakerChoiceDialog;
import ttit.com.shuvo.docdiary.dashboard.extra.AppointMarkerView;
import ttit.com.shuvo.docdiary.dashboard.extra.MyMarkerView;
import ttit.com.shuvo.docdiary.dashboard.interfaces.PictureChooseListener;
import ttit.com.shuvo.docdiary.hr_accounts.HRAccounts;
import ttit.com.shuvo.docdiary.login.interfaces.AdminCallBackListener;
import ttit.com.shuvo.docdiary.login.interfaces.AdminIDCallbackListener;
import ttit.com.shuvo.docdiary.patient_search.PatientSearchAdmin;
import ttit.com.shuvo.docdiary.payment.AddPayment;
import ttit.com.shuvo.docdiary.all_appointment.AllAppointment;
import ttit.com.shuvo.docdiary.appt_schedule.AppointmentSchedule;
import ttit.com.shuvo.docdiary.dashboard.arraylists.UserInfoList;
import ttit.com.shuvo.docdiary.dashboard.dialogue.CenterSelectDialogue;
import ttit.com.shuvo.docdiary.dashboard.dialogue.UserSelectDialogue;
import ttit.com.shuvo.docdiary.leave_schedule.DocLeave;
import ttit.com.shuvo.docdiary.login.interfaces.CallBackListener;
import ttit.com.shuvo.docdiary.login.DocLogin;
import ttit.com.shuvo.docdiary.login.interfaces.IDCallbackListener;
import ttit.com.shuvo.docdiary.login.arraylists.CenterList;
import ttit.com.shuvo.docdiary.login.arraylists.MultipleUserList;
import ttit.com.shuvo.docdiary.patient_search.PatientSearch;
import ttit.com.shuvo.docdiary.profile.DocProfile;
import ttit.com.shuvo.docdiary.report_manager.ReportManager;
import ttit.com.shuvo.docdiary.unit_app_schedule.UnitWiseAppointment;

public class DocDashboard extends AppCompatActivity implements CallBackListener, IDCallbackListener, AdminIDCallbackListener,
        AdminCallBackListener, BitmapCallBack, PictureChooseListener {

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
    public static final String DOC_ALL_ID = "DOC_ALL_ID";
    public static final String ADMIN_OR_USER_FLAG = "ADMIN_OR_USER_FLAG";
    public static final String ADMIN_USR_ID = "ADMIN_USR_ID";
    String doc_code = "";
    String admin_usr_id = "";
    String admin_usr_name = "";
    String admin_user_flag = "";
    public static String pre_url_api = "";
    public static ArrayList<UserInfoList> userInfoLists;
    public static ArrayList<AdminInfoList> adminInfoLists;
    String expiry_date = "";
    String first_login_flag = "";
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    private Boolean userAvailable = false;
    String effected_date = "";
    String doctor_status = "";
    String parsing_message = "";
    CardView progressBarCard;
    LinearProgressIndicator horizontalProgressView;
//    Counter mCounter;
    long prv_seconds = 0;
    long progress_track_flag_value = 0;

    String last_schedule = "";
    String next_schedule = "";
    String patient_name = "";
    String doc_id = "";
    String doc_fl_flag = "";
    String doc_head_flag = "";
    String doc_manager_flag = "";
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
    LinearLayout tabFullLayoutAdmin;
    CircularProgressIndicator tabCircularProgressIndicator;
    ImageView tabRefresh;
    TextView dateRangeText;
    TextView dateRangeTextAdmin;
    RelativeLayout graphicalViewLayout;
    String tabFirstDate = "";
    String tabEndDate = "";

    TextView totalSchedule;
    TextView totalAppointmentAdmin;
    TextView blockedSchedule;
    TextView cancelAppointmentAdmin;
    TextView totalPaymentCount;
    TextView totalPaymentAmount;
    int total_schedule = 0;
    int blocked_schedule = 0;
    String total_payment_count = "";
    String total_payment_amount = "";
    String total_appointment_admin = "";
    String cancel_appointment_admin = "";

    String model = "";
    String brand = "";
    String ipAddress = "";
    String hostUserName = "";
    String osName = "";
    Bitmap bitmap;
    private boolean imageFound = false;
    ImageView docImage;
    ImageView adminCaptureImage;
    MaterialButton unitDoctor;
    MaterialButton allDoctorAppointment;
    MaterialButton doctorReports;
    ImageView switchUser;
    ArrayList<CenterList> centerLists;
    boolean tabUpdateNeeded = false;

    String admin_fl_flag = "0";
    String mob_app_access_flag = "0";
    private Boolean adminAvailable = false;

    TabLayout chartTabLayout;
    boolean chartTabUpdateNeeded = false;
    CircularProgressIndicator chartTabCircularProgressIndicator;
    ImageView chartTabRefresh;
    LinearLayout chartTabFullLayout;
    int chartTabPosition = 0;

    LinearLayout payAppointmentTypeLay;
    MaterialCardView allPayAppCard;
    TextView allPayAppText;
    MaterialCardView offlinePayAppCard;
    TextView offPayAppText;
    MaterialCardView onlinePayAppCard;
    TextView onPayAppText;
    int pay_app_type = 0;

    LineChart paymentChart;
    ArrayList<PaymentChartList> paymentChartLists;
    LinearLayout monthSelectionLayPayment;
    TextView monthSelectionPayment;
    LinearLayout yearSelectionLayPayment;
    TextView yearSelectionPayment;
    LinearLayout graphPaymentTotalLay;
    TextView totalPaymentRcvGraph;
    TextView totalPaymentRtnGraph;
    String paymentChartFirstDate = "";
    String paymentChartLastDate = "";
    CircularProgressIndicator paymentChartCircularProgressIndicator;
    ImageView paymentChartRefresh;
    AlertDialog yearDialogPayment;

    LineChart appointmentChart;
    ArrayList<AppointmentChartList> appointmentChartLists;
    LinearLayout monthSelectionLayAppoint;
    TextView monthSelectionAppoint;
    LinearLayout yearSelectionLayAppoint;
    TextView yearSelectionAppoint;
    LinearLayout graphAppointmentTotalLay;
    TextView totalAppointmentGraph;
    TextView totalCancelAppointmentGraph;
    String appointChartFirstDate = "";
    String appointChartLastDate = "";
    CircularProgressIndicator appointChartCircularProgressIndicator;
    ImageView appointChartRefresh;
    AlertDialog yearDialogAppoint;

    Logger logger = Logger.getLogger(DocDashboard.class.getName());
    Bitmap selectedBitmap;
    ReviewManager reviewManager;
    boolean cameraResumeLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (API 34) or Android 15 (API 35)
            WindowCompat.setDecorFitsSystemWindows(window, false);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.clouds));
            WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(false);
        } else {
            // Safe legacy approach for Android < 14
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.clouds));
        }
        setContentView(R.layout.activity_doc_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reviewManager = ReviewManagerFactory.create(this);
        fullLayout = findViewById(R.id.doc_dashboard_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_doc_dashboard);
        circularProgressIndicator.setVisibility(View.GONE);
        welcomeText = findViewById(R.id.greetings_text);
        docName = findViewById(R.id.doctor_name);
        docImage = findViewById(R.id.doc_profile_image_in_dashboard);
        adminCaptureImage = findViewById(R.id.camera_capture_view);
        docCenterName = findViewById(R.id.doctor_s_center_name);
        logOut = findViewById(R.id.log_out_doc);
        progressBarCard = findViewById(R.id.next_meeting_progressbar_details_card_view);
        horizontalProgressView = findViewById(R.id.progressView_horizontal);

        normalCountDownView = findViewById(R.id.normalCountDownView);
        nextMeetingText = findViewById(R.id.next_meeting_text_view);
        timerIcon = findViewById(R.id.timer_icon_image);
        patientName = findViewById(R.id.next_meeting_patient_name);

        unitDoctor = findViewById(R.id.unit_wise_doctor_button);
        allDoctorAppointment = findViewById(R.id.all_doctor_appointment_button);
        doctorReports = findViewById(R.id.doctor_report_manager_button);

        meetingTime = findViewById(R.id.next_meeting_time_dashboard);

        total = findViewById(R.id.total_meeting_count);
        completed = findViewById(R.id.completed_meeting_count);
        remaining = findViewById(R.id.remaining_meeting_count);

        tabLayout = findViewById(R.id.three_tab_layout);
        tabFullLayout = findViewById(R.id.tab_selected_full_layout);
        tabFullLayoutAdmin = findViewById(R.id.tab_selected_full_layout_for_admin);
        tabCircularProgressIndicator = findViewById(R.id.progress_indicator_doc_dashboard_tab_layout);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabRefresh = findViewById(R.id.tab_refresh_button);
        tabRefresh.setVisibility(View.GONE);
        dateRangeText = findViewById(R.id.date_range_tab_text);
        dateRangeTextAdmin = findViewById(R.id.date_range_tab_text_for_admin);
        totalSchedule = findViewById(R.id.total_schedule_count);
        totalAppointmentAdmin = findViewById(R.id.total_appointment_count_for_admin);
        blockedSchedule = findViewById(R.id.blocked_schedule_count);
        cancelAppointmentAdmin = findViewById(R.id.canceled_appointment_count_for_admin);
        totalPaymentCount = findViewById(R.id.total_payment_count_for_admin);
        totalPaymentAmount = findViewById(R.id.total_payment_amount_for_admin);
        switchUser = findViewById(R.id.switch_user_icon);

        graphicalViewLayout = findViewById(R.id.graphical_view_layout_for_admin);
        chartTabLayout = findViewById(R.id.month_year_app_pay_tab_layout);
        chartTabCircularProgressIndicator = findViewById(R.id.progress_indicator_doc_dashboard_chart_tab_layout);
        chartTabCircularProgressIndicator.setVisibility(View.GONE);
        chartTabFullLayout = findViewById(R.id.month_year_app_pay_tab_selected_full_layout);
        chartTabRefresh = findViewById(R.id.chart_tab_refresh_button);
        chartTabRefresh.setVisibility(View.GONE);

        payAppointmentTypeLay = findViewById(R.id.layout_of_type_of_pay_appoint);
        allPayAppCard = findViewById(R.id.all_type_pay_appoint_data);
        allPayAppText = findViewById(R.id.all_type_pay_app_text);
        offlinePayAppCard = findViewById(R.id.offline_pay_appoint_data);
        offPayAppText = findViewById(R.id.offline_pay_app_text);
        onlinePayAppCard = findViewById(R.id.online_pay_appoint_data);
        onPayAppText = findViewById(R.id.online_pay_app_text);

        monthSelectionLayPayment = findViewById(R.id.month_selection_layout_for_payment_graph);
        monthSelectionPayment = findViewById(R.id.select_month_for_payment_graph);
        yearSelectionLayPayment = findViewById(R.id.year_selection_layout_for_payment_graph);
        yearSelectionPayment = findViewById(R.id.select_year_for_payment_graph);
        yearSelectionLayPayment.setVisibility(View.GONE);
        graphPaymentTotalLay = findViewById(R.id.total_graph_payment_lay);
        graphPaymentTotalLay.setVisibility(View.GONE);
        totalPaymentRcvGraph = findViewById(R.id.total_payment_rcv_from_payment_graph);
        totalPaymentRtnGraph = findViewById(R.id.total_payment_rtn_from_payment_graph);
        paymentChart = findViewById(R.id.payment_rcv_rtn_overview_linechart);
        paymentChartCircularProgressIndicator = findViewById(R.id.progress_indicator_for_payment_chart_loading);
        paymentChartCircularProgressIndicator.setVisibility(View.GONE);
        paymentChartRefresh = findViewById(R.id.payment_chart_refresh_button);
        paymentChartRefresh.setVisibility(View.GONE);

        monthSelectionLayAppoint = findViewById(R.id.month_selection_layout_for_appointment_graph);
        monthSelectionAppoint = findViewById(R.id.select_month_for_appointment_graph);
        yearSelectionLayAppoint = findViewById(R.id.year_selection_layout_for_appointment_graph);
        yearSelectionAppoint = findViewById(R.id.select_year_for_appointment_graph);
        yearSelectionLayAppoint.setVisibility(View.GONE);
        graphAppointmentTotalLay = findViewById(R.id.total_graph_appointment_lay);
        graphAppointmentTotalLay.setVisibility(View.GONE);
        totalAppointmentGraph = findViewById(R.id.total_appointment_from_appointment_graph);
        totalCancelAppointmentGraph = findViewById(R.id.total_cancel_appointment_from_appointment_graph);
        appointmentChart = findViewById(R.id.appointment_active_cancel_overview_linechart);
        appointChartCircularProgressIndicator = findViewById(R.id.progress_indicator_for_appointment_chart_loading);
        appointChartCircularProgressIndicator.setVisibility(View.GONE);
        appointChartRefresh = findViewById(R.id.appointment_chart_refresh_button);
        appointChartRefresh.setVisibility(View.GONE);

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
                        int mm = i1 / 60;
                        int hh = mm / 60;

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
                if (!loading) {
                    fullLayout.setVisibility(View.GONE);
                    progressBarCard.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    tabFullLayout.setVisibility(View.GONE);
                    tabFullLayoutAdmin.setVisibility(View.GONE);
                    tabCircularProgressIndicator.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    tabRefresh.setVisibility(View.GONE);
                    graphicalViewLayout.setVisibility(View.GONE);
                    chartTabCircularProgressIndicator.setVisibility(View.GONE);
                    conn = false;
                    connected = false;
                    loading = true;
                    mHandler.postDelayed(() -> getDocSchedule(),3000);
                }
                else {
                    mHandler.postDelayed(() -> onResume(),3000);
                }


            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!tabUpdateNeeded) {
                    tabUpdateNeeded = true;
                }
                else{
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
                            if (admin_user_flag.equals("1")) {
                                dateRangeText.setText(date_range_text);
                                getDocMeetingCount();
                            }
                            else {
                                dateRangeTextAdmin.setText(date_range_text);
                                getAdminAppPayCount();
                            }
                            break;
                        case 1:
                            System.out.println("Tab 2");
                            Calendar ccc = Calendar.getInstance();
                            ccc.setFirstDayOfWeek(Calendar.SATURDAY);
                            ccc.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
                            tabFirstDate = dateFormat.format(ccc.getTime());
                            String first_date_range = full_date_format.format(ccc.getTime());
                            ccc.add(Calendar.DATE, 6);
                            tabEndDate = dateFormat.format(ccc.getTime());
                            String last_date_range = full_date_format.format(ccc.getTime());
                            String text = first_date_range + "  --  " + last_date_range;
                            if (admin_user_flag.equals("1")) {
                                dateRangeText.setText(text);
                                getDocMeetingCount();
                            }
                            else {
                                dateRangeTextAdmin.setText(text);
                                getAdminAppPayCount();
                            }
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

                            String text1 = "01 " + month_only_full + "  --  " + llll + " " + month_only_full;
                            if (admin_user_flag.equals("1")) {
                                dateRangeText.setText(text1);
                                getDocMeetingCount();
                            }
                            else {
                                dateRangeTextAdmin.setText(text1);
                                getAdminAppPayCount();
                            }
                            break;
                        case 3:
                            System.out.println("Tab 4");
                            Calendar n_monthcc = Calendar.getInstance();
                            n_monthcc.add(Calendar.MONTH, 2);
                            n_monthcc.set(Calendar.DAY_OF_MONTH, 1);
                            n_monthcc.add(Calendar.DATE, -1);

                            Date lastDayOf_n_Month = n_monthcc.getTime();

                            String llll_n = sdff.format(lastDayOf_n_Month);
                            String n_llmmmyy = moY.format(lastDayOf_n_Month);
                            tabEndDate = llll_n + "-" + n_llmmmyy;
                            tabFirstDate = "01-"+ n_llmmmyy;
                            String month_only_full1 = only_month_full_date_format.format(lastDayOf_n_Month);
                            String text12 = "01 " + month_only_full1 + "  --  " + llll_n + " " + month_only_full1;
                            if (admin_user_flag.equals("1")) {
                                dateRangeText.setText(text12);
                                getDocMeetingCount();
                            }
                            else {
                                dateRangeTextAdmin.setText(text12);
                                getAdminAppPayCount();
                            }
                            break;
                        default:
                            break;
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (!tabUpdateNeeded) {
                    tabUpdateNeeded = true;
                }
            }
        });

        chartTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!chartTabUpdateNeeded) {
                    chartTabUpdateNeeded = true;
                }
                else {
                    if (tab.getPosition() == 0) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);
                        monthSelectionLayPayment.setVisibility(View.VISIBLE);
                        yearSelectionLayPayment.setVisibility(View.GONE);
                        monthSelectionLayAppoint.setVisibility(View.VISIBLE);
                        yearSelectionLayAppoint.setVisibility(View.GONE);
                        Date dd = Calendar.getInstance().getTime();
                        String mo_name = simpleDateFormat.format(dd);
                        mo_name = mo_name.toUpperCase(Locale.ENGLISH);
                        String ms = "MONTH: " + mo_name;
                        monthSelectionPayment.setText(ms);
                        monthSelectionAppoint.setText(ms);

                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat format = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);

                        String normalDate = format.format(c);
                        paymentChartFirstDate = "01-"+normalDate;
                        appointChartFirstDate = "01-"+normalDate;

                        Calendar calendar1 = Calendar.getInstance();

                        calendar1.set(Calendar.DAY_OF_MONTH, 1);
                        calendar1.add(Calendar.MONTH, 1);
                        calendar1.add(Calendar.DATE, -1);
                        Date lastDayOfMonth = calendar1.getTime();

                        SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.ENGLISH);
                        String llll = sdff.format(lastDayOfMonth);
                        paymentChartLastDate =  llll+ "-" + normalDate;
                        appointChartLastDate =  llll+ "-" + normalDate;

                        chartTabPosition = tab.getPosition();
                        getChartData();
                    }
                    else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
                        monthSelectionLayPayment.setVisibility(View.GONE);
                        yearSelectionLayPayment.setVisibility(View.VISIBLE);
                        monthSelectionLayAppoint.setVisibility(View.GONE);
                        yearSelectionLayAppoint.setVisibility(View.VISIBLE);
                        Date dd = Calendar.getInstance().getTime();
                        String mo_name = simpleDateFormat.format(dd);
                        mo_name = mo_name.toUpperCase(Locale.ENGLISH);
                        String ms = "YEAR: " + mo_name;
                        yearSelectionPayment.setText(ms);
                        yearSelectionAppoint.setText(ms);

                        String short_year = mo_name.substring(mo_name.length()-2);

                        paymentChartFirstDate = "01-JAN-"+short_year;
                        paymentChartLastDate = "31-DEC-"+short_year;
                        appointChartFirstDate = "01-JAN-"+short_year;
                        appointChartLastDate = "31-DEC-"+short_year;

                        chartTabPosition = tab.getPosition();
                        getChartData();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (!chartTabUpdateNeeded) {
                    chartTabUpdateNeeded = true;
                }
            }
        });

        allPayAppCard.setOnClickListener(v -> {
            allPayAppCard.setCardBackgroundColor(getColor(R.color.green_sea));
            allPayAppText.setTextColor(getColor(R.color.white));

            offlinePayAppCard.setCardBackgroundColor(getColor(R.color.primaryColor_alpha));
            offPayAppText.setTextColor(getColor(R.color.green_sea));

            onlinePayAppCard.setCardBackgroundColor(getColor(R.color.primaryColor_alpha));
            onPayAppText.setTextColor(getColor(R.color.green_sea));
            if (pay_app_type != 0) {
                pay_app_type = 0;
                chartTabDataInit();
            }
        });

        offlinePayAppCard.setOnClickListener(v -> {
            allPayAppCard.setCardBackgroundColor(getColor(R.color.primaryColor_alpha));
            allPayAppText.setTextColor(getColor(R.color.green_sea));

            offlinePayAppCard.setCardBackgroundColor(getColor(R.color.green_sea));
            offPayAppText.setTextColor(getColor(R.color.white));

            onlinePayAppCard.setCardBackgroundColor(getColor(R.color.primaryColor_alpha));
            onPayAppText.setTextColor(getColor(R.color.green_sea));
            if (pay_app_type != 1) {
                pay_app_type = 1;
                chartTabDataInit();
            }
        });

        onlinePayAppCard.setOnClickListener(v -> {
            allPayAppCard.setCardBackgroundColor(getColor(R.color.primaryColor_alpha));
            allPayAppText.setTextColor(getColor(R.color.green_sea));

            offlinePayAppCard.setCardBackgroundColor(getColor(R.color.primaryColor_alpha));
            offPayAppText.setTextColor(getColor(R.color.green_sea));

            onlinePayAppCard.setCardBackgroundColor(getColor(R.color.green_sea));
            onPayAppText.setTextColor(getColor(R.color.white));
            if (pay_app_type != 2) {
                pay_app_type = 2;
                chartTabDataInit();
            }
        });

        tabRefresh.setOnClickListener(v -> {
            if (admin_user_flag.equals("1")) {
                getDocMeetingCount();
            }
            else {
                getAdminAppPayCount();
            }

        });

        chartTabRefresh.setOnClickListener(v -> getChartData());

        yearSelectionPayment.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            Date c = Calendar.getInstance().getTime();
            Date d = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ENGLISH);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);
            if (!paymentChartFirstDate.isEmpty()) {
                Date date = null;
                try {
                    date = simpleDateFormat.parse(paymentChartFirstDate);
                }
                catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }
                if (date != null) {
                    d = date;
                }
            }

            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(DocDashboard.this, (selectedMonth, selectedYear) -> {
                System.out.println("Selected Year: "+selectedYear);
                String ms = "YEAR: " + selectedYear;
                yearSelectionPayment.setText(ms);

                String short_year = String.valueOf(selectedYear).substring(String.valueOf(selectedYear).length()-2);
                paymentChartFirstDate = "01-JAN-"+short_year;
                paymentChartLastDate = "31-DEC-"+short_year;

                chartTabPosition = 1;
                getPaymentChart();

            },today.get(Calendar.YEAR),today.get(Calendar.MONTH));

            builder.setActivatedYear(Integer.parseInt(df.format(d)))
                    .setMinYear(Integer.parseInt(df.format(c))-10)
                    .setMaxYear(Integer.parseInt(df.format(c)))
                    .showYearOnly()
                    .setTitle("Selected Year")
                    .setOnYearChangedListener(year1 -> {
                    });

            yearDialogPayment = builder.build();
            yearDialogPayment.show();
        });

        paymentChartRefresh.setOnClickListener(v -> getPaymentChart());

        monthSelectionPayment.setOnClickListener(v -> {

            Date c1 = Calendar.getInstance().getTime();

            String formattedYear;
            String monthValue;
            String lastformattedYear;
            String lastdateView;

            SimpleDateFormat df1 = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("MM",Locale.ENGLISH);

            formattedYear = df1.format(c1);
            monthValue = sdf.format(c1);
            int nowMonNumb = Integer.parseInt(monthValue);
            nowMonNumb = nowMonNumb - 1;
            int lastMonNumb = nowMonNumb - 11;

            if (lastMonNumb < 0) {
                lastMonNumb = lastMonNumb + 12;
                int formatY = Integer.parseInt(formattedYear);
                formatY = formatY - 1;
                lastformattedYear = String.valueOf(formatY);
            } else {
                lastformattedYear = formattedYear;
            }

            Date today1 = new Date();

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(today1);

            calendar1.add(Calendar.MONTH, 1);
            calendar1.set(Calendar.DAY_OF_MONTH, 1);
            calendar1.add(Calendar.DATE, -1);

            Date lastDayOfMonth = calendar1.getTime();

            SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.ENGLISH);
            lastdateView = sdff.format(lastDayOfMonth);

            int yearSelected;
            int monthSelected;
            MonthFormat monthFormat = MonthFormat.LONG;
            String customTitle = "Select Month";

            Calendar calendar = Calendar.getInstance();
            if (!paymentChartFirstDate.isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                Date date = null;
                try {
                    date = simpleDateFormat.parse(paymentChartFirstDate);
                }
                catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }

                if (date != null) {
                    calendar.setTime(date);
                }
            }
            yearSelected = calendar.get(Calendar.YEAR);
            monthSelected = calendar.get(Calendar.MONTH);
            calendar.clear();
            calendar.set(Integer.parseInt(lastformattedYear), lastMonNumb, 1); // Set minimum date to show in dialog
            long minDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

            calendar.clear();
            calendar.set(Integer.parseInt(formattedYear), nowMonNumb, Integer.parseInt(lastdateView)); // Set maximum date to show in dialog
            long maxDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

            MonthYearPickerDialogFragment dialogFragment =  MonthYearPickerDialogFragment
                    .getInstance(monthSelected, yearSelected, minDate, maxDate, customTitle, monthFormat);

            dialogFragment.show(getSupportFragmentManager(), null);

            dialogFragment.setOnDateSetListener((year, monthOfYear) -> {
                System.out.println(year);
                System.out.println(monthOfYear);

                int month = monthOfYear + 1;
                String mon = "";
                String yearName;

                if (month == 1) {
                    mon = "JAN";
                } else if (month == 2) {
                    mon = "FEB";
                } else if (month == 3) {
                    mon = "MAR";
                } else if (month == 4) {
                    mon = "APR";
                } else if (month == 5) {
                    mon = "MAY";
                } else if (month == 6) {
                    mon = "JUN";
                } else if (month == 7) {
                    mon = "JUL";
                } else if (month == 8) {
                    mon = "AUG";
                } else if (month == 9) {
                    mon = "SEP";
                } else if (month == 10) {
                    mon = "OCT";
                } else if (month == 11) {
                    mon = "NOV";
                } else if (month == 12) {
                    mon = "DEC";
                }

                yearName = String.valueOf(year);
                yearName = yearName.substring(yearName.length()-2);


                paymentChartFirstDate = "01-"+mon+"-"+yearName;
                String ms = "MONTH: " + mon+"-"+yearName;
                monthSelectionPayment.setText(ms);

                SimpleDateFormat sss = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

                Date today11 = null;
                try {
                    today11 = sss.parse(paymentChartFirstDate);
                } catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }

                Calendar calendar11 = Calendar.getInstance();
                if (today11 != null) {
                    calendar11.setTime(today11);
                    calendar11.add(Calendar.MONTH, 1);
                    calendar11.set(Calendar.DAY_OF_MONTH, 1);
                    calendar11.add(Calendar.DATE, -1);

                    Date lastDayOfMonth1 = calendar11.getTime();

                    SimpleDateFormat sdff1 = new SimpleDateFormat("dd",Locale.ENGLISH);
                    String llll = sdff1.format(lastDayOfMonth1);
                    paymentChartLastDate =  llll+ "-" + mon +"-"+ yearName;
                }

                chartTabPosition = 0;
                getPaymentChart();
            });

        });

        yearSelectionAppoint.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            Date c = Calendar.getInstance().getTime();
            Date d = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ENGLISH);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy",Locale.ENGLISH);
            if (!appointChartFirstDate.isEmpty()) {
                Date date = null;
                try {
                    date = simpleDateFormat.parse(appointChartFirstDate);
                }
                catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }
                if (date != null) {
                    d = date;
                }
            }

            MonthPickerDialog.Builder appBuilder = new MonthPickerDialog.Builder(DocDashboard.this, (selectedMonth, selectedYear) -> {
                System.out.println("Selected Year: "+selectedYear);
                String ms = "YEAR: " + selectedYear;
                yearSelectionAppoint.setText(ms);

                String short_year = String.valueOf(selectedYear).substring(String.valueOf(selectedYear).length()-2);
                appointChartFirstDate = "01-JAN-"+short_year;
                appointChartLastDate = "31-DEC-"+short_year;

                chartTabPosition = 1;
                getAppointmentChart();

            },today.get(Calendar.YEAR),today.get(Calendar.MONTH));

            appBuilder.setActivatedYear(Integer.parseInt(df.format(d)))
                    .setMinYear(Integer.parseInt(df.format(c))-10)
                    .setMaxYear(Integer.parseInt(df.format(c)))
                    .showYearOnly()
                    .setTitle("Selected Year")
                    .setOnYearChangedListener(year1 -> {
                    });

            yearDialogAppoint = appBuilder.build();
            yearDialogAppoint.show();
        });

        appointChartRefresh.setOnClickListener(v -> getAppointmentChart());

        monthSelectionAppoint.setOnClickListener(v -> {

            Calendar mc = Calendar.getInstance();
            mc.add(Calendar.MONTH, 2);
            Date c1 = mc.getTime();

            String formattedYear;
            String monthValue;
            String lastformattedYear;
            String lastdateView;

            SimpleDateFormat df1 = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("MM",Locale.ENGLISH);

            formattedYear = df1.format(c1);
            monthValue = sdf.format(c1);
            int nowMonNumb = Integer.parseInt(monthValue);
            nowMonNumb = nowMonNumb - 1;
            int lastMonNumb = nowMonNumb - 11;

            if (lastMonNumb < 0) {
                lastMonNumb = lastMonNumb + 12;
                int formatY = Integer.parseInt(formattedYear);
                formatY = formatY - 1;
                lastformattedYear = String.valueOf(formatY);
            } else {
                lastformattedYear = formattedYear;
            }

            Date today1 = new Date();

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(today1);

            calendar1.add(Calendar.MONTH, 3);
            calendar1.set(Calendar.DAY_OF_MONTH, 1);
            calendar1.add(Calendar.DATE, -1);

            Date lastDayOfMonth = calendar1.getTime();

            SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.ENGLISH);
            lastdateView = sdff.format(lastDayOfMonth);

            int yearSelected;
            int monthSelected;
            MonthFormat monthFormat = MonthFormat.LONG;
            String customTitle = "Select Month";

            Calendar calendar = Calendar.getInstance();
            if (!appointChartFirstDate.isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                Date date = null;
                try {
                    date = simpleDateFormat.parse(appointChartFirstDate);
                }
                catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }

                if (date != null) {
                    calendar.setTime(date);
                }
            }
            yearSelected = calendar.get(Calendar.YEAR);
            monthSelected = calendar.get(Calendar.MONTH);
            calendar.clear();
            calendar.set(Integer.parseInt(lastformattedYear), lastMonNumb, 1); // Set minimum date to show in dialog
            long minDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

            calendar.clear();
            calendar.set(Integer.parseInt(formattedYear), nowMonNumb, Integer.parseInt(lastdateView)); // Set maximum date to show in dialog
            long maxDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

            MonthYearPickerDialogFragment dialogFragment =  MonthYearPickerDialogFragment
                    .getInstance(monthSelected, yearSelected, minDate, maxDate, customTitle, monthFormat);

            dialogFragment.show(getSupportFragmentManager(), null);

            dialogFragment.setOnDateSetListener((year, monthOfYear) -> {
                System.out.println(year);
                System.out.println(monthOfYear);

                int month = monthOfYear + 1;
                String mon = "";
                String yearName;

                if (month == 1) {
                    mon = "JAN";
                } else if (month == 2) {
                    mon = "FEB";
                } else if (month == 3) {
                    mon = "MAR";
                } else if (month == 4) {
                    mon = "APR";
                } else if (month == 5) {
                    mon = "MAY";
                } else if (month == 6) {
                    mon = "JUN";
                } else if (month == 7) {
                    mon = "JUL";
                } else if (month == 8) {
                    mon = "AUG";
                } else if (month == 9) {
                    mon = "SEP";
                } else if (month == 10) {
                    mon = "OCT";
                } else if (month == 11) {
                    mon = "NOV";
                } else if (month == 12) {
                    mon = "DEC";
                }

                yearName = String.valueOf(year);
                yearName = yearName.substring(yearName.length()-2);


                appointChartFirstDate = "01-"+mon+"-"+yearName;
                String ms = "MONTH: " + mon+"-"+yearName;
                monthSelectionAppoint.setText(ms);

                SimpleDateFormat sss = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

                Date today11 = null;
                try {
                    today11 = sss.parse(appointChartFirstDate);
                } catch (ParseException e) {
                    logger.log(Level.WARNING,e.getMessage(),e);
                }

                Calendar calendar11 = Calendar.getInstance();
                if (today11 != null) {
                    calendar11.setTime(today11);
                    calendar11.add(Calendar.MONTH, 1);
                    calendar11.set(Calendar.DAY_OF_MONTH, 1);
                    calendar11.add(Calendar.DATE, -1);

                    Date lastDayOfMonth1 = calendar11.getTime();

                    SimpleDateFormat sdff1 = new SimpleDateFormat("dd",Locale.ENGLISH);
                    String llll = sdff1.format(lastDayOfMonth1);
                    appointChartLastDate =  llll+ "-" + mon +"-"+ yearName;
                }

                chartTabPosition = 0;
                getAppointmentChart();
            });

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
            else if (item.getItemId() == R.id.patient_search_menu) {
                Intent intent = new Intent(DocDashboard.this, PatientSearch.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.admin_payment_menu) {
                Intent intent = new Intent(DocDashboard.this, AddPayment.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.admin_patient_search_menu) {
                Intent intent = new Intent(DocDashboard.this, PatientSearchAdmin.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.admin_appointment_schedule_menu) {
                Intent intent = new Intent(DocDashboard.this, AppointmentModify.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.admin_hr_account_menu) {
                if (Integer.parseInt(adminInfoLists.get(0).getHr_acc_active_flag()) == 1) {
                    if (Integer.parseInt(adminInfoLists.get(0).getAll_access_flag()) > 0) {
                        Intent intent = new Intent(DocDashboard.this, HRAccounts.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(DocDashboard.this, ReportManager.class);
                        startActivity(intent);
                    }
                }
                else {
                    Intent intent = new Intent(DocDashboard.this, ReportManager.class);
                    startActivity(intent);
                }
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
                        editor1.remove(DOC_ALL_ID);
                        editor1.remove(ADMIN_USR_ID);
                        editor1.remove(ADMIN_OR_USER_FLAG);
                        editor1.remove(LOGIN_TF);
                        editor1.apply();
                        editor1.commit();

                        Intent intent = new Intent(DocDashboard.this, DocLogin.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    })
                    .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

            AlertDialog alert = alertDialogBuilder.create();
            try {
                alert.show();
            }
            catch (Exception e) {
                restart("App is paused for a long time. Please Start the app again.");
            }
        });

        unitDoctor.setOnClickListener(v -> {
            Intent intent = new Intent(DocDashboard.this, UnitWiseAppointment.class);
            startActivity(intent);
        });

        allDoctorAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(DocDashboard.this, AllAppointment.class);
            intent.putExtra("ADMIN_USER_FLAG",admin_user_flag);
            startActivity(intent);
        });

        doctorReports.setOnClickListener(v -> {
            Intent intent = new Intent(DocDashboard.this, ReportManager.class);
            startActivity(intent);
        });

        switchUser.setOnClickListener(v -> {
            if (centerLists != null && !centerLists.isEmpty()) {
                if (centerLists.size() == 1) {
                    ArrayList<MultipleUserList> multipleUserLists = centerLists.get(0).getMultipleUserLists();
                    String cn = centerLists.get(0).getCenter_name();
                    String c_api = centerLists.get(0).getCenter_api();
                    UserSelectDialogue userSelectDialogue = new UserSelectDialogue(centerLists,multipleUserLists,DocDashboard.this,cn,c_api);
                    try {
                        userSelectDialogue.show(getSupportFragmentManager(),"USER_CENTER");
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
                else {
                    CenterSelectDialogue centerSelectDialogue = new CenterSelectDialogue(centerLists,DocDashboard.this);
                    try {
                        centerSelectDialogue.show(getSupportFragmentManager(),"CENTER");
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
            }
        });

        adminCaptureImage.setOnClickListener(view -> {
            if (admin_user_flag.equals("2")) {
                cameraResumeLoad = false;
//                ImagePicker.with(this)
//                        .crop()
//                        .compress(1024)
//                        .maxResultSize(1080,1080)
//                        .start(1114);
                ImageTakerChoiceDialog imageTakerChoiceDialog = new ImageTakerChoiceDialog();
                imageTakerChoiceDialog.show(getSupportFragmentManager(),"CH_IMAGE");
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
                alertDialogBuilder.setTitle("Exit!")
                        .setIcon(R.drawable.doc_diary_default)
                        .setMessage("Do you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                AlertDialog alert = alertDialogBuilder.create();
                try {
                    alert.show();
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }
            }
        });

        initData();
    }

    private void chartTabDataInit() {
        SimpleDateFormat simpleDateFormat;
        if (chartTabPosition == 0) {
            simpleDateFormat = new SimpleDateFormat("MMM-yy", Locale.ENGLISH);
            monthSelectionLayPayment.setVisibility(View.VISIBLE);
            yearSelectionLayPayment.setVisibility(View.GONE);
            monthSelectionLayAppoint.setVisibility(View.VISIBLE);
            yearSelectionLayAppoint.setVisibility(View.GONE);
            Date dd = Calendar.getInstance().getTime();
            String mo_name = simpleDateFormat.format(dd);
            mo_name = mo_name.toUpperCase(Locale.ENGLISH);
            String ms = "MONTH: " + mo_name;
            monthSelectionPayment.setText(ms);
            monthSelectionAppoint.setText(ms);

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);

            String normalDate = format.format(c);
            paymentChartFirstDate = "01-"+normalDate;
            appointChartFirstDate = "01-"+normalDate;

            Calendar calendar1 = Calendar.getInstance();

            calendar1.set(Calendar.DAY_OF_MONTH, 1);
            calendar1.add(Calendar.MONTH, 1);
            calendar1.add(Calendar.DATE, -1);
            Date lastDayOfMonth = calendar1.getTime();

            SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.ENGLISH);
            String llll = sdff.format(lastDayOfMonth);
            paymentChartLastDate =  llll+ "-" + normalDate;
            appointChartLastDate =  llll+ "-" + normalDate;

        }
        else {
            simpleDateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            monthSelectionLayPayment.setVisibility(View.GONE);
            yearSelectionLayPayment.setVisibility(View.VISIBLE);
            monthSelectionLayAppoint.setVisibility(View.GONE);
            yearSelectionLayAppoint.setVisibility(View.VISIBLE);
            Date dd = Calendar.getInstance().getTime();
            String mo_name = simpleDateFormat.format(dd);
            mo_name = mo_name.toUpperCase(Locale.ENGLISH);
            String ms = "YEAR: " + mo_name;
            yearSelectionPayment.setText(ms);
            yearSelectionAppoint.setText(ms);

            String short_year = mo_name.substring(mo_name.length()-2);

            paymentChartFirstDate = "01-JAN-"+short_year;
            paymentChartLastDate = "31-DEC-"+short_year;
            appointChartFirstDate = "01-JAN-"+short_year;
            appointChartLastDate = "31-DEC-"+short_year;

        }
        getChartData();
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    try {
                        startCrop(uri);
//                        selectedBitmap = getCorrectlyOrientedBitmap(uri);
////                        selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                        System.out.println("UPLOADED PIC");
//                        if (selectedBitmap != null) {
//                            updateAdminPic();
//                        }
//                        else {
//                            Toast.makeText(getApplicationContext(),"Invalid image",Toast.LENGTH_SHORT).show();
//                        }
                    }
                    catch (Exception e) {
                        logger.log(Level.WARNING,e.getMessage(),e);
                        Toast.makeText(getApplicationContext(),"Failed to read image",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Failed to get image",Toast.LENGTH_SHORT).show();
                }
            });

    private Bitmap getCorrectlyOrientedBitmap(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) {
                return null;
            }

            // Get real file path (copying file if necessary)
            String realPath = copyFileToInternalStorage(uri);

            // Read EXIF data
            if (realPath != null) {
                return modifyOrientation(bitmap, realPath);
            }
            else {
                return null;
            }

        }
        catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage(),e);
            Toast.makeText(getApplicationContext(),"Failed to upload image",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private String copyFileToInternalStorage(Uri uri) {
        File directory = getFilesDir(); // Internal storage
        File file = new File(directory, "temp_image.jpg");

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return file.getAbsolutePath(); // Now you have the file path

        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage(),e);
            Toast.makeText(getApplicationContext(),"Failed to get image path",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        File file = new File(context.getCacheDir(), "temp_image.jpg"); // Store in cache
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(context, "ttit.com.shuvo.docdiary.fileProvider", file);
    }

    private void initData() {
        sharedPreferences = getSharedPreferences(LOGIN_ACTIVITY_FILE,MODE_PRIVATE);
        doc_code = sharedPreferences.getString(DOC_USER_CODE,"");
        admin_usr_id = sharedPreferences.getString(ADMIN_USR_ID,"");
        admin_user_flag = sharedPreferences.getString(ADMIN_OR_USER_FLAG,"");
        pre_url_api = sharedPreferences.getString(DOC_DATA_API,"");
        Gson gson1 = new Gson();
        String json1 = sharedPreferences.getString(DOC_ALL_ID, "");
        Type type = new TypeToken<ArrayList<CenterList>>(){}.getType();
        centerLists = gson1.fromJson(json1, type);

        if (centerLists != null) {
            if (centerLists.isEmpty()) {
                System.out.println("NO USER");
                switchUser.setVisibility(View.GONE);
            }
            else {
                switchUser.setVisibility(View.VISIBLE);
            }
        }
        else {
            System.out.println("USER NULL PAISE");
            switchUser.setVisibility(View.GONE);
        }

        if (admin_user_flag.equals("1")) {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_menu);
            adminCaptureImage.setVisibility(View.GONE);

        }
        else {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_menu_admin);
            adminCaptureImage.setVisibility(View.VISIBLE);
            LineChartInit();
        }
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        userInfoLists = new ArrayList<>();
        adminInfoLists = new ArrayList<>();

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
                logger.log(Level.WARNING,e.getMessage(),e);
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(": ").append(fieldName);
            }
        }
        osName = builder.toString();
        //        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        System.out.println("1st Phase");
    }

    public void LineChartInit() {

        XAxis xAxis = paymentChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.GRAY);
        paymentChart.getDescription().setEnabled(false);
        paymentChart.setPinchZoom(false);

        paymentChart.setDrawGridBackground(false);
        paymentChart.setExtraLeftOffset(30);
        paymentChart.setExtraRightOffset(30);
        paymentChart.getAxisLeft().setDrawGridLines(true);
        paymentChart.getAxisLeft().setEnabled(false);
        paymentChart.setScaleEnabled(true);
        paymentChart.setTouchEnabled(true);
        paymentChart.setHighlightPerTapEnabled(true);
        paymentChart.setDoubleTapToZoomEnabled(false);

        paymentChart.setExtraOffsets(20,0,20,20);

        paymentChart.getAxisRight().setEnabled(false);
        paymentChart.getAxisLeft().setAxisMinimum(0);
        paymentChart.getLegend().setEnabled(true);
        paymentChart.getLegend().setStackSpace(20);
        paymentChart.getLegend().setYOffset(10);
        paymentChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        paymentChart.getAxisLeft().setTextColor(Color.GRAY);
        paymentChart.getAxisLeft().setXOffset(10f);
        paymentChart.getAxisLeft().setTextSize(6);
        paymentChart.getAxisLeft().setDrawGridLines(false);
        paymentChart.getXAxis().setLabelRotationAngle(45);

        XAxis xAxisApp = appointmentChart.getXAxis();
        xAxisApp.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisApp.setGranularity(1);
        xAxisApp.setDrawGridLines(false);
        xAxisApp.setTextColor(Color.GRAY);
        appointmentChart.getDescription().setEnabled(false);
        appointmentChart.setPinchZoom(false);

        appointmentChart.setDrawGridBackground(false);
        appointmentChart.setExtraLeftOffset(30);
        appointmentChart.setExtraRightOffset(30);
        appointmentChart.getAxisLeft().setDrawGridLines(true);
        appointmentChart.getAxisLeft().setEnabled(false);
        appointmentChart.setScaleEnabled(true);
        appointmentChart.setTouchEnabled(true);
        appointmentChart.setHighlightPerTapEnabled(true);
        appointmentChart.setDoubleTapToZoomEnabled(false);

        appointmentChart.setExtraOffsets(20,0,20,20);

        appointmentChart.getAxisRight().setEnabled(false);
        appointmentChart.getAxisLeft().setAxisMinimum(0);
        appointmentChart.getLegend().setEnabled(true);
        appointmentChart.getLegend().setStackSpace(20);
        appointmentChart.getLegend().setYOffset(10);
        appointmentChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        appointmentChart.getAxisLeft().setTextColor(Color.GRAY);
        appointmentChart.getAxisLeft().setXOffset(10f);
        appointmentChart.getAxisLeft().setTextSize(6);
        appointmentChart.getAxisLeft().setDrawGridLines(false);
        appointmentChart.getXAxis().setLabelRotationAngle(45);
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
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
            Log.e("Error: ", ex.toString());
        } // for now eat exceptions
        return "";
    }

    public static String getHostName(String defValue) {
        try {
            String tt = "getString";
            @SuppressLint("DiscouragedPrivateApi") Method getString = Build.class.getDeclaredMethod(tt, String.class);
            getString.setAccessible(true);
            if (getString.invoke(null, "net.hostname") == null) {
                return defValue;
            }
            else {
                return Objects.requireNonNull(getString.invoke(null, "net.hostname")).toString();
            }
        } catch (Exception ex) {
            return defValue;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraResumeLoad = false;
        if (requestCode == 1114) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                if (data != null) {
                    Uri resultUri = data.getData();
                    try {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        System.out.println("UPLOADED PIC");

                        updateAdminPic();

                    } catch (IOException e) {
                        logger.log(Level.WARNING,e.getMessage(),e);
                        Toast.makeText(getApplicationContext(),"Failed to upload image",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Failed to get image",Toast.LENGTH_SHORT).show();
            }
        }
        else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                Uri croppedUri = UCrop.getOutput(data);
                selectedBitmap = getCorrectlyOrientedBitmap(croppedUri);
                if (selectedBitmap != null) {
                    updateAdminPic();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Invalid image",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Failed to crop image",Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
//            if (data != null) {
//                Throwable error = UCrop.getError(data);
//                Toast.makeText(getApplicationContext(), error != null ? error.getLocalizedMessage() : "Image handler failed", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(getApplicationContext(),"Failed to crop image",Toast.LENGTH_SHORT).show();
//            }
            Toast.makeText(getApplicationContext(), "Invalid Image", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("First_flag on resume: "+ first_flag);
        if (!cameraResumeLoad) {
            cameraResumeLoad = true;
        }
        else {
            if (loading != null) {
                if (!loading) {
                    if (admin_user_flag.equals("1")) {
                        if (first_flag == 0) {
                            getDocData();
                        } else {
                            loading = true;
                            fullLayout.setVisibility(View.GONE);
                            progressBarCard.setVisibility(View.GONE);
                            bottomNavigationView.setVisibility(View.GONE);
                            circularProgressIndicator.setVisibility(View.VISIBLE);
                            tabFullLayout.setVisibility(View.GONE);
                            tabFullLayoutAdmin.setVisibility(View.GONE);
                            tabCircularProgressIndicator.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.GONE);
                            tabRefresh.setVisibility(View.GONE);
                            graphicalViewLayout.setVisibility(View.GONE);
                            chartTabCircularProgressIndicator.setVisibility(View.GONE);
                            conn = false;
                            connected = false;
                            getDocSchedule();
                        }
                    } else {
                        if (first_flag == 0) {
                            getAdminData();
                        } else {
                            loading = true;
                            fullLayout.setVisibility(View.GONE);
                            progressBarCard.setVisibility(View.GONE);
                            bottomNavigationView.setVisibility(View.GONE);
                            circularProgressIndicator.setVisibility(View.VISIBLE);
                            tabFullLayout.setVisibility(View.GONE);
                            tabFullLayoutAdmin.setVisibility(View.GONE);
                            tabCircularProgressIndicator.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.GONE);
                            tabRefresh.setVisibility(View.GONE);
                            graphicalViewLayout.setVisibility(View.GONE);
                            chartTabCircularProgressIndicator.setVisibility(View.GONE);
                            pay_app_type = 0;
                            conn = false;
                            connected = false;
                            getAdminDashboardData();
                        }
                    }
                }
            } else {
                restart("App is paused for a long time. Please Start the app again.");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        normalCountDownView.stopTimer();
    }

    /// ---------------- DOCTOR DETAILS -------------------
    public void getDocData() {
        loading = true;
        fullLayout.setVisibility(View.GONE);
        progressBarCard.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        tabFullLayout.setVisibility(View.GONE);
        tabFullLayoutAdmin.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);
        graphicalViewLayout.setVisibility(View.GONE);
        chartTabCircularProgressIndicator.setVisibility(View.GONE);
        conn = false;
        connected = false;
        userAvailable = false;
        userInfoLists = new ArrayList<>();
        expiry_date = "";
        doctor_status = "";
        doc_head_flag = "0";
        doc_manager_flag = "0";
        first_login_flag ="1";
        System.out.println("2nd Phase");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

        Calendar calendarcc = Calendar.getInstance();
        calendarcc.add(Calendar.MONTH,3);
        Date mmm = calendarcc.getTime();

        if (expiry_date.isEmpty()) {
            expiry_date = dateFormat.format(mmm);
        }

        String docDataUrl = pre_url_api+"doc_dashboard/getDocData?doc_code="+doc_code;
        String expiryDateUrl = pre_url_api+"doc_dashboard/updateDocExpDate";
//        String flagUpdateUrl = pre_url_api+"login/updateFLFlag";

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
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateInterface();
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateInterface();
        }) {
            @Override
            public Map<String, String> getHeaders() {
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
                            String deptd_id = docInfo.getString("deptd_id")
                                    .equals("null") ? "" :docInfo.getString("deptd_id");
                            doc_head_flag = docInfo.getString("doc_deptd_head_flag")
                                    .equals("null") ? "0" :docInfo.getString("doc_deptd_head_flag");
                            doc_manager_flag = docInfo.getString("doc_manager_flag")
                                    .equals("null") ? "0" :docInfo.getString("doc_manager_flag");
                            String pat_app_history = docInfo.getString("pat_app_history")
                                    .equals("null") ? "0" :docInfo.getString("pat_app_history");
                            String upcoming_pat_history = docInfo.getString("upcoming_pat_history")
                                    .equals("null") ? "0" :docInfo.getString("upcoming_pat_history");
                            String pat_pres_view = docInfo.getString("pat_pres_view")
                                    .equals("null") ? "0" :docInfo.getString("pat_pres_view");
                            String all_presc = docInfo.getString("all_presc")
                                    .equals("null") ? "0" :docInfo.getString("all_presc");

                            userInfoLists.add(new UserInfoList(doc_name,nn_doc_id,doc_code,depts_name,deptd_name,deptm_name,
                                    desig_name,doc_eff_date,doc_status,depts_id,desig_id,doc_video_link,doc_video_link_enable_flag,
                                    doc_center_name,deptd_id,pat_app_history,upcoming_pat_history,pat_pres_view,all_presc,doc_head_flag));
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

//        StringRequest flagUpdateReq = new StringRequest(Request.Method.POST, flagUpdateUrl, response -> {
//            conn = true;
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                String string_out = jsonObject.getString("string_out");
//                if (string_out.equals("Successfully Created")) {
//                    requestQueue.add(docDataReq);
//                }
//                else {
//                    System.out.println(string_out);
//                    parsing_message = string_out;
//                    connected = false;
//                    updateInterface();
//                }
//            }
//            catch (JSONException e) {
//                logger.log(Level.WARNING,e.getMessage(),e);
//                connected = false;
//                parsing_message = e.getLocalizedMessage();
//                updateInterface();
//            }
//        }, error -> {
//            logger.log(Level.WARNING,error.getMessage(),error);
//            conn = false;
//            connected = false;
//            parsing_message = error.getLocalizedMessage();
//            updateInterface();
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("P_DOC_CODE", doc_code);
//                return headers;
//            }
//        };

//        if (user_switch) {
//            requestQueue.add(flagUpdateReq);
//            System.out.println("USER SWITCHED");
//        }
//        else {
//            requestQueue.add(docDataReq);
//            System.out.println("USER NOT SWITCHED");
//        }
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
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateInterface();
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateInterface();
        }) {
            @Override
            public Map<String, String> getHeaders() {
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
        String docDataUrl = pre_url_api+"doc_dashboard/getMeetingSchedule?doc_id="+doc_id+"&time_now="+time_now;
        String docMeetingUrl = pre_url_api+"doc_dashboard/getMeetingCount?doc_id="+doc_id+"&first_date="+date_now+"&end_date="+date_now;
        String docScheduleUrl = pre_url_api+"doc_dashboard/getScheduleCount?doc_id="+doc_id+"&first_date="+date_now+"&end_date="+date_now;
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

                        if (doc_profile_pic.equals("null") || doc_profile_pic.isEmpty()) {
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
                logger.log(Level.WARNING,e.getMessage(),e);
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
            logger.log(Level.WARNING,error.getMessage(),error);
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
                logger.log(Level.WARNING,e.getMessage(),e);
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
            logger.log(Level.WARNING,error.getMessage(),error);
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
                            Date time;
                            try {
                                time = timeFormat.parse(schedule_time);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
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
                logger.log(Level.WARNING,e.getMessage(),e);
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
            logger.log(Level.WARNING,error.getMessage(),error);
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
                logger.log(Level.WARNING,e.getMessage(),e);
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
            logger.log(Level.WARNING,error.getMessage(),error);
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
                        progressBarCard.setVisibility(View.GONE);
                        bottomNavigationView.setVisibility(View.GONE);
                        circularProgressIndicator.setVisibility(View.GONE);
                        tabFullLayout.setVisibility(View.GONE);
                        tabFullLayoutAdmin.setVisibility(View.GONE);
                        tabCircularProgressIndicator.setVisibility(View.GONE);
                        tabLayout.setVisibility(View.GONE);
                        tabRefresh.setVisibility(View.GONE);
                        graphicalViewLayout.setVisibility(View.GONE);
                        chartTabCircularProgressIndicator.setVisibility(View.GONE);

                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
                        alertDialogBuilder.setTitle("Forced Log Out!")
                                .setMessage("You are forced to log out from the app for server maintenance. We are sorry for the disturbance. Please Login again to continue the app.")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                    editor1.remove(DOC_USER_CODE);
                                    editor1.remove(DOC_USER_PASSWORD);
                                    editor1.remove(DOC_DATA_API);
                                    editor1.remove(DOC_ALL_ID);
                                    editor1.remove(ADMIN_USR_ID);
                                    editor1.remove(ADMIN_OR_USER_FLAG);
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
                        Date exp_date;
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
                        if (exp_date.getTime() < now_date.getTime()) {
                            fullLayout.setVisibility(View.GONE);
                            progressBarCard.setVisibility(View.GONE);
                            bottomNavigationView.setVisibility(View.GONE);
                            circularProgressIndicator.setVisibility(View.GONE);
                            tabFullLayout.setVisibility(View.GONE);
                            tabFullLayoutAdmin.setVisibility(View.GONE);
                            tabCircularProgressIndicator.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.GONE);
                            tabRefresh.setVisibility(View.GONE);
                            graphicalViewLayout.setVisibility(View.GONE);
                            chartTabCircularProgressIndicator.setVisibility(View.GONE);

                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
                            alertDialogBuilder.setTitle("Date Expired!")
                                    .setMessage("Your access to the app is expired on: "+expiry_date+".\n" +
                                            "To gain access to the app, Please contact with the administrator")
                                    .setPositiveButton("OK", (dialog, which) -> {

                                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                        editor1.remove(DOC_USER_CODE);
                                        editor1.remove(DOC_USER_PASSWORD);
                                        editor1.remove(DOC_DATA_API);
                                        editor1.remove(DOC_ALL_ID);
                                        editor1.remove(ADMIN_USR_ID);
                                        editor1.remove(ADMIN_OR_USER_FLAG);
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
                            progressBarCard.setVisibility(View.VISIBLE);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            circularProgressIndicator.setVisibility(View.GONE);
                            tabFullLayout.setVisibility(View.VISIBLE);
                            tabFullLayoutAdmin.setVisibility(View.GONE);
                            tabCircularProgressIndicator.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.VISIBLE);
                            tabRefresh.setVisibility(View.GONE);
                            graphicalViewLayout.setVisibility(View.GONE);
                            chartTabCircularProgressIndicator.setVisibility(View.GONE);

                            conn = false;
                            connected = false;
                            userAvailable = false;

                            progress_track_flag_value = 0;
                            String doc_center = "";
                            if (userInfoLists == null) {
                                restart("Could Not Get Doctor Data. Please Restart the App.");
                            }
                            else {
                                if (userInfoLists.isEmpty()) {
                                    restart("Could Not Get Doctor Data. Please Restart the App.");
                                }
                                else {
                                    doc_id = userInfoLists.get(0).getDoc_id();
                                    String dd = userInfoLists.get(0).getDoc_name();
                                    dd = dd.length() < 3 ? dd : dd.substring(0, 3);
                                    dd = dd.toLowerCase();

                                    String doc_name;
                                    if (dd.contains("dr.")) {
                                        doc_name = userInfoLists.get(0).getDoc_name();
                                    }
                                    else {
                                        doc_name = "Dr. "+userInfoLists.get(0).getDoc_name();
                                    }
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
                            if (doc_manager_flag.equals("1")) {
                                allDoctorAppointment.setVisibility(View.VISIBLE);
                                doctorReports.setVisibility(View.VISIBLE);
                                unitDoctor.setVisibility(View.GONE);
                            }
                            else {
                                allDoctorAppointment.setVisibility(View.GONE);
                                doctorReports.setVisibility(View.GONE);
                                if (doc_head_flag.equals("1")) {
                                    unitDoctor.setVisibility(View.VISIBLE);
                                }
                                else {
                                    unitDoctor.setVisibility(View.GONE);
                                }
                            }

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                            SimpleDateFormat only_date_format = new SimpleDateFormat("dd", Locale.ENGLISH);
                            SimpleDateFormat full_date_format = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                            SimpleDateFormat full_date_format_range = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);


                            if (!last_schedule.isEmpty() && !next_schedule.isEmpty()) {
                                String nt = "Next Meeting";
                                nextMeetingText.setText(nt);
                                meetingTime.setVisibility(View.VISIBLE);
                                patientName.setVisibility(View.VISIBLE);
                                timerIcon.setVisibility(View.VISIBLE);
                                normalCountDownView.setVisibility(View.VISIBLE);
                                Date prv_meeting_date;
                                try {
                                    prv_meeting_date = sdf.parse(last_schedule);
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }

                                Date to_Date = Calendar.getInstance().getTime();

                                Date next_meeting_date;
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

                                progress_track = (100 * remaining_seconds) / total_seconds;

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

                                    String at = "At  "+time;
                                    meetingTime.setText(at);
                                }
                                else {
                                    String  tt = "At  " + time +"\nIn  "+ full_date;
                                    meetingTime.setText(tt);
                                }

                                horizontalProgressView.setProgressCompat(progress_track,true);
                                normalCountDownView.stopTimer();

                                int mm = remaining_seconds / 60;
                                int hh = mm / 60;
                                normalCountDownView.setShowHour(hh != 0);

                                normalCountDownView.setShowMinutes(mm != 0);

                                normalCountDownView.initTimer(remaining_seconds, HappyTimer.Type.COUNT_DOWN);
                                normalCountDownView.startTimer();

                                String text;
                                if (patient_name.isEmpty()) {
                                    text = "No Name Found";
                                }
                                else {
                                    text = "With " + patient_name;
                                }
                                patientName.setText(text);
                            }
                            else if (last_schedule.isEmpty() && !next_schedule.isEmpty()) {
                                String nt = "Next Meeting";
                                nextMeetingText.setText(nt);
                                meetingTime.setVisibility(View.VISIBLE);
                                patientName.setVisibility(View.VISIBLE);
                                timerIcon.setVisibility(View.VISIBLE);
                                normalCountDownView.setVisibility(View.VISIBLE);

                                Date to_Date = Calendar.getInstance().getTime();

                                Date next_meeting_date;
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

                                progress_track = (100 * remaining_seconds) / total_seconds;

                                if (progress_track < 0) {
                                    progress_track = 0;
                                }

                                System.out.println("progress_track: "+progress_track);

                                String time = simpleDateFormat.format(next_meeting_date);
                                String date = only_date_format.format(next_meeting_date);
                                String n_date = only_date_format.format(Calendar.getInstance().getTime());
                                String full_date = full_date_format.format(next_meeting_date);

                                if (date.equals(n_date)) {
                                    String mt = "At "+time;
                                    meetingTime.setText(mt);
                                }
                                else {
                                    String  tt = "At " + time +"\nIn "+ full_date;
                                    meetingTime.setText(tt);
                                }

                                horizontalProgressView.setProgressCompat(progress_track,true);
                                normalCountDownView.stopTimer();

                                int mm = remaining_seconds / 60;
                                int hh = mm / 60;
                                normalCountDownView.setShowHour(hh != 0);

                                normalCountDownView.setShowMinutes(mm != 0);

                                normalCountDownView.initTimer(remaining_seconds, HappyTimer.Type.COUNT_DOWN);
                                normalCountDownView.startTimer();

                                String text;
                                if (patient_name.isEmpty()) {
                                    text = "No Name Found";
                                }
                                else {
                                    text = "With " + patient_name;
                                }
                                patientName.setText(text);
                            }
                            else  {
                                String nt = "No Upcoming Meeting Available";
                                nextMeetingText.setText(nt);
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

                            getGreetingText();

                            TabLayout.Tab tabAt = tabLayout.getTabAt(0);
                            tabUpdateNeeded = false;
                            tabLayout.selectTab(tabAt);

                            if (imageFound) {
                                try {
                                    Glide.with(getApplicationContext())
                                            .load(bitmap)
                                            .fitCenter()
                                            .into(docImage);
                                }
                                catch (Exception e) {
                                    restart("App is paused for a long time. Please Start the app again.");
                                }
                            }
                            else {
                                docImage.setImageResource(R.drawable.doctor);
                            }

                            bottomNavigationView.getMenu().findItem(R.id.patient_search_menu).setVisible(userInfoLists.get(0).getPat_pres_view().equals("1"));

                            first_flag = 1;
                        }
                    }
                }
                else {
                    fullLayout.setVisibility(View.GONE);
                    progressBarCard.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.GONE);
                    tabFullLayout.setVisibility(View.GONE);
                    tabFullLayoutAdmin.setVisibility(View.GONE);
                    tabCircularProgressIndicator.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    tabRefresh.setVisibility(View.GONE);
                    graphicalViewLayout.setVisibility(View.GONE);
                    chartTabCircularProgressIndicator.setVisibility(View.GONE);

                    String e_msg;
                    if (doctor_status.isEmpty()) {
                        e_msg = "No User Data Found";
                    }
                    else {
                        e_msg ="Your status is: "+doctor_status+", with effected date: "+effected_date+". " +
                                "So, You are not eligible to access this app.";
                    }
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
                    alertDialogBuilder.setTitle("Access Denied!")
                            .setMessage(e_msg)
                            .setPositiveButton("OK", (dialog, which) -> {

                                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                editor1.remove(DOC_USER_CODE);
                                editor1.remove(DOC_USER_PASSWORD);
                                editor1.remove(DOC_DATA_API);
                                editor1.remove(DOC_ALL_ID);
                                editor1.remove(ADMIN_USR_ID);
                                editor1.remove(ADMIN_OR_USER_FLAG);
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
        fullLayout.setVisibility(View.GONE);
        progressBarCard.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        tabFullLayout.setVisibility(View.GONE);
        tabFullLayoutAdmin.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);
        graphicalViewLayout.setVisibility(View.GONE);
        chartTabCircularProgressIndicator.setVisibility(View.GONE);

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
                    loading = false;
                    getDocData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    loading = false;
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
                progressBarCard.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                tabFullLayout.setVisibility(View.VISIBLE);
                tabFullLayoutAdmin.setVisibility(View.GONE);
                tabCircularProgressIndicator.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                tabRefresh.setVisibility(View.GONE);
                graphicalViewLayout.setVisibility(View.GONE);
                chartTabCircularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                progress_track_flag_value = 0;

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                SimpleDateFormat only_date_format = new SimpleDateFormat("dd", Locale.ENGLISH);
                SimpleDateFormat full_date_format = new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH);
                SimpleDateFormat full_date_format_range = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);


                if (!last_schedule.isEmpty() && !next_schedule.isEmpty()) {
                    String nt = "Next Meeting";
                    nextMeetingText.setText(nt);
                    meetingTime.setVisibility(View.VISIBLE);
                    patientName.setVisibility(View.VISIBLE);
                    timerIcon.setVisibility(View.VISIBLE);
                    normalCountDownView.setVisibility(View.VISIBLE);
                    Date prv_meeting_date;
                    try {
                        prv_meeting_date = sdf.parse(last_schedule);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    Date to_Date = Calendar.getInstance().getTime();

                    Date next_meeting_date;
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

                    progress_track = (100 * remaining_seconds) / total_seconds;

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

                        String mt = "At  "+time;
                        meetingTime.setText(mt);
                    }
                    else {
                        String  tt = "At  " + time +"\nIn  "+ full_date;
                        meetingTime.setText(tt);
                    }

                    horizontalProgressView.setProgressCompat(progress_track,true);
                    normalCountDownView.stopTimer();

                    int mm = remaining_seconds / 60;
                    int hh = mm / 60;
                    normalCountDownView.setShowHour(hh != 0);

                    normalCountDownView.setShowMinutes(mm != 0);

                    normalCountDownView.initTimer(remaining_seconds, HappyTimer.Type.COUNT_DOWN);
                    normalCountDownView.startTimer();

                    String text;
                    if (patient_name.isEmpty()) {
                        text = "No Name Found";
                    }
                    else {
                        text = "With " + patient_name;
                    }
                    patientName.setText(text);
                }
                else if (last_schedule.isEmpty() && !next_schedule.isEmpty()) {
                    String nt = "Next Meeting";
                    nextMeetingText.setText(nt);
                    meetingTime.setVisibility(View.VISIBLE);
                    patientName.setVisibility(View.VISIBLE);
                    timerIcon.setVisibility(View.VISIBLE);
                    normalCountDownView.setVisibility(View.VISIBLE);

                    Date to_Date = Calendar.getInstance().getTime();

                    Date next_meeting_date;
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

                    progress_track = (100 * remaining_seconds) / total_seconds;

                    if (progress_track < 0) {
                        progress_track = 0;
                    }

                    System.out.println("progress_track: "+progress_track);

                    String time = simpleDateFormat.format(next_meeting_date);
                    String date = only_date_format.format(next_meeting_date);
                    String n_date = only_date_format.format(Calendar.getInstance().getTime());
                    String full_date = full_date_format.format(next_meeting_date);

                    if (date.equals(n_date)) {
                        String mt = "At "+time;
                        meetingTime.setText(mt);
                    }
                    else {
                        String  tt = "At " + time +"\nIn "+ full_date;
                        meetingTime.setText(tt);
                    }

                    horizontalProgressView.setProgressCompat(progress_track,true);
                    normalCountDownView.stopTimer();

                    int mm = remaining_seconds / 60;
                    int hh = mm / 60;
                    normalCountDownView.setShowHour(hh != 0);

                    normalCountDownView.setShowMinutes(mm != 0);

                    normalCountDownView.initTimer(remaining_seconds, HappyTimer.Type.COUNT_DOWN);
                    normalCountDownView.startTimer();

                    String text;
                    if (patient_name.isEmpty()) {
                        text = "No Name Found";
                    }
                    else {
                        text = "With " + patient_name;
                    }
                    patientName.setText(text);
                }
                else  {
                    String nt = "No Upcoming Meeting Available";
                    nextMeetingText.setText(nt);
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

                getGreetingText();

                TabLayout.Tab tabAt = tabLayout.getTabAt(0);
                tabUpdateNeeded = false;
                tabLayout.selectTab(tabAt);

                if (imageFound) {
                    try {
                        Glide.with(getApplicationContext())
                                .load(bitmap)
                                .fitCenter()
                                .into(docImage);
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
                else {
                    docImage.setImageResource(R.drawable.doctor);
                }

                askUserForReview();
                first_flag = 1;
                loading = false;
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
        progressBarCard.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        tabFullLayout.setVisibility(View.GONE);
        tabFullLayoutAdmin.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);
        graphicalViewLayout.setVisibility(View.GONE);
        chartTabCircularProgressIndicator.setVisibility(View.GONE);

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
                    loading = false;
                    fullLayout.setVisibility(View.GONE);
                    progressBarCard.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    tabFullLayout.setVisibility(View.GONE);
                    tabFullLayoutAdmin.setVisibility(View.GONE);
                    tabCircularProgressIndicator.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    tabRefresh.setVisibility(View.GONE);
                    graphicalViewLayout.setVisibility(View.GONE);
                    chartTabCircularProgressIndicator.setVisibility(View.GONE);
                    conn = false;
                    connected = false;
                    loading = true;
                    getDocSchedule();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    loading = false;
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
        tabFullLayoutAdmin.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;
        System.out.println("HAI HAI");

        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
        String docMeetingUrl = pre_url_api+"doc_dashboard/getMeetingCount?doc_id="+doc_id+"&first_date="+tabFirstDate+"&end_date="+tabEndDate;
        String docScheduleUrl = pre_url_api+"doc_dashboard/getScheduleCount?doc_id="+doc_id+"&first_date="+tabFirstDate+"&end_date="+tabEndDate;

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
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateTabLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
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
                            Date time;
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
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateTabLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateTabLayout();
        });

        requestQueue.add(docMeetReq);
    }

    private void updateTabLayout() {
        if (conn) {
            if (connected) {
                tabFullLayout.setVisibility(View.VISIBLE);
                tabFullLayoutAdmin.setVisibility(View.GONE);
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
                loading = false;

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
        tabFullLayoutAdmin.setVisibility(View.GONE);
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
                    loading = false;
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

    @Override
    public void onDismiss() {
        normalCountDownView.stopTimer();
        loading = false;
        initData();
        getDocData();
    }

    @Override
    public void onIdDismiss() {
        normalCountDownView.stopTimer();
        loading = false;
        initData();
        getDocData();
    }

    public void getGreetingText() {
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String wt;
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
    }


    ///---------------------- ADMIN DETAILS ------------------------
    public void getAdminData() {
        loading = true;
        fullLayout.setVisibility(View.GONE);
        progressBarCard.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        tabFullLayout.setVisibility(View.GONE);
        tabFullLayoutAdmin.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);
        graphicalViewLayout.setVisibility(View.GONE);
        chartTabCircularProgressIndicator.setVisibility(View.GONE);
        conn = false;
        connected = false;
        adminInfoLists = new ArrayList<>();
        admin_fl_flag = "0";
        mob_app_access_flag = "0";
        adminAvailable = false;
        pay_app_type = 0;

        String adminDataUrl = pre_url_api+"doc_dashboard/getAdminData?p_usr_id="+admin_usr_id;
//        String flagUpdateUrl = pre_url_api+"login/updateAdminFLFlag";

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        StringRequest adminDataReq = new StringRequest(Request.Method.GET, adminDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject adminInfo = array.getJSONObject(i);

                        String usr_name = adminInfo.getString("usr_name")
                                .equals("null") ? "" : adminInfo.getString("usr_name");
                        String usr_fname = adminInfo.getString("usr_fname")
                                .equals("null") ? "" :adminInfo.getString("usr_fname");
                        String usr_lname = adminInfo.getString("usr_lname")
                                .equals("null") ? "" :adminInfo.getString("usr_lname");
                        String usr_email = adminInfo.getString("usr_email")
                                .equals("null") ? "" :adminInfo.getString("usr_email");
                        String usr_contact = adminInfo.getString("usr_contact")
                                .equals("null") ? "" :adminInfo.getString("usr_contact");
                        mob_app_access_flag = adminInfo.getString("usr_mob_app_access")
                                .equals("null") ? "0" :adminInfo.getString("usr_mob_app_access");
//                        String usr_mob_app_analytic_access = adminInfo.getString("usr_mob_app_analytic_access")
//                                .equals("null") ? "0" :adminInfo.getString("usr_mob_app_analytic_access");
                        admin_fl_flag = adminInfo.getString("usr_mob_forced_log_out_flag")
                                .equals("null") ? "0" :adminInfo.getString("usr_mob_forced_log_out_flag");
                        String admin_center_name = adminInfo.getString("admin_center_name")
                                .equals("null") ? "" :adminInfo.getString("admin_center_name");
                        String all_access_flag = adminInfo.getString("all_access_flag")
                                .equals("null") ? "0" :adminInfo.getString("all_access_flag");
                        String hr_payment_active = adminInfo.getString("hr_payment_active")
                                .equals("null") ? "0" :adminInfo.getString("hr_payment_active");
                        String hr_appointment_active = adminInfo.getString("hr_appointment_active")
                                .equals("null") ? "0" :adminInfo.getString("hr_appointment_active");
                        String acc_payment_active = adminInfo.getString("acc_payment_active")
                                .equals("null") ? "0" :adminInfo.getString("acc_payment_active");
                        String acc_appointment_active = adminInfo.getString("acc_appointment_active")
                                .equals("null") ? "0" :adminInfo.getString("acc_appointment_active");
                        String hr_dashboard = adminInfo.getString("hr_dashboard")
                                .equals("null") ? "0" :adminInfo.getString("hr_dashboard");
                        String acc_dashboard = adminInfo.getString("acc_dashboard")
                                .equals("null") ? "0" :adminInfo.getString("acc_dashboard");
                        String hr_acc_active_flag = adminInfo.getString("hr_acc_active_flag")
                                .equals("null") ? "0" :adminInfo.getString("hr_acc_active_flag");
                        String is_pay_mode_active = adminInfo.getString("is_pay_mode_active")
                                .equals("null") ? "0" :adminInfo.getString("is_pay_mode_active");
                        String is_pay_appoint_type_active = adminInfo.getString("is_pay_appoint_type_active")
                                .equals("null") ? "0" :adminInfo.getString("is_pay_appoint_type_active");
                        String analytics_dashboard = adminInfo.getString("analytics_dashboard")
                                .equals("null") ? "0" :adminInfo.getString("analytics_dashboard");

                        adminInfoLists.add(new AdminInfoList(admin_usr_id, usr_name,usr_fname,usr_lname,
                                usr_email,usr_contact,all_access_flag, admin_center_name, hr_payment_active, hr_appointment_active,
                                acc_payment_active,acc_appointment_active,hr_dashboard,acc_dashboard,hr_acc_active_flag, is_pay_mode_active,
                                is_pay_appoint_type_active, analytics_dashboard));

                        if (Integer.parseInt(all_access_flag) == 1) {
                            admin_usr_name = "";
                        }
                        else {
                            admin_usr_name = usr_name;
                        }

                        adminAvailable = true;
                    }
                }
                else {
                    adminAvailable = false;
                }

                if (adminAvailable) {
                    if (admin_fl_flag.equals("1")) {
                        connected = true;
                        updateAdminInterface();
                    }
                    else {
                        adminLoginLogInsert();
                    }
                }
                else {
                    connected = true;
                    updateAdminInterface();
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAdminInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAdminInterface();
        });

//        StringRequest adminFlagUpdateReq = new StringRequest(Request.Method.POST, flagUpdateUrl, response -> {
//            conn = true;
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                String string_out = jsonObject.getString("string_out");
//                if (string_out.equals("Successfully Created")) {
//                    requestQueue.add(adminDataReq);
//                }
//                else {
//                    System.out.println(string_out);
//                    parsing_message = string_out;
//                    connected = false;
//                    updateAdminInterface();
//                }
//            }
//            catch (JSONException e) {
//                logger.log(Level.WARNING,e.getMessage(),e);
//                connected = false;
//                parsing_message = e.getLocalizedMessage();
//                updateAdminInterface();
//            }
//        }, error -> {
//            logger.log(Level.WARNING,error.getMessage(),error);
//            conn = false;
//            connected = false;
//            parsing_message = error.getLocalizedMessage();
//            updateAdminInterface();
//        }) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("P_USR_ID", admin_usr_id);
//                return headers;
//            }
//        };

//        if (user_switch) {
//            requestQueue.add(adminFlagUpdateReq);
//            System.out.println("USER SWITCHED");
//        }
//        else {
//            requestQueue.add(adminDataReq);
//            System.out.println("USER NOT SWITCHED");
//        }
        adminDataReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(adminDataReq);
    }

    public void adminLoginLogInsert() {
        String loginLogUrl = pre_url_api+"doc_dashboard/loginLog";

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        StringRequest loginLogReq = new StringRequest(Request.Method.POST, loginLogUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                if (string_out.equals("Successfully Created")) {
                    getAdminDashboardData();
                }
                else {
                    parsing_message = string_out;
                    System.out.println(string_out);
                    connected = false;
                    updateAdminInterface();
                }

            } catch (JSONException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateAdminInterface();
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateAdminInterface();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String userName = adminInfoLists.get(0).getUsr_name();
                String userId = adminInfoLists.get(0).getUsr_id();
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

    public void getAdminDashboardData() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);

        String normalDate = simpleDateFormat.format(c);
        String firstDate = "01-"+normalDate;

        Calendar calendar1 = Calendar.getInstance();

        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.add(Calendar.MONTH, 1);
        calendar1.add(Calendar.DATE, -1);
        Date lastDayOfMonth = calendar1.getTime();

        SimpleDateFormat sdff = new SimpleDateFormat("dd",Locale.ENGLISH);
        String llll = sdff.format(lastDayOfMonth);
        String lastDate =  llll+ "-" + normalDate;

        total_appointment_admin = "";
        cancel_appointment_admin = "";
        total_payment_count = "";
        total_payment_amount = "";

        paymentChartLists = new ArrayList<>();
        graphPaymentTotalLay.setVisibility(View.GONE);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineData data1 = new LineData(dataSets);
        paymentChart.setData(data1);
        paymentChart.setMarker(null);
        paymentChart.getData().clearValues();
        paymentChart.notifyDataSetChanged();
        paymentChart.clear();
        paymentChart.invalidate();
        paymentChart.fitScreen();

        appointmentChartLists = new ArrayList<>();
        graphAppointmentTotalLay.setVisibility(View.GONE);
        ArrayList<ILineDataSet> dataSetsApp = new ArrayList<>();
        LineData data = new LineData(dataSetsApp);
        appointmentChart.setData(data);
        appointmentChart.setMarker(null);
        appointmentChart.getData().clearValues();
        appointmentChart.notifyDataSetChanged();
        appointmentChart.clear();
        appointmentChart.invalidate();
        appointmentChart.fitScreen();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        String date_now = dateFormat.format(Calendar.getInstance().getTime());

        String adminPicUrl = pre_url_api+"doc_dashboard/getAdminPic?p_usr_id="+admin_usr_id;
        String adminAppPayUrl = pre_url_api + "doc_dashboard/getAdminAppPayCount?begin_date="+date_now+"&end_date="+date_now+"&user_id="+admin_usr_name;
        String paymentChartMonthUrl = pre_url_api + "doc_dashboard/getPaymentDataMonthly?begin_date="+firstDate+"&end_date="+lastDate+"&user_id="+admin_usr_name;
        String appointChartMonthUrl = pre_url_api + "doc_dashboard/getAppointDataMonthly?begin_date="+firstDate+"&end_date="+lastDate;

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        StringRequest adminPicReq = new StringRequest(Request.Method.GET, adminPicUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String usr_profile_pic = docInfo.optString("usr_profile_pic");

                        if (usr_profile_pic.equals("null") || usr_profile_pic.isEmpty()) {
                            System.out.println("NULL IMAGE");
                            imageFound = false;
                        }
                        else {
                            byte[] decodedString = Base64.decode(usr_profile_pic,Base64.DEFAULT);
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
                    updateAdminInterface();
                }
                else {
                    updateAdminLayout();
                }

            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                if (first_flag == 0) {
                    updateAdminInterface();
                }
                else {
                    updateAdminLayout();
                }
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            if (first_flag == 0) {
                updateAdminInterface();
            }
            else {
                updateAdminLayout();
            }
        });

        StringRequest appointChartMonthReq = new StringRequest(Request.Method.GET, appointChartMonthUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("dates")
                                .equals("null") ? "" : docInfo.getString("dates");
                        String total_app = docInfo.getString("total_app")
                                .equals("null") ? "0" : docInfo.getString("total_app");
                        String cancel_app = docInfo.getString("cancel_app")
                                .equals("null") ? "0" : docInfo.getString("cancel_app");

                        appointmentChartLists.add(new AppointmentChartList(String.valueOf(i+1),dates,total_app,cancel_app));
                    }
                }

                requestQueue.add(adminPicReq);

            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                if (first_flag == 0) {
                    updateAdminInterface();
                }
                else {
                    updateAdminLayout();
                }
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            if (first_flag == 0) {
                updateAdminInterface();
            }
            else {
                updateAdminLayout();
            }
        });

        StringRequest payChartMonthReq = new StringRequest(Request.Method.GET, paymentChartMonthUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("dates")
                                .equals("null") ? "" : docInfo.getString("dates");
                        String amount = docInfo.getString("amount")
                                .equals("null") ? "0" : docInfo.getString("amount");
                        String amount1 = docInfo.getString("amount1")
                                .equals("null") ? "0" : docInfo.getString("amount1");

                        paymentChartLists.add(new PaymentChartList(String.valueOf(i+1),dates,amount,amount1));
                    }
                }

                requestQueue.add(appointChartMonthReq);

            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                if (first_flag == 0) {
                    updateAdminInterface();
                }
                else {
                    updateAdminLayout();
                }
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            if (first_flag == 0) {
                updateAdminInterface();
            }
            else {
                updateAdminLayout();
            }
        });

        StringRequest adminPayAppReq = new StringRequest(Request.Method.GET, adminAppPayUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                cancel_appointment_admin = jsonObject.getString("cancel_app_count")
                        .equals("null") ? "0" : jsonObject.getString("cancel_app_count");
                total_appointment_admin = jsonObject.getString("total_app_count")
                        .equals("null") ? "0" : jsonObject.getString("total_app_count");
                total_payment_amount = jsonObject.getString("total_pay_amount")
                        .equals("null") ? "0" : jsonObject.getString("total_pay_amount");
                total_payment_count = jsonObject.getString("total_pay_count")
                        .equals("null") ? "0" : jsonObject.getString("total_pay_count");

                requestQueue.add(payChartMonthReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                if (first_flag == 0) {
                    updateAdminInterface();
                }
                else {
                    updateAdminLayout();
                }
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            if (first_flag == 0) {
                updateAdminInterface();
            }
            else {
                updateAdminLayout();
            }
        });

        adminPicReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        appointChartMonthReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        payChartMonthReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        adminPayAppReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(adminPayAppReq);
    }

    public void updateAdminInterface() {
        if (conn) {
            if (connected) {
                if (adminAvailable) {
                    if (admin_fl_flag.equals("1")) {
                        fullLayout.setVisibility(View.GONE);
                        progressBarCard.setVisibility(View.GONE);
                        bottomNavigationView.setVisibility(View.GONE);
                        circularProgressIndicator.setVisibility(View.GONE);
                        tabFullLayout.setVisibility(View.GONE);
                        tabFullLayoutAdmin.setVisibility(View.GONE);
                        tabCircularProgressIndicator.setVisibility(View.GONE);
                        tabLayout.setVisibility(View.GONE);
                        tabRefresh.setVisibility(View.GONE);
                        graphicalViewLayout.setVisibility(View.GONE);
                        chartTabCircularProgressIndicator.setVisibility(View.GONE);

                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
                        alertDialogBuilder.setTitle("Forced Log Out!")
                                .setMessage("You are forced to log out from the app for server maintenance. We are sorry for the disturbance. Please Login again to continue the app.")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                    editor1.remove(DOC_USER_CODE);
                                    editor1.remove(DOC_USER_PASSWORD);
                                    editor1.remove(DOC_DATA_API);
                                    editor1.remove(DOC_ALL_ID);
                                    editor1.remove(ADMIN_USR_ID);
                                    editor1.remove(ADMIN_OR_USER_FLAG);
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
                        if (mob_app_access_flag.equals("1")) {
                            fullLayout.setVisibility(View.VISIBLE);
                            progressBarCard.setVisibility(View.GONE);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            circularProgressIndicator.setVisibility(View.GONE);
                            tabFullLayout.setVisibility(View.GONE);
                            tabFullLayoutAdmin.setVisibility(View.VISIBLE);
                            tabCircularProgressIndicator.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.VISIBLE);
                            tabRefresh.setVisibility(View.GONE);
                            graphicalViewLayout.setVisibility(View.VISIBLE);
                            payAppointmentTypeLay.setVisibility(View.GONE);
                            chartTabLayout.setVisibility(View.VISIBLE);
                            chartTabFullLayout.setVisibility(View.VISIBLE);
                            chartTabRefresh.setVisibility(View.GONE);
                            chartTabCircularProgressIndicator.setVisibility(View.GONE);
                            paymentChartCircularProgressIndicator.setVisibility(View.GONE);
                            paymentChartRefresh.setVisibility(View.GONE);
                            appointChartCircularProgressIndicator.setVisibility(View.GONE);
                            appointChartRefresh.setVisibility(View.GONE);

                            unitDoctor.setVisibility(View.GONE);
                            allDoctorAppointment.setVisibility(View.GONE);
                            doctorReports.setVisibility(View.GONE);

                            conn = false;
                            connected = false;
                            adminAvailable = false;

                            chartTabPosition = 0;

                            getGreetingText();

                            String doc_center = "";
                            if (adminInfoLists == null) {
                                restart("Could Not Get Doctor Data. Please Restart the App.");
                            }
                            else {
                                if (adminInfoLists.isEmpty()) {
                                    restart("Could Not Get Doctor Data. Please Restart the App.");
                                }
                                else {
                                    if (Integer.parseInt(adminInfoLists.get(0).getAll_access_flag()) == 1) {
                                        admin_usr_name = "";
                                    }
                                    else {
                                        admin_usr_name = adminInfoLists.get(0).getUsr_name();
                                    }
                                    admin_usr_id = adminInfoLists.get(0).getUsr_id();
                                    String dd = adminInfoLists.get(0).getUsr_fname() + " " + adminInfoLists.get(0).getUsr_lname();

                                    docName.setText(dd);

                                    doc_center = adminInfoLists.get(0).getAdmin_center_name();

                                    if (Integer.parseInt(adminInfoLists.get(0).getAll_access_flag()) == 2) {
                                        bottomNavigationView.getMenu().findItem(R.id.admin_payment_menu).setVisible(adminInfoLists.get(0).getHr_payment_active().equals("1"));
                                        bottomNavigationView.getMenu().findItem(R.id.admin_appointment_schedule_menu).setVisible(adminInfoLists.get(0).getHr_appointment_active().equals("1"));
                                        if (adminInfoLists.get(0).getHr_acc_active_flag().equals("1")) {
                                            bottomNavigationView.getMenu().findItem(R.id.admin_hr_account_menu).setTitle("HR & Reports");
                                        }
                                        else {
                                            bottomNavigationView.getMenu().findItem(R.id.admin_hr_account_menu).setTitle("Reports");
                                        }
                                    }
                                    else if (Integer.parseInt(adminInfoLists.get(0).getAll_access_flag()) == 3) {
                                        bottomNavigationView.getMenu().findItem(R.id.admin_payment_menu).setVisible(adminInfoLists.get(0).getAcc_payment_active().equals("1"));
                                        bottomNavigationView.getMenu().findItem(R.id.admin_appointment_schedule_menu).setVisible(adminInfoLists.get(0).getAcc_appointment_active().equals("1"));
                                        if (adminInfoLists.get(0).getHr_acc_active_flag().equals("1")) {
                                            bottomNavigationView.getMenu().findItem(R.id.admin_hr_account_menu).setTitle("Accounts & Reports");
                                        }
                                        else {
                                            bottomNavigationView.getMenu().findItem(R.id.admin_hr_account_menu).setTitle("Reports");
                                        }
                                    }
                                    else if (Integer.parseInt(adminInfoLists.get(0).getAll_access_flag()) == 1) {
                                        bottomNavigationView.getMenu().findItem(R.id.admin_payment_menu).setVisible(true);
                                        bottomNavigationView.getMenu().findItem(R.id.admin_appointment_schedule_menu).setVisible(true);
                                        if (adminInfoLists.get(0).getHr_acc_active_flag().equals("1")) {
                                            bottomNavigationView.getMenu().findItem(R.id.admin_hr_account_menu).setTitle("Management Portal");
                                        }
                                        else {
                                            bottomNavigationView.getMenu().findItem(R.id.admin_hr_account_menu).setTitle("Reports");
                                        }
                                    }
                                    else {
                                        bottomNavigationView.getMenu().findItem(R.id.admin_payment_menu).setVisible(true);
                                        bottomNavigationView.getMenu().findItem(R.id.admin_appointment_schedule_menu).setVisible(true);
                                        bottomNavigationView.getMenu().findItem(R.id.admin_hr_account_menu).setTitle("Reports");
                                    }

                                    if (Integer.parseInt(adminInfoLists.get(0).getIs_pay_appoint_type_active()) == 1) {
                                        payAppointmentTypeLay.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        payAppointmentTypeLay.setVisibility(View.GONE);
                                    }
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

                            SimpleDateFormat full_date_format_range = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);

                            Date dateRangCc = Calendar.getInstance().getTime();
                            String date_range_text = full_date_format_range.format(dateRangCc);
                            dateRangeTextAdmin.setText(date_range_text);

                            TabLayout.Tab tabAt = tabLayout.getTabAt(0);
                            tabUpdateNeeded = false;
                            tabLayout.selectTab(tabAt);

                            totalAppointmentAdmin.setText(total_appointment_admin);
                            cancelAppointmentAdmin.setText(cancel_appointment_admin);
                            totalPaymentCount.setText(total_payment_count);

                            DecimalFormat formatter = new DecimalFormat("###,##,##,###");

                            int p_amnt = Integer.parseInt(total_payment_amount);
                            String formatted = formatter.format(p_amnt);
                            String p_a = "৳ " + formatted ;
                            totalPaymentAmount.setText(p_a);

                            if (imageFound) {
                                try {
                                    Glide.with(getApplicationContext())
                                            .load(bitmap)
                                            .fitCenter()
                                            .into(docImage);
                                }
                                catch (Exception e) {
                                    restart("App is paused for a long time. Please Start the app again.");
                                }
                            }
                            else {
                                docImage.setImageResource(R.drawable.profile);
                            }

                            TabLayout.Tab chartTabAt = chartTabLayout.getTabAt(0);
                            chartTabUpdateNeeded = false;
                            chartTabLayout.selectTab(chartTabAt);

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);
                            monthSelectionLayPayment.setVisibility(View.VISIBLE);
                            yearSelectionLayPayment.setVisibility(View.GONE);
                            graphPaymentTotalLay.setVisibility(View.VISIBLE);
                            Date dd = Calendar.getInstance().getTime();
                            String mo_name = simpleDateFormat.format(dd);
                            mo_name = mo_name.toUpperCase(Locale.ENGLISH);
                            String ms = "MONTH: " + mo_name;
                            monthSelectionPayment.setText(ms);

                            allPayAppCard.setCardBackgroundColor(getColor(R.color.green_sea));
                            allPayAppText.setTextColor(getColor(R.color.white));

                            offlinePayAppCard.setCardBackgroundColor(getColor(R.color.primaryColor_alpha));
                            offPayAppText.setTextColor(getColor(R.color.green_sea));

                            onlinePayAppCard.setCardBackgroundColor(getColor(R.color.primaryColor_alpha));
                            onPayAppText.setTextColor(getColor(R.color.green_sea));

                            // chart
                            MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
                            mv.setChartView(paymentChart);
                            paymentChart.setMarker(mv);

                            ArrayList<Entry> amountValue = new ArrayList<>();
                            ArrayList<Entry> returnValue = new ArrayList<>();
                            ArrayList<String> monthName = new ArrayList<>();

                            double payment_rcv = 0.0;
                            double payment_rtn = 0.0;

                            for (int i = 0; i < paymentChartLists.size(); i++) {
                                amountValue.add(new Entry(i,Float.parseFloat(paymentChartLists.get(i).getPaymentAmount()), paymentChartLists.get(i).getId()));
                                returnValue.add(new Entry(i,Float.parseFloat(paymentChartLists.get(i).getPatymentReturn()),paymentChartLists.get(i).getId()));
                                monthName.add(paymentChartLists.get(i).getDateMonth());
                                if (!paymentChartLists.get(i).getPaymentAmount().isEmpty()) {
                                    payment_rcv = payment_rcv + Double.parseDouble(paymentChartLists.get(i).getPaymentAmount());
                                }
                                if (!paymentChartLists.get(i).getPatymentReturn().isEmpty()) {
                                    payment_rtn = payment_rtn + Double.parseDouble(paymentChartLists.get(i).getPatymentReturn());
                                }
                            }
                            String prcv = formatter.format(payment_rcv);
                            String prtn = formatter.format(payment_rtn);
                            prcv = "৳ " + prcv ;
                            prtn = "৳ " + prtn ;
                            totalPaymentRcvGraph.setText(prcv);
                            totalPaymentRtnGraph.setText(prtn);

                            paymentChart.animateXY(1000,1000);

                            LineDataSet lineDataSet = new LineDataSet(amountValue,"Payment Receive");
                            lineDataSet.setValueFormatter(new LargeValueFormatter());
                            lineDataSet.setCircleColor(getColor(R.color.light_green));
                            lineDataSet.setCircleRadius(3f);
                            lineDataSet.setLineWidth(2f);
                            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            lineDataSet.setColor(getColor(R.color.green_sea));
                            lineDataSet.setDrawFilled(false);
                            lineDataSet.setValueTextSize(9f);

                            lineDataSet.setDrawCircleHole(true);
                            lineDataSet.setValueTextColor(R.color.default_text_color);
                            lineDataSet.setValueTextSize(8f);

                            LineDataSet lineDataSet1 = new LineDataSet(returnValue,"Payment Return");
                            lineDataSet1.setValueFormatter(new LargeValueFormatter());
                            lineDataSet1.setCircleColor(getColor(R.color.red_dark));
                            lineDataSet1.setCircleRadius(3f);
                            lineDataSet1.setLineWidth(2f);
                            lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            lineDataSet1.setColor(getColor(R.color.blue_end));
                            lineDataSet1.setDrawFilled(false);
                            lineDataSet1.setValueTextSize(9f);
                            lineDataSet1.setDrawCircleHole(true);
                            lineDataSet1.setValueTextColor(R.color.default_text_color);
                            lineDataSet1.setValueTextSize(8f);

                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                            dataSets.add(lineDataSet);
                            dataSets.add(lineDataSet1);

                            LineData data1 = new LineData(dataSets);

                            paymentChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(monthName));
                            paymentChart.setData(data1);
                            paymentChart.getData().setHighlightEnabled(true);
                            paymentChart.invalidate();

                            monthSelectionLayAppoint.setVisibility(View.VISIBLE);
                            yearSelectionLayAppoint.setVisibility(View.GONE);
                            graphAppointmentTotalLay.setVisibility(View.VISIBLE);
                            monthSelectionAppoint.setText(ms);

                            // Apointment Chart
                            AppointMarkerView view = new AppointMarkerView(getApplicationContext(), R.layout.custom_marker_view);
                            view.setChartView(appointmentChart);
                            appointmentChart.setMarker(view);

                            ArrayList<Entry> totalAppValue = new ArrayList<>();
                            ArrayList<Entry> cancelAppValue = new ArrayList<>();
                            ArrayList<String> dateMonthName = new ArrayList<>();

                            int totapp = 0;
                            int totcanapp = 0;
                            for (int i = 0; i < appointmentChartLists.size(); i++) {
                                totalAppValue.add(new Entry(i,Float.parseFloat(appointmentChartLists.get(i).getTotal_app()), appointmentChartLists.get(i).getId()));
                                cancelAppValue.add(new Entry(i,Float.parseFloat(appointmentChartLists.get(i).getCancel_app()),appointmentChartLists.get(i).getId()));
                                dateMonthName.add(appointmentChartLists.get(i).getDateMonth());
                                if (!appointmentChartLists.get(i).getTotal_app().isEmpty()) {
                                    totapp = totapp + Integer.parseInt(appointmentChartLists.get(i).getTotal_app());
                                }
                                if (!appointmentChartLists.get(i).getCancel_app().isEmpty()) {
                                    totcanapp = totcanapp + Integer.parseInt(appointmentChartLists.get(i).getCancel_app());
                                }
                            }
                            totalAppointmentGraph.setText(String.valueOf(totapp));
                            totalCancelAppointmentGraph.setText(String.valueOf(totcanapp));

                            appointmentChart.animateXY(1000,1000);

                            LineDataSet lineDataSet2 = new LineDataSet(totalAppValue,"Total Appointment");
                            lineDataSet2.setValueFormatter(new LargeValueFormatter());
                            lineDataSet2.setCircleColor(getColor(R.color.light_green));
                            lineDataSet2.setCircleRadius(3f);
                            lineDataSet2.setLineWidth(2f);
                            lineDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            lineDataSet2.setColor(getColor(R.color.green_sea));
                            lineDataSet2.setDrawFilled(false);
                            lineDataSet2.setValueTextSize(9f);

                            lineDataSet2.setDrawCircleHole(true);
                            lineDataSet2.setValueTextColor(R.color.default_text_color);
                            lineDataSet2.setValueTextSize(8f);

                            LineDataSet lineDataSet3 = new LineDataSet(cancelAppValue,"Cancel Appointment");
                            lineDataSet3.setValueFormatter(new LargeValueFormatter());
                            lineDataSet3.setCircleColor(getColor(R.color.back_color));
                            lineDataSet3.setCircleRadius(3f);
                            lineDataSet3.setLineWidth(2f);
                            lineDataSet3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            lineDataSet3.setColor(getColor(R.color.disabled));
                            lineDataSet3.setDrawFilled(false);
                            lineDataSet3.setValueTextSize(9f);
                            lineDataSet3.setDrawCircleHole(true);
                            lineDataSet3.setValueTextColor(R.color.default_text_color);
                            lineDataSet3.setValueTextSize(8f);

                            ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                            dataSets1.add(lineDataSet2);
                            dataSets1.add(lineDataSet3);

                            LineData data2 = new LineData(dataSets1);

                            appointmentChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateMonthName));
                            appointmentChart.setData(data2);
                            appointmentChart.getData().setHighlightEnabled(true);
                            appointmentChart.invalidate();

                            first_flag = 1;
                        }
                        else {
                            fullLayout.setVisibility(View.GONE);
                            progressBarCard.setVisibility(View.GONE);
                            bottomNavigationView.setVisibility(View.GONE);
                            circularProgressIndicator.setVisibility(View.GONE);
                            tabFullLayout.setVisibility(View.GONE);
                            tabFullLayoutAdmin.setVisibility(View.GONE);
                            tabCircularProgressIndicator.setVisibility(View.GONE);
                            tabLayout.setVisibility(View.GONE);
                            tabRefresh.setVisibility(View.GONE);
                            graphicalViewLayout.setVisibility(View.GONE);
                            chartTabCircularProgressIndicator.setVisibility(View.GONE);


                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
                            alertDialogBuilder.setTitle("Access Denied!")
                                    .setMessage("You have not permission to access this app. Please contact with administrator to access this app.")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                        editor1.remove(DOC_USER_CODE);
                                        editor1.remove(DOC_USER_PASSWORD);
                                        editor1.remove(DOC_DATA_API);
                                        editor1.remove(DOC_ALL_ID);
                                        editor1.remove(ADMIN_USR_ID);
                                        editor1.remove(ADMIN_OR_USER_FLAG);
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
                }
                else {
                    fullLayout.setVisibility(View.GONE);
                    progressBarCard.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.GONE);
                    tabFullLayout.setVisibility(View.GONE);
                    tabFullLayoutAdmin.setVisibility(View.GONE);
                    tabCircularProgressIndicator.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    tabRefresh.setVisibility(View.GONE);
                    graphicalViewLayout.setVisibility(View.GONE);
                    chartTabCircularProgressIndicator.setVisibility(View.GONE);

                    String e_msg = "No Admin Information found for this Id. Please contact with Administrator.";
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocDashboard.this);
                    alertDialogBuilder.setTitle("No Information Found!")
                            .setMessage(e_msg)
                            .setPositiveButton("OK", (dialog, which) -> {

                                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                editor1.remove(DOC_USER_CODE);
                                editor1.remove(DOC_USER_PASSWORD);
                                editor1.remove(DOC_DATA_API);
                                editor1.remove(DOC_ALL_ID);
                                editor1.remove(ADMIN_USR_ID);
                                editor1.remove(ADMIN_OR_USER_FLAG);
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
                loading = false;
            }
            else {
                adminAlertMessage();
            }
        }
        else {
            adminAlertMessage();
        }

    }

    public void adminAlertMessage() {
        fullLayout.setVisibility(View.GONE);
        progressBarCard.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        tabFullLayout.setVisibility(View.GONE);
        tabFullLayoutAdmin.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);
        graphicalViewLayout.setVisibility(View.GONE);
        chartTabCircularProgressIndicator.setVisibility(View.GONE);

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
                    loading = false;
                    getAdminData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    loading = false;
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

    public void updateAdminLayout() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                progressBarCard.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                tabFullLayout.setVisibility(View.GONE);
                tabFullLayoutAdmin.setVisibility(View.VISIBLE);
                tabCircularProgressIndicator.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                tabRefresh.setVisibility(View.GONE);
                graphicalViewLayout.setVisibility(View.VISIBLE);
                payAppointmentTypeLay.setVisibility(View.GONE);
                chartTabLayout.setVisibility(View.VISIBLE);
                chartTabFullLayout.setVisibility(View.VISIBLE);
                chartTabRefresh.setVisibility(View.GONE);
                chartTabCircularProgressIndicator.setVisibility(View.GONE);
                paymentChartCircularProgressIndicator.setVisibility(View.GONE);
                paymentChartRefresh.setVisibility(View.GONE);
                appointChartCircularProgressIndicator.setVisibility(View.GONE);
                appointChartRefresh.setVisibility(View.GONE);

                unitDoctor.setVisibility(View.GONE);
                allDoctorAppointment.setVisibility(View.GONE);
                doctorReports.setVisibility(View.GONE);

                conn = false;
                connected = false;

                SimpleDateFormat full_date_format_range = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);

                Date dateRangCc = Calendar.getInstance().getTime();
                String date_range_text = full_date_format_range.format(dateRangCc);
                dateRangeTextAdmin.setText(date_range_text);

                getGreetingText();

                TabLayout.Tab tabAt = tabLayout.getTabAt(0);
                tabUpdateNeeded = false;
                tabLayout.selectTab(tabAt);

                totalAppointmentAdmin.setText(total_appointment_admin);
                cancelAppointmentAdmin.setText(cancel_appointment_admin);
                totalPaymentCount.setText(total_payment_count);

                DecimalFormat formatter = new DecimalFormat("###,##,##,###");

                int p_amnt = Integer.parseInt(total_payment_amount);
                String formatted = formatter.format(p_amnt);
                String p_a = "৳ " + formatted ;
                totalPaymentAmount.setText(p_a);

                if (imageFound) {
                    try {
                        Glide.with(getApplicationContext())
                                .load(bitmap)
                                .fitCenter()
                                .into(docImage);
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
                else {
                    docImage.setImageResource(R.drawable.profile);
                }

                TabLayout.Tab chartTabAt = chartTabLayout.getTabAt(0);
                chartTabUpdateNeeded = false;
                chartTabLayout.selectTab(chartTabAt);

                chartTabPosition = 0;

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);
                monthSelectionLayPayment.setVisibility(View.VISIBLE);
                yearSelectionLayPayment.setVisibility(View.GONE);
                graphPaymentTotalLay.setVisibility(View.VISIBLE);
                Date dd = Calendar.getInstance().getTime();
                String mo_name = simpleDateFormat.format(dd);
                mo_name = mo_name.toUpperCase(Locale.ENGLISH);
                String ms = "MONTH: " + mo_name;
                monthSelectionPayment.setText(ms);

                if (adminInfoLists == null) {
                    restart("Could Not Get Doctor Data. Please Restart the App.");
                }
                else {
                    if (adminInfoLists.isEmpty()) {
                        restart("Could Not Get Doctor Data. Please Restart the App.");
                    }
                    else {
                        if (Integer.parseInt(adminInfoLists.get(0).getIs_pay_appoint_type_active()) == 1) {
                            payAppointmentTypeLay.setVisibility(View.VISIBLE);
                        }
                        else {
                            payAppointmentTypeLay.setVisibility(View.GONE);
                        }
                    }
                }

                allPayAppCard.setCardBackgroundColor(getColor(R.color.green_sea));
                allPayAppText.setTextColor(getColor(R.color.white));

                offlinePayAppCard.setCardBackgroundColor(getColor(R.color.primaryColor_alpha));
                offPayAppText.setTextColor(getColor(R.color.green_sea));

                onlinePayAppCard.setCardBackgroundColor(getColor(R.color.primaryColor_alpha));
                onPayAppText.setTextColor(getColor(R.color.green_sea));

                // chart
                MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
                mv.setChartView(paymentChart);
                paymentChart.setMarker(mv);

                ArrayList<Entry> amountValue = new ArrayList<>();
                ArrayList<Entry> returnValue = new ArrayList<>();
                ArrayList<String> monthName = new ArrayList<>();

                double payment_rcv = 0.0;
                double payment_rtn = 0.0;

                for (int i = 0; i < paymentChartLists.size(); i++) {
                    amountValue.add(new Entry(i,Float.parseFloat(paymentChartLists.get(i).getPaymentAmount()), paymentChartLists.get(i).getId()));
                    returnValue.add(new Entry(i,Float.parseFloat(paymentChartLists.get(i).getPatymentReturn()),paymentChartLists.get(i).getId()));
                    monthName.add(paymentChartLists.get(i).getDateMonth());
                    if (!paymentChartLists.get(i).getPaymentAmount().isEmpty()) {
                        payment_rcv = payment_rcv + Double.parseDouble(paymentChartLists.get(i).getPaymentAmount());
                    }
                    if (!paymentChartLists.get(i).getPatymentReturn().isEmpty()) {
                        payment_rtn = payment_rtn + Double.parseDouble(paymentChartLists.get(i).getPatymentReturn());
                    }
                }
                String prcv = formatter.format(payment_rcv);
                String prtn = formatter.format(payment_rtn);
                prcv = "৳ " + prcv ;
                prtn = "৳ " + prtn ;
                totalPaymentRcvGraph.setText(prcv);
                totalPaymentRtnGraph.setText(prtn);

                paymentChart.animateXY(1000,1000);

                LineDataSet lineDataSet = new LineDataSet(amountValue,"Payment Receive");
                lineDataSet.setValueFormatter(new LargeValueFormatter());
                lineDataSet.setCircleColor(getColor(R.color.light_green));
                lineDataSet.setCircleRadius(3f);
                lineDataSet.setLineWidth(2f);
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet.setColor(getColor(R.color.green_sea));
                lineDataSet.setDrawFilled(false);
                lineDataSet.setValueTextSize(9f);

                lineDataSet.setDrawCircleHole(true);
                lineDataSet.setValueTextColor(R.color.default_text_color);
                lineDataSet.setValueTextSize(8f);

                LineDataSet lineDataSet1 = new LineDataSet(returnValue,"Payment Return");
                lineDataSet1.setValueFormatter(new LargeValueFormatter());
                lineDataSet1.setCircleColor(getColor(R.color.red_dark));
                lineDataSet1.setCircleRadius(3f);
                lineDataSet1.setLineWidth(2f);
                lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet1.setColor(getColor(R.color.blue_end));
                lineDataSet1.setDrawFilled(false);
                lineDataSet1.setValueTextSize(9f);
                lineDataSet1.setDrawCircleHole(true);
                lineDataSet1.setValueTextColor(R.color.default_text_color);
                lineDataSet1.setValueTextSize(8f);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet);
                dataSets.add(lineDataSet1);

                LineData data1 = new LineData(dataSets);

                paymentChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(monthName));

                if (chartTabPosition == 0) {
                    paymentChart.getXAxis().setLabelRotationAngle(45);
                }
                else {
                    paymentChart.getXAxis().setLabelRotationAngle(0);
                }
                paymentChart.setData(data1);
                paymentChart.getData().setHighlightEnabled(true);
                paymentChart.invalidate();


                monthSelectionLayAppoint.setVisibility(View.VISIBLE);
                yearSelectionLayAppoint.setVisibility(View.GONE);
                graphAppointmentTotalLay.setVisibility(View.VISIBLE);
                monthSelectionAppoint.setText(ms);

                // Apointment Chart
                AppointMarkerView view = new AppointMarkerView(getApplicationContext(), R.layout.custom_marker_view);
                view.setChartView(appointmentChart);
                appointmentChart.setMarker(view);

                ArrayList<Entry> totalAppValue = new ArrayList<>();
                ArrayList<Entry> cancelAppValue = new ArrayList<>();
                ArrayList<String> dateMonthName = new ArrayList<>();

                int totapp = 0;
                int totcanapp = 0;
                for (int i = 0; i < appointmentChartLists.size(); i++) {
                    totalAppValue.add(new Entry(i,Float.parseFloat(appointmentChartLists.get(i).getTotal_app()), appointmentChartLists.get(i).getId()));
                    cancelAppValue.add(new Entry(i,Float.parseFloat(appointmentChartLists.get(i).getCancel_app()),appointmentChartLists.get(i).getId()));
                    dateMonthName.add(appointmentChartLists.get(i).getDateMonth());
                    if (!appointmentChartLists.get(i).getTotal_app().isEmpty()) {
                        totapp = totapp + Integer.parseInt(appointmentChartLists.get(i).getTotal_app());
                    }
                    if (!appointmentChartLists.get(i).getCancel_app().isEmpty()) {
                        totcanapp = totcanapp + Integer.parseInt(appointmentChartLists.get(i).getCancel_app());
                    }
                }
                totalAppointmentGraph.setText(String.valueOf(totapp));
                totalCancelAppointmentGraph.setText(String.valueOf(totcanapp));

                appointmentChart.animateXY(1000,1000);

                LineDataSet lineDataSet2 = new LineDataSet(totalAppValue,"Total Appointment");
                lineDataSet2.setValueFormatter(new LargeValueFormatter());
                lineDataSet2.setCircleColor(getColor(R.color.light_green));
                lineDataSet2.setCircleRadius(3f);
                lineDataSet2.setLineWidth(2f);
                lineDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet2.setColor(getColor(R.color.green_sea));
                lineDataSet2.setDrawFilled(false);
                lineDataSet2.setValueTextSize(9f);

                lineDataSet2.setDrawCircleHole(true);
                lineDataSet2.setValueTextColor(R.color.default_text_color);
                lineDataSet2.setValueTextSize(8f);

                LineDataSet lineDataSet3 = new LineDataSet(cancelAppValue,"Cancel Appointment");
                lineDataSet3.setValueFormatter(new LargeValueFormatter());
                lineDataSet3.setCircleColor(getColor(R.color.back_color));
                lineDataSet3.setCircleRadius(3f);
                lineDataSet3.setLineWidth(2f);
                lineDataSet3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet3.setColor(getColor(R.color.disabled));
                lineDataSet3.setDrawFilled(false);
                lineDataSet3.setValueTextSize(9f);
                lineDataSet3.setDrawCircleHole(true);
                lineDataSet3.setValueTextColor(R.color.default_text_color);
                lineDataSet3.setValueTextSize(8f);

                ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                dataSets1.add(lineDataSet2);
                dataSets1.add(lineDataSet3);

                LineData data2 = new LineData(dataSets1);

                appointmentChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateMonthName));

                if (chartTabPosition == 0) {
                    appointmentChart.getXAxis().setLabelRotationAngle(45);
                }
                else {
                    appointmentChart.getXAxis().setLabelRotationAngle(0);
                }
                appointmentChart.setData(data2);
                appointmentChart.getData().setHighlightEnabled(true);
                appointmentChart.invalidate();

                askUserForReview();
                first_flag = 1;
                loading = false;
            }
            else {
                adminAlertMessage2();
            }
        }
        else {
            adminAlertMessage2();
        }
    }

    public void adminAlertMessage2() {
        fullLayout.setVisibility(View.GONE);
        progressBarCard.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        tabFullLayout.setVisibility(View.GONE);
        tabFullLayoutAdmin.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);
        graphicalViewLayout.setVisibility(View.GONE);
        chartTabCircularProgressIndicator.setVisibility(View.GONE);

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
                    loading = false;
                    fullLayout.setVisibility(View.GONE);
                    progressBarCard.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    tabFullLayout.setVisibility(View.GONE);
                    tabFullLayoutAdmin.setVisibility(View.GONE);
                    tabCircularProgressIndicator.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.GONE);
                    tabRefresh.setVisibility(View.GONE);
                    graphicalViewLayout.setVisibility(View.GONE);
                    chartTabCircularProgressIndicator.setVisibility(View.GONE);
                    pay_app_type = 0;
                    conn = false;
                    connected = false;
                    loading = true;
                    getAdminDashboardData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    loading = false;
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

    public void getAdminAppPayCount() {
        tabFullLayout.setVisibility(View.GONE);
        tabFullLayoutAdmin.setVisibility(View.GONE);
        tabCircularProgressIndicator.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        tabRefresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        total_appointment_admin = "";
        cancel_appointment_admin = "";
        total_payment_count = "";
        total_payment_amount = "";

        String adminAppPayUrl = pre_url_api + "doc_dashboard/getAdminAppPayCount?begin_date="+tabFirstDate+"&end_date="+tabEndDate+"&user_id="+admin_usr_name;

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        StringRequest adminPayAppReq = new StringRequest(Request.Method.GET, adminAppPayUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                cancel_appointment_admin = jsonObject.getString("cancel_app_count")
                        .equals("null") ? "0" : jsonObject.getString("cancel_app_count");
                total_appointment_admin = jsonObject.getString("total_app_count")
                        .equals("null") ? "0" : jsonObject.getString("total_app_count");
                total_payment_amount = jsonObject.getString("total_pay_amount")
                        .equals("null") ? "0" : jsonObject.getString("total_pay_amount");
                total_payment_count = jsonObject.getString("total_pay_count")
                        .equals("null") ? "0" : jsonObject.getString("total_pay_count");

                connected = true;
                updateTabLayoutAdmin();

            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateTabLayoutAdmin();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateTabLayoutAdmin();
        });

        adminPayAppReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(adminPayAppReq);
    }

    private void updateTabLayoutAdmin() {
        if (conn) {
            if (connected) {
                tabFullLayout.setVisibility(View.GONE);
                tabFullLayoutAdmin.setVisibility(View.VISIBLE);
                tabCircularProgressIndicator.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                tabRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;

                totalAppointmentAdmin.setText(total_appointment_admin);
                cancelAppointmentAdmin.setText(cancel_appointment_admin);
                totalPaymentCount.setText(total_payment_count);

                DecimalFormat formatter = new DecimalFormat("###,##,##,###");

                int p_amnt = Integer.parseInt(total_payment_amount);
                String formatted = formatter.format(p_amnt);
                String p_a = "৳ " + formatted ;
                totalPaymentAmount.setText(p_a);

                loading = false;

            }
            else {
                alertMessage3();
            }
        }
        else {
            alertMessage3();
        }
    }

    @Override
    public void onBitmapReceived(Bitmap bitmap) {
        Uri uri = getImageUri(this,bitmap);
        startCrop(uri);
    }

    @Override
    public void onPictureChoose(int type) {
        if (type == 1) {
            Intent intent = new Intent(this, CameraPreview.class);
            CameraPreview.setBitmapCallback(this);
            startActivity(intent);
        }
        else if (type == 2) {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        }
        else {
            cameraResumeLoad = true;
        }
    }

    private void startCrop(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

        UCrop.of(sourceUri, destinationUri)
                //.withAspectRatio(1, 1)  // Optional: Set aspect ratio
                .withMaxResultSize(1080, 1080) // Optional: Set max resolution
                .start(this);
    }

    public static class MyAxisValueFormatter extends ValueFormatter {
        private final ArrayList<String> mvalues;
        public MyAxisValueFormatter(ArrayList<String> mvalues) {
            this.mvalues = mvalues;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {

            if (mvalues != null) {
                if (value < 0 || value >= mvalues.size()) {
                    return "";
                } else {
                    return (mvalues.get((int) value));
                }
            } else {
                return "";
            }
        }
    }

    public void getChartData() {
        chartTabFullLayout.setVisibility(View.GONE);
        payAppointmentTypeLay.setVisibility(View.GONE);
        chartTabCircularProgressIndicator.setVisibility(View.VISIBLE);
        chartTabLayout.setVisibility(View.GONE);
        chartTabRefresh.setVisibility(View.GONE);
        paymentChartCircularProgressIndicator.setVisibility(View.GONE);
        paymentChartRefresh.setVisibility(View.GONE);
        appointChartCircularProgressIndicator.setVisibility(View.GONE);
        appointChartRefresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        paymentChartLists = new ArrayList<>();
        graphPaymentTotalLay.setVisibility(View.GONE);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineData data1 = new LineData(dataSets);
        paymentChart.setData(data1);
        paymentChart.setMarker(null);
        paymentChart.getData().clearValues();
        paymentChart.notifyDataSetChanged();
        paymentChart.clear();
        paymentChart.invalidate();
        paymentChart.fitScreen();

        appointmentChartLists = new ArrayList<>();
        graphAppointmentTotalLay.setVisibility(View.GONE);
        ArrayList<ILineDataSet> dataSetsApp = new ArrayList<>();
        LineData data = new LineData(dataSetsApp);
        appointmentChart.setData(data);
        appointmentChart.setMarker(null);
        appointmentChart.getData().clearValues();
        appointmentChart.notifyDataSetChanged();
        appointmentChart.clear();
        appointmentChart.invalidate();
        appointmentChart.fitScreen();

        String paymentChartUrl;
        String appointChartUrl;

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        if (chartTabPosition == 0) {
            if (pay_app_type == 0) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataMonthly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataMonthly?begin_date="+appointChartFirstDate+"&end_date="+appointChartLastDate;
            }
            else if (pay_app_type == 1) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataOfflineMonthly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataOfflineMonthly?begin_date="+appointChartFirstDate+"&end_date="+appointChartLastDate;
            }
            else if (pay_app_type == 2){
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataOnlineMonthly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataOnlineMonthly?begin_date="+appointChartFirstDate+"&end_date="+appointChartLastDate;
            }
            else {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataMonthly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataMonthly?begin_date="+appointChartFirstDate+"&end_date="+appointChartLastDate;
            }

            StringRequest appChartReq = new StringRequest(Request.Method.GET, appointChartUrl, response -> {
                conn = true;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String items = jsonObject.getString("items");
                    String count = jsonObject.getString("count");
                    if (!count.equals("0")) {
                        JSONArray array = new JSONArray(items);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);

                            String dates = docInfo.getString("dates")
                                    .equals("null") ? "" : docInfo.getString("dates");
                            String total_app = docInfo.getString("total_app")
                                    .equals("null") ? "0" : docInfo.getString("total_app");
                            String cancel_app = docInfo.getString("cancel_app")
                                    .equals("null") ? "0" : docInfo.getString("cancel_app");

                            appointmentChartLists.add(new AppointmentChartList(String.valueOf(i+1),dates,total_app,cancel_app));
                        }
                    }

                    connected = true;
                    updateChartTabLayout();

                }
                catch (JSONException e) {
                    connected = false;
                    logger.log(Level.WARNING,e.getMessage(),e);
                    parsing_message = e.getLocalizedMessage();
                    updateChartTabLayout();
                }
            }, error -> {
                conn = false;
                connected = false;
                logger.log(Level.WARNING,error.getMessage(),error);
                parsing_message = error.getLocalizedMessage();
                updateChartTabLayout();
            });

            StringRequest payChartReq = new StringRequest(Request.Method.GET, paymentChartUrl, response -> {
                conn = true;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String items = jsonObject.getString("items");
                    String count = jsonObject.getString("count");
                    if (!count.equals("0")) {
                        JSONArray array = new JSONArray(items);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);

                            String dates = docInfo.getString("dates")
                                    .equals("null") ? "" : docInfo.getString("dates");
                            String amount = docInfo.getString("amount")
                                    .equals("null") ? "0" : docInfo.getString("amount");
                            String amount1 = docInfo.getString("amount1")
                                    .equals("null") ? "0" : docInfo.getString("amount1");

                            paymentChartLists.add(new PaymentChartList(String.valueOf(i+1),dates,amount,amount1));
                        }
                    }

                    requestQueue.add(appChartReq);

                }
                catch (JSONException e) {
                    connected = false;
                    logger.log(Level.WARNING,e.getMessage(),e);
                    parsing_message = e.getLocalizedMessage();
                    updateChartTabLayout();
                }
            }, error -> {
                conn = false;
                connected = false;
                logger.log(Level.WARNING,error.getMessage(),error);
                parsing_message = error.getLocalizedMessage();
                updateChartTabLayout();
            });

            appChartReq.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            payChartReq.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(payChartReq);
        }
        else {
            if (pay_app_type == 0) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataYearly?begin_date=" + paymentChartFirstDate + "&end_date=" + paymentChartLastDate + "&user_id=" + admin_usr_name;
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataYearly?begin_date=" + appointChartFirstDate + "&end_date=" + appointChartLastDate;
            }
            else if (pay_app_type == 1) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataOfflineYearly?begin_date=" + paymentChartFirstDate + "&end_date=" + paymentChartLastDate + "&user_id=" + admin_usr_name;
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataOfflineYearly?begin_date=" + appointChartFirstDate + "&end_date=" + appointChartLastDate;
            }
            else if (pay_app_type == 2) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataOnlineYearly?begin_date=" + paymentChartFirstDate + "&end_date=" + paymentChartLastDate + "&user_id=" + admin_usr_name;
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataOnlineYearly?begin_date=" + appointChartFirstDate + "&end_date=" + appointChartLastDate;
            }
            else {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataYearly?begin_date=" + paymentChartFirstDate + "&end_date=" + paymentChartLastDate + "&user_id=" + admin_usr_name;
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataYearly?begin_date=" + appointChartFirstDate + "&end_date=" + appointChartLastDate;
            }

            StringRequest appChartReq = new StringRequest(Request.Method.GET, appointChartUrl, response -> {
                conn = true;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String items = jsonObject.getString("items");
                    String count = jsonObject.getString("count");
                    if (!count.equals("0")) {
                        JSONArray array = new JSONArray(items);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);

                            String pmonths = docInfo.getString("pmonths")
                                    .equals("null") ? "" : docInfo.getString("pmonths");
                            String total_app = docInfo.getString("total_app")
                                    .equals("null") ? "0" : docInfo.getString("total_app");
                            String cancel_app = docInfo.getString("cancel_app")
                                    .equals("null") ? "0" : docInfo.getString("cancel_app");

                            appointmentChartLists.add(new AppointmentChartList(String.valueOf(i+1),pmonths,total_app,cancel_app));
                        }
                    }

                    connected = true;
                    updateChartTabLayout();

                }
                catch (JSONException e) {
                    connected = false;
                    logger.log(Level.WARNING,e.getMessage(),e);
                    parsing_message = e.getLocalizedMessage();
                    updateChartTabLayout();
                }
            }, error -> {
                conn = false;
                connected = false;
                logger.log(Level.WARNING,error.getMessage(),error);
                parsing_message = error.getLocalizedMessage();
                updateChartTabLayout();
            });

            StringRequest payChartReq = new StringRequest(Request.Method.GET, paymentChartUrl, response -> {
                conn = true;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String items = jsonObject.getString("items");
                    String count = jsonObject.getString("count");
                    if (!count.equals("0")) {
                        JSONArray array = new JSONArray(items);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);

                            String pmonths = docInfo.getString("pmonths")
                                    .equals("null") ? "" : docInfo.getString("pmonths");
                            String amount = docInfo.getString("amount")
                                    .equals("null") ? "0" : docInfo.getString("amount");
                            String amount1 = docInfo.getString("amount1")
                                    .equals("null") ? "0" : docInfo.getString("amount1");

                            paymentChartLists.add(new PaymentChartList(String.valueOf(i+1),pmonths,amount,amount1));
                        }
                    }

                    requestQueue.add(appChartReq);

                }
                catch (JSONException e) {
                    connected = false;
                    logger.log(Level.WARNING,e.getMessage(),e);
                    parsing_message = e.getLocalizedMessage();
                    updateChartTabLayout();
                }
            }, error -> {
                conn = false;
                connected = false;
                logger.log(Level.WARNING,error.getMessage(),error);
                parsing_message = error.getLocalizedMessage();
                updateChartTabLayout();
            });

            appChartReq.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            payChartReq.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(payChartReq);
        }
    }

    private void updateChartTabLayout() {
        if (conn) {
            if (connected) {
                chartTabFullLayout.setVisibility(View.VISIBLE);
                payAppointmentTypeLay.setVisibility(View.GONE);
                chartTabCircularProgressIndicator.setVisibility(View.GONE);
                chartTabLayout.setVisibility(View.VISIBLE);
                chartTabRefresh.setVisibility(View.GONE);
                paymentChartCircularProgressIndicator.setVisibility(View.GONE);
                paymentChartRefresh.setVisibility(View.GONE);
                appointChartCircularProgressIndicator.setVisibility(View.GONE);
                appointChartRefresh.setVisibility(View.GONE);
                graphPaymentTotalLay.setVisibility(View.VISIBLE);
                graphAppointmentTotalLay.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                if (adminInfoLists == null) {
                    restart("Could Not Get Doctor Data. Please Restart the App.");
                }
                else {
                    if (adminInfoLists.isEmpty()) {
                        restart("Could Not Get Doctor Data. Please Restart the App.");
                    }
                    else {
                        if (Integer.parseInt(adminInfoLists.get(0).getIs_pay_appoint_type_active()) == 1) {
                            payAppointmentTypeLay.setVisibility(View.VISIBLE);
                        }
                        else {
                            payAppointmentTypeLay.setVisibility(View.GONE);
                        }
                    }
                }

                MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
                mv.setChartView(paymentChart);
                paymentChart.setMarker(mv);

                ArrayList<Entry> amountValue = new ArrayList<>();
                ArrayList<Entry> returnValue = new ArrayList<>();
                ArrayList<String> monthName = new ArrayList<>();

                DecimalFormat formatter = new DecimalFormat("###,##,##,###");
                double payment_rcv = 0.0;
                double payment_rtn = 0.0;

                for (int i = 0; i < paymentChartLists.size(); i++) {
                    amountValue.add(new Entry(i,Float.parseFloat(paymentChartLists.get(i).getPaymentAmount()), paymentChartLists.get(i).getId()));
                    returnValue.add(new Entry(i,Float.parseFloat(paymentChartLists.get(i).getPatymentReturn()),paymentChartLists.get(i).getId()));
                    monthName.add(paymentChartLists.get(i).getDateMonth());
                    if (!paymentChartLists.get(i).getPaymentAmount().isEmpty()) {
                        payment_rcv = payment_rcv + Double.parseDouble(paymentChartLists.get(i).getPaymentAmount());
                    }
                    if (!paymentChartLists.get(i).getPatymentReturn().isEmpty()) {
                        payment_rtn = payment_rtn + Double.parseDouble(paymentChartLists.get(i).getPatymentReturn());
                    }
                }
                String prcv = formatter.format(payment_rcv);
                String prtn = formatter.format(payment_rtn);
                prcv = "৳ " + prcv ;
                prtn = "৳ " + prtn ;
                totalPaymentRcvGraph.setText(prcv);
                totalPaymentRtnGraph.setText(prtn);

                paymentChart.animateXY(1000,1000);

                LineDataSet lineDataSet = new LineDataSet(amountValue,"Payment Receive");
                lineDataSet.setValueFormatter(new LargeValueFormatter());
                lineDataSet.setCircleColor(getColor(R.color.light_green));
                lineDataSet.setCircleRadius(3f);
                lineDataSet.setLineWidth(2f);
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet.setColor(getColor(R.color.green_sea));
                lineDataSet.setDrawFilled(false);
                lineDataSet.setValueTextSize(9f);

                lineDataSet.setDrawCircleHole(true);
                lineDataSet.setValueTextColor(R.color.default_text_color);
                lineDataSet.setValueTextSize(8f);

                LineDataSet lineDataSet1 = new LineDataSet(returnValue,"Payment Return");
                lineDataSet1.setValueFormatter(new LargeValueFormatter());
                lineDataSet1.setCircleColor(getColor(R.color.red_dark));
                lineDataSet1.setCircleRadius(3f);
                lineDataSet1.setLineWidth(2f);
                lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet1.setColor(getColor(R.color.blue_end));
                lineDataSet1.setDrawFilled(false);
                lineDataSet1.setValueTextSize(9f);
                lineDataSet1.setDrawCircleHole(true);
                lineDataSet1.setValueTextColor(R.color.default_text_color);
                lineDataSet1.setValueTextSize(8f);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet);
                dataSets.add(lineDataSet1);

                LineData data1 = new LineData(dataSets);

                paymentChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(monthName));
                if (chartTabPosition == 0) {
                    paymentChart.getXAxis().setLabelRotationAngle(45);
                }
                else {
                    paymentChart.getXAxis().setLabelRotationAngle(0);
                }
                paymentChart.setData(data1);
                paymentChart.getData().setHighlightEnabled(true);
                paymentChart.invalidate();

                // Appointment Chart
                AppointMarkerView view = new AppointMarkerView(getApplicationContext(), R.layout.custom_marker_view);
                view.setChartView(appointmentChart);
                appointmentChart.setMarker(view);

                ArrayList<Entry> totalAppValue = new ArrayList<>();
                ArrayList<Entry> cancelAppValue = new ArrayList<>();
                ArrayList<String> dateMonthName = new ArrayList<>();

                int totapp = 0;
                int totcanapp = 0;
                for (int i = 0; i < appointmentChartLists.size(); i++) {
                    totalAppValue.add(new Entry(i,Float.parseFloat(appointmentChartLists.get(i).getTotal_app()), appointmentChartLists.get(i).getId()));
                    cancelAppValue.add(new Entry(i,Float.parseFloat(appointmentChartLists.get(i).getCancel_app()),appointmentChartLists.get(i).getId()));
                    dateMonthName.add(appointmentChartLists.get(i).getDateMonth());
                    if (!appointmentChartLists.get(i).getTotal_app().isEmpty()) {
                        totapp = totapp + Integer.parseInt(appointmentChartLists.get(i).getTotal_app());
                    }
                    if (!appointmentChartLists.get(i).getCancel_app().isEmpty()) {
                        totcanapp = totcanapp + Integer.parseInt(appointmentChartLists.get(i).getCancel_app());
                    }
                }
                totalAppointmentGraph.setText(String.valueOf(totapp));
                totalCancelAppointmentGraph.setText(String.valueOf(totcanapp));

                appointmentChart.animateXY(1000,1000);

                LineDataSet lineDataSet2 = new LineDataSet(totalAppValue,"Total Appointment");
                lineDataSet2.setValueFormatter(new LargeValueFormatter());
                lineDataSet2.setCircleColor(getColor(R.color.light_green));
                lineDataSet2.setCircleRadius(3f);
                lineDataSet2.setLineWidth(2f);
                lineDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet2.setColor(getColor(R.color.green_sea));
                lineDataSet2.setDrawFilled(false);
                lineDataSet2.setValueTextSize(9f);

                lineDataSet2.setDrawCircleHole(true);
                lineDataSet2.setValueTextColor(R.color.default_text_color);
                lineDataSet2.setValueTextSize(8f);

                LineDataSet lineDataSet3 = new LineDataSet(cancelAppValue,"Cancel Appointment");
                lineDataSet3.setValueFormatter(new LargeValueFormatter());
                lineDataSet3.setCircleColor(getColor(R.color.back_color));
                lineDataSet3.setCircleRadius(3f);
                lineDataSet3.setLineWidth(2f);
                lineDataSet3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet3.setColor(getColor(R.color.disabled));
                lineDataSet3.setDrawFilled(false);
                lineDataSet3.setValueTextSize(9f);
                lineDataSet3.setDrawCircleHole(true);
                lineDataSet3.setValueTextColor(R.color.default_text_color);
                lineDataSet3.setValueTextSize(8f);

                ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                dataSets1.add(lineDataSet2);
                dataSets1.add(lineDataSet3);

                LineData data2 = new LineData(dataSets1);

                appointmentChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateMonthName));
                if (chartTabPosition == 0) {
                    appointmentChart.getXAxis().setLabelRotationAngle(45);
                }
                else {
                    appointmentChart.getXAxis().setLabelRotationAngle(0);
                }
                appointmentChart.setData(data2);
                appointmentChart.getData().setHighlightEnabled(true);
                appointmentChart.invalidate();

                loading = false;
            }
            else {
                alertMessage4();
            }
        }
        else {
            alertMessage4();
        }
    }

    public void alertMessage4() {
        chartTabFullLayout.setVisibility(View.GONE);
        chartTabCircularProgressIndicator.setVisibility(View.GONE);
        chartTabLayout.setVisibility(View.VISIBLE);
        chartTabRefresh.setVisibility(View.VISIBLE);
        paymentChartCircularProgressIndicator.setVisibility(View.GONE);
        paymentChartRefresh.setVisibility(View.GONE);
        appointChartCircularProgressIndicator.setVisibility(View.GONE);
        appointChartRefresh.setVisibility(View.GONE);

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
                    loading = false;
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

    public void getPaymentChart() {
        monthSelectionLayPayment.setVisibility(View.GONE);
        yearSelectionLayPayment.setVisibility(View.GONE);
        paymentChartCircularProgressIndicator.setVisibility(View.VISIBLE);
        paymentChartRefresh.setVisibility(View.GONE);

        conn = false;
        connected = false;
        loading = true;

        paymentChartLists = new ArrayList<>();
        graphPaymentTotalLay.setVisibility(View.GONE);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineData data1 = new LineData(dataSets);
        paymentChart.setData(data1);
        paymentChart.setMarker(null);
        paymentChart.getData().clearValues();
        paymentChart.notifyDataSetChanged();
        paymentChart.clear();
        paymentChart.invalidate();
        paymentChart.fitScreen();

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        String paymentChartUrl;

        if (chartTabPosition == 0) {
            if (pay_app_type == 0) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataMonthly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
            }
            else if (pay_app_type == 1) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataOfflineMonthly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
            }
            else if (pay_app_type == 2) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataOnlineMonthly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
            }
            else {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataMonthly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
            }

            StringRequest payChartReq = new StringRequest(Request.Method.GET, paymentChartUrl, response -> {
                conn = true;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String items = jsonObject.getString("items");
                    String count = jsonObject.getString("count");
                    if (!count.equals("0")) {
                        JSONArray array = new JSONArray(items);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);

                            String dates = docInfo.getString("dates")
                                    .equals("null") ? "" : docInfo.getString("dates");
                            String amount = docInfo.getString("amount")
                                    .equals("null") ? "0" : docInfo.getString("amount");
                            String amount1 = docInfo.getString("amount1")
                                    .equals("null") ? "0" : docInfo.getString("amount1");

                            paymentChartLists.add(new PaymentChartList(String.valueOf(i+1),dates,amount,amount1));
                        }
                    }

                    connected = true;
                    updatePaymentChartLayout();

                }
                catch (JSONException e) {
                    connected = false;
                    logger.log(Level.WARNING,e.getMessage(),e);
                    parsing_message = e.getLocalizedMessage();
                    updatePaymentChartLayout();
                }
            }, error -> {
                conn = false;
                connected = false;
                logger.log(Level.WARNING,error.getMessage(),error);
                parsing_message = error.getLocalizedMessage();
                updatePaymentChartLayout();
            });

            payChartReq.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(payChartReq);
        }
        else {
            if (pay_app_type == 0) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataYearly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
            }
            else if (pay_app_type == 1) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataOfflineYearly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
            }
            else if (pay_app_type == 2) {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataOnlineYearly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
            }
            else {
                paymentChartUrl = pre_url_api + "doc_dashboard/getPaymentDataYearly?begin_date="+paymentChartFirstDate+"&end_date="+paymentChartLastDate+"&user_id="+admin_usr_name;
            }

            StringRequest payChartReq = new StringRequest(Request.Method.GET, paymentChartUrl, response -> {
                conn = true;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String items = jsonObject.getString("items");
                    String count = jsonObject.getString("count");
                    if (!count.equals("0")) {
                        JSONArray array = new JSONArray(items);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);

                            String pmonths = docInfo.getString("pmonths")
                                    .equals("null") ? "" : docInfo.getString("pmonths");
                            String amount = docInfo.getString("amount")
                                    .equals("null") ? "0" : docInfo.getString("amount");
                            String amount1 = docInfo.getString("amount1")
                                    .equals("null") ? "0" : docInfo.getString("amount1");

                            paymentChartLists.add(new PaymentChartList(String.valueOf(i+1),pmonths,amount,amount1));
                        }
                    }

                    connected = true;
                    updatePaymentChartLayout();

                }
                catch (JSONException e) {
                    connected = false;
                    logger.log(Level.WARNING,e.getMessage(),e);
                    parsing_message = e.getLocalizedMessage();
                    updatePaymentChartLayout();
                }
            }, error -> {
                conn = false;
                connected = false;
                logger.log(Level.WARNING,error.getMessage(),error);
                parsing_message = error.getLocalizedMessage();
                updatePaymentChartLayout();
            });

            payChartReq.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(payChartReq);
        }
    }

    private void updatePaymentChartLayout() {
        if (conn) {
            if (connected) {
                if (chartTabPosition == 0) {
                    monthSelectionLayPayment.setVisibility(View.VISIBLE);
                    yearSelectionLayPayment.setVisibility(View.GONE);
                }
                else {
                    monthSelectionLayPayment.setVisibility(View.GONE);
                    yearSelectionLayPayment.setVisibility(View.VISIBLE);
                }

                paymentChartCircularProgressIndicator.setVisibility(View.GONE);
                paymentChartRefresh.setVisibility(View.GONE);
                graphPaymentTotalLay.setVisibility(View.VISIBLE);

                conn = false;
                connected = false;

                MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
                mv.setChartView(paymentChart);
                paymentChart.setMarker(mv);

                ArrayList<Entry> amountValue = new ArrayList<>();
                ArrayList<Entry> returnValue = new ArrayList<>();
                ArrayList<String> monthName = new ArrayList<>();

                DecimalFormat formatter = new DecimalFormat("###,##,##,###");
                double payment_rcv = 0.0;
                double payment_rtn = 0.0;

                for (int i = 0; i < paymentChartLists.size(); i++) {
                    amountValue.add(new Entry(i,Float.parseFloat(paymentChartLists.get(i).getPaymentAmount()), paymentChartLists.get(i).getId()));
                    returnValue.add(new Entry(i,Float.parseFloat(paymentChartLists.get(i).getPatymentReturn()),paymentChartLists.get(i).getId()));
                    monthName.add(paymentChartLists.get(i).getDateMonth());
                    if (!paymentChartLists.get(i).getPaymentAmount().isEmpty()) {
                        payment_rcv = payment_rcv + Double.parseDouble(paymentChartLists.get(i).getPaymentAmount());
                    }
                    if (!paymentChartLists.get(i).getPatymentReturn().isEmpty()) {
                        payment_rtn = payment_rtn + Double.parseDouble(paymentChartLists.get(i).getPatymentReturn());
                    }
                }
                String prcv = formatter.format(payment_rcv);
                String prtn = formatter.format(payment_rtn);
                prcv = "৳ " + prcv ;
                prtn = "৳ " + prtn ;
                totalPaymentRcvGraph.setText(prcv);
                totalPaymentRtnGraph.setText(prtn);

                paymentChart.animateXY(1000,1000);

                LineDataSet lineDataSet = new LineDataSet(amountValue,"Payment Receive");
                lineDataSet.setValueFormatter(new LargeValueFormatter());
                lineDataSet.setCircleColor(getColor(R.color.light_green));
                lineDataSet.setCircleRadius(3f);
                lineDataSet.setLineWidth(2f);
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet.setColor(getColor(R.color.green_sea));
                lineDataSet.setDrawFilled(false);
                lineDataSet.setValueTextSize(9f);

                lineDataSet.setDrawCircleHole(true);
                lineDataSet.setValueTextColor(R.color.default_text_color);
                lineDataSet.setValueTextSize(8f);

                LineDataSet lineDataSet1 = new LineDataSet(returnValue,"Payment Return");
                lineDataSet1.setValueFormatter(new LargeValueFormatter());
                lineDataSet1.setCircleColor(getColor(R.color.red_dark));
                lineDataSet1.setCircleRadius(3f);
                lineDataSet1.setLineWidth(2f);
                lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet1.setColor(getColor(R.color.blue_end));
                lineDataSet1.setDrawFilled(false);
                lineDataSet1.setValueTextSize(9f);
                lineDataSet1.setDrawCircleHole(true);
                lineDataSet1.setValueTextColor(R.color.default_text_color);
                lineDataSet1.setValueTextSize(8f);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet);
                dataSets.add(lineDataSet1);

                LineData data1 = new LineData(dataSets);

                paymentChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(monthName));
                if (chartTabPosition == 0) {
                    paymentChart.getXAxis().setLabelRotationAngle(45);
                }
                else {
                    paymentChart.getXAxis().setLabelRotationAngle(0);
                }
                paymentChart.setData(data1);
                paymentChart.getData().setHighlightEnabled(true);
                paymentChart.invalidate();

                loading = false;
            }
            else {
                alertMessage5();
            }
        }
        else {
            alertMessage5();
        }
    }

    public void alertMessage5() {
        if (chartTabPosition == 0) {
            monthSelectionLayPayment.setVisibility(View.VISIBLE);
            yearSelectionLayPayment.setVisibility(View.GONE);
        }
        else {
            monthSelectionLayPayment.setVisibility(View.GONE);
            yearSelectionLayPayment.setVisibility(View.VISIBLE);
        }

        paymentChartCircularProgressIndicator.setVisibility(View.GONE);
        paymentChartRefresh.setVisibility(View.VISIBLE);

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
                    loading = false;
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

    public void getAppointmentChart() {
        monthSelectionLayAppoint.setVisibility(View.GONE);
        yearSelectionLayAppoint.setVisibility(View.GONE);
        appointChartCircularProgressIndicator.setVisibility(View.VISIBLE);
        appointChartRefresh.setVisibility(View.GONE);

        conn = false;
        connected = false;
        loading = true;

        appointmentChartLists = new ArrayList<>();
        graphAppointmentTotalLay.setVisibility(View.GONE);
        ArrayList<ILineDataSet> dataSetsApp = new ArrayList<>();
        LineData data = new LineData(dataSetsApp);
        appointmentChart.setData(data);
        appointmentChart.setMarker(null);
        appointmentChart.getData().clearValues();
        appointmentChart.notifyDataSetChanged();
        appointmentChart.clear();
        appointmentChart.invalidate();
        appointmentChart.fitScreen();

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        String appointChartUrl;

        if (chartTabPosition == 0) {
            if (pay_app_type == 0) {
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataMonthly?begin_date="+appointChartFirstDate+"&end_date="+appointChartLastDate;
            }
            else if (pay_app_type == 1) {
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataOfflineMonthly?begin_date="+appointChartFirstDate+"&end_date="+appointChartLastDate;
            }
            else if (pay_app_type == 2){
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataOnlineMonthly?begin_date="+appointChartFirstDate+"&end_date="+appointChartLastDate;
            }
            else {
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataMonthly?begin_date="+appointChartFirstDate+"&end_date="+appointChartLastDate;
            }

            StringRequest appointChartReq = new StringRequest(Request.Method.GET, appointChartUrl, response -> {
                conn = true;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String items = jsonObject.getString("items");
                    String count = jsonObject.getString("count");
                    if (!count.equals("0")) {
                        JSONArray array = new JSONArray(items);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);

                            String dates = docInfo.getString("dates")
                                    .equals("null") ? "" : docInfo.getString("dates");
                            String total_app = docInfo.getString("total_app")
                                    .equals("null") ? "0" : docInfo.getString("total_app");
                            String cancel_app = docInfo.getString("cancel_app")
                                    .equals("null") ? "0" : docInfo.getString("cancel_app");

                            appointmentChartLists.add(new AppointmentChartList(String.valueOf(i+1),dates,total_app,cancel_app));
                        }
                    }

                    connected = true;
                    updateAppointChartLayout();

                }
                catch (JSONException e) {
                    connected = false;
                    logger.log(Level.WARNING,e.getMessage(),e);
                    parsing_message = e.getLocalizedMessage();
                    updateAppointChartLayout();
                }
            }, error -> {
                conn = false;
                connected = false;
                logger.log(Level.WARNING,error.getMessage(),error);
                parsing_message = error.getLocalizedMessage();
                updateAppointChartLayout();
            });

            appointChartReq.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(appointChartReq);
        }
        else {
            if (pay_app_type == 0) {
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataYearly?begin_date="+appointChartFirstDate+"&end_date="+appointChartLastDate;
            }
            else if (pay_app_type == 1) {
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataOfflineYearly?begin_date=" + appointChartFirstDate + "&end_date=" + appointChartLastDate;
            }
            else if (pay_app_type == 2) {
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataOnlineYearly?begin_date=" + appointChartFirstDate + "&end_date=" + appointChartLastDate;
            }
            else {
                appointChartUrl = pre_url_api + "doc_dashboard/getAppointDataYearly?begin_date=" + appointChartFirstDate + "&end_date=" + appointChartLastDate;
            }

            StringRequest appointChartReq = new StringRequest(Request.Method.GET, appointChartUrl, response -> {
                conn = true;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String items = jsonObject.getString("items");
                    String count = jsonObject.getString("count");
                    if (!count.equals("0")) {
                        JSONArray array = new JSONArray(items);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);

                            String pmonths = docInfo.getString("pmonths")
                                    .equals("null") ? "" : docInfo.getString("pmonths");
                            String total_app = docInfo.getString("total_app")
                                    .equals("null") ? "0" : docInfo.getString("total_app");
                            String cancel_app = docInfo.getString("cancel_app")
                                    .equals("null") ? "0" : docInfo.getString("cancel_app");

                            appointmentChartLists.add(new AppointmentChartList(String.valueOf(i+1),pmonths,total_app,cancel_app));
                        }
                    }

                    connected = true;
                    updateAppointChartLayout();

                }
                catch (JSONException e) {
                    connected = false;
                    logger.log(Level.WARNING,e.getMessage(),e);
                    parsing_message = e.getLocalizedMessage();
                    updateAppointChartLayout();
                }
            }, error -> {
                conn = false;
                connected = false;
                logger.log(Level.WARNING,error.getMessage(),error);
                parsing_message = error.getLocalizedMessage();
                updateAppointChartLayout();
            });

            appointChartReq.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(appointChartReq);
        }
    }

    private void updateAppointChartLayout() {
        if (conn) {
            if (connected) {
                if (chartTabPosition == 0) {
                    monthSelectionLayAppoint.setVisibility(View.VISIBLE);
                    yearSelectionLayAppoint.setVisibility(View.GONE);
                }
                else {
                    monthSelectionLayAppoint.setVisibility(View.GONE);
                    yearSelectionLayAppoint.setVisibility(View.VISIBLE);
                }

                appointChartCircularProgressIndicator.setVisibility(View.GONE);
                appointChartRefresh.setVisibility(View.GONE);
                graphAppointmentTotalLay.setVisibility(View.VISIBLE);

                conn = false;
                connected = false;

                AppointMarkerView view = new AppointMarkerView(getApplicationContext(), R.layout.custom_marker_view);
                view.setChartView(appointmentChart);
                appointmentChart.setMarker(view);

                ArrayList<Entry> totalAppValue = new ArrayList<>();
                ArrayList<Entry> cancelAppValue = new ArrayList<>();
                ArrayList<String> dateMonthName = new ArrayList<>();

                int totapp = 0;
                int totcanapp = 0;
                for (int i = 0; i < appointmentChartLists.size(); i++) {
                    totalAppValue.add(new Entry(i,Float.parseFloat(appointmentChartLists.get(i).getTotal_app()), appointmentChartLists.get(i).getId()));
                    cancelAppValue.add(new Entry(i,Float.parseFloat(appointmentChartLists.get(i).getCancel_app()),appointmentChartLists.get(i).getId()));
                    dateMonthName.add(appointmentChartLists.get(i).getDateMonth());
                    if (!appointmentChartLists.get(i).getTotal_app().isEmpty()) {
                        totapp = totapp + Integer.parseInt(appointmentChartLists.get(i).getTotal_app());
                    }
                    if (!appointmentChartLists.get(i).getCancel_app().isEmpty()) {
                        totcanapp = totcanapp + Integer.parseInt(appointmentChartLists.get(i).getCancel_app());
                    }
                }
                totalAppointmentGraph.setText(String.valueOf(totapp));
                totalCancelAppointmentGraph.setText(String.valueOf(totcanapp));

                appointmentChart.animateXY(1000,1000);

                LineDataSet lineDataSet2 = new LineDataSet(totalAppValue,"Total Appointment");
                lineDataSet2.setValueFormatter(new LargeValueFormatter());
                lineDataSet2.setCircleColor(getColor(R.color.light_green));
                lineDataSet2.setCircleRadius(3f);
                lineDataSet2.setLineWidth(2f);
                lineDataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet2.setColor(getColor(R.color.green_sea));
                lineDataSet2.setDrawFilled(false);
                lineDataSet2.setValueTextSize(9f);

                lineDataSet2.setDrawCircleHole(true);
                lineDataSet2.setValueTextColor(R.color.default_text_color);
                lineDataSet2.setValueTextSize(8f);

                LineDataSet lineDataSet3 = new LineDataSet(cancelAppValue,"Cancel Appointment");
                lineDataSet3.setValueFormatter(new LargeValueFormatter());
                lineDataSet3.setCircleColor(getColor(R.color.back_color));
                lineDataSet3.setCircleRadius(3f);
                lineDataSet3.setLineWidth(2f);
                lineDataSet3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet3.setColor(getColor(R.color.disabled));
                lineDataSet3.setDrawFilled(false);
                lineDataSet3.setValueTextSize(9f);
                lineDataSet3.setDrawCircleHole(true);
                lineDataSet3.setValueTextColor(R.color.default_text_color);
                lineDataSet3.setValueTextSize(8f);

                ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
                dataSets1.add(lineDataSet2);
                dataSets1.add(lineDataSet3);

                LineData data2 = new LineData(dataSets1);

                appointmentChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateMonthName));
                if (chartTabPosition == 0) {
                    appointmentChart.getXAxis().setLabelRotationAngle(45);
                }
                else {
                    appointmentChart.getXAxis().setLabelRotationAngle(0);
                }
                appointmentChart.setData(data2);
                appointmentChart.getData().setHighlightEnabled(true);
                appointmentChart.invalidate();

                loading = false;
            }
            else {
                alertMessage6();
            }
        }
        else {
            alertMessage6();
        }
    }

    public void alertMessage6() {
        if (chartTabPosition == 0) {
            monthSelectionLayAppoint.setVisibility(View.VISIBLE);
            yearSelectionLayAppoint.setVisibility(View.GONE);
        }
        else {
            monthSelectionLayAppoint.setVisibility(View.GONE);
            yearSelectionLayAppoint.setVisibility(View.VISIBLE);
        }

        appointChartCircularProgressIndicator.setVisibility(View.GONE);
        appointChartRefresh.setVisibility(View.VISIBLE);

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
                    loading = false;
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

    public Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight); // Maintain aspect ratio

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    public byte[] compressBitmap(Bitmap bitmap, int maxSizeKB) {
        int quality = 100; // Start at highest quality
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        do {
            outputStream.reset(); // Clear the stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            quality -= 5; // Reduce quality in steps of 5
        } while (outputStream.toByteArray().length / 1024 > maxSizeKB && quality > 5);

        return outputStream.toByteArray();
    }

    public void updateAdminPic() {
        String url = pre_url_api+"doc_dashboard/updateAdminPic";
        conn = false;
        connected = false;
        loading = true;
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);

        selectedBitmap = resizeBitmap(selectedBitmap, 1080,1080);

//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//        int quality = 30;
//        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
//        byte[] bArray = bos.toByteArray();
//        long lengthbmp1 = bArray.length;
//        lengthbmp1 = (lengthbmp1/1024);
//
//        if (lengthbmp1 > 100) {
//            quality = quality - 10;
//            ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
//            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos1);
//            bArray = bos1.toByteArray();
//            lengthbmp1 = bArray.length;
//            lengthbmp1 = (lengthbmp1/1024);
//            System.out.println(lengthbmp1);
//            if (lengthbmp1 > 100) {
//                quality = quality - 10;
//                ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
//                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos2);
//                bArray = bos2.toByteArray();
//                lengthbmp1 = bArray.length;
//                lengthbmp1 = (lengthbmp1/1024);
//                System.out.println(lengthbmp1);
//                if (lengthbmp1 > 100) {
//                    quality = quality - 5;
//                    ByteArrayOutputStream bos3 = new ByteArrayOutputStream();
//                    selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos3);
//                    bArray = bos3.toByteArray();
//                    lengthbmp1 = bArray.length;
//                    lengthbmp1 = (lengthbmp1/1024);
//                    System.out.println(lengthbmp1);
//                    if (lengthbmp1 > 100) {
//                        quality = quality - 3;
//                        ByteArrayOutputStream bos4 = new ByteArrayOutputStream();
//                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos4);
//                        bArray = bos4.toByteArray();
//                        lengthbmp1 = bArray.length;
//                        lengthbmp1 = (lengthbmp1/1024);
//                        System.out.println(lengthbmp1);
//                    }
//                }
//            }
//        }
//
        byte[] finalBArray = compressBitmap(selectedBitmap,1024);

        RequestQueue requestQueue = Volley.newRequestQueue(DocDashboard.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response ->  {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                if (string_out.equals("Successfully Created")) {
                    System.out.println(string_out);
                    connected = true;
                }
                else {
                    parsing_message = string_out;
                    System.out.println(string_out);
                    connected = false;
                }
                updateAdminImageLayout();
            }
            catch (JSONException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateAdminImageLayout();
            }
        }, error ->  {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateAdminImageLayout();
        })
        {
            @Override
            public byte[] getBody() {
                return finalBArray;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("p_user_id",admin_usr_id);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/binary";
            }
        };

        requestQueue.add(stringRequest);

    }

    private void updateAdminImageLayout() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                Toast.makeText(getApplicationContext(), "Picture Uploaded", Toast.LENGTH_SHORT).show();

                try {
                    Glide.with(getApplicationContext())
                            .load(selectedBitmap)
                            .fitCenter()
                            .into(docImage);
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }

                loading = false;
            }
            else {
                fullLayout.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
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
                            loading = false;
                            updateAdminPic();
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel",(dialog, which) -> {
                            loading = false;
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
        }
        else {
            fullLayout.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
            circularProgressIndicator.setVisibility(View.GONE);
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
                        loading = false;
                        updateAdminPic();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel",(dialog, which) -> {
                        loading = false;
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
    }

    @Override
    public void onAdminIdSelection() {
        normalCountDownView.stopTimer();
        loading = false;
        initData();
        getAdminData();
    }

    @Override
    public void onAdminCenterSelection() {
        normalCountDownView.stopTimer();
        loading = false;
        initData();
        getAdminData();
    }

    // in app review
    public void askUserForReview() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Task<Void> flow = reviewManager.launchReviewFlow(DocDashboard.this, task.getResult());
                flow.addOnCompleteListener(flow_task -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            }
        });
    }
}