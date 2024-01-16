package ttit.com.shuvo.docdiary.login.dialogue;

import static ttit.com.shuvo.docdiary.login.DocLogin.center_doc_code;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.login.CloseCallBack;
import ttit.com.shuvo.docdiary.login.IDCallbackListener;
import ttit.com.shuvo.docdiary.login.adapters.MultipleUserAdapter;
import ttit.com.shuvo.docdiary.login.arraylists.MultipleUserList;

public class SelectUserIdDialogue extends AppCompatDialogFragment implements MultipleUserAdapter.ClickedItem{

    RecyclerView userIdView;
    MultipleUserAdapter multipleUserAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView centerName;
    ImageView close;

    AlertDialog dialog;

    String d_code = "";

    ArrayList<MultipleUserList> multipleUserLists;
    Context mContext;
    String c_name;

    public SelectUserIdDialogue(ArrayList<MultipleUserList> multipleUserLists, Context mContext,String c_name) {
        this.multipleUserLists = multipleUserLists;
        this.mContext = mContext;
        this.c_name = c_name;
    }

    private IDCallbackListener idCallbackListener;
    private CloseCallBack closeCallBack;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (getActivity() instanceof IDCallbackListener)
            idCallbackListener = (IDCallbackListener) getActivity();

        if (getActivity() instanceof CloseCallBack)
            closeCallBack = (CloseCallBack) getActivity();

        View view = inflater.inflate(R.layout.user_id_selectable_view, null);

        userIdView = view.findViewById(R.id.multiple_user_list_view);
        centerName = view.findViewById(R.id.center_name_of_selectable_id);
        close = view.findViewById(R.id.close_user_choice);

        builder.setView(view);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        centerName.setText(c_name);
        userIdView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        userIdView.setLayoutManager(layoutManager);

        multipleUserAdapter = new MultipleUserAdapter(multipleUserLists,mContext,this);
        userIdView.setAdapter(multipleUserAdapter);

        close.setOnClickListener(v -> {
            if(closeCallBack != null)
                closeCallBack.closeCallDismiss();
            dialog.dismiss();
        });
        return dialog;
    }

    @Override
    public void onCategoryClicked(int CategoryPosition) {

        d_code = multipleUserLists.get(CategoryPosition).getDoc_code();
        center_doc_code = d_code;

        if(idCallbackListener != null)
            idCallbackListener.onIdDismiss();

        dialog.dismiss();
    }
}
