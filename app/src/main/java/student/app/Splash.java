package student.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import student.app.ui.AgreementActivity;
import student.app.ui.dashboard.Home;
import student.app.ui.UserGroupActivity;

public class Splash extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Thread wait = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    Log.d("Starting","Starting");
                    if(user == null){
                        startActivity(new Intent(Splash.this, UserGroupActivity.class));
                    }else{
                        startActivity(new Intent(Splash.this, AgreementActivity.class));
                    }
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