package cc.lukas.rssreader.services;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.lukas.rssreader.RssFeedContentProvider;
import cc.lukas.rssreader.RssFeedDao;
import cc.lukas.rssreader.RssItemDao;
import nl.matshofman.saxrssreader.RssFeedModel;
import nl.matshofman.saxrssreader.RssItemModel;
import nl.matshofman.saxrssreader.RssReader;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RssFeedDownloadService extends IntentService {
    private static final String ACTION_CREATE_FEED = "cc.lukas.rssreader.services.action.CREATE_FEED";
    private static final String ACTION_UPDATE_FEED = "cc.lukas.rssreader.services.action.UPDATE_FEED";

    private static final String EXTRA_URL = "cc.lukas.rssreader.services.extra.URL";
    private static final String EXTRA_FEED_ID = "cc.lukas.rssreader.services.extra.FEED_ID";

    public RssFeedDownloadService() {
        super("RssFeedDownloadService");
    }

    /**
     * Starts this service to perform action CREATE_FEED with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCreateFeed(Context context, String url) {
        Intent intent = new Intent(context, RssFeedDownloadService.class);
        intent.setAction(ACTION_CREATE_FEED);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action DownloadFeed with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateFeed(Context context, String url, long id) {
        Intent intent = new Intent(context, RssFeedDownloadService.class);
        intent.setAction(ACTION_UPDATE_FEED);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final URL url;
            final RssFeedModel rssFeedModel;

            // Parse URL and try downloading feed
            try {
                url = new URL(intent.getStringExtra(EXTRA_URL));
                rssFeedModel = RssReader.read(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(this, "URL invalid.", Toast.LENGTH_SHORT).show();
                return;
            } catch (SAXException e) {
                Toast.makeText(this, "Invalid XML.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            } catch (IOException e) {
                Toast.makeText(this, "An error occurred.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }

            // Either create RssFeed entry in database and insert feed items
            // or update feed items (check which ones are already inserted).
            if (ACTION_CREATE_FEED.equals(action)) {
                long feedId = createRssFeedEntry(rssFeedModel);
                createRssItemEntries(feedId, rssFeedModel.getRssItems());

                // Local broadcast necessary? --> ContentObserver gets informed about new content.
                //Intent answer = new Intent();
                //LocalBroadcastManager.getInstance(this).sendBroadcast(answer);
            } else if (ACTION_UPDATE_FEED.equals(action)) {
                long feedId = intent.getLongExtra(EXTRA_FEED_ID, 0);
                if (feedId < 1) {
                    return;
                }

                updateRssItemEntries(feedId, rssFeedModel.getRssItems());
            }
        }
    }

    // Convert RssFeedModel to ContentValues object and insert it in the database.
    private long createRssFeedEntry(RssFeedModel rssFeedModel) {
        ContentValues values = new ContentValues();
        values.put(RssFeedDao.Properties.Title.columnName, rssFeedModel.getTitle());
        values.put(RssFeedDao.Properties.Link.columnName, rssFeedModel.getTitle());
        values.put(RssFeedDao.Properties.UpdatedAt.columnName, new SimpleDateFormat().format(new Date()));

        // Insert rss feed.
        Uri uri = getContentResolver().insert(RssFeedContentProvider.CONTENT_URI, values);

        // Return new record id.
        return ContentUris.parseId(uri);
    }

    private int createRssItemEntries(long feedId, ArrayList<RssItemModel> rssItemModels) {
        List<ContentValues> list = convertRssItems(feedId, rssItemModels);

        // Convert list to array of ContentValues
        ContentValues[] values = new ContentValues[list.size()];
        values = list.toArray(values);

        // Bulk insert all feed items
        return getContentResolver().bulkInsert(RssFeedContentProvider.CONTENT_URI, values);
    }

    private void updateRssItemEntries(long feedId, ArrayList<RssItemModel> rssItems) {
        throw new UnsupportedOperationException("updateRssItemEntries not implemented yet.");
    }

    private List<ContentValues> convertRssItems(long feedId, List<RssItemModel> rssItemModels) {
        List<ContentValues> list = new ArrayList<ContentValues>(rssItemModels.size());

        // Map RssItemModels to RssItems
        for (RssItemModel rssItemModel : rssItemModels) {
            ContentValues values = new ContentValues();
            values.put(RssItemDao.Properties.Title.columnName, rssItemModel.getTitle());
            values.put(RssItemDao.Properties.Link.columnName, rssItemModel.getLink());
            values.put(RssItemDao.Properties.Description.columnName, rssItemModel.getDescription());
            values.put(RssItemDao.Properties.PubDate.columnName, new SimpleDateFormat().format(rssItemModel.getPubDate()));
            values.put(RssItemDao.Properties.FeedId.columnName, feedId);

            list.add(values);
        }

        return list;
    }
}
