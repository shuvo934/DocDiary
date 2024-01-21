package ttit.com.shuvo.docdiary.profile;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.ComplainModify;
import ttit.com.shuvo.docdiary.profile.arraylists.ThanaList;

public class UpdateAddress extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    TextInputLayout streetAddressLay;
    TextInputEditText streetAddress;
    TextInputLayout postOfficeLay;
    TextInputEditText postOffice;
    TextInputLayout thanaLay;
    AppCompatAutoCompleteTextView thanaSpinner;
    TextInputEditText district;

    ImageView close;

    Button update;
    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";
    boolean loading = false;

    String doc_id = "";

    String street_address = "";
    String post_office = "";
    String thana_name = "";
    String dd_id = "";
    String district_name = "";

    ArrayList<ThanaList> thanaLists;
    boolean selectedFromItems = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(UpdateAddress.this,R.color.clouds));
        setContentView(R.layout.activity_update_address);

        fullLayout = findViewById(R.id.doc_update_address_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_doc_profile_update_address);
        circularProgressIndicator.setVisibility(View.GONE);

        close = findViewById(R.id.close_logo_of_doc_address_update);

        streetAddressLay = findViewById(R.id.street_address_text_layout);
        streetAddress = findViewById(R.id.street_address_text);
        postOfficeLay = findViewById(R.id.post_office_text_layout);
        postOffice = findViewById(R.id.post_office_text);
        thanaLay = findViewById(R.id.thana_upazilla_text_layout);
        thanaSpinner = findViewById(R.id.thana_upazilla_text);
        district = findViewById(R.id.district_text_doc_profile);

        update = findViewById(R.id.doc_edit_address_button);

        if (userInfoLists == null) {
            restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (userInfoLists.size() == 0) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            }
            else {
                doc_id = userInfoLists.get(0).getDoc_id();
            }
        }
        thanaLists = new ArrayList<>();

        Intent intent = getIntent();
        street_address = intent.getStringExtra("STREET");
        post_office = intent.getStringExtra("POST_OFF");
        thana_name = intent.getStringExtra("THANA_NAME");
        dd_id = intent.getStringExtra("DD_ID");
        district_name = intent.getStringExtra("DISTRICT");

        streetAddress.setText(street_address);
        postOffice.setText(post_office);
        thanaSpinner.setText(thana_name);
        district.setText(district_name);

        close.setOnClickListener(v -> onBackPressed());

        streetAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    streetAddress.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        streetAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    streetAddressLay.setHelperText("");
                }
                else {
                    streetAddressLay.setHelperText("Please Provide Street Address/Village");
                }
            }
        });

        streetAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = Objects.requireNonNull(streetAddress.getText()).toString();
                if (ss.isEmpty()) {
                    streetAddressLay.setHelperText("Please Provide Street Address/Village");
                }
                else {
                    streetAddressLay.setHelperText("");
                }
            }
        });

        postOffice.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    postOffice.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        postOffice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    postOfficeLay.setHelperText("");
                }
                else {
                    postOfficeLay.setHelperText("Please Provide Post Office");
                }
            }
        });

        postOffice.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = Objects.requireNonNull(postOffice.getText()).toString();
                if (ss.isEmpty()) {
                    postOfficeLay.setHelperText("Please Provide Post Office");
                }
                else {
                    postOfficeLay.setHelperText("");
                }
            }
        });

        thanaSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    thanaLay.setHelperText("");
                }
            }
        });

        thanaSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String name = parent.getItemAtPosition(position).toString();
            System.out.println(position+": "+name);
            dd_id = "";
            for (int i = 0; i <thanaLists.size(); i++) {
                if (name.equals(thanaLists.get(i).getDd_thana_name())) {
                    dd_id = thanaLists.get(i).getDd_id();
                    thana_name = thanaLists.get(i).getDd_thana_name();
                    district_name = thanaLists.get(i).getDist_name();
                }
            }

            district.setText(district_name);
            selectedFromItems = true;
            thanaLay.setHelperText("");
            System.out.println("thana_name: " + thana_name);
            System.out.println("dd_id: "+ dd_id);
            closeKeyBoard();
        });

        thanaSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String ss = thanaSpinner.getText().toString();
                if (!selectedFromItems) {
                    dd_id = "";
                    for (int i = 0; i < thanaLists.size(); i++) {
                        if (ss.equals(thanaLists.get(i).getDd_thana_name())) {
                            dd_id = thanaLists.get(i).getDd_id();
                            thana_name = thanaLists.get(i).getDd_thana_name();
                            district_name = thanaLists.get(i).getDist_name();
                        }
                    }
                    if (dd_id.isEmpty()) {

                        if (ss.isEmpty()) {
                            thanaLay.setHelperText("Please Select Thana/Upazila");
                            district.setText("");
                        }
                        else {
                            thanaLay.setHelperText("Invalid Thana/Upazila");
                            district.setText("");
                        }
                    }
                    else {
                        district.setText(district_name);
                    }
                    System.out.println("thana_name: " + thana_name);
                    System.out.println("dd_id: "+ dd_id);
                }
                else {
                    selectedFromItems = false;
                    System.out.println("thana_name: " + thana_name);
                    System.out.println("dd_id: "+ dd_id);
                }
            }
        });

        thanaSpinner.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    thanaSpinner.clearFocus();
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        update.setOnClickListener(v -> {
            street_address = Objects.requireNonNull(streetAddress.getText()).toString();
            post_office = Objects.requireNonNull(postOffice.getText()).toString();

            if (!street_address.isEmpty()) {
                if (!post_office.isEmpty()) {
                    if (!dd_id.isEmpty()) {
                        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(UpdateAddress.this);
                        alertDialogBuilder.setTitle("Update Address!")
                                .setMessage("Do you want to update your address?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    dialog.dismiss();
                                    updateAddress();
                                })
                                .setNegativeButton("No",(dialog, which) -> {
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
                        Toast.makeText(getApplicationContext(),"Please Select Thana/Upazila",Toast.LENGTH_SHORT).show();
                        thanaLay.setHelperText("Please Select Thana/Upazila");
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please Provide Post Office",Toast.LENGTH_SHORT).show();
                    postOfficeLay.setHelperText("Please Provide Post Office");
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Please Provide Street Address/Village",Toast.LENGTH_SHORT).show();
                streetAddressLay.setHelperText("Please Provide Street Address/Village");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getThanas();
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
    public void onBackPressed() {
        if (loading) {
            Toast.makeText(getApplicationContext(),"Please wait while loading",Toast.LENGTH_SHORT).show();
        }
        else {
            super.onBackPressed();
        }
    }

    public void getThanas() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;
        thanaLists = new ArrayList<>();

        String url = pre_url_api+"doc_profile/getDocThana";

        RequestQueue requestQueue = Volley.newRequestQueue(UpdateAddress.this);

        StringRequest docDataReq = new StringRequest(Request.Method.GET, url, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject docInfo = array.getJSONObject(i);

                        String n_dd_id = docInfo.getString("dd_id")
                                .equals("null") ? "" : docInfo.getString("dd_id");

                        String n_dd_dist_id = docInfo.getString("dd_dist_id")
                                .equals("null") ? "" : docInfo.getString("dd_dist_id");

                        String n_dd_thana_name = docInfo.getString("dd_thana_name")
                                .equals("null") ? "" : docInfo.getString("dd_thana_name");

                        String n_dist_name = docInfo.getString("dist_name")
                                .equals("null") ? "" : docInfo.getString("dist_name");

                        thanaLists.add(new ThanaList(n_dd_id,n_dd_dist_id,n_dd_thana_name,n_dist_name));

                    }
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

        requestQueue.add(docDataReq);
    }

    private void updateInterface() {
        loading = false;
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                if (thanaLists.size() == 0) {
                    thanaLay.setHelperText("No Thana/Upazila Found");
                }
                else {
                    thanaLay.setHelperText("");
                }

                ArrayList<String> type = new ArrayList<>();
                for(int i = 0; i < thanaLists.size(); i++) {
                    type.add(thanaLists.get(i).getDd_thana_name());
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UpdateAddress.this,R.layout.dropdown_menu_popup_item,R.id.drop_down_item,type);
                thanaSpinner.setAdapter(arrayAdapter);

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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(UpdateAddress.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    getThanas();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
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

    public void updateAddress() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String updateAddsUrl = pre_url_api+"doc_profile/updateAddress";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, updateAddsUrl, response -> {
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
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateAfterUpdate();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateAfterUpdate();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_S_ADDRESS",street_address);
                headers.put("P_POST_OFFICE",post_office);
                headers.put("P_DD_ID",dd_id);
                headers.put("P_DOC_ID",doc_id);
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
                conn = false;
                connected = false;

                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(UpdateAddress.this);
                alertDialogBuilder.setTitle("Success!")
                        .setMessage("Address Updated Successfully.")
                        .setPositiveButton("Ok", (dialog, which) -> {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(UpdateAddress.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    updateAddress();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
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