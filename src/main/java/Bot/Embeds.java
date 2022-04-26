package Bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;

public enum Embeds {

    // Embed that is sent to a channel whenever a member needs help with the bot
    HELP {
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Better Alts Help**")
                .setColor(Color.CYAN)
                .setDescription("Help for using the Better Alts bot")
                .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG")
                .addField("**Staff Commands**", "Run /staffhelp to view staff commands", false)
                .addField("**User Commands**", "Run /userhelp to view user commands", false)
                .addField("**Customer Commands**", "Run /customerhelp to view customer commands", false)
                .addField("**Frequently Asked Questions**", "Run /faq to view questions that might have been answered", false)
                .addField("**Questions/Problems**", "Create a #ticket if you have any questions or problems", false);
        }
    },

    // Embed to show the server rules
    RULES {
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                    .setTitle("**__Official Better Alts Server Rules__**")
                    .setDescription("Read and follow all rules")
                    .setColor(Color.BLACK)

                    .addField("**General**",
                            """
                                    **1.1)** No talking about selling/buying products not related to this server
                                    **1.2)** No spamming/ghost pinging
                                    **1.3)** No releasing anyone's information without consent
                                    **1.4)** No sending NSFW or gore contents
                                    **1.5)** No comments that will discourage a user from buying
                                    **1.6)** No advertising unless you have permission
                                    **1.7)** No pinging staff members unless it's about an order
                                    **1.8)** No mass pinging
                                    **1.9)** Use channels according to their purpose
                                    **1.10)** Don't ask for free stuff""", false)
                    .addField("**Invites**",
                            """
                                    **2.1)** No creating fake accounts for giveaways or invite rewards
                                    **2.2)** No j4j invites for rewards
                                    **2.3)** Invite source must be shown for rewards
                                    """, false)
                    .addField("**Purchases**",
                            "**3.1)** Don't leave bad feedback in chat or store before talking to us\n" +
                                    "*This may result in no replacements/refunds*", false)
                    .addField("**Punishments**",
                            """
                                    - Ban
                                    - Mute
                                    - Warning
                                    - Order Taken Away
                                    - Ban from future giveaways
                                    *You may receive any of these punishments for breaking a rule*""", false)
                    .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG");
        }
    },

    RESTOCKALERTS{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("Restock Roles")
                .addField("React to get alerts for the specific product",
                        """
                                MFA - :e_mail:
                                Skyblock - :cloud:
                                Hypixel LvL21+/Ranked - :evergreen_tree:
                                Hypixel Unbanned NFA/SFA Mix - :heart:
                                NFA Banned - :red_square:
                                SFA Banned - 
                                VyprVPN - :alien:
                                Special MFA - :heart_eyes_cat:
                                Migrated Unbanned NFA - :white_check_mark:
                                Migrated Banned NFA - :peach:
                                Yahoo FA - :yin_yang:""", false)
                .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG")
                .setColor(Color.CYAN);
        }
    },

    SKYBLOCK{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("Skyblock Coins")
                .addField("**Prices**",
                        """
                                100+ million: $0.19/mil
                                300+ million: $0.17/mil
                                500+ million: $0.15/mil
                                1 billion+: $0.14/mil""", false)
                .addField("**Payment Methods**",
                        """
                                Crypto (Any Type)
                                Debit/Credit/Apple Pay/Google Pay (Through Stripe)
                                CashApp/Venmo/Zelle
                                                                    
                                **PayPal**
                                To use PayPal create a ticket""", false)
                .addField("**How to buy**", "Open a ticket and click Purchase", false)
                .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG")
                .setColor(Color.YELLOW);
        }
    },

    GIVEAWAY {
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("Giveaway Role")

                .addField("React to be alerted for future giveaways\n",
                        "Giveaway Role - :gift:\n", true)
                .addField("Note",
                        "*No replacements for giveaways*", false)
                .addField("**Remove role**", "*Remove your reaction to remove the role from your role list*", false)
                .setColor(Color.ORANGE)
                .setThumbnail("https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG");
        }
    },

    FAQ{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("Frequently Asked Questions")
                .setColor(Color.CYAN)
                .setDescription("If this answers your question then please close the ticket")
                .addField("**Nitro**", "We do not accept nitro as any sort of payment and it can not be used to gain any types of rewards.", false)
                .addField("**PayPal**", "Create a ticket to purchase and type PayPal as the payment method", false)
                .addField("**Restocked**", "There is no estimated time of restock for any product. To be alert for restocks go to #store and react to the proper message", false)
                .addField("**Warranty**", """
                        All products have a 24 hour warranty. Except MFAs which have a 1 hour warranty. 
                        During that warranty you have to confirm whether your products are working or we can not give you any reimbursements.
                        Leaving or commenting any negative feedback in any way before contacting support about the issue will remove any warranty.
                        """, false);
        }
    },

    AUTONUKE{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("Better Alts Moderation")
                .setDescription("Channel is nuked every 30 minutes. Create a ticket if you have any questions.")
                .addField("**Reason**", """
                        Channel is nuked because staff members can't always moderate the chat and this is the best way to keep the chat completely clean so we do not get punished by discord.
                        If you want to leave permanent feedback then please go to #vouch.
                        *Feedback is only removed if you break one of our terms of services.*
                        """, false)
                .setColor(Color.BLACK);
        }
    },

    SPONSORSHIP{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
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
        }
    },


    PAYMENTMETHODS{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
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
                                To use PayPal contact @Blacman, @AdeaS or @@Flipfudge""", false);
        }
    },

    TICKETCLOSED{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("Better Alts Tickets")
                .setColor(Color.RED)
                .setDescription("Your ticket has been closed. If you have anymore issues then please make another ticket!");
        }
    },

    TICKETREASON{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Choose Reason**")
                .setDescription("""
                        **Order** - Claim Order/Replacements
                        **Partnership** - Requirements or steps for sponsorship or partnership
                        **Purchase** - Purchase an item
                        **General** - Ask a general questions and see FAQ
                        **Close** - Close the ticket
                        """);
        }
    },

    CHOOSEORDERREASON{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("Better Alts Ticket")
                .setDescription("""
                    Please choose one of the reasons
                    **Claim Order** - Claim an order that you made
                    **Replacements** - Gain replacements for an order
                    
                    If it has passed 24 hours since your order was sent, you will **NOT** receive replacements/refunds
                    """);
        }
    },

    FETCHINGORDER{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Better Alts Tickets**")
                .setDescription("Fetching your order...");
        }
    },

    // HELP
    USERHELP{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**User Command Help**")
                .setDescription("User commands and descriptions")
                .addField("**Commands**",
                        """
                                /btc - Mythik's BTC addy
                                /ltc - Mythik's LTC addy
                                /eth - Mythik's ETH addy
                                /bch - Mythik's BCH addy
                                /sol - Mythik's SOL addy
                                /ada - Mythik's ADA addy
                                /cashapp - Mythik's CashApp Username
                                /venmo - Mythik's Venmo Username
                                """, false);
        }
    },

    CUSTOMERHELP{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Customer Command Help**")
                .setDescription("Customer commands and descriptions")
                .addField("**Commands",
                        """
                                /orders - View orders and amount spent
                                /registerorder - Claim an order and add it to your profile
                                """, false);
        }
    },

    STAFFHELP{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                    .setTitle("**Admin Command Help**")
                    .setDescription("Admin commands and descriptions")
                    .addField("**Moderation**",
                            """
                                    /ban - Ban a member
                                    /kick - Kick a member
                                    /mute - Mute a member
                                    /unmute - Unmute a member
                                    /warn - Warn a member
                                    /autonuke - Autonuke a channel
                                    /nuke - nuke a channel
                                    """, false)
                    .addField("**Tickets**",
                            """
                                    /send - Send a product
                                    /close - Close a ticket
                                    /order - Check an order
                                    /cardcheck - Ask user to verify their card
                                    /openticket - Open a ticket for a user
                                    """, false)
                    .addField("**Directions**",
                            """
                                    *Run the* /directionslist *command to see all directions options*
                                    /directions - View the directions to different scenarios
                                    """, false);
        }
    },

    DIRECTIONS{
        @Override
        public EmbedBuilder getEmbed()
        {
            return new EmbedBuilder()
                    .setTitle("**Directions**")
                    .setDescription("Directions Category. Use this with the /directions (category): Command")
                    .addField("**Tickets Direction List:**",
                            """
                                    ClaimTicket - How to claim a ticket as a staff member
                                    SendOrder - How to send a user an order
                                    FraudulentCheck - How to verify an order/What to check for
                                    Replacements - How to send replacements
                                    Unknown - What to do in an unknown situation
                                    """,
                            false)
                    .addField("**Order Issue Directions List:**",
                            """
                                    UnbannedProductIssue - How to deal with issue with unbanned products
                                    MinecraftMFAIssue - How to deal with issues with Minecraft MFA
                                    Unknown - What to do in an unknown situation
                                    """,
                            false)
                    .addField("**General Direction List:**",
                            """
                                    ChatModeration - How to moderate chat
                                    GeneralQuestions - What to do when a ticket about a general question is asked
                                    Unknown - What to do in an unknown situation
                                    """, false)
                    .addField("**Note**", "If you mess up even once you will be fired and not receive any payouts. Make sure you read and follow all directions", false);
        }

    },

    CLAIMTICKET{
        @Override
        public EmbedBuilder getEmbed()
        {
            return new EmbedBuilder()
                    .setTitle("**How to Claim Ticket**")
                    .setDescription("When a user creates a ticket you will be able to claim the ticket to get paid for it")
                    .addField("**How?**", "Run the command /claim which will assign the ticket to you", false);
        }
    },

    SENDORDER{
        @Override
        public EmbedBuilder getEmbed()
        {
            return new EmbedBuilder()
                    .setTitle("**How to Send Order**")
                    .setDescription("Directions on steps to take before and while sending an order")
                    .addField("**How?**", """
                            **1** - Log into stripe and copy and paste the order ID into the Stripe dashboard
                            **2** - Click on the order
                            **3** - Check if the order is fraudulent (View Directions for this)
                            **4** - If the order is not fraudulent or customer verifies then send the product
                            **5** - The command is /send <ORDERID> <MEMBER>
                            **MAKE SURE THE MEMBER IS THE ONE PINGED INSIDE THE ORDER DETAILS MESSAGE.** There can be two people with the same name.
                            """, false);
        }
    },

    FRAUDULENTCHECK{
        @Override
        public EmbedBuilder getEmbed()
        {
            return new EmbedBuilder()
                    .setTitle("**How to check if an order is fraudulent**")
                    .setDescription("How to check if an order is potentially fraudulent. **ONLY** check for the four things listed. Do **NOT** check fraud score")
                    .addField("**What to check for**",
                            """
                        **1** - **ONLY** check these 4 things
                        - "Previous Disputes from this IP address"
                        - "Number of cards previously associated with this IP address (last 7 days)"
                        - "Number of cards previously associated with this device ID (last 7 days)"
                        - "Checkout behavior"
                        **2** - If any of these match then run the /cardcheck command and copy and paste the last 4 digits of the card into the command
                        - "Previous Disputes from this IP address" = "Yes"
                        - "Number of cards previously associated with this IP address (last 7 days)" >= 3
                        - "Number of cards previously associated with this device ID (last 7 days)" >= 3
                        - "Checkout behavior" - If (Card Number/Expiration Date/CVC) = "Pasted"
                        **3** - Make sure the last 4 digits in the card match
                        **4** - If the customer doesn't send a picture of their card tell them they have to wait for Mythik and ping me
                """, false);
        }
    },

    REPLACEMENTS{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                    .setTitle("**How to send replacements**")
                    .setDescription("As of now this is only available for Mythik");
        }
    },

    UNKNOWN{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                    .setTitle("**Directions Not Listed?**")
                    .setDescription("Stop whatever you're doing. Let Mythik know what the situation is. Don't do anything else");
        }
    },

    UNBANNEDPRODUCTISSUE{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Issue with Unbanned Product**")
                .setDescription("How to deal with inquiries related to Unbanned Products")
                    .addField("**Steps:**",
                            """
                            **1** - First run the command /openticket <MEMBER> and make sure that you know the exact problem.
                                - Example: Is it a login issue? A Banned Issue?
                            **2** - Make sure it isn't passed the warranty. 24 hours for all products except MFAs
                                - If it passed the warranty, tell the customer "Sorry it has passed your warranty and we can't do anything about this".
                                *Don't do anything else after that, tell the customer to wait for Mythik to get on if they rant*   
                            **3** - Make sure they're logging in with the right mode
                                - Example: If the product is a migrated account, make sure they're using Microsoft Login and same for Mojang
                                - If the account really doesn't login or requires migration, ping Mythik and tell the customer they needs to wait for Mythik.
                            **4** - If it's a ban issue make sure the customer didn't get the account banned
                                - If it is a security ban, go to https://plancke.io and search up the username of the account
                                - Check the last login and if it is after the customer purchased tell them
                                    "It looks like the account was security banned after you purchased. This usually means that your IP was marked as suspicious by Hypixel
                                    We're sorry, but we cannot provide any replacements for this. However, we recommend using Vypr VPN to avoid this in the future"
                                - If the account was last logged in before their purchase then ping Mythik and tell the customer they need to wait for Mythik. 
                            **5** - If the alts sent to the customer is blank that means there was no stock as the time the product was sent and tell them to wait for Mythik    
                            """,
                            false);
        }
    },

    MINECRAFTMFAISSUE
    {
        @Override
        public EmbedBuilder getEmbed()
        {
            return new EmbedBuilder()
                .setTitle("**Issue with MFA**")
                .setDescription("How to deal with inquiries related to Minecraft MFAs")
                .addField("**Steps**",
                        """
                            **1** - First run the command /openticket <MEMBER> and make sure that you know the exact problem.
                            **2** - Make sure it isn't passed the warranty. 24 hours for all products except MFAs
                                - If it passed the warranty, tell the customer "Sorry it has passed your warranty and we can't do anything about this".
                                *Don't do anything else after that, tell the customer to wait for Mythik to get on if they rant*
                            **3** - If the account doesn't sign into the email
                                - Make sure they're signing into the right email. Ask them to send **only** the email of the account and check which domain it is
                                - Make sure they're entering the right information. The format is usually email:emailPassword - <Additional Information>
                                - If they're doing everything correct, ping Mythik and tell the customer to wait
                            **4** - No emails from Mojang
                                - Ask the customer for the login details and sign into the account and verify that there are no emails from Mojang. If this is true, ping Mythik and tell the customer to wait
                            **5** - Email can't be moved
                                - Tell the customer to migrate the account to microsoft and watch the video on how to change the email for Microsoft accounts
                                """, false);
        }
    },

    CHATMODERATION
    {
        @Override
        public EmbedBuilder getEmbed()
        {
            return new EmbedBuilder()
               .setTitle("**How to moderation chat**")
                    .setDescription("What to look for and how to moderation chat")
                    .addField("**Steps**",
                            """
                                    **1** - Is the chat being auto-nuked every 30 minutes? If not run the /autonuke Minutes: 30 command in the channel
                                    **2** - Check if anyone is breaking any rules. If they are pinging then warn them. If it is any other rule breaking, mute them and delete their message.
                                    **3** - If anyone needs help then tell them to create a ticket 
                                    **4** - Make sure everyone is using the channels according to their purchase.
                                    """, false);
        }
    },

    GENERALQUESTIONS
    {
        @Override
        public EmbedBuilder getEmbed()
        {
            return new EmbedBuilder()
                    .setTitle("**General Question Tickets**")
                    .setDescription("How to deal with general tickets")
                    .addField("**How to respond**",
                            """
                                    **1** - If the ticket is for claiming/replacements, tell the customer to re-create a ticket about that
                                    **2** - Tell the customer to read FAQ if that doesn't work then let them ask the question
                                    **3** - Use common sense to answer the question. If you don't know the answer ping Mythik and tell the customer to wait for Mythik to respond
                                    """, false);
        }
    },

    // Moderation
    KICK{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Better Alts Moderation**")
                .setDescription("You have been **KICKED**!")
                .setFooter("Better Alts Moderation");
        }
    },

    WARNING{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Better Alts Moderation**")
                .setDescription("You have been **WARNED!**")
                .setFooter("Better Alts Moderation");
        }
    },

    MUTE{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Better Alts Moderation**")
                .setDescription("You have been **MUTED**!")
                .setFooter("Better Alts Moderation");
        }
    },

    UNMUTE{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Better Alts Moderation**")
                .setDescription("You have been **UNNMUTED**!")
                .setFooter("Better Alts Moderation");
        }
    },

    BAN{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Better Alts Moderation**")
                .setDescription("You have been **BANNED**!")
                .setFooter("Better Alts Moderation");
        }
    },

    ALTDETECTION{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("Better Alts Security")
                .setColor(Color.BLACK)
                .setDescription("""
                        Your account has been quarantined because it was detected as a bot or an alt account.
                        You will be able to join in 24 hours
                        """)
                .setFooter("Better Alts Security");
        }
    },

    // Generator
    GENSUCCESS{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Better Alts Account Gen**")
                .setDescription("Unbanned NFA sent to your DMs! Enjoy :)");
        }
    },

    GENFAIL{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Better Alts Account Gen**")
                .setDescription("""
                        You do not have permissions to run this command!

                        **Gen Requirements:**
                        Youtuber/Partner **or**\s
                        $50 for the gen needed! Make a ticket for this!
                        *If you paid then make a ticket and send order ID*""")
                .setColor(Color.RED);
        }
    },

    GENERATOR{
        @Override
        public EmbedBuilder getEmbed() {
            return new EmbedBuilder()
                .setTitle("**Better Alts Account Gen**")
                .setDescription("Unbanned NFA/SFA")
                .setFooter("Better Alts Account Gen");
        }
    };

    /**
     * Send a private message with a specified embed to a member
     *
     * @param embedBuilder Type of embed to send
     * @param member Member to send the message to
     * @param storeHeader Add the store headers
     */
    public static void sendEmbed(EmbedBuilder embedBuilder, Member member, boolean storeHeader)
    {
        embedBuilder
            .setColor(Color.GREEN);
        if(storeHeader) {
            embedBuilder.addField("**Discord**", "https://discord.gg/vzwJz3NK7a", false)
                    .addField("**Store**", "https://betteralts.com", false);
        }

        member.getUser().openPrivateChannel().flatMap(privateChannel ->
            privateChannel.sendMessageEmbeds(embedBuilder.build())
        ).queue();
    }

    /**
     * Send a message with a specific embed to a channel
     *
     * @param embedBuilder The embed to send
     * @param textChannel Channel to send the message to
     */
    public static void sendEmbed(EmbedBuilder embedBuilder, TextChannel textChannel, boolean storeHeader)
    {
        embedBuilder
            .setColor(Color.GREEN);
        if(storeHeader) {
            embedBuilder.addField("**Discord**", "https://discord.gg/vzwJz3NK7a", false)
                    .addField("**Store**", "https://betteralts.com", false);
        }

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public abstract EmbedBuilder getEmbed();
}