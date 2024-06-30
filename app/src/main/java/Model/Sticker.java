package Model;

public class Sticker {

    private String stickerId;
    private String stickerName;
    private String stickerUrl;

    public Sticker() {
    }

    public Sticker(String stickerId, String stickerName, String stickerUrl) {
        this.stickerId = stickerId;
        this.stickerName = stickerName;
        this.stickerUrl = stickerUrl;
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
}

