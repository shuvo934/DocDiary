package ttit.com.shuvo.docdiary.hr_accounts.vouch_approval;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.adminInfoLists;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.adapters.DebitVoucherApprovedAdapter;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.arraylists.DebitVoucherDetailList;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.arraylists.VoucherSelectionList;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.dialogs.VoucherSelectionDialogue;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.interfaces.VoucherSelectListener;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.dialogs.VoucherDialog;

public class DebitVoucherApproval extends AppCompatActivity implements VoucherSelectListener {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    TextView voucherPendingCount;
    TextView voucherApprovedCount;
    String total_pending = "";
    String total_approved = "";

    TextInputEditText debitVoucherSelectSpinner;
    String vm_no = "";
    String vm_id = "";
    String status_approved_DV = "";
    String appDisUser_DV = "";
    String appDisTime_DV = "";

    CardView reportButton;

    RelativeLayout afterVoucherSelect;

    TextInputEditText vDate;
    TextInputEditText vTime;
    TextInputEditText preparedBy;
    TextInputEditText billRefNo;
    TextInputEditText billRefDate;
    TextInputEditText remarks;

    TextInputEditText statusReview;
    TextInputEditText appOrDisAppUser;
    TextInputEditText appOrDisAppTime;
    TextInputLayout appOrDisAppUserLay;
    TextInputLayout appOrDisAppTimeLay;

    TextView totalDebit;
    TextView totalCredit;

    Button approve;
    Button reject;

    ArrayList<VoucherSelectionList> voucherSelectionLists;

    RecyclerView itemView;
    DebitVoucherApprovedAdapter debitVoucherApprovedAdapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<DebitVoucherDetailList> debitVoucherDetailLists;

    double total_debit = 0.0;
    double total_credit = 0.0;
    String approve_flag = "";
    String v_type = "DV";

    Logger logger = Logger.getLogger(DebitVoucherApproval.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debit_voucher_approval);

        fullLayout = findViewById(R.id.debit_voucher_approved_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_debit_voucher_approved);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_debit_voucher_approved);

        voucherPendingCount = findViewById(R.id.debit_voucher_pending_total_count);
        voucherApprovedCount = findViewById(R.id.debit_voucher_approved_total_count);

        debitVoucherSelectSpinner = findViewById(R.id.debit_voucher_search_text);
        reportButton = findViewById(R.id.debit_voucher_report_button);
        reportButton.setVisibility(View.GONE);

        afterVoucherSelect = findViewById(R.id.debit_voucher_approved_details_card);
        afterVoucherSelect.setVisibility(View.GONE);

        vDate = findViewById(R.id.date_debit_approved);
        vTime = findViewById(R.id.time_debit_approved);
        preparedBy = findViewById(R.id.prepared_by_debit_approved);
        statusReview = findViewById(R.id.status_debit_approved);
        billRefNo = findViewById(R.id.bill_ref_no_debit_approved);
        billRefDate = findViewById(R.id.bill_ref_date_debit_approved);
        remarks = findViewById(R.id.remarks_debit_approved);

        appOrDisAppUser = findViewById(R.id.app_or_disapp_debit_approved);
        appOrDisAppUserLay = findViewById(R.id.app_or_disapp_debit_approved_layout);
        appOrDisAppTime = findViewById(R.id.app_or_disapp_time_debit_approved);
        appOrDisAppTimeLay = findViewById(R.id.app_or_disapp_time_debit_approved_layout);

        totalDebit = findViewById(R.id.total_debit_dva);
        totalCredit = findViewById(R.id.total_credit_dva);

        approve = findViewById(R.id.approve_button_dva);
        reject = findViewById(R.id.reject_button_dva);

        itemView = findViewById(R.id.debit_voucher_approved_transaction_view);

        itemView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        itemView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(itemView.getContext(),DividerItemDecoration.VERTICAL);
        itemView.addItemDecoration(dividerItemDecoration);

        voucherSelectionLists = new ArrayList<>();
        debitVoucherDetailLists = new ArrayList<>();

        debitVoucherSelectSpinner.setOnClickListener(view -> {
            String pre_code = "";
            if (!voucherSelectionLists.isEmpty()) {
                String vc = voucherSelectionLists.get(0).getVm_no();
                int i = vc.indexOf("."+v_type);
                pre_code = vc.substring(0,i+4);
            }

            VoucherSelectionDialogue voucherSelectionDialogue = new VoucherSelectionDialogue(DebitVoucherApproval.this,v_type,pre_code,voucherSelectionLists);
            voucherSelectionDialogue.show(getSupportFragmentManager(),"VSD");
        });

        reportButton.setOnClickListener(view -> {
            if (!vm_no.isEmpty()) {
                VoucherDialog debitCreditVoucher = new VoucherDialog(DebitVoucherApproval.this, vm_no);
                debitCreditVoucher.show(getSupportFragmentManager(),"V_CDJ");
            }
        });

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(DebitVoucherApproval.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        });

        approve.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DebitVoucherApproval.this);
            if (total_debit != total_credit) {
                alertDialogBuilder.setTitle("Warning!")
                        .setMessage("Total Debit Amount is not equal to Total Credit Amount")
                        .setNegativeButton("OK", (dialog, which) -> dialog.dismiss());

                AlertDialog alert = alertDialogBuilder.create();
                try {
                    alert.show();
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }
            }
