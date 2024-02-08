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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.MedicalHistory;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.MedHistoryModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatMedicHistList;

public class PatMedicHistAdapter extends RecyclerView.Adapter<PatMedicHistAdapter.PMHAHolder> {
    private ArrayList<PatMedicHistList> mCategory;
    private Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";

    public PatMedicHistAdapter(ArrayList<PatMedicHistList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

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
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String pmh_id = mCategory.get(getAdapterPosition()).getPmh_id();
                String pmm_id = mCategory.get(getAdapterPosition()).getPmh_pmm_id();
                String mhm_id = mCategory.get(getAdapterPosition()).getMhm_id();
                String mhm_name = mCategory.get(getAdapterPosition()).getMhm_name();
                String details = mCategory.get(getAdapterPosition()).getPmh_details();

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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setTitle(mCategory.get(getAdapterPosition()).getMhm_name())
                        .setMessage("Do you want to delete this Medical History?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String pmh_id = mCategory.get(getAdapterPosition()).getPmh_id();
                            System.out.println(pmh_id);
                            deleteMedicalHistory(pmh_id,getAdapterPosition());
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });

        }
    }

    public void deleteMedicalHistory(String pmh_id, int index) {
        PrescriptionSetup.circularProgressIndicator.setVisibility(View.VISIBLE);
        PrescriptionSetup.fullLayout.setVisibility(View.GONE);
        PrescriptionSetup.prescriptionLoading = true;
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
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateAfterDeleteMH(pmh_id,index);
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateAfterDeleteMH(pmh_id,index);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PMH_ID",pmh_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void updateAfterDeleteMH(String pmh_id,int pos) {
        if (conn) {
            if (connected) {
                PrescriptionSetup.circularProgressIndicator.setVisibility(View.GONE);
                PrescriptionSetup.fullLayout.setVisibility(View.VISIBLE);
                Toast.makeText(mContext,"Medical History Deleted Successfully",Toast.LENGTH_SHORT).show();
                mCategory.remove(pos);
                notifyDataSetChanged();
                if (mCategory.size() == 0) {
                    MedicalHistory.noPatMedHistMsg.setVisibility(View.VISIBLE);
                }
                else {
                    MedicalHistory.noPatMedHistMsg.setVisibility(View.GONE);
                }
                PrescriptionSetup.prescriptionLoading = false;
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
                    deleteMedicalHistory(pmh_id,pos);
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
