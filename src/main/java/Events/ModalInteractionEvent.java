package Events;

import Bot.BotProperty;
import Bot.SQLConnection;
import BotObjects.GuildObject;
import CustomObjects.CustomChannel;
import CustomObjects.CustomMember;
import Shoppy.ShoppyConnection;
import Shoppy.ShoppyOrder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ModalInteractionEvent extends ListenerAdapter {
    @Override
    public void onModalInteraction(@Nonnull net.dv8tion.jda.api.events.interaction.ModalInteractionEvent event)
    {
        event.deferReply().queue();

        // Declaring regular variables
        CustomChannel customChannel = new CustomChannel(event.getJDA(), event.getTextChannel().getId());

        TextChannel channel = event.getTextChannel();

        Guild guild = event.getGuild();

        JDA jda = event.getJDA();

        CustomMember member = new CustomMember(jda, event.getMember().getId(), guild.getId());

        // Checking if the modal is either for a replacement or claiming order
        if (event.getModalId().equals("claim-order-modal") || event.getModalId().equals("replacement-modal"))
        {
            // Getting the order ID
            String orderId = event.getValue("order-id").getAsString().strip();

            try {
                // Getting the order that connects to the order ID
                ShoppyOrder order = ShoppyConnection.getShoppyOrder(orderId);

                EmbedBuilder productDescriptionEmbed = order.sendOrderEmbed();

                // If the order is for claiming an order
                if (event.getModalId().equals("claim-order-modal")){

                    try {
                        // Insert the order into the database so the same order cannot be claimed twice
                        SQLConnection.addOrder(orderId, member.getMember().getId());

                        String product = SQLConnection.getProductByName(guild.getId(), order.getProduct().getTitle(), order.getQuantity());

                        member.sendProduct(order.getId(), product, order.getProduct().getTitle());

                        event.getHook().sendMessage("Accounts successfully sent! Check DMs " + member.getMember().getAsMention()).queue();
                    } catch (IOException | InterruptedException e) {
                        event.getHook().sendMessage("**[LOG]** There was an issue with sending the product").queue();
                        e.printStackTrace();
                    } catch (SQLIntegrityConstraintViolationException e) {
                        event.getHook().sendMessage(
                                """
                                        **[Fraud Detection]**
                                        Unable to send the product. The product was already claimed
                                        """).queue();
                        e.printStackTrace();
                    } catch (SQLException e){
                        event.getHook().sendMessage("**[ERROR]** There was an issue with registering the order to the database").queue();
                        e.printStackTrace();
                    }

                    productDescriptionEmbed
                            .addField("**User**", member.getMember().getAsMention(), false)
                            .addField("**What to do now**", """
                                    First, thank you for choosing Better Alts!
                                    
                                    Thank you for submitting your information. A staff will be with you shortly!
                                    
                                    We have disabled your ability to talk. Don't worry! This is to ensure there is no confusion between the customer and staff!
                                    """, false);

                    event.getHook().editOriginalEmbeds(productDescriptionEmbed.build()).queue();

                }else if(event.getModalId().equals("replacement-modal")){
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime claimedDate = null;

                    try {
                        ResultSet resultSet = SQLConnection.getOrder(orderId);
                        while(resultSet.next())
                        {
                            claimedDate = resultSet.getTimestamp("ClaimedDate").toLocalDateTime();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    
                    if((now.toEpochSecond(ZoneOffset.UTC) - claimedDate.toEpochSecond(ZoneOffset.UTC) > 86400) && claimedDate != null)
                    {
                        event.getHook().sendMessage("""
                        Thank you for submitting your information and choosing Better Alts.
                        
                        Unfortunately, the maximum warranty of 24 hours has expired and replacements or a refund can not be given for this order.
                        
                        The ticket will be closed soon.
                        """).queue();
                    }else{
                        String replacementAmount = event.getValue("replacement-amount").getAsString();
                        String replacementReason = event.getValue("replacement-reason").getAsString();


                        productDescriptionEmbed
                                .addField("**User**", member.getMember().getAsMention(), false)
                                .addField("**Replacement Amount**", replacementAmount, false)
                                .addField("**Replacement Reason**", replacementReason, false)
                                .addField("**What to do now**", """
                                We're sorry to hear that you are having an issue with your purchase.
                                
                                Thank you so much for submitting your information. A staff member will be with you shortly!
                                """, false);
                        event.getHook().editOriginalEmbeds(productDescriptionEmbed.build()).queue();
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException | NullPointerException e){
                event.getHook().sendMessage("That is not a valid order ID. Make sure you copy and paste only and the full order ID!").queue();
                e.printStackTrace();
            }
        }else{
            switch (event.getModalId())
            {
                case "purchase-modal" -> {
                    String purchaseItemName = event.getValue("purchase-item-name").getAsString();
                    String purchaseItemAmount = event.getValue("purchase-item-amount").getAsString();
                    String purchasePaymentMethod = event.getValue("purchase-payment-method").getAsString();

                    EmbedBuilder purchaseEmbed = new EmbedBuilder()
                            .setTitle("**Better Alts Purchase**")
                            .setDescription("The details of what you requested to purchase. If there are any incorrect details please re-submit the form!")
                            .setColor(Color.blue)
                            .addField("**Item**", purchaseItemName, false)
                            .addField("**Amount**", purchaseItemAmount, false)
                            .addField("**Payment Method**", purchasePaymentMethod, false);

                    event.getHook().editOriginalEmbeds(purchaseEmbed.build())
                            .setActionRow(Button.danger("close-ticket", "Close"))
                            .queue();
                    if(!purchasePaymentMethod.toLowerCase().contains("cashapp") && (purchasePaymentMethod.toLowerCase().contains("pp") || purchasePaymentMethod.toLowerCase().contains("paypal"))){
                        Role paypalExchangerRole = guild.getRoleById(938905340001542235L);

                        channel.upsertPermissionOverride(paypalExchangerRole)
                                .setAllow(Permission.MESSAGE_SEND)
                                .setAllow(Permission.VIEW_CHANNEL)
                                .queue();

                        EmbedBuilder paypalClaim = new EmbedBuilder()
                                .setTitle("**PayPal Order**")
                                .setDescription("A PayPaler will claim this order soon. Please be patient!");
                        channel.sendMessageEmbeds(paypalClaim.build()).setActionRow(
                                Button.primary("paypal-claim-order", "PayPal Claim")
                        ).queue();

                        channel.sendMessage(guild.getRoleById("938905340001542235").getAsMention() + " there is a PayPal order!").queue();
                    }else if(purchasePaymentMethod.toLowerCase().contains("steam")){
                        channel.sendMessage(member.getMember().getAsMention() + " we do not accept steam as a payment method! Close the ticket when you see this.").queue();
                    }else if(purchasePaymentMethod.toLowerCase().contains("paysafe")){
                        channel.sendMessage(member.getMember().getAsMention() + " we do not accept paysafe card as a payment method! Close the ticket when you see this.").queue();
                    }
                }

                case "sponsorship-partnership" -> {
                    String email = event.getValue("email").getAsString();
                    String channelLink = event.getValue("channel-link").getAsString();
                    String discordLink = event.getValue("discord-link").getAsString();
                    String sponsorshipService = event.getValue("sponsorship-service").getAsString();
                    String sponsorshipPayment = event.getValue("sponsorship-payment").getAsString();

                    EmbedBuilder partnershipEmbed = new EmbedBuilder()
                            .setTitle("**Sponsorship/Partnership Application**")
                            .setAuthor(member.getMember().getUser().getAsTag())
                            .setColor(Color.CYAN)
                            .addField("**User**", member.getMember().getAsMention(), false)
                            .addField("**Email**", email, false)
                            .addField("**Youtube Channel Link**", channelLink, false)
                            .addField("**Discord Channel Link**", discordLink, false)
                            .addField("**Service**", sponsorshipService, false)
                            .addField("**Payment**", sponsorshipPayment, false);
                    event.getHook().editOriginalEmbeds(partnershipEmbed.build()).queue();
                }

                case "configure-server-modal" -> {
                    String prefix = event.getValue("configure-prefix").getAsString();
                    String serverOwnerId = event.getValue("configure-server-owner").getAsString();

                    try {
                        GuildObject configuredGuild = new GuildObject(
                                prefix,
                                0,
                                null,
                                null,
                                serverOwnerId,
                                null,
                                null,
                                null,
                                null,
                                null,
                                guild.getId());

                        SQLConnection.updateGuildInfo(configuredGuild);

                        BotProperty.guildsHashMap.put(guild.getId(), configuredGuild);
                        event.getHook().sendMessage("Successfully configured server").queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                case "configure-ticket-modal" -> {
                    String ticketCategoryId = event.getValue("configure-ticket-category").getAsString();
                    int ticketLimit = Integer.valueOf(event.getValue("configure-ticket-limit").getAsString());

                    try {
                        GuildObject configuredGuild = new GuildObject(
                                null,
                                ticketLimit,
                                null,
                                ticketCategoryId,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                guild.getId());

                        SQLConnection.updateGuildInfo(configuredGuild);

                        BotProperty.guildsHashMap.put(guild.getId(), configuredGuild);
                        event.getHook().sendMessage("Successfully configured server").queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                case "configure-roles-modal" -> {
                    String staffRoleId = event.getValue("configure-staff-role").getAsString();
                    String customerRoleId = event.getValue("configure-customer-role").getAsString();
                    String memberRoleId = event.getValue("configure-member-role").getAsString();

                    try {
                        GuildObject configuredGuild = new GuildObject(
                                null,
                                0,
                                null,
                                null,
                                staffRoleId,
                                null,
                                customerRoleId,
                                memberRoleId,
                                null,
                                null,
                                guild.getId());

                        SQLConnection.updateGuildInfo(configuredGuild);

                        event.getHook().sendMessage("Successfully configured server").queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                case "configure-channels-modal" -> {
                    String logChannelId = event.getValue("configure-log-channel").getAsString();
                    String joinChannelId = event.getValue("configure-join-channel").getAsString();
                    String leaveChannelId = event.getValue("configure-leave-channel").getAsString();

                    try {
                        GuildObject configuredGuild = new GuildObject(
                                null,
                                0,
                                null,
                                null,
                                null,
                                logChannelId,
                                null,
                                null,
                                joinChannelId,
                                leaveChannelId,
                                guild.getId());

                        SQLConnection.updateGuildInfo(configuredGuild);

                        BotProperty.guildsHashMap.put(guild.getId(), configuredGuild);
                        event.getHook().sendMessage("Successfully configured server").queue();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
