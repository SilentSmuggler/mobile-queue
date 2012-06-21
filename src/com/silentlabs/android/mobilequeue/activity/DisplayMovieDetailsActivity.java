
package com.silentlabs.android.mobilequeue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.silentlabs.android.mobilequeue.R;
import com.silentlabs.android.mobilequeue.classes.Item;
import com.silentlabs.android.mobilequeue.parser.NetflixParser;
import com.silentlabs.android.mobilequeue.util.AsyncImageLoader;
import com.silentlabs.android.mobilequeue.util.AsyncImageLoader.ImageCallback;
import com.silentlabs.android.mobilequeue.util.UIUtils;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

public class DisplayMovieDetailsActivity extends TabActivity {

    static final int RATING_DIALOG = 0;

    private final static String ACCESS = "MobileQueueAccess";

    private SharedPreferences access;
    private String accessKey;
    private String accessSecret;
    private String userid;
    private String ratingUrl;
    private String actualUrl;
    private String ratingId;
    private float currentRating = 0;

    private AsyncImageLoader asyncImageLoader;
    private Item movieTitle;
    private String type;

    private View layout;

    private final Date currentDate = new Date();
    private boolean discAvaliable = false;
    private boolean instantAvaliable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        asyncImageLoader = new AsyncImageLoader(getResources());

        access = getSharedPreferences(ACCESS, Context.MODE_PRIVATE);
        accessKey = access.getString("ACCESS_KEY", null);
        accessSecret = access.getString("ACCESS_SECRET", null);
        userid = access.getString("USER_ID", null);

        ratingUrl = getString(R.string.user_api_URL) + userid
                + getString(R.string.rating_title_URL);
        actualUrl = getString(R.string.user_api_URL) + userid
                + getString(R.string.actual_rating_URL);

        Intent intent = getIntent();
        movieTitle = (Item) intent.getSerializableExtra("Title");
        type = intent.getStringExtra("TYPE");

        ImageView addImageView = (ImageView) findViewById(R.id.addImageView);
        ImageButton addImageButton = (ImageButton) findViewById(R.id.addImageButton);
        if (type.equals("BROWSE") || type.equals("HISTORY") || type.equals("SIMILAR")) {
            addImageView.setVisibility(View.VISIBLE);
            addImageButton.setVisibility(View.VISIBLE);
        } else {
            addImageView.setVisibility(View.GONE);
            addImageButton.setVisibility(View.GONE);
        }

        TextView brandingTest = (TextView) findViewById(R.id.BrandingText);
        brandingTest.setMovementMethod(LinkMovementMethod.getInstance());

        final TabHost host = getTabHost();
        host.clearAllTabs();

        LayoutInflater.from(this).inflate(R.layout.activity_title_content,
                host.getTabContentView(), true);

