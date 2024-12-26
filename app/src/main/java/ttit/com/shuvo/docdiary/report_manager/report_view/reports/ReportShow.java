package ttit.com.shuvo.docdiary.report_manager.report_view.reports;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;

public class ReportShow extends AppCompatActivity {

    ImageView close;
    TextView appbarName;
    ImageView download;
    PDFView pdfView;
    CircularProgressIndicator circularProgressIndicator;
    private AlertDialog pDialog;
    boolean infoConnected = false;
    String report_url = "";
    String firstDate = "";
    String lastDate = "";
    String app_bar_name = "";

    Logger logger = Logger.getLogger(ReportShow.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ReportShow.this,R.color.clouds));
        setContentView(R.layout.activity_report_show);

        close = findViewById(R.id.close_logo_of_report_show);
        appbarName = findViewById(R.id.report_show_app_bar_text);
        download = findViewById(R.id.download_report);
        pdfView = findViewById(R.id.pdfView);
        circularProgressIndicator = findViewById(R.id.progress_indicator_report_show);
        circularProgressIndicator.setVisibility(View.GONE);

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
        showReport();
    }

    public void showReport() {
        circularProgressIndicator.setVisibility(View.VISIBLE);
        final InputStream[] inputStream = {null};
        new Thread(() -> {
            try {
                URL url = new URL(report_url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream[0] = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
            }
            runOnUiThread(() -> pdfView.fromStream(inputStream[0])
                    .enableSwipe(true)
                    .enableDoubletap(true)
                    .enableAntialiasing(true)
                    .onLoad(nbPages -> circularProgressIndicator.setVisibility(View.GONE))
                    .onError(t -> {
                        AlertDialog dialog = new AlertDialog.Builder(ReportShow.this)
                                .setMessage(t.getLocalizedMessage())
                                .setPositiveButton("Retry", null)
                                .setNegativeButton("Cancel",null)
                                .show();

                        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positive.setOnClickListener(v -> {
                            dialog.dismiss();
                            showReport();
                        });
                        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negative.setOnClickListener(v -> {
                            dialog.dismiss();
                            finish();
                        });
                    })
                    .onPageError((page, t) -> {
                        AlertDialog dialog = new AlertDialog.Builder(ReportShow.this)
                                .setMessage(t.getLocalizedMessage())
                                .setPositiveButton("Retry", null)
                                .setNegativeButton("Cancel",null)
                                .show();

                        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positive.setOnClickListener(v -> {
                            dialog.dismiss();
                            showReport();
                        });
                        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negative.setOnClickListener(v -> {
                            dialog.dismiss();
                            finish();
                        });
                    })
                    .load());
        }).start();
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