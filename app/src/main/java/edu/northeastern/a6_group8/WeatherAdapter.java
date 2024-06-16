package edu.northeastern.a6_group8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private List<WeatherModel> weatherDataList;
    Context context;
    ExecutorService executorService = Executors.newFixedThreadPool(4);

    WeatherAdapter(Context context, ArrayList<WeatherModel> weatherDataList){
        this.context = context;
        this.weatherDataList = weatherDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.temperature.setText(String.valueOf(weatherDataList.get(position).temperature));
        holder.city.setText(weatherDataList.get(position).city);
        holder.condition.setText(weatherDataList.get(position).condition);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = downloadImage(weatherDataList.get(position).getIcon());
                holder.weatherIcon.post(() -> {
                    if (bitmap != null) {
                        holder.weatherIcon.setImageBitmap(bitmap);
                    } else {
                        holder.weatherIcon.setImageResource(R.drawable.baseline_image_not_supported_24);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return weatherDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView weatherIcon;
        public TextView condition, temperature, city;

        public ViewHolder(View itemView) {
            super(itemView);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            condition = itemView.findViewById(R.id.txtCondition);
            city = itemView.findViewById(R.id.txtCity);
            temperature = itemView.findViewById(R.id.txtTemperature);
        }
    }

    public Bitmap downloadImage(String urlStr) {
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

}
