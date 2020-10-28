package student.app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import student.app.R;

public class Login extends AppCompatActivity {
    FirebaseAuth auth;
    EditText email;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.edittextPassword);

    }

    public void logIn(View view) {

        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Email can't be empty");
            return;
        }

        if(TextUtils.isEmpty(password.getText().toString())){
            email.setError("Password can't be empty");
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying..");

        Task<AuthResult> authTask =  auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString());
        progressDialog.show();
        progressDialog.setCancelable(false);
        authTask.addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Intent intent = new Intent(Login.this, Dashboard.class);
                    startActivity(intent);
                }
            }
        });

        authTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signUp(View view) {
        startActivity(new Intent(Login.this, Register.class));
        finish();
    }
}