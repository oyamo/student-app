package student.wnetwork.common.listeners;


import student.wnetwork.common.messages.MessageWrapper;

public interface DataReceivedListener {

    void onDataReceived(MessageWrapper messageWrapper);

}
