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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import edu.northeastern.a6_group8.Adapter.ChatAdapter;
import edu.northeastern.a6_group8.Adapter.MessageAdapter;
import edu.northeastern.a6_group8.Adapter.StickerAdapter;
import edu.northeastern.a6_group8.Model.Chat;
import edu.northeastern.a6_group8.Model.Message;
import edu.northeastern.a6_group8.Model.Sticker;

public class MessageActivity extends AppCompatActivity {

    private static final int TYPE_CHAT_LEFT = 0;
    private static final int TYPE_CHAT_RIGHT = 1;
    private static final int TYPE_STICKER_LEFT = 2;
    private static final int TYPE_STICKER_RIGHT = 3;
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
    ChatAdapter chatAdapter;
    ArrayList<Chat> arrChats = new ArrayList<>();
    ArrayList<Message> messageItems = new ArrayList<>();
    RecyclerView chatList;
    StickerAdapter stickerAdapter;
    MessageAdapter messageAdapter;
    String userid;
    String currentName;
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

        stickerList = findViewById(R.id.stickerList);
        stickerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        stickerList.setAdapter(stickerAdapter);
        Log.d("MessageActivity", "Adapter set to RecyclerView");

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        currentName = intent.getStringExtra("username");
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
                loadMessages(userid, senderId);
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
        hashMap.put("timestamp", System.currentTimeMillis());

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
                stickerAdapter = new StickerAdapter(arrStickers, MessageActivity.this, senderId, userid, senderId);
                stickerList.setAdapter(stickerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("loadStickers", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void loadMessages(String senderId, String receiverId) {
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chats");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null &&
                            (chat.getReceiver().equals(receiverId) && chat.getSender().equals(senderId) ||
                                    chat.getReceiver().equals(senderId) && chat.getSender().equals(receiverId))) {
                        int viewType = chat.getSender().equals(userid) ? TYPE_CHAT_LEFT : TYPE_CHAT_RIGHT;
                        messageItems.add(new Message(chat, viewType));
                    }
                }
                readStickers(senderId, receiverId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("loadMessages", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void readStickers(String senderId, String receiverId) {
        DatabaseReference stickerReference = FirebaseDatabase.getInstance().getReference("StickerHistory");
        stickerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> stickerData = (HashMap<String, Object>) snapshot.getValue();
                    if (stickerData != null) {
                        String stickerId = (String) stickerData.get("stickerId");
                        String stickerName = (String) stickerData.get("stickerName");
                        String stickerUrl = (String) stickerData.get("stickerUrl");
                        long timestamp = (Long) stickerData.get("timestamp");
                        Sticker sticker = new Sticker(stickerId, stickerName, stickerUrl, timestamp);
                        String sender = (String) stickerData.get("sender");
                        String receiver = (String) stickerData.get("receiver");

                        int viewType = sender.equals(senderId) ? TYPE_STICKER_LEFT : TYPE_STICKER_RIGHT;
                        if ((sender.equals(senderId) && receiver.equals(receiverId)) ||
                                (sender.equals(receiverId) && receiver.equals(senderId))) {
                            messageItems.add(new Message(sticker, viewType));
                        }
                    }
                }
                Collections.sort(messageItems, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        long time1 = o1.isChat() ? o1.getChat().getTimestamp() : o1.getSticker().getTimestamp();
                        long time2 = o2.isChat() ? o2.getChat().getTimestamp() : o2.getSticker().getTimestamp();
                        return Long.compare(time1, time2);
                    }
                });
                messageAdapter = new MessageAdapter(MessageActivity.this, messageItems, senderId);
                chatList.setAdapter(messageAdapter);
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
                chatAdapter = new ChatAdapter(MessageActivity.this, arrChats, senderId);
                chatList.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("loadMessages", "Database error: " + databaseError.getMessage());
            }
        });
    }

}