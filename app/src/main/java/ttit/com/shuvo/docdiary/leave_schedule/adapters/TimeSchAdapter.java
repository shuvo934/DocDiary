package ttit.com.shuvo.docdiary.leave_schedule.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
public class TimeSchAdapter extends RecyclerView.Adapter<TimeSchAdapter.TSHolder> {

    private final ArrayList<String> mCategory;
    private final Context myContext;

    public TimeSchAdapter(ArrayList<String> mCategory, Context myContext) {
        this.mCategory = mCategory;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public TSHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.off_time_schedule, parent, false);
        return new TSHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TSHolder holder, int position) {
        holder.time.setText(mCategory.get(position));
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class TSHolder extends RecyclerView.ViewHolder {
        TextView time;
        public TSHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time_schedule_of_leave);
        }
    }
}
