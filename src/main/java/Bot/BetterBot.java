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

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class BetterBot {

    public static void main(String[] args) throws SQLException{
        Connection connection = SQLConnection.getConnection();

        ResultSet guildInfo = SQLConnection.getGuildInfo();

        // Loading the information about a guild
        while(guildInfo.next())
        {
            // Populating the data of the guild
            String guildId = guildInfo.getString(1);
            String guildPrefix = guildInfo.getString(2);
            int guildTicketLimit = guildInfo.getInt(3);
            String guildOwner = guildInfo.getString(4);
            String ticketCategoryId = guildInfo.getString(5);
            String staffRoleId = guildInfo.getString(6);
            String logChannelId = guildInfo.getString(7);
            String customerRoleId = guildInfo.getString(8);
            String memberRoleId = guildInfo.getString(9);
            String joinChannelId = guildInfo.getString(10);
            String leaveChannelId = guildInfo.getString(11);

            GuildObject guildObject = new GuildObject(
                    guildPrefix,
                    guildTicketLimit,
                    guildOwner,
                    ticketCategoryId,
                    staffRoleId,
                    logChannelId,
                    customerRoleId,
                    memberRoleId,
                    joinChannelId,
                    leaveChannelId,
                    guildId);

            // Adding the guild for future usage
            BotProperty.guildsHashMap.put(guildId, guildObject);
        }

        connection.close();

        JDABuilder jdaBuilder = JDABuilder.createDefault(System.getenv("MYTHIK_BOT_API_KEY"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS);

        jdaBuilder.addEventListeners(
                new DeleteChannelEvent(),
                new MemberJoinGuildEvent(),
                new MessageAutoResponse(),
                new JoinGuildEvent(),
                new MemberLeaveGuildEvent(),
                new DeleteMessagesEvent(),
                new StaffSlashCommand(),
                new ButtonClick(),
                new UserSlashCommand(),
                new ModalInteractionEvent(),
                new OwnerSlashCommand(),
                new InviteCreateEvent(),
                new OptionSelectionEvent()
                );

        try {

            JDA jda = jdaBuilder.build().awaitReady();
            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            jda.getPresence().setActivity(Activity.playing("/help for help"));

            CommandListUpdateAction commands = jda.updateCommands();

            // USER accessible slash commands
            commands.addCommands(
                    Commands.slash("eth", "Get Mythik's Ethereum Address"),

                    Commands.slash("btc", "Get Mythik's Bitcoin Address"),

                    Commands.slash("ltc", "Get Mythik's Lite coin Address"),

                    Commands.slash("bch", "Get Mythik's Bitcoin Cash Address"),

                    Commands.slash("sol", "Get Mythik's Solana Address"),

                    Commands.slash("ada", "Get Mythik's Cardano Address"),

                    Commands.slash("venmo", "Get Mythik's Venmo Tag"),

                    Commands.slash("cash_app", "Get Mythik's CashApp Tag"),

                    Commands.slash("paypal", "Get Mythik's PayPal email"),

                    // HELP command
                    Commands.slash("help", "How to use the discord bot")
                            .addOptions(new OptionData(STRING, "category", "The category for help")
                                    .setRequired(false))
            );

            // STAFF accessible commands
            commands.addCommands(
                    // SEND_ALTS command
                    Commands.slash("send_product", "Send the product to a member")
                            .addOptions(new OptionData(USER, "target_member", "The member to send the product to", true))
                            .addOptions(new OptionData(STRING, "order_id", "The order id associated with this order", true))
                            .addOptions(new OptionData(STRING, "product_type", "The type of product to send", false))
                            .addOptions(new OptionData(INTEGER, "amount", "The amount to send", false))
                            .addOptions(new OptionData(STRING, "override", "If true, it will send even if the order id is already registered", false)),

                    // BAN command
                    Commands.slash("ban", "Ban a user from this server")
                            .addOptions(new OptionData(USER, "target_member", "The user to ban")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the ban")
                                    .setRequired(true))
                            .addOptions(new OptionData(INTEGER, "delete_days", "Messages to delete for the past days")
                                    .setRequired(true)),

                    // TIMEOUT command
                    Commands.slash("timeout", "Timeout a user")
                            .addOptions(new OptionData(USER, "target_member", "The user to timeout")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the timeout")
                                    .setRequired(true)),

                    // UN_TIMEOUT command
                    Commands.slash("un_timeout", "Un-timeout a user")
                            .addOptions(new OptionData(USER, "user", "The user to un-timeout")
                                    .setRequired(true)),

                    // WARN command
                    Commands.slash("warn", "Warn a user")
                            .addOptions(new OptionData(USER, "target_member", "The user to warn")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "reason", "The reason for the warning")
                                    .setRequired(true)),

                    // NUKE command
                    Commands.slash("nuke", "Nuke the channel"),

                    // AUTO_NUKE command
                    Commands.slash("auto_nuke", "Turn nuke a channel")
                            .addOptions(new OptionData(INTEGER, "minutes", "How many minutes")),

                    // ORDER command
                    Commands.slash("order", "View details about an order")
                            .addOptions(new OptionData(STRING, "order_id", "The order ID")
                                            .setRequired(true))
                            .addOptions(new OptionData(BOOLEAN, "on_off", "Turn auto-nuke on or off")
                                            .setRequired(false)),

                    // CARD_CHECK command
                    Commands.slash("card_check", "Send card check message")
                            .addOptions(new OptionData(STRING, "last_four_digits", "The last 4 digits of the card")
                                            .setRequired(true)),

                    // DELETE_RESPONSE command
                    Commands.slash("delete_response", "Delete a response to a trigger word")
                            .addOptions(new OptionData(STRING, "trigger_word", "The trigger words to delete response to")
                                    .setRequired(true)),

                    // CLOSE command
                    Commands.slash("close", "Close the ticket"),

                    // OPEN_TICKET command
                    Commands.slash("open_ticket", "Allow the creator of the ticket to speak")
                            .addOptions(new OptionData(USER, "target_user", "Person to open the ticket for", true)),

                    // DIRECTIONS_LIST command
                    Commands.slash("directions_list", "Get a list of all directions and what they are for"),

                    // DIRECTIONS command
                    Commands.slash("directions", "Get the directions for a specific direction")
                            .addOptions(new OptionData(STRING, "direction_name", "The direction you want to read", true, true)),

                    // STAFF_HELP command
                    Commands.slash("staff_help", "How to use staff features"),

                    // ADDUSER command
                    Commands.slash("add_user", "Add a user to the database")
                            .addOptions(new OptionData(USER, "member", "The member to add to the database", true)),

                    Commands.slash("configure", "Configure the discord server").addSubcommands(
                            new SubcommandData("server", "Configure server information (Bot Prefix, Server Owner)"),
                            new SubcommandData("tickets", "Configure ticket system (Ticket Category, Ticket Limit)"),
                            new SubcommandData("roles", "Configure roles (Staff Roles, Customer Role, Member Role)"),
                            new SubcommandData("channels", "Configure channels (Log Channel, Leave Channel, Join Channel)")
                    )
            );

            // OWNER Slash Commands
            commands.addCommands(
                    Commands.slash("scan_file", "Scan")
                            .addOptions(new OptionData(STRING, "filepath", "File path")
                                    .setRequired(true)),

                    // WHITELIST add/remove command
                    Commands.slash("whitelist", "Add/Remove member to the verification whitelist")
                            .addOptions(new OptionData(STRING, "action", "Add or remove a member")
                                    .setRequired(true))
                            .addOptions(new OptionData(USER, "target_member", "The member to add/remove")
                                    .setRequired(true)),

                    // UPLOAD command
                    Commands.slash("upload", "Upload a product to the database")
                            .addOptions(new OptionData(STRING, "product_type", "The type of product to upload")
                                    .setRequired(true))
                            .addOptions(new OptionData(ATTACHMENT, "input_file", "The file with the products in it")
                                    .setRequired(true)),

                    // ADD_RESPONSE command
                    Commands.slash("add_response", "Add a response to a trigger word")
                            .addOptions(new OptionData(STRING, "trigger", "The word(s) that will trigger the response")
                                    .setRequired(true))
                            .addOptions(new OptionData(STRING, "response", "The response that will be sent by the bot")
                                    .setRequired(true))
                            .addOptions(new OptionData(BOOLEAN, "delete_trigger", "Delete message containing the trigger word. Default to false")
                                    .setRequired(true))
                            .addOptions(new OptionData(BOOLEAN, "delete_if_contains","Respond if the message contains this word or character(s)")
                                    .setRequired(true)),


                    // DELETE_DM command
                    Commands.slash("delete_dm", "Delete DMs with the target member")
                            .addOptions(new OptionData(USER, "target_member", "The member to delete the DMs with")
                                    .setRequired(true))
                            .addOptions(new OptionData(INTEGER, "amount_to_delete", "The amount of messages to delete")
                                    .setRequired(true)),

                    // REMOVE_ORDER command
                    Commands.slash("remove_order", "Remove an order from the database")
                            .addOptions(new OptionData(STRING, "order_id", "The order id to remove from the database")
                                    .setRequired(true)),

                    // ORDER_DETAILS commands
                    Commands.slash("order_details", "Get the product info for an order already sent")
                            .addOptions(new OptionData(STRING, "order_id", "The order id you want to retrieve the product for")
                                    .setRequired(true))
                            .addOptions(new OptionData(USER, "target_member", "The members DMs to access")
                                    .setRequired(true))
            );

            jda.getGuildById("929101421272510524").updateCommands();

            commands.queue();
        } catch (LoginException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
