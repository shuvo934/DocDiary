package ttit.com.shuvo.docdiary.payment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.payment.arraylists.SelectedPaymentMethodList;

public class SelectedPayMethodAdapter extends RecyclerView.Adapter<SelectedPayMethodAdapter.SPMAHolder> {
    private final ArrayList<SelectedPaymentMethodList> selectedPaymentMethodLists;
    private final Context mContext;

    public SelectedPayMethodAdapter(ArrayList<SelectedPaymentMethodList> selectedPaymentMethodLists, Context mContext) {
        this.selectedPaymentMethodLists = selectedPaymentMethodLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SPMAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.saved_payment_mode_list_view, parent, false);
        return new SPMAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SPMAHolder holder, int position) {
        SelectedPaymentMethodList paymentMethodList = selectedPaymentMethodLists.get(position);
        holder.slNo.setText(paymentMethodList.getSl_no());
        holder.pmName.setText(paymentMethodList.getPmm_name());
        holder.acName.setText(paymentMethodList.getAccount_name());
        holder.pmAmount.setText(paymentMethodList.getMethod_amount());
    }

    @Override
    public int getItemCount() {
        return selectedPaymentMethodLists == null ? 0 : selectedPaymentMethodLists.size();
    }

    public class SPMAHolder extends RecyclerView.ViewHolder {
        TextView slNo;
        TextView pmName;
        TextView acName;
        TextView pmAmount;
        ImageView delete;

        public SPMAHolder(@NonNull View itemView) {
            super(itemView);

            slNo = itemView.findViewById(R.id.no_of_payment_method);
            pmName = itemView.findViewById(R.id.payment_method_name_selected);
            acName = itemView.findViewById(R.id.account_name_selected);
            pmAmount = itemView.findViewById(R.id.amount_of_payment_method_selected);
            delete = itemView.findViewById(R.id.delete_payment_method);

            delete.setOnClickListener(v -> {
                selectedPaymentMethodLists.remove(getAdapterPosition());
                for (int i = 0; i < selectedPaymentMethodLists.size(); i++) {
                    selectedPaymentMethodLists.get(i).setSl_no(String.valueOf(i+1));
                }
                Toast.makeText(mContext,"Payment Method removed successfully",Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            });

        }
    }
}
