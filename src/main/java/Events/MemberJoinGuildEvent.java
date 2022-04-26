package Events;

import Bot.BotProperty;
import Bot.CustomTime;
import Bot.Embeds;
import Bot.SQLConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class MemberJoinGuildEvent extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        BotProperty botProperty = new BotProperty();

        System.out.println("[Log] User Joined: " + event.getMember().getUser().getName());
        Member member = event.getMember();
        Guild guild = event.getGuild();

        Statement statement = SQLConnection.getStatement();

        SQLConnection.addDefaultUser(guild, member);

        int createdMinusJoinedEpochSeconds = (int)(member.getTimeJoined().toEpochSecond() - member.getTimeCreated().toEpochSecond());

        // Check if the account is an alt. 86400 epoch seconds = 1 day.
        if(createdMinusJoinedEpochSeconds < 12000 || member.getUser().getName().toLowerCase().contains("NFT"))
        {
            Embeds.sendEmbed(Embeds.ALTDETECTION.getEmbed(), member, true);

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
