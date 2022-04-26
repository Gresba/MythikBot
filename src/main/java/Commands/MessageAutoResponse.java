package Commands;

import Bot.BotProperty;
import Bot.Embeds;
import Bot.Response;
import Shoppy.ShoppyConnection;
import Shoppy.ShoppyOrder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static Bot.SQLConnection.getStatement;

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
//            if (!member.getRoles().contains(guild.getRoleById("939559233010151476"))) {
//                if (message.getMentionedMembers().size() > 0) {
//
//                    Member pingedMember = message.getMentionedMembers().get(0);
//
//
//                    if (pingedMember.getRoles().contains(guild.getRoleById("939559233010151476")) && !member.getRoles().contains(guild.getRoleById("939559233010151476"))) {
//                        String getWarningCount = "SELECT WarningCount FROM Users WHERE MemberID = '" + member.getId() + "'";
//
//                        try {
//                            ResultSet warningCountReslt = null;
//                            warningCountReslt = statement.executeQuery(getWarningCount);
//                            int warningCount = 0;
//
//                            while (warningCountReslt.next()) {
//                                warningCount = warningCountReslt.getInt(1) + 1;
//                            }
//                            if (warningCount == 3) {
//                                member.getUser().openPrivateChannel().flatMap(privateChannel ->
//                                        privateChannel.sendMessageEmbeds(Embed(Embeds.KICK, "pinging"))
//                                ).queue();
//
//                                channel.sendMessage(member.getAsMention() + " has been kicked for pinging too much!").queue();
//                                guild.kick(member).queue();
//                            }
//
//                            if (warningCount == 5) {
//                                member.getUser().openPrivateChannel().flatMap(privateChannel ->
//                                        privateChannel.sendMessageEmbeds(Embed(Embeds.BAN, "pinging"))
//                                ).queue();
//
//                                channel.sendMessage(member.getAsMention() + " has been banned for pinging too much!").queue();
//
//                                String setWarningCount = "UPDATE Users SET WarningCount = '0' WHERE MemberID = '" + member.getId() + "'";
//
//                                statement.executeUpdate(setWarningCount);
//                                guild.ban(member, 0, "5 Warnings").queue();
//                            }
//
//                            String setWarningCount = "UPDATE Users SET WarningCount = '" + warningCount + "' WHERE MemberID = '" + member.getId() + "'";
//                            statement.executeUpdate(setWarningCount);
//
//                            channel.sendMessage(memberUser.getAsMention() + " do not ping staff members! **Warning:** " + warningCount + "/3\n3 warnings will result in a kick\n5 warnings wil result in a ban").queue();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }

                String messageLowerCase = messageString.toLowerCase();

                // FILTERING MESSAGES FOR ADVERTISEMENTS
                if (message.getMentionedMembers().size() > 5) {
                    message.delete().queue();
                    guild.addRoleToMember(member, guild.getRoleById("936718165130481705")).queue();
                    channel.sendMessage("Botting detected! You have been muted! Make a ticket to appeal!").queue(message1 -> {
                        message1.delete().queueAfter(30, TimeUnit.SECONDS);
                    });
                }

                if (messageLowerCase.contains(" king") || messageLowerCase.contains("k i n g") || messageLowerCase.contains("king alts") || messageLowerCase.contains("kingalts")
                        || messageLowerCase.contains("asteroid") || messageLowerCase.contains("alts.top")
                        || messageLowerCase.contains("discord.gg") || messageLowerCase.contains("alts top")
                        || messageLowerCase.contains("personic")) {
                    message.delete().queue();

                    String query = "SELECT WarningCount FROM Users WHERE MemberID = '" + member.getId() + "'";
                    channel.sendMessage("You are not allowed to send that! Mythik will punish you :/").queue();
                }
            }
        }
    }



