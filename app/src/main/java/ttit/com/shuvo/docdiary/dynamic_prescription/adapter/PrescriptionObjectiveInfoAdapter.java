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
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ObjectiveInfoItem;

public class PrescriptionObjectiveInfoAdapter extends RecyclerView.Adapter<PrescriptionObjectiveInfoAdapter.ObjectiveViewHolder> {
    public interface ObjectiveActionListener {
        void onEditObjective(@NonNull ObjectiveInfoItem item);
        void onDeleteObjective(@NonNull ObjectiveInfoItem item);
    }

    private final ArrayList<ObjectiveInfoItem> items = new ArrayList<>();

    private final ObjectiveActionListener actionListener;

    public PrescriptionObjectiveInfoAdapter(@NonNull ObjectiveActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(@Nullable ArrayList<ObjectiveInfoItem> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ObjectiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_objective_info, parent, false);
        return new ObjectiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObjectiveViewHolder holder, int position) {
        ObjectiveInfoItem item = items.get(position);
        holder.bind(item, actionListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ObjectiveViewHolder extends RecyclerView.ViewHolder {
        private final TextView objectiveNameView;
        private final TextView objectiveValueView;
        private final TextView muscleJointView;
        private final TextView notesView;

        private final AppCompatImageView editButton;
        private final AppCompatImageView deleteButton;

        ObjectiveViewHolder(@NonNull View itemView) {
            super(itemView);
            objectiveNameView = itemView.findViewById(R.id.tv_objective_entry_name);
            objectiveValueView = itemView.findViewById(R.id.tv_objective_entry_value);
            muscleJointView = itemView.findViewById(R.id.tv_objective_entry_muscle_joint);
            notesView = itemView.findViewById(R.id.tv_objective_entry_notes);
            editButton = itemView.findViewById(R.id.iv_edit_objective_entry);
            deleteButton = itemView.findViewById(R.id.iv_delete_objective_entry);
        }

        @SuppressLint("SetTextI18n")
        private void bind(@NonNull ObjectiveInfoItem item, @NonNull ObjectiveActionListener listener) {
            objectiveNameView.setText(item.getObjectiveName());

            if (item.hasObjectiveValue()) {
                objectiveValueView.setVisibility(View.VISIBLE);
                objectiveValueView.setText("Value: " + item.getObjectiveValueName());
            } else {
                objectiveValueView.setVisibility(View.GONE);
                objectiveValueView.setText("");
            }

            if (item.hasMuscleJoint()) {
                muscleJointView.setVisibility(View.VISIBLE);
                muscleJointView.setText("Muscle / Joint: " + item.getMuscleJointName());

            } else {
                muscleJointView.setVisibility(View.GONE);
                muscleJointView.setText("");
            }

            if (item.hasNotes()) {
                notesView.setVisibility(View.VISIBLE);
                notesView.setText("Notes: " + item.getNotes());

            } else {
                notesView.setVisibility(View.GONE);
                notesView.setText("");
            }

            String objectiveName = item.getObjectiveName();
            editButton.setContentDescription("Edit " + objectiveName);
            deleteButton.setContentDescription("Delete " + objectiveName);
            itemView.setOnClickListener(view -> listener.onEditObjective(item));
            editButton.setOnClickListener(view -> listener.onEditObjective(item));
            deleteButton.setOnClickListener(view -> listener.onDeleteObjective(item));
        }
    }
}