package student.nfcnetwork.common.listeners;


import student.nfcnetwork.common.messages.MessageWrapper;

public interface DataReceivedListener {

    void onDataReceived(MessageWrapper messageWrapper);

}
