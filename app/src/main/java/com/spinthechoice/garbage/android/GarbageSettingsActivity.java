package com.spinthechoice.garbage.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.service.GarbageScheduleService;
import com.spinthechoice.garbage.android.service.HolidayService;
import com.spinthechoice.garbage.android.service.PreferencesService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class GarbageSettingsActivity extends AppCompatActivity {
    private enum PickupItem {
        GARBAGE {
            @Override
            List<String> weekOptions(final GarbagePreferences prefs, final HolidayService holidayService,
                                     final GarbageScheduleService scheduleService) {
                if (prefs.isGarbageEnabled()) {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                    final LocalDate now = LocalDate.now();
                    return range(0, prefs.getGarbageWeeks())
                            .mapToObj(week -> {
                                prefs.setGarbageWeekIndex(week);
                                Garbage garbage = scheduleService.createGarbage(prefs, holidayService);
                                return scheduleService.nextPickup(garbage, now, GarbageDay::isGarbageDay);
                            })
                            .map(day -> day.map(GarbageDay::getDate).orElse(now))
                            .map(date -> date.format(formatter))
                            .collect(toList());
                } else {
                    return emptyList();
                }
            }
        },
        RECYCLING {
            @Override
            List<String> weekOptions(final GarbagePreferences prefs, final HolidayService holidayService,
                                     final GarbageScheduleService scheduleService) {
                if (prefs.isRecyclingEnabled()) {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
                    final LocalDate now = LocalDate.now();
                    return range(0, prefs.getRecyclingWeeks())
                            .mapToObj(week -> {
                                prefs.setRecyclingWeekIndex(week);
                                Garbage garbage = scheduleService.createGarbage(prefs, holidayService);
                                return scheduleService.nextPickup(garbage, now, GarbageDay::isRecyclingDay);
                            })
                            .map(day -> day.map(GarbageDay::getDate).orElse(now))
                            .map(date -> date.format(formatter))
                            .collect(toList());
                } else {
                    return emptyList();
                }
            }
        };

        abstract List<String> weekOptions(GarbagePreferences prefs, HolidayService holidayService,
                                          GarbageScheduleService scheduleService);
    }

    private final PreferencesService prefsService = new PreferencesService();
    private final GarbageScheduleService scheduleService = new GarbageScheduleService();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garbage_settings);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setupGarbageSettings();
    }

    private void setupGarbageSettings() {
        final HolidayService holidayService = new HolidayService(this, R.raw.holidays);
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(this);

        final Button holidayPicker = findViewById(R.id.button_holiday_picker);
        holidayPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                launchHolidayPicker();
            }
        });

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

        final TextView garbageWeeklyText = findViewById(R.id.text_garbage_weekly);
        final Spinner garbageWeek = findViewById(R.id.spinner_garbage_week);
        updateWeekOptions(garbageWeek, garbageWeeklyText,
                prefs.isGarbageEnabled(), prefs.getGarbageWeekIndex(),
                PickupItem.GARBAGE.weekOptions(prefs, holidayService, scheduleService));
        garbageWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                updateGarbagePreferences(prefs -> prefs.setGarbageWeekIndex(position));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final TextView recyclingWeeklyText = findViewById(R.id.text_recycling_weekly);
        final Spinner recyclingWeek = findViewById(R.id.spinner_recycling_week);
        updateWeekOptions(recyclingWeek, recyclingWeeklyText,
                prefs.isRecyclingEnabled(), prefs.getRecyclingWeekIndex(),
                PickupItem.RECYCLING.weekOptions(prefs, holidayService, scheduleService));
        recyclingWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                updateGarbagePreferences(prefs -> prefs.setRecyclingWeekIndex(position));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final Spinner garbageWeeks = findViewById(R.id.spinner_garbage_weeks);
        garbageWeeks.setSelection(prefs.getGarbageWeeks());
        garbageWeeks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final GarbagePreferences newPrefs = updateGarbagePreferences(prefs -> {
                    prefs.setGarbageWeeks(position);
                    prefs.setGarbageWeekIndex(0);
                });

                garbageWeek.setSelected(false);
                updateWeekOptions(garbageWeek, garbageWeeklyText, position > 0, 0,
                        PickupItem.GARBAGE.weekOptions(newPrefs, holidayService, scheduleService));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final Spinner recyclingWeeks = findViewById(R.id.spinner_recycling_weeks);
        recyclingWeeks.setSelection(prefs.getRecyclingWeeks());
        recyclingWeeks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final GarbagePreferences newPrefs = updateGarbagePreferences(prefs -> {
                    prefs.setRecyclingWeeks(position);
                    prefs.setRecyclingWeekIndex(0);
                });

                recyclingWeek.setSelected(false);
                updateWeekOptions(recyclingWeek, recyclingWeeklyText, position > 0, 0,
                        PickupItem.RECYCLING.weekOptions(newPrefs, holidayService, scheduleService));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void launchHolidayPicker() {
        final Intent holidayPicker = new Intent(this, HolidayPickerActivity.class);
        startActivity(holidayPicker);
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

    private GarbagePreferences updateGarbagePreferences(final Consumer<GarbagePreferences> updateFunc) {
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(this);
        updateFunc.accept(prefs);
        prefsService.writeGarbagePreferences(this, prefs);
        return prefs;
    }

    private void updateWeekOptions(final Spinner spinner, final TextView weeklyLabel,
                                   final boolean enabled, final int weekIndex,
                                   final List<String> items) {
        weeklyLabel.setVisibility(!enabled || items.isEmpty() ? Spinner.VISIBLE : Spinner.GONE);
        weeklyLabel.setText(getString(enabled ? R.string.weekly : R.string.never));
        spinner.setVisibility(items.isEmpty() ? Spinner.GONE : Spinner.VISIBLE);
        spinner.setEnabled(items.size() > 1);
        spinner.setAdapter(stringListAdapter(items));
        spinner.setSelection(weekIndex);
    }
}
