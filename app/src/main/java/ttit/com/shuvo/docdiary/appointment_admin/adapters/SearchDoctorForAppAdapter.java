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
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.DoctorForAppList;

public class SearchDoctorForAppAdapter extends RecyclerView.Adapter<SearchDoctorForAppAdapter.SDFAHolder> {

    private ArrayList<DoctorForAppList> mCategoryItem;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public SearchDoctorForAppAdapter(ArrayList<DoctorForAppList> mCategoryItem, Context myContext, ClickedItem myClickedItem) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public SDFAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.doctor_search_for_app_details_view, parent, false);
        return new SDFAHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull SDFAHolder holder, int position) {
        DoctorForAppList pa = mCategoryItem.get(position);
        String dc = pa.getDoc_name() + " (" + pa.getDoc_code()+")";
        holder.doctorName.setText(dc);
        holder.doctorDesig.setText(pa.getDesig_name());
        holder.doctorUnit.setText(pa.getDepts_name());
        holder.doctorDepart.setText(pa.getDeptd_name());
    }

    @Override
    public int getItemCount() {
        return mCategoryItem == null ? 0 : mCategoryItem.size();
    }

    public static class SDFAHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView doctorName;
        TextView doctorDesig;
        TextView doctorUnit;
        TextView doctorDepart;
        ClickedItem mClickedItem;
        public SDFAHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name_for_pat_app);
            doctorDesig = itemView.findViewById(R.id.doctor_desig_name_for_pat_app);
            doctorUnit = itemView.findViewById(R.id.doctor_unit_name_for_pat_app);
            doctorDepart = itemView.findViewById(R.id.doctor_department_name_for_pat_app);
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

    public void filterList(ArrayList<DoctorForAppList> filteredList) {
        mCategoryItem = filteredList;
        notifyDataSetChanged();
    }
}
