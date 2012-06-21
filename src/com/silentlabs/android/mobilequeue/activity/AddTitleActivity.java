
package com.silentlabs.android.mobilequeue.activity;

import android.app.Activity;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.silentlabs.android.mobilequeue.MobileQueueApplication;
import com.silentlabs.android.mobilequeue.R;
import com.silentlabs.android.mobilequeue.parser.NetflixParser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;

public class AddTitleActivity extends Activity {

    private final static String ACCESS = "MobileQueueAccess";

    private MobileQueueApplication app;
    private SharedPreferences access;
    private SharedPreferences settings;
    private String accessKey;
    private String accessSecret;
    private String userid;

    private String queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_title_layout);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        app = ((MobileQueueApplication) getApplicationContext());

        Intent intent = getIntent();
        final String titleRef = intent.getStringExtra("TitleRef");
        final String formats = intent.getStringExtra("Formats");

        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        access = getSharedPreferences(ACCESS, Context.MODE_PRIVATE);
        accessKey = access.getString("ACCESS_KEY", null);
        accessSecret = access.getString("ACCESS_SECRET", null);
        userid = access.getString("USER_ID", null);

        final String formatValue = settings.getString("movieFPref", "1");

        final Spinner queueSpinner = (Spinner) findViewById(R.id.queue_spinner);
        final Spinner formatSpinner = (Spinner) findViewById(R.id.format_spinner);
        final TextView formatTextView = (TextView) findViewById(R.id.format_textView);

        ArrayAdapter<CharSequence> adapter = null;
        if (formats.equals("B"))
            adapter = ArrayAdapter.createFromResource(AddTitleActivity.this, R.array.both_queues,
                    android.R.layout.simple_spinner_item);
        else if (formats.equals("D"))
            adapter = ArrayAdapter.createFromResource(AddTitleActivity.this, R.array.disc_queues,
                    android.R.layout.simple_spinner_item);
        else if (formats.equals("I"))
            adapter = ArrayAdapter.createFromResource(AddTitleActivity.this,
                    R.array.instant_queues, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        queueSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = null;
        if ((formats.equals("B") || formats.equals("D")) && formatValue.equals("4")) {
            formatSpinner.setVisibility(View.VISIBLE);
            formatTextView.setVisibility(View.VISIBLE);

            // if (formats.equals("Both"))
            adapter2 = ArrayAdapter.createFromResource(AddTitleActivity.this, R.array.both_formats,
                    android.R.layout.simple_spinner_item);
            // else if (formats.equals("DVD"))
            // adapter2 = ArrayAdapter.createFromResource(AddTitleActivity.this,
            // R.array.dvd_format, android.R.layout.simple_spinner_item);
            // else if (formats.equals("Blu-ray"))
            // adapter2 = ArrayAdapter.createFromResource(AddTitleActivity.this,
            // R.array.blu_format, android.R.layout.simple_spinner_item);

            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            formatSpinner.setAdapter(adapter2);

        } else {
            formatSpinner.setVisibility(View.GONE);
            formatTextView.setVisibility(View.GONE);
        }

        Button addTitleButton = (Button) findViewById(R.id.add_title_button);
        addTitleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String UrlString = null;
                String format = null;

                queue = (String) queueSpinner.getSelectedItem();

                if (queue.equals("Disc")) {
                    UrlString = getString(R.string.user_api_URL) + userid
                            + getString(R.string.disc_queueURL);

                    if (formatValue.equals("1"))
                        format = null;
                    else if (formatValue.equals("2"))
                        format = "DVD";
                    else if (formatValue.equals("3"))
                        format = "Blu-ray";
                    else if (formatValue.equals("4"))
                        format = (String) formatSpinner.getSelectedItem();

                } else if (queue.equals("Instant"))
                    UrlString = getString(R.string.user_api_URL) + userid
                            + getString(R.string.instant_queueURL);

                EditText positionView = (EditText) findViewById(R.id.PostionView);
                String position = positionView.getText().toString();
                if (position.equals(""))
                    position = null;

                executePostTask(UrlString, titleRef, position, format);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void executePostTask(String postURL, String titleRef, String to, String format) {
        new PostTask().execute(accessKey, accessSecret, postURL, titleRef, to, format);
    }

    private class PostTask extends AsyncTask<String, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(AddTitleActivity.this);
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
                if (queue.equals("Disc")) {
                    postParser.postData(parms[2], parms[3], app.getDiscETag(), parms[4], parms[5]);
                    app.setDiscETag(postParser.getETag());

                } else if (queue.equals("Instant")) {
                    postParser.postData(parms[2], parms[3], app.getInstantETag(), parms[4],
                            parms[5]);
                    app.setInstantETag(postParser.getETag());
                }
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
            Toast.makeText(AddTitleActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void result) {

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            if (message != null)
                Toast.makeText(AddTitleActivity.this, message, Toast.LENGTH_LONG).show();

            finish();
        }
    }
}
