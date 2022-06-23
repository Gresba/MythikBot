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
     */
    public void ban(String reason, int delDays)
    {
        // Ban the user
        this.guild.getMemberById(this.member.getId()).ban(delDays, reason).queue();

        // Alert the user they got banned
        sendPrivateMessage(Embeds.createPunishmentEmbed("BANNED", reason));
    }

    /**
     * Times out a member in the guild with the given argument from the slash command that will call this method
     *
     * @param reason The reason for the time
     * @param amount The duration of the timeout depending on the timeUnit
     * @param timeUnitString The timeUnitString which will be converted into a TimeUnit
     * @throws SQLException
     */
    public void timeout(String reason, int amount, @Nonnull String timeUnitString) throws SQLException {
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

            sendPrivateMessage(Embeds.createPunishmentEmbed("TIMEOUT", reason));
            this.guild.timeoutFor(this.member, amount, timeUnit).queue();

            SQLConnection.updatePunishment("timeout", this.member, reason);
        }
    }


    /**
     * Send the product to the member with a description about the order
     *
     * @param orderId The order id for the order
     * @param productInfo The products the customer ordered
     * @throws FileNotFoundException
     */
    public void sendProduct(String orderId, String ...productInfo) throws IOException, InterruptedException {

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

        EmbedBuilder orderEmbed = new EmbedBuilder()
                .setTitle("**Better Alts Order**")
                .addField("**Order ID:**", orderId, false)
                .addField("**Member:**", this.member.getAsMention(), false)
                .setDescription(setOrderDescription(productType));

        // If the product is out of stock then alert the customer
        if(productString == null){
            orderEmbed.addField("Alts", "No accounts in the database. Contact a staff", false);

            // If the product length is less than 1000 then put it in the embed
        }else if (productString.length() < 1000) {

            // Add products to the description
            if(productString.length() == 0)
                orderEmbed.addField("Alts", "No Stock! Don't worry contact the owner!", false);
            else
                orderEmbed.addField("Alts", "```" + productString + "```", false);
            sendPrivateMessage(orderEmbed);

            // If the product length is greater than 1000 then put it in a file
        }else{
            sendPrivateMessage(orderEmbed);

            // Create a text file and add the product to it
            PrintStream outputFile = new PrintStream("output");
            outputFile.print(productString);

            // Send the customer the product
            member.getUser().openPrivateChannel().flatMap(privateChannel ->
                    privateChannel.sendFile(new File("output"), "product.txt")
            ).queue();
        }
        guild.addRoleToMember(member.getId(), guild.getRoleById("929116572063244339")).queue();
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
