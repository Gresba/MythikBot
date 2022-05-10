package Events;

import Bot.SQLConnection;
import CustomObjects.CustomChannel;
import CustomObjects.CustomMember;
import Shoppy.ShoppyConnection;
import Shoppy.ShoppyOrder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

import static Bot.SQLConnection.getStatement;

public class ModalInteractionEvent extends ListenerAdapter {
    @Override
    public void onModalInteraction(@Nonnull net.dv8tion.jda.api.events.interaction.ModalInteractionEvent event)
    {
        event.deferReply().queue();

        // Declaring regular variables
        Statement statement = getStatement();

        CustomChannel customChannel = new CustomChannel(event.getJDA(), event.getTextChannel().getId());

        TextChannel channel = event.getTextChannel();

        Guild guild = event.getGuild();

        JDA jda = event.getJDA();

        CustomMember member = new CustomMember(jda, event.getMember().getId(), guild.getId());
        CustomMember guildOwner = new CustomMember(jda, "639094715605581852", guild.getId());

        // Checking if the modal is either for a replacement or claiming order
        if (event.getModalId().equals("claim-order-modal") || event.getModalId().equals("replacement-modal"))
        {
            // Getting the order ID
            String orderId = event.getValue("order-id").getAsString().strip();

            // Connecting to shoppy
            ShoppyConnection shoppyConnection = new ShoppyConnection();
            try {
                // Getting the order that connects to the order ID
                ShoppyOrder order = shoppyConnection.getShoppyOrder(orderId);

                EmbedBuilder productDescriptionEmbed = order.sendOrderEmbed();

                // If the order is for claiming an order
                if (event.getModalId().equals("claim-order-modal")){

                        // If the payment method is crypto then just send the product
//                        if (!order.requiresVerification(member, order.getGateway())) {
                            try {
                                java.util.Date date = new java.util.Date();
                                java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                                // Insert the order into the database so the same order cannot be claimed twice
                                PreparedStatement insertOrder = statement.getConnection().prepareStatement("INSERT INTO Orders (OrderID, MemberID, ClaimedDate) VALUES (?, ?, ?)");
                                insertOrder.setString(1, orderId);
                                insertOrder.setString(2, member.getMember().getId());
                                insertOrder.setDate(3, sqlDate);
                                insertOrder.executeUpdate();

                                String accounts = SQLConnection.getProductDetails(orderId, guild.getId(), 0);

                                member.sendProduct(orderId, accounts);
                                guildOwner.sendProduct(orderId, accounts);

                                event.getHook().sendMessage("Accounts successfully sent! Check DMs " + member.getMember().getAsMention()).queue();
                            } catch (IOException | InterruptedException e) {
                                channel.sendMessage("**[LOG]** There was an issue with sending the product").queue();
                                e.printStackTrace();
                            } catch (SQLIntegrityConstraintViolationException e) {
                                channel.sendMessage(
                                        """
                                                **[Fraud Detection]** 
                                                Unable to send the product. The product was already claimed
                                                """).queue();
                                e.printStackTrace();
                            } catch (SQLException e){
                                event.reply("**[ERROR]** There was an issue with registering the order to the database").queue();
                                e.printStackTrace();
                            }
//                        }

                        productDescriptionEmbed
                                .addField("**User**", member.getMember().getAsMention(), false)
                                .addField("**What to do now**", """
                                        First, thank you for choosing Better Alts!
                                        
                                        Thank you for submitting your information. A staff will be with you shortly!
                                        
                                        We have disabled your ability to talk. Don't worry! This is to ensure there is no confusion between the customer and staff!
                                        """, false);

                        event.getHook().editOriginalEmbeds(productDescriptionEmbed.build()).queue();

                }else if(event.getModalId().equals("replacement-modal")){
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                event.getHook().sendMessage("That is not a valid order ID. Please put in just your order id.").queue();
            }
        }else if(event.getModalId().equals("purchase-modal")){
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

            if(purchasePaymentMethod.toLowerCase().contains("pp") || purchasePaymentMethod.toLowerCase().contains("paypal"))
            {
                Role paypalExchangerRole = guild.getRoleById(938905340001542235l);

                channel.upsertPermissionOverride(paypalExchangerRole)
                        .setAllow(Permission.MESSAGE_SEND)
                        .setAllow(Permission.VIEW_CHANNEL)
                        .queue();

                EmbedBuilder paypalClaim = new EmbedBuilder()
                        .setTitle("**PayPal Order**")
                                .setDescription("A paypaler will claim this order soon. Please be patient!");
                channel.sendMessageEmbeds(paypalClaim.build()).setActionRow(
                        Button.primary("paypal-claim-order", "PayPal Claim")
                ).queue();

                channel.sendMessage(guild.getRoleById("938905340001542235").getAsMention() + " there is a PayPal order!").queue();
            }else if(purchasePaymentMethod.toLowerCase().contains("steam")){
                channel.sendMessage(member.getMember().getAsMention() + " we do not accept steam as a payment method! Close the ticket when you see this.").queue();
            }else if(purchasePaymentMethod.toLowerCase().contains("paysafe")){
                channel.sendMessage(member.getMember().getAsMention() + " we do not accept paysafe card as a payment method! Close the ticket when you see this.").queue();
            }
        }else if(event.getModalId().equals("sponsorship-partnership")){
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
    }
}
