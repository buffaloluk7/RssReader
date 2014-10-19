package cc.lukas.rssreader.fragments;

import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import cc.lukas.rssreader.R;
import cc.lukas.rssreader.RssItemContentProvider;
import cc.lukas.rssreader.RssItemDao;

/**
 * A fragment representing a list of Items.
 */
public class RssItemListFragment extends ListFragment {
    private static final String ARG_FEED_ID = "FEED_ID";

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
                new String[]{RssItemDao.Properties.Title.columnName},
                new int[]{android.R.id.text1},
                0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rssitem_list, container, false);

        // Set the adapter
        final AbsListView listView = (AbsListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
                // Update the action mode title
                actionMode.setTitle(listView.getCheckedItemCount() + " Items selektiert");

                // Set background color on selected item.
                if (listView.isItemChecked(position)) {
                    listView.getChildAt(position).setBackgroundColor(Color.BLUE);
                } else {
                    listView.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.rssitem_actionmode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                // Get a list of selected item ids.
                long[] selectedItemIds = listView.getCheckedItemIds();
                ContentResolver cr = getActivity().getContentResolver();

                switch (menuItem.getItemId()) {
                    case R.id.action_read:
                    case R.id.action_unread:
                    case R.id.action_star:
                        // Mark all selected items as read/unread/starred.
                        for (long selectedItemId : selectedItemIds) {
                            // Create ContentValues object to store the new field values.
                            ContentValues newValues = new ContentValues();

                            // Determine whether to update the read/unread or the starred flag.
                            if (menuItem.getItemId() == R.id.action_star) {
                                newValues.put(RssItemDao.Properties.Starred.columnName, true);
                            } else {
                                // Determine whether to set the read flag to true or false.
                                boolean itemRead = menuItem.getItemId() == R.id.action_read;
                                newValues.put(RssItemDao.Properties.Read.columnName, itemRead);
                            }

                            // Execute update statement.
                            cr.update(RssItemContentProvider.CONTENT_URI,
                                    newValues,
                                    RssItemDao.Properties.Id.columnName + " = ?",
                                    new String[]{String.valueOf(selectedItemId)});
                        }
                        break;
                    default:
                        return false;
                }

                actionMode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                listView.clearChoices();
                listView.requestLayout();
            }
        });

        return view;
    }
}