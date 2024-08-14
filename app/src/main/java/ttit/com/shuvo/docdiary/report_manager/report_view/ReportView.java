package ttit.com.shuvo.docdiary.report_manager.report_view;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.report_manager.report_view.arraylists.ParameterList;
import ttit.com.shuvo.docdiary.report_manager.report_view.dialog.ParameterSelectionDialogue;
import ttit.com.shuvo.docdiary.report_manager.report_view.interfaces.DepartmentSelectListener;
import ttit.com.shuvo.docdiary.report_manager.report_view.interfaces.DoctorSelectListener;
import ttit.com.shuvo.docdiary.report_manager.report_view.interfaces.ServiceSelectListener;
import ttit.com.shuvo.docdiary.report_manager.report_view.interfaces.UnitSelectListener;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.DocWiseAppRepShow;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.DocWisePayRcvRepShow;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.PaymentRcvReportShow;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.PaymentRcvSumRepShow;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.ReportShow;

public class ReportView extends AppCompatActivity implements DepartmentSelectListener, UnitSelectListener, ServiceSelectListener, DoctorSelectListener {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView close;
    TextView appbarName;
    TextInputEditText beginDate, endDate;
    TextView dateRangInvalid;

    String firstDate = "";
    String lastDate = "";
    private int mYear, mMonth, mDay;

    TextInputLayout departSelectLay;
    TextInputEditText departSelect;

    TextInputLayout unitSelectLay;
    TextInputEditText unitSelect;

    TextInputLayout doctorSelectLay;
    TextInputEditText doctorSelect;

    TextInputLayout serviceSelectLay;
    TextInputEditText serviceSelect;

    Button show;

    String r_type = "0";

    ArrayList<ParameterList> allDepList;
    ArrayList<ParameterList> allUnitList;
    ArrayList<ParameterList> allServiceList;

    ArrayList<ParameterList> modifiedUnitList;
    ArrayList<ParameterList> modifiedServiceList;
    ArrayList<ParameterList> modifiedDoctorList;

    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";
    boolean loading = false;

    public static String selected_dept_name = "";
    public static String selected_dept_id = "";
    public static String selected_unit_name = "";
    public static String selected_unit_id = "";
    public static String selected_doctor_name = "";
    public static String selected_doctor_id = "";
    public static String selected_service_name = "";
    public static String selected_service_id = "";

