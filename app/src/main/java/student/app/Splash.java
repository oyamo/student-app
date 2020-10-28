package student.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import student.app.ui.Dashboard;
import student.app.ui.Login;

public class Splash extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();

        Thread wait = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    if(firebaseAuth.getCurrentUser() == null){
                        startActivity(new Intent(Splash.this, Login.class));
                    }else{
                        startActivity(new Intent(Splash.this, Dashboard.class));
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