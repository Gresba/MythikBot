package Bot;

import BotObjects.GuildObject;
import CustomObjects.Response;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class BotProperty {
    public static HashMap<String, GuildObject> guildsHashMap = new HashMap<>();
    private static final HashMap<String, Response> responseHashMap = new HashMap<>();
    // Storing message
    private static final HashMap<String, MessageObj> messageHistory = new HashMap<>();
    private static final Queue<String> messageHistoryQueue = new LinkedList<>();
    public static HashMap<String, Response> getResponseHashMap() {
        return responseHashMap;
    }

    public BotProperty()
    {

    }

    public static void storeLog(Guild guild, EmbedBuilder embedBuilder, String logType)
    {
        String channelID;

        GuildObject guildObject = guildsHashMap.get(guild.getId());

        // Figure out which channels to send the logs to
        if(logType.equalsIgnoreCase("Joined"))
        {
            channelID = "929114231679365121";
        } else if(logType.equalsIgnoreCase("Left")){
            channelID = "937170925655310386";

        // Any action which isn't a join or leave
        } else {
            channelID = guildObject.getLogChannelId();
        }

        guild.getTextChannelById(channelID).sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * Alert owner and user if a corrupt staff member is detected
     *
     * @param guild The guild this occurred in
     * @param corruptStaff The corrupt staff
     * @param orderId The order which the staff tried to abuse
     */
    public void corruptStaffAlert(Guild guild, User corruptStaff, String orderId)
    {
        Member serverOwner = guild.getMemberById(BotProperty.guildsHashMap.get(guild.getId()).getServerOwnerId());

        // Remove the configured staff member role
        guild.removeRoleFromMember(guild.getMemberById(corruptStaff.getId()), guild.getRoleById(guildsHashMap.get(guild.getId()).getStaffRoleId())).queue();

        // Alert the user that they have been detected
        corruptStaff.openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessage(
                        "**[BETTER ALTS MODERATION]** You have been detected as a corrupt staff. Wait for Mythik to review this!"
                ).queue()
        );

        // Alert Mythik
        serverOwner.getUser().openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessage(
                        "**[BETTER ALTS MODERATION]** " + corruptStaff.getAsMention() + " detected as a corrupt staff" +
                                "**Order Id:** " + orderId
                ).queue()
        );
    }

    public static HashMap<String, MessageObj> getMessageHistory() {
        return messageHistory;
    }

    public static Queue<String> getMessageHistoryQueue() {
        return messageHistoryQueue;
    }
}
