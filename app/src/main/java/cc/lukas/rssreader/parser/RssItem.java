package cc.lukas.rssreader.parser;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Lukas on 28.09.2014.
 * <p/>
 * Represents a RSS item.
 */
public class RssItem implements Parcelable, Comparable<RssItem> {

    public static final Creator<RssItem> CREATOR = new Creator<RssItem>() {
        public RssItem createFromParcel(Parcel parcel) {
            return new RssItem(parcel);
        }

        public RssItem[] newArray(int i) {
            return new RssItem[i];
        }
    };
    private RssFeed feed;
    private String title;
    private String link;
    private Date pubDate;
    private String description;
    private String content;

    public RssItem() {
    }

    public RssItem(Parcel parcel) {
        Bundle data = parcel.readBundle();
        title = data.getString("title");
        link = data.getString("link");
        pubDate = (Date) data.getSerializable("pubDate");
        description = data.getString("description");
        content = data.getString("content");
        feed = data.getParcelable("feed");
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
        data.putSerializable("pubDate", pubDate);
        data.putString("description", description);
        data.putString("content", content);
        data.putParcelable("feed", feed);
        parcel.writeBundle(data);
    }

    @Override
    public int compareTo(RssItem another) {
        if (getPubDate() != null && another.getPubDate() != null) {
            return getPubDate().compareTo(another.getPubDate());
        } else {
            return 0;
        }
    }

    public RssFeed getFeed() {
        return feed;
    }

    public void setFeed(RssFeed feed) {
        this.feed = feed;
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

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            this.pubDate = dateFormat.parse(pubDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}