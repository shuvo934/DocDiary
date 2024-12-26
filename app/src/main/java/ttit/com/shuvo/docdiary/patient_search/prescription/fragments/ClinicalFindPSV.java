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
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatClinicalFindingsList;
import ttit.com.shuvo.docdiary.patient_search.prescription.PatPrescriptionV;
import ttit.com.shuvo.docdiary.patient_search.prescription.fragments.adapters.PatClFindPSVAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClinicalFindPSV#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClinicalFindPSV extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String pmm_Id;
    private String mParam2;

    public ClinicalFindPSV() {
        // Required empty public constructor
    }

    CardView fullFragment;
    CircularProgressIndicator circularProgressIndicator;
    ImageButton refresh;

    RecyclerView clFindingsView;
    RecyclerView.LayoutManager layoutManager;
    PatClFindPSVAdapter patClFindPSVAdapter;
    TextView noPatClFindMsg;
    ArrayList<PatClinicalFindingsList> patClinicalFindingsLists;

    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";

    Context mContext;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
    }

    Logger logger = Logger.getLogger(ClinicalFindPSV.class.getName());

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClinicalFindPSV.
     */
    public static ClinicalFindPSV newInstance(String param1, String param2) {
        ClinicalFindPSV fragment = new ClinicalFindPSV();
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
        View view = inflater.inflate(R.layout.fragment_clinical_find_p_s_v, container, false);
        fullFragment = view.findViewById(R.id.cl_findings_full_fragment_pat_psv);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_cl_findings_pat_psv);
        circularProgressIndicator.setVisibility(View.GONE);
        refresh = view.findViewById(R.id.refresh_button_for_cl_findings_pat_psv);
        refresh.setVisibility(View.GONE);

        clFindingsView = view.findViewById(R.id.cl_findings_recyclerview_pat_psv);
        noPatClFindMsg = view.findViewById(R.id.no_information_found_msg_cl_findings_pat_psv);
        noPatClFindMsg.setVisibility(View.GONE);

        patClinicalFindingsLists = new ArrayList<>();

        clFindingsView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        clFindingsView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(clFindingsView.getContext(),DividerItemDecoration.VERTICAL);
        clFindingsView.addItemDecoration(dividerItemDecoration);

        refresh.setOnClickListener(v -> getClFindings());

        getClFindings();

        return  view;
    }

    public void getClFindings() {
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

        patClinicalFindingsLists = new ArrayList<>();
        String cfUrl = pre_url_api+"prescription/getPatClFindings?pmm_id="+pmm_Id;
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest cfReq = new StringRequest(Request.Method.GET, cfUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String cf_id = info.getString("cf_id")
                                .equals("null") ? "" : info.getString("cf_id");
                        String cf_pmm_id = info.getString("cf_pmm_id")
                                .equals("null") ? "" : info.getString("cf_pmm_id");
                        String cfm_id = info.getString("cfm_id")
                                .equals("null") ? "" : info.getString("cfm_id");
                        String cfm_name = info.getString("cfm_name")
                                .equals("null") ? "" : info.getString("cfm_name");
                        String cf_details = info.getString("cf_details")
                                .equals("null") ? "" : info.getString("cf_details");

                        patClinicalFindingsLists.add(new PatClinicalFindingsList(cf_id,cf_pmm_id,cfm_id,cfm_name,cf_details));
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

        requestQueue.add(cfReq);
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

                if (patClinicalFindingsLists.isEmpty()) {
                    noPatClFindMsg.setVisibility(View.VISIBLE);
                }
                else {
                    noPatClFindMsg.setVisibility(View.GONE);
                }
                patClFindPSVAdapter = new PatClFindPSVAdapter(patClinicalFindingsLists,mContext);
                clFindingsView.setAdapter(patClFindPSVAdapter);

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