package student.nfcnetwork.common.listeners;


import java.util.List;

import student.nfcnetwork.common.WiFiP2PError;
import student.nfcnetwork.common.WroupServiceDevice;

public interface ServiceDiscoveredListener {

    void onNewServiceDeviceDiscovered(WroupServiceDevice serviceDevice);

    void onFinishServiceDeviceDiscovered(List<WroupServiceDevice> serviceDevices);

    void onError(WiFiP2PError wiFiP2PError);

}
