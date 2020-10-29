package student.app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import student.app.R;
import student.app.Splash;
import student.app.livedata.StudentData;
import student.app.models.Student;

public class StudentDashboard extends AppCompatActivity {
    private StudentData studentData;
    FirebaseAuth auth;
    FirebaseFirestore db;
    TextView progressText, fullNames, course, studentId, studentEmail, studentHostel, roomNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        studentData = new ViewModelProvider(this).get(StudentData.class);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressText = findViewById(R.id.progressText);
        fullNames = findViewById(R.id.fullNames);
        course = findViewById(R.id.courseName);
        studentId = findViewById(R.id.studentId);
        studentEmail = findViewById(R.id.studentEmail);
        roomNo = findViewById(R.id.roomNo);
        studentHostel = findViewById(R.id.studentHostel);


        final Observer<Student> observer = new Observer<Student>() {
            /**
             * Called when the data is changed.
             * @param student The new data
             */
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(Student student) {
                progressText.setVisibility(View.GONE);
                fullNames.setText(student.getStudentName());
                studentEmail.setText(html(String.format("%s %s", "<b>Email:</b>", student.getEmailAddress())));
                studentId.setText(html(String.format("%s %d", "<b>Student ID:</b>", student.getStudentID())));
                roomNo.setText(html(String.format("%s %d", "<b>Room No:</b>", student.getRoomNo())));
                course.setText(student.getCourse());
                studentHostel.setText(html(String.format("%s %s", "<b>Hostel:</b>", student.getHostel())));
            }

        };


        studentData.getStudent().observe(this, observer);
        fetch();
    }
    Spanned html(String ht){
        return HtmlCompat.fromHtml(ht, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }
    private void fetch(){
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null) {
            DocumentReference reference = db.collection("Students").document(currentUser.getUid());
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        Student student = Student.studentFromSnap(documentSnapshot);
                        studentData.getStudent().setValue(student);
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(StudentDashboard.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void logOut(View view) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentDashboard.this);
        alertDialog.setTitle("Log out");
        alertDialog.setMessage("Are sure you want to log out?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                startActivity(new Intent(StudentDashboard.this, Splash.class));
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();

    }
}