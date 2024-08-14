package ttit.com.shuvo.docdiary.report_manager.report_view.reports.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists.PaymentRcvSumDataList;

public class PaymentRcvSumDataAdapter extends RecyclerView.Adapter<PaymentRcvSumDataAdapter.PRSDHolder>{
    private final ArrayList<PaymentRcvSumDataList> dataLists;
    private final Context mContext;

    public PaymentRcvSumDataAdapter(ArrayList<PaymentRcvSumDataList> dataLists, Context mContext) {
        this.dataLists = dataLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PRSDHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.payment_rcv_sum_app_made_report_view, parent, false);
        return new PRSDHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PRSDHolder holder, int position) {
        PaymentRcvSumDataList paymentRcvSumDataList = dataLists.get(position);
        holder.amount.setText(paymentRcvSumDataList.getTot_amt());
        if (position > 0) {
            int index = position - 1;

            String dep_name_pre = dataLists.get(index).getDeptd_name();
            String dep_name = paymentRcvSumDataList.getDeptd_name();

            if (position == dataLists.size() - 1) {
                holder.itemDivider.setVisibility(View.VISIBLE);
            }
            else if (position != dataLists.size() - 1 && position < dataLists.size() - 1 ) {
                int dex = position + 1;
                String dep_name_post = dataLists.get(dex).getDeptd_name();
                if (dep_name.equals(dep_name_post)) {
                    holder.itemDivider.setVisibility(View.GONE);
                }
                else {
                    holder.itemDivider.setVisibility(View.VISIBLE);
                }
            }

            if (dep_name.equals(dep_name_pre)) {
                dep_name = "〃";
            }
            holder.depName.setText(dep_name);

            String unit_name_pre = dataLists.get(index).getDepts_name();
            String unit_name = paymentRcvSumDataList.getDepts_name();
            if (unit_name.equals(unit_name_pre)) {
                if (dep_name.equals("〃")) {
                    unit_name = "〃";
                }
            }
            holder.unitName.setText(unit_name);

        }
        else {
            holder.unitName.setText(paymentRcvSumDataList.getDepts_name());
            holder.depName.setText(paymentRcvSumDataList.getDeptd_name());

            if (position == dataLists.size() - 1) {
                holder.itemDivider.setVisibility(View.VISIBLE);
            }
            else if (position != dataLists.size() - 1 && position < dataLists.size() - 1 ) {
                int index = position + 1;

                String dep_name_post = dataLists.get(index).getDeptd_name();
                String dep_name = paymentRcvSumDataList.getDeptd_name();

                if (dep_name.equals(dep_name_post)) {
                    holder.itemDivider.setVisibility(View.GONE);
                }
                else {
                    holder.itemDivider.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataLists == null ? 0 : dataLists.size();
    }

    public static class PRSDHolder extends RecyclerView.ViewHolder {

        TextView depName;
        TextView unitName;
        TextView amount;
        LinearLayout itemDivider;

        public PRSDHolder(@NonNull View itemView) {
            super(itemView);

            depName = itemView.findViewById(R.id.payment_rcv_sum_rep_dep_name);
            unitName = itemView.findViewById(R.id.payment_rcv_sum_rep_unit_name);
            amount = itemView.findViewById(R.id.payment_rcv_sum_rep_amount);
            itemDivider = itemView.findViewById(R.id.divider_for_payment_rcv_sum_rep_val);
        }
    }
}
