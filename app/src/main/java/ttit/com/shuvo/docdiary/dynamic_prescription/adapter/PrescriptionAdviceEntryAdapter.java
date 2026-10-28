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
import ttit.com.shuvo.docdiary.dynamic_prescription.model.AdviceEntryItem;

public class PrescriptionAdviceEntryAdapter extends RecyclerView.Adapter<PrescriptionAdviceEntryAdapter.EntryViewHolder> {
    public interface EntryActionListener {
        void onEditEntry(@NonNull AdviceEntryItem item);
        void onDeleteEntry(@NonNull AdviceEntryItem item);
    }

    private final ArrayList<AdviceEntryItem> items = new ArrayList<>();
    private final EntryActionListener actionListener;

    public PrescriptionAdviceEntryAdapter(@NonNull EntryActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(@Nullable ArrayList<AdviceEntryItem> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_clinical_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        AdviceEntryItem item = items.get(position);
        holder.nameView.setText(item.getDisplayText());
        holder.editButton.setContentDescription("Edit " + item.getDisplayText());
        holder.deleteButton.setContentDescription("Delete " + item.getDisplayText());
        holder.itemView.setOnClickListener(view -> actionListener.onEditEntry(item));
        holder.editButton.setOnClickListener(view -> actionListener.onEditEntry(item));
        holder.deleteButton.setOnClickListener(view -> actionListener.onDeleteEntry(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;

        private final AppCompatImageView editButton;
        private final AppCompatImageView deleteButton;

        EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.tv_clinical_entry_name);
            editButton = itemView.findViewById(R.id.iv_edit_clinical_entry);
            deleteButton = itemView.findViewById(R.id.iv_delete_clinical_entry);
        }
    }
}