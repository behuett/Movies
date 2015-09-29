package com.bigshovellabs.movies;

/**
 * Created by bh on 9/3/15.
 */

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.bigshovellabs.movies.ValueObjects.Movie;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * The Fragment for the main movie screen grid
 */
public class MovieSummaryFragment extends Fragment {
    private ImageAdapter imageAdapter;
    public ArrayList<Movie> movieList;
    private ProgressBar spinner;

    private final String LOG_TAG = MovieSummaryFragment.class.getSimpleName();

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        imageAdapter = new ImageAdapter(getActivity(), movieList);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //initialize the progress spinner
        spinner = (ProgressBar)rootView.findViewById(R.id.progressBarSpinner);
        // initialize the GridView
        GridView gridView = (GridView) rootView.findViewById(R.id.fragment_movie_list_grid);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Constants.SELECTED_MOVIE, (Movie) imageAdapter.getItem(position));
                startActivity(detailIntent);
            }
        });


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void updateMovies(){
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortPref = sharedPrefs.getString(
                getActivity().getString(R.string.pref_sort_key),
                getActivity().getString(R.string.pref_sort_popular));
        GetMoviesTask moviesTask = new GetMoviesTask(Constants.MOVIE_BASE_URL, sortPref);
        moviesTask.execute();
    }


   public class GetMoviesTask extends AsyncTask<String, Integer, ArrayList<Movie>> {
        private final String LOG_TAG = GetMoviesTask.class.getSimpleName();
        private String sortPref;
        private String url;

        public GetMoviesTask (String inURLString, String inSortPref){
            this.sortPref = inSortPref;
            this.url = inURLString;
        }


     @Override
     protected void onPreExecute() {
         spinner.setVisibility(View.VISIBLE);
     }

     @Override
     protected ArrayList<Movie> doInBackground(String... params) {

       HttpURLConnection urlConnection = null;
       BufferedReader reader = null;

       String movieJsonStr = null;
       MovieDataParser movieDataParser = new MovieDataParser();

       try {
           final String SORT_PARAM = "sort_by";
           final String API_KEY = "api_key";

           Uri builtUri = Uri.parse(url).buildUpon()
                   .appendQueryParameter(SORT_PARAM, sortPref + ".desc")
                   .appendQueryParameter(API_KEY, "97c2729fdc5ba9067795f616f5dffbe7")
                   .build();

           URL url = new URL(builtUri.toString());

           urlConnection = (HttpURLConnection) url.openConnection();
           urlConnection.setRequestMethod("GET");
           urlConnection.connect();

           // Read the input stream into a String
           InputStream inputStream = urlConnection.getInputStream();
           StringBuffer buffer = new StringBuffer();
           if (inputStream == null) {
               // Nothing to do.
               return null;
           }
           reader = new BufferedReader(new InputStreamReader(inputStream));

           String line;
           while ((line = reader.readLine()) != null) {

               buffer.append(line + "\n");
           }

           if (buffer.length() == 0) {
               // Stream was empty.  No point in parsing.
               return null;
           }
           movieJsonStr = buffer.toString();

       } catch (IOException e) {
           Log.e(LOG_TAG, "IO except, Error " + e.getMessage(), e);
           // If the code didn't successfully get the movie data, there's no point in attempting
           // to parse it.
           return null;
       } finally {
           if (urlConnection != null) {
               urlConnection.disconnect();
           }
           if (reader != null) {
               try {
                   reader.close();
               } catch (final IOException e) {
                   Log.e(LOG_TAG, "Error closing stream", e);
               }
           }
       }

       try{
           movieList = movieDataParser.getMovieDataFromJSON(movieJsonStr);

       } catch (JSONException e) {
           Log.e(LOG_TAG, "Exception parsing json " + e);
       }

       return movieList;
   }


     @Override
     protected void onPostExecute(ArrayList<Movie> movies) {
         spinner.setVisibility(View.GONE);
         imageAdapter.movieList = movies;
         imageAdapter.notifyDataSetChanged();
     }
 }
}