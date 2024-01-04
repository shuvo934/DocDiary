package ttit.com.shuvo.docdiary.appt_schedule.prescription.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.PrescriptionSetup;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.ReferralUnitModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.arraylists.DiagnosisModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatDiagnosisList;


public class PatDiagnosisAdapter extends RecyclerView.Adapter<PatDiagnosisAdapter.PDIHolder> {
    private ArrayList<PatDiagnosisList> mCategory;
    private Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;

    String parsing_message = "";
    private final ClickedItem myClickedItem;

    public PatDiagnosisAdapter(ArrayList<PatDiagnosisList> mCategory, Context mContext, ClickedItem cli) {
        this.mCategory = mCategory;
        this.mContext = mContext;
        this.myClickedItem = cli;
    }

    @NonNull
    @Override
    public PDIHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_diagnosis_info_layout, parent, false);
        return new PDIHolder(view,myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PDIHolder holder, int position) {
        PatDiagnosisList p = mCategory.get(position);
        holder.dmName.setText(p.getDm_name());
        if(PrescriptionSetup.selectedPosition == position) {
            holder.dmName.setBackgroundColor(mContext.getColor(R.color.back_color));
            holder.dmName.setTextColor(mContext.getColor(R.color.white));
        }
        else {
            holder.dmName.setBackgroundColor(mContext.getColor(R.color.clouds));
            holder.dmName.setTextColor(mContext.getColor(R.color.default_text_color));
        }

        if (p.getChild_count().equals("0")) {
            holder.edit.setVisibility(View.VISIBLE);
//            holder.delete.setVisibility(View.VISIBLE);
        }
        else {
            holder.edit.setVisibility(View.GONE);
//            holder.delete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class PDIHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView dmName;
        ImageView edit;
//        ImageView delete;
        ClickedItem mClickedItem;
        public PDIHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            dmName = itemView.findViewById(R.id.pat_diagnosis_dm_name);
            edit = itemView.findViewById(R.id.pat_diagnosis_info_edit);
//            delete = itemView.findViewById(R.id.pat_diagnosis_info_delete);
            this.mClickedItem = ci;

            itemView.setOnClickListener(this);

            edit.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String dm_id = mCategory.get(getAdapterPosition()).getDm_id();
                String pdi_id = mCategory.get(getAdapterPosition()).getPdi_id();
                String pmm_id = mCategory.get(getAdapterPosition()).getPdi_pmm_id();
                String dm_name = mCategory.get(getAdapterPosition()).getDm_name();

                Intent intent = new Intent(mContext, DiagnosisModify.class);
                intent.putExtra("P_DM_ID", dm_id);
                intent.putExtra("DIAG_MODIFY_TYPE","UPDATE");
                intent.putExtra("P_PDI_ID", pdi_id);
                intent.putExtra("P_PMM_ID", pmm_id);
                intent.putExtra("P_DM_NAME", dm_name);
                intent.putExtra("POSITION",getAdapterPosition());
                activity.startActivity(intent);

            });
        }

        @Override
        public void onClick(View v) {
            mClickedItem.onCategoryClicked(getAdapterPosition());
            //Log.i("Name", mCategoryItem.get(getAdapterPosition()).getName());
        }
    }

    public interface ClickedItem {
        void onCategoryClicked(int CategoryPosition);
    }
}
