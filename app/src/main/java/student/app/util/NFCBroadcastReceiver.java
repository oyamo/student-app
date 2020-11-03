package student.app.util;

import android.app.Activity;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class NFCBroadcastReceiver extends BroadcastReceiver {
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equalsIgnoreCase(action)) {


            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                
            } else {

            }


            // Check if manager is null
            if(manager != null){
                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {

                    }
                });
            }
        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equalsIgnoreCase(action)){

        }
    }

    public void setManager(WifiP2pManager manager) {
        this.manager = manager;
    }

    public void setChannel(WifiP2pManager.Channel channel) {
        this.channel=channel;
    }
}
