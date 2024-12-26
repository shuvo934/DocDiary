package ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.arraylists.JournalVoucherDetailList;

public class JournalVoucherApprovedAdapter extends RecyclerView.Adapter<JournalVoucherApprovedAdapter.JVAHolder> {
    private final ArrayList<JournalVoucherDetailList> mCategoryItem;
    private final Context myContext;

    public JournalVoucherApprovedAdapter(ArrayList<JournalVoucherDetailList> mCategoryItem, Context myContext) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public JVAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.credit_voucher_approved_details_layout, parent, false);
        return new JVAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JVAHolder holder, int position) {
        JournalVoucherDetailList categoryItem = mCategoryItem.get(position);

        holder.acNo.setText(categoryItem.getAdCode());
        holder.acDetails.setText(categoryItem.getAdName());
        holder.chequeNo.setText(categoryItem.getVdChequeNo());
        holder.chequeDate.setText(categoryItem.getVdChequeDate());
        holder.vDebit.setText(categoryItem.getVdDebit());
        holder.vCredit.setText(categoryItem.getVdCredit());
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public static class JVAHolder extends RecyclerView.ViewHolder {
        public TextView acNo;
        public TextView acDetails;
        public TextView chequeNo;
        public TextView chequeDate;
        public TextView vDebit;
        public TextView vCredit;

        public JVAHolder(@NonNull View itemView) {
            super(itemView);
            acNo = itemView.findViewById(R.id.ac_no_voucher_approved);
            acDetails = itemView.findViewById(R.id.ac_details_voucher_approved);
            chequeNo = itemView.findViewById(R.id.cheque_no_voucher_approved);
            chequeDate = itemView.findViewById(R.id.cheque_date_voucher_approved);
            vDebit = itemView.findViewById(R.id.debit_amount_voucher_approved);
            vCredit = itemView.findViewById(R.id.credit_amount_voucher_approved);
        }
    }
}
