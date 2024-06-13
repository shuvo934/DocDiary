package ttit.com.shuvo.docdiary.appointment_admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.PatientForAppointList;

public class SearchPatientForAppAdapter extends RecyclerView.Adapter<SearchPatientForAppAdapter.SPFAHolder> {
    private final ArrayList<PatientForAppointList> mCategoryItem;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public SearchPatientForAppAdapter(ArrayList<PatientForAppointList> mCategoryItem, Context myContext, ClickedItem myClickedItem) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public SPFAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.prescription_code_search_details_view, parent, false);
        return new SPFAHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SPFAHolder holder, int position) {
        PatientForAppointList patientForAppointList = mCategoryItem.get(position);
        holder.prescriptionCode.setText(patientForAppointList.getPh_sub_code());
        holder.patName.setText(patientForAppointList.getPat_name());
        holder.patCode.setText(patientForAppointList.getPat_code());
        String numb = patientForAppointList.getPat_phone();
        if (!patientForAppointList.getPat_phone().isEmpty()) {
            String first = numb.substring(0,3);
            String last = "";
            String mid = "";
            if (numb.length() > 3) {
                last = numb.substring(numb.length()-3);
                mid = numb.substring(3, numb.length() - 3);
            }

            mid = mid.replaceAll("[0-9]","*");
            numb = first + mid + last;
        }
        holder.patPhone.setText(numb);
        holder.patDate.setText(patientForAppointList.getPh_date());
    }

    @Override
    public int getItemCount() {
        return mCategoryItem == null ? 0 : mCategoryItem.size();
    }

    public static class SPFAHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView prescriptionCode;
        TextView patName;
        TextView patCode;
        TextView patPhone;
        TextView patDate;
        ClickedItem mClickedItem;

        public SPFAHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            prescriptionCode = itemView.findViewById(R.id.prescription_code_search_payment);
            patName = itemView.findViewById(R.id.patient_name_search_payment);
            patCode = itemView.findViewById(R.id.patient_code_search_payment);
            patPhone = itemView.findViewById(R.id.patient_phone_search_payment);
            patDate = itemView.findViewById(R.id.patient_date_search_payment);
            this.mClickedItem = ci;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickedItem.onItemClicked(getAdapterPosition());
        }
    }

    public interface ClickedItem {
        void onItemClicked(int Position);
    }
}
