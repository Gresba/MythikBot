package Events;

import Bot.SQLConnection;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.plaf.nimbus.State;
import java.sql.SQLException;
import java.sql.Statement;

public class guildJoinEvent extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        Guild guild = event.getGuild();

        String defaultPrefix = "m!";

        Statement statement = SQLConnection.getStatement();
        try {
            statement.executeUpdate("INSERT INTO Guilds VALUES ('" + guild.getId() + "','" + defaultPrefix + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
