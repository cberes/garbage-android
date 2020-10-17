package com.spinthechoice.garbage.android.settings.holidays;

import com.spinthechoice.garbage.android.preferences.HolidayRef;
import com.spinthechoice.garbage.android.holiday.HolidayService;
import com.spinthechoice.garbage.android.preferences.NamedHoliday;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toSet;

class HolidayPickerItemFactory {
    private final HolidayService holidayService;
    private final DateTimeFormatter formatter;
    private final Set<String> postponeIds;
    private final Set<String> cancelIds;

    HolidayPickerItemFactory(final HolidayService holidayService, final Collection<HolidayRef> prefs) {
        this.holidayService = holidayService;
        this.formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        final Map<Boolean, Set<HolidayRef>> selected = prefs.stream()
                .collect(partitioningBy(HolidayRef::isLeap, toSet()));
        this.postponeIds = selected.get(true).stream().map(HolidayRef::getId).collect(toSet());
        this.cancelIds = selected.get(false).stream().map(HolidayRef::getId).collect(toSet());
    }

    HolidayPickerItem create(final String id) {
        final NamedHoliday holiday = holidayService.findById(id).get();
        final int year = LocalDate.now().getYear();
        final LocalDate date = holidayService.findDateForYear(holiday.getHoliday(), year).orElse(LocalDate.now());
        final HolidayPickerItem item = new HolidayPickerItem(
                holiday.getId(), holiday.getName(), date, date.format(formatter), holiday.getHoliday());
        item.setCancel(cancelIds.contains(holiday.getId()));
        item.setPostpone(postponeIds.contains(holiday.getId()));
        return item;
    }
}
