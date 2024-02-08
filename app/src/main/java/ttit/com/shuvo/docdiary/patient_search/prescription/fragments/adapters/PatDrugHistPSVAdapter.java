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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatDrugHistList;

public class PatDrugHistPSVAdapter extends RecyclerView.Adapter<PatDrugHistPSVAdapter.PDHPSVHolder> {
    private final ArrayList<PatDrugHistList> mCategory;
    private final Context mContext;

    public PatDrugHistPSVAdapter(ArrayList<PatDrugHistList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PDHPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_medic_history_details_psv_layout, parent, false);
        return new PDHPSVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PDHPSVHolder holder, int position) {
        PatDrugHistList patDrugHistList = mCategory.get(position);
        holder.patDrugName.setText(patDrugHistList.getMedicine_name());
        holder.patDrugDetails.setText(patDrugHistList.getPdh_details());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class PDHPSVHolder extends RecyclerView.ViewHolder {

        TextView patDrugName;
        TextView patDrugDetails;

        public PDHPSVHolder(@NonNull View itemView) {
            super(itemView);
            patDrugName = itemView.findViewById(R.id.pat_medic_history_mhm_name_pat_psv);
            patDrugDetails = itemView.findViewById(R.id.pat_medic_history_pmh_details_pat_psv);
        }
    }
}
