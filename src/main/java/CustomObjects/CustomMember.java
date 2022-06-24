package CustomObjects;

import Bot.BotProperty;
import Bot.SQLConnection;
import BotObjects.GuildObject;
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
    private Member member;
    private final Guild guild;

    /**
     * Constructor for the initializing the custom member
     *
     * @param jda The jda of the bot
     * @param memberId The member id for the custom member
     * @param guildId The guild id that the member is in
     */
    public CustomMember(JDA jda, String memberId, String guildId)
    {
        this.member = jda.getGuildById(guildId).getMemberById(memberId);
        this.guild = jda.getGuildById(guildId);
    }

    /**
     * Sends a member a private message
     *
     * @param embedBuilder The embed to send the member
     */
    public void sendPrivateMessage(EmbedBuilder embedBuilder)
    {
        // Open a private channel with the member and send the DM
        this.member.getUser().openPrivateChannel().flatMap(privateChannel ->
                privateChannel.sendMessageEmbeds(embedBuilder.build())
        ).queue();
    }

    /**
     * Bans a member and stores the punishment record in the database
     *
     * @param reason The reason for the ban
     * @param delDays The amount of days which messages should be deleted sent by the member
     * @param staffMember The staff member that banned the member
     */
    public void ban(String reason, int delDays,  Member staffMember) throws SQLException {
        // Store the embed for a ban for future usage
        EmbedBuilder banEmbed = Embeds.createPunishmentEmbed("BAN", reason, staffMember);

        // Alert the member they got banned
        sendPrivateMessage(banEmbed);

        // Ban the user
        this.guild.getMemberById(this.member.getId()).ban(delDays, reason).queue();

        // Insert the punishment into the "punishments" table in the database
        SQLConnection.insertPunishment("ban", this.member, reason, staffMember.getId());

        // Log the punishment in the discord server
        BotProperty.storeLog(guild, banEmbed, "Ban");
    }

    /**
     * Times out a member in the guild with the given argument from the slash command that will call this method
     *
     * @param reason The reason for the time
     * @param amount The duration of the timeout depending on the timeUnit
     * @param timeUnitString The timeUnitString which will be converted into a TimeUnit
     * @param staffMember The staff member that timed out the member
     * @throws SQLException Common SQL issues that must be caught
     */
    public void timeout(String reason, int amount, @Nonnull String timeUnitString, Member staffMember) throws SQLException
    {
        // Check if member is already timed out
        if(this.member.isTimedOut())
        {
            this.guild.removeTimeout(this.member).queue();
        }else{
            TimeUnit timeUnit;
            switch (timeUnitString)
            {
                case "minutes" -> timeUnit = TimeUnit.MINUTES;
                case "hours" -> timeUnit = TimeUnit.HOURS;
                case "days" -> timeUnit = TimeUnit.DAYS;
                default -> timeUnit = TimeUnit.SECONDS;
            }

            // Store the embed for a timeout embed for future usage
            EmbedBuilder timeoutEmbed = Embeds.createPunishmentEmbed("TIMEOUT", reason, staffMember);

            // Alert the member they have been warned
            sendPrivateMessage(timeoutEmbed);

            // Timeout the user from the guild
            this.guild.timeoutFor(this.member, amount, timeUnit).queue();

            // Insert the punishment into the "punishments" table in the database
            SQLConnection.insertPunishment("timeout", this.member, reason, staffMember.getId());

            // Log the punishment in the server
            BotProperty.storeLog(guild, timeoutEmbed, "TIMEOUT");
        }
    }

    /**
     * Sends a warning to a member, logs it and uploads it to the database
     *
     * @param reason The reason for a warning
     * @param staffMember The staff member who gave the warning
     * @throws SQLException Common SQL issues that must be caught
     */
    public void warn(String reason, Member staffMember) throws SQLException
    {
        // Store the embed for a warning embed for future usage
        EmbedBuilder warningEmbed = Embeds.createPunishmentEmbed("WARNING", reason, staffMember);

        // Alert the member they have been warned
        sendPrivateMessage(warningEmbed);

        // Insert the punishment into the "punishments" table in the database
        SQLConnection.insertPunishment("warn", this.member, reason, staffMember.getId());

        // Log the punishment in the server
        BotProperty.storeLog(guild, warningEmbed, "Warning");
    }

    /**
     * Send the product to the member with a description about the order
     *
     * @param orderId The order id for the order
     * @param productInfo The products the customer ordered
     * @throws FileNotFoundException Not found files are common so must be caught
     */
    public void sendProduct(String orderId, String ...productInfo) throws IOException, InterruptedException {

        GuildObject guildObject = BotProperty.guildsHashMap.get(guild.getId());

        CustomMember serverOwner = new CustomMember(guild.getJDA(), guildObject.getServerOwnerId(), guild.getId());

        // Variables for information about the order
        String productString;
        String productType;

        // Check if the product has not been retrieved
        if(productInfo == null) {

            // Get the order from Shoppy
            ShoppyOrder shoppyOrder = ShoppyConnection.getShoppyOrder(orderId);

            // Fill the information for the embed
            orderId = shoppyOrder.getId();
            productType = shoppyOrder.getProduct().getTitle();

            // Get the product
            productString = SQLConnection.getProductByName(guild.getId(), shoppyOrder.getProduct().getTitle(), shoppyOrder.getQuantity());

        // If the product has already been retrieved then set the product and product type for the order embed
        }else{
            productString = productInfo[0];
            productType = productInfo[1];
        }

        System.out.println(productType);

        EmbedBuilder orderEmbed = new EmbedBuilder()
                .setTitle("**Better Alts Order**")
                .addField("**Order ID:**", orderId, false)
                .addField("**Member:**", this.member.getAsMention(), false)
                .setDescription(setOrderDescription(productType));

        // If the product is out of stock then alert the customer
        if(productString == null){
            orderEmbed.addField("**Product**", "No stock! Don't worry contact the owner!", false);

            // If the product length is less than 1000 then put it in the embed
        }else if (productString.length() < 1000) {

            // Add products to the description
            if(productString.length() == 0)
                orderEmbed.addField("**Product**", "No Stock! Don't worry contact the owner!", false);
            else
                orderEmbed.addField("**Product**", "```" + productString + "```", false);
            sendPrivateMessage(orderEmbed);
            serverOwner.sendPrivateMessage(orderEmbed);

        // If the product length is greater than 1000 then put it in a file
        }else{
            sendPrivateMessage(orderEmbed);

            // Create a text file and add the product to it
            PrintStream outputFile = new PrintStream("output");
            outputFile.print(productString);

            // Send the customer and the owner
            member.getUser().openPrivateChannel().flatMap(privateChannel ->
                    privateChannel.sendFile(new File("output"), "product.txt")
            ).queue();

            serverOwner.getMember().getUser().openPrivateChannel().flatMap(privateChannel ->
                    privateChannel.sendFile(new File("output"), "product.txt")
            ).queue();
        }

        // Give the new customer the configured customer role
        guild.addRoleToMember(member.getId(), guild.getRoleById(guildObject.getCustomerRoleId())).queue();
    }

    /**
     * Get the member associated with this custom member
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

    public String setOrderDescription(String productType)
    {
        if(productType.toLowerCase().contains("mfa")) {
            return "Thank you for ordering " + productType + "\n" +
                    """
                    **Format:** email:emailPassword:MinecraftPassword
                    **Mail Site:** rambler.ru (Check the domain of your account)
                    **How to change email for Mojang Account:** https://www.youtube.com/watch?v=AAQFrR0ShNE
                    **How to change email for Microsoft Account:** https://www.youtube.com/watch?v=duowaqDnwdM
                    *If the minecraft password or security questions are incorrect then reset it since you have access to the email*
                    *If the account requires migration, then migrate it.*
                    """;
        }else if(productType.toLowerCase().contains("unbanned") ){
            return "Thank you for ordering " + productType +
                    "" +
                    "**Format:** email:password:username\n" +
                    "" +
                    "Use VyprVPN to avoid getting them security banned on Hypixel";
        }else if(productType.toLowerCase().contains("hypixel") || productType.toLowerCase().contains("nfa")){
            return "Thank you for ordering " + productType + "\n" +
                    "**Format:** email:password:username";
        }else if(productType.toLowerCase().contains("vypr")){
            return "Thank you for ordering " + productType + "\n" +
                    "**Format:** email:password\n" +
                    "**Note:** Don't change password or you may lose access fast";
        }else if(productType.contains("Yahoo")){
            return "Thank you for ordering " + productType + "\n" +
                    "**Format:** email:password | Total Mails: <number of mails>\n" +
                    "Log into the email at yahoo.com";
        }else{
            return  "There was an issue with the order, created a ticket in the discord server";
        }
    }
}
