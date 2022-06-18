package Events;

import CustomObjects.Embeds;
import Bot.SQLConnection;
import CustomObjects.CustomChannel;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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

        // Checking if the member or guild is null
        if(event.getMember() == null || event.getGuild() == null)
            return;
        Member member = event.getMember();
        Guild guild = event.getGuild();
        Statement statement = SQLConnection.getStatement();

        CustomChannel customChannel = new CustomChannel(event.getJDA(), event.getTextChannel().getId());

        switch (event.getComponentId()) {

            // CREATE TICKET button for tickets
            case "create-ticket" ->
                // Creates a new ticket channel
                guild.createTextChannel(member.getUser().getName(), guild.getCategoryById("930157671896731649")).queue(
                    ticketChannel -> {
                        try {
                            customChannel.setChannel(ticketChannel.getId());

                            // Set the channel topic to the user's id for future uses
                            customChannel.getChannel().getManager().setTopic(member.getId()).queue();

                            // Create query with
                            String insertQuery = "INSERT INTO Tickets VALUES ('" + ticketChannel.getId() + "', '" + member.getId() + "')";
                            statement.executeUpdate(insertQuery);

                            // Setting permissions to allow ticket creator to view channel but can't send messages yet
                            ticketChannel.putPermissionOverride(member)
                                    .setDeny(Permission.MESSAGE_SEND)
                                    .setAllow(Permission.VIEW_CHANNEL)
                                    .queue();

                            // Adding the choices
                            ticketChannel.sendMessageEmbeds(Embeds.TICKET_REASON.build()).setActionRow(
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
                event.editMessageEmbeds(Embeds.CHOOSE_ORDER_REASON.build())
                        .setActionRow(
                                Button.primary("order-claim", "Claim Order"),
                                Button.success("order-replacement", "Replacement"),
                                Button.danger("close-ticket", "Close")
                        ).queue();
                customChannel.getChannel().upsertPermissionOverride(member)
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
                TextInput sponsorshipService = TextInput.create("sponsorship-service", "Service", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("What can you provide for BetterAlts?")
                        .setRequired(true)
                        .build();
                TextInput sponsorshipPayment = TextInput.create("sponsorship-payment", "Payments", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("What do you want for your service?")
                        .setRequired(true)
                        .build();
                Modal modal = Modal.create("sponsorship-partnership", "Sponsorship/Partnership")
                        .addActionRows(
                                ActionRow.of(email),
                                ActionRow.of(channelLink),
                                ActionRow.of(discordLink),
                                ActionRow.of(sponsorshipService),
                                ActionRow.of(sponsorshipPayment))
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
                customChannel.getChannel().upsertPermissionOverride(member)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
            }

            // GENERAL QUESTIONS button for tickets
            case "general-ticket" -> {
                event.editMessageEmbeds(Embeds.FAQ.build())
                        .setActionRow(
                                Button.primary("not-answered-ticket", "Question Not Answered"),
                                Button.danger("close-ticket", "Close")
                        ).queue();
            }

            // CLOSE TICKET button for tickets
            case "close-ticket" -> {
                event.reply("Deleting Ticket...").queue();
                customChannel.close();
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
                customChannel.getChannel().upsertPermissionOverride(member)
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
                        .setPlaceholder("Reason for replacements/What is wrong with your product? Explain in details")
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
                customChannel.getChannel().upsertPermissionOverride(member)
                        .setDeny(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
            }


            case "not-answered-ticket" -> {

                // Ask the user if their ticket is related to an order
                event.replyEmbeds(Embeds.ORDER_RELATED.build())
                        .addActionRow(
                                Button.primary("yes", "YES"),
                                Button.primary("no", "NO")
                        );
            }

            case "yes" -> {
                event.reply("Close this ticket and created a new one for \"Order\" or your warranty may expire").queue();
            }

            case "no" -> {
                event.reply("""
            Please ask your ticket. Be as **descriptive** as possible and explain the question/issue as best as you can.
            
            Staff members usually only look at tickets once a day so if you aren't descriptive you will have to wait until the next day to receive a response.
            """).queue();
                customChannel.getChannel().upsertPermissionOverride(member)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
            }

            case "paypal-claim-order" -> {
                Role paypalerRole = guild.getRoleById("938905340001542235");

                if (member.getRoles().contains(paypalerRole)) {
                    customChannel.getChannel().upsertPermissionOverride(event.getMember())
                            .setAllow(Permission.MESSAGE_SEND)
                            .setAllow(Permission.VIEW_CHANNEL)
                            .queue();

                    customChannel.getChannel().upsertPermissionOverride(paypalerRole)
                            .setDeny(Permission.VIEW_CHANNEL)
                            .queue();
                    event.reply(guild.getMemberById(customChannel.getChannel().getTopic()).getAsMention() + ", " + event.getMember().getAsMention() + " will be in charge of your PayPal order. Talk to him in this ticket!").queue();
                } else {
                    event.reply("You are not a PayPal exchanger so click that button does not do anything!").queue();
                }
                customChannel.getChannel().upsertPermissionOverride(member)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();
            }

            // Button add the member role to a user
            case "verify" -> {
                guild.addRoleToMember(event.getMember(), event.getGuild().getRoleById("945973060027166750")).queue();

                event.reply("You have been verified").setEphemeral(true).queue();
            }

            // Add the giveaway role to a user
            case "add-giveaway-role" -> {
                // Add the giveaway role to the user
                guild.addRoleToMember(event.getMember(), event.getGuild().getRoleById("934630629088321646")).queue();

                event.reply("You have received the giveaway role!").setEphemeral(true).queue();
            }

            // Remove the giveaway role to a user
            case "remove-giveaway-role" -> {
                // Remove the giveaway role from the user
                guild.removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("934630629088321646")).queue();

                event.reply("You giveaway role was removed!").setEphemeral(true).queue();
            }

            //
            case "staff-commands-help" -> {
                if(member.hasPermission(Permission.ADMINISTRATOR))
                    event.editMessageEmbeds(Embeds.STAFF_HELP.build()).queue();
                else
                    event.reply("You do not have permissions to view this!").setEphemeral(true).queue();
            }

            case "user-command-help" -> {
                event.editMessageEmbeds(Embeds.USER_HELP.build()).queue();
            }

            case "customer-commands-help" -> {
                event.editMessageEmbeds(Embeds.CUSTOMER_HELP.build()).queue();
            }

            case "faq-help" -> {
                event.editMessageEmbeds(Embeds.FAQ.build()).queue();
            }
        }
    }
}
