package cc.lukas.rssreader.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.lukas.rssreader.DaoMaster;
import cc.lukas.rssreader.R;
import cc.lukas.rssreader.RssFeedContentProvider;
import cc.lukas.rssreader.RssFeedDao;
import cc.lukas.rssreader.RssItemContentProvider;
import cc.lukas.rssreader.RssItemDao;
import cc.lukas.rssreader.fragments.AddRssFeedFragment;
import cc.lukas.rssreader.fragments.RssFeedListFragment;
import cc.lukas.rssreader.fragments.RssItemListFragment;

public class MainActivity extends Activity {
    private BroadcastReceiver rssFeedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long rssFeedId = intent.getLongExtra(RssFeedListFragment.ARG_FEED_ID, 0);
            if (rssFeedId < 1) {
                return;
            }

            loadRssItemListFragment(rssFeedId);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // Register local broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(rssFeedReceiver,
                new IntentFilter(RssFeedListFragment.INTENT_RSSFEED));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister local broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(rssFeedReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Establish database connection and store it in content provider
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "rssreader-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);

        /* ATTENTION:
         * Rename generated EntityContentProvider to the following names:
         * RssFeedContentProvider, RssItemContentProvider
         * Adjust the AUTHORITY and BASE_PATH to the following:
         * AUTHORITY = "cc.lukas.rssreader.provider.rssfeed"; // or rssitem
         * BASE_PATH = "rssfeed"; // or rssitem
         * Add the following to the AndroidManifest.xml:
         * <provider
         *   android:name="cc.lukas.rssreader.RssFeedContentProvider" // or RssItemContentProvider
         *   android:authorities="cc.lukas.rssreader.provider.rssfeed" // or rssitem
         *   android:exported="false" />
         */
        RssFeedContentProvider.daoSession = daoMaster.newSession();
        RssItemContentProvider.daoSession = daoMaster.newSession();

        if (savedInstanceState == null) {
            // Insert dummy data
            insertDummyData();
            // Load RssFeed list fragment
            loadRssFeedListFragment(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // app icon in action bar clicked; goto parent activity.
            case android.R.id.home:
                loadRssFeedListFragment(true);
                return true;
            case R.id.action_add:
                loadAddRssFeedFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Load Fragment containing list of all rss feeds.
    private void loadRssFeedListFragment(boolean replace) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (replace) {
            transaction.replace(R.id.container, new RssFeedListFragment());
        } else {
            transaction.add(R.id.container, new RssFeedListFragment());
        }
        transaction.commit();
        displayHomeAsUpEnabled(false);
    }

    // Load Fragment containing list of all rss items of a given feed.
    private void loadRssItemListFragment(long rssFeedId) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, RssItemListFragment.newInstance(rssFeedId))
                .commit();
        displayHomeAsUpEnabled(true);
    }

    // Load Fragment allowing the user to enter a title and rss link.
    private void loadAddRssFeedFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new AddRssFeedFragment())
                .commit();
        displayHomeAsUpEnabled(true);
    }

    // Enable/Disable the going-back button.
    private void displayHomeAsUpEnabled(boolean enabled) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(enabled);
        }
    }

    // Create and insert some dummy data
    private void insertDummyData() {
        ContentResolver cr = getContentResolver();
        cr.delete(RssFeedContentProvider.CONTENT_URI, null, null);

        // Feed 1
        ContentValues feed1 = new ContentValues();
        feed1.put(RssFeedDao.Properties.Title.columnName, "ORF News");
        feed1.put(RssFeedDao.Properties.Link.columnName, "http://rss.orf.at/news.xml");
        feed1.put(RssFeedDao.Properties.UpdatedAt.columnName, new SimpleDateFormat().format(new Date()));
        Uri feedUri1 = cr.insert(RssFeedContentProvider.CONTENT_URI, feed1);
        // Feed 2
        ContentValues feed2 = new ContentValues();
        feed2.put(RssFeedDao.Properties.Title.columnName, "ORF Help");
        feed2.put(RssFeedDao.Properties.Link.columnName, "http://rss.orf.at/help.xml");
        feed2.put(RssFeedDao.Properties.UpdatedAt.columnName, new SimpleDateFormat().format(new Date()));
        Uri feedUri2 = cr.insert(RssFeedContentProvider.CONTENT_URI, feed2);
        // Feed 3
        ContentValues feed3 = new ContentValues();
        feed3.put(RssFeedDao.Properties.Title.columnName, "ORF Debatten");
        feed3.put(RssFeedDao.Properties.Link.columnName, "http://rss.orf.at/debatten.xml");
        feed3.put(RssFeedDao.Properties.UpdatedAt.columnName, new SimpleDateFormat().format(new Date()));
        Uri feedUri3 = cr.insert(RssFeedContentProvider.CONTENT_URI, feed3);

        // Items for feed #1
        ContentValues item1 = new ContentValues();
        item1.put(RssItemDao.Properties.Title.columnName, "Dummy 1");
        item1.put(RssItemDao.Properties.Link.columnName, "http://dmy.at/");
        item1.put(RssItemDao.Properties.Description.columnName, "Test");
        item1.put(RssItemDao.Properties.PubDate.columnName, new SimpleDateFormat().format(new Date()));
        item1.put(RssItemDao.Properties.FeedId.columnName, ContentUris.parseId(feedUri1));
        ContentValues item2 = new ContentValues();
        item2.put(RssItemDao.Properties.Title.columnName, "Dummy 2");
        item2.put(RssItemDao.Properties.Link.columnName, "http://dmy.at/");
        item2.put(RssItemDao.Properties.Description.columnName, "Test");
        item2.put(RssItemDao.Properties.PubDate.columnName, new SimpleDateFormat().format(new Date()));
        item2.put(RssItemDao.Properties.FeedId.columnName, ContentUris.parseId(feedUri1));
        ContentValues item3 = new ContentValues();
        item3.put(RssItemDao.Properties.Title.columnName, "Dummy 3");
        item3.put(RssItemDao.Properties.Link.columnName, "http://dmy.at/");
        item3.put(RssItemDao.Properties.Description.columnName, "Test");
        item3.put(RssItemDao.Properties.PubDate.columnName, new SimpleDateFormat().format(new Date()));
        item3.put(RssItemDao.Properties.FeedId.columnName, ContentUris.parseId(feedUri1));
        // Items for feed #2
        ContentValues item4 = new ContentValues();
        item4.put(RssItemDao.Properties.Title.columnName, "Dummy 4");
        item4.put(RssItemDao.Properties.Link.columnName, "http://dmy.at/");
        item4.put(RssItemDao.Properties.Description.columnName, "Test");
        item4.put(RssItemDao.Properties.PubDate.columnName, new SimpleDateFormat().format(new Date()));
        item4.put(RssItemDao.Properties.FeedId.columnName, ContentUris.parseId(feedUri2));
        ContentValues item5 = new ContentValues();
        item5.put(RssItemDao.Properties.Title.columnName, "Dummy 5");
        item5.put(RssItemDao.Properties.Link.columnName, "http://dmy.at/");
        item5.put(RssItemDao.Properties.Description.columnName, "Test");
        item5.put(RssItemDao.Properties.PubDate.columnName, new SimpleDateFormat().format(new Date()));
        item5.put(RssItemDao.Properties.FeedId.columnName, ContentUris.parseId(feedUri2));
        // Items for feed #3
        ContentValues item6 = new ContentValues();
        item6.put(RssItemDao.Properties.Title.columnName, "Dummy 6");
        item6.put(RssItemDao.Properties.Link.columnName, "http://dmy.at/");
        item6.put(RssItemDao.Properties.Description.columnName, "Test");
        item6.put(RssItemDao.Properties.PubDate.columnName, new SimpleDateFormat().format(new Date()));
        item6.put(RssItemDao.Properties.FeedId.columnName, ContentUris.parseId(feedUri3));

        // Insert all items at once
        cr.bulkInsert(RssItemContentProvider.CONTENT_URI,
                new ContentValues[]{item1, item2, item3, item4, item5, item6});
    }
}
