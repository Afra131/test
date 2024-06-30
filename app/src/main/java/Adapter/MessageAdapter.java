package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import Model.Chat;
import Model.Message;
import Model.Sticker;
import edu.northeastern.a6_group8.R;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CHAT_LEFT = 0;
    private static final int TYPE_CHAT_RIGHT = 1;
    private static final int TYPE_STICKER_LEFT = 2;
    private static final int TYPE_STICKER_RIGHT = 3;


    private ArrayList<Message> messgaeList;
    private Context context;
    private String senderId;

    public MessageAdapter(Context context, ArrayList<Message> msgList, String senderId) {
        this.messgaeList = msgList;
        this.context = context;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CHAT_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false);
            return new ChatViewHolder(view);
        } else if (viewType == TYPE_CHAT_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);
            return new ChatViewHolder(view);
        } else if(viewType == TYPE_STICKER_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.sticker_left, parent, false);
            return new StickerViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sticker_right, parent, false);
            return new StickerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message messageItem = messgaeList.get(position);
        if (holder instanceof ChatViewHolder) {
            Chat chat = messageItem.getChat();
            ((ChatViewHolder) holder).bind(chat);
        } else {
            Sticker sticker = messageItem.getSticker();
            ((StickerViewHolder) holder).bind(sticker);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message messageItem = messgaeList.get(position);
        return messageItem.getViewType();
    }

    @Override
    public int getItemCount() {
        return messgaeList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView show_message;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.showMsg);
        }

        public void bind(Chat chat) {
            show_message.setText(chat.getMessage());
        }
    }

    public class StickerViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerImageView;

        public StickerViewHolder(@NonNull View itemView) {
            super(itemView);
            stickerImageView = itemView.findViewById(R.id.showSticker);
        }

        public void bind(Sticker sticker) {
            Glide.with(context).load(sticker.getStickerUrl()).into(stickerImageView);
        }
    }

}

