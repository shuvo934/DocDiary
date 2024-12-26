package ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.dialogs;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.adapters.DebitCreditVoucherAdapter;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists.DebitCreditVoucherItemList;

public class VoucherDialog extends AppCompatDialogFragment {
    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    MaterialButton reload;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    TextView debitVoucherNo;
    TextView billRefNo;
    TextView billRefDate;
    TextView vmDate;
    TextView totalDebit;
    TextView totalCredit;
    TextView narration;
    TextView debitCreditText;

    RecyclerView itemView;
    RecyclerView.LayoutManager layoutManager;
    DebitCreditVoucherAdapter debitCreditVoucherAdapter;

    AlertDialog alertDialog;
    AppCompatActivity activity;
    String vm_no;

    double total_debit = 0.0;
    double total_credit = 0.0;

    ArrayList<DebitCreditVoucherItemList> debitCreditVoucherItemLists;
    Context mContext;

    public VoucherDialog(Context mContext, String vm_no) {
        this.mContext = mContext;
        this.vm_no = vm_no;
    }

    Logger logger = Logger.getLogger("VoucherDialog");

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.voucher_report_view, null);

        activity = (AppCompatActivity) view.getContext();

        fullLayout = view.findViewById(R.id.voucher_report_full_layout);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_voucher_report);
        circularProgressIndicator.setVisibility(View.GONE);
        reload = view.findViewById(R.id.reload_page_button_voucher_report);
        reload.setVisibility(View.GONE);

        debitVoucherNo = view.findViewById(R.id.debit_voucher_no);
        billRefNo = view.findViewById(R.id.bill_ref_no_dv);
        billRefDate = view.findViewById(R.id.bill_ref_date_dv);
        vmDate = view.findViewById(R.id.date_dv);
        totalDebit = view.findViewById(R.id.total_debit_dv);
        totalCredit = view.findViewById(R.id.total_credit_dv);
        itemView = view.findViewById(R.id.debit_voucher_details_view);
        narration = view.findViewById(R.id.narration_item_dv);
        debitCreditText = view.findViewById(R.id.debit_or_credit_text);

        debitCreditVoucherItemLists = new ArrayList<>();

        itemView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        itemView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(itemView.getContext(),DividerItemDecoration.VERTICAL);
        itemView.addItemDecoration(dividerItemDecoration);

        if (vm_no.contains("DV")) {
            String tt = "Debit Voucher";
            debitCreditText.setText(tt);
        }
        else if (vm_no.contains("CV")) {
            String tt = "Credit Voucher";
            debitCreditText.setText(tt);
        }
        else if (vm_no.contains("JV")) {
            String tt = "Journal Voucher";
            debitCreditText.setText(tt);
        }
        debitVoucherNo.setText(vm_no);

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        reload.setOnClickListener(v -> getItemData());

        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
            if (loading) {
                Toast.makeText(mContext,"Please wait while loading",Toast.LENGTH_SHORT).show();
            }
            else {
                dialog.dismiss();
            }
        });

        getItemData();

        return alertDialog;
    }

    public void getItemData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        reload.setVisibility(View.GONE);
        conn = false;
        connected = false;
        debitCreditVoucherItemLists = new ArrayList<>();
        total_debit = 0.0;
        total_credit = 0.0;

        String url = pre_url_api+"acc_dashboard/getVoucherReport?p_vm_no="+vm_no;

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject voucher_info = array.getJSONObject(i);

                        String vm_bill_ref_no = voucher_info.getString("vm_bill_ref_no")
                                .equals("null") ? "" : voucher_info.getString("vm_bill_ref_no");
                        String vm_bill_ref_date = voucher_info.getString("vm_bill_ref_date")
                                .equals("null") ? "" : voucher_info.getString("vm_bill_ref_date");
                        String vm_date = voucher_info.getString("vm_date")
                                .equals("null") ? "" : voucher_info.getString("vm_date");
                        String vm_type = voucher_info.getString("vm_type")
                                .equals("null") ? "" : voucher_info.getString("vm_type");
                        String vm_naration = voucher_info.getString("vm_naration")
                                .equals("null") ? "" : voucher_info.getString("vm_naration");
                        String ad_code = voucher_info.getString("ad_code")
                                .equals("null") ? "" : voucher_info.getString("ad_code");
                        String ad_name = voucher_info.getString("ad_name")
                                .equals("null") ? "" : voucher_info.getString("ad_name");
                        String vd_dr_amt = voucher_info.getString("vd_dr_amt")
                                .equals("null") ? "" : voucher_info.getString("vd_dr_amt");
                        String vd_cr_amt = voucher_info.getString("vd_cr_amt")
                                .equals("null") ? "" : voucher_info.getString("vd_cr_amt");

                        debitCreditVoucherItemLists.add(new DebitCreditVoucherItemList(vm_no,vm_date,vm_type,
                                vm_bill_ref_no,vm_bill_ref_date,vm_naration, ad_code,ad_name,vd_dr_amt, vd_cr_amt));

                        if (!vd_dr_amt.isEmpty()) {
                            total_debit = total_debit + Double.parseDouble(vd_dr_amt);
                        }
                        if (!vd_cr_amt.isEmpty()) {
                            total_credit = total_credit + Double.parseDouble(vd_cr_amt);
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

        requestQueue.add(stringRequest);
    }

    private void updateFragment() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                conn = false;
                connected = false;

                System.out.println("SIZE: " + debitCreditVoucherItemLists.size());

                debitCreditVoucherAdapter = new DebitCreditVoucherAdapter(debitCreditVoucherItemLists,getContext());
                itemView.setAdapter(debitCreditVoucherAdapter);

                DecimalFormat formatter = new DecimalFormat("##,##,##,###.##");
                String formatted = formatter.format(total_debit);

                totalDebit.setText(formatted);

                formatted = formatter.format(total_credit);
                totalCredit.setText(formatted);

                billRefNo.setText(debitCreditVoucherItemLists.get(0).getVm_bill_ref_no());
                billRefDate.setText(debitCreditVoucherItemLists.get(0).getVm_bill_ref_date());
                vmDate.setText(debitCreditVoucherItemLists.get(0).getVm_date());

                String totalNarration = debitCreditVoucherItemLists.get(0).getVm_naration();

                narration.setText(totalNarration);

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
        reload.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        Toast.makeText(mContext, parsing_message, Toast.LENGTH_SHORT).show();
        loading = false;
    }

}
