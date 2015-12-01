package com.seanlee.manups.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.seanlee.manups.R;
import com.seanlee.manups.models.RecordModel;

import java.util.List;

/**
 * Created by Sean Lee on 3/11/15.
 */
public class RecordAdapter extends BaseAdapter {

    private Context context;
    private List<RecordModel> recordList;

    public RecordAdapter(Context context, List<RecordModel> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.record_items, null);
            holder.dateTextView = (TextView) convertView.findViewById(R.id.date);
            holder.pushupTextView = (TextView) convertView.findViewById(R.id.pushups);
            holder.situpTextView = (TextView) convertView.findViewById(R.id.situps);
            holder.runningTextView = (TextView) convertView.findViewById(R.id.running);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RecordModel record = recordList.get(position);

        holder.dateTextView.setText(record.getDate());
        holder.pushupTextView.setText("" + record.getPushup());
        holder.situpTextView.setText("" + record.getSitup());
        holder.runningTextView.setText(String.format(context.getString(R.string.distance_unit), record.getRunning()));

        return convertView;
    }

    class ViewHolder {
        TextView dateTextView;
        TextView pushupTextView;
        TextView situpTextView;
        TextView runningTextView;
    }
}
