package boatwars.net;

/**
 * Created by Joonas on 19.7.2015.
 */
public class MessageObject {
    private String type;
    private String message;
    private String sender;

    public MessageObject(String type, String message, String sender){
        this.type = type;
        this.message = message;
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
