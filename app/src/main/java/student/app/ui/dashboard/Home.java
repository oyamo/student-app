package student.app.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



import student.app.R;
import student.app.Splash;
import student.app.livedata.StudentData;
import student.app.models.Student;
import student.app.ui.Pager;
import student.app.util.NFCBroadcastReceiver;

public class Home extends AppCompatActivity implements
        TabLayout.OnTabSelectedListener,
        WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.PeerListListener {
    private StudentData studentData;
    FirebaseAuth auth;
    FirebaseFirestore db;
    TabLayout tl;
    MaterialToolbar tb;
    ViewPager viewPager;

    // Networking
    public static final String TAG = "HOME";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;

    // Broadcast receiver
    private NFCBroadcastReceiver receiver = null;


    // Detected gadget
    private WifiP2pDevice device;
    private WifiP2pInfo info;



    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION:
                if  (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Fine location permission is not granted!");
                }
                break;
        }
    }
    private boolean initP2p() {
        // Device capability definition check
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e(TAG, "Wi-Fi Direct is not supported by this device.");
            return false;
        }
        // Hardware capability check
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (manager == null) {
            Log.e(TAG, "Cannot get Wi-Fi Direct system service.");
            return false;
        }
        channel = manager.initialize(this, getMainLooper(), null);
        if (channel == null) {
            Log.e(TAG, "Cannot initialize Wi-Fi Direct.");
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        studentData = new ViewModelProvider(this).get(StudentData.class);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tl = findViewById(R.id.tabLayout);
        tb = findViewById(R.id.toolBar);
        viewPager = findViewById(R.id.viewPager);
        setSupportActionBar(tb);
        Pager adapter = new Pager(getSupportFragmentManager(), 3);
        viewPager.setAdapter(adapter);
        tl.setupWithViewPager(viewPager);
        tl.setOnTabSelectedListener(this);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        if (manager != null && channel != null) {
            // Since this is the system wireless settings activity, it's
            // not going to send us a result. We will be notified by
            // WiFiDeviceBroadcastReceiver instead.

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("You need to enable WI-FI/NFC or this app will exit.");
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alertDialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            });

        } else {
            Log.e(TAG, "channel or manager is null");
        }



        // SDK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }else{
            if (!initP2p()) {
                finish();
            }else{
                startListening();
            }
        }



    }


    void startListening(){
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(Home.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(Home.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new NFCBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    Spanned html(String ht){
        return HtmlCompat.fromHtml(ht, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }
    private void fetch(){
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null) {
            DocumentReference reference = db.collection("Students").document(currentUser.getUid());
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        Student student = Student.studentFromSnap(documentSnapshot);
                        studentData.getStudent().setValue(student);
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Home.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void logOut(View view) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Log out");
        alertDialog.setMessage("Are sure you want to log out?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                startActivity(new Intent(Home.this, Splash.class));
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();

    }

    /**
     * Called when a tab enters the selected state.
     *
     * @param tab The tab that was selected
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * Called when a tab exits the selected state.
     *
     * @param tab The tab that was unselected
     */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    /**
     * Called when a tab that is already selected is chosen again by the user. Some applications may
     * use this action to return to the top level of a category.
     *
     * @param tab The tab that was reselected.
     */
    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    // The interface from the big class

//    @Override
//    public void showDetails(WifiP2pDevice device) {
//
//    }
//
//    @Override
//    public void cancelDisconnect() {
//
//    }
//
//    @Override
//    public void connect(WifiP2pConfig config) {
//
//    }
//
//    @Override
//    public void disconnect() {
//
//    }

    /**
     * The requested connection info is available
     *
     * @param info Wi-Fi p2p connection info
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        this.info = info;
        Toast.makeText(this, "Connection available", Toast.LENGTH_SHORT).show();
    }

    /**
     * The requested peer list is available
     *
     * @param peers List of available peers
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        Toast.makeText(this, peers.toString(), Toast.LENGTH_SHORT).show();
    }
}