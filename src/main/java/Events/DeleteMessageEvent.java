package Events;

import Bot.BotProperty;
import Bot.MessageObj;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

public class DeleteMessageEvent extends ListenerAdapter {

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        if (event.getGuild() == null)
            return;

        BotProperty botProperty = new BotProperty();

        // If the deleted message is in the log channel
        if (event.getTextChannel().getId().equalsIgnoreCase("929113757408460810")) {

            // Get the audit log
            event.getGuild().retrieveAuditLogs().type(ActionType.MESSAGE_DELETE).limit(1).queue(
                deletedMessageLog -> {
                    botProperty.corruptStaffAlert(event.getJDA(), event.getGuild(), deletedMessageLog.get(0).getUser(), "DELETE A MESSAGE<" + event.getMessageId() + "> IN LOG CHANNEL!");
                }
            );
        }

        Guild guild = event.getGuild();

        TextChannel textChannel = event.getTextChannel();

        String deletedMsgId = event.getMessageId();

        try{
            MessageObj deletedMsgObj = botProperty.getMessageHistory().get(deletedMsgId);

            Member authorOfDeletedMsg = guild.getMemberById(deletedMsgObj.getSenderID());

            EmbedBuilder deletedMsgEmbed = new EmbedBuilder();

            deletedMsgEmbed.setTitle("**Deleted a message**");
            deletedMsgEmbed.setAuthor(authorOfDeletedMsg.getUser().getAsTag(), authorOfDeletedMsg.getEffectiveAvatarUrl(),authorOfDeletedMsg.getEffectiveAvatarUrl());
            deletedMsgEmbed.setDescription("**Message:** " + deletedMsgObj.getMessageContent());

            deletedMsgEmbed.addField("Channel", textChannel.getAsMention(), false);
            deletedMsgEmbed.addField("Author", guild.getMemberById(deletedMsgObj.getSenderID()).getAsMention(), false);
            DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy H:mm a");

            deletedMsgEmbed.addField("Message Created", FOMATTER.format(deletedMsgObj.getTimeCreated().minusHours(5)), false);

            deletedMsgEmbed.setFooter(new Date().toString());
            deletedMsgEmbed.setColor(Color.RED);

            botProperty.storeLog(event.getJDA(), deletedMsgEmbed, "DeletedMessage");

        }catch (NullPointerException e){
            System.out.println("[Log]: Unknown Message Deleted");
        }
    }
}
