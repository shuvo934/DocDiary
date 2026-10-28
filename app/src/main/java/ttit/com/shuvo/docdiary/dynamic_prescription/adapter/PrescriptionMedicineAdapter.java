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
import ttit.com.shuvo.docdiary.dynamic_prescription.model.MedicineEntryItem;

public class PrescriptionMedicineAdapter extends RecyclerView.Adapter<PrescriptionMedicineAdapter.MedicineViewHolder> {

    public interface MedicineActionListener {
        void onEditMedicine(@NonNull MedicineEntryItem item);
        void onDeleteMedicine(@NonNull MedicineEntryItem item);
    }

    private final ArrayList<MedicineEntryItem> items = new ArrayList<>();
    private final MedicineActionListener actionListener;

    public PrescriptionMedicineAdapter(@NonNull MedicineActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(@Nullable ArrayList<MedicineEntryItem> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        MedicineEntryItem item = items.get(position);

        holder.nameView.setText(item.getMedicineName());
        holder.doseView.setText("Dose: " + item.getDoseName());
        holder.timingRouteView.setText(item.getTimingName() + " • " + item.getRouteName());
        if (item.getDurationDays().isEmpty()) {
            holder.durationView.setText("");
            holder.durationView.setVisibility(View.GONE);
        } else {
            holder.durationView.setText("Duration: " + item.getDurationDays() + " Days");
            holder.durationView.setVisibility(View.VISIBLE);
        }

        if (item.getNotes().isEmpty()) {
            holder.notesView.setText("");
            holder.notesView.setVisibility(View.GONE);
        } else {
            holder.notesView.setText("Notes: " + item.getNotes());
            holder.notesView.setVisibility(View.VISIBLE);
        }

        holder.editButton.setContentDescription("Edit " + item.getMedicineName());
        holder.deleteButton.setContentDescription("Delete " + item.getMedicineName());

        holder.itemView.setOnClickListener(view -> actionListener.onEditMedicine(item));
        holder.editButton.setOnClickListener(view -> actionListener.onEditMedicine(item));
        holder.deleteButton.setOnClickListener(view -> actionListener.onDeleteMedicine(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView;
        private final TextView doseView;
        private final TextView timingRouteView;
        private final TextView durationView;
        private final TextView notesView;

        private final AppCompatImageView editButton;
        private final AppCompatImageView deleteButton;

        MedicineViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.tv_medicine_entry_name);
            doseView = itemView.findViewById(R.id.tv_medicine_entry_dose);
            timingRouteView = itemView.findViewById(R.id.tv_medicine_entry_timing_route);
            durationView = itemView.findViewById(R.id.tv_medicine_entry_duration);
            notesView = itemView.findViewById(R.id.tv_medicine_entry_notes);

            editButton = itemView.findViewById(R.id.iv_edit_medicine_entry);
            deleteButton = itemView.findViewById(R.id.iv_delete_medicine_entry);
        }
    }
}