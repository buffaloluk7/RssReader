package cc.lukas.rssreader;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cc.lukas.rssreader.dummy.RssFeeds;
import cc.lukas.rssreader.parser.RssFeed;

/**
 * A fragment representing a list of Items.
 */
public class RssFeedListFragment extends ListFragment {

    private static final String ARG_FEED_ID = "feedId";

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RssFeedListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> rssTitles = new ArrayList<String>();
        for (RssFeed feed : RssFeeds.FEEDS) {
            rssTitles.add(feed.getTitle());
        }

        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, rssTitles);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rssfeed, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent("rssfeed-opened");
        //intent.putExtra(ARG_FEED_ID, RssFeeds.FEEDS.get(position).id);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

}
