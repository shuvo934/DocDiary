package ttit.com.shuvo.docdiary.patient_search.prescription.fragments.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatMedicationList;

public class PatMedicationPSVAdapter extends RecyclerView.Adapter<PatMedicationPSVAdapter.PMEPSVHolder> {
    private final ArrayList<PatMedicationList> mCategory;
    private final Context mContext;

    public PatMedicationPSVAdapter(ArrayList<PatMedicationList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PMEPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.patient_medication_details_psv_layout, parent, false);
        return new PMEPSVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PMEPSVHolder holder, int position) {
        PatMedicationList patMedicationList = mCategory.get(position);
        holder.patMedicineName.setText(patMedicationList.getMedicine_name());
        holder.patMedicineTakeTime.setText(patMedicationList.getMpm_name());
        holder.patDoseName.setText(patMedicationList.getDose_name());
        holder.patMedicineDuration.setText(patMedicationList.getPmp_duration());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class PMEPSVHolder extends RecyclerView.ViewHolder {
        TextView patMedicineName;
        TextView patMedicineTakeTime;
        TextView patDoseName;
        TextView patMedicineDuration;

        public PMEPSVHolder(@NonNull View itemView) {
            super(itemView);
            patMedicineName = itemView.findViewById(R.id.medicine_name_for_patient_pat_psv);
            patMedicineTakeTime = itemView.findViewById(R.id.medicine_take_time_for_patient_pat_psv);
            patDoseName = itemView.findViewById(R.id.medicine_dose_for_patient_pat_psv);
            patMedicineDuration = itemView.findViewById(R.id.medicine_take_duration_for_patient_pat_psv);
        }
    }
}
