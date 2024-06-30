package Model;

public class StickerReceived {

    private String stickerId;
    private String stickerName;
    private String stickerUrl;
    private String sender;
    private String receiver;
    private long timestamp;

    public StickerReceived() {

    }

    public StickerReceived(String stickerId, String stickerName, String stickerUrl, String sender, String receiver, long timestamp) {
        this.stickerId = stickerId;
        this.stickerName = stickerName;
        this.stickerUrl = stickerUrl;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
    }

    public String getStickerId() {
        return stickerId;
    }

    public void setStickerId(String stickerId) {
        this.stickerId = stickerId;
    }

    public String getStickerName() {
        return stickerName;
    }

    public void setStickerName(String stickerName) {
        this.stickerName = stickerName;
    }

    public String getStickerUrl() {
        return stickerUrl;
    }

    public void setStickerUrl(String stickerUrl) {
        this.stickerUrl = stickerUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}

