package ttit.com.shuvo.docdiary.hr_accounts.dashboards;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.adminInfoLists;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
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
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.dashboards.arraylists.LoginChartList;
import ttit.com.shuvo.docdiary.hr_accounts.dashboards.arraylists.OpasAppointChartList;
import ttit.com.shuvo.docdiary.hr_accounts.dashboards.arraylists.OpasPayChartList;
import ttit.com.shuvo.docdiary.hr_accounts.dashboards.extra.NormalMarker;


public class AnalyticsDashboard extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    LinearLayout appLoginFullLayout;
    RelativeLayout dateRangeLay;
    TextView dateRangeText;
    String appLoginChartFirstDate = "";
    String appLoginChartLastDate = "";
    private Long selectedStartDate = null;
    private Long selectedEndDate = null;

    TextView allLoginCount;
    TextView unqLoginCount;
    String total_unq_login = "";

    LineChart appLoginChart;
    ArrayList<LoginChartList> appLoginChartLists;
    CircularProgressIndicator appLoginChartCircularProgressIndicator;
    ImageView appLoginChartRefresh;


    RelativeLayout onlinePatPortalLay;
    boolean isPatOnlineActive = false;

    LinearLayout opasLoginFullLayout;
    RelativeLayout opasDateRangeLay;
    TextView opasDateRangeText;
    String opasLoginChartFirstDate = "";
    String opasLoginChartLastDate = "";
    private Long opasSelectedStartDate = null;
    private Long opasSelectedEndDate = null;

    TextView opasAllLoginCount;
    TextView opasUnqLoginCount;
    String total_opas_unq_login = "";

    LineChart opasLoginChart;
    ArrayList<LoginChartList> opasLoginChartLists;
    CircularProgressIndicator opasLoginChartCircularProgressIndicator;
    ImageView opasLoginChartRefresh;

    LinearLayout opasAppSchFullLayout;
    RelativeLayout opasAppSchDateRangeLay;
    TextView opasAppSchDateRangeText;
    String opasAppSchFirstDate = "";
    String opasAppSchLastDate = "";
    private Long opasAppSchSelectedStartDate = null;
    private Long opasAppSchSelectedEndDate = null;

    TextView opasAppInitCount;
    TextView opasAppInitConfCount;
    TextView opasAppInitUnConfCount;

    LineChart opasAppInitChart;
    ArrayList<OpasAppointChartList> opasAppInitChartLists;

    TextView opasAppSchCount;
    TextView opasAppSchConfCount;
    TextView opasAppSchUnConfCount;

    LineChart opasAppSchChart;
    ArrayList<OpasAppointChartList> opasAppSchChartLists;

    CircularProgressIndicator opasAppSchChartCircularProgressIndicator;
    ImageView opasAppSchChartRefresh;

    LinearLayout opasPayFullLayout;
    RelativeLayout opasPayDateRangeLay;
    TextView opasPayDateRangeText;
    String opasPayChartFirstDate = "";
    String opasPayChartLastDate = "";
    private Long opasPaySelectedStartDate = null;
    private Long opasPaySelectedEndDate = null;

    TextView opasPayTotalCount;
    TextView opasPayCompCount;
    TextView opasPayFailCount;
    TextView opasPayNACount;

    LineChart opasPayChart;
    ArrayList<OpasPayChartList> opasPayChartLists;
    CircularProgressIndicator opasPayChartCircularProgressIndicator;
    ImageView opasPayChartRefresh;

    Logger logger = Logger.getLogger(AnalyticsDashboard.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.analytic_dash_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.analytics_dashboard_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_analytics_dashboard);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_analytics_dashboard);

        appLoginFullLayout = findViewById(R.id.apps_login_status_full_layout);
        appLoginFullLayout.setVisibility(View.VISIBLE);

        dateRangeLay = findViewById(R.id.date_range_text_mob_app_login_altd_lay);
        dateRangeText = findViewById(R.id.date_range_text_mob_app_login_altd);

        allLoginCount = findViewById(R.id.total_all_login_count_alt_dash);
        unqLoginCount = findViewById(R.id.total_unique_login_count_alt_dash);

        appLoginChart = findViewById(R.id.mob_app_login_stat_overview_linechart);

        appLoginChartCircularProgressIndicator = findViewById(R.id.progress_indicator_mob_app_login_stat);
        appLoginChartCircularProgressIndicator.setVisibility(View.GONE);
        appLoginChartRefresh = findViewById(R.id.mob_app_login_stat_chart_refresh_button);
        appLoginChartRefresh.setVisibility(View.GONE);


        onlinePatPortalLay = findViewById(R.id.online_patient_portal_layout);

        opasLoginFullLayout = findViewById(R.id.onl_pat_portal_login_status_full_layout);
        opasLoginFullLayout.setVisibility(View.VISIBLE);

        opasDateRangeLay = findViewById(R.id.date_range_text_onl_pat_portal_login_altd_lay);
        opasDateRangeText = findViewById(R.id.date_range_text_onl_pat_portal_login_altd);

        opasAllLoginCount = findViewById(R.id.total_all_onl_pat_portal_login_count_alt_dash);
        opasUnqLoginCount = findViewById(R.id.total_unique_onl_pat_portal_login_count_alt_dash);

        opasLoginChart = findViewById(R.id.onl_pat_portal_login_stat_overview_linechart);

        opasLoginChartCircularProgressIndicator = findViewById(R.id.progress_indicator_onl_pat_portal_stat);
        opasLoginChartCircularProgressIndicator.setVisibility(View.GONE);
        opasLoginChartRefresh = findViewById(R.id.onl_pat_portal_stat_chart_refresh_button);
        opasLoginChartRefresh.setVisibility(View.GONE);


        opasAppSchFullLayout = findViewById(R.id.onl_pat_portal_app_status_full_layout);
        opasAppSchFullLayout.setVisibility(View.VISIBLE);

        opasAppSchDateRangeLay = findViewById(R.id.date_range_text_onl_pat_portal_app_altd_lay);
        opasAppSchDateRangeText = findViewById(R.id.date_range_text_onl_pat_portal_app_altd);

        opasAppInitCount = findViewById(R.id.total_app_init_count_alt_dash);
        opasAppInitConfCount = findViewById(R.id.confirmed_app_init_count_alt_dash);
        opasAppInitUnConfCount = findViewById(R.id.not_confirmed_app_init_count_alt_dash);

        opasAppInitChart = findViewById(R.id.app_initialize_status_overview_linechart);

        opasAppSchCount = findViewById(R.id.total_app_sch_count_alt_dash);
        opasAppSchConfCount = findViewById(R.id.confirmed_app_sch_count_alt_dash);
        opasAppSchUnConfCount = findViewById(R.id.not_confirmed_app_sch_count_alt_dash);

        opasAppSchChart = findViewById(R.id.app_sch_status_overview_linechart);

        opasAppSchChartCircularProgressIndicator = findViewById(R.id.progress_indicator_onl_pat_app_init_stat);
        opasAppSchChartCircularProgressIndicator.setVisibility(View.GONE);
        opasAppSchChartRefresh = findViewById(R.id.onl_pat_app_init_stat_chart_refresh_button);
        opasAppSchChartRefresh.setVisibility(View.GONE);

        // -- new

        opasPayFullLayout = findViewById(R.id.onl_pat_portal_pay_status_full_layout);
        opasPayFullLayout.setVisibility(View.VISIBLE);

        opasPayDateRangeLay = findViewById(R.id.date_range_text_onl_pat_portal_pay_altd_lay);
        opasPayDateRangeText = findViewById(R.id.date_range_text_onl_pat_portal_pay_altd);

        opasPayTotalCount = findViewById(R.id.total_pay_init_count_alt_dash);
        opasPayCompCount = findViewById(R.id.total_pay_complete_count_alt_dash);
        opasPayFailCount = findViewById(R.id.total_pay_failed_count_alt_dash);
        opasPayNACount = findViewById(R.id.total_pay_not_init_count_alt_dash);

        opasPayChart = findViewById(R.id.pay_initialize_status_overview_linechart);

        opasPayChartCircularProgressIndicator = findViewById(R.id.progress_indicator_onl_pat_pay_init_stat);
        opasPayChartCircularProgressIndicator.setVisibility(View.GONE);
        opasPayChartRefresh = findViewById(R.id.onl_pat_pay_init_stat_chart_refresh_button);
        opasPayChartRefresh.setVisibility(View.GONE);

        if (adminInfoLists == null) {
            restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (adminInfoLists.isEmpty()) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            }
            else {

                if (Integer.parseInt(adminInfoLists.get(0).getIs_pay_appoint_type_active()) == 1) {
                    onlinePatPortalLay.setVisibility(View.VISIBLE);
                    isPatOnlineActive = true;
                }
                else {
                    onlinePatPortalLay.setVisibility(View.GONE);
                    isPatOnlineActive = false;
                }
            }
        }

        appLoginChartLists = new ArrayList<>();
        opasLoginChartLists = new ArrayList<>();
        opasAppInitChartLists = new ArrayList<>();
        opasAppSchChartLists = new ArrayList<>();
        opasPayChartLists = new ArrayList<>();

        LineChartInit(appLoginChart);
        LineChartInit(opasLoginChart);
        LineChartInit(opasAppInitChart);
        LineChartInit(opasAppSchChart);
        LineChartInit(opasPayChart);

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(AnalyticsDashboard.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        dateRangeLay.setOnClickListener(v -> {
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now());

            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Begin & End Date")
                    .setCalendarConstraints(constraintsBuilder.build());

            // Set previously selected dates if available
            if (!appLoginChartFirstDate.isEmpty() && !appLoginChartLastDate.isEmpty()) {
                SimpleDateFormat da = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                da.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date s_date = null;
                Date e_date = null;
                try {
                    s_date = da.parse(appLoginChartFirstDate);
                    e_date = da.parse(appLoginChartLastDate);
                } catch (ParseException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
                if (s_date != null && e_date != null) {
                    System.out.println(s_date);
                    System.out.println(e_date);
                    selectedStartDate = s_date.getTime(); //+ 21600000;
                    selectedEndDate = e_date.getTime(); // + 21600000;
                    System.out.println(selectedStartDate);
                    System.out.println(selectedEndDate);
                }
                if (selectedStartDate != null && selectedEndDate != null) {
                    builder.setSelection(new Pair<>(selectedStartDate, selectedEndDate));
                }
            }

            MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

            // Show Date Picker
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            // Handle Date Selection
            datePicker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null && selection.first != null && selection.second != null) {
                    selectedStartDate = selection.first;  // Save selected start date
                    selectedEndDate = selection.second;
                    System.out.println(selectedStartDate);
                    System.out.println(selectedEndDate);// Save selected end date
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yy", Locale.ENGLISH);
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String startDate = sdf.format(selection.first);
                    String endDate = sdf.format(selection.second);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    appLoginChartFirstDate = dateFormat.format(selection.first);
                    appLoginChartLastDate = dateFormat.format(selection.second);

                    String dateRange;
                    if (startDate.equals(endDate)) {
                        dateRange = startDate;
                    }
                    else {
                        dateRange = startDate + "  ---  " + endDate;
                    }
                    dateRangeText.setText(dateRange);
                    getAppLoginChart();
                }
            });
        });

        appLoginChartRefresh.setOnClickListener(v -> getAppLoginChart());

        opasDateRangeLay.setOnClickListener(v -> {
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now());

            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Begin & End Date")
                    .setCalendarConstraints(constraintsBuilder.build());

            // Set previously selected dates if available
            if (!opasLoginChartFirstDate.isEmpty() && !opasLoginChartLastDate.isEmpty()) {
                SimpleDateFormat da = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                da.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date s_date = null;
                Date e_date = null;
                try {
                    s_date = da.parse(opasLoginChartFirstDate);
                    e_date = da.parse(opasLoginChartLastDate);
                } catch (ParseException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
                if (s_date != null && e_date != null) {
                    System.out.println(s_date);
                    System.out.println(e_date);
                    opasSelectedStartDate = s_date.getTime(); //+ 21600000;
                    opasSelectedEndDate = e_date.getTime(); // + 21600000;
                    System.out.println(opasSelectedStartDate);
                    System.out.println(opasSelectedEndDate);
                }
                if (opasSelectedStartDate != null && opasSelectedEndDate != null) {
                    builder.setSelection(new Pair<>(opasSelectedStartDate, opasSelectedEndDate));
                }
            }

            MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

            // Show Date Picker
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            // Handle Date Selection
            datePicker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null && selection.first != null && selection.second != null) {
                    opasSelectedStartDate = selection.first;  // Save selected start date
                    opasSelectedEndDate = selection.second;
                    System.out.println(opasSelectedStartDate);
                    System.out.println(opasSelectedEndDate);// Save selected end date
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yy", Locale.ENGLISH);
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String startDate = sdf.format(selection.first);
                    String endDate = sdf.format(selection.second);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    opasLoginChartFirstDate = dateFormat.format(selection.first);
                    opasLoginChartLastDate = dateFormat.format(selection.second);

                    String dateRange;
                    if (startDate.equals(endDate)) {
                        dateRange = startDate;
                    }
                    else {
                        dateRange = startDate + "  ---  " + endDate;
                    }
                    opasDateRangeText.setText(dateRange);
                    getOpasLoginChart();
                }
            });
        });

        opasLoginChartRefresh.setOnClickListener(v -> getOpasLoginChart());

        opasAppSchDateRangeLay.setOnClickListener(v -> {
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now());

            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Begin & End Date")
                    .setCalendarConstraints(constraintsBuilder.build());

            // Set previously selected dates if available
            if (!opasAppSchFirstDate.isEmpty() && !opasAppSchLastDate.isEmpty()) {
                SimpleDateFormat da = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                da.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date s_date = null;
                Date e_date = null;
                try {
                    s_date = da.parse(opasAppSchFirstDate);
                    e_date = da.parse(opasAppSchLastDate);
                } catch (ParseException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
                if (s_date != null && e_date != null) {
                    System.out.println(s_date);
                    System.out.println(e_date);
                    opasAppSchSelectedStartDate = s_date.getTime(); //+ 21600000;
                    opasAppSchSelectedEndDate = e_date.getTime(); // + 21600000;
                    System.out.println(opasAppSchSelectedStartDate);
                    System.out.println(opasAppSchSelectedEndDate);
                }
                if (opasAppSchSelectedStartDate != null && opasAppSchSelectedEndDate != null) {
                    builder.setSelection(new Pair<>(opasAppSchSelectedStartDate, opasAppSchSelectedEndDate));
                }
            }

            MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

            // Show Date Picker
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            // Handle Date Selection
            datePicker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null && selection.first != null && selection.second != null) {
                    opasAppSchSelectedStartDate = selection.first;  // Save selected start date
                    opasAppSchSelectedEndDate = selection.second;
                    System.out.println(opasAppSchSelectedStartDate);
                    System.out.println(opasAppSchSelectedEndDate);// Save selected end date
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yy", Locale.ENGLISH);
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String startDate = sdf.format(selection.first);
                    String endDate = sdf.format(selection.second);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    opasAppSchFirstDate = dateFormat.format(selection.first);
                    opasAppSchLastDate = dateFormat.format(selection.second);

                    String dateRange;
                    if (startDate.equals(endDate)) {
                        dateRange = startDate;
                    }
                    else {
                        dateRange = startDate + "  ---  " + endDate;
                    }
                    opasAppSchDateRangeText.setText(dateRange);
                    getOpasAppChart();
                }
            });
        });

        opasAppSchChartRefresh.setOnClickListener(v -> getOpasAppChart());

        opasPayDateRangeLay.setOnClickListener(v -> {
            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now());

            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Begin & End Date")
                    .setCalendarConstraints(constraintsBuilder.build());

            // Set previously selected dates if available
            if (!opasPayChartFirstDate.isEmpty() && !opasPayChartLastDate.isEmpty()) {
                SimpleDateFormat da = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                da.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date s_date = null;
                Date e_date = null;
                try {
                    s_date = da.parse(opasPayChartFirstDate);
                    e_date = da.parse(opasPayChartLastDate);
                } catch (ParseException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
                if (s_date != null && e_date != null) {
                    System.out.println(s_date);
                    System.out.println(e_date);
                    opasPaySelectedStartDate = s_date.getTime(); //+ 21600000;
                    opasPaySelectedEndDate = e_date.getTime(); // + 21600000;
                    System.out.println(opasPaySelectedStartDate);
                    System.out.println(opasPaySelectedEndDate);
                }
                if (opasPaySelectedStartDate != null && opasPaySelectedEndDate != null) {
                    builder.setSelection(new Pair<>(opasPaySelectedStartDate, opasPaySelectedEndDate));
                }
            }

            MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

            // Show Date Picker
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

            // Handle Date Selection
            datePicker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null && selection.first != null && selection.second != null) {
                    opasPaySelectedStartDate = selection.first;  // Save selected start date
                    opasPaySelectedEndDate = selection.second;
                    System.out.println(opasPaySelectedStartDate);
                    System.out.println(opasPaySelectedEndDate);// Save selected end date
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yy", Locale.ENGLISH);
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String startDate = sdf.format(selection.first);
                    String endDate = sdf.format(selection.second);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    opasPayChartFirstDate = dateFormat.format(selection.first);
                    opasPayChartLastDate = dateFormat.format(selection.second);

                    String dateRange;
                    if (startDate.equals(endDate)) {
                        dateRange = startDate;
                    }
                    else {
                        dateRange = startDate + "  ---  " + endDate;
                    }
                    opasPayDateRangeText.setText(dateRange);
                    getOpasPayChart();
                }
            });
        });

        opasPayChartRefresh.setOnClickListener(v -> getOpasPayChart());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(AnalyticsDashboard.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loading != null) {
            if (!loading) {
                getDashboardData();
            }
        }
        else {
            restart("App is paused for a long time. Please Start the app again.");
        }
    }

