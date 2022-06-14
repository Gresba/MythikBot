package Bot;

import BotObjects.GuildObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

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

    public static HashMap<String, GuildObject> guildsHashMap = new HashMap<>();
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

    public void storeLog(JDA jda, EmbedBuilder embedBuilder, String logType)
    {
        String channelID = "";
        if(logType.equalsIgnoreCase("Joined"))
        {
            channelID = "929114231679365121";
        }else if(logType.equalsIgnoreCase("Left")){
            channelID = "937170925655310386";
        }else {
            channelID = "929113757408460810";
        }
        jda.getGuildById(929101421272510524l).getTextChannelById(channelID).sendMessageEmbeds(embedBuilder.build()).queue(message -> {
            embedBuilder
                    .addField("**Log Message ID:**", message.getId(), false);
            jda.getGuildById("859129620493369364").getTextChannelById("965155229999980644").sendMessageEmbeds(embedBuilder.build()).queue();
        });
    }

    public void corruptStaffAlert(JDA jda, Guild guild, User corruptStaff, String reason)
    {
        Guild finalModerationGuild = jda.getGuildById("859129620493369364");

        guild.removeRoleFromMember(guild.getMemberById(corruptStaff.getId()), guild.getRoleById(929114481257234443l)).queue();
        guild.removeRoleFromMember(guild.getMemberById(corruptStaff.getId()), guild.getRoleById(939559233010151476l)).queue();
        corruptStaff.openPrivateChannel().queue(
                privateChannel -> {
                    privateChannel.sendMessage("**[BETTER ALTS MODERATION]** You have been detected as a corrupt staff. Wait for Mythik to review this!").queue();
                }
        );

        finalModerationGuild
                .getTextChannelById("965155229999980644")
                .sendMessage(finalModerationGuild.getRoleById("859133904169730100").getAsMention() + " **[ALERT]** " + corruptStaff.getAsMention() + " " + reason).queue();
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
