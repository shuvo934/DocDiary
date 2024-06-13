package ttit.com.shuvo.docdiary.payment.adapters;

import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_cat_id;
import static ttit.com.shuvo.docdiary.payment.AddPayment.totalAmount;
import static ttit.com.shuvo.docdiary.payment.AddPayment.totalAmountLay;
import static ttit.com.shuvo.docdiary.payment.AddPayment.total_amount;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.payment.arraylists.AddedServiceList;
import ttit.com.shuvo.docdiary.payment.service.ServiceModify;

public class AddedServiceAdapter extends RecyclerView.Adapter<AddedServiceAdapter.ADSHOlder> {
    private ArrayList<AddedServiceList> addedServiceLists;
    private Context mContext;

    public AddedServiceAdapter(ArrayList<AddedServiceList> addedServiceLists, Context mContext) {
        this.addedServiceLists = addedServiceLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ADSHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.added_service_details_layout, parent, false);
        return new ADSHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ADSHOlder holder, int position) {
        AddedServiceList list = addedServiceLists.get(position);
        holder.serviceName.setText(list.getPfn_name());
        holder.unitName.setText(list.getDepts_name());
        holder.serviceQty.setText(list.getService_qty());
        holder.serviceRate.setText(list.getService_rate());
        holder.serviceAmount.setText(list.getService_amnt());
    }

    @Override
    public int getItemCount() {
        return addedServiceLists == null ? 0 : addedServiceLists.size();
    }

    public class ADSHOlder extends RecyclerView.ViewHolder {
        TextView serviceName;
        TextView unitName;
        TextView serviceQty;
        TextView serviceRate;
        TextView serviceAmount;
        LinearLayout delete;

        public ADSHOlder(@NonNull View itemView) {
            super(itemView);

            serviceName = itemView.findViewById(R.id.added_service_name);
            unitName = itemView.findViewById(R.id.added_unit_name);
            serviceQty = itemView.findViewById(R.id.added_service_qty);
            serviceRate = itemView.findViewById(R.id.added_service_rate);
            serviceAmount = itemView.findViewById(R.id.added_service_amount);
            delete = itemView.findViewById(R.id.added_service_delete);

            delete.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setTitle(addedServiceLists.get(getAdapterPosition()).getPfn_name())
                        .setMessage("Do you want to delete this Service?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            addedServiceLists.remove(getAdapterPosition());
                            Toast.makeText(mContext,"Service Deleted Successfully",Toast.LENGTH_SHORT).show();
                            if (addedServiceLists.size() == 0) {
                                totalAmountLay.setVisibility(View.GONE);
                                total_amount = "";
                                totalAmount.setText(total_amount);
                            }
                            else {
                                totalAmountLay.setVisibility(View.VISIBLE);
                                int ttt = 0;
                                for (int j = 0; j < addedServiceLists.size(); j++) {
                                    int amnt = Integer.parseInt(addedServiceLists.get(j).getService_amnt());
                                    ttt = ttt + amnt;
                                }
                                total_amount = String.valueOf(ttt);
                                totalAmount.setText(total_amount);
                            }
                            notifyDataSetChanged();
                            System.out.println("Service List: " + addedServiceLists.size());
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });

            itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String pfn_id = addedServiceLists.get(getAdapterPosition()).getPfn_id();
                String pfn_name = addedServiceLists.get(getAdapterPosition()).getPfn_name();
                String depts_name = addedServiceLists.get(getAdapterPosition()).getDepts_name();
                String depts_id = addedServiceLists.get(getAdapterPosition()).getDepts_id();
                String service_rate = addedServiceLists.get(getAdapterPosition()).getService_rate();
                String service_top_rate = addedServiceLists.get(getAdapterPosition()).getService_top_rate();
                String service_qty = addedServiceLists.get(getAdapterPosition()).getService_qty();
                String service_amount = addedServiceLists.get(getAdapterPosition()).getService_amnt();

                Intent intent = new Intent(mContext, ServiceModify.class);
                intent.putExtra("TYPE","UPDATE");
                intent.putExtra("PAT_CAT_ID",selected_pat_cat_id);
                intent.putExtra("PFN_ID",pfn_id);
                intent.putExtra("PFN_NAME",pfn_name);
                intent.putExtra("DEPTS_NAME",depts_name);
                intent.putExtra("DEPTS_ID",depts_id);
                intent.putExtra("RATE",service_rate);
                intent.putExtra("TOP_RATE",service_top_rate);
                intent.putExtra("QUANTITY",service_qty);
                intent.putExtra("AMOUNT",service_amount);
                intent.putExtra("POSITION", getAdapterPosition());
                activity.startActivity(intent);

            });

        }
    }
}
