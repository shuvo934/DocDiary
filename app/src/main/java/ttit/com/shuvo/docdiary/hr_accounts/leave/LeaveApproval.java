package ttit.com.shuvo.docdiary.hr_accounts.leave;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.adminInfoLists;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.leave.dialogs.LeaveReqSelectionDialog;
import ttit.com.shuvo.docdiary.hr_accounts.leave.interfaces.LeaveSelectedListener;

public class LeaveApproval extends AppCompatActivity implements LeaveSelectedListener {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    TextInputLayout requestCodeLay;
    TextInputEditText requestCodeLeave;

    CardView afterSelecting;
    LinearLayout afterSelectingButton;

    TextInputEditText name;
    TextInputEditText empCode;
    TextInputEditText appDate;
    TextInputEditText title;
    TextInputEditText leaveType;
    TextInputEditText leaveBalance;
    TextInputEditText fromDate;
    TextInputEditText toDate;
    TextInputEditText totalDays;
    TextInputEditText reason;
    TextInputEditText comments;
    TextInputLayout commentsLay;

    Button approve;
    Button reject;

    String emp_name = "";
    String emp_id = "";
    String app_date = "";
    String call_title = "";
    String lc_id = "";
    String leave_type = "";
    String leave_bal = "";
    String from_date = "";
    String to_date = "";
    String total_day = "";
    String reason_desc = "";
    String lc_short_code = "";

    String sl_check = "0";
    String approveSuccess = "";
    String rejectSuccess = "";

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";

    private Boolean isApprovedd = false;
    private Boolean isApprovedChecked = false;
    private Boolean appppppprrrrr = false;

    private Boolean isRejected = false;
    private Boolean isRejectedChecked = false;
    private Boolean rrreeejjjeecctt = false;

    String user_name = "";
    String req_code_leave = "";
    String la_id = "";
    String la_emp_id = "";
    String comment_text = "";

