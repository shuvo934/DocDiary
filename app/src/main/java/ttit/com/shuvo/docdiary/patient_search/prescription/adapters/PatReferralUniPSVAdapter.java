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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatReferralList;
import ttit.com.shuvo.docdiary.patient_search.prescription.PatPrescriptionV;

public class PatReferralUniPSVAdapter extends RecyclerView.Adapter<PatReferralUniPSVAdapter.PRUPSVHolder> {

    private final ArrayList<PatReferralList> mCategory;
    private final Context mContext;
    private final ClickedItem2 myClickedItem2;

    public PatReferralUniPSVAdapter(ArrayList<PatReferralList> mCategory, Context mContext, ClickedItem2 myClickedItem2) {
        this.mCategory = mCategory;
        this.mContext = mContext;
        this.myClickedItem2 = myClickedItem2;
    }

    @NonNull
    @Override
    public PRUPSVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_referral_unit_info_psv_layout, parent, false);
        return new PRUPSVHolder(view, myClickedItem2);
    }

    @Override
    public void onBindViewHolder(@NonNull PRUPSVHolder holder, int position) {
        PatReferralList patReferralList = mCategory.get(position);
        holder.deptsName.setText(patReferralList.getDepts_name());
        holder.docName.setText(patReferralList.getDoc_name());

        if(PatPrescriptionV.selectedPosition2PatPSV == position) {
            holder.deptsName.setBackgroundColor(mContext.getColor(R.color.disabled));
            holder.deptsName.setTextColor(mContext.getColor(R.color.white));
            holder.docName.setBackgroundColor(mContext.getColor(R.color.disabled));
            holder.docName.setTextColor(mContext.getColor(R.color.white));
        }
        else {
            holder.deptsName.setBackgroundColor(mContext.getColor(R.color.clouds));
            holder.deptsName.setTextColor(mContext.getColor(R.color.default_text_color));
            holder.docName.setBackgroundColor(mContext.getColor(R.color.clouds));
            holder.docName.setTextColor(mContext.getColor(R.color.default_text_color));
        }
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public interface ClickedItem2 {
        void onRefUnitClicked(int refUnitPosition);
    }

    public static class PRUPSVHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deptsName;
        TextView docName;
        ClickedItem2 mClickedItem2;
        public PRUPSVHolder(@NonNull View itemView, ClickedItem2 ci) {
            super(itemView);
            deptsName = itemView.findViewById(R.id.pat_referral_unit_depts_name_pat_psv);
            docName = itemView.findViewById(R.id.pat_referral_unit_doc_name_pat_psv);
            this.mClickedItem2 = ci;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickedItem2.onRefUnitClicked(getAdapterPosition());
        }
    }
}
