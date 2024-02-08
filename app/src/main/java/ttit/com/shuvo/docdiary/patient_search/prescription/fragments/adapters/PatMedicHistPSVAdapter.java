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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatMedicHistList;

public class PatMedicHistPSVAdapter extends RecyclerView.Adapter<PatMedicHistPSVAdapter.PMHPSVHolder> {

    private final ArrayList<PatMedicHistList> mCategory;
    private final Context mContext;

    public PatMedicHistPSVAdapter(ArrayList<PatMedicHistList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PMHPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_medic_history_details_psv_layout, parent, false);
        return new PMHPSVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PMHPSVHolder holder, int position) {
        PatMedicHistList patMedicHistList = mCategory.get(position);
        holder.patMedicHistName.setText(patMedicHistList.getMhm_name());
        holder.patMedicHistDetails.setText(patMedicHistList.getPmh_details());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class PMHPSVHolder extends RecyclerView.ViewHolder {
        TextView patMedicHistName;
        TextView patMedicHistDetails;
        public PMHPSVHolder(@NonNull View itemView) {
            super(itemView);
            patMedicHistName = itemView.findViewById(R.id.pat_medic_history_mhm_name_pat_psv);
            patMedicHistDetails = itemView.findViewById(R.id.pat_medic_history_pmh_details_pat_psv);
        }
    }
}
