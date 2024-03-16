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
import ttit.com.shuvo.docdiary.login.CloseCallBack;
import ttit.com.shuvo.docdiary.login.IDCallbackListener;
import ttit.com.shuvo.docdiary.login.adapters.MultipleUserAdapter;
import ttit.com.shuvo.docdiary.login.arraylists.CenterList;
import ttit.com.shuvo.docdiary.login.arraylists.MultipleUserList;

public class UserSelectDialogue extends AppCompatDialogFragment implements MultipleUserAdapter.ClickedItem {
    RecyclerView userIdView;
    MultipleUserAdapter multipleUserAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView centerName;
    ImageView close;
    TextView notifiedMsg;

    AlertDialog dialog;

    String d_code = "";

    ArrayList<MultipleUserList> multipleUserLists;
    ArrayList<CenterList> centerLists;
    Context mContext;
    String c_name;
    String api;

    public UserSelectDialogue(ArrayList<CenterList> centerLists, ArrayList<MultipleUserList> multipleUserLists, Context mContext,String c_name, String api) {
        this.centerLists = centerLists;
        this.multipleUserLists = multipleUserLists;
        this.mContext = mContext;
        this.c_name = c_name;
        this.api = api;
    }

    private IDCallbackListener idCallbackListener;

    SharedPreferences sharedpreferences;
    public static final String LOGIN_ACTIVITY_FILE = "LOGIN_ACTIVITY_FILE_DOCDIARY";
    public static final String LOGIN_TF = "TRUE_FALSE";
    public static final String DOC_USER_CODE = "DOC_USER_CODE";
    public static final String DOC_USER_PASSWORD = "DOC_USER_PASSWORD";
    public static final String DOC_DATA_API = "DOC_DATA_API";
    public static final String DOC_ALL_ID = "DOC_ALL_ID";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        if (getActivity() instanceof IDCallbackListener)
            idCallbackListener = (IDCallbackListener) getActivity();

        View view = inflater.inflate(R.layout.user_id_selectable_view, null);

        userIdView = view.findViewById(R.id.multiple_user_list_view);
        centerName = view.findViewById(R.id.center_name_of_selectable_id);
        close = view.findViewById(R.id.close_user_choice);
        notifiedMsg = view.findViewById(R.id.multi_user_notified_text);
        notifiedMsg.setVisibility(View.GONE);

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
            dialog.dismiss();
        });
        return dialog;
    }

    @Override
    public void onCategoryClicked(int CategoryPosition) {
        d_code = multipleUserLists.get(CategoryPosition).getDoc_code();
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

        editor1.putString(DOC_USER_CODE, d_code);
        editor1.putString(DOC_USER_PASSWORD, pass);
        editor1.putString(DOC_DATA_API, api);
        editor1.putString(DOC_ALL_ID, json);
        editor1.putBoolean(LOGIN_TF, true);
        editor1.apply();
        editor1.commit();

        if(idCallbackListener != null)
            idCallbackListener.onIdDismiss();

        dialog.dismiss();
    }
}
