package cc.lukas.rssreader.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Random;

import cc.lukas.rssreader.R;
import cc.lukas.rssreader.RssFeedDao;

public class RssFeedCursorAdapter extends CursorAdapter {
    public RssFeedCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);

        return inflater.inflate(R.layout.rssfeed_row, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewFeedTitle = (TextView) view.findViewById(R.id.feed_title);
        textViewFeedTitle.setText(
                cursor.getString(
                        cursor.getColumnIndex(
                                RssFeedDao.Properties.Title.columnName)));

        TextView textViewFeedItemCounter = (TextView) view.findViewById(R.id.feed_itemcounter);
        int randomNumber = new Random().nextInt(1) + 23;
        textViewFeedItemCounter.setText(String.valueOf(randomNumber));
    }
}
