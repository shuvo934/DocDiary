package ttit.com.shuvo.docdiary.hr_accounts;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.adminInfoLists;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.dashboards.AccountsDashboard;
import ttit.com.shuvo.docdiary.hr_accounts.dashboards.AnalyticsDashboard;
import ttit.com.shuvo.docdiary.hr_accounts.dashboards.HumanResDashboard;
import ttit.com.shuvo.docdiary.report_manager.ReportManager;

public class HRAccounts extends AppCompatActivity {

    ImageView backButton;

    MaterialCardView hrDashboard;
    MaterialCardView accDashboard;
    MaterialCardView reportManager;
    MaterialCardView analyticDashboard;

    TextView hrAccAltBarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hraccounts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.hr_acc_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.back_logo_of_hr_accounts);
        hrDashboard = findViewById(R.id.hr_dashboard);
        accDashboard = findViewById(R.id.accounts_dashboard);
        reportManager = findViewById(R.id.report_menu);
        analyticDashboard = findViewById(R.id.analytics_menu);

        hrAccAltBarName = findViewById(R.id.hr_acc_alt_bar_name_text);

        backButton.setOnClickListener(v -> finish());

        if (adminInfoLists != null) {
            if (!adminInfoLists.isEmpty()) {
                analyticDashboard.setVisibility(View.GONE);
                if (Integer.parseInt(adminInfoLists.get(0).getAll_access_flag()) == 2) {
                    if (adminInfoLists.get(0).getHr_dashboard().equals("1")) {
                        hrDashboard.setVisibility(View.VISIBLE);
                    }
                    else {
                        hrDashboard.setVisibility(View.GONE);
                    }
                    accDashboard.setVisibility(View.GONE);
                    String tt = "HR & Reports";
                    hrAccAltBarName.setText(tt);
                }
                else if (Integer.parseInt(adminInfoLists.get(0).getAll_access_flag()) == 3) {
                    hrDashboard.setVisibility(View.GONE);
                    if (adminInfoLists.get(0).getAcc_dashboard().equals("1")) {
                        accDashboard.setVisibility(View.VISIBLE);
                    }
                    else {
                        accDashboard.setVisibility(View.GONE);
                    }
                    String tt = "Accounts & Reports";
                    hrAccAltBarName.setText(tt);
                }
                else if (Integer.parseInt(adminInfoLists.get(0).getAll_access_flag()) == 1) {
                    if (adminInfoLists.get(0).getHr_dashboard().equals("1")) {
                        hrDashboard.setVisibility(View.VISIBLE);
                    }
                    else {
                        hrDashboard.setVisibility(View.GONE);
                    }

                    if (adminInfoLists.get(0).getAcc_dashboard().equals("1")) {
                        accDashboard.setVisibility(View.VISIBLE);
                    }
                    else {
                        accDashboard.setVisibility(View.GONE);
                    }

                    if (adminInfoLists.get(0).getAnalytics_dashboard().equals("1")) {
                        analyticDashboard.setVisibility(View.VISIBLE);
                    }
                    else {
                        analyticDashboard.setVisibility(View.GONE);
                    }

                    String tt = "Management Portal";
                    hrAccAltBarName.setText(tt);
                }
                else {
                    hrDashboard.setVisibility(View.GONE);
                    accDashboard.setVisibility(View.GONE);
                    String tt = "Reports";
                    hrAccAltBarName.setText(tt);
                }
            }
        }

        reportManager.setOnClickListener(v -> {
            Intent intent = new Intent(HRAccounts.this, ReportManager.class);
            intent.putExtra("R_TYPE","1");
            startActivity(intent);
        });

        accDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(HRAccounts.this, AccountsDashboard.class);
            startActivity(intent);
        });

        hrDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(HRAccounts.this, HumanResDashboard.class);
            startActivity(intent);
        });

        analyticDashboard.setOnClickListener(view -> {
            Intent intent = new Intent(HRAccounts.this, AnalyticsDashboard.class);
            startActivity(intent);
        });

    }
}