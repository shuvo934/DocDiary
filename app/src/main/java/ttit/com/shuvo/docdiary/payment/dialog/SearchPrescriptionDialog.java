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
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_ph_id;
import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_ph_sub_code;
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
import ttit.com.shuvo.docdiary.payment.interfaces.PatCodeCallBackListener;
import ttit.com.shuvo.docdiary.payment.adapters.SearchPrescriptionAdapter;
import ttit.com.shuvo.docdiary.payment.arraylists.PrescriptionCodeList;

public class SearchPrescriptionDialog extends AppCompatDialogFragment implements SearchPrescriptionAdapter.ClickedItem {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    MaterialButton reload;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;

    TextInputLayout searchLay;
    TextInputEditText search;
    MaterialButton searchButton;
    TextView prescSearchErrorMsg;
    MaterialButton clearButton;
    TextView latestPatListText;

    RecyclerView recyclerView;
    SearchPrescriptionAdapter searchPrescriptionAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView noPatientFound;
    AlertDialog alertDialog;

    String searchingName = "";
    ArrayList<PrescriptionCodeList> prescriptionCodeLists;
    ArrayList<PrescriptionCodeList> fiftyPatLists;
    String parsing_message = "";

    AppCompatActivity activity;
    Context mContext;
    String p_year;
    boolean firstGetter = true;

    public SearchPrescriptionDialog(Context context, String p_year) {
        this.mContext = context;
        this.p_year = p_year;
    }

    Logger logger = Logger.getLogger("SearchPrescriptionDialog");

