package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Model.Chat;
import edu.northeastern.a6_group8.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    Context context;
    ArrayList<Chat> arrChats;
    String senderId;

    public ChatAdapter(Context context, ArrayList<Chat> arrChats, String senderId){
        this.context = context;
        this.arrChats = arrChats;
        this.senderId = senderId;
    }
    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Chat chat = arrChats.get(position);
        holder.showMsg.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return arrChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView showMsg;
        TextView chatUsername;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatUsername = itemView.findViewById(R.id.chatUsername);
            showMsg = itemView.findViewById(R.id.showMsg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!arrChats.get(position).getSender().equals(senderId)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}

