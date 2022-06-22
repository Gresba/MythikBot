package Bot;

import java.time.ZonedDateTime;

public record MessageObj(String messageContent, String senderID, ZonedDateTime timeCreated) {

    private static int messageCount;

    public MessageObj(String messageContent, String senderID, ZonedDateTime timeCreated) {
        this.messageContent = messageContent;
        this.senderID = senderID;
        this.timeCreated = timeCreated;
        messageCount++;
    }

    public static int getMessageCount() {
        return messageCount;
    }

    public static void setMessageCount(int messageCount) {
        MessageObj.messageCount = messageCount;
    }

}
