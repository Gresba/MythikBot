package CustomObjects;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class Modals {
    public static Modal PARTNERSHIP_MODAL = Modal.create("sponsorship-partnership", "Sponsorship/Partnership")
            .addActionRows(
                    ActionRow.of(
                            TextInput.create("email", "Email", TextInputStyle.SHORT)
                                    .setPlaceholder("Enter your E-mail")
                                    .setRequired(true)
                                    .build()),
                    ActionRow.of(
                            TextInput.create("channel-link", "Channel Link", TextInputStyle.SHORT)
                                    .setPlaceholder("Your youtube channel link. Put N/A if none")
                                    .setRequired(true)
                                    .build()),
                    ActionRow.of(TextInput.create("discord-link", "Discord Link", TextInputStyle.SHORT)
                            .setPlaceholder("Your discord channel invite link. Put N/A if none")
                            .setRequired(true)
                            .build()),
                    ActionRow.of(TextInput.create("sponsorship-service", "Service", TextInputStyle.PARAGRAPH)
                            .setPlaceholder("What can you provide for BetterAlts?")
                            .setRequired(true)
                            .build()),
                    ActionRow.of(TextInput.create("sponsorship-payment", "Payments", TextInputStyle.PARAGRAPH)
                            .setPlaceholder("What do you want for your service?")
                            .setRequired(true)
                            .build())).build();


    // Create the modal and add the TextInputs
    public static Modal PURCHASE_MODAL = Modal.create("purchase-modal", "Purchase Item")
            .addActionRows(
                    ActionRow.of(
                            TextInput.create("purchase-item-name", "Item Name", TextInputStyle.SHORT)
                                    .setPlaceholder("What product do you want to purchase?")
                                    .setRequired(true)
                                    .build()
                    ),
                    ActionRow.of(
                            TextInput.create("purchase-item-amount", "Item Amount", TextInputStyle.SHORT)
                                    .setPlaceholder("How many do you need?")
                                    .setRequired(true)
                                    .build()
                    ),
                    ActionRow.of(
                            TextInput.create("purchase-payment-method", "Payment Method", TextInputStyle.SHORT)
                                    .setPlaceholder("Please enter your payment method")
                                    .setRequired(true)
                                    .build())).build();

    public static Modal REPLACEMENT_MODAL = Modal.create("replacement-modal", "Replacements")
            .addActionRows(
                    ActionRow.of(
                            TextInput.create("order-id", "Order ID", TextInputStyle.SHORT)
                                    .setPlaceholder("Enter your Order ID")
                                    .setRequired(true)
                                    .setMinLength(36)
                                    .setMaxLength(36)
                                    .build()
                    ),
                    ActionRow.of(
                            TextInput.create("replacement-amount", "Replacement Amount", TextInputStyle.SHORT)
                                    .setPlaceholder("Amount of replacements you need")
                                    .setRequired(true)
                                    .setMinLength(1)
                                    .setMaxLength(3)
                                    .build()
                    ),
                    ActionRow.of(
                            TextInput.create("replacement-reason", "Replacement Reason", TextInputStyle.PARAGRAPH)
                                    .setPlaceholder("Reason for replacements/What is wrong with your product? Explain in details")
                                    .setRequired(true)
                                    .setMinLength(1)
                                    .build())).build();


    /**
     * SERVER GENERAL INFO CONFIGURATION
     */
    public static Modal CONFIGURE_SERVER = Modal.create("configure-server-modal", "Configure Server")
            .addActionRows(
                    ActionRow.of(
                            TextInput.create("configure-prefix", "Bot Prefix", TextInputStyle.SHORT)
                                    .setPlaceholder("The prefix of all your command (Ex. !)")
                                    .build()
                    ),
                    ActionRow.of(
                            TextInput.create("configure-server-owner", "Server Owner", TextInputStyle.SHORT)
                                    .setPlaceholder("The member id of the owner (Ex. 938989740177383435)")
                                    .setRequired(true)
                                    .build()
                    )
            )
            .build();

    /**
     * SERVER TICKET SYSTEM CONFIGURATION
     */
    public static Modal CONFIGURE_TICKETS = Modal.create("configure-ticket-modal", "Configure Ticket System")
            .addActionRows(
                    ActionRow.of(
                            TextInput.create("configure-ticket-limit", "Ticket Limit", TextInputStyle.SHORT)
                                    .setPlaceholder("The max tickets a user can have at once (Ex. 1)")
                                    .setRequired(true)
                                    .build()
                    ),

                    ActionRow.of(
                            TextInput.create("configure-ticket-category", "Ticket Category Id", TextInputStyle.SHORT)
                                    .setPlaceholder("The id for the category the tickets will be put into (Ex. 838934740177383435")
                                    .setRequired(true)
                                    .build()
                    )
            )
            .build();

    /**
     * SERVER ROLES CONFIGURATION
     */
    public static Modal CONFIGURE_ROLES = Modal.create("configure-roles-modal", "Configure Roles")
            .addActionRows(
                    ActionRow.of(
                            TextInput.create("configure-customer-role", "Customer Role Id", TextInputStyle.SHORT)
                                    .setPlaceholder("The role for a customer (Ex. 349347445373834933")
                                    .setRequired(true)
                                    .build()
                    ),

                    ActionRow.of(
                            TextInput.create("configure-staff-role", "Staff Role Id", TextInputStyle.SHORT)
                                    .setPlaceholder("The role id for staff members (Ex. 838934740177383435")
                                    .setRequired(true)
                                    .build()
                    ),

                    ActionRow.of(
                            TextInput.create("configure-member-role", "Member Role Id", TextInputStyle.SHORT)
                                    .setPlaceholder("The role id for members allowed to view channel (Ex. 838934740177383435")
                                    .setRequired(true)
                                    .build()
                    )
            )
            .build();

    /**
     * SERVER CHANNEL CONFIGURATION
     */
    public static Modal CONFIGURE_CHANNELS = Modal.create("configure-channels-modal", "Configure Channels")
            .addActionRows(
                    ActionRow.of(
                            TextInput.create("configure-log-channel", "Log Channel Id", TextInputStyle.SHORT)
                                    .setPlaceholder("Log channel id (Ex. 349347445373834933)")
                                    .setRequired(true)
                                    .build()
                    ),

                    ActionRow.of(
                            TextInput.create("configure-join-channel", "Join Channel Id", TextInputStyle.SHORT)
                                    .setPlaceholder("Join channel id (Ex. 349347445373834933)")
                                    .setRequired(true)
                                    .build()
                    ),

                    ActionRow.of(
                            TextInput.create("configure-leave-channel", "Leave Channel Id", TextInputStyle.SHORT)
                                    .setPlaceholder("Leave channel id (Ex. 349347445373834933)")
                                    .setRequired(true)
                                    .build()
                    )
            )
            .build();
}
