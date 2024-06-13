package ttit.com.shuvo.docdiary.payment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.payment.arraylists.PaymentCodeList;

public class SearchPaymentAdapter extends RecyclerView.Adapter<SearchPaymentAdapter.SPAYAHolder>{
    private ArrayList<PaymentCodeList> mCategoryItem;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public SearchPaymentAdapter(ArrayList<PaymentCodeList> mCategoryItem, Context myContext, ClickedItem myClickedItem) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public SPAYAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.payment_code_search_details_view, parent, false);
        return new SPAYAHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SPAYAHolder holder, int position) {
        PaymentCodeList pa = mCategoryItem.get(position);
        holder.prescriptionCode.setText(pa.getPh_sub_code());
        holder.patName.setText(pa.getPat_name());
        holder.paymentCode.setText(pa.getPrm_code());
        String numb = pa.getPat_cell();
        if (!pa.getPat_cell().isEmpty()) {
            String first = numb.substring(0,3);
//            String last = numb.substring(3);
            String last = "";
            String mid = "";
            if (numb.length() > 3) {
                last = numb.substring(numb.length()-3);
                mid = numb.substring(3, numb.length() - 3);
            }

            mid = mid.replaceAll("[0-9]","*");
            numb = first + mid + last;
        }
        holder.patPhone.setText(numb);
        holder.payDate.setText(pa.getPrm_date());
    }

    @Override
    public int getItemCount() {
        return mCategoryItem == null ? 0 : mCategoryItem.size();
    }

    public static class SPAYAHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView prescriptionCode;
        TextView patName;
        TextView paymentCode;
        TextView patPhone;
        TextView payDate;
        ClickedItem mClickedItem;
        public SPAYAHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            paymentCode = itemView.findViewById(R.id.payment_code_search_update_payment);
            payDate = itemView.findViewById(R.id.payment_date_search_update_payment);
            prescriptionCode = itemView.findViewById(R.id.prescription_code_search_update_payment);
            patName = itemView.findViewById(R.id.patient_name_search_update_payment);
            patPhone = itemView.findViewById(R.id.patient_phone_search_update_payment);
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

    public void filterList(ArrayList<PaymentCodeList> filteredList) {
        mCategoryItem = filteredList;
        notifyDataSetChanged();
    }
}
