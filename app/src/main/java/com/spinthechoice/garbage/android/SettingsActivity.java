package com.spinthechoice.garbage.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setupClickListeners();
    }

    private void setupClickListeners() {
        final LinearLayout garbageSettings = findViewById(R.id.layout_garbage_settings);
        garbageSettings.setOnClickListener(view -> launchGarbageSettings());

        final LinearLayout notificationSettings = findViewById(R.id.layout_notification_settings);
        notificationSettings.setOnClickListener(view -> launchNotificationSettings());
    }

    private void launchGarbageSettings() {
        final Intent garbageSettings = new Intent(this, GarbageSettingsActivity.class);
        startActivity(garbageSettings);
    }

    private void launchNotificationSettings() {
        final Intent notificationSettings = new Intent(this, NotificationSettingsActivity.class);
        startActivity(notificationSettings);
    }
}
