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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.MedHistoryModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatMedicHistList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.interfaces.FragmentAdapterCallBack;

public class PatMedicHistAdapter extends RecyclerView.Adapter<PatMedicHistAdapter.PMHAHolder> {
    private final ArrayList<PatMedicHistList> mCategory;
    private final Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";
    private final FragmentAdapterCallBack listener;

    public PatMedicHistAdapter(ArrayList<PatMedicHistList> mCategory, Context mContext, FragmentAdapterCallBack listener) {
        this.mCategory = mCategory;
        this.mContext = mContext;
        this.listener = listener;
    }

    Logger logger = Logger.getLogger("PatMedicHistAdapter");

    @NonNull
    @Override
    public PMHAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_medic_history_details_layout, parent, false);
        return new PMHAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PMHAHolder holder, int position) {
        PatMedicHistList patMedicHistList = mCategory.get(position);
        holder.patMedicHistName.setText(patMedicHistList.getMhm_name());
        holder.patMedicHistDetails.setText(patMedicHistList.getPmh_details());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class PMHAHolder extends RecyclerView.ViewHolder {
        TextView patMedicHistName;
        TextView patMedicHistDetails;
        ImageView delete;

        public PMHAHolder(@NonNull View itemView) {
            super(itemView);
            patMedicHistName = itemView.findViewById(R.id.pat_medic_history_mhm_name);
            patMedicHistDetails = itemView.findViewById(R.id.pat_medic_history_pmh_details);
            delete = itemView.findViewById(R.id.pat_medic_history_delete);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String pmh_id = mCategory.get(position).getPmh_id();
                String pmm_id = mCategory.get(position).getPmh_pmm_id();
                String mhm_id = mCategory.get(position).getMhm_id();
                String mhm_name = mCategory.get(position).getMhm_name();
                String details = mCategory.get(position).getPmh_details();

                Intent intent = new Intent(mContext, MedHistoryModify.class);
                intent.putExtra("P_PMM_ID", pmm_id);
                intent.putExtra("HIST_MODIFY_TYPE","UPDATE");
                intent.putExtra("P_PMH_ID", pmh_id);
                intent.putExtra("P_MHM_ID", mhm_id);
                intent.putExtra("P_MHM_NAME", mhm_name);
                intent.putExtra("P_PMH_DETAILS", details);
                activity.startActivity(intent);

            });

            delete.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setTitle(mCategory.get(position).getMhm_name())
                        .setMessage("Do you want to delete this Medical History?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String pmh_id = mCategory.get(position).getPmh_id();
                            System.out.println(pmh_id);
                            deleteMedicalHistory(pmh_id,position);
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });

        }
    }

    public void deleteMedicalHistory(String pmh_id, int index) {
        listener.showLoading();
        listener.setBoolPositive();
        conn = false;
        connected = false;

        String deleteMedHistUrl = pre_url_api+"prescription/deleteMedicalHistory";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest request = new StringRequest(Request.Method.POST, deleteMedHistUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterDeleteMH(pmh_id,index);
                }
                else {
                    connected = false;
                    updateAfterDeleteMH(pmh_id,index);
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAfterDeleteMH(pmh_id,index);
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAfterDeleteMH(pmh_id,index);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PMH_ID",pmh_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAfterDeleteMH(String pmh_id, int pos) {
        if (conn) {
            if (connected) {
                listener.hideLoading();
                Toast.makeText(mContext,"Medical History Deleted Successfully",Toast.LENGTH_SHORT).show();
                mCategory.remove(pos);
                notifyDataSetChanged();
                listener.onListChanged(mCategory.size());
                listener.setBoolNegative();
            }
            else {
                alertMessageForFailedDeleteMH(pmh_id,pos);
            }
        }
        else {
            alertMessageForFailedDeleteMH(pmh_id,pos);
        }
    }

    public void alertMessageForFailedDeleteMH(String pmh_id, int pos) {
        listener.hideLoading();
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
                    listener.setBoolNegative();
                    deleteMedicalHistory(pmh_id,pos);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    listener.setBoolNegative();
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}
