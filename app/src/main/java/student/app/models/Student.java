package student.app.models;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

public class Student extends HashMap<String , Object> {
    int studentID;
    String studentName;
    String hostel;
    int building;
    int roomNo;
    int phoneNo;
    String emirates;
    String course;
    String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        put("emailAddress", emailAddress);
        this.emailAddress = emailAddress;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        put("studentID", studentID);
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        put("studentName", studentName);
        this.studentName = studentName;
    }

    public String getHostel() {
        return hostel;
    }

    public void setHostel(String hostel) {
        put("hostel", hostel);
        this.hostel = hostel;
    }

    public int getBuilding() {
        return building;
    }

    public void setBuilding(int building) {
        put("building", building);
        this.building = building;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(int roomNo) {
        put("roomNo", roomNo);
        this.roomNo = roomNo;
    }

    public int getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(int phoneNo) {
        put("phoneNo",phoneNo);
        this.phoneNo = phoneNo;
    }

    public String getEmirates() {
        return emirates;
    }

    public void setEmirates(String emirates) {
        put("emirates", emirates);
        this.emirates = emirates;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        put("course", course);
        this.course = course;
    }

    public static Student studentFromSnap(DocumentSnapshot documentSnapshot){
        Student student = new Student();
        student.setBuilding(Integer.parseInt(String.valueOf(documentSnapshot.get("building"))));
        student.setPhoneNo(Integer.parseInt(String.valueOf(documentSnapshot.get("phoneNo"))));
        student.setCourse((String) documentSnapshot.get("course"));
        student.setEmailAddress((String) documentSnapshot.get("emailAddress"));
        student.setHostel((String) documentSnapshot.get("hostel"));
        student.setStudentName((String) documentSnapshot.get("studentName"));
        student.setRoomNo(Integer.parseInt(String.valueOf(documentSnapshot.get("roomNo"))));
        student.setStudentID(Integer.parseInt(String.valueOf(documentSnapshot.get("studentID"))));
        return student;
    }

    public Student() {
        super();
    }
}
