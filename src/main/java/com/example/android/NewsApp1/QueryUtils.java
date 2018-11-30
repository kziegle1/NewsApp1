package com.example.android.NewsApp1;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public final class QueryUtils {
    //Tag for LOG message
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final int READ_TIMEOUT = 10000;

    private static final int CONNECT_TIMEOUT = 15000;

    private static final String KEY_TITLE = "webTitle";

    private static final String KEY_URL = "webUrl";

    private static final String KEY_PUBLICATION_DATE = "webPublicationDate";

    private static final String KEY_AUTHOR_NAME = "authorName";

    private static final String KEY_CATEGORY = "pillarName";

    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of NewsStory objects.
     */
    public static List<NewsStory> fetchNewsStoryData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", ioe);
        }
        // Extract relevant fields from the JSON response and create a list of NewsStories
        List<NewsStory> newsStories = extractNewsFromJson(jsonResponse);

        // Return the list of NewsStories
        return newsStories;
    }
    /**
     * Returns new URL object from the given String URL.
     */
    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        // Create the connection
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //HttpURLConnection class actually has the HTTP_OK code of 200 defined as a public static variable,
                // you can use that directly instead of specifying 200 here :smile: It's always a good idea
                // to use constants that are defined for you already, especially in this case since you have already
                // imported HttpURLConnection anyways!
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the newsStory JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();

    }

    /**
     * Return a list of NewsStory objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<NewsStory> extractNewsFromJson(String newsStoryJSON) {
// If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsStoryJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news stories to
        List<NewsStory> newsStories = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsStoryJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of news stories.
            JSONArray currentNewsArray = baseJsonResponse.getJSONArray("results");

            // For each newsStory in the currentNewsArray, create an {@link NewsStory} object
            for (int n = 0; n < currentNewsArray.length(); n++) {

                // Get a single newsStory at position n within the list of news stories
                JSONObject currentStory = currentNewsArray.getJSONObject(n);

                //Create the JSONObject with the key "response"
            JSONObject response = currentStory.getJSONObject("response");



            // Extract the value for the key called "webTitle"
                String title = response.getString(KEY_TITLE);

                // Extract the value for the key called
                String source = response.getString("source-type");

                //Extract the value for the key called "pillarName" -- Article Category
                String category = response.getString(KEY_CATEGORY);

                // Extract the value for the key called "url"
                String url = response.getString(KEY_URL);

                // Extract the value for the key called "webPublicationDate"
                String date = response.getString(KEY_PUBLICATION_DATE);

                String author = response.getString(KEY_AUTHOR_NAME);

                //Extract the JSONArray with the key "tag"
                JSONArray authorArray = currentStory.getJSONArray("tags");

                JSONObject currentAuthor = authorArray.getJSONObject(0);

                String authorName = currentAuthor.getString(KEY_AUTHOR_NAME);
                //Concatenation of author name and type of author (pulled from JSON)
                StringBuilder authorBuilder = new StringBuilder();
                authorBuilder.append(authorName);
                //Check for 2nd author
                if (authorArray.length() > 1 ){
                    JSONObject secondaryAuthor = authorArray.getJSONObject(1);
                    String secondAuthor = secondaryAuthor.getString("webTitle");
                    authorBuilder.append(" & ");
                    authorBuilder.append(secondAuthor);
                }
                author = authorBuilder.toString();

                NewsStory newsStory = new NewsStory (title, source, category, url, author );
                //Add each record to the ArrayList
                newsStories.add(newsStory);
            }
        } catch (JSONException je) {
            Log.e(LOG_TAG, "Problem extracting JSON results ", je);
        }
        return newsStories;
    }
}
