package ttit.com.shuvo.docdiary.patient_search.prescription.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatRefServiceList;

public class PatRefServicePSVAdapter extends RecyclerView.Adapter<PatRefServicePSVAdapter.PRSPSVHolder> {

    private final ArrayList<PatRefServiceList> mCategory;
    private final Context mContext;

    public PatRefServicePSVAdapter(ArrayList<PatRefServiceList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PRSPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_ref_service_info_psv_layout, parent, false);
        return new PRSPSVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PRSPSVHolder holder, int position) {
        PatRefServiceList p = mCategory.get(position);
        holder.serviceName.setText(p.getPfn_fee_name());
        holder.quantity.setText(p.getDrs_qty());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class PRSPSVHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        TextView quantity;

        public PRSPSVHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.pat_ref_service_name_pat_psv);
            quantity = itemView.findViewById(R.id.pat_ref_service_qty_pat_psv);
        }
    }
}
