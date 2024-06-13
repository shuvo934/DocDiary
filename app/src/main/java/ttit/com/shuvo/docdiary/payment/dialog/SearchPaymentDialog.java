package ttit.com.shuvo.docdiary.payment.dialog;

import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_address;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_age;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_blood;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_cat_id;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_category;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_code;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_gender;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_marital;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_name;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_status;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_payment_code;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_payment_date;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_ph_id;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_ph_sub_code;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_prm_id;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.payment.PaymentCodeCallListener;
import ttit.com.shuvo.docdiary.payment.adapters.SearchPaymentAdapter;
import ttit.com.shuvo.docdiary.payment.arraylists.PaymentCodeList;

public class SearchPaymentDialog extends AppCompatDialogFragment implements SearchPaymentAdapter.ClickedItem {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    MaterialButton reload;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;

    TextInputLayout searchLay;
    TextInputEditText search;
    MaterialButton searchButton;
    TextView paymentSearchErrorMsg;
    MaterialButton clearButton;
    TextView latestPayListText;

    RecyclerView recyclerView;
    SearchPaymentAdapter searchPaymentAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView noPaymentFound;
    AlertDialog alertDialog;

    String searchingName = "";
    ArrayList<PaymentCodeList> paymentCodeLists;
    ArrayList<PaymentCodeList> fiftyPaymentLists;
    String parsing_message = "";

    AppCompatActivity activity;
    Context mContext;
    String p_year;
    String p_user;
    boolean firstGetter = true;

    public SearchPaymentDialog(Context context, String p_year, String p_user) {
        this.mContext = context;
        this.p_year = p_year;
        this.p_user = p_user;
    }

