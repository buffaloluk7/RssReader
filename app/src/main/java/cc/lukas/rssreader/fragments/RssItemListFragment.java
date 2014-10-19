package cc.lukas.rssreader.fragments;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import cc.lukas.rssreader.R;
import cc.lukas.rssreader.RssItemContentProvider;
import cc.lukas.rssreader.RssItemDao;
import cc.lukas.rssreader.listener.RssItemMultiChoiceModeListener;

/**
 * A fragment representing a list of Items.
 */
public class RssItemListFragment extends ListFragment {
    public static final String EXTRA_FEED_ID = "cc.lukas.extra.FEED_ID";
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
        args.putLong(EXTRA_FEED_ID, feedId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get feed id from arguments.
        long feedId = getArguments().getLong(EXTRA_FEED_ID);

        // Fetch rss feed items.
        Cursor cursor = getActivity()
                .getContentResolver()
                .query(RssItemContentProvider.CONTENT_URI,
                        new String[]{RssItemDao.Properties.Id.columnName,
                                RssItemDao.Properties.Title.columnName},
                        RssItemDao.Properties.FeedId.columnName + " = ?",
                        new String[]{String.valueOf(feedId)},
                        null);
        // Set up cursor adapter.
        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                cursor,
                new String[]{RssItemDao.Properties.Title.columnName},
                new int[]{android.R.id.text1},
                0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rssitem_list, container, false);

        // Set the adapter, choice mode and choice mode listener
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new RssItemMultiChoiceModeListener(getActivity(), listView, getResources()));

        return view;
    }
}