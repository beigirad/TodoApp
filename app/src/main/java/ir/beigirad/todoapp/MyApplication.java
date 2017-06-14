package ir.beigirad.todoapp;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import io.realm.Realm;
import ir.beigirad.todoapp.Utils.SharedPrefUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class MyApplication extends Application {

    private static SharedPrefUtil mPrefUtil;
    private static Realm mRealm;
    private static Typeface defaultTypeFace;

    @Override
    public void onCreate() {
        Log.i("TODO INITED", "MY APPLICATION");
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mPrefUtil = new SharedPrefUtil(this);
        defaultTypeFace = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.ttf");


        Realm.init(this);
        mRealm = Realm.getDefaultInstance();


    }


    public static Realm getRealm() {
        if (mRealm.isClosed())
            mRealm = Realm.getDefaultInstance();
        return mRealm;
    }

    public static SharedPrefUtil getPref() {
        return mPrefUtil;
    }

    public static Typeface getDefaultTypeface() {
        return defaultTypeFace;
    }



    private static final boolean IS_ENABLED = true;
    private static final String EVENT_OPEN_ACTIVITY = "OpenActivity";
    private static final String EVENT_ACTION = "Action";

    private static final String TYPE_ACTIVITY = "Activity";


    private void send(String eventName, String itemId, String itemName, String contentType) {
        if (IS_ENABLED) {

            FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);

            analytics.logEvent(eventName, bundle);
        }
    }


    public void send(Context context) {
        String contextName = context.getClass().getSimpleName();
        send(EVENT_OPEN_ACTIVITY, "", contextName, TYPE_ACTIVITY);
    }


    public void send(String action, String label) {
        send(action,"", label,"");
    }


}
