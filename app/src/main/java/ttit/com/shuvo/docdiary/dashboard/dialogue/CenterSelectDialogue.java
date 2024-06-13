package ttit.com.shuvo.docdiary.dashboard.dialogue;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.login.AdminCallBackListener;
import ttit.com.shuvo.docdiary.login.CallBackListener;
import ttit.com.shuvo.docdiary.login.CloseCallBack;
import ttit.com.shuvo.docdiary.login.adapters.CenterAdapter;
import ttit.com.shuvo.docdiary.login.arraylists.CenterList;
import ttit.com.shuvo.docdiary.login.arraylists.MultipleUserList;

public class CenterSelectDialogue extends AppCompatDialogFragment implements CenterAdapter.ClickedItem {
    RecyclerView centerView;
    CenterAdapter centerAdapter;
    RecyclerView.LayoutManager layoutManager;

    TextView notifiedMsg;
    ImageView close;
    AlertDialog dialog;

    String centerAPI = "";
    String d_code = "";
    String admin_id= "";
    String admin_user_flag = "";

    ArrayList<CenterList> centerLists;
    Context mContext;

    public CenterSelectDialogue(ArrayList<CenterList> centerLists, Context mContext) {
        this.centerLists = centerLists;
        this.mContext = mContext;
    }

    private CallBackListener callBackListener;
    private AdminCallBackListener adminCallBackListener;

    SharedPreferences sharedpreferences;
    public static final String LOGIN_ACTIVITY_FILE = "LOGIN_ACTIVITY_FILE_DOCDIARY";
    public static final String LOGIN_TF = "TRUE_FALSE";
    public static final String DOC_USER_CODE = "DOC_USER_CODE";
    public static final String DOC_USER_PASSWORD = "DOC_USER_PASSWORD";
    public static final String DOC_DATA_API = "DOC_DATA_API";
    public static final String DOC_ALL_ID = "DOC_ALL_ID";
    public static final String ADMIN_OR_USER_FLAG = "ADMIN_OR_USER_FLAG";
    public static final String ADMIN_USR_ID = "ADMIN_USR_ID";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof CallBackListener)
            callBackListener = (CallBackListener) getActivity();

        if (getActivity() instanceof AdminCallBackListener)
            adminCallBackListener = (AdminCallBackListener) getActivity();

        View view = inflater.inflate(R.layout.center_selectable_view, null);

        centerView = view.findViewById(R.id.center_list_view);
        close = view.findViewById(R.id.close_center_choice);
        notifiedMsg = view.findViewById(R.id.multi_center_notified_text);
        notifiedMsg.setVisibility(View.GONE);

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
            dialog.dismiss();
        });

        return dialog;
    }

    @Override
    public void onCategoryClicked(int CategoryPosition) {
        ArrayList<MultipleUserList> multipleUserLists = centerLists.get(CategoryPosition).getMultipleUserLists();
        centerAPI = centerLists.get(CategoryPosition).getCenter_api();

        if (multipleUserLists.size() == 0) {

            d_code = centerLists.get(CategoryPosition).getDoc_code();
            admin_id = centerLists.get(CategoryPosition).getAdmin_user_id();
            admin_user_flag = centerLists.get(CategoryPosition).getUser_admin_flag();

            Gson gson = new Gson();
            String json;
            json = gson.toJson(centerLists);
            sharedpreferences = requireActivity().getSharedPreferences(LOGIN_ACTIVITY_FILE, MODE_PRIVATE);
            String pass = sharedpreferences.getString(DOC_USER_PASSWORD,"");

            SharedPreferences.Editor editor1 = sharedpreferences.edit();
            editor1.remove(DOC_USER_CODE);
            editor1.remove(DOC_USER_PASSWORD);
            editor1.remove(DOC_DATA_API);
            editor1.remove(DOC_ALL_ID);
            editor1.remove(LOGIN_TF);
            editor1.remove(ADMIN_OR_USER_FLAG);
            editor1.remove(ADMIN_USR_ID);

            editor1.putString(DOC_USER_CODE, d_code);
            editor1.putString(DOC_USER_PASSWORD, pass);
            editor1.putString(DOC_DATA_API, centerAPI);
            editor1.putString(DOC_ALL_ID, json);
            editor1.putBoolean(LOGIN_TF, true);
            editor1.putString(ADMIN_USR_ID,admin_id);
            editor1.putString(ADMIN_OR_USER_FLAG,admin_user_flag);
            editor1.apply();
            editor1.commit();

            if (admin_user_flag.equals("1")) {
                if(callBackListener != null)
                    callBackListener.onDismiss();

                dialog.dismiss();
            }
            else {
                if(adminCallBackListener != null)
                    adminCallBackListener.onAdminCenterSelection();

                dialog.dismiss();
            }

        }
        else {
            System.out.println("USERs Found : "+multipleUserLists.size());
            String cn = centerLists.get(CategoryPosition).getCenter_name();
            String api = centerLists.get(CategoryPosition).getCenter_api();

            UserSelectDialogue userSelectDialogue = new UserSelectDialogue(centerLists,multipleUserLists, mContext,cn,api);
            userSelectDialogue.show(requireActivity().getSupportFragmentManager(),"USER_CENTER");
            dialog.dismiss();
        }
    }
}
