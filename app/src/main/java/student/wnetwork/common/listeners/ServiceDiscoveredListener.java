package student.wnetwork.common.listeners;


import java.util.List;

import student.wnetwork.common.WiFiP2PError;
import student.wnetwork.common.WroupServiceDevice;

public interface ServiceDiscoveredListener {

    void onNewServiceDeviceDiscovered(WroupServiceDevice serviceDevice);

    void onFinishServiceDeviceDiscovered(List<WroupServiceDevice> serviceDevices);

    void onError(WiFiP2PError wiFiP2PError);

}
