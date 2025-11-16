package ttit.com.shuvo.docdiary.hr_accounts.dashboards.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.hr_accounts.dashboards.arraylists.OpasPayCardWiseList;


public class CardWisePayAdapter extends RecyclerView.Adapter<CardWisePayAdapter.CWPHolder>{
    private final ArrayList<OpasPayCardWiseList> dataLists;
    private final Context mContext;

    public CardWisePayAdapter(ArrayList<OpasPayCardWiseList> dataLists, Context mContext) {
        this.dataLists = dataLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CWPHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_wise_payment_view, parent, false);
        return new CWPHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CWPHolder holder, int position) {
        OpasPayCardWiseList opasPayCardWiseList = dataLists.get(position);
        holder.cardName.setText(opasPayCardWiseList.getCard_name());

        int total_pay = Integer.parseInt(opasPayCardWiseList.getAll_pay_count() != null ? opasPayCardWiseList.getAll_pay_count(): "0");
        int com_pay = Integer.parseInt(opasPayCardWiseList.getC_pay_count() != null ? opasPayCardWiseList.getC_pay_count(): "0");
        int fail_pay = Integer.parseInt(opasPayCardWiseList.getF_pay_count() != null ? opasPayCardWiseList.getF_pay_count(): "0");
        int nr_pay = Integer.parseInt(opasPayCardWiseList.getNa_pay_count() != null ? opasPayCardWiseList.getNa_pay_count(): "0");

        com_pay = (int) Math.round((com_pay * 100.0) / total_pay);
        fail_pay = (int) Math.round((fail_pay * 100.0) / total_pay);
        nr_pay = (int) Math.round((nr_pay * 100.0) / total_pay);

        holder.totalPay.setText(String.valueOf(total_pay));
        String ct = opasPayCardWiseList.getC_pay_count() + " (" + com_pay + "%)";
        String ft = opasPayCardWiseList.getF_pay_count() + " (" + fail_pay + "%)";
        String nt = opasPayCardWiseList.getNa_pay_count() + " (" + nr_pay + "%)";

        SpannableString ctSpannable = new SpannableString(ct);
        int start = ct.indexOf('(');
        int end = ct.indexOf(')') + 1;

        ctSpannable.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.green_sea)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ctSpannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.comPay.setText(ctSpannable);

        SpannableString ftSpannable = new SpannableString(ft);
        int ftStart = ft.indexOf('(');
        int ftEnd = ft.indexOf(')') + 1;

        ftSpannable.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.red_dark)), ftStart, ftEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ftSpannable.setSpan(new StyleSpan(Typeface.BOLD), ftStart, ftEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.failPay.setText(ftSpannable);

        SpannableString ntSpannable = new SpannableString(nt);
        int ntStart = nt.indexOf('(');
        int ntEnd = nt.indexOf(')') + 1;

        ntSpannable.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.back_color)), ntStart, ntEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ntSpannable.setSpan(new StyleSpan(Typeface.BOLD), ntStart, ntEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.nrPay.setText(ntSpannable);

        if (position == dataLists.size() - 1) {
            holder.itemDivider.setVisibility(View.GONE);
        }
        else {
            holder.itemDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataLists == null ? 0 : dataLists.size();
    }

    public static class CWPHolder extends RecyclerView.ViewHolder {

        TextView cardName;
        TextView totalPay;
        TextView comPay;
        TextView failPay;
        TextView nrPay;
        LinearLayout itemDivider;

        public CWPHolder(@NonNull View itemView) {
            super(itemView);

            cardName = itemView.findViewById(R.id.card_name_of_card_wise_pay);
            totalPay = itemView.findViewById(R.id.total_of_card_wise_pay);
            comPay = itemView.findViewById(R.id.completed_of_card_wise_pay);
            failPay = itemView.findViewById(R.id.failed_of_card_wise_pay);
            nrPay = itemView.findViewById(R.id.no_result_of_card_wise_pay);
            itemDivider = itemView.findViewById(R.id.divider_for_card_wise_pay);
        }
    }
}
