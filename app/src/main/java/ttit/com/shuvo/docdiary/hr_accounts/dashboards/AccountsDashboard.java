package ttit.com.shuvo.docdiary.hr_accounts.dashboards;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
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

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.acc_ledger.AccountLedger;
import ttit.com.shuvo.docdiary.hr_accounts.acc_ledger_pat.AccountLedgerPatient;
import ttit.com.shuvo.docdiary.hr_accounts.income_expense.IncomeExpenseStatement;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.CreditVoucherApproval;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.DebitVoucherApproval;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.JournalVoucherApproval;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.VoucherTransaction;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.ReportShow;

public class AccountsDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView acc_menu;

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    TabLayout tabLayout;
    boolean tabUpdateNeeded = false;
    String tabFirstDate = "";
    String tabEndDate = "";
    boolean fromTab = false;

    TextView dateRangeText;

    String total_voucher_count = "";
    String total_voucher_amount = "";

    PieChart voucherPieChart;
    ArrayList<PieEntry> voucherPieEntry;

    TextView voucherCount;
    TextView voucherAmount;

    String total_income = "";
    String total_expense = "";

    PieChart incExpPieChart;
    ArrayList<PieEntry> incExpPieEntry;

    TextView totalIncome;
    TextView totalExpense;

    String report_name = "";
    String server_port = "";
    String hrs_link = "";
    String rep_path = "";
    String cid_id = "";

    Logger logger = Logger.getLogger(AccountsDashboard.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.acc_dashboard_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawerLayout = findViewById(R.id.account_dashboard_drawer);
        navigationView = findViewById(R.id.account_menu_navigation);
        acc_menu = findViewById(R.id.account_menu_icon);

        fullLayout = findViewById(R.id.accounts_dashboard_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_accounts_dashboard);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_accounts_dashboard);
        tabLayout = findViewById(R.id.accounts_tab_layout);
        dateRangeText = findViewById(R.id.date_range_tab_text_acc_dashboard);

        voucherPieChart = findViewById(R.id.voucher_count_overview_pie_chart);

        voucherCount = findViewById(R.id.total_voucher_count_from_graph);
        voucherAmount = findViewById(R.id.total_amount_from_voucher_graph);

        incExpPieChart = findViewById(R.id.income_expense_overview_pie_chart);

        totalIncome = findViewById(R.id.total_income_from_dash_graph);
        totalExpense = findViewById(R.id.total_expense_from_dash_graph);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        acc_menu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)){
                drawerLayout.closeDrawer(GravityCompat.END);
            }
            drawerLayout.openDrawer(GravityCompat.END);
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout , R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);

        ImageView imageView = headerView.findViewById(R.id.close_icon_accounts_menu);

        imageView.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.END));

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(AccountsDashboard.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
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
                            dateRangeText.setText(date_range_text);
                            fromTab = true;
                            getAllGraphValue(tabFirstDate,tabEndDate);
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
                            dateRangeText.setText(text);
                            fromTab = true;
                            getAllGraphValue(tabFirstDate,tabEndDate);
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

                            dateRangeText.setText(text1);
                            fromTab = true;
                            getAllGraphValue(tabFirstDate,tabEndDate);
                            break;
                        case 3:
                            System.out.println("Tab 4");
                            Calendar n_monthcc = Calendar.getInstance();
                            n_monthcc.set(Calendar.DAY_OF_MONTH, 1);
                            n_monthcc.add(Calendar.DATE, -1);

                            Date lastDayOf_l_Month = n_monthcc.getTime();

                            String llll_n = sdff.format(lastDayOf_l_Month);
                            String n_llmmmyy = moY.format(lastDayOf_l_Month);
                            tabEndDate = llll_n + "-" + n_llmmmyy;
                            tabFirstDate = "01-"+ n_llmmmyy;
                            String month_only_full1 = only_month_full_date_format.format(lastDayOf_l_Month);

                            String text12 = "01 " + month_only_full1 + "  --  " + llll_n + " " + month_only_full1;
                            dateRangeText.setText(text12);
                            fromTab = true;
                            getAllGraphValue(tabFirstDate,tabEndDate);
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

        voucherChartInit();

        voucherPieChart.setOnClickListener(view -> {
            Intent intent = new Intent(AccountsDashboard.this, VoucherTransaction.class);
            if (fromTab) {
                intent.putExtra("FIRST_DATE",tabFirstDate);
                intent.putExtra("LAST_DATE",tabEndDate);
            }
            else {
                intent.putExtra("FIRST_DATE","");
                intent.putExtra("LAST_DATE","");
            }

            startActivity(intent);
        });

        incExpPieChart.setOnClickListener(view -> {
            Intent intent = new Intent(AccountsDashboard.this, IncomeExpenseStatement.class);
            if (fromTab) {
                intent.putExtra("FIRST_DATE",tabFirstDate);
                intent.putExtra("LAST_DATE",tabEndDate);
            }
            else {
                intent.putExtra("FIRST_DATE","");
                intent.putExtra("LAST_DATE","");
            }

            startActivity(intent);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
               if (loading) {
                   Toast.makeText(AccountsDashboard.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
               }
               else {
                   if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                       drawerLayout.closeDrawer(GravityCompat.END);
                   }
                   else {
                       finish();
                   }
               }
            }
        });

        navigationView.getMenu().findItem(R.id.daily_statement_acc_menu).setVisible(pre_url_api.contains("cstar") || pre_url_api.contains("btrf"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loading != null) {
            if (!loading) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                String date_now = dateFormat.format(Calendar.getInstance().getTime());
                fromTab = false;
                getAllGraphValue(date_now,date_now);
            }
        }
        else {
            restart("App is paused for a long time. Please Start the app again.");
        }
    }

    private void voucherChartInit() {
        voucherPieChart.setCenterText("Voucher");
        voucherPieChart.setDrawEntryLabels(true);
        voucherPieChart.setCenterTextSize(14);
        voucherPieChart.setHoleRadius(40);
        voucherPieChart.setTransparentCircleRadius(40);

        voucherPieChart.setEntryLabelTextSize(11);
        voucherPieChart.setEntryLabelColor(Color.DKGRAY);
        voucherPieChart.getDescription().setEnabled(false);

        voucherPieChart.setTouchEnabled(true);
        voucherPieChart.setClickable(true);
        voucherPieChart.setHighlightPerTapEnabled(true);
        voucherPieChart.setOnTouchListener(null);

        Legend l = voucherPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setForm(Legend.LegendForm.CIRCLE);

        l.setXOffset(8f);
        l.setTextSize(12);
        l.setWordWrapEnabled(false);
        l.setDrawInside(false);
        l.setYOffset(5f);

        voucherPieChart.animateXY(1000, 1000);


        incExpPieChart.setCenterText("Income/Expense");
        incExpPieChart.setDrawEntryLabels(true);
        incExpPieChart.setCenterTextSize(14);
        incExpPieChart.setHoleRadius(40);
        incExpPieChart.setTransparentCircleRadius(40);

        incExpPieChart.setEntryLabelTextSize(11);
        incExpPieChart.setEntryLabelColor(Color.DKGRAY);
        incExpPieChart.getDescription().setEnabled(false);

        incExpPieChart.setTouchEnabled(true);
        incExpPieChart.setClickable(true);
        incExpPieChart.setHighlightPerTapEnabled(true);
        incExpPieChart.setOnTouchListener(null);

        Legend i_e_l = incExpPieChart.getLegend();
        i_e_l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        i_e_l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        i_e_l.setOrientation(Legend.LegendOrientation.VERTICAL);
        i_e_l.setForm(Legend.LegendForm.CIRCLE);

        i_e_l.setXOffset(8f);
        i_e_l.setTextSize(12);
        i_e_l.setWordWrapEnabled(false);
        i_e_l.setDrawInside(false);
        i_e_l.setYOffset(5f);

        incExpPieChart.animateXY(1000, 1000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (loading) {
            Toast.makeText(AccountsDashboard.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
        }
        else {
            if (item.getItemId() == R.id.credit_voucher_approved) {
                Intent intent = new Intent(AccountsDashboard.this, CreditVoucherApproval.class);
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.debit_voucher_approved) {
                Intent intent = new Intent(AccountsDashboard.this, DebitVoucherApproval.class);
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.journal_voucher_approved) {
                Intent intent = new Intent(AccountsDashboard.this, JournalVoucherApproval.class);
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.voucher_transaction_acc_menu) {
                Intent intent = new Intent(AccountsDashboard.this, VoucherTransaction.class);
                intent.putExtra("FIRST_DATE", "");
                intent.putExtra("LAST_DATE", "");
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.accnt_ledger_acc_menu) {
                Intent intent = new Intent(AccountsDashboard.this, AccountLedger.class);
                intent.putExtra("FIRST_DATE", "");
                intent.putExtra("LAST_DATE", "");
                intent.putExtra("AD_ID", "");
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.income_expense_acc_menu) {
                Intent intent = new Intent(AccountsDashboard.this, IncomeExpenseStatement.class);
                intent.putExtra("FIRST_DATE", "");
                intent.putExtra("LAST_DATE", "");
                startActivity(intent);
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.daily_statement_acc_menu) {
                getUrlData();
                item.setChecked(true);
            }
            else if (item.getItemId() == R.id.accnt_ledger_ph_acc_menu) {
                Intent intent = new Intent(AccountsDashboard.this, AccountLedgerPatient.class);
                startActivity(intent);
                item.setChecked(true);
            }
            else {
                Toast.makeText(getApplicationContext(), "No Menu Selected", Toast.LENGTH_SHORT).show();
            }
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    public void getAllGraphValue(String first_date, String end_date) {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        voucherPieEntry = new ArrayList<>();
        incExpPieEntry = new ArrayList<>();
        total_income = "0";
        total_expense = "0";

        String voucherCountUrl = pre_url_api+"acc_dashboard/getVoucherCount?first_date="+first_date+"&end_date="+end_date;
        String voucherAmntUrl = pre_url_api+"acc_dashboard/getVoucherTotalAmnt?first_date="+first_date+"&end_date="+end_date;
        String incExpUrl = pre_url_api+"acc_dashboard/getIncomeExpense?first_date="+first_date+"&end_date="+end_date;

        RequestQueue requestQueue = Volley.newRequestQueue(AccountsDashboard.this);

        StringRequest incExpReq = new StringRequest(Request.Method.GET, incExpUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        total_income = docInfo.getString("income")
                                .equals("null") ? "0" : docInfo.getString("income");
                        total_expense = docInfo.getString("expense")
                                .equals("null") ? "0" : docInfo.getString("expense");

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

        StringRequest voucherAmountReq = new StringRequest(Request.Method.GET, voucherAmntUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        total_voucher_amount = docInfo.getString("amount")
                                .equals("null") ? "0" : docInfo.getString("amount");

                    }
                }

                requestQueue.add(incExpReq);
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

        StringRequest voucherCountReq = new StringRequest(Request.Method.GET, voucherCountUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String voucher_type = docInfo.getString("voucher_type")
                                .equals("null") ? "" : docInfo.getString("voucher_type");
                        String cc = docInfo.getString("cc")
                                .equals("null") ? "0" : docInfo.getString("cc");

                        if (!voucher_type.isEmpty()) {
                            voucherPieEntry.add(new PieEntry(Float.parseFloat(cc),voucher_type,i));
                        }
                    }
                }

                requestQueue.add(voucherAmountReq);

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

        incExpReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));


        voucherAmountReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        voucherCountReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(voucherCountReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);

                conn = false;
                connected = false;

                double va = Double.parseDouble(total_voucher_amount);
                if (va > 0.0) {
                    voucherAmount.setTextColor(getColor(R.color.green_sea));
                    String tt = total_voucher_amount + " (Net Profit)";
                    voucherAmount.setText(tt);
                }
                else if (va == 0.0) {
                    voucherAmount.setTextColor(getColor(R.color.green_sea));
                    String tt = total_voucher_amount + " (Break Even Point)";
                    voucherAmount.setText(tt);
                }
                else {
                    voucherAmount.setTextColor(getColor(R.color.red_dark));
                    total_voucher_amount = total_voucher_amount.replace("-","");
                    String tt = total_voucher_amount + " (Net Loss)";
                    voucherAmount.setText(tt);
                }

                int cc = 0;
                for (int i = 0; i < voucherPieEntry.size(); i++) {
                    cc = cc + (int) voucherPieEntry.get(i).getValue();
                }

                total_voucher_count = String.valueOf(cc);
                voucherCount.setText(total_voucher_count);


                if (!fromTab) {
                    SimpleDateFormat full_date_format_range = new SimpleDateFormat("dd MMMM, yy", Locale.ENGLISH);

                    Date dateRangCc = Calendar.getInstance().getTime();
                    String date_range_text = full_date_format_range.format(dateRangCc);
                    dateRangeText.setText(date_range_text);

                    TabLayout.Tab tabAt = tabLayout.getTabAt(0);
                    tabUpdateNeeded = false;
                    tabLayout.selectTab(tabAt);
                }

                if (voucherPieEntry.isEmpty()) {
                    voucherPieEntry.add(new PieEntry(1,"No Data", 6));
                }

                PieDataSet dataSet = new PieDataSet(voucherPieEntry, "");
                voucherPieChart.animateXY(1000, 1000);
                voucherPieChart.setEntryLabelColor(Color.TRANSPARENT);

                PieData data = new PieData(dataSet);
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) Math.floor(value));
                    }
                });
                String label = dataSet.getValues().get(0).getLabel();
                System.out.println(label);
                if (label.equals("No Data")) {
                    dataSet.setValueTextColor(Color.TRANSPARENT);
                } else {
                    dataSet.setValueTextColor(Color.BLACK);
                }
                dataSet.setHighlightEnabled(true);
                dataSet.setValueTextSize(12);

                final int[] piecolors = new int[]{
                        Color.rgb(129, 236, 236),
                        Color.rgb(116, 185, 255),
                        Color.rgb(255, 234, 167),

                        Color.rgb(223, 230, 233),
                        Color.rgb(255, 234, 167),
                        Color.rgb(250, 177, 160),
                        Color.rgb(178, 190, 195),
                        Color.rgb(129, 236, 236),
                        Color.rgb(255, 118, 117),
                        Color.rgb(253, 121, 168),
                        Color.rgb(96, 163, 188)};

                int[] num = new int[voucherPieEntry.size()];
                for (int i = 0; i < voucherPieEntry.size(); i++) {
                    int neki = (int) voucherPieEntry.get(i).getData();
                    num[i] = piecolors[neki];
                }

                dataSet.setColors(ColorTemplate.createColors(num));

                voucherPieChart.setData(data);
                voucherPieChart.invalidate();

                totalIncome.setText(total_income);
                totalExpense.setText(total_expense);

                if (!total_income.equals("0")) {
                    incExpPieEntry.add(new PieEntry(Float.parseFloat(total_income), "Income",0));
                }
                if (!total_expense.equals("0")) {
                    incExpPieEntry.add(new PieEntry(Float.parseFloat(total_expense), "Expense",1));
                }

                if (incExpPieEntry.isEmpty()) {
                    incExpPieEntry.add(new PieEntry(1,"No Data", 6));
                }

                PieDataSet incExpdataSet = new PieDataSet(incExpPieEntry, "");
                incExpPieChart.animateXY(1000, 1000);
                incExpPieChart.setEntryLabelColor(Color.TRANSPARENT);

                PieData incExpdata = new PieData(incExpdataSet);
                incExpdataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) Math.floor(value));
                    }
                });

                String inclabel = incExpdataSet.getValues().get(0).getLabel();
                System.out.println(inclabel);
                if (inclabel.equals("No Data")) {
                    incExpdataSet.setValueTextColor(Color.TRANSPARENT);
                } else {
                    incExpdataSet.setValueTextColor(Color.WHITE);
                }

                incExpdataSet.setHighlightEnabled(true);
                incExpdataSet.setValueTextSize(12);

                final int[] incExppiecolors = new int[]{
                        Color.rgb(29, 209, 161),
                        Color.rgb(255, 107, 107),

                        Color.rgb(255, 234, 167),
                        Color.rgb(223, 230, 233),
                        Color.rgb(255, 234, 167),
                        Color.rgb(250, 177, 160),
                        Color.rgb(178, 190, 195),
                        Color.rgb(129, 236, 236),
                        Color.rgb(255, 118, 117),
                        Color.rgb(253, 121, 168),
                        Color.rgb(96, 163, 188)};

                int[] incExpnum = new int[incExpPieEntry.size()];
                for (int i = 0; i < incExpPieEntry.size(); i++) {
                    int neki = (int) incExpPieEntry.get(i).getData();
                    incExpnum[i] = incExppiecolors[neki];
                }

                incExpdataSet.setColors(ColorTemplate.createColors(incExpnum));

                incExpPieChart.setData(incExpdata);
                incExpPieChart.invalidate();

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
        circularProgressIndicator.setVisibility(View.GONE);

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AccountsDashboard.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    if (fromTab) {
                        getAllGraphValue(tabFirstDate, tabEndDate);
                    }
                    else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                        String date_now = dateFormat.format(Calendar.getInstance().getTime());
                        getAllGraphValue(date_now,date_now);
                    }

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

    public void getUrlData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String reportUrl = pre_url_api+"report_manager/getUrlData";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest reportUrlReq = new StringRequest(Request.Method.GET, reportUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject apptDataInfo = array.getJSONObject(i);

                        server_port = apptDataInfo.getString("lic_rep_server_port")
                                .equals("null") ? "" :apptDataInfo.getString("lic_rep_server_port");
                        rep_path = apptDataInfo.getString("lic_rep_path")
                                .equals("null") ? "" :apptDataInfo.getString("lic_rep_path");
                        hrs_link = apptDataInfo.getString("lic_rep_hrs_link")
                                .equals("null") ? "" :apptDataInfo.getString("lic_rep_hrs_link");
                        cid_id = apptDataInfo.getString("cid_id")
                                .equals("null") ? "" :apptDataInfo.getString("cid_id");
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


        requestQueue.add(reportUrlReq);
    }

    private void updateLayout() {
        loading = false;
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                String date_now = dateFormat.format(Calendar.getInstance().getTime());
                report_name = "DAILY_STATEMENT.rep";
                String url = "http://" + server_port + "/reports/rwservlet?" + hrs_link + "+report=" + rep_path + report_name + "+BEGIN_DATE=" + date_now + "+END_DATE=" + date_now;
                Intent intent1 = new Intent(AccountsDashboard.this, ReportShow.class);
                intent1.putExtra("REPORT_URL", url);
                intent1.putExtra("FIRST_DATE", date_now);
                intent1.putExtra("LAST_DATE", date_now);
                intent1.putExtra("APP_BAR_NAME", getString(R.string.daily_state_bar_name));
                startActivity(intent1);
                circularProgressIndicator.setVisibility(View.GONE);
                fullLayout.setVisibility(View.VISIBLE);

            }
            else {
                alertMessageUrl();
            }
        }
        else {
            alertMessageUrl();
        }
    }

    public void alertMessageUrl() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AccountsDashboard.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getUrlData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
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