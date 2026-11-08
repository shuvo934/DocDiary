package ttit.com.shuvo.docdiary.login;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dashboard.DocDashboard;
import ttit.com.shuvo.docdiary.login.arraylists.AllUrlList;
import ttit.com.shuvo.docdiary.login.arraylists.CenterList;
import ttit.com.shuvo.docdiary.login.arraylists.MultipleUserList;
import ttit.com.shuvo.docdiary.login.dialogue.SelectCenterDialogue;
import ttit.com.shuvo.docdiary.login.dialogue.SelectUserIdDialogue;
import ttit.com.shuvo.docdiary.login.interfaces.AdminCallBackListener;
import ttit.com.shuvo.docdiary.login.interfaces.AdminIDCallbackListener;
import ttit.com.shuvo.docdiary.login.interfaces.CallBackListener;
import ttit.com.shuvo.docdiary.login.interfaces.CloseCallBack;
import ttit.com.shuvo.docdiary.login.interfaces.IDCallbackListener;
import ttit.com.shuvo.docdiary.utilities.EdgeToEdgeHelper;

public class DocLogin extends AppCompatActivity implements CallBackListener, IDCallbackListener, CloseCallBack, AdminIDCallbackListener, AdminCallBackListener {

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
    SharedPreferences sharedpreferences;
    SharedPreferences sharedLoginRemember;

    public static final String LOGIN_ACTIVITY_FILE = "LOGIN_ACTIVITY_FILE_DOCDIARY";
    public static final String LOGIN_TF = "TRUE_FALSE";
    public static final String DOC_USER_CODE = "DOC_USER_CODE";
    public static final String DOC_USER_PASSWORD = "DOC_USER_PASSWORD";
    public static final String DOC_DATA_API = "DOC_DATA_API";
    public static final String DOC_ALL_ID = "DOC_ALL_ID";
    public static final String ADMIN_OR_USER_FLAG = "ADMIN_OR_USER_FLAG";
    public static final String ADMIN_USR_ID = "ADMIN_USR_ID";

    public static final String MyPREFERENCES = "UserPass";
    public static final String user_mobile_remember = "nameKey";
    public static final String user_password_remember = "passKey";
    public static final String checked = "trueFalse";

    String getUserMobile = "";
    String getPassword = "";
    boolean getChecked = false;

    public TextView centerName;
    public static String center_api = "";
    public static String center_admin_user_id = "";
    public static String user_or_admin_flag = "";
    String user_center_name = "";
    ArrayList<CenterList> centerLists;
    boolean loading = false;
    Gson gson = new Gson();
    String json = "";
    Logger logger = Logger.getLogger(DocLogin.class.getName());

    private long lastClickTime = 0L;

    private boolean isFastClick() {
        long now = System.currentTimeMillis();
        if (now - lastClickTime < 500) {
            return true;
        }
        lastClickTime = now;
        return false;
    }

    private final Object LOGIN_TAG = new Object();
    private final Object SERVER_LIST_TAG = new Object();
    private final Object FLAG_UPDATE_TAG = new Object();

    // One Activity-owned queue for server discovery, login and flag updates.
    private RequestQueue loginQueue;
    private int loginGeneration = 0;
    private static final int MAX_LOGIN_THREADS = 24;
    private static final int LOGIN_TIMEOUT_MS = 15000;
    private static final int SERVER_LIST_TIMEOUT_MS = 60000;
    private static final int FLAG_TIMEOUT_MS = 15000;

    // These fields describe the whole login attempt, not the last server.
    private String loginFailureMessage = "Could not find your id";
    private boolean loginFailureCanRetry = false;

    private boolean uiResumed = false;
    private Runnable pendingUiAction;

    private RequestQueue getLoginQueue() {
        if (loginQueue == null) {
            loginQueue = new RequestQueue(new NoCache(),
                    new BasicNetwork(new HurlStack()), MAX_LOGIN_THREADS);
            loginQueue.start();
        }
        return loginQueue;
    }

