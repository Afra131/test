package Model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private long timestamp;

    public Chat(){

    }
    public Chat(String sender, String receiver, String message, long timestamp){
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.timestamp = timestamp;
    }

    public String getSender(){
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
