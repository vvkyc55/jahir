package com.example.kalio.adapterview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kalio.adapterview.models.Post;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class TemplateAdapter extends BaseAdapter {

    Context mContext;
    List<Post> mPosts;
    LayoutInflater mLayoutInflater;

    public TemplateAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts = posts;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return mPosts.get(position);
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
            convertView = mLayoutInflater.inflate(R.layout.main_list_item, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.post_thumb);
            viewHolder.author = (TextView) convertView.findViewById(R.id.post_author);
            viewHolder.title = (TextView) convertView.findViewById(R.id.post_title);
            viewHolder.date = (TextView) convertView.findViewById(R.id.post_date);

            convertView.setTag(viewHolder);
        } else {    // 캐시된 뷰가 있을 경우 저장된 뷰홀더를 사용한다.
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Post post = mPosts.get(position);

        if (post.thumbnail !=  null) {
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    Bitmap bitmap = null;
                    try {
                        URL url = new URL(params[0]);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        //options.inSampleSize = 10;
                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    viewHolder.thumbnail.setImageBitmap(bitmap);
                }
            }.execute(post.thumbnail);
        }

        viewHolder.author.setText(post.author);
        viewHolder.title.setText(post.title);
        viewHolder.date.setText(post.date);

        return convertView;
    }

    static class ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView author;
        TextView date;
    }
}
