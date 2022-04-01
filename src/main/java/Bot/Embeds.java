package Bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;

public enum Embeds {
    // General Embeds
    HELP,
    RULES,
    RESTOCKALERTS,
    SKYBLOCK,
    GIVEAWAY,
    FAQ,
    AUTONUKE,

    // Ticket Embed
    SPONSORSHIP,
    ORDERCLAIM,
    ORDERREPLACEMENTAMOUNT,
    PAYMENTMETHODS,
    TICKETCLOSED,

    // Moderation
    KICK,
    WARNING,
    MUTE,
    UNMUTE,
    BAN,
    ALTDETECTION,

    // Generator
    GENSUCCESS,
    GENFAIL,
    GENERATOR;


    /**
     * Send a private message with a specified embed to a member
     *
     * @param embedType Type of embed to send
     * @param member Member to send the message to
     * @param args Optional Strings to pass
     */
    public static void sendEmbed(Embeds embedType, Member member, String... args)
    {
        MessageEmbed messageEmbed;
        if(args.length == 0)
            messageEmbed = embed(embedType);
        else
            messageEmbed = embed(embedType, args[0]);

        member.getUser().openPrivateChannel().flatMap(privateChannel ->
            privateChannel.sendMessageEmbeds(messageEmbed)
        ).queue();
    }

    /**
     * Send a message with a specific embed to a channel
     *
     * @param embedType The type  to send
     * @param textChannel Channel to send the message to
     */
    public static void sendEmbed(Embeds embedType, TextChannel textChannel, String... args)
    {
        MessageEmbed messageEmbed;
        System.out.println(args);

        if(args.length == 0)
            messageEmbed = embed(embedType);
        else
            messageEmbed = embed(embedType, args[0]);

        textChannel.sendMessageEmbeds(messageEmbed).queue();
    }

