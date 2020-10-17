package com.spinthechoice.garbage.android.settings.garbage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.spinthechoice.garbage.android.R;
import com.spinthechoice.garbage.android.adapters.DayOfWeekAdapter;
import com.spinthechoice.garbage.android.adapters.SimpleStringAdapter;
import com.spinthechoice.garbage.android.mixins.WithGarbageScheduleService;
import com.spinthechoice.garbage.android.mixins.WithHolidayService;
import com.spinthechoice.garbage.android.mixins.WithPreferencesService;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.settings.holidays.HolidayPickerActivity;

import java.time.DayOfWeek;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class GarbageSettingsActivity extends AppCompatActivity implements WithPreferencesService,
        WithGarbageScheduleService, WithHolidayService {
    private TextView garbageNoChoicesText;
    private Spinner garbageWeek;

    private TextView recyclingNoChoicesText;
    private Spinner recyclingWeek;

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
        findViews();
        setupGarbageSettings();
        setupHolidayPicker();
    }

    private void findViews() {
        garbageNoChoicesText = findViewById(R.id.text_garbage_no_choices);
        garbageWeek = findViewById(R.id.spinner_garbage_week);

        recyclingNoChoicesText = findViewById(R.id.text_recycling_no_choices);
        recyclingWeek = findViewById(R.id.spinner_recycling_week);
    }

    private void setupGarbageSettings() {
        final GarbagePreferences prefs = preferencesService().readGarbagePreferences(this);
        updateGarbageWeekOptions(prefs);
        updateRecyclingWeekOptions(prefs);
        setupDayOfWeekSpinner(prefs);
        setupGarbageWeekSpinner();
        setupRecyclingWeekSpinner();
        setupGarbageFrequencySpinner(prefs);
        setupRecyclingFrequencySpinner(prefs);
    }

    private void setupDayOfWeekSpinner(final GarbagePreferences prefs) {
        final List<DayOfWeek> daysOfWeek = asList(DayOfWeek.values());
        final Spinner dayOfWeek = findViewById(R.id.spinner_day_of_week);
        dayOfWeek.setAdapter(new DayOfWeekAdapter(this, daysOfWeek));
        dayOfWeek.setSelection(daysOfWeek.indexOf(prefs.getDayOfWeek()));
        dayOfWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final GarbagePreferences newPrefs =
                        updatePreferences(prefs -> prefs.setDayOfWeek(daysOfWeek.get(position)));
                updateGarbageWeekOptions(newPrefs);
                updateRecyclingWeekOptions(newPrefs);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void setupGarbageWeekSpinner() {
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
    }

    private void setupRecyclingWeekSpinner() {
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
    }

    private void setupGarbageFrequencySpinner(final GarbagePreferences prefs) {
        final Spinner garbageWeeks = findViewById(R.id.spinner_garbage_weeks);
        ((ArrayAdapter<?>) garbageWeeks.getAdapter()).setDropDownViewResource(R.layout.spinner_item);
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
                updateGarbageWeekOptions(newPrefs);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void setupRecyclingFrequencySpinner(final GarbagePreferences prefs) {
        final Spinner recyclingWeeks = findViewById(R.id.spinner_recycling_weeks);
        ((ArrayAdapter<?>) recyclingWeeks.getAdapter()).setDropDownViewResource(R.layout.spinner_item);
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
                updateRecyclingWeekOptions(newPrefs);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private GarbagePreferences updatePreferences(final Consumer<GarbagePreferences> updateFunc) {
        final GarbagePreferences prefs = preferencesService().readGarbagePreferences(this);
        updateFunc.accept(prefs);
        preferencesService().writeGarbagePreferences(this, prefs);
        return prefs;
    }

    private void updateGarbageWeekOptions(final GarbagePreferences prefs) {
        garbageOptions = PickupItem.GARBAGE.weekOptions(prefs, garbageScheduleService(this));
        updateWeekOptions(garbageWeek, garbageNoChoicesText,
                prefs.isGarbageEnabled(), prefs.getGarbageWeekIndex(), garbageOptions);
    }

    private void updateRecyclingWeekOptions(final GarbagePreferences prefs) {
        recyclingOptions = PickupItem.RECYCLING.weekOptions(prefs, garbageScheduleService(this));
        updateWeekOptions(recyclingWeek, recyclingNoChoicesText,
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
        spinner.setAdapter(new SimpleStringAdapter(this, items.stream().map(WeekOption::toString).collect(toList())));
        spinner.setSelection(range(0, items.size()).filter(i -> items.get(i).getId() == weekIndex).findFirst().orElse(0));
    }

    private void setupHolidayPicker() {
        final Button holidayPicker = findViewById(R.id.button_holiday_picker);
        holidayPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                launchHolidayPicker();
            }
        });
    }

    private void launchHolidayPicker() {
        final Intent holidayPicker = new Intent(this, HolidayPickerActivity.class);
        startActivity(holidayPicker);
    }
}
