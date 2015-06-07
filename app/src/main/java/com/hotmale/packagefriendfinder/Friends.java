package com.hotmale.packagefriendfinder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

/**
 * Created by savery on 6/3/15.
 * Same deal.
 */
public class Friends extends Fragment implements AsyncResponse {

    Database db;
    Button b;

//    SimpleCursorAdapter mAdapter;

    public void processFinish(String output) {}

    // XXX
    public void processFinish(ArrayList<String> output) {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        db = new Database(activity);
    }

    public View onCreateView(LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        LinearLayout ll = new LinearLayout(getActivity());
        b = new Button(getActivity());
        b.setText("Hi there");
        ll.addView(b);

        db.execute();

        // Issues here on getActivity. Why?
        mServiceIntent = new Intent(getActivity(), BackgroundService.class);
        mServiceIntent.setData(Uri.parse("unseen"));
        getActivity().startService(mServiceIntent);

        return ll;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db.delegate = this;
    }

    /*
    // We'll add this later
    public class FriendListLoader extends ListActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Create a progress bar to display while the list loads
            ProgressBar progressBar = new ProgressBar(this);
            progressBar.setLayoutParams(new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            progressBar.setIndeterminate(true);
            getListView().setEmptyView(progressBar);

            // Must add the progress bar to the root of the layout
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            root.addView(progressBar);

            // For the cursor adapter, specify which columns go into which views
            String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
            int[] toViews = {android.R.id.text1}; // The TextView in simple_list_item_1

            // Create an empty adapter we will use to display the loaded data.
            // We pass null for the cursor, then update it in onLoadFinished()
            mAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_1, null,
                    fromColumns, toViews, 0);
            setListAdapter(mAdapter);

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one.
            getLoaderManager().initLoader(0, null, this);
        }
    }
    */
}

