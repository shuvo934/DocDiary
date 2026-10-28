package ttit.com.shuvo.docdiary.dynamic_prescription.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ttit.com.shuvo.docdiary.R;

public class PrescriptionTextItemAdapter extends RecyclerView.Adapter<PrescriptionTextItemAdapter.TextItemViewHolder> {

    public interface OnItemCountChangedListener {
        void onItemCountChanged(int itemCount);
    }

    private final ArrayList<String> items = new ArrayList<>();
    private final String inputHint;
    private final OnItemCountChangedListener countChangedListener;

    private int pendingFocusPosition = RecyclerView.NO_POSITION;

    public PrescriptionTextItemAdapter(String inputHint, OnItemCountChangedListener countChangedListener) {
        this.inputHint = inputHint;
        this.countChangedListener = countChangedListener;
    }

    @NonNull
    @Override
    public TextItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription_text, parent, false);
        return new TextItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextItemViewHolder holder, int position) {
        boolean shouldFocus = position == pendingFocusPosition;
        holder.bind(items.get(position), shouldFocus);
        if (shouldFocus) {
            pendingFocusPosition = RecyclerView.NO_POSITION;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /*
     * Doctor manually নতুন item add করলে এটি ব্যবহার হবে।
     */
    public int addEmptyItem() {
        int newPosition = items.size();

        items.add("");
        pendingFocusPosition = newPosition;

        notifyItemInserted(newPosition);
        notifyCountChanged();

        return newPosition;
    }

    /*
     * Disease Template load করার সময় এটি ব্যবহার করা যাবে।
     */
    @SuppressLint("NotifyDataSetChanged")
    public void replaceItems(List<String> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
        notifyCountChanged();
    }

    /*
     * বর্তমানে doctor যা লিখেছেন তার copy পাওয়া যাবে।
     */
    public ArrayList<String> getItems() {
        return new ArrayList<>(items);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
        notifyCountChanged();
    }

    private void deleteItem(int position) {
        if (position == RecyclerView.NO_POSITION ||
                position >= items.size()) {
            return;
        }

        items.remove(position);
        notifyItemRemoved(position);
        notifyCountChanged();
    }

    private void notifyCountChanged() {
        if (countChangedListener != null) {
            countChangedListener.onItemCountChanged(items.size());
        }
    }

    public class TextItemViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatEditText editText;
        private final AppCompatImageButton deleteButton;

        private TextWatcher textWatcher;

        TextItemViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.et_prescription_item_text);
            deleteButton = itemView.findViewById(R.id.btn_delete_prescription_item);
        }

        void bind(String value, boolean shouldFocus) {
            if (textWatcher != null) {
                editText.removeTextChangedListener(textWatcher);
            }
            editText.setHint(inputHint);
            editText.setText(value);

            if (value != null) {
                editText.setSelection(Objects.requireNonNull(editText.getText()).length());
            }

            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence text, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && position < items.size()) {
                        items.set(position, editable.toString());
                    }
                }
            };

            editText.addTextChangedListener(textWatcher);

            deleteButton.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                deleteItem(position);
            });

            if (shouldFocus) {
                editText.post(() -> {
                    editText.requestFocus();
                    InputMethodManager keyboard =
                            (InputMethodManager) editText
                                    .getContext()
                                    .getSystemService(
                                            Context.INPUT_METHOD_SERVICE
                                    );

                    if (keyboard != null) {
                        keyboard.showSoftInput(
                                editText,
                                InputMethodManager.SHOW_IMPLICIT
                        );
                    }
                });
            }
        }
    }
}
