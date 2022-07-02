package BotCommands;

import Bot.BotProperty;
import Bot.SQLConnection;
import CustomObjects.CustomMember;
import CustomObjects.Modals;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.*;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class OwnerSlashCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Only accept commands from guilds or non-null members
        if (!event.getChannelType().isGuild() || event.getMember() == null)
            return;
        Guild guild = event.getGuild();
        JDA jda = event.getJDA();
        Member sender = event.getMember();
        CustomMember guildOwner = new CustomMember(jda, BotProperty.guildsHashMap.get(guild.getId()).getOwnerId(), guild.getId());

        // If the sender is the bot then exit
        if (sender.getUser().isBot())
            return;

        // If send contains the role "Owner" then allow them to use this command
        if (sender.getId().equalsIgnoreCase(guildOwner.getMember().getId())) {

            switch (event.getName())
            {
                case "configure" -> {
                    switch (event.getSubcommandName()) {
                        case "tickets" -> event.replyModal(Modals.CONFIGURE_TICKETS).queue();
                        case "server" -> event.replyModal(Modals.CONFIGURE_SERVER).queue();
                        case "roles" -> event.replyModal(Modals.CONFIGURE_ROLES).queue();
                        case "channels" -> event.replyModal(Modals.CONFIGURE_CHANNELS).queue();
                    }
                }


                // UPLOAD a product into the database
                case "upload" -> {
                    String productType = event.getOption("product_type").getAsString();
                    try {
                        File inputFile = event.getOption("input_file").getAsAttachment().downloadToFile().get();

                        event.reply(SQLConnection.uploadProducts(guild.getId(), productType, inputFile) + " " + productType + " successfully uploaded!")
                                .setEphemeral(true).queue();
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
                        event.getHook().sendMessage("**[ERROR]** Product could not be uploaded").setEphemeral(true).queue();
                        e.printStackTrace();
                    }
                }

                // REMOVE_ORDER from the database
                case "remove_order" ->{
                    try {
                        SQLConnection.removeOrder(event.getOption("order_id").getAsString());
                        event.getHook().sendMessage("Order successfully removed from the database!").setEphemeral(true).queue();
                    } catch (SQLException e) {
                        event.getHook().sendMessage("**[ERROR]** There was an error with removing the order from the database").queue();
                        e.printStackTrace();
                    }
                }

                // DELETE_DM delete a DM with a user and the bot
                case "delete_dm" -> {

                    // Get arguments passed into the command
                    Member targetMember = event.getOption("target_member").getAsMember();
                    int amountToDelete = event.getOption("amount_to_delete").getAsInt();

                    // Open DMs with the user
                    guild.getMemberById(targetMember.getId()).getUser().openPrivateChannel().queue(
                        privateChannel -> {

                            // Get the amount of messages
                            privateChannel.getHistory().retrievePast(amountToDelete).queue(
                                messages -> {

                                    // Delete and print the product
                                    for (Message msg : messages) {
                                        msg.delete().queue();
                                    }
                                }
                            );
                        }
                    );

                    event.reply("Successfully deleted DMs with the user " + targetMember.getAsMention()).setEphemeral(true).queue();
                }

                // SCAN_FILE scan a file
                case "scan_file" -> {
                    // The path of the file
                    String filePath = event.getOption("filepath").getAsString();
                    File folder = new File(filePath);
                    File[] listOfFiles = folder.listFiles();

                    try {
                        PrintStream outputFile = new PrintStream("output");

                        for (File file : listOfFiles) {
                            Scanner inputFile = new Scanner(file);
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
                case "order_details" -> {
                    String orderId = event.getOption("order_id").getAsString();
                    Member member = event.getOption("target_member").getAsMember();

                    // Open DMs with the user
                    guild.getMemberById(member.getId()).getUser().openPrivateChannel().queue(
                            privateChannel -> {

                                // Get the amount of messages
                                privateChannel.getHistory().retrievePast(100).queue(
                                    messages -> {

                                        // Delete and print the product
                                        for (Message msg : messages) {
                                            if(msg.getEmbeds().get(0).getFields().get(0).getValue().equalsIgnoreCase(orderId))
                                            {
                                                System.out.println(msg.getEmbeds().get(0).getFields().get(1).getValue());

                                                EmbedBuilder embedBuilder = new EmbedBuilder()
                                                        .setTitle("Retrieved Order")
                                                                .addField("Order ID", orderId, false)
                                                                        .addField("Product", msg.getEmbeds().get(0).getFields().get(1).getValue(), false);
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
