package ttit.com.shuvo.docdiary.appointment_admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.PaymentForAppList;

public class SearchPaymentForAppAdapter extends RecyclerView.Adapter<SearchPaymentForAppAdapter.SPFAHolder> {
    private ArrayList<PaymentForAppList> mCategoryItem;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public SearchPaymentForAppAdapter(ArrayList<PaymentForAppList> mCategoryItem, Context myContext, ClickedItem myClickedItem) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public SPFAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.payment_search_for_app_details_view, parent, false);
        return new SPFAHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SPFAHolder holder, int position) {
        PaymentForAppList p = mCategoryItem.get(position);
        holder.paymentCode.setText(p.getPrm_code());
        holder.paymentDate.setText(p.getPrm_date());
    }

    @Override
    public int getItemCount() {
        return mCategoryItem == null ? 0 : mCategoryItem.size();
    }

    public static class SPFAHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView paymentCode;
        TextView paymentDate;
        ClickedItem mClickedItem;
        public SPFAHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            paymentCode = itemView.findViewById(R.id.payment_code_for_pat_app);
            paymentDate = itemView.findViewById(R.id.payment_date_for_pat_app);
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

    public void filterList(ArrayList<PaymentForAppList> filteredList) {
        mCategoryItem = filteredList;
        notifyDataSetChanged();
    }
}
