package ttit.com.shuvo.docdiary.report_manager.report_view.reports.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.report_manager.report_view.reports.arraylists.DocWisePayDataList;

public class DocWisePayDataAdapter extends RecyclerView.Adapter<DocWisePayDataAdapter.DWPDHolder> {

    private final ArrayList<DocWisePayDataList> dataLists;
    private final Context mContext;

    public DocWisePayDataAdapter(ArrayList<DocWisePayDataList> dataLists, Context mContext) {
        this.dataLists = dataLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public DWPDHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.doc_wise_pay_app_made_rep_view, parent, false);
        return new DWPDHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DWPDHolder holder, int position) {
        DocWisePayDataList docWisePayDataList = dataLists.get(position);
        holder.appDate.setText(docWisePayDataList.getApp_date1());
        holder.dateReg.setText(docWisePayDataList.getDate_total_reg());
        holder.dateETS.setText(docWisePayDataList.getDate_total_ets());
        holder.dateTotal.setText(docWisePayDataList.getDate_total());
    }

    @Override
    public int getItemCount() {
        return dataLists == null ? 0 : dataLists.size();
    }

    public static class DWPDHolder extends RecyclerView.ViewHolder {

        TextView appDate;
        TextView dateReg;
        TextView dateETS;
        TextView dateTotal;


        public DWPDHolder(@NonNull View itemView) {
            super(itemView);

            appDate = itemView.findViewById(R.id.doc_wise_pay_rcv_rep_app_date);
            dateReg = itemView.findViewById(R.id.doc_wise_pay_rcv_rep_date_amnt_reg);
            dateETS = itemView.findViewById(R.id.doc_wise_pay_rcv_rep_date_amnt_ets);
            dateTotal = itemView.findViewById(R.id.doc_wise_pay_rcv_rep_date_tot_amnt);
        }
    }
}
