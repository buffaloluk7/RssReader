package cc.lukas.rssreader.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cc.lukas.rssreader.R;
import cc.lukas.rssreader.services.RssFeedService;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRssFeedFragment extends Fragment {
    private BroadcastReceiver rssFeedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(RssFeedService.EXTRA_ERROR_CODE)) {
                switch (intent.getIntExtra(RssFeedService.EXTRA_ERROR_CODE, 0)) {
                    case 0:
                        Toast.makeText(getActivity(), "URL invalid.", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getActivity(), "Invalid XML.", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getActivity(), "An error occurred.", Toast.LENGTH_SHORT).show();
                        break;
                }
                (getActivity().findViewById(R.id.button_addfeed)).setEnabled(true);

                return;
            }

            // start callback for activity to change fragment
            long feedId = intent.getLongExtra(RssFeedService.EXTRA_FEED_ID, 0);
            getActivity().getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, RssItemListFragment.newInstance(feedId))
                    .commit();
        }
    };

    public AddRssFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register local broadcast receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(rssFeedReceiver,
                new IntentFilter(RssFeedService.ACTION_CREATE_FEED));
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister local broadcast receiver
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(rssFeedReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_rssfeed, container, false);

        // UI elements
        final EditText rssFeedTitle = (EditText) view.findViewById(R.id.editText_rssfeed_title);
        final EditText rssFeedLink = (EditText) view.findViewById(R.id.editText_rssfeed_link);
        final Button button = (Button) view.findViewById(R.id.button_addfeed);

        // Add onclick listener to button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = rssFeedTitle.getText().toString();
                String link = rssFeedLink.getText().toString();

                // Show toast if url is invalid.
                if (!Patterns.WEB_URL.matcher(link).matches()) {
                    Toast.makeText(getActivity(), "Invalid URL.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show toast and disable UI elements
                Toast.makeText(getActivity(), "Bitte warten...", Toast.LENGTH_SHORT).show();
                button.setEnabled(false);

                // Start the service using a helper method
                RssFeedService.startActionCreateFeed(getActivity(), title, link);
            }
        });

        return view;
    }
}
