package Events;

import Bot.BotProperty;
import Bot.MessageObj;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DeleteMessagesEvent extends ListenerAdapter {

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        if (!event.getChannelType().isGuild())
            return;

        BotProperty botProperty = new BotProperty();

        Guild guild = event.getGuild();

        TextChannel textChannel = event.getTextChannel();

        String deletedMsgId = event.getMessageId();

        try{
            MessageObj deletedMsgObj = BotProperty.getMessageHistory().get(deletedMsgId);

            Member authorOfDeletedMsg = guild.getMemberById(deletedMsgObj.senderID());

            EmbedBuilder deletedMsgEmbed = new EmbedBuilder();

            deletedMsgEmbed.setTitle("**Deleted a message**");
            deletedMsgEmbed.setAuthor(authorOfDeletedMsg.getUser().getAsTag(), authorOfDeletedMsg.getEffectiveAvatarUrl(),authorOfDeletedMsg.getEffectiveAvatarUrl());
            deletedMsgEmbed.setDescription("**Message:** " + deletedMsgObj.messageContent());

            deletedMsgEmbed.addField("Channel", textChannel.getAsMention(), false);
            deletedMsgEmbed.addField("Author", guild.getMemberById(deletedMsgObj.senderID()).getAsMention(), false);
            DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy H:mm a");

            deletedMsgEmbed.addField("Message Created", FORMATTER.format(deletedMsgObj.timeCreated().minusHours(5)), false);

            deletedMsgEmbed.setFooter(new Date().toString());
            deletedMsgEmbed.setColor(Color.RED);

            botProperty.storeLog(guild, deletedMsgEmbed, "DeletedMessage");

        }catch (NullPointerException e){
            System.out.println("[Log]: Unknown Message Deleted");
        }
    }
}
