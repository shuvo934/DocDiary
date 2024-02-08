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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatDiagnosisList;
import ttit.com.shuvo.docdiary.patient_search.prescription.PatPrescriptionV;

public class PatDiagnosisPSVAdapter extends RecyclerView.Adapter<PatDiagnosisPSVAdapter.PDPSVHolder> {

    private final ArrayList<PatDiagnosisList> mCategory;
    private final Context mContext;
    private final ClickedItem myClickedItem;

    public PatDiagnosisPSVAdapter(ArrayList<PatDiagnosisList> mCategory, Context mContext, ClickedItem myClickedItem) {
        this.mCategory = mCategory;
        this.mContext = mContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public PDPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_diagnosis_info_psv_layout, parent, false);
        return new PDPSVHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PDPSVHolder holder, int position) {
        PatDiagnosisList p = mCategory.get(position);
        holder.dmName.setText(p.getDm_name());

        if(PatPrescriptionV.selectedPositionPatPSV == position) {
            holder.dmName.setBackgroundColor(mContext.getColor(R.color.disabled));
            holder.dmName.setTextColor(mContext.getColor(R.color.white));
        }
        else {
            holder.dmName.setBackgroundColor(mContext.getColor(R.color.clouds));
            holder.dmName.setTextColor(mContext.getColor(R.color.default_text_color));
        }
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public interface ClickedItem {
        void onDiagnosisClicked(int diagPosition);
    }

    public static class PDPSVHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView dmName;
        ClickedItem mClickedItem;
        public PDPSVHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            dmName = itemView.findViewById(R.id.pat_diagnosis_dm_name_pat_psv);
            this.mClickedItem = ci;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickedItem.onDiagnosisClicked(getAdapterPosition());
        }
    }
}
