package ttit.com.shuvo.docdiary.report_manager.report_view.reports.web_view;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
//import ttit.com.shuvo.docdiary.report_manager.report_view.reports.ReportShow;

public class ReportWebView extends AppCompatActivity {

    ImageView close;
    TextView appbarName;
    ImageView download;
    WebView pdfView;
    CircularProgressIndicator circularProgressIndicator;
    private AlertDialog pDialog;
    boolean infoConnected = false;
    String report_url = "";
    String firstDate = "";
    String lastDate = "";
    String app_bar_name = "";

    Logger logger = Logger.getLogger(ReportWebView.class.getName());

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
        setContentView(R.layout.activity_report_web_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rep_web_view_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        circularProgressIndicator = findViewById(R.id.progress_indicator_web_report_show);
        circularProgressIndicator.setVisibility(View.GONE);
        pdfView = findViewById(R.id.all_report_web_view);

        close = findViewById(R.id.close_logo_of_web_report_show);
        appbarName = findViewById(R.id.web_report_show_app_bar_text);
        download = findViewById(R.id.download_web_report);

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

        download.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(ReportWebView.this);
            builder.setTitle("Download Report!")
                    .setMessage("Do you want to download this report?")
                    .setPositiveButton("YES", (dialog, which) -> downloadPDF())
                    .setNegativeButton("NO", (dialog, which) -> {
                    });
            AlertDialog alert = builder.create();
            alert.show();

        });

        setupWebViewWithUrl(pdfView, report_url);
        System.out.println(report_url);

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebViewWithUrl(WebView webView, String url) {
        if (webView != null) {
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);

            // Configure a WebViewClient to handle navigation events
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    // Return false to allow the WebView to handle the URL
                    return false;
                }
            });

            // Configure a WebChromeClient (optional)
            webView.setWebChromeClient(new WebChromeClient());

            // Generate HTML content to embed the PDF
            String htmlContent = getPDFHtml(url);

            // Load the HTML content into the WebView
            webView.loadData(htmlContent, "text/html", "utf-8");
        }
    }

    private String getPDFHtml(String url) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no\">\n" +
                "    <style>\n" +
                "        body, html {\n" +
                "            margin: 0;\n" +
                "            height: 100%;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        iframe {\n" +
                "            position: absolute;\n" +
                "            top: 0;\n" +
                "            left: 0;\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            border: none;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <iframe src=\"" + url + "\" allow=\"autoplay\"></iframe>\n" +
                "</body>\n" +
                "</html>";
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
                    AlertDialog dialog = new AlertDialog.Builder(ReportWebView.this)
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