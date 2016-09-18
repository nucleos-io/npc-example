package io.nucleos.npc.example;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import io.nucleos.nuclearpore.backend.manager.NuclearPore;

/**
 * Created by msalcedo on 18/09/16.
 */
public class App extends Application {



    private static final String TAG = App.class.getSimpleName();

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        NuclearPore.init(
                "https://persona-mono.herokuapp.com",
                "layer:///apps/staging/6b2a0708-6b35-11e6-9adb-181c7d0c1206",
                "580818924250"
        );

        NuclearPore.setTest(false);

    }

    public static Context getAppContext() {
        return App.context;
    }

    public static Uri ResourceToUri (Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    public static void closeKeyboard(Activity activity) {
        if (activity != null) {
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

    }

    public static String getVersionName(Context context) {
        try {
            return (context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static boolean isAppInForeground(Context context) {
        Log.d(TAG, "Checking app");

        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

            if (appProcesses != null && appProcesses.size() > 0) {
                for (ActivityManager.RunningAppProcessInfo aux : appProcesses) {
                    if (aux.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                            && aux.processName.contains("io.nucleos.spark")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
