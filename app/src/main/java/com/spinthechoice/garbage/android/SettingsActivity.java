package com.spinthechoice.garbage.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

import com.spinthechoice.garbage.GlobalGarbageConfiguration;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.preferences.NotificationPreferences;
import com.spinthechoice.garbage.android.service.GarbageOption;
import com.spinthechoice.garbage.android.service.GarbagePresetService;
import com.spinthechoice.garbage.android.service.PreferencesService;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class SettingsActivity extends AppCompatActivity {
    private final GarbagePresetService presetService = new GarbagePresetService();
    private final PreferencesService prefsService = new PreferencesService();
    private final AtomicReference<GarbageOption> optionRef = new AtomicReference<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // TODO set initial values
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(this);
        final GarbageOption initialOption = presetService.findPresetById(prefs.getOptionId())
                .orElseGet(presetService::getDefaultPreset);
        final GlobalGarbageConfiguration initialConfig = initialOption.getConfiguration();

        final List<DayOfWeek> daysOfWeek = asList(DayOfWeek.values());
        final Spinner dayOfWeek = findViewById(R.id.spinner_day_of_week);
        dayOfWeek.setAdapter(dayOfWeekAdapter(daysOfWeek));
        dayOfWeek.setSelection(daysOfWeek.indexOf(prefs.getDayOfWeek()));
        dayOfWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                updateGarbagePreferences(prefs -> prefs.setDayOfWeek(daysOfWeek.get(position)));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final Spinner garbageWeek = findViewById(R.id.spinner_garbage_week);
        updateWeekOptions(garbageWeek, initialConfig.getGarbageWeeks(), prefs.getGarbageWeek());
        garbageWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final List<String> items = optionRef.get().getConfiguration().getGarbageWeeks();
                updateGarbagePreferences(prefs -> prefs.setGarbageWeek(items.get(position)));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final Spinner recyclingWeek = findViewById(R.id.spinner_recycling_week);
        updateWeekOptions(recyclingWeek, initialConfig.getRecyclingWeeks(), prefs.getRecyclingWeek());
        recyclingWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final List<String> items = optionRef.get().getConfiguration().getRecyclingWeeks();
                updateGarbagePreferences(prefs -> prefs.setRecyclingWeek(items.get(position)));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final Spinner municipality = findViewById(R.id.spinner_municipality);
        final List<GarbageOption> presets = presetService.getAllPresets();
        final List<String> presetIds = presets.stream().map(GarbageOption::getId).collect(toList());
        municipality.setAdapter(municipalityAdapter(presets));
        municipality.setSelection(presetIds.indexOf(prefs.getOptionId()));
        municipality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final GarbageOption preset = presets.get(position);
                optionRef.set(preset);

                final GarbagePreferences defaults = prefsService.createDefaultPreferences(preset);
                updateGarbagePreferences(prefs -> {
                    prefs.setOptionId(defaults.getOptionId());
                    prefs.setGarbageWeek(defaults.getGarbageWeek());
                    prefs.setRecyclingWeek(defaults.getRecyclingWeek());
                });

                final GlobalGarbageConfiguration config = preset.getConfiguration();
                garbageWeek.setSelected(false);
                updateWeekOptions(garbageWeek, config.getGarbageWeeks(), defaults.getGarbageWeek());

                recyclingWeek.setSelected(false);
                updateWeekOptions(recyclingWeek, config.getRecyclingWeeks(), defaults.getRecyclingWeek());
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final Switch notificationsEnabled = findViewById(R.id.switch_notifications);
        notificationsEnabled.setChecked(prefsService.readNotificationPreferences(this).isNotificationEnabled());
        notificationsEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean checked) {
                updateNotificationPreferences(prefs -> prefs.setNotificationEnabled(checked));
            }
        });
    }

    private static <E> List<E> defaultList(final List<E> list) {
        return list == null ? emptyList() : list;
    }

    private SpinnerAdapter municipalityAdapter(final List<GarbageOption> presets) {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                presets.stream()
                        .map(GarbageOption::getName)
                        .collect(toList()));
    }

    private SpinnerAdapter stringListAdapter(final List<String> strings) {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, strings);
    }

    private SpinnerAdapter dayOfWeekAdapter(final List<DayOfWeek> daysOfWeek) {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                daysOfWeek.stream()
                        .map(day -> day.getDisplayName(TextStyle.FULL, getResources().getConfiguration().getLocales().get(0)))
                        .collect(toList()));
    }

    private void updateGarbagePreferences(final Consumer<GarbagePreferences> updateFunc) {
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(this);
        updateFunc.accept(prefs);
        prefsService.writeGarbagePreferences(this, prefs);
    }

    private void updateNotificationPreferences(final Consumer<NotificationPreferences> updateFunc) {
        final NotificationPreferences prefs = prefsService.readNotificationPreferences(this);
        updateFunc.accept(prefs);
        prefsService.writeNotificationPreferences(this, prefs);
    }

    private void updateWeekOptions(final Spinner spinner, final List<String> nullableItems, final String selected) {
        final List<String> items = defaultList(nullableItems);
        spinner.setEnabled(items.size() > 1);
        spinner.setAdapter(stringListAdapter(items));
        if (selected != null) {
            spinner.setSelection(items.indexOf(selected));
        }
    }
}
