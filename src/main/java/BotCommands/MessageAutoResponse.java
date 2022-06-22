package BotCommands;

import Bot.BotProperty;
import CustomObjects.Embeds;
import Bot.Response;
import Bot.SQLConnection;
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

        Statement statement = SQLConnection.getStatement();

        if(member.getId().equalsIgnoreCase("976956826472050689"))
        {
            if(messageArr[0].equalsIgnoreCase("m!editEmbed"))
            {
                guild.getTextChannelById("953923167305465916").retrieveMessageById("953923565894402048").complete().editMessageEmbeds(Embeds.RULES.build()).queue();
                guild.getTextChannelById("934489170456494161").retrieveMessageById("934736207701762088").complete().editMessageEmbeds(Embeds.RULES.build()).queue();
            }else if(messageArr[0].equals("m!loadInvites1")){
                guild.retrieveInvites().queue(
                        invites ->
                        {
                            String query = "INSERT INTO Invites (Code, InviteCount, MemberID) VALUES";

                            for (Invite invite: invites)
                            {
                                query += "('" + invite.getCode() + "', " + invite.getUses() + ", '" + invite.getInviter().getId() + "'),";
                            }
                            query = query.substring(0, query.length() - 1) + ";";
                            try {
                                statement.executeUpdate(query);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                );
            }
        }

        if (!memberUser.isBot()) {
            if(channel.getId().equalsIgnoreCase("965030819041255454")){
               channel.sendMessage("""
                       Thank you for your suggestion. We will add it if there are enough people that want this!
                       
                       Make a suggestion for a new product down below.
                               """).queue();
            }

            // Auto Response Code
            HashMap<String, Response> responses = BotProperty.getResponseHashMap();

            for (Map.Entry<String, Response> set : responses.entrySet()) {
                if (messageString.toLowerCase().contains(set.getKey())) {
                    Response response = set.getValue();
                    if (response.isDeleteTriggerMsg())
                        message.delete().queue();
                    channel.sendMessage(response.getResponse()).queue(responseMsg -> {
                        if (response.isDeleteResponse()) {
                            responseMsg.delete().queueAfter(response.getDeleteDelay(), TimeUnit.SECONDS);
                        }
                    });
                    break;
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



