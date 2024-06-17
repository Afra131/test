package edu.northeastern.a6_group8;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;

import java.net.URLEncoder;
import java.util.ArrayList;

public class WeatherActivity extends AppCompatActivity {
    ArrayList<WeatherModel> arrWeather = new ArrayList<>();
    WeatherAdapter adapter;
    Handler handler;
    RecyclerView recyclerView;
    private EditText cityEdit;
    private EditText etLatitude;
    private EditText etLongitude;
    private Button btnGetWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // UI Initialization
        cityEdit = findViewById(R.id.editCity);
        etLatitude = findViewById(R.id.latitude_input);
        etLongitude = findViewById(R.id.longitude_input);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        recyclerView = findViewById(R.id.recyclerViewWeather);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherAdapter(this, arrWeather);
        recyclerView.setAdapter(adapter);
        handler = new Handler(Looper.getMainLooper());

        btnGetWeather.setOnClickListener(v -> {
            String city = cityEdit.getText().toString();
            String latitude = etLatitude.getText().toString();
            String longitude = etLongitude.getText().toString();
            if (!city.isEmpty() || (!latitude.isEmpty() && !longitude.isEmpty())) {
                fetchData(city, latitude, longitude);
            } else {
                Toast.makeText(WeatherActivity.this, "Please enter a city name or both latitude and longitude", Toast.LENGTH_SHORT).show();
            }
            cityEdit.setText("");
            etLatitude.setText("");
            etLongitude.setText("");
        });

        if (savedInstanceState != null) {
            arrWeather = (ArrayList<WeatherModel>) savedInstanceState.getSerializable("link_list");
        } else {
            arrWeather = new ArrayList<>();
        }

        initRecyclerView();
    }

    private void fetchData(String city, String latitude, String longitude) {
        Thread thread = new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                String input = latitude + "," + longitude;
                if (!city.isEmpty()) {
                    input = URLEncoder.encode(city, "UTF-8");
                }
                URL url = new URL("https://api.weatherapi.com/v1/current.json?key=2f6e441cae8b4e38a5b143422241606&q=" + input + "&aqi=no");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                Log.d("WeatherApp", "HTTP Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                    String response = convertStreamToString(inputStream);
                    Log.d("WeatherApp", "Response: " + response);
                    parseWeatherData(response);
                } else {
                    Log.d("WeatherApp", "Error response code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
        thread.start();
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    private void parseWeatherData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject location = jsonObject.getJSONObject("location");
            JSONObject current = jsonObject.getJSONObject("current");
            JSONObject condition = current.getJSONObject("condition");

            String cityName = location.getString("name");
            double tempC = current.getDouble("temp_c");
            String weatherCondition = condition.getString("text");
            String iconUrl = "https:" + condition.getString("icon");
            WeatherModel weatherData = new WeatherModel(cityName, tempC, weatherCondition, iconUrl);

            handler.post(() -> {
                arrWeather.add(weatherData);
                adapter.notifyDataSetChanged();
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("link_list", (Serializable) arrWeather);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewWeather);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherAdapter(this, arrWeather);
        recyclerView.setAdapter(adapter);
    }
}
