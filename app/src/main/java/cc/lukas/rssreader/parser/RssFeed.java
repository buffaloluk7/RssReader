package cc.lukas.rssreader.parser;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Lukas on 28.09.2014.
 * <p/>
 * Represents a RSS feed containing RSS items.
 */
public class RssFeed implements Parcelable {

    public static final Creator<RssFeed> CREATOR = new Creator<RssFeed>() {
        public RssFeed createFromParcel(Parcel parcel) {
            return new RssFeed(parcel);
        }

        public RssFeed[] newArray(int i) {
            return new RssFeed[i];
        }
    };

    private String title;
    private String link;
    private String description;
    private String language;
    private ArrayList<RssItem> rssItems;

    public RssFeed() {
        rssItems = new ArrayList<RssItem>();
    }

    public RssFeed(Parcel parcel) {
        Bundle data = parcel.readBundle();
        title = data.getString("title");
        link = data.getString("link");
        description = data.getString("description");
        language = data.getString("language");
        rssItems = data.getParcelableArrayList("rssItems");
    }

    public RssFeed(String title, String link, ArrayList<RssItem> rssItems) {
        this.title = title;
        this.link = link;
        this.rssItems = rssItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Bundle data = new Bundle();
        data.putString("title", title);
        data.putString("link", link);
        data.putString("description", description);
        data.putString("language", language);
        data.putParcelableArrayList("rssItems", rssItems);
        parcel.writeBundle(data);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ArrayList<RssItem> getRssItems() {
        return rssItems;
    }

    public void setRssItems(ArrayList<RssItem> rssItems) {
        this.rssItems = rssItems;
    }

    public void addRssItem(RssItem item) {
        rssItems.add(item);
    }

}