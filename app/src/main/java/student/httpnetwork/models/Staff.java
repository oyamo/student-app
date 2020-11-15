package student.httpnetwork.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Staff {
    @SerializedName("staffId")
    @Expose
    int staffId;

    @SerializedName("staffName")
    @Expose
    String staffName;

    @SerializedName("staffEmail")
    @Expose
    String staffEmail;

    @SerializedName("staffType")
    @Expose
    String staffType;

    @SerializedName("_id")
    @Expose
    String _id;

    public Staff() {
    }

    public int getStaffId() {
        return staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public String getStaffType() {
        return staffType;
    }

    public String get_id() {
        return _id;
    }
}
