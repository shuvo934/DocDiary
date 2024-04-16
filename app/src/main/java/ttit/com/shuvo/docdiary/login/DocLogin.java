package ttit.com.shuvo.docdiary.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dashboard.DocDashboard;
import ttit.com.shuvo.docdiary.login.arraylists.CenterList;
import ttit.com.shuvo.docdiary.login.arraylists.MultipleUserList;
import ttit.com.shuvo.docdiary.login.dialogue.SelectCenterDialogue;
import ttit.com.shuvo.docdiary.login.dialogue.SelectUserIdDialogue;

public class DocLogin extends AppCompatActivity implements CallBackListener,IDCallbackListener,CloseCallBack{

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;

    TextInputEditText userMobile;
    TextInputEditText userPassword;
    Button login;
    CheckBox checkBox;
    TextView contactAdmin;

    String user_mobile = "";
    public static String center_doc_code = "";
    String user_password = "";
    private Boolean conn = false;
    private Boolean connected = false;
    String user_message = "";
    String is_available = "";
    String prv_message = "";
    SharedPreferences sharedpreferences;
    SharedPreferences sharedLoginRemember;

    public static final String LOGIN_ACTIVITY_FILE = "LOGIN_ACTIVITY_FILE_DOCDIARY";
    public static final String LOGIN_TF = "TRUE_FALSE";
    public static final String DOC_USER_CODE = "DOC_USER_CODE";
    public static final String DOC_USER_PASSWORD = "DOC_USER_PASSWORD";
    public static final String DOC_DATA_API = "DOC_DATA_API";
    public static final String DOC_ALL_ID = "DOC_ALL_ID";

    public static final String MyPREFERENCES = "UserPass";
    public static final String user_mobile_remember = "nameKey";
    public static final String user_password_remember = "passKey";
    public static final String checked = "trueFalse";

    String getUserMobile = "";
    String getPassword = "";
    boolean getChecked = false;

    public static TextView centerName;
    public static String center_api = "";
    ArrayList<CenterList> centerLists;
    boolean loading = false;
    Gson gson = new Gson();
    String json = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_login);

        fullLayout = findViewById(R.id.login_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_doc_log_in);
        circularProgressIndicator.setVisibility(View.GONE);

        userMobile = findViewById(R.id.user_mobile_given_log_in);
        userPassword = findViewById(R.id.password_given_log_in);
        login = findViewById(R.id.doc_log_in_button);
        checkBox = findViewById(R.id.remember_checkbox);
        contactAdmin = findViewById(R.id.contact_here_text);
        centerName = findViewById(R.id.selected_center_name);
        centerName.setVisibility(View.GONE);
        centerLists = new ArrayList<>();

        sharedpreferences = getSharedPreferences(LOGIN_ACTIVITY_FILE, MODE_PRIVATE);
        sharedLoginRemember = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        getUserMobile = sharedLoginRemember.getString(user_mobile_remember,null);
        getPassword = sharedLoginRemember.getString(user_password_remember,null);
        getChecked = sharedLoginRemember.getBoolean(checked,false);

        if (getUserMobile != null) {
            userMobile.setText(getUserMobile);
        }
        if (getPassword != null) {
            userPassword.setText(getPassword);
        }
        checkBox.setChecked(getChecked);

        userMobile.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    userMobile.clearFocus();
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
            String bodyText = "Please fill out the field to create your user id.\n\n" +
                    "Center/Hospital Name: \n" +
                    "Center/Hospital Address: \n" +
                    "User Name: \n" +
                    "User Mobile No: \n" +
                    "User Designation: \n" +
                    "User Mail: \n" +
                    "User Code(If any): ";
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocLogin.this)
                    .setMessage("Do you want to send an email to "+mmm+" ?")
