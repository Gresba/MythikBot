package Events;

import Bot.BotProperty;
import Bot.SQLConnection;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class InviteCreateEvent extends ListenerAdapter {
    @Override
    public void onGuildInviteCreate(@NotNull GuildInviteCreateEvent event) {
        // Find the user who created and add invite to database

        Statement statement = SQLConnection.getStatement();

        User inviter = event.getInvite().getInviter();

        Invite invite = event.getInvite();

        try {
            PreparedStatement addInviteQuery = statement.getConnection().prepareStatement("INSERT INTO Invites VALUES (?, ?, ?)");

            // Setting invite code, invite code uses and inviter, respectively.
            addInviteQuery.setString(1, invite.getCode());
            addInviteQuery.setInt(2, invite.getUses());
            addInviteQuery.setString(3, inviter.getId());

            addInviteQuery.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onGuildInviteCreate(event);
    }
}
