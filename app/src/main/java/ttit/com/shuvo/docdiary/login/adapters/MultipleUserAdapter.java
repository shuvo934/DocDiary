package ttit.com.shuvo.docdiary.login.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.login.arraylists.MultipleUserList;

public class MultipleUserAdapter extends RecyclerView.Adapter<MultipleUserAdapter.MUHolder>{

    private final ArrayList<MultipleUserList> mCategory;
    private final Context myContext;
    private final ClickedItem myClickedItem;

    public MultipleUserAdapter(ArrayList<MultipleUserList> mCategory, Context myContext, ClickedItem myClickedItem) {
        this.mCategory = mCategory;
        this.myContext = myContext;
        this.myClickedItem = myClickedItem;
    }

    @NonNull
    @Override
    public MUHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.user_list_details_view, parent, false);
        return new MUHolder(view, myClickedItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MUHolder holder, int position) {
        MultipleUserList centerList = mCategory.get(position);
        holder.docName.setText(centerList.getDoc_name());
        holder.docCode.setText(centerList.getDoc_code());
        holder.depName.setText(centerList.getDepts_name());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class MUHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView docName;
        TextView docCode;
        TextView depName;
        ClickedItem mClickedItem;

        public MUHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            docName = itemView.findViewById(R.id.selectable_user_name);
            docCode = itemView.findViewById(R.id.selectable_user_code);
            depName = itemView.findViewById(R.id.selectable_user_depts_name);
            this.mClickedItem = ci;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickedItem.onCategoryClicked(getAdapterPosition());
        }
    }

    public interface ClickedItem {
        void onCategoryClicked(int CategoryPosition);
    }
}
