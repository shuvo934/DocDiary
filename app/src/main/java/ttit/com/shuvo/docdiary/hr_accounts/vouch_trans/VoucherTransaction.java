package ttit.com.shuvo.docdiary.hr_accounts.vouch_trans;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
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
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.adapters.VouchTrans1Adapter;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists.VoucherLists1;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists.VoucherLists2;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists.VoucherLists3;

public class VoucherTransaction extends AppCompatActivity {

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

    AmazingSpinner voucherSpinner;
    ArrayList<String> voucherTypeLists;
    String voucherType = "";

    TextView totalVoucher;
    PieChart voucherPieChart;
    ArrayList<PieEntry> voucherPieEntry;

    TextView vtDateListText;

    RecyclerView itemView;
    VouchTrans1Adapter vouchTrans1Adapter;
    RecyclerView.LayoutManager layoutManager;
    TextView noVoucherMsg;

    ArrayList<VoucherLists1> voucherLists1s;
    ArrayList<VoucherLists1> filteredLists;

    LinearLayout debitCreditLay;
    TextView totalDebit;
    TextView totalCredit;

    Logger logger = Logger.getLogger(VoucherTransaction.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_transaction);

        fullLayout = findViewById(R.id.voucher_transaction_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_voucher_transaction);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_voucher_transaction);

        beginDate = findViewById(R.id.begin_date_voucher_transaction);
        endDate = findViewById(R.id.end_date_voucher_transaction);
        dateRange = findViewById(R.id.date_range_msg_voucher_transaction);

        voucherSpinner = findViewById(R.id.voucher_type_vt_spinner);

        totalVoucher = findViewById(R.id.v_counter_text_in_vt);
        voucherPieChart = findViewById(R.id.voucher_transaction_overview_pie_chart);

        vtDateListText = findViewById(R.id.date_range_value_for_vt);

        itemView = findViewById(R.id.voucher_trans_report_view);
        noVoucherMsg = findViewById(R.id.no_voucher_trans_msg);
        noVoucherMsg.setVisibility(View.GONE);

        itemView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        itemView.setLayoutManager(layoutManager);

        debitCreditLay = findViewById(R.id.total_debit_credit_layout_voucher_transaction);
        totalDebit = findViewById(R.id.total_debit_val_in_voucher_transaction);
        totalCredit = findViewById(R.id.total_credit_val_in_voucher_transaction);

        voucherTypeLists = new ArrayList<>();
        voucherLists1s = new ArrayList<>();
        filteredLists = new ArrayList<>();

        Intent intent = getIntent();
        firstDate = intent.getStringExtra("FIRST_DATE");
        lastDate = intent.getStringExtra("LAST_DATE");

        voucherTypeLists.add("...");
        voucherTypeLists.add("CV (Receive)");
        voucherTypeLists.add("DV (Payment)");
        voucherTypeLists.add("JV (Transfer)");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, R.id.drop_down_item, voucherTypeLists);

        voucherSpinner.setAdapter(arrayAdapter);

        voucherSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();

            if (name.equals("...")) {
                voucherType = "";
                voucherSpinner.setText("");
            }
            else {
                voucherType = name;
            }

            filterVoucher(voucherType);

        });

        voucherChartInit();

        // Getting Date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy",Locale.getDefault());

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

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(VoucherTransaction.this, (view, year, month, dayOfMonth) -> {

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
                                voucherType = "";
                                voucherSpinner.setText("");
                                getVoucherList();
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

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(VoucherTransaction.this, (view, year, month, dayOfMonth) -> {

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
                                voucherType = "";
                                voucherSpinner.setText("");
                                getVoucherList();
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
                Toast.makeText(VoucherTransaction.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(VoucherTransaction.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        getVoucherList();
    }

    private void voucherChartInit() {
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

        l.setXOffset(20f);
        l.setTextSize(12);
        l.setWordWrapEnabled(false);
        l.setDrawInside(false);
        l.setYOffset(10f);

        voucherPieChart.animateXY(1000, 1000);
    }

    private void filterVoucher(String text) {
        filteredLists = new ArrayList<>();
        if (!text.isEmpty()) {
            text = text.substring(0,2);
        }
//        for (VoucherLists1 item : voucherLists1s) {
//            if (text.isEmpty()) {
//                filteredLists.add(item);
//            }
//            else {
//                ArrayList<VoucherLists2> voucherLists2s = item.getVoucherLists2s();
//                ArrayList<VoucherLists2> filteredLists2 = new ArrayList<>();
//                boolean found = false;
//                for (VoucherLists2 item2 : voucherLists2s) {
//                    if (item2.getvNo().toUpperCase().contains(text.toUpperCase())) {
//                        found = true;
//                        filteredLists2.add(item2);
//                    }
//                }
//                if (found) {
//                    item.setVoucherLists2s(filteredLists2);
//                    filteredLists.add(item);
//                }
//            }
//        }

        for (int i = 0; i < voucherLists1s.size(); i++) {
            String date = voucherLists1s.get(i).getvDate();
            ArrayList<VoucherLists2> voucherLists2s = voucherLists1s.get(i).getVoucherLists2s();
            boolean up = voucherLists1s.get(i).isUpdated();
            if (text.isEmpty()) {
                filteredLists.add(new VoucherLists1(date,voucherLists2s,up));
            }
            else {
                boolean found = false;
                ArrayList<VoucherLists2> ft2 = new ArrayList<>();
                for (int j = 0; j < voucherLists2s.size(); j++) {
                    String vNo = voucherLists2s.get(j).getvNo();
                    String particulars = voucherLists2s.get(j).getParticulars();
                    ArrayList<VoucherLists3> voucherLists3s = voucherLists2s.get(j).getVoucherLists3s();
                    String type = voucherLists2s.get(j).getType();
                    String orderNo = voucherLists2s.get(j).getOrderNo();
                    String flag = voucherLists2s.get(j).getPay_type_flag();
                    boolean updated = voucherLists2s.get(j).isUpdated();

                    if (vNo.toUpperCase().contains(text.toUpperCase())) {
                        found = true;
                        ft2.add(new VoucherLists2(vNo,particulars,voucherLists3s,type,orderNo,flag,updated));
                    }
                }

                if (found) {
                    filteredLists.add(new VoucherLists1(date,ft2,up));
                }
            }
        }

        vouchTrans1Adapter.filterList(filteredLists);

        if (filteredLists.isEmpty()) {
            noVoucherMsg.setVisibility(View.VISIBLE);
            debitCreditLay.setVisibility(View.GONE);
        }
        else {
            noVoucherMsg.setVisibility(View.GONE);
            debitCreditLay.setVisibility(View.VISIBLE);
        }
        double credit_amnt = 0.0;
        double debit_amnt = 0.0;
        for (int i = 0; i < filteredLists.size(); i++) {
            ArrayList<VoucherLists2> voucherLists2s = filteredLists.get(i).getVoucherLists2s();
            for (int j = 0; j < voucherLists2s.size(); j++) {
                ArrayList<VoucherLists3> voucherLists3s = voucherLists2s.get(j).getVoucherLists3s();
                for (int k = 0; k < voucherLists3s.size(); k++) {
                    if (!voucherLists3s.get(k).getDebit().isEmpty()) {
                        debit_amnt = debit_amnt + Double.parseDouble(voucherLists3s.get(k).getDebit());
                    }
                    if (!voucherLists3s.get(k).getCredit().isEmpty()) {
                        credit_amnt = credit_amnt + Double.parseDouble(voucherLists3s.get(k).getCredit());
                    }
                }
            }
        }

        DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

        String dba = formatter.format(debit_amnt);
        String da = "৳ " + dba;

        String cba = formatter.format(credit_amnt);
        String ca = "৳ " + cba;

        totalDebit.setText(da);
        totalCredit.setText(ca);

        updatePieChart();
    }

    public void updatePieChart() {
        ArrayList<PieEntry> filteredPie = new ArrayList<>();

        for (int i = 0; i < voucherPieEntry.size(); i++) {
            if (voucherType.isEmpty()) {
                filteredPie.add(new PieEntry(voucherPieEntry.get(i).getValue(),voucherPieEntry.get(i).getLabel(),voucherPieEntry.get(i).getData()));
            }
            else {
                if (voucherPieEntry.get(i).getLabel().equals(voucherType)) {
                    filteredPie.add(new PieEntry(voucherPieEntry.get(i).getValue(),voucherPieEntry.get(i).getLabel(),voucherPieEntry.get(i).getData()));
                }
            }
        }

        int cc = 0;
        for (int i = 0; i < filteredPie.size(); i++) {
            cc = cc + (int) filteredPie.get(i).getValue();
        }

        String tt = "Total Voucher : " + cc;
        totalVoucher.setText(tt);

        if (filteredPie.isEmpty()) {
            filteredPie.add(new PieEntry(1,"No Data", 6));
        }

        PieDataSet dataSet = new PieDataSet(filteredPie, "");
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

        int[] num = new int[filteredPie.size()];
        for (int i = 0; i < filteredPie.size(); i++) {
            int neki = (int) filteredPie.get(i).getData();
            num[i] = piecolors[neki];
        }

        dataSet.setColors(ColorTemplate.createColors(num));

        voucherPieChart.setData(data);
        voucherPieChart.invalidate();
    }

    public void getVoucherList() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;
        voucherType = "";
        voucherSpinner.setText("");

        voucherPieEntry = new ArrayList<>();
        voucherLists1s = new ArrayList<>();

        String voucherCountUrl = pre_url_api+"acc_dashboard/getVoucherCount?first_date="+firstDate+"&end_date="+lastDate;
        String voucherDateUrl = pre_url_api+"acc_dashboard/getVoucherList?st_date="+firstDate+"&end_date="+lastDate+"&voucher_type=&voucher_date=&voucher_no=&options=1";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, voucherDateUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);
                        String voucher_transaction_list = info.getString("voucher_transaction_list");
                        JSONArray itemsArray = new JSONArray(voucher_transaction_list);
                        for (int j = 0; j < itemsArray.length(); j++) {
                            JSONObject v_date_info = itemsArray.getJSONObject(j);

                            String vdate = v_date_info.getString("vdate");

                            voucherLists1s.add(new VoucherLists1(vdate,new ArrayList<>(),false));
                        }
                    }
                }
                checkToGetVlist2();
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

                requestQueue.add(stringRequest);

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

        requestQueue.add(voucherCountReq);
    }

    private void checkToGetVlist2() {
        if (!voucherLists1s.isEmpty()) {
            boolean allUpdated = false;
            for (int i = 0; i < voucherLists1s.size(); i++) {
                allUpdated = voucherLists1s.get(i).isUpdated();
                if (!voucherLists1s.get(i).isUpdated()) {
                    allUpdated = voucherLists1s.get(i).isUpdated();
                    String vDate = voucherLists1s.get(i).getvDate();
                    getVoucherLists2(vDate,i);
                    break;
                }
            }
            if (allUpdated) {
//                checkToGetSalesOrderList();
                connected = true;
                updateInterface();
            }
        }
        else {
//            checkToGetSalesOrderList();
            connected = true;
            updateInterface();
        }
    }

    public void getVoucherLists2(String vDate, int firstIndex) {
        String voucherLists2Url = pre_url_api+"acc_dashboard/getVoucherList?st_date="+firstDate+"&end_date="+lastDate+"&voucher_type=&voucher_date="+vDate+"&voucher_no=&options=2";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        ArrayList<VoucherLists2> voucherLists2s = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, voucherLists2Url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);
                        String voucher_transaction_list = info.getString("voucher_transaction_list");
                        JSONArray itemsArray = new JSONArray(voucher_transaction_list);
                        for (int j = 0; j < itemsArray.length(); j++) {
                            JSONObject vlist2_info = itemsArray.getJSONObject(j);

                            String vNo = vlist2_info.getString("lg_voucher_no");
                            String purchase = vlist2_info.getString("lg_inv_pur_no");
                            String type = vlist2_info.getString("lg_trans_type");
                            String lg_particulars = vlist2_info.getString("lg_particulars")
                                    .equals("null") ? "" : vlist2_info.getString("lg_particulars");
                            String flag = vlist2_info.getString("prm_pay_type_flag")
                                    .equals("null") ? "" : vlist2_info.getString("prm_pay_type_flag");

                            voucherLists2s.add(new VoucherLists2(vNo,lg_particulars,new ArrayList<>(),type,purchase,flag,false));
                        }
                    }
                }
                checkToGetVlist3(voucherLists2s, vDate, firstIndex);
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

        requestQueue.add(stringRequest);
    }

    private void checkToGetVlist3(ArrayList<VoucherLists2> voucherLists2s, String vDate, int firstIndex) {
        if (!voucherLists2s.isEmpty()) {
            boolean allUpdated = false;
            for (int i = 0; i < voucherLists2s.size(); i++) {
                allUpdated = voucherLists2s.get(i).isUpdated();
                if (!voucherLists2s.get(i).isUpdated()) {
                    allUpdated = voucherLists2s.get(i).isUpdated();
                    String vNo = voucherLists2s.get(i).getvNo();
                    getVoucherLists3(voucherLists2s,vNo,vDate,i,firstIndex);
                    break;
                }
            }
            if (allUpdated) {
                voucherLists1s.get(firstIndex).setVoucherLists2s(voucherLists2s);
                voucherLists1s.get(firstIndex).setUpdated(true);
                checkToGetVlist2();
            }
        }
        else {
            voucherLists1s.get(firstIndex).setVoucherLists2s(voucherLists2s);
            voucherLists1s.get(firstIndex).setUpdated(true);
            checkToGetVlist2();
        }
    }

    public void getVoucherLists3(ArrayList<VoucherLists2> voucherLists2s, String vNo, String vDate, int secondIndex, int firstIndex) {
        String voucherLists3Url =  pre_url_api+"acc_dashboard/getVoucherList?st_date="+firstDate+"&end_date="+lastDate+"&voucher_type=&voucher_date="+vDate+"&voucher_no="+vNo+"&options=3";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        ArrayList<VoucherLists3> voucherLists3s = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, voucherLists3Url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);
                        String voucher_transaction_list = info.getString("voucher_transaction_list");
                        JSONArray itemsArray = new JSONArray(voucher_transaction_list);
                        for (int j = 0; j < itemsArray.length(); j++) {
                            JSONObject vlist3_info = itemsArray.getJSONObject(j);

                            String acc_dtl = vlist3_info.getString("acc_dtl")
                                    .equals("null") ? "" : vlist3_info.getString("acc_dtl");

                            String lg_dr_amt = vlist3_info.getString("lg_dr_amt")
                                    .equals("null") ? "" : vlist3_info.getString("lg_dr_amt");
                            String lg_cr_amt = vlist3_info.getString("lg_cr_amt")
                                    .equals("null") ? "" : vlist3_info.getString("lg_cr_amt");


                            voucherLists3s.add(new VoucherLists3(acc_dtl,lg_dr_amt,lg_cr_amt));
                        }
                    }
                }
                voucherLists2s.get(secondIndex).setVoucherLists3s(voucherLists3s);
                voucherLists2s.get(secondIndex).setUpdated(true);
                checkToGetVlist3(voucherLists2s, vDate, firstIndex);
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

        requestQueue.add(stringRequest);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (voucherLists1s.isEmpty()) {
                    noVoucherMsg.setVisibility(View.VISIBLE);
                    debitCreditLay.setVisibility(View.GONE);
                } else {
                    noVoucherMsg.setVisibility(View.GONE);
                    debitCreditLay.setVisibility(View.VISIBLE);
                }

                String vt_date = "Voucher Transaction List ("+firstDate+" to "+lastDate+")";
                vtDateListText.setText(vt_date);

                double credit_amnt = 0.0;
                double debit_amnt = 0.0;
                for (int i = 0; i < voucherLists1s.size(); i++) {
                    ArrayList<VoucherLists2> voucherLists2s = voucherLists1s.get(i).getVoucherLists2s();
                    for (int j = 0; j < voucherLists2s.size(); j++) {
                        ArrayList<VoucherLists3> voucherLists3s = voucherLists2s.get(j).getVoucherLists3s();
                        for (int k = 0; k < voucherLists3s.size(); k++) {
                            if (!voucherLists3s.get(k).getDebit().isEmpty()) {
                                debit_amnt = debit_amnt + Double.parseDouble(voucherLists3s.get(k).getDebit());
                            }
                            if (!voucherLists3s.get(k).getCredit().isEmpty()) {
                                credit_amnt = credit_amnt + Double.parseDouble(voucherLists3s.get(k).getCredit());
                            }
                        }
                    }
                }

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                String dba = formatter.format(debit_amnt);
                String da = "৳ " + dba;

                String cba = formatter.format(credit_amnt);
                String ca = "৳ " + cba;

                totalDebit.setText(da);
                totalCredit.setText(ca);

                vouchTrans1Adapter = new VouchTrans1Adapter(voucherLists1s, VoucherTransaction.this);
                itemView.setAdapter(vouchTrans1Adapter);

                int cc = 0;
                for (int i = 0; i < voucherPieEntry.size(); i++) {
                    cc = cc + (int) voucherPieEntry.get(i).getValue();
                }

                String tt = "Total Voucher : " + cc;
                totalVoucher.setText(tt);

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
                    getVoucherList();
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