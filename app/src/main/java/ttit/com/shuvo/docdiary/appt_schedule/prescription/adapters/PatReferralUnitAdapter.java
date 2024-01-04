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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.RefServiceModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.ReferralUnitModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatReferralList;

public class PatReferralUnitAdapter extends RecyclerView.Adapter<PatReferralUnitAdapter.PRUHolder> {
    private ArrayList<PatReferralList> mCategory;
    private Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;

    String parsing_message = "";
    private final ClickedItem2 myClickedItem2;

    public PatReferralUnitAdapter(ArrayList<PatReferralList> mCategory, Context mContext,ClickedItem2 cli) {
        this.mCategory = mCategory;
        this.mContext = mContext;
        this.myClickedItem2 = cli;
    }

    @NonNull
    @Override
    public PRUHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_referral_unit_info_layout, parent, false);
        return new PRUHolder(view, myClickedItem2);
    }

    @Override
    public void onBindViewHolder(@NonNull PRUHolder holder, int position) {
        PatReferralList patReferralList = mCategory.get(position);
        holder.deptsName.setText(patReferralList.getDepts_name());
        holder.docName.setText(patReferralList.getDoc_name());

        if(PrescriptionSetup.selectedPosition2 == position) {
            holder.deptsName.setBackgroundColor(mContext.getColor(R.color.back_color));
            holder.deptsName.setTextColor(mContext.getColor(R.color.white));
            holder.docName.setBackgroundColor(mContext.getColor(R.color.back_color));
            holder.docName.setTextColor(mContext.getColor(R.color.white));
        }
        else {
            holder.deptsName.setBackgroundColor(mContext.getColor(R.color.clouds));
            holder.deptsName.setTextColor(mContext.getColor(R.color.default_text_color));
            holder.docName.setBackgroundColor(mContext.getColor(R.color.clouds));
            holder.docName.setTextColor(mContext.getColor(R.color.default_text_color));
        }

        if (patReferralList.getChild_count().equals("0")) {
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

    public class PRUHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deptsName;
        TextView docName;
        ImageView edit;
//        ImageView delete;
        ClickedItem2 mClickedItem2;

        public PRUHolder(@NonNull View itemView, ClickedItem2 ci) {
            super(itemView);

            deptsName = itemView.findViewById(R.id.pat_referral_unit_depts_name);
            docName = itemView.findViewById(R.id.pat_referral_unit_doc_name);
            edit = itemView.findViewById(R.id.pat_referral_unit_edit);
//            delete = itemView.findViewById(R.id.pat_referral_unit_delete);
            this.mClickedItem2 = ci;

            itemView.setOnClickListener(this);

            edit.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String drd_id = mCategory.get(getAdapterPosition()).getDrd_id();
                String deptsId = mCategory.get(getAdapterPosition()).getDepts_id();
                String pdiId = mCategory.get(getAdapterPosition()).getDrd_pdi_id();
                String depts_name = mCategory.get(getAdapterPosition()).getDepts_name();
                String doc_id = mCategory.get(getAdapterPosition()).getDoc_id();
                String doc_name = mCategory.get(getAdapterPosition()).getDoc_name();

                Intent intent = new Intent(mContext, ReferralUnitModify.class);
                intent.putExtra("P_DRD_ID", drd_id);
                intent.putExtra("REF_MODIFY_TYPE","UPDATE");
                intent.putExtra("P_PDI_ID", pdiId);
                intent.putExtra("P_DEPTS_ID", deptsId);
                intent.putExtra("P_DEPTS_NAME", depts_name);
                intent.putExtra("P_DOC_NAME", doc_name);
                intent.putExtra("P_DOC_ID", doc_id);
                intent.putExtra("POSITION",getAdapterPosition());
                activity.startActivity(intent);

            });

        }
        @Override
        public void onClick(View v) {
            mClickedItem2.onCategoryClicked2(getAdapterPosition());
            //Log.i("Name", mCategoryItem.get(getAdapterPosition()).getName());
        }
    }
    public interface ClickedItem2 {
        void onCategoryClicked2(int CategoryPosition2);
    }
}
