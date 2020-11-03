package student.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import student.app.ui.dashboard.Home;
import student.app.ui.UserGroupActivity;

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
                        startActivity(new Intent(Splash.this, UserGroupActivity.class));
                    }else{
                        startActivity(new Intent(Splash.this, Home.class));
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