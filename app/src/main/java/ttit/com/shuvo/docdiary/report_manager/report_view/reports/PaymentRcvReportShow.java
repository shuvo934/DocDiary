package ttit.com.shuvo.docdiary.report_manager.report_view.reports;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.adapters.PaymentRcvDataAdapter;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists.PaymentReceiveDataList;

public class PaymentRcvReportShow extends AppCompatActivity {

    ImageView close;
    TextView appbarName;
    MaterialButton pdfShow;

    RelativeLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    PaymentRcvDataAdapter paymentRcvDataAdapter;
    ArrayList<PaymentReceiveDataList> paymentReceiveDataLists;

    TextView totalPayRcv;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    String depts_id = "";
    String deptd_id = "";
    String pfn_id = "";
    String begin_date = "";
    String end_date = "";

    String url = "";

    Logger logger = Logger.getLogger(PaymentRcvReportShow.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(PaymentRcvReportShow.this,R.color.clouds));
        setContentView(R.layout.activity_payment_rcv_report_show);

        close = findViewById(R.id.close_logo_of_payment_rcv_app_made_report_show);
        appbarName = findViewById(R.id.payment_rcv_app_made_report_show_app_bar_text);
        pdfShow = findViewById(R.id.payment_rcv_pdf_report_show_report);

        if (pre_url_api.contains("cstar")) {
            pdfShow.setVisibility(View.VISIBLE);
        }
        else {
            pdfShow.setVisibility(View.GONE);
        }

        fullLayout = findViewById(R.id.payment_rcv_report_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_payment_rcv_app_made_report_show);
        circularProgressIndicator.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.payment_receive_app_made_report_show_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        totalPayRcv = findViewById(R.id.total_amount_val_in_payment_receive);

        Intent intent = getIntent();
        url = intent.getStringExtra("REPORT_URL");
        begin_date = intent.getStringExtra("FIRST_DATE");
        end_date = intent.getStringExtra("LAST_DATE");
        deptd_id = intent.getStringExtra("DEPTD_ID");
        depts_id = intent.getStringExtra("DEPTS_ID");
        pfn_id = intent.getStringExtra("PFN_ID");
        String app_bar_name = intent.getStringExtra("APP_BAR_NAME");

        appbarName.setText(app_bar_name);

        close.setOnClickListener(v -> finish());

        pdfShow.setOnClickListener(v -> {
            Intent intent1 = new Intent(PaymentRcvReportShow.this, ReportShow.class);
            intent1.putExtra("REPORT_URL", url);
            intent1.putExtra("FIRST_DATE", begin_date);
            intent1.putExtra("LAST_DATE", end_date);
            intent1.putExtra("APP_BAR_NAME", appbarName.getText().toString());
            startActivity(intent1);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        getReportData();
    }

//    @Override
//    public void onBackPressed() {
//
//    }

    public void getReportData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        paymentReceiveDataLists = new ArrayList<>();

        String url = pre_url_api+"report_manager/getPaymentRcvData?deptdid="+deptd_id+"&deptsid="+depts_id+"&pfnid="+pfn_id+"&b_date="+begin_date+"&e_date="+end_date;

        RequestQueue requestQueue = Volley.newRequestQueue(PaymentRcvReportShow.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String deptd_id_new = docInfo.getString("deptd_id")
                                .equals("null") ? "" : docInfo.getString("deptd_id");

                        String deptd_name = docInfo.getString("deptd_name")
                                .equals("null") ? "" : docInfo.getString("deptd_name");

                        String depts_id_new = docInfo.getString("depts_id")
                                .equals("null") ? "" : docInfo.getString("depts_id");

                        String depts_name = docInfo.getString("depts_name")
                                .equals("null") ? "" : docInfo.getString("depts_name");

                        String pfn_id_new = docInfo.getString("pfn_id")
                                .equals("null") ? "" : docInfo.getString("pfn_id");

                        String pfn_fee_name = docInfo.getString("pfn_fee_name")
                                .equals("null") ? "" : docInfo.getString("pfn_fee_name");

                        String prd_rate = docInfo.getString("prd_rate")
                                .equals("null") ? "" : docInfo.getString("prd_rate");

                        String tot_qty = docInfo.getString("tot_qty")
                                .equals("null") ? "" : docInfo.getString("tot_qty");

                        String tot_amt = docInfo.getString("tot_amt")
                                .equals("null") ? "" : docInfo.getString("tot_amt");

                        paymentReceiveDataLists.add(new PaymentReceiveDataList(deptd_id_new,deptd_name,depts_id_new,depts_name,
                                pfn_id_new,pfn_fee_name,prd_rate,tot_qty,tot_amt));
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

        requestQueue.add(stringRequest);
    }

    private void updateInterface() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                paymentRcvDataAdapter = new PaymentRcvDataAdapter(PaymentRcvReportShow.this,paymentReceiveDataLists);
                recyclerView.setAdapter(paymentRcvDataAdapter);

                if (paymentReceiveDataLists.isEmpty()) {
                    String tt = "৳ 0";
                    totalPayRcv.setText(tt);
                }
                else {
                    int am = 0;
                    for (int i = 0; i < paymentReceiveDataLists.size(); i++) {
                        am = am + Integer.parseInt(paymentReceiveDataLists.get(i).getTot_amt());
                    }
                    DecimalFormat formatter = new DecimalFormat("###,##,##,###");
                    String prt = formatter.format(am);
                    String tt = "৳ " + prt;
                    totalPayRcv.setText(tt);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PaymentRcvReportShow.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getReportData();
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