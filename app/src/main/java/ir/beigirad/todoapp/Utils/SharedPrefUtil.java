package ir.beigirad.todoapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import ir.beigirad.todoapp.Utils.Constants;


/**
 * Created by farhad-mbp on 6/12/17.
 */

public class SharedPrefUtil {

    private static final String THEME_SAVED = "savedtheme";
    private static final String DARKTHEME = "darktheme";
    private static final String LIGHTTHEME = "lighttheme";

    private static final String RECREATE_ACTIVITY = "recreate_activity";
    private static final String CHANGED_ITEMS = "changed_items";
    private static final String FIRST_OPEN = "first_open";


    private SharedPreferences mPreferences;
    private Context mContext;
    private SharedPreferences.Editor editor;

    public SharedPrefUtil(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    public void setTheme(Constants.Theme theme) {
        if (theme == Constants.Theme.DarkTheme)
            editor.putString(THEME_SAVED, DARKTHEME);
        else
            editor.putString(THEME_SAVED, LIGHTTHEME);

        editor.apply();
    }

    public Constants.Theme getTheme() {
        if (mPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTTHEME))
            return Constants.Theme.LightTheme;
        else
            return Constants.Theme.DarkTheme;
    }

    public boolean getRecreate() {
        return mPreferences.getBoolean(RECREATE_ACTIVITY, false);
    }

    public void setRecreate(boolean recreate) {
        editor.putBoolean(RECREATE_ACTIVITY, recreate);
        editor.apply();
    }

    public boolean getChange() {
        return mPreferences.getBoolean(CHANGED_ITEMS, false);
    }

    public void setChange(boolean changed) {
        editor.putBoolean(CHANGED_ITEMS, changed);
        editor.apply();
    }


    public boolean getFirstOpen() {
        return mPreferences.getBoolean(FIRST_OPEN, true);
    }

    public void setFirstOprn(boolean first) {
        editor.putBoolean(FIRST_OPEN, first);
        editor.apply();
    }
}
