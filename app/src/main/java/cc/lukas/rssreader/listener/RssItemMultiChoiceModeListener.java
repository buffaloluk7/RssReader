package cc.lukas.rssreader.listener;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import cc.lukas.rssreader.R;
import cc.lukas.rssreader.RssItemContentProvider;
import cc.lukas.rssreader.RssItemDao;

public class RssItemMultiChoiceModeListener implements ListView.MultiChoiceModeListener {
    private Activity activity;
    private ListView listView;
    private Resources resources;

    public RssItemMultiChoiceModeListener(Activity activity, ListView listView, Resources resources) {
        this.activity = activity;
        this.listView = listView;
        this.resources = resources;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
        // Set the action mode title.
        int numberOfCheckedItems = listView.getCheckedItemCount();
        String actionModeTitle = resources.getQuantityString(R.plurals.feeds,
                numberOfCheckedItems,
                numberOfCheckedItems);
        actionMode.setTitle(actionModeTitle);

        // Set background color on selected item.
        if (listView.isItemChecked(position)) {
            listView.getChildAt(position).setBackgroundColor(Color.LTGRAY);
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
        ContentResolver cr = activity.getContentResolver();

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
}