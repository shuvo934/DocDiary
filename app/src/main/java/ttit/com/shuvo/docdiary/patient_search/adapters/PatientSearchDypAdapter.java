package ttit.com.shuvo.docdiary.patient_search.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.HealthProgress;
import ttit.com.shuvo.docdiary.patient_search.appointment_calendar.PatAppointmentCalendar;
import ttit.com.shuvo.docdiary.patient_search.arraylists.PatientSearchDypList;

public class PatientSearchDypAdapter extends RecyclerView.Adapter<PatientSearchDypAdapter.PSDAHolder> {
    private final ArrayList<PatientSearchDypList> mCategory;
    private final Context mContext;
    private final PatientSelection patientSelection;

    public PatientSearchDypAdapter(ArrayList<PatientSearchDypList> mCategory, Context mContext, PatientSelection patientSelection) {
        this.mCategory = mCategory;
        this.mContext = mContext;
        this.patientSelection = patientSelection;
    }

    @NonNull
    @Override
    public PSDAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.patient_search_dy_prescription_view, parent, false);
        return new PSDAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PSDAHolder holder, int position) {
        PatientSearchDypList patientSearchList = mCategory.get(position);

        holder.patName.setText(patientSearchList.getPat_name());

        String area = safeText(patientSearchList.getDd_thana_name());
        holder.patArea.setText(area.isEmpty() ? "" : "(" + area + ")");
        holder.patArea.setVisibility(area.isEmpty() ? View.GONE : View.VISIBLE);

        String year = safeText(patientSearchList.getPyear());
        holder.patYear.setText(year.isEmpty() ? "" : "(" + year + ")");
        holder.patYear.setVisibility(year.isEmpty() ? View.GONE : View.VISIBLE);

        holder.patCode.setText(patientSearchList.getSub_code());

        String gender = safeText(patientSearchList.getGender());
        String age = safeText(patientSearchList.getPat_age());

        String genderAge;

        if (!gender.isEmpty() && !age.isEmpty()) {
            genderAge = gender + "  •  " + age;
        }
        else if (!gender.isEmpty()) {
            genderAge = gender;
        }
        else {
            genderAge = age;
        }

        holder.patAG.setText(genderAge);

        int progress = safeProgress(patientSearchList.getPph_progress());
        boolean hasProgress = progress > 0;
        holder.hpBar.setProgress(progress);
        holder.hpLayout.setVisibility(hasProgress ? View.VISIBLE : View.GONE);
        holder.hpMissing.setVisibility(hasProgress ? View.GONE : View.VISIBLE);
        holder.viewProgress.setVisibility(hasProgress ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mCategory != null ? mCategory.size() : 0;
    }

    private static String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private static int safeProgress(String value) {
        try {
            int progress = (int) Math.round(Double.parseDouble(safeText(value)));
            return Math.max(0, Math.min(10, progress));
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public class PSDAHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        AppCompatTextView patName;
        AppCompatTextView patArea;
        AppCompatTextView patCode;
        AppCompatTextView patYear;
        MaterialButton appCalendar;
        AppCompatTextView patAG;

        RelativeLayout hpLayout;
        TextView hpText;
        ProgressBar hpBar;
        TextView hpMissing;
        MaterialButton viewProgress;

        public PSDAHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view_of_patient_search_dy);
            patName = itemView.findViewById(R.id.patient_name_in_pat_search_dy);
            patArea = itemView.findViewById(R.id.patient_area_in_pat_search_dy);
            patYear = itemView.findViewById(R.id.patient_year_in_pat_search_dy);
            patCode = itemView.findViewById(R.id.pat_ph_code_in_pat_search_dy);
            appCalendar = itemView.findViewById(R.id.patient_view_appointment_calendar_pat_search_dy);
            patAG = itemView.findViewById(R.id.pat_age_gender_in_pat_search_dy);

            hpLayout = itemView.findViewById(R.id.layout_of_pat_health_progress_in_pat_search_dy);
            hpText = itemView.findViewById(R.id.text_pat_search_hp_text_dy);
            hpBar = itemView.findViewById(R.id.pat_health_progress_bar_in_pat_search_dy);
            hpMissing = itemView.findViewById(R.id.text_pat_search_hp_not_found_dy);
            viewProgress = itemView.findViewById(R.id.patient_view_health_progress_pat_search_dy);


            cardView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }

                patientSelection.onSelect(position);
            });

            viewProgress.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                Context context = v.getContext();
                Intent intent = new Intent(mContext, HealthProgress.class);
                intent.putExtra("P_NAME",mCategory.get(position).getPat_name());
                intent.putExtra("P_CODE",mCategory.get(position).getSub_code());
                intent.putExtra("P_PH_ID",mCategory.get(position).getPh_id());
                intent.putExtra("FROM_PSV",true);

                context.startActivity(intent);

            });

            appCalendar.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                Context context = v.getContext();
                Intent intent = new Intent(mContext, PatAppointmentCalendar.class);
                intent.putExtra("P_NAME",mCategory.get(position).getPat_name());
                intent.putExtra("P_CODE",mCategory.get(position).getSub_code());
                intent.putExtra("P_PH_ID",mCategory.get(position).getPh_id());

                context.startActivity(intent);
            });
        }
    }

    public interface PatientSelection {
        void onSelect(int position);
    }
}