//                else if (total_inv_amnt > total_debit){
//                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                    builder.setTitle("Warning!")
//                            .setMessage("Total Invoice Amount is greater than Total Debit Amount")
//                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            });
//                    AlertDialog alert = builder.create();
//                    alert.show();
//
//                }
            else {
                alertDialogBuilder.setTitle("Approve!")
                        .setMessage("Do you want to approve this voucher?")
                        .setPositiveButton("YES", (dialog, which) -> {
                            approve_flag = "1";
                            approveProcess();
                            dialog.dismiss();
                        })
                        .setNegativeButton("NO",(dialog, which) -> dialog.dismiss());

                AlertDialog alert = alertDialogBuilder.create();
                try {
                    alert.show();
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }
            }

        });

        reject.setOnClickListener(v -> {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DebitVoucherApproval.this);
            alertDialogBuilder.setTitle("Disapprove!")
                    .setMessage("Do you want to disapprove this voucher?")
                    .setPositiveButton("YES", (dialog, which) -> {
                        approve_flag = "0";
                        approveProcess();
                        dialog.dismiss();
                    })
                    .setNegativeButton("NO",(dialog, which) -> dialog.dismiss());

            AlertDialog alert = alertDialogBuilder.create();
            try {
                alert.show();
            }
            catch (Exception e) {
                restart("App is paused for a long time. Please Start the app again.");
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(DebitVoucherApproval.this, "Please wait while loading", Toast.LENGTH_SHORT).show();
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
        vm_no = "";
        vm_id = "";
        status_approved_DV = "";
        appDisUser_DV = "";
        appDisTime_DV = "";
        debitVoucherSelectSpinner.setText(vm_no);
        getVoucherLists();
    }

    @Override
    public void onVoucherSelect(String name, String id, String status, String approvedUser, String approvedTime) {
        vm_no = name;
        vm_id = id;
        status_approved_DV =  status;
        appDisUser_DV = approvedUser;
        appDisTime_DV = approvedTime;
        debitVoucherSelectSpinner.setText(vm_no);
        getVoucherDetails();
    }

    public void getVoucherLists() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        voucherSelectionLists = new ArrayList<>();

        String vListUrl = pre_url_api+"acc_dashboard/getVoucherSelectListHundred?v_type="+v_type;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest vListReq = new StringRequest(Request.Method.GET, vListUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String vm_id = docInfo.getString("vm_id")
                                .equals("null") ? "" : docInfo.getString("vm_id");
                        String vm_no = docInfo.getString("vm_no")
                                .equals("null") ? "" : docInfo.getString("vm_no");
                        String vm_date = docInfo.getString("vm_date")
                                .equals("null") ? "" : docInfo.getString("vm_date");
                        String vm_type = docInfo.getString("vm_type")
                                .equals("null") ? "" : docInfo.getString("vm_type");
                        String vm_bill_ref_no = docInfo.getString("vm_bill_ref_no")
                                .equals("null") ? "" : docInfo.getString("vm_bill_ref_no");
                        String vm_naration = docInfo.getString("vm_naration")
                                .equals("null") ? "" : docInfo.getString("vm_naration");
                        String bill_date = docInfo.getString("bill_date")
                                .equals("null") ? "" : docInfo.getString("bill_date");
                        String vm_voucher_approved_flag = docInfo.getString("vm_voucher_approved_flag")
                                .equals("null") ? "" : docInfo.getString("vm_voucher_approved_flag");
                        String amount = docInfo.getString("amount")
                                .equals("null") ? "" : docInfo.getString("amount");
                        String status = docInfo.getString("status")
                                .equals("null") ? "" : docInfo.getString("status");
                        String approved_user = docInfo.getString("approved_user")
                                .equals("null") ? "" : docInfo.getString("approved_user");
                        String approved_date = docInfo.getString("approved_date")
                                .equals("null") ? "" : docInfo.getString("approved_date");


                        voucherSelectionLists.add(new VoucherSelectionList(vm_id,vm_no,vm_date,vm_type,vm_bill_ref_no,vm_naration,bill_date,
                                vm_voucher_approved_flag,amount,status,approved_user,approved_date));
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

        requestQueue.add(vListReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                loading = false;
                conn = false;
                connected = false;
                reportButton.setVisibility(View.GONE);
                afterVoucherSelect.setVisibility(View.GONE);

                int p_i = 0;
                int a_i = 0;
                if (!voucherSelectionLists.isEmpty()) {
                    for (int i = 0; i < voucherSelectionLists.size(); i++) {
                        if (voucherSelectionLists.get(i).getVm_voucher_approved_flag().equals("0")) {
                            p_i++;
                        }
                        else {
                            a_i++;
                        }
                    }

                    total_approved = String.valueOf(a_i);
                    total_pending = String.valueOf(p_i);
                }
                else {
                    total_approved = "0";
                    total_pending = "0";
                }

                voucherPendingCount.setText(total_pending);
                voucherApprovedCount.setText(total_approved);

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
                    getVoucherLists();
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

    public void getVoucherDetails() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        debitVoucherDetailLists = new ArrayList<>();

        total_debit = 0.0;
        total_credit = 0.0;

        String url = pre_url_api+"acc_dashboard/getVoucherDetails?vm_id="+vm_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);
                        String credit_voucher_list = info.getString("voucher_list");
                        JSONArray voucherArray = new JSONArray(credit_voucher_list);
                        for (int j = 0; j < voucherArray.length(); j++) {
                            JSONObject voucher_info = voucherArray.getJSONObject(j);

                            String vm_id_new = voucher_info.getString("vm_id")
                                    .equals("null") ? "" : voucher_info.getString("vm_id");
                            String vm_no_new = voucher_info.getString("vm_no")
                                    .equals("null") ? "" : voucher_info.getString("vm_no");
                            String vm_date_new = voucher_info.getString("vm_date")
                                    .equals("null") ? "" : voucher_info.getString("vm_date");
                            String vm_time_new = voucher_info.getString("vm_time")
                                    .equals("null") ? "" : voucher_info.getString("vm_time");
                            String vm_naration = voucher_info.getString("vm_naration")
                                    .equals("null") ? "" : voucher_info.getString("vm_naration");
                            String vm_user = voucher_info.getString("vm_user")
                                    .equals("null") ? "" : voucher_info.getString("vm_user");
                            String vm_bill_ref_no = voucher_info.getString("vm_bill_ref_no")
                                    .equals("null") ? "" : voucher_info.getString("vm_bill_ref_no");
                            String vm_bill_ref_date = voucher_info.getString("vm_bill_ref_date")
                                    .equals("null") ? "" : voucher_info.getString("vm_bill_ref_date");
                            String vm_no_id = voucher_info.getString("vm_no_id")
                                    .equals("null") ? "" : voucher_info.getString("vm_no_id");
                            String vm_cid_id = voucher_info.getString("vm_cid_id")
                                    .equals("null") ? "" : voucher_info.getString("vm_cid_id");
                            String vm_proj_id = voucher_info.getString("vm_proj_id")
                                    .equals("null") ? "" : voucher_info.getString("vm_proj_id");
                            String vm_voucher_approved_flag = voucher_info.getString("vm_voucher_approved_flag")
                                    .equals("null") ? "0" : voucher_info.getString("vm_voucher_approved_flag");
                            String vm_voucher_approved_by = voucher_info.getString("vm_voucher_approved_by")
                                    .equals("null") ? "" : voucher_info.getString("vm_voucher_approved_by");
                            String vm_voucher_approved_time = voucher_info.getString("vm_voucher_approved_time")
                                    .equals("null") ? "" : voucher_info.getString("vm_voucher_approved_time");
                            String vm_der_id = voucher_info.getString("vm_der_id")
                                    .equals("null") ? "" : voucher_info.getString("vm_der_id");
                            String vd_id = voucher_info.getString("vd_id")
                                    .equals("null") ? "" : voucher_info.getString("vd_id");
                            String vd_ad_id = voucher_info.getString("vd_ad_id")
                                    .equals("null") ? "" : voucher_info.getString("vd_ad_id");
                            String ad_code = voucher_info.getString("ad_code")
                                    .equals("null") ? "" : voucher_info.getString("ad_code");
                            String ad_name = voucher_info.getString("ad_name")
                                    .equals("null") ? "" : voucher_info.getString("ad_name");
                            String vd_cheque_no = voucher_info.getString("vd_cheque_no")
                                    .equals("null") ? "" : voucher_info.getString("vd_cheque_no");
                            String vd_cheque_date = voucher_info.getString("vd_cheque_date")
                                    .equals("null") ? "" : voucher_info.getString("vd_cheque_date");
                            String vd_dr_amt = voucher_info.getString("vd_dr_amt")
                                    .equals("null") ? "" : voucher_info.getString("vd_dr_amt");
                            String vd_cr_amt = voucher_info.getString("vd_cr_amt")
                                    .equals("null") ? "" : voucher_info.getString("vd_cr_amt");

                            debitVoucherDetailLists.add(new DebitVoucherDetailList(vm_id_new,vm_no_new,vm_date_new,vm_time_new,vm_naration,
                                    vm_user,vm_bill_ref_no,vm_bill_ref_date,vm_no_id,vm_cid_id,vm_proj_id,vm_voucher_approved_flag,
                                    vm_voucher_approved_by,vm_voucher_approved_time,vm_der_id,vd_id,vd_ad_id,ad_code,ad_name,vd_cheque_no,
                                    vd_cheque_date,vd_dr_amt,vd_cr_amt,false));
                        }
                    }
                }
                if (!debitVoucherDetailLists.isEmpty()) {
                    for (int i = 0; i < debitVoucherDetailLists.size() ; i++) {
                        if (debitVoucherDetailLists.get(i).getVdDebit() != null) {
                            if (!debitVoucherDetailLists.get(i).getVdDebit().isEmpty()) {
                                total_debit = total_debit + Double.parseDouble(debitVoucherDetailLists.get(i).getVdDebit());
                            }
                        }
                        if (debitVoucherDetailLists.get(i).getVdCredit() != null) {
                            if (!debitVoucherDetailLists.get(i).getVdCredit().isEmpty()) {
                                total_credit = total_credit + Double.parseDouble(debitVoucherDetailLists.get(i).getVdCredit());
                            }
                        }
                    }
                }
                connected = true;
                updateFragment();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateFragment();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateFragment();
        });

        requestQueue.add(request);
    }

    private void updateFragment() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                loading = false;
                conn = false;
                connected = false;
                reportButton.setVisibility(View.VISIBLE);
                afterVoucherSelect.setVisibility(View.VISIBLE);

                if (status_approved_DV.equals("Approved")) {
                    approve.setVisibility(View.GONE);
                    reject.setVisibility(View.VISIBLE);

                    if (appDisUser_DV != null) {
                        if (!appDisUser_DV.isEmpty()) {
                            appOrDisAppUserLay.setHint("Approved By:");
                            appOrDisAppUser.setText(appDisUser_DV);
                            appOrDisAppTimeLay.setHint("Approved Time:");
                            appOrDisAppTime.setText(appDisTime_DV);
                        }
                        else {
                            appOrDisAppUserLay.setHint("Approved By:");
                            String tt = "No Name found";
                            appOrDisAppUser.setText(tt);
                            appOrDisAppTimeLay.setHint("Approved Time:");
                            appOrDisAppTime.setText(appDisTime_DV);
                        }
                    }
                    else {
                        appOrDisAppUserLay.setHint("Approved By:");
                        String tt = "No Name found";
                        appOrDisAppUser.setText(tt);
                        appOrDisAppTimeLay.setHint("Approved Time:");
                        appOrDisAppTime.setText(appDisTime_DV);
                    }
                }
                else if (status_approved_DV.equals("Pending")) {
                    approve.setVisibility(View.VISIBLE);
                    reject.setVisibility(View.GONE);

                    if (appDisUser_DV != null) {
                        if (!appDisUser_DV.isEmpty()) {
                            appOrDisAppUserLay.setHint("Disapproved By:");
                            appOrDisAppUser.setText(appDisUser_DV);
                            appOrDisAppTimeLay.setHint("Disapproved Time:");
                            appOrDisAppTime.setText(appDisTime_DV);
                        }
                        else {
                            appOrDisAppUserLay.setHint("Approved By / Disapproved By:");
                            appOrDisAppUser.setText(appDisUser_DV);
                            appOrDisAppTimeLay.setHint("Approved / Disapproved Time:");
                            appOrDisAppTime.setText(appDisTime_DV);
                        }
                    }
                    else {
                        appOrDisAppUserLay.setHint("Approved By / Disapproved By:");
                        appOrDisAppUser.setText(appDisUser_DV);
                        appOrDisAppTimeLay.setHint("Approved / Disapproved Time:");
                        appOrDisAppTime.setText(appDisTime_DV);
                    }
                }

                statusReview.setText(status_approved_DV);

                debitVoucherApprovedAdapter = new DebitVoucherApprovedAdapter(debitVoucherDetailLists,DebitVoucherApproval.this);
                itemView.setAdapter(debitVoucherApprovedAdapter);

                if (!debitVoucherDetailLists.isEmpty()) {
                    vDate.setText(debitVoucherDetailLists.get(0).getVmDate());
                    vTime.setText(debitVoucherDetailLists.get(0).getVmTime());
                    preparedBy.setText(debitVoucherDetailLists.get(0).getVmUser());
                    billRefNo.setText(debitVoucherDetailLists.get(0).getVmBillRefNo());
                    billRefDate.setText(debitVoucherDetailLists.get(0).getVmBillRefDate());
                    remarks.setText(debitVoucherDetailLists.get(0).getVmNarration());
                }
                else {
                    vDate.setText("");
                    vTime.setText("");
                    preparedBy.setText("");
                    billRefNo.setText("");
                    billRefDate.setText("");
                    remarks.setText("");
                }

                DecimalFormat formatter = new DecimalFormat("##,##,##,###.##");
                String formatted = formatter.format(total_debit);
                totalDebit.setText(formatted);

                formatted = formatter.format(total_credit);
                totalCredit.setText(formatted);

            }
            else {
                alertMessageVD();
            }
        }
        else {
            alertMessageVD();
        }
    }

    public void alertMessageVD() {
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

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getVoucherDetails();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    reportButton.setVisibility(View.GONE);
                    afterVoucherSelect.setVisibility(View.GONE);
                    vm_no = "";
                    vm_id = "";
                    status_approved_DV = "";
                    appDisUser_DV = "";
                    appDisTime_DV = "";
                    debitVoucherSelectSpinner.setText(vm_no);
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

//    public void checkToGetBillListFirst() {
//        if (debitVoucherApprovedLists.size() != 0) {
//            boolean allUpdated = false;
//            for (int i = 0; i < debitVoucherApprovedLists.size(); i++) {
//                allUpdated = debitVoucherApprovedLists.get(i).isUpdated();
//                if (!debitVoucherApprovedLists.get(i).isUpdated()) {
//                    allUpdated = debitVoucherApprovedLists.get(i).isUpdated();
//                    String vd_id = debitVoucherApprovedLists.get(i).getVdid();
//                    getBillListsFirst(vd_id, i);
//                    break;
//                }
//            }
//            if (allUpdated) {
//                connected = true;
//                updateFragment();
//            }
//        }
//        else {
//            connected = true;
//            updateFragment();
//        }
//    }
//
//    public void getBillListsFirst(String vd_id, int firstIndex) {
//        String url = "http://103.56.208.123:8001/apex/tterp/voucherApprove/getVoucherBillList?vd_id="+vd_id+"&avdp_id=&choice_flag=1";
//        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
//        ArrayList<DebitVABillLists> debitVABillLists = new ArrayList<>();
//
//        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
//            conn = true;
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                String items = jsonObject.getString("items");
//                String count = jsonObject.getString("count");
//                if (!count.equals("0")) {
//                    JSONArray array = new JSONArray(items);
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject info = array.getJSONObject(i);
//                        String voucher_bill_list = info.getString("voucher_bill_list");
//                        JSONArray voucherArray = new JSONArray(voucher_bill_list);
//                        for (int j = 0; j < voucherArray.length(); j++) {
//                            JSONObject bill_info = voucherArray.getJSONObject(j);
//
//                            String avdp_id = bill_info.getString("avdp_id");
//                            String avdp_vd_id = bill_info.getString("avdp_vd_id");
//                            String avdp_remarks= bill_info.getString("avdp_remarks");
//                            String avdp_brm_id = bill_info.getString("avdp_brm_id");
//                            String brm_no = bill_info.getString("brm_no");
//                            String brm_type_flag = bill_info.getString("brm_type_flag");
//
//                            debitVABillLists.add(new DebitVABillLists(avdp_id,avdp_vd_id,avdp_remarks,
//                                    avdp_brm_id,brm_no,brm_type_flag,false,new ArrayList<>()));
//                        }
//                    }
//                }
//                checkToGetBillListSecond(debitVABillLists, firstIndex);
//            }
//            catch (JSONException e) {
//                e.printStackTrace();
//                connected = false;
//                updateFragment();
//            }
//        }, error -> {
//            error.printStackTrace();
//            conn = false;
//            connected = false;
//            updateFragment();
//        });
//
//        requestQueue.add(request);
//    }
//
//    public void checkToGetBillListSecond(ArrayList<DebitVABillLists> debitVABillLists, int firstIndex) {
//        if (debitVABillLists.size() != 0) {
//            boolean allUpdated = false;
//            for (int i = 0; i < debitVABillLists.size(); i++) {
//                allUpdated = debitVABillLists.get(i).isUpdated();
//                if (!debitVABillLists.get(i).isUpdated()) {
//                    allUpdated = debitVABillLists.get(i).isUpdated();
//                    String avdp_id = debitVABillLists.get(i).getAvdpId();
//                    getBillListsSecond(debitVABillLists, avdp_id, i,firstIndex);
//                    break;
//                }
//            }
//            if (allUpdated) {
//                debitVoucherApprovedLists.get(firstIndex).setDebitVABillLists(debitVABillLists);
//                debitVoucherApprovedLists.get(firstIndex).setUpdated(true);
//                checkToGetBillListFirst();
//            }
//        }
//        else {
//            debitVoucherApprovedLists.get(firstIndex).setDebitVABillLists(debitVABillLists);
//            debitVoucherApprovedLists.get(firstIndex).setUpdated(true);
//            checkToGetBillListFirst();
//        }
//    }
//
//    public void getBillListsSecond(ArrayList<DebitVABillLists> debitVABillLists, String avdp_id, int secondIndex,int firstIndex) {
//        String url = "http://103.56.208.123:8001/apex/tterp/voucherApprove/getVoucherBillList?vd_id=&avdp_id="+avdp_id+"&choice_flag=2";
//        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
//        ArrayList<DebitVABillDetailsLists> debitVABillDetailsLists = new ArrayList<>();
//
//        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
//            conn = true;
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                String items = jsonObject.getString("items");
//                String count = jsonObject.getString("count");
//                if (!count.equals("0")) {
//                    JSONArray array = new JSONArray(items);
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject info = array.getJSONObject(i);
//                        String voucher_bill_list = info.getString("voucher_bill_list");
//                        JSONArray voucherArray = new JSONArray(voucher_bill_list);
//                        for (int j = 0; j < voucherArray.length(); j++) {
//                            JSONObject bill_info = voucherArray.getJSONObject(j);
//
//                            String vj_id = bill_info.getString("vj_id");
//                            String vj_avdp_id = bill_info.getString("vj_avdp_id");
//                            String vj_amt= bill_info.getString("vj_amt")
//                                    .equals("null") ? "" : bill_info.getString("vj_amt");
//                            String vj_brb_id = bill_info.getString("vj_brb_id");
//                            String vj_vat_paid_amt = bill_info.getString("vj_vat_paid_amt");
//                            String wom_no = bill_info.getString("wom_no");
//                            String invoice_no = bill_info.getString("invoice_no");
//
//                            if (!vj_amt.isEmpty()) {
//                                total_inv_amnt  = total_inv_amnt + Double.parseDouble(vj_amt);
//                            }
//
//                            debitVABillDetailsLists.add(new DebitVABillDetailsLists(vj_id,vj_avdp_id,vj_amt,
//                                    vj_brb_id,vj_vat_paid_amt,wom_no,invoice_no));
//
//                        }
//                    }
//                }
//                debitVABillLists.get(secondIndex).setDebitVABillDetailsLists(debitVABillDetailsLists);
//                debitVABillLists.get(secondIndex).setUpdated(true);
//                checkToGetBillListSecond(debitVABillLists, firstIndex);
//            }
//            catch (JSONException e) {
//                e.printStackTrace();
//                connected = false;
//                updateFragment();
//            }
//        }, error -> {
//            error.printStackTrace();
//            conn = false;
//            connected = false;
//            updateFragment();
//        });
//
//        requestQueue.add(request);
//    }

    public void approveProcess() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        String url = pre_url_api+"acc_dashboard/updateVoucher";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                connected = string_out.equals("Successfully Created");
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
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_VM_ID",vm_id);
                headers.put("P_APPROVE_FLAG",approve_flag);
                headers.put("P_USER_ID",adminInfoLists.get(0).getUsr_name());
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void updateLayout() {
        if (conn) {
            if (connected) {
                loading = false;
                conn = false;
                connected = false;
                reportButton.setVisibility(View.GONE);
                afterVoucherSelect.setVisibility(View.GONE);

                if (approve_flag.equals("1")) {
                    Toast.makeText(getApplicationContext(),"APPROVED",Toast.LENGTH_SHORT).show();
                }
                else if (approve_flag.equals("0")) {
                    Toast.makeText(getApplicationContext(),"DISAPPROVED",Toast.LENGTH_SHORT).show();
                }
                vm_no = "";
                vm_id = "";
                status_approved_DV = "";
                appDisUser_DV = "";
                appDisTime_DV = "";
                debitVoucherSelectSpinner.setText(vm_no);
                getVoucherLists();
            }
            else {
                alertMessageApCr();
            }
        }
        else {
            alertMessageApCr();
        }
    }

    public void alertMessageApCr() {
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

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    approveProcess();
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