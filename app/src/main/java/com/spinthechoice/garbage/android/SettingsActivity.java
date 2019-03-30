package com.spinthechoice.garbage.android;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Spinner daysOfWeek = findViewById(R.id.spinner_day_of_week);
        daysOfWeek.setAdapter(dayOfWeekAdapter());

        final Spinner garbageWeeks = findViewById(R.id.spinner_garbage_week);
        final Spinner recyclingWeeks = findViewById(R.id.spinner_recycling_week);

        final List<GarbageOption> presets = GarbagePresets.allPresets();
        final Spinner municipalities = findViewById(R.id.spinner_municipality);
        municipalities.setAdapter(municipalityAdapter(presets));
        municipalities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                Snackbar.make(view, "Selected " + presets.get(position).getName(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private SpinnerAdapter municipalityAdapter(final List<GarbageOption> presets) {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                presets.stream()
                        .map(GarbageOption::getName)
                        .collect(toList()));
    }

    private SpinnerAdapter dayOfWeekAdapter() {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.stream(DayOfWeek.values())
                        .map(day -> day.getDisplayName(TextStyle.FULL, getResources().getConfiguration().locale))
                        .collect(toList()));
    }
}
