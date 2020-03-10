package com.cognizant.poc.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cognizant.poc.R;
import com.cognizant.poc.model.NewsArticle;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsListAdapter extends ArrayAdapter<NewsArticle>{

    private ArrayList<NewsArticle> articles;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView tvName;
        TextView tvDesCription;
        ImageView ivNews;
    }

    public NewsListAdapter(ArrayList<NewsArticle> data, Context context) {
        super(context, R.layout.news_item, data);
        this.articles = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NewsArticle dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.news_item, parent, false);
            viewHolder.tvName = convertView.findViewById(R.id.tvName);
            viewHolder.tvDesCription = convertView.findViewById(R.id.tvDesCription);
            viewHolder.ivNews = convertView.findViewById(R.id.ivNews);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.tvName.setText(dataModel.getTitle());
        viewHolder.tvDesCription.setText(dataModel.getDescription());
        if(dataModel.getUrlToImage().equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.no_news_icon).resize(250, 250)
                    .centerCrop().into(viewHolder.ivNews);
        } else {
            Log.d("imageURL : "," "+dataModel.getUrlToImage());
            Picasso.get().load(dataModel.getUrlToImage())
                    .error(R.drawable.no_news_icon)
                    .resize(250, 250)
                    .centerCrop()
                    .into(viewHolder.ivNews);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
