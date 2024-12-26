package ttit.com.shuvo.docdiary.appointment_admin.dialogue;

import static ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify.*;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.ArrayList;
import java.util.Locale;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appointment_admin.interfaces.PatAppDoctorSelectionListener;
import ttit.com.shuvo.docdiary.appointment_admin.adapters.SearchDoctorForAppAdapter;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.DoctorForAppList;

public class SearchDoctorAppDialog extends AppCompatDialogFragment implements SearchDoctorForAppAdapter.ClickedItem {
    TextInputLayout searchLay;
    TextInputEditText search;

    RecyclerView recyclerView;
    SearchDoctorForAppAdapter searchDoctorForAppAdapter;
    RecyclerView.LayoutManager layoutManager;
    AlertDialog alertDialog;
    TextView noDoctorMsg;

    String searchingName = "";
    ArrayList<DoctorForAppList> doctorForAppLists;
    ArrayList<DoctorForAppList> filteredLists;

    AppCompatActivity activity;
    Context mContext;
    boolean isFiltered = false;

    private PatAppDoctorSelectionListener patAppDoctorSelectionListener;

    public SearchDoctorAppDialog(Context context, ArrayList<DoctorForAppList> doctorForAppLists) {
        this.mContext = context;
        this.doctorForAppLists = doctorForAppLists;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof PatAppDoctorSelectionListener)
            patAppDoctorSelectionListener = (PatAppDoctorSelectionListener) getActivity();

        View view = inflater.inflate(R.layout.doctor_search_for_app_dial_layout,null);

        activity = (AppCompatActivity) view.getContext();

        filteredLists = new ArrayList<>();

        searchLay = view.findViewById(R.id.search_doctor_layout_for_patient_appointment);
        search = view.findViewById(R.id.search_doctor_for_patient_appointment);
        noDoctorMsg = view.findViewById(R.id.no_doctor_found_message_for_pat_appoint);
        noDoctorMsg.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.doctor_list_view_for_patient_appointment);

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                searchingName = s.toString();
                filter(searchingName);
                if(searchingName.isEmpty()) {
                    searchLay.setHint("Search Doctor By Name");
                }
                else {
                    searchLay.setHint("Doctor Name");
                }
            }
        });

        search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    search.clearFocus();
                    InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        searchDoctorForAppAdapter = new SearchDoctorForAppAdapter(doctorForAppLists, mContext,this);
        recyclerView.setAdapter(searchDoctorForAppAdapter);
        System.out.println("SIZE: "+doctorForAppLists.size());

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> dialog.dismiss());

        return alertDialog;
    }

    private void closeKeyBoard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void filter(String text) {
        filteredLists = new ArrayList<>();
        for (DoctorForAppList item : doctorForAppLists) {
            if (item.getDoc_name().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH))) {
                filteredLists.add((item));
                isFiltered = true;
            }
        }
        if (filteredLists.isEmpty()) {
            noDoctorMsg.setVisibility(View.VISIBLE);
        }
        else {
            noDoctorMsg.setVisibility(View.GONE);
        }
        if (searchDoctorForAppAdapter != null) {
            try {
                searchDoctorForAppAdapter.filterList(filteredLists);
            }
            catch (Exception e) {
                restart("App is paused for a long time. Please Start the app again.");
            }
        }
        else {
            restart("App is paused for a long time. Please Start the app again.");
        }
    }

    public void restart(String msg) {
        try {
            ProcessPhoenix.triggerRebirth(mContext);
        }
        catch (Exception e) {
            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }

    @Override
    public void onItemClicked(int Position) {
        String doc_id;
        String doc_name;
        String depts_id;
        String depts_name;
        String deptd_id;
        String desig_id;

        if (isFiltered) {
            doc_id = filteredLists.get(Position).getDoc_id();
            doc_name = filteredLists.get(Position).getDoc_name();
            depts_id = filteredLists.get(Position).getDepts_id();
            depts_name = filteredLists.get(Position).getDepts_name();
            deptd_id = filteredLists.get(Position).getDeptd_id();
            desig_id = filteredLists.get(Position).getDesig_id();
        }
        else {
            doc_id = doctorForAppLists.get(Position).getDoc_id();
            doc_name = doctorForAppLists.get(Position).getDoc_name();
            depts_id = doctorForAppLists.get(Position).getDepts_id();
            depts_name = doctorForAppLists.get(Position).getDepts_name();
            deptd_id = doctorForAppLists.get(Position).getDeptd_id();
            desig_id = doctorForAppLists.get(Position).getDesig_id();
        }

        if (!doc_name.equals(selected_doc_name)) {
            selected_doc_id = doc_id;
            selected_doc_name = doc_name;
            selected_depts_id = depts_id;
            selected_depts_name = depts_name;
            selected_deptd_id = deptd_id;
            selected_desig_id = desig_id;

            if(patAppDoctorSelectionListener != null)
                patAppDoctorSelectionListener.onDoctorSelect();
        }
        alertDialog.dismiss();
    }
}
