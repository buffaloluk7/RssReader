package cc.lukas.rssreader.services;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

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
import cc.lukas.rssreader.RssItemContentProvider;
import cc.lukas.rssreader.RssItemDao;
import nl.matshofman.saxrssreader.RssFeedModel;
import nl.matshofman.saxrssreader.RssItemModel;
import nl.matshofman.saxrssreader.RssReader;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RssFeedService extends IntentService {
    public static final String ACTION_CREATE_FEED = "cc.lukas.rssreader.services.action.CREATE_FEED";
    public static final String ACTION_UPDATE_FEED = "cc.lukas.rssreader.services.action.UPDATE_FEED";

    public static final String EXTRA_URL = "cc.lukas.rssreader.services.extra.URL";
    public static final String EXTRA_TITLE = "cc.lukas.rssreader.services.extra.TITLE";
    public static final String EXTRA_FEED_ID = "cc.lukas.rssreader.services.extra.FEED_ID";
    public static final String EXTRA_ERROR_CODE = "cc.lukas.rssreader.services.extra.ERROR_CODE";

    public RssFeedService() {
        super("RssFeedDownloadService");
    }

    /**
     * Starts this service to perform action CREATE_FEED with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCreateFeed(Context context, String title, String link) {
        Intent intent = new Intent(context, RssFeedService.class);
        intent.setAction(ACTION_CREATE_FEED);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, link);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action DownloadFeed with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateFeed(Context context, String url, long id) {
        Intent intent = new Intent(context, RssFeedService.class);
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
            final Intent response = new Intent(action);

            // Parse URL and try downloading feed
            try {
                url = new URL(intent.getStringExtra(EXTRA_URL));
                rssFeedModel = RssReader.read(url);
            } catch (MalformedURLException e) {
                response.putExtra(EXTRA_ERROR_CODE, 0);
                sendLocalBroadCast(response);
                return;
            } catch (SAXException e) {
                response.putExtra(EXTRA_ERROR_CODE, 1);
                sendLocalBroadCast(response);
                return;
            } catch (IOException e) {
                response.putExtra(EXTRA_ERROR_CODE, 2);
                sendLocalBroadCast(response);
                return;
            }

            // Either create RssFeed entry in database and insert feed items
            // or update feed items (check which ones are already inserted).
            if (ACTION_CREATE_FEED.equals(action)) {
                long feedId = createRssFeedEntry(rssFeedModel, intent.getStringExtra(EXTRA_TITLE));
                createRssItemEntries(feedId, rssFeedModel.getRssItems());

                // Send local broadcast containing the new feed id.
                response.putExtra(EXTRA_FEED_ID, feedId);
                sendLocalBroadCast(response);
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
    private long createRssFeedEntry(RssFeedModel rssFeedModel, String title) {
        title = TextUtils.isEmpty(title) ? rssFeedModel.getTitle() : title;

        ContentValues values = new ContentValues();
        values.put(RssFeedDao.Properties.Title.columnName, title);
        values.put(RssFeedDao.Properties.Link.columnName, rssFeedModel.getLink());
        values.put(RssFeedDao.Properties.UpdatedAt.columnName, parseDate(new Date()));

        // Insert rss feed.
        Uri uri = getContentResolver().insert(RssFeedContentProvider.CONTENT_URI, values);

        // Return new record id.
        return ContentUris.parseId(uri);
    }

    private int createRssItemEntries(long feedId, ArrayList<RssItemModel> rssItemModels) {
        List<ContentValues> list = convertRssItems(feedId, rssItemModels);

        // Convert list to array of ContentValues.
        ContentValues[] values = new ContentValues[list.size()];
        values = list.toArray(values);

        // Bulk insert all feed items.
        return getContentResolver().bulkInsert(RssItemContentProvider.CONTENT_URI, values);
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
            values.put(RssItemDao.Properties.PubDate.columnName, parseDate(rssItemModel.getPubDate()));
            values.put(RssItemDao.Properties.FeedId.columnName, feedId);

            list.add(values);
        }

        return list;
    }

    // Convert Date to String
    private String parseDate(Date date) {
        if (date == null) {
            date = new Date();
        }

        return new SimpleDateFormat().format(date);
    }

    private void sendLocalBroadCast(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
