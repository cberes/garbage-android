package com.spinthechoice.garbage.android;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.spinthechoice.garbage.android.preferences.NotificationPreferences;
import com.spinthechoice.garbage.android.service.PreferencesService;
import com.spinthechoice.garbage.android.util.TextUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

public class NotificationSettingsActivity extends AppCompatActivity {
    private final PreferencesService prefsService = new PreferencesService();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setupNotificationSettings();
    }

    private void setupNotificationSettings() {
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
                final TimePickerDialog timeDialog = new TimePickerDialog(v.getContext(),
                        android.R.style.Theme_Material_Dialog_Alert, new TimePickerDialog.OnTimeSetListener() {
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

        final SwitchCompat notificationsEnabled = findViewById(R.id.switch_notifications);
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

    private void updateNotificationPreferences(final Consumer<NotificationPreferences> updateFunc) {
        final NotificationPreferences prefs = prefsService.readNotificationPreferences(this);
        updateFunc.accept(prefs);
        prefsService.writeNotificationPreferences(this, prefs);
    }

    private SpinnerAdapter notificationDaysAdapter() {
        return new ArrayAdapter<>(this, R.layout.spinner_item,
                Arrays.stream(NotificationDay.values())
                        .map(day -> day.getText(this))
                        .collect(toList()));
    }
}
