
package com.silentlabs.android.mobilequeue.parser;

import com.silentlabs.android.mobilequeue.classes.Container;
import com.silentlabs.android.mobilequeue.classes.Item;
import com.silentlabs.android.mobilequeue.classes.User;
import com.silentlabs.android.mobilequeue.oauth.NetflixAccess;

import org.apache.http.client.ClientProtocolException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.NetflixApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NetflixParser extends DefaultHandler {

    private boolean inETag = false;
    private boolean inNumberOfResults = false;
    private boolean inStartIndex = false;
    private boolean inResultsPerPage = false;
    private boolean inUserRating = false;
    private boolean inPredictedRating = false;
    private boolean inId = false;
    private boolean inPostion = false;
    private boolean inUpdated = false;
    private boolean inReleaseYear = false;
    private boolean inRuntime = false;
    private boolean inAverageRating = false;
    private boolean inSynopsis = false;
    private boolean inStatusCode = false;
    private boolean inMessage = false;
    private boolean inUserId = false;
    private boolean inFirstName = false;
    private boolean inLastName = false;
    private boolean inNickName = false;
    private boolean inInstantWatch = false;
    private boolean inShippedDate = false;
    private boolean inArrivalDate = false;
    private boolean inReturnedDate = false;
    private boolean inWatchedDate = false;
    private boolean inViewedTime = false;

    private Container container;
    private Item item;
    private User user;

    private final StringBuilder text;
    private String statusCode;
    private String message;
    private String eTag;
    private String availableFrom;
    private String availableTo;

    // private Scribe mScribe;
    private final OAuthService mOAuthservice;
    private final Token accessToken;

    public NetflixParser(String _accessKey, String _accessSecret) throws MalformedURLException {

        mOAuthservice = new ServiceBuilder()
                .provider(NetflixApi.class)
                .apiKey(NetflixAccess.getConsumerKey())
                .apiSecret(NetflixAccess.getConsumerSecret())
                .build();

        accessToken = new Token(_accessKey, _accessSecret);

        text = new StringBuilder();
    }

    public void parseFeed(String urlString) throws Exception {

        OAuthRequest request = new OAuthRequest(Verb.GET, urlString);

        mOAuthservice.signRequest(accessToken, request);
        Response response = request.send();

        InputStream inXML = null;
        inXML = response.getStream();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();

        sp.parse(inXML, this);

        inXML.close();
    }

    public void postData(String urlString, String titleRef, String _eTag, String position,
            String format) throws ClientProtocolException, IOException, Exception {

        String url = urlString;

        // Add Title Reference
        if (titleRef != null)
            url = url + "?title_ref=" + titleRef + "&expand=synopsis,formats";

        // add eTag
        if (_eTag != null)
            url = url + "&etag=" + _eTag;

        if (position != null)
            url = url + "&position=" + position;

        if (format != null)
            url = url + "&format=" + format;

        OAuthRequest request = new OAuthRequest(Verb.POST, url);

        mOAuthservice.signRequest(accessToken, request);
        Response response = request.send();

        InputStream inXML = null;
        inXML = response.getStream();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();

        sp.parse(inXML, this);

        inXML.close();
    }

    public void deleteData(String titleRef, String eTag) throws ClientProtocolException,
            IOException, Exception {

        String url = titleRef + "?etag=" + eTag + "&method=delete";

        OAuthRequest request = new OAuthRequest(Verb.POST, url);

        mOAuthservice.signRequest(accessToken, request);
        Response response = request.send();

        InputStream inXML = null;
        inXML = response.getStream();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();

        sp.parse(inXML, this);

        inXML.close();
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes atts) {
        if (name.trim().equals("queue") || name.trim().equals("recommendations")
                || name.trim().equals("catalog_titles") ||
                name.trim().equals("at_home") || name.trim().equals("rental_history")
                || name.trim().equals("similars")) {
            if (this.container == null)
                this.container = new Container();
        }

        else if (name.trim().equals("queue_item") || name.trim().equals("recommendation")
                || name.trim().equals("catalog_title") ||
                name.trim().equals("ratings_item") || name.trim().equals("at_home_item")
                || name.trim().equals("rental_history_item") ||
                name.trim().equals("similars_item")) {
            if (this.item == null)
                this.item = new Item();
        }

        else if (name.trim().equals("user")) {
            if (this.user == null)
                this.user = new User();
        }

        else if (name.trim().equals("etag"))
            inETag = true;

        else if (name.trim().equals("number_of_results"))
            inNumberOfResults = true;

        else if (name.trim().equals("start_index"))
            inStartIndex = true;

        else if (name.trim().equals("results_per_page"))
            inResultsPerPage = true;

        else if (name.trim().equals("user_rating"))
            inUserRating = true;

        else if (name.trim().equals("predicted_rating"))
            inPredictedRating = true;

        else if (name.trim().equals("id"))
            inId = true;

        else if (name.trim().equals("position"))
            inPostion = true;

        else if (name.trim().equals("updated"))
            inUpdated = true;

        else if (name.trim().equals("shipped_date"))
            inShippedDate = true;

        else if (name.trim().equals("estimated_arrival_date"))
            inArrivalDate = true;

        else if (name.trim().equals("returned_date"))
            inReturnedDate = true;

        else if (name.trim().equals("watched_date"))
            inWatchedDate = true;

        else if (name.trim().equals("viewed_time"))
            inViewedTime = true;

        else if (name.trim().equals("release_year"))
            inReleaseYear = true;

        else if (name.trim().equals("runtime"))
            inRuntime = true;

        else if (name.trim().equals("average_rating"))
            inAverageRating = true;

        else if (name.trim().equals("synopsis"))
            inSynopsis = true;

        else if (name.trim().equals("status_code"))
            inStatusCode = true;

        else if (name.trim().equals("message"))
            inMessage = true;

        else if (name.trim().equals("user_id"))
            inUserId = true;

        else if (name.trim().equals("first_name"))
            inFirstName = true;

        else if (name.trim().equals("last_name"))
            inLastName = true;

        else if (name.trim().equals("nickname"))
            inNickName = true;

        else if (name.trim().equals("can_instant_watch"))
            inInstantWatch = true;

        else if (name.trim().equals("title")) {
            this.item.setTitleShort(atts.getValue("short"));
            this.item.setTitleReqular(atts.getValue("regular"));
        }

        else if (name.trim().equals("box_art")) {
            try {
                this.item.setBoxArtSmall(new URL(atts.getValue("small")));
                this.item.setBoxArtMedium(new URL(atts.getValue("medium")));
                this.item.setBoxArtLarge(new URL(atts.getValue("large")));

            } catch (MalformedURLException e) {

            }
        }

        else if (name.trim().equals("category")) {
            for (int i = 0; i < atts.getLength(); i++) {

                if (atts.getValue(i).trim().equals("http://api.netflix.com/categories/genres")) {
                    this.item.addGenre(atts.getValue("term"));

                } else if (atts.getValue(i).trim()
                        .equals("http://api.netflix.com/categories/mpaa_ratings")) {
                    this.item.setMpaaRatings(atts.getValue("term"));

                } else if (atts.getValue(i).trim()
                        .equals("http://api.netflix.com/categories/tv_ratings")) {
                    this.item.setTvRatings(atts.getValue("term"));

                } else if (atts.getValue(i).trim()
                        .equals("http://api.netflix.com/categories/title_formats")) {
                    if (item != null) {
                        String media = atts.getValue("term");
                        this.item.addFormats(media);

                        if (media.equalsIgnoreCase("Blu-ray")) {
                            if (availableFrom != null)
                                this.item.setAvailableFromBlu(Long.parseLong(availableFrom));
                            else
                                this.item.setAvailableFromBlu(Long.parseLong("0"));

                            if (availableTo != null)
                                this.item.setAvailableToBlu(Long.parseLong(availableTo));
                            else
                                this.item.setAvailableToBlu(Long.parseLong("9999999999"));

                        } else if (media.equalsIgnoreCase("DVD")) {
                            if (availableFrom != null)
                                this.item.setAvailableFromDisc(Long.parseLong(availableFrom));
                            else
                                this.item.setAvailableFromDisc(Long.parseLong("0"));

                            if (availableTo != null)
                                this.item.setAvailableToDisc(Long.parseLong(availableTo));
                            else
                                this.item.setAvailableToDisc(Long.parseLong("9999999999"));

                        } else if (media.equalsIgnoreCase("instant")) {
                            if (availableFrom != null)
                                this.item.setAvailableFromInstant(Long.parseLong(availableFrom));
                            else
                                this.item.setAvailableFromInstant(Long.parseLong("0"));

                            if (availableTo != null)
                                this.item.setAvailableToInstant(Long.parseLong(availableTo));
                            else
                                this.item.setAvailableToInstant(Long.parseLong("9999999999"));
                        }

                    } else if (user != null) {
                        this.user.addPreferredFormat(atts.getValue("term"));
                    }

                } else if (atts.getValue(i).trim()
                        .equals("http://api.netflix.com/categories/queue_availability")) {
                    this.item.setAvailability(atts.getValue("term"));

                } else if (atts.getValue(i).trim()
                        .equals("http://api.netflix.com/categories/rental_states")) {
                    this.item.setRentalStates(atts.getValue("term"));
                }
            }
        }

        else if (name.trim().equals("link")) {
            for (int i = 0; i < atts.getLength(); i++) {

                if (atts.getValue(i).trim().equals("similars")) {
                    this.item.setLinkId(atts.getValue("href"));
                }
            }
        }

        else if (name.trim().equals("availability")) {
            availableFrom = atts.getValue("available_from");
            availableTo = atts.getValue("available_to");
        }
    }

    @Override
    public void endElement(String uri, String name, String qName) {
        if (name.trim().equals("queue_item") || name.trim().equals("recommendation")
                || name.trim().equals("catalog_title") ||
                name.trim().equals("at_home_item") || name.trim().equals("rental_history_item")
                || name.trim().equals("similars_item")) {
            if (container != null) {
                this.container.setItem(item);
                this.item = null;
            }
        }

        else if (name.trim().equals("etag"))
            inETag = false;

        else if (name.trim().equals("number_of_results"))
            inNumberOfResults = false;

        else if (name.trim().equals("start_index"))
            inStartIndex = false;

        else if (name.trim().equals("results_per_page"))
            inResultsPerPage = false;

        else if (name.trim().equals("user_rating"))
            inUserRating = false;

        else if (name.trim().equals("predicted_rating"))
            inPredictedRating = false;

        else if (name.trim().equals("id"))
            inId = false;

        else if (name.trim().equals("position"))
            inPostion = false;

        else if (name.trim().equals("updated"))
            inUpdated = false;

        else if (name.trim().equals("shipped_date"))
            inShippedDate = false;

        else if (name.trim().equals("estimated_arrival_date"))
            inArrivalDate = false;

        else if (name.trim().equals("returned_date"))
            inReturnedDate = false;

        else if (name.trim().equals("watched_date"))
            inWatchedDate = false;

        else if (name.trim().equals("viewed_time"))
            inViewedTime = false;

        else if (name.trim().equals("release_year"))
            inReleaseYear = false;

        else if (name.trim().equals("runtime"))
            inRuntime = false;

        else if (name.trim().equals("average_rating"))
            inAverageRating = false;

        else if (name.trim().equals("synopsis"))
            inSynopsis = false;

        else if (name.trim().equals("status_code"))
            inStatusCode = false;

        else if (name.trim().equals("message"))
            inMessage = false;

        else if (name.trim().equals("user_id"))
            inUserId = false;

        else if (name.trim().equals("first_name"))
            inFirstName = false;

        else if (name.trim().equals("last_name"))
            inLastName = false;

        else if (name.trim().equals("nickname"))
            inNickName = false;

        else if (name.trim().equals("can_instant_watch"))
            inInstantWatch = false;

        this.text.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        text.append(ch, start, length);

        if (inETag) {
            if (this.container != null)
                this.container.setETag(text.toString().trim());
            else if (this.container == null)
                this.eTag = text.toString().trim();

        } else if (inNumberOfResults) {
            this.container.setNumberOfResults(Integer.parseInt(text.toString().trim()));

        } else if (inStartIndex) {
            this.container.setStartIndex(Integer.parseInt(text.toString().trim()));

        } else if (inResultsPerPage) {
            this.container.setResultsPerPage(Integer.parseInt(text.toString().trim()));

        } else if (inUserRating) {
            this.item.setUserRating(text.toString().trim());

        } else if (inPredictedRating) {
            this.item.setPredictedRating(text.toString().trim());

        } else if (inId) {
            this.item.setID(text.toString().trim());

        } else if (inPostion) {
            this.item.setPosition(Integer.parseInt(text.toString().trim()));

        } else if (inUpdated) {
            this.item.setUpdated(Long.parseLong(text.toString().trim()));

        } else if (inShippedDate) {
            this.item.setShippedDate(Long.parseLong(text.toString().trim()));

        } else if (inArrivalDate) {
            this.item.setArrivalDate(Long.parseLong(text.toString().trim()));

        } else if (inReturnedDate) {
            this.item.setReturnedDate(Long.parseLong(text.toString().trim()));

        } else if (inWatchedDate) {
            this.item.setWatchedDate(Long.parseLong(text.toString().trim()));

        } else if (inViewedTime) {
            this.item.setviewedTime(text.toString().trim());

        } else if (inReleaseYear) {
            this.item.setReleaseYear(text.toString().trim());

        } else if (inRuntime) {
            this.item.setRuntime(text.toString().trim());

        } else if (inAverageRating) {
            this.item.setAverageRating(text.toString().trim());

        } else if (inSynopsis) {
            this.item.setSynopsis(text.toString().trim());

        } else if (inStatusCode) {
            this.statusCode = text.toString().trim();

        } else if (inMessage) {
            this.message = text.toString().trim();

        } else if (inUserId)
            this.user.setUserId(text.toString().trim());

        else if (inFirstName)
            this.user.setFirstName(text.toString().trim());

        else if (inLastName)
            this.user.setLastName(text.toString().trim());

        else if (inNickName)
            this.user.setNickName(text.toString().trim());

        else if (inInstantWatch)
            this.user.setInstantWatch(Boolean.parseBoolean(text.toString().trim()));
    }

    public Container getContainer() {
        return this.container;
    }

    public Item getItem() {
        return this.item;
    }

    public User getUser() {
        return this.user;
    }

    public String getETag() {
        return this.eTag;
    }

    public String getStatus() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.message;
    }

    public String getStatusMessage() {
        return this.statusCode + ": " + this.message;
    }
}
