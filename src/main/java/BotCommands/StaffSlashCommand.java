package BotCommands;

import Bot.BotProperty;
import Bot.Embeds;
import Bot.Response;
import Bot.SQLConnection;
import CustomObjects.CustomMember;
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

        TextChannel channel = event.getTextChannel();

        Statement statement = SQLConnection.getStatement();

        BotProperty botProperty = new BotProperty();

        if (sender.getUser().isBot())
            return;
        if (sender.hasPermission(Permission.ADMINISTRATOR)) {
            switch (event.getName()) {

                // BAN COMMAND CONTROLLER
                case "ban" -> {
                    // Get the values from the arguments passed in through the slash command
                    CustomMember banTarget = new CustomMember(jda, Objects.requireNonNull(event.getOption("user")).getAsMember().getId(), guild.getId());
                    String banReason = Objects.requireNonNull(event.getOption("reason")).getAsString();
                    int delete_days = (int) Objects.requireNonNull(event.getOption("delete_days")).getAsLong();

                    // Acknowledge the event and alert that the user was banned
                    event.reply(banTarget.getMember().getAsMention() + " banned. **Reason:** " + banReason).queue();

                    // Ban the user
                    banTarget.ban(banReason, delete_days);
                }

                // MUTE COMMAND CONTROLLER
                case "mute" -> {

                    // Get the values from the arguments passed in through the slash command
                    CustomMember muteTarget = new CustomMember(jda, Objects.requireNonNull(event.getOption("user")).getAsMember().getId(), guild.getId());
                    String muteReason = Objects.requireNonNull(event.getOption("reason")).getAsString();

                    // Mute the user
                    try {
                        muteTarget.mute(muteReason);
                        event.getHook().sendMessage(muteTarget.getMember().getAsMention() + " muted. **Reason:** " + muteReason).queue();
                    } catch (SQLException e) {
                        event.getHook().sendMessage("**[Error]** There was an issue with adding that mute to the database").queue();
                        e.printStackTrace();
                    }
                }

                // UNMUTE COMMAND CONTROLLER
                case "unmute" -> {
                    CustomMember muteTarget = new CustomMember(jda, Objects.requireNonNull(event.getOption("user")).getAsMember().getId(), guild.getId());

                    try {
                        muteTarget.mute(null);
                        event.getHook().sendMessage(muteTarget.getMember().getAsMention() + " unmuted.").queue();
                    } catch (SQLException e) {
                        event.getHook().sendMessage("**[Error]** There was an issue with updating the unmute in the database").queue();
                        e.printStackTrace();
                    }
                }

                // KICK COMMAND CONTROLLER
                case "kick" -> {
                    // Get the values from the arguments passed in through the slash command
                    CustomMember kickTarget = new CustomMember(jda, event.getOption("user").getAsMember().getId(), guild.getId());
                    String kickReason = event.getOption("reason").getAsString();

                    try {
                        kickTarget.kick(kickReason);

                        // Acknowledge the event
                        event.getHook().sendMessage(kickTarget.getMember().getAsMention() + " kicked! **Reason:** " + kickReason).queue();
                    } catch (SQLException e) {
                        event.getHook().sendMessage("**[Error]** There was an issue with updating the punishment in the database").queue();
                        e.printStackTrace();
                    }
                }

                // KICK COMMAND CONTROLLER
                case "warn" -> {
                    // Get the values from the arguments passed in through the slash command
                    Member warnTarget = event.getOption("user").getAsMember();
                    String warnReason = event.getOption("reason").getAsString();

                    // Acknowledge the event
                    event.reply(warnTarget.getAsMention() + " warned! **Reason:** " + warnReason).queue();

                    // Alert the user they got banned
                    Embeds.sendEmbed(Embeds.WARNING, warnTarget, true);

                    // Ban the user
                    guild.kick(warnTarget, warnReason).queue();
                }

                case "addresponse" -> {
                    String triggerWord = event.getOption("trigger").getAsString();
                    ;
                    String response = event.getOption("response").getAsString();
                    boolean deleteTriggerMsg = false;
                    boolean deleteResponse = false;
                    int deleteDelay = 0;
                    if (event.getOption("delete_trigger") != null)
                        deleteTriggerMsg = event.getOption("delete_trigger").getAsBoolean();
                    else if (event.getOption("delete_response") != null)
                        deleteResponse = event.getOption("delete_response").getAsBoolean();
                    else if (event.getOption("delete_delay") != null)
                        deleteDelay = (int) event.getOption("delete_delay").getAsLong();
                    try {
                        // Populating response members
                        Response responseObj = new Response(deleteResponse, deleteTriggerMsg, response, deleteDelay);

                        //
                        String responseAddQuery = "INSERT INTO Responses VALUES ('" + triggerWord + "', '" + response + "', " + deleteTriggerMsg + ", " + deleteResponse + ", " + deleteDelay + ",'" + guild.getId() + "')";
                        statement.executeUpdate(responseAddQuery);
                        BotProperty.getResponseHashMap().put(triggerWord, responseObj);

                        // Checking if the response trigger word is already in the database
                    } catch (SQLIntegrityConstraintViolationException e) {
                        channel.sendMessage("**Error: ** That trigger word is already used! Use another one!").queue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    event.reply("**Trigger Word:** " + triggerWord + " | **Delete Message:** " + deleteTriggerMsg + " | **Delete Message After:** " + deleteDelay +
                            " Seconds\nResponse: " + response).queue();
                }

                case "deleteresponse" -> {
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

                case "cardcheck" -> event.reply("Your order was marked as fraud. To verify you're the owner of the card we need to see the card with the last 4 digits of: " + event.getOption("last_four_digits").getAsString() + "\n" +
                                "You can blur everything else out we just need to see the last 4 digits. If this cannot be done, we can provide a full refund. Just let us know.\n" +
                                "Example: https://cdn.discordapp.com/attachments/882612837380399174/955249207466422332/IMG_5751.jpg")
                        .queue();

                // Opens the ticket so the target user can speak in it and view the ticket
                case "openticket" -> {
                    event.deferReply().queue();
                    Member member = event.getOption("target_user").getAsMember();
                    channel.upsertPermissionOverride(member)
                            .setAllow(Permission.MESSAGE_SEND)
                            .setAllow(Permission.VIEW_CHANNEL)
                            .queue();
                    event.getHook().sendMessage(member.getUser().getName() + " is allowed to speak").queue();
                }

                // CLOSE slash command to close a ticket and send a message to the user alerting the ticket has been closed
                case "close" -> {
                    event.reply("Deleting ticket...").queue();
                    try {
                        Embeds.sendEmbed(Embeds.TICKET_CLOSED, guild.getMemberById(channel.getTopic()), true);
                        channel.delete().queue();
                    } catch (IllegalArgumentException e) {
                        event.reply("Can't close this channel because it is not a ticket channel!").queue();
                    }
                }

                // AUTONUKE slash command to autonuke a channel every given minutes
                case "autonuke" -> {
                    event.deferReply().queue();
                    event.getHook().sendMessage("Auto Nuke Starting...").queue();
                    final String[] channelId = {channel.getId()};
                    Timer timer = new Timer();
                    int begin = 0;
                    int seconds = (int) event.getOption("minutes").getAsLong();
                    int timeInterval = seconds * 60000;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            String channelName = channel.getName();
                            Category previousCategory = channel.getParentCategory();
                            guild.getTextChannelById(channelId[0]).delete().queue();
                            guild.createTextChannel(channelName, previousCategory).queue(newChannel -> {
                                Embeds.sendEmbed(Embeds.AUTO_NUKE, newChannel, true);
                                channelId[0] = newChannel.getId();
                                System.out.println("[LOG] Channel Nuked");
                            });
                        }
                    }, begin, timeInterval);
                }

                // NUKE slash command to nuke a channel
                case "nuke" -> {
                    String channelName = channel.getName();
                    Category previousCategory = channel.getParentCategory();
                    channel.delete().queue();
                    guild.createTextChannel(channelName, previousCategory).queue(newChannel -> {
                        newChannel.sendMessage("Channel has been nuked!").queue();
                    });
                }

                // SEND slash command to view an order
                case "send" -> {
                    // Get the options
                    String orderID = event.getOption("order_id").getAsString();
                    CustomMember targetMember = new CustomMember(jda, event.getOption("target_user").getAsMember().getId(), guild.getId());
                    CustomMember guildOwner = new CustomMember(jda, "639094715605581852", guild.getId());

                    // Check if the admin is sending accounts to himself
                    if(!targetMember.getMember().getId().equalsIgnoreCase(event.getMember().getId())) {
                        try {
                            java.util.Date date = new java.util.Date();
                            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                            SQLConnection.addDefaultUser(guild, targetMember.getMember());

                            // Insert the order into the database so the same order cannot be claimed twice
                            SQLConnection.insertOrder(orderID, targetMember.getMember(), sqlDate);

                            String accounts = SQLConnection.getProductDetails(orderID, guild.getId(), 0);

                            targetMember.sendProduct(orderID, accounts);
                            guildOwner.sendProduct(orderID, accounts);

                            event.getHook().sendMessage("Accounts successfully sent. Check your DMs! " + targetMember.getMember().getAsMention()).queue();
                        } catch (IOException | InterruptedException e) {
                            event.getHook().sendMessage("**[LOG]** There was an issue with sending the product").queue();
                            e.printStackTrace();
                        } catch (SQLIntegrityConstraintViolationException e) {
                            channel.sendMessage(
                                    """
                                            **[Fraud Detection]** 
                                            Unable to send the product. The product was already claimed.
                                            """).queue();
                            e.printStackTrace();
                        } catch (SQLException e){
                            event.getHook().sendMessage("**[ERROR]** There was an issue with registering the order to the database").queue();
                            e.printStackTrace();
                        }
                    }else{
                        // Alert Mythik of a corrupt staff member
                        botProperty.corruptStaffAlert(event.getJDA(), guild, sender.getUser(), "SENDING ACCOUNTS TO HIMSELF **Order ID:** " + orderID);
                        event.reply("Corrupt Staff Member Detect! " + event.getMember().getUser().getAsMention()).queue();
                    }
                }

                // ORDER slash command to view a shoppy order
                case "order" -> {
                    ShoppyConnection shoppyConnection = new ShoppyConnection();
                    String orderID = event.getOption("order_id").getAsString();
                    try {
                        ShoppyOrder order = shoppyConnection.getShoppyOrder(orderID);

                        event.replyEmbeds(order.sendOrderEmbed().build()).queue();
                    } catch (NullPointerException e){
                        event.getHook().sendMessage("That order id does not exist!").queue();
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // STAFF_HELP slash command to view staffcommands
                case "staffhelp" -> {
                    event.replyEmbeds(Embeds.STAFF_HELP.build()).queue();
                }

                // DIRECTIONSLIST slash command to view a list of all directions and their description
                case "directionslist" -> {
                    event.replyEmbeds(Embeds.DIRECTIONS.build()).queue();
                }

                // DIRECTIONS slash command to view a set of directions for a specific command
                case "directions" -> {
                    String direction = event.getOption("direction_name").getAsString();

                    event.replyEmbeds(displayDirections(direction).build()).queue();
                }

                // ADDUSER slash command to add a user to the database
                case "add_user" -> {
                    SQLConnection.addDefaultUser(guild, event.getOption("member").getAsMember());
                }
            }
        } else {
            channel.sendMessage("**Error:** You do not have permissions to run this command!").queue();
        }
    }

    /**
     * Overridden method to auto-complete a slash command optioo
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
