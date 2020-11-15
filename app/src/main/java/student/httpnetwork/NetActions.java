package student.httpnetwork;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import student.httpnetwork.models.Staff;
import student.httpnetwork.models.Status;
import student.httpnetwork.models.Student;

public interface NetActions {
    @GET("/api/users/student/details")
    Call<Student> getStudent(@Query("studentEmail") String email);

    @GET("/api/users/staff/details")
    Call<Staff> getStaff(@Query("staffEmail") String email);

    @GET("/api/auth/dorm/{studentId}/{staffId}")
    Call<Status> authDorm(@Path("studentId") int studentId, @Path("staffId") int staffId);

    @GET("/api/auth/attend/{studentId}/{staffId}")
    Call<Status> authAttend(@Path("studentId") int studentId, @Path("staffId") int staffId);

    @GET("/api/auth/canteen/{studentId}/{staffId}")
    Call<Status> authCanteen(@Path("studentId") int studentId, @Path("staffId") int staffId);


}
