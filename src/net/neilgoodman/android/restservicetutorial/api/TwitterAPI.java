package net.neilgoodman.android.restservicetutorial.api;

import net.neilgoodman.android.restservicetutorial.service.RESTService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class TwitterAPI {
    private static String TAG = TwitterAPI.class.getName();
    
    public static void searchForAndroidTweets(Context context, ResultReceiver resultReceiver) {
        if (context == null || resultReceiver == null) {
            Log.e(TAG, "Either context or resultReceiver were not given.");
            return;
        }
        
        // We will explicitly call our Service since we probably want to keep it as a private
        // component in our app. You could do this with Intent actions as well, but you have
        // to make sure you define your intent filters correctly in your manifest.
        Intent intent = new Intent(context, RESTService.class);
        intent.setData(Uri.parse("http://search.twitter.com/search.json"));
        
        // Here we are going to place our REST call parameters. Note that
        // we could have just used Uri.Builder and appendQueryParameter()
        // here, but I wanted to illustrate how to use the Bundle params.
        Bundle params = new Bundle();
        params.putString("q", "android");
        
        intent.putExtra(RESTService.EXTRA_PARAMS, params);
        intent.putExtra(RESTService.EXTRA_RESULT_RECEIVER, resultReceiver);
        
        // Here we send our Intent to our RESTService.
        context.startService(intent);
    }
}
