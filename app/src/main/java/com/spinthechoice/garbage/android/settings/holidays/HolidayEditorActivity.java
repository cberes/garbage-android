package com.spinthechoice.garbage.android.settings.holidays;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.spinthechoice.garbage.Holiday;
import com.spinthechoice.garbage.HolidayOffset;
import com.spinthechoice.garbage.HolidayType;
import com.spinthechoice.garbage.android.R;
import com.spinthechoice.garbage.android.adapters.DayOfWeekAdapter;
import com.spinthechoice.garbage.android.adapters.MonthAdapter;
import com.spinthechoice.garbage.android.mixins.WithHolidayService;
import com.spinthechoice.garbage.android.preferences.NamedHoliday;
import com.spinthechoice.garbage.android.text.Text;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

public class HolidayEditorActivity extends AppCompatActivity implements WithHolidayService {
    private EditText holidayName;
    private EditText date;
    private Spinner dayOfWeek;
    private Spinner month;
    private Spinner offset;
    private Spinner type;
    private Spinner weekIndex;
    private LinearLayout dateLayout;
    private LinearLayout dayAndWeekLayout;
    private TextView errorHolidayDate;
    private TextView errorHolidayName;

    private List<DayOfWeek> daysOfWeek;
    private List<Month> months;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_editor);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle();
        findViews();
        setupForm();
        populateForm(getEditingHoliday());
    }

    private void setTitle() {
        if (!getHolidayId().isPresent()) {
            setTitle(R.string.action_add_holiday);
        }
    }

    private Optional<String> getHolidayId() {
        return Optional.ofNullable(getIntent().getStringExtra("id"));
    }

    private void findViews() {
        holidayName = findViewById(R.id.edit_holiday_name);
        date = findViewById(R.id.number_holiday_date);
        dayOfWeek = findViewById(R.id.spinner_holiday_day_of_week);
        month = findViewById(R.id.spinner_holiday_month);
        offset = findViewById(R.id.spinner_holiday_offset);
        type = findViewById(R.id.spinner_holiday_type);
        weekIndex = findViewById(R.id.spinner_holiday_week);
        dateLayout = findViewById(R.id.layout_holiday_date);
        dayAndWeekLayout = findViewById(R.id.layout_holiday_day_and_week);
        errorHolidayDate = findViewById(R.id.error_holiday_date);
        errorHolidayName = findViewById(R.id.error_holiday_name);
    }

    private void setupForm() {
        daysOfWeek = asList(DayOfWeek.values());
        dayOfWeek.setAdapter(new DayOfWeekAdapter(this, daysOfWeek));

        months = asList(Month.values());
        month.setAdapter(new MonthAdapter(this, months));

        ((ArrayAdapter<?>) type.getAdapter()).setDropDownViewResource(R.layout.spinner_item);
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

        ((ArrayAdapter<?>) offset.getAdapter()).setDropDownViewResource(R.layout.spinner_item);

        ((ArrayAdapter<?>) weekIndex.getAdapter()).setDropDownViewResource(R.layout.spinner_item);
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
        return getHolidayId()
                .flatMap(holidayService(this)::findById)
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
        date.setText(Text.intToString(this, holiday.getHoliday().getDate()));
        dayOfWeek.setSelection(daysOfWeek.indexOf(holiday.getHoliday().getDayOfWeek()));
        month.setSelection(holiday.getHoliday().getMonth().ordinal());
        offset.setSelection(holiday.getHoliday().getOffset().ordinal());
        type.setSelection(holiday.getHoliday().getType().ordinal());
        weekIndex.setSelection(holiday.getHoliday().getWeekIndex() == -1 ?
                weekIndex.getCount() - 1 : holiday.getHoliday().getWeekIndex());
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.holiday_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            handleCancel();
            return true;
        }

        if (id == R.id.action_save_holiday) {
            handleDone();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void handleDone() {
        resetErrors();
        final NamedHoliday holiday = buildHoliday();
        if (validate(holiday)) {
            saveHoliday(holiday);
            setResult(RESULT_OK);
            finish();
        }
    }

    private void resetErrors() {
        errorHolidayDate.setVisibility(TextView.GONE);
        errorHolidayName.setVisibility(TextView.GONE);
    }

    private boolean validate(final NamedHoliday holiday) {
        boolean valid = true;

        if (holiday.getHoliday().getType() == HolidayType.STATIC_DATE) {
            if (holiday.getHoliday().getDate() < 1 || holiday.getHoliday().getDate() > 31) {
                errorHolidayDate.setVisibility(TextView.VISIBLE);
                valid = false;
            }
        }

        if (holiday.getName() == null || holiday.getName().isEmpty()) {
            errorHolidayName.setVisibility(TextView.VISIBLE);
            valid = false;
        }

        return valid;
    }

    private NamedHoliday buildHoliday() {
        final String name = holidayName.getText().toString();
        final Holiday holiday = Holiday.builder()
                .setDate(tryParseInt(date.getText().toString(), -1))
                .setDayOfWeek(daysOfWeek.get(dayOfWeek.getSelectedItemPosition()))
                .setMonth(months.get(month.getSelectedItemPosition()))
                .setOffset(HolidayOffset.values()[offset.getSelectedItemPosition()])
                .setType(HolidayType.values()[type.getSelectedItemPosition()])
                .setWeekIndex(weekIndex.getSelectedItemPosition() == weekIndex.getCount() - 1 ?
                        -1 : weekIndex.getSelectedItemPosition())
                .build();
        return new NamedHoliday(getHolidayId().orElse(null), name, holiday);
    }

    private static int tryParseInt(final String s, final int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void saveHoliday(final NamedHoliday holiday) {
        holidayService(this).save(this, holiday);
    }
}
