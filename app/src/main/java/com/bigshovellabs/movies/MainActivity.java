package com.bigshovellabs.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.bigshovellabs.movies.ValueObjects.Movie;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
///implements TaskFragment.TaskCallbacks
{
    public ArrayList<Movie> movieList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FIXME - move to fragment?  or move to ImageAdapter??
        updateMovies();

        GridView gridview = (GridView) findViewById(R.id.movie_home);
////   Log.v("Main", "size o movie list- "+ movieList.size());
/*        final ImageAdapter imageAdapter = new ImageAdapter(this, movieList);
 //       final ImageAdapter imageAdapter = new ImageAdapter(this);
        //FIXME  - is this the right way to share data?
 //       this.getApplicationContext().set
///        imageAdapter.setM      //set the movies into the adapter?
        gridview.setAdapter(imageAdapter);
*/
  //      gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //               Toast.makeText(MainActivity.this, "" + position,
                //                      Toast.LENGTH_SHORT).show();
//FIXME - dont want to pass in whole context, narrow down
                Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class)
                        .putExtra("selectedMovie", movieList.get(position));
 //               Movie deleteThis = movieList.get(position)
               //         .putExtra("detail_movie_obj", movieList.get(position));
               //         .putExtra(Intent.EXTRA_TEXT, String.valueOf(position));
            ////    .putExtra("image_adapter", imageAdapter); //FIXME - pass in movie object instead of whole adapter
                startActivity(detailIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void updateMovies(){
        GetMoviesTask moviesTask = new GetMoviesTask();
        //  weatherTask.execute("94043");
        //  weatherTask.execute("80023");
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String location = prefs.getString(getString(R.string.pref_location_key),
//                getString(R.string.pref_location_default));
        moviesTask.execute("prefString");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class GetMoviesTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = GetMoviesTask.class.getSimpleName();

       /* public ArrayList<Movie> getMovieList(){
            return movieList; //FIXME - clearly not a good place for this
        }*/

        @Override
        protected String doInBackground(String... params) {
   Log.v(LOG_TAG, "In get movies task-----------");
            // Escape early if cancel() is called
       ///FIXME     if (isCancelled()) break;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            MovieDataParser movieDataParser = new MovieDataParser();
           // ArrayList <Movie> parsedMovies = null;

            try {
 //FIXME was getDefaultSharedPreferences(getActivity()); changed to context, should narrow focus
                SharedPreferences sharedPrefs =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String sortPref = sharedPrefs.getString(
                        getString(R.string.pref_sort_key),
                        getString(R.string.pref_sort_popular));

                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortPref + ".desc")
                        .appendQueryParameter(API_KEY, "97c2729fdc5ba9067795f616f5dffbe7")
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "*** MOVIE string: " + movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error " + e.getMessage(), e);
                // If the code didn't successfully get the weather data, there's no point in attempting
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
         /*   String[] resultStrs = new String[1];
            resultStrs[0] = movieJsonStr;
            return resultStrs;
            */
            try{
  Log.v(LOG_TAG, "going to parse the json string " + movieJsonStr);
                movieList = movieDataParser.getMovieDataFromJSON(movieJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

         ///   return parsedMovies;
            return "true"; //FIXME - may as well return void here or a boolean value?

        }

        protected void onProgressUpdate(Integer... progress) {
  //          setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
///            showDialog("Downloaded " + result + " bytes");
            GridView gridview = (GridView) findViewById(R.id.movie_home);
////   Log.v("Main", "size o movie list- "+ movieList.size());
            if(movieList != null)Log.v(LOG_TAG, "Size of movie list - " + movieList.size());
            final ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), movieList);
            //       final ImageAdapter imageAdapter = new ImageAdapter(this);
            //FIXME  - is this the right way to share data?
            //       this.getApplicationContext().set
///        imageAdapter.setM      //set the movies into the adapter?
            gridview.setAdapter(imageAdapter);

        }
    }

}
