package ttit.com.shuvo.docdiary.dynamic_prescription.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionPatientItem;

public class PrescriptionPatientAdapter extends RecyclerView.Adapter<PrescriptionPatientAdapter.PatientViewHolder> {

    public interface PatientSelectionListener {
        void onPatientSelected(@NonNull PrescriptionPatientItem patientItem);
    }

    private final ArrayList<PrescriptionPatientItem> items = new ArrayList<>();

    @NonNull
    private final PatientSelectionListener selectionListener;

    @Nullable
    private PrescriptionPatientItem selectedPatient;

    public PrescriptionPatientAdapter(@NonNull PatientSelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(@Nullable List<PrescriptionPatientItem> newItems) {
        items.clear();

        if (newItems != null) {
            items.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    public void setSelectedPatient(@Nullable PrescriptionPatientItem patientItem) {
        int previousSelectedPosition = findPatientPosition(selectedPatient);
        selectedPatient = patientItem;
        int currentSelectedPosition = findPatientPosition(selectedPatient);

        if (previousSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelectedPosition);
        }

        if (currentSelectedPosition != RecyclerView.NO_POSITION && currentSelectedPosition != previousSelectedPosition) {
            notifyItemChanged(currentSelectedPosition);
        }
    }

    @Nullable
    public PrescriptionPatientItem getSelectedPatient() {
        return selectedPatient;
    }

    public void clearSelection() {
        setSelectedPatient(null);
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        PrescriptionPatientItem item = items.get(position);

        boolean selected = isSamePatient(item, selectedPatient);

        holder.patientNameView.setText(item.getPatientName());
        holder.patientCodeView.setText(item.getPatientCode());
        holder.genderAgeView.setText(item.getGenderAgeText());

        setOptionalText(holder.thanaNameView, item.getThanaName(), true);
        setOptionalText(holder.registrationYearView, item.getRegistrationYear(), true);
        setOptionalText(holder.phoneView, item.getMobileNumber(), false);
        holder.selectedIcon.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        holder.cardView.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), selected ? R.color.green_sea : R.color.prescription_outline));

        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                selected ? R.color.prescription_primary_light : R.color.white));

        holder.itemView.setOnClickListener(view -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }

            PrescriptionPatientItem selectedItem = items.get(adapterPosition);
            setSelectedPatient(selectedItem);
            selectionListener.onPatientSelected(selectedItem);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("SetTextI18n")
    private void setOptionalText(@NonNull TextView textView, @Nullable String value, boolean addBrackets) {
        String cleanValue = value == null ? "" : value.trim();

        if (cleanValue.isEmpty()) {
            textView.setText("");
            textView.setVisibility(View.GONE);
            return;
        }

        textView.setVisibility(View.VISIBLE);

        if (addBrackets) {
            textView.setText("(" + cleanValue + ")");

        } else {
            textView.setText(cleanValue);
        }
    }

    private int findPatientPosition(@Nullable PrescriptionPatientItem patientItem) {
        if (patientItem == null) {
            return RecyclerView.NO_POSITION;
        }

        for (int position = 0; position < items.size(); position++) {

            if (isSamePatient(items.get(position), patientItem)) {
                return position;
            }
        }

        return RecyclerView.NO_POSITION;
    }

    private boolean isSamePatient(@Nullable PrescriptionPatientItem firstPatient, @Nullable PrescriptionPatientItem secondPatient) {
        if (firstPatient == null || secondPatient == null) {
            return false;
        }

        /*
         * PH_ID দিয়ে patient history uniquely identify হবে।
         */
        if (!firstPatient.getPatientHistoryId().isEmpty() && !secondPatient.getPatientHistoryId().isEmpty()) {
            return firstPatient.getPatientHistoryId().equals(secondPatient.getPatientHistoryId());
        }

        return firstPatient.getPatientId().equals(secondPatient.getPatientId());
    }

    public static class PatientViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;

        private final TextView patientNameView;
        private final TextView patientCodeView;
        private final TextView genderAgeView;

        private final TextView thanaNameView;
        private final TextView registrationYearView;
        private final TextView phoneView;

        private final AppCompatImageView selectedIcon;

        PatientViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_prescription_patient_item);
            patientNameView = itemView.findViewById(R.id.tv_patient_item_name);
            patientCodeView = itemView.findViewById(R.id.tv_patient_item_code);
            genderAgeView = itemView.findViewById(R.id.tv_patient_item_gender_age);
            thanaNameView = itemView.findViewById(R.id.tv_patient_thana_name);
            registrationYearView = itemView.findViewById(R.id.tv_patient_reg_year);
            phoneView = itemView.findViewById(R.id.tv_patient_phone);
            selectedIcon = itemView.findViewById(R.id.iv_patient_item_selected);
        }
    }
}