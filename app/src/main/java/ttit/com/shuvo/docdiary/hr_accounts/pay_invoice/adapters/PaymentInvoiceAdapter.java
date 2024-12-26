package ttit.com.shuvo.docdiary.hr_accounts.pay_invoice.adapters;

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
import ttit.com.shuvo.docdiary.hr_accounts.pay_invoice.arraylists.PaymentInvoiceItemList;

public class PaymentInvoiceAdapter extends RecyclerView.Adapter<PaymentInvoiceAdapter.PIAHolder> {

    private final ArrayList<PaymentInvoiceItemList> mCategoryItem;
    private final Context myContext;

    public PaymentInvoiceAdapter(ArrayList<PaymentInvoiceItemList> mCategoryItem, Context myContext) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public PIAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.payment_invoice_item_details_layout, parent, false);
        return new PIAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PIAHolder holder, int position) {
        PaymentInvoiceItemList invoiceItemList = mCategoryItem.get(position);

        holder.slNo.setText(invoiceItemList.getSl_no());
        holder.paymentName.setText(invoiceItemList.getPfn_fee_name());
        holder.unitName.setText(invoiceItemList.getDepts_name());

        if (invoiceItemList.getPrd_payment_for_month().isEmpty()) {
            holder.monthLay.setVisibility(View.GONE);
        }
        else {
            holder.monthLay.setVisibility(View.VISIBLE);
        }

        holder.monthName.setText(invoiceItemList.getPrd_payment_for_month());

        holder.qty.setText(invoiceItemList.getPrd_qty());

        if (invoiceItemList.getPrd_rate().equals("0") || invoiceItemList.getPrd_rate().isEmpty()) {
            String tt = "Free of Cost";
            holder.s_rate.setText(tt);
        }
        else {
            holder.s_rate.setText(invoiceItemList.getPrd_rate());
        }

        if (invoiceItemList.getAmt().equals("0") || invoiceItemList.getAmt().isEmpty()) {
            String tt = "Free of Cost";
            holder.s_total.setText(tt);
        }
        else {
            holder.s_total.setText(invoiceItemList.getAmt());
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryItem != null ? mCategoryItem.size() : 0;
    }

    public static class PIAHolder extends RecyclerView.ViewHolder {

        public TextView slNo;
        public TextView paymentName;
        public TextView unitName;
        public LinearLayout monthLay;
        public TextView monthName;
        public TextView qty;
        public TextView s_rate;
        public TextView s_total;

        public PIAHolder(@NonNull View itemView) {
            super(itemView);
            slNo = itemView.findViewById(R.id.sl_no_of_pay_inv);
            paymentName = itemView.findViewById(R.id.payment_name_in_pay_inv);
            unitName = itemView.findViewById(R.id.unit_name_in_pay_inv);
            monthLay = itemView.findViewById(R.id.month_for_school_lay_in_pay_inv);
            monthName = itemView.findViewById(R.id.month_for_school_in_pay_inv);
            qty = itemView.findViewById(R.id.service_quantity_in_pay_inv);
            s_rate = itemView.findViewById(R.id.service_rate_in_pay_inv);
            s_total = itemView.findViewById(R.id.total_amount_in_pay_inv);

        }
    }
}
