package com.example.jsonproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ShibeAdapter.OnShibeClicked {
    private static final String TAG = "JSON_Shibe";
    public static final String PHOTO_KEY = "com.example.jsonproject.PHOTO";

    private RecyclerView recyclerView;
    private ShibeAdapter shibeAdapter;
    boolean layoutSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        new ShibeTask().execute("100");

        final Button button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateDisplay();
            }
        });
    }

    void updateDisplay() {

        if (layoutSwitch) {
            recyclerView = findViewById(R.id.rv_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            layoutSwitch = false;
        } else {
            recyclerView = findViewById(R.id.rv_list);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            recyclerView.setHasFixedSize(true);
            layoutSwitch = true;
        }
       new ShibeTask().execute("100");
    }

    @Override
    public void shibeClicked(String url) {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra(PHOTO_KEY, url);
        startActivity(intent);
    }

//        Toast.makeText(this,url,Toast.LENGTH_SHORT).show();

    class ShibeTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {

            HttpURLConnection httpURLConnection = null;

            // Declare and Init variables
            String baseUrl = "http://shibe.online/api/shibes";
            String query = "?count=" + strings[0];

            // This will hold all the json
            StringBuilder result = new StringBuilder();

            // Create a URL Object, passing the url string into the constructor

            try {
                URL url = new URL(baseUrl + query);

                // Use the url object instance to create a internet connection
                httpURLConnection = (HttpURLConnection) url.openConnection();

                // Create a InputStream instance and initialize it with a BufferedInputStream
                // then pass the stream from the httpURLConnection instance
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                // Declare InputStreamReader and Init with our inputStream
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                // Declare BufferedReader object & init it with the inputStream
                BufferedReader reader = new BufferedReader(inputStreamReader);

                // Variable to hold each line from the reader
                String line;

                // Read each line from the BufferedReader object and append it into our result(StringBuilder)
                while ((line = reader.readLine()) != null) {
                    // If line is not null append to result
                    result.append(line);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Important to close the connection when done
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
            Log.d(TAG, "doInTheBackground : " + result);

            // Convert String (json) into List<String>

            String removeBrackets = result.substring(1, result.length() - 1);

            String removeQuotes = removeBrackets.replace("\"", "");

            String[] urls = removeQuotes.split(",");

            return Arrays.asList(urls);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            shibeAdapter = new ShibeAdapter(strings, MainActivity.this);
            recyclerView.setAdapter(shibeAdapter);
        }
    }
}
