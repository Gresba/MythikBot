package Bot;

import java.time.ZonedDateTime;

public class MessageObj {

    private static int messageCount;

    private final String messageId;
    private final String messageContent;

    private final String senderID;

    private final ZonedDateTime timeCreated;

    public MessageObj(String messageId, String messageContent, String senderID, ZonedDateTime timeCreated)
    {
        this.messageId = messageId;
        this.messageContent = messageContent;
        this.senderID = senderID;
        this.timeCreated = timeCreated;
        messageCount++;
    }

    public ZonedDateTime getTimeCreated() {
        return timeCreated;
    }

    public static int getMessageCount() {
        return messageCount;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getSenderID() {
        return senderID;
    }

    public static void setMessageCount(int messageCount) {
        MessageObj.messageCount = messageCount;
    }

}
