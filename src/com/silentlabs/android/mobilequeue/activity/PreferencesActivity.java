
package com.silentlabs.android.mobilequeue.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.silentlabs.android.mobilequeue.R;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