        setupTabHost();
        setupSynopsisTab();
        setupSimilarTab();
        setupGenreTab();
    }

    protected void setupTabHost() {
        TextView title = (TextView) findViewById(R.id.TitleViewDetail);
        TextView year = (TextView) findViewById(R.id.YearViewDetail);
        final ImageView boxArt = (ImageView) findViewById(R.id.BoxArtViewDetail);

        RatingBar averageRating = (RatingBar) findViewById(R.id.RatingBarViewDetail);
        ImageView ratingView = (ImageView) findViewById(R.id.RatingViewDetail);
        TextView availability = (TextView) findViewById(R.id.AvailabilityViewDetail);
        ImageView dvd = (ImageView) findViewById(R.id.DvdView);
        ImageView bluray = (ImageView) findViewById(R.id.BluRayView);
        ImageView instant = (ImageView) findViewById(R.id.InstantView);

        ((TextView) findViewById(R.id.title_text)).setText(movieTitle.getTitleReqular());

        title.setText(movieTitle.getTitleReqular());
        year.setText("(" + movieTitle.getReleaseYear() + ")");

        URL imageUrl = movieTitle.getBoxArtLarge();
        try {
            Bitmap cachedImage = asyncImageLoader.loadBitmap(imageUrl, new ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageBitmap, URL imageUrl) {
                    ImageView imageViewById = (ImageView) findViewById(R.id.BoxArtViewDetail);
                    if (imageViewById != null) {
                        imageViewById.setImageBitmap(imageBitmap);
                    }
                }
            });
            if (cachedImage != null)
                boxArt.setImageBitmap(cachedImage);
            else
                boxArt.setImageResource(R.drawable.loading);

        } catch (NullPointerException e) {
            boxArt.setImageResource(R.drawable.ic_launcher_netflix);
        }

        try {
            averageRating.setRating(Float.parseFloat(movieTitle.getAverageRating()));
        } catch (NullPointerException e) {
            averageRating.setRating((float) 0.0);
        }

        try {
            String mpaaRatingSting = movieTitle.getMpaaRatings().trim();
            if (mpaaRatingSting != null) {
                if (mpaaRatingSting.equalsIgnoreCase("G"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_g);
                else if (mpaaRatingSting.equalsIgnoreCase("PG"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_pg);
                else if (mpaaRatingSting.equalsIgnoreCase("PG-13"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_pg13);
                else if (mpaaRatingSting.equalsIgnoreCase("R"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_r);
                else if (mpaaRatingSting.equalsIgnoreCase("NC-17"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_nc17);
                else if (mpaaRatingSting.equalsIgnoreCase("NR"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_nr);
                else if (mpaaRatingSting.equalsIgnoreCase("UR"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_ur);
            }

        } catch (NullPointerException e) {
            TextView mpaaView = (TextView) findViewById(R.id.MpaaText);
            mpaaView.setVisibility(View.GONE);
        }

        try {
            String tvRatingString = movieTitle.getTvRatings().trim();
            if (tvRatingString != null) {
                if (tvRatingString.equalsIgnoreCase("TV-Y"))
                    ratingView.setImageResource(R.drawable.ic_tv_rating_y);
                else if (tvRatingString.equalsIgnoreCase("TV-Y7"))
                    ratingView.setImageResource(R.drawable.ic_tv_rating_y7);
                else if (tvRatingString.equalsIgnoreCase("TV-Y7-FV"))
                    ratingView.setImageResource(R.drawable.ic_tv_rating_y7fv);
                else if (tvRatingString.equalsIgnoreCase("TV-G"))
                    ratingView.setImageResource(R.drawable.ic_tv_rating_g);
                else if (tvRatingString.equalsIgnoreCase("TV-PG"))
                    ratingView.setImageResource(R.drawable.ic_tv_rating_pg);
                else if (tvRatingString.equalsIgnoreCase("TV-14"))
                    ratingView.setImageResource(R.drawable.ic_tv_rating_14);
                else if (tvRatingString.equalsIgnoreCase("TV-MA"))
                    ratingView.setImageResource(R.drawable.ic_tv_rating_ma);
                else if (tvRatingString.equalsIgnoreCase("NR"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_nr);
            }

        } catch (NullPointerException e) {
            TextView tvView = (TextView) findViewById(R.id.TvText);
            tvView.setVisibility(View.GONE);
        }

        discAvaliable = false;
        instantAvaliable = false;
        try {
            int formatCount = movieTitle.getFormats().size();
            for (int i = 0; i < formatCount; i++) {
                if (movieTitle.getFormats().get(i).equalsIgnoreCase("Blu-ray")) {
                    bluray.setVisibility(View.VISIBLE);
                    discAvaliable = true;

                } else if (movieTitle.getFormats().get(i).equalsIgnoreCase("DVD")) {
                    dvd.setVisibility(View.VISIBLE);
                    discAvaliable = true;

                } else if (movieTitle.getFormats().get(i).equalsIgnoreCase("Instant")) {
                    if (currentDate.after(movieTitle.getAvailableFromInstant())
                            && currentDate.before(movieTitle.getAvailableToInstant())) {
                        instant.setVisibility(View.VISIBLE);
                        instantAvaliable = true;
                    }
                }
            }
        } catch (NullPointerException e) {
            TextView formatView = (TextView) findViewById(R.id.FormatText);
            formatView.setVisibility(View.INVISIBLE);
        }

        try {
            if (!movieTitle.getAvailability().equals(""))
                availability.setText(movieTitle.getAvailability());

        } catch (NullPointerException e) {
            TextView availabilityView = (TextView) findViewById(R.id.AvailabilityText);
            availabilityView.setVisibility(View.INVISIBLE);
        }
    }

    protected void setupSynopsisTab() {
        final TabHost host = getTabHost();

        TextView synopsis = (TextView) findViewById(R.id.SynopsisViewDetail);

        try {
            synopsis.setText(Html.fromHtml(movieTitle.getSynopsis()));
            synopsis.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (NullPointerException e) {

        }

        // Summary content comes from existing layout
        host.addTab(host.newTabSpec("Synopsis")
                .setIndicator(buildIndicator(R.string.synopsis))
                .setContent(R.id.synopsisScroller));
    }

    protected void setupSimilarTab() {
        final TabHost host = getTabHost();

        Intent similarIntent = new Intent(DisplayMovieDetailsActivity.this, BrowseActivity.class);
        similarIntent.putExtra("QUEUE", movieTitle.getLinkId());
        similarIntent.putExtra("TYPE", "SIMILAR");

        // Similar content comes from existing layout
        host.addTab(host.newTabSpec("Similar")
                .setIndicator(buildIndicator(R.string.similar))
                .setContent(similarIntent));
    }

    protected void setupGenreTab() {
        final TabHost host = getTabHost();

        TextView genre = (TextView) findViewById(R.id.GenreViewDetail);

        try {
            int genreCount = movieTitle.getGenres().size();
            String genreString = new String();
            for (int i = 0; i < genreCount; i++) {
                genreString = genreString + movieTitle.getGenres().get(i).trim() + "\n";
            }
            genre.setText(genreString);
        } catch (NullPointerException e) {

        }

        // Summary content comes from existing layout
        host.addTab(host.newTabSpec("Genre")
                .setIndicator(buildIndicator(R.string.genre))
                .setContent(R.id.genreScroller));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case RATING_DIALOG:
                LayoutInflater factory = LayoutInflater.from(this);
                layout = factory.inflate(R.layout.ratingdialog, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Rate Title")
                .setView(layout)
                .setPositiveButton("Rate", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RatingBar rb = (RatingBar) layout.findViewById(R.id.ratingbar);
                        float userRating = rb.getRating();

                        if (currentRating <= ((float) 0.0)) {
                            executeSetRatingTask(actualUrl + "?title_ref=" + ratingId
                                    + "&rating=" + String.valueOf((int) userRating));
                        }
                        else if (currentRating > ((float) 0.0)) {
                            ratingId = ratingId.replace("/ratings/title/",
                                    "/ratings/title/actual/");
                            executeSetRatingTask(ratingId + "?method=PUT&rating="
                                    + String.valueOf((int) userRating));
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
                AlertDialog alert = builder.create();
                return alert;
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

        RatingBar rb = (RatingBar) layout.findViewById(R.id.ratingbar);
        rb.setRating(currentRating);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_title_menu, menu);

        MenuItem menuItemAdd = menu.findItem(R.id.display_menu_add);
        if (type.equals("BROWSE") || type.equals("HISTORY") || type.equals("SIMILAR")) {
            menuItemAdd.setEnabled(true);
        } else {
            menuItemAdd.setEnabled(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.display_menu_add) {
            String formats = "D";
            if (instantAvaliable == true && discAvaliable == true)
                formats = "B";
            else if (instantAvaliable == false && discAvaliable == true)
                formats = "D";
            else if (instantAvaliable == true && discAvaliable == false)
                formats = "I";
            Intent addTitle = new Intent(this, AddTitleActivity.class);
            addTitle.putExtra("TitleRef", movieTitle.getID());
            addTitle.putExtra("Formats", formats);
            startActivity(addTitle);
            return true;
        } else if (item.getItemId() == R.id.display_menu_rate) {
            executeRequestRatingTask(ratingUrl);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void executeRequestRatingTask(String ratingUrl) {
        new RequestRatingTask().execute(accessKey, accessSecret, ratingUrl + "?title_refs="
                + movieTitle.getID());
    }

    private void executeSetRatingTask(String setUrl) {
        new SetRatingTask().execute(accessKey, accessSecret, setUrl);
    }

    private View buildIndicator(int textRes) {
        final TextView indicator = (TextView) getLayoutInflater().inflate(R.layout.tab_indicator,
                getTabWidget(), false);
        indicator.setText(textRes);
        return indicator;
    }

    public void onHomeClick(View v) {
        UIUtils.goHome(this);
    }

    public void onAddClick(View v) {
        String formats = "D";
        if (instantAvaliable == true && discAvaliable == true)
            formats = "B";
        else if (instantAvaliable == false && discAvaliable == true)
            formats = "D";
        else if (instantAvaliable == true && discAvaliable == false)
            formats = "I";

        Intent addTitle = new Intent(this, AddTitleActivity.class);
        addTitle.putExtra("TitleRef", movieTitle.getID());
        addTitle.putExtra("Formats", formats);
        startActivity(addTitle);
    }

    public void onSearchClick(View v) {
        UIUtils.goSearch(this);
    }

    private class RequestRatingTask extends AsyncTask<String, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(DisplayMovieDetailsActivity.this);
        private String message = null;
        private Item rating;

        @Override
        protected void onPreExecute() {
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

        @Override
        protected Void doInBackground(String... parms) {

            try {
                NetflixParser postParser = new NetflixParser(parms[0], parms[1]);
                postParser.parseFeed(parms[2]);
                rating = postParser.getItem();

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
            message = "Operation Cancelled";
            Toast.makeText(DisplayMovieDetailsActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void result) {

            ratingId = null;
            if (rating != null) {
                try {
                    currentRating = Float.parseFloat(rating.getUserRating());
                } catch (NullPointerException e) {
                    currentRating = (float) 0.0;
                }
                ratingId = rating.getID();
            }

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            if (message != null)
                Toast.makeText(DisplayMovieDetailsActivity.this, message, Toast.LENGTH_LONG).show();

            if (ratingId != null)
                showDialog(RATING_DIALOG);
            else
                Toast.makeText(DisplayMovieDetailsActivity.this, "Title cannot be rated.",
                        Toast.LENGTH_LONG).show();
        }
    }

    private class SetRatingTask extends AsyncTask<String, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(DisplayMovieDetailsActivity.this);
        private String message = null;

        @Override
        protected void onPreExecute() {
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

        @Override
        protected Void doInBackground(String... parms) {

            try {
                NetflixParser postParser = new NetflixParser(parms[0], parms[1]);
                postParser.postData(parms[2], null, null, null, null);
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
            message = "Operation Cancelled";
            Toast.makeText(DisplayMovieDetailsActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void result) {

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            if (message != null)
                Toast.makeText(DisplayMovieDetailsActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
}
