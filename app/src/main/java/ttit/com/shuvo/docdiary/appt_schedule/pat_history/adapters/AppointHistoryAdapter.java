package ttit.com.shuvo.docdiary.appt_schedule.pat_history.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.pat_history.arraylists.PatAppHistList;

public class AppointHistoryAdapter extends RecyclerView.Adapter<AppointHistoryAdapter.AHAHolder> {

    private ArrayList<PatAppHistList> mCategory;
    private final Context mContext;

    public AppointHistoryAdapter(ArrayList<PatAppHistList> mCategory, Context mContext) {
        this.mCategory = mCategory;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AHAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pat_appoint_history_view, parent, false);
        return new AHAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AHAHolder holder, int position) {
        PatAppHistList list = mCategory.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
        Date to_date = Calendar.getInstance().getTime();
        String now_date = dateFormat.format(to_date);
        Date app_date;
        Date only_date;

        try {
            app_date = dateFormat.parse(list.getApp_date());
            only_date = dateFormat.parse(now_date);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (app_date != null && only_date != null) {
            if (app_date.before(only_date)) {
                if (list.getCancel_status().equals("Yes")) {
                    holder.appStatusCard.setCardBackgroundColor(mContext.getColor(R.color.red_dark));
                    String ast = "Canceled";
                    holder.appStatus.setText(ast);
                }
                else {
                    holder.appStatusCard.setCardBackgroundColor(mContext.getColor(R.color.green_sea));
                    String ast = "Completed";
                    holder.appStatus.setText(ast);
                }
            }
            else if (app_date.equals(only_date)) {
                String schedule = list.getSchedule_time();
                String schedule_time_date = list.getApp_date() + " " + schedule;
                Date appDate;
                try {
                    appDate = simpleDateFormat.parse(schedule_time_date);
                }
                catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (appDate != null) {
                    if (appDate.before(to_date)) {
                        if (list.getCancel_status().equals("Yes")) {
                            holder.appStatusCard.setCardBackgroundColor(mContext.getColor(R.color.red_dark));
                            String ast = "Canceled";
                            holder.appStatus.setText(ast);
                        }
                        else {
                            holder.appStatusCard.setCardBackgroundColor(mContext.getColor(R.color.green_sea));
                            String ast = "Completed";
                            holder.appStatus.setText(ast);
                        }
                    }
                    else {
                        if (list.getCancel_status().equals("Yes")) {
                            holder.appStatusCard.setCardBackgroundColor(mContext.getColor(R.color.red_dark));
                            String ast = "Canceled";
                            holder.appStatus.setText(ast);
                        }
                        else {
                            holder.appStatusCard.setCardBackgroundColor(mContext.getColor(R.color.default_text_color));
                            String ast = "Upcoming";
                            holder.appStatus.setText(ast);
                        }
                    }
                }
                else {
                    holder.appStatusCard.setCardBackgroundColor(mContext.getColor(R.color.default_text_color));
                    String ast = "N/A";
                    holder.appStatus.setText(ast);
                }
            }
            else {
                if (list.getCancel_status().equals("Yes")) {
                    holder.appStatusCard.setCardBackgroundColor(mContext.getColor(R.color.red_dark));
                    String ast = "Canceled";
                    holder.appStatus.setText(ast);
                }
                else {
                    holder.appStatusCard.setCardBackgroundColor(mContext.getColor(R.color.default_text_color));
                    String ast = "Upcoming";
                    holder.appStatus.setText(ast);
                }
            }
        }
        else {
            holder.appStatusCard.setCardBackgroundColor(mContext.getColor(R.color.default_text_color));
            String ast = "N/A";
            holder.appStatus.setText(ast);
        }

        String dat_with_name = list.getApp_date().toUpperCase(Locale.ENGLISH) + ", " + list.getDate_name();
        holder.appDate.setText(dat_with_name);
        holder.schTime.setText(list.getSchedule_time());
        holder.serviceName.setText(list.getPfn_fee_name());
        holder.docName.setText(list.getDoc_name());
        String depts = "( " + list.getDepts_name() + " )";
        String deptd = "( " + list.getDeptd_name() + " )";
        holder.deptsName.setText(depts);
        holder.deptdName.setText(deptd);
        String sch_type = "( " + list.getEts() + " )";
        holder.schType.setText(sch_type);
        String pat_type = "( " + list.getAd_pat_app_status() + " )";
        pat_type = pat_type.toUpperCase(Locale.ENGLISH);
        holder.patAppStatus.setText(pat_type);
        holder.patAppStatus.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mCategory == null ? 0 : mCategory.size();
    }

    public static class AHAHolder extends RecyclerView.ViewHolder {
        TextView appDate;
        TextView schTime;
        CardView appStatusCard;
        TextView appStatus;
        TextView serviceName;
        TextView docName;
        TextView deptsName;
        TextView deptdName;
        TextView schType;
        TextView patAppStatus;
        public AHAHolder(@NonNull View itemView) {
            super(itemView);
            appDate = itemView.findViewById(R.id.pat_appointment_sch_date_pat_app_history);
            schTime = itemView.findViewById(R.id.pat_appointment_sch_date_time_pat_app_history);
            appStatusCard = itemView.findViewById(R.id.card_view_of_pat_appointment_status_pat_app_hist);
            appStatus = itemView.findViewById(R.id.pat_appointment_status_pat_app_history);
            serviceName = itemView.findViewById(R.id.pat_appointment_service_name_pat_app_history);
            docName = itemView.findViewById(R.id.pat_appointment_doc_name_pat_app_history);
            deptsName = itemView.findViewById(R.id.pat_appointment_depts_name_pat_app_history);
            deptdName = itemView.findViewById(R.id.pat_appointment_deptd_name_pat_app_history);
            schType = itemView.findViewById(R.id.pat_appointment_sch_type_pat_app_history);
            patAppStatus = itemView.findViewById(R.id.pat_appointment_appt_status_pat_app_history);
        }
    }

    public void filterList(ArrayList<PatAppHistList> filteredList) {
        mCategory = filteredList;
        notifyDataSetChanged();
    }
}
