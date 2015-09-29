package com.bigshovellabs.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bigshovellabs.movies.ValueObjects.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created on 8/27/15.
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // The detail Activity called via intent.
        Intent intent = getActivity().getIntent();
        Movie selectedMovie = intent.getParcelableExtra(Constants.SELECTED_MOVIE);
        if (intent != null && selectedMovie != null){

            View view = rootView.findViewById(R.id.detail_poster);
            ((TextView)rootView.findViewById(R.id.detail_movie_title)).
                    setText(selectedMovie.getOriginalTitle());
            ((TextView)rootView.findViewById(R.id.detail_release_date)).
                    setText(selectedMovie.getReleaseDate());
            float voteStars = formatMovieRating(selectedMovie.getVoteAverage());
            ((TextView)rootView.findViewById(R.id.detail_vote_avg)).
                    setText(voteStars + "/" + getString(R.string.rating_max));
            ((RatingBar)rootView.findViewById(R.id.detail_rating_bar)).setRating(voteStars);
            ((TextView)rootView.findViewById(R.id.detail_plot)).
                    setText(selectedMovie.getPlotSynopsis());

            Picasso.with(getActivity().getApplicationContext()).
                    load("http://image.tmdb.org/t/p/w185/" + selectedMovie.getPosterPath()).
                    into((ImageView) view);
        }

        return rootView;
    }

    private float formatMovieRating(String inRatingAvg){
        //formatting into 5 stars so need to cut (10 star based) rating in half
        float ratingInt = 0;

        ratingInt = Float.valueOf(inRatingAvg);
        if(ratingInt > 0){
            ratingInt = ratingInt / 2;
        }

        return ratingInt;
    }
}