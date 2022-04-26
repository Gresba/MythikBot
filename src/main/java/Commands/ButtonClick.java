package Commands;

import Bot.Embeds;
import Bot.SQLConnection;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class ButtonClick extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if(event.getMember() == null || event.getGuild() == null)
            return;
        Member member = event.getMember();
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        Statement statement = SQLConnection.getStatement();

        switch (event.getComponentId()) {

            // CREATE TICKET button for tickets
            case "create-ticket" ->
                    // Creates a new ticket channel
                    guild.createTextChannel(member.getUser().getName(), guild.getCategoryById("930157671896731649")).queue(
                            ticketChannel -> {
                                try {
                                    // Set the channel topic to the username for future uses
                                    ticketChannel.getManager().setTopic(member.getId()).queue();

                                    // Create query with
                                    String insertQuery = "INSERT INTO Tickets VALUES ('" + ticketChannel.getId() + "', '" + member.getId() + "')";
                                    statement.executeUpdate(insertQuery);

                                    // Setting permissions to allow ticket creator to view channel but can't send messages yet
                                    ticketChannel.putPermissionOverride(member)
                                            .setDeny(Permission.MESSAGE_SEND)
                                            .setAllow(Permission.VIEW_CHANNEL)
                                            .queue();

                                    // Adding the choices
                                    ticketChannel.sendMessageEmbeds(Embeds.TICKETREASON.getEmbed().build()).setActionRow(
                                            Button.primary("order-ticket", "Order"),
                                            Button.success("partnership-ticket", "Partnership"),
                                            Button.primary("purchase-ticket", "Purchase"),
                                            Button.success("general-ticket", "General"),
                                            Button.danger("close-ticket", "Close")
                                    ).queue();

                                    // Tell the user that a ticket has successfully been created
                                    event.reply("Ticket successfully created! " + ticketChannel.getAsMention()).setEphemeral(true).queue();

                                // Checks if the user already has a ticket
                                } catch (SQLIntegrityConstraintViolationException e) {

                                    // If the user does then tell them and delete the ticket
                                    event.reply("You can't create anymore tickets. Close your old one first.").setEphemeral(true).queue();
                                    ticketChannel.delete().queue();
                                } catch (SQLException e) {
                                    event.reply("There was an issue with creating your ticket. Contact Mythik.").setEphemeral(true).queue();
                                    ticketChannel.delete().queue();
                                    e.printStackTrace();
                                }
                            }
                    );

            // ORDER button for tickets
            case "order-ticket" -> {
                event.editMessageEmbeds(Embeds.CHOOSEORDERREASON.getEmbed().build())
                        .setActionRow(
                                Button.primary("order-claim", "Claim Order"),
                                Button.success("order-replacement", "Replacement"),
                                Button.danger("close-ticket", "Close")
                        ).queue();
                channel.upsertPermissionOverride(member)
                        .setDeny(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
            }

            // PARTNERSHIP button for tickets
            case "partnership-ticket" -> {
                TextInput email = TextInput.create("email", "Email", TextInputStyle.SHORT)
                        .setPlaceholder("Enter your E-mail")
                        .setRequired(true)
                        .build();
                TextInput channelLink = TextInput.create("channel-link", "Channel Link", TextInputStyle.SHORT)
                        .setPlaceholder("Your youtube channel link. Put N/A if none")
                        .setRequired(true)
                        .build();
                TextInput discordLink = TextInput.create("discord-link", "Discord Link", TextInputStyle.SHORT)
                        .setPlaceholder("Your discord channel invite link. Put N/A if none")
                        .setRequired(true)
                        .build();
                Modal modal = Modal.create("sponsorship-partnership", "Sponsorship/Partnership")
                        .addActionRows(
                                ActionRow.of(email),
                                ActionRow.of(channelLink),
                                ActionRow.of(discordLink))
                        .build();
                event.replyModal(modal).queue();
            }

            // PURCHASE button for tickets
            case "purchase-ticket" -> {

                // Input to ask for the product they want to purchase
                TextInput purchaseItemName = TextInput.create("purchase-item-name", "Item Name", TextInputStyle.SHORT)
                        .setPlaceholder("What product do you want to purchase?")
                        .setRequired(true)
                        .build();

                // Input to ask for the amount they want to purchase
                TextInput purchaseItemAmount = TextInput.create("purchase-item-amount", "Item Amount", TextInputStyle.SHORT)
                        .setPlaceholder("How many do you need?")
                        .setRequired(true)
                        .build();

                // Input to ask for the payment method they will be using
                TextInput paymentMethod = TextInput.create("purchase-payment-method", "Payment Method", TextInputStyle.SHORT)
                        .setPlaceholder("Please enter your payment method")
                        .setRequired(true)
                        .build();

                // Create the modal and add the TextInputs
                Modal purchaseModal = Modal.create("purchase-modal", "Purchase Item")
                        .addActionRows(
                                ActionRow.of(purchaseItemName),
                                ActionRow.of(purchaseItemAmount),
                                ActionRow.of(paymentMethod))
                        .build();

                // Show the modal to the client
                event.replyModal(purchaseModal).queue();
                channel.upsertPermissionOverride(member)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
            }

            // GENERAL QUESTIONS button for tickets
            case "general-ticket" -> event.editMessageEmbeds(Embeds.FAQ.getEmbed().build())
                    .setActionRow(
                            Button.primary("not-answered-ticket", "Question Not Answered"),
                            Button.danger("close-ticket", "Close")
                    ).queue();

            // CLOSE TICKET button for tickets
            case "close-ticket" -> {
                event.reply("Deleting Ticket...").queue();
                Embeds.sendEmbed(Embeds.TICKETCLOSED.getEmbed(), guild.getMemberById(channel.getTopic()), false);
                channel.delete().queue();
            }

            // ORDER CLAIM button for tickets
            case "order-claim" -> {
                TextInput claimOrderID = TextInput.create("order-id", "Order ID", TextInputStyle.SHORT)
                        .setPlaceholder("Ex. ha6d6876-4875-4c63-883e-036c55b0b338")
                        .setRequired(true)
                        .setMinLength(36)
                        .setMaxLength(36)
                        .build();
                Modal orderClaimModal = Modal.create("claim-order-modal", "Claim Order")
                        .addActionRows(ActionRow.of(claimOrderID))
                        .build();
                event.replyModal(orderClaimModal).queue();

                // Allow user to type to send their order ID
                channel.upsertPermissionOverride(member)
                        .setDeny(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
            }
            case "order-replacement" -> {
                TextInput replacementOrderID = TextInput.create("order-id", "Order ID", TextInputStyle.SHORT)
                        .setPlaceholder("Enter your Order ID")
                        .setRequired(true)
                        .setMinLength(36)
                        .setMaxLength(36)
                        .build();
                TextInput replacementAmount = TextInput.create("replacement-amount", "Replacement Amount", TextInputStyle.SHORT)
                        .setPlaceholder("Amount of replacements you need")
                        .setRequired(true)
                        .setMinLength(1)
                        .setMaxLength(3)
                        .build();
                TextInput replacementReason = TextInput.create("replacement-reason", "Replacement Reason", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Reason for replacements/What is wrong with your product")
                        .setRequired(true)
                        .setMinLength(1)
                        .build();
                Modal replacementModal = Modal.create("replacement-modal", "Replacements")
                        .addActionRows(
                                ActionRow.of(replacementOrderID),
                                ActionRow.of(replacementAmount),
                                ActionRow.of(replacementReason)
                        )
                        .build();
                event.replyModal(replacementModal).queue();

                // Allow user to type to send their order ID
                channel.upsertPermissionOverride(member)
                        .setDeny(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
            }

            case "not-answered-ticket" -> {
                channel.upsertPermissionOverride(member)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
                event.reply("Please ask your question. A staff member will try their best to answer it soon.").queue();
            }

            case "requirements-ready" -> {
                channel.upsertPermissionOverride(member)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
                event.reply("Send the discord link or video link. Make sure this link is **NOT** your channel. I want the video itself with our sponsorship message.").queue();
            }

            case "paypal-claim-order" -> {
                Role paypalerRole = guild.getRoleById("938905340001542235");

                if(member.getRoles().contains(paypalerRole)) {
                    channel.upsertPermissionOverride(event.getMember())
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();

                    channel.upsertPermissionOverride(paypalerRole)
                        .setDeny(Permission.VIEW_CHANNEL)
                        .queue();
                    event.reply(guild.getMemberById(channel.getTopic()).getAsMention() + ", " + event.getMember().getAsMention() + " will be in charge of your PayPal order. Talk to him in this ticket!").queue();
                }else{
                    event.reply("You are not a PayPal exchanger so click that button does not do anything!").queue();
                }
                channel.upsertPermissionOverride(member)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();

            }
        }
    }
}
