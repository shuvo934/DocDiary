package ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments;

import static ttit.com.shuvo.docdiary.dashboard.DocDashboard.pre_url_api;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.PrescriptionSetup;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.adapters.PatDrugHistAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.adapters.PatMedicHistAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.DrugHistoryModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatDrugHistList;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatMedicHistList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrugHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrugHistory extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String pmm_Id;
    private String mParam2;


    public DrugHistory() {
        // Required empty public constructor
    }

    CardView fullFragment;
    CircularProgressIndicator circularProgressIndicator;
    ImageButton refresh;

    RecyclerView drugHistoryView;
    RecyclerView.LayoutManager layoutManager;
    PatDrugHistAdapter patDrugHistAdapter;
    public static TextView noPatDrugHistMsg;
    MaterialButton drugHistAdd;
    ArrayList<PatDrugHistList> patDrugHistLists;

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
     * @return A new instance of fragment DrugHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static DrugHistory newInstance(String param1, String param2) {
        DrugHistory fragment = new DrugHistory();
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
        View view =  inflater.inflate(R.layout.fragment_drug_history, container, false);
        fullFragment = view.findViewById(R.id.drug_history_full_fragment);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_drug_history);
        circularProgressIndicator.setVisibility(View.GONE);
        refresh = view.findViewById(R.id.refresh_button_for_drug_history);
        refresh.setVisibility(View.GONE);

        drugHistoryView = view.findViewById(R.id.drug_history_recyclerview);
        noPatDrugHistMsg = view.findViewById(R.id.no_information_found_msg_drug_history);
        noPatDrugHistMsg.setVisibility(View.GONE);
        drugHistAdd = view.findViewById(R.id.add_drug_history_for_patient);

        patDrugHistLists = new ArrayList<>();

        drugHistoryView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        drugHistoryView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(drugHistoryView.getContext(),DividerItemDecoration.VERTICAL);
        drugHistoryView.addItemDecoration(dividerItemDecoration);

        drugHistAdd.setOnClickListener(v -> {
            Intent intent1 = new Intent(mContext, DrugHistoryModify.class);
            intent1.putExtra("P_PMM_ID", pmm_Id);
            intent1.putExtra("HIST_MODIFY_TYPE","ADD");
            startActivity(intent1);
        });
        refresh.setOnClickListener(v -> {
            getDrugHistory();
        });

        getDrugHistory();

        return view;
    }

    public void getDrugHistory() {
        fullFragment.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        PrescriptionSetup.historyLoading = true;
        int pos = PrescriptionSetup.historytabLayout.getSelectedTabPosition();
        int tabCount = PrescriptionSetup.historytabLayout.getTabCount();
//        System.out.println("POS: " + pos + " count: "+ tabCount);
        for (int i = 0; i < tabCount; i++) {
            if (pos != i) {
                Objects.requireNonNull(PrescriptionSetup.historytabLayout.getTabAt(i)).view.setClickable(false);
            }
        }

        patDrugHistLists = new ArrayList<>();
        String dHistUrl = pre_url_api+"prescription/getPatDrugHist?pmm_id="+pmm_Id+"";
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

        StringRequest dHistReq = new StringRequest(Request.Method.GET, dHistUrl, response -> {
            conn = true;
            try {
                JSONObject jsonObject = new JSONObject(response);
                String items = jsonObject.getString("items");
                String count = jsonObject.getString("count");
                if (!count.equals("0")) {
                    JSONArray array = new JSONArray(items);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject info = array.getJSONObject(i);

                        String pdh_id = info.getString("pdh_id")
                                .equals("null") ? "" : info.getString("pdh_id");
                        String pdh_pmm_id = info.getString("pdh_pmm_id")
                                .equals("null") ? "" : info.getString("pdh_pmm_id");
                        String medicine_id = info.getString("medicine_id")
                                .equals("null") ? "" : info.getString("medicine_id");
                        String medicine_name = info.getString("medicine_name")
                                .equals("null") ? "" : info.getString("medicine_name");
                        String pdh_details = info.getString("pdh_details")
                                .equals("null") ? "" : info.getString("pdh_details");

                        patDrugHistLists.add(new PatDrugHistList(pdh_id,pdh_pmm_id,medicine_id,medicine_name,pdh_details));
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

        requestQueue.add(dHistReq);
    }

    private void updateInterface() {
        PrescriptionSetup.historyLoading = false;
        circularProgressIndicator.setVisibility(View.GONE);
//        int pos = PrescriptionSetup.historytabLayout.getSelectedTabPosition();
        int tabCount = PrescriptionSetup.historytabLayout.getTabCount();
//        System.out.println("POS: " + pos + " count: "+ tabCount);
        for (int i = 0; i < tabCount; i++) {
            Objects.requireNonNull(PrescriptionSetup.historytabLayout.getTabAt(i)).view.setClickable(true);
        }
        if(conn) {
            if (connected) {
                fullFragment.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                if (patDrugHistLists.size() == 0) {
                    noPatDrugHistMsg.setVisibility(View.VISIBLE);
                }
                else {
                    noPatDrugHistMsg.setVisibility(View.GONE);
                }
                patDrugHistAdapter = new PatDrugHistAdapter(patDrugHistLists,mContext);
                drugHistoryView.setAdapter(patDrugHistAdapter);

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