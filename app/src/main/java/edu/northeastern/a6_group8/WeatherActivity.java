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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
    private Button btnToggle;
    private ConstraintLayout clWeatherInfo;
    private boolean showDetailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // UI Initialization
        cityEdit = findViewById(R.id.editCity);
        etLatitude = findViewById(R.id.latitude_input);
        etLongitude = findViewById(R.id.longitude_input);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        btnToggle = findViewById(R.id.toggle_button);
        clWeatherInfo = findViewById(R.id.weather_info_layout);
        recyclerView = findViewById(R.id.recyclerViewWeather);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeatherAdapter(this, arrWeather);
        recyclerView.setAdapter(adapter);
        handler = new Handler(Looper.getMainLooper());

        // Fetch weather by city name
        btnGetWeather.setOnClickListener(v -> {
            String city = cityEdit.getText().toString();
            if (!city.isEmpty()) {
                fetchData(city);
                cityEdit.setText("");
            } else {
                Toast.makeText(WeatherActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch weather by latitude and longitude
        findViewById(R.id.fetch_weather_button).setOnClickListener(v -> {
            String latitude = etLatitude.getText().toString();
            String longitude = etLongitude.getText().toString();
            if (!latitude.isEmpty() && !longitude.isEmpty()) {
                fetchWeatherData(latitude, longitude);
            } else {
                Toast.makeText(WeatherActivity.this, "Please enter latitude and longitude", Toast.LENGTH_SHORT).show();
            }
        });

        // Toggle detailed view
        btnToggle.setOnClickListener(v -> {
            showDetailed = !showDetailed;
            btnToggle.setText(showDetailed ? "Show Simple" : "Show Detailed");
        });

        if (savedInstanceState != null) {
            arrWeather = (ArrayList<WeatherModel>) savedInstanceState.getSerializable("link_list");
        } else {
            arrWeather = new ArrayList<>();
        }

        initRecyclerView();
    }

    private void fetchData(String city) {
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

    private void fetchWeatherData(String latitude, String longitude) {
        new Thread(() -> {
            String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,precipitation,humidity_2m,windspeed_10m,sunrise,sunset";
            try {
                String response = makeHttpRequest(url);
                if (response != null) {
                    processWeatherData(response);
                } else {
                    handler.post(() -> Toast.makeText(WeatherActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(WeatherActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String makeHttpRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        BufferedReader reader;
        StringBuilder response = new StringBuilder();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } else {
            reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        reader.close();
        urlConnection.disconnect();
        return response.toString();
    }

    private void processWeatherData(final String response) {
        handler.post(() -> {
            clWeatherInfo.removeAllViews();
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray hourly = jsonObject.getJSONArray("hourly");
                for (int i = 0; i < hourly.length(); i++) {
                    JSONObject hourData = hourly.getJSONObject(i);
                    StringBuilder weatherInfo = new StringBuilder();
                    weatherInfo.append("Temperature: ").append(hourData.getDouble("temperature_2m")).append("°C\n");
                    weatherInfo.append("Precipitation: ").append(hourData.getDouble("precipitation")).append("mm");

                    if (showDetailed) {
                        weatherInfo.append("\nHumidity: ").append(hourData.getDouble("humidity_2m")).append("%");
                        weatherInfo.append("\nWind Speed: ").append(hourData.getDouble("windspeed_10m")).append(" km/h");
                        weatherInfo.append("\nSunrise: ").append(hourData.getString("sunrise"));
                        weatherInfo.append("\nSunset: ").append(hourData.getString("sunset"));
                    }

                    ConstraintLayout weatherItemLayout = new ConstraintLayout(WeatherActivity.this);
                    ConstraintSet constraintSet = getConstraintSetForWeatherItem(weatherInfo.toString());
                    constraintSet.applyTo(weatherItemLayout);
                    clWeatherInfo.addView(weatherItemLayout);
                }
            } catch (Exception e) {
                e.printStackTrace();
                TextView errorText = new TextView(WeatherActivity.this);
                errorText.setText("Failed to retrieve weather data.");
                clWeatherInfo.addView(errorText);
            }
        });
    }

    private ConstraintSet getConstraintSetForWeatherItem(String weatherInfo) {
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        TextView textView = new TextView(this);
        textView.setText(weatherInfo);
        textView.setId(View.generateViewId());
        textView.setLayoutParams(layoutParams);

        constraintSet.clone(clWeatherInfo);
        constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(textView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.constrainHeight(textView.getId(), ConstraintSet.WRAP_CONTENT);

        return constraintSet;
    }
}
