package student.nfcnetwork.common.listeners;


import android.net.wifi.p2p.WifiP2pInfo;

public interface PeerConnectedListener {

    void onPeerConnected(WifiP2pInfo wifiP2pInfo);

}
