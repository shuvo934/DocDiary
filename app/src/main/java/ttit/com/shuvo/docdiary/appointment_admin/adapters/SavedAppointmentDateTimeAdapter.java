package ttit.com.shuvo.docdiary.appointment_admin.adapters;

import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.selected_service_qty;
import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.serviceQty;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify;
import ttit.com.shuvo.docdiary.appointment_admin.add_schedule.AddSchedule;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.SavedAppointmentDateTimeList;

public class SavedAppointmentDateTimeAdapter extends RecyclerView.Adapter<SavedAppointmentDateTimeAdapter.SADTHolder> {

    private ArrayList<SavedAppointmentDateTimeList> mCategory;
    private Context mContext;

    public SavedAppointmentDateTimeAdapter(ArrayList<SavedAppointmentDateTimeList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SADTHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.added_appoint_date_time_details_layout, parent, false);
        return new SADTHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SADTHolder holder, int position) {
        SavedAppointmentDateTimeList dateTimeList = mCategory.get(position);
        holder.appDate.setText(dateTimeList.getAdm_date());
        holder.appTime.setText(dateTimeList.getSchedule());
        holder.appTime.setTextColor(mContext.getColor(R.color.green_sea));
        if (dateTimeList.isInserted()) {
            if (!dateTimeList.isSchDuplicate()) {
                holder.appTime.setTextColor(mContext.getColor(R.color.green_sea));
                holder.image.setVisibility(View.GONE);
            }
            else {
                holder.appTime.setTextColor(mContext.getColor(R.color.red_dark));
                holder.image.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.appTime.setTextColor(mContext.getColor(R.color.default_text_color));
            holder.image.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mCategory == null ? 0 : mCategory.size();
    }

    public class SADTHolder extends RecyclerView.ViewHolder {

        TextView appDate;
        TextView appTime;

        LinearLayout delete;
        ImageView image;

        public SADTHolder(@NonNull View itemView) {
            super(itemView);

            appDate = itemView.findViewById(R.id.added_appointment_date_for_patient);
            appTime = itemView.findViewById(R.id.added_appointment_time_for_patient);
            delete = itemView.findViewById(R.id.added_appointment_date_time_delete_for_patient);
            image = itemView.findViewById(R.id.delete_imageview_of_pat_appoint_time);

            delete.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                if (mCategory.get(getAdapterPosition()).isInserted()) {
                    if (mCategory.get(getAdapterPosition()).isSchDuplicate()) {
                        int num = 0;
                        for (int i = 0; i < mCategory.size(); i++) {
                            if (mCategory.get(i).isInserted()) {
                                if (mCategory.get(i).isSchDuplicate()) {
                                    num++;
                                }
                            }
                        }
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                        if (num > 1) {
                            builder.setTitle(mCategory.get(getAdapterPosition()).getAdm_date() +", "+ mCategory.get(getAdapterPosition()).getSchedule())
                                    .setMessage("Do you want to remove this schedule?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        int adm_count = 0;
                                        for (int i = 0; i < mCategory.size(); i++) {
                                            if (mCategory.get(i).getAdm_id().equals(mCategory.get(getAdapterPosition()).getAdm_id())) {
                                                adm_count++;
                                            }
                                        }
                                        if (adm_count <= 1) {
                                            int service_qty = Integer.parseInt(selected_service_qty);
                                            boolean tsa = mCategory.get(getAdapterPosition()).isTakenScheduleAvailable();
                                            if (!tsa) {
                                                service_qty = service_qty + 1;
                                                selected_service_qty = String.valueOf(service_qty);
                                                serviceQty.setText(selected_service_qty);
                                            }
                                        }
                                        mCategory.remove(getAdapterPosition());
                                        Toast.makeText(mContext,"Schedule Removed Successfully",Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("No", (dialog, which) -> {
                                        dialog.dismiss();
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        else {
                            builder.setTitle(mCategory.get(getAdapterPosition()).getAdm_date() + ", " + mCategory.get(getAdapterPosition()).getSchedule())
                                    .setMessage("Do you want to remove this schedule? By removing this Schedule you will complete this add appointment process.")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        mCategory.remove(getAdapterPosition());
                                        Toast.makeText(mContext, "Schedule Removed Successfully", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                        activity.finish();
                                    })
                                    .setNegativeButton("No", (dialog, which) -> {
                                        dialog.dismiss();
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                }
                else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mContext);
                    builder.setTitle(mCategory.get(getAdapterPosition()).getAdm_date() +", "+ mCategory.get(getAdapterPosition()).getSchedule())
                            .setMessage("Do you want to remove this schedule?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                int adm_count = 0;
                                for (int i = 0; i < mCategory.size(); i++) {
                                    if (mCategory.get(i).getAdm_id().equals(mCategory.get(getAdapterPosition()).getAdm_id())) {
                                        adm_count++;
                                    }
                                }

                                if (adm_count <= 1) {
                                    int service_qty = Integer.parseInt(selected_service_qty);
                                    boolean tsa = mCategory.get(getAdapterPosition()).isTakenScheduleAvailable();
                                    if (!tsa) {
                                        service_qty = service_qty + 1;
                                        selected_service_qty = String.valueOf(service_qty);
                                        serviceQty.setText(selected_service_qty);
                                    }
                                }
                                mCategory.remove(getAdapterPosition());
                                Toast.makeText(mContext,"Schedule Removed Successfully",Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                dialog.dismiss();
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                dialog.dismiss();
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            });

            itemView.setOnClickListener(v -> {
                if (mCategory.get(getAdapterPosition()).isInserted()) {
                    if (mCategory.get(getAdapterPosition()).isSchDuplicate()) {
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Intent intent = new Intent(mContext, AddSchedule.class);
                        intent.putExtra("P_DOC_ID",mCategory.get(getAdapterPosition()).getDoc_id());
                        intent.putExtra("P_PFN_ID",mCategory.get(getAdapterPosition()).getPfn_id());
                        intent.putExtra("P_DEPTS_ID",mCategory.get(getAdapterPosition()).getDepts_id());
                        intent.putExtra("P_PH_ID",mCategory.get(getAdapterPosition()).getPh_id());
                        intent.putExtra("P_PRM_ID",mCategory.get(getAdapterPosition()).getPrm_id());
                        intent.putExtra("P_PRD_ID",mCategory.get(getAdapterPosition()).getPrd_id());
                        intent.putExtra("P_UPDATE",true);

                        intent.putExtra("P_DATE",mCategory.get(getAdapterPosition()).getAdm_date());
                        intent.putExtra("P_ADM_ID", mCategory.get(getAdapterPosition()).getAdm_id());
                        intent.putExtra("P_SCHEDULE",mCategory.get(getAdapterPosition()).getSchedule());
                        intent.putExtra("P_TS_ID",mCategory.get(getAdapterPosition()).getTs_id());
                        intent.putExtra("P_POSITION",getAdapterPosition());
                        intent.putExtra("SCH_INSERTED", mCategory.get(getAdapterPosition()).isInserted());
                        intent.putExtra("SCH_DUPLICATE",mCategory.get(getAdapterPosition()).isSchDuplicate());

                        activity.startActivity(intent);
                    }
                }
                else {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Intent intent = new Intent(mContext, AddSchedule.class);
                    intent.putExtra("P_DOC_ID",mCategory.get(getAdapterPosition()).getDoc_id());
                    intent.putExtra("P_PFN_ID",mCategory.get(getAdapterPosition()).getPfn_id());
                    intent.putExtra("P_DEPTS_ID",mCategory.get(getAdapterPosition()).getDepts_id());
                    intent.putExtra("P_PH_ID",mCategory.get(getAdapterPosition()).getPh_id());
                    intent.putExtra("P_PRM_ID",mCategory.get(getAdapterPosition()).getPrm_id());
                    intent.putExtra("P_PRD_ID",mCategory.get(getAdapterPosition()).getPrd_id());
                    intent.putExtra("P_UPDATE",true);

                    intent.putExtra("P_DATE",mCategory.get(getAdapterPosition()).getAdm_date());
                    intent.putExtra("P_ADM_ID", mCategory.get(getAdapterPosition()).getAdm_id());
                    intent.putExtra("P_SCHEDULE",mCategory.get(getAdapterPosition()).getSchedule());
                    intent.putExtra("P_TS_ID",mCategory.get(getAdapterPosition()).getTs_id());
                    intent.putExtra("P_POSITION",getAdapterPosition());
                    intent.putExtra("SCH_INSERTED", mCategory.get(getAdapterPosition()).isInserted());
                    intent.putExtra("SCH_DUPLICATE",mCategory.get(getAdapterPosition()).isSchDuplicate());

                    activity.startActivity(intent);
                }

            });
        }
    }
}
