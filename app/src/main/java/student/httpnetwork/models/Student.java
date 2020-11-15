package student.httpnetwork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("studentId")
    @Expose
    int studentId;

    @SerializedName("studentName")
    @Expose
    String studentName;

    @SerializedName("hostelName")
    @Expose
    String hostelName;

    @SerializedName("studentBuilding")
    @Expose
    String studentBuilding;

    @SerializedName("studentPhoneNo")
    @Expose
    String studentPhoneNo;

    @SerializedName("emailAddress")
    @Expose
    String emailAddress;

    @SerializedName("_id")
    @Expose
    String _id;

    @SerializedName("course")
    @Expose
    String course;

    public Student() {

    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getHostelName() {
        return hostelName;
    }

    public void setHostelName(String hostelName) {
        this.hostelName = hostelName;
    }

    public String getStudentBuilding() {
        return studentBuilding;
    }

    public void setStudentBuilding(String studentBuilding) {
        this.studentBuilding = studentBuilding;
    }

    public String getStudentPhoneNo() {
        return studentPhoneNo;
    }

    public void setStudentPhoneNo(String studentPhoneNo) {
        this.studentPhoneNo = studentPhoneNo;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
