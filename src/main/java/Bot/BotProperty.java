package Bot;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class BotProperty {

    // Bot prefix
    private static String prefix;

    private static int messageHistoryCount;

    public static void setMessageHistoryQueue(Queue<String> messageHistoryQueue) {
        BotProperty.messageHistoryQueue = messageHistoryQueue;
    }

    private static HashMap<String, Response> responseHashMap = new HashMap<>();
    private static HashMap<String, Boolean> autoNukeChannels = new HashMap<>();
    private static HashMap<String, String> verifyCodes = new HashMap<>();

    // Storing message
    private static HashMap<String, MessageObj> messageHistory = new HashMap<>();
    private static Queue<String> messageHistoryQueue = new LinkedList<>();

    public static HashMap<String, Response> getResponseHashMap() {
        return responseHashMap;
    }

    public static void setResponseHashMap(HashMap<String, Response> responseHashMap) {
        BotProperty.responseHashMap = responseHashMap;
    }

    public static int getMessageHistoryCount() {
        return messageHistoryCount;
    }


    public static void setMessageHistoryCount(int messageHistoryCount) {
        BotProperty.messageHistoryCount = messageHistoryCount;
    }

    public BotProperty(String guildID)
    {
        try {
            ResultSet resultSet = new SQLConnection().getStatement().executeQuery("SELECT Prefix FROM GUILDS WHERE GuildID=" + guildID);
            while(resultSet.next())
            {
                prefix = resultSet.getString("Prefix");
            }
        }catch (Exception e){
            System.out.println("[SQL CONNECTION]: Issue getting the prefix of guildId: " + guildID);
            e.printStackTrace();
        }
    }

    public BotProperty()
    {

    }

    public static HashMap<String, String> getVerifyCodes() {
        return verifyCodes;
    }

    public static void setVerifyCodes(HashMap<String, String> verifyCodes) {
        BotProperty.verifyCodes = verifyCodes;
    }

    public static String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public static HashMap<String, Boolean> getAutoNukeChannels() {
        return autoNukeChannels;
    }

    public static void setAutoNukeChannels(HashMap<String, Boolean> autoNukeChannel) {
        BotProperty.autoNukeChannels = autoNukeChannels;
    }

    public static HashMap<String, MessageObj> getMessageHistory() {
        return messageHistory;
    }

    public static void setMessageHistory(HashMap<String, MessageObj> messageHistory) {
        BotProperty.messageHistory = messageHistory;
    }

    public static Queue<String> getMessageHistoryQueue() {
        return messageHistoryQueue;
    }

    public static void setMessageHistory(Queue<String> messageHistoryQueue) {
        BotProperty.messageHistoryQueue = messageHistoryQueue;
    }
}
