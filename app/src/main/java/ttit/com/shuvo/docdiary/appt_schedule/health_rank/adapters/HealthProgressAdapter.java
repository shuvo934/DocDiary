package ttit.com.shuvo.docdiary.appt_schedule.health_rank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.arraylists.HealthProgressList;

public class HealthProgressAdapter extends RecyclerView.Adapter<HealthProgressAdapter.HPAHolder> {
    private ArrayList<HealthProgressList> mCategory;
    private final Context mContext;

    public HealthProgressAdapter(ArrayList<HealthProgressList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public HPAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_progress_lists_view, parent, false);
        return new HPAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HPAHolder holder, int position) {
        HealthProgressList healthProgressList = mCategory.get(position);
        holder.slNo.setText(healthProgressList.getSerialNo());
        holder.prDate.setText(healthProgressList.getPph_date());
        holder.serviceName.setText(healthProgressList.getPfn_name());
        holder.unitName.setText(healthProgressList.getDetps_name());
        holder.doctorName.setText(healthProgressList.getDoc_name());
        holder.progressScore.setText(healthProgressList.getPph_progress());
        holder.progressNotes.setText(healthProgressList.getPph_notes());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class HPAHolder extends RecyclerView.ViewHolder {
        TextView slNo;
        TextView prDate;
        TextView serviceName;
        TextView unitName;
        TextView doctorName;
        TextView progressScore;
        TextView progressNotes;
        public HPAHolder(@NonNull View itemView) {
            super(itemView);
            slNo = itemView.findViewById(R.id.serial_no_for_p_ph);
            prDate = itemView.findViewById(R.id.progress_date_for_p_ph);
            serviceName = itemView.findViewById(R.id.service_name_for_p_ph);
            unitName = itemView.findViewById(R.id.unit_name_for_p_ph);
            doctorName = itemView.findViewById(R.id.doctor_name_for_p_ph);
            progressScore = itemView.findViewById(R.id.progress_score_for_p_ph);
            progressNotes = itemView.findViewById(R.id.progress_notes_for_p_ph);
        }
    }

    public void filterList(ArrayList<HealthProgressList> filteredList) {
        mCategory = filteredList;
        notifyDataSetChanged();
    }
}
