package ttit.com.shuvo.docdiary.profile;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import ttit.com.shuvo.docdiary.R;

public class DocProfile extends AppCompatActivity {

    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    ImageView backButton;

    CircleImageView docImage;
    ImageView cameraCap;

    TextView docName;
    String doc_name = "";
    TextView docCode;
    String doc_code = "";
    TextView desigName;
    String desig_name = "";
    TextView depName;
    String dep_name = "";
    TextView unitName;
    String unit_name = "";
    TextView docCenter;
    String doc_center_name = "";
    TextView streetAdds;
    String street_adds = "";
    TextView postOff;
    String post_off = "";
    TextView thanaName;
    String thana_name = "";
    TextView districtName;
    String dist_name = "";
    String doc_dd_id = "";
    String dist_id = "";
    TextView mobNum;
    String mob_num = "";
    TextView homePhn;
    String home_phn = "";
    TextView docEmail;
    String doc_email = "";
    String doc_video = "";
    String doc_video_enable_flag = "";
    MaterialButton videoCall;
    MaterialButton chngPass;

    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;
    String parsing_message = "";
    String doc_id = "";

    Bitmap bitmap;
    Bitmap selectedBitmap;
    private boolean imageFound = false;
    boolean onResumeLoad = true;

    ImageView editAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_profile);

        fullLayout = findViewById(R.id.doc_profile_full_layout);
        circularProgressIndicator = findViewById(R.id.progress_indicator_doc_profile);
        circularProgressIndicator.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_logo_of_doc_profile);
        docName = findViewById(R.id.doc_profile_name);
        docCode = findViewById(R.id.doc_profile_code);
        desigName = findViewById(R.id.doc_designation_name);
        depName = findViewById(R.id.doc_department_name);
        unitName = findViewById(R.id.doc_unit_name);
        docCenter = findViewById(R.id.doc_s_center_name_in_profile);
        chngPass = findViewById(R.id.change_password_button);
        videoCall = findViewById(R.id.video_call_button_with_doc);

        streetAdds = findViewById(R.id.doc_street_address);
        postOff = findViewById(R.id.doc_post_office);
        thanaName = findViewById(R.id.doc_thana_name);
        districtName = findViewById(R.id.doc_district_name);
        mobNum = findViewById(R.id.doc_mobile_no);
        homePhn = findViewById(R.id.doc_home_phn_no);
        docEmail = findViewById(R.id.doc_email_no);

        editAddress = findViewById(R.id.edit_doctor_address_profile);

        docImage = findViewById(R.id.doc_profile_image);
        cameraCap = findViewById(R.id.camera_view);

        if (userInfoLists == null) {
            restart("Could Not Get Doctor Data. Please Restart the App.");
        }
        else {
            if (userInfoLists.size() == 0) {
                restart("Could Not Get Doctor Data. Please Restart the App.");
            }
            else {
                doc_id = userInfoLists.get(0).getDoc_id();
                doc_name = userInfoLists.get(0).getDoc_name();
                doc_code = userInfoLists.get(0).getDoc_code();
                desig_name = userInfoLists.get(0).getDesig_name();
                dep_name = userInfoLists.get(0).getDeptd_name();
                unit_name = userInfoLists.get(0).getDepts_name();
                doc_center_name = userInfoLists.get(0).getDoc_center_name();
                doc_video = userInfoLists.get(0).getDoc_video_link();
                doc_video_enable_flag = userInfoLists.get(0).getDoc_video_link_enable_flag();
            }
        }

        if (doc_video_enable_flag.equals("1")) {
            videoCall.setVisibility(View.VISIBLE);
//            if (doc_video.isEmpty()) {
//                videoCall.setVisibility(View.GONE);
//            }
//            else {
//                videoCall.setVisibility(View.VISIBLE);
//            }
        }
        else {
            videoCall.setVisibility(View.GONE);
        }


        docName.setText(doc_name);
        String ddcc = "("+doc_code+")";
        docCode.setText(ddcc);
        desigName.setText(desig_name);
        depName.setText(dep_name);
        unitName.setText(unit_name);
        if (doc_center_name.isEmpty()) {
            docCenter.setVisibility(View.GONE);
        }
        else {
            docCenter.setVisibility(View.VISIBLE);
            docCenter.setText(doc_center_name);
        }

        backButton.setOnClickListener(v -> finish());

        chngPass.setOnClickListener(v -> {
            onResumeLoad = true;
            Intent intent = new Intent(DocProfile.this, UpdatePassword.class);
            startActivity(intent);
        });

        videoCall.setOnClickListener(v -> {
            onResumeLoad = true;

            if (doc_video.isEmpty()) {
                Toast.makeText(getApplicationContext(),"Video call is not available yet",Toast.LENGTH_SHORT).show();
            }
            else {
                Uri uri = Uri.parse(doc_video); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        cameraCap.setOnClickListener(v -> {
            onResumeLoad = false;
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(DocProfile.this);
        });

        editAddress.setOnClickListener(v -> {
            onResumeLoad = true;
            Intent intent = new Intent(DocProfile.this, UpdateAddress.class);
            intent.putExtra("STREET",street_adds);
            intent.putExtra("POST_OFF",post_off);
            intent.putExtra("THANA_NAME",thana_name);
            intent.putExtra("DD_ID",doc_dd_id);
            intent.putExtra("DISTRICT",dist_name);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onResumeLoad) {
            System.out.println("RESUME");
            if (loading != null) {
                if (!loading) {
                    getDocData();
                }
            }
            else {
                restart("App is paused for a long time. Please Start the app again.");
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                Uri resultUri = result.getUri();
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    System.out.println("UPLOADED PIC");

                    updateUserImage();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                error.printStackTrace();
            }
        }
    }

    public void getDocData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String url = pre_url_api+"doc_profile/getDocProfile?doc_id="+doc_id+"";

        RequestQueue requestQueue = Volley.newRequestQueue(DocProfile.this);

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

                        street_adds = docInfo.getString("doc_village")
                                .equals("null") ? "" : docInfo.getString("doc_village");

                        post_off = docInfo.getString("doc_poffice")
                                .equals("null") ? "" : docInfo.getString("doc_poffice");

                        thana_name = docInfo.getString("dd_thana_name")
                                .equals("null") ? "" : docInfo.getString("dd_thana_name");

                        dist_name = docInfo.getString("dist_name")
                                .equals("null") ? "" : docInfo.getString("dist_name");

                        mob_num = docInfo.getString("doc_cell")
                                .equals("null") ? "" : docInfo.getString("doc_cell");

                        home_phn = docInfo.getString("doc_phone")
                                .equals("null") ? "" : docInfo.getString("doc_phone");

                        doc_email = docInfo.getString("doc_email")
                                .equals("null") ? "" : docInfo.getString("doc_email");

                        doc_dd_id = docInfo.getString("doc_dd_id")
                                .equals("null") ? "" : docInfo.getString("doc_dd_id");

                        dist_id = docInfo.getString("dist_id")
                                .equals("null") ? "" : docInfo.getString("dist_id");

                        String doc_profile_pic = docInfo.optString("doc_profile_pic");

                        if (doc_profile_pic.equals("null") || doc_profile_pic.equals("") ) {
                            System.out.println("NULL IMAGE");
                            imageFound = false;
                        }
                        else {
                            byte[] decodedString = Base64.decode(doc_profile_pic,Base64.DEFAULT);
                            bitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
                            if (bitmap != null) {
                                System.out.println("OK");
                                imageFound = true;
                            }
                            else {
                                System.out.println("NOT OK");
                                imageFound = false;
                            }
                        }

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
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                streetAdds.setText(street_adds);
                postOff.setText(post_off);
                thanaName.setText(thana_name);
                districtName.setText(dist_name);
                mobNum.setText(mob_num);
                homePhn.setText(home_phn);
                docEmail.setText(doc_email);

                if (imageFound) {
                    Glide.with(DocProfile.this)
                            .load(bitmap)
                            .fitCenter()
                            .into(docImage);
                }
                else {
                    docImage.setImageResource(R.drawable.doctor);
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocProfile.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    getDocData();
                    dialog.dismiss();
                })
                .setNegativeButton("Exit",(dialog, which) -> {
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

    public void updateUserImage() {
        String url = pre_url_api+"doc_profile/updateImage";
        conn = false;
        connected = false;
        loading = true;
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int quality = 30;
        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] bArray = bos.toByteArray();
        long lengthbmp1 = bArray.length;
        lengthbmp1 = (lengthbmp1/1024);

        if (lengthbmp1 > 100) {
            quality = quality - 10;
            ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos1);
            bArray = bos1.toByteArray();
            lengthbmp1 = bArray.length;
            lengthbmp1 = (lengthbmp1/1024);
            System.out.println(lengthbmp1);
            if (lengthbmp1 > 100) {
                quality = quality - 10;
                ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos2);
                bArray = bos2.toByteArray();
                lengthbmp1 = bArray.length;
                lengthbmp1 = (lengthbmp1/1024);
                System.out.println(lengthbmp1);
                if (lengthbmp1 > 100) {
                    quality = quality - 5;
                    ByteArrayOutputStream bos3 = new ByteArrayOutputStream();
                    selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos3);
                    bArray = bos3.toByteArray();
                    lengthbmp1 = bArray.length;
                    lengthbmp1 = (lengthbmp1/1024);
                    System.out.println(lengthbmp1);
                    if (lengthbmp1 > 100) {
                        quality = quality - 3;
                        ByteArrayOutputStream bos4 = new ByteArrayOutputStream();
                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos4);
                        bArray = bos4.toByteArray();
                        lengthbmp1 = bArray.length;
                        lengthbmp1 = (lengthbmp1/1024);
                        System.out.println(lengthbmp1);
                    }
                }
            }
        }

        RequestQueue requestQueue = Volley.newRequestQueue(DocProfile.this);

        byte[] finalBArray = bArray;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response ->  {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                if (string_out.equals("Successfully Created")) {
                    System.out.println(string_out);
                    connected = true;
                }
                else {
                    parsing_message = string_out;
                    System.out.println(string_out);
                    connected = false;
                }
                updateLayout();
            }
            catch (JSONException e) {
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateLayout();
            }
        }, error ->  {
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateLayout();
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return finalBArray;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("p_doc_id",doc_id);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/binary";
            }
        };

        requestQueue.add(stringRequest);
    }

    private void updateLayout() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                conn = false;
                connected = false;

                Toast.makeText(getApplicationContext(), "Picture Uploaded", Toast.LENGTH_SHORT).show();

                Glide.with(DocProfile.this)
                        .load(selectedBitmap)
                        .fitCenter()
                        .into(docImage);

                loading = false;
            }
            else {
                updateAlertMessage();
            }
        }
        else {
            updateAlertMessage();
        }
    }

    public void updateAlertMessage() {
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
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(DocProfile.this);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    loading = false;
                    updateUserImage();
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