package com.spinthechoice.garbage.android.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spinthechoice.garbage.Garbage;
import com.spinthechoice.garbage.GarbageDay;
import com.spinthechoice.garbage.android.R;
import com.spinthechoice.garbage.android.adapters.TwoLineListAdapter;
import com.spinthechoice.garbage.android.garbage.GarbageScheduleService;
import com.spinthechoice.garbage.android.garbage.PickupItemFormatter;
import com.spinthechoice.garbage.android.mixins.NotificationStatusAware;
import com.spinthechoice.garbage.android.mixins.WithGarbageScheduleService;
import com.spinthechoice.garbage.android.mixins.WithNavigationService;
import com.spinthechoice.garbage.android.mixins.WithPreferencesService;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.navigation.NavigationPreferences;
import com.spinthechoice.garbage.android.settings.SettingsActivity;
import com.spinthechoice.garbage.android.settings.notifications.GarbageNotifier;
import com.spinthechoice.garbage.android.text.Text;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class MainActivity extends AppCompatActivity implements NotificationStatusAware,
        WithGarbageScheduleService, WithNavigationService, WithPreferencesService {
    private static final int PICKUP_DAYS_TO_SHOW = 15;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final GarbagePreferences prefs = preferencesService().readGarbagePreferences(this);
        setupHeader(prefs);
        setupDates(prefs);
        setupHelpText();
        if (isNotificationEnabled(this)) {
            requestNotificationPermission();
            GarbageNotifier.startNotificationAlarmRepeating(this);
        }
    }

    private void setupHeader(final GarbagePreferences prefs) {
        final TextView header = findViewById(R.id.text_header);
        final Locale locale = getResources().getConfiguration().getLocales().get(0);
        final String formatted = getString(R.string.label_dates, prefs.getDayOfWeek().getDisplayName(TextStyle.FULL, locale));
        header.setText(formatted);
    }

    private void setupDates(final GarbagePreferences prefs) {
        final RecyclerView dates = findViewById(R.id.list_dates);
        dates.setLayoutManager(new LinearLayoutManager(this));
        dates.setAdapter(datesAdapter(findGarbageDaysIfEnabled(prefs)));
    }

    private List<GarbageDay> findGarbageDaysIfEnabled(final GarbagePreferences prefs) {
        if (prefs.isGarbageEnabled() || prefs.isRecyclingEnabled()) {
            return findGarbageDays(prefs);
        } else {
            return emptyList();
        }
    }

    private List<GarbageDay> findGarbageDays(final GarbagePreferences prefs) {
        final GarbageScheduleService service = garbageScheduleService(this);
        final Garbage garbage = service.createGarbage(prefs);
        return service.getGarbageDays(garbage, LocalDate.now(), PICKUP_DAYS_TO_SHOW);
    }

    private RecyclerView.Adapter<?> datesAdapter(final List<GarbageDay> days) {
        return new TwoLineListAdapter(days.stream()
                .map(this::formatPickupDay)
                .collect(toList()));
    }

    private String[] formatPickupDay(final GarbageDay day) {
        return new String[] {Text.formatDateMedium(this, day.getDate()), formatPickupItem(day)};
    }

    private String formatPickupItem(final GarbageDay day) {
        final PickupItemFormatter formatter = new PickupItemFormatter(this,
                R.string.notification_item_garbage,
                R.string.notification_item_bulk,
                R.string.notification_item_recycling);
        final String items = formatter.format(day, ", ");
        return Text.capitalize(this, items);
    }

    private void setupHelpText() {
        final NavigationPreferences prefs = navigationService().readNavigationPreferences(this);

        if (!prefs.hasNavigatedToSettings()) {
            final TextView help = findViewById(R.id.text_help);
            help.setVisibility(TextView.VISIBLE);

            prefs.setNavigatedToSettings(true);
            navigationService().writeNavigationPreferences(this, prefs);
        }
    }

    private void requestNotificationPermission() {
        if (isTiramisuOrLater() && !isNotificationPermissionGranted()) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.POST_NOTIFICATIONS }, 1);
        }
    }

    private boolean isTiramisuOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    private boolean isNotificationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            launchSettings();
            return true;
        }

        if (id == R.id.action_about) {
            launchAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchSettings() {
        final Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

    private void launchAbout() {
        final Intent about = new Intent(this, AboutActivity.class);
        startActivity(about);
    }
}
