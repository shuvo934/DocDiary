package ttit.com.shuvo.docdiary.hr_accounts.income_expense;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.rosemaryapp.amazingspinner.AmazingSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.income_expense.adapters.IncomeExpenseStateAdapter;
import ttit.com.shuvo.docdiary.hr_accounts.income_expense.arraylists.IncExpStatementList;

public class IncomeExpenseStatement extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    TextInputEditText beginDate;
    TextInputEditText endDate;
    TextView dateRange;

    private int mYear, mMonth, mDay;
    String firstDate = "";
    String lastDate = "";

    AmazingSpinner acLevelSpinner;
    ArrayList<String> acLevelLists;
    String acLevelType = "";

    TextView totalAmount;
    PieChart incExpPieChart;
    ArrayList<PieEntry> incExpPieEntry;
    String total_income = "0";
    String total_expense = "0";

    RecyclerView incomeView;
    IncomeExpenseStateAdapter incomeStateAdapter;
    RecyclerView.LayoutManager incLayoutManager;

    ArrayList<IncExpStatementList> incomeStatementLists;
    ArrayList<IncExpStatementList> incFilteredLists;

    TextView totalIncBfrDebit;
    TextView totalIncBfrCredit;
    TextView totalIncCurDebit;
    TextView totalIncCurCredit;
    TextView totalIncBalance;

    RecyclerView expenseView;
    IncomeExpenseStateAdapter expenseStateAdapter;
    RecyclerView.LayoutManager expLayoutManager;

    ArrayList<IncExpStatementList> expenseStatementLists;
    ArrayList<IncExpStatementList> expFilteredLists;

    TextView totalExpBfrDebit;
    TextView totalExpBfrCredit;
    TextView totalExpCurDebit;
    TextView totalExpCurCredit;
    TextView totalExpBalance;

    TextView sumProfitLossBefore;
    TextView sumProfitLossDuring;
    TextView sumProfitLossBalance;

    Logger logger = Logger.getLogger(IncomeExpenseStatement.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_expense_statement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inc_exp_stat_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.income_expense_state_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_income_expense_state);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_income_expense_state);

        beginDate = findViewById(R.id.begin_date_income_expense_state);
        endDate = findViewById(R.id.end_date_income_expense_state);
        dateRange = findViewById(R.id.date_range_msg_income_expense_state);

        acLevelSpinner = findViewById(R.id.account_level_type_ies_spinner);

        totalAmount = findViewById(R.id.profit_loss_amount_text_in_ies);
        incExpPieChart = findViewById(R.id.income_statement_overview_pie_chart);

        incomeView = findViewById(R.id.income_statement_report_view);

        incomeView.setHasFixedSize(true);
        incLayoutManager = new LinearLayoutManager(getApplicationContext());
        incomeView.setLayoutManager(incLayoutManager);

        totalIncBfrDebit = findViewById(R.id.total_before_debit_val_in_income_statement);
        totalIncBfrCredit = findViewById(R.id.total_before_credit_val_in_income_statement);
        totalIncCurDebit = findViewById(R.id.total_during_debit_val_in_income_statement);
        totalIncCurCredit = findViewById(R.id.total_during_credit_val_in_income_statement);
        totalIncBalance = findViewById(R.id.total_balance_val_in_income_statement);

        expenseView = findViewById(R.id.expense_statement_report_view);

        expenseView.setHasFixedSize(true);
        expLayoutManager = new LinearLayoutManager(getApplicationContext());
        expenseView.setLayoutManager(expLayoutManager);

        totalExpBfrDebit = findViewById(R.id.total_before_debit_val_in_expense_statement);
        totalExpBfrCredit = findViewById(R.id.total_before_credit_val_in_expense_statement);
        totalExpCurDebit = findViewById(R.id.total_during_debit_val_in_expense_statement);
        totalExpCurCredit = findViewById(R.id.total_during_credit_val_in_expense_statement);
        totalExpBalance = findViewById(R.id.total_balance_val_in_expense_statement);

        sumProfitLossBefore = findViewById(R.id.before_sum_profit_loss);
        sumProfitLossDuring = findViewById(R.id.during_sum_profit_loss);
        sumProfitLossBalance = findViewById(R.id.balance_sum_profit_loss);

        acLevelLists = new ArrayList<>();
        incExpPieEntry = new ArrayList<>();

        incomeStatementLists = new ArrayList<>();
        incFilteredLists = new ArrayList<>();

        expenseStatementLists = new ArrayList<>();
        expFilteredLists = new ArrayList<>();

        Intent intent = getIntent();
        firstDate = intent.getStringExtra("FIRST_DATE");
        lastDate = intent.getStringExtra("LAST_DATE");

        acLevelLists.add("Level-01 (Control Head)");
        acLevelLists.add("Level-02 (Sub Control Head)");
        acLevelLists.add("Level-03 (Account Details)");

        String lt = "Level-03 (Account Details)";
        acLevelSpinner.setText(lt);
        acLevelType = "3";

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, R.id.drop_down_item, acLevelLists);

        acLevelSpinner.setAdapter(arrayAdapter);

        acLevelSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();

            if (name.equals("Level-01 (Control Head)")) {
                acLevelType = "1";
            }
            else if (name.equals("Level-02 (Sub Control Head)")) {
                acLevelType = "2";
            }
            else {
                acLevelType = "3";
            }

            filterIncExp(acLevelType);
        });

        incExpChartInit();

        // Getting Date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy",Locale.ENGLISH);

        if (firstDate.isEmpty()) {
//            firstDate = simpleDateFormat.format(c);
//            firstDate = "01-"+firstDate;
            firstDate = df.format(c);
        }
        if (lastDate.isEmpty()) {
            lastDate = df.format(c);
        }

        beginDate.setText(firstDate);
        endDate.setText(lastDate);

        beginDate.setOnClickListener(v -> {
            final Calendar c1 = Calendar.getInstance();
            Date fdate = null;
            try {
                fdate = df.parse(firstDate);
            } catch (ParseException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
            }
            if (fdate != null) {
                c1.setTime(fdate);
            }
            mYear = c1.get(Calendar.YEAR);
            mMonth = c1.get(Calendar.MONTH);
            mDay = c1.get(Calendar.DAY_OF_MONTH);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(IncomeExpenseStatement.this, (view, year, month, dayOfMonth) -> {

                    String monthName = "";
                    String dayOfMonthName;
                    String yearName;
                    month = month + 1;
                    if (month == 1) {
                        monthName = "JAN";
                    } else if (month == 2) {
                        monthName = "FEB";
                    } else if (month == 3) {
                        monthName = "MAR";
                    } else if (month == 4) {
                        monthName = "APR";
                    } else if (month == 5) {
                        monthName = "MAY";
                    } else if (month == 6) {
                        monthName = "JUN";
                    } else if (month == 7) {
                        monthName = "JUL";
                    } else if (month == 8) {
                        monthName = "AUG";
                    } else if (month == 9) {
                        monthName = "SEP";
                    } else if (month == 10) {
                        monthName = "OCT";
                    } else if (month == 11) {
                        monthName = "NOV";
                    } else if (month == 12) {
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
                    beginDate.setText(dateText);
                    firstDate = dateText;
                    if (lastDate.isEmpty()) {
                        dateRange.setVisibility(View.GONE);
                    }
                    else {
                        Date bDate = null;
                        Date eDate = null;

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

                        try {
                            bDate = sdf.parse(firstDate);
                            eDate = sdf.parse(lastDate);
                        }
                        catch (ParseException e) {
                            logger.log(Level.WARNING,e.getMessage(),e);
                        }

                        if (bDate != null && eDate != null) {
                            if (eDate.after(bDate)|| eDate.equals(bDate)) {
                                dateRange.setVisibility(View.GONE);
                                getIncomeExpList();
                            }
                            else {
                                dateRange.setVisibility(View.VISIBLE);
                                beginDate.setText("");
                                firstDate = "";
                            }
                        }
                    }

                }, mYear, mMonth, mDay);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -2);
                calendar.set(Calendar.DATE,1);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTime().getTime());
                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(v -> {
            final Calendar c12 = Calendar.getInstance();
            Date ldate = null;
            try {
                ldate = df.parse(lastDate);
            } catch (ParseException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
            }
            if (ldate != null) {
                c12.setTime(ldate);
            }
            mYear = c12.get(Calendar.YEAR);
            mMonth = c12.get(Calendar.MONTH);
            mDay = c12.get(Calendar.DAY_OF_MONTH);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(IncomeExpenseStatement.this, (view, year, month, dayOfMonth) -> {

                    String monthName = "";
                    String dayOfMonthName;
                    String yearName;
                    month = month + 1;
                    if (month == 1) {
                        monthName = "JAN";
                    } else if (month == 2) {
                        monthName = "FEB";
                    } else if (month == 3) {
                        monthName = "MAR";
                    } else if (month == 4) {
                        monthName = "APR";
                    } else if (month == 5) {
                        monthName = "MAY";
                    } else if (month == 6) {
                        monthName = "JUN";
                    } else if (month == 7) {
                        monthName = "JUL";
                    } else if (month == 8) {
                        monthName = "AUG";
                    } else if (month == 9) {
                        monthName = "SEP";
                    } else if (month == 10) {
                        monthName = "OCT";
                    } else if (month == 11) {
                        monthName = "NOV";
                    } else if (month == 12) {
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
                    endDate.setText(dateText);
                    lastDate = dateText;

                    if (firstDate.isEmpty()) {
                        dateRange.setVisibility(View.GONE);
                    }
                    else {
                        Date bDate = null;
                        Date eDate = null;

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

                        try {
                            bDate = sdf.parse(firstDate);
                            eDate = sdf.parse(lastDate);
                        } catch (ParseException e) {
                            logger.log(Level.WARNING,e.getMessage(),e);
                        }

                        if (bDate != null && eDate != null) {
                            if (eDate.after(bDate)|| eDate.equals(bDate)) {
                                dateRange.setVisibility(View.GONE);
                                getIncomeExpList();
                            }
                            else {
                                dateRange.setVisibility(View.VISIBLE);
                                endDate.setText("");
                                lastDate = "";
                            }
                        }
                    }

                }, mYear, mMonth, mDay);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -2);
                calendar.set(Calendar.DATE,1);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTime().getTime());
                datePickerDialog.show();
            }
        });

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(IncomeExpenseStatement.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(IncomeExpenseStatement.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        getIncomeExpList();
    }

    private void incExpChartInit() {
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

        Legend l = incExpPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setForm(Legend.LegendForm.CIRCLE);

        l.setXOffset(20f);
        l.setTextSize(12);
        l.setWordWrapEnabled(false);
        l.setDrawInside(false);
        l.setYOffset(10f);

        incExpPieChart.animateXY(1000, 1000);
    }
    
    public void filterIncExp(String level) {
        DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");
        switch (level) {
            case "3": {
                // income statement
                double inc_bf_debit_amnt = 0.0;
                double inc_bf_credit_amnt = 0.0;
                double inc_cr_debit_amnt = 0.0;
                double inc_cr_credit_amnt = 0.0;
                double inc_bal = 0.0;
                for (int i = 0; i < incomeStatementLists.size(); i++) {
                    incomeStatementLists.get(i).setLevel_type(level);
                    if (!incomeStatementLists.get(i).getBfdr().isEmpty()) {
                        inc_bf_debit_amnt = inc_bf_debit_amnt + Double.parseDouble(incomeStatementLists.get(i).getBfdr());
                    }
                    if (!incomeStatementLists.get(i).getBfcr().isEmpty()) {
                        inc_bf_credit_amnt = inc_bf_credit_amnt + Double.parseDouble(incomeStatementLists.get(i).getBfcr());
                    }
                    if (!incomeStatementLists.get(i).getCurdr().isEmpty()) {
                        inc_cr_debit_amnt = inc_cr_debit_amnt + Double.parseDouble(incomeStatementLists.get(i).getCurdr());
                    }
                    if (!incomeStatementLists.get(i).getCurcr().isEmpty()) {
                        inc_cr_credit_amnt = inc_cr_credit_amnt + Double.parseDouble(incomeStatementLists.get(i).getCurcr());
                    }
                    if (!incomeStatementLists.get(i).getBfdr().isEmpty() && !incomeStatementLists.get(i).getBfcr().isEmpty()
                            && !incomeStatementLists.get(i).getCurdr().isEmpty() && !incomeStatementLists.get(i).getCurcr().isEmpty()) {
                        double b_debit = Double.parseDouble(incomeStatementLists.get(i).getBfdr());
                        double b_credit = Double.parseDouble(incomeStatementLists.get(i).getBfcr());
                        double c_debit = Double.parseDouble(incomeStatementLists.get(i).getCurdr());
                        double c_credit = Double.parseDouble(incomeStatementLists.get(i).getCurcr());

                        double balance = (b_credit + c_credit) - (b_debit + c_debit);
                        inc_bal = inc_bal + balance;
                    }
                }

                String inc_bfdr = formatter.format(inc_bf_debit_amnt);
                inc_bfdr = "৳ " + inc_bfdr;

                String inc_bfcr = formatter.format(inc_bf_credit_amnt);
                inc_bfcr = "৳ " + inc_bfcr;

                String inc_crdr = formatter.format(inc_cr_debit_amnt);
                inc_crdr = "৳ " + inc_crdr;

                String inc_crcr = formatter.format(inc_cr_credit_amnt);
                inc_crcr = "৳ " + inc_crcr;

                String inc_totbal = formatter.format(inc_bal);
                inc_totbal = "৳ " + inc_totbal;
                inc_totbal = inc_totbal.replace("-","");

                totalIncBfrDebit.setText(inc_bfdr);
                totalIncBfrCredit.setText(inc_bfcr);
                totalIncCurDebit.setText(inc_crdr);
                totalIncCurCredit.setText(inc_crcr);
                totalIncBalance.setText(inc_totbal);

                incomeStateAdapter = new IncomeExpenseStateAdapter(incomeStatementLists, IncomeExpenseStatement.this, firstDate,lastDate);
                incomeView.setAdapter(incomeStateAdapter);

                // expense statement
                double exp_bf_debit_amnt = 0.0;
                double exp_bf_credit_amnt = 0.0;
                double exp_cr_debit_amnt = 0.0;
                double exp_cr_credit_amnt = 0.0;
                double exp_bal = 0.0;
                for (int i = 0; i < expenseStatementLists.size(); i++) {
                    expenseStatementLists.get(i).setLevel_type(level);
                    if (!expenseStatementLists.get(i).getBfdr().isEmpty()) {
                        exp_bf_debit_amnt = exp_bf_debit_amnt + Double.parseDouble(expenseStatementLists.get(i).getBfdr());
                    }
                    if (!expenseStatementLists.get(i).getBfcr().isEmpty()) {
                        exp_bf_credit_amnt = exp_bf_credit_amnt + Double.parseDouble(expenseStatementLists.get(i).getBfcr());
                    }
                    if (!expenseStatementLists.get(i).getCurdr().isEmpty()) {
                        exp_cr_debit_amnt = exp_cr_debit_amnt + Double.parseDouble(expenseStatementLists.get(i).getCurdr());
                    }
                    if (!expenseStatementLists.get(i).getCurcr().isEmpty()) {
                        exp_cr_credit_amnt = exp_cr_credit_amnt + Double.parseDouble(expenseStatementLists.get(i).getCurcr());
                    }
                    if (!expenseStatementLists.get(i).getBfdr().isEmpty() && !expenseStatementLists.get(i).getBfcr().isEmpty()
                            && !expenseStatementLists.get(i).getCurdr().isEmpty() && !expenseStatementLists.get(i).getCurcr().isEmpty()) {
                        double b_debit = Double.parseDouble(expenseStatementLists.get(i).getBfdr());
                        double b_credit = Double.parseDouble(expenseStatementLists.get(i).getBfcr());
                        double c_debit = Double.parseDouble(expenseStatementLists.get(i).getCurdr());
                        double c_credit = Double.parseDouble(expenseStatementLists.get(i).getCurcr());

                        double balance = (b_credit + c_credit) - (b_debit + c_debit);
                        exp_bal = exp_bal + balance;
                    }
                }

                String exp_bfdr = formatter.format(exp_bf_debit_amnt);
                exp_bfdr = "৳ " + exp_bfdr;

                String exp_bfcr = formatter.format(exp_bf_credit_amnt);
                exp_bfcr = "৳ " + exp_bfcr;

                String exp_crdr = formatter.format(exp_cr_debit_amnt);
                exp_crdr = "৳ " + exp_crdr;

                String exp_crcr = formatter.format(exp_cr_credit_amnt);
                exp_crcr = "৳ " + exp_crcr;

                String exp_totbal = formatter.format(exp_bal);
                exp_totbal = "৳ " + exp_totbal;
                exp_totbal = exp_totbal.replace("-","");

                totalExpBfrDebit.setText(exp_bfdr);
                totalExpBfrCredit.setText(exp_bfcr);
                totalExpCurDebit.setText(exp_crdr);
                totalExpCurCredit.setText(exp_crcr);
                totalExpBalance.setText(exp_totbal);

                expenseStateAdapter = new IncomeExpenseStateAdapter(expenseStatementLists, IncomeExpenseStatement.this, firstDate,lastDate);
                expenseView.setAdapter(expenseStateAdapter);

                // summation
                double bf_sum = (inc_bf_credit_amnt - inc_bf_debit_amnt) - (exp_bf_debit_amnt - exp_bf_credit_amnt);
                String before_sum = formatter.format(bf_sum);
                before_sum = "৳ " + before_sum;

                if (bf_sum > 0.0) {
                    sumProfitLossBefore.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBefore.setText(before_sum);
                }
                else if (bf_sum == 0.0) {
                    sumProfitLossBefore.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBefore.setText(before_sum);
                }
                else {
                    sumProfitLossBefore.setTextColor(getColor(R.color.red_dark));
                    before_sum = before_sum.replace("-","");
                    sumProfitLossBefore.setText(before_sum);
                }

                double cur_sum = (inc_cr_credit_amnt - inc_cr_debit_amnt) - (exp_cr_debit_amnt - exp_cr_credit_amnt);
                String current_sum = formatter.format(cur_sum);
                current_sum = "৳ " + current_sum;

                if (cur_sum > 0.0) {
                    sumProfitLossDuring.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossDuring.setText(current_sum);
                }
                else if (cur_sum == 0.0) {
                    sumProfitLossDuring.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossDuring.setText(current_sum);
                }
                else {
                    sumProfitLossDuring.setTextColor(getColor(R.color.red_dark));
                    current_sum = current_sum.replace("-","");
                    sumProfitLossDuring.setText(current_sum);
                }

                double bal_sum = inc_bal + exp_bal;
                String balance_sum = formatter.format(bal_sum);
                balance_sum = "৳ " + balance_sum;

                if (bal_sum > 0.0) {
                    sumProfitLossBalance.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBalance.setText(balance_sum);
                }
                else if (bal_sum == 0.0) {
                    sumProfitLossBalance.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBalance.setText(balance_sum);
                }
                else {
                    sumProfitLossBalance.setTextColor(getColor(R.color.red_dark));
                    balance_sum = balance_sum.replace("-","");
                    sumProfitLossBalance.setText(balance_sum);
                }

                break;
            }
            case "2": {
                // income statement
                incFilteredLists = new ArrayList<>();
                for (int i = 0; i < incomeStatementLists.size(); i++) {
                    String lvl1 = incomeStatementLists.get(i).getLvl1();
                    String lvl2 = incomeStatementLists.get(i).getLvl2();
                    String lvl3 = incomeStatementLists.get(i).getLvl3();
                    String bfdr = incomeStatementLists.get(i).getBfdr();
                    String bfcr = incomeStatementLists.get(i).getBfcr();
                    String curdr = incomeStatementLists.get(i).getCurdr();
                    String curcr = incomeStatementLists.get(i).getCurcr();
                    String lg_ad_id = incomeStatementLists.get(i).getLg_ad_id();
                    String ah_code = incomeStatementLists.get(i).getAh_code();
                    String al1_code = incomeStatementLists.get(i).getAl1_code();
                    String al2_code = incomeStatementLists.get(i).getAl2_code();
                    String ad_code = incomeStatementLists.get(i).getAd_code();

                    incFilteredLists.add(new IncExpStatementList(lvl1, lvl2, lvl3, bfdr, bfcr, curdr, curcr, lg_ad_id, ah_code, al1_code, al2_code, ad_code, level));
                }

                boolean finished = false;
                while (!finished) {
                    boolean all_done = false;
                    boolean need_to_remove = false;
                    int index_need_to_remove = 0;
                    for (int i = 0; i < incFilteredLists.size(); i++) {
                        String al1_code = incFilteredLists.get(i).getAl1_code();
                        String al2_code = incFilteredLists.get(i).getAl2_code();

                        if (i == 0) {
                            if (i < incFilteredLists.size() - 1) {
                                int index = i + 1;
                                String next_al1_code = incFilteredLists.get(index).getAl1_code();
                                String next_al2_code = incFilteredLists.get(index).getAl2_code();
                                if (al1_code.equals(next_al1_code)) {
                                    if (al2_code.equals(next_al2_code)) {

                                        double bf_debit_amnt = Double.parseDouble(incFilteredLists.get(i).getBfdr());
                                        double bf_credit_amnt = Double.parseDouble(incFilteredLists.get(i).getBfcr());
                                        double cr_debit_amnt = Double.parseDouble(incFilteredLists.get(i).getCurdr());
                                        double cr_credit_amnt = Double.parseDouble(incFilteredLists.get(i).getCurcr());

                                        double next_bf_debit_amnt = Double.parseDouble(incFilteredLists.get(index).getBfdr());
                                        double next_bf_credit_amnt = Double.parseDouble(incFilteredLists.get(index).getBfcr());
                                        double next_cr_debit_amnt = Double.parseDouble(incFilteredLists.get(index).getCurdr());
                                        double next_cr_credit_amnt = Double.parseDouble(incFilteredLists.get(index).getCurcr());

                                        bf_debit_amnt = bf_debit_amnt + next_bf_debit_amnt;
                                        bf_credit_amnt = bf_credit_amnt + next_bf_credit_amnt;
                                        cr_debit_amnt = cr_debit_amnt + next_cr_debit_amnt;
                                        cr_credit_amnt = cr_credit_amnt + next_cr_credit_amnt;

                                        incFilteredLists.get(i).setBfdr(String.valueOf(bf_debit_amnt));
                                        incFilteredLists.get(i).setBfcr(String.valueOf(bf_credit_amnt));
                                        incFilteredLists.get(i).setCurdr(String.valueOf(cr_debit_amnt));
                                        incFilteredLists.get(i).setCurcr(String.valueOf(cr_credit_amnt));

                                        index_need_to_remove = index;
                                        need_to_remove = true;
                                        break;
                                    }
                                }
                            } else {
                                all_done = true;
                            }
                        } else if (i == incFilteredLists.size() - 1) {
                            all_done = true;
                        } else {
                            int index = i + 1;
                            String next_al1_code = incFilteredLists.get(index).getAl1_code();
                            String next_al2_code = incFilteredLists.get(index).getAl2_code();
                            if (al1_code.equals(next_al1_code)) {
                                if (al2_code.equals(next_al2_code)) {

                                    double bf_debit_amnt = Double.parseDouble(incFilteredLists.get(i).getBfdr());
                                    double bf_credit_amnt = Double.parseDouble(incFilteredLists.get(i).getBfcr());
                                    double cr_debit_amnt = Double.parseDouble(incFilteredLists.get(i).getCurdr());
                                    double cr_credit_amnt = Double.parseDouble(incFilteredLists.get(i).getCurcr());

                                    double next_bf_debit_amnt = Double.parseDouble(incFilteredLists.get(index).getBfdr());
                                    double next_bf_credit_amnt = Double.parseDouble(incFilteredLists.get(index).getBfcr());
                                    double next_cr_debit_amnt = Double.parseDouble(incFilteredLists.get(index).getCurdr());
                                    double next_cr_credit_amnt = Double.parseDouble(incFilteredLists.get(index).getCurcr());

                                    bf_debit_amnt = bf_debit_amnt + next_bf_debit_amnt;
                                    bf_credit_amnt = bf_credit_amnt + next_bf_credit_amnt;
                                    cr_debit_amnt = cr_debit_amnt + next_cr_debit_amnt;
                                    cr_credit_amnt = cr_credit_amnt + next_cr_credit_amnt;

                                    incFilteredLists.get(i).setBfdr(String.valueOf(bf_debit_amnt));
                                    incFilteredLists.get(i).setBfcr(String.valueOf(bf_credit_amnt));
                                    incFilteredLists.get(i).setCurdr(String.valueOf(cr_debit_amnt));
                                    incFilteredLists.get(i).setCurcr(String.valueOf(cr_credit_amnt));

                                    index_need_to_remove = index;
                                    need_to_remove = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (need_to_remove) {
                        incFilteredLists.remove(index_need_to_remove);
                    }

                    if (all_done) {
                        finished = true;
                    }
                }

                double inc_bf_debit_amnt = 0.0;
                double inc_bf_credit_amnt = 0.0;
                double inc_cr_debit_amnt = 0.0;
                double inc_cr_credit_amnt = 0.0;
                double inc_bal = 0.0;
                for (int i = 0; i < incFilteredLists.size(); i++) {
                    if (!incFilteredLists.get(i).getBfdr().isEmpty()) {
                        inc_bf_debit_amnt = inc_bf_debit_amnt + Double.parseDouble(incFilteredLists.get(i).getBfdr());
                    }
                    if (!incFilteredLists.get(i).getBfcr().isEmpty()) {
                        inc_bf_credit_amnt = inc_bf_credit_amnt + Double.parseDouble(incFilteredLists.get(i).getBfcr());
                    }
                    if (!incFilteredLists.get(i).getCurdr().isEmpty()) {
                        inc_cr_debit_amnt = inc_cr_debit_amnt + Double.parseDouble(incFilteredLists.get(i).getCurdr());
                    }
                    if (!incFilteredLists.get(i).getCurcr().isEmpty()) {
                        inc_cr_credit_amnt = inc_cr_credit_amnt + Double.parseDouble(incFilteredLists.get(i).getCurcr());
                    }
                    if (!incFilteredLists.get(i).getBfdr().isEmpty() && !incFilteredLists.get(i).getBfcr().isEmpty()
                            && !incFilteredLists.get(i).getCurdr().isEmpty() && !incFilteredLists.get(i).getCurcr().isEmpty()) {
                        double b_debit = Double.parseDouble(incFilteredLists.get(i).getBfdr());
                        double b_credit = Double.parseDouble(incFilteredLists.get(i).getBfcr());
                        double c_debit = Double.parseDouble(incFilteredLists.get(i).getCurdr());
                        double c_credit = Double.parseDouble(incFilteredLists.get(i).getCurcr());

                        double balance = (b_credit + c_credit) - (b_debit + c_debit);
                        inc_bal = inc_bal + balance;
                    }
                }

                String inc_bfdr = formatter.format(inc_bf_debit_amnt);
                inc_bfdr = "৳ " + inc_bfdr;

                String inc_bfcr = formatter.format(inc_bf_credit_amnt);
                inc_bfcr = "৳ " + inc_bfcr;

                String crdr = formatter.format(inc_cr_debit_amnt);
                crdr = "৳ " + crdr;

                String crcr = formatter.format(inc_cr_credit_amnt);
                crcr = "৳ " + crcr;

                String totbal = formatter.format(inc_bal);
                totbal = "৳ " + totbal;
                totbal = totbal.replace("-","");

                totalIncBfrDebit.setText(inc_bfdr);
                totalIncBfrCredit.setText(inc_bfcr);
                totalIncCurDebit.setText(crdr);
                totalIncCurCredit.setText(crcr);
                totalIncBalance.setText(totbal);

                incomeStateAdapter = new IncomeExpenseStateAdapter(incFilteredLists, IncomeExpenseStatement.this, firstDate,lastDate);
                incomeView.setAdapter(incomeStateAdapter);

                // expense statement
                expFilteredLists = new ArrayList<>();
                for (int i = 0; i < expenseStatementLists.size(); i++) {
                    String lvl1 = expenseStatementLists.get(i).getLvl1();
                    String lvl2 = expenseStatementLists.get(i).getLvl2();
                    String lvl3 = expenseStatementLists.get(i).getLvl3();
                    String bfdr = expenseStatementLists.get(i).getBfdr();
                    String bfcr = expenseStatementLists.get(i).getBfcr();
                    String curdr = expenseStatementLists.get(i).getCurdr();
                    String curcr = expenseStatementLists.get(i).getCurcr();
                    String lg_ad_id = expenseStatementLists.get(i).getLg_ad_id();
                    String ah_code = expenseStatementLists.get(i).getAh_code();
                    String al1_code = expenseStatementLists.get(i).getAl1_code();
                    String al2_code = expenseStatementLists.get(i).getAl2_code();
                    String ad_code = expenseStatementLists.get(i).getAd_code();

                    expFilteredLists.add(new IncExpStatementList(lvl1, lvl2, lvl3, bfdr, bfcr, curdr, curcr, lg_ad_id, ah_code, al1_code, al2_code, ad_code, level));
                }

                boolean expfinished = false;
                while (!expfinished) {
                    boolean all_done = false;
                    boolean need_to_remove = false;
                    int index_need_to_remove = 0;
                    for (int i = 0; i < expFilteredLists.size(); i++) {
                        String al1_code = expFilteredLists.get(i).getAl1_code();
                        String al2_code = expFilteredLists.get(i).getAl2_code();

                        if (i == 0) {
                            if (i < expFilteredLists.size() - 1) {
                                int index = i + 1;
                                String next_al1_code = expFilteredLists.get(index).getAl1_code();
                                String next_al2_code = expFilteredLists.get(index).getAl2_code();
                                if (al1_code.equals(next_al1_code)) {
                                    if (al2_code.equals(next_al2_code)) {

                                        double exp_bf_debit_amnt = Double.parseDouble(expFilteredLists.get(i).getBfdr());
                                        double exp_bf_credit_amnt = Double.parseDouble(expFilteredLists.get(i).getBfcr());
                                        double exp_cr_debit_amnt = Double.parseDouble(expFilteredLists.get(i).getCurdr());
                                        double exp_cr_credit_amnt = Double.parseDouble(expFilteredLists.get(i).getCurcr());

                                        double next_bf_debit_amnt = Double.parseDouble(expFilteredLists.get(index).getBfdr());
                                        double next_bf_credit_amnt = Double.parseDouble(expFilteredLists.get(index).getBfcr());
                                        double next_cr_debit_amnt = Double.parseDouble(expFilteredLists.get(index).getCurdr());
                                        double next_cr_credit_amnt = Double.parseDouble(expFilteredLists.get(index).getCurcr());

                                        exp_bf_debit_amnt = exp_bf_debit_amnt + next_bf_debit_amnt;
                                        exp_bf_credit_amnt = exp_bf_credit_amnt + next_bf_credit_amnt;
                                        exp_cr_debit_amnt = exp_cr_debit_amnt + next_cr_debit_amnt;
                                        exp_cr_credit_amnt = exp_cr_credit_amnt + next_cr_credit_amnt;

                                        expFilteredLists.get(i).setBfdr(String.valueOf(exp_bf_debit_amnt));
                                        expFilteredLists.get(i).setBfcr(String.valueOf(exp_bf_credit_amnt));
                                        expFilteredLists.get(i).setCurdr(String.valueOf(exp_cr_debit_amnt));
                                        expFilteredLists.get(i).setCurcr(String.valueOf(exp_cr_credit_amnt));

                                        index_need_to_remove = index;
                                        need_to_remove = true;
                                        break;
                                    }
                                }
                            } else {
                                all_done = true;
                            }
                        }
                        else if (i == expFilteredLists.size() - 1) {
                            all_done = true;
                        }
                        else {
                            int index = i + 1;
                            String next_al1_code = expFilteredLists.get(index).getAl1_code();
                            String next_al2_code = expFilteredLists.get(index).getAl2_code();
                            if (al1_code.equals(next_al1_code)) {
                                if (al2_code.equals(next_al2_code)) {

                                    double exp_bf_debit_amnt = Double.parseDouble(expFilteredLists.get(i).getBfdr());
                                    double exp_bf_credit_amnt = Double.parseDouble(expFilteredLists.get(i).getBfcr());
                                    double exp_cr_debit_amnt = Double.parseDouble(expFilteredLists.get(i).getCurdr());
                                    double exp_cr_credit_amnt = Double.parseDouble(expFilteredLists.get(i).getCurcr());

                                    double next_bf_debit_amnt = Double.parseDouble(expFilteredLists.get(index).getBfdr());
                                    double next_bf_credit_amnt = Double.parseDouble(expFilteredLists.get(index).getBfcr());
                                    double next_cr_debit_amnt = Double.parseDouble(expFilteredLists.get(index).getCurdr());
                                    double next_cr_credit_amnt = Double.parseDouble(expFilteredLists.get(index).getCurcr());

                                    exp_bf_debit_amnt = exp_bf_debit_amnt + next_bf_debit_amnt;
                                    exp_bf_credit_amnt = exp_bf_credit_amnt + next_bf_credit_amnt;
                                    exp_cr_debit_amnt = exp_cr_debit_amnt + next_cr_debit_amnt;
                                    exp_cr_credit_amnt = exp_cr_credit_amnt + next_cr_credit_amnt;

                                    expFilteredLists.get(i).setBfdr(String.valueOf(exp_bf_debit_amnt));
                                    expFilteredLists.get(i).setBfcr(String.valueOf(exp_bf_credit_amnt));
                                    expFilteredLists.get(i).setCurdr(String.valueOf(exp_cr_debit_amnt));
                                    expFilteredLists.get(i).setCurcr(String.valueOf(exp_cr_credit_amnt));

                                    index_need_to_remove = index;
                                    need_to_remove = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (need_to_remove) {
                        expFilteredLists.remove(index_need_to_remove);
                    }

                    if (all_done) {
                        expfinished = true;
                    }
                }

                double exp_bf_debit_amnt = 0.0;
                double exp_bf_credit_amnt = 0.0;
                double exp_cr_debit_amnt = 0.0;
                double exp_cr_credit_amnt = 0.0;
                double exp_bal = 0.0;
                for (int i = 0; i < expFilteredLists.size(); i++) {
                    if (!expFilteredLists.get(i).getBfdr().isEmpty()) {
                        exp_bf_debit_amnt = exp_bf_debit_amnt + Double.parseDouble(expFilteredLists.get(i).getBfdr());
                    }
                    if (!expFilteredLists.get(i).getBfcr().isEmpty()) {
                        exp_bf_credit_amnt = exp_bf_credit_amnt + Double.parseDouble(expFilteredLists.get(i).getBfcr());
                    }
                    if (!expFilteredLists.get(i).getCurdr().isEmpty()) {
                        exp_cr_debit_amnt = exp_cr_debit_amnt + Double.parseDouble(expFilteredLists.get(i).getCurdr());
                    }
                    if (!expFilteredLists.get(i).getCurcr().isEmpty()) {
                        exp_cr_credit_amnt = exp_cr_credit_amnt + Double.parseDouble(expFilteredLists.get(i).getCurcr());
                    }
                    if (!expFilteredLists.get(i).getBfdr().isEmpty() && !expFilteredLists.get(i).getBfcr().isEmpty()
                            && !expFilteredLists.get(i).getCurdr().isEmpty() && !expFilteredLists.get(i).getCurcr().isEmpty()) {
                        double b_debit = Double.parseDouble(expFilteredLists.get(i).getBfdr());
                        double b_credit = Double.parseDouble(expFilteredLists.get(i).getBfcr());
                        double c_debit = Double.parseDouble(expFilteredLists.get(i).getCurdr());
                        double c_credit = Double.parseDouble(expFilteredLists.get(i).getCurcr());

                        double balance = (b_credit + c_credit) - (b_debit + c_debit);
                        exp_bal = exp_bal + balance;
                    }
                }

                String exp_bfdr = formatter.format(exp_bf_debit_amnt);
                exp_bfdr = "৳ " + exp_bfdr;

                String exp_bfcr = formatter.format(exp_bf_credit_amnt);
                exp_bfcr = "৳ " + exp_bfcr;

                String exp_crdr = formatter.format(exp_cr_debit_amnt);
                exp_crdr = "৳ " + exp_crdr;

                String exp_crcr = formatter.format(exp_cr_credit_amnt);
                exp_crcr = "৳ " + exp_crcr;

                String exp_totbal = formatter.format(exp_bal);
                exp_totbal = "৳ " + exp_totbal;
                exp_totbal = exp_totbal.replace("-","");

                totalExpBfrDebit.setText(exp_bfdr);
                totalExpBfrCredit.setText(exp_bfcr);
                totalExpCurDebit.setText(exp_crdr);
                totalExpCurCredit.setText(exp_crcr);
                totalExpBalance.setText(exp_totbal);

                expenseStateAdapter = new IncomeExpenseStateAdapter(expFilteredLists, IncomeExpenseStatement.this, firstDate,lastDate);
                expenseView.setAdapter(expenseStateAdapter);

                // summation
                double bf_sum = (inc_bf_credit_amnt - inc_bf_debit_amnt) - (exp_bf_debit_amnt - exp_bf_credit_amnt);
                String before_sum = formatter.format(bf_sum);
                before_sum = "৳ " + before_sum;

                if (bf_sum > 0.0) {
                    sumProfitLossBefore.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBefore.setText(before_sum);
                }
                else if (bf_sum == 0.0) {
                    sumProfitLossBefore.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBefore.setText(before_sum);
                }
                else {
                    sumProfitLossBefore.setTextColor(getColor(R.color.red_dark));
                    before_sum = before_sum.replace("-","");
                    sumProfitLossBefore.setText(before_sum);
                }

                double cur_sum = (inc_cr_credit_amnt - inc_cr_debit_amnt) - (exp_cr_debit_amnt - exp_cr_credit_amnt);
                String current_sum = formatter.format(cur_sum);
                current_sum = "৳ " + current_sum;

                if (cur_sum > 0.0) {
                    sumProfitLossDuring.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossDuring.setText(current_sum);
                }
                else if (cur_sum == 0.0) {
                    sumProfitLossDuring.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossDuring.setText(current_sum);
                }
                else {
                    sumProfitLossDuring.setTextColor(getColor(R.color.red_dark));
                    current_sum = current_sum.replace("-","");
                    sumProfitLossDuring.setText(current_sum);
                }

                double bal_sum = inc_bal + exp_bal;
                String balance_sum = formatter.format(bal_sum);
                balance_sum = "৳ " + balance_sum;

                if (bal_sum > 0.0) {
                    sumProfitLossBalance.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBalance.setText(balance_sum);
                }
                else if (bal_sum == 0.0) {
                    sumProfitLossBalance.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBalance.setText(balance_sum);
                }
                else {
                    sumProfitLossBalance.setTextColor(getColor(R.color.red_dark));
                    balance_sum = balance_sum.replace("-","");
                    sumProfitLossBalance.setText(balance_sum);
                }

                break;
            }
            case "1": {
                // income statement
                incFilteredLists = new ArrayList<>();
                for (int i = 0; i < incomeStatementLists.size(); i++) {
                    String lvl1 = incomeStatementLists.get(i).getLvl1();
                    String lvl2 = incomeStatementLists.get(i).getLvl2();
                    String lvl3 = incomeStatementLists.get(i).getLvl3();
                    String bfdr = incomeStatementLists.get(i).getBfdr();
                    String bfcr = incomeStatementLists.get(i).getBfcr();
                    String curdr = incomeStatementLists.get(i).getCurdr();
                    String curcr = incomeStatementLists.get(i).getCurcr();
                    String lg_ad_id = incomeStatementLists.get(i).getLg_ad_id();
                    String ah_code = incomeStatementLists.get(i).getAh_code();
                    String al1_code = incomeStatementLists.get(i).getAl1_code();
                    String al2_code = incomeStatementLists.get(i).getAl2_code();
                    String ad_code = incomeStatementLists.get(i).getAd_code();

                    incFilteredLists.add(new IncExpStatementList(lvl1, lvl2, lvl3, bfdr, bfcr, curdr, curcr, lg_ad_id, ah_code, al1_code, al2_code, ad_code, level));
                }

                boolean finished = false;
                while (!finished) {
                    boolean all_done = false;
                    boolean need_to_remove = false;
                    int index_need_to_remove = 0;
                    for (int i = 0; i < incFilteredLists.size(); i++) {
                        String al1_code = incFilteredLists.get(i).getAl1_code();

                        if (i == 0) {
                            if (i < incFilteredLists.size() - 1) {
                                int index = i + 1;
                                String next_al1_code = incFilteredLists.get(index).getAl1_code();
                                if (al1_code.equals(next_al1_code)) {
                                    double bf_debit_amnt = Double.parseDouble(incFilteredLists.get(i).getBfdr());
                                    double bf_credit_amnt = Double.parseDouble(incFilteredLists.get(i).getBfcr());
                                    double cr_debit_amnt = Double.parseDouble(incFilteredLists.get(i).getCurdr());
                                    double cr_credit_amnt = Double.parseDouble(incFilteredLists.get(i).getCurcr());

                                    double next_bf_debit_amnt = Double.parseDouble(incFilteredLists.get(index).getBfdr());
                                    double next_bf_credit_amnt = Double.parseDouble(incFilteredLists.get(index).getBfcr());
                                    double next_cr_debit_amnt = Double.parseDouble(incFilteredLists.get(index).getCurdr());
                                    double next_cr_credit_amnt = Double.parseDouble(incFilteredLists.get(index).getCurcr());

                                    bf_debit_amnt = bf_debit_amnt + next_bf_debit_amnt;
                                    bf_credit_amnt = bf_credit_amnt + next_bf_credit_amnt;
                                    cr_debit_amnt = cr_debit_amnt + next_cr_debit_amnt;
                                    cr_credit_amnt = cr_credit_amnt + next_cr_credit_amnt;

                                    incFilteredLists.get(i).setBfdr(String.valueOf(bf_debit_amnt));
                                    incFilteredLists.get(i).setBfcr(String.valueOf(bf_credit_amnt));
                                    incFilteredLists.get(i).setCurdr(String.valueOf(cr_debit_amnt));
                                    incFilteredLists.get(i).setCurcr(String.valueOf(cr_credit_amnt));

                                    index_need_to_remove = index;
                                    need_to_remove = true;
                                    break;
                                }
                            } else {
                                all_done = true;
                            }
                        } else if (i == incFilteredLists.size() - 1) {
                            all_done = true;
                        } else {
                            int index = i + 1;
                            String next_al1_code = incFilteredLists.get(index).getAl1_code();
                            if (al1_code.equals(next_al1_code)) {
                                double bf_debit_amnt = Double.parseDouble(incFilteredLists.get(i).getBfdr());
                                double bf_credit_amnt = Double.parseDouble(incFilteredLists.get(i).getBfcr());
                                double cr_debit_amnt = Double.parseDouble(incFilteredLists.get(i).getCurdr());
                                double cr_credit_amnt = Double.parseDouble(incFilteredLists.get(i).getCurcr());

                                double next_bf_debit_amnt = Double.parseDouble(incFilteredLists.get(index).getBfdr());
                                double next_bf_credit_amnt = Double.parseDouble(incFilteredLists.get(index).getBfcr());
                                double next_cr_debit_amnt = Double.parseDouble(incFilteredLists.get(index).getCurdr());
                                double next_cr_credit_amnt = Double.parseDouble(incFilteredLists.get(index).getCurcr());

                                bf_debit_amnt = bf_debit_amnt + next_bf_debit_amnt;
                                bf_credit_amnt = bf_credit_amnt + next_bf_credit_amnt;
                                cr_debit_amnt = cr_debit_amnt + next_cr_debit_amnt;
                                cr_credit_amnt = cr_credit_amnt + next_cr_credit_amnt;

                                incFilteredLists.get(i).setBfdr(String.valueOf(bf_debit_amnt));
                                incFilteredLists.get(i).setBfcr(String.valueOf(bf_credit_amnt));
                                incFilteredLists.get(i).setCurdr(String.valueOf(cr_debit_amnt));
                                incFilteredLists.get(i).setCurcr(String.valueOf(cr_credit_amnt));

                                index_need_to_remove = index;
                                need_to_remove = true;
                                break;
                            }
                        }
                    }

                    if (need_to_remove) {
                        incFilteredLists.remove(index_need_to_remove);
                    }

                    if (all_done) {
                        finished = true;
                    }
                }

                double inc_bf_debit_amnt = 0.0;
                double inc_bf_credit_amnt = 0.0;
                double inc_cr_debit_amnt = 0.0;
                double inc_cr_credit_amnt = 0.0;
                double inc_bal = 0.0;
                for (int i = 0; i < incFilteredLists.size(); i++) {
                    if (!incFilteredLists.get(i).getBfdr().isEmpty()) {
                        inc_bf_debit_amnt = inc_bf_debit_amnt + Double.parseDouble(incFilteredLists.get(i).getBfdr());
                    }
                    if (!incFilteredLists.get(i).getBfcr().isEmpty()) {
                        inc_bf_credit_amnt = inc_bf_credit_amnt + Double.parseDouble(incFilteredLists.get(i).getBfcr());
                    }
                    if (!incFilteredLists.get(i).getCurdr().isEmpty()) {
                        inc_cr_debit_amnt = inc_cr_debit_amnt + Double.parseDouble(incFilteredLists.get(i).getCurdr());
                    }
                    if (!incFilteredLists.get(i).getCurcr().isEmpty()) {
                        inc_cr_credit_amnt = inc_cr_credit_amnt + Double.parseDouble(incFilteredLists.get(i).getCurcr());
                    }
                    if (!incFilteredLists.get(i).getBfdr().isEmpty() && !incFilteredLists.get(i).getBfcr().isEmpty()
                            && !incFilteredLists.get(i).getCurdr().isEmpty() && !incFilteredLists.get(i).getCurcr().isEmpty()) {
                        double b_debit = Double.parseDouble(incFilteredLists.get(i).getBfdr());
                        double b_credit = Double.parseDouble(incFilteredLists.get(i).getBfcr());
                        double c_debit = Double.parseDouble(incFilteredLists.get(i).getCurdr());
                        double c_credit = Double.parseDouble(incFilteredLists.get(i).getCurcr());

                        double balance = (b_credit + c_credit) - (b_debit + c_debit);
                        inc_bal = inc_bal + balance;
                    }
                }

                String bfdr = formatter.format(inc_bf_debit_amnt);
                bfdr = "৳ " + bfdr;

                String bfcr = formatter.format(inc_bf_credit_amnt);
                bfcr = "৳ " + bfcr;

                String crdr = formatter.format(inc_cr_debit_amnt);
                crdr = "৳ " + crdr;

                String crcr = formatter.format(inc_cr_credit_amnt);
                crcr = "৳ " + crcr;

                String totbal = formatter.format(inc_bal);
                totbal = "৳ " + totbal;
                totbal = totbal.replace("-","");

                totalIncBfrDebit.setText(bfdr);
                totalIncBfrCredit.setText(bfcr);
                totalIncCurDebit.setText(crdr);
                totalIncCurCredit.setText(crcr);
                totalIncBalance.setText(totbal);

                incomeStateAdapter = new IncomeExpenseStateAdapter(incFilteredLists, IncomeExpenseStatement.this, firstDate,lastDate);
                incomeView.setAdapter(incomeStateAdapter);

                // expense statement
                expFilteredLists = new ArrayList<>();
                for (int i = 0; i < expenseStatementLists.size(); i++) {
                    String lvl1 = expenseStatementLists.get(i).getLvl1();
                    String lvl2 = expenseStatementLists.get(i).getLvl2();
                    String lvl3 = expenseStatementLists.get(i).getLvl3();
                    String e_bfdr = expenseStatementLists.get(i).getBfdr();
                    String e_bfcr = expenseStatementLists.get(i).getBfcr();
                    String curdr = expenseStatementLists.get(i).getCurdr();
                    String curcr = expenseStatementLists.get(i).getCurcr();
                    String lg_ad_id = expenseStatementLists.get(i).getLg_ad_id();
                    String ah_code = expenseStatementLists.get(i).getAh_code();
                    String al1_code = expenseStatementLists.get(i).getAl1_code();
                    String al2_code = expenseStatementLists.get(i).getAl2_code();
                    String ad_code = expenseStatementLists.get(i).getAd_code();

                    expFilteredLists.add(new IncExpStatementList(lvl1, lvl2, lvl3, e_bfdr, e_bfcr, curdr, curcr, lg_ad_id, ah_code, al1_code, al2_code, ad_code, level));
                }

                boolean expfinished = false;
                while (!expfinished) {
                    boolean all_done = false;
                    boolean need_to_remove = false;
                    int index_need_to_remove = 0;
                    for (int i = 0; i < expFilteredLists.size(); i++) {
                        String al1_code = expFilteredLists.get(i).getAl1_code();

                        if (i == 0) {
                            if (i < expFilteredLists.size() - 1) {
                                int index = i + 1;
                                String next_al1_code = expFilteredLists.get(index).getAl1_code();
                                if (al1_code.equals(next_al1_code)) {
                                    double exp_bf_debit_amnt = Double.parseDouble(expFilteredLists.get(i).getBfdr());
                                    double exp_bf_credit_amnt = Double.parseDouble(expFilteredLists.get(i).getBfcr());
                                    double exp_cr_debit_amnt = Double.parseDouble(expFilteredLists.get(i).getCurdr());
                                    double exp_cr_credit_amnt = Double.parseDouble(expFilteredLists.get(i).getCurcr());

                                    double next_bf_debit_amnt = Double.parseDouble(expFilteredLists.get(index).getBfdr());
                                    double next_bf_credit_amnt = Double.parseDouble(expFilteredLists.get(index).getBfcr());
                                    double next_cr_debit_amnt = Double.parseDouble(expFilteredLists.get(index).getCurdr());
                                    double next_cr_credit_amnt = Double.parseDouble(expFilteredLists.get(index).getCurcr());

                                    exp_bf_debit_amnt = exp_bf_debit_amnt + next_bf_debit_amnt;
                                    exp_bf_credit_amnt = exp_bf_credit_amnt + next_bf_credit_amnt;
                                    exp_cr_debit_amnt = exp_cr_debit_amnt + next_cr_debit_amnt;
                                    exp_cr_credit_amnt = exp_cr_credit_amnt + next_cr_credit_amnt;

                                    expFilteredLists.get(i).setBfdr(String.valueOf(exp_bf_debit_amnt));
                                    expFilteredLists.get(i).setBfcr(String.valueOf(exp_bf_credit_amnt));
                                    expFilteredLists.get(i).setCurdr(String.valueOf(exp_cr_debit_amnt));
                                    expFilteredLists.get(i).setCurcr(String.valueOf(exp_cr_credit_amnt));

                                    index_need_to_remove = index;
                                    need_to_remove = true;
                                    break;
                                }
                            } else {
                                all_done = true;
                            }
                        } else if (i == expFilteredLists.size() - 1) {
                            all_done = true;
                        } else {
                            int index = i + 1;
                            String next_al1_code = expFilteredLists.get(index).getAl1_code();
                            if (al1_code.equals(next_al1_code)) {
                                double exp_bf_debit_amnt = Double.parseDouble(expFilteredLists.get(i).getBfdr());
                                double exp_bf_credit_amnt = Double.parseDouble(expFilteredLists.get(i).getBfcr());
                                double exp_cr_debit_amnt = Double.parseDouble(expFilteredLists.get(i).getCurdr());
                                double exp_cr_credit_amnt = Double.parseDouble(expFilteredLists.get(i).getCurcr());

                                double next_bf_debit_amnt = Double.parseDouble(expFilteredLists.get(index).getBfdr());
                                double next_bf_credit_amnt = Double.parseDouble(expFilteredLists.get(index).getBfcr());
                                double next_cr_debit_amnt = Double.parseDouble(expFilteredLists.get(index).getCurdr());
                                double next_cr_credit_amnt = Double.parseDouble(expFilteredLists.get(index).getCurcr());

                                exp_bf_debit_amnt = exp_bf_debit_amnt + next_bf_debit_amnt;
                                exp_bf_credit_amnt = exp_bf_credit_amnt + next_bf_credit_amnt;
                                exp_cr_debit_amnt = exp_cr_debit_amnt + next_cr_debit_amnt;
                                exp_cr_credit_amnt = exp_cr_credit_amnt + next_cr_credit_amnt;

                                expFilteredLists.get(i).setBfdr(String.valueOf(exp_bf_debit_amnt));
                                expFilteredLists.get(i).setBfcr(String.valueOf(exp_bf_credit_amnt));
                                expFilteredLists.get(i).setCurdr(String.valueOf(exp_cr_debit_amnt));
                                expFilteredLists.get(i).setCurcr(String.valueOf(exp_cr_credit_amnt));

                                index_need_to_remove = index;
                                need_to_remove = true;
                                break;
                            }
                        }
                    }

                    if (need_to_remove) {
                        expFilteredLists.remove(index_need_to_remove);
                    }

                    if (all_done) {
                        expfinished = true;
                    }
                }

                double exp_bf_debit_amnt = 0.0;
                double exp_bf_credit_amnt = 0.0;
                double exp_cr_debit_amnt = 0.0;
                double exp_cr_credit_amnt = 0.0;
                double exp_bal = 0.0;
                for (int i = 0; i < expFilteredLists.size(); i++) {
                    if (!expFilteredLists.get(i).getBfdr().isEmpty()) {
                        exp_bf_debit_amnt = exp_bf_debit_amnt + Double.parseDouble(expFilteredLists.get(i).getBfdr());
                    }
                    if (!expFilteredLists.get(i).getBfcr().isEmpty()) {
                        exp_bf_credit_amnt = exp_bf_credit_amnt + Double.parseDouble(expFilteredLists.get(i).getBfcr());
                    }
                    if (!expFilteredLists.get(i).getCurdr().isEmpty()) {
                        exp_cr_debit_amnt = exp_cr_debit_amnt + Double.parseDouble(expFilteredLists.get(i).getCurdr());
                    }
                    if (!expFilteredLists.get(i).getCurcr().isEmpty()) {
                        exp_cr_credit_amnt = exp_cr_credit_amnt + Double.parseDouble(expFilteredLists.get(i).getCurcr());
                    }
                    if (!expFilteredLists.get(i).getBfdr().isEmpty() && !expFilteredLists.get(i).getBfcr().isEmpty()
                            && !expFilteredLists.get(i).getCurdr().isEmpty() && !expFilteredLists.get(i).getCurcr().isEmpty()) {
                        double b_debit = Double.parseDouble(expFilteredLists.get(i).getBfdr());
                        double b_credit = Double.parseDouble(expFilteredLists.get(i).getBfcr());
                        double c_debit = Double.parseDouble(expFilteredLists.get(i).getCurdr());
                        double c_credit = Double.parseDouble(expFilteredLists.get(i).getCurcr());

                        double balance = (b_credit + c_credit) - (b_debit + c_debit);
                        exp_bal = exp_bal + balance;
                    }
                }

                String exp_bfdr = formatter.format(exp_bf_debit_amnt);
                exp_bfdr = "৳ " + exp_bfdr;

                String exp_bfcr = formatter.format(exp_bf_credit_amnt);
                exp_bfcr = "৳ " + exp_bfcr;

                String exp_crdr = formatter.format(exp_cr_debit_amnt);
                exp_crdr = "৳ " + exp_crdr;

                String exp_crcr = formatter.format(exp_cr_credit_amnt);
                exp_crcr = "৳ " + exp_crcr;

                String exp_totbal = formatter.format(exp_bal);
                exp_totbal = "৳ " + exp_totbal;
                exp_totbal = exp_totbal.replace("-","");

                totalExpBfrDebit.setText(exp_bfdr);
                totalExpBfrCredit.setText(exp_bfcr);
                totalExpCurDebit.setText(exp_crdr);
                totalExpCurCredit.setText(exp_crcr);
                totalExpBalance.setText(exp_totbal);

                expenseStateAdapter = new IncomeExpenseStateAdapter(expFilteredLists, IncomeExpenseStatement.this, firstDate,lastDate);
                expenseView.setAdapter(expenseStateAdapter);

                // summation
                double bf_sum = (inc_bf_credit_amnt - inc_bf_debit_amnt) - (exp_bf_debit_amnt - exp_bf_credit_amnt);
                String before_sum = formatter.format(bf_sum);
                before_sum = "৳ " + before_sum;

                if (bf_sum > 0.0) {
                    sumProfitLossBefore.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBefore.setText(before_sum);
                }
                else if (bf_sum == 0.0) {
                    sumProfitLossBefore.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBefore.setText(before_sum);
                }
                else {
                    sumProfitLossBefore.setTextColor(getColor(R.color.red_dark));
                    before_sum = before_sum.replace("-","");
                    sumProfitLossBefore.setText(before_sum);
                }

                double cur_sum = (inc_cr_credit_amnt - inc_cr_debit_amnt) - (exp_cr_debit_amnt - exp_cr_credit_amnt);
                String current_sum = formatter.format(cur_sum);
                current_sum = "৳ " + current_sum;

                if (cur_sum > 0.0) {
                    sumProfitLossDuring.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossDuring.setText(current_sum);
                }
                else if (cur_sum == 0.0) {
                    sumProfitLossDuring.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossDuring.setText(current_sum);
                }
                else {
                    sumProfitLossDuring.setTextColor(getColor(R.color.red_dark));
                    current_sum = current_sum.replace("-","");
                    sumProfitLossDuring.setText(current_sum);
                }

                double bal_sum = inc_bal + exp_bal;
                String balance_sum = formatter.format(bal_sum);
                balance_sum = "৳ " + balance_sum;

                if (bal_sum > 0.0) {
                    sumProfitLossBalance.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBalance.setText(balance_sum);
                }
                else if (bal_sum == 0.0) {
                    sumProfitLossBalance.setTextColor(getColor(R.color.green_sea));
                    sumProfitLossBalance.setText(balance_sum);
                }
                else {
                    sumProfitLossBalance.setTextColor(getColor(R.color.red_dark));
                    balance_sum = balance_sum.replace("-","");
                    sumProfitLossBalance.setText(balance_sum);
                }

                break;
            }
        }
    }

    public void getIncomeExpList() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;
        total_income = "0";
        total_expense = "0";

        incExpPieEntry = new ArrayList<>();
        incomeStatementLists = new ArrayList<>();
        expenseStatementLists = new ArrayList<>();

        String incExpUrl = pre_url_api+"acc_dashboard/getIncomeExpense?first_date="+firstDate+"&end_date="+lastDate;
        String incStateUrl = pre_url_api+"acc_dashboard/getNIncomeStatement?begin_date="+firstDate+"&end_date="+lastDate;
        String expStateUrl = pre_url_api+"acc_dashboard/getNExpenseStatement?begin_date="+firstDate+"&end_date="+lastDate;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest expStateRequest = new StringRequest(Request.Method.GET, expStateUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String lvl1 = info.getString("lvl1")
                                .equals("null") ? "" : info.getString("lvl1");
                        String lvl2 = info.getString("lvl2")
                                .equals("null") ? "" : info.getString("lvl2");
                        String lvl3 = info.getString("lvl3")
                                .equals("null") ? "" : info.getString("lvl3");
                        String bfdr = info.getString("bfdr")
                                .equals("null") ? "0" : info.getString("bfdr");
                        String bfcr = info.getString("bfcr")
                                .equals("null") ? "0" : info.getString("bfcr");
                        String curdr = info.getString("curdr")
                                .equals("null") ? "0" : info.getString("curdr");
                        String curcr = info.getString("curcr")
                                .equals("null") ? "0" : info.getString("curcr");
                        String lg_ad_id = info.getString("lg_ad_id")
                                .equals("null") ? "" : info.getString("lg_ad_id");
                        String ah_code = info.getString("ah_code")
                                .equals("null") ? "" : info.getString("ah_code");
                        String al1_code = info.getString("al1_code")
                                .equals("null") ? "" : info.getString("al1_code");
                        String al2_code = info.getString("al2_code")
                                .equals("null") ? "" : info.getString("al2_code");
                        String ad_code = info.getString("ad_code")
                                .equals("null") ? "" : info.getString("ad_code");

                        expenseStatementLists.add(new IncExpStatementList(lvl1,lvl2,lvl3,bfdr,bfcr,curdr,curcr,lg_ad_id,
                                ah_code,al1_code,al2_code,ad_code,acLevelType));
                    }
                }
                connected = true;
                updateInterface();
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
        });

        StringRequest incStateRequest = new StringRequest(Request.Method.GET, incStateUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String lvl1 = info.getString("lvl1")
                                .equals("null") ? "" : info.getString("lvl1");
                        String lvl2 = info.getString("lvl2")
                                .equals("null") ? "" : info.getString("lvl2");
                        String lvl3 = info.getString("lvl3")
                                .equals("null") ? "" : info.getString("lvl3");
                        String bfdr = info.getString("bfdr")
                                .equals("null") ? "0" : info.getString("bfdr");
                        String bfcr = info.getString("bfcr")
                                .equals("null") ? "0" : info.getString("bfcr");
                        String curdr = info.getString("curdr")
                                .equals("null") ? "0" : info.getString("curdr");
                        String curcr = info.getString("curcr")
                                .equals("null") ? "0" : info.getString("curcr");
                        String lg_ad_id = info.getString("lg_ad_id")
                                .equals("null") ? "" : info.getString("lg_ad_id");
                        String ah_code = info.getString("ah_code")
                                .equals("null") ? "" : info.getString("ah_code");
                        String al1_code = info.getString("al1_code")
                                .equals("null") ? "" : info.getString("al1_code");
                        String al2_code = info.getString("al2_code")
                                .equals("null") ? "" : info.getString("al2_code");
                        String ad_code = info.getString("ad_code")
                                .equals("null") ? "" : info.getString("ad_code");
                        
                        incomeStatementLists.add(new IncExpStatementList(lvl1,lvl2,lvl3,bfdr,bfcr,curdr,curcr,lg_ad_id,
                                ah_code,al1_code,al2_code,ad_code,acLevelType));
                    }
                }
                requestQueue.add(expStateRequest);
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
        });

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

                requestQueue.add(incStateRequest);
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

        requestQueue.add(incExpReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");
                
                double t_i = Double.parseDouble(total_income);
                double t_e = Double.parseDouble(total_expense);
                double amnt = t_i - t_e;
                String total_amount = formatter.format(amnt);

                if (amnt > 0.0) {
                    totalAmount.setTextColor(getColor(R.color.green_sea));
                    String tt = "Amount : " + total_amount + " (Net Profit)";
                    totalAmount.setText(tt);
                }
                else if (amnt == 0.0) {
                    totalAmount.setTextColor(getColor(R.color.green_sea));
                    String tt = "Amount : " + total_amount + " (Break Even Point)";
                    totalAmount.setText(tt);
                }
                else {
                    totalAmount.setTextColor(getColor(R.color.red_dark));
                    total_amount = total_amount.replace("-","");
                    String tt = "Amount : " + total_amount + " (Net Loss)";
                    totalAmount.setText(tt);
                }

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

                filterIncExp(acLevelType);

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

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getIncomeExpList();
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