    /**
     * Creates an EmbedBuilder with preset properties. No arguments passed in.
     *
     * @param embed Type of Embed to return
     * @return Returns an EmbedBuilder
     */
    public static MessageEmbed embed(Embeds embed)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder()
            .addField("**Discord**", "https://discord.gg/vzwJz3NK7a", false)
            .addField("**Store**", "https://betteralts.com", false);
        switch (embed) {
            // Embed for the message that is sent to the user when the ticket is closed
            case TICKETCLOSED -> embedBuilder
                    .setTitle("Better Alts Tickets")
                    .setColor(Color.RED)
                    .setDescription("Your ticket has been closed. If you have anymore issues then please make another ticket!");

            // Embed that is sent to a channel after a channel is nuked
            case AUTONUKE -> embedBuilder
                    .setTitle("Better Alts Moderation")
                    .setDescription("Channel is nuked every 30 minutes. Create a ticket if you have any questions")
                    .setColor(Color.BLACK);

            // Embed that is used to give members the [Giveaway] role
            case GIVEAWAY -> embedBuilder
                    .setTitle("Giveaway Role")

                    .addField("React to be alerted for future giveaways\n",
                            "Giveaway Role - :gift:\n", true)
                    .addField("Note",
                            "*No replacements for giveaways*", false)
                    .addField("**Remove role**", "*If you want to remove this role from yourself react then unreact to this message*", false)
                    .setColor(Color.ORANGE)
                    .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG");

            // Embed that is used to give members restock roles
            case RESTOCKALERTS -> embedBuilder
                    .setTitle("Restock Roles")
                    .addField("React to get alerts for the specific product",
                            """
                                    MFA - :e_mail:
                                    Skyblock - :cloud:
                                    Hypixel LvL21+/Ranked - :evergreen_tree:
                                    Hypixel Unbanned NFA/SFA Mix - :heart:
                                    NFA Banned - :red_square:
                                    Minecon - :grinning:
                                    VyprVPN - :alien:
                                    Special MFA - :heart_eyes_cat:
                                    Migrated Unbanned NFA - :white_check_mark:
                                    Migrated Banned NFA - :peach:
                                    Discord Nitro - :sparkles:
                                    Yahoo FA - :yin_yang:""", false)
                    .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG")
                    .setColor(Color.CYAN);

            // Embed that is used to display skyblock coin prices
            case SKYBLOCK ->
                    embedBuilder
                            .setTitle("Skyblock Coins")
                            .addField("**Prices**",
                                    """
                                            100+ million: $0.18/mil
                                            300+ million: $0.16/mil
                                            500+ million: $0.14/mil
                                            1 billion+: $0.13/mil""", false)
                            .addField("**Payment Methods**",
                                    """
                                            Crypto (Any Type)
                                            Debit/Credit/Apple Pay/Google Pay (Through Stripe)
                                            CashApp/Venmo/Zelle
                                                                                
                                            **PayPal**
                                            To use PayPal contact @Lemons or @Flipfudge""", false)
                            .addField("**How to buy**", "Open a ticket and click Purchase", false)
                            .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG")
                            .setColor(Color.YELLOW);

            // Embed that is sent to tell a user they don't have permissions to run a command
            case GENFAIL -> embedBuilder
                    .setTitle("**Better Alts Account Gen**")
                    .setDescription("""
                            You do not have permissions to run this command!

                            **Gen Requirements:**
                            Youtuber/Partner **or**\s
                            $15 for the gen needed! Make a ticket for this!
                            *If you paid then make a ticket and send order ID*""")
                    .setColor(Color.RED);
            // Embed that is sent to a channel when a user successfully generates an account
            case GENSUCCESS -> embedBuilder
                    .setTitle("**Better Alts Account Gen**")
                    .setDescription("Unbanned NFA sent to your DMs! Enjoy :)")
                    .setColor(Color.GREEN);

            // Embed that is used to show frequent asked questions
            case FAQ -> embedBuilder
                    .setTitle("Frequently Asked Questions")
                    .setColor(Color.CYAN)
                    .setDescription("If this answers your question then please close the ticket")
                    .addField("**Nitro**", "We do not accept nitro as any sort of payment and it can not be used to gain any types of rewards.", false)
                    .addField("**PayPal**", "To pay with PayPal you **must** pay with PayPal friends and family", false)
                    .addField("**Restocked**", "There is no estimated time of restock for any product. To be alert for restocks go to #store and react to the proper message", false)
                    .addField("**Warranty**", """
                            All products have a 24 hour warranty. Except MFAs which have a 1 hour warranty.
                            During that warranty you have to confirm whether your products are working or we can not give you any reimbursements.
                            Leaving or commenting any negative feedback in any way before contact support about the issue will remove any warranty.
                            """, false);

            // Embed that is sent to a user when they are marked as an alt
            case ALTDETECTION -> embedBuilder
                    .setTitle("Better Alts Security")
                    .setColor(Color.BLACK)
                    .setDescription("""
                            Your account has been quarantined because it was detected as a bot or an alt account.
                            You will be able to join in 24 hours
                            """)
                    .setFooter("Better Alts Security");

            // Embed that is sent to a channel whenever a member needs help with the bot
            case HELP -> embedBuilder
                    .setTitle("**Better Alts Help**")
                    .setColor(Color.CYAN)
                    .setDescription("Tutorial for using the Better Alts bot")
                    .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG")
                    .addField("**Staff Commands**", "Run /staffcommands to view staff commands", false)
                    .addField("**User Commands**", "Run /usercommands to view user commands", false)
                    .addField("**Customer Commands**", "Run /customercommands to view customer commands", false)
                    .addField("**Frequently Asked Questions**", "Run /faq to view questions that might have been answered", false)
                    .addField("**Questions/Problems**", "Create a ticket if you have any questions or problems", false);

            // Embed that is sent to the user to display sponsorship/partnership requirements
            case SPONSORSHIP -> embedBuilder
                    .setColor(Color.RED)
                    .setTitle("Better Alts Ticket")
                    .setDescription("Youtuber/Partnership Requirements")
                    .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG")
                    .addField("**Youtuber:**", """
                                - 3 Unbanned NFAs for every 100 Views
                                - If you have more than 1k views per video. $7 per video.
                                - Rewards are given after the video is posted
                                
                                **Video Requirements**
                                - https://betteralts.com and an invite to the discord server must be in the video description
                                - It must be **clear** that you are being sponsored by Better Alts
                            """, false)
                    .addField("**Partnership:**", """
                                - Server with 1,000+ channel members
                                - Real communities or shops, not join for rewards server
                            """, false)
                    .addField("**What to do now:**", """
                                - If you can't meet the requirements, then close this ticket
                                - If you can then send the invite to the server or publish the video and send the link to the video.
                            """, false);

            // Embed that is sent to a ticket channel after they submit their order ID
            case ORDERCLAIM -> embedBuilder
                    .setTitle("Better Alts Ticket")
                    .setColor(Color.GREEN)
                    .setDescription("Thank you for submitting you information. A staff will be with you shortly")
                    .addField("**Note**", """
                            We have disabled your ability to send messages.
                            This is not a bug and is done to ensure the best results for staff and customer.
                            Please bare with us.
                            """, false);

            // Embed that is sent to a ticket channel to ask for the amount of replacements they need
            case ORDERREPLACEMENTAMOUNT -> embedBuilder
                    .setTitle("Better Alts Ticket")
                    .setColor(Color.GREEN)
                    .setDescription("Please enter the amount of replacements you need")
                    .addField("**Note**",
                            """
                                    Be aware if you are caught lying you will receive no replacements or refunds." +
                                    So make sure you submit accurate and honest information.""", false);

            // Embed that is sent to a ticket channel to show members the current payment methods
            case PAYMENTMETHODS -> embedBuilder
                    .setTitle("Better Alts Tickets")
                    .setColor(Color.BLUE)
                    .setDescription("Please choose a payment method")
                    .addField("**Payment Methods**",
                            """
                                    Crypto (Any Type)
                                    Debit/Credit
                                    CashApp/Venmo/Zelle/Google Pay/Apple Pay
                                    Amazon Gift Card (40% Fee)
                                              
                                    **PayPal**
                                    To use PayPal contact @Lemons or @Flipfudge""", false);

            // Embed to show the server rules
            case RULES -> embedBuilder
                    .setTitle("**__Official Better Alts Server Rules__**")
                    .setDescription("Read and follow all rules")
                    .setColor(Color.BLACK)

                    .addField("**General**",
                            """
                                    **1.1)** No talking about selling/buying products that are not Mythiks/BetterAlts
                                    **1.2)** No arguing with staff members
                                    **1.3)** No spamming/ghost pinging
                                    **1.4)** No releasing anyone's information without consent**1.5)** No sending NSFW or gore contents
                                    **1.6)** No sending anything that will discourage users from buying
                                    **1.7)** No advertising unless you have permission
                                    **1.8)** No pinging staff members unless it's about an order
                                    **1.9)** No talking about any illegal activity
                                    **1.10)** No mass pinging
                                    **1.11)** No talking about other shops
                                    **1.12)** Use channels according to their purpose
                                    **1.13)** Don't ask for free stuff""", false)
                    .addField("**Invites**",
                            """
                                    **2.1)** No creating fake accounts for giveaways or invite rewards
                                    **2.2)** No j4j invites for rewards
                                    **2.3)** Invite source must be shown for rewards
                                    **2.4)** At least one invited member must be a customer for rewards
                                    **2.5)** One day to cash in rewards""", false)
                    .addField("**Purchases**",
                            "**3.1)** Don't leave bad feedback in chat or store before talking to us\n" +
                                    "*This may result in no replacements/refunds*", false)
                    .addField("**Punishments**",
                            """
                                    - Ban
                                    - Mute
                                    - Warning
                                    - Order Taken Away
                                    *You may receive any of these punishments for breaking a rule*""", false)
                    .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG");

            // Embed that is sent to a user when they get UNMUTED
            case UNMUTE -> embedBuilder
                    .setTitle("**Better Alts Moderation**")
                    .setDescription("You have been **UNNMUTED**!")
                    .setFooter("Better Alts Moderation");
        }
        return embedBuilder.build();
    }

    /**
     * Creates an EmbedBuilder with preset properties. String arg passed in
     *
     * @param embed Type of Embed to return
     * @return Returns an EmbedBuilder
     */
    public static MessageEmbed embed(Embeds embed, String args)
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        switch (embed) {

            // Embed that is sent to a user when they get KICKED
            case KICK -> embedBuilder
                    .setTitle("**Better Alts Moderation**")
                    .setDescription("You have been **KICKED**!")
                    .addField("**Reason: **", args, false)
                    .setFooter("Better Alts Moderation");

            // Embed that is sent to a user when they get WARNED
            case WARNING -> embedBuilder
                    .setTitle("**Better Alts Moderation**")
                    .setDescription("You have been **WARNED!**")
                    .addField("**Reason: **", args, false)
                    .setFooter("Better Alts Moderation");

            // Embed that is sent to a user when they get MUTED
            case MUTE -> embedBuilder
                    .setTitle("**Better Alts Moderation**")
                    .setDescription("You have been **MUTED**!")
                    .addField("**Reason: **", args, false)
                    .setFooter("Better Alts Moderation");

            // Embed that is sent to a user when they get BANNED
            case BAN -> embedBuilder
                    .setTitle("**Better Alts Moderation**")
                    .setDescription("You have been **BANNED**!")
                    .addField("**Reason: **", args, false)
                    .setFooter("Better Alts Moderation");

            // Embed that is sent to a user when they successfully generate an alt
            case GENERATOR -> embedBuilder
                    .setTitle("**Better Alts Account Gen**")
                    .setDescription("Unbanned NFA/SFA")
                    .addField("**Alt:**", "`" + args + "`", false)
                    .setFooter("Better Alts Account Gen");
        }
        return embedBuilder.build();
    }
}