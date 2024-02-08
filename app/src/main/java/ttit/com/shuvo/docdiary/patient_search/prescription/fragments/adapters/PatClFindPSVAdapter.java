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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatClinicalFindingsList;

public class PatClFindPSVAdapter extends RecyclerView.Adapter<PatClFindPSVAdapter.PCFPSVHolder> {

    private final ArrayList<PatClinicalFindingsList> mCategory;
    private final Context mContext;

    public PatClFindPSVAdapter(ArrayList<PatClinicalFindingsList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PCFPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_medic_history_details_psv_layout, parent, false);
        return new PCFPSVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PCFPSVHolder holder, int position) {
        PatClinicalFindingsList patClinicalFindingsList = mCategory.get(position);
        holder.patClFindings.setText(patClinicalFindingsList.getCfm_name());
        holder.patFindingsDetails.setText(patClinicalFindingsList.getCf_details());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class PCFPSVHolder extends RecyclerView.ViewHolder {
        TextView patClFindings;
        TextView patFindingsDetails;
        public PCFPSVHolder(@NonNull View itemView) {
            super(itemView);
            patClFindings = itemView.findViewById(R.id.pat_medic_history_mhm_name_pat_psv);
            patFindingsDetails = itemView.findViewById(R.id.pat_medic_history_pmh_details_pat_psv);
        }
    }
}
