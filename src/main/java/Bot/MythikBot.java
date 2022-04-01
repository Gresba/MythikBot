package Bot;

import Commands.*;
import Events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.*;

import static Bot.SQLConnection.getStatement;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class MythikBot {

    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        Statement statement = getStatement();

        String importGuilds = "SELECT GuildID FROM Guilds";

        ResultSet guildsResults = statement.executeQuery(importGuilds);

        while(guildsResults.next())
        {
            Statement statement2 = getStatement();

            String guildId = guildsResults.getString(1);

            String importGuildResponses = "SELECT * FROM Responses WHERE GuildId = '" + guildId + "'";
            ResultSet responsesResults = statement2.executeQuery(importGuildResponses);

            while (responsesResults.next())
            {
                String triggerWord = responsesResults.getString(1);
                String response = responsesResults.getString(2);
                boolean deleteMsg = responsesResults.getBoolean(3);
                int deleteMsgDelay = responsesResults.getInt(4);
                String containsOrMatches = responsesResults.getString(5);

                try {
                    // Populating response members
//                    Response responseObj = new Response(containsOrMatches, response, deleteMsg, deleteMsgDelay);

//                    BotProperty.getResponseHashMap().put(triggerWord, responseObj);
                } catch (Exception e){
                    System.out.println("[Bot Log]: Error with inserting responses into ResponseHashMap");
                    e.printStackTrace();
                }
            }
            statement2.close();
        }

        JDABuilder jdaBuilder = JDABuilder.createDefault(System.getenv("MYTHIK_BOT_API_KEY"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS);;

        JDA jda = null;

        jdaBuilder.addEventListeners(
                new channelDeleteEvent(),
                new adminCommand(),
                new reactionEvent(),
                new memberJoinGuildEvent(),
                new userCommand(),
                new messageAutoResponse(),
                new deleteMessageEvent(),
                new guildJoinEvent(),
                new memberLeaveGuildEvent(),
                new StaffSlash(),
                new buttonClick()
                );

        try {

            jda = jdaBuilder.build();
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            jda.getPresence().setActivity(Activity.playing("/help for help"));

            CommandListUpdateAction commands = jda.updateCommands();

            // User accessible slash commands
            commands.addCommands(
                    Commands.slash("eth", "Get Mythik's Ethereum Address"),

                    Commands.slash("btc", "Get Mythik's Bitcoin Address"),

                    Commands.slash("ltc", "Get Mythik's Litecoin Address"),

                    Commands.slash("bch", "Get Mythik's Bitcoin Cash Address"),

                    Commands.slash("sol", "Get Mythik's Solana Address"),

                    Commands.slash("ada", "Get Mythik's Cardano Address"),

                    Commands.slash("venmo", "Get Mythik's Venmo Tag"),

                    Commands.slash("cashapp", "Get Mythik's CashApp Tag")
            );

            commands.addCommands(
                    Commands.slash("nuke", "Nuke the channel"),

                    Commands.slash("autonuke", "Turn nuke a channel")
                            .addOptions(new OptionData(INTEGER, "minutes", "How many minutes")),

                    Commands.slash("generateticket", "Generate a ticket in the channel you run this command."),

                    Commands.slash("mute", "Mute a user in the server. Requires administrator permissions."),

                    Commands.slash("order", "View details about an order")
                            .addOptions(new OptionData(STRING, "order_id", "The order ID")
                                            .setRequired(true))
                            .addOptions(new OptionData(BOOLEAN, "on_off", "Turn auto-nuke on or off")
                                            .setRequired(false)),

                    Commands.slash("cardcheck", "Send card check message")
                            .addOptions(new OptionData(STRING, "last_four_digits", "The last 4 digits of the card")
                                            .setRequired(true)),

                    Commands.slash("addresponse", "Add a response to a trigger word")
                            .addOptions(new OptionData(STRING, "trigger", "The word(s) that will trigger the response")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "response", "The response that the word(s) will trigger")
                                    .setRequired(true))
                            .addOptions(new OptionData(BOOLEAN, "delete_trigger", "Delete message containing the trigger word. Default to false")
                                    .setRequired(false))
                            .addOptions(new OptionData(BOOLEAN, "delete_response","Delete the response. Default to false")
                                    .setRequired(false))
                            .addOptions(new OptionData(INTEGER, "delete_delay", "How long in seconds to delete the response. Default 0")
                                    .setRequired(false))
                            .addOptions(new OptionData(BOOLEAN, "direct_match", "Does the trigger word have to directly match or be in the message. Default false")
                                    .setRequired(false)),

                    Commands.slash("deleteresponse", "Delete a response to a trigger word")
                            .addOptions(new OptionData(STRING, "trigger_word", "The trigger words to delete response to")
                                    .setRequired(true)),

                    Commands.slash("close", "Close the ticket"),

                    Commands.slash("openticket", "Allow the creator of the ticket to speak")
                            .addOptions(new OptionData(USER, "target_user", "Person to open the ticket for")
                                    .setRequired(true)),
                    Commands.slash("send", "Send a product to a user")
                            .addOptions(new OptionData(STRING, "product_name", "The name of the product")
                                    .setRequired(true))
                            .addOptions(new OptionData(INTEGER, "amount", "The amount to send")
                                    .setRequired(true))
                            .addOptions(new OptionData(USER, "target_user", "Person to send it to")
                                    .setRequired(true)),
                    Commands.slash("help", "How to use the discord bot")
                            .addOptions(new OptionData(STRING, "category", "The category for help")
                                    .setRequired(false))
            );

            // Moderation Slash Commands
            commands.addCommands(
                    // Ban command
                    Commands.slash("ban", "Ban a user from this server")
                            .addOptions(new OptionData(USER, "user", "The user to ban") // USER type allows to include members of the server or other users by id
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the ban")
                                    .setRequired(true))
                            .addOptions(new OptionData(INTEGER, "delete_days", "Messages to delete for the past days")
                                    .setRequired(true)),

                    // Mute command
                    Commands.slash("mute", "Mute a user")
                            .addOptions(new OptionData(USER, "user", "The user to mute")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the mute")
                                    .setRequired(true)),

                    // Unmute command
                    Commands.slash("unmute", "Unmute a user")
                            .addOptions(new OptionData(USER, "user", "The user to unmute")
                                    .setRequired(true)),

                    // Kick command
                    Commands.slash("kick", "Kick a user")
                            .addOptions(new OptionData(USER, "user", "The user to kick")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the kick")
                                    .setRequired(true)),

                    // Warn command
                    Commands.slash("warn", "Warn a user")
                            .addOptions(new OptionData(USER, "user", "The user to warn")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the warning")
                                    .setRequired(true))

            );

            commands.queue();
        } catch (LoginException exception) {
            exception.printStackTrace();
        }
    }
}
