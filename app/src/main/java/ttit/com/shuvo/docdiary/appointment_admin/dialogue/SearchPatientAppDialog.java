package ttit.com.shuvo.docdiary.appointment_admin.dialogue;

import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.selected_patient_name;
import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.selected_pat_cat_id;
import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.selected_pat_id;
import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.selected_pat_ph_id;
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

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appointment_admin.PatAppSelectCallBackListener;
import ttit.com.shuvo.docdiary.appointment_admin.adapters.SearchPatientForAppAdapter;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.PatientForAppointList;

public class SearchPatientAppDialog extends AppCompatDialogFragment implements SearchPatientForAppAdapter.ClickedItem {
    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    MaterialButton reload;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;

    TextView topText;
    TextInputLayout searchLay;
    TextInputEditText search;
    MaterialButton searchButton;
    TextView patSearchErrorMsg;
    MaterialButton clearButton;
    TextView latestPatListText;

    RecyclerView recyclerView;
    SearchPatientForAppAdapter searchPatientForAppAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView noPatientFound;
    AlertDialog alertDialog;

    String searchingName = "";
    ArrayList<PatientForAppointList> patientForAppointLists;
    ArrayList<PatientForAppointList> fiftyPatLists;
    String parsing_message = "";

    AppCompatActivity activity;
    Context mContext;
    boolean firstGetter = true;

    public SearchPatientAppDialog(Context context) {
        this.mContext = context;
    }

