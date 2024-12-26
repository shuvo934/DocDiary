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
import ttit.com.shuvo.docdiary.hr_accounts.vouch_approval.arraylists.VoucherSelectionList;

public class VoucherSelectAdapter extends RecyclerView.Adapter<VoucherSelectAdapter.VSHolder> {

    private final ArrayList<VoucherSelectionList> mCategoryItem;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public VoucherSelectAdapter(ArrayList<VoucherSelectionList> mCategoryItem, Context myContext, ClickedItem myClickedItem) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public VSHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.voucher_select_item_view, parent, false);
        return new VSHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull VSHolder holder, int position) {
        VoucherSelectionList categoryItem = mCategoryItem.get(position);

        holder.voucherNo.setText(categoryItem.getVm_no());
        holder.voucherDate.setText(categoryItem.getVm_date());
        holder.narration.setText(categoryItem.getVm_naration());
        holder.voucherStatus.setText(categoryItem.getStatus());
        if (categoryItem.getVm_voucher_approved_flag().equals("0")) {
            holder.voucherStatus.setTextColor(myContext.getColor(R.color.red_dark));
        }
        else {
            holder.voucherStatus.setTextColor(myContext.getColor(R.color.green_sea));
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public static class VSHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView voucherNo;
        TextView voucherDate;
        TextView narration;
        TextView voucherStatus;

        ClickedItem mClickedItem;

        public VSHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            voucherNo = itemView.findViewById(R.id.voucher_no_svnva);
            voucherDate = itemView.findViewById(R.id.voucher_date_svnva);
            narration = itemView.findViewById(R.id.voucher_narration_svnva);
            voucherStatus = itemView.findViewById(R.id.voucher_status_svnva);
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
