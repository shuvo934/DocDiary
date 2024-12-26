package ttit.com.shuvo.docdiary.payment.service;

import static ttit.com.shuvo.docdiary.payment.AddPayment.addedServiceLists;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.payment.arraylists.AddedServiceList;
import ttit.com.shuvo.docdiary.payment.arraylists.ServiceList;
import ttit.com.shuvo.docdiary.payment.arraylists.ServiceUnitList;

public class ServiceModify extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView close;
    TextView appbarName;

    TextInputLayout serviceLay;
    AppCompatAutoCompleteTextView service;
    TextView serviceMissing;
    ArrayList<ServiceList> serviceLists;

    String pfn_id = "";
    String pfn_name = "";

    TextInputLayout serviceUnitLay;
    AppCompatAutoCompleteTextView serviceUnit;
    TextView serviceUnitMissing;
    ArrayList<ServiceUnitList> serviceUnitLists;

    String depts_id = "";
    String depts_name = "";

    TextInputEditText serviceRate;
    TextView serviceRateMissing;
    String service_rate = "";
    String service_top_rate = "";

    TextInputEditText serviceQty;
    TextView serviceQtyMissing;
    String service_qty = "";
    TextInputEditText serviceAmount;
    String service_amount = "";

    Button serviceAdd;
    Button serviceUpdate;

    String pat_cat_id = "";
    String today_date = "";
    String s_type = "";
    int position = 0;

    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";
    boolean selectedFromItems = false;

    Logger logger = Logger.getLogger(ServiceModify.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ServiceModify.this,R.color.clouds));
        setContentView(R.layout.activity_service_modify);

        fullLayout = findViewById(R.id.service_modify_for_payment_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_service_modify_for_payment);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_service_modify_for_payment);
        appbarName = findViewById(R.id.service_modify_for_payment_app_bar_text);

        serviceLay = findViewById(R.id.spinner_layout_service_for_patient_payment);
        service = findViewById(R.id.service_for_patient_payment_spinner);
        serviceMissing = findViewById(R.id.service_for_patient_payment_missing_msg);
        serviceMissing.setVisibility(View.GONE);

        serviceUnitLay = findViewById(R.id.spinner_layout_unit_for_patient_payment);
        serviceUnitLay.setEnabled(false);
        serviceUnit = findViewById(R.id.unit_for_patient_payment_spinner);
        serviceUnitMissing = findViewById(R.id.unit_for_patient_payment_missing_msg);
        serviceUnitMissing.setVisibility(View.GONE);

        serviceRate = findViewById(R.id.service_rate_for_patient_payment);
        serviceRateMissing = findViewById(R.id.service_rate_for_patient_payment_missing_msg);
        serviceRateMissing.setVisibility(View.GONE);
        serviceQty = findViewById(R.id.service_quantity_for_patient_payment);
        serviceQtyMissing = findViewById(R.id.service_qty_for_patient_payment_missing_msg);
        serviceQtyMissing.setVisibility(View.GONE);

        serviceAmount = findViewById(R.id.service_amount_for_patient_payment);

        serviceAdd = findViewById(R.id.service_add_for_payment_button);
        serviceUpdate = findViewById(R.id.service_update_for_payment_button);

        serviceLists = new ArrayList<>();
        serviceUnitLists = new ArrayList<>();

        Intent intent = getIntent();
        s_type = intent.getStringExtra("TYPE");
        pat_cat_id = intent.getStringExtra("PAT_CAT_ID");

        assert s_type != null;
        if (s_type.equals("ADD")) {
            String an = "Add Service";
            appbarName.setText(an);
            serviceAdd.setVisibility(View.VISIBLE);
            serviceUpdate.setVisibility(View.GONE);
        }
        else {
            String an = "Update Service";
            appbarName.setText(an);
            serviceAdd.setVisibility(View.GONE);
            serviceUpdate.setVisibility(View.VISIBLE);

            pfn_id = intent.getStringExtra("PFN_ID");
            pfn_name = intent.getStringExtra("PFN_NAME");
            depts_name = intent.getStringExtra("DEPTS_NAME");
            depts_id = intent.getStringExtra("DEPTS_ID");
            service_rate = intent.getStringExtra("RATE");
            service_top_rate = intent.getStringExtra("TOP_RATE");
            service_qty = intent.getStringExtra("QUANTITY");
            service_amount = intent.getStringExtra("AMOUNT");
            position = intent.getIntExtra("POSITION",0);

            service.setText(pfn_name);
            serviceUnit.setText(depts_name);
            serviceRate.setText(service_rate);
            serviceQty.setText(service_qty);
            serviceAmount.setText(service_amount);
        }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        today_date = sdf.format(c);

        service.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    serviceMissing.setVisibility(View.GONE);
                }
            }
        });

        service.setOnItemClickListener((parent, view, position, id) -> {
            service_amount = "";
            serviceAmount.setText(service_amount);
            serviceRate.setText("");
            serviceRateMissing.setVisibility(View.GONE);
            service_top_rate = "";
            service_rate = "";
            depts_name = "";
            depts_id = "";
            serviceUnit.setText("");
            serviceUnitLay.setEnabled(false);
            pfn_id = "";
            pfn_name = "";

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <serviceLists.size(); i++) {
                if (name.equals(serviceLists.get(i).getPfn_fee_name())) {
                    pfn_id = serviceLists.get(i).getPfn_id();
                    pfn_name = serviceLists.get(i).getPfn_fee_name();
                }
            }

            selectedFromItems = true;
            serviceMissing.setVisibility(View.GONE);
            closeKeyBoard();
            getServiceUnit();
        });

        service.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = service.getText().toString();
                if (!selectedFromItems) {
                    service_amount = "";
                    serviceAmount.setText(service_amount);
                    serviceRate.setText("");
                    serviceRateMissing.setVisibility(View.GONE);
                    service_top_rate = "";
                    service_rate = "";
                    depts_name = "";
                    depts_id = "";
                    serviceUnit.setText("");
                    serviceUnitLay.setEnabled(false);
                    pfn_id = "";
                    pfn_name = "";
                    for (int i = 0; i < serviceLists.size(); i++) {
                        if (ss.equals(serviceLists.get(i).getPfn_fee_name())) {
                            pfn_id = serviceLists.get(i).getPfn_id();
                            pfn_name = serviceLists.get(i).getPfn_fee_name();

                        }
                    }
                    if (pfn_id.isEmpty()) {
                        if (ss.isEmpty()) {
                            serviceMissing.setVisibility(View.VISIBLE);
                            String smt = "Please Select Service";
                            serviceMissing.setText(smt);
                        }
                        else {
                            serviceMissing.setVisibility(View.VISIBLE);
                            String smt = "Invalid Service";
                            serviceMissing.setText(smt);
                        }
                    }
                    else{
                        getServiceUnit();
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        service.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    service.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        serviceUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    serviceUnitMissing.setVisibility(View.GONE);
                }
            }
        });

        serviceUnit.setOnItemClickListener((parent, view, position, id) -> {
            service_amount = "";
            serviceAmount.setText(service_amount);
            serviceRate.setText("");
            serviceRateMissing.setVisibility(View.GONE);
            service_top_rate = "";
            service_rate = "";
            depts_name = "";
            depts_id = "";

            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <serviceUnitLists.size(); i++) {
                if (name.equals(serviceUnitLists.get(i).getDepts_name())) {
                    depts_id = serviceUnitLists.get(i).getDepts_id();
                    depts_name = serviceUnitLists.get(i).getDepts_name();
                }
            }

            selectedFromItems = true;
            serviceUnitMissing.setVisibility(View.GONE);
            closeKeyBoard();
            getServiceRate();
        });

        serviceUnit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = serviceUnit.getText().toString();
                if (!selectedFromItems) {
                    service_amount = "";
                    serviceAmount.setText(service_amount);
                    serviceRate.setText("");
                    serviceRateMissing.setVisibility(View.GONE);
                    service_top_rate = "";
                    service_rate = "";
                    depts_name = "";
                    depts_id = "";

                    for (int i = 0; i < serviceUnitLists.size(); i++) {
                        if (ss.equals(serviceUnitLists.get(i).getDepts_name())) {
                            depts_id = serviceUnitLists.get(i).getDepts_id();
                            depts_name = serviceUnitLists.get(i).getDepts_name();

                        }
                    }
                    if (depts_id.isEmpty()) {
                        if (ss.isEmpty()) {
                            serviceUnitMissing.setVisibility(View.VISIBLE);
                            String sut = "Please Select Unit";
                            serviceUnitMissing.setText(sut);
                        }
                        else {
                            serviceUnitMissing.setVisibility(View.VISIBLE);
                            String sut = "Invalid Unit";
                            serviceUnitMissing.setText(sut);
                        }
                    }
                    else{
                        getServiceRate();
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        serviceUnit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    serviceUnit.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        serviceQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    serviceQtyMissing.setVisibility(View.GONE);
                    if (isNumeric(s.toString())) {
                        if (!s.toString().startsWith("0") && !s.toString().contains(".")) {
                            service_qty = s.toString();
                            if (!service_rate.isEmpty() && isNumeric(service_rate)) {
                                int qty = Integer.parseInt(service_qty);
                                int rate = Integer.parseInt(service_rate);
                                int amnt = qty * rate;
                                service_amount = String.valueOf(amnt);
                                serviceAmount.setText(service_amount);
                            }
                            else {
                                service_amount = "";
                                serviceAmount.setText(service_amount);
                            }
                        }
                        else {
                            String sqt = "Invalid Quantity";
                            serviceQtyMissing.setText(sqt);
                            serviceQtyMissing.setVisibility(View.VISIBLE);
                            service_amount = "";
                            serviceAmount.setText(service_amount);
                        }
                    }
                    else {
                        String sqt = "Invalid Quantity";
                        serviceQtyMissing.setText(sqt);
                        serviceQtyMissing.setVisibility(View.VISIBLE);
                        service_amount = "";
                        serviceAmount.setText(service_amount);
                    }
                }
                else {
                    String sqt = "Please Provide Quantity";
                    serviceQtyMissing.setText(sqt);
                    serviceQtyMissing.setVisibility(View.VISIBLE);
                    service_amount = "";
                    serviceAmount.setText(service_amount);
                }
            }
        });

        serviceQty.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    serviceQty.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        close.setOnClickListener(v -> finish());

        serviceAdd.setOnClickListener(v -> {
            service_qty = Objects.requireNonNull(serviceQty.getText()).toString();
            service_amount = Objects.requireNonNull(serviceAmount.getText()).toString();
            if (!pfn_id.isEmpty()) {
                if (!depts_id.isEmpty()) {
                    if (!service_rate.isEmpty()) {
//                        if (!service_rate.equals("0")) {
                            if (!service_qty.isEmpty()) {
                                if (!service_qty.startsWith("0")) {
                                    if (!service_amount.isEmpty()) {
                                        if (addedServiceLists.isEmpty()) {
                                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ServiceModify.this);
                                            alertDialogBuilder.setTitle("Add Service!")
                                                    .setMessage("Do you want to Add Service for patient?")
                                                    .setPositiveButton("Yes", (dialog, which) -> {
                                                        addedServiceLists.add(new AddedServiceList(pfn_id,pfn_name,depts_id,depts_name,service_rate,
                                                                service_top_rate,service_qty,service_amount,false));
                                                        dialog.dismiss();
                                                        finish();
                                                    })
                                                    .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                                            AlertDialog alert = alertDialogBuilder.create();
                                            alert.show();
                                        }
                                        else {
                                            boolean sFound = false;
                                            for (int i = 0; i < addedServiceLists.size(); i++) {
                                                String p_id = addedServiceLists.get(i).getPfn_id();
                                                String d_id = addedServiceLists.get(i).getDepts_id();
                                                if (pfn_id.equals(p_id) && depts_id.equals(d_id)) {
                                                    sFound = true;
                                                    break;
                                                }
                                            }

                                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ServiceModify.this);
                                            if (!sFound) {
                                                alertDialogBuilder.setTitle("Add Service!")
                                                        .setMessage("Do you want to Add Service for patient?")
                                                        .setPositiveButton("Yes", (dialog, which) -> {
                                                            addedServiceLists.add(new AddedServiceList(pfn_id,pfn_name,depts_id,depts_name,service_rate,
                                                                    service_top_rate,service_qty,service_amount,false));
                                                            dialog.dismiss();
                                                            finish();
                                                        })
                                                        .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                                                AlertDialog alert = alertDialogBuilder.create();
                                                alert.show();
                                            }
                                            else {
                                                alertDialogBuilder.setTitle("Duplicate Service Found!")
                                                        .setMessage("This Service is already added. Recheck again.")
                                                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                                                AlertDialog alert = alertDialogBuilder.create();
                                                alert.setCancelable(false);
                                                alert.setCanceledOnTouchOutside(false);
                                                alert.show();
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(this, "Please Provide Quantity Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    String sqt = "Invalid Quantity";
                                    serviceQtyMissing.setText(sqt);
                                    serviceQtyMissing.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                String sqt = "Please Provide Quantity";
                                serviceQtyMissing.setText(sqt);
                                serviceQtyMissing.setVisibility(View.VISIBLE);
                            }
//                        }
//                        else {
//                            serviceRateMissing.setText("Invalid Rate. Select Unit Again");
//                            serviceRateMissing.setVisibility(View.VISIBLE);
//                        }
                    }
                    else {
                        serviceRateMissing.setVisibility(View.VISIBLE);
                        String srt = "Invalid Rate. Select Unit Again";
                        serviceRateMissing.setText(srt);
                    }
                }
                else {
                    serviceUnitMissing.setVisibility(View.VISIBLE);
                    String sut = "Please Select Unit";
                    serviceUnitMissing.setText(sut);
                }
            }
            else {
                serviceMissing.setVisibility(View.VISIBLE);
                String st = "Please Select Service";
                serviceMissing.setText(st);
            }
        });

        serviceUpdate.setOnClickListener(v -> {
            service_qty = Objects.requireNonNull(serviceQty.getText()).toString();
            service_amount = Objects.requireNonNull(serviceAmount.getText()).toString();
            if (!pfn_id.isEmpty()) {
                if (!depts_id.isEmpty()) {
                    if (!service_rate.isEmpty()) {
//                        if (!service_rate.equals("0")) {
                            if (!service_qty.isEmpty()) {
                                if (!service_qty.startsWith("0")) {
                                    if (!service_amount.isEmpty()) {
                                        boolean sFound = false;
                                        for (int i = 0; i < addedServiceLists.size(); i++) {
                                            String p_id = addedServiceLists.get(i).getPfn_id();
                                            String d_id = addedServiceLists.get(i).getDepts_id();
                                            if (pfn_id.equals(p_id) && depts_id.equals(d_id)) {
                                                if (position != i) {
                                                    sFound = true;
                                                    break;
                                                }
                                            }
                                        }

                                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ServiceModify.this);
                                        if (!sFound) {
                                            alertDialogBuilder.setTitle("Update Service!")
                                                    .setMessage("Do you want to Update this Service for patient?")
                                                    .setPositiveButton("Yes", (dialog, which) -> {

                                                        addedServiceLists.get(position).setPfn_id(pfn_id);
                                                        addedServiceLists.get(position).setPfn_name(pfn_name);
                                                        addedServiceLists.get(position).setDepts_id(depts_id);
                                                        addedServiceLists.get(position).setDepts_name(depts_name);
                                                        addedServiceLists.get(position).setService_rate(service_rate);
                                                        addedServiceLists.get(position).setService_top_rate(service_top_rate);
                                                        addedServiceLists.get(position).setService_qty(service_qty);
                                                        addedServiceLists.get(position).setService_amnt(service_amount);

                                                        dialog.dismiss();
                                                        finish();
                                                    })
                                                    .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                                            AlertDialog alert = alertDialogBuilder.create();
                                            alert.show();
                                        }
                                        else {
                                            alertDialogBuilder.setTitle("Duplicate Service Found!")
                                                    .setMessage("This Service is already added. Recheck again.")
                                                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                                            AlertDialog alert = alertDialogBuilder.create();
                                            alert.setCancelable(false);
                                            alert.setCanceledOnTouchOutside(false);
                                            alert.show();
                                        }
                                    }
                                    else {
                                        Toast.makeText(this, "Please Provide Quantity Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    String sqt = "Invalid Quantity";
                                    serviceQtyMissing.setText(sqt);
                                    serviceQtyMissing.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                String sqt = "Please Provide Quantity";
                                serviceQtyMissing.setText(sqt);
                                serviceQtyMissing.setVisibility(View.VISIBLE);
                            }
//                        }
//                        else {
//                            serviceRateMissing.setText("Invalid Rate. Select Unit Again");
//                            serviceRateMissing.setVisibility(View.VISIBLE);
//                        }
                    }
                    else {
                        serviceRateMissing.setVisibility(View.VISIBLE);
                        String srt = "Invalid Rate. Select Unit Again";
                        serviceRateMissing.setText(srt);
                    }
                }
                else {
                    serviceUnitMissing.setVisibility(View.VISIBLE);
                    String sut = "Please Select Unit";
                    serviceUnitMissing.setText(sut);
                }
            }
            else {
                serviceMissing.setVisibility(View.VISIBLE);
                String st = "Please Select Service";
                serviceMissing.setText(st);
            }
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


        if (s_type.equals("ADD")) {
            getServices();
        }
        else {
            getServiceAndUnit();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

//    @Override
//    public void onBackPressed() {
//
//    }

    private void closeKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void getServices() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        serviceLists = new ArrayList<>();

        String serviceUrl = pre_url_api+"payement_receive/getServices";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest serviceReq = new StringRequest(Request.Method.GET, serviceUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");

                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pfn_fee_name = info.getString("pfn_fee_name")
                                .equals("null") ? "" : info.getString("pfn_fee_name");
                        String pfn_id = info.getString("pfn_id")
                                .equals("null") ? "" : info.getString("pfn_id");
                        String pct_flag = info.getString("pct_flag")
                                .equals("null") ? "" : info.getString("pct_flag");

                        serviceLists.add(new ServiceList(pfn_fee_name,pfn_id,pct_flag));
                    }
                }

                connected = true;
                updateInterface();
            }
            catch (Exception e) {
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

        requestQueue.add(serviceReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (serviceLists.isEmpty()) {
                    String st = "No Service List Found";
                    serviceMissing.setText(st);
                    serviceMissing.setVisibility(View.VISIBLE);
                }
                else {
                    serviceMissing.setVisibility(View.GONE);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < serviceLists.size(); i++) {
                    type.add(serviceLists.get(i).getPfn_fee_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ServiceModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                service.setAdapter(arrayAdapter);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ServiceModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getServices();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getServiceAndUnit() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        serviceLists = new ArrayList<>();
        serviceUnitLists = new ArrayList<>();

        String serviceUrl = pre_url_api+"payement_receive/getServices";
        String serviceUnitUrl = pre_url_api+"payement_receive/getServiceUnit?p_pfn_id="+pfn_id;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest serviceUnitReq = new StringRequest(Request.Method.GET, serviceUnitUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");

                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String depts_name = info.getString("depts_name")
                                .equals("null") ? "" : info.getString("depts_name");
                        String depts_id = info.getString("depts_id")
                                .equals("null") ? "" : info.getString("depts_id");

                        serviceUnitLists.add(new ServiceUnitList(depts_name,depts_id));
                    }
                }

                connected = true;
                updateUI();
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateUI();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateUI();
        });

        StringRequest serviceReq = new StringRequest(Request.Method.GET, serviceUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");

                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pfn_fee_name = info.getString("pfn_fee_name")
                                .equals("null") ? "" : info.getString("pfn_fee_name");
                        String pfn_id = info.getString("pfn_id")
                                .equals("null") ? "" : info.getString("pfn_id");
                        String pct_flag = info.getString("pct_flag")
                                .equals("null") ? "" : info.getString("pct_flag");

                        serviceLists.add(new ServiceList(pfn_fee_name,pfn_id,pct_flag));
                    }
                }

                requestQueue.add(serviceUnitReq);
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateUI();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateUI();
        });

        requestQueue.add(serviceReq);
    }

    public void updateUI() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (serviceLists.isEmpty()) {
                    String st = "No Service List Found";
                    serviceMissing.setText(st);
                    serviceMissing.setVisibility(View.VISIBLE);
                }
                else {
                    serviceMissing.setVisibility(View.GONE);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < serviceLists.size(); i++) {
                    type.add(serviceLists.get(i).getPfn_fee_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ServiceModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                service.setAdapter(arrayAdapter);

                serviceUnitLay.setEnabled(true);

                if (serviceUnitLists.isEmpty()) {
                    String sut = "No Unit List Found";
                    serviceUnitMissing.setText(sut);
                    serviceUnitMissing.setVisibility(View.VISIBLE);
                }
                else {
                    serviceUnitMissing.setVisibility(View.GONE);
                }

                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < serviceUnitLists.size(); i++) {
                    type1.add(serviceUnitLists.get(i).getDepts_name());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(ServiceModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                serviceUnit.setAdapter(arrayAdapter1);

            }
            else {
                alertMessage3();
            }
        }
        else {
            alertMessage3();
        }
    }

    public void alertMessage3() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ServiceModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getServiceAndUnit();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getServiceUnit() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        serviceUnitLists = new ArrayList<>();

        String serviceUnitUrl = pre_url_api+"payement_receive/getServiceUnit?p_pfn_id="+pfn_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest serviceUnitReq = new StringRequest(Request.Method.GET, serviceUnitUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");

                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String depts_name = info.getString("depts_name")
                                .equals("null") ? "" : info.getString("depts_name");
                        String depts_id = info.getString("depts_id")
                                .equals("null") ? "" : info.getString("depts_id");

                        serviceUnitLists.add(new ServiceUnitList(depts_name,depts_id));
                    }
                }

                connected = true;
                updateLayout();
            }
            catch (Exception e) {
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

        requestQueue.add(serviceUnitReq);
    }

    private void updateLayout() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                serviceUnitLay.setEnabled(true);

                if (serviceUnitLists.isEmpty()) {
                    String sut = "No Unit List Found";
                    serviceUnitMissing.setText(sut);
                    serviceUnitMissing.setVisibility(View.VISIBLE);
                }
                else {
                    serviceUnitMissing.setVisibility(View.GONE);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < serviceUnitLists.size(); i++) {
                    type.add(serviceUnitLists.get(i).getDepts_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ServiceModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                serviceUnit.setAdapter(arrayAdapter);

            }
            else {
                alertMessage1();
            }
        }
        else {
            alertMessage1();
        }
    }

    public void alertMessage1() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ServiceModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getServiceUnit();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getServiceRate() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        service_rate = "";
        service_top_rate = "";

        String serviceRateUrl = pre_url_api+"payement_receive/getServiceRate?P_PFN_ID="+pfn_id+"&P_DEPTS_ID="+depts_id+"&P_PRM_DATE="+today_date+"&P_PAT_CAT_ID="+pat_cat_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest serviceRateReq = new StringRequest(Request.Method.GET, serviceRateUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                service_rate = jsonObject.getString("p_service_rate")
                        .equals("null") ? "0" : jsonObject.getString("p_service_rate");
                service_top_rate = jsonObject.getString("p_service_top_rate")
                        .equals("null") ? "0" : jsonObject.getString("p_service_top_rate");

                connected = true;
                updateRateValue();
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateRateValue();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateRateValue();
        });

        requestQueue.add(serviceRateReq);
    }

    private void updateRateValue() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                serviceRate.setText(service_rate);
//                if (service_rate.equals("0")) {
//                    serviceRateMissing.setText("Invalid Rate. Select Unit Again");
//                    serviceRateMissing.setVisibility(View.VISIBLE);
//                }
//                else {
//                    serviceRateMissing.setVisibility(View.GONE);
//                }
                serviceRateMissing.setVisibility(View.GONE);

                if (!service_qty.isEmpty() && isNumeric(service_qty)) {
                    int qty = Integer.parseInt(service_qty);
                    int rate = Integer.parseInt(service_rate);
                    int amnt = qty * rate;
                    service_amount = String.valueOf(amnt);
                    serviceAmount.setText(service_amount);
                }
            }
            else {
                alertMessage2();
            }
        }
        else {
            alertMessage2();
        }
    }

    public void alertMessage2() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ServiceModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getServiceRate();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
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
}