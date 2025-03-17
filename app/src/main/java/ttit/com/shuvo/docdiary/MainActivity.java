package ttit.com.shuvo.docdiary;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.UpdateAvailability;

import ttit.com.shuvo.docdiary.dashboard.DocDashboard;
import ttit.com.shuvo.docdiary.login.DocLogin;

public class MainActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    SharedPreferences sharedPreferences;
    boolean loginfile = false;
    public static final String LOGIN_ACTIVITY_FILE = "LOGIN_ACTIVITY_FILE_DOCDIARY";
    public static final String LOGIN_TF = "TRUE_FALSE";

    AppUpdateManager appUpdateManager;

    boolean perm = false;

    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK) {

                            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this)
                                    .setTitle("Update Failed!")
                                    .setMessage("Failed to update the app. Please try again.")
                                    .setIcon(R.drawable.doc_diary_default)
                                    .setPositiveButton("Retry", (dialog, which) -> getAppUpdate())
                                    .setNegativeButton("Cancel", (dialog, which) -> finish());
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    });

    private ActivityResultLauncher<String> cameraPermResultLauncher;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        sharedPreferences = getSharedPreferences(LOGIN_ACTIVITY_FILE, MODE_PRIVATE);

        loginfile = sharedPreferences.getBoolean(LOGIN_TF,false);

        cameraPermResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(), result -> {
            System.out.println("OnActivityResult: " +result);
            if (result) {
                System.out.println("HOLA5");
                goToActivity();
            }
            else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    showDialog("Camera Permission!", "This app needs the Camera permission to function. Please Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                        startActivity(intent);
                        perm = true;
                    });
                }
                else {
                    System.out.println("HOLA6");
                    enableCameraPermission();
                }
            }
        });

        System.out.println(loginfile);
        perm = false;
        getAppUpdate();

    }

    private void getAppUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE))  {

                appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                        activityResultLauncher, AppUpdateOptions
                                .newBuilder(IMMEDIATE)
                                .build());
            }
            else {
                System.out.println("No update available");
                enableFileAccess();
            }
        });
        appUpdateInfoTask.addOnFailureListener(e -> {
            System.out.println("FAILED TO LISTEN");
            enableFileAccess();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                                        activityResultLauncher,AppUpdateOptions
                                                .newBuilder(IMMEDIATE)
                                                .build());
                            }
                        });
        if (perm) {
            perm = false;
            enableFileAccess();
        }
    }

    private void goToActivity() {
        mHandler.postDelayed(() -> {

            Intent intent;
            if (loginfile) {
                intent = new Intent(MainActivity.this, DocDashboard.class);
            } else {
                intent = new Intent(MainActivity.this, DocLogin.class);
            }
            startActivity(intent);
            showSystemUI();
            finish();
        }, 1000);
    }

    public void showDialog(String title, String message, String positiveButtonTitle, DialogInterface.OnClickListener positiveListener) {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setIcon(R.drawable.doc_diary_default)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonTitle, positiveListener);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void enableFileAccess() {
        if (Build.VERSION.SDK_INT < 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

                Log.i("Ekhane", "1");
                enableCameraPermission();
            }
            else {
                Log.i("Ekhane", "2");
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    Log.i("Ekhane", "3");
                    showDialog("Storage Permission!", "This app needs the storage permission for functioning.", "OK", (dialogInterface, i) -> {
                        storageResultLauncher.launch(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE});
                    });
                }
                else {
                    Log.i("Ekhane", "4");
                    storageResultLauncher.launch(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
            }
        }
