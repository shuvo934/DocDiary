package ttit.com.shuvo.docdiary.login.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.login.arraylists.CenterList;

public class CenterAdapter extends RecyclerView.Adapter<CenterAdapter.CENTERHolder> {

    private final ArrayList<CenterList> mCategory;
    private final Context myContext;
    private final String selectedCenter;
    private final ClickedItem myClickedItem;

    public CenterAdapter(ArrayList<CenterList> mCategory, Context myContext, String selectedCenter, ClickedItem myClickedItem) {
        this.mCategory = mCategory;
        this.myContext = myContext;
        this.selectedCenter = selectedCenter;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public CENTERHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.center_list_details_view, parent, false);
        return new CENTERHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull CENTERHolder holder, int position) {
        CenterList centerList = mCategory.get(position);
        holder.centerName.setText(centerList.getCenter_name());
        if (selectedCenter != null && !selectedCenter.isEmpty()) {
            if (selectedCenter.equals(centerList.getCenter_api())) {
                holder.checkCenter.setVisibility(View.VISIBLE);
                holder.centerLay.setBackground(ContextCompat.getDrawable(myContext, R.drawable.border_design_selected));
            }
            else {
                holder.checkCenter.setVisibility(View.GONE);
                holder.centerLay.setBackground(ContextCompat.getDrawable(myContext, R.drawable.border_design));
            }
        }
        else {
            holder.checkCenter.setVisibility(View.GONE);
            holder.centerLay.setBackground(ContextCompat.getDrawable(myContext, R.drawable.border_design));
        }
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class CENTERHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView centerName;
        ImageView checkCenter;
        LinearLayout centerLay;
        ClickedItem mClickedItem;

        public CENTERHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            centerName = itemView.findViewById(R.id.data_center_name);
            centerLay = itemView.findViewById(R.id.center_list_layout);
            checkCenter = itemView.findViewById(R.id.selected_center_check_image);
            checkCenter.setVisibility(View.GONE);
            this.mClickedItem = ci;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickedItem.onCategoryClicked(getAdapterPosition());
        }
    }
    public interface ClickedItem {
        void onCategoryClicked(int CategoryPosition);
    }
}
