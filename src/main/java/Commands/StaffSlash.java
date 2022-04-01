package Commands;

import Bot.BotProperty;
import Bot.Embeds;
import Bot.Response;
import Bot.SQLConnection;
import Shoppy.ShoppyConnection;
import Shoppy.ShoppyOrder;
import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class StaffSlash extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        // Only accept commands from guilds
        if(!event.getChannelType().isGuild() || event.getMember() == null)
            return;
        Guild guild = event.getGuild();

        Member sender = event.getMember();

        TextChannel channel = event.getTextChannel();

        Statement statement = SQLConnection.getStatement();

        if(sender.getUser().isBot())
            return;
        if(sender.hasPermission(Permission.ADMINISTRATOR)){
            switch (event.getName()) {
                // BAN COMMAND CONTROLLER
                case "ban":
                    // Get the values from the arguments passed in through the slash command
                    Member banTarget = event.getOption("user").getAsMember();
                    String banReason = event.getOption("reason").getAsString();
                    int delete_days = (int) event.getOption("delete_days").getAsLong();

                    // Acknowledge the event and alert that the user was banned
                    event.reply(banTarget.getAsMention() + " banned. **Reason:** " + banReason).queue();

                    // Alert the user they got banned
                    Embeds.sendEmbed(Embeds.BAN, banTarget, banReason);

                    // Ban the user
                    guild.ban(banTarget, delete_days, banReason).queue();
                    break;

                // MUTE COMMAND CONTROLLER
                case "mute":

                    // Get the values from the arguments passed in through the slash command
                    Member muteTarget = event.getOption("user").getAsMember();
                    String muteReason = event.getOption("reason").getAsString();


                    // Mute the user
                    try {
                        // Update the database that the member is muted
                        String QUERY = "UPDATE Users SET Muted = TRUE WHERE MemberID = " + muteTarget.getId();
                        statement.executeUpdate(QUERY);

                        // Assign the mute role to the user
                        Role muteRole = guild.getRoleById("936718165130481705");
                        guild.addRoleToMember(muteTarget, muteRole).queue();

                        // Alert the user they got muted
                        Embeds.sendEmbed(Embeds.MUTE, muteTarget, muteReason);

                        // Acknowledge the event and aler that the user was muted
                        event.reply(muteTarget.getAsMention() + " muted. **Reason:** " + muteReason).queue();
                    } catch (SQLException e) {

                        // Alert that there was an issue with the SQL statement
                        event.reply("[ERROR] There was an issue with adding the mute to the database!").queue();
                        e.printStackTrace();
                    }
                    break;

                // UNMUTE COMMAND CONTROLLER
                case "unmute":
                    Member unmuteTarget = event.getOption("user").getAsMember();
                    Role muteRole = guild.getRoleById("936718165130481705");
                    try {
                        String QUERY = "UPDATE Users SET Muted = FALSE WHERE MemberID = " + unmuteTarget.getId();
                        statement.executeUpdate(QUERY);

                        guild.removeRoleFromMember(unmuteTarget, muteRole).queue();

                        // Alert the user they got unmuted
                        Embeds.sendEmbed(Embeds.UNMUTE, unmuteTarget);

                        // Acknowledge the event and alert that the user was unmuted
                        event.reply(unmuteTarget.getAsMention() + " unmuted.").queue();
                    } catch (SQLException e) {
                        event.reply("[ERROR] There was an issue with adding the mute to the database!").queue();
                        e.printStackTrace();
                    }

                    break;
                // KICK COMMAND CONTROLLER
                case "kick":

                    // Get the values from the arguments passed in through the slash command
                    Member kickTarget = event.getOption("user").getAsMember();
                    String kickReason = event.getOption("reason").getAsString();

                    // Acknowledge the event
                    event.reply(kickTarget.getAsMention() + " kicked! **Reason:** " + kickReason).queue();

                    // Alert the user they got banned
                    Embeds.sendEmbed(Embeds.KICK, kickTarget, kickReason);

                    // Ban the user
                    guild.kick(kickTarget, kickReason).queue();
                    break;

                // KICK COMMAND CONTROLLER
                case "warn":
                    // Get the values from the arguments passed in through the slash command
                    Member warnTarget = event.getOption("user").getAsMember();
                    String warnReason = event.getOption("reason").getAsString();

                    // Acknowledge the event
                    event.reply(warnTarget.getAsMention() + " warned! **Reason:** " + warnReason).queue();

                    // Alert the user they got banned
                    Embeds.sendEmbed(Embeds.WARNING, warnTarget, warnReason);

                    // Ban the user
                    guild.kick(warnTarget, warnReason).queue();
                    break;
                case "generateticket":
                    EmbedBuilder ticketEmbed = new EmbedBuilder().setTitle("**Open Ticket**")
                            .setColor(Color.WHITE)
                            .setFooter("Mythik Bot", sender.getEffectiveAvatarUrl())
                            .setDescription("Create a ticket if you have any questions/issues");
                    event.replyEmbeds(ticketEmbed.build()).addActionRow(Button.primary("create-ticket", "Create Ticket")).queue();
                    break;
                case "addresponse":

                    String triggerWord = event.getOption("trigger").getAsString();;
                    String response = event.getOption("response").getAsString();

                    boolean deleteTriggerMsg = false;
                    boolean deleteResponse = false;
                    int deleteDelay = 0;

                    if(event.getOption("delete_trigger") != null)
                        deleteTriggerMsg = event.getOption("delete_trigger").getAsBoolean();
                    else if(event.getOption("delete_response") != null)
                        deleteResponse = event.getOption("delete_response").getAsBoolean();
                    else if(event.getOption("delete_delay") != null)
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
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    event.reply("**Trigger Word:** " + triggerWord + " | **Delete Message:** " + deleteTriggerMsg + " | **Delete Message After:** " + deleteDelay +
                            " Seconds\nResponse: " + response).queue();
                    break;
                case "deleteresponse":
                    String deleteWord = event.getOption("trigger_word").getAsString();
                    BotProperty.getResponseHashMap().remove(deleteWord);
                    String removeResponseQuery = "DELETE FROM Responses WHERE TriggerWord = '" + deleteWord + "'";
                    try {
                        statement.executeUpdate(removeResponseQuery);
                        event.reply("**Success:** Response successfully deleted").queue();
                    }catch (Exception e){
                        event.reply("**FAILURE:** There was an error in deleting that triggerKey! Make sure it exist!").queue();
                        e.printStackTrace();
                    }
                    break;
                case "cardcheck":
                    event.reply("Your order was marked as fraud. To verify you're the owner of the card we need to see the card with the last 4 digits of: " + event.getOption("last_four_digits").getAsString() + "\n" +
                            "You can blur everything else out we just need to see the last 4 digits. If this cannot be done, we can provide a full refund. Just let us know.\n" +
                            "Example: https://cdn.discordapp.com/attachments/882612837380399174/955249207466422332/IMG_5751.jpg"
                            ).queue();
                            break;
                case "openticket":
                    event.deferReply().queue();
                    Member member = event.getOption("target_user").getAsMember();
                    channel.upsertPermissionOverride(member)
                            .setAllow(Permission.MESSAGE_SEND)
                            .setAllow(Permission.VIEW_CHANNEL)
                            .queue();
                    event.getHook().sendMessage(member.getUser().getName() + " is allowed to speak").queue();
                    break;

                // Close slash command to close a ticket and send a message to the user alerting the ticket has been closed
                case "close":
                    event.reply("Deleting ticket...").queue();
                    try {
                        Embeds.sendEmbed(Embeds.TICKETCLOSED, guild.getMemberById(channel.getTopic()));
                        channel.delete().queue();
                    }catch (IllegalArgumentException e){
                        event.reply("Can't close this channel because it is not a ticket channel!").queue();
                    }
                    break;
                case "autonuke":
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
                                newChannel.sendMessage("""
                                        **[CHANNEL NUKED]**
                                        This channel is nuked every 30 minutes!\nMake a ticket if you have any questions about an order!
                                        **Shop Link:** https://betteralts.com
                                        https://media.giphy.com/media/xIytx7kHpq74c/giphy.gif""").queue();
                                channelId[0] = newChannel.getId();
                                System.out.println("[LOG] Channel Nuked");
                            });
                        }
                    }, begin, timeInterval);
                    break;
                case "nuke":
                    String channelName = channel.getName();
                    Category previousCategory = channel.getParentCategory();
                    channel.delete().queue();
                    guild.createTextChannel(channelName, previousCategory).queue(newChannel -> {
                        newChannel.sendMessage("Channel has been nuked!").queue();
                    });
                    break;
