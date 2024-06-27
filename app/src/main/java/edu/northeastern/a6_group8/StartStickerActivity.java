package edu.northeastern.a6_group8;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartStickerActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    Button login, register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_sticker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // check if user is null
        /**
        *if (firebaseUser != null){
        *    Intent intent = new Intent(StartStickerActivity.this, StickerActivity.class);
        *    startActivity(intent);
        *    finish();
        *}
         * */
        login = findViewById(R.id.btnStartlogin);
        register = findViewById(R.id.btnStartRegister);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartStickerActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartStickerActivity.this, RegisterActivity.class));
            }
        });
    }
}