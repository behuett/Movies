package com.bigshovellabs.movies;

import android.util.Log;

import com.bigshovellabs.movies.ValueObjects.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created on 8/28/15.
 */
public class MovieDataParser {

    public ArrayList<Movie> getMovieDataFromJSON(String inJSONString)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String ORIG_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVG = "vote_average";
        final String OVERVIEW = "overview";

        JSONObject movieJson = new JSONObject(inJSONString);
        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        ArrayList<Movie> movieList = new ArrayList<Movie>();
        if (movieArray != null) {
            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject movieObject = movieArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setOriginalTitle(movieObject.getString(ORIG_TITLE));
                movie.setPosterPath(movieObject.getString(POSTER_PATH));
                movie.setReleaseDate(movieObject.getString(RELEASE_DATE));
                movie.setVoteAverage(movieObject.getString(VOTE_AVG));
                movie.setPlotSynopsis(movieObject.getString(OVERVIEW));

                movieList.add(movie);
            }
        }

        return movieList;
    }
}
