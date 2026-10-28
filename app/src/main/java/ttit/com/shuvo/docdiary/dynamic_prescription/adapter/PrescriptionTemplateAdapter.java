package ttit.com.shuvo.docdiary.dynamic_prescription.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.PrescriptionTemplateItem;

public class PrescriptionTemplateAdapter extends RecyclerView.Adapter<PrescriptionTemplateAdapter.TemplateViewHolder> {

    public interface TemplateSelectionListener {
        void onTemplateSelected(@NonNull PrescriptionTemplateItem templateItem);
    }

    /*
     * API থেকে পাওয়া সম্পূর্ণ list।
     */
    private final ArrayList<PrescriptionTemplateItem> allItems = new ArrayList<>();

    /*
     * Search অনুযায়ী RecyclerView-তে দেখানো list।
     */
    private final ArrayList<PrescriptionTemplateItem> visibleItems = new ArrayList<>();

    private final TemplateSelectionListener selectionListener;

    @Nullable
    private String selectedTemplateId;

    @NonNull
    private String currentSearchQuery = "";

    public PrescriptionTemplateAdapter(@NonNull TemplateSelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submitList(@Nullable List<PrescriptionTemplateItem> newItems) {
        allItems.clear();

        if (newItems != null) {
            allItems.addAll(newItems);
        }

        if (selectedTemplateId != null && findItemById(selectedTemplateId) == null) {
            selectedTemplateId = null;
        }

        applyCurrentFilter();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(@Nullable String query) {
        currentSearchQuery = normalizeSearchQuery(query);
        applyCurrentFilter();
        notifyDataSetChanged();
    }

    public void clearFilter() {
        filter("");
    }

    public boolean isFiltering() {
        return !currentSearchQuery.isEmpty();
    }

    public int getTotalItemCount() {
        return allItems.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedTemplateId(@Nullable String templateId) {
        selectedTemplateId = normalizeId(templateId);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelection() {
        selectedTemplateId = null;
        notifyDataSetChanged();
    }

    @Nullable
    public PrescriptionTemplateItem getSelectedItem() {
        if (selectedTemplateId == null) {
            return null;
        }

        return findItemById(selectedTemplateId);
    }

    @NonNull
    @Override
    public TemplateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_template, parent, false);

        return new TemplateViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull TemplateViewHolder holder, int position) {
        PrescriptionTemplateItem item = visibleItems.get(position);
        boolean selected = item.getTemplateId().equals(selectedTemplateId);

        holder.nameView.setText(item.getTemplateName());
        holder.descriptionView.setText(item.getTemplateDescription());
        holder.descriptionView.setVisibility(item.hasDescription() ? View.VISIBLE : View.GONE);

        holder.radioButton.setChecked(selected);

        holder.cardView.setStrokeColor(Color.parseColor(selected ? "#27AE60" : "#EAECF0"));

        holder.cardView.setCardBackgroundColor(Color.parseColor(selected ? "#F2FBF6" : "#FFFFFF"));

        holder.itemView.setOnClickListener(view -> {
            selectedTemplateId = item.getTemplateId();
            notifyDataSetChanged();
            selectionListener.onTemplateSelected(item);
        });
    }

    @Override
    public int getItemCount() {
        return visibleItems.size();
    }

    private void applyCurrentFilter() {
        visibleItems.clear();

        if (currentSearchQuery.isEmpty()) {
            visibleItems.addAll(allItems);
            return;
        }

        for (PrescriptionTemplateItem item : allItems) {
            String templateName = normalizeSearchValue(item.getTemplateName());
            String templateDescription = normalizeSearchValue(item.getTemplateDescription());

            if (templateName.contains(currentSearchQuery) || templateDescription.contains(currentSearchQuery)) {
                visibleItems.add(item);
            }
        }
    }

    @Nullable
    private PrescriptionTemplateItem findItemById(@NonNull String templateId) {
        for (PrescriptionTemplateItem item : allItems) {
            if (item.getTemplateId().equals(templateId)) {
                return item;
            }
        }

        return null;
    }

    @NonNull
    private String normalizeSearchQuery(@Nullable String query) {
        if (query == null) {
            return "";
        }

        return query.trim().toLowerCase(Locale.ROOT);
    }

    @NonNull
    private String normalizeSearchValue(@Nullable String value) {
        if (value == null) {
            return "";
        }

        return value.trim().toLowerCase(Locale.ROOT);
    }

    @Nullable
    private String normalizeId(@Nullable String templateId) {
        if (templateId == null || templateId.trim().isEmpty()) {
            return null;
        }

        return templateId.trim();
    }

    public static class TemplateViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final TextView nameView;
        private final TextView descriptionView;
        private final MaterialRadioButton radioButton;

        TemplateViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_prescription_template_item);
            nameView = itemView.findViewById(R.id.tv_prescription_template_name);
            descriptionView = itemView.findViewById(R.id.tv_prescription_template_description);
            radioButton = itemView.findViewById(R.id.radio_prescription_template);
        }
    }
}