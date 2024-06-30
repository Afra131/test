package edu.northeastern.a6_group8;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DatabaseReference database;
    private EditText recipientEditText;
    private String selectedSticker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        recipientEditText = findViewById(R.id.recipientEditText);
        Button selectStickerButton = findViewById(R.id.selectStickerButton);
        Button sendStickerButton = findViewById(R.id.sendStickerButton);
        Button viewHistoryButton = findViewById(R.id.viewHistoryButton);

        // Set button click listeners
        selectStickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch SelectStickerActivity to select a sticker
                Intent intent = new Intent(MainActivity.this, SelectStickerActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        sendStickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipient = recipientEditText.getText().toString();
                if (recipient.isEmpty() || selectedSticker == null) {
                    Toast.makeText(MainActivity.this, "Please select a recipient and a sticker.", Toast.LENGTH_SHORT).show();
                } else {
                    sendSticker(recipient, selectedSticker);
                }
            }
        });

        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch StickerHistoryActivity to view received stickers
                Intent intent = new Intent(MainActivity.this, StickerHistoryActivity.class);
                startActivity(intent);
            }
        });

        // Example of fetching sent sticker counts
        fetchSentStickerCounts("currentUsername");
    }

    private void sendSticker(String recipient, String sticker) {
        String currentUser = "currentUsername"; // Replace with actual current user logic

        // Increment sent sticker count
        database.child("users").child(currentUser).child("sent").child(sticker)
                .setValue(ServerValue.increment(1));

        // Add to recipient's received stickers
        String stickerId = database.child("users").child(recipient).child("received").push().getKey();
        Map<String, Object> stickerData = new HashMap<>();
        stickerData.put("sticker", sticker);
        stickerData.put("from", currentUser);
        stickerData.put("timestamp", ServerValue.TIMESTAMP);

        database.child("users").child(recipient).child("received").child(stickerId)
                .setValue(stickerData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Sticker sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to send sticker.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchSentStickerCounts(String username) {
        DatabaseReference sentRef = database.child("users").child(username).child("sent");
        sentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Long> stickerCounts = (Map<String, Long>) snapshot.getValue();
                // Update UI with stickerCounts
                // For example, log the counts or update a TextView
                Log.d(TAG, "Sticker counts: " + stickerCounts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e(TAG, "Failed to fetch sticker counts", error.toException());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedSticker = data.getStringExtra("selectedSticker");
        }
    }
}


