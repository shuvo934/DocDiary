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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatReferenceList;

public class PatReferencePSVAdapter extends RecyclerView.Adapter<PatReferencePSVAdapter.PRPSVHolder> {

    private final ArrayList<PatReferenceList> mCategory;
    private final Context mContext;

    public PatReferencePSVAdapter(ArrayList<PatReferenceList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PRPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_management_plan_details_psv_layout, parent, false);
        return new PRPSVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PRPSVHolder holder, int position) {
        PatReferenceList patReferenceList = mCategory.get(position);
        holder.reference.setText(patReferenceList.getPref_name());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class PRPSVHolder extends RecyclerView.ViewHolder {
        TextView reference;

        public PRPSVHolder(@NonNull View itemView) {
            super(itemView);
            reference = itemView.findViewById(R.id.pat_management_plan_for_patient_pat_psv);
        }
    }
}
