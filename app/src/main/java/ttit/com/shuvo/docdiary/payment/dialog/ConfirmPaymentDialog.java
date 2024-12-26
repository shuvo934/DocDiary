package ttit.com.shuvo.docdiary.payment.dialog;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selectedPaymentMethodLists;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.payment.adapters.SelectedPayMethodAdapter;
import ttit.com.shuvo.docdiary.payment.arraylists.PaymentMethodList;
import ttit.com.shuvo.docdiary.payment.arraylists.SelectedPaymentMethodList;
import ttit.com.shuvo.docdiary.payment.arraylists.ServiceAmountIdList;
import ttit.com.shuvo.docdiary.payment.interfaces.ConfirmPaymentListener;

public class ConfirmPaymentDialog extends AppCompatDialogFragment {

    RelativeLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    MaterialButton reload;
    ImageView close;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    TextView totalAmount;
    TextView serviceCharge;
    TextView grandTotal;
    TextView needToPay;
    TextView paidAmount;

    TextInputLayout payMethodSpinnerLay;
    AppCompatAutoCompleteTextView payMethodSpinner;
    TextInputLayout payAmountLay;
    TextInputEditText payAmount;
    TextView amountMissingMsg;

    MaterialButton saveMethod;

    RecyclerView savedPayMethod;
    RecyclerView.LayoutManager layoutManager;
    SelectedPayMethodAdapter selectedPayMethodAdapter;
    ArrayList<PaymentMethodList> paymentMethodLists;
    TextView noPaymentMethodFound;

    MaterialButton cancel;
    MaterialButton confirmPayment;

    Logger logger = Logger.getLogger("ConfirmPaymentDialog");

    AlertDialog alertDialog;
    AppCompatActivity activity;
    Context mContext;

    ArrayList<ServiceAmountIdList> pfnIds;
    String service_amount;
    String service_charge = "0";
    String grand_total = "0";
    String need_to_pay = "0";
    String paid_amount = "0";
    int reload_choice = 0;

    String selected_pmm_id = "";
    String selected_pmd_id = "";
    String selected_ad_id = "";
    String selected_pmm_name = "";
    String selected_account_name = "";
    String selected_pmm_amnt = "";

    private ConfirmPaymentListener confirmPaymentListener;

    public ConfirmPaymentDialog(Context mContext, String service_amount, ArrayList<ServiceAmountIdList> pfnIds) {
        this.mContext = mContext;
        this.service_amount = service_amount;
        this.pfnIds = pfnIds;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof ConfirmPaymentListener)
            confirmPaymentListener = (ConfirmPaymentListener) getActivity();

        View view = inflater.inflate(R.layout.payment_mode_layout,null);

        activity = (AppCompatActivity) view.getContext();

        paymentMethodLists = new ArrayList<>();

