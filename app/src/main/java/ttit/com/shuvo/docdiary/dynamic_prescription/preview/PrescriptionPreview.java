package ttit.com.shuvo.docdiary.dynamic_prescription.preview;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPreviewData;
import ttit.com.shuvo.docdiary.dynamic_prescription.viewmodel.PrescriptionPreviewViewModel;
import ttit.com.shuvo.docdiary.utilities.EdgeToEdgeHelper;

public class PrescriptionPreview extends AppCompatActivity {

    public static final String EXTRA_RVM_ID = "RVM_ID";

    private ImageView backButton;
    private TextView previewSubtitle;

    private WebView prescriptionWebView;

    private View loadingLayout;
    private MaterialCardView errorCard;
    private TextView errorMessageView;
    private MaterialButton retryButton;

    private MaterialButton downloadButton;
    private MaterialButton printButton;
    private MaterialButton shareButton;

    private PrescriptionPreviewViewModel previewViewModel;

    @NonNull
    private String rvmId = "";

    @NonNull
    private String loadedPrescriptionHtml = "";

    private PrescriptionPreviewData loadedPreviewData;
    private boolean previewHtmlLoaded = false;
    private boolean pdfGenerationRunning = false;

    private File generatedPdfFile;
    private File pendingDownloadPdfFile;

