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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.addInformation.ComplainModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.arraylists.ComplainLists;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.CMPHolder> {

    private final ArrayList<ComplainLists> mCategory;
    private final Context mContext;
    private Boolean conn = false;
    private Boolean connected = false;

    String parsing_message = "";

    public ComplainAdapter(ArrayList<ComplainLists> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }
    Logger logger = Logger.getLogger("ComplainAdapter");

    @NonNull
    @Override
    public CMPHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.complain_details_layout, parent, false);
        return new CMPHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CMPHolder holder, int position) {
        ComplainLists complainLists = mCategory.get(position);
        holder.complainName.setText(complainLists.getCm_name());
        holder.causeOfInjury.setText(complainLists.getInjury_name());
        holder.injuryDate.setText(complainLists.getPci_date());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public class CMPHolder extends RecyclerView.ViewHolder {

        TextView complainName;
        TextView causeOfInjury;
        TextView injuryDate;
        ImageView delete;

        public CMPHolder(@NonNull View itemView) {
            super(itemView);

            complainName = itemView.findViewById(R.id.prescription_complain_name);
            causeOfInjury = itemView.findViewById(R.id.prescription_cause_of_injury);
            injuryDate = itemView.findViewById(R.id.prescription_injury_date);
            delete = itemView.findViewById(R.id.delete_complain);

            itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                String pci_id = mCategory.get(getAdapterPosition()).getPci_id();
                String pmm_id = mCategory.get(getAdapterPosition()).getPci_pmm_id();
                String cm_id = mCategory.get(getAdapterPosition()).getCm_id();
                String cm_name = mCategory.get(getAdapterPosition()).getCm_name();
                String injury_id = mCategory.get(getAdapterPosition()).getInjury_id();
                String injury_name = mCategory.get(getAdapterPosition()).getInjury_name();
                String injury_date = mCategory.get(getAdapterPosition()).getPci_date();

                Intent intent = new Intent(mContext, ComplainModify.class);
                intent.putExtra("P_PMM_ID", pmm_id);
                intent.putExtra("MODIFY_TYPE","UPDATE");
                intent.putExtra("P_PCI_ID", pci_id);
                intent.putExtra("P_CM_ID", cm_id);
                intent.putExtra("P_CM_NAME", cm_name);
                intent.putExtra("P_INJURY_ID", injury_id);
                intent.putExtra("P_INJURY_NAME", injury_name);
                intent.putExtra("P_DATE", injury_date);
                activity.startActivity(intent);

            });

            delete.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                builder.setTitle(mCategory.get(getAdapterPosition()).getCm_name())
                        .setMessage("Do you want to delete this Complain?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            String pci_id = mCategory.get(getAdapterPosition()).getPci_id();
                            System.out.println(pci_id);
                            deleteComplain(pci_id,getAdapterPosition());
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    }

    public void deleteComplain(String pci_id, int index) {
        PrescriptionSetup.circularProgressIndicator.setVisibility(View.VISIBLE);
        PrescriptionSetup.fullLayout.setVisibility(View.GONE);
        PrescriptionSetup.prescriptionLoading = true;
        conn = false;
        connected = false;

        String deleteComplainUrl = pre_url_api+"prescription/deleteComplain";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest request = new StringRequest(Request.Method.POST, deleteComplainUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;
                if (string_out.equals("Successfully Created")) {
                    connected = true;
                    updateAfterDeleteComp(pci_id,index);
                }
                else {
                    connected = false;
                    updateAfterDeleteComp(pci_id,index);
                }
            }
            catch (JSONException e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateAfterDeleteComp(pci_id,index);
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateAfterDeleteComp(pci_id,index);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_PCI_ID",pci_id);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void updateAfterDeleteComp(String pci_id,int pos) {
        if (conn) {
            if (connected) {
                PrescriptionSetup.circularProgressIndicator.setVisibility(View.GONE);
                PrescriptionSetup.fullLayout.setVisibility(View.VISIBLE);
                Toast.makeText(mContext,"Complain Deleted Successfully",Toast.LENGTH_SHORT).show();
                mCategory.remove(pos);
                notifyDataSetChanged();
                if (mCategory.isEmpty()) {
                    PrescriptionSetup.noComplainFoundMsg.setVisibility(View.VISIBLE);
                }
                else {
                    PrescriptionSetup.noComplainFoundMsg.setVisibility(View.GONE);
                }
                PrescriptionSetup.prescriptionLoading = false;
            }
            else {
                alertMessageForFailedDeleteComp(pci_id,pos);
            }
        }
        else {
            alertMessageForFailedDeleteComp(pci_id,pos);
        }
    }

    public void alertMessageForFailedDeleteComp(String pci_id, int pos) {
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
                    deleteComplain(pci_id,pos);
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
