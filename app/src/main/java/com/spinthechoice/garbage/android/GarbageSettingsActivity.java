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
import com.spinthechoice.garbage.android.util.AdapterUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
                    final int weekIndex = prefs.getGarbageWeekIndex();
                    final List<WeekOption> options = range(0, prefs.getGarbageWeeks())
                            .mapToObj(week -> {
                                prefs.setGarbageWeekIndex(week);
                                Garbage garbage = scheduleService.createGarbage(prefs, holidayService);
                                Optional<GarbageDay> day = scheduleService.nextPickup(garbage, now, GarbageDay::isGarbageDay);
                                return new WeekOption(week, day.map(GarbageDay::getDate).orElse(now));
                            })
                            .sorted(Comparator.comparing(WeekOption::getDate))
                            .collect(toList());
                    prefs.setGarbageWeekIndex(weekIndex);
                    return options;
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
                    final int weekIndex = prefs.getRecyclingWeekIndex();
                    final List<WeekOption> options = range(0, prefs.getRecyclingWeeks())
                            .mapToObj(week -> {
                                prefs.setRecyclingWeekIndex(week);
                                Garbage garbage = scheduleService.createGarbage(prefs, holidayService);
                                Optional<GarbageDay> day = scheduleService.nextPickup(garbage, now, GarbageDay::isRecyclingDay);
                                return new WeekOption(week, day.map(GarbageDay::getDate).orElse(now));
                            })
                            .sorted(Comparator.comparing(WeekOption::getDate))
                            .collect(toList());
                    prefs.setRecyclingWeekIndex(weekIndex);
                    return options;
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
    private HolidayService holidayService;
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
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(this, R.raw.holidays);
        holidayService = new HolidayService(prefsService, this);

        final Button holidayPicker = findViewById(R.id.button_holiday_picker);
        holidayPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                launchHolidayPicker();
            }
        });

        final TextView garbageNoChoicesText = findViewById(R.id.text_garbage_no_choices);
        final Spinner garbageWeek = findViewById(R.id.spinner_garbage_week);

        final TextView recyclingNoChoicesText = findViewById(R.id.text_recycling_no_choices);
        final Spinner recyclingWeek = findViewById(R.id.spinner_recycling_week);

        final List<DayOfWeek> daysOfWeek = asList(DayOfWeek.values());
        final Spinner dayOfWeek = findViewById(R.id.spinner_day_of_week);
        dayOfWeek.setAdapter(AdapterUtils.dayOfWeekAdapter(this, daysOfWeek));
        dayOfWeek.setSelection(daysOfWeek.indexOf(prefs.getDayOfWeek()));
        dayOfWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final GarbagePreferences newPrefs =
                        updatePreferences(prefs -> prefs.setDayOfWeek(daysOfWeek.get(position)));
                updateGarbageWeekOptions(newPrefs, garbageWeek, garbageNoChoicesText);
                updateRecyclingWeekOptions(newPrefs, recyclingWeek, recyclingNoChoicesText);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        updateGarbageWeekOptions(prefs, garbageWeek, garbageNoChoicesText);
        garbageWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                updatePreferences(prefs -> prefs.setGarbageWeekIndex(garbageOptions.get(position).getId()));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        updateRecyclingWeekOptions(prefs, recyclingWeek, recyclingNoChoicesText);
        recyclingWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                updatePreferences(prefs -> prefs.setRecyclingWeekIndex(recyclingOptions.get(position).getId()));
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
                final GarbagePreferences newPrefs = updatePreferences(prefs -> {
                    prefs.setGarbageWeeks(position);
                    if (prefs.getGarbageWeekIndex() >= position) {
                        prefs.setGarbageWeekIndex(0);
                    }
                });

                garbageWeek.setSelected(false);
                updateGarbageWeekOptions(newPrefs, garbageWeek, garbageNoChoicesText);
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
                final GarbagePreferences newPrefs = updatePreferences(prefs -> {
                    prefs.setRecyclingWeeks(position);
                    if (prefs.getRecyclingWeekIndex() >= position) {
                        prefs.setRecyclingWeekIndex(0);
                    }
                });

                recyclingWeek.setSelected(false);
                updateRecyclingWeekOptions(newPrefs, recyclingWeek, recyclingNoChoicesText);
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

    private GarbagePreferences updatePreferences(final Consumer<GarbagePreferences> updateFunc) {
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(this, R.raw.holidays);
        updateFunc.accept(prefs);
        prefsService.writeGarbagePreferences(this, prefs);
        return prefs;
    }

    private void updateGarbageWeekOptions(final GarbagePreferences prefs, final Spinner garbageWeek, final TextView noChoicesLabel) {
        garbageOptions = PickupItem.GARBAGE.weekOptions(prefs, holidayService, scheduleService);
        updateWeekOptions(garbageWeek, noChoicesLabel,
                prefs.isGarbageEnabled(), prefs.getGarbageWeekIndex(), garbageOptions);
    }

    private void updateRecyclingWeekOptions(final GarbagePreferences prefs, final Spinner recyclingWeek, final TextView noChoicesLabel) {
        recyclingOptions = PickupItem.RECYCLING.weekOptions(prefs, holidayService, scheduleService);
        updateWeekOptions(recyclingWeek, noChoicesLabel,
                prefs.isRecyclingEnabled(), prefs.getRecyclingWeekIndex(), recyclingOptions);
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
