package ttit.com.shuvo.docdiary.unit_app_schedule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.unit_app_schedule.arraylists.DoctorAppSchList;
import ttit.com.shuvo.docdiary.unit_app_schedule.arraylists.UnitDoctorsList;

public class DoctorsListAdapter extends RecyclerView.Adapter<DoctorsListAdapter.DLHolder> {
    private ArrayList<UnitDoctorsList> mCategoryItem;
    private final Context myContext;

    public DoctorsListAdapter(ArrayList<UnitDoctorsList> mCategoryItem, Context myContext) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public DLHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.doc_list_view_layout, parent, false);
        return new DLHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DLHolder holder, int position) {
        UnitDoctorsList unitDoctorsList = mCategoryItem.get(position);
        holder.docName.setText(unitDoctorsList.getDoc_name());
        String app_text = "Total Appointment:  " + unitDoctorsList.getApp_count();
        holder.appCount.setText(app_text);
        String bl_text = "Total Blank Schedule:  " + unitDoctorsList.getBlank_count();
        holder.blankCount.setText(bl_text);

        ArrayList<DoctorAppSchList> doctorAppSchLists = unitDoctorsList.getDoctorAppSchLists();

        if (doctorAppSchLists.isEmpty()) {
            holder.noSchFound.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager layoutManager =
                    new LinearLayoutManager(myContext, LinearLayoutManager.HORIZONTAL,false);
            DoctorAppSchAdapter doctorAppSchAdapter = new DoctorAppSchAdapter(doctorAppSchLists,myContext);

            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.recyclerView.setAdapter(doctorAppSchAdapter);
        }
        else {
            holder.noSchFound.setVisibility(View.GONE);
            RecyclerView.LayoutManager layoutManager =
                    new LinearLayoutManager(myContext, LinearLayoutManager.HORIZONTAL,false);
            DoctorAppSchAdapter doctorAppSchAdapter = new DoctorAppSchAdapter(doctorAppSchLists,myContext);

            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.recyclerView.setAdapter(doctorAppSchAdapter);
            String pos = "0";
            for (int i = 0; i < doctorAppSchLists.size(); i++) {
                if (!doctorAppSchLists.get(i).getPosition().equals("0")) {
                    pos = doctorAppSchLists.get(i).getPosition();
                }
            }
            holder.recyclerView.scrollToPosition(Integer.parseInt(pos));
        }
    }

    @Override
    public int getItemCount() {
        return mCategoryItem.size();
    }

    public static class DLHolder extends RecyclerView.ViewHolder {
        TextView docName;
        TextView appCount;
        TextView blankCount;
        TextView noSchFound;
        RecyclerView recyclerView;
        public DLHolder(@NonNull View itemView) {
            super(itemView);
            docName = itemView.findViewById(R.id.doc_name_unit_wise);
            appCount = itemView.findViewById(R.id.doc_app_count_unit_wise);
            blankCount = itemView.findViewById(R.id.doc_blank_count_unit_wise);
            noSchFound = itemView.findViewById(R.id.no_schedule_found_message_for_unit_wise_doctors);
            recyclerView = itemView.findViewById(R.id.doc_app_schedule_list_view);
        }
    }

    public void filterList(ArrayList<UnitDoctorsList> filteredList) {
        mCategoryItem = filteredList;
        notifyDataSetChanged();
    }
}
