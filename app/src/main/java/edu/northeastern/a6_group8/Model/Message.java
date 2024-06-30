package edu.northeastern.a6_group8.Model;

public class Message {
    private Chat chat;
    private Sticker sticker;
    private boolean isChat;
    private int viewType;
    public static final int TYPE_CHAT_LEFT = 0;
    public static final int TYPE_CHAT_RIGHT = 1;
    public static final int TYPE_STICKER_LEFT = 2;
    public static final int TYPE_STICKER_RIGHT = 3;
    public Message(Chat chat, int viewType) {
        this.chat = chat;
        this.isChat = true;
        this.viewType = viewType;
    }

    public Message(Sticker sticker, int viewType) {
        this.sticker = sticker;
        this.isChat = false;
        this.viewType = viewType;
    }

    public boolean isChat() {
        return isChat;
    }

    public Chat getChat() {
        return chat;
    }

    public Sticker getSticker() {
        return sticker;
    }

    public int getViewType() {
        return viewType;
    }

}

