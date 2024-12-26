package ttit.com.shuvo.docdiary.unit_app_schedule.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.unit_app_schedule.arraylists.DoctorAppSchList;

public class DoctorAppSchAdapter extends RecyclerView.Adapter<DoctorAppSchAdapter.DASHolder> {
    private final ArrayList<DoctorAppSchList> mCategoryItem;
    private final Context myContext;

    public DoctorAppSchAdapter(ArrayList<DoctorAppSchList> mCategoryItem, Context myContext) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public DASHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.doc_app_sch_view_layout, parent, false);
        return new DASHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DASHolder holder, int position) {
        DoctorAppSchList schedule = mCategoryItem.get(position);

        holder.timeOfAppt.setText(schedule.getSchedule_time());
        String status = schedule.getPatient_data();

        String appt_date = schedule.getAdm_date();
        appt_date = appt_date + " " + schedule.getSchedule_time();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
        Date appDate;
        try {
            appDate = simpleDateFormat.parse(appt_date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        if (status.isEmpty()) {
            holder.pat_code.setVisibility(View.VISIBLE);
            holder.serviceName.setVisibility(View.VISIBLE);
            holder.messageText.setVisibility(View.VISIBLE);
            holder.calling.setVisibility(View.GONE);

            holder.pat_code.setText("");
            holder.serviceName.setText("");
            holder.pat_name.setText("");

            System.out.println("APP DATE1: "+ appt_date);
            holder.materialCardView.setCardBackgroundColor(myContext.getColor(R.color.progress_back_color));
            holder.materialCardView.setCardElevation(3);
//            holder.border.setVisibility(View.GONE);

            holder.pat_name.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.pat_code.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.serviceName.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.timeOfAppt.setTextColor(myContext.getColor(R.color.black));
            holder.messageText.setTextColor(myContext.getColor(R.color.default_text_color));

            Calendar calendar = Calendar.getInstance();
            Date nowDate = calendar.getTime();

            if (appDate != null) {
                String text;
                if (appDate.before(nowDate)) {
                    text = "No Meeting happened";
                }
                else {
                    text = "...";
                }
                holder.messageText.setText(text);
            }
            else {
                String text = "...";
                holder.messageText.setText(text);
            }
        }
        else if (isNumeric(status)) {
            holder.pat_code.setVisibility(View.VISIBLE);
            holder.serviceName.setVisibility(View.VISIBLE);
            holder.messageText.setVisibility(View.VISIBLE);

            System.out.println("APP DATE2: "+ appt_date);
            holder.messageText.setText("");
            String p_code = "("+schedule.getPat_code()+")";
            holder.pat_code.setText(p_code);
            holder.serviceName.setText(schedule.getPfn_fee_name());
            String text = schedule.getPat_name();
            holder.pat_name.setText(text);
            if (schedule.getCalling_permission().equals("1")) {
                holder.calling.setVisibility(View.GONE);
            }
            else if (schedule.getCalling_permission().equals("2")) {
                if (!schedule.getPat_cell().isEmpty()) {
                    holder.calling.setVisibility(View.VISIBLE);
                }
                else {
                    holder.calling.setVisibility(View.GONE);
                }
            }
            else {
                holder.calling.setVisibility(View.GONE);
            }
            System.out.println(p_code);

            if (appDate != null) {
                Calendar calendar = Calendar.getInstance();
                Date nowDate = calendar.getTime();
                if (appDate.before(nowDate)) {
                    holder.materialCardView.setCardBackgroundColor(myContext.getColor(R.color.light_greenish_blue));
                    holder.materialCardView.setCardElevation(7);
//                    holder.border.setVisibility(View.GONE);

                    holder.pat_name.setTextColor(myContext.getColor(R.color.white));
                    holder.calling.setColorFilter(myContext.getColor(R.color.white));
                    holder.pat_code.setTextColor(myContext.getColor(R.color.white));
                    holder.serviceName.setTextColor(myContext.getColor(R.color.white));
                    holder.timeOfAppt.setTextColor(myContext.getColor(R.color.white));
                    holder.messageText.setTextColor(myContext.getColor(R.color.white));

                }
                else {
                    holder.materialCardView.setCardBackgroundColor(myContext.getColor(R.color.default_disabled_color));
                    holder.materialCardView.setCardElevation(10);
//                    holder.border.setVisibility(View.VISIBLE);

                    holder.pat_name.setTextColor(myContext.getColor(R.color.black));
                    holder.calling.setColorFilter(myContext.getColor(R.color.green_sea));
                    holder.pat_code.setTextColor(myContext.getColor(R.color.default_text_color));
                    holder.serviceName.setTextColor(myContext.getColor(R.color.default_text_color));
                    holder.timeOfAppt.setTextColor(myContext.getColor(R.color.black));
                    holder.messageText.setTextColor(myContext.getColor(R.color.black));
                }
            }
            else {
                holder.materialCardView.setCardBackgroundColor(myContext.getColor(R.color.default_disabled_color));
//                holder.border.setVisibility(View.VISIBLE);

                holder.pat_name.setTextColor(myContext.getColor(R.color.default_text_color));
                holder.calling.setVisibility(View.GONE);
                holder.pat_code.setTextColor(myContext.getColor(R.color.default_text_color));
                holder.serviceName.setTextColor(myContext.getColor(R.color.default_text_color));
                holder.timeOfAppt.setTextColor(myContext.getColor(R.color.black));
                holder.messageText.setTextColor(myContext.getColor(R.color.default_text_color));
            }
        }
        else {
            holder.pat_code.setVisibility(View.VISIBLE);
            holder.serviceName.setVisibility(View.VISIBLE);
            holder.messageText.setVisibility(View.VISIBLE);
            holder.calling.setVisibility(View.GONE);

            holder.pat_code.setText("");
            holder.serviceName.setText("");
            holder.pat_name.setText("");

            holder.messageText.setText(schedule.getPatient_data());

            holder.materialCardView.setCardBackgroundColor(myContext.getColor(R.color.progress_back_color));
            holder.materialCardView.setCardElevation(3);
//            holder.border.setVisibility(View.GONE);

            holder.pat_name.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.pat_code.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.serviceName.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.timeOfAppt.setTextColor(myContext.getColor(R.color.black));
            holder.messageText.setTextColor(myContext.getColor(R.color.default_text_color));
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return mCategoryItem.size();
    }

    public class DASHolder extends RecyclerView.ViewHolder {

        TextView timeOfAppt;
        TextView pat_name;
        TextView pat_code;
        MaterialCardView materialCardView;
        TextView serviceName;
        RelativeLayout border;
        TextView messageText;
        AppCompatImageView calling;

        public DASHolder(@NonNull View itemView) {
            super(itemView);
            timeOfAppt = itemView.findViewById(R.id.time_schedule_of_docs_unit_wise);
            pat_name = itemView.findViewById(R.id.pat_name_for_docs_unit_wise);
            pat_code = itemView.findViewById(R.id.pat_code_for_docs_unit_wise);
            serviceName = itemView.findViewById(R.id.pat_fee_service_name_for_docs_unit_wise);
            materialCardView = itemView.findViewById(R.id.car_view_of_doc_app_sch_view);
            border = itemView.findViewById(R.id.border_layout_unit_wise_doctor);
            messageText = itemView.findViewById(R.id.message_for_docs_unit_wise);
            calling = itemView.findViewById(R.id.phone_call_from_all_appointment);

            calling.setOnClickListener(view -> {
                String number = mCategoryItem.get(getAdapterPosition()).getPat_cell();
                String name = mCategoryItem.get(getAdapterPosition()).getPat_name();
                if (!number.isEmpty()) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    AlertDialog dialog = new AlertDialog.Builder(activity)
                            .setMessage("Do you want to make a call to "+name+" ?")
                            .setPositiveButton("Yes", null)
                            .setNegativeButton("No",null)
                            .show();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positive.setOnClickListener(v1 -> {
                        dialog.dismiss();
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:"+number));
                        activity.startActivity(callIntent);

                    });
                    Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    negative.setOnClickListener(v12 -> dialog.dismiss());
                }
            });
        }
    }
}