    private PaymentCodeCallListener paymentCodeCallListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof PaymentCodeCallListener)
            paymentCodeCallListener = (PaymentCodeCallListener) getActivity();

        View view = inflater.inflate(R.layout.payment_code_search_dialog_layout,null);

        activity = (AppCompatActivity) view.getContext();

        paymentCodeLists = new ArrayList<>();
        fiftyPaymentLists = new ArrayList<>();

        fullLayout = view.findViewById(R.id.search_payment_full_layout);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_search_payment_code_for_update);
        circularProgressIndicator.setVisibility(View.GONE);
        reload = view.findViewById(R.id.reload_page_button_search_payment_code_for_update);
        reload.setVisibility(View.GONE);
        noPaymentFound = view.findViewById(R.id.no_payment_found_message_for_update_payment);
        noPaymentFound.setVisibility(View.GONE);

        searchLay = view.findViewById(R.id.search_payment_code_layout_for_payment_dialogue);
        search = view.findViewById(R.id.search_payment_for_update_payment_dialogue);
        searchButton = view.findViewById(R.id.search_button_for_payment_code_to_update);
        paymentSearchErrorMsg = view.findViewById(R.id.search_payemnt_code_error_handling_msg);
        paymentSearchErrorMsg.setVisibility(View.GONE);
        clearButton = view.findViewById(R.id.clear_list_view_button_payment_search);
        clearButton.setVisibility(View.GONE);
        latestPayListText = view.findViewById(R.id.latest_fifty_payment_msg_in_payment_search);
        latestPayListText.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.payment_list_view_for_update_payment);
        firstGetter = true;

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                searchingName = s.toString();
                paymentSearchErrorMsg.setVisibility(View.GONE);
                if (searchingName.startsWith(".")) {
                    paymentSearchErrorMsg.setVisibility(View.VISIBLE);
                    String text = "Invalid Character. Code should not start with '.'";
                    paymentSearchErrorMsg.setText(text);
                }
            }
        });

        search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    search.clearFocus();
                    InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        searchButton.setOnClickListener(v -> {
            searchingName = Objects.requireNonNull(search.getText()).toString();
            if (!searchingName.isEmpty()) {
                if (searchingName.length() >= 5) {
                    if (!searchingName.contains("PR.")) {
                        search.clearFocus();
                        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        closeKeyBoard();
                        paymentSearchErrorMsg.setVisibility(View.GONE);
                        getSearchData();
                    }
                    else {
                        paymentSearchErrorMsg.setVisibility(View.VISIBLE);
                        String text = "Code must not include 'PR'";
                        paymentSearchErrorMsg.setText(text);
                        Toast.makeText(mContext, "Code must not include 'PR'", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    paymentSearchErrorMsg.setVisibility(View.VISIBLE);
                    String text = "Code must be greater than 4 character";
                    paymentSearchErrorMsg.setText(text);
                    Toast.makeText(mContext, "Code must be greater than 4 character", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                paymentSearchErrorMsg.setVisibility(View.VISIBLE);
                String text = "Please Provide Payment Code";
                paymentSearchErrorMsg.setText(text);
                Toast.makeText(mContext, "Please Provide Payment Code", Toast.LENGTH_SHORT).show();
            }

        });

        reload.setOnClickListener(v -> {
            if (firstGetter) {
                getList();
            }
            else {
                getSearchData();
            }
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> {
            if (loading) {
                Toast.makeText(mContext,"Please wait while loading",Toast.LENGTH_SHORT).show();
            }
            else {
                dialog.dismiss();
            }
        });

        clearButton.setOnClickListener(v -> {
            firstGetter = true;
            clearButton.setVisibility(View.GONE);
            searchingName = "";
            paymentSearchErrorMsg.setVisibility(View.GONE);
            search.setText("");

            latestPayListText.setVisibility(View.VISIBLE);
            if (fiftyPaymentLists.size() == 0) {
                noPaymentFound.setVisibility(View.VISIBLE);

            }
            else {
                noPaymentFound.setVisibility(View.GONE);
            }

            searchPaymentAdapter = new SearchPaymentAdapter(fiftyPaymentLists, mContext,this);
            recyclerView.setAdapter(searchPaymentAdapter);
        });

        getList();

        return alertDialog;
    }

    private void closeKeyBoard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

//    private void filter(String text) {
//        filteredList = new ArrayList<>();
//        for (PaymentCodeList item : paymentCodeLists) {
//            if (item.getPrm_code().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH)) ||
//                    item.getPat_name().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH))) {
//                filteredList.add((item));
//                isfiltered = true;
//            }
//        }
//        if (filteredList.size() == 0) {
//            noPaymentFound.setVisibility(View.VISIBLE);
//        }
//        else {
//            noPaymentFound.setVisibility(View.GONE);
//        }
//        if (searchPaymentAdapter != null) {
//            try {
//                searchPaymentAdapter.filterList(filteredList);
//            }
//            catch (Exception e) {
//                restart("App is paused for a long time. Please Start the app again.");
//            }
//        }
//        else {
//            restart("App is paused for a long time. Please Start the app again.");
//        }
//    }

    public void restart(String msg) {
        try {
            ProcessPhoenix.triggerRebirth(mContext);
        }
        catch (Exception e) {
            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }

    public void getList() {
        firstGetter = true;
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        reload.setVisibility(View.GONE);
        noPaymentFound.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        fiftyPaymentLists = new ArrayList<>();

        String url = pre_url_api+"payement_receive/getFiftyPaymentList";

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String pay_list = jsonObject.getString("pay_list");
                JSONArray array = new JSONArray(pay_list);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject docInfo = array.getJSONObject(i);

                    String prm_code = docInfo.getString("prm_code")
                            .equals("null") ? "" : docInfo.getString("prm_code");
                    String prm_id = docInfo.getString("prm_id")
                            .equals("null") ? "" : docInfo.getString("prm_id");
                    String prm_date = docInfo.getString("prm_date")
                            .equals("null") ? "" : docInfo.getString("prm_date");
                    String ph_sub_code = docInfo.getString("ph_sub_code")
                            .equals("null") ? "" : docInfo.getString("ph_sub_code");
                    String ph_patient_cat = docInfo.getString("ph_patient_cat")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat");
                    String ph_patient_cat_id = docInfo.getString("ph_patient_cat_id")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat_id");
                    String pat_name = docInfo.getString("pat_name")
                            .equals("null") ? "" : docInfo.getString("pat_name");
                    String pat_code = docInfo.getString("pat_code")
                            .equals("null") ? "" : docInfo.getString("pat_code");
                    String ph_id = docInfo.getString("ph_id")
                            .equals("null") ? "" : docInfo.getString("ph_id");
                    String pat_cell = docInfo.getString("pat_cell")
                            .equals("null") ? "" : docInfo.getString("pat_cell");
                    String pat_blood = docInfo.getString("pat_blood")
                            .equals("null") ? "" : docInfo.getString("pat_blood");
                    String ph_patient_status = docInfo.getString("ph_patient_status")
                            .equals("null") ? "" : docInfo.getString("ph_patient_status");
                    String p_age = docInfo.getString("p_age")
                            .equals("null") ? "" : docInfo.getString("p_age");
                    String gender = docInfo.getString("gender")
                            .equals("null") ? "" : docInfo.getString("gender");
                    String address = docInfo.getString("address")
                            .equals("null") ? "" : docInfo.getString("address");
                    String marital_status = docInfo.getString("marital_status")
                            .equals("null") ? "" : docInfo.getString("marital_status");

                    fiftyPaymentLists.add(new PaymentCodeList(prm_code,prm_id,prm_date,ph_sub_code,ph_patient_cat
                            ,ph_patient_cat_id,pat_name,pat_code,ph_id,p_age,pat_cell,
                            gender,pat_blood,address,ph_patient_cat,ph_patient_status,marital_status,""));
                }

                connected = true;
                updateLayout();
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateLayout();
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);
    }

    private void updateLayout() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                conn = false;
                connected = false;

                clearButton.setVisibility(View.GONE);
                searchingName = "";
                paymentSearchErrorMsg.setVisibility(View.GONE);
                search.setText("");

                latestPayListText.setVisibility(View.VISIBLE);
                if (fiftyPaymentLists.size() == 0) {
                    noPaymentFound.setVisibility(View.VISIBLE);

                }
                else {
                    noPaymentFound.setVisibility(View.GONE);
                }

                searchPaymentAdapter = new SearchPaymentAdapter(fiftyPaymentLists, mContext,this);
                recyclerView.setAdapter(searchPaymentAdapter);
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

    public void getSearchData() {
        firstGetter = false;
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        reload.setVisibility(View.GONE);
        noPaymentFound.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        paymentCodeLists = new ArrayList<>();

        String url = pre_url_api+"payement_receive/getPaymentCodeList?prm_code="+searchingName;

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String pay_list = jsonObject.getString("pay_list");
                JSONArray array = new JSONArray(pay_list);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject docInfo = array.getJSONObject(i);

                    String prm_code = docInfo.getString("prm_code")
                            .equals("null") ? "" : docInfo.getString("prm_code");
                    String prm_id = docInfo.getString("prm_id")
                            .equals("null") ? "" : docInfo.getString("prm_id");
                    String prm_date = docInfo.getString("prm_date")
                            .equals("null") ? "" : docInfo.getString("prm_date");
                    String ph_sub_code = docInfo.getString("ph_sub_code")
                            .equals("null") ? "" : docInfo.getString("ph_sub_code");
                    String ph_patient_cat = docInfo.getString("ph_patient_cat")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat");
                    String ph_patient_cat_id = docInfo.getString("ph_patient_cat_id")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat_id");
                    String pat_name = docInfo.getString("pat_name")
                            .equals("null") ? "" : docInfo.getString("pat_name");
                    String pat_code = docInfo.getString("pat_code")
                            .equals("null") ? "" : docInfo.getString("pat_code");
                    String ph_id = docInfo.getString("ph_id")
                            .equals("null") ? "" : docInfo.getString("ph_id");
                    String pat_cell = docInfo.getString("pat_cell")
                            .equals("null") ? "" : docInfo.getString("pat_cell");
                    String pat_blood = docInfo.getString("pat_blood")
                            .equals("null") ? "" : docInfo.getString("pat_blood");
                    String ph_patient_status = docInfo.getString("ph_patient_status")
                            .equals("null") ? "" : docInfo.getString("ph_patient_status");
                    String p_age = docInfo.getString("p_age")
                            .equals("null") ? "" : docInfo.getString("p_age");
                    String gender = docInfo.getString("gender")
                            .equals("null") ? "" : docInfo.getString("gender");
                    String address = docInfo.getString("address")
                            .equals("null") ? "" : docInfo.getString("address");
                    String marital_status = docInfo.getString("marital_status")
                            .equals("null") ? "" : docInfo.getString("marital_status");


                    paymentCodeLists.add(new PaymentCodeList(prm_code,prm_id,prm_date,ph_sub_code,ph_patient_cat
                            ,ph_patient_cat_id,pat_name,pat_code,ph_id,p_age,pat_cell,
                            gender,pat_blood,address,ph_patient_cat,ph_patient_status,marital_status,""));
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        requestQueue.add(stringRequest);
    }

    private void updateInterface() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                conn = false;
                connected = false;

                latestPayListText.setVisibility(View.GONE);
                clearButton.setVisibility(View.VISIBLE);

                if (paymentCodeLists.size() == 0) {
                    noPaymentFound.setVisibility(View.VISIBLE);
                }
                else {
                    noPaymentFound.setVisibility(View.GONE);
                }

                searchPaymentAdapter = new SearchPaymentAdapter(paymentCodeLists, mContext,this);
                recyclerView.setAdapter(searchPaymentAdapter);
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

    @Override
    public void onItemClicked(int Position) {
        String prm_code = "";
        String prm_id = "";
        String payment_date = "";
        String ph_id = "";
        String pat_name = "";
        String ph_sub_code = "";
        String pat_code = "";
        String pat_age = "";
        String pat_material = "";
        String pat_gender = "";
        String pat_blood = "";
        String pat_category = "";
        String pat_status = "";
        String pat_address = "";
        String pat_cat_id = "";

        if (firstGetter) {
            prm_code = fiftyPaymentLists.get(Position).getPrm_code();
            prm_id = fiftyPaymentLists.get(Position).getPrm_id();
            payment_date = fiftyPaymentLists.get(Position).getPrm_date();
            ph_id = fiftyPaymentLists.get(Position).getPh_id();
            pat_name = fiftyPaymentLists.get(Position).getPat_name();
            ph_sub_code = fiftyPaymentLists.get(Position).getPh_sub_code();
            pat_code = fiftyPaymentLists.get(Position).getPat_code();
            pat_age = fiftyPaymentLists.get(Position).getP_age();
            pat_material = fiftyPaymentLists.get(Position).getMarital_status();
            pat_gender = fiftyPaymentLists.get(Position).getGender();
            pat_blood = fiftyPaymentLists.get(Position).getPat_blood();
            pat_category = fiftyPaymentLists.get(Position).getPat_category_name();
            pat_status = fiftyPaymentLists.get(Position).getPh_patient_status();
            pat_address = fiftyPaymentLists.get(Position).getAddress();
            pat_cat_id = fiftyPaymentLists.get(Position).getPh_patient_cat_id();
        }
        else {
            prm_code = paymentCodeLists.get(Position).getPrm_code();
            prm_id = paymentCodeLists.get(Position).getPrm_id();
            payment_date = paymentCodeLists.get(Position).getPrm_date();
            ph_id = paymentCodeLists.get(Position).getPh_id();
            pat_name = paymentCodeLists.get(Position).getPat_name();
            ph_sub_code = paymentCodeLists.get(Position).getPh_sub_code();
            pat_code = paymentCodeLists.get(Position).getPat_code();
            pat_age = paymentCodeLists.get(Position).getP_age();
            pat_material = paymentCodeLists.get(Position).getMarital_status();
            pat_gender = paymentCodeLists.get(Position).getGender();
            pat_blood = paymentCodeLists.get(Position).getPat_blood();
            pat_category = paymentCodeLists.get(Position).getPat_category_name();
            pat_status = paymentCodeLists.get(Position).getPh_patient_status();
            pat_address = paymentCodeLists.get(Position).getAddress();
            pat_cat_id = paymentCodeLists.get(Position).getPh_patient_cat_id();
        }

        selected_payment_code = prm_code;
        selected_prm_id = prm_id;
        selected_payment_date = payment_date;
        selected_ph_id = ph_id;
        selected_pat_name = pat_name;
        selected_ph_sub_code = ph_sub_code;
        selected_pat_code = pat_code;
        selected_pat_age = pat_age;
        selected_pat_marital = pat_material;
        selected_pat_gender = pat_gender;
        selected_pat_blood = pat_blood;
        selected_pat_category = pat_category;
        selected_pat_status = pat_status;
        selected_pat_address = pat_address;
        selected_pat_cat_id = pat_cat_id;

        if(paymentCodeCallListener != null)
            paymentCodeCallListener.onPaymentSelection();

        alertDialog.dismiss();
    }
}
