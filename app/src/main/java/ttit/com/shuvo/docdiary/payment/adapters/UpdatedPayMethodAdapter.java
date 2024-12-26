package ttit.com.shuvo.docdiary.payment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.payment.arraylists.UpdatedPaymentMethodList;

public class UpdatedPayMethodAdapter extends RecyclerView.Adapter<UpdatedPayMethodAdapter.UPMAHolder> {
    private final ArrayList<UpdatedPaymentMethodList> updatedPaymentMethodLists;
    private final Context mContext;

    public UpdatedPayMethodAdapter(ArrayList<UpdatedPaymentMethodList> updatedPaymentMethodLists, Context mContext) {
        this.updatedPaymentMethodLists = updatedPaymentMethodLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public UPMAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.saved_payment_mode_list_view, parent, false);
        return new UPMAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UPMAHolder holder, int position) {
        UpdatedPaymentMethodList paymentMethodList = updatedPaymentMethodLists.get(position);
        holder.slNo.setText(paymentMethodList.getSl_no());
        holder.pmName.setText(paymentMethodList.getPmm_name());
        holder.acName.setText(paymentMethodList.getAccount_name());
        holder.pmAmount.setText(paymentMethodList.getPrmd_amt());
    }

    @Override
    public int getItemCount() {
        return updatedPaymentMethodLists == null ? 0 : updatedPaymentMethodLists.size();
    }

    public class UPMAHolder extends RecyclerView.ViewHolder {
        TextView slNo;
        TextView pmName;
        TextView acName;
        TextView pmAmount;
        ImageView delete;

        public UPMAHolder(@NonNull View itemView) {
            super(itemView);

            slNo = itemView.findViewById(R.id.no_of_payment_method);
            pmName = itemView.findViewById(R.id.payment_method_name_selected);
            acName = itemView.findViewById(R.id.account_name_selected);
            pmAmount = itemView.findViewById(R.id.amount_of_payment_method_selected);
            delete = itemView.findViewById(R.id.delete_payment_method);

            delete.setOnClickListener(v -> {
                updatedPaymentMethodLists.remove(getAdapterPosition());
                for (int i = 0; i < updatedPaymentMethodLists.size(); i++) {
                    updatedPaymentMethodLists.get(i).setSl_no(String.valueOf(i+1));
                }
                Toast.makeText(mContext,"Payment Method removed successfully",Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            });

        }
    }
}
