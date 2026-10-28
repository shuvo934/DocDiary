package ttit.com.shuvo.docdiary.dynamic_prescription.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.VitalData;

public class PrescriptionVitalsAdapter extends RecyclerView.Adapter<PrescriptionVitalsAdapter.VitalsViewHolder> {

    public interface OnVitalsClickListener {
        void onVitalsClick();
    }

    private VitalData vitalData;

    private final OnVitalsClickListener clickListener;

    public PrescriptionVitalsAdapter(@NonNull OnVitalsClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitVitals(@Nullable VitalData vitalData) {
        this.vitalData = vitalData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VitalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_vitals_summary, parent, false);
        return new VitalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VitalsViewHolder holder, int position) {
        if (vitalData == null) {
            return;
        }

        String bloodPressure = "";

        if (!vitalData.getSystolic().isEmpty() && !vitalData.getDiastolic().isEmpty()) {
            bloodPressure = "BP: "
                            + vitalData.getSystolic()
                            + "/"
                            + vitalData.getDiastolic()
                            + " mmHg";
        }

        String pulse = createValue("Pulse", vitalData.getPulse(), " bpm");

        String temperature = createValue("Temperature", vitalData.getTemperature(), " °F");

        String spo2 = createValue("SpO₂", vitalData.getSpo2(), "%");

        String respiratoryRate = createValue("Respiratory", vitalData.getRespiratoryRate(), "/min");

        String bmi = createValue("BMI", vitalData.getBmi(), " kg/m²");

        String height = createValue("Height", vitalData.getHeight(), " cm");

        String weight = createValue("Weight", vitalData.getWeight(), " kg");

        bindSummaryRow(holder.rowOne, bloodPressure, pulse);
        bindSummaryRow(holder.rowTwo, temperature, spo2);
        bindSummaryRow(holder.rowThree, respiratoryRate, bmi);
        bindSummaryRow(holder.rowFour, height, weight);

        holder.itemView.setOnClickListener(view -> clickListener.onVitalsClick());
        holder.editButton.setOnClickListener(view -> clickListener.onVitalsClick());
    }

    private String createValue(@NonNull String label, @NonNull String value, @NonNull String unit) {
        if (value.isEmpty()) {
            return "";
        }
        return label + ": " + value + unit;
    }

    private void bindSummaryRow(@NonNull TextView textView, @NonNull String firstValue, @NonNull String secondValue) {
        String rowText;
        if (!firstValue.isEmpty() && !secondValue.isEmpty()) {
            rowText = firstValue + "  •  " + secondValue;

        } else if (!firstValue.isEmpty()) {
            rowText = firstValue;

        } else {
            rowText = secondValue;
        }

        boolean hasValue = !rowText.isEmpty();

        textView.setText(rowText);
        textView.setVisibility(hasValue ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return vitalData == null ? 0 : 1;
    }

    public static class VitalsViewHolder extends RecyclerView.ViewHolder {
        private final View editButton;
        private final TextView rowOne;
        private final TextView rowTwo;
        private final TextView rowThree;
        private final TextView rowFour;

        VitalsViewHolder(@NonNull View itemView) {
            super(itemView);
            editButton = itemView.findViewById(R.id.iv_edit_vitals);
            rowOne = itemView.findViewById(R.id.tv_vitals_summary_row_one);
            rowTwo = itemView.findViewById(R.id.tv_vitals_summary_row_two);
            rowThree = itemView.findViewById(R.id.tv_vitals_summary_row_three);
            rowFour = itemView.findViewById(R.id.tv_vitals_summary_row_four);
        }
    }
}