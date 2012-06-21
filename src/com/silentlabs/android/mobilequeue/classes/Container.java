
package com.silentlabs.android.mobilequeue.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Container implements Serializable {

    private static final long serialVersionUID = -4795203965944076411L;

    // Details in ArrayList
    private final ArrayList<Item> items;

    // Common Variables
    private int numberOfResults;
    private int startIndex;
    private int resultsPerPage;

    // Queue Variables
    private String etag;

    public Container() {
        items = new ArrayList<Item>();
    }

    public ArrayList<Item> getArrayItems() {
        return items;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public String getETag() {
        return etag;
    }

    public void setItem(Item _item) {
        items.add(_item);
    }

    public void setNumberOfResults(int _numberofresults) {
        numberOfResults = _numberofresults;
    }

    public void setStartIndex(int _startindex) {
        startIndex = _startindex;
    }

    public void setResultsPerPage(int _resultsperpage) {
        resultsPerPage = _resultsperpage;
    }

    public void setETag(String _etag) {
        etag = _etag;
    }
}