    Logger logger = Logger.getLogger(LeaveApproval.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_approval);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.leave_apr_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.leave_approval_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_leave_approval);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_leave_approval);

        requestCodeLay = findViewById(R.id.request_code_leave_approve_lay);
        requestCodeLeave = findViewById(R.id.request_code_leave_approve);

        afterSelecting = findViewById(R.id.after_request_selecting_leave_approve);
        afterSelecting.setVisibility(View.GONE);
        afterSelectingButton = findViewById(R.id.button_visiblity_leave_lay);
        afterSelectingButton.setVisibility(View.GONE);

        name = findViewById(R.id.name_leave_approve);
        empCode = findViewById(R.id.id_leave_approve);
        appDate = findViewById(R.id.now_date_leave_approve);
        title = findViewById(R.id.calling_title_leave_approve);
        leaveType = findViewById(R.id.leave_type_leave_approve);
        leaveBalance = findViewById(R.id.leave_balance_leave_approve);
        fromDate = findViewById(R.id.from_date_leave_approve);
        toDate = findViewById(R.id.to_date_leave_approve);
        totalDays = findViewById(R.id.total_days_leave_approve);
        reason = findViewById(R.id.reason_leave_approve);
        comments = findViewById(R.id.comments_given_leave_approve);
        commentsLay = findViewById(R.id.comments_given_layout_leave_approve);

        approve = findViewById(R.id.approve_button_leave);
        reject = findViewById(R.id.reject_button_leave);

        if (adminInfoLists != null) {
            if (!adminInfoLists.isEmpty()) {
                user_name = adminInfoLists.get(0).getUsr_name();
            }
        }

        requestCodeLeave.setOnClickListener(v -> {
            LeaveReqSelectionDialog selectRequest = new LeaveReqSelectionDialog(LeaveApproval.this);
            selectRequest.show(getSupportFragmentManager(),"Request");
        });

        comments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    String text = "Write Approve/Reject Comments:";
                    commentsLay.setHint(text);
                }
                else {
                    String text = "Approve/Reject Comments:";
                    commentsLay.setHint(text);
                }
            }
        });

        comments.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    comments.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        approve.setOnClickListener(v -> {
            if (Integer.parseInt(leave_bal) < Integer.parseInt(total_day)) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LeaveApproval.this);
                builder.setTitle("Warning!")
                        .setMessage("Leave Balance is lower than Total Leave Days.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                AlertDialog alert = builder.create();
                try {
                    alert.show();
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }
            }
            else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LeaveApproval.this);
                builder.setTitle("Approve Leave!")
                        .setMessage("Do you want approve this leave?")
                        .setPositiveButton("YES", (dialog, which) -> {
                            // checking it is sick leave or not
                            if (lc_short_code.equals("SL")) {
                                dialog.dismiss();
                                MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(LeaveApproval.this);
                                builder1.setTitle("Prescription Check!")
                                        .setMessage("Did you checked prescription of the applicant?")
                                        .setPositiveButton("YES", (dialog1, which1) -> {
                                            sl_check = "1";
                                            comment_text = Objects.requireNonNull(comments.getText()).toString();
                                            leaveApproveProcess();
                                        })
                                        .setNegativeButton("NO", (dialog12, which12) -> {
                                            sl_check = "0";
                                            comment_text = Objects.requireNonNull(comments.getText()).toString();
                                            leaveApproveProcess();
                                        });
                                AlertDialog alert = builder1.create();
                                try {
                                    alert.show();
                                } catch (Exception e) {
                                    restart("App is paused for a long time. Please Start the app again.");
                                }
                            }
                            else {
                                dialog.dismiss();
                                comment_text = Objects.requireNonNull(comments.getText()).toString();
                                leaveApproveProcess();
                            }

                        })
                        .setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
                AlertDialog alert = builder.create();
                try {
                    alert.show();
                } catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }
            }
        });

        reject.setOnClickListener(v -> {

            comment_text = Objects.requireNonNull(comments.getText()).toString();

            if (comment_text.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please mention reason for reject", Toast.LENGTH_SHORT).show();
            }
            else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LeaveApproval.this);
                builder.setTitle("Reject Leave!")
                        .setMessage("Do you want reject this leave?")
                        .setPositiveButton("YES", (dialog, which) -> leaveRejectProcess())
                        .setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
                AlertDialog alert = builder.create();
                try {
                    alert.show();
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }
            }
        });

        backButton.setOnClickListener(view -> {
            if (loading) {
                Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
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

        Intent intent = getIntent();
        int fl = intent.getIntExtra("FLAG",0);

        if (fl == 0) {
            LeaveReqSelectionDialog selectRequest = new LeaveReqSelectionDialog(LeaveApproval.this);
            selectRequest.show(getSupportFragmentManager(),"Request");
        }
        else {
            req_code_leave = intent.getStringExtra("LA_APP_CODE");
            la_id = intent.getStringExtra("LA_ID");
            la_emp_id = intent.getStringExtra("LA_EMP_ID");
            requestCodeLeave.setText(req_code_leave);
            requestCodeLay.setHint("Leave Request Code");
            getReqData();
        }
    }

    private void closeKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onLeaveSelected(String la_id, String la_app_code, String la_emp_id) {
        req_code_leave = la_app_code;
        this.la_id = la_id;
        this.la_emp_id = la_emp_id;
        requestCodeLeave.setText(req_code_leave);
        requestCodeLay.setHint("Leave Request Code");
        getReqData();
    }

    public void getReqData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        conn = false;
        connected = false;

        emp_name = "";
        emp_id = "";
        app_date = "";
        call_title = "";
        leave_type = "";
        leave_bal = "";
        from_date = "";
        to_date = "";
        total_day = "";
        reason_desc = "";
        lc_id = "";
        lc_short_code = "";

        String reqDataUrl = pre_url_api+"hrm_dashboard/getLeaveData?p_la_id="+la_id+"&p_usr_name="+user_name;

        RequestQueue requestQueue = Volley.newRequestQueue(LeaveApproval.this);

        StringRequest reqDataReq = new StringRequest(Request.Method.GET, reqDataUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject reqDataInfo = array.getJSONObject(i);

                        emp_id = reqDataInfo.getString("emp_code")
                                .equals("null") ? "" : reqDataInfo.getString("emp_code");
                        emp_name = reqDataInfo.getString("emp_name")
                                .equals("null") ? "" : reqDataInfo.getString("emp_name");
                        call_title = reqDataInfo.getString("la_calling_title")
                                .equals("null") ? "" : reqDataInfo.getString("la_calling_title");
                        app_date = reqDataInfo.getString("la_date")
                                .equals("null") ? "" : reqDataInfo.getString("la_date");
                        lc_id = reqDataInfo.getString("la_lc_id")
                                .equals("null") ? "" : reqDataInfo.getString("la_lc_id");
                        leave_type = reqDataInfo.getString("leave_type")
                                .equals("null") ? "" : reqDataInfo.getString("leave_type");
                        from_date = reqDataInfo.getString("la_from_date")
                                .equals("null") ? "" : reqDataInfo.getString("la_from_date");
                        to_date = reqDataInfo.getString("la_to_date")
                                .equals("null") ? "" : reqDataInfo.getString("la_to_date");
                        total_day = reqDataInfo.getString("la_leave_days")
                                .equals("null") ? "0" : reqDataInfo.getString("la_leave_days");
                        leave_bal= reqDataInfo.getString("leave_balance")
                                .equals("null") ? "0" : reqDataInfo.getString("leave_balance");
                        reason_desc = reqDataInfo.getString("la_reason")
                                .equals("null") ? "" : reqDataInfo.getString("la_reason");
                        lc_short_code = reqDataInfo.getString("lc_short_code")
                                .equals("null") ? "" : reqDataInfo.getString("lc_short_code");
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

        requestQueue.add(reqDataReq);
    }

    private void updateInterface() {
        if (conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                afterSelecting.setVisibility(View.VISIBLE);
                afterSelectingButton.setVisibility(View.VISIBLE);

                if (emp_name == null) {
                    name.setText("");
                } else {
                    name.setText(emp_name);
                }

                if (emp_id == null) {
                    empCode.setText("");
                } else {
                    empCode.setText(emp_id);
                }

                if (app_date == null) {
                    appDate.setText("");
                } else {
                    appDate.setText(app_date);
                }

                if (call_title == null) {
                    title.setText("");
                } else {
                    title.setText(call_title);
                }

                if (leave_type == null) {
                    leaveType.setText("");
                } else {
                    leaveType.setText(leave_type);
                }

                if (leave_bal == null) {
                    leaveBalance.setText("");
                } else {
                    leaveBalance.setText(leave_bal);
                }

                if (from_date == null) {
                    fromDate.setText("");
                } else {
                    fromDate.setText(from_date);
                }

                if (to_date == null) {
                    toDate.setText("");
                } else {
                    toDate.setText(to_date);
                }

                if (total_day == null) {
                    totalDays.setText("");
                } else {
                    totalDays.setText(total_day);
                }

                if (reason_desc == null) {
                    reason.setText("");
                } else {
                    reason.setText(reason_desc);
                }

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

        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(LeaveApproval.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getReqData();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                    finish();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        try {
            alert.show();
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }
    }

    public void leaveApproveProcess() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        appppppprrrrr = false;
        isApprovedd = false;
        isApprovedChecked = false;

        approveSuccess = "";

        String url = pre_url_api+"hrm_dashboard/approveLeave";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest approveReq = new StringRequest(Request.Method.POST, url, response -> {
            appppppprrrrr = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                System.out.println(string_out);
                String updated_req = jsonObject.getString("updated_req");
                if (string_out.equals("Successfully Created")) {
                    isApprovedd = true;
                    isApprovedChecked = updated_req.toLowerCase(Locale.ENGLISH).equals("ok");
                    approveSuccess = updated_req;
                }
                else {
                    System.out.println(string_out);
                    isApprovedd = false;
                }
                updateLayout();
            }
            catch (JSONException e) {
                isApprovedd = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateLayout();
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            appppppprrrrr = false;
            isApprovedd = false;
            updateLayout();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_LA_ID",la_id);
                headers.put("P_USR_NAME",user_name);
                headers.put("P_SL_CHECK",sl_check);
                headers.put("P_TEXT_COMMENTS",comment_text);
                return headers;
            }
        };

        requestQueue.add(approveReq);
    }

    private void updateLayout() {
        if (appppppprrrrr) {
            if (isApprovedd) {
                fullLayout.setVisibility(View.GONE);
                circularProgressIndicator.setVisibility(View.GONE);
                loading = false;
                if (isApprovedChecked) {
                    appppppprrrrr = false;
                    isApprovedd = false;
                    isApprovedChecked = false;
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LeaveApproval.this);
                    builder.setMessage("Leave Approved Successfully")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            });

                    AlertDialog alert = builder.create();
                    alert.setCancelable(false);
                    alert.setCanceledOnTouchOutside(false);
                    try {
                        alert.show();
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
                else {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), approveSuccess, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                alertMessageApLeave();
            }
        }
        else {
            alertMessageApLeave();
        }
    }

    public void alertMessageApLeave() {
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

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    leaveApproveProcess();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        try {
            alert.show();
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }
    }

    public void leaveRejectProcess() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
        rejectSuccess = "";

        rrreeejjjeecctt = false;
        isRejected = false;
        isRejectedChecked = false;

        String url = pre_url_api+"hrm_dashboard/rejectLeave";

        RequestQueue requestQueue = Volley.newRequestQueue(LeaveApproval.this);

        StringRequest rejectReq = new StringRequest(Request.Method.POST, url, response -> {
            rrreeejjjeecctt = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                String updated_req = jsonObject.getString("updated_req");
                if (string_out.equals("Successfully Created")) {
                    isRejected = true;
                    isRejectedChecked = updated_req.toLowerCase(Locale.ENGLISH).equals("ok");
                    rejectSuccess = updated_req;
                }
                else {
                    System.out.println(string_out);
                    isRejected = false;
                }
                updateAfterReject();
            }
            catch (JSONException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                isRejected = false;
                updateAfterReject();
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            rrreeejjjeecctt = false;
            isRejected = false;
            updateAfterReject();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_LA_ID",la_id);
                headers.put("P_USR_NAME",user_name);
                headers.put("P_TEXT_COMMENTS",comment_text);
                return headers;
            }
        };

        requestQueue.add(rejectReq);
    }

    private void updateAfterReject() {
        if (rrreeejjjeecctt) {
            if (isRejected) {
                fullLayout.setVisibility(View.GONE);
                circularProgressIndicator.setVisibility(View.GONE);
                loading = false;
                if (isRejectedChecked) {
                    rrreeejjjeecctt = false;
                    isRejected = false;
                    isRejectedChecked = false;
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LeaveApproval.this);
                    builder
                            .setMessage("Leave Rejected Successfully")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            });

                    AlertDialog alert = builder.create();
                    alert.setCancelable(false);
                    alert.setCanceledOnTouchOutside(false);
                    try {
                        alert.show();
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
                else {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), rejectSuccess, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                alertMessageReject();
            }
        }
        else {
            alertMessageReject();
        }
    }

    public void alertMessageReject() {
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

        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    leaveRejectProcess();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    loading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        try {
            alert.show();
        }
        catch (Exception e) {
            restart("App is paused for a long time. Please Start the app again.");
        }
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