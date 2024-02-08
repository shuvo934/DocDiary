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

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatMedicHistList;
import ttit.com.shuvo.docdiary.patient_search.prescription.PatPrescriptionV;
import ttit.com.shuvo.docdiary.patient_search.prescription.fragments.adapters.PatMedicHistPSVAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MedicalHistPSV#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicalHistPSV extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String pmm_Id;
    private String mParam2;

    public MedicalHistPSV() {
        // Required empty public constructor
    }

    CardView fullFragment;
    CircularProgressIndicator circularProgressIndicator;
    ImageButton refresh;

    RecyclerView medHistoryView;
    PatMedicHistPSVAdapter patMedicHistPSVAdapter;
    RecyclerView.LayoutManager layoutManager;
    TextView noPatMedHistMsg;
    ArrayList<PatMedicHistList> patMedicHistLists;

    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";

    Context mContext;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MedicalHistPSV.
     */
    // TODO: Rename and change types and number of parameters
    public static MedicalHistPSV newInstance(String param1, String param2) {
        MedicalHistPSV fragment = new MedicalHistPSV();
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
        View view = inflater.inflate(R.layout.fragment_medical_hist_p_s_v, container, false);
        fullFragment = view.findViewById(R.id.past_medical_history_full_fragment_pat_psv);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_past_medical_history_pat_psv);
        circularProgressIndicator.setVisibility(View.GONE);
        refresh = view.findViewById(R.id.refresh_button_for_past_medical_history_pat_psv);
        refresh.setVisibility(View.GONE);

        medHistoryView = view.findViewById(R.id.past_medical_history_recyclerview_pat_psv);
        noPatMedHistMsg = view.findViewById(R.id.no_information_found_msg_past_medical_history_pat_psv);
        noPatMedHistMsg.setVisibility(View.GONE);

        patMedicHistLists = new ArrayList<>();

        medHistoryView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        medHistoryView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(medHistoryView.getContext(),DividerItemDecoration.VERTICAL);
        medHistoryView.addItemDecoration(dividerItemDecoration);

        refresh.setOnClickListener(v -> {
            getMedHistory();
        });

        getMedHistory();
        return view;
    }

    public void getMedHistory() {
        fullFragment.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        PatPrescriptionV.historyLoadingPatPSV = true;
        int pos = PatPrescriptionV.historytabLayoutPatPSV.getSelectedTabPosition();
        int tabCount = PatPrescriptionV.historytabLayoutPatPSV.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            if (pos != i) {
                Objects.requireNonNull(PatPrescriptionV.historytabLayoutPatPSV.getTabAt(i)).view.setClickable(false);
            }
        }

        patMedicHistLists = new ArrayList<>();

        String medHistUrl = pre_url_api+"prescription/getPatMedicalHist?pmm_id="+pmm_Id+"";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest medHistReq = new StringRequest(Request.Method.GET, medHistUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pmh_id = info.getString("pmh_id")
                                .equals("null") ? "" : info.getString("pmh_id");
                        String pmh_pmm_id = info.getString("pmh_pmm_id")
                                .equals("null") ? "" : info.getString("pmh_pmm_id");
                        String mhm_id = info.getString("mhm_id")
                                .equals("null") ? "" : info.getString("mhm_id");
                        String mhm_name = info.getString("mhm_name")
                                .equals("null") ? "" : info.getString("mhm_name");
                        String pmh_details = info.getString("pmh_details")
                                .equals("null") ? "" : info.getString("pmh_details");

                        patMedicHistLists.add(new PatMedicHistList(pmh_id,pmh_pmm_id,mhm_id,mhm_name,pmh_details));
                    }
                }

                connected = true;
                updateInterface();
            }
            catch (Exception e) {
                connected = false;
                e.printStackTrace();
                parsing_message = e.getLocalizedMessage();
                updateInterface();
            }
        }, error -> {
            conn = false;
            connected = false;
            error.printStackTrace();
            parsing_message = error.getLocalizedMessage();
            updateInterface();
        });

        requestQueue.add(medHistReq);
    }

    private void updateInterface() {
        PatPrescriptionV.historyLoadingPatPSV = false;
        circularProgressIndicator.setVisibility(View.GONE);
        int tabCount = PatPrescriptionV.historytabLayoutPatPSV.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            Objects.requireNonNull(PatPrescriptionV.historytabLayoutPatPSV.getTabAt(i)).view.setClickable(true);
        }
        if(conn) {
            if (connected) {
                fullFragment.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                if (patMedicHistLists.size() == 0) {
                    noPatMedHistMsg.setVisibility(View.VISIBLE);
                }
                else {
                    noPatMedHistMsg.setVisibility(View.GONE);
                }
                patMedicHistPSVAdapter = new PatMedicHistPSVAdapter(patMedicHistLists,mContext);
                medHistoryView.setAdapter(patMedicHistPSVAdapter);

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