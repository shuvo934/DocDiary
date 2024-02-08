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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatManagementList;

public class PatManagementPSVAdapter extends RecyclerView.Adapter<PatManagementPSVAdapter.PMPSVHolder> {
    private final ArrayList<PatManagementList> mCategory;
    private final Context mContext;

    public PatManagementPSVAdapter(ArrayList<PatManagementList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PMPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_management_plan_details_psv_layout, parent, false);
        return new PMPSVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PMPSVHolder holder, int position) {
        PatManagementList patManagementList = mCategory.get(position);
        holder.managementPlan.setText(patManagementList.getPmap_details());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class PMPSVHolder extends RecyclerView.ViewHolder {
        TextView managementPlan;
        public PMPSVHolder(@NonNull View itemView) {
            super(itemView);
            managementPlan = itemView.findViewById(R.id.pat_management_plan_for_patient_pat_psv);
        }
    }
}
