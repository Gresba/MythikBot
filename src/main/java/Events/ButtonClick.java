package Events;

import Bot.BotProperty;
import BotObjects.GuildObject;
import CustomObjects.Embeds;
import Bot.SQLConnection;
import CustomObjects.CustomChannel;
import CustomObjects.Modals;
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

public class ButtonClick extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        // Checking if the member or guild is null
        if (event.getMember() == null || event.getGuild() == null)
            return;
        Member member = event.getMember();
        Guild guild = event.getGuild();

        GuildObject guildObject = BotProperty.guildsHashMap.get(guild.getId());
        System.out.println(guildObject);

        CustomChannel customChannel = new CustomChannel(event.getJDA(), event.getTextChannel().getId());

        switch (event.getComponentId()) {

            // CREATE TICKET button for tickets
            case "create-ticket" ->

                // Creates a new ticket channel in the category configured
                guild.createTextChannel(member.getUser().getName(), guild.getCategoryById(guildObject.getTicketCategoryId())).queue(
                    ticketChannel -> {
                        try {
                            customChannel.setChannel(ticketChannel.getId());

                            // Set the channel topic to the user's id for future uses
                            customChannel.getChannel().getManager().setTopic(member.getId()).queue();

                            // Uses method in SQLConnection class to insert a new ticket record into the database
                            SQLConnection.insertTicket(ticketChannel.getId(), member.getId());

                            // Setting permissions to allow ticket creator to view channel but can't send messages yet
                            ticketChannel.upsertPermissionOverride(member)
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
                customChannel.muteTicket(member);
            }

            // PARTNERSHIP button for tickets
            case "partnership-ticket" -> event.replyModal(Modals.PARTNERSHIP_MODAL).queue();

            // PURCHASE button for tickets
            case "purchase-ticket" -> {

                // Show the modal to the client
                event.replyModal(Modals.PURCHASE_MODAL).queue();
                customChannel.openTicket(member);
            }

            // GENERAL QUESTIONS button for tickets
            case "general-ticket" -> event.editMessageEmbeds(Embeds.FAQ.build())
                    .setActionRow(
                            Button.primary("not-answered-ticket", "Question Not Answered"),
                            Button.danger("close-ticket", "Close")
                    ).queue();

            // CLOSE TICKET button for tickets
            case "close-ticket" -> {
                event.reply("Deleting Ticket...").queue();
                customChannel.close();
            }

            // ORDER CLAIM button for tickets
            case "order-claim" -> event.replyModal(Modals.CLAIM_ORDER).queue();

            case "order-replacement" -> {
                event.replyModal(Modals.REPLACEMENT_MODAL).queue();

                // Allow user to type to send their order ID
                customChannel.muteTicket(member);
            }

            case "not-answered-ticket" -> // Ask the user if their ticket is related to an order
                    event.replyEmbeds(Embeds.ORDER_RELATED.build())
                            .addActionRow(
                                    Button.primary("yes", "YES"),
                                    Button.secondary("no", "NO")
                            ).queue();

            case "yes" -> event.reply("Close this ticket and created a new one for \"Order\" or your warranty may expire").queue();

            case "no" -> {
                customChannel.openTicket(member);

                event.reply("""
                        Please ask your ticket. Be as **descriptive** as possible and explain the question/issue as best as you can.
                                        
                        Staff members usually only look at tickets once a day so if you aren't descriptive you will have to wait until the next day to receive a response.
                        """).queue();
            }

            case "paypal-claim-order" -> {
                Role paypalRole = guild.getRoleById("938905340001542235");

                customChannel.openTicket(member);

                if (member.getRoles().contains(paypalRole)) {
                    customChannel.muteTicket(paypalRole);
                    event.reply(guild.getMemberById(customChannel.getChannel().getTopic()).getAsMention() + ", " + event.getMember().getAsMention() + " will be in charge of your PayPal order. Talk to him in this ticket!").queue();
                } else {
                    event.reply("You are not a PayPal exchanger so click that button does not do anything!").queue();
                }
            }

            // Add the member role to a user
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
                if (member.hasPermission(Permission.ADMINISTRATOR))
                    event.editMessageEmbeds(Embeds.STAFF_HELP.build()).queue();
                else
                    event.reply("You do not have permissions to view this!").setEphemeral(true).queue();
            }

            case "user-command-help" -> event.editMessageEmbeds(Embeds.USER_HELP.build()).queue();

            case "customer-commands-help" -> event.editMessageEmbeds(Embeds.CUSTOMER_HELP.build()).queue();

            case "faq-help" -> event.editMessageEmbeds(Embeds.FAQ.build()).queue();
        }
    }
}