    private PatCodeCallBackListener patCodeCallBackListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof PatCodeCallBackListener)
            patCodeCallBackListener = (PatCodeCallBackListener) getActivity();

        View view = inflater.inflate(R.layout.prescription_code_search_dialog_layout,null);

        activity = (AppCompatActivity) view.getContext();

        prescriptionCodeLists = new ArrayList<>();
        fiftyPatLists = new ArrayList<>();

        fullLayout = view.findViewById(R.id.search_prescription_full_layout);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_search_pre_add_payment);
        circularProgressIndicator.setVisibility(View.GONE);
        reload = view.findViewById(R.id.reload_page_button);
        reload.setVisibility(View.GONE);
        noPatientFound = view.findViewById(R.id.no_patient_found_message_for_payment);
        noPatientFound.setVisibility(View.GONE);

        searchLay = view.findViewById(R.id.search_prescription_code_layout_for_patient_dialogue);
        search = view.findViewById(R.id.search_patient_or_presc_for_payment_dialogue);
        searchButton = view.findViewById(R.id.search_button_for_prescription_code_to_add_pay);
        prescSearchErrorMsg = view.findViewById(R.id.search_patient_name_code_error_handling_msg_add_pay);
        prescSearchErrorMsg.setVisibility(View.GONE);
        clearButton = view.findViewById(R.id.clear_list_view_button_prescription_search_add_pay);
        clearButton.setVisibility(View.GONE);
        latestPatListText = view.findViewById(R.id.latest_fifty_patient_msg_in_prescription_search);
        latestPatListText.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.patient_list_view_for_payment);
        firstGetter = true;

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

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
                prescSearchErrorMsg.setVisibility(View.GONE);
                if (searchingName.contains(".") || searchingName.contains(",") || searchingName.contains("-")) {
                    prescSearchErrorMsg.setVisibility(View.VISIBLE);
                    String text = "Invalid Character.";
                    prescSearchErrorMsg.setText(text);
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
                if (searchingName.length() >= 3) {
                    if (searchingName.contains(".") || searchingName.contains(",") || searchingName.contains("-")) {
                        prescSearchErrorMsg.setVisibility(View.VISIBLE);
                        String text = "Name or Code must not contain '.,-' character";
                        prescSearchErrorMsg.setText(text);
                        Toast.makeText(mContext, "Name or Code must not contain '.,-' character", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        search.clearFocus();
                        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        closeKeyBoard();
                        prescSearchErrorMsg.setVisibility(View.GONE);
                        getSearchData();
                    }
                }
                else {
                    prescSearchErrorMsg.setVisibility(View.VISIBLE);
                    String text = "Name or Code must be greater than 2 character";
                    prescSearchErrorMsg.setText(text);
                    Toast.makeText(mContext, "Name or Code must be greater than 2 character", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                prescSearchErrorMsg.setVisibility(View.VISIBLE);
                String text = "Please Provide Patient Name or Code";
                prescSearchErrorMsg.setText(text);
                Toast.makeText(mContext, "Please Provide Patient Name or Code", Toast.LENGTH_SHORT).show();
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
            prescSearchErrorMsg.setVisibility(View.GONE);
            search.setText("");

            latestPatListText.setVisibility(View.VISIBLE);
            if (fiftyPatLists.isEmpty()) {
                noPatientFound.setVisibility(View.VISIBLE);

            }
            else {
                noPatientFound.setVisibility(View.GONE);
            }

            searchPrescriptionAdapter = new SearchPrescriptionAdapter(fiftyPatLists, mContext,this);
            recyclerView.setAdapter(searchPrescriptionAdapter);
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
//        for (PrescriptionCodeList item : prescriptionCodeLists) {
//            if (item.getPh_sub_code().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH)) ||
//                    item.getPat_name().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH))) {
//                filteredList.add((item));
//                isfiltered = true;
//            }
//        }
//        if (filteredList.size() == 0) {
//            noPatientFound.setVisibility(View.VISIBLE);
//        }
//        else {
//            noPatientFound.setVisibility(View.GONE);
//        }
//        if (searchPrescriptionAdapter != null) {
//            try {
//                searchPrescriptionAdapter.filterList(filteredList);
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
        noPatientFound.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        fiftyPatLists = new ArrayList<>();

        String url = pre_url_api+"payement_receive/getFiftyPrescriptionList";

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String pat_pres_list = jsonObject.getString("pat_pres_list");
                JSONArray array = new JSONArray(pat_pres_list);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject docInfo = array.getJSONObject(i);

                    String ph_id = docInfo.getString("ph_id")
                            .equals("null") ? "" : docInfo.getString("ph_id");
                    String ph_sub_code = docInfo.getString("ph_sub_code")
                            .equals("null") ? "" : docInfo.getString("ph_sub_code");
                    String pat_name = docInfo.getString("pat_name")
                            .equals("null") ? "" : docInfo.getString("pat_name");
                    String pat_code = docInfo.getString("pat_code")
                            .equals("null") ? "" : docInfo.getString("pat_code");
                    String pat_cell = docInfo.getString("pat_cell")
                            .equals("null") ? "" : docInfo.getString("pat_cell");
                    String gender = docInfo.getString("gender")
                            .equals("null") ? "" : docInfo.getString("gender");
                    String pat_blood = docInfo.getString("pat_blood")
                            .equals("null") ? "" : docInfo.getString("pat_blood");
                    String address = docInfo.getString("address")
                            .equals("null") ? "" : docInfo.getString("address");
                    String ph_patient_cat = docInfo.getString("ph_patient_cat")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat");
                    String ph_patient_cat_id = docInfo.getString("ph_patient_cat_id")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat_id");
                    String ph_patient_status = docInfo.getString("ph_patient_status")
                            .equals("null") ? "" : docInfo.getString("ph_patient_status");
                    String marital_status = docInfo.getString("marital_status")
                            .equals("null") ? "" : docInfo.getString("marital_status");
                    String p_age = docInfo.getString("p_age")
                            .equals("null") ? "" : docInfo.getString("p_age");
                    String pat_date = docInfo.getString("pat_date")
                            .equals("null") ? "" : docInfo.getString("pat_date");


                    fiftyPatLists.add(new PrescriptionCodeList(ph_id,ph_sub_code,pat_name,pat_code,pat_cell
                            ,gender,pat_blood,address,ph_patient_cat,ph_patient_cat_id,ph_patient_cat,
                            ph_patient_status,marital_status,p_age,pat_date));
                }

                connected = true;
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
                prescSearchErrorMsg.setVisibility(View.GONE);
                search.setText("");

                latestPatListText.setVisibility(View.VISIBLE);
                if (fiftyPatLists.isEmpty()) {
                    noPatientFound.setVisibility(View.VISIBLE);

                }
                else {
                    noPatientFound.setVisibility(View.GONE);
                }

                searchPrescriptionAdapter = new SearchPrescriptionAdapter(fiftyPatLists, mContext,this);
                recyclerView.setAdapter(searchPrescriptionAdapter);

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
        noPatientFound.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        prescriptionCodeLists = new ArrayList<>();

        String url = pre_url_api+"payement_receive/getPrescriptionCodeList?ph_pat="+searchingName;

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String pat_pres_list = jsonObject.getString("pat_pres_list");
                JSONArray array = new JSONArray(pat_pres_list);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject docInfo = array.getJSONObject(i);

                    String ph_id = docInfo.getString("ph_id")
                            .equals("null") ? "" : docInfo.getString("ph_id");
                    String ph_sub_code = docInfo.getString("ph_sub_code")
                            .equals("null") ? "" : docInfo.getString("ph_sub_code");
                    String pat_name = docInfo.getString("pat_name")
                            .equals("null") ? "" : docInfo.getString("pat_name");
                    String pat_code = docInfo.getString("pat_code")
                            .equals("null") ? "" : docInfo.getString("pat_code");
                    String pat_cell = docInfo.getString("pat_cell")
                            .equals("null") ? "" : docInfo.getString("pat_cell");
                    String gender = docInfo.getString("gender")
                            .equals("null") ? "" : docInfo.getString("gender");
                    String pat_blood = docInfo.getString("pat_blood")
                            .equals("null") ? "" : docInfo.getString("pat_blood");
                    String address = docInfo.getString("address")
                            .equals("null") ? "" : docInfo.getString("address");
                    String ph_patient_cat = docInfo.getString("ph_patient_cat")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat");
                    String ph_patient_cat_id = docInfo.getString("ph_patient_cat_id")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat_id");
                    String ph_patient_status = docInfo.getString("ph_patient_status")
                            .equals("null") ? "" : docInfo.getString("ph_patient_status");
                    String marital_status = docInfo.getString("marital_status")
                            .equals("null") ? "" : docInfo.getString("marital_status");
                    String p_age = docInfo.getString("p_age")
                            .equals("null") ? "" : docInfo.getString("p_age");
                    String pat_date = docInfo.getString("pat_date")
                            .equals("null") ? "" : docInfo.getString("pat_date");


                    prescriptionCodeLists.add(new PrescriptionCodeList(ph_id,ph_sub_code,pat_name,pat_code,pat_cell
                            ,gender,pat_blood,address,ph_patient_cat,ph_patient_cat_id,ph_patient_cat,
                            ph_patient_status,marital_status,p_age,pat_date));
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

                latestPatListText.setVisibility(View.GONE);
                clearButton.setVisibility(View.VISIBLE);

                if (prescriptionCodeLists.isEmpty()) {
                    noPatientFound.setVisibility(View.VISIBLE);
                }
                else {
                    noPatientFound.setVisibility(View.GONE);
                }

                searchPrescriptionAdapter = new SearchPrescriptionAdapter(prescriptionCodeLists, mContext,this);
                recyclerView.setAdapter(searchPrescriptionAdapter);
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
        String ph_id;
        String pat_name;
        String ph_sub_code;
        String pat_code;
        String pat_age;
        String pat_material;
        String pat_gender;
        String pat_blood;
        String pat_category;
        String pat_status;
        String pat_address;
        String pat_cat_id;

        if (firstGetter) {
            ph_id = fiftyPatLists.get(Position).getPh_id();
            pat_name = fiftyPatLists.get(Position).getPat_name();
            ph_sub_code = fiftyPatLists.get(Position).getPh_sub_code();
            pat_code = fiftyPatLists.get(Position).getPat_code();
            pat_age = fiftyPatLists.get(Position).getP_age();
            pat_material = fiftyPatLists.get(Position).getMarital_status();
            pat_gender = fiftyPatLists.get(Position).getGender();
            pat_blood = fiftyPatLists.get(Position).getPat_blood();
            pat_category = fiftyPatLists.get(Position).getPat_category_name();
            pat_status = fiftyPatLists.get(Position).getPh_patient_status();
            pat_address = fiftyPatLists.get(Position).getAddress();
            pat_cat_id = fiftyPatLists.get(Position).getPh_patient_cat_id();
        }
        else {
            ph_id = prescriptionCodeLists.get(Position).getPh_id();
            pat_name = prescriptionCodeLists.get(Position).getPat_name();
            ph_sub_code = prescriptionCodeLists.get(Position).getPh_sub_code();
            pat_code = prescriptionCodeLists.get(Position).getPat_code();
            pat_age = prescriptionCodeLists.get(Position).getP_age();
            pat_material = prescriptionCodeLists.get(Position).getMarital_status();
            pat_gender = prescriptionCodeLists.get(Position).getGender();
            pat_blood = prescriptionCodeLists.get(Position).getPat_blood();
            pat_category = prescriptionCodeLists.get(Position).getPat_category_name();
            pat_status = prescriptionCodeLists.get(Position).getPh_patient_status();
            pat_address = prescriptionCodeLists.get(Position).getAddress();
            pat_cat_id = prescriptionCodeLists.get(Position).getPh_patient_cat_id();
        }

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

        if(patCodeCallBackListener != null)
            patCodeCallBackListener.onPatSelection();

        alertDialog.dismiss();
    }
}
