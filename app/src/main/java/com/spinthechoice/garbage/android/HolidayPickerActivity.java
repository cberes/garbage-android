package com.spinthechoice.garbage.android;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.preferences.NavigationPreferences;
import com.spinthechoice.garbage.android.service.HolidayRef;
import com.spinthechoice.garbage.android.service.HolidayService;
import com.spinthechoice.garbage.android.service.NavigationService;
import com.spinthechoice.garbage.android.service.PreferencesService;

import java.util.Set;

public class HolidayPickerActivity extends AppCompatActivity {
    private final PreferencesService prefsService = new PreferencesService();

    private HolidayService holidayService;
    private HolidayPickerAdapter adapter;
    private ActionMode actionMode;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holiday_picker);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final GarbagePreferences prefs = prefsService.readGarbagePreferences(this, R.raw.holidays);
        holidayService = new HolidayService(prefsService, this);

        final TextView header = findViewById(R.id.text_header);
        header.setText(getString(R.string.label_holiday_picker));

        final RecyclerView dates = findViewById(R.id.list_holiday_picker);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        dates.setLayoutManager(layoutManager);
        adapter = new HolidayPickerAdapter(holidayService,
                new HolidayPickerItemFactory(holidayService, prefs.getSelectedHolidays()));
        adapter.setOnChangeListener(new HolidayPickerAdapter.OnChangeListener() {
            @Override
            public void changed(final String id, final boolean postpone, final boolean cancel) {
                updateGarbagePreferences(id, postpone, cancel);
            }
        });
        adapter.setOnItemSelectedListener(new HolidayPickerAdapter.OnItemSelectedListener() {
            @Override
            public boolean selected(final String holidayId) {
                actionMode = HolidayPickerActivity.this.startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
                        getMenuInflater().inflate(R.menu.holiday_picker_action, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.action_edit_holiday) {
                            editHoliday(holidayId, holidayService.indexOf(holidayId));
                            finishActionMode();
                            return true;
                        }

                        if (id == R.id.action_delete_holiday) {
                            int index = holidayService.deleteById(holidayId, HolidayPickerActivity.this);
                            if (index != -1) {
                                adapter.notifyItemRemoved(index);
                            }
                            finishActionMode();
                            return true;
                        }

                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(final ActionMode actionMode) {
                        HolidayPickerActivity.this.actionMode = null;
                    }
                });
                return true;
            }
        });
        dates.setAdapter(adapter);

        setupHelpText();
    }

    private void finishActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    protected void onStop() {
        finishActionMode();
        super.onStop();
    }

    private void updateGarbagePreferences(final String id, final boolean postpone, final boolean cancel) {
        final GarbagePreferences prefs = prefsService.readGarbagePreferences(this, R.raw.holidays);
        final Set<HolidayRef> holidays = prefs.getSelectedHolidays();
        holidays.removeIf(holiday -> holiday.getId().equals(id));
        if (postpone || cancel) {
            holidays.add(new HolidayRef(id, postpone));
        }
        prefs.setSelectedHolidays(holidays);
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.holiday_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_holiday) {
            addHoliday();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addHoliday() {
        final Intent holidayEditor = new Intent(this, HolidayEditorActivity.class);
        startActivityForResult(holidayEditor, adapter.getItemCount());
    }

    private void editHoliday(final String id, final int index) {
        final Intent holidayEditor = new Intent(this, HolidayEditorActivity.class);
        holidayEditor.putExtra("id", id);
        startActivityForResult(holidayEditor, index);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if (resultCode == RESULT_OK) {
            final int oldHolidayCount = holidayService.holidayCount();
            holidayService.refresh(this);
            if (requestCode < oldHolidayCount) {
                adapter.notifyItemChanged(requestCode);
            } else {
                adapter.notifyItemInserted(requestCode);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
