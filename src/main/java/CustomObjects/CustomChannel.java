package CustomObjects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.TextChannel;

public class CustomChannel {
    private final JDA jda;
    private TextChannel customChannel;

    /**
     * Constructor for initializing a ticket channel
     *
     * @param jda The jda the channel belongs to
     * @param channelId The channel id
     */
    public CustomChannel(JDA jda, String channelId)
    {
        this.jda = jda;
        this.customChannel = jda.getTextChannelById(channelId);
    }

    /**
     * Let a role or member speak and view the ticket
     *
     * @param permissionHolder The role or member to give permissions to
     */
    public void openTicket(IPermissionHolder permissionHolder)
    {
        this.customChannel.upsertPermissionOverride(permissionHolder)
                .setAllow(Permission.MESSAGE_SEND)
                .setAllow(Permission.VIEW_CHANNEL);
    }

    /**
     * Prevent a role or member to speak in the ticket
     *
     * @param permissionHolder The role or member to give permissions to
     */
    public void muteTicket(IPermissionHolder permissionHolder)
    {
        this.customChannel.upsertPermissionOverride(permissionHolder)
                .setDeny(Permission.MESSAGE_SEND)
                .setAllow(Permission.VIEW_CHANNEL);
    }

    /**
     * Prevent a role or member to speak or view the ticket
     *
     * @param permissionHolder The role or member to give permissions to
     */
    public void hideTicket(IPermissionHolder permissionHolder)
    {
        this.customChannel.upsertPermissionOverride(permissionHolder)
                .setDeny(Permission.MESSAGE_SEND)
                .setDeny(Permission.VIEW_CHANNEL);
    }

    /**
     * Close the current ticket channel
     */
    public void close()
    {
        // Send an embed to the user that their ticket was closed
        CustomMember customMember = new CustomMember(jda, customChannel.getTopic(), customChannel.getGuild().getId());
        customMember.sendPrivateMessage(Embeds.TICKET_CLOSED);

        // Delete the channel
        this.customChannel.delete().queue();
    }

    public void sendEmbed(EmbedBuilder embeds)
    {
        customChannel.sendMessageEmbeds(embeds.build()).queue();
    }

    public TextChannel getChannel()
    {
        return customChannel;
    }

    public void setChannel(String channelId)
    {
        this.customChannel = jda.getTextChannelById(channelId);
    }
}
