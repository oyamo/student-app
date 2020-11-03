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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import student.app.R;
import student.app.prefs.AuthPref;
import student.app.ui.dashboard.Home;
import student.app.ui.register.StaffRegister;
import student.app.ui.register.StudentRegister;

public class Login extends AppCompatActivity {
    FirebaseAuth auth;
    EditText email;
    EditText password;
    String Label = "UserGroup";
    Bundle bundle;
    String userGroup;
    FirebaseFirestore mStore;
    FirebaseAuth mAuth;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editTextCourse);
        password = findViewById(R.id.edittextPassword);
        bundle = getIntent().getExtras();
        if(bundle == null){
            startActivity(new Intent(Login.this, UserGroupActivity.class));
        }
        userGroup = bundle.getString(Label, "");
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        Toast.makeText(this, String.format("You have selected a %s user group", userGroup), Toast.LENGTH_SHORT).show();
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
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        authTask.addOnSuccessListener(Login.this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                progressDialog.setMessage("Almost, hold on");
                progressDialog.setProgress(50);
                progressDialog.setIndeterminate(false);
                FirebaseUser u = authResult.getUser();
                if(u!=null){
                    documentReference = mStore.collection(userGroup).document(u.getUid());
                    documentReference
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.getData() != null){
                                        AuthPref authPref = new AuthPref(Login.this);
                                        authPref.setUserGroup(userGroup);
                                        startActivity(new Intent(Login.this, Home.class));
                                        finish();
                                    }else{
                                        progressDialog.setMessage("We swear we did our best, but you cant get in, you have no permissions. Tap outside to close");
                                        progressDialog.setProgress(100);
                                        progressDialog.setCancelable(true);
                                        mAuth.signOut();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.setMessage("We swear we did our best, you can't get in. Tap outside to close");
                                    progressDialog.setProgress(100);
                                    progressDialog.setCancelable(true);
                                }
                            });
                }else{
                    progressDialog.setMessage("We swear we did our best, but the login failed. Tap outside to close");
                    progressDialog.setProgress(100);
                    progressDialog.setMax(100);
                    progressDialog.setCancelable(true);
                }

            }
        });

        authTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.setMessage("We swear we did our best, but the login failed. Tap outside to close");
                progressDialog.setProgress(100);
                progressDialog.setMax(100);
                progressDialog.setCancelable(true);
                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signUp(View view) {
        Intent intent = new Intent(Login.this, StudentRegister.class);
        if(!userGroup.equalsIgnoreCase("Students")){
            intent =  new Intent(Login.this, StaffRegister.class);
            intent.putExtra(Label, userGroup);
        }
        startActivity(intent);
        finish();
    }
}