package ttit.com.shuvo.docdiary.payment;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.adminInfoLists;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.payment.adapters.AddedServiceAdapter;
import ttit.com.shuvo.docdiary.payment.adapters.UpdatedServiceAdapter;
import ttit.com.shuvo.docdiary.payment.arraylists.AddedServiceList;
import ttit.com.shuvo.docdiary.payment.arraylists.UpdatedServiceList;
import ttit.com.shuvo.docdiary.payment.dialog.SearchPaymentDialog;
import ttit.com.shuvo.docdiary.payment.dialog.SearchPrescriptionDialog;
import ttit.com.shuvo.docdiary.payment.service.ServiceModify;
import ttit.com.shuvo.docdiary.payment.service.ServiceModifyForUp;

public class AddPayment extends AppCompatActivity implements PatCodeCallBackListener, PaymentCodeCallListener {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    TabLayout paymentTab;

    TextView selectYear;
    String selected_year = "";
    String selected_year_short = "";
    AlertDialog yearDialog;

    CardView paymentCodeCard;
    TextInputLayout searchPaymentLay;
    TextInputEditText searchPayment;

    CardView searchPrescCard;
    TextInputLayout searchPrescLay;
    TextInputEditText searchPresc;

    LinearLayout afterSelectingPrescription;

    TextInputLayout patPrescCodeLay;
    TextInputEditText patPrescCode;
    TextInputEditText patName;
    TextInputEditText patCode;
    TextInputEditText paymentDate;
    TextInputEditText patAge;
    TextInputEditText patMaritalStatus;
    TextInputEditText patGender;
    TextInputEditText patBlood;
    TextInputEditText patCategory;
    TextInputEditText patStatus;
    TextInputEditText patAddress;


    public static String selected_ph_id = "";
    public static String selected_pat_name = "";
    public static String selected_ph_sub_code = "";
    public static String selected_pat_code = "";
    public static String selected_pat_age = "";
    public static String selected_pat_marital = "";
    public static String selected_pat_gender = "";
    public static String selected_pat_blood = "";
    public static String selected_pat_category = "";
    public static String selected_pat_cat_id = "";
    public static String selected_pat_status = "";
    public static String selected_pat_address = "";
    public static String selected_payment_code = "";
    public static String selected_prm_id = "";
    public static String selected_payment_date = "";
    String payment_date = "";

    LinearLayout buttonLay;
    Button addPayment;
    Button updatePayment;

    CardView addPayServiceCard;
    CardView upPayServiceCard;

    RecyclerView serviceView;
    RecyclerView.LayoutManager layoutManager;
    AddedServiceAdapter addedServiceAdapter;
    public static LinearLayout totalAmountLay;
    public static TextView totalAmount;
    public static String total_amount = "";
    MaterialButton addService;

    RecyclerView upServiceView;
    RecyclerView.LayoutManager upLayoutManager;
    UpdatedServiceAdapter updatedServiceAdapter;
    public static LinearLayout totalAmountUpLay;
    public static TextView totalAmountUp;
    public static String total_amount_up = "";
    MaterialButton addUpService;

    public static ArrayList<AddedServiceList> addedServiceLists;
    public static ArrayList<UpdatedServiceList> updatedServiceLists;

    private Boolean conn = false;
    private Boolean connected = false;

    private Boolean masterConn = false;
    private Boolean masterConnected = false;
    boolean loading = false;
    String parsing_message = "";
    String usr_name = "";
    String out_prm_id = "";
    String out_prm_code = "";
    int previousTabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
        fullLayout = findViewById(R.id.add_payment_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_add_payment);
        circularProgressIndicator.setVisibility(View.GONE);

        paymentTab = findViewById(R.id.payment_tab_layout);

        backButton = findViewById(R.id.back_logo_of_add_payment);

        paymentCodeCard = findViewById(R.id.payment_code_selectable_card_view);
        paymentCodeCard.setVisibility(View.GONE);
        searchPaymentLay = findViewById(R.id.spinner_layout_search_payment_code_for_update_payment);
        searchPayment = findViewById(R.id.search_payment_code_for_update_payment_spinner);

        selectYear = findViewById(R.id.select_year_for_add_payment);

        searchPrescCard = findViewById(R.id.search_patient_selectable_card_view);
        searchPrescLay = findViewById(R.id.spinner_layout_search_patient_for_payment);
        searchPresc = findViewById(R.id.search_patient_for_payment_spinner);

        afterSelectingPrescription = findViewById(R.id.after_selecting_prescription_code_lay);
        afterSelectingPrescription.setVisibility(View.GONE);

