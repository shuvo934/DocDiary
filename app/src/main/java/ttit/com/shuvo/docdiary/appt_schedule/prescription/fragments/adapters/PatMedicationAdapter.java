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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.Medication;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.MedicationModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatMedicationList;

public class PatMedicationAdapter extends RecyclerView.Adapter<PatMedicationAdapter.PMAHolder> {
    private final ArrayList<PatMedicationList> mCategory;
    private final Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";

    public PatMedicationAdapter(ArrayList<PatMedicationList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }
    Logger logger = Logger.getLogger("PatMedicationAdapter");

    @NonNull
    @Override
    public PMAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.patient_medication_details_layout, parent, false);
        return new PMAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PMAHolder holder, int position) {
        PatMedicationList patMedicationList = mCategory.get(position);
        holder.patMedicineName.setText(patMedicationList.getMedicine_name());
        holder.patMedicineTakeTime.setText(patMedicationList.getMpm_name());
        holder.patDoseName.setText(patMedicationList.getDose_name());
        holder.patMedicineDuration.setText(patMedicationList.getPmp_duration());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class PMAHolder extends RecyclerView.ViewHolder {
        TextView patMedicineName;
        TextView patMedicineTakeTime;
        TextView patDoseName;
        TextView patMedicineDuration;
        ImageView delete;

        public PMAHolder(@NonNull View itemView) {
            super(itemView);

            patMedicineName = itemView.findViewById(R.id.medicine_name_for_patient);
            patMedicineTakeTime = itemView.findViewById(R.id.medicine_take_time_for_patient);
            patDoseName = itemView.findViewById(R.id.medicine_dose_for_patient);
            patMedicineDuration = itemView.findViewById(R.id.medicine_take_duration_for_patient);
            delete = itemView.findViewById(R.id.patient_medication_delete);

            itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String pmp_id = mCategory.get(getAdapterPosition()).getPmp_id();
                String pmm_id = mCategory.get(getAdapterPosition()).getPmp_pmm_id();
                String medicine_id = mCategory.get(getAdapterPosition()).getMedicine_id();
                String medicine_name = mCategory.get(getAdapterPosition()).getMedicine_name();
                String dose_id = mCategory.get(getAdapterPosition()).getDose_id();
                String dose_name = mCategory.get(getAdapterPosition()).getDose_name();
                String mpm_name = mCategory.get(getAdapterPosition()).getMpm_name();
                String mpm_id = mCategory.get(getAdapterPosition()).getMpm_id();
                String duration = mCategory.get(getAdapterPosition()).getPmp_duration();

                Intent intent = new Intent(mContext, MedicationModify.class);
                intent.putExtra("P_PMM_ID", pmm_id);
                intent.putExtra("MED_MODIFY_TYPE","UPDATE");
                intent.putExtra("P_PMP_ID", pmp_id);
                intent.putExtra("P_MEDICINE_ID", medicine_id);
                intent.putExtra("P_MEDICINE_NAME", medicine_name);
                intent.putExtra("P_DOSE_ID", dose_id);
                intent.putExtra("P_DOSE_NAME", dose_name);
                intent.putExtra("P_MPM_NAME", mpm_name);
                intent.putExtra("P_MPM_ID", mpm_id);
                intent.putExtra("P_DURATION", duration);
                activity.startActivity(intent);

            });

            delete.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setTitle(mCategory.get(getAdapterPosition()).getMedicine_name())
                        .setMessage("Do you want to delete this Medicine?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String pmp_id = mCategory.get(getAdapterPosition()).getPmp_id();
                            System.out.println(pmp_id);
                            deleteMedicine(pmp_id,getAdapterPosition());
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    }

    public void deleteMedicine(String pmp_id, int index) {
        PrescriptionSetup.circularProgressIndicator.setVisibility(View.VISIBLE);
        PrescriptionSetup.fullLayout.setVisibility(View.GONE);
        PrescriptionSetup.prescriptionLoading = true;
        conn = false;
        connected = false;

        String deleteMedUrl = pre_url_api+"prescription/deleteMedication";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest request = new StringRequest(Request.Method.POST, deleteMedUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterDeleteMed(pmp_id,index);
                }
                else {
                    connected = false;
                    updateAfterDeleteMed(pmp_id,index);
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAfterDeleteMed(pmp_id,index);
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAfterDeleteMed(pmp_id,index);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PMP_ID",pmp_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void updateAfterDeleteMed(String pmp_id,int pos) {
        if (conn) {
            if (connected) {
                PrescriptionSetup.circularProgressIndicator.setVisibility(View.GONE);
                PrescriptionSetup.fullLayout.setVisibility(View.VISIBLE);
                Toast.makeText(mContext,"Medicine Deleted Successfully",Toast.LENGTH_SHORT).show();
                mCategory.remove(pos);
                notifyDataSetChanged();
                if (mCategory.isEmpty()) {
                    Medication.noPatMedMsg.setVisibility(View.VISIBLE);
                }
                else {
                    Medication.noPatMedMsg.setVisibility(View.GONE);
                }
                PrescriptionSetup.prescriptionLoading = false;
            }
            else {
                alertMessageForFailedDeleteMed(pmp_id,pos);
            }
        }
        else {
            alertMessageForFailedDeleteMed(pmp_id,pos);
        }
    }

    public void alertMessageForFailedDeleteMed(String pmp_id, int pos) {
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
                    deleteMedicine(pmp_id,pos);
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
