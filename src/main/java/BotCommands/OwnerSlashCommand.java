package BotCommands;

import Bot.BotProperty;
import CustomObjects.DropDowns;
import CustomObjects.Embeds;
import Bot.SQLConnection;
import CustomObjects.CustomMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class OwnerSlashCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Only accept commands from guilds or non null members
        if (!event.getChannelType().isGuild() || event.getMember() == null)
            return;
        Guild guild = event.getGuild();
        JDA jda = event.getJDA();
        Member sender = event.getMember();
        CustomMember guildOwner = new CustomMember(jda, BotProperty.guildsHashMap.get(guild.getId()).getServerOwnerId(), guild.getId());
        Statement statement = SQLConnection.getStatement();

        // If the sender is the bot then exit
        if (sender.getUser().isBot())
            return;

        // If send contains the role "Owner" then allow them to use this command
        if (sender.getId().equalsIgnoreCase(guildOwner.getMember().getId())) {
            event.deferReply().queue();

            switch (event.getName()) {

                // UPLOAD a product into the database
                case "upload" -> {
                    String accountType = event.getOption("account_type").getAsString();
                    try {
                        File inputFile = event.getOption("input_file").getAsAttachment().downloadToFile().get();
                        Scanner input = new Scanner(inputFile);

                        String query = "INSERT INTO Accounts (AccountInfo, AccountType, GuildID) VALUES";
                        int accountNumber = 0;

                        // Build the query
                        while (input.hasNext()) {
                            String accountInfo = input.nextLine();
                            System.out.println("Account Number " + accountNumber + ": " + accountInfo);

                            query += "('" + accountInfo + "', '" + accountType + "', '" + guild.getId() + "'),";
                            accountNumber++;

                            // Replace the last character ',' with a ';'
                            query = query.substring(0, query.length() - 1) + ";";
                        }

                        inputFile.delete();

                        statement.executeUpdate(query);
                        event.getHook().sendMessage(accountNumber + " " + accountType + " successfully uploaded!").setEphemeral(true).queue();
                    } catch (InterruptedException e) {
                        event.getHook().sendMessage("**[ERROR]** Interrupted exception").setEphemeral(true).queue();
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        event.getHook().sendMessage("**[ERROR]** Execution exception").setEphemeral(true).queue();
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        event.getHook().sendMessage("**[ERROR]** File not found exception").setEphemeral(true).queue();
                        e.printStackTrace();
                    } catch (SQLException e) {
                        event.getHook().sendMessage("**[ERROR]** Accounts could not be uploaded").setEphemeral(true).queue();
                        e.printStackTrace();
                    }
                }

                // REPLACE an order
                case "replace" -> {
                    String orderID = event.getOption("order_id").getAsString();
                    CustomMember member = new CustomMember(jda, event.getOption("target_user").getAsMember().getId(), guild.getId());
                    int replacementAmount = (int) event.getOption("replacement_amount").getAsLong();

                    try {
                        String accounts = SQLConnection.getProductDetails(orderID, guild.getId(), replacementAmount);

                        member.sendProduct(orderID, accounts);
                        guildOwner.sendProduct(orderID, accounts);

                        event.getHook().sendMessage("Accounts successfully sent").queue();
                    } catch (IOException e) {
                        System.out.println("**[LOG]** There was an issue with sending the product");
                        event.getHook().sendMessage("**[LOG]** There was an issue with sending the product").queue();
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        System.out.println("**[LOG]** There was an issue with sending the product");
                        event.getHook().sendMessage("**[LOG]** There was an issue with sending the product").queue();
                        e.printStackTrace();
                    }
                }

                // WHITELIST either add or remove a customer from the whitelist
                case "whitelist" -> {
                    // Get the arguments passed in
                    String actionType = event.getOption("action").getAsString();
                    Member targetMember = event.getOption("target_member").getAsMember();

                    // Build the query
                    String whiteListQuery = "";
                    if (actionType.equalsIgnoreCase("add")) {
                        whiteListQuery = "INSERT INTO Whitelist VALUES ('" + targetMember.getId() + "')";
                    } else if(actionType.equalsIgnoreCase("remove")){
                        whiteListQuery = "DELETE FROM Whitelist WHERE MemberID = '" + targetMember.getId() + "'";
                    } else {
                        event.getHook().sendMessage("That is not a valid action type. Either add or remove!").setEphemeral(true).queue();
                        return;
                    }

                    // Execute the query and handle any exceptions
                    try {
                        statement.executeUpdate(whiteListQuery);
                        event.getHook().sendMessage("User successfully added to the whitelist").queue();
                    // Check if entry is already in the database
                    }catch (SQLIntegrityConstraintViolationException e){
                        event.getHook().sendMessage("That user is already in the whitelist!").setEphemeral(true).queue();
                        e.printStackTrace();
                    }catch (SQLException e) {
                        event.getHook().sendMessage("There was an error with adding that member to the whitelist!").setEphemeral(true).queue();
                        e.printStackTrace();
                    }
                }

                // REMOVEORDER from the database
                case "removeorder" ->{
                    String query = "DELETE FROM Orders WHERE OrderId='" + event.getOption("order_id").getAsString() + "'";
                    try {
                        statement.executeUpdate(query);
                        event.getHook().sendMessage("Order successfully removed from the database!").setEphemeral(true).queue();
                    } catch (SQLException e) {
                        event.getHook().sendMessage("**[ERROR]** There was an error with removing the order from the databdase").queue();
                        e.printStackTrace();
                    }
                }

                // DELETEDM delete a DM with a user and the bot
                case "deletedm" -> {

                    // Get arguments passed into the command
                    Member targetMember = event.getOption("target_member").getAsMember();
                    int amountToDelete = event.getOption("amount_to_delete").getAsInt();

                    // Open DMs with the user
                    guild.getMemberById(targetMember.getId()).getUser().openPrivateChannel().queue(
                        privateChannel -> {

                            // Get the amount of messages
                            privateChannel.getHistory().retrievePast(amountToDelete).queue(
                                messages -> {

                                    // Delete and print the accounts
                                    for (Message msg : messages) {
                                        msg.delete().queue();
                                    }
                                }
                            );
                        }
                    );

                    event.getHook().sendMessage("Successfully deleted DMs with the user " + targetMember.getAsMention()).setEphemeral(true).queue();
                }

                // SCANFILE scan a file
                case "scanfile" -> {
                    // The path of the file
                    String filePath = event.getOption("filepath").getAsString();
                    File folder = new File(filePath);
                    File[] listOfFiles = folder.listFiles();

                    try {
                        PrintStream outputFile = new PrintStream("output");

                        for (File file : listOfFiles) {
                            Scanner inputFile = null;
                            inputFile = new Scanner(file);
                            while(inputFile.hasNext())
                            {
                                outputFile.println(inputFile.nextLine());
                            }
                        }

                        event.getHook().sendMessage("Files Scanned").setEphemeral(true).queue();
                    } catch (FileNotFoundException e) {
                        event.getHook().sendMessage("There was an issue with scanning the files").setEphemeral(true).queue();
                        e.printStackTrace();
                    }
                }

                //
                case "orderdetails" -> {
                    String orderId = event.getOption("order_id").getAsString();
                    Member member = event.getOption("target_member").getAsMember();

                    // Open DMs with the user
                    guild.getMemberById(member.getId()).getUser().openPrivateChannel().queue(
                            privateChannel -> {

                                // Get the amount of messages
                                privateChannel.getHistory().retrievePast(100).queue(
                                    messages -> {

                                        // Delete and print the accounts
                                        for (Message msg : messages) {
                                            if(msg.getEmbeds().get(0).getFields().get(0).getValue().equalsIgnoreCase(orderId))
                                            {
                                                System.out.println(msg.getEmbeds().get(0).getFields().get(1).getValue());

                                                EmbedBuilder embedBuilder = new EmbedBuilder()
                                                        .setTitle("Retrieved Order")
                                                                .addField("Order ID", orderId, false)
                                                                        .addField("Alts", msg.getEmbeds().get(0).getFields().get(1).getValue(), false);
                                                guildOwner.sendPrivateMessage(
                                                        embedBuilder
                                                );

                                                event.getHook().sendMessage("Got order").setEphemeral(true).queue();
                                            }
                                        }
                                    }
                                );
                            }
                    );

                    event.getHook().sendMessage("Failed to get order").setEphemeral(true).queue();
                }
            }
        } else {
            event.getHook().sendMessage("Only Owners can run this command! Ping Mythik to run it!").setEphemeral(true).queue();
        }
    }
}
