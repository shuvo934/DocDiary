package ttit.com.shuvo.docdiary.login.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.login.arraylists.MultipleUserList;

public class MultipleUserAdapter extends RecyclerView.Adapter<MultipleUserAdapter.MUHolder>{

    private final ArrayList<MultipleUserList> mCategory;
    private final Context myContext;
    private final String selected_doc_code;
    private final String selected_admin_id;
    private final String admin_or_user;
    private final ClickedItem myClickedItem;

    public MultipleUserAdapter(ArrayList<MultipleUserList> mCategory, Context myContext, String selectedDocCode, String selectedAdminId, String adminOrUser, ClickedItem myClickedItem) {
        this.mCategory = mCategory;
        this.myContext = myContext;
        selected_doc_code = selectedDocCode;
        selected_admin_id = selectedAdminId;
        admin_or_user = adminOrUser;
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
        String flag = centerList.getUser_admin_flag();
        if (flag.equals("1")) {
            holder.docCode.setVisibility(View.VISIBLE);
            holder.docName.setText(centerList.getDoc_name());
            holder.docCode.setText(centerList.getDoc_code());
            holder.depName.setText(centerList.getDepts_name());
        }
        else {
            holder.docCode.setVisibility(View.GONE);
            String name = centerList.getAdmin_user_fname() + " " + centerList.getAdmin_user_lname();
            holder.docName.setText(name);
            String user_name = "("+centerList.getAdmin_user_name()+")";
            holder.depName.setText(user_name);
        }

        if (admin_or_user != null && !admin_or_user.isEmpty()) {
            if (admin_or_user.equals("1")) {
                if (selected_doc_code != null && !selected_doc_code.isEmpty()) {
                    if (selected_doc_code.equals(centerList.getDoc_code())) {
                        holder.checkUser.setVisibility(View.VISIBLE);
                        holder.userLay.setBackground(ContextCompat.getDrawable(myContext, R.drawable.border_design_selected));
                    } else {
                        holder.checkUser.setVisibility(View.GONE);
                        holder.userLay.setBackground(ContextCompat.getDrawable(myContext, R.drawable.border_design));
                    }
                }
                else {
                    holder.checkUser.setVisibility(View.GONE);
                    holder.userLay.setBackground(ContextCompat.getDrawable(myContext, R.drawable.border_design));
                }
            }
            else {
                if (selected_admin_id != null && !selected_admin_id.isEmpty()) {
                    if (selected_admin_id.equals(centerList.getAdmin_user_id())) {
                        holder.checkUser.setVisibility(View.VISIBLE);
                        holder.userLay.setBackground(ContextCompat.getDrawable(myContext, R.drawable.border_design_selected));
                    } else {
                        holder.checkUser.setVisibility(View.GONE);
                        holder.userLay.setBackground(ContextCompat.getDrawable(myContext, R.drawable.border_design));
                    }
                }
                else {
                    holder.checkUser.setVisibility(View.GONE);
                    holder.userLay.setBackground(ContextCompat.getDrawable(myContext, R.drawable.border_design));
                }
            }
        }
        else {
            holder.checkUser.setVisibility(View.GONE);
            holder.userLay.setBackground(ContextCompat.getDrawable(myContext, R.drawable.border_design));
        }

    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class MUHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView docName;
        TextView docCode;
        TextView depName;
        ImageView checkUser;
        LinearLayout userLay;
        ClickedItem mClickedItem;

        public MUHolder(@NonNull View itemView, ClickedItem ci) {
            super(itemView);
            docName = itemView.findViewById(R.id.selectable_user_name);
            docCode = itemView.findViewById(R.id.selectable_user_code);
            depName = itemView.findViewById(R.id.selectable_user_depts_name);
            userLay = itemView.findViewById(R.id.user_list_layout);
            checkUser = itemView.findViewById(R.id.selected_user_check_image);
            checkUser.setVisibility(View.GONE);
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