        patPrescCodeLay = findViewById(R.id.patient_prescription_code_for_update_payment_lay);
        patPrescCode = findViewById(R.id.patient_prescription_code_for_update_payment);
        patPrescCodeLay.setVisibility(View.GONE);
        patName = findViewById(R.id.patient_name_in_add_payment);
        patCode = findViewById(R.id.patient_code_in_add_payment);
        paymentDate = findViewById(R.id.payment_date_in_add_payment);
        patAge = findViewById(R.id.patient_age_in_add_payment);
        patMaritalStatus = findViewById(R.id.patient_marital_status_in_add_payment);
        patGender = findViewById(R.id.patient_gender_in_add_payment);
        patBlood = findViewById(R.id.patient_blood_group_in_add_payment);
        patCategory = findViewById(R.id.patient_category_in_add_payment);
        patStatus = findViewById(R.id.patient_status_in_add_payment);
        patAddress = findViewById(R.id.patient_address_in_add_payment);

        buttonLay = findViewById(R.id.add_update_payment_button_layout);
        buttonLay.setVisibility(View.GONE);
        addPayment = findViewById(R.id.add_payment_button);
        addPayment.setVisibility(View.VISIBLE);
        updatePayment = findViewById(R.id.update_payment_button);
        updatePayment.setVisibility(View.GONE);

        addPayServiceCard = findViewById(R.id.add_payment_service_list_card_view);
        upPayServiceCard = findViewById(R.id.update_payment_service_list_card_view);
        upPayServiceCard.setVisibility(View.GONE);

        serviceView = findViewById(R.id.service_unit_recyclerview_for_payment);

        totalAmountLay = findViewById(R.id.total_amount_layout_in_add_payment);
        totalAmountLay.setVisibility(View.GONE);
        totalAmount = findViewById(R.id.total_amount_in_add_payment);

        addService = findViewById(R.id.add_service_for_payment);

        upServiceView = findViewById(R.id.service_unit_recyclerview_for_update_payment);

        totalAmountUpLay = findViewById(R.id.total_amount_layout_in_update_payment);
        totalAmountLay.setVisibility(View.GONE);
        totalAmountUp = findViewById(R.id.total_amount_in_update_payment);

        addUpService = findViewById(R.id.add_service_for_update_payment);

        addedServiceLists = new ArrayList<>();
        updatedServiceLists = new ArrayList<>();

        if (adminInfoLists == null) {
            restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (adminInfoLists.size() == 0) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            }
            else {
                if (Integer.parseInt(adminInfoLists.get(0).getAll_access_flag()) > 0) {
                    usr_name = "";
                    int ct = paymentTab.getTabCount();
                    if (ct == 2) {
                        Objects.requireNonNull(paymentTab.getTabAt(1)).view.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    usr_name = adminInfoLists.get(0).getUsr_name();
                    int ct = paymentTab.getTabCount();
                    if (ct == 2) {
                        Objects.requireNonNull(paymentTab.getTabAt(1)).view.setVisibility(View.GONE);
                    }
                }
            }
        }

        serviceView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(AddPayment.this);
        serviceView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecorationRS = new DividerItemDecoration(serviceView.getContext(),DividerItemDecoration.VERTICAL);
        serviceView.addItemDecoration(dividerItemDecorationRS);

        addedServiceAdapter = new AddedServiceAdapter(addedServiceLists,AddPayment.this);
        serviceView.setAdapter(addedServiceAdapter);

