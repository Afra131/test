package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import Model.Chat;
import edu.northeastern.a6_group8.MessageActivity;
import edu.northeastern.a6_group8.R;
import edu.northeastern.a6_group8.User;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    Context context;
    ArrayList<Chat> arrChats;
    String senderId;

    public MessageAdapter(Context context, ArrayList<Chat> arrChats, String senderId){
        this.context = context;
        this.arrChats = arrChats;
        this.senderId = senderId;
    }
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
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

