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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.ComplainLists;

public class ComplainPSVAdapter extends RecyclerView.Adapter<ComplainPSVAdapter.CPSVHolder> {
    private final ArrayList<ComplainLists> mCategory;
    private final Context mContext;

    public ComplainPSVAdapter(ArrayList<ComplainLists> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.complain_details_psv_layout, parent, false);
        return new CPSVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CPSVHolder holder, int position) {
        ComplainLists complainLists = mCategory.get(position);
        holder.complainName.setText(complainLists.getCm_name());
        holder.causeOfInjury.setText(complainLists.getInjury_name());
        holder.injuryDate.setText(complainLists.getPci_date());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class CPSVHolder extends RecyclerView.ViewHolder {
        TextView complainName;
        TextView causeOfInjury;
        TextView injuryDate;

        public CPSVHolder(@NonNull View itemView) {
            super(itemView);
            complainName = itemView.findViewById(R.id.prescription_complain_name_psv);
            causeOfInjury = itemView.findViewById(R.id.prescription_cause_of_injury_psv);
            injuryDate = itemView.findViewById(R.id.prescription_injury_date_psv);
        }
    }
}
