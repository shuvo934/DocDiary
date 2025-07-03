package ttit.com.shuvo.docdiary.report_manager.report_view.reports;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
//import com.github.barteksc.pdfviewer.PDFView;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.request.ByteArrayRequest;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.request.PdfRendererHelper;

public class ReportShow extends AppCompatActivity {

    ImageView close;
    TextView appbarName;
    ImageView download;
//    PDFView pdfView;
    CircularProgressIndicator circularProgressIndicator;
    private AlertDialog pDialog;
    boolean infoConnected = false;
    String report_url = "";
    String firstDate = "";
    String lastDate = "";
    String app_bar_name = "";

    Logger logger = Logger.getLogger(ReportShow.class.getName());

    PhotoView pdfPageView;
    MaterialCardView btnPrev, btnNext;

    PdfRenderer pdfRenderer;
    PdfRenderer.Page currentPage;
    ParcelFileDescriptor fileDescriptor;
    int pageIndex = 0;
    LinearLayout buttonLay;

//    private RecyclerView recyclerView;
//    private PdfRendererHelper pdfHelper;

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
        setContentView(R.layout.activity_report_show);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.parent_layout_report_show), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        close = findViewById(R.id.close_logo_of_report_show);
        appbarName = findViewById(R.id.report_show_app_bar_text);
        download = findViewById(R.id.download_report);
//        pdfView = findViewById(R.id.pdfView);
        circularProgressIndicator = findViewById(R.id.progress_indicator_report_show);
        circularProgressIndicator.setVisibility(GONE);
//        recyclerView = findViewById(R.id.pdf_page_recycler);
        buttonLay = findViewById(R.id.button_report_lay);
        buttonLay.setVisibility(GONE);

        pdfPageView = findViewById(R.id.pdfPageView);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        AlertDialog.Builder dbuilder = new AlertDialog.Builder(this);
        dbuilder.setView(R.layout.dowloading_progress_bar);
        pDialog = dbuilder.create();

        close.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        report_url = intent.getStringExtra("REPORT_URL");
        app_bar_name = intent.getStringExtra("APP_BAR_NAME");
        firstDate = intent.getStringExtra("FIRST_DATE");
        lastDate = intent.getStringExtra("LAST_DATE");
        appbarName.setText(app_bar_name);
        System.out.println(report_url);

        download.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(ReportShow.this);
            builder.setTitle("Download Report!")
                    .setMessage("Do you want to download this report?")
                    .setPositiveButton("YES", (dialog, which) -> downloadPDF())
                    .setNegativeButton("NO", (dialog, which) -> {
                    });
            AlertDialog alert = builder.create();
            alert.show();

        });

//        new RetrievePDFfromUrl().execute(report_url);
//        showReport();
        downloadPdf(report_url);

        btnPrev.setOnClickListener(v -> showPage(pageIndex - 1));
        btnNext.setOnClickListener(v -> showPage(pageIndex + 1));
    }

