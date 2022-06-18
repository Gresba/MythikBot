package CustomObjects;

import Bot.SQLConnection;
import Shoppy.ShoppyConnection;
import Shoppy.ShoppyOrder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class CustomMember {
    private JDA jda;
    private Member member;
    private Guild guild;

    /**
     * Constuctor for the initializing the custom member
     *
     * @param jda The jda of the bot
     * @param memberId The member id for the custom member
     * @param guildId The guild id that the member is in
     */
    public CustomMember(JDA jda, String memberId, String guildId)
    {
        this.jda = jda;
        member = jda.getGuildById(guildId).getMemberById(memberId);
        guild = jda.getGuildById(guildId);
    }

    /**
     * Sends a member a private message
     *
     * @param embedBuilder The embed to send the member
     */
    public void sendPrivateMessage(EmbedBuilder embedBuilder)
    {
        // Open a private channel with the member and send the DM
        member.getUser().openPrivateChannel().flatMap(privateChannel ->
                privateChannel.sendMessageEmbeds(embedBuilder.build())
        ).queue();
    }

    /**
     * Bans a member and stores the punishment record in the database
     *
     * @param reason The reason for the ban
     * @param delDays The amount of days which messages should be deleted sent by the member
     */
    public void ban(String reason, int delDays)
    {
        // Ban the user
        guild.getMemberById(this.member.getId()).ban(delDays, reason).queue();

        // Alert the user they got banned
        sendPrivateMessage(Embeds.BANNED
                .addField("**Reason**", reason, false)
        );
    }

    /**
     * Times out a server in the guild with the given argument from the slash command that will call this method
     *
     * @param reason The reason for the time
     * @param amount The duration of the timeout depending on the timeUnit
     * @param timeUnitString The timeUnitString which will be converted into a TimeUnit
     * @throws SQLException
     */
    public void timeout(String reason, int amount, @Nonnull String timeUnitString) throws SQLException {
        // Check if member is already timed out
        if(member.isTimedOut())
        {
            // If they are then untime them out
            guild.removeTimeout(member).queue();
        }else{
            TimeUnit timeUnit;
            switch (timeUnitString)
            {
                case "minutes" ->
                        timeUnit = TimeUnit.MINUTES;
                case "hours" ->
                        timeUnit = TimeUnit.HOURS;
                case "days" ->
                        timeUnit = TimeUnit.DAYS;
                default ->
                        timeUnit = TimeUnit.SECONDS;
            }

            guild.timeoutFor(member, amount, timeUnit).queue();

            SQLConnection.updatePunishment("timeout", member, reason);
        }
    }


    /**
     *
     */
    public void sendAlts(String accounts, String orderId, String accountType) throws FileNotFoundException {
        EmbedBuilder orderEmbed = new EmbedBuilder()
                .setTitle("**Better Alts Order**")
                .addField("**Order ID:**", orderId, false)
                .setDescription(setOrderDescription(accountType));

        // If the product is out of stock then alert the customer
        if(accounts == null){
            orderEmbed.addField("Alts", "No accounts in the database. Contact a staff", false);

            // If the product length is less than 1000 then put it in the embed
        }else if (accounts.length() < 1000) {

            // Add products to the description
            if(accounts.length() == 0)
                orderEmbed.addField("Alts", "No Stock! Don't worry contact the owner!", false);
            else
                orderEmbed.addField("Alts", "```" + accounts + "```", false);
            sendPrivateMessage(orderEmbed);

            // If the product length is greater than 1000 then put it in a file
        }else{
            sendPrivateMessage(orderEmbed);

            // Create a text file and add the product to it
            PrintStream outputFile = new PrintStream("output");
            outputFile.print(accounts);

            // Send the customer the product
            member.getUser().openPrivateChannel().flatMap(privateChannel ->
                    privateChannel.sendFile(new File("output"), "product.txt")
            ).queue();
        }
        guild.addRoleToMember(member.getId(), guild.getRoleById("929116572063244339")).queue();
    }

    /**
     * Send a product to a user
     *
     * @param orderID The order ID for the product
     * @return Whether the accounts were successfully sent or not
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean sendProduct(String orderID, String accounts) throws IOException, InterruptedException {
        ShoppyOrder orderObject = ShoppyConnection.getShoppyOrder(orderID);

        // Check if the account was paid for
        if (orderObject.getPaid_at() != null) {

            EmbedBuilder orderEmbed = new EmbedBuilder()
                    .setTitle("**Better Alts Order**")
                    .addField("**Order ID:**", orderID, false)
                    .setDescription(setOrderDescription(orderObject.getProduct().getTitle()));

            // If the product is out of stock then alert the customer
            if(accounts == null){
                orderEmbed.addField("Alts", "No accounts in the database. Contact a staff", false);

            // If the product length is less than 1000 then put it in the embed
            }else if (accounts.length() < 1000) {

                // Add products to the description
                orderEmbed.addField("Alts", "```" + accounts + "```", false);
                sendPrivateMessage(orderEmbed);

            // If the product length is greater than 1000 then put it in a file
            }else{
                sendPrivateMessage(orderEmbed);
                // Create a text file and add the product to it
                PrintStream outputFile = new PrintStream("output");
                outputFile.print(accounts);

                // Send the customer the product
                member.getUser().openPrivateChannel().flatMap(privateChannel ->
                        privateChannel.sendFile(new File("output"), "product.txt")
                ).queue();
            }
            guild.addRoleToMember(member.getId(), guild.getRoleById("929116572063244339")).queue();

            return true;
        }

        return false;
    }

    /**
     * Get a member
     *
     * @return The custom member
     */
    public Member getMember()
    {
        return this.member;
    }

    /**
     * Set the member to a new member
     *
     * @param memberId The new memberId
     */
    public void setMember(String memberId)
    {
        this.member =  guild.getMemberById(memberId);
    }

    public String setOrderDescription(String accountType)
    {
        if(accountType.toLowerCase().contains("mfa")) {
            return "Thank you for ordering " + accountType + "\n" +
                    """
                    **Format:** email:emailPassword:MinecraftPassword
                    **Mail Site:** rambler.ru (Check the domain of your account)
                    **How to change email for Mojang Account:** https://www.youtube.com/watch?v=AAQFrR0ShNE
                    **How to change email for Microsoft Account:** https://www.youtube.com/watch?v=duowaqDnwdM
                    *If the minecraft password or security questions are incorrect then reset it since you have access to the email*
                    *If the account requires migration, then migrate it.*
                    """;
        }else if(accountType.toLowerCase().contains("unbanned") ){
            return "Thank you for ordering " + accountType +
                    "" +
                    "**Format:** email:password:username\n" +
                    "" +
                    "Use VyprVPN to avoid getting them security banned on Hypixel";
        }else if(accountType.toLowerCase().contains("hypixel") || accountType.toLowerCase().contains("nfa")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password:username";
        }else if(accountType.toLowerCase().contains("vypr")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password\n" +
                    "**Note:** Don't change password or you may lose access fast";
        }else if(accountType.contains("Yahoo")){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password | Total Mails: <number of mails>\n" +
                    "Log into the email at yahoo.com";
        }else{
            return  "There was an issue with the order, created a ticket in the discord server";
        }
    }
}
