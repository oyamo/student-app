package student.app.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import student.app.R;
import student.app.Splash;
import student.app.livedata.StudentData;
import student.app.models.Student;
import student.app.ui.Pager;

public class Home extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    private StudentData studentData;
    FirebaseAuth auth;
    FirebaseFirestore db;
    TabLayout tl;
    MaterialToolbar tb;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        studentData = new ViewModelProvider(this).get(StudentData.class);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tl = findViewById(R.id.tabLayout);
        tb = findViewById(R.id.toolBar);
        viewPager = findViewById(R.id.viewPager);
        setSupportActionBar(tb);
        Pager adapter = new Pager(getSupportFragmentManager(), 3);
        viewPager.setAdapter(adapter);
        tl.setupWithViewPager(viewPager);
        tl.setOnTabSelectedListener(this);

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
                            Toast.makeText(Home.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void logOut(View view) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Log out");
        alertDialog.setMessage("Are sure you want to log out?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                startActivity(new Intent(Home.this, Splash.class));
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

    /**
     * Called when a tab enters the selected state.
     *
     * @param tab The tab that was selected
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * Called when a tab exits the selected state.
     *
     * @param tab The tab that was unselected
     */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    /**
     * Called when a tab that is already selected is chosen again by the user. Some applications may
     * use this action to return to the top level of a category.
     *
     * @param tab The tab that was reselected.
     */
    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}