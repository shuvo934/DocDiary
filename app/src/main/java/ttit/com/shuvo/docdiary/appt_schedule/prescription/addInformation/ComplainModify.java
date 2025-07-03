package ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.ListOfComplains;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.ListOfInjuries;

public class ComplainModify extends AppCompatActivity {


    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView close;
    TextView appbarName;

    TextInputLayout complainLay;
    AppCompatAutoCompleteTextView complain;
    TextView complainMissing;
    ArrayList<ListOfComplains> listOfComplains;

    TextInputLayout injuryLay;
    AppCompatAutoCompleteTextView injury;
    TextView injuryMissing;
    ArrayList<ListOfInjuries> listOfInjuries;

    TextInputEditText injuryDate;
    TextView injuryDateMissing;
    private int mYear, mMonth, mDay;

    Button modify;


    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";

    String type = "";
    String pmm_id = "";
    String cm_name = "";
    String cm_id = "";
    String injury_id = "";
    String injury_name = "";
    String injury_date = "";
    boolean selectedFromItems = false;

    String pci_id = "";

    Logger logger = Logger.getLogger(ComplainModify.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (API 34) or Android 15 (API 35)
            WindowCompat.setDecorFitsSystemWindows(window, false);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.clouds));
            WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
            controller.setAppearanceLightStatusBars(true);
            controller.setAppearanceLightNavigationBars(false);
        } else {
            // Safe legacy approach for Android < 14
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.clouds));
        }
        setContentView(R.layout.activity_complain_modify);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.comp_modify_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.pat_complain_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_complain_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_pat_complain_modify);
        appbarName = findViewById(R.id.pat_complain_modify_app_bar_text);

        complainLay = findViewById(R.id.spinner_layout_complain_for_patient);
        complain = findViewById(R.id.complain_for_patient_spinner);
        complainMissing = findViewById(R.id.complain_for_patient_missing_msg);
        complainMissing.setVisibility(View.GONE);

        injuryLay = findViewById(R.id.spinner_layout_injury_for_patient);
        injury = findViewById(R.id.injury_for_patient_spinner);
        injuryMissing = findViewById(R.id.injury_for_patient_missing_msg);
        injuryMissing.setVisibility(View.GONE);

        injuryDate = findViewById(R.id.injury_date_for_patient_complain);
        injuryDateMissing = findViewById(R.id.injury_date_for_patient_missing_msg);
        injuryDateMissing.setVisibility(View.GONE);

        modify = findViewById(R.id.pat_complain_modify_button);

        Intent intent = getIntent();
        pmm_id = intent.getStringExtra("P_PMM_ID");
        type = intent.getStringExtra("MODIFY_TYPE");

        assert type != null;
        if (type.equals("ADD")) {
            String an = "Add Complain";
            appbarName.setText(an);
            modify.setText(type);
        }
        else {
            String an = "Update Complain";
            appbarName.setText(an);
            modify.setText(type);
            pci_id = intent.getStringExtra("P_PCI_ID" );
            cm_id = intent.getStringExtra("P_CM_ID");
            cm_name = intent.getStringExtra("P_CM_NAME");
            injury_id = intent.getStringExtra("P_INJURY_ID");
            injury_name = intent.getStringExtra("P_INJURY_NAME");
            injury_date = intent.getStringExtra("P_DATE");

            complain.setText(cm_name);
            injury.setText(injury_name);
            injuryDate.setText(injury_date);
        }

        complain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    complainMissing.setVisibility(View.GONE);
                }
            }
        });

        complain.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <listOfComplains.size(); i++) {
                if (name.equals(listOfComplains.get(i).getCm_name())) {
                    cm_id = listOfComplains.get(i).getCm_id();
                    cm_name = listOfComplains.get(i).getCm_name();
                }
            }

            selectedFromItems = true;
            complainMissing.setVisibility(View.GONE);
            System.out.println("CM_NAME: " + cm_name);
            System.out.println("CM_ID: "+ cm_id);
            closeKeyBoard();
        });

        complain.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = complain.getText().toString();
                if (!selectedFromItems) {
                    cm_id = "";
                    for (int i = 0; i < listOfComplains.size(); i++) {
                        if (ss.equals(listOfComplains.get(i).getCm_name())) {
                            cm_id = listOfComplains.get(i).getCm_id();
                            cm_name = listOfComplains.get(i).getCm_name();

                        }
                    }
                    if (cm_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            complainMissing.setVisibility(View.VISIBLE);
                            String cmt = "Please Write Complain";
                            complainMissing.setText(cmt);
                        }
                        else {
                            complainMissing.setVisibility(View.VISIBLE);
                            String cmt = "Invalid Complain";
                            complainMissing.setText(cmt);
                        }
                    }

                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        complain.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    complain.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        injury.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    injuryMissing.setVisibility(View.GONE);
                }
            }
        });

        injury.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <listOfInjuries.size(); i++) {
                if (name.equals(listOfInjuries.get(i).getInjury_name())) {
                    injury_id = listOfInjuries.get(i).getInjury_id();
                    injury_name = listOfInjuries.get(i).getInjury_name();
                }
            }

            selectedFromItems = true;
            injuryMissing.setVisibility(View.GONE);
            closeKeyBoard();
        });

        injury.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = injury.getText().toString();
                if (!selectedFromItems) {
                    injury_id = "";
                    for (int i = 0; i < listOfInjuries.size(); i++) {
                        if (ss.equals(listOfInjuries.get(i).getInjury_name())) {
                            injury_id = listOfInjuries.get(i).getInjury_id();
                            injury_name = listOfInjuries.get(i).getInjury_name();

                        }
                    }
                    if (injury_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            injuryMissing.setVisibility(View.VISIBLE);
                            String imt = "Please Write Cause of Injury";
                            injuryMissing.setText(imt);
                        }
                        else {
                            injuryMissing.setVisibility(View.VISIBLE);
                            String imt = "Invalid Cause of Injury";
                            injuryMissing.setText(imt);
                        }
                    }
                }
                else {
                    selectedFromItems = false;
                }
            }
        });

        injury.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    injury.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        injuryDate.setOnClickListener(v -> {
            final Calendar c1 = Calendar.getInstance();
            mYear = c1.get(Calendar.YEAR);
            mMonth = c1.get(Calendar.MONTH);
            mDay = c1.get(Calendar.DAY_OF_MONTH);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ComplainModify.this, (view1, year, month, dayOfMonth) -> {

                    String monthName = "";
                    String dayOfMonthName;
                    String yearName;
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
                    injuryDate.setText(dateText);
                    injuryDateMissing.setVisibility(View.GONE);
                    injury_date = Objects.requireNonNull(injuryDate.getText()).toString();

                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            }
        });

        close.setOnClickListener(v -> finish());

        modify.setOnClickListener(v -> {
            if (!cm_id.isEmpty()) {
                if (!injury_id.isEmpty()) {
                    if (!injury_date.isEmpty()) {
                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ComplainModify.this);
                        if (type.equals("ADD")) {
                            alertDialogBuilder.setTitle("Add Complain!")
                                    .setMessage("Do you want to add patient's new complain?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        dialog.dismiss();
                                        addComplain();
                                    })
                                    .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
                        }
                        else {
                            alertDialogBuilder.setTitle("Update Complain!")
                                    .setMessage("Do you want to update this complain?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        dialog.dismiss();
                                        updateComplain();
                                    })
                                    .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                            AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Please Provide Injury Date",Toast.LENGTH_SHORT).show();
                        injuryDateMissing.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please Provide Cause of Injury",Toast.LENGTH_SHORT).show();
                    injuryMissing.setVisibility(View.VISIBLE);
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Provide Complain",Toast.LENGTH_SHORT).show();
                complainMissing.setVisibility(View.VISIBLE);
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

        getData();
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

    public void getData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        listOfComplains = new ArrayList<>();
        listOfInjuries = new ArrayList<>();

        String complainUrl = pre_url_api+"prescription/getComplain?pmm_id="+pmm_id;
        String injuryUrl = pre_url_api+"prescription/getInjury?pmm_id="+pmm_id;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest injuryReq = new StringRequest(Request.Method.GET, injuryUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (type.equals("UPDATE")) {
                    listOfInjuries.add(new ListOfInjuries(injury_id,injury_name,""));
                }
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String injury_id = info.getString("injury_id")
                                .equals("null") ? "" : info.getString("injury_id");
                        String injury_name = info.getString("injury_name")
                                .equals("null") ? "" : info.getString("injury_name");
                        String injury_notes = info.getString("injury_notes")
                                .equals("null") ? "" : info.getString("injury_notes");

                        listOfInjuries.add(new ListOfInjuries(injury_id,injury_name,injury_notes));
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

        StringRequest complainReq = new StringRequest(Request.Method.GET, complainUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (type.equals("UPDATE")) {
                    listOfComplains.add(new ListOfComplains(cm_name,"",cm_id));
                }
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String cm_name = info.getString("cm_name")
                                .equals("null") ? "" : info.getString("cm_name");
                        String cm_notes = info.getString("cm_notes")
                                .equals("null") ? "" : info.getString("cm_notes");
                        String cm_id = info.getString("cm_id")
                                .equals("null") ? "" : info.getString("cm_id");

                        listOfComplains.add(new ListOfComplains(cm_name,cm_notes,cm_id));
                    }
                }

                requestQueue.add(injuryReq);
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

        requestQueue.add(complainReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (listOfComplains.isEmpty()) {
                    String cmt = "No Complain List Found";
                    complainMissing.setText(cmt);
                    complainMissing.setVisibility(View.VISIBLE);
                }
                else {
                    complainMissing.setVisibility(View.GONE);
                }

                if (listOfInjuries.isEmpty()) {
                    String imt = "No Cause of Injury List Found";
                    injuryMissing.setText(imt);
                    injuryMissing.setVisibility(View.VISIBLE);
                }
                else {
                    injuryMissing.setVisibility(View.GONE);
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < listOfComplains.size(); i++) {
                    type.add(listOfComplains.get(i).getCm_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ComplainModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                complain.setAdapter(arrayAdapter);

                ArrayList<String> type1 = new ArrayList<>();
                for(int i = 0; i < listOfInjuries.size(); i++) {
                    type1.add(listOfInjuries.get(i).getInjury_name());
                }

                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(ComplainModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type1);
                injury.setAdapter(arrayAdapter1);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ComplainModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getData();
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

    public void addComplain() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String insertComplain = pre_url_api+"prescription/insertComplain";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, insertComplain, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;

                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterAdd();
                }
                else {
                    connected = false;
                    updateAfterAdd();
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAfterAdd();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAfterAdd();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PMM_ID",pmm_id);
                headers.put("P_CM_ID",cm_id);
                headers.put("P_INJURY_DATE",injury_date);
                headers.put("P_INJURY_ID",injury_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void updateAfterAdd() {
        loading = false;
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
//                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ComplainModify.this);
//                alertDialogBuilder.setTitle("Success!")
//                        .setMessage("New Complain Added.")
//                        .setPositiveButton("Ok", (dialog, which) -> {
//                            dialog.dismiss();
//                            finish();
//                        });
//
//                AlertDialog alert = alertDialogBuilder.create();
//                alert.setCancelable(false);
//                alert.setCanceledOnTouchOutside(false);
//                alert.show();
                finish();
            }
            else {
                alertMessageFailedAdd();
            }
        }
        else {
            alertMessageFailedAdd();
        }
    }

    public void alertMessageFailedAdd() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ComplainModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    addComplain();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void updateComplain() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateComplain = pre_url_api+"prescription/updateComplain";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, updateComplain, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;

                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterUpdate();
                }
                else {
                    connected = false;
                    updateAfterUpdate();
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAfterUpdate();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAfterUpdate();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PMM_ID",pmm_id);
                headers.put("P_CM_ID",cm_id);
                headers.put("P_DATE",injury_date);
                headers.put("P_INJURY_ID",injury_id);
                headers.put("P_PCI_ID",pci_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    private void updateAfterUpdate() {
        loading = false;
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                finish();
            }
            else {
                alertMessageFailedUpdate();
            }
        }
        else {
            alertMessageFailedUpdate();
        }
    }

    public void alertMessageFailedUpdate() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ComplainModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateComplain();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}