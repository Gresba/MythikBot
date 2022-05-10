package Events;

import Bot.BotProperty;
import Bot.CustomTime;
import Bot.Embeds;
import Bot.SQLConnection;
import CustomObjects.CustomMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class MemberJoinGuildEvent extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        BotProperty botProperty = new BotProperty();

        System.out.println("[Log] User Joined: " + event.getMember().getUser().getName());

        CustomMember customMember = new CustomMember(event.getJDA(), event.getMember().getId(), event.getGuild().getId());

        Member member = customMember.getMember();

        Guild guild = event.getGuild();

        Statement statement = SQLConnection.getStatement();

        SQLConnection.addDefaultUser(guild, member);

        // View Current invites in the guild
        List<Invite> invites = guild.retrieveInvites().complete();

        HashMap<String, Invite> invitesHashMap = new HashMap<>();

        for (Invite invite: invites)
        {
            invitesHashMap.put(invite.getCode(), invite);
        }

        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("945973060027166750")).queue();

        try {
            PreparedStatement getInvitesQuery = statement.getConnection().prepareStatement("SELECT * FROM Invites");
            ResultSet savedInvites = getInvitesQuery.executeQuery();
            while(savedInvites.next())
            {
                String inviteCode = savedInvites.getString(1);

                if(!invitesHashMap.containsKey(inviteCode))
                {
                    PreparedStatement removeInviteQuery = statement.getConnection().prepareStatement("DELETE FROM Invites WHERE Code = ?");

                    removeInviteQuery.setString(1, inviteCode);

                    removeInviteQuery.executeUpdate();
                }else{
                    Invite invite = invitesHashMap.get(inviteCode);

                    int inviteCount = invite.getUses();
                    int savedInviteUses = savedInvites.getInt(2);

                    if(inviteCount > savedInviteUses)
                    {
                        // UPDATE COUNT FOR THE USER

                        // UPDATE the invite count
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Get invites in the database
        for (Invite invite: invites)
        {

        }

        // Compare the invites to the values in the database
            // If any of the values are different that means that is the invite link that was used

        // Incrememnt Counter for inviter

        int createdMinusJoinedEpochSeconds = (int)(member.getTimeJoined().toEpochSecond() - member.getTimeCreated().toEpochSecond());

        // Check if the account is an alt. 86400 epoch seconds = 1 day.
        if(createdMinusJoinedEpochSeconds < 12000 || member.getUser().getName().toLowerCase().contains("NFT"))
        {
            customMember.sendPrivateMessage(Embeds.ALT_DETECTION);

            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("934749962057699339")).queue();
        }

        /*****************************************************************************************************/
        // Embed for joined users info
        /*****************************************************************************************************/
        EmbedBuilder joinEmbed = new EmbedBuilder()
            .setTitle("Member Joined!")
            .setAuthor(member.getUser().getAsTag(), member.getEffectiveAvatarUrl(), member.getEffectiveAvatarUrl())
            .setDescription(member.getAsMention() + " joined the server")
            .setColor(Color.GREEN);

        long accountAge = member.getTimeJoined().minusHours(5).toEpochSecond() - member.getTimeCreated().minusHours(5).toEpochSecond();
        DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy");
        CustomTime customTime = new CustomTime(accountAge);
        joinEmbed.addField("Account Created", FOMATTER.format(member.getTimeCreated().minusHours(5)), false);
        joinEmbed.addField("Account Joined", FOMATTER.format(member.getTimeJoined().minusHours(5)), false);
        joinEmbed.addField("Account Age: ", customTime.toString(), false);

        joinEmbed.setFooter("Member ID: " + member.getId());

        botProperty.storeLog(event.getJDA(), joinEmbed, "Joined");
    }
}
