package ttit.com.shuvo.docdiary.login.dialogue;

import static ttit.com.shuvo.docdiary.login.DocLogin.centerName;
import static ttit.com.shuvo.docdiary.login.DocLogin.center_admin_user_id;
import static ttit.com.shuvo.docdiary.login.DocLogin.center_api;
import static ttit.com.shuvo.docdiary.login.DocLogin.center_doc_code;
import static ttit.com.shuvo.docdiary.login.DocLogin.user_or_admin_flag;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.login.AdminCallBackListener;
import ttit.com.shuvo.docdiary.login.CallBackListener;
import ttit.com.shuvo.docdiary.login.CloseCallBack;
import ttit.com.shuvo.docdiary.login.DocLogin;
import ttit.com.shuvo.docdiary.login.adapters.CenterAdapter;
import ttit.com.shuvo.docdiary.login.arraylists.CenterList;
import ttit.com.shuvo.docdiary.login.arraylists.MultipleUserList;

public class SelectCenterDialogue extends AppCompatDialogFragment implements CenterAdapter.ClickedItem {
    RecyclerView centerView;
    CenterAdapter centerAdapter;
    RecyclerView.LayoutManager layoutManager;

    ImageView close;
    AlertDialog dialog;

    String centerAPI = "";
    String d_code = "";

    ArrayList<CenterList> centerLists;
    Context mContext;

    public SelectCenterDialogue(ArrayList<CenterList> centerLists, Context mContext) {
        this.centerLists = centerLists;
        this.mContext = mContext;
    }
    private CallBackListener callBackListener;
    private CloseCallBack closeCallBack;
    private AdminCallBackListener adminCallBackListener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (getActivity() instanceof CallBackListener)
            callBackListener = (CallBackListener) getActivity();

        if (getActivity() instanceof CloseCallBack)
            closeCallBack = (CloseCallBack) getActivity();

        if (getActivity() instanceof AdminCallBackListener)
            adminCallBackListener = (AdminCallBackListener) getActivity();

        View view = inflater.inflate(R.layout.center_selectable_view, null);

        centerView = view.findViewById(R.id.center_list_view);
        close = view.findViewById(R.id.close_center_choice);

        builder.setView(view);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        centerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        centerView.setLayoutManager(layoutManager);

        centerAdapter = new CenterAdapter(centerLists,mContext,this);
        centerView.setAdapter(centerAdapter);
        close.setOnClickListener(v -> {
            if(closeCallBack != null)
                closeCallBack.closeCallDismiss();
            dialog.dismiss();
        });

        return dialog;
    }

    @Override
    public void onCategoryClicked(int CategoryPosition) {

        ArrayList<MultipleUserList> multipleUserLists = centerLists.get(CategoryPosition).getMultipleUserLists();
        centerAPI = centerLists.get(CategoryPosition).getCenter_api();
        center_api = centerAPI;
        if (multipleUserLists.size() == 0) {
            String user_admin_flag = centerLists.get(CategoryPosition).getUser_admin_flag();
            if (user_admin_flag.equals("1")) {
                d_code = centerLists.get(CategoryPosition).getDoc_code();
                center_doc_code = d_code;
                user_or_admin_flag = user_admin_flag;
                center_admin_user_id = centerLists.get(CategoryPosition).getAdmin_user_id();

                if(callBackListener != null)
                    callBackListener.onDismiss();

                dialog.dismiss();
            }
            else {
                d_code = centerLists.get(CategoryPosition).getDoc_code();
                center_doc_code = d_code;
                user_or_admin_flag = user_admin_flag;
                center_admin_user_id = centerLists.get(CategoryPosition).getAdmin_user_id();

                if(adminCallBackListener != null)
                    adminCallBackListener.onAdminCenterSelection();

                dialog.dismiss();
            }

        }
        else {
            System.out.println("USERs Found : "+multipleUserLists.size());
            String cn = centerLists.get(CategoryPosition).getCenter_name();

            SelectUserIdDialogue selectUserIdDialogue = new SelectUserIdDialogue(multipleUserLists, mContext,cn);
            selectUserIdDialogue.show(getActivity().getSupportFragmentManager(),"USER_CENTER");
            dialog.dismiss();
        }
    }
}
