package student.wnetwork.common.listeners;


import student.wnetwork.common.WiFiP2PError;

public interface ServiceRegisteredListener {

    void onSuccessServiceRegistered();

    void onErrorServiceRegistered(WiFiP2PError wiFiP2PError);

}
