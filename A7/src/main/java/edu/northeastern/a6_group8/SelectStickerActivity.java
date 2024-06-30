package edu.northeastern.a6_group8;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SelectStickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sticker);

        ImageView stickerSun = findViewById(R.id.sticker_sun);
        ImageView stickerStar = findViewById(R.id.sticker_star);
        // Add more stickers if required...

        stickerSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnSelectedSticker("sun_sticker");
            }
        });

        stickerStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnSelectedSticker("star_sticker");
            }
        });
    }

    private void returnSelectedSticker(String sticker) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedSticker", sticker);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
