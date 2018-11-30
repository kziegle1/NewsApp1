package com.example.android.NewsApp1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * An {@link NewsStoryAdapter} knows how to create a list item layout for each news story
 * in the data source (a list of {@link NewsStory} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class NewsStoryAdapter extends ArrayAdapter<NewsStory> {
//public class NewsStoryAdapter extends RecyclerView.Adapter<NewsStoryAdapter.NewsHolder> {
    //private LayoutInflater layoutInflater;
    //private List<NewsStory> NewsList;

    /**
     * @param context     of the app
     * @param newsStories is the list of newsStories, which is the data source of the adapter
     */

    public NewsStoryAdapter(@NonNull Context context, List<NewsStory> newsStories) {
        super(context, 0, newsStories);

    }

    /**
     * Returns a list item view that displays information about the news story at the given position
     * in the list of stories.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_story_list_item, parent, false);
        }
        // Find the earthquake at the given position in the list of earthquakes
        NewsStory currentNewsStory = getItem(position);

        // Create a new author object from the time in milliseconds of the earthquake
        //Author authorObject = new Author(currentNewsStory.getAuthor());


        // Create a new Date object from the time in milliseconds of the earthquake
        Date dateObject = new Date(currentNewsStory.getDate());

        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date_textview);
        // Format the date string (i.e. "Mar 3, 1984")
        String formattedDate = formatDate(dateObject);
        // Display the date of the current earthquake in that TextView
        dateView.setText(formattedDate);


        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

}

