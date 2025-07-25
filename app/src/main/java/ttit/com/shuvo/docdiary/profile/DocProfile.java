package ttit.com.shuvo.docdiary.profile;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;
import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.userInfoLists;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hdodenhof.circleimageview.CircleImageView;
import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.camera_preview.BitmapCallBack;
import ttit.com.shuvo.docdiary.camera_preview.CameraPreview;
import ttit.com.shuvo.docdiary.dashboard.dialogue.ImageTakerChoiceDialog;
import ttit.com.shuvo.docdiary.dashboard.interfaces.PictureChooseListener;

public class DocProfile extends AppCompatActivity implements PictureChooseListener, BitmapCallBack {

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

    Logger logger = Logger.getLogger(DocProfile.class.getName());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.doc_profile_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
            if (userInfoLists.isEmpty()) {
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
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .start(DocProfile.this);
//            ImagePicker.with(this)
//                    .crop()
//                    .compress(1024)
//                    .maxResultSize(1080,1080)
//                    .start(1113);
            ImageTakerChoiceDialog imageTakerChoiceDialog = new ImageTakerChoiceDialog();
            imageTakerChoiceDialog.show(getSupportFragmentManager(),"CH_IMAGE_DOC");
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

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    try {
//                        selectedBitmap = getCorrectlyOrientedBitmap(uri);
////                        selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                        System.out.println("UPLOADED PIC");
//
//                        if (selectedBitmap != null) {
//                            updateUserImage();
//                        }
//                        else {
//                            Toast.makeText(getApplicationContext(),"Invalid image",Toast.LENGTH_SHORT).show();
//                        }
                        startCrop(uri);

                    } catch (Exception e) {
                        logger.log(Level.WARNING,e.getMessage(),e);
                        Toast.makeText(getApplicationContext(),"Failed to upload image",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Failed to get image",Toast.LENGTH_SHORT).show();
                }
            });

    private Bitmap getCorrectlyOrientedBitmap(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) {
                return null;
            }

            // Get real file path (copying file if necessary)
            String realPath = copyFileToInternalStorage(uri);

