package edu.northeastern.a6_group8.Adapter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import edu.northeastern.a6_group8.MainActivity;
import edu.northeastern.a6_group8.MessageActivity;
import edu.northeastern.a6_group8.Model.Sticker;
import edu.northeastern.a6_group8.R;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    private ArrayList<Sticker> stickerList;
    private Context context;
    private String senderId;
    private String receiverId;
    private String userid;
    public StickerAdapter(ArrayList<Sticker> stickerList, Context context, String senderId, String receiverId, String userid) {
        this.stickerList = stickerList;
        this.context = context;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.userid = userid;
    }

    @NonNull
    @Override
    public StickerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sticker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerAdapter.ViewHolder holder, int position) {
        Sticker sticker = stickerList.get(position);
        Glide.with(context).load(sticker.getStickerUrl()).into(holder.stickerImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSticker(senderId, receiverId, sticker);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stickerList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView stickerImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerImageView = itemView.findViewById(R.id.stickerItem);
        }
    }

    private void sendSticker(String sender, String receiver, Sticker sticker){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("stickerId", sticker.getStickerId());
        hashMap.put("stickerName", sticker.getStickerName());
        hashMap.put("stickerUrl", sticker.getStickerUrl());
        hashMap.put("timestamp", System.currentTimeMillis());
        reference.child("StickerHistory").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("sendSticker", "Sticker sent successfully");
                    if (receiver.equals(userid)) {
                        sendStickerNotification(context, "New Sticker Received", "You have received a " + sticker.getStickerName() + " sticker", sticker.getStickerUrl());
                    }
                } else {
                    Log.e("sendSticker", "Failed to send sticker", task.getException());
                }
            }
        });
    }

    private void sendStickerNotification(Context context, String title, String message, String imageUrl) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap largeIcon = null;
        try {
            largeIcon = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(largeIcon)
                        .bigLargeIcon((Bitmap) null));

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}


