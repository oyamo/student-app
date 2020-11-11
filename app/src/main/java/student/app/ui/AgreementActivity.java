package student.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;

import student.app.R;
import student.app.Splash;
import student.app.prefs.AuthPref;
import student.app.ui.dashboard.Home;

public class AgreementActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    MaterialCheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        firebaseAuth = FirebaseAuth.getInstance();
        checkBox = findViewById(R.id.checkBox);
    }

    public void Agree(View view) {
        if(checkBox.isChecked()){
            AuthPref authPref = new AuthPref(this);
            String userGroup = authPref.getUserGroup();
            if(userGroup.equalsIgnoreCase("Students")){
                startActivity(new Intent(AgreementActivity.this, StudentAttentActivity.class));
            }else{
                startActivity(new Intent(AgreementActivity.this, AdminDashBoard.class));
            }
            finish();
        }else{
            Toast.makeText(this, "You have to consent", Toast.LENGTH_SHORT).show();
        }
    }
}