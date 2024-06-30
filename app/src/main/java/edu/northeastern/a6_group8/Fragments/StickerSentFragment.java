package edu.northeastern.a6_group8.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import edu.northeastern.a6_group8.Model.Sticker;
import edu.northeastern.a6_group8.R;

public class StickerSentFragment extends Fragment {
    private DatabaseReference stickerReference;
    private String username;
    private TextView txtStickerSent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sticker_sent, container, false);
        txtStickerSent = view.findViewById(R.id.txtStickerSent);
        Bundle bundle = getArguments();
        if (bundle != null) {
            username = bundle.getString("username");
        }
        countStickerSent();
        return view;
    }

    private void countStickerSent() {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = usersReference.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userId = snapshot.getKey();
                        if (userId != null) {
                            getStickers(userId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("StickerCountFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void getStickers(String userId) {
        stickerReference = FirebaseDatabase.getInstance().getReference("StickerHistory");
        stickerReference.orderByChild("sender").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Integer> stickerCounts = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Sticker sticker = snapshot.getValue(Sticker.class);
                    if (sticker != null) {
                        String stickerName = sticker.getStickerName();
                        if (stickerCounts.containsKey(stickerName)) {
                            stickerCounts.put(stickerName, stickerCounts.get(stickerName) + 1);
                        } else {
                            stickerCounts.put(stickerName, 1);
                        }
                    }
                }

                showStickerSent(stickerCounts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("StickerCountFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void showStickerSent(HashMap<String, Integer> stickerCounts) {
        StringBuilder displayText = new StringBuilder();
        for (Map.Entry<String, Integer> entry : stickerCounts.entrySet()) {
            displayText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        txtStickerSent.setText(displayText.toString());
    }
}