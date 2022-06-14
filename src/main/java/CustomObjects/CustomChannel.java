package CustomObjects;

import Bot.Embeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

public class CustomChannel {
    private JDA jda;
    private TextChannel customChannel;
    /**
     * Constuctor for initializing a ticket channel
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
