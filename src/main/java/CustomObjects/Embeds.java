package CustomObjects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;

public class Embeds {

    /**
     * The logo for the url of the Better Alts bot
     */
    private static final String logoUrl = "https://cdn.discordapp.com/attachments/934569594830614618/945219120276852776/logo.PNG";

    /**
     * Embed that is sent to a channel whenever a member needs help with the bot
     */
    public static final EmbedBuilder HELP = new EmbedBuilder()
            .setTitle("**Better Alts Help**")
            .setColor(Color.CYAN)
            .setDescription("Choose one of the follow options")
            .setThumbnail(logoUrl)
            .addField("**Questions/Problems**", "Create a #ticket if you have any questions or problems", false);

    /**
     * Embed to show the server rules
     */
    public static final EmbedBuilder RULES = new EmbedBuilder()
            .setTitle("**Official Better Alts Server Rules**")
            .setDescription("Read and follow all rules")
            .setColor(Color.BLACK)

            .addField("**General**",
                    """
                            **1.1)** No talking about selling/buying products not related to Better Alts
                            **1.2)** No spamming/ghost pinging
                            **1.3)** No comments that will discourage another user from buying.
                            **1.4)** No advertising unless you have permission.
                            **1.5)** No mass pinging.
                            **1.6)** Don't ask for free stuff.
                            **1.7)** Any threats will get you banned and will get your order revoked.
                            """, false)
            .addField("**Invites**",
                    """
                            **2.1)** No creating fake accounts for giveaways or invite rewards
                            **2.2)** No j4j invites for rewards
                            **2.3)** Invite source must be shown for rewards
                            """, false)
            .addField("**Purchases**",
                    "**3.1)** Don't leave bad feedback in chat or store before talking to us\n" +
                            "*This may result in no replacements/refunds/your products being given as a giveaway*", false)
            .addField("**Punishments**",
                    """
                            - Ban
                            - Mute
                            - Warning
                            - Order Taken Away
                            - Ban from future giveaways
                            *You may receive any of these punishments for breaking a rule*""", false)
            .setThumbnail(logoUrl);

    /**
     * Embed to show skyblock prices and how to purchase it
     */
    public static final EmbedBuilder SKYBLOCK_PRICES = new EmbedBuilder()
                .setTitle("Skyblock Coins")
                .addField("**Prices**",
                        """
                                250+ million: $0.11/mil
                                500+ million: $0.10/mil
                                1+ billion: $0.09/mil
                                2+ billion: $0.085/mil""", false)
                .addField("**Payment Methods**",
                        """
                                Crypto (Any Type)
                                Debit/Credit/Apple Pay/Google Pay (Through Stripe)
                                CashApp/Venmo/Zelle
                                                                    
                                **PayPal**
                                To use PayPal create a ticket""", false)
                .addField("**How to buy**", "Open a ticket and click Purchase", false)
                .setThumbnail(logoUrl)
                .setColor(Color.YELLOW);

    /**
     * Embed to tell people how to get the giveaway role
     */
    public static final EmbedBuilder GIVEAWAY = new EmbedBuilder()
                .setTitle("**Better Alts Giveaway**")
                .setDescription("Choose an option")
                .addField("Note",
                        "*No replacements for giveaways. If you complain about  something you received in a giveaway not working. You will be banned.*", false)
                .setColor(Color.ORANGE)
                .setThumbnail(logoUrl);

    /**
     * Embed to tell show people answers to frequently asked questions
     */
    public static final EmbedBuilder FAQ = new EmbedBuilder()
                .setTitle("**Frequently Asked Questions**")
                .setColor(Color.CYAN)
                .setDescription("If this answers your question then please close the ticket")
                .addField("**Nitro**", "We do not accept nitro as any sort of payment and it can not be used to gain any types of rewards.", false)
                .addField("**Steam**", "We do not accept steam gift cards/balance as a payment method", false)
                .addField("**PayPal**", "Create a ticket to purchase and type PayPal as the payment method", false)
                .addField("**Restocked**", "There is no estimated time of restock for any product. To be alert for restocks go to #store and react to the proper message", false)
                .addField("**Warranty**", """
                        All products have a 24 hour warranty. Except MFAs which have a 1 hour warranty.
                        During that warranty you have to confirm whether your products are working or we can not give you any reimbursements.
                        Leaving or commenting any negative feedback in any way before giving support a chance to resolve the issue will remove any warranty.
                        """, false);

