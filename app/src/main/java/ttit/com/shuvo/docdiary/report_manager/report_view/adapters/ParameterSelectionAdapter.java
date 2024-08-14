package ttit.com.shuvo.docdiary.report_manager.report_view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.report_manager.report_view.arraylists.ParameterList;


public class ParameterSelectionAdapter extends RecyclerView.Adapter<ParameterSelectionAdapter.PSAHolder> {
    private ArrayList<ParameterList> mCategoryItem;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public ParameterSelectionAdapter(ArrayList<ParameterList> mCategoryItem, Context myContext, ClickedItem myClickedItem) {
        this.mCategoryItem = mCategoryItem;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public PSAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.report_parameter_selection_details_view, parent, false);
        return new PSAHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PSAHolder holder, int position) {
        holder.parameterName.setText(mCategoryItem.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mCategoryItem == null ? 0 : mCategoryItem.size();
    }

    public static class PSAHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView parameterName;
        ClickedItem mClickedItem;
        public PSAHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            parameterName = itemView.findViewById(R.id.report_parameter_name_details);
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

    public void filterList(ArrayList<ParameterList> filteredList) {
        mCategoryItem = filteredList;
        notifyDataSetChanged();
    }
}
