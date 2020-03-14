package com.spinthechoice.garbage.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

class HolidayPickerAdapter extends RecyclerView.Adapter<HolidayPickerAdapter.ItemViewHolder> {
    @FunctionalInterface
    interface OnChangeListener {
        void changed(String id, boolean postpone, boolean cancel);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView holiday;
        private final TextView date;
        private final CheckBox postpone;
        private final CheckBox cancel;

        ItemViewHolder(final RelativeLayout layout, final TextView holiday, final TextView date,
                       final CheckBox postpone, final CheckBox cancel) {
            super(layout);
            this.holiday = holiday;
            this.date = date;
            this.postpone = postpone;
            this.cancel = cancel;
        }
    }

    private final List<HolidayPickerItem> holidays;
    private OnChangeListener listener;

    HolidayPickerAdapter(final List<HolidayPickerItem> holidays) {
        this.holidays = holidays;
    }

    public void setOnChangeListener(final OnChangeListener listener) {
        this.listener = listener;
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
        final HolidayPickerItem item = holidays.get(position);
        holder.holiday.setText(item.getName());
        holder.date.setText(item.getDateText());
        holder.postpone.setChecked(item.isPostpone());
        holder.cancel.setChecked(item.isCancel());

        holder.postpone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton button, final boolean checked) {
                if (checked) {
                    holder.cancel.setChecked(false);
                    item.setCancel(false);
                }

                item.setPostpone(checked);

                invokeListener(item);
            }
        });

        holder.cancel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton button, final boolean checked) {
                if (checked) {
                    holder.postpone.setChecked(false);
                    item.setPostpone(false);
                }

                item.setCancel(checked);

                invokeListener(item);
            }
        });
    }

    private void invokeListener(final HolidayPickerItem item) {
        if (listener != null) {
            listener.changed(item.getId(), item.isPostpone(), item.isCancel());
        }
    }

    @Override
    public int getItemCount() {
        return holidays.size();
    }
}
