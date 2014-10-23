package cc.lukas.rssreader.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import java.lang.ref.WeakReference;

import cc.lukas.rssreader.R;
import cc.lukas.rssreader.RssFeedContentProvider;
import cc.lukas.rssreader.RssFeedDao;
import cc.lukas.rssreader.adapter.RssFeedCursorAdapter;
import cc.lukas.rssreader.listener.RssFeedMultiChoiceModeListener;

/**
 * A fragment representing a list of Items.
 */
public class RssFeedListFragment extends ListFragment {
    public static final String ACTION_OPEN_FEED = "cc.lukas.action.OPEN_FEED";
    public static final String EXTRA_FEED_ID = "cc.lukas.extra.FEED_ID";

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private CursorAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RssFeedListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fetch rss feeds.
        Cursor cursor = getActivity()
                .getContentResolver()
                .query(RssFeedContentProvider.CONTENT_URI,
                        new String[]{RssFeedDao.Properties.Id.columnName,
                                RssFeedDao.Properties.Title.columnName},
                        null, null, null);
        // Set up the cursor adapter.
        adapter = new RssFeedCursorAdapter(getActivity(), cursor, true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rssfeed_list, container, false);

        // Set the adapter, choice mode and choice mode listener.
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(
                new RssFeedMultiChoiceModeListener(
                        new WeakReference<Activity>(getActivity()), listView, getResources()));

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Create intent containing the feed id.
        Intent intent = new Intent(ACTION_OPEN_FEED);
        intent.putExtra(EXTRA_FEED_ID, id);

        // Send local broadcast to main activity which navigates to the feed items.
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }
}