        upServiceView.setHasFixedSize(true);
        upLayoutManager = new LinearLayoutManager(AddPayment.this);
        upServiceView.setLayoutManager(upLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(upServiceView.getContext(),DividerItemDecoration.VERTICAL);
        upServiceView.addItemDecoration(dividerItemDecoration);

        Calendar today = Calendar.getInstance();
        today.get(Calendar.YEAR);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yy", Locale.ENGLISH);

        selected_year = df.format(c);
        selected_year_short = sdf.format(c);

        int year = Integer.parseInt(selected_year);
        String yy = "Year: "+ selected_year;
        selectYear.setText(yy);

        previousTabPosition = paymentTab.getSelectedTabPosition();
        paymentTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println(previousTabPosition);
                System.out.println(tab.getPosition());
                if (previousTabPosition != tab.getPosition()) {
                    if (tab.getPosition() == 1) {
                        if (addedServiceLists.size() != 0) {
                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
                            alertDialogBuilder
                                    .setMessage("You have Data Stored in this section. Do you want to switch to Update payment section?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        previousTabPosition = tab.getPosition();

                                        paymentCodeCard.setVisibility(View.VISIBLE);
                                        searchPrescCard.setVisibility(View.GONE);
                                        afterSelectingPrescription.setVisibility(View.GONE);
                                        patPrescCodeLay.setVisibility(View.VISIBLE);
                                        buttonLay.setVisibility(View.GONE);
                                        addPayment.setVisibility(View.GONE);
                                        updatePayment.setVisibility(View.VISIBLE);
                                        addPayServiceCard.setVisibility(View.GONE);
                                        upPayServiceCard.setVisibility(View.VISIBLE);
                                        totalAmountLay.setVisibility(View.GONE);
                                        totalAmountUpLay.setVisibility(View.GONE);

                                        searchPaymentLay.setHint("Select Payment Code");
                                        searchPayment.setText("");

                                        resetAll();

                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("No",((dialog, which) -> {
                                        paymentTab.selectTab(paymentTab.getTabAt(previousTabPosition));
                                        dialog.dismiss();
                                    }));

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.setCancelable(false);
                            alert.setCanceledOnTouchOutside(false);
                            alert.show();
                        }
                        else {
                            previousTabPosition = tab.getPosition();

                            paymentCodeCard.setVisibility(View.VISIBLE);
                            searchPrescCard.setVisibility(View.GONE);
                            afterSelectingPrescription.setVisibility(View.GONE);
                            patPrescCodeLay.setVisibility(View.VISIBLE);
                            buttonLay.setVisibility(View.GONE);
                            addPayment.setVisibility(View.GONE);
                            updatePayment.setVisibility(View.VISIBLE);
                            addPayServiceCard.setVisibility(View.GONE);
                            upPayServiceCard.setVisibility(View.VISIBLE);
                            totalAmountLay.setVisibility(View.GONE);
                            totalAmountUpLay.setVisibility(View.GONE);

                            searchPaymentLay.setHint("Select Payment Code");
                            searchPayment.setText("");

                            resetAll();
                        }
                    }
                    else {
                        if (updatedServiceLists.size() != 0) {
                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
                            alertDialogBuilder
                                    .setMessage("You have Data Stored in this section. Do you want to switch to Add payment section?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        previousTabPosition = tab.getPosition();

                                        paymentCodeCard.setVisibility(View.GONE);
                                        searchPrescCard.setVisibility(View.VISIBLE);
                                        afterSelectingPrescription.setVisibility(View.GONE);
                                        patPrescCodeLay.setVisibility(View.GONE);
                                        buttonLay.setVisibility(View.GONE);
                                        addPayment.setVisibility(View.VISIBLE);
                                        updatePayment.setVisibility(View.GONE);
                                        addPayServiceCard.setVisibility(View.VISIBLE);
                                        upPayServiceCard.setVisibility(View.GONE);
                                        totalAmountLay.setVisibility(View.GONE);
                                        totalAmountUpLay.setVisibility(View.GONE);

                                        searchPrescLay.setHint("Select Prescription Code");
                                        searchPresc.setText("");

                                        resetAll();

                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("No",((dialog, which) -> {
                                        paymentTab.selectTab(paymentTab.getTabAt(previousTabPosition));
                                        dialog.dismiss();
                                    }));

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.setCancelable(false);
                            alert.setCanceledOnTouchOutside(false);
                            alert.show();
                        }
                        else {
                            previousTabPosition = tab.getPosition();

                            paymentCodeCard.setVisibility(View.GONE);
                            searchPrescCard.setVisibility(View.VISIBLE);
                            afterSelectingPrescription.setVisibility(View.GONE);
                            patPrescCodeLay.setVisibility(View.GONE);
                            buttonLay.setVisibility(View.GONE);
                            addPayment.setVisibility(View.VISIBLE);
                            updatePayment.setVisibility(View.GONE);
                            addPayServiceCard.setVisibility(View.VISIBLE);
                            upPayServiceCard.setVisibility(View.GONE);
                            totalAmountLay.setVisibility(View.GONE);
                            totalAmountUpLay.setVisibility(View.GONE);

                            searchPrescLay.setHint("Select Prescription Code");
                            searchPresc.setText("");

                            resetAll();
                        }
                    }

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(AddPayment.this, (selectedMonth, selectedYear) -> {
            System.out.println("Selected Year: "+selectedYear);
            String yy1 = "Year: "+selectedYear;
            selectYear.setText(yy1);

            selected_year = String.valueOf(selectedYear);
            selected_year_short = String.valueOf(selectedYear).substring(String.valueOf(selectedYear).length()-2);
            searchPrescLay.setHint("Select Prescription Code");
            searchPresc.setText("");
            searchPaymentLay.setHint("Select Payment Code");
            searchPayment.setText("");

            afterSelectingPrescription.setVisibility(View.GONE);
            buttonLay.setVisibility(View.GONE);
            totalAmountLay.setVisibility(View.GONE);
            totalAmountUpLay.setVisibility(View.GONE);

            resetAll();

        },today.get(Calendar.YEAR),today.get(Calendar.MONTH));

        builder.setActivatedYear(Integer.parseInt(selected_year))
                .setMinYear(1950)
                .setMaxYear(year)
                .showYearOnly()
                .setTitle("Selected Year")
                .setOnYearChangedListener(year1 -> {
                });

        yearDialog = builder.build();

        selectYear.setOnClickListener(v -> yearDialog.show());

        backButton.setOnClickListener(v -> onBackPressed());

        searchPresc.setOnClickListener(v -> {
            SearchPrescriptionDialog searchPrescriptionDialog = new SearchPrescriptionDialog(AddPayment.this,selected_year_short);
            searchPrescriptionDialog.show(getSupportFragmentManager(),"SEARCH_CODE");
        });

        searchPayment.setOnClickListener(v -> {
            SearchPaymentDialog searchPaymentDialog = new SearchPaymentDialog(AddPayment.this, selected_year_short, usr_name);
            searchPaymentDialog.show(getSupportFragmentManager(),"SEARCH_PAYMENT");
        });

        addService.setOnClickListener(v -> {
            Intent intent = new Intent(AddPayment.this, ServiceModify.class);
            intent.putExtra("TYPE","ADD");
            intent.putExtra("PAT_CAT_ID",selected_pat_cat_id);
            startActivity(intent);
        });

        addUpService.setOnClickListener(v -> {
            boolean found = false;
            for (int i = 0; i < updatedServiceLists.size(); i++) {
                if (!updatedServiceLists.get(i).getPrd_sched_avail_mark().isEmpty()) {
                    int sch = Integer.parseInt(updatedServiceLists.get(i).getPrd_sched_avail_mark());
                    if (sch > 0) {
                        found = true;
                    }
                }
            }
            if (found) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
                alertDialogBuilder.setTitle("Warning!")
                        .setMessage("Appointment Schedule already taken from this payment. You will not be able to update this payment.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                        });

                AlertDialog alert = alertDialogBuilder.create();
                alert.setCancelable(false);
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
            else {
                Intent intent = new Intent(AddPayment.this, ServiceModifyForUp.class);
                intent.putExtra("TYPE","ADD");
                intent.putExtra("PAT_CAT_ID",selected_pat_cat_id);
                startActivity(intent);
            }
        });

        addPayment.setOnClickListener(v -> {
            if (addedServiceLists.size() != 0) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
                alertDialogBuilder.setTitle("Add Payment!")
                        .setMessage("Do you want to Add Payment from this patient?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            insertPaymentRcv();
                            dialog.dismiss();
                        })
                        .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
            else {
                Toast.makeText(this, "Please Add Service.", Toast.LENGTH_SHORT).show();
            }
        });

        updatePayment.setOnClickListener(v -> {
            if (updatedServiceLists.size() != 0) {
                boolean updated = true;
                for (int i = 0; i < updatedServiceLists.size(); i++) {
                    updated = updatedServiceLists.get(i).isUpdated();
                    if (!updated) {
                        break;
                    }
                }
                if (!updated) {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
                    alertDialogBuilder.setTitle("Update Payment!")
                            .setMessage("Do you want to Update Payment for this patient?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                updatePaymentDetails();
                                dialog.dismiss();
                            })
                            .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
                else {
                    Toast.makeText(this, "No Service Changed for Update.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Please Add Service.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAmount();
    }

    public void resetAll() {
        addedServiceLists = new ArrayList<>();
        updatedServiceLists = new ArrayList<>();

        selected_payment_code = "";
        selected_prm_id = "";
        selected_payment_date = "";
        selected_ph_id = "";
        selected_pat_name = "";
        selected_ph_sub_code = "";
        selected_pat_code = "";
        selected_pat_age = "";
        selected_pat_marital = "";
        selected_pat_gender = "";
        selected_pat_blood = "";
        selected_pat_category = "";
        selected_pat_cat_id = "";
        selected_pat_status = "";
        selected_pat_address = "";
        payment_date = "";
        total_amount = "";
        total_amount_up = "";

        patPrescCode.setText(selected_ph_sub_code);
        patName.setText(selected_pat_name);
        patCode.setText(selected_pat_code);
        paymentDate.setText(payment_date);
        patAge.setText(selected_pat_age);
        patMaritalStatus.setText(selected_pat_marital);
        patGender.setText(selected_pat_gender);
        patBlood.setText(selected_pat_blood);
        patCategory.setText(selected_pat_category);
        patStatus.setText(selected_pat_status);
        patAddress.setText(selected_pat_address);
        totalAmount.setText(total_amount);
        totalAmountUp.setText(total_amount_up);

        addedServiceAdapter = new AddedServiceAdapter(addedServiceLists,AddPayment.this);
        serviceView.setAdapter(addedServiceAdapter);

        updatedServiceAdapter = new UpdatedServiceAdapter(updatedServiceLists, AddPayment.this);
        upServiceView.setAdapter(updatedServiceAdapter);
    }

    public void checkAmount() {
        if (addedServiceLists.size() == 0) {
            totalAmountLay.setVisibility(View.GONE);
            total_amount = "";
            totalAmount.setText(total_amount);
        }
        else {
            totalAmountLay.setVisibility(View.VISIBLE);
            int ttt = 0;
            for (int i = 0; i < addedServiceLists.size(); i++) {
                int amnt = Integer.parseInt(addedServiceLists.get(i).getService_amnt());
                ttt = ttt + amnt;
            }
            total_amount = String.valueOf(ttt);
            totalAmount.setText(total_amount);
        }
        System.out.println("Service List: " + addedServiceLists.size());
        addedServiceAdapter = new AddedServiceAdapter(addedServiceLists,AddPayment.this);
        serviceView.setAdapter(addedServiceAdapter);

        if (updatedServiceLists.size() == 0) {
            totalAmountUpLay.setVisibility(View.GONE);
            total_amount_up = "";
            totalAmountUp.setText(total_amount_up);
        }
        else {
            totalAmountUpLay.setVisibility(View.VISIBLE);
            int ttt = 0;
            for (int i = 0; i < updatedServiceLists.size(); i++) {
                int amnt = Integer.parseInt(updatedServiceLists.get(i).getAmount());
                ttt = ttt + amnt;
            }
            total_amount_up = String.valueOf(ttt);
            totalAmountUp.setText(total_amount_up);
        }

        updatedServiceAdapter = new UpdatedServiceAdapter(updatedServiceLists, AddPayment.this);
        upServiceView.setAdapter(updatedServiceAdapter);
    }

    @Override
    public void onPatSelection() {
        searchPresc.setText(selected_ph_sub_code);
        searchPrescLay.setHint("Prescription Code");
        afterSelectingPrescription.setVisibility(View.VISIBLE);
        buttonLay.setVisibility(View.VISIBLE);

        patName.setText(selected_pat_name);
        patCode.setText(selected_pat_code);
        patAge.setText(selected_pat_age);
        patMaritalStatus.setText(selected_pat_marital);
        patGender.setText(selected_pat_gender);
        patBlood.setText(selected_pat_blood);
        patCategory.setText(selected_pat_category);
        patStatus.setText(selected_pat_status);
        patAddress.setText(selected_pat_address);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        payment_date = sdf.format(c);

        paymentDate.setText(payment_date);
        addedServiceLists = new ArrayList<>();
        total_amount = "";
        totalAmountLay.setVisibility(View.GONE);
        totalAmount.setText(total_amount);
        addedServiceAdapter = new AddedServiceAdapter(addedServiceLists,AddPayment.this);
        serviceView.setAdapter(addedServiceAdapter);
    }

    @Override
    public void onPaymentSelection() {
        searchPayment.setText(selected_payment_code);
        searchPaymentLay.setHint("Payment Code");
        afterSelectingPrescription.setVisibility(View.VISIBLE);
        buttonLay.setVisibility(View.VISIBLE);

        patPrescCode.setText(selected_ph_sub_code);
        patName.setText(selected_pat_name);
        patCode.setText(selected_pat_code);
        patAge.setText(selected_pat_age);
        patMaritalStatus.setText(selected_pat_marital);
        patGender.setText(selected_pat_gender);
        patBlood.setText(selected_pat_blood);
        patCategory.setText(selected_pat_category);
        patStatus.setText(selected_pat_status);
        patAddress.setText(selected_pat_address);

        paymentDate.setText(selected_payment_date);

        getPaymentDetails();
    }

    public void insertPaymentRcv() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        masterConn = false;
        masterConnected = false;
        parsing_message = "";

        String rcvMasterUrl = pre_url_api+"payement_receive/insertPaymentMaster";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, rcvMasterUrl, response -> {
            masterConn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);

                out_prm_id = jsonObject.getString("out_prm_id")
                        .equals("Not Found") ? "" : jsonObject.getString("out_prm_id");
                out_prm_code = jsonObject.getString("out_prm_code")
                        .equals("Not Found") ? "" : jsonObject.getString("out_prm_code");

                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;

                if (string_out.equals("Successfully Created")) {
                    if (!out_prm_id.isEmpty()) {
                        masterConnected = true;
                        checkToInsertPayRcvDetails();
                    }
                    else {
                        masterConnected = false;
                        updateAfterInsert();
                    }
                }
                else {
                    masterConnected = false;
                    updateAfterInsert();
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                masterConnected = false;
                updateAfterInsert();
            }
        }, error -> {
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            masterConn = false;
            masterConnected = false;
            updateAfterInsert();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PRM_DATE",payment_date);
                headers.put("P_PRM_ONLINE_OFFLINE_FLAG","0");
                headers.put("P_PRM_PATIENT_CAT_ID",selected_pat_cat_id);
                headers.put("P_PRM_PH_ID",selected_ph_id);
                headers.put("P_PRM_USER", adminInfoLists.get(0).getUsr_name());
                headers.put("P_INSERT_TYPE_FLAG","3");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    public void checkToInsertPayRcvDetails() {
        boolean allUpdated = false;
        for (int x = 0; x < addedServiceLists.size(); x++) {
            allUpdated = addedServiceLists.get(x).isInserted();
            if (!addedServiceLists.get(x).isInserted()) {
                insertDetailsData(x);
                break;
            }
        }

        if (allUpdated) {
            conn = true;
            connected = true;
            updateAfterInsert();
        }
    }

    public void insertDetailsData(int index) {
        conn = false;
        connected = false;
        String insertPayDetailsUrl = pre_url_api +"payement_receive/insertPaymentDetails";
        RequestQueue requestQueue = Volley.newRequestQueue(AddPayment.this);

        StringRequest insertSODReq = new StringRequest(Request.Method.POST, insertPayDetailsUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;

                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    addedServiceLists.get(index).setInserted(true);
                    checkToInsertPayRcvDetails();
                }
                else {
                    connected = false;
                    updateAfterInsert();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateAfterInsert();
            }
        }, error -> {
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateAfterInsert();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_DEPTS_ID",addedServiceLists.get(index).getDepts_id());
                headers.put("P_PFN_ID",addedServiceLists.get(index).getPfn_id());
                headers.put("P_PRD_PATIENT_CAT_ID",selected_pat_cat_id);
                headers.put("P_PRD_QTY",addedServiceLists.get(index).getService_qty());
                headers.put("P_PRD_RATE", addedServiceLists.get(index).getService_rate());
                headers.put("P_PRD_TOP_CAT_RATE", addedServiceLists.get(index).getService_top_rate());
                headers.put("P_PRD_USR", adminInfoLists.get(0).getUsr_name());
                headers.put("P_PRM_CODE", out_prm_code);
                headers.put("P_PRM_ID", out_prm_id);
                headers.put("P_INSERT_TYPE_FLAG","3");
                return headers;
            }
        };

        insertSODReq.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(insertSODReq);
    }

    private void updateAfterInsert() {
        loading = false;
        if (masterConn) {
            if (masterConnected) {
                if (conn) {
                    if (connected) {
                        circularProgressIndicator.setVisibility(View.GONE);
                        fullLayout.setVisibility(View.VISIBLE);
                        masterConn = false;
                        masterConnected = false;
                        conn = false;
                        connected = false;

                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
                        alertDialogBuilder.setTitle("Success!")
                                .setMessage("Payment Received Successfully.\n" +
                                        "Payment Code: "+ out_prm_code)
                                .setPositiveButton("Ok", (dialog, which) -> {
                                    dialog.dismiss();
                                    finish();
                                });

                        AlertDialog alert = alertDialogBuilder.create();
                        alert.setCancelable(false);
                        alert.setCanceledOnTouchOutside(false);
                        alert.show();
                    }
                    else {
                        alertMessage1();
                    }
                }
                else {
                    alertMessage1();
                }
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    insertPaymentRcv();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void alertMessage1() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected. Please retry or data will be lost.";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected. Please retry or data will be lost.";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please retry or data will be lost..")
                .setPositiveButton("Retry", (dialog, which) -> {
                    circularProgressIndicator.setVisibility(View.VISIBLE);
                    fullLayout.setVisibility(View.GONE);
                    conn = false;
                    connected = false;
                    loading = true;
                    parsing_message = "";
                    checkToInsertPayRcvDetails();
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getPaymentDetails() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        parsing_message = "";

        updatedServiceLists = new ArrayList<>();

        String paymentDetailUrl = pre_url_api+"payement_receive/getPaymentDetailsList?prm_id="+selected_prm_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest paymentDetailsReq = new StringRequest(Request.Method.GET, paymentDetailUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");

                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String prd_id = info.getString("prd_id")
                                .equals("null") ? "" : info.getString("prd_id");
                        String prd_pfn_id = info.getString("prd_pfn_id")
                                .equals("null") ? "" : info.getString("prd_pfn_id");
                        String pfn_fee_name = info.getString("pfn_fee_name")
                                .equals("null") ? "" : info.getString("pfn_fee_name");
                        String prd_depts_id = info.getString("prd_depts_id")
                                .equals("null") ? "" : info.getString("prd_depts_id");
                        String depts_name = info.getString("depts_name")
                                .equals("null") ? "" : info.getString("depts_name");
                        String prd_rate = info.getString("prd_rate")
                                .equals("null") ? "" : info.getString("prd_rate");
                        String prd_top_cat_rate = info.getString("prd_top_cat_rate")
                                .equals("null") ? "" : info.getString("prd_top_cat_rate");
                        String prd_qty = info.getString("prd_qty")
                                .equals("null") ? "" : info.getString("prd_qty");
                        String amount = info.getString("amount")
                                .equals("null") ? "" : info.getString("amount");
                        String available_qty = info.getString("available_qty")
                                .equals("null") ? "" : info.getString("available_qty");
                        String prd_sched_avail_mark = info.getString("prd_sched_avail_mark")
                                .equals("null") ? "" : info.getString("prd_sched_avail_mark");
                        String prd_cancel_mark = info.getString("prd_cancel_mark")
                                .equals("null") ? "" : info.getString("prd_cancel_mark");
                        String prd_return_mark = info.getString("prd_return_mark")
                                .equals("null") ? "" : info.getString("prd_return_mark");

                        updatedServiceLists.add(new UpdatedServiceList(prd_id,prd_pfn_id,pfn_fee_name,prd_depts_id,
                                depts_name,prd_rate,prd_top_cat_rate,prd_qty,amount,available_qty,
                                prd_sched_avail_mark,prd_cancel_mark,prd_return_mark,true));
                    }
                }

                connected = true;
                updateLayout();
            }
            catch (Exception e) {
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

        requestQueue.add(paymentDetailsReq);
    }

    public void updateLayout() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (updatedServiceLists.size() == 0) {
                    totalAmountUpLay.setVisibility(View.GONE);
                    total_amount_up = "";
                    totalAmountUp.setText(total_amount_up);
                }
                else {
                    totalAmountUpLay.setVisibility(View.VISIBLE);
                    boolean found = false;
                    int ttt = 0;
                    for (int i = 0; i < updatedServiceLists.size(); i++) {
                        int amnt = Integer.parseInt(updatedServiceLists.get(i).getAmount());
                        ttt = ttt + amnt;
                        if (!updatedServiceLists.get(i).getPrd_sched_avail_mark().isEmpty()) {
                            int sch = Integer.parseInt(updatedServiceLists.get(i).getPrd_sched_avail_mark());
                            if (sch > 0) {
                                found = true;
                            }
                        }
                    }
                    total_amount_up = String.valueOf(ttt);
                    totalAmountUp.setText(total_amount_up);
                    if (found) {
                        updatePayment.setEnabled(false);
                        updatePayment.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.custom_button_disabled));
                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
                        alertDialogBuilder.setTitle("Warning!")
                                .setMessage("Appointment Schedule already taken from this payment. You will not be able to update this payment.")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                });

                        AlertDialog alert = alertDialogBuilder.create();
                        alert.setCancelable(false);
                        alert.setCanceledOnTouchOutside(false);
                        alert.show();
                    }
                    else {
                        updatePayment.setEnabled(true);
                        updatePayment.setBackground(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.custom_button));
                    }
                }

                updatedServiceAdapter = new UpdatedServiceAdapter(updatedServiceLists, AddPayment.this);
                upServiceView.setAdapter(updatedServiceAdapter);

            }
            else {
                alertMessageUp();
            }
        }
        else {
            alertMessageUp();
        }
    }

    public void alertMessageUp() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getPaymentDetails();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    searchPaymentLay.setHint("Select Payment Code");
                    searchPayment.setText("");

                    afterSelectingPrescription.setVisibility(View.GONE);
                    buttonLay.setVisibility(View.GONE);
                    totalAmountLay.setVisibility(View.GONE);
                    totalAmountUpLay.setVisibility(View.GONE);

                    resetAll();
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void updatePaymentDetails() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        parsing_message = "";

        checkToUpdatePaymentDtl();
    }

