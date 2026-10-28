package ttit.com.shuvo.docdiary.dynamic_prescription.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.dynamic_prescription.model.ClinicalOption;

public class ClinicalOptionAdapter extends BaseAdapter implements Filterable {
    private final LayoutInflater inflater;
    private final ArrayList<ClinicalOption> sourceItems = new ArrayList<>();
    private final ArrayList<ClinicalOption> filteredItems = new ArrayList<>();

    public ClinicalOptionAdapter(@NonNull Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void submitList(@Nullable ArrayList<ClinicalOption> newItems) {
        sourceItems.clear();
        filteredItems.clear();

        if (newItems != null) {
            sourceItems.addAll(newItems);
            filteredItems.addAll(newItems);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {return filteredItems.size();}

    @Override
    public ClinicalOption getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OptionViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_prescription_clinical_option, parent, false);
            holder = new OptionViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (OptionViewHolder) convertView.getTag();
        }

        ClinicalOption option = getItem(position);

        holder.nameView.setText(option.getOptionName());

        if (option.hasDoctorInfo()) {
            holder.doctorInfoView.setText(option.getDoctorInfo());
            holder.doctorInfoView.setVisibility(View.VISIBLE);

        } else {
            holder.doctorInfoView.setText("");
            holder.doctorInfoView.setVisibility(View.GONE);
        }

        holder.dividerView.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);

        return convertView;
    }

    /**
     * User পুরো option name manually লিখলেও
     * master list-এর exact option খুঁজে পাওয়া যাবে।
     */
    @Nullable
    public ClinicalOption findExactMatch(@Nullable String enteredText) {
        if (enteredText == null) {
            return null;
        }

        String normalizedText = enteredText.trim();

        if (normalizedText.isEmpty()) {
            return null;
        }

        for (ClinicalOption option : sourceItems) {
            if (option.getOptionName().equalsIgnoreCase(normalizedText)) {
                return option;
            }
        }

        return null;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<ClinicalOption> results = new ArrayList<>();
                String query = constraint == null ? "" : constraint.toString().trim().toLowerCase(Locale.ROOT);

                if (query.isEmpty()) {
                    results.addAll(sourceItems);
                } else {
                    for (ClinicalOption option : sourceItems) {
                        if (matchesQuery(option.getOptionName(), query)) {
                            results.add(option);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();

                filterResults.values = results;
                filterResults.count = results.size();

                return filterResults;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems.clear();
                if (results.values != null) {
                    filteredItems.addAll((ArrayList<ClinicalOption>) results.values);
                }

                if (filteredItems.isEmpty()) {
                    notifyDataSetInvalidated();

                } else {
                    notifyDataSetChanged();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof ClinicalOption) {
                    return ((ClinicalOption) resultValue).getOptionName();
                }
                return super.convertResultToString(resultValue);
            }
        };
    }

    /**
     * Query-এর সব word option name-এর মধ্যে থাকলে
     * item match হিসেবে দেখাবে।
     */
    private boolean matchesQuery(@NonNull String optionName, @NonNull String query) {
        String normalizedName = optionName.toLowerCase(Locale.ROOT);

        String[] searchWords = query.split("\\s+");
        for (String word : searchWords) {
            if (!normalizedName.contains(word)) {
                return false;
            }
        }

        return true;
    }

    private static class OptionViewHolder {
        private final TextView nameView;
        private final TextView doctorInfoView;
        private final View dividerView;

        OptionViewHolder(@NonNull View itemView) {
            nameView = itemView.findViewById(R.id.tv_clinical_option_name);
            doctorInfoView = itemView.findViewById(R.id.tv_clinical_option_doctor_info);
            dividerView = itemView.findViewById(R.id.view_clinical_option_divider);
        }
    }
}