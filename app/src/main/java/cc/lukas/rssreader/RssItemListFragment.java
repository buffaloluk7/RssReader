package cc.lukas.rssreader;

import android.app.ListFragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import cc.lukas.rssreader.parser.RssItem;

/**
 * A fragment representing a list of Items.
 */
public class RssItemListFragment extends ListFragment {

    private static final String ARG_FEED_ID = "feedId";
    private int feedId;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RssItemListFragment() {
    }

    // TODO: Rename and change types of parameters
    public static RssItemListFragment newInstance(int feedId) {
        RssItemListFragment fragment = new RssItemListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FEED_ID, feedId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RssItem rssItem = new RssItem();
        rssItem.setTitle("test-titel");
        rssItem.setLink("http://test.com");

        List<RssItem> rssItems = new ArrayList<RssItem>(1);
        rssItems.add(rssItem);

        setListAdapter(new ArrayAdapter<RssItem>(getActivity(),
                android.R.layout.simple_list_item_1, rssItems));
    }

}
