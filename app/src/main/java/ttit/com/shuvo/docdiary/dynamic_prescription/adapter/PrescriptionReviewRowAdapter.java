package ttit.com.shuvo.docdiary.dynamic_prescription.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ReviewSummaryItem;

public class PrescriptionReviewRowAdapter extends RecyclerView.Adapter<PrescriptionReviewRowAdapter.RowViewHolder> {

    private final ArrayList<ReviewSummaryItem> items =
            new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(ArrayList<ReviewSummaryItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_review_row, parent, false);
        return new RowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {

        ReviewSummaryItem item = items.get(position);
        holder.labelView.setText(item.getLabel());
        holder.valueView.setText(item.getValue());
        holder.divider.setVisibility(
                position == items.size() - 1
                        ? View.GONE
                        : View.VISIBLE
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RowViewHolder extends RecyclerView.ViewHolder {
        private final TextView labelView;
        private final TextView valueView;
        private final View divider;

        RowViewHolder(@NonNull View itemView) {
            super(itemView);
            labelView = itemView.findViewById(R.id.tv_review_row_label);
            valueView = itemView.findViewById(R.id.tv_review_row_value);
            divider = itemView.findViewById(R.id.view_review_row_divider);
        }
    }
}