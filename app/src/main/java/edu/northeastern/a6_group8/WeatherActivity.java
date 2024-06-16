package edu.northeastern.a6_group8;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText cityEdit = findViewById(R.id.editCity);
        Button btnCheckWeather = findViewById(R.id.btnGetWeather);
        recyclerView = findViewById(R.id.recyclerViewWeather);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherAdapter(this, arrWeather);
        recyclerView.setAdapter(adapter);
        handler = new Handler(Looper.getMainLooper());

        btnCheckWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdit.getText().toString();
                if (!city.isEmpty()) {
                    fetchData(city);
                    cityEdit.setText("");
                } else {
                    Toast.makeText(WeatherActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (savedInstanceState != null) {
            arrWeather = (ArrayList<WeatherModel>) savedInstanceState.getSerializable("link_list");
        } else {
            arrWeather = new ArrayList<>();
        }

        initRecyclerView();
    }

    public void fetchData(String city) {
        Thread thread = new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL("https://api.weatherapi.com/v1/current.json?key=2f6e441cae8b4e38a5b143422241606&q=" + URLEncoder.encode(city, "UTF-8") + "&aqi=no");
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

            handler.post(new Runnable() {
                @Override
                public void run() {
                    arrWeather.add(weatherData);
                    adapter.notifyDataSetChanged();
                }
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