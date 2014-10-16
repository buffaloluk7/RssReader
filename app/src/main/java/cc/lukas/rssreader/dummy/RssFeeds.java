package cc.lukas.rssreader.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.lukas.rssreader.parser.RssFeed;

public class RssFeeds {

    public static List<RssFeed> FEEDS = new ArrayList<RssFeed>();
    public static Map<Integer, RssFeed> FEED_MAP = new HashMap<Integer, RssFeed>();

    static {
        // Add 3 sample items.
        addItem(1, new RssFeed("ORF News", "http://rss.orf.at/news.xml", null));
        addItem(2, new RssFeed("ORF Help", "http://rss.orf.at/help.xml", null));
        addItem(3, new RssFeed("ORF Debatten", "http://rss.orf.at/debatten.xml", null));
    }

    private static void addItem(int id, RssFeed item) {
        FEEDS.add(item);
        FEED_MAP.put(id, item);
    }

}