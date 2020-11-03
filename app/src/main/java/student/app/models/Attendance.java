package student.app.models;

public class Attendance {
    int studentId;
    String studentName;
    int courseCode;
    int dateTime;
    int secNo;

    public Attendance() {
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

    public int getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(int courseCode) {
        this.courseCode = courseCode;
    }

    public int getDateTime() {
        return dateTime;
    }

    public void setDateTime(int dateTime) {
        this.dateTime = dateTime;
    }

    public int getSecNo() {
        return secNo;
    }

    public void setSecNo(int secNo) {
        this.secNo = secNo;
    }
}