    public void checkToUpdatePaymentDtl() {
        boolean allUpdated = false;
        for (int x = 0; x < updatedServiceLists.size(); x++) {
            allUpdated = updatedServiceLists.get(x).isUpdated();
            if (!updatedServiceLists.get(x).isUpdated()) {
                updateDetailsData(x);
                break;
            }
        }

        if (allUpdated) {
            conn = true;
            connected = true;
            updateAfterUpdate();
        }
    }

    public void updateDetailsData(int index) {
        conn = false;
        connected = false;
        String prd_id = updatedServiceLists.get(index).getPrd_id();

        if (prd_id.isEmpty()) {
            String insertPayDetailsUrl = pre_url_api +"payement_receive/insertPaymentDetails";
            RequestQueue requestQueue = Volley.newRequestQueue(AddPayment.this);

            StringRequest insertPDReq = new StringRequest(Request.Method.POST, insertPayDetailsUrl, response -> {
                conn = true;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String string_out = jsonObject.getString("string_out");
                    parsing_message = string_out;

                    if (string_out.equals("Successfully Created")) {
                        connected = true;
                        updatedServiceLists.get(index).setUpdated(true);
                        checkToUpdatePaymentDtl();
                    }
                    else {
                        connected = false;
                        updateAfterUpdate();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    parsing_message = e.getLocalizedMessage();
                    connected = false;
                    updateAfterUpdate();
                }
            }, error -> {
                error.printStackTrace();
                parsing_message = error.getLocalizedMessage();
                conn = false;
                connected = false;
                updateAfterUpdate();
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("P_DEPTS_ID",updatedServiceLists.get(index).getPrd_depts_id());
                    headers.put("P_PFN_ID",updatedServiceLists.get(index).getPrd_pfn_id());
                    headers.put("P_PRD_PATIENT_CAT_ID",selected_pat_cat_id);
                    headers.put("P_PRD_QTY",updatedServiceLists.get(index).getPrd_qty());
                    headers.put("P_PRD_RATE", updatedServiceLists.get(index).getPrd_rate());
                    headers.put("P_PRD_TOP_CAT_RATE", updatedServiceLists.get(index).getPrd_top_cat_rate());
                    headers.put("P_PRD_USR", adminInfoLists.get(0).getUsr_name());
                    headers.put("P_PRM_CODE", selected_payment_code);
                    headers.put("P_PRM_ID", selected_prm_id);
                    headers.put("P_INSERT_TYPE_FLAG","3");
                    return headers;
                }
            };

            insertPDReq.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(insertPDReq);
        }
        else {
            String updatePayDetailsUrl = pre_url_api +"payement_receive/updatePaymentDetails";
            RequestQueue requestQueue = Volley.newRequestQueue(AddPayment.this);

            StringRequest updatePDReq = new StringRequest(Request.Method.POST, updatePayDetailsUrl, response -> {
                conn = true;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String string_out = jsonObject.getString("string_out");
                    parsing_message = string_out;

                    if (string_out.equals("Successfully Created")) {
                        connected = true;
                        updatedServiceLists.get(index).setUpdated(true);
                        checkToUpdatePaymentDtl();
                    }
                    else {
                        connected = false;
                        updateAfterUpdate();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    parsing_message = e.getLocalizedMessage();
                    connected = false;
                    updateAfterUpdate();
                }
            }, error -> {
                error.printStackTrace();
                parsing_message = error.getLocalizedMessage();
                conn = false;
                connected = false;
                updateAfterUpdate();
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("P_PRD_USR", adminInfoLists.get(0).getUsr_name());
                    headers.put("P_PRM_CODE", selected_payment_code);
                    headers.put("P_PRD_ID", updatedServiceLists.get(index).getPrd_id());
                    headers.put("P_PFN_ID",updatedServiceLists.get(index).getPrd_pfn_id());
                    headers.put("P_DEPTS_ID",updatedServiceLists.get(index).getPrd_depts_id());
                    headers.put("P_PRD_QTY",updatedServiceLists.get(index).getPrd_qty());
                    headers.put("P_PRD_RATE", updatedServiceLists.get(index).getPrd_rate());
                    headers.put("P_TOP_CAT_RATE",updatedServiceLists.get(index).getPrd_top_cat_rate());
                    return headers;
                }
            };

            updatePDReq.setRetryPolicy(new DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(updatePDReq);
        }
    }

    public void updateAfterUpdate() {
        loading = false;
        if (conn) {
            if (connected) {
                circularProgressIndicator.setVisibility(View.GONE);
                fullLayout.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
                alertDialogBuilder.setTitle("Success!")
                        .setMessage("Payment Updated Successfully.\n" +
                                "Payment Code: "+ selected_payment_code)
                        .setPositiveButton("Ok", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        });

                AlertDialog alert = alertDialogBuilder.create();
                alert.setCancelable(false);
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
            else {
                alertMessageUpPay();
            }
        }
        else {
            alertMessageUpPay();
        }
    }

    public void alertMessageUpPay() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected. Please retry or data will be lost.";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected. Please retry or data will be lost.";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(AddPayment.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please retry or data will be lost..")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updatePaymentDetails();
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
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