package student.app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;

import android.net.wifi.p2p.WifiP2pManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import student.app.R;
import student.app.Splash;
import student.app.prefs.AuthPref;
import student.wnetwork.client.WroupClient;
import student.wnetwork.common.WiFiDirectBroadcastReceiver;
import student.wnetwork.common.WiFiP2PError;
import student.wnetwork.common.WiFiP2PInstance;
import student.wnetwork.common.WroupDevice;
import student.wnetwork.common.WroupServiceDevice;
import student.wnetwork.common.listeners.ClientConnectedListener;
import student.wnetwork.common.listeners.DataReceivedListener;
import student.wnetwork.common.listeners.ServiceConnectedListener;
import student.wnetwork.common.listeners.ServiceDisconnectedListener;
import student.wnetwork.common.listeners.ServiceDiscoveredListener;
import student.wnetwork.common.messages.MessageWrapper;
;

public class StudentAttendActivity extends AppCompatActivity  {
    WiFiDirectBroadcastReceiver fiDirectBroadcastReceiver;
    FirebaseAuth auth;
    FirebaseUser user;
    final  String TAG = "StudentAttend";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attend);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.NFC) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.NFC, Manifest.permission.ACCESS_FINE_LOCATION}, 30);
            }
        }
        fiDirectBroadcastReceiver = WiFiP2PInstance.getInstance(getApplicationContext()).getBroadcastReceiver();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter =  new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        registerReceiver(fiDirectBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(fiDirectBroadcastReceiver);
    }


    public void Attend(View view) {

        final ProgressIndicator indicator = findViewById(R.id.progress);

        final MaterialCardView stopBtn = findViewById(R.id.stopWifi);

        if(indicator.getVisibility() == View.VISIBLE){
            return;
        }


        String channelId = "efae";

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "NFC running",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
        }
        final NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, channelId);
        notifBuilder.setContentTitle("One Touch NFC");
        notifBuilder.setContentText("Student attendance is currently in session. It might take some time.");
        notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notifBuilder.setSmallIcon(R.drawable.ic_baseline_nfc_24);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, notifBuilder.build());

        final TextView textView = findViewById(R.id.buttonText);
        indicator.setVisibility(View.VISIBLE);
        textView.setText(R.string.discovering);
        stopBtn.setVisibility(View.VISIBLE);
        final WroupClient wroupClient = WroupClient.getInstance(getApplicationContext());


        wroupClient.discoverServices(15000L, new ServiceDiscoveredListener() {
            @Override
            public void onNewServiceDeviceDiscovered(WroupServiceDevice serviceDevice) {
                notifBuilder.setContentText("One Admin discovered. Please wait while we connect with the admin");
                notificationManager.notify(1, notifBuilder.build());
                textView.setText(R.string.connecting);
                wroupClient.connectToService(serviceDevice, new ServiceConnectedListener() {
                    @Override
                    public void onServiceConnected(WroupDevice serviceDevice) {
                        notifBuilder.setContentText("Connected to the admin successfully. Transferring data.");
                        notificationManager.notify(1, notifBuilder.build());
                        Toast.makeText(StudentAttendActivity.this, "Connected to " + serviceDevice.getDeviceName(), Toast.LENGTH_SHORT).show();


                        AuthPref authPref = new AuthPref(getApplicationContext());
                        String name = authPref.getUserName();
                        String course = authPref.getCourse();
                        String dorm = authPref.getHostel();
                        int studentId = authPref.getUserId();


                        MessageWrapper messageWrapper = new MessageWrapper();
                        String builder = studentId + ":" +
                                name + ":" +
                                dorm + ":" +
                                course;
                        messageWrapper.setMessage(builder);
                        messageWrapper.setMessageType(MessageWrapper.MessageType.NORMAL);
                        textView.setText(R.string.sending_info);
                        wroupClient.sendMessageToServer(messageWrapper);

                    }
                });
            }

            @Override
            public void onFinishServiceDeviceDiscovered(List<WroupServiceDevice> serviceDevices) {
                notifBuilder.setContentText("Finished scanning."+ serviceDevices.size() +" admins found");
                notificationManager.notify(2, notifBuilder.build());
                if(serviceDevices.size() == 0){
                    indicator.setVisibility(View.GONE);
                    stopBtn.setVisibility(View.GONE);
                    textView.setText(R.string.retry);
                }
            }

            @Override
            public void onError(WiFiP2PError wiFiP2PError) {
                notifBuilder.setContentText("Error occurred while scanning. You may want to restart this app. If the problem persists report this problem");
                notificationManager.notify(3, notifBuilder.build());
                indicator.setVisibility(View.GONE);
                stopBtn.setVisibility(View.GONE);
                textView.setText(R.string.retry);
            }
        });

        wroupClient.setDataReceivedListener(new DataReceivedListener() {
            @Override
            public void onDataReceived(MessageWrapper messageWrapper) {
                String message = messageWrapper.getMessage();
                notifBuilder.setContentText(message);
                notificationManager.notify(4, notifBuilder.build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indicator.setVisibility(View.GONE);
                        stopBtn.setVisibility(View.GONE);
                        textView.setText(R.string.attend);
                    }
                });
            }

        });

        wroupClient.setClientConnectedListener(new ClientConnectedListener() {
            @Override
            public void onClientConnected(WroupDevice wroupDevice) {

                Toast.makeText(StudentAttendActivity.this, "Sending details", Toast.LENGTH_SHORT).show();
            }
        });

        wroupClient.setServerDisconnetedListener(new ServiceDisconnectedListener() {
            @Override
            public void onServerDisconnectedListener() {
                indicator.setVisibility(View.GONE);
                stopBtn.setVisibility(View.GONE);
                textView.setText(R.string.attend);
                Toast.makeText(StudentAttendActivity.this, "Admin disconnected", Toast.LENGTH_SHORT).show();
            }
        });


        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wroupClient.disconnect();
                indicator.setVisibility(View.GONE);
                stopBtn.setVisibility(View.GONE);
                textView.setText(R.string.attend);
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 30){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                finish();
            }
        }
    }

    public void Logout(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(StudentAttendActivity.this);
        alert.setCancelable(true);
        alert.setTitle("Log out");
        alert.setMessage("Are you sure you want to log out?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                startActivity(new Intent(StudentAttendActivity.this, Splash.class));
                finish();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();

    }

}