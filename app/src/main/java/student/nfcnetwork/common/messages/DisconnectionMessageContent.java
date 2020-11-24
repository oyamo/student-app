package student.nfcnetwork.common.messages;


import student.nfcnetwork.common.WroupDevice;

public class DisconnectionMessageContent {

    private WroupDevice wroupDevice;


    public void setWroupDevice(WroupDevice wroupDevice) {
        this.wroupDevice = wroupDevice;
    }

    public WroupDevice getWroupDevice() {
        return wroupDevice;
    }

}
