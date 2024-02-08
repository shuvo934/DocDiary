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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatAdviceList;

public class PatAdvicePSVAdapter extends RecyclerView.Adapter<PatAdvicePSVAdapter.PAPSVHolder> {

    private final ArrayList<PatAdviceList> mCategory;
    private final Context mContext;

    public PatAdvicePSVAdapter(ArrayList<PatAdviceList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PAPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_management_plan_details_psv_layout, parent, false);
        return new PAPSVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PAPSVHolder holder, int position) {
        PatAdviceList patAdviceList = mCategory.get(position);
        holder.advice.setText(patAdviceList.getPa_name());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class PAPSVHolder extends RecyclerView.ViewHolder {
        TextView advice;
        public PAPSVHolder(@NonNull View itemView) {
            super(itemView);
            advice = itemView.findViewById(R.id.pat_management_plan_for_patient_pat_psv);
        }
    }
}