    /**
     * Embed to tell people that the channel is in auto-nuke mode and the reason
     */
    public static final EmbedBuilder AUTO_NUKE = new EmbedBuilder()
                .setTitle("**Better Alts Moderation**")
                .setDescription("Channel is nuked every 30 minutes. Create a ticket if you have any questions.")
                .addField("**Reason**", """
                        Channel is nuked because staff members can't always moderate the chat and this is the best way to keep the chat completely clean so we do not get punished by discord.
                        
                        We have tried to keep the chat un-nuked; however, people take advantage of it so it will be nuked.        
                        """, false)
                .addField("**Important**", """
                        If you have any issues regarding your order or questions make a #ticket
                        Leaving any negative feedback or comments anywhere before talking to a staff member will result in your order being revoked.
                        """, false)
                .setColor(Color.BLACK);
    /**
     * Embed to show users all the payment methods
     */
    public static final EmbedBuilder PAYMENT_METHODS = new EmbedBuilder()
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
                                Create a new ticket -> Click "Purchase" -> Type only "PayPal" as the payment method
                                """, false);

    /**
     * Embed to tell a user their ticket was closed
     */
    public static final EmbedBuilder TICKET_CLOSED = new EmbedBuilder()
                .setTitle("Better Alts Tickets")
                .setColor(Color.RED)
                .setDescription("Your ticket has been closed. If you have anymore issues then please make another ticket!")
                .addField("**Discord**", "https://discord.gg/vzwJz3NK7a", false)
                .addField("**Store**", "https://betteralts.com", false);

    /**
     * Embed to show a user the options for a ticket
     */
    public static final EmbedBuilder TICKET_REASON = new EmbedBuilder()
                .setTitle("**Better Alts Tickets**")
                .setDescription("""
                        Please choose a reason for creating this ticket
                        
                        **Order** - Claim Order/Replacements/Problems with Product
                        **Partnership** - Requirements or steps for sponsorship or partnership
                        **Purchase** - Purchase an item
                        **General** - Ask a general questions and see FAQ
                        **Close** - Close the ticket
                        """)
                .addField("**Note**", """
                        Read everything before choosing an option or your warranty may expire.
                        
                        Do **NOT** create new tickets unless your problem has changed.
                        
                        Do **NOT** create new tickets because a staff member isn't answering. That will only extend the wait time for your ticket.
                        """, false);


    /**
     * Embed to ask if someone's ticket is related to an order
     */
    public static final EmbedBuilder ORDER_RELATED = new EmbedBuilder()
            .setTitle("**Better Alts Tickets**")
            .setDescription("Is your ticket for anything related to a product you purchased?")
            .addField("**IMPORTANT**", """
                If you click "NO", this ticket will not be counted as you reporting a problem with your order.
                This may cause your warranty to expire even though you reported it here.
                """, false);
    /**
     * Embed to show user the choices for an order ticket
     */
    public static final EmbedBuilder CHOOSE_ORDER_REASON = new EmbedBuilder()
                .setTitle("Better Alts Ticket")
                .setDescription("""
                    Please choose one of the reasons
                    **Claim Order** - Claim an order that you made
                    **Replacements** - Request for replacements for an order
                    
