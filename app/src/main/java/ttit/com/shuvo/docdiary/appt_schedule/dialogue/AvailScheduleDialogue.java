package ttit.com.shuvo.docdiary.appt_schedule.dialogue;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.interfaces.AvailCallBack;

public class AvailScheduleDialogue extends AppCompatDialogFragment {
    LinearLayout fullLayout;
    CircularProgressIndicator circularProgressIndicator;
    MaterialButton reload;
    private Boolean conn = false;
    private Boolean connected = false;
    private Boolean loading = false;

    TextInputEditText availNotes;
    String notes = "";
    MaterialButton cancel;
    MaterialButton avail;

    AlertDialog alertDialog;
    String parsing_message = "";
    AppCompatActivity activity;
    Context mContext;
    String ad_id;

    public AvailScheduleDialogue(Context context, String ad_id) {
        this.mContext = context;
        this.ad_id = ad_id;
    }

    private AvailCallBack availCallBack;
    Logger logger = Logger.getLogger("AvailScheduleDialogue");

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        if (getActivity() instanceof AvailCallBack)
            availCallBack = (AvailCallBack) getActivity();

        View view = inflater.inflate(R.layout.avail_schedule_dialog_layout,null);

        activity = (AppCompatActivity) view.getContext();

        fullLayout = view.findViewById(R.id.avail_sch_full_layout);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_final_avail_sch);
        circularProgressIndicator.setVisibility(View.GONE);
        reload = view.findViewById(R.id.reload_final_avail_sch_button);
        reload.setVisibility(View.GONE);

        availNotes = view.findViewById(R.id.avail_sch_notes_dialogue);

        cancel = view.findViewById(R.id.cancel_avail_button);
        avail = view.findViewById(R.id.avail_final_button);

        builder.setView(view);

        alertDialog = builder.create();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        setCancelable(false);

        availNotes.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                    event.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                if (event == null || !event.isShiftPressed()) {
                    // the user is done typing.
                    Log.i("Let see", "Come here");
                    availNotes.clearFocus();
                    InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    closeKeyBoard();

                    return false; // consume.
                }
            }
            return false;
        });

        avail.setOnClickListener(v -> {
            notes = Objects.requireNonNull(availNotes.getText()).toString();
            availSchedule();
        });

        cancel.setOnClickListener(v -> {
            if (loading) {
                Toast.makeText(mContext,"Please wait while loading",Toast.LENGTH_SHORT).show();
            }
            else {
                alertDialog.dismiss();
            }
        });

        reload.setOnClickListener(v -> availSchedule());

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

    public void restart(String msg) {
        try {
            ProcessPhoenix.triggerRebirth(mContext);
        }
        catch (Exception e) {
            Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }

    public void availSchedule() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        reload.setVisibility(View.GONE);
        conn = false;
        connected = false;
        loading = true;

        String avSchUrl = pre_url_api+"appointment_schedule/availSchedule";

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, avSchUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String string_out = jsonObject.getString("string_out");
                parsing_message = string_out;

                connected = string_out.equals("Successfully Created");
                System.out.println(string_out);
                updateLayout();

            }
            catch (JSONException e) {
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                connected = false;
                updateLayout();
            }
        }, error -> {
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            conn = false;
            connected = false;
            updateLayout();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("P_AD_ID",ad_id);
                headers.put("P_NOTE",notes);
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(stringRequest);
    }

    private void updateLayout() {
        if(conn) {
            if (connected) {
                fullLayout.setVisibility(View.VISIBLE);
                circularProgressIndicator.setVisibility(View.GONE);
                reload.setVisibility(View.GONE);
                conn = false;
                connected = false;

                Toast.makeText(mContext, "Successfully Availed", Toast.LENGTH_SHORT).show();

                if(availCallBack != null)
                    availCallBack.onAvailed();

                alertDialog.dismiss();

            }
            else {
                alertMessage();
            }
        }
        else {
            alertMessage();
        }
    }

    public void alertMessage() {
        fullLayout.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.GONE);
        reload.setVisibility(View.VISIBLE);
        if (parsing_message != null) {
            if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                parsing_message = "Server problem or Internet not connected";
            }
        }
        else {
            parsing_message = "Server problem or Internet not connected";
        }
        Toast.makeText(mContext, parsing_message, Toast.LENGTH_SHORT).show();
        loading = false;
    }
}
