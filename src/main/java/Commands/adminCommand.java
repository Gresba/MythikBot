package Commands;

import Bot.BotProperty;
import Bot.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;

import static Bot.Embeds.embed;
import static Bot.SQLConnection.getStatement;

public class adminCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(!event.getChannelType().isGuild() || event.getMember() == null)
            return;
        Member member = event.getMember();
        Guild guild = event.getGuild();

        try{
            if (member.getRoles().contains(guild.getRoleById("938989740177383435"))) {
                String botPrefix = new BotProperty(event.getGuild().getId()).getPrefix();
                Statement statement = getStatement();
                Message message = event.getMessage();

                String messageString = message.toString();
                String[] messageArr = message.getContentRaw().split(" ");

                TextChannel channel = event.getTextChannel();

                /*****************************************************************************************************/
                // Adding Roles Command
                /*****************************************************************************************************/
                if (messageArr[0].equalsIgnoreCase(botPrefix + "addRoles")) {
                    if (messageArr[1].equalsIgnoreCase("member") || messageArr[1].equalsIgnoreCase("m")) {
                        try {
                            Member targetRoleMember = message.getMentionedMembers().get(0);
                            Role targetAssignRole = message.getMentionedRoles().get(0);

                            guild.addRoleToMember(targetRoleMember, targetAssignRole).queue();
                        } catch (Exception e) {
                            channel.sendMessage("**Error: ** " + botPrefix + "addRoles <member> <@user> <@role>").queue();
                        }
                    } else if (messageArr[1].equalsIgnoreCase("server") || messageArr[1].equalsIgnoreCase("s")) {
                        try {
                            List<Member> allGuildMembers = guild.getMembers();
                            for (Member targetMember : allGuildMembers) {
                                System.out.println("[Log] SQL: Current User " + targetMember.getUser().getAsTag());
                                String query = "INSERT INTO Users (GuildID, MemberID, WarningCount, UserLvL, UserXP, Muted, InviteLink) VALUES ('" + guild.getId() + "', '" + targetMember.getId() + "', " + 0 + ", " + 0 + ", " + 0 + ", " + false + ", 'NeedsSet')";
                                statement.executeUpdate(query);
                                System.out.println("[Log] SQL: User " + targetMember.getUser().getAsTag() + " successfully registered into MySQL DB!");
                            }
                        } catch (Exception e) {
                            channel.sendMessage("**Error: ** " + botPrefix + "addRoles <server> <@role>").queue();
                            e.printStackTrace();
                        }
                    }
                }else if(messageArr[0].equalsIgnoreCase(botPrefix + "deletedm")){

                /*****************************************************************************************************/
                // Purge specific amount of messages
                /*****************************************************************************************************/
                }   else if (messageArr[0].equalsIgnoreCase(botPrefix + "purge")) {
                    if (messageArr.length >= 2) {
                        int pastMessageCount = Integer.valueOf(messageArr[1]);

                        channel.getHistory().retrievePast(pastMessageCount).queue(messages -> {
                            if (message.getMentionedMembers().size() > 0) {
                                for (Message msg : messages) {
                                    if (message.getMentionedMembers().get(0).getId().equalsIgnoreCase(msg.getMember().getId()))
                                        msg.delete().queue();
                                }
                            } else {
                                for (Message msg : messages) {
                                    msg.delete().queue();
                                }
                            }
                        });
                    } else {
                        channel.sendMessage("**Usage:** " + botPrefix + "purge <Amount> <Sender>").queue();
                    }
                }else if(messageArr[0].equalsIgnoreCase(botPrefix + "editEmbed")){
                    EmbedBuilder builtEmbed = new EmbedBuilder();
                    builtEmbed.setTitle("Skyblock Coins");
                    builtEmbed.addField("**Prices**",
                            """
                                    100+ million: $0.18/mil
                                    300+ million: $0.16/mil
                                    500+ million: $0.14/mil
                                    1 billion+: $0.13/mil""", false);
                    builtEmbed.addField("**Payment Methods**",
                            """
                                    Crypto (LTC/BTC)
                                    Debit/Credit (Can connect to Google Pay with any type of card)
                                    CashApp/Venmo/Zelle/Google Pay
                                    
                                    **PayPal**
                                    Contact @Flipfudge or @Lemons to purchase with PayPal""", false);
                    builtEmbed.addField("**How to buy**", "Open a channel in " + guild.getTextChannelById("954183734083588126").getAsMention(), false);
                    builtEmbed.setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG");
                    builtEmbed.setColor(Color.YELLOW);
                    guild.getTextChannelById("934231649812611082").getHistoryFromBeginning(10).complete().getMessageById("952699875043213313").editMessageEmbeds(builtEmbed.build()).queue();
                /*****************************************************************************************************/
                // Add/Remove Order ID
                /*****************************************************************************************************/
                } else if(messageArr[0].equalsIgnoreCase(botPrefix + "removeOrder")) {
                    String orderId = messageArr[1];

                    String query = "DELETE FROM Orders WHERE OrderId='" + orderId + "'";
                    statement.executeUpdate(query);
                    channel.sendMessage("Order successfully **removed** from the user!").queue();
                /*****************************************************************************************************/
                // Add/Remove User ID
                /*****************************************************************************************************/
                } else if(messageArr[0].equalsIgnoreCase(botPrefix + "addUser")) {
                    String userID = message.getMentionedMembers().get(0).getId();
                    statement.executeUpdate("INSERT INTO Users VALUES ('" + guild.getId() + "', '" + userID + "', " + 0 + ", " + 0 + ", " + 0 + ", " + false + ", 'NeedsSet')");
                    channel.sendMessage("User successfully **added** to the database!").queue();
                }else if(messageArr[0].equalsIgnoreCase(botPrefix + "removeUser")){
                    String memberId = messageArr[1];

                    String query = "DELETE FROM Users WHERE MemberId='" + memberId + "'";
                    statement.executeUpdate(query);
                    channel.sendMessage("Member has successfully been **removed**!").queue();

                /*****************************************************************************************************/
                // Mute Command
                /*****************************************************************************************************/
                }else if(messageArr[0].equalsIgnoreCase(botPrefix + "mute")) {
                    Member targetMute = message.getMentionedMembers().get(0);
                    Role muteRole = guild.getRoleById("936718165130481705");
                    guild.addRoleToMember(targetMute, muteRole).queue();

                    String QUERY = "UPDATE Users SET Muted = TRUE WHERE MemberID = " + targetMute.getId();
                    statement.executeUpdate(QUERY);
                    channel.sendMessage(targetMute.getAsMention() + " has been muted from the chat!").queue();
                }else if(messageArr[0].equalsIgnoreCase(botPrefix + "unmute")){
                    Member targetMute = message.getMentionedMembers().get(0);
                    Role muteRole = guild.getRoleById("936718165130481705");
                    guild.removeRoleFromMember(targetMute, muteRole).queue();

                    String QUERY = "UPDATE Users SET Muted = FALSE WHERE MemberID = " + targetMute.getId();
                    statement.executeUpdate(QUERY);
                    channel.sendMessage(targetMute.getAsMention() + " has been unmuted from the chat!").queue();
                }else if(messageArr[0].equalsIgnoreCase(botPrefix + "ban")) {
                    Member targetBan = message.getMentionedMembers().get(0);
                    channel.sendMessage(targetBan.getAsMention() + " has been ban from the server!").queue();
                    guild.ban(targetBan.getUser(), 0).queue();
                }else if(messageArr[0].equalsIgnoreCase(botPrefix + "purge")){
                    MessageHistory messageHistory = channel.getHistory();

                /*****************************************************************************************************/
                // Warning Command
                /*****************************************************************************************************/
                } else if(messageArr[0].equalsIgnoreCase(botPrefix + "warning")) {
                    String warningQuery = "";
                    Member target = null;
                    if(message.getMentionedMembers().size() != 0)
                        target = message.getMentionedMembers().get(0);
                    else
                        target = member;
                    if (messageArr[1].equalsIgnoreCase("reset")) {
                        warningQuery = "UPDATE Users SET WarningCount = 0 WHERE MemberID = '" + target.getId() + "'";
                    }else if(messageArr[1].equalsIgnoreCase("count")){

                        String getWarningCount = "SELECT WarningCount FROM Users WHERE MemberID = '" + target.getId() + "'";

                        ResultSet warningCountResult = statement.executeQuery(getWarningCount);
                        int warningCount = 0;
                        while(warningCountResult.next())
                        {
                            warningCount = warningCountResult.getInt(1);
                        }

                        channel.sendMessage("**Warning Count:** " + warningCount + " | " + target.getAsMention()).queue();
                        return;
                    }else{
                        String getWarningCount = "SELECT WarningCount FROM Users WHERE MemberID = '" + target.getId() + "'";

                        ResultSet warningCountReslt = statement.executeQuery(getWarningCount);
                        int warningCount = 0;

                        while(warningCountReslt.next())
                        {
                            warningCount = warningCountReslt.getInt(1) + 1;
                        }

                        if(messageArr[1].equalsIgnoreCase("remove")){
                            warningQuery = "UPDATE Users SET WarningCount = "  + (warningCount - 1)+ " WHERE MemberID = '" + target.getId() + "'";
                        }else if(messageArr[1].equalsIgnoreCase("add")){
                            if(warningCount == 3)
                            {
                                EmbedBuilder altAccountErrEmb = new EmbedBuilder();
                                altAccountErrEmb.setTitle("**You have been BANNED!**");
                                altAccountErrEmb.setDescription("**Reason: ** Pinged staff member 3/3 you have been warned before.\nContact Mythik#0001 to appeal!");
                                altAccountErrEmb.setFooter("Better Alts Security");
                                target.getUser().openPrivateChannel().flatMap(privateChannel ->
                                        privateChannel.sendMessageEmbeds(altAccountErrEmb.build())
                                ).queue();
                                channel.sendMessage(target.getAsMention() + " has been banned for pinging too much!").queue();
                                guild.ban(target, 0, "3 Warnings").queue();
                            }

                            warningQuery = "UPDATE Users SET WarningCount = " + warningCount + " WHERE MemberID = '" + target.getId() + "'";
                        }
                    }

                    statement.executeUpdate(warningQuery);

                    channel.sendMessage("Warning " + messageArr[1] + " success!").queue();
                /*****************************************************************************************************/
                // Upload Command
                /*****************************************************************************************************/
                }else if(messageArr[0].equalsIgnoreCase("upload")) {
                    String accountType = messageArr[1];

                    Scanner inputFile = new Scanner(new File("input.txt"));
                    int accountNumber = 1;
                    while (inputFile.hasNext()) {
                        String accountInfo = inputFile.nextLine();
                        System.out.println("Account Number " + accountNumber + ": " + accountInfo);
                        String query = "INSERT INTO Accounts (AccountInfo, AccountType) VALUES ('" + accountInfo + "', '" + accountType + "')";
                        accountNumber++;
                        statement.executeUpdate(query);
                    }

                    channel.sendMessage(accountType + " successfully uploaded!").queue();
                /*****************************************************************************************************/
                // Send Order Command
                /*****************************************************************************************************/
                }else if(messageArr[0].equalsIgnoreCase(botPrefix + "send")) {
                    String accountType = messageArr[1];
                    int amount = Integer.valueOf(messageArr[2]);
                    int amountSent = 0;

                    Member target = message.getMentionedMembers().get(0);

                    String updateStatusQuery = "UPDATE accounts SET status = 'SELECTED' WHERE AccountType = '" + accountType + "' LIMIT " + amount;

                    statement.executeUpdate(updateStatusQuery);

                    String retrieveAccountsQuery = "SELECT * FROM accounts WHERE status = 'SELECTED'";

                    ResultSet resultSet = statement.executeQuery(retrieveAccountsQuery);

                    String accountsSent = "";

                    PrintStream outputFile = new PrintStream("output");
                    String accountInfo = "";
                    while (resultSet.next()) {

                        accountInfo = resultSet.getString(4);

                        if (amount > 3)
                            outputFile.println(accountInfo);
                        else
                            accountsSent += accountInfo + "\n";
                        amountSent++;
                        System.out.println(accountInfo);
                    }

                    if(amountSent == 0)
                    {
                        channel.sendMessage(accountType + " is out of stock!").queue();
                    }else {
                        String orderDescription = setOrderDescription(accountType);

                        EmbedBuilder orderEmbed = new EmbedBuilder();
                        orderEmbed.setTitle("**Better Alts Order**");
                        orderEmbed.setDescription(orderDescription);
                        if (amount <= 3)
                            orderEmbed.addField("Alts", accountsSent, false);
                        orderEmbed.setFooter("BetterAlts");
                        target.getUser().openPrivateChannel().flatMap(privateChannel ->
                                privateChannel.sendMessageEmbeds(orderEmbed.build())
                        ).queue();
                        guild.getMemberById("845917592580259841").getUser().openPrivateChannel().flatMap(privateChannel ->
                                privateChannel.sendMessageEmbeds(orderEmbed.build())
                        ).queue();
                        if (amount > 3) {
                            target.getUser().openPrivateChannel().flatMap(privateChannel ->
                                    privateChannel.sendFile(new File("output"), "message.txt")
                            ).queue();
                            guild.getMemberById("845917592580259841").getUser().openPrivateChannel().flatMap(privateChannel ->
                                    privateChannel.sendFile(new File("output"), "message.txt")
                            ).queue();
                        }
                        String deleteQuery = "DELETE FROM accounts WHERE status = 'SELECTED'";

                        statement.executeUpdate(deleteQuery);

                        Role customerRole = guild.getRoleById("929116572063244339");

                        File outputFileDelete = new File("output");
                        outputFileDelete.delete();
                        guild.addRoleToMember(target.getId(), customerRole).queue();

                        channel.sendMessage(amountSent + " accounts Successfully Sent! " + guild.getMemberById(target.getId()).getAsMention() + " check your DMs").queue();
                    }

                /*****************************************************************************************************/
                // Ban member for fraud
                /*****************************************************************************************************/
                }else if(messageArr[0].equalsIgnoreCase(botPrefix + "fraud")) {
                    String memberId = "";
                    if (message.getMentionedMembers().size() != 0) {
                        memberId = message.getMentionedMembers().get(0).getId();
                    } else {
                        memberId = messageArr[1];
                    }
                    Member targetMember = guild.getMemberById(memberId);
                    String addFraudQuery = "INSERT INTO FraudList VALUES ('" + memberId + "')";
                    statement.executeUpdate(addFraudQuery);
                    member.getUser().openPrivateChannel().flatMap(privateChannel ->
                        privateChannel.sendMessageEmbeds(embed(Embeds.BAN))
                    ).queue();
                    guild.ban(targetMember.getId(), 0, "Fraudulent").queue();
                    channel.sendMessage(targetMember.getUser().getAsTag() + " has been banned and added to the fraud list").queue();
                }else if(messageArr[0].equalsIgnoreCase(botPrefix + "scanFile")){
                    PrintStream outputFile = new PrintStream("output");
                    File folder = new File("C:\\Users\\paulk\\Downloads\\2022-03-18 10-11-58");
                    File[] listOfFiles = folder.listFiles();

                    for (File file : listOfFiles) {
                        Scanner inputFile = new Scanner(file);
                        while(inputFile.hasNext())
                        {
                            outputFile.println(inputFile.nextLine());
                        }
                    }
                }
            }
        }catch (NullPointerException | SQLException | FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public String setOrderDescription(String accountType)
    {
        if(accountType.toLowerCase().contains("mfa")) {
            return "Thank you for ordering " + accountType + "\n" +
                    """
                    **Format:** email:emailPassword
                    **Minecraft Password:** Wanker!!22 or trollddu1
                    **Mail Site:** mail.com or yahoo.com
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
