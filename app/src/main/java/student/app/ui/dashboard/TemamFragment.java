package student.app.ui.dashboard;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import student.app.R;
import student.app.prefs.AuthPref;

import static android.net.wifi.p2p.WifiP2pManager.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TemamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TemamFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button scan;
    TextView na;
    TextView tv;
    EditText editText;
    IntentFilter intentFilter;
    Channel channel;
    WifiP2pManager manager;
    public TemamFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TemamFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TemamFragment newInstance(String param1, String param2) {
        TemamFragment fragment = new TemamFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_temam, container, false);
        scan = v.findViewById(R.id.scanBtn);
        editText = v.findViewById(R.id.studentIDInput);
        tv = v.findViewById(R.id.studentIdLabel);
        na = v.findViewById(R.id.notAllowed);

        intentFilter = new IntentFilter();
        // Indicates a change in the wifi P2p status
        intentFilter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates change in the list of available peers
        intentFilter.addAction(WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wifi p2p connectivity has changed
        intentFilter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's de tails have changed
        intentFilter.addAction(WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // Assign to the manager
        manager = (WifiP2pManager) getContext().getSystemService(Context.WIFI_P2P_SERVICE);

        if(manager != null){
            channel = manager.initialize(getContext(), Looper.getMainLooper(), null);
        }


        AuthPref authPref = new AuthPref(this.getContext());
        String userGroup = authPref.getUserGroup();
        if(userGroup.equalsIgnoreCase("Dorms")){
            editText.setHint("Enter a Student ID");
            scan.setText("Scan");
            tv.setText("Enter Student ID to scan");
        }else if(userGroup.equalsIgnoreCase("Students")){
            editText.setHint("Your student ID");
            scan.setText("Scan");
            tv.setText("Confirm Student ID");
        }else{
            editText.setHint("Student ID");
            scan.setText("Scan");
            tv.setText("Enter a Student ID");
            scan.setEnabled(false);
            editText.setEnabled(false);
            na.setVisibility(View.VISIBLE);
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



}