    private void configureRequest(Request<?> request, Object tag, int timeoutMs) {
        request.setTag(tag);
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(
                timeoutMs, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    ArrayList<AllUrlList> urls = new ArrayList<>();
    String text_url = "https://raw.githubusercontent.com/shuvo934/Story/refs/heads/master/docDiaryServers";

    @Override
    protected void onSaveInstanceState(@NonNull Bundle out) {
        super.onSaveInstanceState(out);
        out.putString("u_mobile", user_mobile);
        out.putString("u_pass", user_password);
        out.putString("json", json);
        out.putString("c_api", center_api);
        out.putString("c_doc", center_doc_code);
        out.putString("c_admin", center_admin_user_id);
        out.putString("c_flag", user_or_admin_flag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdgeHelper.enable(this);
        setContentView(R.layout.activity_doc_login);
        EdgeToEdgeHelper.applyInsetsLogin(this,
                findViewById(R.id.login_root),
                findViewById(R.id.login_scroll_view),
                false,
                false);

        if (savedInstanceState != null) {
            user_mobile = savedInstanceState.getString("u_mobile", "");
            user_password = savedInstanceState.getString("u_pass", "");
            json = savedInstanceState.getString("json", "");
            center_api = savedInstanceState.getString("c_api", "");
            center_doc_code = savedInstanceState.getString("c_doc", "");
            center_admin_user_id = savedInstanceState.getString("c_admin", "");
            user_or_admin_flag = savedInstanceState.getString("c_flag", "");
        }

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
                    .setIcon(R.drawable.doc_diary_default)
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
            if (isFastClick() || loading) return;

            user_mobile = Objects.requireNonNull(userMobile.getText()).toString();
            user_password = Objects.requireNonNull(userPassword.getText()).toString();

            if (!urls.isEmpty()) {
                if (!user_mobile.isEmpty() && !user_password.isEmpty()) {
                    for (int i = 0; i < urls.size(); i++) {
                        urls.get(i).setChecked(false);
                    }
                    dynamicLoginCheck();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Give Mobile No and Password", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                AlertDialog dialog = new AlertDialog.Builder(DocLogin.this)
                        .setMessage("Server Error. Please Try again later.")
                        .setPositiveButton("OK", null)
                        .show();

                Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(vv -> dialog.dismiss());
            }

        });

        readApiText();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loading) {
                    Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
                }
                else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DocLogin.this);
                    builder.setTitle("Exit!")
                            .setIcon(R.drawable.doc_diary_default)
                            .setMessage("Do you want to exit?")
                            .setPositiveButton("YES", (dialog, which) -> finish())
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
        });
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

    public void readApiText() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = getLoginQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, text_url, response -> {
            urls = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new StringReader(response))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        urls.add(new AllUrlList(line, false));
                    }
                }
            }
            catch (Exception e) {
                Log.e("READ_API_TEXT", "Could not parse server list", e);
                addFallbackUrls();
            }
            if (urls.isEmpty()) {
                addFallbackUrls();
            }

            for (AllUrlList item : urls) {
                Log.d("SERVER_URL", item.getUrls());
            }
            if (!loading) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
            }

        }, volleyError -> {
            Log.e("READ_API_TEXT", "Could not download server list", volleyError);
            addFallbackUrls();
            if (!loading) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
            }
        });

        configureRequest(stringRequest, SERVER_LIST_TAG, SERVER_LIST_TIMEOUT_MS);
        requestQueue.add(stringRequest);
    }

    private void addFallbackUrls() {
        if (urls == null) {
            urls = new ArrayList<>();
        }
        urls.clear();

        urls.add(new AllUrlList("http://103.56.208.123:8001/apex/cstar_local/",false));
        urls.add(new AllUrlList("http://103.73.227.28:8080/cstar/cstar-ramp/",false));
        urls.add(new AllUrlList("http://202.4.109.126:8003/crps/crp_savar/",false));
        urls.add(new AllUrlList("http://144.48.119.59:8002/apex/crp_mirpur/",false));
        urls.add(new AllUrlList("http://103.73.227.28:8080/cstar/cstar_bsd/",false));
    }

    private void showLoading() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        loading = true;
    }

    public void dynamicLoginCheck() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        user_message = "";
        conn = false;
        connected = false;
        loading = true;
        is_available = "";
        json = "";
        loginFailureMessage = "Could not find your id";
        loginFailureCanRetry = false;
        pendingUiAction = null;
        centerLists = new ArrayList<>();
        center_api = "";
        center_doc_code = "";
        center_admin_user_id = "";
        user_or_admin_flag = "";
        user_center_name = "";
