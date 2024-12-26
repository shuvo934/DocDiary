package ttit.com.shuvo.docdiary.patient_search.appointment_calendar.adapters;

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
import ttit.com.shuvo.docdiary.patient_search.appointment_calendar.arraylist.PatAppointmentViewList;

public class PatientAppSchAdapter extends RecyclerView.Adapter<PatientAppSchAdapter.PASAHolder> {
    private final ArrayList<PatAppointmentViewList> mCategory;
    private final Context myContext;
    private final String appointment_date;

    public PatientAppSchAdapter(ArrayList<PatAppointmentViewList> mCategory, Context myContext, String appointment_date) {
        this.mCategory = mCategory;
        this.myContext = myContext;
        this.appointment_date = appointment_date;
    }

    @NonNull
    @Override
    public PASAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.pat_app_schedule_view, parent, false);
        return new PASAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PASAHolder holder, int position) {
        PatAppointmentViewList list = mCategory.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
        Date to_date = Calendar.getInstance().getTime();
        String now_date = dateFormat.format(to_date);
        Date app_date;
        Date only_date;
        try {
            app_date = dateFormat.parse(appointment_date);
            only_date = dateFormat.parse(now_date);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (app_date != null && only_date != null) {
            if (app_date.before(only_date)) {
                if (list.getCancel_status().equals("Yes")) {
                    holder.appStatusCard.setCardBackgroundColor(myContext.getColor(R.color.red_dark));
                    String ast = "Canceled";
                    holder.appStatus.setText(ast);
                }
                else {
                    holder.appStatusCard.setCardBackgroundColor(myContext.getColor(R.color.green_sea));
                    String ast = "Completed";
                    holder.appStatus.setText(ast);
                }
            }
            else if (app_date.equals(only_date)) {
                String schedule = list.getSchedule_time();
                String schedule_time_date = appointment_date + " " + schedule;
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
                            holder.appStatusCard.setCardBackgroundColor(myContext.getColor(R.color.red_dark));
                            String ast = "Canceled";
                            holder.appStatus.setText(ast);
                        }
                        else {
                            holder.appStatusCard.setCardBackgroundColor(myContext.getColor(R.color.green_sea));
                            String ast = "Completed";
                            holder.appStatus.setText(ast);
                        }
                    }
                    else {
                        if (list.getCancel_status().equals("Yes")) {
                            holder.appStatusCard.setCardBackgroundColor(myContext.getColor(R.color.red_dark));
                            String ast = "Canceled";
                            holder.appStatus.setText(ast);
                        }
                        else {
                            holder.appStatusCard.setCardBackgroundColor(myContext.getColor(R.color.default_text_color));
                            String ast = "Upcoming";
                            holder.appStatus.setText(ast);
                        }
                    }
                }
                else {
                    holder.appStatusCard.setCardBackgroundColor(myContext.getColor(R.color.default_text_color));
                    String ast = "N/A";
                    holder.appStatus.setText(ast);
                }
            }
            else {
                if (list.getCancel_status().equals("Yes")) {
                    holder.appStatusCard.setCardBackgroundColor(myContext.getColor(R.color.red_dark));
                    String ast = "Canceled";
                    holder.appStatus.setText(ast);
                }
                else {
                    holder.appStatusCard.setCardBackgroundColor(myContext.getColor(R.color.default_text_color));
                    String ast = "Upcoming";
                    holder.appStatus.setText(ast);
                }
            }
        }
        else {
            holder.appStatusCard.setCardBackgroundColor(myContext.getColor(R.color.default_text_color));
            String ast = "N/A";
            holder.appStatus.setText(ast);
        }

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
    }

    @Override
    public int getItemCount() {
        return mCategory == null ? 0 : mCategory.size();
    }

    public static class PASAHolder extends RecyclerView.ViewHolder {
        TextView schTime;
        CardView appStatusCard;
        TextView appStatus;
        TextView serviceName;
        TextView docName;
        TextView deptsName;
        TextView deptdName;
        TextView schType;
        TextView patAppStatus;

        public PASAHolder(@NonNull View itemView) {
            super(itemView);
            schTime = itemView.findViewById(R.id.pat_appointment_sch_date_time_from_calendar);
            appStatusCard = itemView.findViewById(R.id.card_view_of_pat_appointment_status);
            appStatus = itemView.findViewById(R.id.pat_appointment_status_from_calendar);
            serviceName = itemView.findViewById(R.id.pat_appointment_service_name_from_calendar);
            docName = itemView.findViewById(R.id.pat_appointment_doc_name_from_calendar);
            deptsName = itemView.findViewById(R.id.pat_appointment_depts_name_from_calendar);
            deptdName = itemView.findViewById(R.id.pat_appointment_deptd_name_from_calendar);
            schType = itemView.findViewById(R.id.pat_appointment_sch_type_from_calendar);
            patAppStatus = itemView.findViewById(R.id.pat_appointment_appt_status_from_calendar);

        }
    }
}
