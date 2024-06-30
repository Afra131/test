package edu.northeastern.a6_group8;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StickerHistoryActivity extends AppCompatActivity {

    private static final String TAG = "StickerHistoryActivity";
    private DatabaseReference database;
    private ListView historyListView;
    private ArrayAdapter<String> adapter;
    private List<String> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_history);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        historyListView = findViewById(R.id.historyListView);
        historyList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        historyListView.setAdapter(adapter);

        // Fetch received stickers history
        fetchReceivedStickers("currentUsername");
    }

    private void fetchReceivedStickers(String username) {
        DatabaseReference receivedRef = database.child("users").child(username).child("received");
        receivedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot stickerSnapshot : snapshot.getChildren()) {
                    String sticker = stickerSnapshot.child("sticker").getValue(String.class);
                    String from = stickerSnapshot.child("from").getValue(String.class);
                    String timestamp = stickerSnapshot.child("timestamp").getValue().toString();
                    String historyItem = "Sticker: " + sticker + "\nFrom: " + from + "\nAt: " + timestamp;
                    historyList.add(historyItem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e(TAG, "Failed to fetch received stickers", error.toException());
            }
        });
    }
}