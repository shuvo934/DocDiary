package ttit.com.shuvo.docdiary.hr_accounts.acc_ledger;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.processphoenix.ProcessPhoenix;

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
import ttit.com.shuvo.docdiary.hr_accounts.acc_ledger.adapters.AccountLedgerAdapter;
import ttit.com.shuvo.docdiary.hr_accounts.acc_ledger.arraylists.AccountLedgerLists;
import ttit.com.shuvo.docdiary.hr_accounts.acc_ledger.arraylists.AccountList;

public class AccountLedger extends AppCompatActivity {

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

    TextInputLayout accNoLay;
    AppCompatAutoCompleteTextView accNo;
    TextView accNoMissing;
    ArrayList<AccountList> accLists;
    String ad_id = "";
    String ad_name = "";
    String al1_name = "";
    String al2_name = "";
    String ad_code = "";
    String selected_ah_code = "";
    boolean selectedFromItems = false;

    ScrollView vCard;

    TextView adNameSelected;
    TextView adCodeSelected;
    TextView al2NameSelected;
    TextView al1NameSelected;

    RecyclerView itemView;
    AccountLedgerAdapter accountLedgerAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView openingDebit;
    TextView openingCredit;
    TextView closingDebit;
    TextView closingCredit;
    TextView closingBalance;
    TextView openingDate;

    ArrayList<AccountLedgerLists> accountLedgerLists;

    String openD = "";
    String openC = "";

