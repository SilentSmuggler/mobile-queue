
package com.silentlabs.android.mobilequeue.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.silentlabs.android.mobilequeue.MobileQueueApplication;
import com.silentlabs.android.mobilequeue.R;
import com.silentlabs.android.mobilequeue.adapter.BrowseAdapter;
import com.silentlabs.android.mobilequeue.classes.Item;
import com.silentlabs.android.mobilequeue.parser.NetflixParser;
import com.silentlabs.android.mobilequeue.util.UIUtils;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

public class SearchActivity extends SherlockListActivity implements OnScrollListener {

    private class PostTask extends AsyncTask<String, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(SearchActivity.this);
        private String message = null;

        @Override
        protected Void doInBackground(String... parms) {

            try {
                NetflixParser postParser = new NetflixParser(parms[0], parms[1]);
                if (queue.equals("Disc")) {
                    postParser.postData(parms[2], parms[3], app.getDiscETag(), parms[4], parms[5]);
                    app.setDiscETag(postParser.getETag());
                } else if (queue.equals("Instant")) {
                    postParser.postData(parms[2], parms[3], app.getInstantETag(), parms[4],
                            parms[5]);
                    app.setInstantETag(postParser.getETag());
                }
                message = postParser.getStatusMessage();

            } catch (MalformedURLException e) {
                message = "MalformedURLException";
                cancel(true);

            } catch (ParserConfigurationException e) {
                message = "ParserConfigurationException";
                cancel(true);

            } catch (SAXException e) {
                message = "SAXException";
                cancel(true);

            } catch (IOException e) {
                message = e.getMessage();
                cancel(true);

            } catch (NullPointerException e) {
                message = e.getMessage();
                cancel(true);

            } catch (RuntimeException e) {
                message = e.getMessage();
                cancel(true);

            } catch (Exception e) {
                message = e.getMessage();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            mPullData = false;
            message = "Operation Cancelled";
            Toast.makeText(SearchActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void result) {

            mPullData = false;

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            if (message != null)
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            mPullData = true;
            this.dialog.setMessage("Updating. Please wait...");
            this.dialog.setIndeterminate(true);
            this.dialog.setCancelable(true);
            this.dialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
            this.dialog.show();
        }
    }

    private class SearchTask extends AsyncTask<String, String, Void> {

        private final ProgressDialog dialog = new ProgressDialog(SearchActivity.this);
        ArrayList<Item> listItems = new ArrayList<Item>(25);
        private String message = null;

        @Override
        protected Void doInBackground(String... parms) {

            try {
                NetflixParser catalogParser = new NetflixParser(parms[0], parms[1]);
                catalogParser.parseFeed(parms[2]);
                listItems = catalogParser.getContainer().getArrayItems();
                catalogSize = catalogParser.getContainer().getNumberOfResults();
                startIndex += catalogParser.getContainer().getResultsPerPage();

            } catch (MalformedURLException e) {
                message = "MalformedURLException";
                cancel(true);

            } catch (ParserConfigurationException e) {
                message = "ParserConfigurationException";
                cancel(true);

            } catch (SAXException e) {
                message = "SAXException";
                cancel(true);

            } catch (IOException e) {
                message = e.getMessage();
                cancel(true);

            } catch (NullPointerException e) {
                message = e.getMessage();
                cancel(true);

            } catch (RuntimeException e) {
                message = e.getMessage();
                cancel(true);

            } catch (Exception e) {
                message = e.getMessage();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onCancelled() {
            mPullData = false;
            message = "Operation Cancelled";
            Toast.makeText(SearchActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void result) {

            if (listItems.size() > 0) {
                for (int i = 0; i < listItems.size(); i++) {
                    browseAdapter.add(listItems.get(i));
                }
                browseAdapter.notifyDataSetChanged();
            }

            mPullData = false;

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            if (message != null)
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            mPullData = true;
            this.dialog.setMessage("Searching. Please wait...");
            this.dialog.setIndeterminate(true);
            this.dialog.setCancelable(true);
            this.dialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
            this.dialog.show();
        }
    }

    private final static String ACCESS = "MobileQueueAccess";
    private MobileQueueApplication app;
    private SharedPreferences access;

    private String accessKey;
    private String accessSecret;
    private boolean mPullData = false;
    private int catalogSize = 0;
    private int startIndex = 0;
    private int priorFirst = -1;
    private String queueUrl;

    private String userid;

    private String queue;
    private int mScrollState = SCROLL_STATE_IDLE;
    private String addUrl;
    private ListView mList;

    private BrowseAdapter browseAdapter;

    private final ArrayList<Item> listItem = new ArrayList<Item>();

    private void executePostTask(String postURL, String titleRef, String to, String format) {
        if (mPullData == false)
            new PostTask().execute(accessKey, accessSecret, postURL, titleRef, to, format);
    }

    private void executeSearchTask() {
        if (mPullData == false)
            new SearchTask().execute(accessKey, accessSecret,
                    (queueUrl + "&start_index=" + String.valueOf(startIndex))
                            + "&expand=synopsis,formats");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Item contextTitle;

        if (item.getItemId() == R.id.browse_context_menu_display_title) {
            Intent displayMovie = new Intent(SearchActivity.this,
                    DisplayMovieDetailsActivity.class);
            displayMovie.putExtra("Title", listItem.get((int) info.id));
            displayMovie.putExtra("TYPE", "BROWSE");
            startActivity(displayMovie);
            return true;
        } else if (item.getItemId() == R.id.browse_context_menu_similar) {
            contextTitle = listItem.get((int) info.id);
            Intent similarIntent = new Intent(SearchActivity.this, BrowseActivity.class);
            similarIntent.putExtra("QUEUE", contextTitle.getLinkId());
            similarIntent.putExtra("TYPE", "SIMILAR");
            startActivity(similarIntent);
            return true;
        } else if (item.getItemId() == R.id.browse_context_menu_queue_add_top) {
            queue = "Disc";
            String queueAddTop = addUrl + getString(R.string.disc_queueURL);
            contextTitle = listItem.get((int) info.id);
            executePostTask(queueAddTop, contextTitle.getID(), "1", null);
            return true;
        } else if (item.getItemId() == R.id.browse_context_menu_queue_add_bottom) {
            queue = "Disc";
            String queueAddBottom = addUrl + getString(R.string.disc_queueURL);
            contextTitle = listItem.get((int) info.id);
            executePostTask(queueAddBottom, contextTitle.getID(), null, null);
            return true;
        } else if (item.getItemId() == R.id.browse_context_menu_instant_add_top) {
            queue = "Instant";
            String instantAddTop = addUrl + getString(R.string.instant_queueURL);
            contextTitle = listItem.get((int) info.id);
            executePostTask(instantAddTop, contextTitle.getID(), "1", null);
            return true;
        } else if (item.getItemId() == R.id.browse_context_menu_instant_add_bottom) {
            queue = "Instant";
            String instantAddBottom = addUrl + getString(R.string.instant_queueURL);
            contextTitle = listItem.get((int) info.id);
            executePostTask(instantAddBottom, contextTitle.getID(), null, null);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        app = ((MobileQueueApplication) getApplicationContext());

        TextView brandingTest = (TextView) findViewById(R.id.BrandingText);
        brandingTest.setMovementMethod(LinkMovementMethod.getInstance());

        mList = getListView();
        mList.setOnScrollListener(this);

        this.browseAdapter = new BrowseAdapter(this, R.layout.list_item_browse, this.listItem,
                mList);
        setListAdapter(this.browseAdapter);

        mList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

                AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
                Item contextTitle = listItem.get((int) info.id);
                ArrayList<String> formats = contextTitle.getFormats();
                Date currentDate = new Date();
                boolean discAvaliable = false;
                boolean instantAvaliable = false;

                int formatCount = formats.size();
                for (int i = 0; i < formatCount; i++) {
                    if (formats.get(i).equalsIgnoreCase("Blu-ray")
                            || formats.get(i).equalsIgnoreCase("DVD")) {
                        discAvaliable = true;

                    } else if (formats.get(i).equalsIgnoreCase("instant")) {
                        if (currentDate.after(contextTitle.getAvailableFromInstant())
                                && currentDate.before(contextTitle.getAvailableToInstant()))
                            instantAvaliable = true;
                    }
                }

                menu.setHeaderTitle(R.string.queue_menu_label);
                getMenuInflater().inflate(R.menu.browse_context_menu, menu);

                MenuItem addQTop = menu.findItem(R.id.browse_context_menu_queue_add_top);
                MenuItem addQBottom = menu.findItem(R.id.browse_context_menu_queue_add_bottom);
                MenuItem addITop = menu.findItem(R.id.browse_context_menu_instant_add_top);
                MenuItem addIBottom = menu.findItem(R.id.browse_context_menu_instant_add_bottom);

                if (discAvaliable == true) {
                    addQTop.setVisible(true);
                    addQBottom.setVisible(true);
                } else {
                    addQTop.setVisible(false);
                    addQBottom.setVisible(false);
                }

                if (instantAvaliable == true) {
                    addITop.setVisible(true);
                    addIBottom.setVisible(true);
                } else {
                    addITop.setVisible(false);
                    addIBottom.setVisible(false);
                }
            }
        });

        onNewIntent(getIntent());
    }

    public void onHomeClick(View v) {
        UIUtils.goHome(this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent displayMovie = new Intent(this, DisplayMovieDetailsActivity.class);
        displayMovie.putExtra("Title", (Item) l.getItemAtPosition(position));
        displayMovie.putExtra("TYPE", "BROWSE");
        startActivity(displayMovie);
    }

    @Override
    public void onNewIntent(Intent intent) {

        if (this.browseAdapter != null) {
            catalogSize = 0;
            startIndex = 0;
            priorFirst = -1;
            browseAdapter.clear();
        }

        String query = intent.getStringExtra(SearchManager.QUERY);
        final CharSequence title = getString(R.string.title_search_query, query);

        setTitle(title);
        getSupportActionBar().setTitle(title);

        searchNetflix(query.replace(" ", "%20").replace("&", "%26"));
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {

        if (mScrollState == SCROLL_STATE_FLING)
            return;

        if ((firstVisibleItem + visibleItemCount == totalItemCount) && totalItemCount < catalogSize)
            // see if we have more results
            if (firstVisibleItem != priorFirst) {
                priorFirst = firstVisibleItem;
                executeSearchTask();
            }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
    }

    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }

    private void searchNetflix(String query) {

        access = getSharedPreferences(ACCESS, Context.MODE_PRIVATE);
        accessKey = access.getString("ACCESS_KEY", null);
        accessSecret = access.getString("ACCESS_SECRET", null);
        userid = access.getString("USER_ID", null);

        queueUrl = getString(R.string.catalog_titles_api_URL) + "?term=" + query;
        addUrl = getString(R.string.user_api_URL) + userid;

        executeSearchTask();
    }
}
