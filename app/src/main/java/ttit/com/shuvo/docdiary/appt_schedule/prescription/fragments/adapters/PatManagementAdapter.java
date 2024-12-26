package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.adapters;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.PrescriptionSetup;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.ManagementPlan;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.ManagementModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatManagementList;

public class PatManagementAdapter extends RecyclerView.Adapter<PatManagementAdapter.PMAHolder> {
    private final ArrayList<PatManagementList> mCategory;
    private final Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";

    public PatManagementAdapter(ArrayList<PatManagementList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    Logger logger = Logger.getLogger("PatManagementAdapter");
    
    @NonNull
    @Override
    public PMAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_management_plan_details_layout, parent, false);
        return new PMAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PMAHolder holder, int position) {
        PatManagementList patManagementList = mCategory.get(position);
        holder.managementPlan.setText(patManagementList.getPmap_details());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class PMAHolder extends RecyclerView.ViewHolder {
        TextView managementPlan;
        ImageView delete;

        public PMAHolder(@NonNull View itemView) {
            super(itemView);
            managementPlan = itemView.findViewById(R.id.pat_management_plan_for_patient);
            delete = itemView.findViewById(R.id.pat_management_plan_delete);

            itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String pmap_id = mCategory.get(getAdapterPosition()).getPmap_id();
                String pmm_id = mCategory.get(getAdapterPosition()).getPmap_pmm_id();
                String pmapDetails = mCategory.get(getAdapterPosition()).getPmap_details();

                Intent intent = new Intent(mContext, ManagementModify.class);
                intent.putExtra("P_PMM_ID", pmm_id);
                intent.putExtra("MANAGE_MODIFY_TYPE", "UPDATE");
                intent.putExtra("P_PMAP_ID", pmap_id);
                intent.putExtra("P_DETAILS", pmapDetails);
                activity.startActivity(intent);
            });

            delete.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setMessage("Do you want to delete this Management Plan?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String pmap_id = mCategory.get(getAdapterPosition()).getPmap_id();
                            System.out.println(pmap_id);
                            deleteManagement(pmap_id,getAdapterPosition());
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    }

    public void deleteManagement(String pmap_id, int index) {
        PrescriptionSetup.circularProgressIndicator.setVisibility(View.VISIBLE);
        PrescriptionSetup.fullLayout.setVisibility(View.GONE);
        PrescriptionSetup.prescriptionLoading = true;
        conn = false;
        connected = false;

        String deleteManageUrl = pre_url_api+"prescription/deleteManagement";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest request = new StringRequest(Request.Method.POST, deleteManageUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterDeleteMang(pmap_id,index);
                }
                else {
                    connected = false;
                    updateAfterDeleteMang(pmap_id,index);
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAfterDeleteMang(pmap_id,index);
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAfterDeleteMang(pmap_id,index);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PMAP_ID",pmap_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void updateAfterDeleteMang(String pmap_id,int pos) {
        if (conn) {
            if (connected) {
                PrescriptionSetup.circularProgressIndicator.setVisibility(View.GONE);
                PrescriptionSetup.fullLayout.setVisibility(View.VISIBLE);
                Toast.makeText(mContext,"Management Plan Deleted Successfully",Toast.LENGTH_SHORT).show();
                mCategory.remove(pos);
                notifyDataSetChanged();
                if (mCategory.isEmpty()) {
                    ManagementPlan.noManagementMsg.setVisibility(View.VISIBLE);
                }
                else {
                    ManagementPlan.noManagementMsg.setVisibility(View.GONE);
                }
                PrescriptionSetup.prescriptionLoading = false;
            }
            else {
                alertMessageForFailedDeleteMang(pmap_id,pos);
            }
        }
        else {
            alertMessageForFailedDeleteMang(pmap_id,pos);
        }
    }

    public void alertMessageForFailedDeleteMang(String pmap_id, int pos) {
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
                    deleteManagement(pmap_id,pos);
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
