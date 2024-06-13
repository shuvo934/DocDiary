package ttit.com.shuvo.docdiary.payment.adapters;

import static ttit.com.shuvo.docdiary.payment.AddPayment.selected_pat_cat_id;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.payment.arraylists.UpdatedServiceList;
import ttit.com.shuvo.docdiary.payment.service.ServiceModifyForUp;

public class UpdatedServiceAdapter extends RecyclerView.Adapter<UpdatedServiceAdapter.USAHolder>{
    private ArrayList<UpdatedServiceList> updatedServiceLists;
    private Context mContext;

    public UpdatedServiceAdapter(ArrayList<UpdatedServiceList> updatedServiceLists, Context mContext) {
        this.updatedServiceLists = updatedServiceLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public USAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.updated_service_details_layout, parent, false);
        return new USAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull USAHolder holder, int position) {
        UpdatedServiceList list = updatedServiceLists.get(position);
        holder.serviceName.setText(list.getPfn_fee_name());
        holder.unitName.setText(list.getDepts_name());
        holder.serviceQty.setText(list.getPrd_qty());
        holder.serviceRate.setText(list.getPrd_rate());
        holder.serviceAmount.setText(list.getAmount());
        holder.availableQty.setText(list.getAvailable_qty());
    }

    @Override
    public int getItemCount() {
        return updatedServiceLists == null ? 0 : updatedServiceLists.size();
    }

    public class USAHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        TextView unitName;
        TextView serviceQty;
        TextView serviceRate;
        TextView serviceAmount;
        TextView availableQty;
        public USAHolder(@NonNull View itemView) {
            super(itemView);

            serviceName = itemView.findViewById(R.id.updated_service_name);
            unitName = itemView.findViewById(R.id.updated_unit_name);
            serviceQty = itemView.findViewById(R.id.updated_service_qty);
            serviceRate = itemView.findViewById(R.id.updated_service_rate);
            serviceAmount = itemView.findViewById(R.id.updated_service_amount);
            availableQty = itemView.findViewById(R.id.updated_service_avail_qty);

            availableQty.setOnClickListener(v -> {
                Spanned spanned = Html.fromHtml("Total Quantity:   <font color='black'><b>"+updatedServiceLists.get(getAdapterPosition()).getPrd_qty()+"</b></font><br>"+
                        "Schedule Taken:   <font color='black'><b>"+updatedServiceLists.get(getAdapterPosition()).getPrd_sched_avail_mark()+"</b></font><br>"+
                        "Cancel:   <font color='black'><b>"+updatedServiceLists.get(getAdapterPosition()).getPrd_cancel_mark()+"</b></font><br>"+
                        "Return:   <font color='black'><b>"+updatedServiceLists.get(getAdapterPosition()).getPrd_return_mark()+"</b></font><br>"+
                        "Available Quantity:   <font color='black'><b>"+updatedServiceLists.get(getAdapterPosition()).getAvailable_qty()+"</b></font><br>");

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setTitle(updatedServiceLists.get(getAdapterPosition()).getPfn_fee_name())
                        .setMessage(spanned)
                        .setPositiveButton("Close", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });

            itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String pfn_id = updatedServiceLists.get(getAdapterPosition()).getPrd_pfn_id();
                String pfn_name = updatedServiceLists.get(getAdapterPosition()).getPfn_fee_name();
                String depts_name = updatedServiceLists.get(getAdapterPosition()).getDepts_name();
                String depts_id = updatedServiceLists.get(getAdapterPosition()).getPrd_depts_id();
                String service_rate = updatedServiceLists.get(getAdapterPosition()).getPrd_rate();
                String service_top_rate = updatedServiceLists.get(getAdapterPosition()).getPrd_top_cat_rate();
                String service_qty = updatedServiceLists.get(getAdapterPosition()).getPrd_qty();
                String service_amount = updatedServiceLists.get(getAdapterPosition()).getAmount();
                String available_qty = updatedServiceLists.get(getAdapterPosition()).getAvailable_qty();
                String schedule_taken = updatedServiceLists.get(getAdapterPosition()).getPrd_sched_avail_mark();
                String cancel_qty = updatedServiceLists.get(getAdapterPosition()).getPrd_cancel_mark();
                String return_qty = updatedServiceLists.get(getAdapterPosition()).getPrd_return_mark();

                boolean found = false;
                for (int i = 0; i < updatedServiceLists.size(); i++) {
                    if (!updatedServiceLists.get(i).getPrd_sched_avail_mark().isEmpty()) {
                        int sch = Integer.parseInt(updatedServiceLists.get(i).getPrd_sched_avail_mark());
                        if (sch > 0) {
                            found = true;
                        }
                    }
                }
                if (found) {
                    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(mContext);
                    alertDialogBuilder.setTitle("Warning!")
                            .setMessage("Appointment Schedule already taken from this payment. You will not be able to update this payment.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                dialog.dismiss();
                            });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.setCancelable(false);
                    alert.setCanceledOnTouchOutside(false);
                    alert.show();
                }
                else {
                    Intent intent = new Intent(mContext, ServiceModifyForUp.class);
                    intent.putExtra("TYPE", "UPDATE");
                    intent.putExtra("PAT_CAT_ID", selected_pat_cat_id);
                    intent.putExtra("PFN_ID", pfn_id);
                    intent.putExtra("PFN_NAME", pfn_name);
                    intent.putExtra("DEPTS_NAME", depts_name);
                    intent.putExtra("DEPTS_ID", depts_id);
                    intent.putExtra("RATE", service_rate);
                    intent.putExtra("TOP_RATE", service_top_rate);
                    intent.putExtra("QUANTITY", service_qty);
                    intent.putExtra("AMOUNT", service_amount);
                    intent.putExtra("AVAIL_QTY", available_qty);
                    intent.putExtra("SCH_QTY", schedule_taken);
                    intent.putExtra("CANCEL_QTY", cancel_qty);
                    intent.putExtra("RETURN_QTY", return_qty);
                    intent.putExtra("POSITION", getAdapterPosition());
                    activity.startActivity(intent);
                }
            });
        }
    }
}
