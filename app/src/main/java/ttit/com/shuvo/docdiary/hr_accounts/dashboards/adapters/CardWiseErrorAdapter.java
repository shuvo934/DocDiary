package ttit.com.shuvo.docdiary.hr_accounts.dashboards.adapters;

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
import ttit.com.shuvo.docdiary.hr_accounts.dashboards.arraylists.OpasCardWiseErrorList;

public class CardWiseErrorAdapter extends RecyclerView.Adapter<CardWiseErrorAdapter.CWEHolder>{

    private final ArrayList<OpasCardWiseErrorList> dataLists;
    private final Context mContext;

    public CardWiseErrorAdapter(ArrayList<OpasCardWiseErrorList> dataLists, Context mContext) {
        this.dataLists = dataLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CWEHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_wise_error_view, parent, false);
        return new CWEHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CWEHolder holder, int position) {
        OpasCardWiseErrorList opasCardWiseErrorList = dataLists.get(position);
        holder.rType.setText(opasCardWiseErrorList.getResult_type());
        holder.rDesc.setText(opasCardWiseErrorList.getResult_desc());
        holder.errCount.setText(opasCardWiseErrorList.getFailed_count());
    }

    @Override
    public int getItemCount() {
        return dataLists == null ? 0 : dataLists.size();
    }

    public static class CWEHolder extends RecyclerView.ViewHolder {

        TextView rType;
        TextView rDesc;
        TextView errCount;
        LinearLayout itemDivider;

        public CWEHolder(@NonNull View itemView) {
            super(itemView);
            rType = itemView.findViewById(R.id.error_result_type);
            rDesc = itemView.findViewById(R.id.error_result_description);
            errCount = itemView.findViewById(R.id.total_error_count);
            itemDivider = itemView.findViewById(R.id.divider_for_card_wise_pay);
        }
    }
}