    Logger logger = Logger.getLogger(AccountLedger.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_ledger);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.acc_ledger_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.account_ledger_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_account_ledger);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_account_ledger);

        beginDate = findViewById(R.id.begin_date_account_ledger);
        endDate = findViewById(R.id.end_date_account_ledger);
        dateRange = findViewById(R.id.date_range_msg_account_ledger);
        dateRange.setVisibility(View.GONE);

        accNoLay = findViewById(R.id.spinner_layout_account_no_acc_ledger);
        accNo = findViewById(R.id.account_no_acc_ledger_spinner);
        accNoMissing = findViewById(R.id.acc_no_acc_ledger_missing_msg);
        accNoMissing.setVisibility(View.GONE);

        adNameSelected = findViewById(R.id.ad_name_selected_view_al);
        adCodeSelected = findViewById(R.id.ad_code_selected_view_al);
        al2NameSelected = findViewById(R.id.al2_name_selected_view_al);
        al1NameSelected = findViewById(R.id.al1_name_selected_view_al);

        itemView = findViewById(R.id.acc_ledger_report_view);

        itemView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        itemView.setLayoutManager(layoutManager);

        vCard = findViewById(R.id.account_ledger_card_view);
        vCard.setVisibility(View.GONE);

        openingDate = findViewById(R.id.opening_date_acc_ledger);
        openingCredit = findViewById(R.id.opening_credit_balance);
        openingDebit = findViewById(R.id.opening_debit_balance);
        closingDebit = findViewById(R.id.closing_debit_balance);
        closingCredit = findViewById(R.id.closing_credit_balance);
        closingBalance = findViewById(R.id.closing_balance);

        accLists = new ArrayList<>();
        accountLedgerLists = new ArrayList<>();

        Intent intent = getIntent();
        firstDate = intent.getStringExtra("FIRST_DATE");
        lastDate = intent.getStringExtra("LAST_DATE");
        ad_id = intent.getStringExtra("AD_ID");

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(AccountLedger.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        // Getting Date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy",Locale.getDefault());

        if (firstDate.isEmpty()) {
            firstDate = simpleDateFormat.format(c);
            firstDate = "01-"+firstDate;
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(AccountLedger.this, (view, year, month, dayOfMonth) -> {

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

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());

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
                                if (!ad_id.isEmpty()) {
                                    getAccountLedger();
                                }
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(AccountLedger.this, (view, year, month, dayOfMonth) -> {

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

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());

                        try {
                            bDate = sdf.parse(firstDate);
                            eDate = sdf.parse(lastDate);
                        } catch (ParseException e) {
                            logger.log(Level.WARNING,e.getMessage(),e);
                        }

                        if (bDate != null && eDate != null) {
                            if (eDate.after(bDate)|| eDate.equals(bDate)) {
                                dateRange.setVisibility(View.GONE);
                                if (!ad_id.isEmpty()) {
                                    getAccountLedger();
                                }
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

        accNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    accNoMissing.setVisibility(View.GONE);
                }
            }
        });

        accNo.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <accLists.size(); i++) {
                if (name.equals(accLists.get(i).getAd_name())) {
                    ad_id = accLists.get(i).getLg_ad_id();
                    ad_name = accLists.get(i).getAd_name();
                    al1_name = accLists.get(i).getAl1_name();
                    al2_name = accLists.get(i).getAl2_name();
                    ad_code = accLists.get(i).getAd_code();
                    selected_ah_code = accLists.get(i).getAh_code();
                }
            }

            selectedFromItems = true;
            accNoMissing.setVisibility(View.GONE);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
            closeKeyBoard();
            if (!firstDate.isEmpty() && !lastDate.isEmpty()) {
                getAccountLedger();
            }
        });

        accNo.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = accNo.getText().toString();
                if (!selectedFromItems) {
                    ad_id = "";
                    ad_name = "";
                    al1_name = "";
                    al2_name = "";
                    ad_code = "";
                    selected_ah_code = "";
                    for (int i = 0; i < accLists.size(); i++) {
                        if (ss.equals(accLists.get(i).getAd_name())) {
                            ad_id = accLists.get(i).getLg_ad_id();
                            ad_name = accLists.get(i).getAd_name();
                            al1_name = accLists.get(i).getAl1_name();
                            al2_name = accLists.get(i).getAl2_name();
                            ad_code = accLists.get(i).getAd_code();
                            selected_ah_code = accLists.get(i).getAh_code();
                        }
                    }
                    if (ad_id.isEmpty()) {
                        if (ss.isEmpty()) {
                            accNoMissing.setVisibility(View.VISIBLE);
                            String cmt = "Please Select A/C No";
                            accNoMissing.setText(cmt);
                        }
                        else {
                            accNoMissing.setVisibility(View.VISIBLE);
                            String cmt = "Invalid A/C No";
                            accNoMissing.setText(cmt);
                        }
                    }
                    else {
                        if (!firstDate.isEmpty() && !lastDate.isEmpty()) {
                            getAccountLedger();
                        }
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        accNo.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    accNo.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(AccountLedger.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        getAccList();

    }

    private void closeKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void getAccList() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        accLists = new ArrayList<>();

        String accListUrl = pre_url_api+"acc_dashboard/getAccountList";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest accListReq = new StringRequest(Request.Method.GET, accListUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String ad_name_new = info.getString("ad_name")
                                .equals("null") ? "" : info.getString("ad_name");
                        String ad_code_new = info.getString("ad_code")
                                .equals("null") ? "" : info.getString("ad_code");
                        String al2_name_new = info.getString("al2_name")
                                .equals("null") ? "" : info.getString("al2_name");
                        String al1_name_new = info.getString("al1_name")
                                .equals("null") ? "" : info.getString("al1_name");
                        String ah_name = info.getString("ah_name")
                                .equals("null") ? "" : info.getString("ah_name");
                        String lg_ad_id = info.getString("lg_ad_id")
                                .equals("null") ? "" : info.getString("lg_ad_id");
                        String ah_code = info.getString("ah_code")
                                .equals("null") ? "" : info.getString("ah_code");

                        accLists.add(new AccountList(ad_name_new,ad_code_new,al2_name_new,al1_name_new,ah_name,lg_ad_id,ah_code));

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

        requestQueue.add(accListReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {
                conn = false;
                connected = false;

                if (accLists.isEmpty()) {
                    String cmt = "No Account List Found";
                    accNoMissing.setText(cmt);
                    accNoMissing.setVisibility(View.VISIBLE);
                }
                else {
                    accNoMissing.setVisibility(View.GONE);
                }

                if (!ad_id.isEmpty()) {
                    for (int i = 0; i < accLists.size(); i++) {
                        if (ad_id.equals(accLists.get(i).getLg_ad_id())) {
                            ad_id = accLists.get(i).getLg_ad_id();
                            ad_name = accLists.get(i).getAd_name();
                            al1_name = accLists.get(i).getAl1_name();
                            al2_name = accLists.get(i).getAl2_name();
                            ad_code = accLists.get(i).getAd_code();
                            selected_ah_code = accLists.get(i).getAh_code();
                        }
                    }
                    accNo.setText(ad_name);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < accLists.size(); i++) {
                    type.add(accLists.get(i).getAd_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AccountLedger.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                accNo.setAdapter(arrayAdapter);

                if (!ad_id.isEmpty()) {
                    getAccountLedger();
                }
                else {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);
                    loading = false;
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
                    getAccList();
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

    public void getAccountLedger() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        vCard.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;
        accountLedgerLists = new ArrayList<>();

        String accLedgerUrl = pre_url_api+"acc_dashboard/getAccLedger?st_date="+firstDate+"&end_date="+lastDate+"&ad_id="+ad_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest accLedReq = new StringRequest(Request.Method.GET, accLedgerUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);
                        String acc_ledger = info.getString("acc_ledger");
                        JSONArray ledgerArray = new JSONArray(acc_ledger);
                        double balanceAmnt = 0.0;
                        int k = 0;

                        for (int j = 0; j < ledgerArray.length(); j++) {
                            JSONObject ledger_info = ledgerArray.getJSONObject(j);
                            String lg_id = ledger_info.getString("lg_id");
                            if (!lg_id.equals("0")) {

                                String ahCode = ledger_info.getString("ah_code")
                                        .equals("null") ? "" : ledger_info.getString("ah_code");

                                if (k == 0) {
                                    String odV = ledger_info.getString("opening_dr")
                                            .equals("null") ? "0" : ledger_info.getString("opening_dr");
                                    String ocV = ledger_info.getString("opening_cr")
                                            .equals("null") ? "0" : ledger_info.getString("opening_cr");
                                    double openDebit = Double.parseDouble(odV);
                                    double openCredit = Double.parseDouble(ocV);

                                    if (ahCode.equals("01") || ahCode.equals("04")) {
                                        balanceAmnt = openDebit - openCredit;
                                    }
                                    else if (ahCode.equals("02") || ahCode.equals("03") || ahCode.equals("05")) {
                                        balanceAmnt = openCredit - openDebit;
                                    }
                                }

                                System.out.println("Previous Balance: "+balanceAmnt);
                                k++;

                                String lg_voucher_date = ledger_info.getString("lg_voucher_date")
                                        .equals("null") ? "" : ledger_info.getString("lg_voucher_date");

                                String lg_voucher_no = ledger_info.getString("lg_voucher_no")
                                        .equals("null") ? "" : ledger_info.getString("lg_voucher_no");

                                String lg_particulars = ledger_info.getString("lg_particulars")
                                        .equals("null") ? "" : ledger_info.getString("lg_particulars");

                                String lg_dr_amt = ledger_info.getString("lg_dr_amt")
                                        .equals("null") ? "0" : ledger_info.getString("lg_dr_amt");

                                String dr_null = ledger_info.getString("dr_null")
                                        .equals("null") ? "0" : ledger_info.getString("dr_null");

                                String lg_cr_amt = ledger_info.getString("lg_cr_amt")
                                        .equals("null") ? "0" : ledger_info.getString("lg_cr_amt");

                                String cr_null = ledger_info.getString("cr_null")
                                        .equals("null") ? "0" : ledger_info.getString("cr_null");

                                String opening_dr = ledger_info.getString("opening_dr")
                                        .equals("null") ? "0" : ledger_info.getString("opening_dr");

                                String opening_cr = ledger_info.getString("opening_cr")
                                        .equals("null") ? "0" : ledger_info.getString("opening_cr");

                                String lg_trans_type = ledger_info.getString("lg_trans_type")
                                        .equals("null") ? "" : ledger_info.getString("lg_trans_type");

                                String lg_inv_pur_no = ledger_info.getString("lg_inv_pur_no")
                                        .equals("null") ? "" : ledger_info.getString("lg_inv_pur_no");

                                String prm_pay_type_flag = ledger_info.getString("prm_pay_type_flag")
                                        .equals("null") ? "" : ledger_info.getString("prm_pay_type_flag");

                                accountLedgerLists.add(new AccountLedgerLists(lg_id,lg_voucher_date,lg_voucher_no,
                                        lg_particulars,lg_dr_amt,dr_null,
                                        lg_cr_amt,cr_null,opening_dr,
                                        opening_cr,ahCode,String.valueOf(balanceAmnt),lg_trans_type,lg_inv_pur_no,prm_pay_type_flag,false));
                            }
                            else {
                                openD = ledger_info.getString("opening_dr")
                                        .equals("null") ? "0" : ledger_info.getString("opening_dr");
                                openC = ledger_info.getString("opening_cr")
                                        .equals("null") ? "0" : ledger_info.getString("opening_cr");
                            }
                        }
                    }
                }

                checkLedgerBalance();
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

        requestQueue.add(accLedReq);
    }

    public void checkLedgerBalance() {
        if (!accountLedgerLists.isEmpty()) {
            boolean allUpdated = false;
            for (int i = 0; i < accountLedgerLists.size(); i++) {
                allUpdated = accountLedgerLists.get(i).isUpdated();
                if (!accountLedgerLists.get(i).isUpdated()) {
                    allUpdated = accountLedgerLists.get(i).isUpdated();
                    String debit_amnt = accountLedgerLists.get(i).getDebit();
                    String credit_amnt = accountLedgerLists.get(i).getCredit();
                    String ah_code = accountLedgerLists.get(i).getAhCode();
                    String pr_balance = accountLedgerLists.get(i).getBalance();

                    getLedgerBalance(debit_amnt, credit_amnt, ah_code, pr_balance, i);
                    break;
                }
            }
            if (allUpdated) {
                connected = true;
                updateLayout();
            }
        }
        else {
            connected = true;
            updateLayout();
        }
    }

    public void getLedgerBalance(String debit, String credit, String ahCode, String balance, int index) {

        String url = pre_url_api+"acc_dashboard/getLedgerBalance?st_date="+firstDate+"&ad_id="+ad_id+"&debit_amnt="+debit+"&credit_amnt="+credit+"&ah_code="+ahCode+"&balance="+balance;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);
                        String ledger_balance = info.getString("ledger_balance")
                                .equals("null") ? "0" : info.getString("ledger_balance");

                        accountLedgerLists.get(index).setBalance(ledger_balance);
                        accountLedgerLists.get(index).setUpdated(true);

                        int newIndex = index + 1;
                        if (newIndex < accountLedgerLists.size()) {
                            accountLedgerLists.get(newIndex).setBalance(ledger_balance);
                        }
                    }
                }
                checkLedgerBalance();
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

        requestQueue.add(stringRequest);
    }

    private void updateLayout() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                vCard.setVisibility(View.VISIBLE);

                accountLedgerAdapter = new AccountLedgerAdapter(AccountLedger.this, accountLedgerLists);
                itemView.setAdapter(accountLedgerAdapter);

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                if (!accountLedgerLists.isEmpty()) {
                    double openDebit = accountLedgerLists.get(0).getOpeningDebit() != null ?
                            Double.parseDouble(accountLedgerLists.get(0).getOpeningDebit()) : 0.0;
                    String o_d = formatter.format(openDebit);

                    if (o_d.contains("-")) {
                        o_d = o_d.replace("-","");
                        o_d = "(-)  "+ o_d;
                    }
                    openingDebit.setText(o_d);

                    double openCredit = accountLedgerLists.get(0).getOpeningCredit() != null ?
                            Double.parseDouble(accountLedgerLists.get(0).getOpeningCredit()) : 0.0;
                    String o_c = formatter.format(openCredit);

                    if (o_c.contains("-")) {
                        o_c = o_c.replace("-","");
                        o_c = "(-)  "+ o_c;
                    }

                    openingCredit.setText(o_c);

                    double totalD = 0;
                    double totalC = 0;
                    for (int i = 0; i < accountLedgerLists.size(); i++) {
                        if (accountLedgerLists.get(i).getDebit() != null) {
                            totalD = totalD + Double.parseDouble(accountLedgerLists.get(i).getDebit());
                        }
                        if (accountLedgerLists.get(i).getCredit() != null) {
                            totalC = totalC + Double.parseDouble(accountLedgerLists.get(i).getCredit());
                        }
                    }

                    totalD = totalD + openDebit;
                    totalC = totalC + openCredit;

                    String closeD = formatter.format(totalD);
                    String closeC = formatter.format(totalC);

                    if (closeD.contains("-")) {
                        closeD = closeD.replace("-","");
                        closeD = "(-)  "+ closeD;
                    }

                    closingDebit.setText(closeD);

                    if (closeC.contains("-")) {
                        closeC = closeC.replace("-","");
                        closeC = "(-)  "+ closeC;
                    }

                    closingCredit.setText(closeC);

                    double closingBal = accountLedgerLists.get(accountLedgerLists.size() - 1).getBalance() != null ?
                            Double.parseDouble(accountLedgerLists.get(accountLedgerLists.size() - 1).getBalance()) : 0.0;

                    String closeBal = formatter.format(closingBal);

                    if (closeBal.contains("-")) {
                        closeBal = closeBal.replace("-","");
                        closeBal = "(-)  "+ closeBal;
                    }

                    closingBalance.setText(closeBal);
                }
                else  {

                    double opD = Double.parseDouble(openD);
                    double opC = Double.parseDouble(openC);

                    String o_d = formatter.format(opD);

                    if (o_d.contains("-")) {
                        o_d = o_d.replace("-","");
                        o_d = "(-)  "+ o_d;
                    }
                    openingDebit.setText(o_d);

                    String o_c = formatter.format(opC);

                    if (o_c.contains("-")) {
                        o_c = o_c.replace("-","");
                        o_c = "(-)  "+ o_c;
                    }

                    openingCredit.setText(o_c);

                    closingDebit.setText(o_d);

                    closingCredit.setText(o_c);



                    double cloB = 0.0;

                    if (selected_ah_code.equals("01") || selected_ah_code.equals("04")) {
                        cloB = opD - opC;
                    }
                    else if (selected_ah_code.equals("02") || selected_ah_code.equals("03") || selected_ah_code.equals("05")) {
                        cloB = opC - opD;
                    }

                    String closeBal = formatter.format(cloB);

                    if (closeBal.contains("-")) {
                        closeBal = closeBal.replace("-","");
                        closeBal = "(-)  "+ closeBal;
                    }

                    closingBalance.setText(closeBal);

                }

                openingDate.setText(firstDate);
                adNameSelected.setText(ad_name);
                String ac = "(" + ad_code + ")";
                adCodeSelected.setText(ac);
                al2NameSelected.setText(al2_name);
                al1NameSelected.setText(al1_name);
                loading = false;

            }
            else {
                alertMessageAccLedger();
            }
        }
        else {
            alertMessageAccLedger();
        }
    }

    public void alertMessageAccLedger() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        vCard.setVisibility(View.GONE);

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
                    getAccountLedger();
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