//        else if (Build.VERSION.SDK_INT == 33) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
//                    == PackageManager.PERMISSION_GRANTED) {
//
//                Log.i("Ekhane", "5");
//                enableCameraPermission();
//            }
//            else {
//                Log.i("Ekhane", "6");
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_IMAGES)) {
//
//                    Log.i("Ekhane", "7");
//                    showDialog("Photos and Videos Permission!", "This app needs the photos and videos permission for functioning.", "OK", (dialogInterface, i) -> {
//                        storageResultLauncher.launch(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES});
//                    });
//                }
//                else {
//                    Log.i("Ekhane", "8");
//                    storageResultLauncher.launch(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES});
//                }
//            }
//        }
        else {
            enableCameraPermission();
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
//                    == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
//                            == PackageManager.PERMISSION_GRANTED) {
//
//                Log.i("Ekhane", "9");
//                enableCameraPermission();
//            }
//            else {
//                Log.i("Ekhane", "10");
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_IMAGES)
//                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
//
//                    Log.i("Ekhane", "11");
//                    showDialog("Photos and Videos Permission!", "This app needs the photos and videos permission for functioning. Don't Select Limited Access. Please select Allow all.", "OK", (dialogInterface, i) -> {
//                        storageResultLauncher.launch(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES,
//                                android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED});
//                    });
//                }
//                else {
//                    Log.i("Ekhane", "12");
//                    storageResultLauncher.launch(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES,
//                            android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED});
//                }
//            }
        }

    }

    private final ActivityResultLauncher<String[]> storageResultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        System.out.println("OnActivityResult: " +result);
        boolean allGranted = true;
        for (String key: result.keySet()) {
            allGranted = allGranted && result.get(key);
        }
        if (allGranted) {
            System.out.println("HOLA1");
            enableCameraPermission();
        }
        else {
//            if (Build.VERSION.SDK_INT < 33) {
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    showDialog("Storage Permission!", "This app needs the storage permission to function. Please Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
//                        startActivity(intent);
//                        perm = true;
//                    });
//                }
//                else {
//                    System.out.println("HOLA2");
//                    enableFileAccess();
//                }
//            }
//            else if (Build.VERSION.SDK_INT == 33) {
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
//                    showDialog("Photos and Videos Permission!", "This app needs the photos and videos permission to function. Please Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
//                        startActivity(intent);
//                        perm = true;
//                    });
//                }
//                else {
//                    System.out.println("HOLA3");
//                    enableFileAccess();
//                }
//            }
//            else {
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
//                    showDialog("Photos and Videos Permission!", "This app needs the photos and videos permission to function. Don't Select Limited Access. Please Allow all from permission settings.", "Go to Settings", (dialogInterface, i) -> {
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
//                        startActivity(intent);
//                        perm = true;
//                    });
//                }
//                else {
//                    System.out.println("HOLA4");
//                    enableFileAccess();
//                }
//            }
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showDialog("Storage Permission!", "This app needs the storage permission to function. Please Allow that permission from settings.", "Go to Settings", (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ getPackageName()));
                    startActivity(intent);
                    perm = true;
                });
            }
            else {
                System.out.println("HOLA2");
                enableFileAccess();
            }
        }
    });

    private void enableCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            Log.i("Ekhane", "13");
            goToActivity();
        }
        else {
            Log.i("Ekhane", "14");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Log.i("Ekhane", "15");
                showDialog("Camera Permission!", "This app needs the Camera permission for functioning.", "OK", (dialogInterface, i) -> {
                    cameraPermResultLauncher.launch(Manifest.permission.CAMERA);
                });
            }
            else {
                Log.i("Ekhane", "16");
                cameraPermResultLauncher.launch(Manifest.permission.CAMERA);
            }
        }
    }

//    private void enableFileAccess() {
//
//        if (Build.VERSION.SDK_INT >= 34) {
//            int REQUEST_CODE_PERMISSION_STORAGE = 100;
//            String[] permission = {
//                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//            };
//
//            for (String str : permission) {
//                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
//                    this.requestPermissions(permission, REQUEST_CODE_PERMISSION_STORAGE);
//                    return;
//                }
//            }
//
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                goToActivity();
//            }
//        }
//        else if (Build.VERSION.SDK_INT < 33) {
//            int REQUEST_CODE_PERMISSION_STORAGE = 100;
//            String[] permission = {
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//            };
//
//            for (String str : permission) {
//                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
//                    this.requestPermissions(permission, REQUEST_CODE_PERMISSION_STORAGE);
//                    return;
//                }
//            }
//
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                goToActivity();
//            }
//        }
//        else  {
//            int REQUEST_CODE_PERMISSION_STORAGE = 100;
//            String[] permission = {
//                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//            };
//
//            for (String str : permission) {
//                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
//                    this.requestPermissions(permission, REQUEST_CODE_PERMISSION_STORAGE);
//                    return;
//                }
//            }
//
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                goToActivity();
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 100) {
//            if (grantResults.length > 0 &&
//                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//
//                goToActivity();
//                // Permission is granted. Continue the action or workflow
//                // in your app.
//            }
//            else {
//                Toast.makeText(this, "Please Give the Permission to Access Your File for Using This App", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}