package ttit.com.shuvo.docdiary.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dashboard.DocDashboard;
import ttit.com.shuvo.docdiary.login.arraylists.CenterList;
import ttit.com.shuvo.docdiary.login.dialogue.SelectCenterDialogue;

public class DocLogin extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    TextInputEditText userCode;
    TextInputEditText userPassword;
    Button login;
    CheckBox checkBox;
    TextView contactAdmin;

    String user_code = "";
    String user_password = "";
    private Boolean conn = false;
    private Boolean connected = false;
    String user_message = "";
    SharedPreferences sharedpreferences;
    SharedPreferences sharedLoginRemember;

    public static final String LOGIN_ACTIVITY_FILE = "LOGIN_ACTIVITY_FILE_DOCDIARY";
    public static final String LOGIN_TF = "TRUE_FALSE";
    public static final String DOC_USER_CODE = "DOC_USER_CODE";
    public static final String DOC_USER_PASSWORD = "DOC_USER_PASSWORD";
    public static final String DOC_DATA_API = "DOC_DATA_API";

    public static final String MyPREFERENCES = "UserPass";
    public static final String user_code_remember = "nameKey";
    public static final String user_password_remember = "passKey";
    public static final String checked = "trueFalse";

    String getUserName = "";
    String getPassword = "";
    boolean getChecked = false;

    public static TextView centerName;
    public static String center_api = "";
    ArrayList<CenterList> centerLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_login);

        fullLayout = findViewById(R.id.login_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_doc_log_in);
        circularProgressIndicator.setVisibility(View.GONE);

        userCode = findViewById(R.id.user_code_given_log_in);
        userPassword = findViewById(R.id.password_given_log_in);
        login = findViewById(R.id.doc_log_in_button);
        checkBox = findViewById(R.id.remember_checkbox);
        contactAdmin = findViewById(R.id.contact_here_text);
        centerName = findViewById(R.id.selected_center_name);
