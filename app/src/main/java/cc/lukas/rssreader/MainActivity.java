package cc.lukas.rssreader;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

public class MainActivity extends Activity {

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private RssFeedDao rssFeedDao;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Establish database connection
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "rssreader-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        rssFeedDao = daoSession.getRssFeedDao();

        // Insert dummy data
        insertDummyData();

        if (savedInstanceState == null) {
            loadRssFeedListFragment(false);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(rssFeedReceiver,
                new IntentFilter(RssFeedListFragment.INTENT_RSSFEED));
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

    private void loadRssItemListFragment(long rssFeedId) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, RssItemListFragment.newInstance(rssFeedId))
                .commit();
        displayHomeAsUpEnabled(true);
    }

    private void loadAddRssFeedFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new AddRssFeedFragment())
                .commit();
        displayHomeAsUpEnabled(true);
    }

    private void displayHomeAsUpEnabled(boolean enabled) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(enabled);
        }
    }

    private void insertDummyData() {
        rssFeedDao.deleteAll();
        long feedId1 = rssFeedDao.insert(new RssFeed(null, "ORF News", "http://rss.orf.at/news.xml", new Date()));
        long feedId2 = rssFeedDao.insert(new RssFeed(null, "ORF Help", "http://rss.orf.at/help.xml", new Date()));
        long feedId3 = rssFeedDao.insert(new RssFeed(null, "ORF Debatten", "http://rss.orf.at/debatten.xml", new Date()));
        RssItem item1 = new RssItem(null, "Dummy 1", "http://dmy.at/", "Test", new Date(), feedId1);
        RssItem item2 = new RssItem(null, "Dummy 2", "http://dmy.at/", "Test", new Date(), feedId1);
        RssItem item3 = new RssItem(null, "Dummy 3", "http://dmy.at/", "Test", new Date(), feedId1);
        RssItem item4 = new RssItem(null, "Dummy 4", "http://dmy.at/", "Test", new Date(), feedId2);
        RssItem item5 = new RssItem(null, "Dummy 5", "http://dmy.at/", "Test", new Date(), feedId2);
        RssItem item6 = new RssItem(null, "Dummy 6", "http://dmy.at/", "Test", new Date(), feedId3);
        daoSession.insert(item1);
        daoSession.insert(item2);
        daoSession.insert(item3);
        daoSession.insert(item4);
        daoSession.insert(item5);
        daoSession.insert(item6);
    }
}
