package ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists.VoucherLists1;
import ttit.com.shuvo.docdiary.hr_accounts.vouch_trans.arraylists.VoucherLists2;

public class VouchTrans1Adapter extends RecyclerView.Adapter<VouchTrans1Adapter.VT1Holder> {

    private ArrayList<VoucherLists1> mCategoryItem;
    private final Context myContext;

    public VouchTrans1Adapter(ArrayList<VoucherLists1> mCategoryItem, Context myContext) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public VT1Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.v_trans_view_1, parent, false);
        return new VT1Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VT1Holder holder, int position) {
        VoucherLists1 categoryItem = mCategoryItem.get(position);

        holder.vDate.setText(categoryItem.getvDate());

        ArrayList<VoucherLists2> voucherLists2s = categoryItem.getVoucherLists2s();

        holder.itemDetails.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(myContext);
        holder.itemDetails.setLayoutManager(layoutManager);
        VouchTrans2Adapter vouchTrans2Adapter = new VouchTrans2Adapter(voucherLists2s,myContext);
        holder.itemDetails.setAdapter(vouchTrans2Adapter);

        if (position == mCategoryItem.size()-1) {
            holder.bottomBorder.setVisibility(View.GONE);
        } else {
            holder.bottomBorder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public static class VT1Holder extends RecyclerView.ViewHolder {

        public TextView vDate;
        LinearLayout bottomBorder;
        public RecyclerView itemDetails;

        public VT1Holder(@NonNull View itemView) {
            super(itemView);
            vDate = itemView.findViewById(R.id.v_trans_date);
            bottomBorder = itemView.findViewById(R.id.layout_bottom_border_v_trans_first);
            itemDetails = itemView.findViewById(R.id.v_trans_report_view2);
        }
    }

    public void filterList(ArrayList<VoucherLists1> filteredList) {
        mCategoryItem = filteredList;
        notifyDataSetChanged();
    }
}