//        centerName.setVisibility(View.GONE);

        sharedpreferences = getSharedPreferences(LOGIN_ACTIVITY_FILE, MODE_PRIVATE);
        sharedLoginRemember = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        getUserName = sharedLoginRemember.getString(user_code_remember,null);
        getPassword = sharedLoginRemember.getString(user_password_remember,null);
        getChecked = sharedLoginRemember.getBoolean(checked,false);

        if (getUserName != null) {
            userCode.setText(getUserName);
        }
        if (getPassword != null) {
            userPassword.setText(getPassword);
        }
        checkBox.setChecked(getChecked);

        userCode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    userCode.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        userPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    userPassword.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        contactAdmin.setOnClickListener(v -> {
            String mmm = "info@techterrain-it.com";
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocLogin.this)
                    .setMessage("Do you want to send an email to "+mmm+" ?")
//                        .setIcon(R.drawable.terrain_shop_icon)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.parse("mailto:"+mmm);
                        intent.setData(data);
                        try {
                            startActivity(intent);

                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(DocLogin.this, "There is no email app found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.show();
        });

        login.setOnClickListener(v -> {
            closeKeyBoard();

            user_code = Objects.requireNonNull(userCode.getText()).toString();
            user_password = Objects.requireNonNull(userPassword.getText()).toString();

            if (!center_api.isEmpty()) {
                if (!user_code.isEmpty() && !user_password.isEmpty()) {
                    loginCheck();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Give User Code and Password", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Center not selected correctly. Please exit the app and try again.", Toast.LENGTH_SHORT).show();
            }

        });

        centerName.setOnClickListener(v -> {
            SelectCenterDialogue selectCenterDialogue = new SelectCenterDialogue(centerLists,DocLogin.this);
            selectCenterDialogue.show(getSupportFragmentManager(),"CENTER");
        });

        getCenter();
    }

    private void closeKeyBoard () {
        View view = this.getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent event){
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed () {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Exit!")
//                .setIcon(R.drawable.thrm_logo)
                .setMessage("Do you want to exit?")
                .setPositiveButton("YES", (dialog, which) -> System.exit(0))
                .setNegativeButton("NO", (dialog, which) -> {

                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void getCenter() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        centerLists = new ArrayList<>();

        String url = "http://103.56.208.123:8001/apex/cstar_local/all_database/getAllDatabase";
        RequestQueue requestQueue = Volley.newRequestQueue(DocLogin.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String center_name = info.getString("center_name");
                        String center_api = info.getString("center_api");

                        centerLists.add(new CenterList(center_name,center_api));
                    }
                }
                connected = true;
                updateCenterList();
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                updateCenterList();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            updateCenterList();
        });

        requestQueue.add(stringRequest);
    }

    private void updateCenterList() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (conn) {
            if (connected) {
                if (centerLists.size() == 0) {
                    centerName.setVisibility(View.GONE);
                    AlertDialog dialog = new AlertDialog.Builder(DocLogin.this)
                            .setMessage("Center Database Not Found. Please contact to the administration to active Database.")
                            .setPositiveButton("OK", null)
                            .show();

                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positive.setOnClickListener(v -> {

                        dialog.dismiss();
                        finish();
                    });

                }
                else if(centerLists.size() == 1 ) {
                    center_api = centerLists.get(0).getCenter_api();
                    centerName.setVisibility(View.GONE);
                }
                else {
                    SelectCenterDialogue selectCenterDialogue = new SelectCenterDialogue(centerLists,DocLogin.this);
                    selectCenterDialogue.show(getSupportFragmentManager(),"CENTER");
                }
            }
            else {
                AlertDialog dialog = new AlertDialog.Builder(DocLogin.this)
                        .setMessage("There is a network issue in the server. Please Try later.")
                        .setPositiveButton("Retry", null)
                        .setNegativeButton("Exit",null)
                        .show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(v -> {

                    getCenter();
                    dialog.dismiss();
                });
                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setOnClickListener(v -> {

                    dialog.dismiss();
                    finish();
                });
            }
        }
        else {
            AlertDialog dialog = new AlertDialog.Builder(DocLogin.this)
                    .setMessage("Server Problem or Internet Not Connected.")
                    .setPositiveButton("Retry", null)
                    .setNegativeButton("Exit",null)
                    .show();

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                getCenter();
                dialog.dismiss();
            });
            Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negative.setOnClickListener(v -> {

                dialog.dismiss();
                finish();
            });
        }
    }

    public void loginCheck() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        user_message = "";
        conn = false;
        connected = false;

        String useridUrl = center_api+"login/getUserloginData?doc_code="+user_code+"&doc_password="+user_password+"";

        RequestQueue requestQueue = Volley.newRequestQueue(DocLogin.this);

        StringRequest getUserMessage = new StringRequest(Request.Method.GET, useridUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                user_message = jsonObject.getString("is_online");
                connected = true;
                updateLayout();
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                updateLayout();
            }

        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            updateLayout();
        });

        requestQueue.add(getUserMessage);
    }

    private void updateLayout() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        if (conn) {
            if (connected) {
                if (user_message.equals("true")) {
                    SharedPreferences.Editor editor = sharedLoginRemember.edit();
                    if (checkBox.isChecked()) {
                        editor.remove(user_code_remember);
                        editor.remove(user_password_remember);
                        editor.remove(checked);
                        editor.putString(user_code_remember,user_code);
                        editor.putString(user_password_remember,user_password);
                        editor.putBoolean(checked,true);
                        editor.apply();
                        editor.commit();
                    }
                    else {
                        editor.remove(user_code_remember);
                        editor.remove(user_password_remember);
                        editor.remove(checked);
                        editor.apply();
                        editor.commit();
                    }
                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                    editor1.remove(DOC_USER_CODE);
                    editor1.remove(DOC_USER_PASSWORD);
                    editor1.remove(DOC_DATA_API);
                    editor1.remove(LOGIN_TF);

                    editor1.putString(DOC_USER_CODE, user_code);
                    editor1.putString(DOC_USER_PASSWORD, user_password);
                    editor1.putString(DOC_DATA_API, center_api);
                    editor1.putBoolean(LOGIN_TF, true);
                    editor1.apply();
                    editor1.commit();

                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DocLogin.this, DocDashboard.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocLogin.this);
                    alertDialogBuilder.setTitle("Warning!")
//                .setIcon(R.drawable.thrm_logo)
                            .setMessage(user_message)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }
            }
            else {
                AlertDialog dialog = new AlertDialog.Builder(DocLogin.this)
                        .setMessage("There is a network issue in the server. Please Try later.")
                        .setPositiveButton("Retry", null)
                        .show();

                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(v -> {

                    loginCheck();
                    dialog.dismiss();
                });
            }
        }
        else {
            AlertDialog dialog = new AlertDialog.Builder(DocLogin.this)
                    .setMessage("Server Problem or Internet Not Connected")
                    .setPositiveButton("Retry", null)
                    .show();

            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {

                loginCheck();
                dialog.dismiss();
            });
        }
    }
}