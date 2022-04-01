package Commands;

import Bot.BotProperty;
import Bot.Embeds;
import Bot.Response;
import Shoppy.ShoppyConnection;
import Shoppy.ShoppyOrder;
import com.google.gson.Gson;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static Bot.Embeds.embed;
import static Bot.SQLConnection.getStatement;

public class messageAutoResponse extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getChannelType().isGuild() || event.getMember() == null)
            return;
        Guild guild = event.getGuild();

        Member member = event.getMember();

        User memberUser = member.getUser();

        Statement statement = getStatement();

        Message message = event.getMessage();

        String messageString = message.getContentRaw();

        TextChannel channel = event.getTextChannel();

        Category category = event.getMessage().getCategory();
        memberUser = member.getUser();

        if (!memberUser.isBot()) {
            if (category.getId().equalsIgnoreCase("930157671896731649")) {
                String replacementReason;
                int replacementAmount = 0;
                try {
                    String getTicketStatus = "SELECT Status FROM Tickets WHERE TicketID = '" + channel.getId() + "'";
                    ResultSet resultSet = statement.executeQuery(getTicketStatus);
                    if (resultSet == null)
                        return;
                    resultSet.next();
                    String ticketStatus = resultSet.getString(1);
                    if (ticketStatus.equalsIgnoreCase("claim") || ticketStatus.equalsIgnoreCase("replacement")) {
                        if (messageString.length() == 36) {
                            ShoppyConnection shoppyConnection = new ShoppyConnection();
                            Gson gson = new Gson();
                            try {
                                ShoppyOrder order = gson.fromJson(shoppyConnection.getShoppyItem("orders", messageString).body(), ShoppyOrder.class);
                                channel.sendMessageEmbeds(order.sendOrderEmbed(order).build()).queue();
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }catch (IOException e){
                                e.printStackTrace();
                            }

                            if (ticketStatus.equalsIgnoreCase("claim")) {
                                try {
                                    String insertOrder = "INSERT INTO Orders (OrderID, MemberID) VALUES ('" + messageString + "','" + member.getId() + "')";
                                    String updateQuery = "UPDATE Tickets SET Status = 'order-id-sent' WHERE TicketID ='" + channel.getId() + "'";
                                    statement.executeUpdate(updateQuery);
                                    statement.executeUpdate(insertOrder);

                                    channel.sendMessageEmbeds(embed(Embeds.ORDERCLAIM)).queue();
                                } catch (SQLIntegrityConstraintViolationException e) {
                                    channel.sendMessage(
                                    """
                                    **[Fraud Detection]** Order ID was already claimed. Do not send product!
                                    If this is a mistake, don't worry a staff member will help you as soon as possible
                                    """).queue();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                channel.upsertPermissionOverride(member)
                                        .setDeny(Permission.MESSAGE_SEND)
                                        .setAllow(Permission.VIEW_CHANNEL)
                                        .queue();
                            } else if (ticketStatus.equalsIgnoreCase("replacement")) {
                                try {
                                    String updateQuery = "UPDATE Tickets SET Status = 'replacement-ask' WHERE TicketID ='" + channel.getId() + "'";
                                    statement.executeUpdate(updateQuery);

                                    channel.sendMessageEmbeds(embed(Embeds.ORDERREPLACEMENTAMOUNT)).setActionRow(
                                            Button.danger("close-ticket", "Close")
                                    ).queue();
                                } catch (SQLException e) {
                                    channel.sendMessage("There was an issue with that request. Contact Mythik!").queue();
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            channel.sendMessage("Not a valid order ID. Send the order ID you received in the mail.").queue(message1 -> {
                                message1.delete().queueAfter(20, TimeUnit.SECONDS);
                            });
                        }
                    } else if (ticketStatus.equalsIgnoreCase("replacement-ask")) {
                        try {
                            String updateQuery = "UPDATE Tickets SET Status = 'replacement-reason' WHERE TicketID ='" + channel.getId() + "'";
                            statement.executeUpdate(updateQuery);
                            channel.sendMessage("""
                                    What is the issue with the product?
                                    You will only be able to send one message after this message so please be descriptive as possible.
                                    """).queue();

                        } catch (NumberFormatException e) {
                            message.delete().queue();
                            channel.sendMessage("That is not a valid input. The input must be a number." +
                                    "\nPlease send the amount of replacements you need." +
                                    "\nBe aware if you are caught lying you will receive no replacements and no refunds." +
                                    "\nSo make sure you submit accurate and honest information.")
                                    .queue(message1 -> message1.delete().queueAfter(10, TimeUnit.SECONDS)
                            );
                        }catch (SQLException e){
                            channel.sendMessage("There was an issue with changing the ticket status in the database!").queue();
                            e.printStackTrace();
                        }
                    } else if (ticketStatus.equalsIgnoreCase("replacement-reason")) {
                        try {
                            replacementReason = messageString;
                            String updateQuery = "UPDATE Tickets SET Status = 'replacement-await' WHERE TicketID ='" + channel.getId() + "'";
                            statement.executeUpdate(updateQuery);
                            EmbedBuilder replacementEmbed = new EmbedBuilder()
                                    .setTitle("Better Alts Tickets")
                                    .setColor(Color.GREEN)
                                    .setDescription("Thank you, a staff member will be with you shortly to view your inquiry")
                                    .addField("**Replacement Reason:**", replacementReason, false);
                            channel.sendMessageEmbeds(replacementEmbed.build()).queue();
                            channel.upsertPermissionOverride(member)
                                    .setDeny(Permission.MESSAGE_SEND)
                                    .setAllow(Permission.VIEW_CHANNEL)
                                    .queue();
                        } catch (SQLException e) {
                            channel.sendMessage("There was an issue with changing the ticket status in the database!").queue();
                            e.printStackTrace();
                        }
                    } else if(ticketStatus.equalsIgnoreCase("purchase")){
                        String updateQuery = "UPDATE Tickets SET Status = 'purchase-method' WHERE TicketID ='" + channel.getId() + "'";
                        statement.executeUpdate(updateQuery);

                        // List the payment methods to the user
                        channel.sendMessageEmbeds(embed(Embeds.PAYMENTMETHODS)).queue();
                    } else if(ticketStatus.equalsIgnoreCase("purchase-method")){
                        String updateQuery = "UPDATE Tickets SET Status = 'purchase-await' WHERE TicketID ='" + channel.getId() + "'";
                        statement.executeUpdate(updateQuery);

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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



