package Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import Model.Sticker;
import edu.northeastern.a6_group8.R;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    private ArrayList<Sticker> stickerList;
    private Context context;

    public StickerAdapter(ArrayList<Sticker> stickerList, Context context) {
        this.stickerList = stickerList;
        this.context = context;
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

        reference.child("StickerHistory").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("sendSticker", "Sticker sent successfully");
                } else {
                    Log.e("sendSticker", "Failed to send sticker", task.getException());
                }
            }
        });
    }
}


