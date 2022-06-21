package CustomObjects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class CustomChannel {
    private JDA jda;
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
     * Allows the passed in member to speak in a custom channel
     *
     * @param member The member that will be allowed to speak
     */
    public void openTicket(Member member)
    {
        this.customChannel.upsertPermissionOverride(member)
                .setAllow(Permission.MESSAGE_SEND)
                .setAllow(Permission.VIEW_CHANNEL)
                .queue();
    }

    /**
     * Denies the ability for a member to speak in a custom channel
     *
     * @param permissionTarget The target either a role or member that will be muted from the chat
     */
    public void muteTicket(IPermissionHolder permissionTarget)
    {
        this.customChannel.upsertPermissionOverride(permissionTarget)
                .setDeny(Permission.MESSAGE_SEND)
                .setAllow(Permission.VIEW_CHANNEL)
                .queue();
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
