package student.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import student.app.prefs.AuthPref;
import student.app.ui.AgreementActivity;
import student.app.ui.UserGroupActivity;
import timber.log.Timber;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AuthPref authPref = new AuthPref(this);
        Thread wait = new Thread(() -> {
            try {
                Thread.sleep(2500);
                Timber.d("Starting");
                if(!authPref.isLoggedIn()){
                    startActivity(new Intent(Splash.this, UserGroupActivity.class));
                }else{
                    startActivity(new Intent(Splash.this, AgreementActivity.class));
                }
                finish();
            }catch (Exception e){

            }
        });
        wait.start();
        try {
            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if( wifiManager != null){
                wifiManager.setWifiEnabled(true);
            }
        }catch (Exception e){
            Timber.d(e);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}