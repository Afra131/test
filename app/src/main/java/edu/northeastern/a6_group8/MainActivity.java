package edu.northeastern.a6_group8;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "Sticker Received Notification";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Sticker Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Notify Sticker Received");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void clickGetWeather(View v) {
//        startActivity(new Intent(MainActivity.this, FutureActivity.class));
    }

    public void checkWeatherActivity(View view){
        startActivity(new Intent(MainActivity.this, WeatherActivity.class));
    }

    public void startSendStickers(View view){
        startActivity(new Intent(MainActivity.this, StartStickerActivity.class));
    }

    public void openAbout(View view){
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }
}