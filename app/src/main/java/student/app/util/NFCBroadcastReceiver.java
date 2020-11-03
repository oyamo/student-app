package student.app.util;

import android.app.Activity;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import student.app.ui.dashboard.Home;

public class NFCBroadcastReceiver extends BroadcastReceiver {

    private List<WifiP2pDevice> peers = new ArrayList<>();
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private  Home home;
    String TAG = "BroadcastReceiver";

    public NFCBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Home home) {
        this.manager = manager;
        this.channel = channel;
        this.home = home;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equalsIgnoreCase(action)) {

            Log.d(TAG,"State changed");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi direct is enabled
                home.setIsWifiP2pEnabled(true);
            } else {
                home.setIsWifiP2pEnabled(false);
            }
        }else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equalsIgnoreCase(action)){
            // Check if manager is null
            if(manager != null){
                manager.requestPeers(channel, home);
            }
        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equalsIgnoreCase(action)){
            if (manager == null) {
                return;
            }
            Log.d(TAG, "CONNECTION CHANGEd");

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP
                Log.d(TAG, "CONNECTED");
                manager.requestConnectionInfo(channel, home);
            } else {
                // It's a disconnect
                Log.d(TAG, "DISCONNECTED");

            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equalsIgnoreCase(action)){

        }
    }

    public void setManager(WifiP2pManager manager) {
        this.manager = manager;
    }

    public void setChannel(WifiP2pManager.Channel channel) {
        this.channel=channel;
    }
}
