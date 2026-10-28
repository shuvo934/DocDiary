package ttit.com.shuvo.docdiary.dynamic_prescription.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.TreatmentPlanEntryItem;

public class PrescriptionTreatmentPlanAdapter extends RecyclerView.Adapter<PrescriptionTreatmentPlanAdapter.TreatmentPlanViewHolder> {

    public interface TreatmentPlanActionListener {
        void onEditTreatmentPlan(@NonNull TreatmentPlanEntryItem item);
        void onDeleteTreatmentPlan(@NonNull TreatmentPlanEntryItem item);
    }

    private final ArrayList<TreatmentPlanEntryItem> items = new ArrayList<>();
    private final TreatmentPlanActionListener actionListener;

    public PrescriptionTreatmentPlanAdapter(@NonNull TreatmentPlanActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(@Nullable ArrayList<TreatmentPlanEntryItem> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TreatmentPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_clinical_entry, parent, false);
        return new TreatmentPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreatmentPlanViewHolder holder, int position) {
        TreatmentPlanEntryItem item = items.get(position);

        holder.nameView.setText(item.getDisplayText());

        holder.editButton.setContentDescription("Edit " + item.getDisplayText());
        holder.deleteButton.setContentDescription("Delete " + item.getDisplayText());

        holder.itemView.setOnClickListener(view -> actionListener.onEditTreatmentPlan(item));
        holder.editButton.setOnClickListener(view -> actionListener.onEditTreatmentPlan(item));
        holder.deleteButton.setOnClickListener(view -> actionListener.onDeleteTreatmentPlan(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TreatmentPlanViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView;
        private final AppCompatImageView editButton;
        private final AppCompatImageView deleteButton;

        TreatmentPlanViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.tv_clinical_entry_name);
            editButton = itemView.findViewById(R.id.iv_edit_clinical_entry);
            deleteButton = itemView.findViewById(R.id.iv_delete_clinical_entry);
        }
    }
}