            // Read EXIF data
            if (realPath != null) {
                return modifyOrientation(bitmap, realPath);
            }
            else {
                return null;
            }
        }
        catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage(),e);
            Toast.makeText(getApplicationContext(),"Failed to upload image",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private String copyFileToInternalStorage(Uri uri) {
        File directory = getFilesDir(); // Internal storage
        File file = new File(directory, "temp_image.jpg");

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return file.getAbsolutePath(); // Now you have the file path

        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage(),e);
            Toast.makeText(getApplicationContext(),"Failed to get image path",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                assert result != null;
//                Uri resultUri = result.getUri();
//                try {
//                    selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
//                    System.out.println("UPLOADED PIC");
//
//                    updateUserImage();
//
//                } catch (IOException e) {
//                    logger.log(Level.WARNING,e.getMessage(),e);
//                }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                assert result != null;
//                Exception error = result.getError();
//                logger.log(Level.WARNING,error.getMessage(),error);
//            }
//        }
        if (requestCode == 1113) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                if (data != null) {
                    Uri resultUri = data.getData();
                    try {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                        System.out.println("UPLOADED PIC");

                        updateUserImage();

                    } catch (IOException e) {
                        logger.log(Level.WARNING,e.getMessage(),e);
                        Toast.makeText(getApplicationContext(),"Failed to upload image",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Failed to get image",Toast.LENGTH_SHORT).show();
            }
        }
        else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                Uri croppedUri = UCrop.getOutput(data);
                selectedBitmap = getCorrectlyOrientedBitmap(croppedUri);
                if (selectedBitmap != null) {
                    updateUserImage();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Invalid image",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),"Failed to crop image",Toast.LENGTH_SHORT).show();
            }
        }
        else if (resultCode == UCrop.RESULT_ERROR) {
//            if (data != null) {
//                Throwable error = UCrop.getError(data);
//                Toast.makeText(getApplicationContext(), error != null ? error.getLocalizedMessage() : "Image handler failed", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(getApplicationContext(),"Failed to crop image",Toast.LENGTH_SHORT).show();
//            }
            Toast.makeText(getApplicationContext(), "Invalid Image", Toast.LENGTH_SHORT).show();
        }
    }

    public void getDocData() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        conn = false;
        connected = false;
        loading = true;

        String url = pre_url_api+"doc_profile/getDocProfile?doc_id="+doc_id;

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

                        if (doc_profile_pic.equals("null") || doc_profile_pic.isEmpty()) {
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
                    try {
                        Glide.with(getApplicationContext())
                                .load(bitmap)
                                .fitCenter()
                                .into(docImage);
                    }
                    catch (Exception e) {
                        restart("App is paused for a long time. Please Start the app again.");
                    }
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

    public Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight); // Maintain aspect ratio

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    public byte[] compressBitmap(Bitmap bitmap, int maxSizeKB) {
        int quality = 100; // Start at highest quality
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        do {
            outputStream.reset(); // Clear the stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            quality -= 5; // Reduce quality in steps of 5
        } while (outputStream.toByteArray().length / 1024 > maxSizeKB && quality > 5);

        return outputStream.toByteArray();
    }

    public void updateUserImage() {
        String url = pre_url_api+"doc_profile/updateImage";
        conn = false;
        connected = false;
        loading = true;
        circularProgressIndicator.setVisibility(View.VISIBLE);
        fullLayout.setVisibility(View.GONE);

        selectedBitmap = resizeBitmap(selectedBitmap, 1080,1080);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//        int quality = 30;
//        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
//        byte[] bArray = bos.toByteArray();
//        long lengthbmp1 = bArray.length;
//        lengthbmp1 = (lengthbmp1/1024);
//
//        if (lengthbmp1 > 100) {
//            quality = quality - 10;
//            ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
//            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos1);
//            bArray = bos1.toByteArray();
//            lengthbmp1 = bArray.length;
//            lengthbmp1 = (lengthbmp1/1024);
//            System.out.println(lengthbmp1);
//            if (lengthbmp1 > 100) {
//                quality = quality - 10;
//                ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
//                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos2);
//                bArray = bos2.toByteArray();
//                lengthbmp1 = bArray.length;
//                lengthbmp1 = (lengthbmp1/1024);
//                System.out.println(lengthbmp1);
//                if (lengthbmp1 > 100) {
//                    quality = quality - 5;
//                    ByteArrayOutputStream bos3 = new ByteArrayOutputStream();
//                    selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos3);
//                    bArray = bos3.toByteArray();
//                    lengthbmp1 = bArray.length;
//                    lengthbmp1 = (lengthbmp1/1024);
//                    System.out.println(lengthbmp1);
//                    if (lengthbmp1 > 100) {
//                        quality = quality - 3;
//                        ByteArrayOutputStream bos4 = new ByteArrayOutputStream();
//                        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos4);
//                        bArray = bos4.toByteArray();
//                        lengthbmp1 = bArray.length;
//                        lengthbmp1 = (lengthbmp1/1024);
//                        System.out.println(lengthbmp1);
//                    }
//                }
//            }
//        }

        RequestQueue requestQueue = Volley.newRequestQueue(DocProfile.this);

        byte[] finalBArray = compressBitmap(selectedBitmap,1024);
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
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateLayout();
            }
        }, error ->  {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateLayout();
        })
        {
            @Override
            public byte[] getBody() {
                return finalBArray;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
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

                try {
                    Glide.with(getApplicationContext())
                            .load(selectedBitmap)
                            .fitCenter()
                            .into(docImage);
                }
                catch (Exception e) {
                    restart("App is paused for a long time. Please Start the app again.");
                }

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

    @Override
    public void onPictureChoose(int type) {
        if (type == 1) {
            Intent intent = new Intent(this, CameraPreview.class);
            CameraPreview.setBitmapCallback(this);
            startActivity(intent);
        }
        else if (type == 2) {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        File file = new File(context.getCacheDir(), "temp_image.jpg"); // Store in cache
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage(),e);
        }
        return FileProvider.getUriForFile(context, "ttit.com.shuvo.docdiary.fileProvider", file);
    }

    @Override
    public void onBitmapReceived(Bitmap bitmap) {
        Uri uri = getImageUri(this,bitmap);
        startCrop(uri);
    }

    private void startCrop(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

        UCrop.of(sourceUri, destinationUri)
                //.withAspectRatio(1, 1)  // Optional: Set aspect ratio
                .withMaxResultSize(1080, 1080) // Optional: Set max resolution
                .start(this);
    }
}