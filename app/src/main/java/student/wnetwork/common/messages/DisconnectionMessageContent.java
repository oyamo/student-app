package student.wnetwork.common.messages;


import student.wnetwork.common.WroupDevice;

public class DisconnectionMessageContent {

    private WroupDevice wroupDevice;


    public void setWroupDevice(WroupDevice wroupDevice) {
        this.wroupDevice = wroupDevice;
    }

    public WroupDevice getWroupDevice() {
        return wroupDevice;
    }

}
