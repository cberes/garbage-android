package com.spinthechoice.garbage.android.settings.holidays;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.spinthechoice.garbage.android.R;
import com.spinthechoice.garbage.android.holidays.HolidayService;

class HolidayPickerAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private final HolidayService holidayService;
    private final HolidayPickerItemFactory viewHolderFactory;
    private OnChangeListener changeListener;
    private OnItemSelectedListener selectedListener;

    HolidayPickerAdapter(final HolidayService holidayService,
                         final HolidayPickerItemFactory viewHolderFactory) {
        this.holidayService = holidayService;
        this.viewHolderFactory = viewHolderFactory;
    }

    public void setOnChangeListener(final OnChangeListener listener) {
        this.changeListener = listener;
    }

    public void setOnItemSelectedListener(final OnItemSelectedListener listener) {
        this.selectedListener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holiday_picker_item, parent, false);
        TextView holiday = v.findViewById(R.id.text_holiday);
        TextView date = v.findViewById(R.id.text_date);
        CheckBox postpone = v.findViewById(R.id.check_postpone);
        CheckBox cancel = v.findViewById(R.id.check_cancel);
        return new ItemViewHolder(v, holiday, date, postpone, cancel);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final String id = holidayService.findAll().get(position).getId();
        final HolidayPickerItem item = viewHolderFactory.create(id);
        holder.setHolidayText(item.getName());
        holder.setDateText(item.getDateText());
        holder.setPostponeChecked(item.isPostpone());
        holder.setCancelChecked(item.isCancel());

        holder.setPostponeChangedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton button, final boolean checked) {
                if (checked) {
                    holder.setCancelChecked(false);
                    item.setCancel(false);
                }

                item.setPostpone(checked);

                invokeChangeListener(item);
            }
        });

        holder.setCancelChangedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton button, final boolean checked) {
                if (checked) {
                    holder.setPostponeChecked(false);
                    item.setPostpone(false);
                }

                item.setCancel(checked);

                invokeChangeListener(item);
            }
        });

        holder.setLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                return invokeSelectedListener(item);
            }
        });
    }

    private void invokeChangeListener(final HolidayPickerItem item) {
        if (changeListener != null) {
            changeListener.changed(item.getId(), item.isPostpone(), item.isCancel());
        }
    }

    private boolean invokeSelectedListener(final HolidayPickerItem item) {
        return selectedListener != null &&
                selectedListener.selected(item.getId());
    }

    @Override
    public int getItemCount() {
        return holidayService.holidayCount();
    }
}
