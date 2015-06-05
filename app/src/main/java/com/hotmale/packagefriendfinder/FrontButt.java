package com.hotmale.packagefriendfinder;

import android.app.ActionBar;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Created by savery on 6/3/15.
 * Same deal.
 */
public class FrontButt extends Fragment implements AsyncResponse {

    Button b;
    Database db = new Database();

    public void processFinish(String output) {
        b.setText(output);
    }

    public View onCreateView(LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        LinearLayout ll = new LinearLayout(getActivity());
        b = new Button(getActivity());
        b.setText("Hi there");
        ll.addView(b);

        db.execute();

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

