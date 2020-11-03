package student.app.ui.register;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import student.app.R;
import student.app.models.Staff;
import student.app.prefs.AuthPref;
import student.app.ui.UserGroupActivity;
import student.app.ui.dashboard.Home;

public class StaffRegister extends AppCompatActivity {
    EditText editTextName,
            editTextStaffID,
            editTextEmail,
            editTextHostel,
            edittextPassword;
    String type;
    String Label = "UserGroup";
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    DocumentReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_register);
        editTextName = findViewById(R.id.editTextName);
        editTextStaffID = findViewById(R.id.editTextStaffID);
        editTextEmail = findViewById(R.id.editTextCourse);
        editTextHostel = findViewById(R.id.editTextHostel);
        edittextPassword = findViewById(R.id.edittextPassword);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            startActivity(new Intent(StaffRegister.this, UserGroupActivity.class));
            finish();
        } else {
            type = bundle.getString(Label);
        }
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    private boolean isEmpty(EditText e) {
        e.setError("This can't be empty");
        return TextUtils.isEmpty(e.getText().toString());
    }

    public void staffSignup(View view) {
        if (
                isEmpty(editTextName) ||
                        isEmpty(editTextEmail) ||
                        isEmpty(editTextHostel) ||
                        isEmpty(edittextPassword) ||
                        isEmpty(editTextStaffID)
        ) return;
        final Staff staff = new Staff();
        staff.setHostel(editTextHostel.getText().toString());
        staff.setStaffID(Integer.parseInt(String.valueOf(editTextStaffID.getText().toString())));
        staff.setStaffType(type);
        staff.setStaffName(editTextName.getText().toString());
        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage("Submitting..");
        dlg.setCancelable(false);
        dlg.setMax(100);
        dlg.show();
        mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), edittextPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        dlg.setMessage("Almost there...");
                        dlg.setProgress(50);
                        FirebaseUser user = authResult.getUser();

                        // Running away from null safety errors
                        if (user != null) {
                            reference = mStore.collection(type).document(user.getUid());
                            reference.set(staff)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(StaffRegister.this, Home.class);
                                            intent.putExtra(Label, type);
                                            AuthPref authPref = new AuthPref(StaffRegister.this);
                                            authPref.setUserGroup(type);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dlg.setMessage("We tried our best, but it failed");
                                            dlg.setProgress(100);
                                            dlg.setMax(100);
                                            Toast.makeText(StaffRegister.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            dlg.setCancelable(true);
                                        }
                                    });
                        } else {
                            dlg.setMessage("We tried our best, but it failed");
                            dlg.setProgress(100);
                            dlg.setMax(100);
                            dlg.setCancelable(true);

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dlg.setMessage("Oops, it failed");
                        dlg.setCancelable(true);
                        dlg.setProgress(100);
                        dlg.setMax(100);
                        Toast.makeText(StaffRegister.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void staffLogin(View view) {
    }
}