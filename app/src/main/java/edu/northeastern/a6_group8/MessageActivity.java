package edu.northeastern.a6_group8;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import Adapter.MessageAdapter;
import Adapter.StickerAdapter;
import Model.Chat;
import Model.Sticker;

public class MessageActivity extends AppCompatActivity {

    TextView usernameView;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Intent intent;
    ImageButton btnSend;
    ImageButton btnSticker;
    EditText editMsg;
    String senderId;
    ArrayList<Sticker> arrStickers = new ArrayList<>();;
    RecyclerView stickerList;
    MessageAdapter messageAdapter;
    ArrayList<Chat> arrChats = new ArrayList<>();
    RecyclerView chatList;
    StickerAdapter stickerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        usernameView = findViewById(R.id.messageUserName);
        btnSend = findViewById(R.id.btnSend);
        btnSticker = findViewById(R.id.btnSticker);
        editMsg = findViewById(R.id.editMsg);

        chatList = findViewById(R.id.msgRecyclerView);
        chatList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chatList.setLayoutManager(linearLayoutManager);

        stickerAdapter = new StickerAdapter(arrStickers, this);
        stickerList = findViewById(R.id.stickerList);
        stickerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        stickerList.setAdapter(stickerAdapter);
        Log.d("MessageActivity", "Adapter set to RecyclerView");

        intent = getIntent();
        String userid = intent.getStringExtra("userid");
        String currentName = intent.getStringExtra("username");
        if (currentName != null) {
            getSenderId(currentName);
        }
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert userid != null;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                usernameView.setText(user.getUsername());
                readMessages(userid, senderId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editMsg.getText().toString();
                if (!msg.isEmpty()){
                    sendMsg(senderId, userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                editMsg.setText("");
            }
        });

        btnSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stickerList.getVisibility() == View.GONE) {
                    stickerList.setVisibility(View.VISIBLE);
                    loadStickers();
                } else {
                    stickerList.setVisibility(View.GONE);
                }
            }
        });

    }

    private void sendMsg(String sender, String receiver, String msg){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", msg);

        reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("sendMsg", "Message sent successfully");
                } else {
                    Log.e("sendMsg", "Failed to send message", task.getException());
                }
            }
        });
    }

    private void getSenderId(String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        senderId = user.getId();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error: " + databaseError.getMessage());
            }
        });
    }

    private void loadStickers() {
        Log.d("loadStickers", "Loading stickers...");
        DatabaseReference stickerReference = FirebaseDatabase.getInstance().getReference("Stickers");
        stickerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrStickers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Sticker sticker = snapshot.getValue(Sticker.class);
                    if (sticker != null) {
                        Log.d("loadStickers", "Sticker loaded: " + sticker.getStickerName());
                        arrStickers.add(sticker);
                    }
                }
                stickerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("loadStickers", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void readMessages(String senderId, String receiverId) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrChats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(receiverId) && chat.getSender().equals(senderId) ||
                                    chat.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)) {
                        arrChats.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(MessageActivity.this, arrChats, senderId);
                chatList.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("loadMessages", "Database error: " + databaseError.getMessage());
            }
        });
    }
}