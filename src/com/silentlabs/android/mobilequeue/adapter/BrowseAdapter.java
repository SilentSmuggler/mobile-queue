
package com.silentlabs.android.mobilequeue.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.silentlabs.android.mobilequeue.R;
import com.silentlabs.android.mobilequeue.classes.Item;
import com.silentlabs.android.mobilequeue.util.AsyncImageLoader;
import com.silentlabs.android.mobilequeue.util.AsyncImageLoader.ImageCallback;

import java.net.URL;
import java.util.Date;
import java.util.List;

public class BrowseAdapter extends ArrayAdapter<Item> {

    private final ListView listView;
    private final AsyncImageLoader asyncImageLoader;
    private final SharedPreferences settings;

    private final Date currentDate = new Date();
    private final Activity activity;
    private final int resource;

    public BrowseAdapter(Activity activity, int _resource, List<Item> imageAndTexts,
            ListView listView) {
        super(activity, _resource, imageAndTexts);

        this.activity = activity;
        this.resource = _resource;
        this.listView = listView;

        settings = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());

        asyncImageLoader = new AsyncImageLoader(activity.getResources());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate the views from XML
        View rowView = convertView;
        ViewCache viewCache;
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(resource, null);
            viewCache = new ViewCache(rowView);
            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }
        Item imageAndText = getItem(position);

        // Load the image and set it on the ImageView
        String boxArtSize = settings.getString("boxArtPref", "Medium");
        URL imageUrl = null;
        if (boxArtSize.equals("Small")) {
            imageUrl = imageAndText.getBoxArtSmall();
        } else if (boxArtSize.equals("Medium")) {
            imageUrl = imageAndText.getBoxArtMedium();
        } else if (boxArtSize.equals("Large")) {
            imageUrl = imageAndText.getBoxArtLarge();
        }
        ImageView boxArtView = viewCache.getImageView();
        boxArtView.setTag(imageUrl);
        try {
            Bitmap cachedImage = asyncImageLoader.loadBitmap(imageUrl, new ImageCallback() {
                @Override
                public void imageLoaded(Bitmap imageBitmap, URL imageUrl) {
                    ImageView imageViewByTag = null;

                    try {
                        imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);

                        if (imageViewByTag != null) {
                            imageViewByTag.setImageBitmap(imageBitmap);
                        }
                    } catch (RuntimeException e) {

                    }
                }
            });
            if (cachedImage != null)
                boxArtView.setImageBitmap(cachedImage);
            else
                boxArtView.setImageResource(R.drawable.loading);
        } catch (NullPointerException e) {
            boxArtView.setImageResource(R.drawable.netflix_branding);
        }

        // Set the position on the TextView
        TextView positionView = viewCache.getPositionView();
        try {
            String positionString = String.valueOf(imageAndText.getPosition());
            if (positionString.equals("0"))
                positionView.setText("");
            else
                positionView.setText(String.valueOf(position + 1));
            // positionView.setText(positionString);
        } catch (NullPointerException e) {
            positionView.setText("");
        }

        // Set the title on the TextView
        TextView titleView = viewCache.getTitleView();
        try {
            titleView.setText(imageAndText.getTitleReqular());
        } catch (NullPointerException e) {
            titleView.setText("");
        }

        // Set the release year on the TextView
        TextView yearView = viewCache.getYearView();
        try {
            yearView.setText("(" + imageAndText.getReleaseYear() + ")");
        } catch (NullPointerException e) {
            yearView.setText("");
        }

        // Set the genre on the TextView
        TextView genreView = viewCache.getGenreView();
        try {
            genreView.setText(imageAndText.getGenres().get(0));
        } catch (NullPointerException e) {
            genreView.setText("");
        }

        // Set the formats on the TextView
        ImageView ratingView = viewCache.getRatingView();
        try {
            String mpaaRatingString = imageAndText.getMpaaRatings().trim();
            if (mpaaRatingString != null) {
                ratingView.setVisibility(View.VISIBLE);
                if (mpaaRatingString.equalsIgnoreCase("G"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_g);
                else if (mpaaRatingString.equalsIgnoreCase("PG"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_pg);
                else if (mpaaRatingString.equalsIgnoreCase("PG-13"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_pg13);
                else if (mpaaRatingString.equalsIgnoreCase("R"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_r);
                else if (mpaaRatingString.equalsIgnoreCase("NC-17"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_nc17);
                else if (mpaaRatingString.equalsIgnoreCase("NR"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_nr);
                else if (mpaaRatingString.equalsIgnoreCase("UR"))
                    ratingView.setImageResource(R.drawable.ic_movie_rating_ur);

                ratingView.setVisibility(View.VISIBLE);
            }

        } catch (NullPointerException e) {

            try {
                String tvRatingString = imageAndText.getTvRatings().trim();
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

                    ratingView.setVisibility(View.VISIBLE);
                }

            } catch (NullPointerException e1) {
                ratingView.setVisibility(View.GONE);
            }
        }

        ImageView formatsView = viewCache.getFormatView();
        try {
            String format = imageAndText.getFormats().get(0).trim();
            if (format != null) {
                formatsView.setVisibility(View.VISIBLE);
                if (format.equalsIgnoreCase("Blu-ray")) {
                    formatsView.setImageResource(R.drawable.ic_blu_ray);

                } else if (format.equalsIgnoreCase("DVD")) {
                    formatsView.setImageResource(R.drawable.ic_dvd);

                } else if (format.equalsIgnoreCase("Instant")) {
                    if (currentDate.after(imageAndText.getAvailableFromInstant())
                            && currentDate.before(imageAndText.getAvailableToInstant()))
                        formatsView.setImageResource(R.drawable.ic_instant);
                    else
                        formatsView.setImageResource(R.drawable.ic_dvd);
                }
            }
        } catch (NullPointerException e) {
            formatsView.setVisibility(View.INVISIBLE);
        }

        // Set the rating on the RatingBar
        RatingBar ratingBarView = viewCache.getRatingBarView();
        try {
            ratingBarView.setRating(Float.parseFloat(imageAndText.getAverageRating()));
        } catch (NullPointerException e) {
            ratingBarView.setRating((float) 0.0);
        }

        // Set the Availability on the TextView
        TextView availabilityView = viewCache.getAvailabilityView();
        try {
            availabilityView.setText(imageAndText.getAvailability());
        } catch (NullPointerException e) {
            availabilityView.setText("");
        }

        // Set the Rental States on the TextView
        TextView rentalStatesView = viewCache.getRentalStatesView();
        try {
            rentalStatesView.setText(imageAndText.getRentalStates());
        } catch (NullPointerException e) {
            rentalStatesView.setText("");
        }

        return rowView;
    }

    public class ViewCache {

        private final View baseView;
        private ImageView boxArtView;
        private TextView positionView;
        private TextView titleView;
        private TextView yearView;
        private TextView genreView;
        private ImageView ratingView;
        private ImageView formatView;
        private RatingBar ratingBarView;
        private TextView availabilityView;
        private TextView rentalStatesView;

        public ViewCache(View baseView) {
            this.baseView = baseView;
        }

        public ImageView getImageView() {
            if (boxArtView == null) {
                boxArtView = (ImageView) baseView.findViewById(R.id.BoxArtView);
            }
            return boxArtView;
        }

        public TextView getPositionView() {
            if (positionView == null) {
                positionView = (TextView) baseView.findViewById(R.id.PositionView);
            }
            return positionView;
        }

        public TextView getTitleView() {
            if (titleView == null) {
                titleView = (TextView) baseView.findViewById(R.id.TitleView);
            }
            return titleView;
        }

        public TextView getYearView() {
            if (yearView == null) {
                yearView = (TextView) baseView.findViewById(R.id.YearView);
            }
            return yearView;
        }

        public TextView getGenreView() {
            if (genreView == null) {
                genreView = (TextView) baseView.findViewById(R.id.GenreView);
            }
            return genreView;
        }

        public ImageView getRatingView() {
            if (ratingView == null) {
                ratingView = (ImageView) baseView.findViewById(R.id.RatingView);
            }
            return ratingView;
        }

        public ImageView getFormatView() {
            if (formatView == null) {
                formatView = (ImageView) baseView.findViewById(R.id.FormatView);
            }
            return formatView;
        }

        public RatingBar getRatingBarView() {
            if (ratingBarView == null) {
                ratingBarView = (RatingBar) baseView.findViewById(R.id.RatingBarView);
            }
            return ratingBarView;
        }

        public TextView getAvailabilityView() {
            if (availabilityView == null) {
                availabilityView = (TextView) baseView.findViewById(R.id.AvailabilityView);
            }
            return availabilityView;
        }

        public TextView getRentalStatesView() {
            if (rentalStatesView == null) {
                rentalStatesView = (TextView) baseView.findViewById(R.id.RentalStateView);
            }
            return rentalStatesView;
        }
    }
}
