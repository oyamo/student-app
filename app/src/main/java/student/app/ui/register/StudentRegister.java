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

import java.util.HashMap;

import student.app.R;
import student.app.models.Student;
import student.app.ui.Login;
import student.app.ui.dashboard.Dashboard;

public class StudentRegister extends AppCompatActivity {
    EditText studentName;
    EditText studentEmail;
    EditText hostel;
    EditText room;
    EditText phoneNumber;
    EditText password;
    EditText building;
    EditText studentID;
    EditText course;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    DocumentReference df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        studentName = findViewById(R.id.editTextName);
        studentEmail = findViewById(R.id.editTextCourse);
        hostel = findViewById(R.id.editTextHostel);
        room = findViewById(R.id.editTextRoom);
        password = findViewById(R.id.edittextPassword);
        building = findViewById(R.id.editTextBuilding);
        phoneNumber = findViewById(R.id.editTextPhone);
        studentID = findViewById(R.id.editTextStudentID);
        course = findViewById(R.id.editTextCourse);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
//        df = db.collection("Students").document(user.getUid());
    }


    public void logIn(View view) {
        startActivity(new Intent(StudentRegister.this, Login.class));
        finish();
    }

    boolean detectEmpty(EditText e) {
        boolean res = TextUtils.isEmpty(e.getText().toString());
        if (res) {
            String msg = e.getHint() + " cannot be empty";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            e.setError(msg);
        }
        return res;
    }

    String getText(EditText e) {
        return e.getText().toString();
    }

    public void signUp(View view) throws InstantiationException, IllegalAccessException {
        if (
                detectEmpty(studentName) ||
                detectEmpty(hostel) ||
                detectEmpty(password) ||
                detectEmpty(room) ||
                detectEmpty(phoneNumber) ||
                detectEmpty(studentEmail) ||
                detectEmpty(course) ||
                detectEmpty(studentID)
        ) return;


        Student student = new Student();
        student.setBuilding(Integer.parseInt(getText(building)));
        student.setHostel(getText(hostel));
        student.setEmailAddress(getText(studentEmail));
        student.setStudentID(Integer.parseInt(getText(studentID)));
        student.setCourse(getText(course));
        student.setRoomNo(Integer.parseInt(getText(room)));
        student.setStudentName(getText(studentName));
        student.setPhoneNo(Integer.parseInt(getText(phoneNumber)));
        final HashMap<String, Object> UserHash = student;

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Submitting");
        dialog.setCancelable(false);
        dialog.show();
        auth.createUserWithEmailAndPassword(student.getEmailAddress(), password.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        dialog.setMessage("Almost..");
                        user = authResult.getUser();

                        df = db.collection("Students").document(user.getUid());
                        df.set(UserHash)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(StudentRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                        Toast.makeText(StudentRegister.this, "Success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(StudentRegister.this, Dashboard.class));
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(StudentRegister.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}