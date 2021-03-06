package com.spinthechoice.garbage.android.settings.holidays;

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

import com.spinthechoice.garbage.android.R;
import com.spinthechoice.garbage.android.mixins.WithHolidayService;
import com.spinthechoice.garbage.android.mixins.WithNavigationService;
import com.spinthechoice.garbage.android.mixins.WithPreferencesService;
import com.spinthechoice.garbage.android.preferences.GarbagePreferences;
import com.spinthechoice.garbage.android.navigation.NavigationPreferences;

public class HolidayPickerActivity extends AppCompatActivity
        implements WithHolidayService, WithNavigationService, WithPreferencesService {
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
        setupHolidaysView();
        setupHelpText();
    }

    private void setupHolidaysView() {
        final RecyclerView dates = findViewById(R.id.list_holiday_picker);
        dates.setLayoutManager(new LinearLayoutManager(this));
        final GarbagePreferences prefs = preferencesService().readGarbagePreferences(this);
        final HolidayPickerItemFactory itemFactory =
                new HolidayPickerItemFactory(holidayService(this), prefs.getSelectedHolidays());
        adapter = new HolidayPickerAdapter(holidayService(this), itemFactory);
        dates.setAdapter(adapter);
        setupHolidayChangeListener();
        setupHolidaySelectedListener();
    }

    private void setupHolidayChangeListener() {
        adapter.setOnChangeListener(new OnChangeListener() {
            @Override
            public void changed(final String id, final boolean postpone, final boolean cancel) {
                final GarbagePreferences prefs = preferencesService().readGarbagePreferences(HolidayPickerActivity.this);
                prefs.updateHoliday(id, postpone, cancel);
                preferencesService().writeGarbagePreferences(HolidayPickerActivity.this, prefs);
            }
        });
    }

    private void setupHolidaySelectedListener() {
        adapter.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean selected(final String holidayId) {
                setupActionMode(holidayId);
                return true;
            }
        });
    }

    private void setupActionMode(final String holidayId) {
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
            updateAdapterAfterEdit(requestCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateAdapterAfterEdit(final int requestCode) {
        final int oldHolidayCount = holidayService(this).holidayCount();
        if (requestCode < oldHolidayCount) {
            adapter.notifyItemChanged(requestCode);
        } else {
            adapter.notifyItemInserted(requestCode);
        }
    }
}
