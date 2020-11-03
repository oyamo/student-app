package student.app.ui.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import student.app.models.Attendance;
import student.app.models.Canteen;
import student.app.prefs.AuthPref;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CanteenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CanteenFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button scan;
    TextView na;
    TextView tv;
    EditText editText;
    ProgressBar pb;
    public CanteenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentFreagment.
     */
    // TODO: Rename and change types and number of parameters
    public static CanteenFragment newInstance(String param1, String param2) {
        CanteenFragment fragment = new CanteenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_canteen, container, false);
        scan = v.findViewById(R.id.scanBtn);
        editText = v.findViewById(R.id.studentIDInput);
        tv = v.findViewById(R.id.studentIdLabel);
        na = v.findViewById(R.id.notAllowed);

        pb = v.findViewById(R.id.progress);
        scan.setOnClickListener(this);

        AuthPref authPref = new AuthPref(this.getContext());
        String userGroup = authPref.getUserGroup();
        if(userGroup.equalsIgnoreCase("Cafeteria")){
            editText.setHint("Enter a Student ID");
            scan.setText("Scan");
            tv.setText("Enter Student ID to scan");
        }else if(userGroup.equalsIgnoreCase("Students")){
            editText.setHint("Your student ID");
            scan.setText("Scan");
            tv.setText("Confirm Student ID");
        }else{
            editText.setHint("Student ID");
            scan.setText("Scan");
            tv.setText("Enter a Student ID");
            scan.setEnabled(false);
            editText.setEnabled(false);
            na.setVisibility(View.VISIBLE);
        }
        return v;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int view = v.getId();
        if(pb.getVisibility() != View.VISIBLE){
            if (view == R.id.scanBtn){

                AuthPref authPref = new AuthPref(getContext());
                String userGroup = authPref.getUserGroup();
                final String studentID = editText.getText().toString();


                if(TextUtils.isEmpty(studentID)) return;
                pb.setVisibility(View.VISIBLE);
                final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                DocumentReference reference = firebaseFirestore.collection("Students").document(user.getUid());
                if(userGroup.equalsIgnoreCase("Students")){
                    final Canteen canteen = new Canteen() ;
                    reference.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.contains("studentID")){
                                        String studentId = String.valueOf(documentSnapshot.get("studentID"));
                                        if(studentID.equalsIgnoreCase(studentId)){
                                            int StudentID = Integer.parseInt(studentID);
                                            canteen.setHostel(String.valueOf(documentSnapshot.get("hostel")));
                                            canteen.setStudentID(StudentID);
                                            canteen.setStudentName(String.valueOf(documentSnapshot.get("studentName")));
                                            DocumentReference attendanceRef  =  firebaseFirestore.collection("Canteen").document(studentID);
                                            attendanceRef.set(canteen)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            pb.setVisibility(View.GONE);
                                                            editText.setText("");
                                                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pb.setVisibility(View.GONE);
                                                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }else{
                                            pb.setVisibility(View.GONE);
                                            Toast.makeText(getContext(), "Error: Invalid StudentID", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        pb.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Error encountered", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pb.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1220);
                                DocumentReference doc = firebaseFirestore.collection("Canteen").document(studentID);
                                doc.get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pb.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                pb.setVisibility(View.GONE);
                                                if(documentSnapshot.contains("studentId")){
                                                    String studentName = String.valueOf(documentSnapshot.get("studentName"));
                                                    editText.setText("");
                                                    Toast.makeText(getContext(), studentName + " successfully verified", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }
}