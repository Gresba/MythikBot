package Bot;

import java.time.ZonedDateTime;

public class MessageObj {

    /*****************************************************************************************************/
    // Channel Nuke Command
    /*****************************************************************************************************/
    private static int messageCount;

    private String messageId;
    private String messageContent;

    private String senderID;

    private ZonedDateTime timeCreated;

    public MessageObj(String messageId, String messageContent, String senderID, ZonedDateTime timeCreated)
    {
        this.messageId = messageId;
        this.messageContent = messageContent;
        this.senderID = senderID;
        this.timeCreated = timeCreated;
        messageCount++;
    }

    public MessageObj()
    {

    }

    public ZonedDateTime getTimeCreated() {
        return timeCreated;
    }

    public static int getMessageCount() {
        return messageCount;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setTimeCreated(ZonedDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public static void setMessageCount(int messageCount) {
        MessageObj.messageCount = messageCount;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
