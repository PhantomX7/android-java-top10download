package com.phantomdeveloper.top10download;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedCachedUrl = "INVALIDATED";
    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";

    @BindView(R.id.xmlRecyclerView)
    RecyclerView recyclerView;

    private FeedEntryAdapter feedEntryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadUrl(String.format(feedUrl, feedLimit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if (feedLimit == 10) {
            menu.findItem(R.id.mnu10).setChecked(true);
        } else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 10;
                }
                break;
            case R.id.mnu25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 25;
                }
                break;
            case R.id.mnuRefresh:
                feedCachedUrl = "INVALIDATED";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        downloadUrl(String.format(feedUrl, feedLimit));

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    private void downloadUrl(String feedUrl) {

        if (!feedUrl.equalsIgnoreCase(feedCachedUrl)) {
            Log.d(TAG, "onCreate: Starting AsyncTask");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedUrl);
            feedCachedUrl = feedUrl;
            Log.d(TAG, "onCreate: done");
        }

    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is " + s);

            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);
            feedEntryAdapter = new FeedEntryAdapter(getApplicationContext(), parseApplications.getApplications());
            recyclerView.setAdapter(feedEntryAdapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(false);

        }

    }

    private String downloadXML(String urlPath) {
        StringBuilder xmlResult = new StringBuilder();

        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            Log.d(TAG, "downloadXML : The response code is" + response);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            int charRead;
            char[] inputBuffer = new char[500];
            while (true) {
                charRead = reader.read(inputBuffer);
                if (charRead < 0) {
                    break;
                }
                if (charRead > 0) {
                    xmlResult.append(String.copyValueOf(inputBuffer, 0, charRead));
                }
            }

            reader.close();

            return xmlResult.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadXML:  invalid url" + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "downloadXML: IO exception" + e.getMessage());
        }
        return null;
    }
}
