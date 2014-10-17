package cc.lukas.rssreader;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRssFeedFragment extends Fragment {
    public AddRssFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_rss_feed, container, false);
        Button button = (Button) view.findViewById(R.id.btn_addrssfeed);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Button clicked
                int x = 0;
                // Start service, try to fetch rss feed, start intent so activity changes fragment
            }
        });

        return view;
    }
}
