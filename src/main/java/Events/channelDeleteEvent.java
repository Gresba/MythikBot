package Events;

import Bot.SQLConnection;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Statement;

public class channelDeleteEvent extends ListenerAdapter {
    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        Statement statement = SQLConnection.getStatement();
        try {
            String decrementTicketCount = "DELETE FROM Tickets WHERE TicketID = '" + event.getChannel().getId() + "'";
            statement.executeUpdate(decrementTicketCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
