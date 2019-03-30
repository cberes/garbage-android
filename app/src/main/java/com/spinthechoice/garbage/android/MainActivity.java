package com.spinthechoice.garbage.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;
import com.spinthechoice.garbage.GlobalGarbageConfiguration;
import com.spinthechoice.garbage.UserGarbageConfiguration;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final GarbageConfiguration config = new GarbageConfiguration();
        config.setOptionId(GarbagePresets.westSeneca().getId());
        config.setDayOfWeek(DayOfWeek.THURSDAY);
        config.setRecyclingWeek("A");
        config.setNotificationEnabled(false);
        final UserGarbageConfiguration userConfig = new UserGarbageConfiguration(
                config.getDayOfWeek(), config.getGarbageWeek(), config.getRecyclingWeek());
        final GarbageOption option = GarbagePresets.allPresets().stream()
                .filter(preset -> preset.getId().equals(config.getOptionId()))
                .findAny()
                .orElseGet(GarbagePresets::buffalo);
        final Garbage garbage = new Garbage(option.getConfiguration(), userConfig);
        final List<GarbageDay> garbageDays = getGarbageDays(garbage, LocalDate.now(), 15);

        final TextView header = findViewById(R.id.text_header);
        final String format = getResources().getString(R.string.label_dates);
        final Locale locale = getResources().getConfiguration().getLocales().get(0);
        final String formatted = String.format(format, config.getDayOfWeek().getDisplayName(TextStyle.FULL, locale), option.getName());
        header.setText(formatted);

        final RecyclerView dates = findViewById(R.id.list_dates);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        dates.setLayoutManager(layoutManager);
        dates.setAdapter(datesAdapter(garbageDays));

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                launchSettings();
            }
        });
    }

    private List<GarbageDay> getGarbageDays(final Garbage garbage, final LocalDate start, final int count) {
        return Stream.iterate(start, date -> date.plusDays(1))
                .map(garbage::compute)
                .filter(day -> day.isGarbageDay() || day.isRecyclingDay())
                .limit(count)
                .collect(toList());
    }

    private RecyclerView.Adapter datesAdapter(final List<GarbageDay> days) {
        return new SimpleListAdapter(days.stream()
                .map(day -> day.getDate() + ": " + (day.isGarbageDay() ? "G " : "") + (day.isRecyclingDay() ? "R" : ""))
                .collect(toList()));
    }

    private void launchSettings() {
        final Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            launchSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
