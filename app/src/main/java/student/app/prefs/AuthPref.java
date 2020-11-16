package student.app.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthPref {
    private SharedPreferences sharedPreferences;
    private final String fileName = "AuthDetails";
    private final String ugLabel = "Usergroup";
    public AuthPref(Context ctx){
        sharedPreferences = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public void setUserGroup(String usergroup){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(ugLabel, usergroup);
        edit.apply();
    }

    public String getUserGroup() {
        return sharedPreferences.getString(ugLabel,null);
    }

    public int getUserId() {
        return sharedPreferences.getInt("userId", -1);
    }

    public String getUserName() {
        return sharedPreferences.getString("userName", null);
    }

    public String getCourse(){
        return sharedPreferences.getString("course", null);
    }


    public String getHostel() {
        return sharedPreferences.getString("hostelName", null);
    }

    public String getStaffType() {
        return sharedPreferences.getString("staffType", null);
    }

    public void setUserId(int userId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId",userId);
        editor.apply();
    }

    public void setUserName(String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName",userName);
        editor.apply();
    }

    public void setCourse(String course) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("course",course);
        editor.apply();
    }

    public void setHostel(String hostel) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("hostelName",hostel);
        editor.apply();
    }

    public void setStaffType(String staffType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("staffType",staffType);
        editor.apply();
    }

    public boolean isGroupSet() {
        return sharedPreferences.contains(ugLabel);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("loggedIn", false);
    }

    public void setLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", loggedIn);
        editor.apply();
    }
}
