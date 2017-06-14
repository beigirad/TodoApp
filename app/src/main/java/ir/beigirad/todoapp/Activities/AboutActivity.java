package ir.beigirad.todoapp.Activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import ir.beigirad.todoapp.MyApplication;
import ir.beigirad.todoapp.Utils.Constants;
import ir.beigirad.todoapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AboutActivity extends AppCompatActivity {
    private TextView mVersionTextView;
    private String appVersion = "0.1";
    private Toolbar toolbar;
    private TextView contactMe;
    private TextView contactTelegram;
    private MyApplication app;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = (MyApplication)getApplication();
        app.send(this);

        if(MyApplication.getPref().getTheme().equals(Constants.Theme.DarkTheme)){
            setTheme(R.style.CustomStyle_DarkTheme);
        }
        else{
            setTheme(R.style.CustomStyle_LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);


        final Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        if(backArrow!=null){
            backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        }
        try{
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = info.versionName;
        }
        catch (Exception e){
            e.printStackTrace();
        }


        mVersionTextView = (TextView)findViewById(R.id.aboutVersionTextView);
        mVersionTextView.setText(String.format(getResources().getString(R.string.app_version), appVersion));
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        contactMe = (TextView)findViewById(R.id.aboutContactMe);
        contactTelegram = (TextView)findViewById(R.id.aboutContactMeByTelegram);


        contactMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.send("Contact", "Mail Contact");
            }
        });

        contactTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.send("Contact","Telegram Contact");
            }
        });

        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }
    }

}
