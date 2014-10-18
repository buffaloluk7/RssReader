package cc.lukas.rssreader.fragments;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.SimpleCursorAdapter;

import cc.lukas.rssreader.R;
import cc.lukas.rssreader.RssFeedDao;
import cc.lukas.rssreader.RssItemContentProvider;
import cc.lukas.rssreader.RssItemDao;

/**
 * A fragment representing a list of Items.
 */
public class RssItemListFragment extends ListFragment {

    private static final String ARG_FEED_ID = "feedId";

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SimpleCursorAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RssItemListFragment() {
    }

    public static RssItemListFragment newInstance(long feedId) {
        RssItemListFragment fragment = new RssItemListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_FEED_ID, feedId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get feed id from arguments
        long feedId = getArguments().getLong(ARG_FEED_ID);

        // Fetch rss feed items
        Cursor cursor = getActivity()
                .getContentResolver()
                .query(RssItemContentProvider.CONTENT_URI,
                        new String[]{RssItemDao.Properties.Id.columnName,
                                RssItemDao.Properties.Title.columnName},
                        RssItemDao.Properties.FeedId.columnName + " = ?",
                        new String[]{String.valueOf(feedId)},
                        null);
        // Set up cursor adapter
        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{RssFeedDao.Properties.Title.columnName},
                new int[]{android.R.id.text1},
                0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rssfeed, container, false);

        // Set the adapter
        AbsListView mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(adapter);

        return view;
    }

}
