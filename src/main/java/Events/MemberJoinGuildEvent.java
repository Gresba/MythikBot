package Events;

import Bot.BotProperty;
import Bot.CustomTime;
import CustomObjects.Embeds;
import Bot.SQLConnection;
import CustomObjects.CustomMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class MemberJoinGuildEvent extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        System.out.println("[Log] User Joined: " + event.getMember().getUser().getName());

        CustomMember customMember = new CustomMember(event.getJDA(), event.getMember().getId(), event.getGuild().getId());

        Member member = customMember.getMember();

        Guild guild = event.getGuild();

        SQLConnection.addDefaultUser(guild, member);

        event.getGuild().addRoleToMember(member, guild.getRoleById(BotProperty.guildsHashMap.get(guild.getId()).getMemberRoleId())).queue();

        int createdMinusJoinedEpochSeconds = (int)(member.getTimeJoined().toEpochSecond() - member.getTimeCreated().toEpochSecond());

        // Check if the account is an alt. 86400 epoch seconds = 1 day.
        if(createdMinusJoinedEpochSeconds < 12000 || member.getUser().getName().toLowerCase().contains("nft"))
        {
            customMember.sendPrivateMessage(Embeds.ALT_DETECTION);

            guild.addRoleToMember(member, guild.getRoleById("934749962057699339")).queue();
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
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy");
        CustomTime customTime = new CustomTime(accountAge);
        joinEmbed.addField("Account Created", dateFormat.format(member.getTimeCreated().minusHours(5)), false);
        joinEmbed.addField("Account Joined", dateFormat.format(member.getTimeJoined().minusHours(5)), false);
        joinEmbed.addField("Account Age: ", customTime.toString(), false);

        joinEmbed.setFooter("Member ID: " + member.getId());

        BotProperty.storeLog(guild, joinEmbed, "Joined");

        // GOAL: Figure out which invite code the user joined with and who invited the user

        // 1. Get all invites from the guild
        HashMap<String, Integer> guildInvitesHashMap = new HashMap<>();

        // Load invites into hashMap
        guild.retrieveInvites().complete().forEach(invite -> guildInvitesHashMap.put(invite.getCode(), invite.getUses()));

        try {
            // 2. Get all invites from the database
            ResultSet invites = SQLConnection.getInvites(guild.getId());

            // 3. Loop through guild invites and database invites to find any difference
            while(invites.next())
            {
                String invCode = invites.getString(1);
                int uses = invites.getInt(4);
                if(guildInvitesHashMap.get(invCode) > uses)
                    System.out.println("fdsf");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // 3. Loop through guild invites and database invites to find any difference
            // If guild invite does not exist in database invites
                // that means that the invite expired or was deleted so delete invite from database
            // else if guild invite has more uses than the invites from the database
                // That invite is the code that the user used to join
                    // Increment user invite count
                    // Set the joined users invite code and inviter to the member



    }
}
