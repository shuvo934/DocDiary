package ttit.com.shuvo.docdiary.report_manager.report_view.reports;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.adapters.DocWisePayDataAdapter;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists.DocWisePayDataList;

public class DocWisePayRcvRepShow extends AppCompatActivity {

    ImageView close;
    TextView appbarName;
    MaterialButton pdfShow;

    RelativeLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DocWisePayDataAdapter docWisePayDataAdapter;
    ArrayList<DocWisePayDataList> docWisePayDataLists;

    TextView totalRegPay;
    TextView totalETSPay;
    TextView totalPay;
    TextView docName;
    TextView docDesignation;
    TextView docUnit;
    TextView docDepartment;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    String doc_name = "";
    String dep_name = "";
    String desig_name = "";
    String unit_name = "";
    String doc_id = "";
    String pfn_id = "";
    String begin_date = "";
    String end_date = "";

    String url = "";

    Logger logger = Logger.getLogger(DocWisePayRcvRepShow.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(DocWisePayRcvRepShow.this,R.color.clouds));
        setContentView(R.layout.activity_doc_wise_pay_rcv_rep_show);

        close = findViewById(R.id.close_logo_of_doc_wise_payment_rcv_app_made_report_show);
        appbarName = findViewById(R.id.doc_wise_payment_rcv_app_made_report_show_app_bar_text);
        pdfShow = findViewById(R.id.doc_wise_payment_rcv_pdf_report_show_report);

        fullLayout = findViewById(R.id.doc_wise_payment_rcv_report_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_doc_wise_payment_rcv_app_made_report_show);
        circularProgressIndicator.setVisibility(View.GONE);

        docName = findViewById(R.id.doctor_therapist_name_doc_wise_pay);
        docDesignation = findViewById(R.id.designation_doc_wise_pay);
        docUnit = findViewById(R.id.unit_name_doc_wise_pay);
        docDepartment = findViewById(R.id.department_name_doc_wise_pay);

        if (pre_url_api.contains("cstar")) {
            pdfShow.setVisibility(View.VISIBLE);
        }
        else {
            pdfShow.setVisibility(View.GONE);
        }

        recyclerView = findViewById(R.id.doc_wise_pay_receive_app_made_report_show_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        totalRegPay = findViewById(R.id.total_date_reg_amount_val_in_doc_wise_pay);
        totalETSPay = findViewById(R.id.total_date_ets_amount_val_in_doc_wise_pay);
        totalPay = findViewById(R.id.total_date_amount_val_in_doc_wise_pay);

        Intent intent = getIntent();
        url = intent.getStringExtra("REPORT_URL");
        begin_date = intent.getStringExtra("FIRST_DATE");
        end_date = intent.getStringExtra("LAST_DATE");
        pfn_id = intent.getStringExtra("PFN_ID");
        doc_id = intent.getStringExtra("DOC_ID");
        unit_name = intent.getStringExtra("UNIT_NAME");
        doc_name = intent.getStringExtra("DOC_NAME");
        dep_name = intent.getStringExtra("DEP_NAME");
        String app_bar_name = intent.getStringExtra("APP_BAR_NAME");

        appbarName.setText(app_bar_name);
        docUnit.setText(unit_name);
        docName.setText(doc_name);
        docDepartment.setText(dep_name);

        close.setOnClickListener(v -> finish());

        pdfShow.setOnClickListener(v -> {
            Intent intent1 = new Intent(DocWisePayRcvRepShow.this, ReportShow.class);
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

        docWisePayDataLists = new ArrayList<>();

        String url = pre_url_api+"report_manager/getDocWisePayData?p_doc_id="+doc_id+"&p_pfn_id="+pfn_id+"&p_begin_date="+begin_date+"&p_end_date="+end_date;

        RequestQueue requestQueue = Volley.newRequestQueue(DocWisePayRcvRepShow.this);

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

                        String app_date1 = docInfo.getString("app_date1")
                                .equals("null") ? "" : docInfo.getString("app_date1");

                        desig_name = docInfo.getString("designation")
                                .equals("null") ? "" : docInfo.getString("designation");

                        String date_total_reg = docInfo.getString("date_total_reg")
                                .equals("null") ? "0" : docInfo.getString("date_total_reg");

                        String date_total_ets = docInfo.getString("date_total_ets")
                                .equals("null") ? "0" : docInfo.getString("date_total_ets");

                        String date_total = docInfo.getString("date_total")
                                .equals("null") ? "0" : docInfo.getString("date_total");


                        docWisePayDataLists.add(new DocWisePayDataList(app_date1,date_total_reg,date_total_ets,date_total));
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

                docWisePayDataAdapter = new DocWisePayDataAdapter(docWisePayDataLists,DocWisePayRcvRepShow.this);
                recyclerView.setAdapter(docWisePayDataAdapter);

                docDesignation.setText(desig_name);

                if (docWisePayDataLists.isEmpty()) {
                    String rt = "0";
                    String et = "0";
                    String tt = "0";

                    totalRegPay.setText(rt);
                    totalETSPay.setText(et);
                    totalPay.setText(tt);

                }
                else {
                    int r_t = 0;
                    int e_t = 0;
                    int t_t = 0;
                    for (int i = 0; i < docWisePayDataLists.size(); i++) {
                        r_t = r_t + Integer.parseInt(docWisePayDataLists.get(i).getDate_total_reg());
                        e_t = e_t + Integer.parseInt(docWisePayDataLists.get(i).getDate_total_ets());
                        t_t = t_t + Integer.parseInt(docWisePayDataLists.get(i).getDate_total());
                    }


                    DecimalFormat formatter = new DecimalFormat("###,##,##,###");

                    String rt = formatter.format(r_t);
                    rt = "৳ " + rt;
                    String et = formatter.format(e_t);
                    et = "৳ " + et;
                    String tt = formatter.format(t_t);
                    tt = "৳ " + tt;

                    totalRegPay.setText(rt);
                    totalETSPay.setText(et);
                    totalPay.setText(tt);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocWisePayRcvRepShow.this);
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