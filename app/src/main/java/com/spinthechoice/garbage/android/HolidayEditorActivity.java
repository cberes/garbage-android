package com.spinthechoice.garbage.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.spinthechoice.garbage.Holiday;
import com.spinthechoice.garbage.HolidayOffset;
import com.spinthechoice.garbage.HolidayType;
import com.spinthechoice.garbage.android.service.HolidayService;
import com.spinthechoice.garbage.android.service.NamedHoliday;
import com.spinthechoice.garbage.android.service.PreferencesService;
import com.spinthechoice.garbage.android.util.AdapterUtils;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

public class HolidayEditorActivity extends AppCompatActivity {
    private final PreferencesService prefsService = new PreferencesService();

    private EditText holidayName;
    private EditText date;
    private Spinner dayOfWeek;
    private Spinner month;
    private Spinner offset;
    private Spinner type;
    private Spinner weekIndex;
    private LinearLayout dateLayout;
    private LinearLayout dayAndWeekLayout;

    private List<DayOfWeek> daysOfWeek;
    private List<Month> months;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_editor);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setFields();
        setupForm();
        populateForm(getEditingHoliday());
    }

    private void setFields() {
        holidayName = findViewById(R.id.edit_holiday_name);
        date = findViewById(R.id.number_holiday_date);
        dayOfWeek = findViewById(R.id.spinner_holiday_day_of_week);
        month = findViewById(R.id.spinner_holiday_month);
        offset = findViewById(R.id.spinner_holiday_offset);
        type = findViewById(R.id.spinner_holiday_type);
        weekIndex = findViewById(R.id.spinner_holiday_week);
        dateLayout = findViewById(R.id.layout_holiday_date);
        dayAndWeekLayout = findViewById(R.id.layout_holiday_day_and_week);
    }

    private void setupForm() {
        daysOfWeek = asList(DayOfWeek.values());
        dayOfWeek.setAdapter(AdapterUtils.dayOfWeekAdapter(this, daysOfWeek));

        months = asList(Month.values());
        month.setAdapter(AdapterUtils.monthAdapter(this, months));

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final HolidayType type = HolidayType.values()[position];
                toggleFormElements(type);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    private void toggleFormElements(final HolidayType type) {
        if (type == HolidayType.NTH_DAY_OF_WEEK) {
            dateLayout.setVisibility(LinearLayout.GONE);
            dayAndWeekLayout.setVisibility(LinearLayout.VISIBLE);
        } else {
            dayAndWeekLayout.setVisibility(LinearLayout.GONE);
            dateLayout.setVisibility(LinearLayout.VISIBLE);
        }
    }

    private NamedHoliday getEditingHoliday() {
        final HolidayService holidayService = new HolidayService(prefsService, this);
        return Optional.ofNullable(getHolidayId())
                .flatMap(holidayService::findById)
                .orElseGet(this::defaultHoliday);
    }

    private NamedHoliday defaultHoliday() {
        final Holiday holiday = Holiday.builder()
                .setDate(1)
                .setDayOfWeek(DayOfWeek.MONDAY)
                .setMonth(Month.JANUARY)
                .setOffset(HolidayOffset.DAY_OF)
                .setType(HolidayType.STATIC_DATE)
                .setWeekIndex(0)
                .build();
        return new NamedHoliday(null, "", holiday);
    }

    private void populateForm(final NamedHoliday holiday) {
        holidayName.setText(holiday.getName());
        date.setText(Integer.toString(holiday.getHoliday().getDate()));
        dayOfWeek.setSelection(daysOfWeek.indexOf(holiday.getHoliday().getDayOfWeek()));
        month.setSelection(holiday.getHoliday().getMonth().ordinal());
        offset.setSelection(holiday.getHoliday().getOffset().ordinal());
        type.setSelection(holiday.getHoliday().getType().ordinal());
        weekIndex.setSelection(holiday.getHoliday().getWeekIndex() == -1 ?
                weekIndex.getCount() - 1 : holiday.getHoliday().getWeekIndex());
    }

    private String getHolidayId() {
        return getIntent().getStringExtra("id");
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.holiday_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save_holiday) {
            saveHoliday(buildHoliday());
            setResult(RESULT_OK);
            finish();
            return true;
        }

        if (id == R.id.action_cancel_holiday) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private NamedHoliday buildHoliday() {
        final String name = holidayName.getText().toString();
        final Holiday holiday = Holiday.builder()
                .setDate(Integer.parseInt(date.getText().toString()))
                .setDayOfWeek(daysOfWeek.get(dayOfWeek.getSelectedItemPosition()))
                .setMonth(months.get(month.getSelectedItemPosition()))
                .setOffset(HolidayOffset.values()[offset.getSelectedItemPosition()])
                .setType(HolidayType.values()[type.getSelectedItemPosition()])
                .setWeekIndex(weekIndex.getSelectedItemPosition() == weekIndex.getCount() - 1 ?
                        -1 : weekIndex.getSelectedItemPosition())
                .build();
        return new NamedHoliday(getHolidayId(), name, holiday);
    }

    private void saveHoliday(final NamedHoliday holiday) {
        final HolidayService holidayService = new HolidayService(prefsService, this);
        holidayService.save(holiday, this);
    }
}
