
package com.silentlabs.android.mobilequeue.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.silentlabs.android.mobilequeue.activity.MainActivity;

public class UIUtils {

    /**
     * Invoke "home" action, returning to {@link MainActivity}.
     */
    public static void goHome(Context context) {
        final Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * Invoke "search" action, triggering a default search.
     */
    public static void goSearch(Activity activity) {
        activity.startSearch(null, false, Bundle.EMPTY, false);
    }
}
