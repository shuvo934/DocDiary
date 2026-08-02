package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.adapters;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.annotation.SuppressLint;
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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.ClinicalFindingsModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatClinicalFindingsList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.interfaces.FragmentAdapterCallBack;

public class PatClFindingsAdapter extends RecyclerView.Adapter<PatClFindingsAdapter.PCFHolder> {

    private final ArrayList<PatClinicalFindingsList> mCategory;
    private final Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";
    private final FragmentAdapterCallBack callback;

    public PatClFindingsAdapter(ArrayList<PatClinicalFindingsList> mCategory, Context mContext, FragmentAdapterCallBack callback) {
        this.mCategory = mCategory;
        this.mContext = mContext;
        this.callback = callback;
    }

    Logger logger = Logger.getLogger("PatClFindingsAdapter");

    @NonNull
    @Override
    public PCFHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_medic_history_details_layout, parent, false);
        return new PCFHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PCFHolder holder, int position) {
        PatClinicalFindingsList patClinicalFindingsList = mCategory.get(position);
        holder.patClFindings.setText(patClinicalFindingsList.getCfm_name());
        holder.patFindingsDetails.setText(patClinicalFindingsList.getCf_details());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class PCFHolder extends RecyclerView.ViewHolder {
        TextView patClFindings;
        TextView patFindingsDetails;
        ImageView delete;

        public PCFHolder(@NonNull View itemView) {
            super(itemView);
            patClFindings = itemView.findViewById(R.id.pat_medic_history_mhm_name);
            patFindingsDetails = itemView.findViewById(R.id.pat_medic_history_pmh_details);
            delete = itemView.findViewById(R.id.pat_medic_history_delete);

            itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String cf_id = mCategory.get(getAdapterPosition()).getCf_id();
                String pmm_id = mCategory.get(getAdapterPosition()).getCf_pmm_id();
                String cfm_id = mCategory.get(getAdapterPosition()).getCfm_id();
                String cfm_name = mCategory.get(getAdapterPosition()).getCfm_name();
                String details = mCategory.get(getAdapterPosition()).getCf_details();

                Intent intent = new Intent(mContext, ClinicalFindingsModify.class);
                intent.putExtra("P_PMM_ID", pmm_id);
                intent.putExtra("HIST_MODIFY_TYPE","UPDATE");
                intent.putExtra("P_CF_ID", cf_id);
                intent.putExtra("P_CFM_ID", cfm_id);
                intent.putExtra("P_CFM_NAME", cfm_name);
                intent.putExtra("P_CF_DETAILS", details);
                activity.startActivity(intent);

            });

            delete.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setTitle(mCategory.get(getAdapterPosition()).getCfm_name())
                        .setMessage("Do you want to delete this Clinical Findings?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String cf_id = mCategory.get(getAdapterPosition()).getCf_id();
                            System.out.println(cf_id);
                            deleteClinicalFindings(cf_id,getAdapterPosition());
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    }

    public void deleteClinicalFindings(String cf_id, int index) {
        callback.showLoading();
        callback.setBoolPositive();
        conn = false;
        connected = false;

        String deleteCFLUrl = pre_url_api+"prescription/deleteClinicalFindings";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest request = new StringRequest(Request.Method.POST, deleteCFLUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterDeleteCFL(cf_id,index);
                }
                else {
                    connected = false;
                    updateAfterDeleteCFL(cf_id,index);
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAfterDeleteCFL(cf_id,index);
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAfterDeleteCFL(cf_id,index);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_CF_ID",cf_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAfterDeleteCFL(String cf_id, int pos) {
        if (conn) {
            if (connected) {
                callback.hideLoading();
                Toast.makeText(mContext,"Clinical Findings Deleted Successfully",Toast.LENGTH_SHORT).show();
                mCategory.remove(pos);
                notifyDataSetChanged();
                callback.onListChanged(mCategory.size());
                callback.setBoolNegative();
            }
            else {
                alertMessageForFailedDeleteCFL(cf_id,pos);
            }
        }
        else {
            alertMessageForFailedDeleteCFL(cf_id,pos);
        }
    }

    public void alertMessageForFailedDeleteCFL(String cf_id, int pos) {
        callback.hideLoading();
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
                    callback.setBoolNegative();
                    deleteClinicalFindings(cf_id,pos);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    callback.setBoolNegative();
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}