    private final ExecutorService pdfFileExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<String> savePdfLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("application/pdf"),
                    this::savePdfToSelectedLocation
            );

    private interface PdfReadyAction {
        void onPdfReady(@NonNull File pdfFile);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdgeHelper.enable(this);
        setContentView(R.layout.activity_prescription_preview);
        EdgeToEdgeHelper.applyInsets(
                this,
                findViewById(R.id.presc_preview_root),
                false,
                false);

        initializeViews();
        configureWebView();
        configureViewModel();
        setupClickListeners();
        readIntentData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_logo_of_prescription_preview);
        previewSubtitle = findViewById(R.id.tv_prescription_preview_subtitle);
        prescriptionWebView = findViewById(R.id.webview_prescription_preview);
        loadingLayout = findViewById(R.id.layout_prescription_preview_loading);
        errorCard = findViewById(R.id.card_prescription_preview_error);
        errorMessageView = findViewById(R.id.tv_prescription_preview_error);
        retryButton = findViewById(R.id.btn_retry_prescription_preview);
        downloadButton = findViewById(R.id.btn_download_prescription);
        printButton = findViewById(R.id.btn_print_prescription);
        shareButton = findViewById(R.id.btn_share_prescription);

        setActionButtonsEnabled(false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureWebView() {
        WebSettings webSettings = prescriptionWebView.getSettings();

        /*
         * Prescription HTML render করার জন্য JavaScript প্রয়োজন নেই।
         */
        webSettings.setJavaScriptEnabled(false);
        webSettings.setDomStorageEnabled(false);

        /*
         * Local device file/content সরাসরি access করতে পারবে না।
         */
        webSettings.setAllowFileAccess(false);
        webSettings.setAllowContentAccess(false);

        /*
         * A4 prescription mobile screen-এর width অনুযায়ী fit হবে।
         */
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        /*
         * User চাইলে preview zoom করতে পারবে।
         */
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);

        /*
         * HTML-এর original font scaling বজায় রাখবে।
         */
        webSettings.setTextZoom(100);
        webSettings.setDefaultTextEncodingName("UTF-8");

        prescriptionWebView.setWebViewClient(new WebViewClient() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (loadedPreviewData == null) {
                    return;
                }

                previewHtmlLoaded = true;
                updateReadySubtitle();
                setActionButtonsEnabled(true);
            }
        });
    }

    private void configureViewModel() {
        previewViewModel = new ViewModelProvider(this).get(PrescriptionPreviewViewModel.class);
        previewViewModel.getPreviewLoading().observe(this, this::handleLoadingState);

        previewViewModel.getPreviewError().observe(this, errorMessage -> {
            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                return;
            }

            showErrorState(errorMessage);
        });

        previewViewModel.getPreviewData().observe(this, previewData -> {
            if (previewData == null) {
                return;
            }

            loadedPreviewData = previewData;
            showPreviewDataLoadedState();
        });
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(view -> finish());

        retryButton.setOnClickListener(view -> {
            if (rvmId.isEmpty()) {
                showErrorState("Prescription information is not available.");
                return;
            }

            previewViewModel.reloadPreview(rvmId);
        });

        downloadButton.setOnClickListener(view -> downloadPrescriptionPdf());

        printButton.setOnClickListener(view -> printPrescription());

        shareButton.setOnClickListener(view -> sharePrescriptionPdf());
    }

    private void readIntentData() {
        Intent intent = getIntent();

        if (intent != null) {
            rvmId = clean(intent.getStringExtra(EXTRA_RVM_ID));
        }

        if (rvmId.isEmpty()) {
            showErrorState("Prescription ID was not provided.");
            return;
        }

        previewViewModel.loadPreviewIfNeeded(rvmId);
    }

    @SuppressLint("SetTextI18n")
    private void handleLoadingState(Boolean loading) {
        boolean isLoading = loading != null && loading;

        if (isLoading) {
            previewHtmlLoaded = false;
            pdfGenerationRunning = false;

            loadedPreviewData = null;
            loadedPrescriptionHtml = "";

            generatedPdfFile = null;
            pendingDownloadPdfFile = null;

            loadingLayout.setVisibility(View.VISIBLE);
            errorCard.setVisibility(View.GONE);
            prescriptionWebView.setVisibility(View.GONE);
            previewSubtitle.setText("Preparing prescription...");

            setActionButtonsEnabled(false);

        } else {
            loadingLayout.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showErrorState(@NonNull String errorMessage) {
        previewHtmlLoaded = false;
        pdfGenerationRunning = false;

        loadedPrescriptionHtml = "";

        loadingLayout.setVisibility(View.GONE);
        prescriptionWebView.setVisibility(View.GONE);
        errorCard.setVisibility(View.VISIBLE);

        errorMessageView.setText(errorMessage);

        previewSubtitle.setText("Unable to prepare preview");
        setActionButtonsEnabled(false);
    }

    @SuppressLint("SetTextI18n")
    private void showPreviewDataLoadedState() {
        if (loadedPreviewData == null) {
            showErrorState("Prescription preview information not found.");
            return;
        }

        generatedPdfFile = null;
        pendingDownloadPdfFile = null;

        loadingLayout.setVisibility(View.GONE);
        errorCard.setVisibility(View.GONE);
        prescriptionWebView.setVisibility(View.VISIBLE);

        previewSubtitle.setText("Preparing prescription layout...");

        previewHtmlLoaded = false;
        setActionButtonsEnabled(false);


        loadedPrescriptionHtml = PrescriptionPreviewHtmlBuilder.build(loadedPreviewData);

        prescriptionWebView.loadDataWithBaseURL(
                null,
                loadedPrescriptionHtml,
                "text/html",
                "UTF-8",
                null
        );
    }

    private void downloadPrescriptionPdf() {
        preparePrescriptionPdf(pdfFile -> {
            pendingDownloadPdfFile = pdfFile;

            try {
                savePdfLauncher.launch(getPdfFileName());
            } catch (Exception exception) {
                pendingDownloadPdfFile = null;
                Toast.makeText(this, getErrorMessage(exception, "Unable to open the PDF save location."), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sharePrescriptionPdf() {
        preparePrescriptionPdf(pdfFile -> {
            try {
                Uri pdfUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", pdfFile);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("application/pdf");
                shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getPdfDocumentName());
                shareIntent.setClipData(ClipData.newRawUri("Prescription PDF", pdfUri));
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share prescription"));

            } catch (ActivityNotFoundException exception) {
                Toast.makeText(this, "No application is available to share the PDF.", Toast.LENGTH_SHORT).show();
            } catch (Exception exception) {
                Toast.makeText(this, getErrorMessage(exception, "Unable to share the prescription PDF."), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void printPrescription() {
        preparePrescriptionPdf(pdfFile -> {
            try {
                PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

                if (printManager == null) {
                    Toast.makeText(this, "Print service is not available.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String documentName = getPdfDocumentName();

                PrescriptionPdfPrintAdapter printAdapter = new PrescriptionPdfPrintAdapter(
                        pdfFile,
                        documentName
                );

                printManager.print(
                        documentName,
                        printAdapter,
                        PrescriptionPdfGenerator.createA4PrintAttributes()
                );

            } catch (Exception exception) {
                Toast.makeText(this, getErrorMessage(exception, "Unable to start printing."), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void preparePrescriptionPdf(@NonNull PdfReadyAction pdfReadyAction) {
        if (!canUsePrescriptionPreview()) {
            Toast.makeText(this, "Prescription preview is not ready.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (generatedPdfFile != null && generatedPdfFile.exists() && generatedPdfFile.length() > 0) {
            pdfReadyAction.onPdfReady(generatedPdfFile);
            return;
        }

        if (pdfGenerationRunning) {
            Toast.makeText(this, "Prescription PDF is being prepared.", Toast.LENGTH_SHORT).show();
            return;
        }

        File pdfDirectory = new File(getCacheDir(), "prescription_pdf");

        File destinationPdf = new File(pdfDirectory, getPdfFileName());

        pdfGenerationRunning = true;
        previewSubtitle.setText("Preparing prescription PDF...");

        setActionButtonsEnabled(false);

        PrescriptionPdfGenerator.generatePdf(this, loadedPrescriptionHtml, destinationPdf, new PrescriptionPdfGenerator.PdfGenerationCallback() {
            @Override
            public void onPdfGenerated(@NonNull File pdfFile) {
                pdfGenerationRunning = false;
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                generatedPdfFile = pdfFile;

                updateReadySubtitle();
                setActionButtonsEnabled(true);

                pdfReadyAction.onPdfReady(pdfFile);
            }

            @Override
            public void onPdfGenerationFailed(@NonNull String errorMessage) {
                pdfGenerationRunning = false;
                generatedPdfFile = null;
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                updateReadySubtitle();
                setActionButtonsEnabled(previewHtmlLoaded);

                Toast.makeText(PrescriptionPreview.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void savePdfToSelectedLocation(@Nullable Uri destinationUri) {
        File sourcePdf = pendingDownloadPdfFile;

        pendingDownloadPdfFile = null;

        if (destinationUri == null) {
            return;
        }

        if (sourcePdf == null || !sourcePdf.exists() || sourcePdf.length() <= 0) {
            Toast.makeText(this, "Generated prescription PDF was not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        previewSubtitle.setText("Saving prescription PDF...");

        setActionButtonsEnabled(false);
        pdfFileExecutor.execute(() -> {
            String errorMessage = "";

            try {
                copyPdfFile(sourcePdf, destinationUri);

            } catch (Exception exception) {
                errorMessage = getErrorMessage(exception, "Prescription PDF could not be saved.");
            }

            String finalErrorMessage = errorMessage;

            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                updateReadySubtitle();
                setActionButtonsEnabled(previewHtmlLoaded);
                if (finalErrorMessage.isEmpty()) {
                    Toast.makeText(this, "Prescription PDF saved successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, finalErrorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void copyPdfFile(@NonNull File sourcePdf, @NonNull Uri destinationUri) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(sourcePdf);
             OutputStream outputStream = getContentResolver().openOutputStream(destinationUri, "w")) {
            if (outputStream == null) {
                throw new IOException("PDF output location could not be opened.");
            }

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
        }
    }

    private boolean canUsePrescriptionPreview() {
        return previewHtmlLoaded && loadedPreviewData != null && !loadedPrescriptionHtml.isEmpty();
    }

    @SuppressLint("SetTextI18n")
    private void updateReadySubtitle() {
        if (loadedPreviewData == null) {
            return;
        }

        String prescriptionCode = loadedPreviewData.getRvmCode();

        if (prescriptionCode.isEmpty()) {
            previewSubtitle.setText("Prescription preview ready");
        } else {
            previewSubtitle.setText("Prescription No: " + prescriptionCode);
        }
    }

    @NonNull
    private String getPdfFileName() {
        return getPdfDocumentName() + ".pdf";
    }

    @NonNull
    private String getPdfDocumentName() {
        String prescriptionIdentity = rvmId;

        if (loadedPreviewData != null && !loadedPreviewData.getRvmCode().isEmpty()) {
            prescriptionIdentity = loadedPreviewData.getRvmCode();
        }

        prescriptionIdentity = prescriptionIdentity.trim().replaceAll("[^A-Za-z0-9_-]+", "_");

        if (prescriptionIdentity.isEmpty()) {
            prescriptionIdentity = "Prescription";
        }

        return "Prescription_" + prescriptionIdentity;
    }

    private void setActionButtonsEnabled(boolean enabled) {
        float buttonAlpha = enabled ? 1.0f : 0.45f;

        downloadButton.setEnabled(enabled);
        printButton.setEnabled(enabled);
        shareButton.setEnabled(enabled);

        downloadButton.setAlpha(buttonAlpha);
        printButton.setAlpha(buttonAlpha);
        shareButton.setAlpha(buttonAlpha);
    }

    @NonNull
    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    @NonNull
    private String getErrorMessage(@NonNull Exception exception, @NonNull String fallbackMessage) {
        if (exception.getMessage() == null || exception.getMessage().trim().isEmpty()) {
            return fallbackMessage;
        }

        return exception.getMessage().trim();
    }

    @Override
    protected void onDestroy() {
        pdfGenerationRunning = false;

        pendingDownloadPdfFile = null;
        generatedPdfFile = null;

        loadedPrescriptionHtml = "";
        loadedPreviewData = null;

        mainHandler.removeCallbacksAndMessages(null);

        pdfFileExecutor.shutdownNow();

        if (prescriptionWebView != null) {
            prescriptionWebView.stopLoading();
            prescriptionWebView.loadUrl("about:blank");
            prescriptionWebView.clearHistory();
            prescriptionWebView.removeAllViews();
            prescriptionWebView.destroy();
            prescriptionWebView = null;
        }

        previewViewModel = null;

        super.onDestroy();
    }
}