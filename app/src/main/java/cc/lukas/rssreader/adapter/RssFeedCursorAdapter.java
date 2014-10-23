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
    private Context context;
    private Cursor cursor;

    public RssFeedCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.context = context;
        this.cursor = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // There is no view at this position, we create a new one.
        // In this case by inflating an xml layout.
        if (convertView == null) {
            // Inflate a layout
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.rssfeed_row, parent, false);

            holder = new ViewHolder();
            holder.feedTitle = (TextView) convertView.findViewById(R.id.feed_title);
            holder.feedItemCounter = (TextView) convertView.findViewById(R.id.feed_itemcounter);
            convertView.setTag(holder);
        } else {
            // We recycle a View that already exists.
            holder = (ViewHolder) convertView.getTag();
        }

        cursor.moveToPosition(position);
        if (cursor.getCount() > 0) {
            holder.feedTitle.setText(
                    cursor.getString(
                            cursor.getColumnIndex(
                                    RssFeedDao.Properties.Title.columnName)));

            int randomNumber = new Random().nextInt(1) + 23;
            holder.feedItemCounter.setText(String.valueOf(randomNumber));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView feedTitle, feedItemCounter;
    }
}
