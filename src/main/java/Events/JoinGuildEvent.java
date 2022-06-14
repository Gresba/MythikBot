package Events;

import Bot.SQLConnection;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class JoinGuildEvent extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        try {
            SQLConnection.addDefaultGuild(event.getGuild());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