//                case "send":
//                    String accountType = event.getOption("product_name").getAsString();
//                    int amount = (int)event.getOption("amount").getAsLong();
//                    Member targetMember = event.getOption("target_user").getAsMember();
//
//                    String updateStatusQuery = "UPDATE accounts SET status = 'SELECTED' WHERE AccountType = '" + accountType + "' LIMIT " + amount;
//                    String retrieveAccountsQuery = "SELECT * FROM accounts WHERE status = 'SELECTED'";
//
//                    try {
//                        statement.executeUpdate(updateStatusQuery);
//                        ResultSet resultSet = statement.executeQuery(retrieveAccountsQuery);
//                        String accountsSent = "";
//
//                        PrintStream outputFile = new PrintStream("output");
//                        String accountInfo = "";
//                        while (resultSet.next()) {
//
//                            accountInfo = resultSet.getString(4);
//
//                            if (amount > 3)
//                                outputFile.println(accountInfo);
//                            else
//                                accountsSent += accountInfo + "\n";
//                            System.out.println(accountInfo);
//                        }
//                        EmbedBuilder orderEmbed = new EmbedBuilder()
//                            .setTitle("**Better Alts Order**")
//                            .setDescription(setOrderDescription(accountType));
//                        if (amount <= 3)
//                            orderEmbed.addField("Alts", accountsSent, false);
//                            orderEmbed.setFooter("Better Alts");
//                            targetMember.getUser().openPrivateChannel().flatMap(privateChannel ->
//                                privateChannel.sendMessageEmbeds(orderEmbed.build())
//                        ).queue();
//                        guild.getMemberById("845917592580259841").getUser().openPrivateChannel().flatMap(privateChannel ->
//                                privateChannel.sendMessageEmbeds(orderEmbed.build())
//                        ).queue();
//                        if (amount > 3) {
//                            targetMember.getUser().openPrivateChannel().flatMap(privateChannel ->
//                                    privateChannel.sendFile(new File("output"), "message.txt")
//                            ).queue();
//                            guild.getMemberById("845917592580259841").getUser().openPrivateChannel().flatMap(privateChannel ->
//                                    privateChannel.sendFile(new File("output"), "message.txt")
//                            ).queue();
//                        }
//                        String deleteQuery = "DELETE FROM accounts WHERE status = 'SELECTED'";
//
//                        statement.executeUpdate(deleteQuery);
//
//                        Role customerRole = guild.getRoleById("929116572063244339");
//
//                        File outputFileDelete = new File("output");
//                        outputFileDelete.delete();
//                        guild.addRoleToMember(targetMember.getId(), customerRole).queue();
//                        event.reply("Accounts Successfully Sent! " + guild.getMemberById(targetMember.getId()).getAsMention() + " check your DMs").queue();
//                    } catch (SQLException | FileNotFoundException e) {
//                        event.reply("There was an issue with sending the product!").queue();
//                        e.printStackTrace();
//                    }

                case "order":
                    ShoppyConnection shoppyConnection = new ShoppyConnection();
                    Gson gson = new Gson();
                    String orderID = event.getOption("order_id").getAsString();
                    try {
                        ShoppyOrder order = gson.fromJson(shoppyConnection.getShoppyItem("orders", orderID).body(), ShoppyOrder.class);

                        event.replyEmbeds(order.sendOrderEmbed(order).build()).queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }else{
            channel.sendMessage("**Error:** You do not have permissions to run this command!").queue();
        }
    }

    public String setOrderDescription(String accountType)
    {
        if(accountType.toLowerCase().contains("mfa")) {
            return "Thank you for ordering " + accountType +
                    """
                    **Format:** email:emailPassword:minecraft password
                    **Mail Site:** mail.com
                    **Mojang Security Questions:** a a a
                    **Note:** Login through Mojang
                    **How to change mail for Microsoft Minecraft Account**
                    **Link:** https://youtu.be/duowaqDnwdM
                    *If the minecraft password or security questions are incorrect then reset it since you have access to the email*
                    """;
        }else if(accountType.equalsIgnoreCase("UnbannedNFA") || accountType.equalsIgnoreCase("MigratedUnbannedNFA")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password:username\n" +
                    "Use VyprVPN to avoid getting them security banned on Hypixel";
        }else if(accountType.equalsIgnoreCase("LvL21") || accountType.equalsIgnoreCase("Ranked+LvL") || accountType.equalsIgnoreCase("Ranked")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password:username";
        }else if(accountType.equalsIgnoreCase("MigratedBannedNFA") || accountType.equalsIgnoreCase("MineconNFA")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password:username";
        }else if(accountType.equalsIgnoreCase("VyprVPN")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password\n" +
                    "**Note:** Don't change password or you may lose access fast";
        }else if(accountType.equalsIgnoreCase("DoorDash")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password\n" +
                    "Log into the email and DoorDash app, when it asked for a code click 'Send To Email'" +
                    "*Make sure you have a clean IP address and clean device or payment methods will not appear*";
        }else if(accountType.contains("Yahoo")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password | Total Mails: <number of mails from riot>\n" +
                    "Log into the email at yahoo.com";
        }else{
            return  "There was an issue with the order, created a ticket in the discord server";
        }
    }
}
