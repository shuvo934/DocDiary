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
import java.util.logging.Level;
import java.util.logging.Logger;

import ttit.com.shuvo.docdiary.R;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.PrescriptionSetup;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.adapters.PatMedicationAdapter;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.addinfo.MedicationModify;
import ttit.com.shuvo.docdiary.appt_schedule.prescription.fragments.arraylists.PatMedicationList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Medication#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Medication extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String pmm_Id;
    private String mParam2;

    public Medication() {
        // Required empty public constructor
    }

    CardView fullFragment;
    CircularProgressIndicator circularProgressIndicator;
    ImageButton refresh;

    RecyclerView medicationView;
    PatMedicationAdapter patMedicationAdapter;
    RecyclerView.LayoutManager layoutManager;
    public static TextView noPatMedMsg;
    MaterialButton medAdd;
    ArrayList<PatMedicationList> patMedicationLists;

    private Boolean conn = false;
    private Boolean connected = false;
    String parsing_message = "";

    Context mContext;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
    }

    Logger logger = Logger.getLogger(Medication.class.getName());

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Medication.
     */
    public static Medication newInstance(String param1, String param2) {
        Medication fragment = new Medication();
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
        View view =  inflater.inflate(R.layout.fragment_medication, container, false);
        fullFragment = view.findViewById(R.id.patient_medication_full_fragment);
        circularProgressIndicator = view.findViewById(R.id.progress_indicator_patient_medication);
        circularProgressIndicator.setVisibility(View.GONE);
        refresh = view.findViewById(R.id.refresh_button_for_patient_medication);
        refresh.setVisibility(View.GONE);

        medicationView = view.findViewById(R.id.patient_medication_recyclerview);
        noPatMedMsg = view.findViewById(R.id.no_information_found_msg_patient_medication);
        noPatMedMsg.setVisibility(View.GONE);
        medAdd = view.findViewById(R.id.add_patient_medication);

        patMedicationLists = new ArrayList<>();

        medicationView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        medicationView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(medicationView.getContext(),DividerItemDecoration.VERTICAL);
        medicationView.addItemDecoration(dividerItemDecoration);

        medAdd.setOnClickListener(v -> {
            Intent intent1 = new Intent(mContext, MedicationModify.class);
            intent1.putExtra("P_PMM_ID", pmm_Id);
            intent1.putExtra("MED_MODIFY_TYPE","ADD");
            startActivity(intent1);
        });

        refresh.setOnClickListener(v -> getPatMedication());

        getPatMedication();

        return view;
    }

    public void getPatMedication() {
        fullFragment.setVisibility(View.GONE);
        circularProgressIndicator.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.GONE);
        conn = false;
        connected = false;
        PrescriptionSetup.medicationLoading = true;
        int pos = PrescriptionSetup.medicationtabLayout.getSelectedTabPosition();
        int tabCount = PrescriptionSetup.medicationtabLayout.getTabCount();
//        System.out.println("POS: " + pos + " count: "+ tabCount);
        for (int i = 0; i < tabCount; i++) {
            if (pos != i) {
                Objects.requireNonNull(PrescriptionSetup.medicationtabLayout.getTabAt(i)).view.setClickable(false);
            }
        }

        patMedicationLists = new ArrayList<>();
        String medHistUrl = pre_url_api+"prescription/getPatMedication?pmm_id="+pmm_Id;
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

                        String pmp_id = info.getString("pmp_id")
                                .equals("null") ? "" : info.getString("pmp_id");
                        String pmp_pmm_id = info.getString("pmp_pmm_id")
                                .equals("null") ? "" : info.getString("pmp_pmm_id");
                        String medicine_id = info.getString("medicine_id")
                                .equals("null") ? "" : info.getString("medicine_id");
                        String medicine_name = info.getString("medicine_name")
                                .equals("null") ? "" : info.getString("medicine_name");
                        String mpm_id = info.getString("mpm_id")
                                .equals("null") ? "" : info.getString("mpm_id");
                        String mpm_name = info.getString("mpm_name")
                                .equals("null") ? "" : info.getString("mpm_name");
                        String dose_id = info.getString("dose_id")
                                .equals("null") ? "" : info.getString("dose_id");
                        String dose_name = info.getString("dose_name")
                                .equals("null") ? "" : info.getString("dose_name");
                        String pmp_duration = info.getString("pmp_duration")
                                .equals("null") ? "" : info.getString("pmp_duration");

                        patMedicationLists.add(new PatMedicationList(pmp_id,pmp_pmm_id,medicine_id,medicine_name,
                                mpm_id,mpm_name,dose_id,dose_name,pmp_duration));
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

        requestQueue.add(medHistReq);
    }

    private void updateInterface() {
        PrescriptionSetup.medicationLoading = false;
        circularProgressIndicator.setVisibility(View.GONE);
//        int pos = PrescriptionSetup.historytabLayout.getSelectedTabPosition();
        int tabCount = PrescriptionSetup.medicationtabLayout.getTabCount();
//        System.out.println("POS: " + pos + " count: "+ tabCount);
        for (int i = 0; i < tabCount; i++) {
            Objects.requireNonNull(PrescriptionSetup.medicationtabLayout.getTabAt(i)).view.setClickable(true);
        }
        if(conn) {
            if (connected) {
                fullFragment.setVisibility(View.VISIBLE);
                conn = false;
                connected = false;

                if (patMedicationLists.isEmpty()) {
                    noPatMedMsg.setVisibility(View.VISIBLE);
                }
                else {
                    noPatMedMsg.setVisibility(View.GONE);
                }
                patMedicationAdapter = new PatMedicationAdapter(patMedicationLists,mContext);
                medicationView.setAdapter(patMedicationAdapter);

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