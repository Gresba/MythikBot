package Commands;

import Bot.BotProperty;
import Bot.Embeds;
import Bot.Response;
import Bot.SQLConnection;
import Shoppy.ShoppyConnection;
import Shoppy.ShoppyOrder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.sql.PreparedStatement;
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
                    Member banTarget = Objects.requireNonNull(event.getOption("user")).getAsMember();
                    String banReason = Objects.requireNonNull(event.getOption("reason")).getAsString();
                    int delete_days = (int) Objects.requireNonNull(event.getOption("delete_days")).getAsLong();

                    // Acknowledge the event and alert that the user was banned
                    event.reply(banTarget.getAsMention() + " banned. **Reason:** " + banReason).queue();

                    // Alert the user they got banned
                    Embeds.sendEmbed(Embeds.BAN.getEmbed(), banTarget, true);

                    // Ban the user
                    guild.ban(banTarget, delete_days, banReason).queue();
                }

                // MUTE COMMAND CONTROLLER
                case "mute" -> {

                    // Get the values from the arguments passed in through the slash command
                    Member muteTarget = Objects.requireNonNull(event.getOption("user")).getAsMember();
                    String muteReason = Objects.requireNonNull(event.getOption("reason")).getAsString();


                    // Mute the user
                    try {
                        // Update the database that the member is muted
                        String updateMute = "UPDATE Users SET Muted = TRUE WHERE MemberID = " + muteTarget.getId();
                        statement.executeUpdate(updateMute);

                        PreparedStatement addMuteQuery = statement.getConnection().prepareStatement("INSERT INTO Punishments VALUES (?, ?, ?)");

                        // Setting type, reason and member, respectively, for punishment
                        addMuteQuery.setString(1, "mute");
                        addMuteQuery.setString(2, muteReason);
                        addMuteQuery.setString(3, muteTarget.getId());

                        addMuteQuery.executeUpdate();

                        // Assign the mute role to the user
                        Role muteRole = guild.getRoleById("936718165130481705");
                        guild.addRoleToMember(muteTarget, muteRole).queue();

                        // Alert the user they got muted
                        Embeds.sendEmbed(Embeds.MUTE.getEmbed(), muteTarget, true);

                        // Acknowledge the event and aler that the user was muted
                        event.reply(muteTarget.getAsMention() + " muted. **Reason:** " + muteReason).queue();
                    } catch (SQLException e) {

                        // Alert that there was an issue with the SQL statement
                        event.reply("[ERROR] There was an issue with adding the mute to the database!").queue();
                        e.printStackTrace();
                    }
                }

                // UNMUTE COMMAND CONTROLLER
                case "unmute" -> {
                    Member unmuteTarget = event.getOption("user").getAsMember();
                    Role muteRole = guild.getRoleById("936718165130481705");
                    try {
                        String QUERY = "UPDATE Users SET Muted = FALSE WHERE MemberID = " + unmuteTarget.getId();
                        statement.executeUpdate(QUERY);

                        guild.removeRoleFromMember(unmuteTarget, muteRole).queue();

                        // Alert the user they got unmuted
                        Embeds.sendEmbed(Embeds.UNMUTE.getEmbed(), unmuteTarget, true);

                        // Acknowledge the event and alert that the user was unmuted
                        event.reply(unmuteTarget.getAsMention() + " unmuted.").queue();
                    } catch (SQLException e) {
                        event.reply("[ERROR] There was an issue with adding the mute to the database!").queue();
                        e.printStackTrace();
                    }
                }

                // KICK COMMAND CONTROLLER
                case "kick" -> {

                    // Get the values from the arguments passed in through the slash command
                    Member kickTarget = event.getOption("user").getAsMember();
                    String kickReason = event.getOption("reason").getAsString();

                    // Acknowledge the event
                    event.reply(kickTarget.getAsMention() + " kicked! **Reason:** " + kickReason).queue();

                    // Alert the user they got banned
                    Embeds.sendEmbed(Embeds.KICK.getEmbed(), kickTarget, true);

                    // Ban the user
                    guild.kick(kickTarget, kickReason).queue();
                }

                // KICK COMMAND CONTROLLER
                case "warn" -> {
                    // Get the values from the arguments passed in through the slash command
                    Member warnTarget = event.getOption("user").getAsMember();
                    String warnReason = event.getOption("reason").getAsString();

                    // Acknowledge the event
                    event.reply(warnTarget.getAsMention() + " warned! **Reason:** " + warnReason).queue();

                    // Alert the user they got banned
                    Embeds.sendEmbed(Embeds.WARNING.getEmbed(), warnTarget, true);

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
                        Embeds.sendEmbed(Embeds.TICKETCLOSED.getEmbed(), guild.getMemberById(channel.getTopic()), true);
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
                                Embeds.sendEmbed(Embeds.AUTONUKE.getEmbed(), newChannel, true);
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
                    ShoppyOrder shoppyOrder = new ShoppyOrder();

                    // Get the options
                    String orderID = event.getOption("order_id").getAsString();
                    Member targetMemer = event.getOption("target_user").getAsMember();

                    // Check if the admin is sending accounts to himself
                    if(!targetMemer.getId().equalsIgnoreCase(event.getMember().getId())) {
                        try {
                            java.util.Date date = new java.util.Date();
                            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                            SQLConnection.addDefaultUser(guild, event.getOption("member").getAsMember());

                            // Insert the order into the database so the same order cannot be claimed twice
                            PreparedStatement insertOrder = statement.getConnection().prepareStatement("INSERT INTO Orders (OrderID, MemberID, ClaimedDate) VALUES (?, ?, ?)");
                            insertOrder.setString(1, orderID);
                            insertOrder.setString(2, targetMemer.getId());
                            insertOrder.setDate(3, sqlDate);
                            insertOrder.executeUpdate();

                            shoppyOrder.sendProductInformation(orderID, targetMemer, channel, guild, 0);
                            event.reply("Accounts successfully sent. Check your DMs! " + targetMemer.getAsMention()).queue();
                        } catch (IOException | InterruptedException e) {
                            event.reply("**[LOG]** There was an issue with sending the product").queue();
                            e.printStackTrace();
                        } catch (SQLIntegrityConstraintViolationException e) {
                            channel.sendMessage(
                                    """
                                            **[Fraud Detection]** 
                                            Unable to send the product. The product was already claimed.
                                            """).queue();
                            e.printStackTrace();
                        } catch (SQLException e){
                            event.reply("**[ERROR]** There was an issue with registering the order to the database").queue();
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // STAFFCOMAMND slash command to view staffcommands
                case "staffhelp" -> {
                    event.replyEmbeds(Embeds.STAFFHELP.getEmbed().build()).queue();
                }

                // DIRECTIONSLIST slash command to view a list of all directions and their description
                case "directionslist" -> {
                    event.replyEmbeds(Embeds.DIRECTIONS.getEmbed().build()).queue();
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

    public EmbedBuilder displayDirections(String direction)
    {
        EmbedBuilder directionList = null;

        switch (direction)
        {
            case "ClaimTicket" -> {
                return Embeds.CLAIMTICKET.getEmbed();
            }

            case "SendOrder" -> {
                return Embeds.SENDORDER.getEmbed();
            }

            case "FraudulentCheck" -> {
                return  Embeds.FRAUDULENTCHECK.getEmbed();
            }

            case "Replacements" -> {
                return  Embeds.REPLACEMENTS.getEmbed();
            }

            case "Unknown" -> {
                return Embeds.UNKNOWN.getEmbed();
            }

            case "UNBANNEDPRODUCTISSUE" -> {
                return Embeds.UNBANNEDPRODUCTISSUE.getEmbed();
            }

            case "MinecraftMFAIssue" -> {
                return Embeds.MINECRAFTMFAISSUE.getEmbed();
            }

            case "ChatModeration" -> {
                return Embeds.CHATMODERATION.getEmbed();
            }

            case "GeneralQuestions" -> {
                return Embeds.GENERALQUESTIONS.getEmbed();
            }
        }

        directionList.setColor(Color.BLACK);

        return directionList;
    }
}
