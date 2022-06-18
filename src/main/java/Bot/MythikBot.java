package Bot;

import BotCommands.*;
import BotObjects.GuildObject;
import Events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.sql.*;

import static Bot.SQLConnection.getStatement;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class MythikBot {

    private static JDA jda;
    public static void main(String[] args) throws SQLException{
        Statement statement = getStatement();

        ResultSet guildInfo = SQLConnection.getGuildInfo();

        // Loading the information about a guild
        while(guildInfo.next())
        {
            // Populating the data of the guild
            String guildId = guildInfo.getString(1);
            String guildPrefix = guildInfo.getString(2);
            int guildTicketLimit = guildInfo.getInt(3);
            String guildOwner = guildInfo.getString(4);

            GuildObject guildObject = new GuildObject(guildId, guildPrefix, guildTicketLimit, guildOwner);

            // Adding the guild for future usage
            BotProperty.guildsHashMap.put(guildId, guildObject);
        }

        JDABuilder jdaBuilder = JDABuilder.createDefault(System.getenv("MYTHIK_BOT_API_KEY"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS);

        jdaBuilder.addEventListeners(
                new DeleteChannelEvent(),
                new MemberJoinGuildEvent(),
                new UserCommand(),
                new MessageAutoResponse(),
                new DeleteMessageEvent(),
                new JoinGuildEvent(),
                new MemberLeaveGuildEvent(),
                new StaffSlashCommand(),
                new ButtonClick(),
                new UserSlashCommand(),
                new ModalInteractionEvent(),
                new OwnerSlashCommand(),
                new InviteCreateEvent(),
                new OptionSelectionEvent()
                );

        try {

            jda = jdaBuilder.build().awaitReady();
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            jda.getPresence().setActivity(Activity.playing("/help for help"));

            CommandListUpdateAction commands = jda.updateCommands();

            jda.getGuildById("929101421272510524").updateCommands();
            // USER accessible slash commands
            commands.addCommands(
                    Commands.slash("eth", "Get Mythik's Ethereum Address"),

                    Commands.slash("btc", "Get Mythik's Bitcoin Address"),

                    Commands.slash("ltc", "Get Mythik's Litecoin Address"),

                    Commands.slash("bch", "Get Mythik's Bitcoin Cash Address"),

                    Commands.slash("sol", "Get Mythik's Solana Address"),

                    Commands.slash("ada", "Get Mythik's Cardano Address"),

                    Commands.slash("venmo", "Get Mythik's Venmo Tag"),

                    Commands.slash("cashapp", "Get Mythik's CashApp Tag"),

                    Commands.slash("paypal", "Get Mythik's PayPal email"),

                    // HELP command
                    Commands.slash("help", "How to use the discord bot")
                            .addOptions(new OptionData(STRING, "category", "The category for help")
                                    .setRequired(false))
            );

            // STAFF accessible commands
            commands.addCommands(
                    // SEND_ALTS command
                    Commands.slash("send_alts", "Send alts to a user")
                            .addOptions(new OptionData(USER, "target_member", "The member to send the alts to")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "account_type", "The type of account to send")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "order_id", "The order id associated with this order")
                                    .setRequired(true))
                            .addOptions(new OptionData(INTEGER, "amount", "The amount to send")
                                    .setRequired(true)),

                    // BAN command
                    Commands.slash("ban", "Ban a user from this server")
                            .addOptions(new OptionData(USER, "user", "The user to ban")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the ban")
                                    .setRequired(true))
                            .addOptions(new OptionData(INTEGER, "delete_days", "Messages to delete for the past days")
                                    .setRequired(true)),

                    // TIMEOUT command
                    Commands.slash("timeout", "Timeout a user")
                            .addOptions(new OptionData(USER, "user", "The user to timeout")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the timeout")
                                    .setRequired(true)),

                    // UNTIMEOUT command
                    Commands.slash("untimeout", "Un-timeout a user")
                            .addOptions(new OptionData(USER, "user", "The user to un-timeout")
                                    .setRequired(true)),

                    // KICK command
                    Commands.slash("kick", "Kick a user")
                            .addOptions(new OptionData(USER, "user", "The user to kick")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the kick")
                                    .setRequired(true)),

                    // WARN command
                    Commands.slash("warn", "Warn a user")
                            .addOptions(new OptionData(USER, "user", "The user to warn")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the warning")
                                    .setRequired(true)),

                    // NUKE command
                    Commands.slash("nuke", "Nuke the channel"),

                    // AUTONUKE command
                    Commands.slash("autonuke", "Turn nuke a channel")
                            .addOptions(new OptionData(INTEGER, "minutes", "How many minutes")),

                    // ORDER command
                    Commands.slash("order", "View details about an order")
                            .addOptions(new OptionData(STRING, "order_id", "The order ID")
                                            .setRequired(true))
                            .addOptions(new OptionData(BOOLEAN, "on_off", "Turn auto-nuke on or off")
                                            .setRequired(false)),

                    // CARDCHECK command
                    Commands.slash("cardcheck", "Send card check message")
                            .addOptions(new OptionData(STRING, "last_four_digits", "The last 4 digits of the card")
                                            .setRequired(true)),

                    // DELETERESPONSE command
                    Commands.slash("deleteresponse", "Delete a response to a trigger word")
                            .addOptions(new OptionData(STRING, "trigger_word", "The trigger words to delete response to")
                                    .setRequired(true)),

                    // CLOSE command
                    Commands.slash("close", "Close the ticket"),

                    // OPENTICKET command
                    Commands.slash("openticket", "Allow the creator of the ticket to speak")
                            .addOptions(new OptionData(USER, "target_user", "Person to open the ticket for", true)),
                    // SEND command
                    Commands.slash("send", "Send a product to a user")
                            .addOptions(new OptionData(STRING, "order_id", "The order ID", true))
                            .addOptions(new OptionData(USER, "target_user", "Person to send it to", true)),

                    // DIRECTIONSLIST command
                    Commands.slash("directionslist", "Get a list of all directions and what they are for"),

                    // DIRECTIONS command
                    Commands.slash("directions", "Get the directions for a specific direction")
                            .addOptions(new OptionData(STRING, "direction_name", "The direction you want to read", true, true)),

                    // STAFFHELP command
                    Commands.slash("staffhelp", "How to use staff features"),

                    // ADDUSER command
                    Commands.slash("add_user", "Add a user to the database")
                            .addOptions(new OptionData(USER, "member", "The member to add to the database", true)),

                    Commands.slash("configure_server", "Configure the discord server")
            );

            // OWNER Slash Commands
            commands.addCommands(
                    // GENERATETICKET command
                    Commands.slash("generateticket", "Generate a ticket in the channel you run this command."),

                    Commands.slash("scanfile", "Scan")
                            .addOptions(new OptionData(STRING, "filepath", "File path")
                                    .setRequired(true)),
                    // REPLACE command
                    Commands.slash("replace", "Send replacements for an order")
                            .addOptions(new OptionData(STRING, "order_id", "The order ID")
                                    .setRequired(true))
                            .addOptions(new OptionData(USER, "target_user", "Person to send it to")
                                    .setRequired(true))
                            .addOptions(new OptionData(INTEGER, "replacement_amount", "Amount of replacements to send")
                                    .setRequired(true)),

                    // WHITELIST add/remove command
                    Commands.slash("whitelist", "Add/Remove member to the verification whitelist")
                            .addOptions(new OptionData(STRING, "action", "Add or remove a member")
                                    .setRequired(true))
                            .addOptions(new OptionData(USER, "target_member", "The member to add/remove")
                                    .setRequired(true)),

                    // UPLOAD command
                    Commands.slash("upload", "Upload a product to the database")
                            .addOptions(new OptionData(STRING, "account_type", "The type of product to upload")
                                    .setRequired(true))
                            .addOptions(new OptionData(ATTACHMENT, "input_file", "The file with the products in it")
                                    .setRequired(true)),

                    // ADDRESPONSE command
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

                    // DELETEDM command
                    Commands.slash("deletedm", "Delete DMs with the target member")
                            .addOptions(new OptionData(USER, "target_member", "The member to delete the DMs with")
                                    .setRequired(true))
                            .addOptions(new OptionData(INTEGER, "amount_to_delete", "The amount of messages to delete")
                                    .setRequired(true)),

                    // REMOVEORDER command
                    Commands.slash("removeorder", "Remove an order from the database")
                            .addOptions(new OptionData(STRING, "order_id", "The order id to remove from the database")
                                    .setRequired(true)),

                    // ORDER_DETAILS commands
                    Commands.slash("orderdetails", "Get the accounts already sent")
                            .addOptions(new OptionData(STRING, "order_id", "The order id you want to retrieve the accounts for")
                                    .setRequired(true))
                            .addOptions(new OptionData(USER, "target_member", "The members DMs to access")
                                    .setRequired(true)),
                    Commands.slash("rolesembed", "Add embed")

            );

            jda.getGuildById("929101421272510524").updateCommands();

            commands.queue();
        } catch (LoginException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
