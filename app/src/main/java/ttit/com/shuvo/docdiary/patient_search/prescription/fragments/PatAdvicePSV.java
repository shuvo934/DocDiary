package ttit.com.shuvo.docdiary.patient_search.prescription.fragments;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatAdviceList;
import ttit.com.shuvo.docdiary.patient_search.prescription.PatPrescriptionV;
import ttit.com.shuvo.docdiary.patient_search.prescription.fragments.adapters.PatAdvicePSVAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatAdvicePSV#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatAdvicePSV extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String pmm_Id;
    private String mParam2;

    public PatAdvicePSV() {
        // Required empty public constructor
    }

    CardView fullFragment;
    CircularProgressIndicator circularProgressIndicator;
    ImageButton refresh;

    RecyclerView adviceView;
    PatAdvicePSVAdapter patAdvicePSVAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView noAdviceMsg;
    ArrayList<PatAdviceList> patAdviceLists;

    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";

    Context mContext;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
    }

    Logger logger = Logger.getLogger(PatAdvicePSV.class.getName());

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PatAdvicePSV.
     */
    public static PatAdvicePSV newInstance(String param1, String param2) {
        PatAdvicePSV fragment = new PatAdvicePSV();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pmm_Id = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pat_advice_p_s_v, container, false);
        fullFragment = view.findViewById(R.id.patient_advice_full_fragment_pat_psv);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_patient_advice_pat_psv);
        circularProgressIndicator.setVisibility(View.GONE);
        refresh = view.findViewById(R.id.refresh_button_for_patient_advice_pat_psv);
        refresh.setVisibility(View.GONE);

        adviceView = view.findViewById(R.id.patient_advice_recyclerview_pat_psv);
        noAdviceMsg = view.findViewById(R.id.no_information_found_msg_patient_advice_pat_psv);
        noAdviceMsg.setVisibility(View.GONE);

        patAdviceLists = new ArrayList<>();

        adviceView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        adviceView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(adviceView.getContext(),DividerItemDecoration.VERTICAL);
        adviceView.addItemDecoration(dividerItemDecoration);

        refresh.setOnClickListener(v -> getPatAdvice());

        getPatAdvice();

        return view;
    }

    public void getPatAdvice() {
        fullFragment.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        PatPrescriptionV.medicationLoadingPatPSV = true;
        int pos = PatPrescriptionV.medicationtabLayoutPatPSV.getSelectedTabPosition();
        int tabCount = PatPrescriptionV.medicationtabLayoutPatPSV.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            if (pos != i) {
                Objects.requireNonNull(PatPrescriptionV.medicationtabLayoutPatPSV.getTabAt(i)).view.setClickable(false);
            }
        }

        patAdviceLists = new ArrayList<>();
        String advUrl = pre_url_api+"prescription/getPatAdvice?pmm_id="+pmm_Id;
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest manageReq = new StringRequest(Request.Method.GET, advUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pa_id = info.getString("pa_id")
                                .equals("null") ? "" : info.getString("pa_id");
                        String pa_name = info.getString("pa_name")
                                .equals("null") ? "" : info.getString("pa_name");
                        String pa_pmm_id = info.getString("pa_pmm_id")
                                .equals("null") ? "" : info.getString("pa_pmm_id");

                        patAdviceLists.add(new PatAdviceList(pa_id,pa_name,pa_pmm_id));
                    }
                }

                connected = true;
                updateInterface();
            }
            catch (Exception e) {
                connected = false;
                logger.log(Level.WARNING,e.getMessage(),e);
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            logger.log(Level.WARNING,error.getMessage(),error);
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(manageReq);
    }

    private void updateInterface() {
        PatPrescriptionV.medicationLoadingPatPSV = false;
        circularProgressIndicator.setVisibility(View.GONE);
        int tabCount = PatPrescriptionV.medicationtabLayoutPatPSV.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            Objects.requireNonNull(PatPrescriptionV.medicationtabLayoutPatPSV.getTabAt(i)).view.setClickable(true);
        }
        if(conn) {
            if (connected) {
                fullFragment.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                if (patAdviceLists.isEmpty()) {
                    noAdviceMsg.setVisibility(View.VISIBLE);
                }
                else {
                    noAdviceMsg.setVisibility(View.GONE);
                }
                patAdvicePSVAdapter = new PatAdvicePSVAdapter(patAdviceLists,mContext);
                adviceView.setAdapter(patAdvicePSVAdapter);

            }
            else {
                if (parsing_message != null) {
                    if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                        parsing_message = "Server problem or Internet not connected";
                    }
                }
                else {
                    parsing_message = "Server problem or Internet not connected";
                }

                Toast.makeText(mContext, parsing_message, Toast.LENGTH_SHORT).show();
                refresh.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (parsing_message != null) {
                if (parsing_message.isEmpty() || parsing_message.equals("null")) {
                    parsing_message = "Server problem or Internet not connected";
                }
            }
            else {
                parsing_message = "Server problem or Internet not connected";
            }
            Toast.makeText(mContext, parsing_message, Toast.LENGTH_SHORT).show();
            refresh.setVisibility(View.VISIBLE);
        }
    }
}