                    If it has passed 24 hours since your order was sent, you will **NOT** receive replacements/refunds
                    """);

    /**
     * Embed to show user commands with the bot
     */
    public static final EmbedBuilder USER_HELP = new EmbedBuilder()
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
                                /cash_app - Mythik's CashApp Username
                                /venmo - Mythik's Venmo Username
                                """, false);

    /**
     * Embed to show customers commands with the bot
     */
    public static final EmbedBuilder CUSTOMER_HELP = new EmbedBuilder()
                .setTitle("**Customer Command Help**")
                .setDescription("Customer commands and descriptions")
                .addField("**Commands",
                        """
                                /orders - View orders and amount spent
                                /register_order - Add an order to your profile
                                """, false);

    /**
     * Embed to show staff commands and how to use the bot
     */
    public static final EmbedBuilder STAFF_HELP = new EmbedBuilder()
                .setTitle("**Admin Command Help**")
                .setDescription("Admin commands and descriptions")
                .addField("**Moderation**",
                        """
                                /ban - Ban a member
                                /kick - Kick a member
                                /timeout - Timeout a member
                                /warn - Warn a member
                                /auto_nuke - Auto-nuke a channel
                                /nuke - nuke a channel
                                """, false)
                .addField("**Tickets**",
                        """
                                /send - Send a product
                                /close - Close a ticket
                                /order - Check an order
                                /card_check - Ask user to verify their card
                                /open_ticket - Open a ticket for a user
                                """, false)
                .addField("**Directions**",
                        """
                                *Run the* /directions_list *command to see all directions options*
                                /directions - View the directions to different scenarios
                                """, false);

    /**
     * Embed to show staff a list of different directions
     */
    public static final EmbedBuilder DIRECTIONS = new EmbedBuilder()
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
                .addField("**Order Issue Directions List**",
                        """
                                UnbannedProductIssue - How to deal with issue with unbanned products
                                MinecraftMFAIssue - How to deal with issues with Minecraft MFA
                                Unknown - What to do in an unknown situation
                                """,
                        false)
                .addField("**General Direction List**",
                        """
                                ChatModeration - How to moderate chat
                                GeneralQuestions - What to do when a ticket about a general question is asked
                                Unknown - What to do in an unknown situation
                                """, false)
                .addField("**Note**", "If you mess up even once you will be fired and not receive any payouts. Make sure you read and follow all directions", false);

    /**
     * Embed to show staff how to claim a ticket
     */
    public static final EmbedBuilder CLAIM_TICKET = new EmbedBuilder()
                    .setTitle("**How to Claim Ticket**")
                    .setDescription("When a user creates a ticket you will be able to claim the ticket to get paid for it")
                    .addField("**How?**", "Run the command /claim which will assign the ticket to you", false);

    /**
     * Embed to show staff how to send an order
     */
    public static final EmbedBuilder SEND_ORDER = new EmbedBuilder()
                    .setTitle("**How to Send Order**")
                    .setDescription("Directions on steps to take before and while sending an order")
                    .addField("**How?**", """
                            **1** - Log into stripe and copy and paste the order ID into the Stripe dashboard
                            **2** - Click on the order
                            **3** - Check if the order is fraudulent (View Directions for this)
                            **4** - If the order is not fraudulent or customer verifies then send the product
                            **5** - The command is /send <ORDER_ID> <MEMBER>
                            **MAKE SURE THE MEMBER IS THE ONE PINGED INSIDE THE ORDER DETAILS MESSAGE.** There can be two people with the same name.
                            """, false);

    /**
     * Embed to show staff how to check if an order is potentially fraudulent
     */
    public static final EmbedBuilder FRAUDULENT_CHECK = new EmbedBuilder()
                    .setTitle("**How to check if an order is fraudulent**")
                    .setDescription("How to check if an order is potentially fraudulent. **ONLY** check for the four things listed. Do **NOT** check fraud score")
                    .addField("**What to check for**",
                            """
                        **1** - **ONLY** check these 4 things
                        - "Previous Disputes from this IP address"
                        - "Number of cards previously associated with this IP address (last 7 days)"
                        - "Number of cards previously associated with this device ID (last 7 days)"
                        - "Checkout behavior"
                        **2** - If any of these match then run the /card_check command and copy and paste the last 4 digits of the card into the command
                        - "Previous Disputes from this IP address" = "Yes"
                        - "Number of cards previously associated with this IP address (last 7 days)" >= 3
                        - "Number of cards previously associated with this device ID (last 7 days)" >= 3
                        - "Checkout behavior" - If (Card Number/Expiration Date/CVC) = "Pasted"
                        **3** - Make sure the last 4 digits in the card match
                        **4** - If the customer doesn't send a picture of their card tell them they have to wait for Mythik and ping me""", false);

    /**
     * Embed to show staff how to send replacements
     */
    public static final EmbedBuilder REPLACEMENTS = new EmbedBuilder()
        .setTitle("**How to send replacements**")
        .setDescription("As of now this is only available for Mythik");

    /**
     * Embed to show staff what to do in an unknown situation
     */
    public static final EmbedBuilder UNKNOWN = new EmbedBuilder()
            .setTitle("**Directions Not Listed?**")
            .setDescription("Stop whatever you're doing. Let Mythik know what the situation is. Don't do anything else");

    /**
     * Embed to show staff how to deal with an issue with unbanned products
     */
    public static final EmbedBuilder UNBANNED_PRODUCT_ISSUE = new EmbedBuilder()
                .setTitle("**Issue with Unbanned Product**")
                .setDescription("Dealing with inquiries related to Unbanned Products")
                    .addField("**Steps:**",
                            """
                            **1** - Run /open_ticket and figure out the exact problem.
                            **2** - Check if warranty is passed
                            **3** - Make sure they're logging in with the right mode (Microsoft/Mojang)
                            **4** - Ban issue: Confirm the customer didn't get the account banned
                                - Go to https://plancke.io and lookup the username
                                - Check the last login and if it is after the customer purchased tell them
                                    "It looks like the account was banned after you purchased. This usually means that your IP was marked as suspicious by Hypixel
                                    Unfortunately, but we cannot replacements for this. However, we recommend using Vypr VPN to avoid this in the future"
                                - If the account was last logged in before their purchase then ping Mythik and tell the customer they need to wait for Mythik.
                            **5** - If the alts sent to the customer is blank there was no stock at the time the product was sent and tell them to wait for Mythik.
                            """,
                            false);

    /**
     * Embed to show staff how to deal with issues about an MFA
     */
    public static final EmbedBuilder MINECRAFT_MFA_ISSUE = new EmbedBuilder()
                .setTitle("**Issue with MFA**")
                .setDescription("How to deal with inquiries related to Minecraft MFAs")
                .addField("**Steps**",
                        """
                            **1** - First run the command /open_ticket <MEMBER> and make sure that you know the exact problem.
                            **2** - Check if warranty is passed
                            **3** - If the account doesn't sign into the email
                                - Make sure they're signing into the right email. Ask them to send **only** the email of the account and check which domain it is
                                - Make sure they're entering the right information. The format is usually email:emailPassword - <Additional Information>
                                - If they're doing everything correct, ping Mythik and tell the customer to wait
                            **4** - No emails from Mojang
                                - Ask the customer for the login details and sign into the account and verify that there are no emails from Mojang
                            **5** - Email can't be moved
                                - Tell the customer to migrate the account to microsoft and watch the video on how to change the email for Microsoft accounts
                                """, false);

    /**
     * Embed to show staff how to moderate chat
     */
    public static final EmbedBuilder CHAT_MODERATION = new EmbedBuilder()
                .setTitle("**How to moderation chat**")
                .setDescription("What to look for and how to moderation chat")
                .addField("**Steps**",
                        """
                                **1** - Is the chat being auto-nuked every 30 minutes? If not run the /auto_nuke Minutes: 30 command in the channel
                                **2** - Check if anyone is breaking any rules. If they are pinging then warn them. If it is any other rule breaking, mute them and delete their message.
                                **3** - If anyone needs help then tell them to create a ticket.
                                **4** - Make sure everyone is using the channels according to their purchase.
                                """, false);

    /**
     * Embed to show staff how to moderate chat
     */
    public static final EmbedBuilder GENERAL_QUESTIONS = new EmbedBuilder()
                    .setTitle("**General Question Tickets**")
                    .setDescription("How to deal with general tickets")
                    .addField("**How to respond**",
                            """
                                    **1** - If the ticket is for claiming/replacements, tell the customer to re-create a ticket about that
                                    **2** - Tell the customer to read FAQ if that doesn't work then let them ask the question
                                    **3** - Use common sense to answer the question. If you don't know the answer ping Mythik and tell the customer to wait for Mythik to respond
                                    """, false);

    /**
     * Embed to tell a user they got detected as an alt
     */
    public static final EmbedBuilder ALT_DETECTION = new EmbedBuilder()
                .setTitle("Better Alts Security")
                .setColor(Color.BLACK)
                .setDescription("""
                        Your account has been quarantined because it was detected as a bot or an alt account.
                        You will be able to join in 24 hours
                        """)
                .setFooter("Better Alts Security");

    public static EmbedBuilder createPunishmentEmbed(String punishmentType, String reason, Member moderator)
    {
        EmbedBuilder punishmentEmbed = new EmbedBuilder()
                .setTitle("**Better Alts Moderation**")
                .setDescription(String.format("**Punishment: **", punishmentType))
                .addField("**REASON**", reason, false)
                .addField("**MODERATOR**", moderator.getAsMention(), false)
                .setFooter("Better Alts Moderation")
                .setThumbnail(logoUrl);
        if(punishmentType.equalsIgnoreCase("WARNING"))
            punishmentEmbed.addField("**IMPORTANT**",
                    """
                    The next warning will result in an immediate ban.
                    
                    Make sure you read all the rules in the #rules channel.
                    
                    If you have any on-going order, you will not receive any support for the order since if you are banned.
                    """, false);
        return punishmentEmbed;
    }
}