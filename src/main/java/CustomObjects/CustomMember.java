package CustomObjects;

import Bot.Embeds;
import Bot.SQLConnection;
import Shoppy.ShoppyConnection;
import Shoppy.ShoppyOrder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

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
        // The header which shows the store information
        embedBuilder
                .setColor(Color.GREEN)
                .addField("**Discord**", "https://discord.gg/vzwJz3NK7a", false)
                .addField("**Store**", "https://betteralts.com", false);

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
     * Handle unmute and mute by checking if the reason is null or not.
     * If the reason is null then it's an unmute. If not, it's a mute.
     * Stores the punishment record in the database
     *
     * @param reason The reason for the mute
     * @throws SQLException If there are any errors with updating the database
     */
    public void mute(String reason) throws SQLException {
        // Add the mute role to the user and alert the user they got muted
        Role muteRole = guild.getRoleById("936718165130481705");
        guild.addRoleToMember(member, muteRole).queue();

        // Check if it's an unmute or mute by check if the reason is null
        if(reason != null)
            sendPrivateMessage(Embeds.MUTED.addField("**Reason**", reason, false));
        else
            sendPrivateMessage(Embeds.UNMUTED);

        SQLConnection.updateMute(member, reason);

        SQLConnection.updatePunishment("mute", member, reason);
    }

    /**
     * Kicks a user and stores the punishment record in the database.
     *
     * @param reason The reason for kicking the member
     * @throws SQLException If there are any errors with updating the database
     */
    public void kick(String reason) throws SQLException {
        guild.kick(member, reason).queue();

        SQLConnection.updatePunishment("mute", member, reason);
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

        if (orderObject.getPaid_at() != null) {

            EmbedBuilder orderEmbed = new EmbedBuilder()
                    .setTitle("**Better Alts Order**")
                    .addField("**Order ID:**", orderID, false)
                    .setDescription(setOrderDescription(orderObject.getProduct().getTitle()));

            // If the product is out of stock then alert the customer
            if(accounts == null){
                orderEmbed.addField("Alts", "Out of Stock. Contact staff through a ticket", false);

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

        }

        // Add customer role to the user
        guild.addRoleToMember(member.getId(), guild.getRoleById("929116572063244339")).queue();

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
                    **Format:** email:emailPassword
                    **Minecraft Password:** Wanker!!22 or Vbkfybz22!
                    **Mail Site:** yahoo.com, mail.com or rambler.ru (Check the domain of your account)
                    **Security Questions:** a a a
                    **How to change email for Mojang Account:** https://www.youtube.com/watch?v=AAQFrR0ShNE
                    **How to change email for Microsoft Account:** https://www.youtube.com/watch?v=duowaqDnwdM
                    *If the minecraft password or security questions are incorrect then reset it since you have access to the email*
                    *If the account requires migration, then migrate it.*
                    """;
        }else if(accountType.toLowerCase().contains("Unbanned") ){
            return "Thank you for ordering " + accountType + "\n" +
                    "**Format:** email:password:username\n" +
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
