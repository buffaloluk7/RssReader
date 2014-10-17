package cc.lukas.rssreader;

import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
/**
 * A fragment representing a list of Items.
 */
public class RssFeedListFragment extends ListFragment implements AdapterView.OnItemLongClickListener {

    public static final String ARG_FEED_ID = "feedId";
    public static final String INTENT_RSSFEED = "rssfeed";
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private RssFeedDao rssFeedDao;
    private Cursor cursor;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SimpleCursorAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RssFeedListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establish database connection
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "rssreader-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        rssFeedDao = daoSession.getRssFeedDao();

        // Fetch rss feeds
        cursor = daoSession.getDatabase().query(rssFeedDao.getTablename(),
                new String[] {RssFeedDao.Properties.Id.columnName,
                        RssFeedDao.Properties.Title.columnName},
                null,
                null,
                null,
                null,
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(INTENT_RSSFEED);
        intent.putExtra(ARG_FEED_ID, id);

        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }

}
