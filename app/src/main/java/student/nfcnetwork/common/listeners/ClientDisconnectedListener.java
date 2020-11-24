package student.nfcnetwork.common.listeners;


import student.nfcnetwork.common.WroupDevice;

public interface ClientDisconnectedListener {

    void onClientDisconnected(WroupDevice wroupDevice);

}
