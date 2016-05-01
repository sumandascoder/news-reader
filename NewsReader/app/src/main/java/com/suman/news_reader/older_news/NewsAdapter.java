package com.suman.news_reader.older_news;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suman.news_reader.R;

/**
 * The Adapter class to have images and text displayed in Viacom Format
 * @author sumansucharitdas
 *
 */
public class NewsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private static Context mainContext;

    public NewsAdapter(Context applicationContext, List<String> thumbnailURLs) {
        mainContext = applicationContext;
        inflater = (LayoutInflater) mainContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getCount() {
        return OlderNewsFileNamesPOJO.fileNames.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Using View Holder Pattern to have the content loaded dynamically
        NewsViewHolder holder = new NewsViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_item, null);
            holder.header = (TextView) convertView.findViewById(R.id.headerText);
            holder.details = (TextView) convertView.findViewById(R.id.detailsText);
            holder.img = (ImageView) convertView.findViewById(R.id.listImage);
            holder.checkBoxDelete = (CheckBox) convertView.findViewById(R.id.select);
            holder.checkBoxDelete.setVisibility(View.INVISIBLE);
            convertView.setTag(holder);
        } else {
            holder = (NewsViewHolder) convertView.getTag();
        }
        // Add the text to be displayed along with the Thumbnails
        holder.header.setText(OlderNewsFileNamesPOJO.dateOfCreation.get(position));
        holder.details.setText(OlderNewsFileNamesPOJO.fileNames.get(position));

        //holder.img.setImageBitmap(VineMyJSONFormatter.thumbnails.get(position));

        holder.checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
            }
        });

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (OlderNewsFileNamesPOJO.fileNames.size() != 0) {
            return OlderNewsFileNamesPOJO.fileNames.size();
        } else {
            return 1;
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

}