    private PatAppSelectCallBackListener patAppSelectCallBackListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof PatAppSelectCallBackListener)
            patAppSelectCallBackListener = (PatAppSelectCallBackListener) getActivity();

        View view = inflater.inflate(R.layout.prescription_code_search_dialog_layout,null);

        activity = (AppCompatActivity) view.getContext();

        patientForAppointLists = new ArrayList<>();
        fiftyPatLists = new ArrayList<>();

        topText = view.findViewById(R.id.search_presc_dial_top_bar_text);
        String t = "Select Patient";
        topText.setText(t);

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
        patSearchErrorMsg = view.findViewById(R.id.search_patient_name_code_error_handling_msg_add_pay);
        patSearchErrorMsg.setVisibility(View.GONE);
        clearButton = view.findViewById(R.id.clear_list_view_button_prescription_search_add_pay);
        clearButton.setVisibility(View.GONE);
        latestPatListText = view.findViewById(R.id.latest_fifty_patient_msg_in_prescription_search);
        latestPatListText.setVisibility(View.GONE);
        String latest_text = "Latest 50 Patients Available for Schedule";
        latestPatListText.setText(latest_text);

        recyclerView = view.findViewById(R.id.patient_list_view_for_payment);
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
                patSearchErrorMsg.setVisibility(View.GONE);
                if (searchingName.contains(".") || searchingName.contains(",") || searchingName.contains("-")) {
                    patSearchErrorMsg.setVisibility(View.VISIBLE);
                    String text = "Invalid Character.";
                    patSearchErrorMsg.setText(text);
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
                        patSearchErrorMsg.setVisibility(View.VISIBLE);
                        String text = "Name or Code must not contain '.,-' character";
                        patSearchErrorMsg.setText(text);
                        Toast.makeText(mContext, "Name or Code must not contain '.,-' character", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        search.clearFocus();
                        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        closeKeyBoard();
                        patSearchErrorMsg.setVisibility(View.GONE);
                        getSearchData();
                    }
                }
                else {
                    patSearchErrorMsg.setVisibility(View.VISIBLE);
                    String text = "Name or Code must be greater than 2 character";
                    patSearchErrorMsg.setText(text);
                    Toast.makeText(mContext, "Name or Code must be greater than 2 character", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                patSearchErrorMsg.setVisibility(View.VISIBLE);
                String text = "Please Provide Patient Name or Code";
                patSearchErrorMsg.setText(text);
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
            patSearchErrorMsg.setVisibility(View.GONE);
            search.setText("");

            latestPatListText.setVisibility(View.VISIBLE);
            if (fiftyPatLists.size() == 0) {
                noPatientFound.setVisibility(View.VISIBLE);

            }
            else {
                noPatientFound.setVisibility(View.GONE);
            }

            searchPatientForAppAdapter = new SearchPatientForAppAdapter(fiftyPatLists, mContext,this);
            recyclerView.setAdapter(searchPatientForAppAdapter);
        });

        getList();

        return alertDialog;
    }

    @Override
    public void onItemClicked(int Position) {
        String ph_id = "";
        String pat_name = "";
        String pat_id = "";
        String pat_cat_id = "";
        if (firstGetter) {
            ph_id = fiftyPatLists.get(Position).getPh_id();
            pat_name = fiftyPatLists.get(Position).getPat_name();
            pat_id = fiftyPatLists.get(Position).getPh_pat_id();
            pat_cat_id = fiftyPatLists.get(Position).getPh_patient_cat_id();
        }
        else {
            ph_id = patientForAppointLists.get(Position).getPh_id();
            pat_name = patientForAppointLists.get(Position).getPat_name();
            pat_id = patientForAppointLists.get(Position).getPh_pat_id();
            pat_cat_id = patientForAppointLists.get(Position).getPh_patient_cat_id();
        }

        selected_patient_name = pat_name;
        selected_pat_ph_id = ph_id;
        selected_pat_id = pat_id;
        selected_pat_cat_id = pat_cat_id;

        if(patAppSelectCallBackListener != null)
            patAppSelectCallBackListener.onPatientSelection();

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

        String url = pre_url_api+"appointmentModify/getFiftyPatientListForAddApp";

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                JSONArray array = new JSONArray(items);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject docInfo = array.getJSONObject(i);

                    String ph_id = docInfo.getString("ph_id")
                            .equals("null") ? "" : docInfo.getString("ph_id");
                    String ph_pat_id = docInfo.getString("ph_pat_id")
                            .equals("null") ? "" : docInfo.getString("ph_pat_id");
                    String ph_sub_code = docInfo.getString("ph_sub_code")
                            .equals("null") ? "" : docInfo.getString("ph_sub_code");
                    String ph_date = docInfo.getString("ph_date")
                            .equals("null") ? "" : docInfo.getString("ph_date");
                    String ph_patient_cat_id = docInfo.getString("ph_patient_cat_id")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat_id");
                    String ph_patient_cat = docInfo.getString("ph_patient_cat")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat");
                    String pat_code = docInfo.getString("pat_code")
                            .equals("null") ? "" : docInfo.getString("pat_code");
                    String pat_name = docInfo.getString("pat_name")
                            .equals("null") ? "" : docInfo.getString("pat_name");
                    String pat_phone = docInfo.getString("pat_phone")
                            .equals("null") ? "" : docInfo.getString("pat_phone");


                    fiftyPatLists.add(new PatientForAppointList(ph_id,ph_pat_id,ph_sub_code,ph_date,ph_patient_cat_id,
                            ph_patient_cat,pat_code,pat_name,pat_phone));
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
                patSearchErrorMsg.setVisibility(View.GONE);
                search.setText("");

                latestPatListText.setVisibility(View.VISIBLE);
                if (fiftyPatLists.size() == 0) {
                    noPatientFound.setVisibility(View.VISIBLE);

                }
                else {
                    noPatientFound.setVisibility(View.GONE);
                }

                searchPatientForAppAdapter = new SearchPatientForAppAdapter(fiftyPatLists, mContext,this);
                recyclerView.setAdapter(searchPatientForAppAdapter);

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

        patientForAppointLists = new ArrayList<>();

        String url = pre_url_api+"appointmentModify/getPatListForAddAppBySearch?ph_pat="+searchingName;

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String pat_list = jsonObject.getString("pat_list");
                JSONArray array = new JSONArray(pat_list);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject docInfo = array.getJSONObject(i);

                    String ph_id = docInfo.getString("ph_id")
                            .equals("null") ? "" : docInfo.getString("ph_id");
                    String ph_pat_id = docInfo.getString("ph_pat_id")
                            .equals("null") ? "" : docInfo.getString("ph_pat_id");
                    String ph_sub_code = docInfo.getString("ph_sub_code")
                            .equals("null") ? "" : docInfo.getString("ph_sub_code");
                    String ph_date = docInfo.getString("ph_date")
                            .equals("null") ? "" : docInfo.getString("ph_date");
                    String ph_patient_cat_id = docInfo.getString("ph_patient_cat_id")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat_id");
                    String ph_patient_cat = docInfo.getString("ph_patient_cat")
                            .equals("null") ? "" : docInfo.getString("ph_patient_cat");
                    String pat_code = docInfo.getString("pat_code")
                            .equals("null") ? "" : docInfo.getString("pat_code");
                    String pat_name = docInfo.getString("pat_name")
                            .equals("null") ? "" : docInfo.getString("pat_name");
                    String pat_phone = docInfo.getString("pat_phone")
                            .equals("null") ? "" : docInfo.getString("pat_phone");

                    patientForAppointLists.add(new PatientForAppointList(ph_id,ph_pat_id,ph_sub_code,ph_date,ph_patient_cat_id,
                            ph_patient_cat,pat_code,pat_name,pat_phone));
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

                latestPatListText.setVisibility(View.GONE);
                clearButton.setVisibility(View.VISIBLE);

                if (patientForAppointLists.size() == 0) {
                    noPatientFound.setVisibility(View.VISIBLE);
                }
                else {
                    noPatientFound.setVisibility(View.GONE);
                }

                searchPatientForAppAdapter = new SearchPatientForAppAdapter(patientForAppointLists, mContext,this);
                recyclerView.setAdapter(searchPatientForAppAdapter);
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
