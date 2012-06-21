
package com.silentlabs.android.mobilequeue.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.silentlabs.android.mobilequeue.R;
import com.silentlabs.android.mobilequeue.classes.User;
import com.silentlabs.android.mobilequeue.parser.NetflixParser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;

public class DisplayProfileActivity extends Activity {

    private final static String ACCESS = "MobileQueueAccess";

    private SharedPreferences access;
    private String accessKey;
    private String accessSecret;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        setContentView(R.layout.profile_layout);

        access = getSharedPreferences(ACCESS, Context.MODE_PRIVATE);
        accessKey = access.getString("ACCESS_KEY", null);
        accessSecret = access.getString("ACCESS_SECRET", null);
        userid = access.getString("USER_ID", null);

        TextView brandingTest = (TextView) findViewById(R.id.BrandingText);
        brandingTest.setMovementMethod(LinkMovementMethod.getInstance());

        executeProfileTask(getString(R.string.user_api_URL) + userid);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void executeProfileTask(String userURL) {
        new ProfileTask().execute(accessKey, accessSecret, userURL);
    }

    private class ProfileTask extends AsyncTask<String, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(DisplayProfileActivity.this);
        private String message = null;
        private User user;

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
                user = postParser.getUser();

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
            Toast.makeText(DisplayProfileActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void result) {

            if (user != null) {
                TextView fullName = (TextView) findViewById(R.id.full_name_textview);
                fullName.setText(user.getFullName());

                TextView nickName = (TextView) findViewById(R.id.nickname_textview);
                nickName.setText(user.getNickName());

                CheckBox instantWatch = (CheckBox) findViewById(R.id.can_watch_checkbox);
                instantWatch.setChecked(user.getInstantWatch());

                try {
                    int formatCount = user.getPreferredFormats().size();
                    for (int i = 0; i < formatCount; i++) {
                        if (user.getPreferredFormats().get(i).equalsIgnoreCase("Blu-ray")) {
                            ImageView bluray = (ImageView) findViewById(R.id.BluRayView);
                            bluray.setVisibility(View.VISIBLE);

                        } else if (user.getPreferredFormats().get(i).equalsIgnoreCase("DVD")) {
                            ImageView dvd = (ImageView) findViewById(R.id.DvdView);
                            dvd.setVisibility(View.VISIBLE);

                        } else if (user.getPreferredFormats().get(i).equalsIgnoreCase("Instant")) {
                            ImageView instant = (ImageView) findViewById(R.id.InstantView);
                            instant.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (NullPointerException e) {
                    TextView formatView = (TextView) findViewById(R.id.FormatText);
                    formatView.setVisibility(View.INVISIBLE);
                }

            }

            if (this.dialog.isShowing())
                this.dialog.dismiss();

            if (message != null)
                Toast.makeText(DisplayProfileActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
}
