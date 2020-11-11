package student.app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.progressindicator.ProgressIndicator;

import student.app.R;

public class AdminDashBoard extends AppCompatActivity implements  NfcAdapter.CreateNdefMessageCallback{
    NfcAdapter nfcAdapter;
    NfcManager nfcManager;
    final  String TAG = "AdminDashBoard";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.NFC) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.NFC}, 30);
            }
        }
    }

    // Initialise the NFC adapter
    void initAdapter(){
        nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Activity a = this;

        if (nfcAdapter != null) {
            Log.w(TAG, "NFC available. Setting Beam Push URI callback");
            nfcAdapter.setNdefPushMessageCallback(this, a);
        } else {
            Log.w(TAG, "NFC is not available");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // The onclick handler for handling NFC
    public void Attend(View view) {
        String channelId = "efaee";
        initAdapter();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "NFC running",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, channelId);
        notifBuilder.setContentTitle("One Touch NFC");
        notifBuilder.setContentText("Scanning a student. Please wait, this will take some time.");
        notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notifBuilder.setSmallIcon(R.drawable.ic_baseline_nfc_24);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, notifBuilder.build());

        ProgressIndicator indicator = findViewById(R.id.progress);
        TextView textView = findViewById(R.id.buttonText);
        indicator.setVisibility(View.VISIBLE);
        textView.setText(R.string.scanning);

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
}