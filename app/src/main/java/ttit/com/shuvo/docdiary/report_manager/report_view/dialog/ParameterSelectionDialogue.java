package ttit.com.shuvo.docdiary.report_manager.report_view.dialog;

import static ttit.com.shuvo.docdiary.report_manager.report_view.ReportView.*;

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
import ttit.com.shuvo.docdiary.report_manager.report_view.adapters.ParameterSelectionAdapter;
import ttit.com.shuvo.docdiary.report_manager.report_view.arraylists.ParameterList;
import ttit.com.shuvo.docdiary.report_manager.report_view.interfaces.DepartmentSelectListener;
import ttit.com.shuvo.docdiary.report_manager.report_view.interfaces.DoctorSelectListener;
import ttit.com.shuvo.docdiary.report_manager.report_view.interfaces.ServiceSelectListener;
import ttit.com.shuvo.docdiary.report_manager.report_view.interfaces.UnitSelectListener;

public class ParameterSelectionDialogue extends AppCompatDialogFragment implements ParameterSelectionAdapter.ClickedItem {
    TextView appBarName;
    TextInputLayout searchLay;
    TextInputEditText search;
    TextView parameterNameText;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ParameterSelectionAdapter parameterSelectionAdapter;
    TextView noParamFound;

    AlertDialog alertDialog;

    String searchingName = "";

    ArrayList<ParameterList> parameterLists;
    ArrayList<ParameterList> filteredLists;
    Context mContext;
    String pmType;
    AppCompatActivity activity;
    boolean isfiltered = false;

    public ParameterSelectionDialogue(ArrayList<ParameterList> parameterLists, Context context, String pmType) {
        this.parameterLists = parameterLists;
        this.mContext = context;
        this.pmType = pmType;
    }

    private DepartmentSelectListener departmentSelectListener;
    private UnitSelectListener unitSelectListener;
    private ServiceSelectListener serviceSelectListener;
    private DoctorSelectListener doctorSelectListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.report_parameter_selection_layout,null);

        if (getActivity() instanceof DepartmentSelectListener)
            departmentSelectListener = (DepartmentSelectListener) getActivity();

        if (getActivity() instanceof UnitSelectListener)
            unitSelectListener = (UnitSelectListener) getActivity();

        if (getActivity() instanceof ServiceSelectListener)
            serviceSelectListener = (ServiceSelectListener) getActivity();

        if (getActivity() instanceof DoctorSelectListener)
            doctorSelectListener = (DoctorSelectListener) getActivity();

        activity = (AppCompatActivity) view.getContext();
        appBarName = view.findViewById(R.id.dialog_app_bar_report_parameter_text);

        searchLay = view.findViewById(R.id.search_report_parameter_layout);
        search = view.findViewById(R.id.search_report_parameter);
        parameterNameText = view.findViewById(R.id.parameter_name_text);

        recyclerView = view.findViewById(R.id.report_parameter_list_view);
        noParamFound = view.findViewById(R.id.no_report_parameter_found_message);
        noParamFound.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        filteredLists = new ArrayList<>();

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        switch (pmType) {
            case "DEPT": {
                String at = "Select Department";
                appBarName.setText(at);
                String st = "Search Department";
                searchLay.setHint(st);
                String pt = "DEPARTMENT";
                parameterNameText.setText(pt);
                break;
            }
            case "UNT": {
                String at = "Select Unit";
                appBarName.setText(at);
                String st = "Search Unit";
                searchLay.setHint(st);
                String pt = "UNIT";
                parameterNameText.setText(pt);
                break;
            }
            case "SRV": {
                String at = "Select Service";
                appBarName.setText(at);
                String st = "Search Service";
                searchLay.setHint(st);
                String pt = "SERVICE";
                parameterNameText.setText(pt);
                break;
            }
            case "DOC": {
                String at = "Select Doctor";
                appBarName.setText(at);
                String st = "Search Doctor";
                searchLay.setHint(st);
                String pt = "DOCTOR NAME";
                parameterNameText.setText(pt);
                break;
            }
        }

        parameterSelectionAdapter = new ParameterSelectionAdapter(parameterLists,mContext,this);
        recyclerView.setAdapter(parameterSelectionAdapter);

        if (parameterLists.isEmpty()) {
            noParamFound.setVisibility(View.VISIBLE);
        }
        else {
            noParamFound.setVisibility(View.GONE);
        }

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

                if (searchingName.isEmpty()) {
                    switch (pmType) {
                        case "DEPT":
                            searchLay.setHint("Search Department");
                            break;
                        case "UNT":
                            searchLay.setHint("Search Unit");
                            break;
                        case "SRV":
                            searchLay.setHint("Search Service");
                            break;
                        case "DOC":
                            searchLay.setHint("Search Doctor");
                            break;
                    }
                }
                else {
                    switch (pmType) {
                        case "DEPT":
                            searchLay.setHint("Department");
                            break;
                        case "UNT":
                            searchLay.setHint("Unit");
                            break;
                        case "SRV":
                            searchLay.setHint("Service");
                            break;
                        case "DOC":
                            searchLay.setHint("Doctor");
                            break;
                    }
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
        for (ParameterList item : parameterLists) {
            if (item.getName().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH))) {
                filteredLists.add((item));
                isfiltered = true;
            }
        }
        if (filteredLists.isEmpty()) {
            noParamFound.setVisibility(View.VISIBLE);
        }
        else {
            noParamFound.setVisibility(View.GONE);
        }
        if (parameterSelectionAdapter != null) {
            try {
                parameterSelectionAdapter.filterList(filteredLists);
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
        String name;
        String id;
        if (isfiltered) {
            name = filteredLists.get(Position).getName();
            id = filteredLists.get(Position).getId();
        }
        else {
            name = parameterLists.get(Position).getName();
            id = parameterLists.get(Position).getId();
        }
        switch (pmType) {
            case "DEPT":
                if (name.equals("...")) {
                    name = "";
                }
                selected_dept_name = name;
                selected_dept_id = id;

                if (departmentSelectListener != null)
                    departmentSelectListener.onDeptSelect();

                alertDialog.dismiss();
                break;
            case "UNT":
                if (name.equals("...")) {
                    name = "";
                }
                selected_unit_name = name;
                selected_unit_id = id;

                if (unitSelectListener != null)
                    unitSelectListener.onUnitSelect();

                alertDialog.dismiss();
                break;
            case "SRV":
                if (name.equals("...")) {
                    name = "";
                }
                selected_service_name = name;
                selected_service_id = id;

                if (serviceSelectListener != null)
                    serviceSelectListener.onServiceSelect();

                alertDialog.dismiss();
                break;
            case "DOC":
                if (name.equals("...")) {
                    name = "";
                }
                selected_doctor_name = name;
                selected_doctor_id = id;

                if (doctorSelectListener != null)
                    doctorSelectListener.onDoctorSelect();

                alertDialog.dismiss();
                break;
        }
    }
}
