package com.example.kalio.adapterview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kalio.adapterview.models.Entry;

import java.util.ArrayList;


public class RssAdapter extends ArrayAdapter<Entry> {

    Context mContext;
    int mResouruce;
    ArrayList<Entry> mEntries;
    LayoutInflater mLayoutInflater;

    public RssAdapter(Context context, int resource, ArrayList<Entry> entries) {
        super(context, resource);
        mContext = context;
        mResouruce = resource;
        mEntries = entries;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        // 캐시된 뷰가 없을 경우 새로 생성하고 뷰홀더를 생성한다.
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(mResouruce, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.title = (TextView) convertView.findViewById(R.id.feed_list_item_title);
            viewHolder.author = (TextView) convertView.findViewById(R.id.feed_list_item_author);
            viewHolder.publishedDate = (TextView) convertView.findViewById(R.id.feed_list_item_published_date);
            viewHolder.contentSnippet = (TextView) convertView.findViewById(R.id.feed_list_item_content_snippet);
            viewHolder.categories = (TextView) convertView.findViewById(R.id.feed_list_item_categories);

            convertView.setTag(viewHolder);
        } else {    // 캐시된 뷰가 있을 경우 저장된 뷰홀더를 사용한다.
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Entry entry = mEntries.get(position);

        viewHolder.title.setText(entry.title);
        viewHolder.author.setText(entry.author);
        viewHolder.publishedDate.setText(entry.publishedDate);
        viewHolder.contentSnippet.setText(entry.contentSnippet);
        viewHolder.categories.setText(entry.categories.get(0));

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView author;
        TextView publishedDate;
        TextView contentSnippet;
        TextView categories;
    }
}
