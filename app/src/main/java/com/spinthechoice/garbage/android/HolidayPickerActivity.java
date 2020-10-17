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
import com.spinthechoice.garbage.android.navigation.NavigationPreferences;
import com.spinthechoice.garbage.android.preferences.HolidayRef;

import java.util.Set;

public class HolidayPickerActivity extends AppCompatActivity implements WithHolidayService,
        WithNavigationService, WithPreferencesService {
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

        final GarbagePreferences prefs = preferencesService().readGarbagePreferences(this);

        final TextView header = findViewById(R.id.text_header);
        header.setText(getString(R.string.label_holiday_picker));

        final RecyclerView dates = findViewById(R.id.list_holiday_picker);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        dates.setLayoutManager(layoutManager);
        adapter = new HolidayPickerAdapter(holidayService(this),
                new HolidayPickerItemFactory(holidayService(this), prefs.getSelectedHolidays()));
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
                            editHoliday(holidayId, holidayService(HolidayPickerActivity.this).indexOf(holidayId));
                            finishActionMode();
                            return true;
                        }

                        if (id == R.id.action_delete_holiday) {
                            int index = holidayService(HolidayPickerActivity.this).deleteById(HolidayPickerActivity.this, holidayId);
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
        final GarbagePreferences prefs = preferencesService().readGarbagePreferences(this);
        final Set<HolidayRef> holidays = prefs.getSelectedHolidays();
        holidays.removeIf(holiday -> holiday.getId().equals(id));
        if (postpone || cancel) {
            holidays.add(new HolidayRef(id, postpone));
        }
        prefs.setSelectedHolidays(holidays);
        preferencesService().writeGarbagePreferences(this, prefs);
    }

    private void setupHelpText() {
        final NavigationPreferences prefs = navigationService().readNavigationPreferences(this);

        if (!prefs.hasNavigatedToHolidayPicker()) {
            final TextView help = findViewById(R.id.text_help);
            help.setVisibility(TextView.VISIBLE);

            prefs.setNavigatedToHolidayPicker(true);
            navigationService().writeNavigationPreferences(this, prefs);
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
            final int oldHolidayCount = holidayService(this).holidayCount();
            if (requestCode < oldHolidayCount) {
                adapter.notifyItemChanged(requestCode);
            } else {
                adapter.notifyItemInserted(requestCode);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
