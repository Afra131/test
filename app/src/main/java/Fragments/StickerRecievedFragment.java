package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapter.StickerReceivedAdapter;
import Model.StickerReceived;
import edu.northeastern.a6_group8.R;
import edu.northeastern.a6_group8.User;

public class StickerRecievedFragment extends Fragment {
    private RecyclerView recyclerView;
    private StickerReceivedAdapter adapter;
    private ArrayList<StickerReceived> arrStickerReceived = new ArrayList<>();
    private DatabaseReference stickerReference;
    String username;
    String receiverId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sticker_recieved, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            username = bundle.getString("username");
        }
        adapter = new StickerReceivedAdapter(arrStickerReceived, getContext());
        recyclerView = view.findViewById(R.id.StickerReceivedList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getReceiverId(username);
        return view;
    }

    private void getReceiverId(String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        receiverId = user.getId();
                        getStickerReceived();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error: " + databaseError.getMessage());
            }
        });
    }

    private void getStickerReceived() {
        if (receiverId == null) {
            Log.e("StickerReceivedFragment", "Receiver ID is null. Cannot load stickers.");
            return;
        }

        stickerReference = FirebaseDatabase.getInstance().getReference("StickerHistory");
        stickerReference.orderByChild("receiver").equalTo(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrStickerReceived.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StickerReceived stickerReceived = snapshot.getValue(StickerReceived.class);
                    if (stickerReceived != null) {
                        arrStickerReceived.add(stickerReceived);
                    } else {
                        Log.e("StickerReceivedFragment", "Received null StickerReceived data for snapshot: " + snapshot.getKey());
                    }
                }
                adapter = new StickerReceivedAdapter(arrStickerReceived, getContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("StickerReceivedFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }
}