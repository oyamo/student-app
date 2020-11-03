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

    public boolean isGroupSet() {
        return (sharedPreferences.contains(ugLabel));
    }
}
