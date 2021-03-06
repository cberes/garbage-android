package com.spinthechoice.garbage.android.settings.notifications;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.spinthechoice.garbage.android.R;
import com.spinthechoice.garbage.android.adapters.SimpleStringAdapter;
import com.spinthechoice.garbage.android.mixins.WithPreferencesService;
import com.spinthechoice.garbage.android.preferences.NotificationDay;
import com.spinthechoice.garbage.android.preferences.NotificationPreferences;
import com.spinthechoice.garbage.android.text.Text;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

public class NotificationSettingsActivity extends AppCompatActivity implements WithPreferencesService {

    private Spinner notificationDay;
    private EditText notificationTime;
    private TextView notifyTimeLabel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        findViews();
        setupNotificationSettings();
    }

    private void findViews() {
        notificationDay = findViewById(R.id.spinner_notify_time);
        notificationTime = findViewById(R.id.time_notification);
        notifyTimeLabel = findViewById(R.id.text_notify_time);
    }

    private void setupNotificationSettings() {
        final NotificationPreferences notificationPrefs = preferencesService().readNotificationPreferences(this);
        notifyTimeLabel.setVisibility(notificationPrefs.isNotificationEnabled() ? TextView.VISIBLE : TextView.GONE);
        setupNotificationDaySpinner(notificationPrefs);
        setupNotificationTimePicker(notificationPrefs);
        setupNotificationSwitch(notificationPrefs);
    }

    private void setupNotificationDaySpinner(final NotificationPreferences notificationPrefs) {
        notificationDay.setVisibility(notificationPrefs.isNotificationEnabled() ? Spinner.VISIBLE : Spinner.GONE);
        notificationDay.setAdapter(notificationDaysAdapter());
        notificationDay.setSelection(NotificationDay.fromNotificationOffset(notificationPrefs.getOffset()).ordinal());
        notificationDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final NotificationDay day = NotificationDay.fromIndex(position);
                updateNotificationPreferences(newPrefs -> newPrefs.updateOffset(day));
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void setupNotificationTimePicker(final NotificationPreferences notificationPrefs) {
        notificationTime.setVisibility(notificationPrefs.isNotificationEnabled() ? EditText.VISIBLE : EditText.GONE);
        final LocalDateTime notificationDateTime = notificationPrefs.getNotificationDateTime();
        notificationTime.setText(Text.formatTimeShort(this, notificationDateTime.toLocalTime()));
        notificationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final NotificationPreferences originalPrefs = preferencesService().readNotificationPreferences(v.getContext());
                final LocalTime originalTime = originalPrefs.getNotificationTime();
                final TimePickerDialog timeDialog = new TimePickerDialog(v.getContext(),
                        android.R.style.Theme_Material_Dialog_Alert, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(final TimePicker timePicker, final int selectedHour, final int selectedMinute) {
                        handleTimeSet(timePicker.getContext(), LocalTime.of(selectedHour, selectedMinute));
                    }
                }, originalTime.getHour(), originalTime.getMinute(), DateFormat.is24HourFormat(v.getContext()));
                timeDialog.setTitle(R.string.label_notify_time);
                timeDialog.show();
            }
        });
    }

    private void handleTimeSet(final Context context, final LocalTime newTime) {
        final NotificationDay day = NotificationDay.fromIndex(notificationDay.getSelectedItemPosition());
        notificationTime.setText(Text.formatTimeShort(context, newTime));
        updateNotificationPreferences(prefs -> prefs.setOffset(day.getOffset(newTime)));
    }

    private void setupNotificationSwitch(final NotificationPreferences notificationPrefs) {
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
        final NotificationPreferences prefs = preferencesService().readNotificationPreferences(this);
        updateFunc.accept(prefs);
        preferencesService().writeNotificationPreferences(this, prefs);
    }

    private SpinnerAdapter notificationDaysAdapter() {
        return new SimpleStringAdapter(this, Arrays.stream(NotificationDay.values())
                .map(day -> day.getText(this))
                .collect(toList()));
    }
}