        fullLayout = view.findViewById(R.id.confirm_payment_full_layout);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_confirm_payment);
        circularProgressIndicator.setVisibility(View.GONE);
        reload = view.findViewById(R.id.reload_page_button_confirm_payment);
        reload.setVisibility(View.GONE);
        noPaymentMethodFound = view.findViewById(R.id.no_payment_method_selected_msg);
        noPaymentMethodFound.setVisibility(View.VISIBLE);

        close = view.findViewById(R.id.close_logo_of_confirm_payment);

        totalAmount = view.findViewById(R.id.service_total_amount);
        serviceCharge = view.findViewById(R.id.payment_service_charge);
        grandTotal = view.findViewById(R.id.payment_grand_total);
        needToPay = view.findViewById(R.id.payment_need_to_pay);
        paidAmount = view.findViewById(R.id.payment_paid_amount);

        payMethodSpinnerLay = view.findViewById(R.id.spinner_layout_payment_mode_type);
        payMethodSpinner = view.findViewById(R.id.spinner_payment_mode_type);

        payAmountLay = view.findViewById(R.id.amount_in_payment_mode_layout);
        payAmountLay.setEnabled(false);
        payAmount = view.findViewById(R.id.amount_in_payment_mode);
        amountMissingMsg = view.findViewById(R.id.payment_mode_amount_missing_msg);
        amountMissingMsg.setVisibility(View.GONE);

        saveMethod = view.findViewById(R.id.save_payment_mode);

        savedPayMethod = view.findViewById(R.id.saved_payment_mode_list);

        cancel = view.findViewById(R.id.cancel_payment_button);
        confirmPayment = view.findViewById(R.id.confirm_payment_button);

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        savedPayMethod.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        savedPayMethod.setLayoutManager(layoutManager);
        selectedPayMethodAdapter = new SelectedPayMethodAdapter(selectedPaymentMethodLists, mContext);
        savedPayMethod.setAdapter(selectedPayMethodAdapter);

        payMethodSpinner.setOnItemClickListener((adapterView, view12, i, l) -> {
            selected_pmm_id = "";
            selected_pmd_id = "";
            selected_ad_id = "";
            selected_pmm_name = "";
            selected_account_name = "";
            String name = adapterView.getItemAtPosition(i).toString();
            selected_pmm_name = name;
            for (int j = 0; j < paymentMethodLists.size(); j++) {
                if (name.equals(paymentMethodLists.get(j).getPmm_name())) {
                    selected_pmm_id = paymentMethodLists.get(j).getPmm_id();
                    selected_pmd_id = paymentMethodLists.get(j).getPmd_id();
                    selected_ad_id = paymentMethodLists.get(j).getAd_id();
                    selected_account_name = paymentMethodLists.get(j).getAccount_name();
                }
            }
            payAmountLay.setEnabled(true);
            if (selected_pmm_amnt.isEmpty()) {
                double pa = Double.parseDouble(paid_amount);
                double np = Double.parseDouble(need_to_pay);
                pa = pa + np;
                paid_amount = String.format(Locale.ENGLISH,"%.2f",pa);
                need_to_pay = "0.00";
                paidAmount.setText(paid_amount);
                needToPay.setText(need_to_pay);
                payAmount.setText(String.format(Locale.ENGLISH,"%.2f",np));
                selected_pmm_amnt = String.format(Locale.ENGLISH,"%.2f",np);
            }
        });

        payAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    String amnt = editable.toString();
                    if (!amnt.startsWith("0") && isNumeric(amnt)) {
                        amountMissingMsg.setVisibility(View.GONE);

                        double pre_paid_amnt = 0.00;
                        for (int i = 0; i < selectedPaymentMethodLists.size(); i++) {
                            pre_paid_amnt = pre_paid_amnt + Double.parseDouble(selectedPaymentMethodLists.get(i).getMethod_amount());
                        }
                        double g_tot = Double.parseDouble(grand_total);

                        double n_pay = g_tot - pre_paid_amnt;

                        double method_amnt = Double.parseDouble(amnt);

                        if (method_amnt > n_pay) {
                            selected_pmm_amnt = "";
                            String tee = "Amount is greater than need to pay";
                            amountMissingMsg.setText(tee);
                            amountMissingMsg.setVisibility(View.VISIBLE);

                            paid_amount = String.format(Locale.ENGLISH,"%.2f",pre_paid_amnt);
                            need_to_pay = String.format(Locale.ENGLISH,"%.2f",n_pay);

                            paidAmount.setText(paid_amount);
                            needToPay.setText(need_to_pay);
                        }
                        else {
                            selected_pmm_amnt = String.format(Locale.ENGLISH,"%.2f",method_amnt);
                            double pa = pre_paid_amnt + method_amnt;
                            paid_amount = String.format(Locale.ENGLISH, "%.2f",pa);
                            paidAmount.setText(paid_amount);

                            double new_n_pay = g_tot - pa;
                            need_to_pay = String.format(Locale.ENGLISH,"%.2f",new_n_pay);
                            needToPay.setText(need_to_pay);
                        }
                    }
                    else {
                        selected_pmm_amnt = "";
                        String tee = "Invalid Amount";
                        amountMissingMsg.setText(tee);
                        amountMissingMsg.setVisibility(View.VISIBLE);

                        double pre_paid_amnt = 0.00;
                        for (int i = 0; i < selectedPaymentMethodLists.size(); i++) {
                            pre_paid_amnt = pre_paid_amnt + Double.parseDouble(selectedPaymentMethodLists.get(i).getMethod_amount());
                        }
                        double g_tot = Double.parseDouble(grand_total);
                        double n_pay = g_tot - pre_paid_amnt;

                        paid_amount = String.format(Locale.ENGLISH,"%.2f",pre_paid_amnt);
                        need_to_pay = String.format(Locale.ENGLISH,"%.2f",n_pay);

                        paidAmount.setText(paid_amount);
                        needToPay.setText(need_to_pay);
                    }
                }
                else {
                    selected_pmm_amnt = "";
                    String tee = "Please Provide Amount";
                    amountMissingMsg.setText(tee);
                    amountMissingMsg.setVisibility(View.VISIBLE);

                    double pre_paid_amnt = 0.00;
                    for (int i = 0; i < selectedPaymentMethodLists.size(); i++) {
                        pre_paid_amnt = pre_paid_amnt + Double.parseDouble(selectedPaymentMethodLists.get(i).getMethod_amount());
                    }
                    double g_tot = Double.parseDouble(grand_total);
                    double n_pay = g_tot - pre_paid_amnt;

                    paid_amount = String.format(Locale.ENGLISH,"%.2f",pre_paid_amnt);
                    need_to_pay = String.format(Locale.ENGLISH,"%.2f",n_pay);

                    paidAmount.setText(paid_amount);
                    needToPay.setText(need_to_pay);

                }
            }
        });

        payAmount.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    payAmount.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        saveMethod.setOnClickListener(view1 -> {
            if (!selected_pmd_id.isEmpty()) {
                if (!selected_pmm_amnt.isEmpty() && !selected_pmm_amnt.startsWith("0") && isNumeric(selected_pmm_amnt)) {
                    selectedPaymentMethodLists.add(new SelectedPaymentMethodList(String.valueOf(selectedPaymentMethodLists.size()+1),
                            selected_pmm_id,selected_pmm_name,selected_pmd_id,selected_ad_id,selected_account_name,selected_pmm_amnt,false));

                    selected_pmm_id = "";
                    selected_pmd_id = "";
                    selected_ad_id = "";
                    selected_pmm_name = "";
                    selected_account_name = "";
                    selected_pmm_amnt = "";
                    payMethodSpinner.setText("");
                    payAmount.setText("");
                    amountMissingMsg.setVisibility(View.GONE);
                    payAmountLay.setEnabled(false);

                    double np = Double.parseDouble(need_to_pay);

                    if (np == 0.00) {
                        payMethodSpinnerLay.setEnabled(false);
                    }

                    ArrayList<String> type = new ArrayList<>();
                    for(int i = 0; i < paymentMethodLists.size(); i++) {
                        String pmm_name = paymentMethodLists.get(i).getPmm_name();
                        boolean found = false;
                        for (int j = 0; j < selectedPaymentMethodLists.size(); j++) {
                            if (pmm_name.equals(selectedPaymentMethodLists.get(j).getPmm_name())) {
                                found = true;
                            }
                        }
                        if (!found) {
                            type.add(paymentMethodLists.get(i).getPmm_name());
                        }
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                    payMethodSpinner.setAdapter(arrayAdapter);

                    noPaymentMethodFound.setVisibility(View.GONE);

                    selectedPayMethodAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(mContext, "Please Provide Amount", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(mContext, "Please Select Payment Method", Toast.LENGTH_SHORT).show();
            }
        });

        reload.setOnClickListener(view1 -> {
            reload.setVisibility(View.GONE);
            if (reload_choice == 1) {
                getPaymentMethod();
            }
            else {
                fullLayout.setVisibility(View.GONE);
                circularProgressIndicator.setVisibility(View.VISIBLE);
                checkToGetServiceCharge();
            }
        });

        selectedPayMethodAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (selectedPaymentMethodLists.isEmpty()) {
                    noPaymentMethodFound.setVisibility(View.VISIBLE);
                }
                else {
                    noPaymentMethodFound.setVisibility(View.GONE);
                }
                double pre_paid_amnt = 0.00;
                for (int i = 0; i < selectedPaymentMethodLists.size(); i++) {
                    pre_paid_amnt = pre_paid_amnt + Double.parseDouble(selectedPaymentMethodLists.get(i).getMethod_amount());
                }
                double g_tot = Double.parseDouble(grand_total);
                double n_pay = g_tot - pre_paid_amnt;

                paid_amount = String.format(Locale.ENGLISH,"%.2f",pre_paid_amnt);
                need_to_pay = String.format(Locale.ENGLISH,"%.2f",n_pay);

                payMethodSpinnerLay.setEnabled(n_pay != 0.00);

                paidAmount.setText(paid_amount);
                needToPay.setText(need_to_pay);

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < paymentMethodLists.size(); i++) {
                    String pmm_name = paymentMethodLists.get(i).getPmm_name();
                    boolean found = false;
                    for (int j = 0; j < selectedPaymentMethodLists.size(); j++) {
                        if (pmm_name.equals(selectedPaymentMethodLists.get(j).getPmm_name())) {
                            found = true;
                        }
                    }
                    if (!found) {
                        type.add(paymentMethodLists.get(i).getPmm_name());
                    }
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                payMethodSpinner.setAdapter(arrayAdapter);

            }
        });

        confirmPayment.setOnClickListener(view1 -> {
            double np = Double.parseDouble(need_to_pay);
            if (np == 0.00) {
                double paid_amnt = 0.00;
                for (int i = 0; i < selectedPaymentMethodLists.size(); i++) {
                    paid_amnt = paid_amnt + Double.parseDouble(selectedPaymentMethodLists.get(i).getMethod_amount());
                }
                double g_tot = Double.parseDouble(grand_total);
                if (g_tot == paid_amnt) {
                    if (confirmPaymentListener != null)
                        confirmPaymentListener.onPayConfirmation();
                    alertDialog.dismiss();
                }
                else {
                    Toast.makeText(mContext, "Please add your payment method first.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(mContext, "Please provide full payment information", Toast.LENGTH_SHORT).show();
            }
        });

        close.setOnClickListener(view1 -> {
            selectedPaymentMethodLists = new ArrayList<>();
            alertDialog.dismiss();
        });

        cancel.setOnClickListener(view1 -> {
            selectedPaymentMethodLists = new ArrayList<>();
            alertDialog.dismiss();
        });

        getPaymentMethod();

        return alertDialog;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void closeKeyBoard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void getPaymentMethod() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        reload.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        paymentMethodLists = new ArrayList<>();

        String url = pre_url_api+"payement_receive/getPaymentMode";

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
                        JSONObject info = array.getJSONObject(i);

                        String pmm_id = info.getString("pmm_id")
                                .equals("null") ? "" : info.getString("pmm_id");
                        String pmm_name = info.getString("pmm_name")
                                .equals("null") ? "" : info.getString("pmm_name");
                        String pmd_id = info.getString("pmd_id")
                                .equals("null") ? "" : info.getString("pmd_id");
                        String ad_id = info.getString("ad_id")
                                .equals("null") ? "" : info.getString("ad_id");
                        String account_name = info.getString("account_name")
                                .equals("null") ? "" : info.getString("account_name");

                        paymentMethodLists.add(new PaymentMethodList(pmm_id,pmm_name,pmd_id,ad_id,account_name));
                    }
                }

                connected = true;
                checkToGetServiceCharge();
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateLayout(1);
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateLayout(1);
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);
    }

    public void checkToGetServiceCharge() {
        boolean allUpdated = false;
        for (int x = 0; x < pfnIds.size(); x++) {
            allUpdated = pfnIds.get(x).isUpdated();
            if (!pfnIds.get(x).isUpdated()) {
                String pfn_id = pfnIds.get(x).getPfn_id();
                getServiceCharge(x,pfn_id);
                break;
            }
        }

        if (allUpdated) {
            conn = true;
            connected = true;
            updateLayout(2);
        }
    }

    public void getServiceCharge(int index, String pfn_id) {
        conn = false;
        connected = false;
        String s_c_Url = pre_url_api +"payement_receive/getServiceChargePFN?p_pfn_id="+pfn_id;
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest serviceChargeReq = new StringRequest(Request.Method.GET, s_c_Url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                String s_c = "0";
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                         s_c = info.getString("s_c")
                                .equals("null") ? "0" : info.getString("s_c");

                    }
                    connected = true;
                    pfnIds.get(index).setService_charge(s_c);
                    pfnIds.get(index).setUpdated(true);
                    checkToGetServiceCharge();
                }
                else {
                    connected = false;
                    updateLayout(2);
                }

            } catch (JSONException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateLayout(2);
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateLayout(2);
        });

        serviceChargeReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(serviceChargeReq);
    }

    private void updateLayout(int choice) {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                conn = false;
                connected = false;

                totalAmount.setText(service_amount);
                service_charge = "0";
                double tot_am = Double.parseDouble(service_amount);
                double tot_sc = 0.0;
                for (int i = 0; i < pfnIds.size(); i++) {
                    String amount = pfnIds.get(i).getAmount();
                    double a = Double.parseDouble(amount);
                    double p_sc = Double.parseDouble(pfnIds.get(i).getService_charge());
                    double sc = (a / 100) * p_sc;
                    tot_sc = tot_sc + sc;
                }
                service_charge = String.format(Locale.ENGLISH,"%.2f",tot_sc);
                serviceCharge.setText(service_charge);
                double tot_gr = tot_am + tot_sc;
                grand_total = String.format(Locale.ENGLISH,"%.2f",tot_gr);
                grandTotal.setText(grand_total);
                need_to_pay = grand_total;
                needToPay.setText(need_to_pay);
                paid_amount = "0.00";
                paidAmount.setText(paid_amount);

                payMethodSpinner.setText("");
                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < paymentMethodLists.size(); i++) {
                    type.add(paymentMethodLists.get(i).getPmm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                payMethodSpinner.setAdapter(arrayAdapter);

                loading = false;

            }
            else {
                reload_choice = choice;
                alertMessage();
            }
        }
        else {
            reload_choice = choice;
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
