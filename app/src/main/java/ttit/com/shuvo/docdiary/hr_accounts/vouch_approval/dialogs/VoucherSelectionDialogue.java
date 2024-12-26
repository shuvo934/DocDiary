package ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.dialogs;

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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.adapters.VoucherSelectAdapter;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.arraylists.VoucherSelectionList;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.interfaces.VoucherSelectListener;

public class VoucherSelectionDialogue extends AppCompatDialogFragment implements VoucherSelectAdapter.ClickedItem {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    MaterialButton reload;

    TextView preVCodeText;
    TextInputLayout searchLay;
    TextInputEditText search;
    MaterialButton searchButton;
    TextView voucherSearchErrorMsg;
    MaterialButton clearButton;
    TextView latestVoucherListText;

    RecyclerView recyclerView;
    VoucherSelectAdapter voucherSelectAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView noVoucherFound;

    String searchingName = "";
    ArrayList<VoucherSelectionList> voucherSelectionLists;
    ArrayList<VoucherSelectionList> hundredVoucherLists;


    AppCompatActivity activity;
    Context mContext;
    String v_type;
    String preCode;
    boolean firstGetter = true;
    AlertDialog alertDialog;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    private VoucherSelectListener voucherSelectListener;

    Logger logger = Logger.getLogger("VoucherSelectionDialogue");

    public VoucherSelectionDialogue(Context mContext, String v_type, String preCode, ArrayList<VoucherSelectionList> hundredVoucherLists) {
        this.mContext = mContext;
        this.v_type = v_type;
        this.preCode = preCode;
        this.hundredVoucherLists = hundredVoucherLists;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        if (getActivity() instanceof VoucherSelectListener)
            voucherSelectListener = (VoucherSelectListener) getActivity();

        View view = inflater.inflate(R.layout.voucher_select_list_view,null);

        activity = (AppCompatActivity) view.getContext();

        fullLayout = view.findViewById(R.id.search_voucher_dialog_full_layout);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_search_voucher_dialog);
        circularProgressIndicator.setVisibility(View.GONE);
        reload = view.findViewById(R.id.reload_page_button_search_voucher_dialog);
        reload.setVisibility(View.GONE);

        preVCodeText = view.findViewById(R.id.pre_v_code_text_select_voucher_dialog);
        preVCodeText.setText(preCode);
        searchLay = view.findViewById(R.id.search_voucher_no_layout_for_voucher_dialogue);
        search = view.findViewById(R.id.search_v_no_voucher_dialogue);
        searchButton = view.findViewById(R.id.search_button_for_voucher_no_to_approve);
        voucherSearchErrorMsg = view.findViewById(R.id.search_voucher_no_error_handling_msg);
        voucherSearchErrorMsg.setVisibility(View.GONE);
        clearButton = view.findViewById(R.id.clear_list_view_button_voucher_search);
        clearButton.setVisibility(View.GONE);
        latestVoucherListText = view.findViewById(R.id.latest_hundred_voucher_msg_in_voucher_search);
        latestVoucherListText.setVisibility(View.VISIBLE);
        String tt = "Latest 100 " + v_type + " Voucher";
        latestVoucherListText.setText(tt);

        recyclerView = view.findViewById(R.id.voucher_list_view_for_approve_voucher);
        noVoucherFound = view.findViewById(R.id.no_voucher_found_message_for_approve_voucher);
        if (hundredVoucherLists.isEmpty()) {
            noVoucherFound.setVisibility(View.VISIBLE);
        }
        else {
            noVoucherFound.setVisibility(View.GONE);
        }

