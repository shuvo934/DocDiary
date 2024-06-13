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
import ttit.com.shuvo.docdiary.appointment_admin.AppointmentModify;
import ttit.com.shuvo.docdiary.appointment_admin.PatAppDoctorSelectionListener;
import ttit.com.shuvo.docdiary.appointment_admin.PatAppPaymentSelectionListener;
import ttit.com.shuvo.docdiary.appointment_admin.adapters.SearchDoctorForAppAdapter;
import ttit.com.shuvo.docdiary.appointment_admin.adapters.SearchPaymentForAppAdapter;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.DoctorForAppList;
import ttit.com.shuvo.docdiary.appointment_admin.arraylists.PaymentForAppList;

public class SearchPaymentAppDialog extends AppCompatDialogFragment implements SearchPaymentForAppAdapter.ClickedItem {

    TextInputLayout searchLay;
    TextInputEditText search;

    RecyclerView recyclerView;
    SearchPaymentForAppAdapter searchPaymentForAppAdapter;
    RecyclerView.LayoutManager layoutManager;
    AlertDialog alertDialog;
    TextView noPaymentMsg;

    String searchingName = "";
    ArrayList<PaymentForAppList> paymentForAppLists;
    ArrayList<PaymentForAppList> filteredLists;

    AppCompatActivity activity;
    Context mContext;
    boolean isFiltered = false;

    private PatAppPaymentSelectionListener patAppPaymentSelectionListener;

    public SearchPaymentAppDialog(Context context, ArrayList<PaymentForAppList> paymentForAppLists) {
        this.mContext = context;
        this.paymentForAppLists = paymentForAppLists;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof PatAppPaymentSelectionListener)
            patAppPaymentSelectionListener = (PatAppPaymentSelectionListener) getActivity();

        View view = inflater.inflate(R.layout.payment_search_for_pat_app_layout,null);

        activity = (AppCompatActivity) view.getContext();

        filteredLists = new ArrayList<>();

        searchLay = view.findViewById(R.id.search_payment_layout_for_patient_appointment);
        search = view.findViewById(R.id.search_payment_for_patient_appointment);
        noPaymentMsg = view.findViewById(R.id.no_payment_found_message_for_pat_appoint);
        noPaymentMsg.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.payment_list_view_for_patient_appointment);

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

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
                    searchLay.setHint("Search Payment Code");
                }
                else {
                    searchLay.setHint("Payment Code");
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
        searchPaymentForAppAdapter = new SearchPaymentForAppAdapter(paymentForAppLists, mContext,this);
        recyclerView.setAdapter(searchPaymentForAppAdapter);

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
        for (PaymentForAppList item : paymentForAppLists) {
            if (item.getPrm_code().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH))) {
                filteredLists.add((item));
                isFiltered = true;
            }
        }
        if (filteredLists.size() == 0) {
            noPaymentMsg.setVisibility(View.VISIBLE);
        }
        else {
            noPaymentMsg.setVisibility(View.GONE);
        }
        if (searchPaymentForAppAdapter != null) {
            try {
                searchPaymentForAppAdapter.filterList(filteredLists);
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
        String prm_id = "";
        String prm_code = "";
        if (isFiltered) {
            prm_id = filteredLists.get(Position).getPrm_id();
            prm_code = filteredLists.get(Position).getPrm_code();
        }
        else {
            prm_id = paymentForAppLists.get(Position).getPrm_id();
            prm_code = paymentForAppLists.get(Position).getPrm_code();
        }
        if (!prm_id.equals(selected_prm_id)) {
            selected_prm_id = prm_id;
            selected_prm_code = prm_code;

            if(patAppPaymentSelectionListener != null)
                patAppPaymentSelectionListener.onPaymentSelect();
        }

        alertDialog.dismiss();
    }
}
