package com.spinthechoice.garbage.android.garbage;

import com.spinthechoice.garbage.GlobalGarbageConfiguration;
import com.spinthechoice.garbage.android.preferences.HolidayRef;
import com.spinthechoice.garbage.android.holiday.HolidayService;
import com.spinthechoice.garbage.android.preferences.NamedHoliday;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.json.Jsonable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

public class AppGlobalGarbageConfiguration implements Jsonable {
    private LocalDate start;
    private DayOfWeek reset;
    private Map<PickupItem, PickupItemConfiguration> pickupItems;
    private Set<LocalDate> bulkDays;
    private Set<HolidayRef> holidays;

    public static AppGlobalGarbageConfiguration fromPreferences(final GarbagePreferences prefs) {
        final AppGlobalGarbageConfiguration config = new AppGlobalGarbageConfiguration();
        config.setReset(DayOfWeek.SUNDAY);
        config.setStart(rangeClosed(1, DayOfWeek.values().length)
                .mapToObj(date -> LocalDate.of(2020, Month.JANUARY, date))
                .filter(date -> date.getDayOfWeek() == DayOfWeek.SUNDAY)
                .findFirst()
                .get());
        final HashMap<PickupItem, PickupItemConfiguration> items = new HashMap<>();
        if (prefs.isGarbageEnabled()) {
            items.put(PickupItem.GARBAGE,
                    new PickupItemConfiguration(PickupItem.GARBAGE, true, prefs.getGarbageWeeks()));
        }
        if (prefs.isRecyclingEnabled()) {
            items.put(PickupItem.RECYCLING,
                    new PickupItemConfiguration(PickupItem.RECYCLING, true, prefs.getRecyclingWeeks()));
        }
        config.setPickupItems(items);
        config.setHolidays(prefs.getSelectedHolidays());
        config.setBulkDays(emptySet());
        return config;
    }

    public GlobalGarbageConfiguration toConfig(final HolidayService holidayService) {
        final GlobalGarbageConfiguration.Builder builder = GlobalGarbageConfiguration.builder()
                .setResetDay(reset)
                .setStart(start)
                .setBulkDays(bulkDays)
                .setHolidays(holidays.stream()
                        .filter(h -> !h.isLeap())
                        .map(h -> holidayService.findById(h.getId()).map(NamedHoliday::getHoliday))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(toSet()))
                .setLeapDays(holidays.stream()
                        .filter(h -> h.isLeap())
                        .map(h -> holidayService.findById(h.getId()).map(NamedHoliday::getHoliday))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(toSet()));

        if (pickupItems.containsKey(PickupItem.GARBAGE)) {
            final PickupItemConfiguration item = pickupItems.get(PickupItem.GARBAGE);
            builder.setGarbageEnabled(item.isEnabled());
            builder.setGarbageWeeks(item.getWeeks());
        }

        if (pickupItems.containsKey(PickupItem.RECYCLING)) {
            final PickupItemConfiguration item = pickupItems.get(PickupItem.RECYCLING);
            builder.setRecyclingEnabled(item.isEnabled());
            builder.setRecyclingWeeks(item.getWeeks());
        }

        return builder.build();
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(final LocalDate start) {
        this.start = start;
    }

    public DayOfWeek getReset() {
        return reset;
    }

    public void setReset(final DayOfWeek reset) {
        this.reset = reset;
    }

    public Map<PickupItem, PickupItemConfiguration> getPickupItems() {
        return pickupItems;
    }

    public void setPickupItems(final Map<PickupItem, PickupItemConfiguration> pickupItems) {
        this.pickupItems = pickupItems;
    }

    public Set<LocalDate> getBulkDays() {
        return bulkDays;
    }

    public void setBulkDays(final Set<LocalDate> bulkDays) {
        this.bulkDays = bulkDays;
    }

    public Set<HolidayRef> getHolidays() {
        return holidays;
    }

    public void setHolidays(final Set<HolidayRef> holidays) {
        this.holidays = holidays;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("start", start.toString());
        json.put("reset", reset.name());
        json.put("items", toJson(pickupItems.values()));
        json.putOpt("bulkDays", datesToJson(bulkDays));
        json.putOpt("holidays", toJson(holidays));
        return json;
    }

    private static List<PickupItemConfiguration> toPickupItems(final GlobalGarbageConfiguration configuration) {
        final List<PickupItemConfiguration> items = new LinkedList<>();
        items.add(new PickupItemConfiguration(PickupItem.GARBAGE,
                configuration.isGarbageEnabled(), configuration.getGarbageWeeks()));
        items.add(new PickupItemConfiguration(PickupItem.RECYCLING,
                configuration.isRecyclingEnabled(), configuration.getRecyclingWeeks()));
        return items;
    }

    static JSONArray toJson(final Collection<? extends Jsonable> list) throws JSONException {
        final JSONArray json = new JSONArray();
        if (list != null) {
            for (Jsonable elem : list) {
                json.put(elem.toJson());
            }
        }
        return json;
    }

    private static JSONArray datesToJson(final Collection<LocalDate> dates) {
        return dates == null ? null : dates.stream()
                .map(LocalDate::toString)
                .reduce(new JSONArray(), JSONArray::put, (acc, cur) -> acc);
    }

    static AppGlobalGarbageConfiguration fromJson(final JSONObject json) {
        return json == null ? null : fromJsonNonNull(json);
    }

    private static AppGlobalGarbageConfiguration fromJsonNonNull(final JSONObject json) {
        final AppGlobalGarbageConfiguration config = new AppGlobalGarbageConfiguration();
        config.start = dateFromJson(json.optString("start"));
        config.reset = dayFromJson(json.optString("reset"));
        config.pickupItems = pickupItemsFromJson(json.optJSONArray("items"));
        config.bulkDays = datesFromJson(json.optJSONArray("bulkDays"));
        config.holidays = holidaysFromJson(json.optJSONArray("holidays"));
        return config;
    }

    private static LocalDate dateFromJson(final String json) {
        return json.isEmpty() ? null : LocalDate.parse(json);
    }

    private static DayOfWeek dayFromJson(final String json) {
        return json.isEmpty() ? null : DayOfWeek.valueOf(json);
    }

    private static Map<PickupItem, PickupItemConfiguration> pickupItemsFromJson(final JSONArray json) {
        return json == null ? emptyMap() : range(0, json.length())
                .mapToObj(json::optJSONObject)
                .filter(Objects::nonNull)
                .map(PickupItemConfiguration::fromJson)
                .collect(toMap(PickupItemConfiguration::getItem, Function.identity()));
    }

    private static Set<LocalDate> datesFromJson(final JSONArray json) {
        return json == null ? emptySet() : range(0, json.length())
                .mapToObj(json::optString)
                .filter(s -> !s.isEmpty())
                .map(LocalDate::parse)
                .collect(toSet());
    }

    private static Set<HolidayRef> holidaysFromJson(final JSONArray json) {
        return json == null ? emptySet() : range(0, json.length())
                .mapToObj(json::optJSONObject)
                .filter(Objects::nonNull)
                .map(HolidayRef::fromJson)
                .collect(toSet());
    }
}
