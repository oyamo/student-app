package student.app.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import student.app.R;
import student.app.prefs.AuthPref;
import student.app.util.NFCBroadcastReceiver;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION;
import static android.os.Looper.getMainLooper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendanceFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String  TAG = "Attendance";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button scan;
    TextView na;
    TextView tv;
    EditText editText;
    ProgressBar pb;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    IntentFilter intentFilter;
    NFCBroadcastReceiver receiver;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;


    final int FL_C = 0;
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case FL_C:
                if  (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Fine location permission is not granted!");
                }
                break;
        }
    }


    public AttendanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendanceFragment newInstance(String param1, String param2) {
        AttendanceFragment fragment = new AttendanceFragment();
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

    private boolean initP2p() {
        // Device capability definition check
        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e(TAG, "Wi-Fi Direct is not supported by this device.");
            return false;
        }
        // Hardware capability check
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e(TAG, "Cannot get Wi-Fi system service.");
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!wifiManager.isP2pSupported()) {
                Log.e(TAG, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.");
                return false;
            }
        }else{
            return false;
        }
        manager = (WifiP2pManager) getContext().getSystemService(Context.WIFI_P2P_SERVICE);
        if (manager == null) {
            Log.e(TAG, "Cannot get Wi-Fi Direct system service.");
            return false;
        }
        channel = manager.initialize(getContext(), getMainLooper(), null);
        if (channel == null) {
            Log.e(TAG, "Cannot initialize Wi-Fi Direct.");
            return false;
        }
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_attendance, container, false);

        scan = v.findViewById(R.id.scanBtn);
        editText = v.findViewById(R.id.studentIDInput);
        tv = v.findViewById(R.id.studentIdLabel);
        na = v.findViewById(R.id.notAllowed);
        pb = v.findViewById(R.id.progress);
        scan.setOnClickListener(this);
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
        receiver = new NFCBroadcastReceiver();
        receiver.setManager(manager);
        receiver.setChannel(channel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FL_C);
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }
        if(manager != null){
            channel = manager.initialize(getContext(), getMainLooper(), null);
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "RUnnoign", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getContext(), "Err"+reason, Toast.LENGTH_SHORT).show();
                }
            });
        }

        AuthPref authPref = new AuthPref(this.getContext());
        String userGroup = authPref.getUserGroup();
        if(userGroup.equalsIgnoreCase("Teachers")){
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int view = v.getId();
        if (view == R.id.scanBtn){
            pb.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new NFCBroadcastReceiver();
        receiver.setManager(manager);
        receiver.setChannel(channel);
        getContext().registerReceiver(receiver, intentFilter);
    }
}