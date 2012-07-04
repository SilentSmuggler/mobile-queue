
package com.silentlabs.android.mobilequeue.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.silentlabs.android.mobilequeue.R;
import com.silentlabs.android.mobilequeue.util.UIUtils;

public class MainActivity extends SherlockFragmentActivity {

    private final static String ACCESS = "MobileQueueAccess";

    private SharedPreferences access;

    /** Handle "at home" action. */
    public void onAtHomeClick(View v) {
        Intent atHomeIntent = new Intent(MainActivity.this, BrowseActivity.class);
        atHomeIntent.putExtra("QUEUE", getString(R.string.athome_URL));
        atHomeIntent.putExtra("TYPE", "ATHOME");
        startActivity(atHomeIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(R.string.app_name);

        access = getSharedPreferences(ACCESS, Context.MODE_PRIVATE);
        String accessKey = access.getString("ACCESS_KEY", null);
        String accessSecret = access.getString("ACCESS_SECRET", null);
        if (accessKey == null && accessSecret == null) {
            Intent register = new Intent(this, RegisterAppActivity.class);
            register.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(register);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Handle "disc queue" action. */
    public void onDiscQueueClick(View v) {
        Intent discIntent = new Intent(MainActivity.this, BrowseActivity.class);
        discIntent.putExtra("QUEUE", getString(R.string.disc_queueURL));
        discIntent.putExtra("TYPE", "QUEUE");
        startActivity(discIntent);
    }

    /** Handle "history" action. */
    public void onHistoryClick(View v) {
        Intent historyIntent = new Intent(MainActivity.this, BrowseActivity.class);
        historyIntent.putExtra("QUEUE", getString(R.string.history_URL));
        historyIntent.putExtra("TYPE", "HISTORY");
        startActivity(historyIntent);
    }

    /** Handle "instant queue" action. */
    public void onInstantQueueClick(View v) {
        Intent instantIntent = new Intent(MainActivity.this, BrowseActivity.class);
        instantIntent.putExtra("QUEUE", getString(R.string.instant_queueURL));
        instantIntent.putExtra("TYPE", "QUEUE");
        startActivity(instantIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_search) {
            UIUtils.goSearch(this);
            return true;
        } else if (item.getItemId() == R.id.menu_display_profile) {
            startActivity(new Intent(this, DisplayProfileActivity.class));
            return true;
        } else if (item.getItemId() == R.id.menu_prefrences) {
            startActivity(new Intent(this, PreferencesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        access = getSharedPreferences(ACCESS, Context.MODE_PRIVATE);
        String accessKey = access.getString("ACCESS_KEY", null);
        String accessSecret = access.getString("ACCESS_SECRET", null);
        if (accessKey == null && accessSecret == null) {
            finish();
        }
    }

    /** Handle "suggestions" action. */
    public void onSuggestionsClick(View v) {
        Intent suggestIntent = new Intent(MainActivity.this, BrowseActivity.class);
        suggestIntent.putExtra("QUEUE", getString(R.string.recommendations_URL));
        suggestIntent.putExtra("TYPE", "BROWSE");
        startActivity(suggestIntent);
    }
}
