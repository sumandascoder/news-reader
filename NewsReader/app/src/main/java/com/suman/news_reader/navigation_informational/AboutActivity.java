package com.suman.news_reader.navigation_informational;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import com.suman.news_reader.R;

/**
 * @author sumansucharitdas
 * For HTML webview dialog
 *  */
public class AboutActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle helpBundle = getIntent().getExtras();
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.dialog_about);

        // UI based initiation
        WebView helpText = (WebView) findViewById(R.id.txtHelp);
        Button okButton = (Button) findViewById(R.id.ok);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "FortuneCity.ttf");

        // Don't close if clicked outside as a WebView
        setFinishOnTouchOutside(true);

        // WebView uses a specific HTML page that is Help.html
        helpText.loadUrl("file:///android_asset/html/" + helpBundle.getString("about-page"));
        helpText.setBackgroundColor(0x00000000);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.mipmap.ic_launcher);

        // Using the font in button entity
        okButton.setTypeface(typeface);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
