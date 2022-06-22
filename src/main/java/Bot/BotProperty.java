package Bot;

import BotObjects.GuildObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class BotProperty {
    public static HashMap<String, GuildObject> guildsHashMap = new HashMap<>();
    private static final HashMap<String, Response> responseHashMap = new HashMap<>();
    private static HashMap<String, Boolean> autoNukeChannels = new HashMap<>();
    private static HashMap<String, String> verifyCodes = new HashMap<>();
    // Storing message
    private static HashMap<String, MessageObj> messageHistory = new HashMap<>();
    private static Queue<String> messageHistoryQueue = new LinkedList<>();
    public static HashMap<String, Response> getResponseHashMap() {
        return responseHashMap;
    }

    public BotProperty()
    {

    }

    public void storeLog(JDA jda, EmbedBuilder embedBuilder, String logType)
    {
        String channelID;
        if(logType.equalsIgnoreCase("Joined"))
        {
            channelID = "929114231679365121";
        }else if(logType.equalsIgnoreCase("Left")){
            channelID = "937170925655310386";
        }else {
            channelID = "929113757408460810";
        }
        jda.getGuildById(929101421272510524L).getTextChannelById(channelID).sendMessageEmbeds(embedBuilder.build()).queue(message -> {
            embedBuilder
                    .addField("**Log Message ID:**", message.getId(), false);
            jda.getGuildById("859129620493369364").getTextChannelById("965155229999980644").sendMessageEmbeds(embedBuilder.build()).queue();
        });
    }

    public void corruptStaffAlert(JDA jda, Guild guild, User corruptStaff, String reason)
    {
        Guild finalModerationGuild = jda.getGuildById("859129620493369364");

        guild.removeRoleFromMember(guild.getMemberById(corruptStaff.getId()), guild.getRoleById(929114481257234443L)).queue();
        guild.removeRoleFromMember(guild.getMemberById(corruptStaff.getId()), guild.getRoleById(939559233010151476L)).queue();
        corruptStaff.openPrivateChannel().queue(
                privateChannel -> privateChannel.sendMessage("**[BETTER ALTS MODERATION]** You have been detected as a corrupt staff. Wait for Mythik to review this!").queue()
        );

        finalModerationGuild
                .getTextChannelById("965155229999980644")
                .sendMessage(finalModerationGuild.getRoleById("859133904169730100").getAsMention() + " **[ALERT]** " + corruptStaff.getAsMention() + " " + reason).queue();
    }

    public static HashMap<String, MessageObj> getMessageHistory() {
        return messageHistory;
    }

    public static Queue<String> getMessageHistoryQueue() {
        return messageHistoryQueue;
    }
}
