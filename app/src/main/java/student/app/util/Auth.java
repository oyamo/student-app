package student.app.util;

import com.google.firebase.auth.FirebaseAuth;

public class Auth {
    public static enum TYPE{
        Student,
        Teacher,
        DormStaff,
        CafeteriaStaff
    }

    private TYPE type;
    private Object payload;
    private String username;
    private String password;

    private FirebaseAuth mAuth;
    public Auth(String username, String password){
        this.username = username;
        this.password = password;
        mAuth = FirebaseAuth.getInstance();
    }

    public Auth(){}

    public Auth(Object object){
        this.payload = object;
    }


}
