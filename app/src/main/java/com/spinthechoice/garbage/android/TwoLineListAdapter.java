package com.spinthechoice.garbage.android;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class TwoLineListAdapter extends RecyclerView.Adapter<TwoLineListAdapter.TextViewHolder> {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class TextViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final TextView line1;
        private final TextView line2;

        TextViewHolder(final LinearLayout layout, final TextView line1, final TextView line2) {
            super(layout);
            this.line1 = line1;
            this.line2 = line2;
        }
    }

    private final List<String[]> dataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    TwoLineListAdapter(final List<String[]> dataset) {
        this.dataset = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TextViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.two_line_text, parent, false);
        TextView line1 = v.findViewById(R.id.text_line_1);
        TextView line2 = v.findViewById(R.id.text_line_2);
        return new TextViewHolder(v, line1, line2);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final TextViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String[] item = dataset.get(position);
        holder.line1.setText(item[0]);
        holder.line2.setText(item[1]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
