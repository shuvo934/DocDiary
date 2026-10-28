package ttit.com.shuvo.docdiary.patient_search.adapters;

import static ttit.com.shuvo.docdiary.patient_search.PatientSearchAdmin.needToUpdatePatList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.health_rank.HealthProgress;
import ttit.com.shuvo.docdiary.patient_search.appointment_calendar.PatAppointmentCalendar;
import ttit.com.shuvo.docdiary.patient_search.arraylists.PatientSearchList;

public class PatientSearchAdapter extends RecyclerView.Adapter<PatientSearchAdapter.PSAHolder> {
    private ArrayList<PatientSearchList> mCategory;
    private final Context mContext;
    private final PatientSelection patientSelection;

    public PatientSearchAdapter(ArrayList<PatientSearchList> mCategory, Context mContext, PatientSelection patientSelection) {
        this.mCategory = mCategory;
        this.mContext = mContext;
        this.patientSelection = patientSelection;
    }

    @NonNull
    @Override
    public PSAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.patient_search_prescription_view, parent, false);
        return new PSAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PSAHolder holder, int position) {
        PatientSearchList patientSearchList = mCategory.get(position);

        holder.patName.setText(patientSearchList.getPat_name());
        String area = patientSearchList.getDd_thana_name();
        if (!area.isEmpty()) {
            String tt = "(" + area + ")";
            holder.patArea.setText(tt);
            holder.patArea.setVisibility(View.VISIBLE);
        }
        else {
            String tt = "(No Address Found)";
            holder.patArea.setText(tt);
            holder.patArea.setVisibility(View.GONE);
        }

        holder.patCode.setText(patientSearchList.getSub_code());

        String gender = safeText(patientSearchList.getPat_gender());
        String age = safeText(patientSearchList.getPat_age());

        String genderAge;

        if (!gender.isEmpty() && !age.isEmpty()) {
            genderAge = gender + "  •  " + age;
        }
        else if (!gender.isEmpty()) {
            genderAge = gender;
        }
        else {
            genderAge = age;
        }

        holder.patAG.setText(genderAge);

        String pph_prog = patientSearchList.getPph_progress();

        if (pph_prog.equals("0")) {
            holder.hpLayout.setVisibility(View.GONE);
            holder.hpMissing.setVisibility(View.VISIBLE);
            holder.hpBar.setProgress(Integer.parseInt(pph_prog));
            holder.viewProgress.setVisibility(View.GONE);
        }
        else {
            holder.hpLayout.setVisibility(View.VISIBLE);
            holder.hpMissing.setVisibility(View.GONE);
            holder.hpBar.setProgress(Integer.parseInt(pph_prog));
            holder.viewProgress.setVisibility(View.VISIBLE);
        }
        String pyear = patientSearchList.getPat_year();
        if (!pyear.isEmpty()) {
            String tt = "(" + pyear + ")";
            holder.patYear.setText(tt);
            holder.patYear.setVisibility(View.VISIBLE);
        }
        else {
            String tt = "()";
            holder.patYear.setText(tt);
            holder.patYear.setVisibility(View.GONE);
        }
        if (patientSearchList.getCalling_permission().equals("1")) {
            holder.calling.setVisibility(View.GONE);
        }
        else if (patientSearchList.getCalling_permission().equals("2")) {
            if (!patientSearchList.getPat_cell().isEmpty()) {
                holder.calling.setVisibility(View.VISIBLE);
            }
            else {
                holder.calling.setVisibility(View.GONE);
            }
        }
        else {
            holder.calling.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    private static String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    public class PSAHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        AppCompatTextView patName;
        AppCompatTextView patArea;
        AppCompatTextView patCode;
        AppCompatTextView patYear;
        AppCompatTextView patAG;
        RelativeLayout hpLayout;
        TextView hpText;
        ProgressBar hpBar;
        TextView hpMissing;
        MaterialButton viewProgress;
        MaterialButton appCalendar;
        MaterialButton calling;
        public PSAHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view_of_patient_search);
            patName = itemView.findViewById(R.id.patient_name_in_pat_search);
            patArea = itemView.findViewById(R.id.patient_area_in_pat_search);
            patCode = itemView.findViewById(R.id.pat_ph_code_in_pat_search);
            patAG = itemView.findViewById(R.id.pat_age_gender_in_pat_search);
            hpLayout = itemView.findViewById(R.id.layout_of_pat_health_progress_in_pat_search);
            hpText = itemView.findViewById(R.id.text_pat_search_hp_text);
            hpBar = itemView.findViewById(R.id.pat_health_progress_bar_in_pat_search);
            hpMissing = itemView.findViewById(R.id.text_pat_search_hp_not_found);
            viewProgress = itemView.findViewById(R.id.patient_view_health_progress_pat_search);
            appCalendar = itemView.findViewById(R.id.patient_view_appointment_calendar_pat_search);
            patYear = itemView.findViewById(R.id.patient_year_in_pat_search);
            calling = itemView.findViewById(R.id.phone_call_from_pat_search);

            cardView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }

                String patId = mCategory.get(position).getPat_id();
                String phId = mCategory.get(position).getPh_id();
                patientSelection.onSelect(patId,phId);
            });

            viewProgress.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                Context context = v.getContext();
                needToUpdatePatList = false;
                Intent intent = new Intent(mContext, HealthProgress.class);
                intent.putExtra("P_NAME",mCategory.get(position).getPat_name());
                intent.putExtra("P_CODE",mCategory.get(position).getSub_code());
                intent.putExtra("P_PH_ID",mCategory.get(position).getPh_id());
                intent.putExtra("FROM_PSV",true);

                context.startActivity(intent);

            });

            appCalendar.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                Context context = v.getContext();
                needToUpdatePatList = false;
                Intent intent = new Intent(mContext, PatAppointmentCalendar.class);
                intent.putExtra("P_NAME",mCategory.get(position).getPat_name());
                intent.putExtra("P_CODE",mCategory.get(position).getSub_code());
                intent.putExtra("P_PH_ID",mCategory.get(position).getPh_id());

                context.startActivity(intent);
            });

            calling.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }

                String number = mCategory.get(position).getPat_cell();
                String name = mCategory.get(position).getPat_name();
                if (!number.isEmpty()) {
                    Context context = view.getContext();
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setMessage("Do you want to make a call to "+name+" ?")
                            .setPositiveButton("Yes", null)
                            .setNegativeButton("No",null)
                            .show();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positive.setOnClickListener(v1 -> {
                        dialog.dismiss();
                        needToUpdatePatList = false;
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:"+number));
                        context.startActivity(callIntent);

                    });
                    Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    negative.setOnClickListener(v12 -> dialog.dismiss());
                }
            });
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<PatientSearchList> filteredList) {
        mCategory = filteredList;
        notifyDataSetChanged();
    }

    public interface PatientSelection {
        void onSelect(String pat_id, String ph_id);
    }
}
