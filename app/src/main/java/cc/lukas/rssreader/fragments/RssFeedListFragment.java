package cc.lukas.rssreader.fragments;

import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import cc.lukas.rssreader.R;
import cc.lukas.rssreader.RssFeedContentProvider;
import cc.lukas.rssreader.RssFeedDao;

/**
 * A fragment representing a list of Items.
 */
public class RssFeedListFragment extends ListFragment {
    public static final String ARG_FEED_ID = "FEED_ID";
    public static final String INTENT_RSSFEED = "RSSFEED";

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SimpleCursorAdapter adapter;
    private ActionMode actionMode;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.rssfeed_actionmode, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete:
                    //deleteCurrentItem();
                    actionMode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            //actionMode = null;
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RssFeedListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fetch rss feeds
        Cursor cursor = getActivity()
                .getContentResolver()
                .query(RssFeedContentProvider.CONTENT_URI,
                        new String[]{RssFeedDao.Properties.Id.columnName,
                                RssFeedDao.Properties.Title.columnName},
                        null, null, null);
        // Setup cursor adapter
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
        View view = inflater.inflate(R.layout.fragment_rssfeed_list, container, false);

        // Set the adapter
        final AbsListView mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
                // Update the action mode title
                actionMode.setTitle(mListView.getCheckedItemCount() + " Selected");

                // Set background color on selected item.
                //mListView.getItemAtPosition(position);
                //View x = ((View)mListView.getItemAtPosition(position)).setBackgroundColor(Color.BLUE); // NOT WORKING
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.rssfeed_actionmode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        // Get a list of selected item ids.
                        long[] selectedFeedIds = mListView.getCheckedItemIds();
                        ContentResolver cr = getActivity().getContentResolver();

                        // Delete all selected items.
                        for (long selectedFeedId : selectedFeedIds) {
                            cr.delete(RssFeedContentProvider.CONTENT_URI,
                                    RssFeedDao.Properties.Id.columnName + " =  ?",
                                    new String[]{String.valueOf(selectedFeedId)});
                            // TODO: use observer to remove deleted items.
                        }

                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                mListView.clearChoices();
                mListView.requestLayout();
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(INTENT_RSSFEED);
        intent.putExtra(ARG_FEED_ID, id);

        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        Log.i("RssFeedList", "Option " + position + " selected from FeedList");
    }
}
