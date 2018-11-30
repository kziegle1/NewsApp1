package com.example.android.NewsApp1;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsStoryActivity extends AppCompatActivity
        implements LoaderCallbacks<List<NewsStory>>,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = NewsStoryActivity.class.getSimpleName();

    private static final String URL_API = "https://content.guardianapis.com/search";
    //https://content.guardianapis.com/search?show-tags=contributor&q=driverless%20cars&api-key=91574d5c-1798-4125-b7d7-b80c7351862e

    private static final int LOADER_ID = 1;

    // Adapter for the list of articles
    private NewsStoryAdapter mAdapter;

    // Text view that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    // Swipe Refresh Layout
    //SwipeRefreshLayout mySwipeRefreshLayout;
    //SharedPreferences sharedPrefs;

    // URL for request API data
    private static final String requestUrl = "https://content.guardianapis.com/search";

    private static final String QUERY_SECTION_TAG = "section";
    private static final String QUERY_DATE_TAG = "webPublicationDate";
    private static final String QUERY_CONTRIBUTOR_TAG = "show-tags";
    private static final String CONTRIBUTOR_VALUE = "contributor";
    private static final String QUERY_API_KEY_TAG = "api-key";
    private static final String API_KEY_VALUE = "91574d5c-1798-4125-b7d7-b80c7351862e";


    //private static final String ACTIVITY_NOT_FOUND_EXCEPTION = "ActivityNotFoundException";

    //Tag for LOG message
    //private static final String LOG_TAG = QueryUtils.class.getSimpleName();


    ListView newsListView;

    //ConstraintLayout homeScreen;
    //RelativeLayout emptyLayout;
    //RelativeLayout noNewsLayout;
    //RelativeLayout noNetworkLayout;
    //RelativeLayout loadingLayout;
    //LinearLayout filtersDisplay;
    TextView filteredStartDate;
    TextView filteredEndDate;
    TextView filteredOrderBy;
    Boolean correctDateFormat;
    String startDate;
    String endDate;
    String submitDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_story_activity);

        //Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.news_list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_textview);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news stories as input
        mAdapter = new NewsStoryAdapter(this, new ArrayList<NewsStory>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);


        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected newsStory.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                NewsStory currentNewsStory = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNewsStory.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.news_loading_progress);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_network_available);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_order_by_most_recent_label)) ||
                key.equals(getString(R.string.settings_order_by_key))) {
            // Clear the ListView as a new query will be kicked off
            mAdapter.clear();

            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyStateTextView.setVisibility(View.GONE);

            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.news_loading_progress);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart the loader to requery the USGS as the query settings have been updated
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<NewsStory>> onCreateLoader(int n, Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String pageSize = "20";
        String section = sharedPreferences.getString(
                getString(R.string.tag_list_key),
                getString(R.string.tag_list_default_value));

        String date = sharedPreferences.getString(
                getString(R.string.date_list_key),
                getString(R.string.date_list_default_value));

        String category = sharedPreferences.getString(getString(R.string.settings_category_key),
                getString(R.string.settings_category_default));


            Uri uri = Uri.parse(requestUrl);
            Uri.Builder builder = uri.buildUpon();

            //builder.appendQueryParameter("from-date", startDate);
            //builder.appendQueryParameter("to-date", endDate);
            builder.appendQueryParameter("q", category);
            builder.appendQueryParameter("pageSize", "20");
            builder.appendQueryParameter(QUERY_SECTION_TAG, section);
            builder.appendQueryParameter(QUERY_CONTRIBUTOR_TAG, CONTRIBUTOR_VALUE);
            builder.appendQueryParameter(QUERY_DATE_TAG, date);
            builder.appendQueryParameter(QUERY_API_KEY_TAG, API_KEY_VALUE);

            //execute NewsStoryLoader;
            return new NewsStoryLoader(this, builder.toString());

        }

        @Override
        public void onLoadFinished(Loader<List<NewsStory>> loader, List<NewsStory> newsStories) {
            // Hide loading indicator because the data has been loaded
            View loadingIndicator = findViewById(R.id.news_loading_progress);
            loadingIndicator.setVisibility(View.GONE);

            // Set empty state text to display "No new stories found."
            mEmptyStateTextView.setText(R.string.no_news_available);

            // Clear the adapter of previous news story data
            //mAdapter.clear();

            // If there is a valid list of {@link NewStories}, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (newsStories != null && !newsStories.isEmpty()) {
                mAdapter.addAll(newsStories);
                //updateUi(newsStories);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<NewsStory>>loader) {
            // Loader reset, so we can clear out our existing data.
            mAdapter.clear();
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    private void assignFilterValues(String startDate, String endDate, String category) {
        filteredStartDate.setText(startDate);
        filteredEndDate.setText(endDate);
        filteredOrderBy.setText(category);
    }


}



