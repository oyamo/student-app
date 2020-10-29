package student.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import student.app.R;

public class UserGroupActivity extends AppCompatActivity {
    Intent nextActivity;
    String Label = "UserGroup";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_group);
        nextActivity = new Intent(UserGroupActivity.this, Login.class);
    }

    public void selectStudent(View view) {
        nextActivity.putExtra(Label, "Students");
        startActivity(nextActivity);
        finish();
    }


    public void selectTeacher(View view) {
        nextActivity.putExtra(Label, "Teachers");
        startActivity(nextActivity);
        finish();
    }

    public void selectDormUG(View view) {
        nextActivity.putExtra(Label, "Dorms");
        startActivity(nextActivity);
        finish();
    }

    public void selectCafeteria(View view) {
        nextActivity.putExtra(Label, "Cafeteria");
        startActivity(nextActivity);
        finish();
    }
}