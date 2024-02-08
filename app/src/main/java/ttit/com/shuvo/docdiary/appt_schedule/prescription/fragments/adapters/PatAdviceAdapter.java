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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.PatAdvice;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.AdviceModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.ManagementModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatAdviceList;

public class PatAdviceAdapter extends RecyclerView.Adapter<PatAdviceAdapter.PAAHolder> {
    private ArrayList<PatAdviceList> mCategory;
    private Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";

    public PatAdviceAdapter(ArrayList<PatAdviceList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PAAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_management_plan_details_layout, parent, false);
        return new PAAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PAAHolder holder, int position) {
        PatAdviceList patAdviceList = mCategory.get(position);
        holder.advice.setText(patAdviceList.getPa_name());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class PAAHolder extends RecyclerView.ViewHolder {
        TextView advice;
        ImageView delete;

        public PAAHolder(@NonNull View itemView) {
            super(itemView);
            advice = itemView.findViewById(R.id.pat_management_plan_for_patient);
            delete = itemView.findViewById(R.id.pat_management_plan_delete);

            itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String pa_id = mCategory.get(getAdapterPosition()).getPa_id();
                String pmm_id = mCategory.get(getAdapterPosition()).getPa_pmm_id();
                String pa_name = mCategory.get(getAdapterPosition()).getPa_name();

                Intent intent = new Intent(mContext, AdviceModify.class);
                intent.putExtra("P_PMM_ID", pmm_id);
                intent.putExtra("ADV_MODIFY_TYPE", "UPDATE");
                intent.putExtra("P_PA_ID", pa_id);
                intent.putExtra("P_PA_NAME", pa_name);
                activity.startActivity(intent);
            });

            delete.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setMessage("Do you want to delete this Advice?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String pa_id = mCategory.get(getAdapterPosition()).getPa_id();
                            System.out.println(pa_id);
                            deleteAdvice(pa_id,getAdapterPosition());
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    }

    public void deleteAdvice(String pa_id, int index) {
        PrescriptionSetup.circularProgressIndicator.setVisibility(View.VISIBLE);
        PrescriptionSetup.fullLayout.setVisibility(View.GONE);
        PrescriptionSetup.prescriptionLoading = true;
        conn = false;
        connected = false;

        String deleteAdvUrl = pre_url_api+"prescription/deleteAdvice";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest request = new StringRequest(Request.Method.POST, deleteAdvUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterDeleteAdv(pa_id,index);
                }
                else {
                    connected = false;
                    updateAfterDeleteAdv(pa_id,index);
                }
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateAfterDeleteAdv(pa_id,index);
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateAfterDeleteAdv(pa_id,index);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PA_ID",pa_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void updateAfterDeleteAdv(String pa_id,int pos) {
        if (conn) {
            if (connected) {
                PrescriptionSetup.circularProgressIndicator.setVisibility(View.GONE);
                PrescriptionSetup.fullLayout.setVisibility(View.VISIBLE);
                Toast.makeText(mContext,"Advice Deleted Successfully",Toast.LENGTH_SHORT).show();
                mCategory.remove(pos);
                notifyDataSetChanged();
                if (mCategory.size() == 0) {
                    PatAdvice.noAdviceMsg.setVisibility(View.VISIBLE);
                }
                else {
                    PatAdvice.noAdviceMsg.setVisibility(View.GONE);
                }
                PrescriptionSetup.prescriptionLoading = false;
            }
            else {
                alertMessageForFailedDeleteAdv(pa_id,pos);
            }
        }
        else {
            alertMessageForFailedDeleteAdv(pa_id,pos);
        }
    }

    public void alertMessageForFailedDeleteAdv(String pa_id, int pos) {
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
                    deleteAdvice(pa_id,pos);
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
