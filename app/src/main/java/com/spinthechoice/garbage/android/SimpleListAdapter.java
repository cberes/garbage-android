package com.spinthechoice.garbage.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.TextViewHolder> {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class TextViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final TextView textView;

        TextViewHolder(final TextView v) {
            super(v);
            textView = v;
        }
    }

    private final List<String> dataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    SimpleListAdapter(final List<String> dataset) {
        this.dataset = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TextViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_view, parent, false);
        return new TextViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final TextViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(dataset.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
