package BotCommands;

import Bot.BotProperty;
import CustomObjects.Embeds;
import CustomObjects.Response;
import Bot.SQLConnection;
import CustomObjects.CustomChannel;
import CustomObjects.CustomMember;
import CustomObjects.Modals;
import Shoppy.ShoppyConnection;
import Shoppy.ShoppyOrder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaffSlashCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        // Only accept commands from guilds
        if (!event.getChannelType().isGuild() || event.getMember() == null)
            return;
        Guild guild = event.getGuild();

        JDA jda = event.getJDA();

        Member sender = event.getMember();

        CustomChannel channel = new CustomChannel(jda, event.getTextChannel().getId());
        CustomMember guildOwner = new CustomMember(jda, "976956826472050689", guild.getId());

        Statement statement = SQLConnection.getStatement();

        BotProperty botProperty = new BotProperty();

        if (sender.getUser().isBot())
            return;
        if (sender.hasPermission(Permission.ADMINISTRATOR)) {

            switch (event.getName()) {
                case "configure_server" -> event.replyModal(Modals.CONFIGURE_MODAL).queue();

                // BAN COMMAND CONTROLLER
                case "ban" -> {
                    // Get the values from the arguments passed in through the slash command
                    CustomMember banTarget = new CustomMember(jda, Objects.requireNonNull(event.getOption("target_member")).getAsMember().getId(), guild.getId());
                    String banReason = Objects.requireNonNull(event.getOption("reason")).getAsString();
                    int delete_days = (int) Objects.requireNonNull(event.getOption("delete_days")).getAsLong();

                    // Acknowledge the event and alert that the user was banned
                    event.reply(banTarget.getMember().getAsMention() + " banned. **Reason:** " + banReason).queue();

                    // Ban the user
                    banTarget.ban(banReason, delete_days);
                }

                case "timeout" -> {
                    CustomMember timeoutTarget = new CustomMember(jda, Objects.requireNonNull(event.getOption("target_member")).getAsMember().getId(), guild.getId());
                    String timeoutReason = Objects.requireNonNull(event.getOption("reason")).getAsString();
                    int duration = Objects.requireNonNull(event.getOption("duration")).getAsInt();
                    String durationType = Objects.requireNonNull(event.getOption("duration_type")).getAsString();

                    try {
                        timeoutTarget.timeout(timeoutReason, duration, durationType);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                // KICK COMMAND CONTROLLER
                case "warn" -> {
                    // Get the values from the arguments passed in through the slash command
                    CustomMember warnTarget = new CustomMember(jda, event.getOption("target_member").getAsMember().getId(), guild.getId());
                    String warnReason = event.getOption("reason").getAsString();

                    // Acknowledge the event
                    event.reply(warnTarget.getMember().getAsMention() + " warned! **Reason:** " + warnReason).queue();

                    // Alert the user they got banned
                    warnTarget.sendPrivateMessage(Embeds.createPunishmentEmbed("WARNED", warnReason));

                }

                case "add_response" -> {
                    String triggerWord = event.getOption("trigger").getAsString();

                    String response = event.getOption("response").getAsString();
                    boolean deleteTriggerMsg = false;
                    boolean contains = false;

                    if (event.getOption("delete_trigger") != null)
                        deleteTriggerMsg = event.getOption("delete_trigger").getAsBoolean();
                    else if (event.getOption("contains") != null)
                        contains = event.getOption("delete_if_contains").getAsBoolean();

                    try {
                        // Populating response members
                        Response responseObj = new Response(deleteTriggerMsg, contains, response, triggerWord);

                        SQLConnection.insertResponse(guild.getId(), responseObj);

                        BotProperty.getResponseHashMap().put(triggerWord, responseObj);

                        // Checking if the response trigger word is already in the database
                    } catch (SQLIntegrityConstraintViolationException e) {
                        channel.getChannel().sendMessage("**Error: ** That trigger word is already used! Use another one!").queue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    event.reply("**Trigger Word:** " + triggerWord +
                            "**Delete Message:** " + deleteTriggerMsg +
                            "**Delete Message if it contains:** " + contains +
                            "**Response:** " + response).queue();
                }

                case "delete_response" -> {
                    String deleteWord = event.getOption("trigger_word").getAsString();
                    BotProperty.getResponseHashMap().remove(deleteWord);
                    String removeResponseQuery = "DELETE FROM Responses WHERE TriggerWord = '" + deleteWord + "'";
                    try {
                        statement.executeUpdate(removeResponseQuery);
                        event.reply("**Success:** Response successfully deleted").queue();
                    } catch (Exception e) {
                        event.reply("**FAILURE:** There was an error in deleting that triggerKey! Make sure it exist!").queue();
                        e.printStackTrace();
                    }
                }

                case "card_check" -> event.reply("Your order was marked as fraud. To verify you're the owner of the card we need to see the card with the last 4 digits of: " + event.getOption("last_four_digits").getAsString() + "\n" +
                                "You can blur everything else out we just need to see the last 4 digits. If this cannot be done, we can provide a full refund. Just let us know.\n" +
                                "Example: https://cdn.discordapp.com/attachments/859129620493369367/987782981684985896/IMG_6438.jpg")
                        .queue();

                // Opens the ticket so the target user can speak in it and view the ticket
                case "open_ticket" -> {
                    event.deferReply().queue();
                    Member member = event.getOption("target_user").getAsMember();
                    channel.openTicket(member);
                    event.getHook().sendMessage(member.getUser().getName() + " is allowed to speak").queue();
                }

                // CLOSE slash command to close a ticket and send a message to the user alerting the ticket has been closed
                case "close" -> {
                    event.reply("Deleting ticket...").queue();
                    try {
                        channel.close();
                    } catch (IllegalArgumentException e) {
                        event.reply("Can't close this channel because it is not a ticket channel!").queue();
                    }
                }

                // AUTO_NUKE slash command to auto-nuke a channel every given minutes
                case "auto_nuke" -> {
                    event.deferReply().queue();
                    event.getHook().sendMessage("Auto Nuke Starting...").queue();
                    final String[] channelId = {channel.getChannel().getId()};
                    Timer timer = new Timer();
                    int begin = 0;
                    int seconds = (int) event.getOption("minutes").getAsLong();
                    int timeInterval = seconds * 60000;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            String channelName = channel.getChannel().getName();
                            Category previousCategory = channel.getChannel().getParentCategory();
                            guild.getTextChannelById(channelId[0]).delete().queue();
                            guild.createTextChannel(channelName, previousCategory).queue(newChannel -> {
                                CustomChannel channel = new CustomChannel(jda, newChannel.getId());
                                channel.sendEmbed(Embeds.AUTO_NUKE);
                                channelId[0] = newChannel.getId();
                                System.out.println("[LOG] Channel Nuked");
                            });
                        }
                    }, begin, timeInterval);
                }

                // NUKE slash command to nuke a channel
                case "nuke" -> {
                    String channelName = channel.getChannel().getName();
                    Category previousCategory = channel.getChannel().getParentCategory();
                    channel.getChannel().delete().queue();
                    guild.createTextChannel(channelName, previousCategory).queue(newChannel -> {
                        newChannel.sendMessage("Channel has been nuked!").queue();
                    });
                }

                case "send_product" -> {
                    // Retrieving the required fields
                    CustomMember targetMember = new CustomMember(jda, Objects.requireNonNull(event.getOption("target_member")).getAsMember().getId(), guild.getId());
                    String orderId = Objects.requireNonNull(event.getOption("order_id")).getAsString();


                    if(!targetMember.getMember().getId().equalsIgnoreCase(event.getMember().getId())) {
                        try {
                            // Get the current time of this command to store when the product was sent
                            java.util.Date date = new java.util.Date();
                            long time = date.getTime();
                            SQLConnection.addOrder(orderId, targetMember.getMember().getId(), new Timestamp(time));

                            // Check if the product type is set
                            try {
                                String productType = event.getOption("account_type").getAsString();

                                /**
                                 * If it's set that means that sender is sending a personal order
                                 *
                                 * Get the information about the order including the amount and the product itself
                                 */
                                int amount = Objects.requireNonNull(event.getOption("amount")).getAsInt();
                                String product = SQLConnection.getProductByName(guild.getId(), productType, amount);

                                // Send the product to the owner and the customer
                                targetMember.sendProduct(orderId, product, productType);
                                guildOwner.sendProduct(orderId, product, productType);
                            }catch (NullPointerException e){

                                // If it's not that set means you can check with just the order id
                                targetMember.sendProduct(orderId);
                                guildOwner.sendProduct(orderId);
                            }
                            event.reply(targetMember.getMember().getAsMention() + " accounts have been sent. Check your DMs!").queue();
                        } catch (IOException | InterruptedException e) {
                            event.reply("**ERROR** Could not successfully sent product!").queue();
                            e.printStackTrace();
                        } catch (SQLIntegrityConstraintViolationException e) {
                            channel.getChannel().sendMessage("""
                                    That order has already been claimed or that order id has already been registered in the database
                                    
                                    BetterBot has blocked the ability to send these product. Mythik will manually have to review this.
                                    """).queue();
                        } catch (SQLException e) {
                            event.reply("**ERROR** Could not upload order to database").queue();
                            e.printStackTrace();
                        }
                    }else{
                        // Alert Mythik of a corrupt staff member
                        botProperty.corruptStaffAlert(event.getJDA(), guild, sender.getUser(), "SENDING ACCOUNTS TO HIMSELF **Order ID:** " + orderId);
                        event.reply("Corrupt Staff Member Detected! " + event.getMember().getUser().getAsMention()).queue();
                    }
                }

                // ORDER slash command to view a Shoppy order
                case "order" -> {
                    ShoppyConnection shoppyConnection = new ShoppyConnection();
                    String orderID = event.getOption("order_id").getAsString();
                    try {
                        ShoppyOrder order = shoppyConnection.getShoppyOrder(orderID);

                        event.replyEmbeds(order.sendOrderEmbed().build()).queue();
                    } catch (NullPointerException e){
                        event.getHook().sendMessage("That order id does not exist!").queue();
                        e.printStackTrace();
                    } catch (IOException | InterruptedException e ) {
                        event.getHook().sendMessage("**[ERROR]** There was an error running that command. Contact Mythik.").queue();
                        e.printStackTrace();
                    }
                }

                // DIRECTIONS_LIST slash command to view a list of all directions and their description
                case "directions_list" -> event.replyEmbeds(Embeds.DIRECTIONS.build()).queue();

                // DIRECTIONS slash command to view a set of directions for a specific command
                case "directions" -> {
                    String direction = event.getOption("direction_name").getAsString();

                    event.replyEmbeds(displayDirections(direction).build()).queue();
                }

                // ADDUSER slash command to add a user to the database
                case "add_user" -> {
                    if(SQLConnection.addDefaultUser(guild, event.getOption("member").getAsMember()))
                    {
                        event.reply("User successfully added into the database").queue();
                    }else{
                        event.reply("**[ERROR]** There was an error with adding that member to the database").queue();
                    }
                }
            }
        } else {
            event.reply("You don't have permissions to run that command").setEphemeral(true).queue();
        }
    }

    /**
     * Overridden method to auto-complete a slash command option
     *
     * @param event The event that will trigger the auto complete
     */
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event)
    {
        String[] directionArray = new String[]{"ClaimTicket", "SendOrder", "FraudulentCheck", "Replacements", "Unknown",
                "UnbannedProductIssue", "MinecraftMFAIssue", "ChatModeration", "GeneralQuestions"};

        if (event.getName().equals("directions") && event.getFocusedOption().getName().equals("direction_name")) {
            List<Command.Choice> options = Stream.of(directionArray)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }

    /**
     * Return the embed associated with the direction
     *
     * @param direction The direction to return the embed for
     * @return An embed that shows the direction
     */
    public EmbedBuilder displayDirections(String direction)
    {
        EmbedBuilder directionList = null;

        switch (direction)
        {
            case "ClaimTicket" -> {
                return Embeds.CLAIM_TICKET;
            }

            case "SendOrder" -> {
                return Embeds.SEND_ORDER;
            }

            case "FraudulentCheck" -> {
                return  Embeds.FRAUDULENT_CHECK;
            }

            case "Replacements" -> {
                return  Embeds.REPLACEMENTS;
            }

            case "Unknown" -> {
                return Embeds.UNKNOWN;
            }

            case "UNBANNEDPRODUCTISSUE" -> {
                return Embeds.UNBANNED_PRODUCT_ISSUE;
            }

            case "MinecraftMFAIssue" -> {
                return Embeds.MINECRAFT_MFA_ISSUE;
            }

            case "ChatModeration" -> {
                return Embeds.CHAT_MODERATION;
            }

            case "GeneralQuestions" -> {
                return Embeds.GENERAL_QUESTIONS;
            }
        }

        directionList.setColor(Color.BLACK);

        return directionList;
    }
}
