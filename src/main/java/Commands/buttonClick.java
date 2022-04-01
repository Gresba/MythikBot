package Commands;

import Bot.BotProperty;
import Bot.Embeds;
import Bot.SQLConnection;
import Bot.TicketObj;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class buttonClick extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        Statement statement = SQLConnection.getStatement();
        switch (event.getComponentId())
        {
            // Creating a ticket button
            case "create-ticket":
                // Creates a channel
                guild.createTextChannel(member.getUser().getName(), guild.getCategoryById("930157671896731649")).queue(
                    ticketChannel -> {
                        try {
                            // Create query with
                            String insertQuery = "INSERT INTO Tickets VALUES ('" + ticketChannel.getId() + "', '" + member.getId() + "', 'starting')";
                            statement.executeUpdate(insertQuery);
                            ticketChannel.getManager().setTopic(member.getId()).queue();

                            // Setting permissions to allow ticket creator to view channel but can't send messages yet
                            ticketChannel.putPermissionOverride(member)
                                    .setDeny(Permission.MESSAGE_SEND)
                                    .setAllow(Permission.VIEW_CHANNEL)
                                    .queue();
                            // No one else can view the ticket
                            ticketChannel.createPermissionOverride(guild.getRoleById("945973060027166750"))
                                    .setDeny(Permission.VIEW_CHANNEL)
                                    .queue();
                            EmbedBuilder ticketQuestionsEmbed = new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setTitle("Better Alts Ticket")
                                    .setDescription("Pick the reason why you created this ticket")
                                    .addField("**Note**", "You will not be able to speak until the bot ask you to", false);


                            // Adding the choices
                            ticketChannel.sendMessageEmbeds(ticketQuestionsEmbed.build()).setActionRow(
                                    Button.primary("order-ticket", "Order"),
                                    Button.success("partnership-ticket", "Sponsor/Partnership"),
                                    Button.primary("purchase-ticket", "Purchase a Product"),
                                    Button.success("general-ticket", "General Question"),
                                    Button.danger("close-ticket", "Close")
                            ).queue();

                            event.reply("Ticket successfully created! " + ticketChannel.getAsMention()).setEphemeral(true).queue();
                        } catch (SQLIntegrityConstraintViolationException e){
                            event.reply("You can't create anymore tickets. Close your old one first.").setEphemeral(true).queue();
                            ticketChannel.delete().queue();
                        } catch (SQLException e) {
                            event.reply("There was an issue with creating your ticket. Contact Mythik.").setEphemeral(true).queue();
                            ticketChannel.delete().queue();
                            e.printStackTrace();
                        }
                    }
                );
                break;
            case "order-ticket":
                event.getGuild().getTextChannelById(channel.getId()).getManager().setName(member.getUser().getName() + "-order").queue();
                EmbedBuilder orderChoiceEmbed = new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle("Better Alts Ticket")
                        .setDescription("""
                            Choose the problem with your order.
                            **Order** - Replacements/Claim Order
                            **Purchase** - Purchase something
                            **Partnership** - Apply for sponsorship/partnership
                            **General Question** - Asking general questions""");
                event.editMessageEmbeds(orderChoiceEmbed.build())
                        .setActionRow(
                                Button.primary("order-claim", "Claim Order"),
                                Button.success("order-replacement", "Replacement"),
                                Button.danger("close-ticket", "Close")
                        ).queue();
                channel.upsertPermissionOverride(member)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
                break;
            case "partnership-ticket":
                event.getGuild().getTextChannelById(channel.getId()).getManager().setName(member.getUser().getName() + "-partnership").queue();

                event.editMessageEmbeds(Embeds.embed(Embeds.SPONSORSHIP))
                        .setActionRow(
                                Button.primary("requirements-ready","Video Or Discord Link is Ready"),
                                Button.danger("close-ticket","Close")
                        ).queue();
                break;
            case "purchase-ticket":
                channel.getManager().setName(member.getUser().getName() + "-purchase").queue();
                String updateQuery = "UPDATE Tickets SET Status = 'purchase' WHERE TicketID ='" + channel.getId() + "'";
                try {
                    statement.executeUpdate(updateQuery);
                } catch (SQLException e) {
                    channel.sendMessage("There was an error with changing the ticket status!").queue();
                    e.printStackTrace();
                }
                EmbedBuilder purchaseTicket = new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle("Better Alts Ticket")
                        .setDescription("What product would you like to order?")
                        .addField("**Skyblock**", "Coins\n " + guild.getTextChannelById("934231649812611082").getAsMention() +
                            "100m Networth Accounts ", false)
                        .addField("**Minecraft NFA/SFA:**", """
                                Unbanned NFA/SFA Mix
                                Banned NFA/SFA Mix
                                Migrated Unbanned NFA
                                Migrated Banned NFA
                                """, false)
                        .addField("**Hypixel Unbanned Ranked/Level**", """
                                Ranked
                                Level 21+
                                Ranked + Level 21+
                                """, false)
                        .addField("**Minecraft MFAs**", """
                                Unbanned MFAs
                                Banned MFAs
                                Unbanned Level 21+ MFAs
                                Unbanned Ranked MFAs
                                """, false)
                        .addField("**Gaming Yahoos**", """
                                Riot Yahoos
                                Epic Games Yahoos
                                Steam Yahoos
                                Discord Yahoos
                                """, false)
                        .addField("**VPN:**", """
                                Vypr VPN
                                """,false)
                        .addField("**Generator:**", """
                                Unbanned NFA/SFA Generator - $15/month
                                """, false)
                        .addField("**Choose a Product**", "Send the exact product you want (Copy and Paste)", false);
                event.editMessageEmbeds(purchaseTicket.build())
                        .setActionRow(
                        Button.danger("close-ticket", "Close")
                ).queue();
                channel.upsertPermissionOverride(member)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
                break;
            case "general-ticket":
                event.editMessageEmbeds(Embeds.embed(Embeds.FAQ))
                        .setActionRow(
                            Button.primary("not-answered-ticket","Question Not Answered"),
                            Button.danger("close-ticket","Close")
                        ).queue();
                break;
            case "close-ticket":
                event.deferReply(true).queue();

                event.getHook().sendMessage("Deleting ticket...").queue();
                EmbedBuilder ticketClosedEmbed = new EmbedBuilder()
                        .setTitle("Better Alts Tickets")
                        .setColor(Color.RED)
                        .setDescription("Your ticket has been closed. If you have anymore issues then please make another ticket!");
                guild.getMemberById(channel.getTopic()).getUser().openPrivateChannel().flatMap(
                        privateChannel -> privateChannel.sendMessageEmbeds(ticketClosedEmbed.build())
                ).queue();

                channel.delete().queue();
                break;
            case "order-claim":
                // Renaming the channel
                channel.getManager().setName(member.getUser().getName() + "-claim");

                // Trying to update the ticket status
                try {
                    String setClaimQuery = "UPDATE Tickets SET Status = 'claim' WHERE TicketID ='" + channel.getId() + "'";
                    statement.executeUpdate(setClaimQuery);
                    EmbedBuilder orderEmbed = new EmbedBuilder()
                            .setTitle("Claim Order")
                            .setColor(Color.GREEN)
                            .setDescription("Send your order ID. The one you got in your email.")
                            .addField("**Note**", """
                                    Make sure you have your DMs turned on for the server! You can turn it back off after receiving your products!
                                    If you do not you can not receive your products!
                                    **How to on DMs for just this server:**
                                    Click on "Better Tea Shop" at the top -> Privacy Settings -> Turn On
                                    """, false);
                    event.replyEmbeds(orderEmbed.build()).queue();
                    channel.upsertPermissionOverride(member)
                            .setAllow(Permission.MESSAGE_SEND)
                            .setAllow(Permission.VIEW_CHANNEL)
                            .queue();

                // If there is an error with updating the ticket status then alert the user.
                }catch (SQLException e){
                    event.reply("There was an error with the request. Contact Mythik.").queue();
                    e.printStackTrace();
                }
                break;
            case "order-replacement":
                // Renaming the channel
                channel.getManager().setName(member.getUser().getName() + "-replacement");

                // Trying to update the ticket status
                try {
                    String setClaimQuery = "UPDATE Tickets SET Status = 'replacement' WHERE TicketID ='" + channel.getId() + "'";
                    statement.executeUpdate(setClaimQuery);
                    EmbedBuilder orderEmbed = new EmbedBuilder()
                            .setTitle("Replacements")
                            .setColor(Color.GREEN)
                            .setDescription("Send your order ID. The one you got in your email.")
                            .addField("**Note**", """
                                    Make sure you have your DMs turned on for the server! 
                                    If you do not you can not receive your replacements!
                                    **How to on DMs for just this server:**
                                    Click on "Better Tea Shop" at the top -> Privacy Settings -> Turn On
                                    """, false);
                    event.replyEmbeds(orderEmbed.build()).queue();
                    channel.upsertPermissionOverride(member)
                            .setAllow(Permission.MESSAGE_SEND)
                            .setAllow(Permission.VIEW_CHANNEL)
                            .queue();

                    // If there is an error with updating the ticket status then alert the user.
                }catch (SQLException e){
                    event.reply("There was an error with the request. Contact Mythik.").queue();
                    e.printStackTrace();
                }
                break;
            case "order-gen":

                break;
            case "not-answered-ticket":
                channel.upsertPermissionOverride(member)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
                event.reply("Please ask your question. A staff member will answer it soon.").queue();
                break;
            case "requirements-ready":
                channel.upsertPermissionOverride(member)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
                event.reply("Send the discord link or video link").queue();
                break;
        }
    }
}
