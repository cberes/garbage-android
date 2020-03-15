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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class GarbageSettingsActivity extends AppCompatActivity {
    private static class WeekOption {
        private static final DateTimeFormatter FORMAT =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

        private final int id;
        private final LocalDate date;

        WeekOption(final int id, final LocalDate date) {
            this.id = id;
            this.date = date;
        }

        int getId() {
            return id;
        }

        LocalDate getDate() {
            return date;
        }

        @Override
        public String toString() {
            return date.format(FORMAT);
        }
    }

    private enum PickupItem {
        GARBAGE {
            @Override
            List<WeekOption> weekOptions(final GarbagePreferences prefs, final HolidayService holidayService,
                                     final GarbageScheduleService scheduleService) {
                if (prefs.isGarbageEnabled()) {
                    final LocalDate now = LocalDate.now();
                    return range(0, prefs.getGarbageWeeks())
                            .mapToObj(week -> {
                                prefs.setGarbageWeekIndex(week);
                                Garbage garbage = scheduleService.createGarbage(prefs, holidayService);
                                Optional<GarbageDay> day = scheduleService.nextPickup(garbage, now, GarbageDay::isGarbageDay);
                                return new WeekOption(week, day.map(GarbageDay::getDate).orElse(now));
                            })
                            .sorted(Comparator.comparing(WeekOption::getDate))
                            .collect(toList());
                } else {
                    return emptyList();
                }
            }
        },
        RECYCLING {
            @Override
            List<WeekOption> weekOptions(final GarbagePreferences prefs, final HolidayService holidayService,
                                     final GarbageScheduleService scheduleService) {
                if (prefs.isRecyclingEnabled()) {
                    final LocalDate now = LocalDate.now();
                    return range(0, prefs.getRecyclingWeeks())
                            .mapToObj(week -> {
                                prefs.setRecyclingWeekIndex(week);
                                Garbage garbage = scheduleService.createGarbage(prefs, holidayService);
                                Optional<GarbageDay> day = scheduleService.nextPickup(garbage, now, GarbageDay::isRecyclingDay);
                                return new WeekOption(week, day.map(GarbageDay::getDate).orElse(now));
                            })
                            .sorted(Comparator.comparing(WeekOption::getDate))
                            .collect(toList());
                } else {
                    return emptyList();
                }
            }
        };

        abstract List<WeekOption> weekOptions(GarbagePreferences prefs, HolidayService holidayService,
                                                GarbageScheduleService scheduleService);
    }

    private final PreferencesService prefsService = new PreferencesService();
    private final GarbageScheduleService scheduleService = new GarbageScheduleService();
    private List<WeekOption> garbageOptions;
    private List<WeekOption> recyclingOptions;

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

        final TextView garbageNoChoicesText = findViewById(R.id.text_garbage_no_choices);
        final Spinner garbageWeek = findViewById(R.id.spinner_garbage_week);
        garbageOptions = PickupItem.GARBAGE.weekOptions(prefs, holidayService, scheduleService);
        updateWeekOptions(garbageWeek, garbageNoChoicesText,
                prefs.isGarbageEnabled(), prefs.getGarbageWeekIndex(), garbageOptions);
        garbageWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                updateGarbagePreferences(prefs -> prefs.setGarbageWeekIndex(garbageOptions.get(position).getId()));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final TextView recyclingNoChoicesText = findViewById(R.id.text_recycling_no_choices);
        final Spinner recyclingWeek = findViewById(R.id.spinner_recycling_week);
        recyclingOptions = PickupItem.RECYCLING.weekOptions(prefs, holidayService, scheduleService);
        updateWeekOptions(recyclingWeek, recyclingNoChoicesText,
                prefs.isRecyclingEnabled(), prefs.getRecyclingWeekIndex(), recyclingOptions);
        recyclingWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                updateGarbagePreferences(prefs -> prefs.setRecyclingWeekIndex(recyclingOptions.get(position).getId()));
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
                garbageOptions = PickupItem.GARBAGE.weekOptions(newPrefs, holidayService, scheduleService);
                updateWeekOptions(garbageWeek, garbageNoChoicesText, position > 0, 0, garbageOptions);
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
                recyclingOptions = PickupItem.RECYCLING.weekOptions(newPrefs, holidayService, scheduleService);
                updateWeekOptions(recyclingWeek, recyclingNoChoicesText, position > 0, 0, recyclingOptions);
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

    private void updateWeekOptions(final Spinner spinner, final TextView noChoicesLabel,
                                   final boolean enabled, final int weekIndex,
                                   final List<WeekOption> items) {
        final boolean choicesAvailable = enabled && items.size() > 1;
        noChoicesLabel.setVisibility(!choicesAvailable ? Spinner.VISIBLE : Spinner.GONE);
        noChoicesLabel.setText(enabled ? items.get(0).toString() : getString(R.string.never));
        spinner.setVisibility(choicesAvailable ? Spinner.VISIBLE : Spinner.GONE);
        spinner.setEnabled(choicesAvailable);
        spinner.setAdapter(stringListAdapter(items.stream().map(WeekOption::toString).collect(toList())));
        spinner.setSelection(range(0, items.size()).filter(i -> items.get(i).getId() == weekIndex).findFirst().orElse(0));
    }
}
