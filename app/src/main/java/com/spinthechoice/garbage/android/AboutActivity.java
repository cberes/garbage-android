package com.spinthechoice.garbage.android;

import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.regex.Pattern;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setupLinks();
    }

    private void setupLinks() {
        TextView text = findViewById(R.id.text_contribute_prompt);
        Linkify.addLinks(text, Pattern.compile("github", Pattern.CASE_INSENSITIVE), "",
                (s, start, end) -> true,
                (match, url) -> "https://github.com/cberes/garbage-android");
    }
}