//    public void LineChartInit(LineChart lineChart) {
//        // -- app login
//        XAxis xAxis = appLoginChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setGranularity(1);
//        xAxis.setDrawGridLines(false);
//        xAxis.setTextColor(Color.GRAY);
//        appLoginChart.getDescription().setEnabled(false);
//        appLoginChart.setPinchZoom(false);
//
//        appLoginChart.setDrawGridBackground(false);
//        appLoginChart.setExtraLeftOffset(30);
//        appLoginChart.setExtraRightOffset(30);
//        appLoginChart.getAxisLeft().setDrawGridLines(true);
//        appLoginChart.getAxisLeft().setEnabled(false);
//        appLoginChart.setScaleEnabled(true);
//        appLoginChart.setTouchEnabled(true);
//        appLoginChart.setHighlightPerTapEnabled(true);
//        appLoginChart.setDoubleTapToZoomEnabled(false);
//
//        appLoginChart.setExtraOffsets(30, 0, 30, 20);
//
//        appLoginChart.getAxisRight().setEnabled(false);
//        appLoginChart.getAxisLeft().setAxisMinimum(0);
//        appLoginChart.getLegend().setEnabled(true);
//        appLoginChart.getLegend().setStackSpace(20);
//        appLoginChart.getLegend().setYOffset(10);
//        appLoginChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        appLoginChart.getAxisLeft().setTextColor(Color.GRAY);
//        appLoginChart.getAxisLeft().setXOffset(10f);
//        appLoginChart.getAxisLeft().setTextSize(6);
//        appLoginChart.getAxisLeft().setDrawGridLines(false);
//        appLoginChart.getXAxis().setLabelRotationAngle(45);
//
//        // -- opas login
//        XAxis xAxis1 = opasLoginChart.getXAxis();
//        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis1.setGranularity(1);
//        xAxis1.setDrawGridLines(false);
//        xAxis1.setTextColor(Color.GRAY);
//        opasLoginChart.getDescription().setEnabled(false);
//        opasLoginChart.setPinchZoom(false);
//
//        opasLoginChart.setDrawGridBackground(false);
//        opasLoginChart.setExtraLeftOffset(30);
//        opasLoginChart.setExtraRightOffset(30);
//        opasLoginChart.getAxisLeft().setDrawGridLines(true);
//        opasLoginChart.getAxisLeft().setEnabled(false);
//        opasLoginChart.setScaleEnabled(true);
//        opasLoginChart.setTouchEnabled(true);
//        opasLoginChart.setHighlightPerTapEnabled(true);
//        opasLoginChart.setDoubleTapToZoomEnabled(false);
//
//        opasLoginChart.setExtraOffsets(30, 0, 30, 20);
//
//        opasLoginChart.getAxisRight().setEnabled(false);
//        opasLoginChart.getAxisLeft().setAxisMinimum(0);
//        opasLoginChart.getLegend().setEnabled(true);
//        opasLoginChart.getLegend().setStackSpace(20);
//        opasLoginChart.getLegend().setYOffset(10);
//        opasLoginChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        opasLoginChart.getAxisLeft().setTextColor(Color.GRAY);
//        opasLoginChart.getAxisLeft().setXOffset(10f);
//        opasLoginChart.getAxisLeft().setTextSize(6);
//        opasLoginChart.getAxisLeft().setDrawGridLines(false);
//        opasLoginChart.getXAxis().setLabelRotationAngle(45);
//
//        // -- opas app init
//        XAxis xAxis2 = opasAppInitChart.getXAxis();
//        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis2.setGranularity(1);
//        xAxis2.setDrawGridLines(false);
//        xAxis2.setTextColor(Color.GRAY);
//        opasAppInitChart.getDescription().setEnabled(false);
//        opasAppInitChart.setPinchZoom(false);
//
//        opasAppInitChart.setDrawGridBackground(false);
//        opasAppInitChart.setExtraLeftOffset(30);
//        opasAppInitChart.setExtraRightOffset(30);
//        opasAppInitChart.getAxisLeft().setDrawGridLines(true);
//        opasAppInitChart.getAxisLeft().setEnabled(false);
//        opasAppInitChart.setScaleEnabled(true);
//        opasAppInitChart.setTouchEnabled(true);
//        opasAppInitChart.setHighlightPerTapEnabled(true);
//        opasAppInitChart.setDoubleTapToZoomEnabled(false);
//
//        opasAppInitChart.setExtraOffsets(30, 0, 30, 20);
//
//        opasAppInitChart.getAxisRight().setEnabled(false);
//        opasAppInitChart.getAxisLeft().setAxisMinimum(0);
//        opasAppInitChart.getLegend().setEnabled(true);
//        opasAppInitChart.getLegend().setStackSpace(20);
//        opasAppInitChart.getLegend().setYOffset(10);
//        opasAppInitChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        opasAppInitChart.getAxisLeft().setTextColor(Color.GRAY);
//        opasAppInitChart.getAxisLeft().setXOffset(10f);
//        opasAppInitChart.getAxisLeft().setTextSize(6);
//        opasAppInitChart.getAxisLeft().setDrawGridLines(false);
//        opasAppInitChart.getXAxis().setLabelRotationAngle(45);
//
//        // -- opas app sch
//        XAxis xAxis3 = opasAppSchChart.getXAxis();
//        xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis3.setGranularity(1);
//        xAxis3.setDrawGridLines(false);
//        xAxis3.setTextColor(Color.GRAY);
//        opasAppSchChart.getDescription().setEnabled(false);
//        opasAppSchChart.setPinchZoom(false);
//
//        opasAppSchChart.setDrawGridBackground(false);
//        opasAppSchChart.setExtraLeftOffset(30);
//        opasAppSchChart.setExtraRightOffset(30);
//        opasAppSchChart.getAxisLeft().setDrawGridLines(true);
//        opasAppSchChart.getAxisLeft().setEnabled(false);
//        opasAppSchChart.setScaleEnabled(true);
//        opasAppSchChart.setTouchEnabled(true);
//        opasAppSchChart.setHighlightPerTapEnabled(true);
//        opasAppSchChart.setDoubleTapToZoomEnabled(false);
//
//        opasAppSchChart.setExtraOffsets(30, 0, 30, 20);
//
//        opasAppSchChart.getAxisRight().setEnabled(false);
//        opasAppSchChart.getAxisLeft().setAxisMinimum(0);
//        opasAppSchChart.getLegend().setEnabled(true);
//        opasAppSchChart.getLegend().setStackSpace(20);
//        opasAppSchChart.getLegend().setYOffset(10);
//        opasAppSchChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
//        opasAppSchChart.getAxisLeft().setTextColor(Color.GRAY);
//        opasAppSchChart.getAxisLeft().setXOffset(10f);
//        opasAppSchChart.getAxisLeft().setTextSize(6);
//        opasAppSchChart.getAxisLeft().setDrawGridLines(false);
//        opasAppSchChart.getXAxis().setLabelRotationAngle(45);
//    }
    public void LineChartInit(LineChart lineChart) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.GRAY);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(false);

        lineChart.setDrawGridBackground(false);
        lineChart.setExtraLeftOffset(30);
        lineChart.setExtraRightOffset(30);
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.setScaleEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.setDoubleTapToZoomEnabled(false);

        lineChart.setExtraOffsets(30, 0, 30, 20);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.getLegend().setEnabled(true);
        lineChart.getLegend().setStackSpace(20);
        lineChart.getLegend().setYOffset(10);
        lineChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        lineChart.getAxisLeft().setTextColor(Color.GRAY);
        lineChart.getAxisLeft().setXOffset(10f);
        lineChart.getAxisLeft().setTextSize(6);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setLabelRotationAngle(45);
    }

    public void getDashboardData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);

        appLoginFullLayout.setVisibility(View.GONE);
        appLoginChartCircularProgressIndicator.setVisibility(View.GONE);
        appLoginChartRefresh.setVisibility(View.GONE);

        opasLoginFullLayout.setVisibility(View.GONE);
        opasLoginChartCircularProgressIndicator.setVisibility(View.GONE);
        opasLoginChartRefresh.setVisibility(View.GONE);

        opasAppSchFullLayout.setVisibility(View.GONE);
        opasAppSchChartCircularProgressIndicator.setVisibility(View.GONE);
        opasAppSchChartRefresh.setVisibility(View.GONE);

        opasPayFullLayout.setVisibility(View.GONE);
        opasPayChartCircularProgressIndicator.setVisibility(View.GONE);
        opasPayChartRefresh.setVisibility(View.GONE);

        loading = true;
        conn = false;
        connected = false;

        appLoginChartLists = new ArrayList<>();
        total_unq_login = "0";
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineData data1 = new LineData(dataSets);
        appLoginChart.setData(data1);
        appLoginChart.setMarker(null);
        appLoginChart.getData().clearValues();
        appLoginChart.notifyDataSetChanged();
        appLoginChart.clear();
        appLoginChart.invalidate();
        appLoginChart.fitScreen();

        opasLoginChartLists = new ArrayList<>();
        total_opas_unq_login = "0";
        ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
        LineData data2 = new LineData(dataSets1);
        opasLoginChart.setData(data2);
        opasLoginChart.setMarker(null);
        opasLoginChart.getData().clearValues();
        opasLoginChart.notifyDataSetChanged();
        opasLoginChart.clear();
        opasLoginChart.invalidate();
        opasLoginChart.fitScreen();

        opasAppInitChartLists = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
        LineData data3 = new LineData(dataSets2);
        opasAppInitChart.setData(data3);
        opasAppInitChart.setMarker(null);
        opasAppInitChart.getData().clearValues();
        opasAppInitChart.notifyDataSetChanged();
        opasAppInitChart.clear();
        opasAppInitChart.invalidate();
        opasAppInitChart.fitScreen();

        opasAppSchChartLists = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
        LineData data4 = new LineData(dataSets3);
        opasAppSchChart.setData(data4);
        opasAppSchChart.setMarker(null);
        opasAppSchChart.getData().clearValues();
        opasAppSchChart.notifyDataSetChanged();
        opasAppSchChart.clear();
        opasAppSchChart.invalidate();
        opasAppSchChart.fitScreen();

        opasPayChartLists = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets4 = new ArrayList<>();
        LineData data5 = new LineData(dataSets4);
        opasPayChart.setData(data5);
        opasPayChart.setMarker(null);
        opasPayChart.getData().clearValues();
        opasPayChart.notifyDataSetChanged();
        opasPayChart.clear();
        opasPayChart.invalidate();
        opasPayChart.fitScreen();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        String date_now = dateFormat.format(Calendar.getInstance().getTime());
        appLoginChartFirstDate = date_now;
        appLoginChartLastDate = date_now;
        opasLoginChartFirstDate = date_now;
        opasLoginChartLastDate = date_now;
        opasAppSchFirstDate = date_now;
        opasAppSchLastDate = date_now;
        opasPayChartFirstDate = date_now;
        opasPayChartLastDate = date_now;

        String loginStatusUrl = pre_url_api+"analytic_dashboard/getAppLoginStatus?begin_date="+appLoginChartFirstDate+"&end_date="+appLoginChartLastDate;
        String loginTotUnqUrl = pre_url_api+"analytic_dashboard/getTotalUnqLogin?begin_date="+appLoginChartFirstDate+"&end_date="+appLoginChartLastDate+"&p_log_type=3";
        String opasStatusUrl = pre_url_api+"analytic_dashboard/getOPASLoginStatus?begin_date="+opasLoginChartFirstDate+"&end_date="+opasLoginChartLastDate;
        String loginTotOpasUnqUrl = pre_url_api+"analytic_dashboard/getTotalUnqLogin?begin_date="+opasLoginChartFirstDate+"&end_date="+opasLoginChartLastDate+"&p_log_type=1";
        String opasAppInitUrl = pre_url_api+"analytic_dashboard/getAppInitCountStatus?begin_date="+opasAppSchFirstDate+"&end_date="+opasAppSchLastDate;
        String opasAppSchUrl = pre_url_api+"analytic_dashboard/getAppSchCountStatus?begin_date="+opasAppSchFirstDate+"&end_date="+opasAppSchLastDate;
        String opasPayUrl = pre_url_api+"analytic_dashboard/getOPASPayStatus?begin_date="+opasPayChartFirstDate+"&end_date="+opasPayChartLastDate;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest opasPayReq = new StringRequest(Request.Method.GET, opasPayUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("all_dates")
                                .equals("null") ? "" : docInfo.getString("all_dates");
                        String ap_c = docInfo.getString("all_pay_count")
                                .equals("null") ? "0" : docInfo.getString("all_pay_count");
                        String cp_c = docInfo.getString("c_pay_count")
                                .equals("null") ? "0" : docInfo.getString("c_pay_count");
                        String fp_c = docInfo.getString("f_pay_count")
                                .equals("null") ? "0" : docInfo.getString("f_pay_count");
                        String np_c = docInfo.getString("na_pay_count")
                                .equals("null") ? "0" : docInfo.getString("na_pay_count");

                        opasPayChartLists.add(new OpasPayChartList(String.valueOf(i+1),dates,ap_c,cp_c,fp_c,np_c));

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

        StringRequest opasAppSchReq = new StringRequest(Request.Method.GET, opasAppSchUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("all_dates")
                                .equals("null") ? "" : docInfo.getString("all_dates");
                        String a_c = docInfo.getString("all_app_count")
                                .equals("null") ? "0" : docInfo.getString("all_app_count");
                        String c_c = docInfo.getString("c_app_count")
                                .equals("null") ? "0" : docInfo.getString("c_app_count");
                        String uc_c = docInfo.getString("uc_app_count")
                                .equals("null") ? "0" : docInfo.getString("uc_app_count");

                        opasAppSchChartLists.add(new OpasAppointChartList(String.valueOf(i+1),dates,a_c,c_c,uc_c));

                    }
                }

                requestQueue.add(opasPayReq);
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

        StringRequest opasAppInitReq = new StringRequest(Request.Method.GET, opasAppInitUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("all_dates")
                                .equals("null") ? "" : docInfo.getString("all_dates");
                        String a_c = docInfo.getString("all_app_count")
                                .equals("null") ? "0" : docInfo.getString("all_app_count");
                        String c_c = docInfo.getString("c_app_count")
                                .equals("null") ? "0" : docInfo.getString("c_app_count");
                        String uc_c = docInfo.getString("uc_app_count")
                                .equals("null") ? "0" : docInfo.getString("uc_app_count");

                        opasAppInitChartLists.add(new OpasAppointChartList(String.valueOf(i+1),dates,a_c,c_c,uc_c));

                    }
                }
                requestQueue.add(opasAppSchReq);
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

        StringRequest loginOpasUnqReq = new StringRequest(Request.Method.GET, loginTotOpasUnqUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        total_opas_unq_login = docInfo.getString("total_unq_amount")
                                .equals("null") ? "0" : docInfo.getString("total_unq_amount");

                    }
                }

                requestQueue.add(opasAppInitReq);
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

        StringRequest opasStatusReq = new StringRequest(Request.Method.GET, opasStatusUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("all_dates")
                                .equals("null") ? "" : docInfo.getString("all_dates");
                        String amount = docInfo.getString("all_amount")
                                .equals("null") ? "0" : docInfo.getString("all_amount");
                        String amount1 = docInfo.getString("u_amount")
                                .equals("null") ? "0" : docInfo.getString("u_amount");

                        opasLoginChartLists.add(new LoginChartList(String.valueOf(i+1),dates,amount,amount1));

                    }
                }

                requestQueue.add(loginOpasUnqReq);
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

        StringRequest loginTotUnqReq = new StringRequest(Request.Method.GET, loginTotUnqUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        total_unq_login = docInfo.getString("total_unq_amount")
                                .equals("null") ? "0" : docInfo.getString("total_unq_amount");
                    }
                }

                if (isPatOnlineActive) {
                    requestQueue.add(opasStatusReq);
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

        StringRequest loginStatusReq = new StringRequest(Request.Method.GET, loginStatusUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("all_dates")
                                .equals("null") ? "" : docInfo.getString("all_dates");
                        String amount = docInfo.getString("all_amount")
                                .equals("null") ? "0" : docInfo.getString("all_amount");
                        String amount1 = docInfo.getString("u_amount")
                                .equals("null") ? "0" : docInfo.getString("u_amount");

                        appLoginChartLists.add(new LoginChartList(String.valueOf(i+1),dates,amount,amount1));

                    }
                }

                requestQueue.add(loginTotUnqReq);
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

        requestQueue.add(loginStatusReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);

                appLoginFullLayout.setVisibility(View.VISIBLE);
                appLoginChartCircularProgressIndicator.setVisibility(View.GONE);
                appLoginChartRefresh.setVisibility(View.GONE);

                opasLoginFullLayout.setVisibility(View.VISIBLE);
                opasLoginChartCircularProgressIndicator.setVisibility(View.GONE);
                opasLoginChartRefresh.setVisibility(View.GONE);

                opasAppSchFullLayout.setVisibility(View.VISIBLE);
                opasAppSchChartCircularProgressIndicator.setVisibility(View.GONE);
                opasAppSchChartRefresh.setVisibility(View.GONE);

                opasPayFullLayout.setVisibility(View.VISIBLE);
                opasPayChartCircularProgressIndicator.setVisibility(View.GONE);
                opasPayChartRefresh.setVisibility(View.GONE);

                conn = false;
                connected = false;

                // App Login
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yy", Locale.ENGLISH);
                String date_now = dateFormat.format(Calendar.getInstance().getTime());
                dateRangeText.setText(date_now);

                //--- App login chart initialization ---

                NormalMarker mv = new NormalMarker(getApplicationContext(), R.layout.custom_marker_view);
                mv.setChartView(appLoginChart);
                appLoginChart.setMarker(mv);

                ArrayList<Entry> totalCountValue = new ArrayList<>();
                ArrayList<Entry> uniqueCountValue = new ArrayList<>();
                ArrayList<String> dateName = new ArrayList<>();

                int total_count = 0;
