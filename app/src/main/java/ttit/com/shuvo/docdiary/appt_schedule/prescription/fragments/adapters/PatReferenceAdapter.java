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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.PatReference;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.AdviceModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.ReferenceModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatReferenceList;

public class PatReferenceAdapter extends RecyclerView.Adapter<PatReferenceAdapter.PRAHolder> {

    private ArrayList<PatReferenceList> mCategory;
    private Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";

    public PatReferenceAdapter(ArrayList<PatReferenceList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PRAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_management_plan_details_layout, parent, false);
        return new PRAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PRAHolder holder, int position) {
        PatReferenceList patReferenceList = mCategory.get(position);
        holder.reference.setText(patReferenceList.getPref_name());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class PRAHolder extends RecyclerView.ViewHolder {
        TextView reference;
        ImageView delete;

        public PRAHolder(@NonNull View itemView) {
            super(itemView);

            reference = itemView.findViewById(R.id.pat_management_plan_for_patient);
            delete = itemView.findViewById(R.id.pat_management_plan_delete);

            itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String pref_id = mCategory.get(getAdapterPosition()).getPref_id();
                String pmm_id = mCategory.get(getAdapterPosition()).getPref_pmm_id();
                String pref_name = mCategory.get(getAdapterPosition()).getPref_name();

                Intent intent = new Intent(mContext, ReferenceModify.class);
                intent.putExtra("P_PMM_ID", pmm_id);
                intent.putExtra("REF_MODIFY_TYPE", "UPDATE");
                intent.putExtra("P_PREF_ID", pref_id);
                intent.putExtra("P_PREF_NAME", pref_name);
                activity.startActivity(intent);
            });

            delete.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setMessage("Do you want to delete this Reference?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String pref_id = mCategory.get(getAdapterPosition()).getPref_id();
                            System.out.println(pref_id);
                            deleteRef(pref_id,getAdapterPosition());
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    }

    public void deleteRef(String pref_id, int index) {
        PrescriptionSetup.circularProgressIndicator.setVisibility(View.VISIBLE);
        PrescriptionSetup.fullLayout.setVisibility(View.GONE);
        PrescriptionSetup.prescriptionLoading = true;
        conn = false;
        connected = false;

        String deleteAdvUrl = pre_url_api+"prescription/deleteReference";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest request = new StringRequest(Request.Method.POST, deleteAdvUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterDeleteRef(pref_id,index);
                }
                else {
                    connected = false;
                    updateAfterDeleteRef(pref_id,index);
                }
            }
            catch (JSONException e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateAfterDeleteRef(pref_id,index);
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateAfterDeleteRef(pref_id,index);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PREF_ID",pref_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void updateAfterDeleteRef(String pref_id,int pos) {
        PrescriptionSetup.prescriptionLoading = false;
        if (conn) {
            if (connected) {
                PrescriptionSetup.circularProgressIndicator.setVisibility(View.GONE);
                PrescriptionSetup.fullLayout.setVisibility(View.VISIBLE);
                Toast.makeText(mContext,"Reference Deleted Successfully",Toast.LENGTH_SHORT).show();
                mCategory.remove(pos);
                notifyDataSetChanged();
                if (mCategory.size() == 0) {
                    PatReference.noRefMsg.setVisibility(View.VISIBLE);
                }
                else {
                    PatReference.noRefMsg.setVisibility(View.GONE);
                }
            }
            else {
                alertMessageForFailedDeleteRef(pref_id,pos);
            }
        }
        else {
            alertMessageForFailedDeleteRef(pref_id,pos);
        }
    }

    public void alertMessageForFailedDeleteRef(String pref_id, int pos) {
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
                    deleteRef(pref_id,pos);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel",(dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}
