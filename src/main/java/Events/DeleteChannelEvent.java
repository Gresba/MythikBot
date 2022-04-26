package Events;

import Bot.BotProperty;
import Bot.SQLConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Statement;

public class DeleteChannelEvent extends ListenerAdapter {
    @Override
    public void onChannelDelete(@NotNull net.dv8tion.jda.api.events.channel.ChannelDeleteEvent event) {
        Statement statement = SQLConnection.getStatement();

        BotProperty botProperty = new BotProperty();
        event.getGuild().retrieveAuditLogs().type(ActionType.CHANNEL_DELETE).limit(1).queue(
                list -> {
                    EmbedBuilder deletedChannelEmbed = new EmbedBuilder()
                            .setTitle("Deleted Channel")
                            .setDescription(list.get(0).getUser().getAsMention() + " deleted channel " + event.getChannel().getName());
                    botProperty.storeLog(event.getJDA(), deletedChannelEmbed, "DeletedChannel");
                }
        );

        try {
            String decrementTicketCount = "DELETE FROM Tickets WHERE TicketID = '" + event.getChannel().getId() + "'";
            statement.executeUpdate(decrementTicketCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
