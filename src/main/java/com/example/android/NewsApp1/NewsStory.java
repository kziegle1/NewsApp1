package com.example.android.NewsApp1;

import java.net.URL;
import java.util.Locale;

/**
 * An {@link NewsStory} object contains information related to a single news story.
 */
public class NewsStory {

    private String mTitle; //webTitle
    private String mDate; //webPublicationDate
    private String mUrl; //webUrl
    private String mCategory; //pillarName
    private String mAuthor;

    public NewsStory (String title, String author, String date, String url, String category) {
            mTitle = title;
            mAuthor = author;
            mDate = date;
            mUrl = url;
            mCategory = category;

    }


    /**
     * Returns the website URL to find more information about the newsstory.
     */
    public String getUrl() {
        return mUrl;
    }

    public String getTitle() {return mTitle; }

    public String getAuthor() {return mAuthor; }

    public String getDate() {return mDate; }

    public String getCategory() {return mCategory; }

}





