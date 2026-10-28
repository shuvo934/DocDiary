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
import ttit.com.shuvo.docdiary.dynamic_prescription.model.TherapyEntryItem;

public class PrescriptionTherapyAdapter extends RecyclerView.Adapter<PrescriptionTherapyAdapter.TherapyViewHolder> {

    public interface TherapyActionListener {
        void onEditTherapy(@NonNull TherapyEntryItem item);
        void onDeleteTherapy(@NonNull TherapyEntryItem item);
    }

    private final ArrayList<TherapyEntryItem> items = new ArrayList<>();
    private final TherapyActionListener actionListener;

    public PrescriptionTherapyAdapter(@NonNull TherapyActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(@Nullable ArrayList<TherapyEntryItem> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TherapyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_clinical_entry, parent, false);
        return new TherapyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TherapyViewHolder holder, int position) {
        TherapyEntryItem item = items.get(position);

        holder.nameView.setText(item.getTherapyName());
        if (item.hasNotes()) {
            holder.notesView.setText("Notes: " + item.getNotes());
            holder.notesView.setVisibility(View.VISIBLE);
        } else {
            holder.notesView.setText("");
            holder.notesView.setVisibility(View.GONE);
        }
        holder.editButton.setContentDescription("Edit " + item.getTherapyName());
        holder.deleteButton.setContentDescription("Delete " + item.getTherapyName());

        holder.itemView.setOnClickListener(view -> actionListener.onEditTherapy(item));
        holder.editButton.setOnClickListener(view -> actionListener.onEditTherapy(item));
        holder.deleteButton.setOnClickListener(view -> actionListener.onDeleteTherapy(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TherapyViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameView;
        private final TextView notesView;
        private final AppCompatImageView editButton;
        private final AppCompatImageView deleteButton;

        TherapyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.tv_clinical_entry_name);
            notesView = itemView.findViewById(R.id.tv_clinical_entry_notes);
            editButton = itemView.findViewById(R.id.iv_edit_clinical_entry);
            deleteButton = itemView.findViewById(R.id.iv_delete_clinical_entry);
        }
    }
}