package student.app.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import student.app.models.Student;

public class StudentData extends ViewModel {
    private MutableLiveData<Student> studentDat;

    public StudentData() {
        this.studentDat = new MutableLiveData<>();
    }

    public MutableLiveData<Student> getStudent(){
        if(studentDat == null){
            return new MutableLiveData<>();
        }else {
            return studentDat;
        }
    }
}