//                        .setIcon(R.drawable.terrain_shop_icon)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri data = Uri.parse("mailto:"+mmm+"?subject="+"New Account Creation Request From DocDiary"
                                +"&body="+Uri.encode(bodyText));
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

            user_mobile = Objects.requireNonNull(userMobile.getText()).toString();
            user_password = Objects.requireNonNull(userPassword.getText()).toString();

            if (!user_mobile.isEmpty() && !user_password.isEmpty()) {
                loginCheck();
            } else {
                Toast.makeText(getApplicationContext(), "Please Give Mobile No and Password", Toast.LENGTH_SHORT).show();
            }
//            if (!center_api.isEmpty()) {
//                if (!user_mobile.isEmpty() && !user_password.isEmpty()) {
//                    loginCheck();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Please Give User Code and Password", Toast.LENGTH_SHORT).show();
//                }
//            }
//            else {
//                Toast.makeText(getApplicationContext(), "Center not selected correctly. Please exit the app and try again.", Toast.LENGTH_SHORT).show();
//            }

        });

//        centerName.setOnClickListener(v -> {
//            SelectCenterDialogue selectCenterDialogue = new SelectCenterDialogue(centerLists,DocLogin.this);
//            selectCenterDialogue.show(getSupportFragmentManager(),"CENTER");
//        });

//        getCenter();
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
        if (loading) {
            Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
        }
        else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Exit!")
                .setIcon(R.drawable.doc_diary_default)
                    .setMessage("Do you want to exit?")
                    .setPositiveButton("YES", (dialog, which) -> System.exit(0))
                    .setNegativeButton("NO", (dialog, which) -> {

                    });
            AlertDialog alert = builder.create();
            try {
                alert.show();
            }
            catch (Exception e) {
                restart("App is paused for a long time. Please Start the app again.");
            }
        }

    }


//    public void getCenter() {
//        fullLayout.setVisibility(View.GONE);
//        circularProgressIndicator.setVisibility(View.VISIBLE);
//        conn = false;
//        connected = false;
//        centerLists = new ArrayList<>();
//
//        // http://103.56.208.123:8001/apex/cstar_local/
//        String url = "http://103.56.208.123:8001/apex/cstar_local/all_database/getAllDatabase";
//        RequestQueue requestQueue = Volley.newRequestQueue(DocLogin.this);
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
//            conn = true;
//            try {
//                JSONObject jsonObject = new JSONObject(response);
//                String items = jsonObject.getString("items");
//                String count = jsonObject.getString("count");
//                if (!count.equals("0")) {
//                    JSONArray array = new JSONArray(items);
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject info = array.getJSONObject(i);
//
//                        String center_name = info.getString("center_name");
//                        String center_api = info.getString("center_api");
//
//                        centerLists.add(new CenterList(center_name,center_api));
//                    }
//                }
//                connected = true;
//                updateCenterList();
//            }
//            catch (JSONException e) {
//                connected = false;
//                e.printStackTrace();
//                updateCenterList();
//            }
//        }, error -> {
//            conn = false;
//            connected = false;
//            error.printStackTrace();
//            updateCenterList();
//        });
//
//        requestQueue.add(stringRequest);
//    }

