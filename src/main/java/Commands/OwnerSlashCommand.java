package Commands;

import Bot.Embeds;
import Bot.SQLConnection;
import Shoppy.ShoppyOrder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Scanner;

public class OwnerSlashCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Only accept commands from guilds
        if (!event.getChannelType().isGuild() || event.getMember() == null)
            return;
        Guild guild = event.getGuild();

        Member sender = event.getMember();

        TextChannel channel = event.getTextChannel();

        Statement statement = SQLConnection.getStatement();

        if (sender.getUser().isBot())
            return;
        if (sender.getRoles().contains(guild.getRoleById("938989740177383435"))) {
            event.deferReply().queue();

            switch (event.getName()) {

                // UPLOAD a product into the database
                case "upload" -> {
                    String accountType = event.getOption("account_type").getAsString();

                    Scanner inputFile = null;
                    try {
                        inputFile = new Scanner(new File("input.txt"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    String query = "INSERT INTO Accounts (AccountInfo, AccountType, GuildID) VALUES";
                    int accountNumber = 1;

                    // Build the query
                    while (inputFile.hasNext()) {
                        String accountInfo = inputFile.nextLine();
                        System.out.println("Account Number " + accountNumber + ": " + accountInfo);

                        query += "('" + accountInfo + "', '" + accountType + "', '" + guild.getId() + "'),";
                        accountNumber++;
                    }

                    // Replace the last character ',' with a ';'
                    query = query.substring(0, query.length() - 1) + ";";

                    // Execute the query to upload the alts. Let the user know if it was successful
                    try {
                        statement.executeUpdate(query);
                        event.getHook().sendMessage(accountType + " successfully uploaded!").setEphemeral(true).queue();
                    } catch (SQLException e) {
                        event.getHook().sendMessage("**[ERROR]** Accounts could not be uploaded").setEphemeral(true).queue();
                        e.printStackTrace();
                    }
                }

                // REPLACE an order
                case "replace" -> {
                    ShoppyOrder shoppyOrder = new ShoppyOrder();

                    String orderID = event.getOption("order_id").getAsString();
                    Member member = event.getOption("target_user").getAsMember();
                    int replacementAmount = (int) event.getOption("replacement_amount").getAsLong();

                    try {
                        shoppyOrder.sendProductInformation(orderID, member, channel, guild, replacementAmount);
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
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    event.getHook().sendMessage("Order successfully removed from the database!").setEphemeral(true).queue();
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

                // FRAUD add a user to the fraud list
                case "fraud" -> {

                    // Get the arguments passed in
                    Member targetMember = event.getOption("target_member").getAsMember();

                    try {
                        // Build and execute the query
                        String addFraudQuery = "INSERT INTO FraudList VALUES ('" + targetMember.getId() + "')";
                        statement.executeUpdate(addFraudQuery);

                        // Send a DM to the user telling them they got banned
                        targetMember.getUser().openPrivateChannel().flatMap(privateChannel ->
                                privateChannel.sendMessageEmbeds(Embeds.BAN.getEmbed().build())
                        ).queue();

                        // Ban the user
                        guild.ban(targetMember.getId(), 0, "Fraudulent").queue();

                        event.getHook().sendMessage(targetMember.getUser().getAsTag() + " has been banned and added to the fraud list").setEphemeral(true).queue();
                    } catch (SQLException e) {
                        event.getHook().sendMessage("There was an issue with adding that user to the fraud list!").setEphemeral(true).queue();
                        e.printStackTrace();
                    }
                }

                // SCANFILE scan a file
                case "scanfile" -> {
                    // The path of the file
                    File folder = new File("C:\\Users\\paulk\\Downloads\\2022-04-02_20-18-38\\2022-04-02_20-18-38\\2022-04-02 20-18-38\\Capture");
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
            }
        } else {
            event.getHook().sendMessage("Only Owners can run this command! Ping Mythik to run it!").setEphemeral(true).queue();
        }
    }
}
