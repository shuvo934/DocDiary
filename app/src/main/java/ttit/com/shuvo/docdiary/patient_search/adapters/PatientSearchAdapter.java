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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.HealthProgress;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.PrescriptionSetup;
import ttit.com.shuvo.docdiary.patient_search.arraylists.PatientSearchList;
import ttit.com.shuvo.docdiary.patient_search.prescription.PatPrescriptionV;

public class PatientSearchAdapter extends RecyclerView.Adapter<PatientSearchAdapter.PSAHolder> {
    private ArrayList<PatientSearchList> mCategory;
    private final Context mContext;

    public PatientSearchAdapter(ArrayList<PatientSearchList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PSAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.patient_search_prescription_view, parent, false);
        return new PSAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PSAHolder holder, int position) {
        PatientSearchList patientSearchList = mCategory.get(position);

        holder.patName.setText(patientSearchList.getPat_name());
        String area = patientSearchList.getDd_thana_name();
        if (!area.isEmpty()) {
            String tt = "(" + area + ")";
            holder.patArea.setText(tt);
            holder.patArea.setVisibility(View.VISIBLE);
        }
        else {
            String tt = "(No Address Found)";
            holder.patArea.setText(tt);
            holder.patArea.setVisibility(View.GONE);
        }

        holder.patCode.setText(patientSearchList.getSub_code());

        String pph_prog = patientSearchList.getPph_progress();

        if (pph_prog.equals("0")) {
            holder.hpLayout.setVisibility(View.GONE);
            holder.hpMissing.setVisibility(View.VISIBLE);
            holder.hpBar.setProgress(Integer.parseInt(pph_prog));
            holder.viewProgress.setVisibility(View.GONE);
        }
        else {
            holder.hpLayout.setVisibility(View.VISIBLE);
            holder.hpMissing.setVisibility(View.GONE);
            holder.hpBar.setProgress(Integer.parseInt(pph_prog));
            holder.viewProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class PSAHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        AppCompatTextView patName;
        AppCompatTextView patArea;
        AppCompatTextView patCode;

        RelativeLayout hpLayout;
        TextView hpText;
        ProgressBar hpBar;
        TextView hpMissing;
        MaterialButton viewProgress;
        public PSAHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view_of_patient_search);
            patName = itemView.findViewById(R.id.patient_name_in_pat_search);
            patArea = itemView.findViewById(R.id.patient_area_in_pat_search);
            patCode = itemView.findViewById(R.id.pat_ph_code_in_pat_search);

            hpLayout = itemView.findViewById(R.id.layout_of_pat_health_progress_in_pat_search);
            hpText = itemView.findViewById(R.id.text_pat_search_hp_text);
            hpBar = itemView.findViewById(R.id.pat_health_progress_bar_in_pat_search);
            hpMissing = itemView.findViewById(R.id.text_pat_search_hp_not_found);
            viewProgress = itemView.findViewById(R.id.patient_view_health_progress_pat_search);

            cardView.setOnClickListener(v -> {
                System.out.println(mCategory.get(getAdapterPosition()).getPat_name());
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Intent intent = new Intent(mContext, PatPrescriptionV.class);
                intent.putExtra("P_PH_ID",mCategory.get(getAdapterPosition()).getPh_id());
                activity.startActivity(intent);
            });

            viewProgress.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Intent intent = new Intent(mContext, HealthProgress.class);
                intent.putExtra("P_NAME",mCategory.get(getAdapterPosition()).getPat_name());
                intent.putExtra("P_CODE",mCategory.get(getAdapterPosition()).getSub_code());
                intent.putExtra("P_PH_ID",mCategory.get(getAdapterPosition()).getPh_id());
                intent.putExtra("FROM_PSV",true);

                activity.startActivity(intent);

            });
        }
    }
    public void filterList(ArrayList<PatientSearchList> filteredList) {
        mCategory = filteredList;
        notifyDataSetChanged();
    }
}
