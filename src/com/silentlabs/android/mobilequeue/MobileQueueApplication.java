
package com.silentlabs.android.mobilequeue;

import android.app.Application;

public class MobileQueueApplication extends Application {

    // private Item item;
    private String DiscETag;
    private String InstantETag;

    public String getDiscETag() {
        return DiscETag;
    }

    public String getInstantETag() {
        return InstantETag;
    }

    public void setDiscETag(String _eTag) {
        if (_eTag != null)
            DiscETag = _eTag;
    }

    public void setInstantETag(String _eTag) {
        if (_eTag != null)
            InstantETag = _eTag;
    }
}
