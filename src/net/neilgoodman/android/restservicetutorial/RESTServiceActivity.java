package net.neilgoodman.android.restservicetutorial;

import net.neilgoodman.android.restservicetutorial.fragment.TwitterSearchResponderFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

public class RESTServiceActivity extends FragmentActivity {
    
    private ArrayAdapter<String> mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_service);
        
        mAdapter = new ArrayAdapter<String>(this, R.layout.item_label_list);
        
        FragmentManager     fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        
        // Since we are using the Android Compatibility library
        // we have to use FragmentActivity. So, we use ListFragment
        // to get the same functionality as ListActivity.
        ListFragment list = new ListFragment();
        ft.add(R.id.fragment_content, list);
        
        // Let's set our list adapter to a simple ArrayAdapter.
        list.setListAdapter(mAdapter);
        
        // RESTResponderFragments call setRetainedInstance(true) in their onCreate() method. So that means
        // we need to check if our FragmentManager is already storing an instance of the responder.
        TwitterSearchResponderFragment responder = (TwitterSearchResponderFragment) fm.findFragmentByTag("RESTResponder");
        if (responder == null) {
            responder = new TwitterSearchResponderFragment();
            
            // We add the fragment using a Tag since it has no views. It will make the Twitter REST call
            // for us each time this Activity is created.
            ft.add(responder, "RESTResponder");
        }

        // Make sure you commit the FragmentTransaction or your fragments
        // won't get added to your FragmentManager. Forgetting to call ft.commit()
        // is a really common mistake when starting out with Fragments.
        ft.commit();
    }

    public ArrayAdapter<String> getArrayAdapter() {
        return mAdapter;
    }
}
