package com.spinthechoice.garbage.android.service;

import android.content.Context;

import com.spinthechoice.garbage.Holiday;
import com.spinthechoice.garbage.Holidays;
import com.spinthechoice.garbage.android.R;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.stream.Collectors.toConcurrentMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;

public class HolidayService {
    private final PreferencesService prefsService;
    private final List<NamedHoliday> holidays;
    private final Map<String, NamedHoliday> holidaysById;

    public HolidayService(final PreferencesService prefsService, final Context context) {
        this.prefsService = prefsService;
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(context, R.raw.holidays);
        this.holidays = new CopyOnWriteArrayList<>(prefs.getHolidays());
        this.holidaysById = this.holidays.stream()
                .collect(toConcurrentMap(NamedHoliday::getId, x -> x));
    }

    public void refresh(final Context context) {
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(context, R.raw.holidays);
        this.holidays.clear();
        this.holidays.addAll(prefs.getHolidays());
        this.holidaysById.clear();
        this.holidaysById.putAll(this.holidays.stream()
                .collect(toMap(NamedHoliday::getId, x -> x)));
    }

    public int holidayCount() {
        return holidays.size();
    }

    Optional<Holiday> findHolidaySettingById(final String id) {
        return findById(id).map(NamedHoliday::getHoliday);
    }

    public Optional<NamedHoliday> findById(final String id) {
        return Optional.ofNullable(holidaysById.get(id));
    }

    public List<NamedHoliday> findAll() {
        return new ArrayList<>(holidays);
    }

    public Optional<LocalDate> findDateForYear(final Holiday holiday, final int year) {
        final Holidays holidayFinder = new Holidays(holiday);
        return holidayFinder.dates(year).stream().findFirst();
    }

    public void save(final NamedHoliday holiday, final Context context) {
        final NamedHoliday holidayWithId = assignId(holiday);

        synchronized (this) {
            if (holidaysById.containsKey(holidayWithId.getId())) {
                holidays.set(indexOf(holidayWithId.getId()), holidayWithId);
            } else {
                holidays.add(holidayWithId);
            }
            holidaysById.put(holidayWithId.getId(), holidayWithId);
            updatePreferences(context);
        }
    }

    private NamedHoliday assignId(final NamedHoliday holiday) {
        if (holiday.getId() == null || holiday.getId().isEmpty()) {
            return new NamedHoliday(UUID.randomUUID().toString(), holiday.getName(), holiday.getHoliday());
        } else {
            return holiday;
        }
    }

    public int deleteById(final String id, final Context context) {
        final int index;

        synchronized (this) {
            index = indexOf(id);
            holidaysById.remove(id);
            final boolean removed = holidays.removeIf(holiday -> holiday.getId().equals(id));

            if (removed) {
                updatePreferences(context);
            }
        }

        return index;
    }

    public int indexOf(final String id) {
        final List<NamedHoliday> holidaysCopy = new ArrayList<>(holidays);
        return range(0, holidaysCopy.size())
                .filter(i -> holidaysCopy.get(i).getId().equals(id))
                .findAny()
                .orElse(-1);
    }

    private void updatePreferences(final Context context) {
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(context, R.raw.holidays);
        final Set<String> allIds = holidays.stream().map(NamedHoliday::getId).collect(toSet());
        prefs.setHolidays(holidays);
        prefs.setSelectedHolidays(prefs.getSelectedHolidays().stream()
                .filter(holiday -> allIds.contains(holiday.getId()))
                .collect(toSet()));
        prefsService.writeGarbagePreferences(context, prefs);
    }
}
