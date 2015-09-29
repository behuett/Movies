package com.bigshovellabs.movies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bigshovellabs.movies.ValueObjects.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by bh on 8/21/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public ArrayList<Movie> movieList;

    public ImageAdapter(Context c) {
        mContext = c;
    }
    public ImageAdapter(Context context, ArrayList<Movie> objects) {
        this.mContext = context;
        this.movieList = objects;
    }


    public int getCount() {
        if (movieList != null) {
            return movieList.size();
        }
        else
            return 0;
    }

    public Object getItem(int position) {
       return movieList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        View movieSummaryView = convertView;
        if (movieSummaryView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        if (movieList != null) {
            Picasso.with(mContext).load(Constants.MOVIE_POSTER_BASE +
                    movieList.get(position).getPosterPath()).into(imageView);
        }

        return imageView;
    }

}