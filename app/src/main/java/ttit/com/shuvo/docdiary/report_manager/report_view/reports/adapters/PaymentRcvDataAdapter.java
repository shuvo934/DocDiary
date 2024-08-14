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
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists.PaymentReceiveDataList;

public class PaymentRcvDataAdapter extends RecyclerView.Adapter<PaymentRcvDataAdapter.PRDHolder> {

    private final ArrayList<PaymentReceiveDataList> dataLists;
    private final Context mContext;

    public PaymentRcvDataAdapter(Context mContext, ArrayList<PaymentReceiveDataList> dataLists) {
        this.mContext = mContext;
        this.dataLists = dataLists;
    }

    @NonNull
    @Override
    public PRDHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.payment_rcv_app_made_report_view, parent, false);
        return new PRDHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PRDHolder holder, int position) {
        PaymentReceiveDataList paymentReceiveDataList = dataLists.get(position);
        holder.amount.setText(paymentReceiveDataList.getTot_amt());
        holder.rate.setText(paymentReceiveDataList.getPrd_rate());
        holder.servQty.setText(paymentReceiveDataList.getTot_qty());
        if (position > 0) {
            int index = position - 1;

            String dep_name_pre = dataLists.get(index).getDeptd_name();
            String dep_name = paymentReceiveDataList.getDeptd_name();

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
            String unit_name = paymentReceiveDataList.getDepts_name();
            if (unit_name.equals(unit_name_pre)) {
                if (dep_name.equals("〃")) {
                    unit_name = "〃";
                }
            }
            holder.unitName.setText(unit_name);

            String srv_name_pre = dataLists.get(index).getPfn_fee_name();
            String srv_name = paymentReceiveDataList.getPfn_fee_name();
            if (srv_name.equals(srv_name_pre)) {
                if (unit_name.equals("〃")) {
                    srv_name = "〃";
                }
            }
            holder.serviceName.setText(srv_name);
        }
        else {
            holder.serviceName.setText(paymentReceiveDataList.getPfn_fee_name());
            holder.unitName.setText(paymentReceiveDataList.getDepts_name());
            holder.depName.setText(paymentReceiveDataList.getDeptd_name());

            if (position == dataLists.size() - 1) {
                holder.itemDivider.setVisibility(View.VISIBLE);
            }
            else if (position != dataLists.size() - 1 && position < dataLists.size() - 1 ) {
                int index = position + 1;

                String dep_name_post = dataLists.get(index).getDeptd_name();
                String dep_name = paymentReceiveDataList.getDeptd_name();

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

    public static class PRDHolder extends RecyclerView.ViewHolder {

        TextView depName;
        TextView unitName;
        TextView serviceName;
        TextView servQty;
        TextView rate;
        TextView amount;
        LinearLayout itemDivider;

        public PRDHolder(@NonNull View itemView) {
            super(itemView);

            depName = itemView.findViewById(R.id.payment_rcv_rep_dep_name);
            unitName = itemView.findViewById(R.id.payment_rcv_rep_unit_name);
            serviceName = itemView.findViewById(R.id.payment_rcv_rep_service_name);
            servQty = itemView.findViewById(R.id.payment_rcv_rep_qty);
            rate = itemView.findViewById(R.id.payment_rcv_rep_rate);
            amount = itemView.findViewById(R.id.payment_rcv_rep_amount);
            itemDivider = itemView.findViewById(R.id.divider_for_payment_rcv_rep_val);
        }
    }
}
