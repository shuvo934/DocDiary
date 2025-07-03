package ttit.com.shuvo.docdiary.hr_accounts.pay_invoice;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import ttit.com.shuvo.docdiary.hr_accounts.pay_invoice.adapters.PaymentInvoiceAdapter;
import ttit.com.shuvo.docdiary.hr_accounts.pay_invoice.arraylists.PaymentInvoiceItemList;

public class PaymentInvoice extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    TextView paymentCode;
    TextView paymentDate;
    TextView patientName;
    TextView patientCode;
    TextView prescCode;
    TextView patAge;
    TextView patGender;
    TextView patMaritalStatus;
    TextView patCategory;
    TextView patAddress;
    TextView patPhone;

    String payment_code = "";
    String payment_date = "";
    String pat_name = "";
    String pat_code = "";
    String presc_code = "";
    String pat_age = "";
    String pat_gender = "";
    String pat_mar_status = "";
    String pat_cat = "";
    String pat_address = "";
    String pat_phone = "";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    PaymentInvoiceAdapter paymentInvoiceAdapter;

    ArrayList<PaymentInvoiceItemList> paymentInvoiceItemLists;

    TextView subTotal;
    LinearLayout discountLay;
    TextView discountTotal;
    LinearLayout serviceChargeLay;
    TextView serviceChargeTotal;
    TextView grandTotal;

    String sub_total = "";
    String disc_total = "";
    String serv_charge_total = "";
    String grand_total = "";

    String prm_code = "";
    Logger logger = Logger.getLogger(PaymentInvoice.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_invoice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pay_inv_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.payment_invoice_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_payment_invoice);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_payment_invoice);

        paymentCode = findViewById(R.id.payment_code_in_pay_inv);
        paymentDate = findViewById(R.id.payment_date_in_pay_inv);
        patientName = findViewById(R.id.patient_name_in_pay_inv);
        patientCode = findViewById(R.id.patient_code_in_pay_inv);
        prescCode = findViewById(R.id.prescription_code_in_pay_inv);
        patAge = findViewById(R.id.patient_age_in_pay_inv);
        patGender = findViewById(R.id.patient_gender_in_pay_inv);
        patMaritalStatus = findViewById(R.id.patient_marital_status_in_pay_inv);
        patCategory = findViewById(R.id.patient_category_in_pay_inv);
        patAddress = findViewById(R.id.patient_address_in_pay_inv);
        patPhone = findViewById(R.id.patient_phone_no_in_pay_inv);

        recyclerView = findViewById(R.id.payment_invoice_item_report_view);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        subTotal = findViewById(R.id.sub_total_in_pay_inv);
        discountLay = findViewById(R.id.discount_layout_in_pay_inv);
        discountTotal = findViewById(R.id.discount_in_pay_inv);
        serviceChargeLay = findViewById(R.id.service_charge_layout_in_pay_inv);
        serviceChargeTotal = findViewById(R.id.service_charge_in_pay_inv);
        grandTotal = findViewById(R.id.grand_total_in_pay_inv);

        paymentInvoiceItemLists = new ArrayList<>();

        Intent intent = getIntent();
        prm_code = intent.getStringExtra("PRM_CODE");

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(PaymentInvoice.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
                }
                else {
                    finish();
                }
            }
        });

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(PaymentInvoice.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        getPaymentInvoice();

    }

    public void getPaymentInvoice() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        paymentInvoiceItemLists = new ArrayList<>();
        payment_code = "";
        payment_date = "";
        pat_name = "";
        pat_code = "";
        presc_code = "";
        pat_age = "";
        pat_gender = "";
        pat_mar_status = "";
        pat_cat = "";
        pat_address = "";
        pat_phone = "";

        sub_total = "";
        disc_total = "";
        serv_charge_total = "";
        grand_total = "";

        String paymentInvUrl = pre_url_api+"acc_dashboard/getPaymentInvoice?p_prm_code="+prm_code;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest paymentInvReq = new StringRequest(Request.Method.GET, paymentInvUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        payment_code = docInfo.getString("prm_code")
                                .equals("null") ? "" : docInfo.getString("prm_code");
                        payment_date = docInfo.getString("prm_date")
                                .equals("null") ? "N/A" : docInfo.getString("prm_date");
                        pat_name = docInfo.getString("pat_name")
                                .equals("null") ? "N/A" : docInfo.getString("pat_name");
                        pat_code = docInfo.getString("pat_code")
                                .equals("null") ? "N/A" : docInfo.getString("pat_code");
                        presc_code = docInfo.getString("ph_sub_code")
                                .equals("null") ? "N/A" : docInfo.getString("ph_sub_code");
                        pat_age = docInfo.getString("p_age")
                                .equals("null") ? "N/A" : docInfo.getString("p_age");
                        pat_gender = docInfo.getString("gender")
                                .equals("null") ? "N/A" : docInfo.getString("gender");
                        pat_mar_status = docInfo.getString("marital_status")
                                .equals("null") ? "N/A" : docInfo.getString("marital_status");
                        pat_cat = docInfo.getString("ph_patient_cat")
                                .equals("null") ? "N/A" : docInfo.getString("ph_patient_cat");
                        pat_address = docInfo.getString("address")
                                .equals("null") ? "N/A" : docInfo.getString("address");
                        pat_phone = docInfo.getString("pat_phone")
                                .equals("null") ? "N/A" : docInfo.getString("pat_phone");

                        String pfn_fee_name = docInfo.getString("pfn_fee_name")
                                .equals("null") ? "" : docInfo.getString("pfn_fee_name");
                        String depts_name = docInfo.getString("depts_name")
                                .equals("null") ? "" : docInfo.getString("depts_name");
                        String prd_payment_for_month = docInfo.getString("prd_payment_for_month")
                                .equals("null") ? "" : docInfo.getString("prd_payment_for_month");
                        String prd_rate = docInfo.getString("prd_rate")
                                .equals("null") ? "0" : docInfo.getString("prd_rate");
                        String prd_qty = docInfo.getString("prd_qty")
                                .equals("null") ? "0" : docInfo.getString("prd_qty");
                        String amt = docInfo.getString("amt")
                                .equals("null") ? "0" : docInfo.getString("amt");
                        String prd_service_charge_pct = docInfo.getString("prd_service_charge_pct")
                                .equals("null") ? "0" : docInfo.getString("prd_service_charge_pct");
                        String prd_discount_amt = docInfo.getString("prd_discount_amt")
                                .equals("null") ? "0" : docInfo.getString("prd_discount_amt");
                        String prm_amt = docInfo.getString("prm_amt")
                                .equals("null") ? "0" : docInfo.getString("prm_amt");

                        paymentInvoiceItemLists.add(new PaymentInvoiceItemList(String.valueOf(i+1),pfn_fee_name,depts_name,
                                prd_payment_for_month,prd_rate,prd_qty,amt,prd_service_charge_pct,prd_discount_amt,prm_amt));

                    }

                    connected = true;
                    updateInterface();
                }
                else {
                    connected = false;
                    parsing_message = "No data found";
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

        requestQueue.add(paymentInvReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);

                conn = false;
                connected = false;

                paymentCode.setText(payment_code);
                paymentDate.setText(payment_date);
                patientName.setText(pat_name);
                patientCode.setText(pat_code);
                prescCode.setText(presc_code);
                patAge.setText(pat_age);
                patGender.setText(pat_gender);
                patMaritalStatus.setText(pat_mar_status);
                patCategory.setText(pat_cat);
                patAddress.setText(pat_address);
                patPhone.setText(pat_phone);

                paymentInvoiceAdapter = new PaymentInvoiceAdapter(paymentInvoiceItemLists,PaymentInvoice.this);
                recyclerView.setAdapter(paymentInvoiceAdapter);

                DecimalFormat formatter = new DecimalFormat("###,##,##,###.#");

                double st = 0.0;
                double dt = 0.0;
                double sc = 0.0;
                for (int i = 0; i < paymentInvoiceItemLists.size(); i++) {
                    st = st + Double.parseDouble(paymentInvoiceItemLists.get(i).getAmt());
                    dt = dt + Double.parseDouble(paymentInvoiceItemLists.get(i).getPrd_discount_amt());

                    double scp = Double.parseDouble(paymentInvoiceItemLists.get(i).getPrd_service_charge_pct());

                    double amt = Double.parseDouble(paymentInvoiceItemLists.get(i).getAmt());
                    double dt_s = Double.parseDouble(paymentInvoiceItemLists.get(i).getPrd_discount_amt());

                    double amtt = amt - dt_s;

                    double sc_s = (amtt * scp) / 100;
                    sc = sc + sc_s;

                }

                double gt = st - dt + sc;

                sub_total = formatter.format(st);
                sub_total = "৳ " + sub_total;

                if (dt == 0.0) {
                    discountLay.setVisibility(View.GONE);
                }
                else {
                    discountLay.setVisibility(View.VISIBLE);
                }

                if (sc == 0.0) {
                    serviceChargeLay.setVisibility(View.GONE);
                }
                else {
                    serviceChargeLay.setVisibility(View.VISIBLE);
                }

                disc_total = formatter.format(dt);
                disc_total = "৳ " + disc_total;

                serv_charge_total = formatter.format(sc);
                serv_charge_total = "৳ " + serv_charge_total;

                grand_total = formatter.format(gt);
                grand_total = "৳ " + grand_total;

                subTotal.setText(sub_total);
                discountTotal.setText(disc_total);
                serviceChargeTotal.setText(serv_charge_total);
                grandTotal.setText(grand_total);

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

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(PaymentInvoice.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getPaymentInvoice();
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