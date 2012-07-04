
package com.silentlabs.android.mobilequeue.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.silentlabs.android.mobilequeue.classes.Container;
import com.silentlabs.android.mobilequeue.classes.Item;
import com.silentlabs.android.mobilequeue.parser.NetflixParser;
import com.silentlabs.android.mobilequeue.util.UIUtils;
import com.silentlabs.android.mobilequeue.view.TouchInterceptor;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

public class BrowseActivity extends SherlockListActivity implements OnScrollListener {

    private class DeleteTask extends AsyncTask<String, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(BrowseActivity.this);
        private int status = 0;
        private String message = null;

        @Override
        protected Void doInBackground(String... parms) {

            try {
                NetflixParser postParser = new NetflixParser(parms[0], parms[1]);
                if (queue.equals("Disc")) {
                    postParser.deleteData(parms[2], app.getDiscETag());
                    app.setDiscETag(postParser.getETag());
                } else if (queue.equals("Instant")) {
                    postParser.deleteData(parms[2], app.getInstantETag());
                    app.setInstantETag(postParser.getETag());
                }
                status = Integer.parseInt(postParser.getStatus());
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
            Toast.makeText(BrowseActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void result) {

            if (deleteItem != null && (status >= 200 && status < 400)) {
                browseAdapter.remove(deleteItem);
                queueSize -= 1;
                browseAdapter.notifyDataSetChanged();
            }

            mPullData = false;

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            if (message != null)
                Toast.makeText(BrowseActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            mPullData = true;
            this.dialog.setMessage("Removing. Please wait...");
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

    private class LoadTask extends AsyncTask<String, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(BrowseActivity.this);
        ArrayList<Item> listItems = new ArrayList<Item>(25);
        private String message = null;

        @Override
        protected Void doInBackground(String... parms) {

            try {
                NetflixParser queueParser = new NetflixParser(parms[0], parms[1]);
                queueParser.parseFeed(parms[2]);

                Container container = queueParser.getContainer();
                if (container != null) {
                    listItems = container.getArrayItems();
                    if (queue.equals("Disc"))
                        app.setDiscETag(container.getETag());
                    else if (queue.equals("Instant"))
                        app.setInstantETag(container.getETag());

                    queueSize = container.getNumberOfResults();
                    startIndex += container.getResultsPerPage();
                }

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
            Toast.makeText(BrowseActivity.this, message, Toast.LENGTH_LONG).show();
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
                Toast.makeText(BrowseActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            mPullData = true;
            this.dialog.setMessage("Retrieving. Please wait...");
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

    private class PostTask extends AsyncTask<String, Void, Integer> {

        private final ProgressDialog dialog = new ProgressDialog(BrowseActivity.this);
        private Item item;
        private int status = 0;
        private String message = null;

        @Override
        protected Integer doInBackground(String... parms) {

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
                item = postParser.getItem();
                status = Integer.parseInt(postParser.getStatus());
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

            if (item != null)
                return item.getPosition();
            else
                return 0;
        }

        @Override
        protected void onCancelled() {
            mPullData = false;
            message = "Operation Cancelled";
            Toast.makeText(BrowseActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (item != null && postItem != null && (status >= 200 && status < 400)) {
                browseAdapter.remove(postItem);
                browseAdapter.insert(item, result - 1);
                browseAdapter.notifyDataSetChanged();
            }

            mPullData = false;

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            if (message != null)
                Toast.makeText(BrowseActivity.this, message, Toast.LENGTH_LONG).show();
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

    private final static String ACCESS = "MobileQueueAccess";
    private MobileQueueApplication app;
    private SharedPreferences access;
    private SharedPreferences settings;

    private String accessKey;

    private String accessSecret;
    private String userid;
    private String type;
    private boolean mPullData = false;
    private int queueSize = 0;
    private int startIndex = 0;
    private int priorFirst = -1;

    private String queueUrl;

    private String addUrl;
    private String queue;
    private int mScrollState = SCROLL_STATE_IDLE;
    private ListView mList;
    private BrowseAdapter browseAdapter;

    private final ArrayList<Item> listItem = new ArrayList<Item>();

    private Item postItem;

    private Item deleteItem;

    private final TouchInterceptor.DropListener mDropListener =
            new TouchInterceptor.DropListener() {

                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        postItem = browseAdapter.getItem(from);
                        executePostTask(queueUrl, postItem.getID(), String.valueOf(to + 1), null);
                    }
                }
            };

    private final TouchInterceptor.RemoveListener mRemoveListener =
            new TouchInterceptor.RemoveListener() {

                @Override
                public void remove(int which) {
                    deleteItem = browseAdapter.getItem(which);
                    executeDeleteTask(deleteItem.getID());
                }
            };

    private void executeDeleteTask(String titleRef) {
        if (mPullData == false)
            new DeleteTask().execute(accessKey, accessSecret, titleRef);
    }

    private void executeLoadTask() {
        if (mPullData == false)
            new LoadTask().execute(accessKey, accessSecret,
                    (queueUrl + "?start_index=" + String.valueOf(startIndex))
                            + "&expand=synopsis,formats");
    }

    private void executePostTask(String postURL, String titleRef, String to, String format) {
        if (mPullData == false)
            new PostTask().execute(accessKey, accessSecret, postURL, titleRef, to, format);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Item contextTitle;

        if (item.getItemId() == R.id.queue_context_menu_queue_top) {
            if (queue.equals("Disc")) {
                String queueAddTop = addUrl + getString(R.string.disc_queueURL);
                postItem = listItem.get((int) info.id);
                executePostTask(queueAddTop, postItem.getID(), "1", null);

            } else if (queue.equals("Instant")) {
                String instantAddTop = addUrl + getString(R.string.instant_queueURL);
                postItem = listItem.get((int) info.id);
                executePostTask(instantAddTop, postItem.getID(), "1", null);
            }
            return true;
        } else if (item.getItemId() == R.id.queue_context_menu_display_title
                || item.getItemId() == R.id.browse_context_menu_display_title) {
            Intent displayMovie = new Intent(BrowseActivity.this,
                    DisplayMovieDetailsActivity.class);
            displayMovie.putExtra("Title", listItem.get((int) info.id));
            displayMovie.putExtra("TYPE", type);
            startActivity(displayMovie);
            return true;
        } else if (item.getItemId() == R.id.queue_context_menu_similar
                || item.getItemId() == R.id.browse_context_menu_similar) {
            contextTitle = listItem.get((int) info.id);
            Intent similarIntent = new Intent(BrowseActivity.this, BrowseActivity.class);
            similarIntent.putExtra("QUEUE", contextTitle.getLinkId());
            similarIntent.putExtra("TYPE", "SIMILAR");
            startActivity(similarIntent);
            return true;
        } else if (item.getItemId() == R.id.queue_context_menu_queue_delete) {
            deleteItem = listItem.get((int) info.id);
            executeDeleteTask(deleteItem.getID());
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ((MobileQueueApplication) getApplicationContext());

        Bundle extras = getIntent().getExtras();
        String term = extras.getString("QUEUE");
        type = extras.getString("TYPE");

        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        access = getSharedPreferences(ACCESS, Context.MODE_PRIVATE);
        accessKey = access.getString("ACCESS_KEY", null);
        accessSecret = access.getString("ACCESS_SECRET", null);
        userid = access.getString("USER_ID", null);

        addUrl = getString(R.string.user_api_URL) + userid;
        if (type.equals("SIMILAR"))
            queueUrl = term;
        else
            queueUrl = addUrl + term;

        if (term.equals(getString(R.string.disc_queueURL)))
            queue = "Disc";
        else if (term.equals(getString(R.string.instant_queueURL)))
            queue = "Instant";
        else
            queue = "";

        if (type.equals("QUEUE")) {
            setContentView(R.layout.activity_queue);
            TextView brandingTest = (TextView) findViewById(R.id.BrandingText);
            brandingTest.setMovementMethod(LinkMovementMethod.getInstance());

            if (term.equals(getString(R.string.disc_queueURL)))
                getSupportActionBar().setTitle(R.string.btn_discqueue);
            else if (term.equals(getString(R.string.instant_queueURL)))
                getSupportActionBar().setTitle(R.string.btn_instantqueue);

            mList = getListView();
            this.browseAdapter = new BrowseAdapter(this, R.layout.list_item_queue, this.listItem,
                    mList);
            setListAdapter(this.browseAdapter);

            ((TouchInterceptor) mList).setDropListener(mDropListener);
            ((TouchInterceptor) mList).setRemoveListener(mRemoveListener);

        } else if (type.equals("BROWSE") || type.equals("ATHOME") || type.equals("HISTORY")
                || type.equals("SIMILAR")) {
            if (type.equals("BROWSE")) {
                setContentView(R.layout.activity_browse);
                TextView brandingTest = (TextView) findViewById(R.id.BrandingText);
                brandingTest.setMovementMethod(LinkMovementMethod.getInstance());
                getSupportActionBar().setTitle(R.string.btn_suggest);

            } else if (type.equals("ATHOME")) {
                setContentView(R.layout.activity_browse);
                TextView brandingTest = (TextView) findViewById(R.id.BrandingText);
                brandingTest.setMovementMethod(LinkMovementMethod.getInstance());
                getSupportActionBar().setTitle(R.string.btn_athome);

            } else if (type.equals("HISTORY")) {
                setContentView(R.layout.activity_browse);
                TextView brandingTest = (TextView) findViewById(R.id.BrandingText);
                brandingTest.setMovementMethod(LinkMovementMethod.getInstance());
                getSupportActionBar().setTitle(R.string.btn_history);

            } else if (type.equals("SIMILAR")) {
                setContentView(R.layout.activity_browse_content);
            }

            mList = getListView();
            this.browseAdapter = new BrowseAdapter(this, R.layout.list_item_browse, this.listItem,
                    mList);
            setListAdapter(this.browseAdapter);
        }

        mList.setOnScrollListener(this);
        mList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

                AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
                Item contextTitle = listItem.get((int) info.id);
                ArrayList<String> formats = contextTitle.getFormats();
                Date currentDate = new Date(System.currentTimeMillis());
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

                if (type.equals("QUEUE")) {
                    getMenuInflater().inflate(R.menu.queue_context_menu, menu);

                } else if (type.equals("BROWSE") || type.equals("HISTORY")
                        || type.equals("SIMILAR")) {
                    getMenuInflater().inflate(R.menu.browse_context_menu, menu);

                    MenuItem addQTop = menu.findItem(R.id.browse_context_menu_queue_add_top);
                    MenuItem addQBottom = menu.findItem(R.id.browse_context_menu_queue_add_bottom);
                    MenuItem addITop = menu.findItem(R.id.browse_context_menu_instant_add_top);
                    MenuItem addIBottom = menu
                            .findItem(R.id.browse_context_menu_instant_add_bottom);

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
            }
        });

        executeLoadTask();
    }

    /** Handle "home" title-bar action. */
    public void onHomeClick(View v) {
        UIUtils.goHome(this);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent displayMovie = new Intent(this, DisplayMovieDetailsActivity.class);
        displayMovie.putExtra("Title", (Item) l.getItemAtPosition(position));
        displayMovie.putExtra("TYPE", type);
        startActivity(displayMovie);
    }

    /** Handle "refresh" title-bar action. */
    public void onRefreshClick(View v) {
        queueSize = 0;
        startIndex = 0;
        priorFirst = -1;
        browseAdapter.clear();
        executeLoadTask();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Boolean refresh = settings.getBoolean("refreshPref", false);
        if (refresh == true) {
            queueSize = 0;
            startIndex = 0;
            priorFirst = -1;
            browseAdapter.clear();
            executeLoadTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {

        if (mScrollState == SCROLL_STATE_FLING)
            return;

        if ((firstVisibleItem + visibleItemCount == totalItemCount) && totalItemCount < queueSize)
            // see if we have more results
            if (firstVisibleItem != priorFirst) {
                priorFirst = firstVisibleItem;
                executeLoadTask();
            }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mScrollState = scrollState;
    }

    /** Handle "search" title-bar action. */
    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }

    @Override
    public boolean onSearchRequested() {
        UIUtils.goSearch(this);
        return true;
    }
}