    String report_name = "";
    String server_port = "";
    String hrs_link = "";
    String rep_path = "";
    String cid_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ReportView.this,R.color.clouds));
        setContentView(R.layout.activity_report_view);

        fullLayout = findViewById(R.id.report_view_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_report_view);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_report_view);
        appbarName = findViewById(R.id.report_view_app_bar_text);

        beginDate = findViewById(R.id.begin_date_payment_rcv);
        endDate = findViewById(R.id.end_date_payment_rcv);
        dateRangInvalid  = findViewById(R.id.date_range_msg_pay_rcv);
        dateRangInvalid.setVisibility(View.GONE);

        departSelectLay = findViewById(R.id.select_department_layout_pay_rcv_report);
        departSelect = findViewById(R.id.department_selection_pay_rcv_report);

        unitSelectLay = findViewById(R.id.select_unit_layout_pay_rcv_report);
        unitSelect = findViewById(R.id.unit_selection_pay_rcv_report);

        doctorSelectLay = findViewById(R.id.select_doctors_layout_pay_rcv_report);
        doctorSelect = findViewById(R.id.doctor_selection_pay_rcv_report);

        serviceSelectLay = findViewById(R.id.select_service_layout_pay_rcv_report);
        serviceSelect = findViewById(R.id.service_selection_pay_rcv_report);

        show = findViewById(R.id.report_show_button);

        Intent i = getIntent();
        r_type = i.getStringExtra("R_TYPE");

        assert r_type != null;
        switch (r_type) {
            case "1": {
                String tt = "Payment Receive Report";
                appbarName.setText(tt);
                report_name = "PAYMENT_RECEIVED.rep";
                doctorSelectLay.setVisibility(View.GONE);
                doctorSelectLay.setEnabled(false);
                departSelectLay.setEnabled(true);
                unitSelectLay.setEnabled(true);
                serviceSelectLay.setVisibility(View.VISIBLE);
                serviceSelectLay.setEnabled(true);
                break;
            }
            case "2": {
                String tt = "Payment Receive Summary Report";
                appbarName.setText(tt);
                report_name = "PAYMENT_RECEIVED_SUMMERY.rep";
                doctorSelectLay.setVisibility(View.GONE);
                doctorSelectLay.setEnabled(false);
                departSelectLay.setEnabled(true);
                unitSelectLay.setEnabled(true);
                serviceSelectLay.setVisibility(View.GONE);
                serviceSelectLay.setEnabled(true);
                break;
            }
            case "3": {
                String tt = "Doctor Wise Payment Receive Report";
                appbarName.setText(tt);
                report_name = "DOC_WISE_PAT_APP_PAY_SUMMERY.rep";
                doctorSelectLay.setVisibility(View.VISIBLE);
                doctorSelectLay.setEnabled(false);
                departSelectLay.setEnabled(true);
                unitSelectLay.setEnabled(false);
                serviceSelectLay.setVisibility(View.VISIBLE);
                serviceSelectLay.setEnabled(false);
                break;
            }
            case "4": {
                String tt = "Doctor Wise Appointment Report";
                appbarName.setText(tt);
                report_name = "APPOINTMENT_SUMMARY.rep"; //DOC_WISE_APPOINTMENT.rep
                doctorSelectLay.setVisibility(View.VISIBLE);
                doctorSelectLay.setEnabled(false);
                departSelectLay.setEnabled(true);
                unitSelectLay.setEnabled(false);
                serviceSelectLay.setVisibility(View.GONE);
                serviceSelectLay.setEnabled(false);
                break;
            }
        }

        allDepList = new ArrayList<>();
        allUnitList = new ArrayList<>();
        allServiceList = new ArrayList<>();

        modifiedUnitList = new ArrayList<>();
        modifiedServiceList = new ArrayList<>();
        modifiedDoctorList = new ArrayList<>();

        close.setOnClickListener(v -> onBackPressed());

        // Getting date
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy",Locale.getDefault());

        firstDate = simpleDateFormat.format(c);
        firstDate = "01-"+firstDate;
        lastDate = df.format(c);

        beginDate.setText(firstDate);
        endDate.setText(lastDate);

        beginDate.setOnClickListener(v -> {
            final Calendar c1 = Calendar.getInstance();
            Date fdate = null;
            try {
                fdate = df.parse(firstDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (fdate != null) {
                c1.setTime(fdate);
            }
            mYear = c1.get(Calendar.YEAR);
            mMonth = c1.get(Calendar.MONTH);
            mDay = c1.get(Calendar.DAY_OF_MONTH);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportView.this, (view, year, month, dayOfMonth) -> {

                    String monthName = "";
                    String dayOfMonthName = "";
                    String yearName = "";
                    month = month + 1;
                    if (month == 1) {
                        monthName = "JAN";
                    } else if (month == 2) {
                        monthName = "FEB";
                    } else if (month == 3) {
                        monthName = "MAR";
                    } else if (month == 4) {
                        monthName = "APR";
                    } else if (month == 5) {
                        monthName = "MAY";
                    } else if (month == 6) {
                        monthName = "JUN";
                    } else if (month == 7) {
                        monthName = "JUL";
                    } else if (month == 8) {
                        monthName = "AUG";
                    } else if (month == 9) {
                        monthName = "SEP";
                    } else if (month == 10) {
                        monthName = "OCT";
                    } else if (month == 11) {
                        monthName = "NOV";
                    } else if (month == 12) {
                        monthName = "DEC";
                    }

                    if (dayOfMonth <= 9) {
                        dayOfMonthName = "0" + dayOfMonth;
                    } else {
                        dayOfMonthName = String.valueOf(dayOfMonth);
                    }
                    yearName  = String.valueOf(year);
                    yearName = yearName.substring(yearName.length()-2);
                    System.out.println(yearName);
                    System.out.println(dayOfMonthName);
                    String dateText = dayOfMonthName + "-" + monthName + "-" + yearName;
                    beginDate.setText(dateText);
                    firstDate = dateText;
                    if (lastDate.isEmpty()) {
                        dateRangInvalid.setVisibility(View.GONE);
                    }
                    else {
                        Date bDate = null;
                        Date eDate = null;

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());

                        try {
                            bDate = sdf.parse(firstDate);
                            eDate = sdf.parse(lastDate);
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (bDate != null && eDate != null) {
                            if (eDate.after(bDate) || eDate.equals(bDate)) {
                                dateRangInvalid.setVisibility(View.GONE);
                            }
                            else {
                                dateRangInvalid.setVisibility(View.VISIBLE);
                                beginDate.setText("");
                                firstDate = "";
                            }
                        }
                    }

                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(v -> {
            final Calendar c12 = Calendar.getInstance();
            Date ldate = null;
            try {
                ldate = df.parse(lastDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (ldate != null) {
                c12.setTime(ldate);
            }
            mYear = c12.get(Calendar.YEAR);
            mMonth = c12.get(Calendar.MONTH);
            mDay = c12.get(Calendar.DAY_OF_MONTH);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReportView.this, (view, year, month, dayOfMonth) -> {

                    String monthName = "";
                    String dayOfMonthName = "";
                    String yearName = "";
                    month = month + 1;
                    if (month == 1) {
                        monthName = "JAN";
                    } else if (month == 2) {
                        monthName = "FEB";
                    } else if (month == 3) {
                        monthName = "MAR";
                    } else if (month == 4) {
                        monthName = "APR";
                    } else if (month == 5) {
                        monthName = "MAY";
                    } else if (month == 6) {
                        monthName = "JUN";
                    } else if (month == 7) {
                        monthName = "JUL";
                    } else if (month == 8) {
                        monthName = "AUG";
                    } else if (month == 9) {
                        monthName = "SEP";
                    } else if (month == 10) {
                        monthName = "OCT";
                    } else if (month == 11) {
                        monthName = "NOV";
                    } else if (month == 12) {
                        monthName = "DEC";
                    }

                    if (dayOfMonth <= 9) {
                        dayOfMonthName = "0" + dayOfMonth;
                    } else {
                        dayOfMonthName = String.valueOf(dayOfMonth);
                    }
                    yearName  = String.valueOf(year);
                    yearName = yearName.substring(yearName.length()-2);
                    System.out.println(yearName);
                    System.out.println(dayOfMonthName);
                    String dateText = dayOfMonthName + "-" + monthName + "-" + yearName;
                    endDate.setText(dateText);
                    lastDate = dateText;

                    if (firstDate.isEmpty()) {
                        dateRangInvalid.setVisibility(View.GONE);
                    }
                    else {
                        Date bDate = null;
                        Date eDate = null;

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault());

                        try {
                            bDate = sdf.parse(firstDate);
                            eDate = sdf.parse(lastDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (bDate != null && eDate != null) {
                            if (eDate.after(bDate) || eDate.equals(bDate)) {
                                dateRangInvalid.setVisibility(View.GONE);
                            }
                            else {
                                dateRangInvalid.setVisibility(View.VISIBLE);
                                endDate.setText("");
                                lastDate = "";
                            }
                        }
                    }

                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        departSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    departSelectLay.setHint("Select Department");
                }
                else {
                    departSelectLay.setHint("Department");
                }
            }
        });

        departSelect.setOnClickListener(v -> {
            ParameterSelectionDialogue parameterSelectionDialogue = new ParameterSelectionDialogue(allDepList, ReportView.this,"DEPT");
            parameterSelectionDialogue.show(getSupportFragmentManager(),"PARAM_DEPT");
        });

        unitSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    unitSelectLay.setHint("Select Unit");
                }
                else {
                    unitSelectLay.setHint("Unit");
                }
            }
        });

        unitSelect.setOnClickListener(v -> {
            ParameterSelectionDialogue parameterSelectionDialogue;
            if (selected_dept_id.isEmpty()) {
                parameterSelectionDialogue = new ParameterSelectionDialogue(allUnitList, ReportView.this, "UNT");
            }
            else {
                parameterSelectionDialogue = new ParameterSelectionDialogue(modifiedUnitList, ReportView.this, "UNT");
            }
            parameterSelectionDialogue.show(getSupportFragmentManager(),"PARAM_UNT");
        });

        doctorSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    doctorSelectLay.setHint("Select Doctor");
                }
                else {
                    doctorSelectLay.setHint("Doctor");
                }
            }
        });

        doctorSelect.setOnClickListener(v -> {
            if (!selected_unit_id.isEmpty()) {
                ParameterSelectionDialogue parameterSelectionDialogue = new ParameterSelectionDialogue(modifiedDoctorList, ReportView.this, "DOC");
                parameterSelectionDialogue.show(getSupportFragmentManager(),"PARAM_DOC");
            }
        });

        serviceSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    serviceSelectLay.setHint("Select Service");
                }
                else {
                    serviceSelectLay.setHint("Service");
                }
            }
        });

        serviceSelect.setOnClickListener(v -> {
            ParameterSelectionDialogue parameterSelectionDialogue;
            if (selected_unit_id.isEmpty()) {
                parameterSelectionDialogue = new ParameterSelectionDialogue(allServiceList, ReportView.this, "SRV");
            }
            else {
                parameterSelectionDialogue = new ParameterSelectionDialogue(modifiedServiceList, ReportView.this, "SRV");
            }
            parameterSelectionDialogue.show(getSupportFragmentManager(),"PARAM_SRV");
        });

        show.setOnClickListener(v -> {
            if (!firstDate.isEmpty() && !lastDate.isEmpty()) {
                switch (r_type) {
                    case "3":
                        if (!selected_doctor_id.isEmpty()) {
                            String url = "http://" + server_port + "/reports/rwservlet?" + hrs_link + "+report=" + rep_path + "" + report_name + "+BEGIN_DATE=" + firstDate + "+END_DATE=" + lastDate + "+UNIT=" + selected_unit_id + "+DOC=" + selected_doctor_id + "+PFN=" + selected_service_id;
                            Intent intent = new Intent(ReportView.this, DocWisePayRcvRepShow.class);
                            intent.putExtra("REPORT_URL", url);
                            intent.putExtra("FIRST_DATE", firstDate);
                            intent.putExtra("LAST_DATE", lastDate);
                            intent.putExtra("DOC_ID", selected_doctor_id);
                            intent.putExtra("PFN_ID", selected_service_id);
                            intent.putExtra("DOC_NAME",selected_doctor_name);
                            intent.putExtra("UNIT_NAME",selected_unit_name);
                            intent.putExtra("DEP_NAME",selected_dept_name);
                            intent.putExtra("APP_BAR_NAME", appbarName.getText().toString());
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Please Select Doctor", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "4": {
                        String url = "http://" + server_port + "/reports/rwservlet?" + hrs_link + "+report=" + rep_path + "" + report_name + "+BEGIN_DATE=" + firstDate + "+END_DATE=" + lastDate + /*"+UNIT=" + selected_unit_id +*/ "+DOC=" + selected_doctor_id;
                        Intent intent = new Intent(ReportView.this, DocWiseAppRepShow.class);
                        intent.putExtra("REPORT_URL", url);
                        intent.putExtra("FIRST_DATE", firstDate);
                        intent.putExtra("LAST_DATE", lastDate);
                        intent.putExtra("DEPTD_ID", selected_dept_id);
                        intent.putExtra("DEPTS_ID",selected_unit_id);
                        intent.putExtra("DOC_ID", selected_doctor_id);
                        intent.putExtra("APP_BAR_NAME", appbarName.getText().toString());
                        startActivity(intent);
                        break;
                    }
                    case "1": {
                        String url = "http://" + server_port + "/reports/rwservlet?" + hrs_link + "+report=" + rep_path + "" + report_name + "+BEGIN_DATE=" + firstDate + "+END_DATE=" + lastDate + "+DEPTD=" + selected_dept_id + "+DEPTS=" + selected_unit_id + "+PFN=" + selected_service_id + "+CIDID=" + cid_id;
                        Intent intent = new Intent(ReportView.this, PaymentRcvReportShow.class);
                        intent.putExtra("REPORT_URL", url);
                        intent.putExtra("FIRST_DATE", firstDate);
                        intent.putExtra("LAST_DATE", lastDate);
                        intent.putExtra("DEPTD_ID", selected_dept_id);
                        intent.putExtra("DEPTS_ID",selected_unit_id);
                        intent.putExtra("PFN_ID", selected_service_id);
                        intent.putExtra("APP_BAR_NAME", appbarName.getText().toString());
                        startActivity(intent);
                        break;
                    }
                    case "2": {
                        String url = "http://" + server_port + "/reports/rwservlet?" + hrs_link + "+report=" + rep_path + "" + report_name + "+BEGIN_DATE=" + firstDate + "+END_DATE=" + lastDate + "+DEPTD=" + selected_dept_id + "+DEPTS=" + selected_unit_id + "+PFN=" + selected_service_id + "+CIDID=" + cid_id;
                        Intent intent = new Intent(ReportView.this, PaymentRcvSumRepShow.class);
                        intent.putExtra("REPORT_URL", url);
                        intent.putExtra("FIRST_DATE", firstDate);
                        intent.putExtra("LAST_DATE", lastDate);
                        intent.putExtra("DEPTD_ID", selected_dept_id);
                        intent.putExtra("DEPTS_ID",selected_unit_id);
                        intent.putExtra("APP_BAR_NAME", appbarName.getText().toString());
                        startActivity(intent);
                        break;
                    }
                }
            }
            else {
                Toast.makeText(this, "Please Select Date Range", Toast.LENGTH_SHORT).show();
            }
        });

        getAllParameterData();
    }

    @Override
    public void onBackPressed() {
        if (loading) {
            Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
        }
        else {
            selected_dept_name = "";
            selected_dept_id = "";
            selected_unit_name = "";
            selected_unit_id = "";
            selected_doctor_name = "";
            selected_doctor_id = "";
            selected_service_name = "";
            selected_service_id = "";
            super.onBackPressed();
        }
    }

    @Override
    public void onDeptSelect() {
        selected_unit_name = "";
        selected_unit_id = "";
        selected_doctor_name = "";
        selected_doctor_id = "";
        selected_service_name = "";
        selected_service_id = "";

        switch (r_type) {
            case "1":
            case "2":
                departSelect.setText(selected_dept_name);

                unitSelectLay.setHint("Select Unit");
                unitSelect.setText("");

                serviceSelectLay.setHint("Select Service");
                serviceSelect.setText("");

                unitSelectLay.setEnabled(true);
                doctorSelectLay.setEnabled(false);
                serviceSelectLay.setEnabled(true);
                break;

            case "3":
            case "4":
                departSelect.setText(selected_dept_name);

                unitSelectLay.setHint("Select Unit");
                unitSelect.setText("");

                serviceSelectLay.setHint("Select Service");
                serviceSelect.setText("");

                doctorSelectLay.setHint("Select Doctor");
                doctorSelect.setText("");

                unitSelectLay.setEnabled(false);
                doctorSelectLay.setEnabled(false);
                serviceSelectLay.setEnabled(false);
                break;

        }

        getUnit();
    }

    @Override
    public void onUnitSelect() {
        selected_doctor_name = "";
        selected_doctor_id = "";
        selected_service_name = "";
        selected_service_id = "";

        switch (r_type) {
            case "1":
                unitSelect.setText(selected_unit_name);

                serviceSelectLay.setHint("Select Service");
                serviceSelect.setText("");

                doctorSelectLay.setEnabled(false);
                serviceSelectLay.setEnabled(true);
                getServices();
                break;
            case "2":
                unitSelect.setText(selected_unit_name);

                serviceSelectLay.setHint("Select Service");
                serviceSelect.setText("");

                doctorSelectLay.setEnabled(false);
                serviceSelectLay.setEnabled(true);
                break;

            case "3":
            case "4":
                unitSelect.setText(selected_unit_name);

                serviceSelectLay.setHint("Select Service");
                serviceSelect.setText("");

                doctorSelectLay.setHint("Select Doctor");
                doctorSelect.setText("");

                doctorSelectLay.setEnabled(false);
                serviceSelectLay.setEnabled(false);
                getDoctors();
                break;

        }
    }

    @Override
    public void onDoctorSelect() {
        selected_service_name = "";
        selected_service_id = "";

        doctorSelect.setText(selected_doctor_name);

        serviceSelectLay.setHint("Select Service");
        serviceSelect.setText("");

        serviceSelectLay.setEnabled(false);

        if (r_type.equals("3")) {
            getServices();
        }
    }

    @Override
    public void onServiceSelect() {
        serviceSelect.setText(selected_service_name);
    }

    public void getAllParameterData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        allDepList = new ArrayList<>();
        allUnitList = new ArrayList<>();
        allServiceList = new ArrayList<>();

        String deptUrl = pre_url_api+"report_manager/getDeptd";
        String unitUrl = pre_url_api+"report_manager/getDepts?deptd_id=";
        String serviceUrl = pre_url_api+"report_manager/getService?depts_id=";
        String reportUrl = pre_url_api+"report_manager/getUrlData";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest rpeortUrlReq = new StringRequest(Request.Method.GET, reportUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject apptDataInfo = array.getJSONObject(i);

                        server_port = apptDataInfo.getString("lic_rep_server_port")
                                .equals("null") ? "" :apptDataInfo.getString("lic_rep_server_port");
                        rep_path = apptDataInfo.getString("lic_rep_path")
                                .equals("null") ? "" :apptDataInfo.getString("lic_rep_path");
                        hrs_link = apptDataInfo.getString("lic_rep_hrs_link")
                                .equals("null") ? "" :apptDataInfo.getString("lic_rep_hrs_link");
                        cid_id = apptDataInfo.getString("cid_id")
                                .equals("null") ? "" :apptDataInfo.getString("cid_id");
                    }
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

        StringRequest serviceReq = new StringRequest(Request.Method.GET, serviceUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    allServiceList.add(new ParameterList("","..."));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject apptDataInfo = array.getJSONObject(i);

                        String pfn_id = apptDataInfo.getString("pfn_id")
                                .equals("null") ? "" :apptDataInfo.getString("pfn_id");
                        String pfn_fee_name = apptDataInfo.getString("pfn_fee_name")
                                .equals("null") ? "" :apptDataInfo.getString("pfn_fee_name");

                        allServiceList.add(new ParameterList(pfn_id,pfn_fee_name));
                    }
                }
                requestQueue.add(rpeortUrlReq);
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

        StringRequest unitReq = new StringRequest(Request.Method.GET, unitUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    allUnitList.add(new ParameterList("","..."));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject apptDataInfo = array.getJSONObject(i);

                        String depts_id = apptDataInfo.getString("depts_id")
                                .equals("null") ? "" :apptDataInfo.getString("depts_id");
                        String depts_name = apptDataInfo.getString("depts_name")
                                .equals("null") ? "" :apptDataInfo.getString("depts_name");

                        allUnitList.add(new ParameterList(depts_id,depts_name));
                    }
                }
                requestQueue.add(serviceReq);
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

        StringRequest deptdReq = new StringRequest(Request.Method.GET, deptUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    allDepList.add(new ParameterList("","..."));
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject apptDataInfo = array.getJSONObject(i);

                        String deptd_id = apptDataInfo.getString("deptd_id")
                                .equals("null") ? "" :apptDataInfo.getString("deptd_id");
                        String deptd_name = apptDataInfo.getString("deptd_name")
                                .equals("null") ? "" :apptDataInfo.getString("deptd_name");

                        allDepList.add(new ParameterList(deptd_id,deptd_name));
                    }
                }
                requestQueue.add(unitReq);
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

        requestQueue.add(deptdReq);
    }

    private void updateLayout() {
        loading = false;
        if (conn) {
            if (connected) {
                circularProgressIndicator.setVisibility(View.GONE);
                fullLayout.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ReportView.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getAllParameterData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getUnit() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        modifiedUnitList = new ArrayList<>();

        String unitUrl = pre_url_api+"report_manager/getDepts?deptd_id="+selected_dept_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest unitReq = new StringRequest(Request.Method.GET, unitUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject apptDataInfo = array.getJSONObject(i);

                        String depts_id = apptDataInfo.getString("depts_id")
                                .equals("null") ? "" :apptDataInfo.getString("depts_id");
                        String depts_name = apptDataInfo.getString("depts_name")
                                .equals("null") ? "" :apptDataInfo.getString("depts_name");

                        modifiedUnitList.add(new ParameterList(depts_id,depts_name));
                    }
                }

                connected = true;
                updateUnitLayout();
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateUnitLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateUnitLayout();
        });

        requestQueue.add(unitReq);
    }

    private void updateUnitLayout() {
        loading = false;
        if (conn) {
            if (connected) {
                circularProgressIndicator.setVisibility(View.GONE);
                fullLayout.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                if(r_type.equals("3")) {
                    unitSelectLay.setEnabled(!selected_dept_id.isEmpty());
                }
                else {
                    unitSelectLay.setEnabled(true);
                }

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ReportView.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getUnit();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getServices() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        modifiedServiceList = new ArrayList<>();

        String serviceUrl = pre_url_api+"report_manager/getService?depts_id="+selected_unit_id;
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
                        JSONObject apptDataInfo = array.getJSONObject(i);

                        String pfn_id = apptDataInfo.getString("pfn_id")
                                .equals("null") ? "" :apptDataInfo.getString("pfn_id");
                        String pfn_fee_name = apptDataInfo.getString("pfn_fee_name")
                                .equals("null") ? "" :apptDataInfo.getString("pfn_fee_name");

                        modifiedServiceList.add(new ParameterList(pfn_id,pfn_fee_name));
                    }
                }
                connected = true;
                updateServLayout();
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateServLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateServLayout();
        });

        requestQueue.add(serviceReq);
    }

    private void updateServLayout() {
        loading = false;
        if (conn) {
            if (connected) {
                circularProgressIndicator.setVisibility(View.GONE);
                fullLayout.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                serviceSelectLay.setEnabled(true);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ReportView.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getServices();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void getDoctors() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        modifiedDoctorList = new ArrayList<>();

        String doctorUrl = pre_url_api+"report_manager/getDoctors?depts_id="+selected_unit_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest doctorReq = new StringRequest(Request.Method.GET, doctorUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject apptDataInfo = array.getJSONObject(i);

                        String doc_id = apptDataInfo.getString("doc_id")
                                .equals("null") ? "" :apptDataInfo.getString("doc_id");
                        String doc_name = apptDataInfo.getString("doc_name")
                                .equals("null") ? "" :apptDataInfo.getString("doc_name");

                        modifiedDoctorList.add(new ParameterList(doc_id,doc_name));
                    }
                }
                connected = true;
                updateDocLayout();
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateDocLayout();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateDocLayout();
        });

        requestQueue.add(doctorReq);
    }

    private void updateDocLayout() {
        loading = false;
        if (conn) {
            if (connected) {
                circularProgressIndicator.setVisibility(View.GONE);
                fullLayout.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                doctorSelectLay.setEnabled(true);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ReportView.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getDoctors();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}