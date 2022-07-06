package BotCommands;

import Bot.BotProperty;
import Bot.MessageObj;
import CustomObjects.Embeds;
import CustomObjects.Response;
import Bot.SQLConnection;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MessageAutoResponse extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getChannelType().isGuild() || event.getMember() == null)
            return;
        Guild guild = event.getGuild();

        Member member = event.getMember();

        User memberUser = member.getUser();

        Message message = event.getMessage();

        String messageString = message.getContentRaw();

        TextChannel channel = event.getTextChannel();

        String[] messageArr = messageString.split("/");

        if(!memberUser.isBot())
        {
            MessageObj messageObj = new MessageObj(messageString, member.getId(), message.getTimeCreated().toZonedDateTime());

            if(MessageObj.getMessageCount() > 100)
            {
                // Decrement the count and remove the last message from the hashMap and queue
                MessageObj.setMessageCount(MessageObj.getMessageCount() - 1);
                String poppedId = BotProperty.getMessageHistoryQueue().poll();
                BotProperty.getMessageHistory().remove(poppedId);
            }

            // Add the messages to the queue and hashMap
            BotProperty.getMessageHistory().put(message.getId(), messageObj);
            BotProperty.getMessageHistoryQueue().add(message.getId());
        }

        if(member.getId().equalsIgnoreCase("976956826472050689"))
        {
            if(messageArr[0].equalsIgnoreCase("m!editEmbed"))
            {
                guild.getTextChannelById("953923167305465916").retrieveMessageById("953923565894402048").complete().editMessageEmbeds(Embeds.RULES.build()).queue();
                guild.getTextChannelById("934489170456494161").retrieveMessageById("934736207701762088").complete().editMessageEmbeds(Embeds.RULES.build()).queue();
            }
        }

        if (!memberUser.isBot()) {

            // Auto Response Code
            HashMap<String, Response> responses = BotProperty.getResponseHashMap();

            for (Map.Entry<String, Response> set : responses.entrySet()) {
                Response responseObj = set.getValue();

                if (messageString.toLowerCase().contains(set.getKey())) {

                    if ((responseObj.isContains() && messageString.toLowerCase().contains(responseObj.getTriggerString().toLowerCase())) ||
                         messageString.equalsIgnoreCase(responseObj.getTriggerString()))
                    {
                        if (responseObj.isDeleteTriggerMsg())
                            message.delete().queue();
                        channel.sendMessage(responseObj.getResponse()).queue();
                        break;
                    }
                }
            }

            String messageLowerCase = messageString.toLowerCase();

            // FILTERING MESSAGES FOR ADVERTISEMENTS
            if (message.getMentionedMembers().size() > 5) {
                message.delete().queue();
                guild.addRoleToMember(member, guild.getRoleById("936718165130481705")).queue();
                channel.sendMessage("Bot detected! You have been muted! Make a ticket to appeal!").queue(message1 -> message1.delete().queueAfter(30, TimeUnit.SECONDS));
            }

            if (messageLowerCase.contains(" king") || messageLowerCase.contains("k i n g") || messageLowerCase.contains("king alts") || messageLowerCase.contains("kingalts")
                    || messageLowerCase.contains("asteroid") || messageLowerCase.contains("alts.top")
                    || messageLowerCase.contains("discord.gg") || messageLowerCase.contains("alts top")
                    || messageLowerCase.contains("personic") || messageLowerCase.contains("alten")) {
                message.delete().queue();

                channel.sendMessage("You are not allowed to send that! Mythik will punish you.").queue();
            }
        }
    }
}



