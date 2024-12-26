package ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists.DebitCreditVoucherItemList;

public class DebitCreditVoucherAdapter extends RecyclerView.Adapter<DebitCreditVoucherAdapter.DCVHolder> {

    private final ArrayList<DebitCreditVoucherItemList> mCategoryItem;
    private final Context myContext;

    public DebitCreditVoucherAdapter(ArrayList<DebitCreditVoucherItemList> mCategoryItem, Context myContext) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public DCVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.debit_credit_voucher_item_view, parent, false);
        return new DCVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DCVHolder holder, int position) {
        DebitCreditVoucherItemList debitCreditVoucherLists = mCategoryItem.get(position);

        holder.ac_no.setText(debitCreditVoucherLists.getAd_code());
        holder.headAc.setText(debitCreditVoucherLists.getAd_name());
        holder.debit.setText(debitCreditVoucherLists.getVd_dr_amt());
        holder.credit.setText(debitCreditVoucherLists.getVd_cr_amt());
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public static class DCVHolder extends RecyclerView.ViewHolder {

        public TextView ac_no;
        public TextView headAc;
        public TextView debit;
        public TextView credit;

        public DCVHolder(@NonNull View itemView) {
            super(itemView);

            ac_no = itemView.findViewById(R.id.ac_no_item_dv);
            headAc = itemView.findViewById(R.id.head_of_acc_dv);
            debit = itemView.findViewById(R.id.debit_dv);
            credit = itemView.findViewById(R.id.credit_dv);
        }
    }
}
