package ttit.com.shuvo.docdiary.appt_schedule.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.divider.MaterialDivider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.arraylists.ApptScheduleInfoList;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.HealthProgress;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.PrescriptionSetup;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.TLHolder> {

    private final ArrayList<ApptScheduleInfoList> mCategory;

    private final Context myContext;

    public TimeLineAdapter(ArrayList<ApptScheduleInfoList> mCategory, Context myContext) {
        this.mCategory = mCategory;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public TLHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.appt_view_with_time_line, parent, false);
        return new TLHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TLHolder holder, int position) {
        ApptScheduleInfoList schedule = mCategory.get(position);

        holder.timeOfAppt.setText(schedule.getTime());
        String status = schedule.getApptStatus();
        String pmm = schedule.getPmm_for_prescription();
        if (pmm.equals("1")) {
            holder.prescription.setText("Prescription");
        }
        else {
            holder.prescription.setText("Create Prescription");
        }

        String appt_date = schedule.getAppointment_date();
        appt_date = appt_date + " " + schedule.getTime();
        System.out.println("APP DATE: "+ appt_date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy hh:mm a", Locale.ENGLISH);
        Date appDate = null;
        try {
            appDate = simpleDateFormat.parse(appt_date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        if (status.isEmpty()) {
            holder.pat_code.setVisibility(View.GONE);
            holder.pat_age.setVisibility(View.GONE);
            holder.serviceName.setVisibility(View.GONE);
            holder.videoLinkNotice.setVisibility(View.GONE);
            holder.videoCallButton.setVisibility(View.GONE);
            holder.prescription.setVisibility(View.GONE);
            holder.hpLayout.setVisibility(View.GONE);
            holder.hpMissing.setVisibility(View.GONE);
            holder.progressAddCheck.setVisibility(View.GONE);
            holder.addProgress.setVisibility(View.GONE);

            holder.materialCardView.setCardBackgroundColor(myContext.getColor(R.color.progress_back_color));
            holder.materialCardView.setCardElevation(3);
            holder.border.setVisibility(View.GONE);

            holder.pat_name.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.pat_code.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.pat_age.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.serviceName.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.timeOfAppt.setTextColor(myContext.getColor(R.color.black));
            holder.hpText.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.hpMissing.setTextColor(myContext.getColor(R.color.default_text_color));

            holder.mTimelineView.setMarker(AppCompatResources.getDrawable(myContext,R.drawable.circle_24));
            holder.mTimelineView.setMarkerColor(myContext.getColor(R.color.back_color));

            Calendar calendar = Calendar.getInstance();
            Date nowDate = calendar.getTime();

            if (appDate != null) {
                System.out.println(simpleDateFormat.format(appDate));
                System.out.println(simpleDateFormat.format(nowDate));
                System.out.println(appDate.getTime());
                System.out.println(nowDate.getTime());
                if (appDate.before(nowDate)) {
                    String text = "No Meeting happened";
                    holder.pat_name.setText(text);
                }
                else {
                    String text = "...";
                    holder.pat_name.setText(text);
                }
            }
            else {
                String text = "...";
                holder.pat_name.setText(text);
            }
        }
        else if (isNumeric(status)) {
            holder.pat_code.setVisibility(View.VISIBLE);
            holder.pat_age.setVisibility(View.VISIBLE);
            holder.serviceName.setVisibility(View.VISIBLE);
            holder.prescription.setVisibility(View.VISIBLE);
            holder.hpLayout.setVisibility(View.VISIBLE);
            holder.hpMissing.setVisibility(View.VISIBLE);
            holder.progressAddCheck.setVisibility(View.VISIBLE);
            holder.addProgress.setVisibility(View.VISIBLE);

            String p_code = "("+schedule.getPatient_code()+")";
            holder.pat_code.setText(p_code);
            holder.pat_age.setText(schedule.getPatient_age());
            holder.serviceName.setText(schedule.getPfn_fee_name());
            String text = schedule.getPatientName();
            holder.pat_name.setText(text);

            if (appDate != null) {
                Calendar calendar = Calendar.getInstance();
                Date nowDate = calendar.getTime();
                System.out.println(simpleDateFormat.format(appDate));
                System.out.println(simpleDateFormat.format(nowDate));
                System.out.println(appDate.getTime());
                System.out.println(nowDate.getTime());
                if (appDate.before(nowDate)) {
                    String pph_prog = schedule.getPph_progress();
                    String hpAdded = schedule.getIs_ranked();
                    SimpleDateFormat sss = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                    String nnn = sss.format(nowDate);
                    nnn = nnn.toUpperCase();
                    String adm_date = schedule.getAdm_date();

                    if (nnn.equals(adm_date)) {
                        if (pph_prog.equals("0")) {
                            holder.addProgress.setText("ADD PROGRESS");
                        }
                        else {
                            if (hpAdded.equals("0")) {
                                holder.addProgress.setText("ADD PROGRESS");
                            }
                            else {
                                holder.addProgress.setText("VIEW PROGRESS");
                            }
                        }
                    }
                    else {
                        if (pph_prog.equals("0")) {
                            holder.addProgress.setText("ADD PROGRESS");
                            holder.addProgress.setVisibility(View.GONE);
                        }
                        else {
                            holder.addProgress.setText("VIEW PROGRESS");
                            holder.addProgress.setVisibility(View.VISIBLE);
                        }
                    }

                    if (pph_prog.equals("0")) {
                        holder.hpLayout.setVisibility(View.GONE);
                        holder.hpMissing.setVisibility(View.VISIBLE);
                        holder.hpBar.setProgress(Integer.parseInt(pph_prog));
                    }
                    else {
                        holder.hpLayout.setVisibility(View.VISIBLE);
                        holder.hpMissing.setVisibility(View.GONE);
                        holder.hpBar.setProgress(Integer.parseInt(pph_prog));
                    }

                    if (hpAdded.equals("0")) {
                        holder.progressAddCheck.setVisibility(View.GONE);
                    }
                    else {
                        holder.progressAddCheck.setVisibility(View.VISIBLE);
                    }

                    holder.materialCardView.setCardBackgroundColor(myContext.getColor(R.color.light_greenish_blue));
                    holder.materialCardView.setCardElevation(7);
                    holder.border.setVisibility(View.GONE);

                    holder.videoLinkNotice.setVisibility(View.GONE);
                    holder.videoCallButton.setVisibility(View.GONE);

                    holder.pat_name.setTextColor(myContext.getColor(R.color.white));
                    holder.pat_code.setTextColor(myContext.getColor(R.color.white));
                    holder.pat_age.setTextColor(myContext.getColor(R.color.white));
                    holder.serviceName.setTextColor(myContext.getColor(R.color.white));
                    holder.timeOfAppt.setTextColor(myContext.getColor(R.color.white));
                    holder.hpText.setTextColor(myContext.getColor(R.color.white));
                    holder.hpMissing.setTextColor(myContext.getColor(R.color.white));

                    holder.mTimelineView.setMarker(AppCompatResources.getDrawable(myContext,R.drawable.check_circle_24));
                    holder.mTimelineView.setMarkerColor(myContext.getColor(R.color.light_green));
                }
                else {
                    String pph_prog = schedule.getPph_progress();
                    holder.addProgress.setText("VIEW PROGRESS");

                    if (pph_prog.equals("0")) {
                        holder.hpLayout.setVisibility(View.GONE);
                        holder.hpMissing.setVisibility(View.VISIBLE);
                        holder.hpBar.setProgress(Integer.parseInt(pph_prog));
                        holder.addProgress.setVisibility(View.GONE);
                    }
                    else {
                        holder.hpLayout.setVisibility(View.VISIBLE);
                        holder.hpMissing.setVisibility(View.GONE);
                        holder.hpBar.setProgress(Integer.parseInt(pph_prog));
                        holder.addProgress.setVisibility(View.VISIBLE);
                    }

                    String hpAdded = schedule.getIs_ranked();
                    if (hpAdded.equals("0")) {
                        holder.progressAddCheck.setVisibility(View.GONE);
                    }
                    else {
                        holder.progressAddCheck.setVisibility(View.VISIBLE);
                    }
                    holder.materialCardView.setCardBackgroundColor(myContext.getColor(R.color.default_disabled_color));
                    holder.materialCardView.setCardElevation(10);
                    holder.border.setVisibility(View.VISIBLE);

                    holder.pat_name.setTextColor(myContext.getColor(R.color.black));
                    holder.pat_code.setTextColor(myContext.getColor(R.color.default_text_color));
                    holder.pat_age.setTextColor(myContext.getColor(R.color.default_text_color));
                    holder.serviceName.setTextColor(myContext.getColor(R.color.default_text_color));
                    holder.timeOfAppt.setTextColor(myContext.getColor(R.color.black));
                    holder.hpText.setTextColor(myContext.getColor(R.color.default_text_color));
                    holder.hpMissing.setTextColor(myContext.getColor(R.color.default_text_color));

                    holder.mTimelineView.setMarker(AppCompatResources.getDrawable(myContext,R.drawable.circle_24));
                    holder.mTimelineView.setMarkerColor(myContext.getColor(R.color.clouds));

                    if (schedule.getTs_video_conf_flag().equals("1")) {
                        long ten_min = 10*60*1000;
                        if ((appDate.getTime() - ten_min) > nowDate.getTime()) {
                            String v_notice = "Video Call Link will be available before 10 mins of Schedule Time";
                            SpannableString spannableString = new SpannableString(v_notice);
                            spannableString.setSpan(new UnderlineSpan(),0,v_notice.length(),0);
                            holder.videoLinkNotice.setText(spannableString);
                            holder.videoLinkNotice.setVisibility(View.VISIBLE);
                            holder.videoCallButton.setVisibility(View.GONE);
                        }
                        else {
                            holder.videoLinkNotice.setVisibility(View.GONE);
                            holder.videoCallButton.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        holder.videoLinkNotice.setVisibility(View.GONE);
                        holder.videoCallButton.setVisibility(View.GONE);
                    }
                }
            }
            else {
                String pph_prog = schedule.getPph_progress();
                holder.addProgress.setText("VIEW PROGRESS");

                if (pph_prog.equals("0")) {
                    holder.hpLayout.setVisibility(View.GONE);
                    holder.hpMissing.setVisibility(View.VISIBLE);
                    holder.hpBar.setProgress(Integer.parseInt(pph_prog));
                    holder.addProgress.setVisibility(View.GONE);
                }
                else {
                    holder.hpLayout.setVisibility(View.VISIBLE);
                    holder.hpMissing.setVisibility(View.GONE);
                    holder.hpBar.setProgress(Integer.parseInt(pph_prog));
                    holder.addProgress.setVisibility(View.VISIBLE);
                }

                String hpAdded = schedule.getIs_ranked();
                if (hpAdded.equals("0")) {
                    holder.progressAddCheck.setVisibility(View.GONE);
                }
                else {
                    holder.progressAddCheck.setVisibility(View.VISIBLE);
                }
                holder.materialCardView.setCardBackgroundColor(myContext.getColor(R.color.default_disabled_color));
                holder.border.setVisibility(View.VISIBLE);

                holder.pat_name.setTextColor(myContext.getColor(R.color.default_text_color));
                holder.pat_code.setTextColor(myContext.getColor(R.color.default_text_color));
                holder.pat_age.setTextColor(myContext.getColor(R.color.default_text_color));
                holder.serviceName.setTextColor(myContext.getColor(R.color.default_text_color));
                holder.timeOfAppt.setTextColor(myContext.getColor(R.color.black));
                holder.hpText.setTextColor(myContext.getColor(R.color.default_text_color));
                holder.hpMissing.setTextColor(myContext.getColor(R.color.default_text_color));

                holder.videoLinkNotice.setVisibility(View.GONE);
                holder.videoCallButton.setVisibility(View.GONE);

                holder.mTimelineView.setMarker(AppCompatResources.getDrawable(myContext,R.drawable.circle_24));
                holder.mTimelineView.setMarkerColor(myContext.getColor(R.color.clouds));
            }

        }
        else {
            holder.pat_code.setVisibility(View.GONE);
            holder.pat_age.setVisibility(View.GONE);
            holder.serviceName.setVisibility(View.GONE);
            holder.prescription.setVisibility(View.GONE);
            holder.hpLayout.setVisibility(View.GONE);
            holder.hpMissing.setVisibility(View.GONE);
            holder.progressAddCheck.setVisibility(View.GONE);
            holder.addProgress.setVisibility(View.GONE);
            holder.pat_name.setText(schedule.getApptStatus());

            holder.videoLinkNotice.setVisibility(View.GONE);
            holder.videoCallButton.setVisibility(View.GONE);

            holder.materialCardView.setCardBackgroundColor(myContext.getColor(R.color.progress_back_color));
            holder.materialCardView.setCardElevation(3);
            holder.border.setVisibility(View.GONE);

            holder.pat_name.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.pat_code.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.pat_age.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.serviceName.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.timeOfAppt.setTextColor(myContext.getColor(R.color.black));
            holder.hpText.setTextColor(myContext.getColor(R.color.default_text_color));
            holder.hpMissing.setTextColor(myContext.getColor(R.color.default_text_color));

            holder.mTimelineView.setMarker(AppCompatResources.getDrawable(myContext,R.drawable.circle_24));
            holder.mTimelineView.setMarkerColor(myContext.getColor(R.color.back_color));
        }

    }

    @Override
    public int getItemCount() {
        return mCategory.size();
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

    public class TLHolder extends RecyclerView.ViewHolder {
        TextView timeOfAppt;
        TextView pat_name;
        TextView pat_code;
        TimelineView mTimelineView;
        MaterialCardView materialCardView;
        TextView pat_age;
        TextView serviceName;
        TextView videoLinkNotice;
        MaterialButton videoCallButton;
        RelativeLayout border;
        MaterialButton prescription;
        RelativeLayout hpLayout;
        TextView hpText;
        ProgressBar hpBar;
        TextView hpMissing;
        ImageView progressAddCheck;
        MaterialButton addProgress;

        public TLHolder(@NonNull View itemView,int viewType) {
            super(itemView);
            mTimelineView =  itemView.findViewById(R.id.timeline);
            timeOfAppt = itemView.findViewById(R.id.text_timeline_date_time);
            pat_name = itemView.findViewById(R.id.text_timeline_pat_name);
            pat_code = itemView.findViewById(R.id.text_timeline_pat_code);
            mTimelineView.initLine(viewType);
            materialCardView = itemView.findViewById(R.id.card_view_of_timeline_details);
            pat_age = itemView.findViewById(R.id.text_timeline_pat_age);
            border = itemView.findViewById(R.id.border_layout);
            serviceName = itemView.findViewById(R.id.text_timeline_pat_fee_service_name);
            videoLinkNotice = itemView.findViewById(R.id.text_timeline_video_link_available_notice);
            videoCallButton = itemView.findViewById(R.id.appointment_wise_video_call_button);
            prescription = itemView.findViewById(R.id.patient_prescription);
            hpLayout = itemView.findViewById(R.id.layout_of_pat_health_progress_in_timeline);
            hpText = itemView.findViewById(R.id.text_timeline_hp_text);
            hpBar = itemView.findViewById(R.id.pat_health_progress_bar_in_timeline);
            hpMissing = itemView.findViewById(R.id.text_timeline_hp_not_found);
            progressAddCheck = itemView.findViewById(R.id.check_image_of_progress_added_in_timeline);
            addProgress = itemView.findViewById(R.id.patient_add_health_progress_timeline);

            videoCallButton.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();

//                String text = "https://meet.google.com/kaj-mfbt-ohk";
                String text = mCategory.get(getAdapterPosition()).getDoc_video_link();
                if (text.isEmpty()) {
                    Toast.makeText(myContext,"Video call is not available yet",Toast.LENGTH_SHORT).show();
                }
                else {
                    Uri uri = Uri.parse(text); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                }
            });

            prescription.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Intent intent = new Intent(myContext, PrescriptionSetup.class);
                intent.putExtra("P_AGE",mCategory.get(getAdapterPosition()).getPatient_age());
                intent.putExtra("PRESCRIPTION_TYPE",mCategory.get(getAdapterPosition()).getPmm_for_prescription());
                intent.putExtra("P_PH_ID",mCategory.get(getAdapterPosition()).getApptStatus());
                activity.startActivity(intent);
            });

            addProgress.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Intent intent = new Intent(myContext, HealthProgress.class);
                intent.putExtra("P_NAME",mCategory.get(getAdapterPosition()).getPatientName());
                intent.putExtra("P_CODE",mCategory.get(getAdapterPosition()).getPatient_code());
                intent.putExtra("P_PH_ID",mCategory.get(getAdapterPosition()).getApptStatus());
                intent.putExtra("IS_RANKED",mCategory.get(getAdapterPosition()).getIs_ranked());
                intent.putExtra("ADM_DATE",mCategory.get(getAdapterPosition()).getAdm_date());
                intent.putExtra("APPT_TIME",mCategory.get(getAdapterPosition()).getTime());
                intent.putExtra("PFN_NAME",mCategory.get(getAdapterPosition()).getPfn_fee_name());
                intent.putExtra("PFN_ID",mCategory.get(getAdapterPosition()).getPfn_id());
                intent.putExtra("DEPTS_NAME",mCategory.get(getAdapterPosition()).getDepts_name());
                intent.putExtra("DEPTS_ID",mCategory.get(getAdapterPosition()).getDepts_id());
                intent.putExtra("CAT_ID",mCategory.get(getAdapterPosition()).getPh_cat_id());
                intent.putExtra("PRM_ID",mCategory.get(getAdapterPosition()).getAd_prm_id());
                intent.putExtra("PRD_ID",mCategory.get(getAdapterPosition()).getAd_prd_id());
                intent.putExtra("AD_ID",mCategory.get(getAdapterPosition()).getAd_id());
                intent.putExtra("FROM_PSV",false);
                activity.startActivity(intent);
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }
}
