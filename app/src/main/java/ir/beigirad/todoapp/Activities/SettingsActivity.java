package ir.beigirad.todoapp.Activities;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ir.beigirad.todoapp.MyApplication;
import ir.beigirad.todoapp.Utils.Constants;
import ir.beigirad.todoapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity{

    MyApplication app;
    @Override
    protected void onResume() {
        super.onResume();
        app.send(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        app = (MyApplication)getApplication();
        if(MyApplication.getPref().getTheme().equals(Constants.Theme.LightTheme)){
            setTheme(R.style.CustomStyle_LightTheme);
        }
        else{
            setTheme(R.style.CustomStyle_DarkTheme);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        if(backArrow!=null){
            backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }

        FragmentManager fm= getFragmentManager();
        fm.beginTransaction().replace(R.id.mycontent, new SettingsFragment()).commit();
    }

}
