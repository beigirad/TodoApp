package ir.beigirad.todoapp.CustomUI;

import android.content.res.Resources;

import ir.beigirad.todoapp.R;

/**
 * Created by avjindersinghsekhon on 9/21/15.
 */
public class PreferenceKeys {
    public static String night_mode_pref_key;

    public PreferenceKeys(Resources resources){
        night_mode_pref_key = resources.getString(R.string.night_mode_pref_key);
    }
}