//                int unique_count = 0;

                if (appLoginChartLists.size() == 1) {
                    totalCountValue.add(new Entry(0,0, "0"));
                    uniqueCountValue.add(new Entry(0,0,"0"));
                    dateName.add("");

                    totalCountValue.add(new Entry(1,Float.parseFloat(appLoginChartLists.get(0).getTotCount()), appLoginChartLists.get(0).getId()));
                    uniqueCountValue.add(new Entry(1,Float.parseFloat(appLoginChartLists.get(0).getUniqueCount()),appLoginChartLists.get(0).getId()));
                    dateName.add(appLoginChartLists.get(0).getDateMonth());

                    totalCountValue.add(new Entry(2,0, "2"));
                    uniqueCountValue.add(new Entry(2,0,"2"));
                    dateName.add("");

                    if (!appLoginChartLists.get(0).getTotCount().isEmpty()) {
                        total_count = total_count + Integer.parseInt(appLoginChartLists.get(0).getTotCount());
                    }
//                    if (!appLoginChartLists.get(0).getUniqueCount().isEmpty()) {
//                        unique_count = unique_count + Integer.parseInt(appLoginChartLists.get(0).getUniqueCount());
//                    }
                }
                else {
                    for (int i = 0; i < appLoginChartLists.size(); i++) {
                        totalCountValue.add(new Entry(i,Float.parseFloat(appLoginChartLists.get(i).getTotCount()), appLoginChartLists.get(i).getId()));
                        uniqueCountValue.add(new Entry(i,Float.parseFloat(appLoginChartLists.get(i).getUniqueCount()),appLoginChartLists.get(i).getId()));
                        dateName.add(appLoginChartLists.get(i).getDateMonth());
                        if (!appLoginChartLists.get(i).getTotCount().isEmpty()) {
                            total_count = total_count + Integer.parseInt(appLoginChartLists.get(i).getTotCount());
                        }
//                        if (!appLoginChartLists.get(i).getUniqueCount().isEmpty()) {
//                            unique_count = unique_count + Integer.parseInt(appLoginChartLists.get(i).getUniqueCount());
//                        }
                    }
                }

                allLoginCount.setText(String.valueOf(total_count));
                unqLoginCount.setText(String.valueOf(total_unq_login));

                appLoginChart.animateXY(1000,1000);
                if (appLoginChartLists.size() > 3) {
                    appLoginChart.getXAxis().setLabelRotationAngle(45);
                }
                else {
                    appLoginChart.getXAxis().setLabelRotationAngle(0);
                }

                LineDataSet lineDataSet = new LineDataSet(totalCountValue,"Total Login Count");
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

                LineDataSet lineDataSet1 = new LineDataSet(uniqueCountValue,"Unique Login Count");
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

                appLoginChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateName));
                appLoginChart.setData(data1);
                appLoginChart.getData().setHighlightEnabled(true);
                appLoginChart.invalidate();

                // Opas Login
                if (isPatOnlineActive) {
                    SimpleDateFormat dateFormatOpas = new SimpleDateFormat("dd-MMMM-yy", Locale.ENGLISH);
                    String date_now_opas = dateFormatOpas.format(Calendar.getInstance().getTime());
                    opasDateRangeText.setText(date_now_opas);

                    //--- App login chart initialization ---
                    NormalMarker mvOpas = new NormalMarker(getApplicationContext(), R.layout.custom_marker_view);
                    mvOpas.setChartView(opasLoginChart);
                    opasLoginChart.setMarker(mvOpas);

                    ArrayList<Entry> totalCountValueOpas = new ArrayList<>();
                    ArrayList<Entry> uniqueCountValueOpas = new ArrayList<>();
                    ArrayList<String> dateNameOpas = new ArrayList<>();

                    int total_count_opas = 0;
//                    int unique_count_opas = 0;

                    if (opasLoginChartLists.size() == 1) {
                        totalCountValueOpas.add(new Entry(0, 0, "0"));
                        uniqueCountValueOpas.add(new Entry(0, 0, "0"));
                        dateNameOpas.add("");

                        totalCountValueOpas.add(new Entry(1, Float.parseFloat(opasLoginChartLists.get(0).getTotCount()), opasLoginChartLists.get(0).getId()));
                        uniqueCountValueOpas.add(new Entry(1, Float.parseFloat(opasLoginChartLists.get(0).getUniqueCount()), opasLoginChartLists.get(0).getId()));
                        dateNameOpas.add(opasLoginChartLists.get(0).getDateMonth());

                        totalCountValueOpas.add(new Entry(2, 0, "2"));
                        uniqueCountValueOpas.add(new Entry(2, 0, "2"));
                        dateNameOpas.add("");

                        if (!opasLoginChartLists.get(0).getTotCount().isEmpty()) {
                            total_count_opas = total_count_opas + Integer.parseInt(opasLoginChartLists.get(0).getTotCount());
                        }
//                        if (!opasLoginChartLists.get(0).getUniqueCount().isEmpty()) {
//                            unique_count_opas = unique_count_opas + Integer.parseInt(opasLoginChartLists.get(0).getUniqueCount());
//                        }
                    }
                    else {
                        for (int i = 0; i < opasLoginChartLists.size(); i++) {
                            totalCountValueOpas.add(new Entry(i, Float.parseFloat(opasLoginChartLists.get(i).getTotCount()), opasLoginChartLists.get(i).getId()));
                            uniqueCountValueOpas.add(new Entry(i, Float.parseFloat(opasLoginChartLists.get(i).getUniqueCount()), opasLoginChartLists.get(i).getId()));
                            dateNameOpas.add(opasLoginChartLists.get(i).getDateMonth());
                            if (!opasLoginChartLists.get(i).getTotCount().isEmpty()) {
                                total_count_opas = total_count_opas + Integer.parseInt(opasLoginChartLists.get(i).getTotCount());
                            }
//                            if (!opasLoginChartLists.get(i).getUniqueCount().isEmpty()) {
//                                unique_count_opas = unique_count_opas + Integer.parseInt(opasLoginChartLists.get(i).getUniqueCount());
//                            }
                        }
                    }

                    opasAllLoginCount.setText(String.valueOf(total_count_opas));
                    opasUnqLoginCount.setText(String.valueOf(total_opas_unq_login));

                    opasLoginChart.animateXY(1000, 1000);
                    if (opasLoginChartLists.size() > 3) {
                        opasLoginChart.getXAxis().setLabelRotationAngle(45);
                    } else {
                        opasLoginChart.getXAxis().setLabelRotationAngle(0);
                    }

                    LineDataSet lineDataSet_opas = new LineDataSet(totalCountValueOpas, "Total Login Count");
                    lineDataSet_opas.setValueFormatter(new LargeValueFormatter());
                    lineDataSet_opas.setCircleColor(getColor(R.color.light_green));
                    lineDataSet_opas.setCircleRadius(3f);
                    lineDataSet_opas.setLineWidth(2f);
                    lineDataSet_opas.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet_opas.setColor(getColor(R.color.green_sea));
                    lineDataSet_opas.setDrawFilled(false);
                    lineDataSet_opas.setValueTextSize(9f);

                    lineDataSet_opas.setDrawCircleHole(true);
                    lineDataSet_opas.setValueTextColor(R.color.default_text_color);
                    lineDataSet_opas.setValueTextSize(8f);

                    LineDataSet lineDataSet1_opas = new LineDataSet(uniqueCountValueOpas, "Unique Login Count");
                    lineDataSet1_opas.setValueFormatter(new LargeValueFormatter());
                    lineDataSet1_opas.setCircleColor(getColor(R.color.red_dark));
                    lineDataSet1_opas.setCircleRadius(3f);
                    lineDataSet1_opas.setLineWidth(2f);
                    lineDataSet1_opas.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet1_opas.setColor(getColor(R.color.blue_end));
                    lineDataSet1_opas.setDrawFilled(false);
                    lineDataSet1_opas.setValueTextSize(9f);
                    lineDataSet1_opas.setDrawCircleHole(true);
                    lineDataSet1_opas.setValueTextColor(R.color.default_text_color);
                    lineDataSet1_opas.setValueTextSize(8f);

                    ArrayList<ILineDataSet> dataSets_opas = new ArrayList<>();
                    dataSets_opas.add(lineDataSet_opas);
                    dataSets_opas.add(lineDataSet1_opas);

                    LineData data1_opas = new LineData(dataSets_opas);

                    opasLoginChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateNameOpas));
                    opasLoginChart.setData(data1_opas);
                    opasLoginChart.getData().setHighlightEnabled(true);
                    opasLoginChart.invalidate();

                    // -- opas App init
                    SimpleDateFormat dateFormatOpasApp = new SimpleDateFormat("dd-MMMM-yy", Locale.ENGLISH);
                    String date_now_opas_app = dateFormatOpasApp.format(Calendar.getInstance().getTime());
                    opasAppSchDateRangeText.setText(date_now_opas_app);

                    //--- App Initialization chart ---
                    NormalMarker mvOpasAppInit = new NormalMarker(getApplicationContext(), R.layout.custom_marker_view);
                    mvOpasAppInit.setChartView(opasAppInitChart);
                    opasAppInitChart.setMarker(mvOpasAppInit);

                    ArrayList<Entry> totalCountValueOpasAppInit = new ArrayList<>();
                    ArrayList<Entry> confirmCountValueOpasAppInit = new ArrayList<>();
                    ArrayList<Entry> unConfCountValueOpasAppInit = new ArrayList<>();
                    ArrayList<String> dateNameOpasAppInit = new ArrayList<>();

                    int total_count_opas_app_init = 0;
                    int confirm_count_opas_app_init = 0;
                    int unConf_count_opas_app_init = 0;

                    if (opasAppInitChartLists.size() == 1) {
                        totalCountValueOpasAppInit.add(new Entry(0, 0, "0"));
                        confirmCountValueOpasAppInit.add(new Entry(0, 0, "0"));
                        unConfCountValueOpasAppInit.add(new Entry(0, 0, "0"));
                        dateNameOpasAppInit.add("");

                        totalCountValueOpasAppInit.add(new Entry(1, Float.parseFloat(opasAppInitChartLists.get(0).getAll_app_count()), opasAppInitChartLists.get(0).getId()));
                        confirmCountValueOpasAppInit.add(new Entry(1, Float.parseFloat(opasAppInitChartLists.get(0).getC_app_count()), opasAppInitChartLists.get(0).getId()));
                        unConfCountValueOpasAppInit.add(new Entry(1, Float.parseFloat(opasAppInitChartLists.get(0).getUc_app_count()), opasAppInitChartLists.get(0).getId()));
                        dateNameOpasAppInit.add(opasAppInitChartLists.get(0).getAll_dates());

                        totalCountValueOpasAppInit.add(new Entry(2, 0, "2"));
                        confirmCountValueOpasAppInit.add(new Entry(2, 0, "2"));
                        unConfCountValueOpasAppInit.add(new Entry(2, 0, "2"));
                        dateNameOpasAppInit.add("");

                        if (!opasAppInitChartLists.get(0).getAll_app_count().isEmpty()) {
                            total_count_opas_app_init = total_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(0).getAll_app_count());
                        }
                        if (!opasAppInitChartLists.get(0).getC_app_count().isEmpty()) {
                            confirm_count_opas_app_init = confirm_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(0).getC_app_count());
                        }
                        if (!opasAppInitChartLists.get(0).getUc_app_count().isEmpty()) {
                            unConf_count_opas_app_init = unConf_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(0).getUc_app_count());
                        }
                    }
                    else {
                        for (int i = 0; i < opasAppInitChartLists.size(); i++) {
                            totalCountValueOpasAppInit.add(new Entry(i, Float.parseFloat(opasAppInitChartLists.get(i).getAll_app_count()), opasAppInitChartLists.get(i).getId()));
                            confirmCountValueOpasAppInit.add(new Entry(i, Float.parseFloat(opasAppInitChartLists.get(i).getC_app_count()), opasAppInitChartLists.get(i).getId()));
                            unConfCountValueOpasAppInit.add(new Entry(i, Float.parseFloat(opasAppInitChartLists.get(i).getUc_app_count()), opasAppInitChartLists.get(i).getId()));
                            dateNameOpasAppInit.add(opasAppInitChartLists.get(i).getAll_dates());

                            if (!opasAppInitChartLists.get(i).getAll_app_count().isEmpty()) {
                                total_count_opas_app_init = total_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(i).getAll_app_count());
                            }
                            if (!opasAppInitChartLists.get(i).getC_app_count().isEmpty()) {
                                confirm_count_opas_app_init = confirm_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(i).getC_app_count());
                            }
                            if (!opasAppInitChartLists.get(i).getUc_app_count().isEmpty()) {
                                unConf_count_opas_app_init = unConf_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(i).getUc_app_count());
                            }
                        }
                    }

                    opasAppInitCount.setText(String.valueOf(total_count_opas_app_init));
                    opasAppInitConfCount.setText(String.valueOf(confirm_count_opas_app_init));
                    opasAppInitUnConfCount.setText(String.valueOf(unConf_count_opas_app_init));

                    opasAppInitChart.animateXY(1000, 1000);
                    if (opasAppInitChartLists.size() > 3) {
                        opasAppInitChart.getXAxis().setLabelRotationAngle(45);
                    } else {
                        opasAppInitChart.getXAxis().setLabelRotationAngle(0);
                    }

                    LineDataSet lineDataSet_opas_app_init = new LineDataSet(totalCountValueOpasAppInit, "Total");
                    lineDataSet_opas_app_init.setValueFormatter(new LargeValueFormatter());
                    lineDataSet_opas_app_init.setCircleColor(getColor(R.color.light_green));
                    lineDataSet_opas_app_init.setCircleRadius(3f);
                    lineDataSet_opas_app_init.setLineWidth(2f);
                    lineDataSet_opas_app_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet_opas_app_init.setColor(getColor(R.color.green_sea));
                    lineDataSet_opas_app_init.setDrawFilled(false);
                    lineDataSet_opas_app_init.setValueTextSize(9f);
                    lineDataSet_opas_app_init.setDrawCircleHole(true);
                    lineDataSet_opas_app_init.setValueTextColor(R.color.default_text_color);
                    lineDataSet_opas_app_init.setValueTextSize(8f);

                    LineDataSet lineDataSet1_opas_app_init = new LineDataSet(confirmCountValueOpasAppInit, "Confirmed");
                    lineDataSet1_opas_app_init.setValueFormatter(new LargeValueFormatter());
                    lineDataSet1_opas_app_init.setCircleColor(getColor(R.color.red_dark));
                    lineDataSet1_opas_app_init.setCircleRadius(3f);
                    lineDataSet1_opas_app_init.setLineWidth(2f);
                    lineDataSet1_opas_app_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet1_opas_app_init.setColor(getColor(R.color.blue_end));
                    lineDataSet1_opas_app_init.setDrawFilled(false);
                    lineDataSet1_opas_app_init.setValueTextSize(9f);
                    lineDataSet1_opas_app_init.setDrawCircleHole(true);
                    lineDataSet1_opas_app_init.setValueTextColor(R.color.default_text_color);
                    lineDataSet1_opas_app_init.setValueTextSize(8f);

                    LineDataSet lineDataSet2_opas_app_init = new LineDataSet(unConfCountValueOpasAppInit, "Not Confirmed");
                    lineDataSet2_opas_app_init.setValueFormatter(new LargeValueFormatter());
                    lineDataSet2_opas_app_init.setCircleColor(getColor(R.color.disabled));
                    lineDataSet2_opas_app_init.setCircleRadius(3f);
                    lineDataSet2_opas_app_init.setLineWidth(2f);
                    lineDataSet2_opas_app_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet2_opas_app_init.setColor(getColor(R.color.leave_color));
                    lineDataSet2_opas_app_init.setDrawFilled(false);
                    lineDataSet2_opas_app_init.setValueTextSize(9f);
                    lineDataSet2_opas_app_init.setDrawCircleHole(true);
                    lineDataSet2_opas_app_init.setValueTextColor(R.color.default_text_color);
                    lineDataSet2_opas_app_init.setValueTextSize(8f);

                    ArrayList<ILineDataSet> dataSets_opas_app_init = new ArrayList<>();
                    dataSets_opas_app_init.add(lineDataSet_opas_app_init);
                    dataSets_opas_app_init.add(lineDataSet1_opas_app_init);
                    dataSets_opas_app_init.add(lineDataSet2_opas_app_init);

                    LineData data1_opas_app_init = new LineData(dataSets_opas_app_init);

                    opasAppInitChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateNameOpasAppInit));
                    opasAppInitChart.setData(data1_opas_app_init);
                    opasAppInitChart.getData().setHighlightEnabled(true);
                    opasAppInitChart.invalidate();

                    //--- App Schedule chart ---
                    NormalMarker mvOpasAppSch = new NormalMarker(getApplicationContext(), R.layout.custom_marker_view);
                    mvOpasAppSch.setChartView(opasAppSchChart);
                    opasAppSchChart.setMarker(mvOpasAppSch);

                    ArrayList<Entry> totalCountValueOpasAppSch = new ArrayList<>();
                    ArrayList<Entry> confirmCountValueOpasAppSch = new ArrayList<>();
                    ArrayList<Entry> unConfCountValueOpasAppSch = new ArrayList<>();
                    ArrayList<String> dateNameOpasAppSch = new ArrayList<>();

                    int total_count_opas_app_sch = 0;
                    int confirm_count_opas_app_sch = 0;
                    int unConf_count_opas_app_sch = 0;

                    if (opasAppSchChartLists.size() == 1) {
                        totalCountValueOpasAppSch.add(new Entry(0, 0, "0"));
                        confirmCountValueOpasAppSch.add(new Entry(0, 0, "0"));
                        unConfCountValueOpasAppSch.add(new Entry(0, 0, "0"));
                        dateNameOpasAppSch.add("");

                        totalCountValueOpasAppSch.add(new Entry(1, Float.parseFloat(opasAppSchChartLists.get(0).getAll_app_count()), opasAppSchChartLists.get(0).getId()));
                        confirmCountValueOpasAppSch.add(new Entry(1, Float.parseFloat(opasAppSchChartLists.get(0).getC_app_count()), opasAppSchChartLists.get(0).getId()));
                        unConfCountValueOpasAppSch.add(new Entry(1, Float.parseFloat(opasAppSchChartLists.get(0).getUc_app_count()), opasAppSchChartLists.get(0).getId()));
                        dateNameOpasAppSch.add(opasAppSchChartLists.get(0).getAll_dates());

                        totalCountValueOpasAppSch.add(new Entry(2, 0, "2"));
                        confirmCountValueOpasAppSch.add(new Entry(2, 0, "2"));
                        unConfCountValueOpasAppSch.add(new Entry(2, 0, "2"));
                        dateNameOpasAppSch.add("");

                        if (!opasAppSchChartLists.get(0).getAll_app_count().isEmpty()) {
                            total_count_opas_app_sch = total_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(0).getAll_app_count());
                        }
                        if (!opasAppSchChartLists.get(0).getC_app_count().isEmpty()) {
                            confirm_count_opas_app_sch = confirm_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(0).getC_app_count());
                        }
                        if (!opasAppSchChartLists.get(0).getUc_app_count().isEmpty()) {
                            unConf_count_opas_app_sch = unConf_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(0).getUc_app_count());
                        }
                    }
                    else {
                        for (int i = 0; i < opasAppSchChartLists.size(); i++) {
                            totalCountValueOpasAppSch.add(new Entry(i, Float.parseFloat(opasAppSchChartLists.get(i).getAll_app_count()), opasAppSchChartLists.get(i).getId()));
                            confirmCountValueOpasAppSch.add(new Entry(i, Float.parseFloat(opasAppSchChartLists.get(i).getC_app_count()), opasAppSchChartLists.get(i).getId()));
                            unConfCountValueOpasAppSch.add(new Entry(i, Float.parseFloat(opasAppSchChartLists.get(i).getUc_app_count()), opasAppSchChartLists.get(i).getId()));
                            dateNameOpasAppSch.add(opasAppSchChartLists.get(i).getAll_dates());

                            if (!opasAppSchChartLists.get(i).getAll_app_count().isEmpty()) {
                                total_count_opas_app_sch = total_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(i).getAll_app_count());
                            }
                            if (!opasAppSchChartLists.get(i).getC_app_count().isEmpty()) {
                                confirm_count_opas_app_sch = confirm_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(i).getC_app_count());
                            }
                            if (!opasAppSchChartLists.get(i).getUc_app_count().isEmpty()) {
                                unConf_count_opas_app_sch = unConf_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(i).getUc_app_count());
                            }
                        }
                    }

                    opasAppSchCount.setText(String.valueOf(total_count_opas_app_sch));
                    opasAppSchConfCount.setText(String.valueOf(confirm_count_opas_app_sch));
                    opasAppSchUnConfCount.setText(String.valueOf(unConf_count_opas_app_sch));

                    opasAppSchChart.animateXY(1000, 1000);
                    if (opasAppSchChartLists.size() > 3) {
                        opasAppSchChart.getXAxis().setLabelRotationAngle(45);
                    } else {
                        opasAppSchChart.getXAxis().setLabelRotationAngle(0);
                    }

                    LineDataSet lineDataSet_opas_app_sch = new LineDataSet(totalCountValueOpasAppSch, "Total");
                    lineDataSet_opas_app_sch.setValueFormatter(new LargeValueFormatter());
                    lineDataSet_opas_app_sch.setCircleColor(getColor(R.color.light_green));
                    lineDataSet_opas_app_sch.setCircleRadius(3f);
                    lineDataSet_opas_app_sch.setLineWidth(2f);
                    lineDataSet_opas_app_sch.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet_opas_app_sch.setColor(getColor(R.color.green_sea));
                    lineDataSet_opas_app_sch.setDrawFilled(false);
                    lineDataSet_opas_app_sch.setValueTextSize(9f);
                    lineDataSet_opas_app_sch.setDrawCircleHole(true);
                    lineDataSet_opas_app_sch.setValueTextColor(R.color.default_text_color);
                    lineDataSet_opas_app_sch.setValueTextSize(8f);

                    LineDataSet lineDataSet1_opas_app_sch = new LineDataSet(confirmCountValueOpasAppSch, "Confirmed");
                    lineDataSet1_opas_app_sch.setValueFormatter(new LargeValueFormatter());
                    lineDataSet1_opas_app_sch.setCircleColor(getColor(R.color.red_dark));
                    lineDataSet1_opas_app_sch.setCircleRadius(3f);
                    lineDataSet1_opas_app_sch.setLineWidth(2f);
                    lineDataSet1_opas_app_sch.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet1_opas_app_sch.setColor(getColor(R.color.blue_end));
                    lineDataSet1_opas_app_sch.setDrawFilled(false);
                    lineDataSet1_opas_app_sch.setValueTextSize(9f);
                    lineDataSet1_opas_app_sch.setDrawCircleHole(true);
                    lineDataSet1_opas_app_sch.setValueTextColor(R.color.default_text_color);
                    lineDataSet1_opas_app_sch.setValueTextSize(8f);

                    LineDataSet lineDataSet2_opas_app_sch = new LineDataSet(unConfCountValueOpasAppSch, "Not Confirmed");
                    lineDataSet2_opas_app_sch.setValueFormatter(new LargeValueFormatter());
                    lineDataSet2_opas_app_sch.setCircleColor(getColor(R.color.disabled));
                    lineDataSet2_opas_app_sch.setCircleRadius(3f);
                    lineDataSet2_opas_app_sch.setLineWidth(2f);
                    lineDataSet2_opas_app_sch.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet2_opas_app_sch.setColor(getColor(R.color.leave_color));
                    lineDataSet2_opas_app_sch.setDrawFilled(false);
                    lineDataSet2_opas_app_sch.setValueTextSize(9f);
                    lineDataSet2_opas_app_sch.setDrawCircleHole(true);
                    lineDataSet2_opas_app_sch.setValueTextColor(R.color.default_text_color);
                    lineDataSet2_opas_app_sch.setValueTextSize(8f);

                    ArrayList<ILineDataSet> dataSets_opas_app_sch = new ArrayList<>();
                    dataSets_opas_app_sch.add(lineDataSet_opas_app_sch);
                    dataSets_opas_app_sch.add(lineDataSet1_opas_app_sch);
                    dataSets_opas_app_sch.add(lineDataSet2_opas_app_sch);

                    LineData data1_opas_app_sch = new LineData(dataSets_opas_app_sch);

                    opasAppSchChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateNameOpasAppSch));
                    opasAppSchChart.setData(data1_opas_app_sch);
                    opasAppSchChart.getData().setHighlightEnabled(true);
                    opasAppSchChart.invalidate();

                    // -- opas Payment Status
                    SimpleDateFormat dateFormatOpasPay = new SimpleDateFormat("dd-MMMM-yy", Locale.ENGLISH);
                    String date_now_opas_pay = dateFormatOpasPay.format(Calendar.getInstance().getTime());
                    opasPayDateRangeText.setText(date_now_opas_pay);

                    //--- App Initialization chart ---
                    NormalMarker mvOpasPayInit = new NormalMarker(getApplicationContext(), R.layout.custom_marker_view);
                    mvOpasPayInit.setChartView(opasPayChart);
                    opasPayChart.setMarker(mvOpasPayInit);

                    ArrayList<Entry> totalCountValueOpasPayInit = new ArrayList<>();
                    ArrayList<Entry> compCountValueOpasPayInit = new ArrayList<>();
                    ArrayList<Entry> failCountValueOpasPayInit = new ArrayList<>();
                    ArrayList<Entry> nrCountValueOpasPayInit = new ArrayList<>();
                    ArrayList<String> dateNameOpasPayInit = new ArrayList<>();

                    int total_count_opas_pay_init = 0;
                    int comp_count_opas_pay_init = 0;
                    int fail_count_opas_pay_init = 0;
                    int nr_count_opas_pay_init = 0;

                    if (opasPayChartLists.size() == 1) {
                        totalCountValueOpasPayInit.add(new Entry(0, 0, "0"));
                        compCountValueOpasPayInit.add(new Entry(0, 0, "0"));
                        failCountValueOpasPayInit.add(new Entry(0, 0, "0"));
                        nrCountValueOpasPayInit.add(new Entry(0, 0, "0"));
                        dateNameOpasPayInit.add("");

                        totalCountValueOpasPayInit.add(new Entry(1, Float.parseFloat(opasPayChartLists.get(0).getAll_pay_count()), opasPayChartLists.get(0).getId()));
                        compCountValueOpasPayInit.add(new Entry(1, Float.parseFloat(opasPayChartLists.get(0).getC_pay_count()), opasPayChartLists.get(0).getId()));
                        failCountValueOpasPayInit.add(new Entry(1, Float.parseFloat(opasPayChartLists.get(0).getF_pay_count()), opasPayChartLists.get(0).getId()));
                        nrCountValueOpasPayInit.add(new Entry(1, Float.parseFloat(opasPayChartLists.get(0).getNa_pay_count()), opasPayChartLists.get(0).getId()));
                        dateNameOpasPayInit.add(opasPayChartLists.get(0).getAll_dates());

                        totalCountValueOpasPayInit.add(new Entry(2, 0, "2"));
                        compCountValueOpasPayInit.add(new Entry(2, 0, "2"));
                        failCountValueOpasPayInit.add(new Entry(2, 0, "2"));
                        nrCountValueOpasPayInit.add(new Entry(2, 0, "2"));
                        dateNameOpasPayInit.add("");

                        if (!opasPayChartLists.get(0).getAll_pay_count().isEmpty()) {
                            total_count_opas_pay_init = total_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(0).getAll_pay_count());
                        }
                        if (!opasPayChartLists.get(0).getC_pay_count().isEmpty()) {
                            comp_count_opas_pay_init = comp_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(0).getC_pay_count());
                        }
                        if (!opasPayChartLists.get(0).getF_pay_count().isEmpty()) {
                            fail_count_opas_pay_init = fail_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(0).getF_pay_count());
                        }
                        if (!opasPayChartLists.get(0).getNa_pay_count().isEmpty()) {
                            nr_count_opas_pay_init = nr_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(0).getNa_pay_count());
                        }
                    }
                    else {
                        for (int i = 0; i < opasPayChartLists.size(); i++) {
                            totalCountValueOpasPayInit.add(new Entry(i, Float.parseFloat(opasPayChartLists.get(i).getAll_pay_count()), opasPayChartLists.get(i).getId()));
                            compCountValueOpasPayInit.add(new Entry(i, Float.parseFloat(opasPayChartLists.get(i).getC_pay_count()), opasPayChartLists.get(i).getId()));
                            failCountValueOpasPayInit.add(new Entry(i, Float.parseFloat(opasPayChartLists.get(i).getF_pay_count()), opasPayChartLists.get(i).getId()));
                            nrCountValueOpasPayInit.add(new Entry(i, Float.parseFloat(opasPayChartLists.get(i).getNa_pay_count()), opasPayChartLists.get(i).getId()));
                            dateNameOpasPayInit.add(opasPayChartLists.get(i).getAll_dates());

                            if (!opasPayChartLists.get(i).getAll_pay_count().isEmpty()) {
                                total_count_opas_pay_init = total_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(i).getAll_pay_count());
                            }
                            if (!opasPayChartLists.get(i).getC_pay_count().isEmpty()) {
                                comp_count_opas_pay_init = comp_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(i).getC_pay_count());
                            }
                            if (!opasPayChartLists.get(i).getF_pay_count().isEmpty()) {
                                fail_count_opas_pay_init = fail_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(i).getF_pay_count());
                            }
                            if (!opasPayChartLists.get(i).getNa_pay_count().isEmpty()) {
                                nr_count_opas_pay_init = nr_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(i).getNa_pay_count());
                            }
                        }
                    }

                    opasPayTotalCount.setText(String.valueOf(total_count_opas_pay_init));
                    opasPayCompCount.setText(String.valueOf(comp_count_opas_pay_init));
                    opasPayFailCount.setText(String.valueOf(fail_count_opas_pay_init));
                    opasPayNACount.setText(String.valueOf(nr_count_opas_pay_init));

                    opasPayChart.animateXY(1000, 1000);
                    if (opasPayChartLists.size() > 3) {
                        opasPayChart.getXAxis().setLabelRotationAngle(45);
                    } else {
                        opasPayChart.getXAxis().setLabelRotationAngle(0);
                    }

                    LineDataSet lineDataSet_opas_pay_init = new LineDataSet(totalCountValueOpasPayInit, "Total");
                    lineDataSet_opas_pay_init.setValueFormatter(new LargeValueFormatter());
                    lineDataSet_opas_pay_init.setCircleColor(getColor(R.color.pres_on_leave));
                    lineDataSet_opas_pay_init.setCircleRadius(3f);
                    lineDataSet_opas_pay_init.setLineWidth(2f);
                    lineDataSet_opas_pay_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet_opas_pay_init.setColor(getColor(R.color.naval_blue));
                    lineDataSet_opas_pay_init.setDrawFilled(false);
                    lineDataSet_opas_pay_init.setValueTextSize(9f);
                    lineDataSet_opas_pay_init.setDrawCircleHole(true);
                    lineDataSet_opas_pay_init.setValueTextColor(R.color.default_text_color);
                    lineDataSet_opas_pay_init.setValueTextSize(8f);

                    LineDataSet lineDataSet1_opas_pay_init = new LineDataSet(compCountValueOpasPayInit, "Completed");
                    lineDataSet1_opas_pay_init.setValueFormatter(new LargeValueFormatter());
                    lineDataSet1_opas_pay_init.setCircleColor(getColor(R.color.light_green));
                    lineDataSet1_opas_pay_init.setCircleRadius(3f);
                    lineDataSet1_opas_pay_init.setLineWidth(2f);
                    lineDataSet1_opas_pay_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet1_opas_pay_init.setColor(getColor(R.color.green_sea));
                    lineDataSet1_opas_pay_init.setDrawFilled(false);
                    lineDataSet1_opas_pay_init.setValueTextSize(9f);
                    lineDataSet1_opas_pay_init.setDrawCircleHole(true);
                    lineDataSet1_opas_pay_init.setValueTextColor(R.color.default_text_color);
                    lineDataSet1_opas_pay_init.setValueTextSize(8f);

                    LineDataSet lineDataSet2_opas_pay_init = new LineDataSet(failCountValueOpasPayInit, "Failed");
                    lineDataSet2_opas_pay_init.setValueFormatter(new LargeValueFormatter());
                    lineDataSet2_opas_pay_init.setCircleColor(getColor(R.color.light_rose));
                    lineDataSet2_opas_pay_init.setCircleRadius(3f);
                    lineDataSet2_opas_pay_init.setLineWidth(2f);
                    lineDataSet2_opas_pay_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet2_opas_pay_init.setColor(getColor(R.color.red_dark));
                    lineDataSet2_opas_pay_init.setDrawFilled(false);
                    lineDataSet2_opas_pay_init.setValueTextSize(9f);
                    lineDataSet2_opas_pay_init.setDrawCircleHole(true);
                    lineDataSet2_opas_pay_init.setValueTextColor(R.color.default_text_color);
                    lineDataSet2_opas_pay_init.setValueTextSize(8f);

                    LineDataSet lineDataSet3_opas_pay_init = new LineDataSet(nrCountValueOpasPayInit, "No Result");
                    lineDataSet3_opas_pay_init.setValueFormatter(new LargeValueFormatter());
                    lineDataSet3_opas_pay_init.setCircleColor(getColor(R.color.disabled));
                    lineDataSet3_opas_pay_init.setCircleRadius(3f);
                    lineDataSet3_opas_pay_init.setLineWidth(2f);
                    lineDataSet3_opas_pay_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    lineDataSet3_opas_pay_init.setColor(getColor(R.color.leave_color));
                    lineDataSet3_opas_pay_init.setDrawFilled(false);
                    lineDataSet3_opas_pay_init.setValueTextSize(9f);
                    lineDataSet3_opas_pay_init.setDrawCircleHole(true);
                    lineDataSet3_opas_pay_init.setValueTextColor(R.color.default_text_color);
                    lineDataSet3_opas_pay_init.setValueTextSize(8f);

                    ArrayList<ILineDataSet> dataSets_opas_pay_init = new ArrayList<>();
                    dataSets_opas_pay_init.add(lineDataSet_opas_pay_init);
                    dataSets_opas_pay_init.add(lineDataSet1_opas_pay_init);
                    dataSets_opas_pay_init.add(lineDataSet2_opas_pay_init);
                    dataSets_opas_pay_init.add(lineDataSet3_opas_pay_init);

                    LineData data1_opas_pay_init = new LineData(dataSets_opas_pay_init);

                    opasPayChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateNameOpasPayInit));
                    opasPayChart.setData(data1_opas_pay_init);
                    opasPayChart.getData().setHighlightEnabled(true);
                    opasPayChart.invalidate();
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

    public void alertMessage() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        appLoginFullLayout.setVisibility(View.GONE);
        appLoginChartCircularProgressIndicator.setVisibility(View.GONE);
        appLoginChartRefresh.setVisibility(View.GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AnalyticsDashboard.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getDashboardData();
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

    public void getAppLoginChart() {
        appLoginFullLayout.setVisibility(View.GONE);
        appLoginChartCircularProgressIndicator.setVisibility(View.VISIBLE);
        appLoginChartRefresh.setVisibility(View.GONE);
        loading = true;
        conn = false;
        connected = false;

        appLoginChartLists = new ArrayList<>();
        total_unq_login = "0";
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        LineData data1 = new LineData(dataSets);
        appLoginChart.setData(data1);
        appLoginChart.setMarker(null);
        appLoginChart.getData().clearValues();
        appLoginChart.notifyDataSetChanged();
        appLoginChart.clear();
        appLoginChart.invalidate();
        appLoginChart.fitScreen();

        String loginStatusUrl = pre_url_api+"analytic_dashboard/getAppLoginStatus?begin_date="+appLoginChartFirstDate+"&end_date="+appLoginChartLastDate;
        String loginTotUnqUrl = pre_url_api+"analytic_dashboard/getTotalUnqLogin?begin_date="+appLoginChartFirstDate+"&end_date="+appLoginChartLastDate+"&p_log_type=3";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest loginTotUnqReq = new StringRequest(Request.Method.GET, loginTotUnqUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        total_unq_login = docInfo.getString("total_unq_amount")
                                .equals("null") ? "0" : docInfo.getString("total_unq_amount");

                    }
                }

                connected = true;
                updateAppLoginLayout();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAppLoginLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAppLoginLayout();
        });

        StringRequest loginStatusReq = new StringRequest(Request.Method.GET, loginStatusUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("all_dates")
                                .equals("null") ? "" : docInfo.getString("all_dates");
                        String amount = docInfo.getString("all_amount")
                                .equals("null") ? "0" : docInfo.getString("all_amount");
                        String amount1 = docInfo.getString("u_amount")
                                .equals("null") ? "0" : docInfo.getString("u_amount");

                        appLoginChartLists.add(new LoginChartList(String.valueOf(i+1),dates,amount,amount1));

                    }
                }

                requestQueue.add(loginTotUnqReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAppLoginLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAppLoginLayout();
        });

        requestQueue.add(loginStatusReq);
    }

    private void updateAppLoginLayout() {
        if (conn) {
            if (connected) {
                appLoginFullLayout.setVisibility(View.VISIBLE);
                appLoginChartCircularProgressIndicator.setVisibility(View.GONE);
                appLoginChartRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;

                NormalMarker mv = new NormalMarker(getApplicationContext(), R.layout.custom_marker_view);
                mv.setChartView(appLoginChart);
                appLoginChart.setMarker(mv);

                ArrayList<Entry> totalCountValue = new ArrayList<>();
                ArrayList<Entry> uniqueCountValue = new ArrayList<>();
                ArrayList<String> dateName = new ArrayList<>();

                int total_count = 0;
//                int unique_count = 0;

                if (appLoginChartLists.size() == 1) {
                    totalCountValue.add(new Entry(0,0, "0"));
                    uniqueCountValue.add(new Entry(0,0,"0"));
                    dateName.add("");

                    totalCountValue.add(new Entry(1,Float.parseFloat(appLoginChartLists.get(0).getTotCount()), appLoginChartLists.get(0).getId()));
                    uniqueCountValue.add(new Entry(1,Float.parseFloat(appLoginChartLists.get(0).getUniqueCount()),appLoginChartLists.get(0).getId()));
                    dateName.add(appLoginChartLists.get(0).getDateMonth());

                    totalCountValue.add(new Entry(2,0, "2"));
                    uniqueCountValue.add(new Entry(2,0,"2"));
                    dateName.add("");

                    if (!appLoginChartLists.get(0).getTotCount().isEmpty()) {
                        total_count = total_count + Integer.parseInt(appLoginChartLists.get(0).getTotCount());
                    }
//                    if (!appLoginChartLists.get(0).getUniqueCount().isEmpty()) {
//                        unique_count = unique_count + Integer.parseInt(appLoginChartLists.get(0).getUniqueCount());
//                    }
                }
                else {
                    for (int i = 0; i < appLoginChartLists.size(); i++) {
                        totalCountValue.add(new Entry(i,Float.parseFloat(appLoginChartLists.get(i).getTotCount()), appLoginChartLists.get(i).getId()));
                        uniqueCountValue.add(new Entry(i,Float.parseFloat(appLoginChartLists.get(i).getUniqueCount()),appLoginChartLists.get(i).getId()));
                        dateName.add(appLoginChartLists.get(i).getDateMonth());
                        if (!appLoginChartLists.get(i).getTotCount().isEmpty()) {
                            total_count = total_count + Integer.parseInt(appLoginChartLists.get(i).getTotCount());
                        }
//                        if (!appLoginChartLists.get(i).getUniqueCount().isEmpty()) {
//                            unique_count = unique_count + Integer.parseInt(appLoginChartLists.get(i).getUniqueCount());
//                        }
                    }
                }

                allLoginCount.setText(String.valueOf(total_count));
                unqLoginCount.setText(String.valueOf(total_unq_login));

                appLoginChart.animateXY(1000,1000);
                if (appLoginChartLists.size() > 3) {
                    appLoginChart.getXAxis().setLabelRotationAngle(45);
                }
                else {
                    appLoginChart.getXAxis().setLabelRotationAngle(0);
                }

                LineDataSet lineDataSet = new LineDataSet(totalCountValue,"Total Login Count");
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

                LineDataSet lineDataSet1 = new LineDataSet(uniqueCountValue,"Unique Login Count");
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

                appLoginChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateName));
                appLoginChart.setData(data1);
                appLoginChart.getData().setHighlightEnabled(true);
                appLoginChart.invalidate();

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
        appLoginFullLayout.setVisibility(View.GONE);
        appLoginChartCircularProgressIndicator.setVisibility(View.GONE);
        appLoginChartRefresh.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AnalyticsDashboard.this);
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

    public void getOpasLoginChart() {
        opasLoginFullLayout.setVisibility(View.GONE);
        opasLoginChartCircularProgressIndicator.setVisibility(View.VISIBLE);
        opasLoginChartRefresh.setVisibility(View.GONE);
        loading = true;
        conn = false;
        connected = false;

        opasLoginChartLists = new ArrayList<>();
        total_opas_unq_login = "0";
        ArrayList<ILineDataSet> dataSets1 = new ArrayList<>();
        LineData data2 = new LineData(dataSets1);
        opasLoginChart.setData(data2);
        opasLoginChart.setMarker(null);
        opasLoginChart.getData().clearValues();
        opasLoginChart.notifyDataSetChanged();
        opasLoginChart.clear();
        opasLoginChart.invalidate();
        opasLoginChart.fitScreen();

        String opasStatusUrl = pre_url_api+"analytic_dashboard/getOPASLoginStatus?begin_date="+opasLoginChartFirstDate+"&end_date="+opasLoginChartLastDate;
        String loginTotOpasUnqUrl = pre_url_api+"analytic_dashboard/getTotalUnqLogin?begin_date="+opasLoginChartFirstDate+"&end_date="+opasLoginChartLastDate+"&p_log_type=1";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest loginOpasUnqReq = new StringRequest(Request.Method.GET, loginTotOpasUnqUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        total_opas_unq_login = docInfo.getString("total_unq_amount")
                                .equals("null") ? "0" : docInfo.getString("total_unq_amount");

                    }
                }

                connected = true;
                updateOpasLoginLayout();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateOpasLoginLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateOpasLoginLayout();
        });

        StringRequest opasStatusReq = new StringRequest(Request.Method.GET, opasStatusUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("all_dates")
                                .equals("null") ? "" : docInfo.getString("all_dates");
                        String amount = docInfo.getString("all_amount")
                                .equals("null") ? "0" : docInfo.getString("all_amount");
                        String amount1 = docInfo.getString("u_amount")
                                .equals("null") ? "0" : docInfo.getString("u_amount");

                        opasLoginChartLists.add(new LoginChartList(String.valueOf(i+1),dates,amount,amount1));

                    }
                }

                requestQueue.add(loginOpasUnqReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateOpasLoginLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateOpasLoginLayout();
        });

        requestQueue.add(opasStatusReq);
    }

    private void updateOpasLoginLayout() {
        if (conn) {
            if (connected) {
                opasLoginFullLayout.setVisibility(View.VISIBLE);
                opasLoginChartCircularProgressIndicator.setVisibility(View.GONE);
                opasLoginChartRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;

                NormalMarker mvOpas = new NormalMarker(getApplicationContext(), R.layout.custom_marker_view);
                mvOpas.setChartView(opasLoginChart);
                opasLoginChart.setMarker(mvOpas);

                ArrayList<Entry> totalCountValueOpas = new ArrayList<>();
                ArrayList<Entry> uniqueCountValueOpas = new ArrayList<>();
                ArrayList<String> dateNameOpas = new ArrayList<>();

                int total_count_opas = 0;
//                int unique_count_opas = 0;

                if (opasLoginChartLists.size() == 1) {
                    totalCountValueOpas.add(new Entry(0,0, "0"));
                    uniqueCountValueOpas.add(new Entry(0,0,"0"));
                    dateNameOpas.add("");

                    totalCountValueOpas.add(new Entry(1,Float.parseFloat(opasLoginChartLists.get(0).getTotCount()), opasLoginChartLists.get(0).getId()));
                    uniqueCountValueOpas.add(new Entry(1,Float.parseFloat(opasLoginChartLists.get(0).getUniqueCount()),opasLoginChartLists.get(0).getId()));
                    dateNameOpas.add(opasLoginChartLists.get(0).getDateMonth());

                    totalCountValueOpas.add(new Entry(2,0, "2"));
                    uniqueCountValueOpas.add(new Entry(2,0,"2"));
                    dateNameOpas.add("");

                    if (!opasLoginChartLists.get(0).getTotCount().isEmpty()) {
                        total_count_opas = total_count_opas + Integer.parseInt(opasLoginChartLists.get(0).getTotCount());
                    }
//                    if (!opasLoginChartLists.get(0).getUniqueCount().isEmpty()) {
//                        unique_count_opas = unique_count_opas + Integer.parseInt(opasLoginChartLists.get(0).getUniqueCount());
//                    }
                }
                else {
                    for (int i = 0; i < opasLoginChartLists.size(); i++) {
                        totalCountValueOpas.add(new Entry(i,Float.parseFloat(opasLoginChartLists.get(i).getTotCount()), opasLoginChartLists.get(i).getId()));
                        uniqueCountValueOpas.add(new Entry(i,Float.parseFloat(opasLoginChartLists.get(i).getUniqueCount()),opasLoginChartLists.get(i).getId()));
                        dateNameOpas.add(opasLoginChartLists.get(i).getDateMonth());
                        if (!opasLoginChartLists.get(i).getTotCount().isEmpty()) {
                            total_count_opas = total_count_opas + Integer.parseInt(opasLoginChartLists.get(i).getTotCount());
                        }
//                        if (!opasLoginChartLists.get(i).getUniqueCount().isEmpty()) {
//                            unique_count_opas = unique_count_opas + Integer.parseInt(opasLoginChartLists.get(i).getUniqueCount());
//                        }
                    }
                }

                opasAllLoginCount.setText(String.valueOf(total_count_opas));
                opasUnqLoginCount.setText(String.valueOf(total_opas_unq_login));

                opasLoginChart.animateXY(1000,1000);
                if (opasLoginChartLists.size() > 3) {
                    opasLoginChart.getXAxis().setLabelRotationAngle(45);
                }
                else {
                    opasLoginChart.getXAxis().setLabelRotationAngle(0);
                }

                LineDataSet lineDataSet_opas = new LineDataSet(totalCountValueOpas,"Total Login Count");
                lineDataSet_opas.setValueFormatter(new LargeValueFormatter());
                lineDataSet_opas.setCircleColor(getColor(R.color.light_green));
                lineDataSet_opas.setCircleRadius(3f);
                lineDataSet_opas.setLineWidth(2f);
                lineDataSet_opas.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet_opas.setColor(getColor(R.color.green_sea));
                lineDataSet_opas.setDrawFilled(false);
                lineDataSet_opas.setValueTextSize(9f);

                lineDataSet_opas.setDrawCircleHole(true);
                lineDataSet_opas.setValueTextColor(R.color.default_text_color);
                lineDataSet_opas.setValueTextSize(8f);

                LineDataSet lineDataSet1_opas = new LineDataSet(uniqueCountValueOpas,"Unique Login Count");
                lineDataSet1_opas.setValueFormatter(new LargeValueFormatter());
                lineDataSet1_opas.setCircleColor(getColor(R.color.red_dark));
                lineDataSet1_opas.setCircleRadius(3f);
                lineDataSet1_opas.setLineWidth(2f);
                lineDataSet1_opas.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet1_opas.setColor(getColor(R.color.blue_end));
                lineDataSet1_opas.setDrawFilled(false);
                lineDataSet1_opas.setValueTextSize(9f);
                lineDataSet1_opas.setDrawCircleHole(true);
                lineDataSet1_opas.setValueTextColor(R.color.default_text_color);
                lineDataSet1_opas.setValueTextSize(8f);

                ArrayList<ILineDataSet> dataSets_opas = new ArrayList<>();
                dataSets_opas.add(lineDataSet_opas);
                dataSets_opas.add(lineDataSet1_opas);

                LineData data1_opas = new LineData(dataSets_opas);

                opasLoginChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateNameOpas));
                opasLoginChart.setData(data1_opas);
                opasLoginChart.getData().setHighlightEnabled(true);
                opasLoginChart.invalidate();

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
        opasLoginFullLayout.setVisibility(View.GONE);
        opasLoginChartCircularProgressIndicator.setVisibility(View.GONE);
        opasLoginChartRefresh.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AnalyticsDashboard.this);
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

    public void getOpasAppChart() {
        opasAppSchFullLayout.setVisibility(View.GONE);
        opasAppSchChartCircularProgressIndicator.setVisibility(View.VISIBLE);
        opasAppSchChartRefresh.setVisibility(View.GONE);
        loading = true;
        conn = false;
        connected = false;

        opasAppInitChartLists = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
        LineData data3 = new LineData(dataSets2);
        opasAppInitChart.setData(data3);
        opasAppInitChart.setMarker(null);
        opasAppInitChart.getData().clearValues();
        opasAppInitChart.notifyDataSetChanged();
        opasAppInitChart.clear();
        opasAppInitChart.invalidate();
        opasAppInitChart.fitScreen();

        opasAppSchChartLists = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets3 = new ArrayList<>();
        LineData data4 = new LineData(dataSets3);
        opasAppSchChart.setData(data4);
        opasAppSchChart.setMarker(null);
        opasAppSchChart.getData().clearValues();
        opasAppSchChart.notifyDataSetChanged();
        opasAppSchChart.clear();
        opasAppSchChart.invalidate();
        opasAppSchChart.fitScreen();

        String opasAppInitUrl = pre_url_api+"analytic_dashboard/getAppInitCountStatus?begin_date="+opasAppSchFirstDate+"&end_date="+opasAppSchLastDate;
        String opasAppSchUrl = pre_url_api+"analytic_dashboard/getAppSchCountStatus?begin_date="+opasAppSchFirstDate+"&end_date="+opasAppSchLastDate;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest opasAppSchReq = new StringRequest(Request.Method.GET, opasAppSchUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("all_dates")
                                .equals("null") ? "" : docInfo.getString("all_dates");
                        String a_c = docInfo.getString("all_app_count")
                                .equals("null") ? "0" : docInfo.getString("all_app_count");
                        String c_c = docInfo.getString("c_app_count")
                                .equals("null") ? "0" : docInfo.getString("c_app_count");
                        String uc_c = docInfo.getString("uc_app_count")
                                .equals("null") ? "0" : docInfo.getString("uc_app_count");

                        opasAppSchChartLists.add(new OpasAppointChartList(String.valueOf(i+1),dates,a_c,c_c,uc_c));

                    }
                }

                connected = true;
                updateOpasAppLayout();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateOpasAppLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateOpasAppLayout();
        });

        StringRequest opasAppInitReq = new StringRequest(Request.Method.GET, opasAppInitUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("all_dates")
                                .equals("null") ? "" : docInfo.getString("all_dates");
                        String a_c = docInfo.getString("all_app_count")
                                .equals("null") ? "0" : docInfo.getString("all_app_count");
                        String c_c = docInfo.getString("c_app_count")
                                .equals("null") ? "0" : docInfo.getString("c_app_count");
                        String uc_c = docInfo.getString("uc_app_count")
                                .equals("null") ? "0" : docInfo.getString("uc_app_count");

                        opasAppInitChartLists.add(new OpasAppointChartList(String.valueOf(i+1),dates,a_c,c_c,uc_c));

                    }
                }
                requestQueue.add(opasAppSchReq);
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateOpasAppLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateOpasAppLayout();
        });

        requestQueue.add(opasAppInitReq);
    }

    private void updateOpasAppLayout() {
        if (conn) {
            if (connected) {
                opasAppSchFullLayout.setVisibility(View.VISIBLE);
                opasAppSchChartCircularProgressIndicator.setVisibility(View.GONE);
                opasAppSchChartRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;

                // -- Opas App Initialization chart
                NormalMarker mvOpasAppInit = new NormalMarker(getApplicationContext(), R.layout.custom_marker_view);
                mvOpasAppInit.setChartView(opasAppInitChart);
                opasAppInitChart.setMarker(mvOpasAppInit);

                ArrayList<Entry> totalCountValueOpasAppInit = new ArrayList<>();
                ArrayList<Entry> confirmCountValueOpasAppInit = new ArrayList<>();
                ArrayList<Entry> unConfCountValueOpasAppInit = new ArrayList<>();
                ArrayList<String> dateNameOpasAppInit = new ArrayList<>();

                int total_count_opas_app_init = 0;
                int confirm_count_opas_app_init = 0;
                int unConf_count_opas_app_init = 0;

                if (opasAppInitChartLists.size() == 1) {
                    totalCountValueOpasAppInit.add(new Entry(0, 0, "0"));
                    confirmCountValueOpasAppInit.add(new Entry(0, 0, "0"));
                    unConfCountValueOpasAppInit.add(new Entry(0, 0, "0"));
                    dateNameOpasAppInit.add("");

                    totalCountValueOpasAppInit.add(new Entry(1, Float.parseFloat(opasAppInitChartLists.get(0).getAll_app_count()), opasAppInitChartLists.get(0).getId()));
                    confirmCountValueOpasAppInit.add(new Entry(1, Float.parseFloat(opasAppInitChartLists.get(0).getC_app_count()), opasAppInitChartLists.get(0).getId()));
                    unConfCountValueOpasAppInit.add(new Entry(1, Float.parseFloat(opasAppInitChartLists.get(0).getUc_app_count()), opasAppInitChartLists.get(0).getId()));
                    dateNameOpasAppInit.add(opasAppInitChartLists.get(0).getAll_dates());

                    totalCountValueOpasAppInit.add(new Entry(2, 0, "2"));
                    confirmCountValueOpasAppInit.add(new Entry(2, 0, "2"));
                    unConfCountValueOpasAppInit.add(new Entry(2, 0, "2"));
                    dateNameOpasAppInit.add("");

                    if (!opasAppInitChartLists.get(0).getAll_app_count().isEmpty()) {
                        total_count_opas_app_init = total_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(0).getAll_app_count());
                    }
                    if (!opasAppInitChartLists.get(0).getC_app_count().isEmpty()) {
                        confirm_count_opas_app_init = confirm_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(0).getC_app_count());
                    }
                    if (!opasAppInitChartLists.get(0).getUc_app_count().isEmpty()) {
                        unConf_count_opas_app_init = unConf_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(0).getUc_app_count());
                    }
                }
                else {
                    for (int i = 0; i < opasAppInitChartLists.size(); i++) {
                        totalCountValueOpasAppInit.add(new Entry(i, Float.parseFloat(opasAppInitChartLists.get(i).getAll_app_count()), opasAppInitChartLists.get(i).getId()));
                        confirmCountValueOpasAppInit.add(new Entry(i, Float.parseFloat(opasAppInitChartLists.get(i).getC_app_count()), opasAppInitChartLists.get(i).getId()));
                        unConfCountValueOpasAppInit.add(new Entry(i, Float.parseFloat(opasAppInitChartLists.get(i).getUc_app_count()), opasAppInitChartLists.get(i).getId()));
                        dateNameOpasAppInit.add(opasAppInitChartLists.get(i).getAll_dates());

                        if (!opasAppInitChartLists.get(i).getAll_app_count().isEmpty()) {
                            total_count_opas_app_init = total_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(i).getAll_app_count());
                        }
                        if (!opasAppInitChartLists.get(i).getC_app_count().isEmpty()) {
                            confirm_count_opas_app_init = confirm_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(i).getC_app_count());
                        }
                        if (!opasAppInitChartLists.get(i).getUc_app_count().isEmpty()) {
                            unConf_count_opas_app_init = unConf_count_opas_app_init + Integer.parseInt(opasAppInitChartLists.get(i).getUc_app_count());
                        }
                    }
                }

                opasAppInitCount.setText(String.valueOf(total_count_opas_app_init));
                opasAppInitConfCount.setText(String.valueOf(confirm_count_opas_app_init));
                opasAppInitUnConfCount.setText(String.valueOf(unConf_count_opas_app_init));

                opasAppInitChart.animateXY(1000, 1000);
                if (opasAppInitChartLists.size() > 3) {
                    opasAppInitChart.getXAxis().setLabelRotationAngle(45);
                } else {
                    opasAppInitChart.getXAxis().setLabelRotationAngle(0);
                }

                LineDataSet lineDataSet_opas_app_init = new LineDataSet(totalCountValueOpasAppInit, "Total");
                lineDataSet_opas_app_init.setValueFormatter(new LargeValueFormatter());
                lineDataSet_opas_app_init.setCircleColor(getColor(R.color.light_green));
                lineDataSet_opas_app_init.setCircleRadius(3f);
                lineDataSet_opas_app_init.setLineWidth(2f);
                lineDataSet_opas_app_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet_opas_app_init.setColor(getColor(R.color.green_sea));
                lineDataSet_opas_app_init.setDrawFilled(false);
                lineDataSet_opas_app_init.setValueTextSize(9f);
                lineDataSet_opas_app_init.setDrawCircleHole(true);
                lineDataSet_opas_app_init.setValueTextColor(R.color.default_text_color);
                lineDataSet_opas_app_init.setValueTextSize(8f);

                LineDataSet lineDataSet1_opas_app_init = new LineDataSet(confirmCountValueOpasAppInit, "Confirmed");
                lineDataSet1_opas_app_init.setValueFormatter(new LargeValueFormatter());
                lineDataSet1_opas_app_init.setCircleColor(getColor(R.color.red_dark));
                lineDataSet1_opas_app_init.setCircleRadius(3f);
                lineDataSet1_opas_app_init.setLineWidth(2f);
                lineDataSet1_opas_app_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet1_opas_app_init.setColor(getColor(R.color.blue_end));
                lineDataSet1_opas_app_init.setDrawFilled(false);
                lineDataSet1_opas_app_init.setValueTextSize(9f);
                lineDataSet1_opas_app_init.setDrawCircleHole(true);
                lineDataSet1_opas_app_init.setValueTextColor(R.color.default_text_color);
                lineDataSet1_opas_app_init.setValueTextSize(8f);

                LineDataSet lineDataSet2_opas_app_init = new LineDataSet(unConfCountValueOpasAppInit, "Not Confirmed");
                lineDataSet2_opas_app_init.setValueFormatter(new LargeValueFormatter());
                lineDataSet2_opas_app_init.setCircleColor(getColor(R.color.disabled));
                lineDataSet2_opas_app_init.setCircleRadius(3f);
                lineDataSet2_opas_app_init.setLineWidth(2f);
                lineDataSet2_opas_app_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet2_opas_app_init.setColor(getColor(R.color.leave_color));
                lineDataSet2_opas_app_init.setDrawFilled(false);
                lineDataSet2_opas_app_init.setValueTextSize(9f);
                lineDataSet2_opas_app_init.setDrawCircleHole(true);
                lineDataSet2_opas_app_init.setValueTextColor(R.color.default_text_color);
                lineDataSet2_opas_app_init.setValueTextSize(8f);

                ArrayList<ILineDataSet> dataSets_opas_app_init = new ArrayList<>();
                dataSets_opas_app_init.add(lineDataSet_opas_app_init);
                dataSets_opas_app_init.add(lineDataSet1_opas_app_init);
                dataSets_opas_app_init.add(lineDataSet2_opas_app_init);

                LineData data1_opas_app_init = new LineData(dataSets_opas_app_init);

                opasAppInitChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateNameOpasAppInit));
                opasAppInitChart.setData(data1_opas_app_init);
                opasAppInitChart.getData().setHighlightEnabled(true);
                opasAppInitChart.invalidate();

                //--- App Schedule chart ---
                NormalMarker mvOpasAppSch = new NormalMarker(getApplicationContext(), R.layout.custom_marker_view);
                mvOpasAppSch.setChartView(opasAppSchChart);
                opasAppSchChart.setMarker(mvOpasAppSch);

                ArrayList<Entry> totalCountValueOpasAppSch = new ArrayList<>();
                ArrayList<Entry> confirmCountValueOpasAppSch = new ArrayList<>();
                ArrayList<Entry> unConfCountValueOpasAppSch = new ArrayList<>();
                ArrayList<String> dateNameOpasAppSch = new ArrayList<>();

                int total_count_opas_app_sch = 0;
                int confirm_count_opas_app_sch = 0;
                int unConf_count_opas_app_sch = 0;

                if (opasAppSchChartLists.size() == 1) {
                    totalCountValueOpasAppSch.add(new Entry(0, 0, "0"));
                    confirmCountValueOpasAppSch.add(new Entry(0, 0, "0"));
                    unConfCountValueOpasAppSch.add(new Entry(0, 0, "0"));
                    dateNameOpasAppSch.add("");

                    totalCountValueOpasAppSch.add(new Entry(1, Float.parseFloat(opasAppSchChartLists.get(0).getAll_app_count()), opasAppSchChartLists.get(0).getId()));
                    confirmCountValueOpasAppSch.add(new Entry(1, Float.parseFloat(opasAppSchChartLists.get(0).getC_app_count()), opasAppSchChartLists.get(0).getId()));
                    unConfCountValueOpasAppSch.add(new Entry(1, Float.parseFloat(opasAppSchChartLists.get(0).getUc_app_count()), opasAppSchChartLists.get(0).getId()));
                    dateNameOpasAppSch.add(opasAppSchChartLists.get(0).getAll_dates());

                    totalCountValueOpasAppSch.add(new Entry(2, 0, "2"));
                    confirmCountValueOpasAppSch.add(new Entry(2, 0, "2"));
                    unConfCountValueOpasAppSch.add(new Entry(2, 0, "2"));
                    dateNameOpasAppSch.add("");

                    if (!opasAppSchChartLists.get(0).getAll_app_count().isEmpty()) {
                        total_count_opas_app_sch = total_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(0).getAll_app_count());
                    }
                    if (!opasAppSchChartLists.get(0).getC_app_count().isEmpty()) {
                        confirm_count_opas_app_sch = confirm_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(0).getC_app_count());
                    }
                    if (!opasAppSchChartLists.get(0).getUc_app_count().isEmpty()) {
                        unConf_count_opas_app_sch = unConf_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(0).getUc_app_count());
                    }
                }
                else {
                    for (int i = 0; i < opasAppSchChartLists.size(); i++) {
                        totalCountValueOpasAppSch.add(new Entry(i, Float.parseFloat(opasAppSchChartLists.get(i).getAll_app_count()), opasAppSchChartLists.get(i).getId()));
                        confirmCountValueOpasAppSch.add(new Entry(i, Float.parseFloat(opasAppSchChartLists.get(i).getC_app_count()), opasAppSchChartLists.get(i).getId()));
                        unConfCountValueOpasAppSch.add(new Entry(i, Float.parseFloat(opasAppSchChartLists.get(i).getUc_app_count()), opasAppSchChartLists.get(i).getId()));
                        dateNameOpasAppSch.add(opasAppSchChartLists.get(i).getAll_dates());

                        if (!opasAppSchChartLists.get(i).getAll_app_count().isEmpty()) {
                            total_count_opas_app_sch = total_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(i).getAll_app_count());
                        }
                        if (!opasAppSchChartLists.get(i).getC_app_count().isEmpty()) {
                            confirm_count_opas_app_sch = confirm_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(i).getC_app_count());
                        }
                        if (!opasAppSchChartLists.get(i).getUc_app_count().isEmpty()) {
                            unConf_count_opas_app_sch = unConf_count_opas_app_sch + Integer.parseInt(opasAppSchChartLists.get(i).getUc_app_count());
                        }
                    }
                }

                opasAppSchCount.setText(String.valueOf(total_count_opas_app_sch));
                opasAppSchConfCount.setText(String.valueOf(confirm_count_opas_app_sch));
                opasAppSchUnConfCount.setText(String.valueOf(unConf_count_opas_app_sch));

                opasAppSchChart.animateXY(1000, 1000);
                if (opasAppSchChartLists.size() > 3) {
                    opasAppSchChart.getXAxis().setLabelRotationAngle(45);
                } else {
                    opasAppSchChart.getXAxis().setLabelRotationAngle(0);
                }

                LineDataSet lineDataSet_opas_app_sch = new LineDataSet(totalCountValueOpasAppSch, "Total");
                lineDataSet_opas_app_sch.setValueFormatter(new LargeValueFormatter());
                lineDataSet_opas_app_sch.setCircleColor(getColor(R.color.light_green));
                lineDataSet_opas_app_sch.setCircleRadius(3f);
                lineDataSet_opas_app_sch.setLineWidth(2f);
                lineDataSet_opas_app_sch.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet_opas_app_sch.setColor(getColor(R.color.green_sea));
                lineDataSet_opas_app_sch.setDrawFilled(false);
                lineDataSet_opas_app_sch.setValueTextSize(9f);
                lineDataSet_opas_app_sch.setDrawCircleHole(true);
                lineDataSet_opas_app_sch.setValueTextColor(R.color.default_text_color);
                lineDataSet_opas_app_sch.setValueTextSize(8f);

                LineDataSet lineDataSet1_opas_app_sch = new LineDataSet(confirmCountValueOpasAppSch, "Confirmed");
                lineDataSet1_opas_app_sch.setValueFormatter(new LargeValueFormatter());
                lineDataSet1_opas_app_sch.setCircleColor(getColor(R.color.red_dark));
                lineDataSet1_opas_app_sch.setCircleRadius(3f);
                lineDataSet1_opas_app_sch.setLineWidth(2f);
                lineDataSet1_opas_app_sch.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet1_opas_app_sch.setColor(getColor(R.color.blue_end));
                lineDataSet1_opas_app_sch.setDrawFilled(false);
                lineDataSet1_opas_app_sch.setValueTextSize(9f);
                lineDataSet1_opas_app_sch.setDrawCircleHole(true);
                lineDataSet1_opas_app_sch.setValueTextColor(R.color.default_text_color);
                lineDataSet1_opas_app_sch.setValueTextSize(8f);

                LineDataSet lineDataSet2_opas_app_sch = new LineDataSet(unConfCountValueOpasAppSch, "Not Confirmed");
                lineDataSet2_opas_app_sch.setValueFormatter(new LargeValueFormatter());
                lineDataSet2_opas_app_sch.setCircleColor(getColor(R.color.disabled));
                lineDataSet2_opas_app_sch.setCircleRadius(3f);
                lineDataSet2_opas_app_sch.setLineWidth(2f);
                lineDataSet2_opas_app_sch.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet2_opas_app_sch.setColor(getColor(R.color.leave_color));
                lineDataSet2_opas_app_sch.setDrawFilled(false);
                lineDataSet2_opas_app_sch.setValueTextSize(9f);
                lineDataSet2_opas_app_sch.setDrawCircleHole(true);
                lineDataSet2_opas_app_sch.setValueTextColor(R.color.default_text_color);
                lineDataSet2_opas_app_sch.setValueTextSize(8f);

                ArrayList<ILineDataSet> dataSets_opas_app_sch = new ArrayList<>();
                dataSets_opas_app_sch.add(lineDataSet_opas_app_sch);
                dataSets_opas_app_sch.add(lineDataSet1_opas_app_sch);
                dataSets_opas_app_sch.add(lineDataSet2_opas_app_sch);

                LineData data1_opas_app_sch = new LineData(dataSets_opas_app_sch);

                opasAppSchChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateNameOpasAppSch));
                opasAppSchChart.setData(data1_opas_app_sch);
                opasAppSchChart.getData().setHighlightEnabled(true);
                opasAppSchChart.invalidate();

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
        opasAppSchFullLayout.setVisibility(View.GONE);
        opasAppSchChartCircularProgressIndicator.setVisibility(View.GONE);
        opasAppSchChartRefresh.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AnalyticsDashboard.this);
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

    public void getOpasPayChart() {
        opasPayFullLayout.setVisibility(View.GONE);
        opasPayChartCircularProgressIndicator.setVisibility(View.VISIBLE);
        opasPayChartRefresh.setVisibility(View.GONE);
        loading = true;
        conn = false;
        connected = false;

        opasPayChartLists = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets4 = new ArrayList<>();
        LineData data5 = new LineData(dataSets4);
        opasPayChart.setData(data5);
        opasPayChart.setMarker(null);
        opasPayChart.getData().clearValues();
        opasPayChart.notifyDataSetChanged();
        opasPayChart.clear();
        opasPayChart.invalidate();
        opasPayChart.fitScreen();

        String opasPayUrl = pre_url_api+"analytic_dashboard/getOPASPayStatus?begin_date="+opasPayChartFirstDate+"&end_date="+opasPayChartLastDate;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest opasPayReq = new StringRequest(Request.Method.GET, opasPayUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String dates = docInfo.getString("all_dates")
                                .equals("null") ? "" : docInfo.getString("all_dates");
                        String ap_c = docInfo.getString("all_pay_count")
                                .equals("null") ? "0" : docInfo.getString("all_pay_count");
                        String cp_c = docInfo.getString("c_pay_count")
                                .equals("null") ? "0" : docInfo.getString("c_pay_count");
                        String fp_c = docInfo.getString("f_pay_count")
                                .equals("null") ? "0" : docInfo.getString("f_pay_count");
                        String np_c = docInfo.getString("na_pay_count")
                                .equals("null") ? "0" : docInfo.getString("na_pay_count");

                        opasPayChartLists.add(new OpasPayChartList(String.valueOf(i+1),dates,ap_c,cp_c,fp_c,np_c));

                    }
                }

                connected = true;
                updateOpasPayLayout();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateOpasPayLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateOpasPayLayout();
        });

        requestQueue.add(opasPayReq);
    }

    private void updateOpasPayLayout() {
        if (conn) {
            if (connected) {
                opasPayFullLayout.setVisibility(View.VISIBLE);
                opasPayChartCircularProgressIndicator.setVisibility(View.GONE);
                opasPayChartRefresh.setVisibility(View.GONE);
                conn = false;
                connected = false;

                NormalMarker mvOpasPayInit = new NormalMarker(getApplicationContext(), R.layout.custom_marker_view);
                mvOpasPayInit.setChartView(opasPayChart);
                opasPayChart.setMarker(mvOpasPayInit);

                ArrayList<Entry> totalCountValueOpasPayInit = new ArrayList<>();
                ArrayList<Entry> compCountValueOpasPayInit = new ArrayList<>();
                ArrayList<Entry> failCountValueOpasPayInit = new ArrayList<>();
                ArrayList<Entry> nrCountValueOpasPayInit = new ArrayList<>();
                ArrayList<String> dateNameOpasPayInit = new ArrayList<>();

                int total_count_opas_pay_init = 0;
                int comp_count_opas_pay_init = 0;
                int fail_count_opas_pay_init = 0;
                int nr_count_opas_pay_init = 0;

                if (opasPayChartLists.size() == 1) {
                    totalCountValueOpasPayInit.add(new Entry(0, 0, "0"));
                    compCountValueOpasPayInit.add(new Entry(0, 0, "0"));
                    failCountValueOpasPayInit.add(new Entry(0, 0, "0"));
                    nrCountValueOpasPayInit.add(new Entry(0, 0, "0"));
                    dateNameOpasPayInit.add("");

                    totalCountValueOpasPayInit.add(new Entry(1, Float.parseFloat(opasPayChartLists.get(0).getAll_pay_count()), opasPayChartLists.get(0).getId()));
                    compCountValueOpasPayInit.add(new Entry(1, Float.parseFloat(opasPayChartLists.get(0).getC_pay_count()), opasPayChartLists.get(0).getId()));
                    failCountValueOpasPayInit.add(new Entry(1, Float.parseFloat(opasPayChartLists.get(0).getF_pay_count()), opasPayChartLists.get(0).getId()));
                    nrCountValueOpasPayInit.add(new Entry(1, Float.parseFloat(opasPayChartLists.get(0).getNa_pay_count()), opasPayChartLists.get(0).getId()));
                    dateNameOpasPayInit.add(opasPayChartLists.get(0).getAll_dates());

                    totalCountValueOpasPayInit.add(new Entry(2, 0, "2"));
                    compCountValueOpasPayInit.add(new Entry(2, 0, "2"));
                    failCountValueOpasPayInit.add(new Entry(2, 0, "2"));
                    nrCountValueOpasPayInit.add(new Entry(2, 0, "2"));
                    dateNameOpasPayInit.add("");

                    if (!opasPayChartLists.get(0).getAll_pay_count().isEmpty()) {
                        total_count_opas_pay_init = total_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(0).getAll_pay_count());
                    }
                    if (!opasPayChartLists.get(0).getC_pay_count().isEmpty()) {
                        comp_count_opas_pay_init = comp_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(0).getC_pay_count());
                    }
                    if (!opasPayChartLists.get(0).getF_pay_count().isEmpty()) {
                        fail_count_opas_pay_init = fail_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(0).getF_pay_count());
                    }
                    if (!opasPayChartLists.get(0).getNa_pay_count().isEmpty()) {
                        nr_count_opas_pay_init = nr_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(0).getNa_pay_count());
                    }
                }
                else {
                    for (int i = 0; i < opasPayChartLists.size(); i++) {
                        totalCountValueOpasPayInit.add(new Entry(i, Float.parseFloat(opasPayChartLists.get(i).getAll_pay_count()), opasPayChartLists.get(i).getId()));
                        compCountValueOpasPayInit.add(new Entry(i, Float.parseFloat(opasPayChartLists.get(i).getC_pay_count()), opasPayChartLists.get(i).getId()));
                        failCountValueOpasPayInit.add(new Entry(i, Float.parseFloat(opasPayChartLists.get(i).getF_pay_count()), opasPayChartLists.get(i).getId()));
                        nrCountValueOpasPayInit.add(new Entry(i, Float.parseFloat(opasPayChartLists.get(i).getNa_pay_count()), opasPayChartLists.get(i).getId()));
                        dateNameOpasPayInit.add(opasPayChartLists.get(i).getAll_dates());

                        if (!opasPayChartLists.get(i).getAll_pay_count().isEmpty()) {
                            total_count_opas_pay_init = total_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(i).getAll_pay_count());
                        }
                        if (!opasPayChartLists.get(i).getC_pay_count().isEmpty()) {
                            comp_count_opas_pay_init = comp_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(i).getC_pay_count());
                        }
                        if (!opasPayChartLists.get(i).getF_pay_count().isEmpty()) {
                            fail_count_opas_pay_init = fail_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(i).getF_pay_count());
                        }
                        if (!opasPayChartLists.get(i).getNa_pay_count().isEmpty()) {
                            nr_count_opas_pay_init = nr_count_opas_pay_init + Integer.parseInt(opasPayChartLists.get(i).getNa_pay_count());
                        }
                    }
                }

                opasPayTotalCount.setText(String.valueOf(total_count_opas_pay_init));
                opasPayCompCount.setText(String.valueOf(comp_count_opas_pay_init));
                opasPayFailCount.setText(String.valueOf(fail_count_opas_pay_init));
                opasPayNACount.setText(String.valueOf(nr_count_opas_pay_init));

                opasPayChart.animateXY(1000, 1000);
                if (opasPayChartLists.size() > 3) {
                    opasPayChart.getXAxis().setLabelRotationAngle(45);
                } else {
                    opasPayChart.getXAxis().setLabelRotationAngle(0);
                }

                LineDataSet lineDataSet_opas_pay_init = new LineDataSet(totalCountValueOpasPayInit, "Total");
                lineDataSet_opas_pay_init.setValueFormatter(new LargeValueFormatter());
                lineDataSet_opas_pay_init.setCircleColor(getColor(R.color.pres_on_leave));
                lineDataSet_opas_pay_init.setCircleRadius(3f);
                lineDataSet_opas_pay_init.setLineWidth(2f);
                lineDataSet_opas_pay_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet_opas_pay_init.setColor(getColor(R.color.naval_blue));
                lineDataSet_opas_pay_init.setDrawFilled(false);
                lineDataSet_opas_pay_init.setValueTextSize(9f);
                lineDataSet_opas_pay_init.setDrawCircleHole(true);
                lineDataSet_opas_pay_init.setValueTextColor(R.color.default_text_color);
                lineDataSet_opas_pay_init.setValueTextSize(8f);

                LineDataSet lineDataSet1_opas_pay_init = new LineDataSet(compCountValueOpasPayInit, "Completed");
                lineDataSet1_opas_pay_init.setValueFormatter(new LargeValueFormatter());
                lineDataSet1_opas_pay_init.setCircleColor(getColor(R.color.light_green));
                lineDataSet1_opas_pay_init.setCircleRadius(3f);
                lineDataSet1_opas_pay_init.setLineWidth(2f);
                lineDataSet1_opas_pay_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet1_opas_pay_init.setColor(getColor(R.color.green_sea));
                lineDataSet1_opas_pay_init.setDrawFilled(false);
                lineDataSet1_opas_pay_init.setValueTextSize(9f);
                lineDataSet1_opas_pay_init.setDrawCircleHole(true);
                lineDataSet1_opas_pay_init.setValueTextColor(R.color.default_text_color);
                lineDataSet1_opas_pay_init.setValueTextSize(8f);

                LineDataSet lineDataSet2_opas_pay_init = new LineDataSet(failCountValueOpasPayInit, "Failed");
                lineDataSet2_opas_pay_init.setValueFormatter(new LargeValueFormatter());
                lineDataSet2_opas_pay_init.setCircleColor(getColor(R.color.light_rose));
                lineDataSet2_opas_pay_init.setCircleRadius(3f);
                lineDataSet2_opas_pay_init.setLineWidth(2f);
                lineDataSet2_opas_pay_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet2_opas_pay_init.setColor(getColor(R.color.red_dark));
                lineDataSet2_opas_pay_init.setDrawFilled(false);
                lineDataSet2_opas_pay_init.setValueTextSize(9f);
                lineDataSet2_opas_pay_init.setDrawCircleHole(true);
                lineDataSet2_opas_pay_init.setValueTextColor(R.color.default_text_color);
                lineDataSet2_opas_pay_init.setValueTextSize(8f);

                LineDataSet lineDataSet3_opas_pay_init = new LineDataSet(nrCountValueOpasPayInit, "No Result");
                lineDataSet3_opas_pay_init.setValueFormatter(new LargeValueFormatter());
                lineDataSet3_opas_pay_init.setCircleColor(getColor(R.color.disabled));
                lineDataSet3_opas_pay_init.setCircleRadius(3f);
                lineDataSet3_opas_pay_init.setLineWidth(2f);
                lineDataSet3_opas_pay_init.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet3_opas_pay_init.setColor(getColor(R.color.leave_color));
                lineDataSet3_opas_pay_init.setDrawFilled(false);
                lineDataSet3_opas_pay_init.setValueTextSize(9f);
                lineDataSet3_opas_pay_init.setDrawCircleHole(true);
                lineDataSet3_opas_pay_init.setValueTextColor(R.color.default_text_color);
                lineDataSet3_opas_pay_init.setValueTextSize(8f);

                ArrayList<ILineDataSet> dataSets_opas_pay_init = new ArrayList<>();
                dataSets_opas_pay_init.add(lineDataSet_opas_pay_init);
                dataSets_opas_pay_init.add(lineDataSet1_opas_pay_init);
                dataSets_opas_pay_init.add(lineDataSet2_opas_pay_init);
                dataSets_opas_pay_init.add(lineDataSet3_opas_pay_init);

                LineData data1_opas_pay_init = new LineData(dataSets_opas_pay_init);

                opasPayChart.getXAxis().setValueFormatter(new MyAxisValueFormatter(dateNameOpasPayInit));
                opasPayChart.setData(data1_opas_pay_init);
                opasPayChart.getData().setHighlightEnabled(true);
                opasPayChart.invalidate();

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
        opasPayFullLayout.setVisibility(View.GONE);
        opasPayChartCircularProgressIndicator.setVisibility(View.GONE);
        opasPayChartRefresh.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AnalyticsDashboard.this);
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
}