package edu.northeastern.a6_group8.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.northeastern.a6_group8.Model.StickerReceived;
import edu.northeastern.a6_group8.R;

public class StickerReceivedAdapter extends RecyclerView.Adapter<StickerReceivedAdapter.ViewHolder> {
    private ArrayList<StickerReceived> stickerReceivedList = new ArrayList<>();
    private Context context;

    public StickerReceivedAdapter(ArrayList<StickerReceived> stickerReceivedList, Context context) {
        this.stickerReceivedList = stickerReceivedList;
        this.context = context;
    }

    @NonNull
    @Override
    public StickerReceivedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sticker_received_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerReceivedAdapter.ViewHolder holder, int position) {
        StickerReceived stickerReceived = stickerReceivedList.get(position);
        if (stickerReceived != null) {
            holder.txtStickerName.setText(stickerReceived.getStickerName());
            holder.txtSender.setText(stickerReceived.getSender());
            holder.txtTimeStamp.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(stickerReceived.getTimestamp())));
            Glide.with(context).load(stickerReceived.getStickerUrl()).into(holder.stickerView);
        } else {
            Log.e("StickerReceivedAdapter", "Null data found at position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return stickerReceivedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView stickerView;
        public TextView txtStickerName;
        public TextView txtSender;
        public TextView txtTimeStamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerView = itemView.findViewById(R.id.stickerView);
            txtStickerName = itemView.findViewById(R.id.txtStickerName);
            txtSender = itemView.findViewById(R.id.txtSender);
            txtTimeStamp = itemView.findViewById(R.id.txtTimeStamp);
        }
    }
}
