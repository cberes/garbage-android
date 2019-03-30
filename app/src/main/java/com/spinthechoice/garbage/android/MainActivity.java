package com.spinthechoice.garbage.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                Toast.makeText(MainActivity.this, presets.get(position).getName(), Toast.LENGTH_LONG).show();;
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
