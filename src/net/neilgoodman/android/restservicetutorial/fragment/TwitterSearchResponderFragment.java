package net.neilgoodman.android.restservicetutorial.fragment;

import java.util.ArrayList;
import java.util.List;

import net.neilgoodman.android.restservicetutorial.RESTServiceActivity;
import net.neilgoodman.android.restservicetutorial.service.RESTService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class TwitterSearchResponderFragment extends RESTResponderFragment {
    private static String TAG = TwitterSearchResponderFragment.class.getName();
    
    // We cache our stored tweets here so that we can return right away
    // on multiple calls to setTweets() during the Activity lifecycle events (such
    // as when the user rotates their device). In a real application we would want
    // to cache this data in a more sophisticated way, probably using SQLite and
    // Content Providers, but for the demo and simple apps this will do.
    private List<String> mTweets;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // This gets called each time our Activity has finished creating itself.
        setTweets();
    }

    private void setTweets() {
        RESTServiceActivity activity = (RESTServiceActivity) getActivity();
        
        if (mTweets == null && activity != null) {
            // This is where we make our REST call to the service. We also pass in our ResultReceiver
            // defined in the RESTResponderFragment super class.
            
            // We will explicitly call our Service since we probably want to keep it as a private
            // component in our app. You could do this with Intent actions as well, but you have
            // to make sure you define your intent filters correctly in your manifest.
            Intent intent = new Intent(activity, RESTService.class);
            intent.setData(Uri.parse("http://search.twitter.com/search.json"));
            
            // Here we are going to place our REST call parameters. Note that
            // we could have just used Uri.Builder and appendQueryParameter()
            // here, but I wanted to illustrate how to use the Bundle params.
            Bundle params = new Bundle();
            params.putString("q", "android");
            
            intent.putExtra(RESTService.EXTRA_PARAMS, params);
            intent.putExtra(RESTService.EXTRA_RESULT_RECEIVER, getResultReceiver());
            
            // Here we send our Intent to our RESTService.
            activity.startService(intent);
        }
        else if (activity != null) {
            // Here we check to see if our activity is null or not.
            // We only want to update our views if our activity exists.
            
            ArrayAdapter<String> adapter = activity.getArrayAdapter();
            
            // Load our list adapter with our Tweets.
            adapter.clear();
            for (String tweet : mTweets) {
                adapter.add(tweet);
            }
        }
    }
    
    @Override
    public void onRESTResult(int code, String result) {
        // Here is where we handle our REST response. This is similar to the 
        // LoaderCallbacks<D>.onLoadFinished() call from the previous tutorial.
        
        Activity activity = getActivity();
        
        if (activity != null) {
            // Check to see if we got an HTTP 200 code and have some data.
            if (code == 200 && result != null) {
                
                // For really complicated JSON decoding I usually do my heavy lifting
                // with Gson and proper model classes, but for now let's keep it simple
                // and use a utility method that relies on some of the built in
                // JSON utilities on Android.
                mTweets = getTweetsFromJson(result);
                setTweets();
            }
            else {
                Toast.makeText(activity, "Failed to load Twitter data. Check your internet settings.", Toast.LENGTH_SHORT).show();
            }
        }
        
    }
    
    private static List<String> getTweetsFromJson(String json) {
        ArrayList<String> tweetList = new ArrayList<String>();
        
        try {
            JSONObject tweetsWrapper = (JSONObject) new JSONTokener(json).nextValue();
            JSONArray  tweets        = tweetsWrapper.getJSONArray("results");
            
            for (int i = 0; i < tweets.length(); i++) {
                JSONObject tweet = tweets.getJSONObject(i);
                tweetList.add(tweet.getString("text"));
            }
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON.", e);
        }
        
        return tweetList;
    }

}
