package com.spinthechoice.garbage.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.preferences.NavigationPreferences;
import com.spinthechoice.garbage.android.service.HolidayRef;
import com.spinthechoice.garbage.android.service.HolidayService;
import com.spinthechoice.garbage.android.service.NamedHoliday;
import com.spinthechoice.garbage.android.service.NavigationService;
import com.spinthechoice.garbage.android.service.PreferencesService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class HolidayPickerActivity extends AppCompatActivity {
    private final PreferencesService prefsService = new PreferencesService();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_picker);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final HolidayService holidayService = new HolidayService(this, R.raw.holidays);
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(this);

        final TextView header = findViewById(R.id.text_header);
        header.setText(getString(R.string.label_holiday_picker));

        final RecyclerView dates = findViewById(R.id.list_holiday_picker);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        dates.setLayoutManager(layoutManager);
        final HolidayPickerAdapter adapter = new HolidayPickerAdapter(buildPickerItems(
                holidayService.findAll(), prefs.getHolidays(), holidayService));
        adapter.setOnChangeListener(new HolidayPickerAdapter.OnChangeListener() {
            @Override
            public void changed(final String id, final boolean postpone, final boolean cancel) {
                updateGarbagePreferences(id, postpone, cancel);
            }
        });
        dates.setAdapter(adapter);

        setupHelpText();
    }

    private List<HolidayPickerItem> buildPickerItems(final List<NamedHoliday> holidays,
                                                     final Collection<HolidayRef> prefs,
                                                     final HolidayService holidayService) {
        final int year = LocalDate.now().getYear();
        final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        final Map<Boolean, Set<HolidayRef>> selected = prefs.stream()
                .collect(partitioningBy(HolidayRef::isLeap, toSet()));
        final Set<String> postponeIds = selected.get(true).stream().map(HolidayRef::getId).collect(toSet());
        final Set<String> cancelIds = selected.get(false).stream().map(HolidayRef::getId).collect(toSet());
        return holidays.stream()
                .map(holiday -> {
                    final LocalDate date = holidayService.findDateForYear(holiday.getHoliday(), year).orElse(LocalDate.now());
                    final HolidayPickerItem item = new HolidayPickerItem(
                            holiday.getId(), holiday.getName(), date, date.format(formatter), holiday.getHoliday());
                    item.setCancel(cancelIds.contains(holiday.getId()));
                    item.setPostpone(postponeIds.contains(holiday.getId()));
                    return item;
                })
                .sorted(Comparator.comparing(HolidayPickerItem::getDate))
                .collect(toList());
    }

    private void updateGarbagePreferences(final String id, final boolean postpone, final boolean cancel) {
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(this);
        final Set<HolidayRef> holidays = prefs.getHolidays();
        holidays.removeIf(holiday -> holiday.getId().equals(id));
        if (postpone || cancel) {
            holidays.add(new HolidayRef(id, postpone));
        }
        prefs.setHolidays(holidays);
        prefsService.writeGarbagePreferences(this, prefs);
    }

    private void setupHelpText() {
        final NavigationService service = new NavigationService();
        final NavigationPreferences prefs = service.readNavigationPreferences(this);

        if (!prefs.hasNavigatedToHolidayPicker()) {
            final TextView help = findViewById(R.id.text_help);
            help.setVisibility(TextView.VISIBLE);

            prefs.setNavigatedToHolidayPicker(true);
            service.writeNavigationPreferences(this, prefs);
        }
    }
}
