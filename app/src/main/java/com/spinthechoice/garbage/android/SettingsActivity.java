package com.spinthechoice.garbage.android;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.spinthechoice.garbage.GlobalGarbageConfiguration;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.preferences.NotificationPreferences;
import com.spinthechoice.garbage.android.service.GarbageOption;
import com.spinthechoice.garbage.android.service.GarbagePresetService;
import com.spinthechoice.garbage.android.service.PreferencesService;
import com.spinthechoice.garbage.android.util.TextUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class SettingsActivity extends AppCompatActivity {
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

        final GarbagePresetService presetService = new GarbagePresetService(this, R.raw.data);
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

        final TextView garbageWeeklyText = findViewById(R.id.text_garbage_weekly);
        final Spinner garbageWeek = findViewById(R.id.spinner_garbage_week);
        updateWeekOptions(garbageWeek, garbageWeeklyText,
                initialConfig.getGarbageWeeks(), prefs.getGarbageWeek());
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

        final TextView recyclingWeeklyText = findViewById(R.id.text_recycling_weekly);
        final Spinner recyclingWeek = findViewById(R.id.spinner_recycling_week);
        updateWeekOptions(recyclingWeek, recyclingWeeklyText,
                initialConfig.getRecyclingWeeks(), prefs.getRecyclingWeek());
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
                updateWeekOptions(garbageWeek, garbageWeeklyText,
                        config.getGarbageWeeks(), defaults.getGarbageWeek());

                recyclingWeek.setSelected(false);
                updateWeekOptions(recyclingWeek, recyclingWeeklyText,
                        config.getRecyclingWeeks(), defaults.getRecyclingWeek());
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final NotificationPreferences notificationPrefs = prefsService.readNotificationPreferences(this);
        final Spinner notificationDay = findViewById(R.id.spinner_notify_time);
        notificationDay.setVisibility(notificationPrefs.isNotificationEnabled() ? Spinner.VISIBLE : Spinner.GONE);
        notificationDay.setAdapter(notificationDaysAdapter());
        notificationDay.setSelection(NotificationDay.fromNotificationOffset(notificationPrefs.getOffset()).ordinal());
        notificationDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final NotificationPreferences originalPrefs = prefsService.readNotificationPreferences(parent.getContext());
                final LocalTime originalTime = originalPrefs.getNotificationTime();
                final NotificationDay day = NotificationDay.fromIndex(position);
                updateNotificationPreferences(prefs -> prefs.setOffset(day.getOffset(originalTime)));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });

        final EditText notificationTime = findViewById(R.id.time_notification);
        notificationTime.setVisibility(notificationPrefs.isNotificationEnabled() ? EditText.VISIBLE : EditText.GONE);
        final LocalDateTime notificationDateTime = notificationPrefs.getNotificationDateTime();
        notificationTime.setText(TextUtils.formatTimeShort(this, notificationDateTime.toLocalTime()));
        notificationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final NotificationPreferences originalPrefs = prefsService.readNotificationPreferences(v.getContext());
                final LocalTime originalTime = originalPrefs.getNotificationTime();
                final TimePickerDialog timeDialog = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(final TimePicker timePicker, final int selectedHour, final int selectedMinute) {
                        final NotificationDay day = NotificationDay.fromIndex(notificationDay.getSelectedItemPosition());
                        final LocalTime newTime = LocalTime.of(selectedHour, selectedMinute);
                        notificationTime.setText(TextUtils.formatTimeShort(timePicker.getContext(), newTime));
                        updateNotificationPreferences(prefs -> prefs.setOffset(day.getOffset(newTime)));
                    }
                }, originalTime.getHour(), originalTime.getMinute(), DateFormat.is24HourFormat(v.getContext()));
                timeDialog.setTitle(R.string.label_notify_time);
                timeDialog.show();
            }
        });

        final TextView notifyTimeLabel = findViewById(R.id.text_notify_time);
        notifyTimeLabel.setVisibility(notificationPrefs.isNotificationEnabled() ? TextView.VISIBLE : TextView.GONE);

        final Switch notificationsEnabled = findViewById(R.id.switch_notifications);
        notificationsEnabled.setChecked(notificationPrefs.isNotificationEnabled());
        notificationsEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean checked) {
                updateNotificationPreferences(prefs -> prefs.setNotificationEnabled(checked));
                if (checked) {
                    GarbageNotifier.startNotificationAlarmRepeating(buttonView.getContext());
                }
                notifyTimeLabel.setVisibility(checked ? TextView.VISIBLE : TextView.GONE);
                notificationTime.setVisibility(checked ? EditText.VISIBLE : EditText.GONE);
                notificationDay.setVisibility(checked ? Spinner.VISIBLE : Spinner.GONE);
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

    private void updateWeekOptions(final Spinner spinner, final TextView weeklyLabel,
                                   final List<String> nullableItems, final String selected) {
        final List<String> items = defaultList(nullableItems);
        weeklyLabel.setVisibility(items.isEmpty() ? Spinner.VISIBLE : Spinner.GONE);
        spinner.setVisibility(items.isEmpty() ? Spinner.GONE : Spinner.VISIBLE);
        spinner.setEnabled(items.size() > 1);
        spinner.setAdapter(stringListAdapter(items));
        if (selected != null) {
            spinner.setSelection(items.indexOf(selected));
        }
    }

    private SpinnerAdapter notificationDaysAdapter() {
        return new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.stream(NotificationDay.values())
                        .map(day -> day.getText(this))
                        .collect(toList()));
    }
}