//        System.out.println("START");

        checkToGetLoginData();
    }

    public void checkToGetLoginData() {
        if (isFinishing() || isDestroyed()) return;

        final int generation = ++loginGeneration;
        RequestQueue queue = getLoginQueue();
        queue.cancelAll(LOGIN_TAG);

        final int total = urls.size();
        if (total == 0) {
            loginFailureMessage = "Could not load the center list. Please try again.";
            loginFailureCanRetry = true;
            updateLayout();
            return;
        }

        final String[] responses = new String[total];
        final VolleyError[] errors = new VolleyError[total];
        final boolean[] isError = new boolean[total];
        final int[] finished = {0};
        final String mobile = user_mobile;
        final String password = user_password;

        for (int i = 0; i < total; i++) {
            final int index = i;
            String useridUrl = Uri.parse(urls.get(i).getUrls()
                            + "login/getAllLoginData")
                    .buildUpon()
                    .appendQueryParameter("doc_code", mobile)
                    .appendQueryParameter("doc_password", password)
                    .build()
                    .toString();

            StringRequest req = new StringRequest(Request.Method.GET, useridUrl, response -> {
                if (generation != loginGeneration || isFinishing() || isDestroyed()) return;
                responses[index] = response;
                finished[0]++;
                if (finished[0] == total) processAllInOrder(responses, errors, isError);
            }, error -> {
                if (generation != loginGeneration || isFinishing() || isDestroyed()) return;
                isError[index] = true;
                errors[index] = error;
                finished[0]++;
                if (finished[0] == total) processAllInOrder(responses, errors, isError);
            });
            configureRequest(req, LOGIN_TAG, LOGIN_TIMEOUT_MS);
            queue.add(req);
        }
    }

    // Replay in server-list order and aggregate every server's outcome.
    private void processAllInOrder(String[] responses, VolleyError[] errors, boolean[] isError) {
        int validResponses = 0;
        int failedResponses = 0;
        String statusZeroMessage = "";
        String otherMessage = "";

        for (int index = 0; index < responses.length; index++) {
            if (isError[index]) {
                handleLoginError(index, errors[index]);
                failedResponses++;
            } else if (handleLoginResponse(index, responses[index])) {
                validResponses++;
                String message = cleanServerMessage(user_message);
                if (!message.isEmpty()) {
                    if ("0".equals(is_available)) {
                        statusZeroMessage = message;
                    } else {
                        otherMessage = message;
                    }
                }
            } else {
                failedResponses++;
            }
        }


        if (!statusZeroMessage.isEmpty()) {
            loginFailureCanRetry = failedResponses > 0;
            loginFailureMessage = statusZeroMessage;

            if (loginFailureCanRetry) {
                loginFailureMessage +=
                        "\n\nSome centers could not be checked. Please retry.";
            }
        } else if (validResponses == 0) {
            loginFailureMessage = "Unable to check the centers. Please check your internet connection or try again.";
            loginFailureCanRetry = true;
        } else if (failedResponses > 0) {
            loginFailureMessage = "Could not check all centers. Please try again.";
            loginFailureCanRetry = true;
        } else {
            loginFailureMessage = !otherMessage.isEmpty() ? otherMessage : "Could not find your id";
            loginFailureCanRetry = false;
        }
        updateLayout();
    }

    private String cleanServerMessage(String message) {
        if (message == null) return "";
        String trimmed = message.trim();
        return "null".equalsIgnoreCase(trimmed) ? "" : trimmed;
    }

    private void handleLoginError(int index, VolleyError error) {
        // Do not log the full request URL: it contains credentials.
        Log.w("DOC_LOGIN", "Center check failed at index " + index
                + ": " + (error == null ? "unknown error" : error.getClass().getSimpleName()));
        urls.get(index).setChecked(true);
    }

    private boolean handleLoginResponse(int index, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            user_message = jsonObject.getString("is_online");
            is_available = jsonObject.getString("is_available");
            center_doc_code = jsonObject.getString("p_doc_code");
            center_api = jsonObject.getString("p_center_api");
            user_or_admin_flag = jsonObject.getString("p_admin_user_flag");
            center_admin_user_id = jsonObject.getString("p_admin_user_id");
            user_center_name = jsonObject.getString("p_center_name");

            switch (is_available) {
                case "1":
                    centerLists.add(new CenterList(user_center_name, center_api, center_doc_code,
                            center_admin_user_id, user_or_admin_flag, new ArrayList<>()));
                    break;
                case "0":
                    // Its message is collected by processAllInOrder().
                    break;
                case "3": {
                    JSONArray array = new JSONArray(jsonObject.getString("p_doc_list"));
                    ArrayList<MultipleUserList> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject d = array.getJSONObject(i);
                        String dc = d.getString("doc_code").equals("null") ? "" : d.getString("doc_code");
                        String dn = d.getString("doc_name").equals("null") ? "" : d.getString("doc_name");
                        String dep = d.getString("depts_name").equals("null") ? "" : d.getString("depts_name");
                        list.add(new MultipleUserList(dc, dn, dep, user_or_admin_flag, "", "", "", ""));
                    }
                    centerLists.add(new CenterList(user_center_name, center_api, center_doc_code,
                            center_admin_user_id, user_or_admin_flag, list));
                    break;
                }
                case "4": {
                    JSONArray arr = new JSONArray(jsonObject.getString("p_admin_list"));
                    ArrayList<MultipleUserList> list = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject a = arr.getJSONObject(i);
                        String id = a.getString("usr_id").equals("null") ? "" : a.getString("usr_id");
                        String nm = a.getString("usr_name").equals("null") ? "" : a.getString("usr_name");
                        String fn = a.getString("usr_fname").equals("null") ? "" : a.getString("usr_fname");
                        String ln = a.getString("usr_lname").equals("null") ? "" : a.getString("usr_lname");
                        list.add(new MultipleUserList("", "", "", user_or_admin_flag, id, fn, ln, nm));
                    }
                    centerLists.add(new CenterList(user_center_name, center_api, center_doc_code,
                            center_admin_user_id, user_or_admin_flag, list));
                    break;
                }
                case "5": {
                    JSONArray adminArr = new JSONArray(jsonObject.getString("p_admin_list"));
                    JSONArray docArr = new JSONArray(jsonObject.getString("p_doc_list"));
                    ArrayList<MultipleUserList> list = new ArrayList<>();
                    for (int i = 0; i < adminArr.length(); i++) {
                        JSONObject a = adminArr.getJSONObject(i);
                        String id = a.getString("usr_id").equals("null") ? "" : a.getString("usr_id");
                        String nm = a.getString("usr_name").equals("null") ? "" : a.getString("usr_name");
                        String fn = a.getString("usr_fname").equals("null") ? "" : a.getString("usr_fname");
                        String ln = a.getString("usr_lname").equals("null") ? "" : a.getString("usr_lname");
                        list.add(new MultipleUserList("", "", "", "2", id, fn, ln, nm));
                    }
                    for (int i = 0; i < docArr.length(); i++) {
                        JSONObject d = docArr.getJSONObject(i);
                        String dc = d.getString("doc_code").equals("null") ? "" : d.getString("doc_code");
                        String dn = d.getString("doc_name").equals("null") ? "" : d.getString("doc_name");
                        String dep = d.getString("depts_name").equals("null") ? "" : d.getString("depts_name");
                        list.add(new MultipleUserList(dc, dn, dep, "1", "", "", "", ""));
                    }
                    centerLists.add(new CenterList(user_center_name, center_api, center_doc_code,
                            center_admin_user_id, user_or_admin_flag, list));
                    break;
                }
                default:
                    break;
            }
            urls.get(index).setChecked(true);
            return true;

        } catch (JSONException e) {
            Log.w("DOC_LOGIN", "Invalid center response at index " + index);
            urls.get(index).setChecked(true);
            return false;
        }
    }

    private void updateLayout() {
        runWhenResumed(this::renderLoginResult);
    }

    private void renderLoginResult() {
        if (!centerLists.isEmpty()) {
            if (centerLists.size() == 1) {
                ArrayList<MultipleUserList> multipleUserLists = centerLists.get(0).getMultipleUserLists();
                if (multipleUserLists.isEmpty()) {
                    user_or_admin_flag = centerLists.get(0).getUser_admin_flag();
                    center_doc_code = centerLists.get(0).getDoc_code();
                    center_api = centerLists.get(0).getCenter_api();
                    center_admin_user_id = centerLists.get(0).getAdmin_user_id();
                    json = "";
                    if (user_or_admin_flag.equals("1")) {
                        System.out.println("Center Found : "+centerLists.size());
                        updateFLFlag();
                    }
                    else {
                        updateAdminFLFlag();
                    }
                }
                else {
                    System.out.println("USERs Found : "+multipleUserLists.size());
                    center_api = centerLists.get(0).getCenter_api();
                    String cn = centerLists.get(0).getCenter_name();
                    json = gson.toJson(centerLists);
//                    SelectUserIdDialogue selectUserIdDialogue = new SelectUserIdDialogue(multipleUserLists,DocLogin.this,cn);
                    SelectUserIdDialogue selectUserIdDialogue = SelectUserIdDialogue.newInstance(multipleUserLists, cn);
                    try {
//                        selectUserIdDialogue.show(getSupportFragmentManager(),"USER_CENTER");
                        showDialogSafely(selectUserIdDialogue, "USER_CENTER");
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
                }
            }
            else {
                System.out.println("Multi Center Found : "+centerLists.size());
                json = gson.toJson(centerLists);

//                SelectCenterDialogue selectCenterDialogue = new SelectCenterDialogue(centerLists,DocLogin.this);
                SelectCenterDialogue selectCenterDialogue = SelectCenterDialogue.newInstance(centerLists);
                try {
//                    selectCenterDialogue.show(getSupportFragmentManager(),"CENTER");
                    showDialogSafely(selectCenterDialogue, "CENTER");
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

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DocLogin.this)
                    .setTitle("Warning!")
                    .setIcon(R.drawable.doc_diary_default)
                    .setMessage(loginFailureMessage);

            if (loginFailureCanRetry) {
                builder.setPositiveButton("Retry", (dialog, which) -> {
                    dialog.dismiss();
                    if (urls.isEmpty()) {
                        readApiText();
                    } else {
                        dynamicLoginCheck();
                    }
                });
                builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
            } else {
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            }
            builder.show();
        }
    }

    public void updateFLFlag() {
        showLoading();
        conn = false;
        connected = false;

        String flagUpdateUrl = center_api+"login/updateFLFlag";
        RequestQueue requestQueue = getLoginQueue();
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
                logger.log(Level.WARNING,e.getMessage(),e);
                connected = false;
                updateInterface();
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            conn = false;
            connected = false;
            updateInterface();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_DOC_CODE", center_doc_code);
                return headers;
            }
        };

        configureRequest(flagUpdateReq, FLAG_UPDATE_TAG, FLAG_TIMEOUT_MS);
        requestQueue.add(flagUpdateReq);
    }

    private void updateInterface() {
        runWhenResumed(this::renderFlagResult);
    }

    private void renderFlagResult() {
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
                editor1.remove(ADMIN_OR_USER_FLAG);
                editor1.remove(ADMIN_USR_ID);

                editor1.putString(DOC_USER_CODE, center_doc_code);
                editor1.putString(DOC_USER_PASSWORD, user_password);
                editor1.putString(DOC_DATA_API, center_api);
                editor1.putString(DOC_ALL_ID, json);
                editor1.putBoolean(LOGIN_TF, true);
                editor1.putString(ADMIN_OR_USER_FLAG,user_or_admin_flag);
                editor1.putString(ADMIN_USR_ID,center_admin_user_id);
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
                    for (int i = 0; i < urls.size(); i++) {
                        urls.get(i).setChecked(false);
                    }
                    dynamicLoginCheck();
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
                for (int i = 0; i < urls.size(); i++) {
                    urls.get(i).setChecked(false);
                }
                dynamicLoginCheck();
                dialog.dismiss();
            });
        }
    }

    public void updateAdminFLFlag() {
        showLoading();
        conn = false;
        connected = false;

        String flagUpdateUrl = center_api+"login/updateAdminFLFlag";
        RequestQueue requestQueue = getLoginQueue();
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
                logger.log(Level.WARNING,e.getMessage(),e);
                connected = false;
                updateInterface();
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            conn = false;
            connected = false;
            updateInterface();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_USR_ID", center_admin_user_id);
                return headers;
            }
        };

        configureRequest(flagUpdateReq, FLAG_UPDATE_TAG, FLAG_TIMEOUT_MS);
        requestQueue.add(flagUpdateReq);
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

    @Override
    public void onAdminIdSelection() {
        updateAdminFLFlag();
    }

    @Override
    public void onAdminCenterSelection() {
        updateAdminFLFlag();
    }

    // Keep network work running while paused, but defer dialogs/navigation.
    private void runWhenResumed(Runnable action) {
        if (isFinishing() || isDestroyed()) return;
        if (!uiResumed || getSupportFragmentManager().isStateSaved()) {
            pendingUiAction = action;
            return;
        }
        action.run();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        uiResumed = true;
        Runnable action = pendingUiAction;
        pendingUiAction = null;
        if (action != null) runWhenResumed(action);
    }

    @Override
    protected void onPause() {
        uiResumed = false;
        super.onPause();
    }

    private void showDialogSafely(androidx.fragment.app.DialogFragment dialog, String tag) {
        runWhenResumed(() -> {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag(tag) == null) {
                // Immediate commit prevents duplicate pending show transactions.
                dialog.showNow(fm, tag);
            }
        });
    }

    @Override
    protected void onDestroy() {
        ++loginGeneration;
        uiResumed = false;
        pendingUiAction = null;
        if (loginQueue != null) {
            // The queue belongs only to this Activity; cancel every request type.
            loginQueue.cancelAll(request -> true);
            loginQueue.stop();
            loginQueue = null;
        }
        super.onDestroy();
    }
}