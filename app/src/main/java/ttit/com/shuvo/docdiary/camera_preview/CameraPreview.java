package ttit.com.shuvo.docdiary.camera_preview;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ttit.com.shuvo.docdiary.R;

public class CameraPreview extends AppCompatActivity {

    private static final String KEY_CAMERA_PERMISSION_PENDING = "camera_permission_pending";

    private boolean cameraPermissionPending = false;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                cameraPermissionPending = false;

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (granted) {
                    startCamera();
                } else {
                    showCameraPermissionDialog();
                }
            });

    private final ActivityResultLauncher<Intent> cameraSettingsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                cameraPermissionPending = false;

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                // Settings does not reliably return RESULT_OK. Check the permission.
                if (hasCameraPermission()) {
                    startCamera();
                } else {
                    Toast.makeText(this, "Camera permission was not granted.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    PreviewView cameraView;

    RelativeLayout cameraStateLayout;
    ImageView cameraSwitch;
    boolean isBackCamera = true;

    ImageView cameraFlash;
    boolean isFlashOn = false;

    ImageView cameraClick;

    RelativeLayout imagePreviewLayout;
    ImageView picPreview;
    ImageView picOk;
    ImageView picCancel;

    Bitmap finalBitmap;

    ImageCapture imageCapture;
    Camera camera;
    ExecutorService cameraExecutor;

    private static BitmapCallBack bitmapCallback;

    public static void setBitmapCallback(BitmapCallBack callback) {
        bitmapCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            cameraPermissionPending = savedInstanceState.getBoolean(KEY_CAMERA_PERMISSION_PENDING, false);
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera_preview);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // initialization
        cameraView = findViewById(R.id.camera_view_fp);
        cameraView.setVisibility(View.VISIBLE);

        cameraStateLayout = findViewById(R.id.image_capture_state_layout);
        cameraStateLayout.setVisibility(View.VISIBLE);

        cameraSwitch = findViewById(R.id.camera_switch);
        cameraFlash = findViewById(R.id.camera_flash_button);

        cameraClick = findViewById(R.id.camera_click);
        cameraClick.setVisibility(View.VISIBLE);

        imagePreviewLayout = findViewById(R.id.image_preview_layout);
        imagePreviewLayout.setVisibility(View.GONE);

        picPreview = findViewById(R.id.saved_picture_preview);
        picOk = findViewById(R.id.picture_check_ok);
        picCancel = findViewById(R.id.picture_check_cancel);

        cameraSwitch.setOnClickListener(view -> {
            isBackCamera = !isBackCamera;

            isFlashOn = false;
            cameraFlash.setImageResource(R.drawable.flash_off_24);

            startCamera();
        });

        cameraFlash.setOnClickListener(view -> {
            if (camera == null || imageCapture == null) {
                return;
            }

            if (!isBackCamera) {
                Toast.makeText(this, "Flash is not available for front camera.", Toast.LENGTH_SHORT).show();
                return;
            }

            isFlashOn = !isFlashOn;
            cameraFlash.setImageResource(isFlashOn ? R.drawable.flash_on_24 : R.drawable.flash_off_24);

            imageCapture.setFlashMode(
                    isFlashOn ? ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF
            );
        });

        cameraClick.setOnClickListener(view -> capturePhoto());

        picCancel.setOnClickListener(view -> {
            cameraView.setVisibility(View.VISIBLE);
            cameraStateLayout.setVisibility(View.VISIBLE);
            cameraClick.setVisibility(View.VISIBLE);

            imagePreviewLayout.setVisibility(View.GONE);

            // Re-bind camera after returning from preview.
            startCamera();
        });

        picOk.setOnClickListener(view -> {
            if (finalBitmap != null && bitmapCallback != null) {
                bitmapCallback.onBitmapReceived(finalBitmap);
                finish();
            } else {
                Toast.makeText(this, "Image is not available.", Toast.LENGTH_SHORT).show();
            }
        });

        if (hasCameraPermission()) {
            startCamera();
        } else if (!cameraPermissionPending) {
            requestCameraPermission();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

    }

    private void startCamera() {
        if (isFinishing() || isDestroyed() || !hasCameraPermission()) {
            return;
        }
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(
                                isBackCamera
                                        ? CameraSelector.LENS_FACING_BACK
                                        : CameraSelector.LENS_FACING_FRONT
                        )
                        .build();

                Preview preview = new Preview.Builder()
                        .build();

                preview.setSurfaceProvider(cameraView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setFlashMode(isFlashOn ? ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF)
                        .build();

                cameraProvider.unbindAll();

                camera = cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );

            } catch (Exception e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void capturePhoto() {
        if (imageCapture == null) {
            Toast.makeText(this, "Camera is not ready yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        imageCapture.takePicture(
                cameraExecutor,
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                        try (imageProxy) {
                            Bitmap bitmap = imageProxyToBitmap(imageProxy);

                            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();

                            finalBitmap = rotateAndFlipBitmap(
                                    bitmap,
                                    rotationDegrees,
                                    !isBackCamera
                            );

                            runOnUiThread(() -> {
                                cameraView.setVisibility(View.GONE);
                                cameraStateLayout.setVisibility(View.GONE);
                                cameraClick.setVisibility(View.GONE);

                                imagePreviewLayout.setVisibility(View.VISIBLE);
                                picPreview.setImageBitmap(finalBitmap);
                            });

                        } catch (Exception e) {
                            runOnUiThread(() ->
                                    Toast.makeText(CameraPreview.this, "Failed to capture image.", Toast.LENGTH_SHORT).show()
                            );
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() ->
                                Toast.makeText(CameraPreview.this, exception.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }
        );
    }

    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        if (imageProxy.getPlanes().length == 0) {
            throw new IllegalStateException("Image is not available");
        }

        if (imageProxy.getFormat() == ImageFormat.JPEG) {
            ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

        byte[] nv21 = yuv420ToNv21(imageProxy);

        YuvImage yuvImage = new YuvImage(
                nv21,
                ImageFormat.NV21,
                imageProxy.getWidth(),
                imageProxy.getHeight(),
                null
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        yuvImage.compressToJpeg(
                new Rect(0, 0, imageProxy.getWidth(), imageProxy.getHeight()),
                100,
                outputStream
        );

        byte[] jpegBytes = outputStream.toByteArray();

        return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);
    }

    private byte[] yuv420ToNv21(ImageProxy imageProxy) {
        ImageProxy.PlaneProxy yPlane = imageProxy.getPlanes()[0];
        ImageProxy.PlaneProxy uPlane = imageProxy.getPlanes()[1];
        ImageProxy.PlaneProxy vPlane = imageProxy.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();

        int ySize = width * height;
        int uvSize = width * height / 4;

        byte[] nv21 = new byte[ySize + uvSize * 2];

        int position = 0;

        for (int row = 0; row < height; row++) {
            yBuffer.position(row * yPlane.getRowStride());
            yBuffer.get(nv21, position, width);
            position += width;
        }

        int chromaHeight = height / 2;
        int chromaWidth = width / 2;

        int vRowStride = vPlane.getRowStride();
        int uRowStride = uPlane.getRowStride();
        int vPixelStride = vPlane.getPixelStride();
        int uPixelStride = uPlane.getPixelStride();

        byte[] vBytes = new byte[vBuffer.capacity()];
        byte[] uBytes = new byte[uBuffer.capacity()];

        vBuffer.rewind();
        uBuffer.rewind();

        vBuffer.get(vBytes);
        uBuffer.get(uBytes);

        for (int row = 0; row < chromaHeight; row++) {
            for (int col = 0; col < chromaWidth; col++) {
                int vIndex = row * vRowStride + col * vPixelStride;
                int uIndex = row * uRowStride + col * uPixelStride;

                nv21[position++] = vBytes[vIndex];
                nv21[position++] = uBytes[uIndex];
            }
        }

        return nv21;
    }

    public Bitmap rotateAndFlipBitmap(Bitmap bitmap, int rotation, boolean isFrontCamera) {
        Matrix matrix = new Matrix();
        System.out.println(isFrontCamera);
        // Rotate the image
        if (rotation != 0) {
//            matrix.postRotate(-rotation);
            matrix.postRotate(rotation);
        }

        // Flip for Front Camera (Mirror Effect)
        if (isFrontCamera) {
//            matrix.preScale(1, -1);
            matrix.postScale(-1, 1);
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        if (isFinishing() || isDestroyed() || cameraPermissionPending) {
            return;
        }
        if (hasCameraPermission()) {
            startCamera();
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showCameraPermissionDialog();
        } else {
            launchCameraPermissionRequest();
        }
    }

    private void launchCameraPermissionRequest() {
        if (isFinishing() || isDestroyed() || cameraPermissionPending) {
            return;
        }

        cameraPermissionPending = true;
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    private void showCameraPermissionDialog() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        boolean canRequestAgain = ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.CAMERA);

        String message = canRequestAgain
                ? "Camera permission is needed to take a photo. You can cancel and choose a gallery image instead."
                : "Camera access was not granted. You can open Settings, choose Permissions > Camera, and allow access to take a photo.";

        new MaterialAlertDialogBuilder(this)
                .setTitle("Camera permission")
                .setMessage(message)
                .setPositiveButton(canRequestAgain ? "Try again" : "Open Settings", (dialog, which) -> {
                    if (canRequestAgain) {
                        launchCameraPermissionRequest();
                    } else {
                        openCameraPermissionSettings();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .setOnCancelListener(dialog -> finish())
                .show();
    }

    private void openCameraPermissionSettings() {
        if (isFinishing() || isDestroyed() || cameraPermissionPending) {
            return;
        }

        Intent settingsIntent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));

        cameraPermissionPending = true;

        try {
            cameraSettingsLauncher.launch(settingsIntent);
        } catch (ActivityNotFoundException exception) {
            cameraPermissionPending = false;
            Toast.makeText(this, "Unable to open application settings.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(KEY_CAMERA_PERMISSION_PENDING, cameraPermissionPending);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}