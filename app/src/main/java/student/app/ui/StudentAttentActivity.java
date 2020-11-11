package student.app.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.progressindicator.ProgressIndicator;

import student.app.R;

public class StudentAttentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attent);
    }

    public void Attend(View view) {
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
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, channelId);
        notifBuilder.setContentTitle("One Touch NFC");
        notifBuilder.setContentText("Student attendance is currently in session. It might take some time.");
        notifBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notifBuilder.setSmallIcon(R.drawable.ic_baseline_nfc_24);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, notifBuilder.build());

        ProgressIndicator indicator = findViewById(R.id.progress);
        TextView textView = findViewById(R.id.buttonText);
        indicator.setVisibility(View.VISIBLE);
        textView.setText(R.string.attending);

    }
}