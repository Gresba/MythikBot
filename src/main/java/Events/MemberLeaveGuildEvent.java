package Events;

import Bot.BotProperty;
import Bot.SQLConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberLeaveGuildEvent extends ListenerAdapter {
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Member member = event.getMember();

        BotProperty botProperty = new BotProperty();

        EmbedBuilder joinEmbed = new EmbedBuilder();

        // Embed Properties
        joinEmbed.setTitle("Member Left")
            .setAuthor(member.getUser().getAsTag(), member.getEffectiveAvatarUrl(), member.getEffectiveAvatarUrl())
            .setDescription(member.getAsMention() + " left the server")
            .setColor(Color.RED);

        joinEmbed.setFooter("Member ID: " + member.getId());

        botProperty.storeLog(event.getGuild(), joinEmbed, "Left");

        // Check the inviter associated with the user and decrement the count of that user in the database
        String getInviterIDQuery = "SELECT Inviter FROM Users WHERE MemberId = '" + member.getId() + "'";

        // Get the inviter of the user

        try {
            ResultSet inviterIDResult = SQLConnection.getStatement().executeQuery(getInviterIDQuery);
            String inviterId = "";
            while (inviterIDResult.next())
            {
                inviterId = inviterIDResult.getString(1);
            }

            String incrementInviterInviteCount = "Update Users SET Invites = Invites - 1 WHERE MemberID = '" + inviterId + "'";

            SQLConnection.getStatement().executeUpdate(incrementInviterInviteCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Get the inviters invite count

            // Decrement the invite count

            // Assign the new value into the database

    }
}
