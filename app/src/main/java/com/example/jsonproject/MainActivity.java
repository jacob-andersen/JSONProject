package com.example.jsonproject;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.jsonproject.retrofit.RetrofitClientInstance;
import com.example.jsonproject.retrofit.ShibeService;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements ShibeAdapter.OnShibeClicked {

    private static final String TAG = "JSON_Shibe";
    public static final String PHOTO_KEY = "com.example.jsonproject.PHOTO";

    private RecyclerView recyclerView;
    private ShibeAdapter shibeAdapter;
    public ShibeResultReceiver shibeReceiver;

    boolean layoutSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        shibeReceiver = new ShibeResultReceiver(new Handler());
        // This is where we specify what happens when data is received from the service

        shibeReceiver.setReceiver(new ShibeResultReceiver.Receiver() {

            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {
                    String[] resultValue = resultData.getStringArray("receiver");
                    Log.d(TAG,"about to leave MyShibeService");
                    loadRecyclerView(Arrays.asList(resultValue));
                }
            }
        });

        onStartService("100");


//       retrofitRequest(100);
//       volleyRequest(100);
//       new ShibeTask().execute("100");

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
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerView.setHasFixedSize(true);
            layoutSwitch = true;
        }
//        new ShibeTask().execute("100");
    }

    @Override
    public void shibeClicked(String url) {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra(PHOTO_KEY, url);
        startActivity(intent);
    }

    public void retrofitRequest(int count) {

        // 1: Declare ShibeService and Initialize it using RetrofitClientInstance
        ShibeService shibeService =
                RetrofitClientInstance
                        .getRetrofit()
                        .create(ShibeService.class);

        // 2: Declare ShibeService Return type and initialize it using the ShibeService
        Call<List<String>> shibeCall = shibeService.loadShibes(count);

        // 3: Use the shibeCall from Step 2 and call the .enqueue method
        shibeCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, retrofit2.Response<List<String>> response) {
                if (response.isSuccessful()) {
//                    Log.d(TAG,"onResponse: Success");
                    loadRecyclerView(response.body());
                } else {
                    Log.d(TAG, "onResponse: Failure");
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });
    }

    public void volleyRequest(int count) {
        String baseUrl = "http://shibe.online/api/shibes";
        String query = "?count=" + count;
        String url = baseUrl + query;

        // 2: Create RequestQueue Object instance and init it with Volley.newRequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        // 3 : Declare JsonArrayRequest or JsonObjectRequest
        //     Then init it with new JsonArrayRequest or JsonOBjectRequest
        JsonArrayRequest request = new JsonArrayRequest(
                url, // Param 1: The url string
                new Response.Listener<JSONArray>() { // Param 2: Success Listener
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            List<String> urls = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                urls.add(response.get(i).toString());
//                                Log.d(TAG, (i+1) +" "+response.get(i).toString());
                            }
                            loadRecyclerView(urls);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() { // Param 3: Error Listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.getLocalizedMessage());
                    }
                }
        );

        // 4: Pass the request object from Step 3 into the requestQueue object from Step 2
        requestQueue.add(request);

    }

    private void loadRecyclerView(List<String> strings) {
        shibeAdapter = new ShibeAdapter(strings, MainActivity.this);
        recyclerView.setAdapter(shibeAdapter);
    }

    //
//    class ShibeTask extends AsyncTask<String, Void, List<String>> {
//
//        @Override
//        protected List<String> doInBackground(String... strings) {
//
//            HttpURLConnection httpURLConnection = null;
//
//            // Declare and Init variables
//            String baseUrl = "http://shibe.online/api/shibes";
//            String query = "?count=" + strings[0];
//
//            // This will hold all the json
//            StringBuilder result = new StringBuilder();
//
//            // Create a URL Object, passing the url string into the constructor
//
//            try {
//                URL url = new URL(baseUrl + query);
//
//                // Use the url object instance to create a internet connection
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//
//                // Create a InputStream instance and initialize it with a BufferedInputStream
//                // then pass the stream from the httpURLConnection instance
//                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
//
//                // Declare InputStreamReader and Init with our inputStream
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//
//                // Declare BufferedReader object & init it with the inputStream
//                BufferedReader reader = new BufferedReader(inputStreamReader);
//
//                // Variable to hold each line from the reader
//                String line;
//
//                // Read each line from the BufferedReader object and append it into our result(StringBuilder)
//                while ((line = reader.readLine()) != null) {
//                    // If line is not null append to result
//                    result.append(line);
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                // Important to close the connection when done
//                if (httpURLConnection != null)
//                    httpURLConnection.disconnect();
//            }
//            Log.d(TAG, "doInTheBackground : " + result);
//
//            // Convert String (json) into List<String>
//
//            String removeBrackets = result.substring(1, result.length() - 1);
//
//            String removeQuotes = removeBrackets.replace("\"", "");
//
//            String[] urls = removeQuotes.split(",");
//
//            return Arrays.asList(urls);
//        }
//
//        @Override
//        protected void onPostExecute(List<String> strings) {
//            super.onPostExecute(strings);
//            loadRecyclerView(strings);
//        }
//    }
    public void onStartService(String amount) {
        Intent serviceIntent = new Intent(this, MyShibeService.class);
        serviceIntent.putExtra("amount", "100");
        serviceIntent.putExtra("receiver", shibeReceiver);
        startService(serviceIntent);
    }

    public static class MyShibeService extends IntentService {

        public MyShibeService() {
            super("shibeservice");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String amount = intent.getStringExtra("amount");
            ResultReceiver rec = intent.getParcelableExtra("receiver");
            Bundle bundle = new Bundle();

            HttpURLConnection httpURLConnection = null;

            // Declare and Init variables
            String baseUrl = "http://shibe.online/api/shibes";
            String query = "?count=" + amount;

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

            // Convert String (json) into List<String>

            String removeBrackets = result.substring(1, result.length() - 1);

            String removeQuotes = removeBrackets.replace("\"", "");

            String[] urls = removeQuotes.split(",");

            //List<String> dogList = Arrays.asList(urls);

            bundle.putStringArray("receiver", urls);
            rec.send(Activity.RESULT_OK, bundle);
        }

    }


}