//    public void showReport() {
//        circularProgressIndicator.setVisibility(View.VISIBLE);
//        final InputStream[] inputStream = {null};
//        new Thread(() -> {
//            try {
//                URL url = new URL(report_url);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                if (urlConnection.getResponseCode() == 200) {
//                    inputStream[0] = new BufferedInputStream(urlConnection.getInputStream());
//                }
//
//            } catch (IOException e) {
//                logger.log(Level.WARNING,e.getMessage(),e);
//            }
//            runOnUiThread(() -> pdfView.fromStream(inputStream[0])
//                    .enableSwipe(true)
//                    .enableDoubletap(true)
//                    .enableAntialiasing(true)
//                    .onLoad(nbPages -> circularProgressIndicator.setVisibility(View.GONE))
//                    .onError(t -> {
//                        AlertDialog dialog = new AlertDialog.Builder(ReportShow.this)
//                                .setMessage(t.getLocalizedMessage())
//                                .setPositiveButton("Retry", null)
//                                .setNegativeButton("Cancel",null)
//                                .show();
//
//                        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                        positive.setOnClickListener(v -> {
//                            dialog.dismiss();
//                            showReport();
//                        });
//                        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                        negative.setOnClickListener(v -> {
//                            dialog.dismiss();
//                            finish();
//                        });
//                    })
//                    .onPageError((page, t) -> {
//                        AlertDialog dialog = new AlertDialog.Builder(ReportShow.this)
//                                .setMessage(t.getLocalizedMessage())
//                                .setPositiveButton("Retry", null)
//                                .setNegativeButton("Cancel",null)
//                                .show();
//
//                        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                        positive.setOnClickListener(v -> {
//                            dialog.dismiss();
//                            showReport();
//                        });
//                        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                        negative.setOnClickListener(v -> {
//                            dialog.dismiss();
//                            finish();
//                        });
//                    })
//                    .load());
//        }).start();
//    }

    private void downloadPdf(String url) {
        circularProgressIndicator.setVisibility(VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);

        ByteArrayRequest request = new ByteArrayRequest(Request.Method.GET, url,
                response -> {
                    try {
                        File pdfFile = new File(getCacheDir(), "downloaded.pdf");
                        FileOutputStream out = new FileOutputStream(pdfFile);
                        out.write(response);
                        out.close();

                        openPdf(pdfFile);
                    } catch (Exception e) {
                        circularProgressIndicator.setVisibility(GONE);
                        Log.e("PDF", "File error: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Could not Download the PDF", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    circularProgressIndicator.setVisibility(GONE);
                    Log.e("PDF", "Download error: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Could not Download the PDF", Toast.LENGTH_SHORT).show();
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 20,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(request);
    }

//    private void openPdf(File file) {
//        pdfHelper = new PdfRendererHelper(this, file);
//        PdfPageAdapter adapter = new PdfPageAdapter(pdfHelper);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
//    }

    private void openPdf(File file) {
        try {
            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
            showPage(0);
        } catch (Exception e) {
            circularProgressIndicator.setVisibility(GONE);
            Log.e("PDF", "Renderer error: " + e.getMessage());
        }
    }

    private void showPage(int index) {
        if (pdfRenderer == null || index < 0 || index >= pdfRenderer.getPageCount()) return;

        if (currentPage != null) currentPage.close();

        currentPage = pdfRenderer.openPage(index);

        int scale = 3;
        int width = currentPage.getWidth() * scale;
        int height = currentPage.getHeight() * scale;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Rect dest = new Rect(0, 0, width, height);

        currentPage.render(bitmap, dest, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        Bitmap enhancedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Objects.requireNonNull(bitmap.getConfig()));

        Canvas canvas = new Canvas(enhancedBitmap);
        Paint paint = new Paint();

// 2. Adjust brightness and contrast
        ColorMatrix matrix = new ColorMatrix();

// Adjust contrast (1.0 = default, higher = more contrast)
        float contrast = 1.4f;
// Adjust brightness (0 = default, positive = brighter)
        float brightness = 40f;

        matrix.set(new float[]{
                contrast, 0, 0, 0, brightness,
                0, contrast, 0, 0, brightness,
                0, 0, contrast, 0, brightness,
                0, 0, 0, 1, 0
        });

        paint.setColorFilter(new ColorMatrixColorFilter(matrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);

//        Bitmap bitmap = Bitmap.createBitmap(
//                currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
//
//        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        pdfPageView.setImageBitmap(bitmap);
        circularProgressIndicator.setVisibility(GONE);
        buttonLay.setVisibility(VISIBLE);

        pageIndex = index;
    }

    @Override
    protected void onDestroy() {
        try {
            if (currentPage != null) currentPage.close();
            if (pdfRenderer != null) pdfRenderer.close();
            if (fileDescriptor != null) fileDescriptor.close();
        } catch (IOException e) {
            e.getLocalizedMessage();
        }
        super.onDestroy();
    }

    public void Download(String url, String title) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String tempTitle = title.replace(" ", "_");
        request.setTitle(tempTitle);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, tempTitle+".pdf");
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
        infoConnected = true;

    }

    public boolean isConnected() {
        boolean connected;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            Network nw = cm.getActiveNetwork();
            if (nw == null) {
                return false;
            }
            else {
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(nw);
                connected =  networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
                return connected;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING,e.getMessage(),e);
            return false;
        }
    }


    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e)          { logger.log(Level.WARNING,e.getMessage(),e); }

        return false;
    }

    public void downloadPDF() {
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        new Thread(() -> {
            if (isConnected() && isOnline()) {

                Download(report_url, app_bar_name+"_"+firstDate+" to "+lastDate);
            }
            else {
                infoConnected = false;
            }
            runOnUiThread(() -> {
                pDialog.dismiss();
                if (infoConnected) {
                    Toast.makeText(getApplicationContext(), "Download Complete", Toast.LENGTH_SHORT).show();
                    infoConnected = false;
                }
                else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    AlertDialog dialog = new AlertDialog.Builder(ReportShow.this)
                            .setMessage("Please Check Your Internet Connection")
                            .setPositiveButton("Retry", null)
                            .setNegativeButton("Cancel",null)
                            .show();

                    Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positive.setOnClickListener(v -> {

                        downloadPDF();
                        dialog.dismiss();
                    });

                    Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    negative.setOnClickListener(v -> dialog.dismiss());
                }
            });
        }).start();
    }
}