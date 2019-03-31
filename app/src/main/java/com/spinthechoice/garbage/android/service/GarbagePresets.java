package com.spinthechoice.garbage.android.service;

import com.spinthechoice.garbage.GlobalGarbageConfiguration;

import java.time.DayOfWeek;
import java.time.LocalDate;

final class GarbagePresets {
    private GarbagePresets() {
        throw new UnsupportedOperationException("cannot instantiate " + getClass());
    }

    // TODO Hamburg, East Aurora, Williamsville, Lancaster, Clarence, Kenmore, Tonawanda

    static GarbageOption amherst() {
        return GarbageOption.builder()
                .setId("661a7325-e301-4c5e-8baa-ec37c17441df")
                .setName("Amherst")
                .setConfiguration(amherstConfig())
                .build();
    }

    static GlobalGarbageConfiguration amherstConfig() {
        return GlobalGarbageConfiguration.builder()
                .setResetDay(DayOfWeek.SUNDAY)
                .setStart(LocalDate.parse("2018-12-30"))
                .setLeapDays(
                        LocalDate.parse("2019-01-01"),
                        LocalDate.parse("2019-05-27"),
                        LocalDate.parse("2019-07-04"),
                        LocalDate.parse("2019-09-02"),
                        LocalDate.parse("2019-11-28"),
                        LocalDate.parse("2019-12-25"),
                        LocalDate.parse("2020-01-01"))
                .build();
    }

    static GarbageOption buffalo() {
        return GarbageOption.builder()
                .setId("2b449a5d-244e-4a0c-bd82-562ad5340db1")
                .setName("Buffalo")
                .setConfiguration(buffaloConfig())
                .build();
    }

    static GlobalGarbageConfiguration buffaloConfig() {
        return GlobalGarbageConfiguration.builder()
                .setResetDay(DayOfWeek.SUNDAY)
                .setStart(LocalDate.parse("2018-12-30"))
                .setHolidays(LocalDate.parse("2019-12-25"), LocalDate.parse("2020-01-01"))
                .build();
    }

    static GarbageOption cheektowaga() {
        return GarbageOption.builder()
                .setId("018fff06-5181-4421-802d-c0063f16e799")
                .setName("Cheektowaga")
                .setConfiguration(cheektowagaConfig())
                .build();
    }

    static GlobalGarbageConfiguration cheektowagaConfig() {
        return GlobalGarbageConfiguration.builder()
                .setResetDay(DayOfWeek.SUNDAY)
                .setStart(LocalDate.parse("2018-04-08"))
                .setRecyclingWeeks("A", "B")
                .setLeapDays(LocalDate.parse("2019-12-25"), LocalDate.parse("2020-01-01"))
                .build();
    }

    static GarbageOption orchardPark() {
        return GarbageOption.builder()
                .setId("1fe69b89-3523-4ab5-9ab5-5447e8566dfe")
                .setName("Orchard Park")
                .setConfiguration(orchardParkConfig())
                .build();
    }

    static GlobalGarbageConfiguration orchardParkConfig() {
        return GlobalGarbageConfiguration.builder()
                .setResetDay(DayOfWeek.SUNDAY)
                .setStart(LocalDate.parse("2018-12-30"))
                .setRecyclingWeeks("White", "Shaded")
                .setLeapDays(
                        LocalDate.parse("2019-01-01"),
                        LocalDate.parse("2019-05-27"),
                        LocalDate.parse("2019-07-04"),
                        LocalDate.parse("2019-09-02"),
                        LocalDate.parse("2019-11-28"),
                        LocalDate.parse("2019-12-25"),
                        LocalDate.parse("2020-01-01"))
                .build();
    }

    static GarbageOption westSeneca() {
        return GarbageOption.builder()
                .setId("1eea6061-63f4-462b-bf97-70e84141f405")
                .setName("West Seneca")
                .setConfiguration(westSenecaConfig())
                .build();
    }

    static GlobalGarbageConfiguration westSenecaConfig() {
        return GlobalGarbageConfiguration.builder()
                .setResetDay(DayOfWeek.SUNDAY)
                .setStart(LocalDate.parse("2019-05-01"))
                .setRecyclingWeeks("A", "B")
                .setLeapDays(
                        LocalDate.parse("2019-05-27"),
                        LocalDate.parse("2019-07-04"),
                        LocalDate.parse("2019-09-02"),
                        LocalDate.parse("2019-11-28"),
                        LocalDate.parse("2019-12-25"),
                        LocalDate.parse("2020-01-01"))
                .build();
    }
}
