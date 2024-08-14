package ttit.com.shuvo.docdiary.report_manager.report_view.reports.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists.DocWiseAppointDataList;


public class DocWiseAppointDataAdapter extends RecyclerView.Adapter<DocWiseAppointDataAdapter.DWADHolder>{
    private final ArrayList<DocWiseAppointDataList> dataLists;
    private final Context mContext;

    public DocWiseAppointDataAdapter(ArrayList<DocWiseAppointDataList> dataLists, Context mContext) {
        this.dataLists = dataLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public DWADHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.doc_wise_appoint_app_made_rep_view, parent, false);
        return new DWADHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DWADHolder holder, int position) {
        DocWiseAppointDataList docWiseAppointDataList = dataLists.get(position);
        holder.docName.setText(docWiseAppointDataList.getDoc_name());
        holder.regTime.setText(docWiseAppointDataList.getRegular_count());
        holder.extTime.setText(docWiseAppointDataList.getExtra_count());
        holder.busyTime.setText(docWiseAppointDataList.getBlock_count());
        holder.blankTime.setText(docWiseAppointDataList.getBlank_count());
        if (position > 0) {
            int index = position - 1;

            String unit_name_pre = dataLists.get(index).getDepts_name();
            String unit_name = docWiseAppointDataList.getDepts_name();

            if (position == dataLists.size() - 1) {
                holder.itemDivider.setVisibility(View.VISIBLE);
            }
            else if (position != dataLists.size() - 1 && position < dataLists.size() - 1 ) {
                int dex = position + 1;
                String unit_name_post = dataLists.get(dex).getDepts_name();
                if (unit_name.equals(unit_name_post)) {
                    holder.itemDivider.setVisibility(View.GONE);
                }
                else {
                    holder.itemDivider.setVisibility(View.VISIBLE);
                }
            }

            if (unit_name.equals(unit_name_pre)) {
                unit_name = "〃";
            }
            holder.unitName.setText(unit_name);

        }
        else {
            holder.unitName.setText(docWiseAppointDataList.getDepts_name());

            if (position == dataLists.size() - 1) {
                holder.itemDivider.setVisibility(View.VISIBLE);
            }
            else if (position != dataLists.size() - 1 && position < dataLists.size() - 1 ) {
                int index = position + 1;

                String unit_name_pre = dataLists.get(index).getDepts_name();
                String unit_name = docWiseAppointDataList.getDepts_name();

                if (unit_name.equals(unit_name_pre)) {
                    holder.itemDivider.setVisibility(View.GONE);
                }
                else {
                    holder.itemDivider.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataLists == null ? 0 : dataLists.size();
    }

    public static class DWADHolder extends RecyclerView.ViewHolder {

        TextView docName;
        TextView unitName;
        TextView regTime;
        TextView extTime;
        TextView busyTime;
        TextView blankTime;
        LinearLayout itemDivider;

        public DWADHolder(@NonNull View itemView) {
            super(itemView);

            docName = itemView.findViewById(R.id.doc_wise_app_rep_doc_name);
            unitName = itemView.findViewById(R.id.doc_wise_app_rep_unit_name);
            regTime = itemView.findViewById(R.id.doc_wise_app_rep_reg_time);
            extTime = itemView.findViewById(R.id.doc_wise_app_rep_extra_time);
            busyTime = itemView.findViewById(R.id.doc_wise_app_rep_blocked_time);
            blankTime = itemView.findViewById(R.id.doc_wise_app_rep_blank_time);
            itemDivider = itemView.findViewById(R.id.divider_for_doc_wise_app_rep_val);
        }
    }
}
