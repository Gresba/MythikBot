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
    }
}
