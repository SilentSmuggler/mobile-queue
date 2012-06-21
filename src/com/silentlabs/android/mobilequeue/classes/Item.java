
package com.silentlabs.android.mobilequeue.classes;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class Item implements Serializable {

    private static final long serialVersionUID = 1165034131364487251L;

    // Common Variables
    private int position;
    private String id;
    private String linkId;
    private String titleShort;
    private String titleReqular;
    private Date updated;
    private Date availableFromDisc;
    private Date availableToDisc;
    private Date availableFromBlu;
    private Date availableToBlu;
    private Date availableFromInstant;
    private Date availableToInstant;
    private Date shippedDate;
    private Date arrivalDate;
    private Date returnedDate;
    private Date watchedDate;
    private String viewedTime;
    private String averageRating;
    private String predictedRating;
    private String userRating;
    private String releaseYear;
    private String runtime;
    private String mpaaRatings;
    private String tvRatings;
    private String availability;
    private String synopsis;
    private String rentalStates;
    private URL boxArtSmall;
    private URL boxArtMedium;
    private URL boxArtLarge;
    private ArrayList<String> genres;
    private ArrayList<String> formats;

    public Item() {

    }

    public String getID() {
        return id;
    }

    public String getLinkId() {
        return linkId;
    }

    public Date getUpdated() {
        return updated;
    }

    public Date getAvailableFromDisc() {
        return availableFromDisc;
    }

    public Date getAvailableToDisc() {
        return availableToDisc;
    }

    public Date getAvailableFromBlu() {
        return availableFromBlu;
    }

    public Date getAvailableToBlu() {
        return availableToBlu;
    }

    public Date getAvailableFromInstant() {
        return availableFromInstant;
    }

    public Date getAvailableToInstant() {
        return availableToInstant;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public Date getArrivalDateDate() {
        return arrivalDate;
    }

    public Date getReturnedDateDate() {
        return returnedDate;
    }

    public Date getWatchedDateDate() {
        return watchedDate;
    }

    public String getviewedTime() {
        return viewedTime;
    }

    public String getTitleShort() {
        return titleShort;
    }

    public String getTitleReqular() {
        return titleReqular;
    }

    public URL getBoxArtSmall() {
        return boxArtSmall;
    }

    public URL getBoxArtMedium() {
        return boxArtMedium;
    }

    public URL getBoxArtLarge() {
        return boxArtLarge;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public int getPosition() {
        return position;
    }

    public String getPredictedRating() {
        return predictedRating;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getMpaaRatings() {
        return mpaaRatings;
    }

    public String getTvRatings() {
        return tvRatings;
    }

    public String getAvailability() {
        return availability;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getRentalStates() {
        return rentalStates;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public ArrayList<String> getFormats() {
        return formats;
    }

    public void setID(String _id) {
        id = _id;
    }

    public void setLinkId(String _id) {
        linkId = _id;
    }

    public void setUpdated(long _sinceEpoch) {
        updated = new Date(_sinceEpoch * 1000);
    }

    public void setAvailableFromDisc(long _sinceEpoch) {
        availableFromDisc = new Date(_sinceEpoch * 1000);
    }

    public void setAvailableToDisc(long _sinceEpoch) {
        availableToDisc = new Date(_sinceEpoch * 1000);
    }

    public void setAvailableFromBlu(long _sinceEpoch) {
        availableFromBlu = new Date(_sinceEpoch * 1000);
    }

    public void setAvailableToBlu(long _sinceEpoch) {
        availableToBlu = new Date(_sinceEpoch * 1000);
    }

    public void setAvailableFromInstant(long _sinceEpoch) {
        availableFromInstant = new Date(_sinceEpoch * 1000);
    }

    public void setAvailableToInstant(long _sinceEpoch) {
        availableToInstant = new Date(_sinceEpoch * 1000);
    }

    public void setTitleShort(String _titleshort) {
        titleShort = _titleshort;
    }

    public void setShippedDate(long _sinceEpoch) {
        shippedDate = new Date(_sinceEpoch * 1000);
    }

    public void setArrivalDate(long _sinceEpoch) {
        arrivalDate = new Date(_sinceEpoch * 1000);
    }

    public void setReturnedDate(long _sinceEpoch) {
        returnedDate = new Date(_sinceEpoch * 1000);
    }

    public void setWatchedDate(long _sinceEpoch) {
        watchedDate = new Date(_sinceEpoch * 1000);
    }

    public void setviewedTime(String _viewtime) {
        viewedTime = _viewtime;
    }

    public void setTitleReqular(String _titlereqular) {
        titleReqular = _titlereqular;
    }

    public void setBoxArtSmall(URL _boxartsmall) {
        boxArtSmall = _boxartsmall;
    }

    public void setBoxArtMedium(URL _boxartmedium) {
        boxArtMedium = _boxartmedium;
    }

    public void setBoxArtLarge(URL _boxartlarge) {
        boxArtLarge = _boxartlarge;
    }

    public void setReleaseYear(String _releaseyear) {
        releaseYear = _releaseyear;
    }

    public void setRuntime(String _runtime) {
        runtime = _runtime;
    }

    public void setAverageRating(String _averagerating) {
        averageRating = _averagerating;
    }

    public void setPosition(int _postion) {
        position = _postion;
    }

    public void setPredictedRating(String _predictedRating) {
        predictedRating = _predictedRating;
    }

    public void setUserRating(String _userRating) {
        userRating = _userRating;
    }

    public void setMpaaRatings(String _mpaaRatings) {
        mpaaRatings = _mpaaRatings;
    }

    public void setTvRatings(String _tvRatings) {
        tvRatings = _tvRatings;
    }

    public void setAvailability(String _availability) {
        availability = _availability;
    }

    public void setSynopsis(String _synopsis) {
        synopsis = _synopsis;
    }

    public void setRentalStates(String _rentalstates) {
        rentalStates = _rentalstates;
    }

    public void addGenre(String _genre) {
        if (genres == null)
            genres = new ArrayList<String>();

        genres.add(_genre);
    }

    public void addFormats(String _formats) {
        if (formats == null)
            formats = new ArrayList<String>();

        formats.add(_formats);
    }
}