//    private void updateCenterList() {
//        fullLayout.setVisibility(View.VISIBLE);
//        circularProgressIndicator.setVisibility(View.GONE);
//        if (conn) {
//            if (connected) {
//                if (centerLists.size() == 0) {
//                    centerName.setVisibility(View.GONE);
//                    AlertDialog dialog = new AlertDialog.Builder(DocLogin.this)
//                            .setMessage("Center Database Not Found. Please contact to the administration to active Database.")
//                            .setPositiveButton("OK", null)
//                            .show();
//
//                    dialog.setCancelable(false);
//                    dialog.setCanceledOnTouchOutside(false);
//                    Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                    positive.setOnClickListener(v -> {
//
//                        dialog.dismiss();
//                        finish();
//                    });
//
//                }
//                center_api = "http://103.56.208.123:8001/apex/cstar_local/";
////                else if(centerLists.size() == 1 ) {
////                    center_api = centerLists.get(0).getCenter_api();
////                    centerName.setVisibility(View.GONE);
////                }
////                else {
////                    SelectCenterDialogue selectCenterDialogue = new SelectCenterDialogue(centerLists,DocLogin.this);
////                    selectCenterDialogue.show(getSupportFragmentManager(),"CENTER");
////                }
//            }
//            else {
//                AlertDialog dialog = new AlertDialog.Builder(DocLogin.this)
//                        .setMessage("There is a network issue in the server. Please Try later.")
//                        .setPositiveButton("Retry", null)
//                        .setNegativeButton("Exit",null)
//                        .show();
//
//                dialog.setCancelable(false);
//                dialog.setCanceledOnTouchOutside(false);
//                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positive.setOnClickListener(v -> {
//
//                    getCenter();
//                    dialog.dismiss();
//                });
//                Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                negative.setOnClickListener(v -> {
//
//                    dialog.dismiss();
//                    finish();
//                });
//            }
//        }
//        else {
//            AlertDialog dialog = new AlertDialog.Builder(DocLogin.this)
//                    .setMessage("Server Problem or Internet Not Connected.")
//                    .setPositiveButton("Retry", null)
//                    .setNegativeButton("Exit",null)
//                    .show();
//
//            dialog.setCancelable(false);
//            dialog.setCanceledOnTouchOutside(false);
//            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//            positive.setOnClickListener(v -> {
//
//                getCenter();
//                dialog.dismiss();
//            });
//            Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//            negative.setOnClickListener(v -> {
//
//                dialog.dismiss();
//                finish();
//            });
//        }
//    }

    public void loginCheck() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        user_message = "";
        conn = false;
        connected = false;
        loading = true;
        prv_message = "";
        centerLists = new ArrayList<>();
        center_api = "";
        center_doc_code = "";
        System.out.println("START");

        String useridUrl = "http://103.56.208.123:8001/apex/cstar_local/"+"login/getUserloginData?doc_code="+ user_mobile +"&doc_password="+user_password+"";
        String userIdUrl2 = "http://103.73.227.28:8080/cstar/cstar-ramp/"+"login/getUserloginData?doc_code="+ user_mobile +"&doc_password="+user_password+"";
        String userIdUrl3 = "http://202.4.109.126:8003/crps/crp_savar/"+"login/getUserloginData?doc_code="+ user_mobile +"&doc_password="+user_password+"";
        String userIdUrl4 = "http://144.48.119.59:8002/apex/crp_mirpur/"+"login/getUserloginData?doc_code="+ user_mobile +"&doc_password="+user_password+"";
        String userIdUrl5 = "http://103.73.227.28:8080/cstar/cstar_bsd/"+"login/getUserloginData?doc_code="+ user_mobile +"&doc_password="+user_password+"";

        RequestQueue requestQueue = Volley.newRequestQueue(DocLogin.this);

        StringRequest getUserMessage5 = new StringRequest(Request.Method.GET, userIdUrl5, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                user_message = jsonObject.getString("is_online");
                is_available = jsonObject.getString("is_available");
                center_doc_code = jsonObject.getString("p_doc_code");
                center_api = jsonObject.getString("p_center_api");

                if (is_available.equals("1")) {
//                    connected = true;
//                    updateLayout();
                    centerLists.add(new CenterList("CSTAR - BASHUNDHARA",center_api, center_doc_code,new ArrayList<>()));
                    System.out.println("5th Found : 1");
                }
                else if (is_available.equals("3")) {
                    String p_doc_list = jsonObject.getString("p_doc_list");
                    JSONArray array = new JSONArray(p_doc_list);
                    ArrayList<MultipleUserList> multipleUserLists = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);
                        String dc = docInfo.getString("doc_code")
                                .equals("null") ? "" : docInfo.getString("doc_code");
                        String dn = docInfo.getString("doc_name")
                                .equals("null") ? "" : docInfo.getString("doc_name");
                        String dep_name = docInfo.getString("depts_name")
                                .equals("null") ? "" : docInfo.getString("depts_name");
                        multipleUserLists.add(new MultipleUserList(dc,dn,dep_name));
                    }
                    centerLists.add(new CenterList("CSTAR - BASHUNDHARA",center_api, center_doc_code,multipleUserLists));
                    System.out.println("5th Found : 3");
                }
                System.out.println("5th Found : 2,0");
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

        StringRequest getUserMessage4 = new StringRequest(Request.Method.GET, userIdUrl4, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                user_message = jsonObject.getString("is_online");
                is_available = jsonObject.getString("is_available");
                center_doc_code = jsonObject.getString("p_doc_code");
                center_api = jsonObject.getString("p_center_api");
                switch (is_available) {
                    case "1":
//                    connected = true;
//                    updateLayout();
                        centerLists.add(new CenterList("CRP - MIRPUR", center_api, center_doc_code, new ArrayList<>()));
                        requestQueue.add(getUserMessage5);
                        System.out.println("4th Found : 1");
                        break;
                    case "0":
//                    connected = true;
//                    updateLayout();
                        prv_message = user_message;
                        requestQueue.add(getUserMessage5);
                        System.out.println("4th Found : 0");
                        break;
                    case "3":
                        String p_doc_list = jsonObject.getString("p_doc_list");
                        JSONArray array = new JSONArray(p_doc_list);
                        ArrayList<MultipleUserList> multipleUserLists = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);
                            String dc = docInfo.getString("doc_code")
                                    .equals("null") ? "" : docInfo.getString("doc_code");
                            String dn = docInfo.getString("doc_name")
                                    .equals("null") ? "" : docInfo.getString("doc_name");
                            String dep_name = docInfo.getString("depts_name")
                                    .equals("null") ? "" : docInfo.getString("depts_name");
                            multipleUserLists.add(new MultipleUserList(dc, dn, dep_name));
                        }
                        centerLists.add(new CenterList("CRP - MIRPUR", center_api, center_doc_code, multipleUserLists));
                        requestQueue.add(getUserMessage5);
                        System.out.println("4th Found : 3");
                        break;
                    default:
                        requestQueue.add(getUserMessage5);
                        System.out.println("4th Found : 2");
                        break;
                }

            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                requestQueue.add(getUserMessage5);
            }

        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            requestQueue.add(getUserMessage5);
        });

        StringRequest getUserMessage3 = new StringRequest(Request.Method.GET, userIdUrl3, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                user_message = jsonObject.getString("is_online");
                is_available = jsonObject.getString("is_available");
                center_doc_code = jsonObject.getString("p_doc_code");
                center_api = jsonObject.getString("p_center_api");
                switch (is_available) {
                    case "1":
//                    connected = true;
//                    updateLayout();
                        centerLists.add(new CenterList("CRP - SAVAR", center_api, center_doc_code, new ArrayList<>()));
                        requestQueue.add(getUserMessage4);
                        System.out.println("3rd Found : 1");
                        break;
                    case "0":
//                    connected = true;
//                    updateLayout();
                        prv_message = user_message;
                        requestQueue.add(getUserMessage4);
                        System.out.println("3rd Found : 0");
                        break;
                    case "3":
                        String p_doc_list = jsonObject.getString("p_doc_list");
                        JSONArray array = new JSONArray(p_doc_list);
                        ArrayList<MultipleUserList> multipleUserLists = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);
                            String dc = docInfo.getString("doc_code")
                                    .equals("null") ? "" : docInfo.getString("doc_code");
                            String dn = docInfo.getString("doc_name")
                                    .equals("null") ? "" : docInfo.getString("doc_name");
                            String dep_name = docInfo.getString("depts_name")
                                    .equals("null") ? "" : docInfo.getString("depts_name");
                            multipleUserLists.add(new MultipleUserList(dc, dn, dep_name));
                        }
                        centerLists.add(new CenterList("CRP - SAVAR", center_api, center_doc_code, multipleUserLists));
                        requestQueue.add(getUserMessage4);
                        System.out.println("3rd Found : 3");
                        break;
                    default:
                        requestQueue.add(getUserMessage4);
                        System.out.println("3rd Found : 2");
                        break;
                }

            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                requestQueue.add(getUserMessage4);
            }

        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            requestQueue.add(getUserMessage4);
        });

        StringRequest getUserMessage2 = new StringRequest(Request.Method.GET, userIdUrl2, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                user_message = jsonObject.getString("is_online");
                is_available = jsonObject.getString("is_available");
                center_doc_code = jsonObject.getString("p_doc_code");
                center_api = jsonObject.getString("p_center_api");
                switch (is_available) {
                    case "1":
//                    connected = true;
//                    updateLayout();
                        centerLists.add(new CenterList("CSTAR - RAMPURA", center_api, center_doc_code, new ArrayList<>()));
                        requestQueue.add(getUserMessage3);
                        System.out.println("2nd Found : 1");
                        break;
                    case "0":
//                    connected = true;
//                    updateLayout();
                        prv_message = user_message;
                        requestQueue.add(getUserMessage3);
                        System.out.println("2nd Found : 0");
                        break;
                    case "3":
                        String p_doc_list = jsonObject.getString("p_doc_list");
                        JSONArray array = new JSONArray(p_doc_list);
                        ArrayList<MultipleUserList> multipleUserLists = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);
                            String dc = docInfo.getString("doc_code")
                                    .equals("null") ? "" : docInfo.getString("doc_code");
                            String dn = docInfo.getString("doc_name")
                                    .equals("null") ? "" : docInfo.getString("doc_name");
                            String dep_name = docInfo.getString("depts_name")
                                    .equals("null") ? "" : docInfo.getString("depts_name");
                            multipleUserLists.add(new MultipleUserList(dc, dn, dep_name));
                        }
                        centerLists.add(new CenterList("CSTAR - RAMPURA", center_api, center_doc_code, multipleUserLists));
                        requestQueue.add(getUserMessage3);
                        System.out.println("2nd Found : 3");
                        break;
                    default:
                        requestQueue.add(getUserMessage3);
                        System.out.println("2nd Found : 2");
                        break;
                }

            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                requestQueue.add(getUserMessage3);
            }

        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            requestQueue.add(getUserMessage3);
        });


        StringRequest getUserMessage = new StringRequest(Request.Method.GET, useridUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                user_message = jsonObject.getString("is_online");
                is_available = jsonObject.getString("is_available");
                center_doc_code = jsonObject.getString("p_doc_code");
                center_api = jsonObject.getString("p_center_api");
                switch (is_available) {
                    case "1":
//                    connected = true;
//                    updateLayout();
                        centerLists.add(new CenterList("CSTAR - LOCAL", center_api, center_doc_code, new ArrayList<>()));
                        requestQueue.add(getUserMessage2);
                        System.out.println("1st Found : 1");
                        break;
                    case "0":
//                    connected = true;
//                    updateLayout();
                        prv_message = user_message;
                        requestQueue.add(getUserMessage2);
                        System.out.println("1st Found : 0");
                        break;
                    case "3":
                        String p_doc_list = jsonObject.getString("p_doc_list");
                        JSONArray array = new JSONArray(p_doc_list);
                        ArrayList<MultipleUserList> multipleUserLists = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject docInfo = array.getJSONObject(i);
                            String dc = docInfo.getString("doc_code")
                                    .equals("null") ? "" : docInfo.getString("doc_code");
                            String dn = docInfo.getString("doc_name")
                                    .equals("null") ? "" : docInfo.getString("doc_name");
                            String dep_name = docInfo.getString("depts_name")
                                    .equals("null") ? "" : docInfo.getString("depts_name");
                            multipleUserLists.add(new MultipleUserList(dc, dn, dep_name));
                        }
                        centerLists.add(new CenterList("CSTAR - LOCAL", center_api, center_doc_code, multipleUserLists));
                        requestQueue.add(getUserMessage2);
                        System.out.println("1st Found : 3");
                        break;
                    default:
                        requestQueue.add(getUserMessage2);
                        System.out.println("1st Found : 2");
                        break;
                }

            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                requestQueue.add(getUserMessage2);
            }

        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            requestQueue.add(getUserMessage2);
        });

        requestQueue.add(getUserMessage);
    }

    private void updateLayout() {
        if (centerLists.size() != 0) {
            if (centerLists.size() == 1) {
                ArrayList<MultipleUserList> multipleUserLists = centerLists.get(0).getMultipleUserLists();
                if (multipleUserLists.size() == 0) {
                    center_doc_code = centerLists.get(0).getDoc_code();
                    center_api = centerLists.get(0).getCenter_api();
                    System.out.println("Center Found : "+centerLists.size());
                    updateFLFlag();
                }
                else {
                    System.out.println("USERs Found : "+multipleUserLists.size());
                    center_api = centerLists.get(0).getCenter_api();
                    String cn = centerLists.get(0).getCenter_name();
                    json = gson.toJson(centerLists);
                    SelectUserIdDialogue selectUserIdDialogue = new SelectUserIdDialogue(multipleUserLists,DocLogin.this,cn);
                    try {
                        selectUserIdDialogue.show(getSupportFragmentManager(),"USER_CENTER");
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
            }
            else {
                System.out.println("Center Found : "+centerLists.size());
                json = gson.toJson(centerLists);

                SelectCenterDialogue selectCenterDialogue = new SelectCenterDialogue(centerLists,DocLogin.this);
                try {
                    selectCenterDialogue.show(getSupportFragmentManager(),"CENTER");
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }
            }
        }
        else {
            fullLayout.setVisibility(View.VISIBLE);
            circularProgressIndicator.setVisibility(View.GONE);
            loading = false;
            if (conn) {
                if (connected) {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocLogin.this);
                    if (is_available.equals("0")) {
                        alertDialogBuilder.setTitle("Warning!")
                                .setIcon(R.drawable.doc_diary_default)
                                .setMessage(user_message)
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = alertDialogBuilder.create();
                        try {
                            alert.show();
                        }
                        catch (Exception e) {
                            restart("App is paused for a long time. Please Start the app again.");
                        }
                    }
                    else {
                        if (prv_message.isEmpty()) {
                            alertDialogBuilder.setTitle("Warning!")
                                    .setIcon(R.drawable.doc_diary_default)
                                    .setMessage(user_message)
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

                            AlertDialog alert = alertDialogBuilder.create();
                            try {
                                alert.show();
                            }
                            catch (Exception e) {
                                restart("App is paused for a long time. Please Start the app again.");
                            }
                        }
                        else {
                            alertDialogBuilder.setTitle("Warning!")
                                    .setIcon(R.drawable.doc_diary_default)
                                    .setMessage(prv_message)
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

                            AlertDialog alert = alertDialogBuilder.create();
                            try {
                                alert.show();
                            }
                            catch (Exception e) {
                                restart("App is paused for a long time. Please Start the app again.");
                            }
                        }
                    }
                }
                else {
                    fullLayout.setVisibility(View.VISIBLE);
                    circularProgressIndicator.setVisibility(View.GONE);
                    loading = false;
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocLogin.this);
                    if (prv_message.isEmpty()) {
                        alertDialogBuilder.setTitle("Warning!")
                                .setIcon(R.drawable.doc_diary_default)
                                .setMessage("There is a network issue in the server. Please Try later.")
                                .setPositiveButton("Retry", (dialog, which) -> {
                                    loginCheck();
                                    dialog.dismiss();
                                });

                        AlertDialog alert = alertDialogBuilder.create();
                        try {
                            alert.show();
                        }
                        catch (Exception e) {
                            restart("App is paused for a long time. Please Start the app again.");
                        }
                    }
                    else {
                        alertDialogBuilder.setTitle("Warning!")
                                .setIcon(R.drawable.doc_diary_default)
                                .setMessage(prv_message)
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = alertDialogBuilder.create();
                        try {
                            alert.show();
                        }
                        catch (Exception e) {
                            restart("App is paused for a long time. Please Start the app again.");
                        }
                    }

                }
            }
            else {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                loading = false;
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocLogin.this);
                if (prv_message.isEmpty()) {
                    alertDialogBuilder.setTitle("Warning!")
                            .setIcon(R.drawable.doc_diary_default)
                            .setMessage("Server Problem or Internet Not Connected")
                            .setPositiveButton("Retry", (dialog, which) -> {
                                loginCheck();
                                dialog.dismiss();
                            });

                    AlertDialog alert = alertDialogBuilder.create();
                    try {
                        alert.show();
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
                else {
                    alertDialogBuilder.setTitle("Warning!")
                            .setIcon(R.drawable.doc_diary_default)
                            .setMessage(prv_message)
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

                    AlertDialog alert = alertDialogBuilder.create();
                    try {
                        alert.show();
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
            }
        }
    }

    public void updateFLFlag() {
        conn = false;
        connected = false;

        String flagUpdateUrl = center_api+"login/updateFLFlag";
        RequestQueue requestQueue = Volley.newRequestQueue(DocLogin.this);
        StringRequest flagUpdateReq = new StringRequest(Request.Method.POST, flagUpdateUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateInterface();
                }
                else {
                    System.out.println(string_out);
                    connected = false;
                    updateInterface();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                connected = false;
                updateInterface();
            }
        }, error -> {
            error.printStackTrace();
            conn = false;
            connected = false;
            updateInterface();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_DOC_CODE", center_doc_code);
                return headers;
            }
        };

        requestQueue.add(flagUpdateReq);
    }

    private void updateInterface() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        loading = false;
        if (conn) {
            if (connected) {
                SharedPreferences.Editor editor = sharedLoginRemember.edit();
                if (checkBox.isChecked()) {
                    editor.remove(user_mobile_remember);
                    editor.remove(user_password_remember);
                    editor.remove(checked);
                    editor.putString(user_mobile_remember, user_mobile);
                    editor.putString(user_password_remember,user_password);
                    editor.putBoolean(checked,true);
                    editor.apply();
                    editor.commit();
                }
                else {
                    editor.remove(user_mobile_remember);
                    editor.remove(user_password_remember);
                    editor.remove(checked);
                    editor.apply();
                    editor.commit();
                }
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.remove(DOC_USER_CODE);
                editor1.remove(DOC_USER_PASSWORD);
                editor1.remove(DOC_DATA_API);
                editor1.remove(DOC_ALL_ID);
                editor1.remove(LOGIN_TF);

                editor1.putString(DOC_USER_CODE, center_doc_code);
                editor1.putString(DOC_USER_PASSWORD, user_password);
                editor1.putString(DOC_DATA_API, center_api);
                editor1.putString(DOC_ALL_ID, json);
                editor1.putBoolean(LOGIN_TF, true);
                editor1.apply();
                editor1.commit();

                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DocLogin.this, DocDashboard.class);
                startActivity(intent);
                finish();
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

    @Override
    public void onDismiss() {
        updateFLFlag();
    }

    @Override
    public void onIdDismiss() {
        updateFLFlag();
    }

    @Override
    public void closeCallDismiss() {
        fullLayout.setVisibility(View.VISIBLE);
        circularProgressIndicator.setVisibility(View.GONE);
        loading = false;
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