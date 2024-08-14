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

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.adapters.DocWiseAppointDataAdapter;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.adapters.PaymentRcvDataAdapter;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists.DocWiseAppointDataList;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists.PaymentReceiveDataList;

public class DocWiseAppRepShow extends AppCompatActivity {

    ImageView close;
    TextView appbarName;
    MaterialButton pdfShow;

    RelativeLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DocWiseAppointDataAdapter docWiseAppointDataAdapter;
    ArrayList<DocWiseAppointDataList> docWiseAppointDataLists;

    TextView totalRegTime;
    TextView totalExtraTime;
    TextView totalBlockTime;
    TextView totalBlankTime;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    String depts_id = "";
    String deptd_id = "";
    String doc_id = "";
    String begin_date = "";
    String end_date = "";

    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(DocWiseAppRepShow.this,R.color.clouds));
        setContentView(R.layout.activity_doc_wise_app_rep_show);

        close = findViewById(R.id.close_logo_of_doc_wise_app_app_made_report_show);
        appbarName = findViewById(R.id.doc_wise_app_app_made_report_show_app_bar_text);
        pdfShow = findViewById(R.id.doc_wise_app_pdf_report_show_report);

        if (pre_url_api.contains("cstar")) {
            pdfShow.setVisibility(View.VISIBLE);
        }
        else {
            pdfShow.setVisibility(View.GONE);
        }

        fullLayout = findViewById(R.id.doc_wise_app_report_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_doc_wise_app_app_made_report_show);
        circularProgressIndicator.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.doc_wise_app_app_made_report_show_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        totalRegTime = findViewById(R.id.total_reg_time_in_doc_wise_app_rep);
        totalExtraTime = findViewById(R.id.total_extra_time_in_doc_wise_app_rep);
        totalBlockTime = findViewById(R.id.total_blocked_time_in_doc_wise_app_rep);
        totalBlankTime = findViewById(R.id.total_blank_time_in_doc_wise_app_rep);

        Intent intent = getIntent();
        url = intent.getStringExtra("REPORT_URL");
        begin_date = intent.getStringExtra("FIRST_DATE");
        end_date = intent.getStringExtra("LAST_DATE");
        deptd_id = intent.getStringExtra("DEPTD_ID");
        depts_id = intent.getStringExtra("DEPTS_ID");
        doc_id = intent.getStringExtra("DOC_ID");
        String app_bar_name = intent.getStringExtra("APP_BAR_NAME");

        appbarName.setText(app_bar_name);

        close.setOnClickListener(v -> onBackPressed());

        pdfShow.setOnClickListener(v -> {
            Intent intent1 = new Intent(DocWiseAppRepShow.this, ReportShow.class);
            intent1.putExtra("REPORT_URL", url);
            intent1.putExtra("FIRST_DATE", begin_date);
            intent1.putExtra("LAST_DATE", end_date);
            intent1.putExtra("APP_BAR_NAME", appbarName.getText().toString());
            startActivity(intent1);
        });

        getReportData();
    }

    @Override
    public void onBackPressed() {
        if (loading) {
            Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
        }
        else {
            super.onBackPressed();
        }
    }

    public void getReportData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        docWiseAppointDataLists = new ArrayList<>();

        String url = pre_url_api+"report_manager/getDocWiseAppData?p_deptd_id="+deptd_id+"&p_depts_id="+depts_id+"&p_doc_id="+doc_id+"&p_begin_date="+begin_date+"&p_end_date="+end_date;

        RequestQueue requestQueue = Volley.newRequestQueue(DocWiseAppRepShow.this);

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

                        String depts_id_new = docInfo.getString("depts_id")
                                .equals("null") ? "" : docInfo.getString("depts_id");

                        String depts_name = docInfo.getString("depts_name")
                                .equals("null") ? "" : docInfo.getString("depts_name");

                        String doc_id_new = docInfo.getString("doc_id")
                                .equals("null") ? "" : docInfo.getString("doc_id");

                        String doc_name = docInfo.getString("doc_name")
                                .equals("null") ? "" : docInfo.getString("doc_name");

                        String regular_count = docInfo.getString("regular_count")
                                .equals("null") ? "0" : docInfo.getString("regular_count");

                        String extra_count = docInfo.getString("extra_count")
                                .equals("null") ? "0" : docInfo.getString("extra_count");

                        String block_count = docInfo.getString("block_count")
                                .equals("null") ? "0" : docInfo.getString("block_count");

                        String blank_count = docInfo.getString("blank_count")
                                .equals("null") ? "0" : docInfo.getString("blank_count");

                        docWiseAppointDataLists.add(new DocWiseAppointDataList(deptd_id_new,depts_id_new,depts_name,doc_id_new,
                                doc_name,regular_count,extra_count,block_count,blank_count));
                    }
                }

                connected = true;
                updateInterface();
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

        requestQueue.add(stringRequest);
    }

    private void updateInterface() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                docWiseAppointDataAdapter = new DocWiseAppointDataAdapter(docWiseAppointDataLists,DocWiseAppRepShow.this);
                recyclerView.setAdapter(docWiseAppointDataAdapter);

                if (docWiseAppointDataLists.isEmpty()) {
                    String rt = "0";
                    String et = "0";
                    String bst = "0";
                    String blt = "0";
                    totalRegTime.setText(rt);
                    totalExtraTime.setText(et);
                    totalBlockTime.setText(bst);
                    totalBlankTime.setText(blt);
                }
                else {
                    int r_t = 0;
                    int e_t = 0;
                    int bs_t = 0;
                    int bl_t = 0;
                    for (int i = 0; i < docWiseAppointDataLists.size(); i++) {
                        r_t = r_t + Integer.parseInt(docWiseAppointDataLists.get(i).getRegular_count());
                        e_t = e_t + Integer.parseInt(docWiseAppointDataLists.get(i).getExtra_count());
                        bs_t = bs_t + Integer.parseInt(docWiseAppointDataLists.get(i).getBlock_count());
                        bl_t = bl_t + Integer.parseInt(docWiseAppointDataLists.get(i).getBlank_count());
                    }

                    String rt = String.valueOf(r_t);
                    String et = String.valueOf(e_t);
                    String bst = String.valueOf(bs_t);
                    String blt = String.valueOf(bl_t);

                    totalRegTime.setText(rt);
                    totalExtraTime.setText(et);
                    totalBlockTime.setText(bst);
                    totalBlankTime.setText(blt);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocWiseAppRepShow.this);
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