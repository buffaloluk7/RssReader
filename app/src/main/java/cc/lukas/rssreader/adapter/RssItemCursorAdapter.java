package cc.lukas.rssreader.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cc.lukas.rssreader.R;
import cc.lukas.rssreader.RssItemDao;

public class RssItemCursorAdapter extends CursorAdapter {
    public RssItemCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);

        return inflater.inflate(R.layout.rssitem_row, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Item title
        TextView textViewItemTitle = (TextView) view.findViewById(R.id.item_title);
        textViewItemTitle.setText(
                cursor.getString(
                        cursor.getColumnIndex(
                                RssItemDao.Properties.Title.columnName)));

        // Item read/unread
        ImageView imageViewItemUnread = (ImageView) view.findViewById(R.id.item_unread);
        int itemRead = cursor.getInt(
                cursor.getColumnIndex(
                        RssItemDao.Properties.Read.columnName));
        imageViewItemUnread.setVisibility(itemRead == 0 ? View.VISIBLE : View.INVISIBLE);

        // Item starred
        ImageView imageViewItemStarred = (ImageView) view.findViewById(R.id.item_starred);
        int itemStarred = cursor.getInt(
                cursor.getColumnIndex(
                        RssItemDao.Properties.Starred.columnName));
        imageViewItemStarred.setVisibility(itemStarred == 0 ? View.INVISIBLE : View.VISIBLE);
    }
}