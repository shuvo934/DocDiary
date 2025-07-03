package ttit.com.shuvo.docdiary.report_manager;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.report_manager.report_view.ReportView;

public class ReportManager extends AppCompatActivity {

    ImageView backButton;

    CardView paymentRcvReport;
    CardView paymentRcvRgReport;
    CardView paymentRcvSumReport;
    CardView doctorPayRcvRep;
    CardView doctorAppRep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report_manager_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.back_logo_of_report_manager);
        paymentRcvReport = findViewById(R.id.payment_rcv_report_card_view);
        paymentRcvRgReport = findViewById(R.id.payment_rcv_register_report_card_view);
        paymentRcvSumReport = findViewById(R.id.payment_rcv_summary_report_card_view);
        doctorPayRcvRep = findViewById(R.id.doctor_wise_payment_rcv_report_card_view);
        doctorAppRep = findViewById(R.id.doctor_wise_appointment_report_card_view);

        backButton.setOnClickListener(v -> finish());

        paymentRcvReport.setOnClickListener(v -> {
            Intent intent = new Intent(ReportManager.this, ReportView.class);
            intent.putExtra("R_TYPE","1");
            startActivity(intent);
        });

        paymentRcvSumReport.setOnClickListener(v -> {
            Intent intent = new Intent(ReportManager.this, ReportView.class);
            intent.putExtra("R_TYPE","2");
            startActivity(intent);
        });

        doctorPayRcvRep.setOnClickListener(v -> {
            Intent intent = new Intent(ReportManager.this, ReportView.class);
            intent.putExtra("R_TYPE","3");
            startActivity(intent);
        });

        doctorAppRep.setOnClickListener(v -> {
            Intent intent = new Intent(ReportManager.this, ReportView.class);
            intent.putExtra("R_TYPE","4");
            startActivity(intent);
        });

        paymentRcvRgReport.setOnClickListener(v -> {
            Intent intent = new Intent(ReportManager.this, ReportView.class);
            intent.putExtra("R_TYPE","5");
            startActivity(intent);
        });

        if (pre_url_api.contains("cstar") || pre_url_api.contains("btrf")) {
            paymentRcvRgReport.setVisibility(View.VISIBLE);
        }
        else {
            paymentRcvRgReport.setVisibility(View.GONE);
        }
    }
}