package ttit.com.shuvo.docdiary.login.dialogue;

import static ttit.com.shuvo.docdiary.login.DocLogin.centerName;
import static ttit.com.shuvo.docdiary.login.DocLogin.center_api;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.login.adapters.CenterAdapter;
import ttit.com.shuvo.docdiary.login.arraylists.CenterList;

public class SelectCenterDialogue extends AppCompatDialogFragment implements CenterAdapter.ClickedItem {
    RecyclerView centerView;
    CenterAdapter centerAdapter;
    RecyclerView.LayoutManager layoutManager;

    AlertDialog dialog;

    String centerAPI = "";

    ArrayList<CenterList> centerLists;
    Context mContext;

    public SelectCenterDialogue(ArrayList<CenterList> centerLists, Context mContext) {
        this.centerLists = centerLists;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.center_selectable_view, null);

        centerView = view.findViewById(R.id.center_list_view);

        builder.setView(view);

        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        centerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        centerView.setLayoutManager(layoutManager);

        centerAdapter = new CenterAdapter(centerLists,mContext,this);
        centerView.setAdapter(centerAdapter);

        return dialog;
    }

    @Override
    public void onCategoryClicked(int CategoryPosition) {
        String name = "";

        name = centerLists.get(CategoryPosition).getCenter_name();
        centerAPI = centerLists.get(CategoryPosition).getCenter_api();

        center_api = centerAPI;
        SpannableString content = new SpannableString(name);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        centerName.setText(content);

        dialog.dismiss();
    }
}
