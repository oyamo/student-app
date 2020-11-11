package student.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.progressindicator.ProgressIndicator;

import student.app.R;

public class StudentAttentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attent);
    }

    public void Attend(View view) {
        ProgressIndicator indicator = findViewById(R.id.progress);
        TextView textView = findViewById(R.id.buttonText);
        indicator.setVisibility(View.VISIBLE);
        textView.setText(R.string.attending);

    }
}