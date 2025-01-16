package ttit.com.shuvo.docdiary.hr_accounts.acc_ledger_pat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.payment.arraylists.PrescriptionCodeList;

public class SearchPatientALAdapter extends RecyclerView.Adapter<SearchPatientALAdapter.SPALHolder> {
    private final ArrayList<PrescriptionCodeList> mCategoryItem;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public SearchPatientALAdapter(Context myContext, ClickedItem myClickedItem, ArrayList<PrescriptionCodeList> mCategoryItem) {
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
        this.mCategoryItem = mCategoryItem;
    }

    @NonNull
    @Override
    public SPALHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.patient_search_al_details_view, parent, false);
        return new SPALHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SPALHolder holder, int position) {
        PrescriptionCodeList prescriptionCodeList = mCategoryItem.get(position);
        holder.prescriptionCode.setText(prescriptionCodeList.getPh_sub_code());
        holder.patName.setText(prescriptionCodeList.getPat_name());
        holder.patCode.setText(prescriptionCodeList.getPat_code());
        holder.patDate.setText(prescriptionCodeList.getPat_date());
    }

    @Override
    public int getItemCount() {
        return mCategoryItem == null ? 0 : mCategoryItem.size();
    }

    public static class SPALHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView prescriptionCode;
        TextView patName;
        TextView patCode;
        TextView patDate;
        ClickedItem mClickedItem;

        public SPALHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            prescriptionCode = itemView.findViewById(R.id.prescription_code_search_pat_al_dl);
            patName = itemView.findViewById(R.id.patient_name_search_pat_al_dl);
            patCode = itemView.findViewById(R.id.patient_code_search_pat_al_dl);
            patDate = itemView.findViewById(R.id.patient_date_search_pat_al_dl);
            this.mClickedItem = ci;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickedItem.onItemClicked(getAdapterPosition());
        }
    }

    public interface ClickedItem {
        void onItemClicked(int Position);
    }
}
