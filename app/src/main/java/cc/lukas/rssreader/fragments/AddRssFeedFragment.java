package cc.lukas.rssreader.fragments;

import android.app.Fragment;
import android.os.Bundle;
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
                    Toast.makeText(getActivity(), getString(R.string.error_invalid_url), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show toast and disable UI elements.
                Toast.makeText(getActivity(), getString(R.string.wait), Toast.LENGTH_SHORT).show();
                button.setEnabled(false);

                // Start the service using a helper method.
                RssFeedService.startActionCreateFeed(getActivity(), title, link);
            }
        });

        return view;
    }
}