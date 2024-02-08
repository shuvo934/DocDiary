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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.DrugHistory;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.DrugHistoryModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.MedHistoryModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatDrugHistList;

public class PatDrugHistAdapter extends RecyclerView.Adapter<PatDrugHistAdapter.PDHHolder> {

    private ArrayList<PatDrugHistList> mCategory;
    private Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";

    public PatDrugHistAdapter(ArrayList<PatDrugHistList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PDHHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_medic_history_details_layout, parent, false);
        return new PDHHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PDHHolder holder, int position) {
        PatDrugHistList patDrugHistList = mCategory.get(position);
        holder.patDrugName.setText(patDrugHistList.getMedicine_name());
        holder.patDrugDetails.setText(patDrugHistList.getPdh_details());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class PDHHolder extends RecyclerView.ViewHolder {
        TextView patDrugName;
        TextView patDrugDetails;
        ImageView delete;

        public PDHHolder(@NonNull View itemView) {
            super(itemView);

            patDrugName = itemView.findViewById(R.id.pat_medic_history_mhm_name);
            patDrugDetails = itemView.findViewById(R.id.pat_medic_history_pmh_details);
            delete = itemView.findViewById(R.id.pat_medic_history_delete);

            itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String pdh_id = mCategory.get(getAdapterPosition()).getPdh_id();
                String pmm_id = mCategory.get(getAdapterPosition()).getPdh_pmm_id();
                String medicine_id = mCategory.get(getAdapterPosition()).getMedicine_id();
                String medicine_name = mCategory.get(getAdapterPosition()).getMedicine_name();
                String details = mCategory.get(getAdapterPosition()).getPdh_details();

                Intent intent = new Intent(mContext, DrugHistoryModify.class);
                intent.putExtra("P_PMM_ID", pmm_id);
                intent.putExtra("HIST_MODIFY_TYPE","UPDATE");
                intent.putExtra("P_PDH_ID", pdh_id);
                intent.putExtra("P_MEDICINE_ID", medicine_id);
                intent.putExtra("P_MEDICINE_NAME", medicine_name);
                intent.putExtra("P_PDH_DETAILS", details);
                activity.startActivity(intent);

            });

            delete.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setTitle(mCategory.get(getAdapterPosition()).getMedicine_name())
                        .setMessage("Do you want to delete this Drug History?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String pdh_id = mCategory.get(getAdapterPosition()).getPdh_id();
                            System.out.println(pdh_id);
                            deleteDrugHistory(pdh_id,getAdapterPosition());
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    }

    public void deleteDrugHistory(String pdh_id, int index) {
        PrescriptionSetup.circularProgressIndicator.setVisibility(View.VISIBLE);
        PrescriptionSetup.fullLayout.setVisibility(View.GONE);
        PrescriptionSetup.prescriptionLoading = true;
        conn = false;
        connected = false;

        String deleteDrugHistUrl = pre_url_api+"prescription/deleteDrugHistory";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest request = new StringRequest(Request.Method.POST, deleteDrugHistUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterDeleteDH(pdh_id,index);
                }
                else {
                    connected = false;
                    updateAfterDeleteDH(pdh_id,index);
                }
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateAfterDeleteDH(pdh_id,index);
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateAfterDeleteDH(pdh_id,index);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PDH_ID",pdh_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void updateAfterDeleteDH(String pdh_id,int pos) {
        if (conn) {
            if (connected) {
                PrescriptionSetup.circularProgressIndicator.setVisibility(View.GONE);
                PrescriptionSetup.fullLayout.setVisibility(View.VISIBLE);
                Toast.makeText(mContext,"Drug History Deleted Successfully",Toast.LENGTH_SHORT).show();
                mCategory.remove(pos);
                notifyDataSetChanged();
                if (mCategory.size() == 0) {
                    DrugHistory.noPatDrugHistMsg.setVisibility(View.VISIBLE);
                }
                else {
                    DrugHistory.noPatDrugHistMsg.setVisibility(View.GONE);
                }
                PrescriptionSetup.prescriptionLoading = false;
            }
            else {
                alertMessageForFailedDeleteDH(pdh_id,pos);
            }
        }
        else {
            alertMessageForFailedDeleteDH(pdh_id,pos);
        }
    }

    public void alertMessageForFailedDeleteDH(String pdh_id, int pos) {
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
                    deleteDrugHistory(pdh_id,pos);
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
