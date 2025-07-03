package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo;

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
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;

public class ManagementModify extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    ImageView close;
    TextView appbarName;

    TextInputLayout managementLay;
    AppCompatAutoCompleteTextView management;
    TextView managementMissing;
    ArrayList<String> managementLists;

    Button modify;

    private Boolean conn = false;
    private Boolean connected = false;
    boolean loading = false;
    String parsing_message = "";

    String type = "";
    String pmm_id = "";
    String pmap_id = "";
    String pmap_details = "";

    Logger logger = Logger.getLogger(ManagementModify.class.getName());

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
        setContentView(R.layout.activity_management_modify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.management_mod_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullLayout = findViewById(R.id.pat_management_modify_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_pat_management_modify);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_pat_management_modify);
        appbarName = findViewById(R.id.pat_management_modify_app_bar_text);

        managementLay = findViewById(R.id.spinner_layout_management_for_patient);
        management = findViewById(R.id.management_for_patient_spinner);
        managementMissing = findViewById(R.id.management_for_patient_missing_msg);
        managementMissing.setVisibility(View.GONE);

        modify = findViewById(R.id.pat_management_modify_button);

        Intent intent = getIntent();
        pmm_id = intent.getStringExtra("P_PMM_ID");
        type = intent.getStringExtra("MANAGE_MODIFY_TYPE");

        assert type != null;
        if (type.equals("ADD")) {
            String an = "Add Management Plan";
            appbarName.setText(an);
            modify.setText(type);
        }
        else {
            String an = "Update Management Plan";
            appbarName.setText(an);
            modify.setText(type);

            pmap_id = intent.getStringExtra("P_PMAP_ID" );
            pmap_details = intent.getStringExtra("P_DETAILS");

            management.setText(pmap_details);
        }



        management.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    managementMissing.setVisibility(View.GONE);
                }
            }
        });

        management.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            for (int i = 0; i <managementLists.size(); i++) {
                if (name.equals(managementLists.get(i))) {
                    pmap_details = managementLists.get(i);
                }
            }

            managementMissing.setVisibility(View.GONE);
            closeKeyBoard();
        });

        management.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    management.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        close.setOnClickListener(v -> finish());

        modify.setOnClickListener(v -> {
            pmap_details = Objects.requireNonNull(management.getText()).toString();
            if (!pmap_details.isEmpty()) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ManagementModify.this);
                if (type.equals("ADD")) {
                    alertDialogBuilder.setTitle("Add Management Plan!")
                            .setMessage("Do you want to add patient's new Management Plan?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                addManagement();
                            })
                            .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
                else {
                    alertDialogBuilder.setTitle("Update Management Plan!")
                            .setMessage("Do you want to update this Management Plan?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                dialog.dismiss();
                                updateManagement();
                            })
                            .setNegativeButton("No",(dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Write Management",Toast.LENGTH_SHORT).show();
                managementMissing.setVisibility(View.VISIBLE);
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

        managementLists = new ArrayList<>();

        String manageUrl = pre_url_api+"prescription/getManagementPlan";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest manageReq = new StringRequest(Request.Method.GET, manageUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pmap_details = info.getString("pmap_details")
                                .equals("null") ? "" : info.getString("pmap_details");

                        managementLists.add(pmap_details);
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

        requestQueue.add(manageReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ManagementModify.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,managementLists);
                management.setAdapter(arrayAdapter);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ManagementModify.this);
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

    public void addManagement() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String insertManage = pre_url_api+"prescription/insertManagement";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, insertManage, response -> {
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
                headers.put("P_DETAILS",pmap_details);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ManagementModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    addManagement();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void updateManagement() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateManage = pre_url_api+"prescription/updateManagement";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, updateManage, response -> {
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
                headers.put("P_PMAP_ID",pmap_id);
                headers.put("P_DETAILS",pmap_details);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(ManagementModify.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateManagement();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}