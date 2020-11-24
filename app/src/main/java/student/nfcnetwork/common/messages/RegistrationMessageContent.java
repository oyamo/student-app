package student.nfcnetwork.common.messages;


import student.nfcnetwork.common.WroupDevice;

public class RegistrationMessageContent {

    private WroupDevice wroupDevice;

    public WroupDevice getWroupDevice() {
        return wroupDevice;
    }

    public void setWroupDevice(WroupDevice wroupDevice) {
        this.wroupDevice = wroupDevice;
    }

}
