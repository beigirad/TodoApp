package ir.beigirad.todoapp.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;

import ir.beigirad.todoapp.MyApplication;
import ir.beigirad.todoapp.Utils.Constants;
import ir.beigirad.todoapp.CustomUI.PreferenceKeys;
import ir.beigirad.todoapp.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    MyApplication app;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_layout);
        app = (MyApplication) getActivity().getApplication();
   }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PreferenceKeys preferenceKeys = new PreferenceKeys(getResources());
        if(key.equals(preferenceKeys.night_mode_pref_key)){

            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)findPreference(preferenceKeys.night_mode_pref_key);

            app.getPref().setRecreate(true);

            if(checkBoxPreference.isChecked()){
                app.send("Settings", "Night Mode used");

                app.getPref().setTheme(Constants.Theme.DarkTheme);
            }
            else{
                app.getPref().setTheme(Constants.Theme.LightTheme);
            }

            getActivity().recreate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
