package ttit.com.shuvo.docdiary.appointment_admin.add_schedule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;

public class TakenScheduleAdapter extends RecyclerView.Adapter<TakenScheduleAdapter.TSAHolder> {

    private final ArrayList<String> list;
    private final Context context;

    public TakenScheduleAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public TSAHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_list_layout_details_view, parent, false);
        return new TSAHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TSAHolder holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class TSAHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public TSAHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.simple_text_view_for_list);
        }
    }
}
