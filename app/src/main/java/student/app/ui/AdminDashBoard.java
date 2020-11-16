package student.app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.invoke.LambdaConversionException;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import student.app.R;
import student.app.Splash;
import student.app.prefs.AuthPref;
import student.httpnetwork.NetActions;
import student.httpnetwork.Service;
import student.httpnetwork.models.Status;
import student.wnetwork.client.WroupClient;
import student.wnetwork.common.WiFiDirectBroadcastReceiver;
import student.wnetwork.common.WiFiP2PError;
import student.wnetwork.common.WiFiP2PInstance;
import student.wnetwork.common.WroupDevice;
import student.wnetwork.common.WroupServiceDevice;
import student.wnetwork.common.listeners.ClientConnectedListener;
import student.wnetwork.common.listeners.ClientDisconnectedListener;
import student.wnetwork.common.listeners.DataReceivedListener;
import student.wnetwork.common.listeners.ServiceDisconnectedListener;
import student.wnetwork.common.listeners.ServiceDiscoveredListener;
import student.wnetwork.common.listeners.ServiceRegisteredListener;
import student.wnetwork.common.messages.MessageWrapper;
import student.wnetwork.service.WroupService;

import static java.lang.Thread.sleep;

public class AdminDashBoard extends AppCompatActivity implements  NfcAdapter.CreateNdefMessageCallback{

    WiFiDirectBroadcastReceiver fiDirectBroadcastReceiver;
    FirebaseAuth auth;
    FirebaseUser user;
    final  String TAG = "AdminDashBoard";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.NFC) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.NFC, Manifest.permission.ACCESS_FINE_LOCATION}, 30);
            }
        }


        fiDirectBroadcastReceiver = WiFiP2PInstance.getInstance(getApplicationContext()).getBroadcastReceiver();
        // Register the server
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
    protected void onPause() {
        super.onPause();
        unregisterReceiver(fiDirectBroadcastReceiver);
    }

    // The onclick handler for handling NFC
    public void Attend(View view) {
        final ProgressIndicator indicator = findViewById(R.id.progress);

        final MaterialCardView stopBtn = findViewById(R.id.stopWifi);

        if(indicator.getVisibility() == View.VISIBLE){
            return;
        }

        String channelId = "efaee";
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
        notifBuilder.setContentTitle("One Touch");
        notifBuilder.setContentText("Scanning a student. Please wait, this will take some time.");
        notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notifBuilder.setSmallIcon(R.drawable.ic_baseline_nfc_24);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, notifBuilder.build());


        final TextView textView = findViewById(R.id.buttonText);
        indicator.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.VISIBLE);
        textView.setText(R.string.starting);

        final WroupService wroupService = WroupService.getInstance(getApplicationContext());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                wroupService.registerService("Admin", new ServiceRegisteredListener() {
                    @Override
                    public void onSuccessServiceRegistered() {
                        textView.setText(R.string.listening);
                    }

                    @Override
                    public void onErrorServiceRegistered(WiFiP2PError wiFiP2PError) {
                        textView.setText(R.string.retry);
                        indicator.setVisibility(View.GONE);
                        stopBtn.setVisibility(View.GONE);

                    }
                });
            }
        });

        wroupService.setClientConnectedListener(new ClientConnectedListener() {
            @Override
            public void onClientConnected(WroupDevice wroupDevice) {
                notifBuilder.setContentText("One student discovered. Please wait while we connect with the admin");
                notificationManager.notify(1, notifBuilder.build());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(R.string.waiting);
                    }
                });
            }
        });

        wroupService.setClientDisconnectedListener(new ClientDisconnectedListener() {
            @Override
            public void onClientDisconnected(WroupDevice wroupDevice) {
                Toast.makeText(AdminDashBoard.this, wroupDevice.getDeviceName() + " has disconnected", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indicator.setVisibility(View.GONE);
                        textView.setText(R.string.gone);
                        stopBtn.setVisibility(View.GONE);
                    }
                });
            }
        });


        ProgressDialog progressDialog = new ProgressDialog(getApplicationContext());

        progressDialog.setMessage("Submitting details");
        progressDialog.setCancelable(false);

        wroupService.setDataReceivedListener(new DataReceivedListener() {
            @Override
            public void onDataReceived(MessageWrapper message) {

                String msg = message.getMessage();
                String[] items = msg.split(":");

                String studentName = items[1];
                String studentId = items[0];
                String dorm = items[2];
                String course = items[3];

                // Start Progress


                // Initialise server submission
                Service service = new Service();
                NetActions actions = service.get();

                Call<Status> statusCall;
                AuthPref authPref = new AuthPref(getApplicationContext());
                String staffType = authPref.getStaffType();
                String staffName = authPref.getUserName();
                int staffId = authPref.getUserId();
                int studentIdInt = Integer.parseInt(studentId);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifBuilder.setContentText("Information received successfully from " +studentName + "of course: " + course + " and hostel " + dorm);
                        notificationManager.notify(1, notifBuilder.build());
                    }
                });

                if(staffType.equalsIgnoreCase("TEACHER")) {
                    statusCall = actions.authAttend(studentIdInt, staffId);
                } else if(staffType.equalsIgnoreCase("TAMAM")) {
                    statusCall = actions.authDorm(studentIdInt, staffId);
                } else{
                    statusCall = actions.authCanteen(studentIdInt, staffId);
                }

               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Toast.makeText(AdminDashBoard.this, "Submitting information to the server", Toast.LENGTH_SHORT).show();
                   }
               });

                statusCall.enqueue(new Callback<Status>() {
                    @Override
                    public void onResponse(Call<Status> call, Response<Status> response) {

                        runOnUiThread(progressDialog::dismiss);

                        MessageWrapper messageWrapper = new MessageWrapper();
                        messageWrapper.setMessage("You have been successfully authorised/checked by " + staffName);
                        messageWrapper.setMessageType(MessageWrapper.MessageType.NORMAL);
                        wroupService.sendMessageToAllClients(messageWrapper);
                    }

                    @Override
                    public void onFailure(Call<Status> call, Throwable t) {

                        runOnUiThread(progressDialog::dismiss);

                        Toast.makeText(AdminDashBoard.this, "Network error. Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });


                runOnUiThread(() -> {
                    indicator.setVisibility(View.GONE);
                    stopBtn.setVisibility(View.VISIBLE);
                    textView.setText(R.string.scan);
                });

            }
        });
        stopBtn.setOnClickListener(v -> {
            wroupService.disconnect();
            indicator.setVisibility(View.GONE);
            stopBtn.setVisibility(View.GONE);
            textView.setText(R.string.attend);

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

    /**
     * Called to provide a {@link NdefMessage} to push.
     *
     * <p>This callback is usually made on a binder thread (not the UI thread).
     *
     * <p>Called when this device is in range of another device
     * that might support NDEF push. It allows the application to
     * create the NDEF message only when it is required.
     *
     * <p>NDEF push cannot occur until this method returns, so do not
     * block for too long.
     *
     * <p>The Android operating system will usually show a system UI
     * on top of your activity during this time, so do not try to request
     * input from the user to complete the callback, or provide custom NDEF
     * push UI. The user probably will not see it.
     *
     * @param event {@link NfcEvent} with the {@link NfcEvent#nfcAdapter} field set
     * @return NDEF message to push, or null to not provide a message
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        return null;
    }

    public void Logout(View view) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Log out");
        alert.setMessage("Are you sure you want to log out?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                startActivity(new Intent(AdminDashBoard.this, Splash.class));
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