package com.spinthechoice.garbage.android.settings.holidays;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class ItemViewHolder extends RecyclerView.ViewHolder {
    private final View itemView;
    private final TextView holiday;
    private final TextView date;
    private final CheckBox postpone;
    private final CheckBox cancel;

    ItemViewHolder(final RelativeLayout layout, final TextView holiday, final TextView date,
                   final CheckBox postpone, final CheckBox cancel) {
        super(layout);
        this.itemView = layout;
        this.holiday = holiday;
        this.date = date;
        this.postpone = postpone;
        this.cancel = cancel;
    }

    void setHolidayText(final String text) {
        holiday.setText(text);
    }

    void setDateText(final String text) {
        date.setText(text);
    }

    void setPostponeChecked(final boolean checked) {
        postpone.setChecked(checked);
    }

    void setCancelChecked(final boolean checked) {
        cancel.setChecked(checked);
    }

    void setPostponeChangedListener(final CompoundButton.OnCheckedChangeListener listener) {
        postpone.setOnCheckedChangeListener(listener);
    }

    void setCancelChangedListener(final CompoundButton.OnCheckedChangeListener listener) {
        cancel.setOnCheckedChangeListener(listener);
    }

    void setLongClickListener(final View.OnLongClickListener listener) {
        itemView.setOnLongClickListener(listener);
    }
}
