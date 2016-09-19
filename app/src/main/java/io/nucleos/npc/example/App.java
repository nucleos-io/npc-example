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

import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageUtils;
import com.layer.atlas.util.Util;
import com.layer.atlas.util.picasso.requesthandlers.MessagePartRequestHandler;
import com.layer.sdk.LayerClient;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import io.nucleos.nuclearpore.backend.manager.NuclearPore;
import io.nucleos.nuclearpore.util.AuthenticationProvider;

/**
 * Created by msalcedo on 18/09/16.
 */
public class App extends Application {


    private static final String TAG = App.class.getSimpleName();

    private static Context context;
    private static Picasso sPicasso;


    @Override
    public void onCreate() {
        super.onCreate();

        NuclearPore.init(
                "https://persona-mono.herokuapp.com",
                "layer:///apps/staging/6b2a0708-6b35-11e6-9adb-181c7d0c1206",
                "580818924250",
                getLayerOption()

        );

        // Enable verbose logging in debug builds
        if (BuildConfig.DEBUG) {
            com.layer.atlas.util.Log.setLoggingEnabled(true);
            LayerClient.setLoggingEnabled(this, true);
        }

        // Allow the LayerClient to track app state
        LayerClient.applicationCreated(this);

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


    //==============================================================================================
    // Getters / Setters
    //==============================================================================================


    public static LayerClient.Options getLayerOption() {
        // Custom options for constructing a LayerClient
        LayerClient.Options options = new LayerClient.Options()

                    /* Fetch the minimum amount per conversation when first authenticated */
                .historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.FROM_LAST_MESSAGE)

                    /* Automatically download text and ThreePartImage info/preview */
                .autoDownloadMimeTypes(Arrays.asList(
                        TextCellFactory.MIME_TYPE,
                        ThreePartImageUtils.MIME_TYPE_INFO,
                        ThreePartImageUtils.MIME_TYPE_PREVIEW));
        return options;
    }

    public static Picasso getPicasso(Context context, LayerClient layerClient) {
        if (sPicasso == null) {
            // Picasso with custom RequestHandler for loading from Layer MessageParts.
            sPicasso = new Picasso.Builder(context)
                    .addRequestHandler(new MessagePartRequestHandler(layerClient))
                    .build();
        }
        return sPicasso;
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
