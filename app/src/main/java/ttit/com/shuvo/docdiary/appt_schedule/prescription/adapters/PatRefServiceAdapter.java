package ttit.com.shuvo.docdiary.appt_schedule.prescription.adapters;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.PrescriptionSetup;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.RefServiceModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.PatRefServiceList;

public class PatRefServiceAdapter extends RecyclerView.Adapter<PatRefServiceAdapter.PRSHolder> {
    private ArrayList<PatRefServiceList> mCategory;
    private Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;

    String parsing_message = "";

    public PatRefServiceAdapter(ArrayList<PatRefServiceList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PRSHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_ref_service_info_layout, parent, false);
        return new PRSHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PRSHolder holder, int position) {
        PatRefServiceList p = mCategory.get(position);
        holder.serviceName.setText(p.getPfn_fee_name());
        holder.quantity.setText(p.getDrs_qty());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class PRSHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        TextView quantity;
        ImageView delete;
        public PRSHolder(@NonNull View itemView) {
            super(itemView);

            serviceName = itemView.findViewById(R.id.pat_ref_service_name);
            quantity = itemView.findViewById(R.id.pat_ref_service_qty);
            delete = itemView.findViewById(R.id.pat_ref_service_delete);

            itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String drs_id = mCategory.get(getAdapterPosition()).getDrs_id();
                String pfn_id = mCategory.get(getAdapterPosition()).getPfn_id();
                String drd_id = mCategory.get(getAdapterPosition()).getDrs_drd_id();
                String pfnFeeName = mCategory.get(getAdapterPosition()).getPfn_fee_name();
                String drsQty = mCategory.get(getAdapterPosition()).getDrs_qty();
                String depts_id = mCategory.get(getAdapterPosition()).getDepts_id();

                Intent intent = new Intent(mContext, RefServiceModify.class);
                intent.putExtra("P_DRD_ID", drd_id);
                intent.putExtra("SERVICE_MODIFY_TYPE","UPDATE");
                intent.putExtra("P_DEPTS_ID", depts_id);
                intent.putExtra("P_DRS_ID", drs_id);
                intent.putExtra("P_PFN_ID", pfn_id);
                intent.putExtra("P_PFN_NAME", pfnFeeName);
                intent.putExtra("P_QTY", drsQty);
                activity.startActivity(intent);

            });

            delete.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setTitle(mCategory.get(getAdapterPosition()).getPfn_fee_name())
                        .setMessage("Do you want to delete this Service?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String drs_id = mCategory.get(getAdapterPosition()).getDrs_id();
                            System.out.println(drs_id);
                            deleteService(drs_id,getAdapterPosition());
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    }

    public void deleteService(String drs_id, int index) {
        PrescriptionSetup.circularProgressIndicator.setVisibility(View.VISIBLE);
        PrescriptionSetup.fullLayout.setVisibility(View.GONE);
        PrescriptionSetup.prescriptionLoading = true;
        conn = false;
        connected = false;

        String deleteSLVUrl = pre_url_api+"prescription/deleteRefService";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest request = new StringRequest(Request.Method.POST, deleteSLVUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterDeleteSLV(drs_id,index);
                }
                else {
                    connected = false;
                    updateAfterDeleteSLV(drs_id,index);
                }
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateAfterDeleteSLV(drs_id,index);
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateAfterDeleteSLV(drs_id,index);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_DRS_ID",drs_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void updateAfterDeleteSLV(String drs_id,int pos) {
        if (conn) {
            if (connected) {
                PrescriptionSetup.circularProgressIndicator.setVisibility(View.GONE);
                PrescriptionSetup.fullLayout.setVisibility(View.VISIBLE);
                Toast.makeText(mContext,"Service Deleted Successfully",Toast.LENGTH_SHORT).show();
                mCategory.remove(pos);
                PrescriptionSetup.patReferralLists.get(PrescriptionSetup.selectedPosition2).setChild_count(String.valueOf(mCategory.size()));
                notifyDataSetChanged();
                PrescriptionSetup.updateDiagnosis();
                PrescriptionSetup.prescriptionLoading = false;
            }
            else {
                alertMessageForFailedDeleteSLV(drs_id,pos);
            }
        }
        else {
            alertMessageForFailedDeleteSLV(drs_id,pos);
        }
    }

    public void alertMessageForFailedDeleteSLV(String drs_id, int pos) {
        PrescriptionSetup.circularProgressIndicator.setVisibility(View.GONE);
        PrescriptionSetup.fullLayout.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(mContext);
        alertDialogBuilder.setTitle("Error!")
                .setMessage("Error Message: "+parsing_message+".\n"+"Please try again.")
                .setPositiveButton("Retry", (dialog, which) -> {
                    PrescriptionSetup.prescriptionLoading = false;
                    deleteService(drs_id,pos);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    PrescriptionSetup.prescriptionLoading = false;
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}
