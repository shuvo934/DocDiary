package ttit.com.shuvo.docdiary.dynamic_prescription.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionHistoryItem;

public class PrescriptionHistoryAdapter extends RecyclerView.Adapter<PrescriptionHistoryAdapter.HistoryViewHolder> {

    public interface HistorySelectionListener {
        void onHistorySelected(@NonNull PrescriptionHistoryItem historyItem);
    }

    private final ArrayList<PrescriptionHistoryItem> allItems = new ArrayList<>();
    private final ArrayList<PrescriptionHistoryItem> items = new ArrayList<>();
    private final HistorySelectionListener selectionListener;

    @NonNull
    private String currentSearchText = "";

    @Nullable
    private String selectedRvmId;

    public PrescriptionHistoryAdapter(@NonNull HistorySelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(@Nullable List<PrescriptionHistoryItem> newItems) {
        allItems.clear();

        if (newItems != null) {
            allItems.addAll(newItems);
        }

        applyFilter();

        if (selectedRvmId != null && findItemByRvmId(selectedRvmId) == null) {
            selectedRvmId = null;
        }

        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(@Nullable CharSequence searchText) {
        currentSearchText = normalizeSearchText(searchText);
        applyFilter();

        selectedRvmId = null;
        notifyDataSetChanged();
    }

    public int getTotalItemCount() {
        return allItems.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelection() {
        selectedRvmId = null;
        notifyDataSetChanged();
    }

    @Nullable
    public PrescriptionHistoryItem getSelectedItem() {
        if (selectedRvmId == null) {
            return null;
        }

        return findItemByRvmId(selectedRvmId);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        PrescriptionHistoryItem item = items.get(position);

        boolean selected = item.getRvmId().equals(selectedRvmId);

        holder.dateView.setText(item.getPrescriptionDate());
        holder.codeView.setText(item.getRvmCode());

        setOptionalText(holder.departmentView, item.getDepartmentName());
        setOptionalText(holder.serviceView, item.getServiceName());

        String appointmentText = buildAppointmentText(item);
        setOptionalText(holder.appointmentView, appointmentText);

        String createdByText = buildCreatedByText(item);
        setOptionalText(holder.createdByView, createdByText);

        String typeText = buildTypeText(item);
        setOptionalText(holder.typeView, typeText);

        String l_t = "";
        if (!item.getLastUpdatedTime().isEmpty()) {
            l_t = "Last Updated: " + item.getLastUpdatedTime();
        }
        setOptionalText(holder.lastUpdatedTimeView, l_t);

        holder.selectedIcon.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);

        holder.cardView.setStrokeColor(Color.parseColor(selected ? "#27AE60" : "#EAECF0"));
        holder.cardView.setCardBackgroundColor(Color.parseColor(selected ? "#F2FBF6" : "#FFFFFF"));

        holder.itemView.setOnClickListener(view -> {
            selectedRvmId = item.getRvmId();
            notifyDataSetChanged();
            selectionListener.onHistorySelected(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void setOptionalText(@NonNull TextView textView, @NonNull String value) {
        textView.setText(value);
        textView.setVisibility(value.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void applyFilter() {
        items.clear();

        if (currentSearchText.isEmpty()) {
            items.addAll(allItems);
            return;
        }

        for (PrescriptionHistoryItem item : allItems) {
            String prescriptionCode = normalizeSearchText(item.getRvmCode());

            if (prescriptionCode.contains(currentSearchText)) {
                items.add(item);
            }
        }
    }

    @NonNull
    private String normalizeSearchText(@Nullable CharSequence value) {
        if (value == null) {
            return "";
        }

        return value.toString()
                .trim()
                .replace(" ", "")
                .toLowerCase(Locale.ROOT);
    }

    @NonNull
    private String buildAppointmentText(@NonNull PrescriptionHistoryItem item) {
        String appointmentDate = item.getAppointmentDate();
        String scheduleTime = item.getScheduleTime();

        if (appointmentDate.isEmpty() && scheduleTime.isEmpty()) {
            return "";
        }

        StringBuilder textBuilder = new StringBuilder("Appointment: ");

        if (!appointmentDate.isEmpty()) {
            textBuilder.append(appointmentDate);
        }

        if (!scheduleTime.isEmpty()) {
            if (!appointmentDate.isEmpty()) {
                textBuilder.append(" • ");
            }

            textBuilder.append(scheduleTime);
        }

        return textBuilder.toString();
    }

    @NonNull
    private String buildCreatedByText(@NonNull PrescriptionHistoryItem item) {
        String doctorName = item.getDoctorName();
        String designationName = item.getDesignationName();

        if (doctorName.isEmpty() && designationName.isEmpty()) {
            return "";
        }

        StringBuilder textBuilder = new StringBuilder("Created By: ");

        if (!doctorName.isEmpty()) {
            textBuilder.append(doctorName);
        }

        if (!designationName.isEmpty()) {
            if (!doctorName.isEmpty()) {
                textBuilder.append(" • ");
            }

            textBuilder.append(designationName);
        }

        return textBuilder.toString();
    }

    @NonNull
    private String buildTypeText(@NonNull PrescriptionHistoryItem item) {
        String prescriptionType = item.getPrescriptionType();
        String patientType = item.getPatientType();

        if (prescriptionType.isEmpty()) {
            return patientType;
        }

        if (patientType.isEmpty()) {
            return prescriptionType;
        }

        return prescriptionType + " • " + patientType;
    }

    @Nullable
    private PrescriptionHistoryItem findItemByRvmId(@NonNull String rvmId) {
        for (PrescriptionHistoryItem item : items) {
            if (item.getRvmId().equals(rvmId)) {
                return item;
            }
        }

        return null;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final TextView dateView;
        private final TextView codeView;
        private final TextView departmentView;
        private final TextView serviceView;
        private final TextView appointmentView;
        private final TextView createdByView;
        private final TextView typeView;
        private final TextView lastUpdatedTimeView;
        private final AppCompatImageView selectedIcon;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_prescription_history_item);
            dateView = itemView.findViewById(R.id.tv_history_date);
            codeView = itemView.findViewById(R.id.tv_history_code);
            departmentView = itemView.findViewById(R.id.tv_history_department);
            serviceView = itemView.findViewById(R.id.tv_history_service);
            appointmentView = itemView.findViewById(R.id.tv_history_appointment);
            createdByView = itemView.findViewById(R.id.tv_history_prescription_creation);
            typeView = itemView.findViewById(R.id.tv_history_type);
            lastUpdatedTimeView = itemView.findViewById(R.id.tv_presc_last_updated);
            selectedIcon = itemView.findViewById(R.id.iv_prescription_history_item_selected);

        }
    }
}