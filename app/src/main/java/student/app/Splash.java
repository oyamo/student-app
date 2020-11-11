package student.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import student.app.ui.AgreementActivity;
import student.app.ui.dashboard.Home;
import student.app.ui.UserGroupActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Thread wait = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    startActivity(new Intent(Splash.this, UserGroupActivity.class));
                    finish();
                }catch (Exception e){

                }
            }
        });
        wait.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}