        firstGetter = true;

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        voucherSelectAdapter = new VoucherSelectAdapter(hundredVoucherLists, mContext,this);
        recyclerView.setAdapter(voucherSelectAdapter);

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
                voucherSearchErrorMsg.setVisibility(View.GONE);
                if (searchingName.startsWith(".")) {
                    voucherSearchErrorMsg.setVisibility(View.VISIBLE);
                    String text = "Invalid Character. Code should not start with '.'";
                    voucherSearchErrorMsg.setText(text);
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

        searchButton.setOnClickListener(v -> {
            searchingName = Objects.requireNonNull(search.getText()).toString();
            if (!searchingName.isEmpty()) {
                if (searchingName.length() >= 3) {
                    if (!searchingName.contains(preCode)) {
                        search.clearFocus();
                        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        closeKeyBoard();
                        voucherSearchErrorMsg.setVisibility(View.GONE);
                        getSearchData();
                    }
                    else {
                        voucherSearchErrorMsg.setVisibility(View.VISIBLE);
                        String text = "Code must not include '"+preCode+"'";
                        voucherSearchErrorMsg.setText(text);
                        Toast.makeText(mContext, "Code must not include '"+preCode+"'", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    voucherSearchErrorMsg.setVisibility(View.VISIBLE);
                    String text = "Voucher No must be greater than 2 character";
                    voucherSearchErrorMsg.setText(text);
                    Toast.makeText(mContext, "Voucher No must be greater than 2 character", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                voucherSearchErrorMsg.setVisibility(View.VISIBLE);
                String text = "Please Provide Voucher No";
                voucherSearchErrorMsg.setText(text);
                Toast.makeText(mContext, "Please Provide Voucher No", Toast.LENGTH_SHORT).show();
            }

        });

        reload.setOnClickListener(v -> getSearchData());

        clearButton.setOnClickListener(v -> {
            firstGetter = true;
            clearButton.setVisibility(View.GONE);
            searchingName = "";
            voucherSearchErrorMsg.setVisibility(View.GONE);
            search.setText("");

            latestVoucherListText.setVisibility(View.VISIBLE);
            if (hundredVoucherLists.isEmpty()) {
                noVoucherFound.setVisibility(View.VISIBLE);
            }
            else {
                noVoucherFound.setVisibility(View.GONE);
            }

            voucherSelectAdapter = new VoucherSelectAdapter(hundredVoucherLists, mContext,this);
            recyclerView.setAdapter(voucherSelectAdapter);
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> {
            if (loading) {
                Toast.makeText(mContext,"Please wait while loading",Toast.LENGTH_SHORT).show();
            }
            else {
                dialog.dismiss();
            }
        });

        return alertDialog;

    }

    @Override
    public void onItemClicked(int Position) {
        String name;
        String id;
        String status;
        String approvedUser;
        String approvedTime;

        if (firstGetter) {
            name = hundredVoucherLists.get(Position).getVm_no();
            id = hundredVoucherLists.get(Position).getVm_id();
            status = hundredVoucherLists.get(Position).getStatus();
            approvedUser = hundredVoucherLists.get(Position).getApprovedUser();
            approvedTime = hundredVoucherLists.get(Position).getApproved_date();
        }
        else {
            name = voucherSelectionLists.get(Position).getVm_no();
            id = voucherSelectionLists.get(Position).getVm_id();
            status = voucherSelectionLists.get(Position).getStatus();
            approvedUser = voucherSelectionLists.get(Position).getApprovedUser();
            approvedTime = voucherSelectionLists.get(Position).getApproved_date();
        }

        if(voucherSelectListener != null)
            voucherSelectListener.onVoucherSelect(name,id,status,approvedUser,approvedTime);

        alertDialog.dismiss();
    }

    private void closeKeyBoard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void getSearchData() {
        firstGetter = false;
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        reload.setVisibility(View.GONE);
        noVoucherFound.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        voucherSelectionLists = new ArrayList<>();

        String url = pre_url_api+"acc_dashboard/getVoucherSelectListBySearch?v_type="+v_type+"&p_vm_no="+searchingName;

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

                latestVoucherListText.setVisibility(View.GONE);
                clearButton.setVisibility(View.VISIBLE);

                if (voucherSelectionLists.isEmpty()) {
                    noVoucherFound.setVisibility(View.VISIBLE);
                }
                else {
                    noVoucherFound.setVisibility(View.GONE);
                }

                voucherSelectAdapter = new VoucherSelectAdapter(voucherSelectionLists, mContext,this);
                recyclerView.setAdapter(voucherSelectAdapter);
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

    public void restart(String msg) {
        try {
            ProcessPhoenix.triggerRebirth(mContext);
        }
        catch (Exception e) {
            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }
}
