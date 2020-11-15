package student.app.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import student.app.R;
import student.app.prefs.AuthPref;
import student.httpnetwork.NetActions;
import student.httpnetwork.Service;
import student.httpnetwork.models.Staff;
import student.httpnetwork.models.Student;
import timber.log.Timber;


public class Login extends AppCompatActivity {
    FirebaseAuth auth;
    EditText email;
    EditText password;
    String Label = "UserGroup";
    Bundle bundle;
    String userGroup;
    FirebaseFirestore mStore;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editTextCourse);
        password = findViewById(R.id.edittextPassword);
        bundle = getIntent().getExtras();
        if (bundle == null) {
            startActivity(new Intent(Login.this, UserGroupActivity.class));
        }
        userGroup = bundle.getString(Label, "");
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        Toast.makeText(this, String.format("You have selected a %s user group", userGroup), Toast.LENGTH_SHORT).show();
    }

    public void logIn(View view) {

        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Email can't be empty");
            return;
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            email.setError("Password can't be empty");
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying..");

        Task<AuthResult> authTask = auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString());
        progressDialog.show();
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);

        String emailAddress = email.getText().toString().trim();

        authTask.addOnSuccessListener(Login.this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                progressDialog.setMessage("Almost, hold on");
                progressDialog.setProgress(50);
                progressDialog.setIndeterminate(false);
                FirebaseUser u = authResult.getUser();
                if (u != null) {
                    Service service = new Service();
                    NetActions actions = service.get();
                    if (userGroup.equalsIgnoreCase("Students")) {
                        Call<Student> studentCall = actions.getStudent(emailAddress);
                        studentCall.enqueue(new Callback<Student>() {
                            @Override
                            public void onResponse(@NotNull Call<Student> call, @NotNull Response<Student> response) {
                                Student student = response.body();

                                if (response.isSuccessful() && student != null && student.get_id() != null) {
                                    AuthPref authPref = new AuthPref(Login.this);
                                    authPref.setUserGroup(userGroup);
                                    authPref.setUserId(student.getStudentId());
                                    authPref.setCourse(student.getCourse());
                                    authPref.setUserName(student.getStudentName());
                                    authPref.setHostel(student.getHostelName());
                                    progressDialog.dismiss();

                                    startActivity(new Intent(Login.this, AgreementActivity.class));
                                    finish();
                                }else {
                                    email.setError("Are the credentials correct?");
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<Student> call, @NotNull Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, "Could not submit credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Call<Staff> staffCall = actions.getStaff(emailAddress);
                        staffCall.enqueue(new Callback<Staff>() {
                            @Override
                            public void onResponse(Call<Staff> call, Response<Staff> response) {
                                Staff staff = response.body();
                                if(staff != null && response.isSuccessful()){
                                    AuthPref authPref = new AuthPref(Login.this);
                                    authPref.setUserGroup(userGroup);
                                    authPref.setUserId(staff.getStaffId());
                                    authPref.setUserName(staff.getStaffName());
                                    authPref.setStaffType(staff.getStaffType());
                                    progressDialog.dismiss();
                                    startActivity(new Intent(Login.this, AgreementActivity.class));
                                    finish();
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(Login.this, "Could not submit credentials", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Staff> call, Throwable t) {
                                Toast.makeText(Login.this, "Could not submit credentials", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                } else {
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

}