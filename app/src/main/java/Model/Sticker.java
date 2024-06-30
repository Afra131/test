package Model;

public class Sticker {

    private String stickerId;
    private String stickerName;
    private String stickerUrl;
    private long timestamp;

    public Sticker() {
    }

    public Sticker(String stickerId, String stickerName, String stickerUrl, long timestamp) {
        this.stickerId = stickerId;
        this.stickerName = stickerName;
        this.stickerUrl = stickerUrl;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}



